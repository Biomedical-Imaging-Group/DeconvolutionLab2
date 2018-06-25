/*
 * DeconvolutionLab2
 * 
 * Conditions of use: You are free to use this software for research or
 * educational purposes. In addition, we expect you to include adequate
 * citations and acknowledgments whenever you present or publish results that
 * are based on it.
 * 
 * Reference: DeconvolutionLab2: An Open-Source Software for Deconvolution
 * Microscopy D. Sage, L. Donati, F. Soulez, D. Fortun, G. Schmit, A. Seitz,
 * R. Guiet, C. Vonesch, M Unser, Methods of Elsevier, 2017.
 */

/*
 * Copyright 2010-2017 Biomedical Imaging Group at the EPFL.
 * 
 * This file is part of DeconvolutionLab2 (DL2).
 * 
 * DL2 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * DL2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * DL2. If not, see <http://www.gnu.org/licenses/>.
 */

package deconvolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bilib.tools.Files;
import bilib.tools.NumFormat;
import deconvolution.algorithm.Algorithm;
import deconvolution.algorithm.AlgorithmList;
import deconvolution.algorithm.Constraint;
import deconvolution.algorithm.Controller;
import deconvolutionlab.Constants;
import deconvolutionlab.module.AbstractModule;
import deconvolutionlab.module.CommandModule;
import deconvolutionlab.module.LanguageModule;
import deconvolutionlab.monitor.ConsoleMonitor;
import deconvolutionlab.monitor.Monitors;
import deconvolutionlab.monitor.TableMonitor;
import deconvolutionlab.monitor.Verbose;
import deconvolutionlab.output.Output;
import deconvolutionlab.output.Output.View;
import fft.FFT;
import signal.Operations;
import signal.apodization.AbstractApodization;
import signal.apodization.Apodization;
import signal.apodization.UniformApodization;
import signal.padding.AbstractPadding;
import signal.padding.NoPadding;
import signal.padding.Padding;
import wavelets.Wavelets;

public class Command {

	public static String keywords[] = { "-image", "-psf", "-reference", "-algorithm", "-path", "-verbose", "-monitor",
			"-multithreading", "-system", "-stats", "-constraint", "-time", "-residu", "-out", "-pad", "-apo", "-norm",
			"-fft", "-epsilon" };

	private static AbstractModule modules[];
	private static CommandModule command;
	private static LanguageModule language;

	public static void active(AbstractModule[] m, CommandModule commandModule, LanguageModule languageModule) {
		modules = m;
		command = commandModule;
		language = languageModule;
	}

	public static String buildCommand() {
		if (modules == null)
			return "";
		String cmd = "";
		for (AbstractModule m : modules)
			cmd += m.getCommand() + " ";
		if (command != null)
			command.setCommand(cmd);
		if (language != null)
			language.setCommand(cmd);
		return cmd;
	}
	
	public static void updateModules() {
	}

