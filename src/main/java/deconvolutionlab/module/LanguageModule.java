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

package deconvolutionlab.module;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import bilib.component.HTMLPane;
import deconvolution.Command;
import deconvolution.CommandToken;
import deconvolution.Deconvolution;
import deconvolution.algorithm.Algorithm;
import deconvolutionlab.Config;

public class LanguageModule extends AbstractModule implements ActionListener {

	private HTMLPane			language;
	private JComboBox<String>	cmb;
	private JComboBox<String>	gui;
	private JTextField			txt;

	public LanguageModule() {
		create("Language");
	}

	public String getJobName() {
		if (txt != null)
			return txt.getText();
		return "";
	}
	@Override
	public JPanel buildExpandedPanel() {
		language = new HTMLPane("Monaco", 100, 100);
		cmb = new JComboBox<String>(new String[] { "Command line", "ImageJ Macro", "Java", "Matlab" });
		gui = new JComboBox<String>(new String[] { "Run (Headless)", "Launch (with control panel)" });
		txt = new JTextField("Job", 8);
		JPanel pn = new JPanel(new BorderLayout());
		pn.add(cmb, BorderLayout.WEST);
		pn.add(txt, BorderLayout.CENTER);
		pn.add(gui, BorderLayout.EAST);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(pn, BorderLayout.NORTH);
		panel.add(language.getPane(), BorderLayout.CENTER);
		cmb.addActionListener(this);
		gui.addActionListener(this);
		Config.register(getName(), "language", cmb, cmb.getItemAt(0));
		Config.register(getName(), "headless", gui, gui.getItemAt(0));
		Config.register(getName(), "job", txt, "Job");
		language.clear();
		return panel;
	}

	@Override
	public void expand() {
		super.expand();
		update();
	}

	public void update() {
		String cmd = Command.buildCommand();
		setCommand(cmd);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		if (e.getSource() == cmb)
			update();
		if (e.getSource() == gui)
			update();
	}

	@Override
	public void close() {
	}

	@Override
	public void setCommand(String cmd) {
		if (cmb.getSelectedIndex() == 0) {
			language.clear();
			String run = gui.getSelectedIndex() == 0 ? " Run " : " Launch ";
			language.append("p", "java -jar DeconvolutionLab_2.jar " + run + cmd);
		}
		else if (cmb.getSelectedIndex() == 1) {
			language.clear();
			language.append("p", imagej(cmd, gui.getSelectedIndex() == 0));
		}
		else if (cmb.getSelectedIndex() == 2) {
			language.clear();
			language.append("p", java(cmd, gui.getSelectedIndex() == 0));
		}
		else if (cmb.getSelectedIndex() == 3) {
			language.clear();
			language.append("p", matlab(cmd));
		}
	}

	@Override
	public String getCommand() {
		return "";
	}

	private String matlab(String cmd) {
		String job = txt.getText();
	
		String script = "";
		Deconvolution d = new Deconvolution("Matlab", cmd);
		String options = Command.extractOptions(cmd);
		Algorithm algo = d.getAlgorithm();
		if (algo == null)
			return "ERROR";
		String s = algo.getShortnames()[0];
		String param = algo.getParametersAsString();
		script += p("% this function returns the deconvolved image as an 3D matrix");
		script += p("% image is a 3D matrix containing the image");
		script += p("% psf is a 3D matrix containing the PSF");
		
		script += p("function result = " + job + "(image, psf)");
		script += p1("% Install first DeconvolutionLab_2.jar into the java directory of Matlab");
		script += p1("javaaddpath([matlabroot filesep 'java' filesep 'DeconvolutionLab_2.jar'])");
		script += p1("% Run the deconvolution\n");
		script += p1("result = DL2." + s + "(image, psf, " + param +" , '" + options +"');");
		script += p("end");
		return script;  
	}
	
	
	private String imagej(String cmd, boolean headless) {
		String job = txt.getText();
		String macro = p("// Job: " + job + " ");
		macro += p("// Macro generated by DeconvolutionLab2 ");
		macro += p("// " + new SimpleDateFormat("dd/MM/yy HH:m:s").format(new Date()) + " ");
		String param = p("parameters = \"\" ");
		ArrayList<CommandToken> tokens = Command.parse(cmd);
		String image = "image = \" NOT DEFINED \" ";
		String psf   = "psf = \" NOT DEFINED \" ";
		String algo  = "algo = \" NOT DEFINED \" ";
		for (CommandToken token : tokens) {
			if (token.keyword.equals("-image"))
				image = p("image = \" -image " + token.parameters.trim() + "\" ");
			else if (token.keyword.equals("-psf"))
				psf = p("psf = \" -psf " + token.parameters.trim() + "\" ");
			else if (token.keyword.equals("-algorithm"))
				algo = p("algorithm = \" -algorithm " + token.parameters.trim() + "\" ");
			else
				param += p("parameters += \" " + token.keyword + " " + token.parameters.trim() + "\"");
		}

		String option = macro + image + psf + algo + param;
		if (headless)
			option += p("run(\"DeconvolutionLab2 Run\", image + psf + algorithm + parameters)");
		else
			option += p("run(\"DeconvolutionLab2 Launch\", image + psf + algorithm + parameters)");

		return option;
	}
	
	
	private String java(String cmd, boolean headless) {
		String job = txt.getText();
		String code = "";
		code += p("import deconvolution.Deconvolution;");
		code += p("import ij.plugin.PlugIn;");
		code += p("");
		
		code += p("public class DeconvolutionLab2_" + job + " implements PlugIn {");
		code += p1("public DeconvolutionLab2_" + job + "() {");
	
		String param = p2("String parameters = \"\";");
		ArrayList<CommandToken> tokens = Command.parse(cmd);
		String image = p2("String image = \" NOT DEFINED \";");
		String psf   = p2("String psf = \" NOT DEFINED \";");
		String algo  = p2("String algo = \" NOT DEFINED \";");
		for (CommandToken token : tokens) {
			if (token.keyword.equals("-image"))
				image = p2("String image = \" -image " + token.parameters.trim() + "\";");
			else if (token.keyword.equals("-psf"))
				psf = p2("String psf = \" -psf " + token.parameters.trim() + "\";");
			else if (token.keyword.equals("-algorithm"))
				algo = p2("String algorithm = \" -algorithm " + token.parameters.trim() + "\";");
			else
				param += p2("parameters += \" " + token.keyword + " " + token.parameters.trim() + "\";");
		}
		code += image + psf + algo + param;
		code += p2("new Deconvolution(\"" + job +  "\", image + psf + algorithm + parameters)");
		code += p1("}");
		
		code += p1("");
		
		code += p1("@Override");
		code += p1("public void run(String arg0) {");
		code += p2(" new DeconvolutionLab2_" + job + "();");
		
		code += p1("}");
		code += p("}");
		return code;
	}
	
	private String p(String content) {
		return "<p>" + content + "</p>";
	}
	
	private String p1(String content) {
		return "<p style=\"padding-left:10px\">" + content + "</p>";
	}
	
	private String p2(String content) {
		return "<p style=\"padding-left:20px\">" + content + "</p>";
	}
}
