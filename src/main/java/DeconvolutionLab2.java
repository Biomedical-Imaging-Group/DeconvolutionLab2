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

import java.io.File;

import bilib.tools.Files;
import deconvolution.Command;
import deconvolution.Deconvolution;
import deconvolutionlab.Constants;
import deconvolutionlab.Lab;
import deconvolutionlab.LabDialog;
import deconvolutionlab.Platform;
import deconvolutionlab.monitor.Monitors;

public class DeconvolutionLab2 {

	public static String	ack	= Constants.name + " " + Constants.version + " " + Constants.copyright;

	public static void main(String arg[]) {

		Lab.init(Platform.STANDALONE);
		if (arg.length == 0) {
			System.out.println("DeconvolutionLab2 Lab " + Constants.version);
			lab(arg);
			return;
		}

		String flag = arg[0].trim().toLowerCase();
		if (flag.equalsIgnoreCase("help")) {
			System.out.println("Starting help");
			help();
			return;
		}

		else if (flag.equalsIgnoreCase("lab")) {
			System.out.println("Starting lab");
			lab(arg);
		}

		else if (flag.equalsIgnoreCase("fft")) {
			System.out.println("Starting fft");
			Lab.checkFFT(Monitors.createDefaultMonitor());
		}

		else if (flag.equalsIgnoreCase("run")) {
			System.out.println("Starting run");
			String cmd = "";
			for (int i = 1; i < arg.length; i++)
				cmd += arg[i] + " ";
			new Deconvolution("Run", cmd, Deconvolution.Finish.KILL).deconvolve();
		}

		else if (flag.equalsIgnoreCase("launch")) {
			System.out.println("Starting launch");
			String cmd = "";
			for (int i = 1; i < arg.length; i++)
				cmd += arg[i] + " ";
			new Deconvolution("Launch", cmd, Deconvolution.Finish.KILL).launch();
		}
		else
			System.out.println("" + flag + " command not found");

	}

	private static void lab(String arg[]) {
		String config = Files.getWorkingDirectory() + "DeconvolutionLab2.config";
		if (arg.length >= 2) {
			String filename = arg[1].trim();
			File file = new File(filename);
			if (file.exists())
				if (file.isFile())
					if (file.canRead())
						config = filename;
		}
		Lab.init(Platform.STANDALONE, config);
		LabDialog dialog = new LabDialog();
		dialog.setVisible(true);
	}

	public static void help() {
		System.out.println("More info:" + Constants.url);
		System.out.println("Syntax:");
		System.out.println("java -jar DeconvolutionLab_2.jar lab");
		System.out.println("java -jar DeconvolutionLab_2.jar run {command} ...");
		System.out.println("java -jar DeconvolutionLab_2.jar launch {command} ...");
		System.out.println("java -jar DeconvolutionLab_2.jar fft");
		System.out.println("java -jar DeconvolutionLab_2.jar info");
		System.out.println("java -jar DeconvolutionLab_2.jar help");
		System.out.println("{command} is the full command line for running a deconvolution");
		System.out.println("Keywords of {command}: ");
		for (String keyword : Command.keywords)
			System.out.println("\t" + keyword);
	}

	public DeconvolutionLab2(String cmd) {
		System.out.println("cmd: " + cmd);
		Lab.init(Platform.STANDALONE, Files.getWorkingDirectory() + "DeconvolutionLab2.config");
		new Deconvolution("CommandLine", cmd).deconvolve();
	}
	
}