	public static Controller decodeController(String command) {

		Controller controller = new Controller();

		ArrayList<CommandToken> tokens = parse(command);
		for (CommandToken token : tokens) {
			if (token.keyword.equalsIgnoreCase("-path")) {
				if (token.parameters.trim().equals("current"))
					controller.setPath(Files.getWorkingDirectory());
				else if (token.parameters.trim().equals("home"))
					controller.setPath(Files.getHomeDirectory());
				else if (token.parameters.trim().equals("desktop"))
					controller.setPath(Files.getDesktopDirectory());
				else
					controller.setPath(token.parameters);
			}

			if (token.keyword.equalsIgnoreCase("-monitor"))
				controller.setMonitors(decodeMonitors(token.parameters));

			if (token.keyword.equalsIgnoreCase("-verbose"))
				controller.setVerbose(Verbose.getByName(token.parameters));

			if (token.keyword.equalsIgnoreCase("-system"))
				controller.setSystem(decodeBoolean(token.parameters));

			if (token.keyword.equalsIgnoreCase("-multithreading"))
				controller.setMultithreading(decodeBoolean(token.parameters));

			if (token.keyword.equalsIgnoreCase("-stats"))
				controller.setStats(decodeStats(token));

			if (token.keyword.equalsIgnoreCase("-constraint"))
				controller.setConstraint(decodeConstraint(token));

			if (token.keyword.equalsIgnoreCase("-time"))
				controller.setTimeLimit(decodeTimeLimit(token));

			if (token.keyword.equalsIgnoreCase("-residu"))
				controller.setResiduMin(decodeResidu(token));

			if (token.keyword.equalsIgnoreCase("-pad"))
				controller.setPadding(decodePadding(token));

			if (token.keyword.equalsIgnoreCase("-apo"))
				controller.setApodization(decodeApodization(token));

			if (token.keyword.equalsIgnoreCase("-norm"))
				controller.setNormalizationPSF(decodeNormalization(token));

			if (token.keyword.equalsIgnoreCase("-epsilon"))
				Operations.epsilon = NumFormat.parseNumber(token.parameters, 1e-6);

			if (token.keyword.equalsIgnoreCase("-fft"))
				controller.setFFT(FFT.getLibraryByName(token.parameters).getDefaultFFT());

			if (token.keyword.equalsIgnoreCase("-epsilon"))
				Operations.epsilon = NumFormat.parseNumber(token.parameters, 1e-6);

			if (token.keyword.equals("-out")) {
				Output out = decodeOut(token);
				if (out != null)
					controller.addOutput(out);
			}
		}
		return controller;
	}

	public static Algorithm decodeAlgorithm(String command) {
		Algorithm algo = AlgorithmList.getDefaultAlgorithm();
		ArrayList<CommandToken> tokens = parse(command);
		for (CommandToken token : tokens) {
			if (token.keyword.equalsIgnoreCase("-algorithm"))
				algo = Command.decodeAlgorithm(token);
		}
		return algo;
	}

	/**
	 * This methods first segments the command line, then create all the tokens of
	 * the command line
	 * 
	 * @param command
	 *            Command line
	 * @return the list of tokens extracted from the command line
	 */
	public static ArrayList<CommandToken> parse(String command) {

		ArrayList<CommandSegment> segments = new ArrayList<CommandSegment>();
		for (String keyword : keywords)
			segments.addAll(findSegment(command, keyword));
		Collections.sort(segments);
		ArrayList<CommandToken> tokens = new ArrayList<CommandToken>();
		for (int i = 0; i < segments.size(); i++) {
			String keyword = segments.get(i).keyword;
			int begin = segments.get(i).index + keyword.length() + 1;
			int end = (i < segments.size() - 1 ? segments.get(i + 1).index : command.length());
			CommandToken token = new CommandToken(keyword, command, begin, end);
			tokens.add(token);
		}
		return tokens;
	}

	public static CommandToken extract(String command, String keyword) {
		ArrayList<CommandToken> tokens = parse(command);
		for (CommandToken token : tokens)
			if (token.keyword.equalsIgnoreCase(keyword))
				return token;
		return (CommandToken) null;
	}

	public static double[] parseNumeric(String line) {
		ArrayList<String> num = new ArrayList<String>();
		Pattern p = Pattern.compile("[-+]?[0-9]+[.]?[0-9]*([eE][-+]?[0-9]+)?");
		Matcher m = p.matcher(line);
		while (m.find()) {
			num.add(m.group());
		}
		double number[] = new double[num.size()];
		for (int i = 0; i < num.size(); i++)
			number[i] = Double.parseDouble(num.get(i));
		return number;
	}

	public static ArrayList<CommandSegment> findSegment(String command, String keyword) {
		ArrayList<CommandSegment> segments = new ArrayList<CommandSegment>();
		String regex = "(?<!\\w)" + keyword + "(?!\\w)";
		if (command == null)
			return segments;
		Matcher matcher = Pattern.compile(regex).matcher(command);
		while (matcher.find()) {
			segments.add(new CommandSegment(keyword, matcher.start()));
		}
		return segments;
	}

