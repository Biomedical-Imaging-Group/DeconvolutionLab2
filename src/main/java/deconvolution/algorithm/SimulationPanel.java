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

package deconvolution.algorithm;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.JTextField;

import bilib.component.GridPanel;
import deconvolution.Command;
import deconvolutionlab.Config;

public class SimulationPanel extends AlgorithmPanel implements KeyListener {

	private JTextField	txtMean;
	private JTextField	txtStdev;
	private JTextField	txtPoisson;

	private Simulation	algo	= new Simulation(0, 0, 0);

	@Override
	public JPanel getPanelParameters() {
		double[] params = algo.getDefaultParameters();
		txtMean = new JTextField("" + params[0], 5);
		txtStdev = new JTextField("" + params[1], 5);
		txtPoisson = new JTextField("" + params[2], 5);

		GridPanel pn = new GridPanel(false);
		pn.place(1, 0, "<html>Gaussian (Mean)</html>");
		pn.place(1, 2, txtMean);
		pn.place(2, 0, "<html>Gaussian (Stdev)</html>");
		pn.place(2, 2, txtStdev);
		pn.place(3, 0, "<html>Poisson</html>");
		pn.place(3, 2, txtPoisson);
		txtMean.addKeyListener(this);
		txtStdev.addKeyListener(this);
		txtPoisson.addKeyListener(this);
		Config.register("Algorithm." + algo.getShortnames()[0], "gaussian.mean", txtMean, params[0]);
		Config.register("Algorithm." + algo.getShortnames()[0], "gaussian.stdev", txtStdev, params[1]);
		Config.register("Algorithm." + algo.getShortnames()[0], "poisson", txtPoisson, params[2]);
		return pn;
	}

	@Override
	public String getCommand() {
		return txtMean.getText() + "  " + txtStdev.getText() + " " + txtPoisson.getText();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		Command.buildCommand();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		Command.buildCommand();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Command.buildCommand();
	}

	@Override
	public String getName() {
		return algo.getName();
	}

	@Override
	public String getDocumentation() {
		String s = "";
		s += "<h1>" + getName();
		s += " [<span style=\"color:#FF3333;font-family:georgia\">SIM</span>]</h1>";
		s += "<p>This algorithm is only used for simulation. It convolves the input image with the PSF and adds some noise.</p>";
		s += "<p>The noise has a Gaussian distribution (mean, stdev) and a Poisson distribution (poisson).</p>";
		return s;
	}

	@Override
	public String[] getShortnames() {
		return algo.getShortnames();
	}

}