	public static String extractOptions(String command) {
		ArrayList<CommandSegment> segments = new ArrayList<CommandSegment>();
		for (String keyword : keywords)
			segments.addAll(findSegment(command, keyword));
		Collections.sort(segments);

		String options = "";
		for (int i = 0; i < segments.size(); i++) {
			String keyword = segments.get(i).keyword;
			int begin = segments.get(i).index + keyword.length() + 1;
			int end = (i < segments.size() - 1 ? segments.get(i + 1).index : command.length());
			if (keyword != "-image" && keyword != "-psf" && keyword != "-algorithm")
				options += keyword + " " + command.substring(begin, end);
		}
		return options;
	}

	public static Algorithm decodeAlgorithm(CommandToken token) {
		String option = token.option;
		Algorithm algo = AlgorithmList.createAlgorithm(option);
		double params[] = parseNumeric(token.parameters);

		if (params != null) {
			algo.setParameters(params);
		}

		if (algo.isWaveletsBased()) {
			for (String wavelet : Wavelets.getWaveletsAsArray()) {
				int pos = token.parameters.toLowerCase().indexOf(wavelet.toLowerCase());
				if (pos >= 0)
					algo.setWavelets(wavelet);
			}
		}
		return algo;
	}

	public static Output decodeOut(CommandToken token) {
		int freq = 0;
		String line = token.parameters;
		String parts[] = token.parameters.split(" ");
		for (int i = 0; i < Math.min(2, parts.length); i++) {
			if (parts[i].startsWith("@")) {
				freq = (int) NumFormat.parseNumber(parts[i], 0);
			}
		}

		String p = token.parameters.toLowerCase();
		Output out = null;
		if (p.startsWith("stack"))
			out = new Output(View.STACK, freq, line.substring("stack".length(), line.length()));
		if (p.startsWith("series"))
			out = new Output(View.SERIES, freq, line.substring("series".length(), line.length()));
		if (p.startsWith("mip"))
			out = new Output(View.MIP, freq, line.substring("mip".length(), line.length()));
		if (p.startsWith("ortho"))
			out = new Output(View.ORTHO, freq, line.substring("ortho".length(), line.length()));
		if (p.startsWith("figure"))
			out = new Output(View.FIGURE, freq, line.substring("figure".length(), line.length()));
		if (p.startsWith("planar"))
			out = new Output(View.PLANAR, freq, line.substring("planar".length(), line.length()));

		return out;
	}

	public static double decodeNormalization(CommandToken token) {
		if (token.parameters.toLowerCase().endsWith("no"))
			return 0;
		else
			return NumFormat.parseNumber(token.parameters, 1);
	}

	public static Stats decodeStats(CommandToken token) {
		String parts[] = token.parameters.split(" ");
		if (parts.length <= 0)
			return new Stats(Stats.Mode.NO, "stats");
		boolean flags[] = new boolean[parts.length];
		boolean show = true;
		boolean save = true;

		for (int i = 0; i < parts.length; i++) {
			String p = parts[i].toLowerCase();
			flags[i] = false;
			if (p.equals("false")) {
				show = false;
				save = false;
				flags[i] = true;
			}
			if (p.equals("true")) {
				show = true;
				save = true;
				flags[i] = true;
			}
			if (p.equals("noshow")) {
				show = false;
				flags[i] = true;
			}
			if (p.equals("nosave")) {
				save = false;
				flags[i] = true;
			}
			if (p.equals("show")) {
				show = true;
				flags[i] = true;
			}
			if (p.equals("save")) {
				save = true;
				flags[i] = true;
			}
		}
		String name = "";
		int count = 0;
		for (int i = 0; i < parts.length; i++) {
			if (flags[i] == false) {
				name += parts[i] + " ";
				count++;
			}
		}
		name = count == 0 ? "stats" : name.trim();

		if (show & !save)
			return new Stats(Stats.Mode.SHOW, name);
		if (!show & save)
			return new Stats(Stats.Mode.SAVE, name);
		if (show & save)
			return new Stats(Stats.Mode.SHOWSAVE, name);
		return new Stats(Stats.Mode.NO, name);

	}

	public static Constraint decodeConstraint(CommandToken token) {
		String p = token.parameters.toLowerCase();
		if (p.startsWith("non"))
			return Constraint.NONNEGATIVE;
		if (p.startsWith("no"))
			return Constraint.NO;
		if (p.startsWith("clip"))
			return Constraint.CLIPPED;
		if (p.equals("0"))
			return Constraint.NO;
		return Constraint.NO;
	}

	public static double decodeResidu(CommandToken token) {
		if (token.parameters.toLowerCase().endsWith("no"))
			return -1;
		else
			return NumFormat.parseNumber(token.parameters, 1);
	}

	public static double decodeTimeLimit(CommandToken token) {
		if (token.parameters.toLowerCase().endsWith("no"))
			return -1;
		else
			return NumFormat.parseNumber(token.parameters, 1);
	}

	public static Padding decodePadding(CommandToken token) {
		AbstractPadding padXY = new NoPadding();
		AbstractPadding padZ = new NoPadding();
		String param = token.parameters.trim();
		String[] parts = param.split(" ");
		if (parts.length > 0)
			padXY = Padding.getByShortname(parts[0].trim());
		if (parts.length > 1)
			padZ = Padding.getByShortname(parts[1].trim());
		double[] ext = NumFormat.parseNumbers(param);
		int extXY = 0;
		if (ext.length > 0)
			extXY = (int) Math.round(ext[0]);
		int extZ = 0;
		if (ext.length > 1)
			extZ = (int) Math.round(ext[1]);

		return new Padding(padXY, padXY, padZ, extXY, extXY, extZ);
	}

	public static Apodization decodeApodization(CommandToken token) {
		AbstractApodization apoXY = new UniformApodization();
		AbstractApodization apoZ = new UniformApodization();
		String[] parts = token.parameters.trim().split(" ");
		if (parts.length >= 1)
			apoXY = Apodization.getByShortname(parts[0].trim());
		if (parts.length >= 2)
			apoZ = Apodization.getByShortname(parts[1].trim());
		return new Apodization(apoXY, apoXY, apoZ);
	}

	public static String getPath() {
		buildCommand();
		ArrayList<CommandToken> tokens = parse(command.getCommand());
		String path = System.getProperty("user.dir");
		for (CommandToken token : tokens)
			if (token.keyword.equalsIgnoreCase("-path") && !token.parameters.equalsIgnoreCase("current"))
				path = token.parameters;
		return path;
	}

	public static Monitors decodeMonitors(String cmd) {
		String parts[] = cmd.toLowerCase().split(" ");
		Monitors monitors = new Monitors();
		for (String p : parts) {
			if (p.equals("0")) {
				monitors.clear();
			}
			if (p.equals("1")) {
				monitors.clear();
				monitors.add(new ConsoleMonitor());
			}
			if (p.equals("2")) {
				monitors.clear();
				monitors.add(new TableMonitor(Constants.widthGUI, 240));
			}
			if (p.equals("3")) {
				monitors.clear();
				monitors.add(new ConsoleMonitor());
				monitors.add(new TableMonitor(Constants.widthGUI, 240));
			}
			if (p.equals("no"))
				monitors.add(new ConsoleMonitor());
			if (p.equals("console"))
				monitors.add(new ConsoleMonitor());
			if (p.equals("table"))
				monitors.add(new TableMonitor(Constants.widthGUI, 240));
		}
		return monitors;
	}

	public static boolean decodeBoolean(String cmd) {
		String p = cmd.toLowerCase();
		if (p.startsWith("no"))
			return false;
		if (p.equals("0"))
			return false;
		if (p.equals("false"))
			return false;
		if (p.startsWith("dis"))
			return false;
		return true;
	}

}
