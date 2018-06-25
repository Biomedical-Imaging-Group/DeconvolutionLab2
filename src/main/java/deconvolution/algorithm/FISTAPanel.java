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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bilib.component.GridPanel;
import bilib.component.SpinnerRangeDouble;
import bilib.component.SpinnerRangeInteger;
import bilib.tools.NumFormat;
import deconvolution.Command;
import deconvolution.RegularizationPanel;
import deconvolutionlab.Config;
import deconvolutionlab.Constants;
import wavelets.AbstractWavelets;
import wavelets.Wavelets;

public class FISTAPanel extends AlgorithmPanel implements KeyListener, ActionListener, ChangeListener {

	private SpinnerRangeInteger	spnIter		= new SpinnerRangeInteger(10, 1, 99999, 1, "###");
	private SpinnerRangeDouble	spnStep		= new SpinnerRangeDouble(1, 0, 2, 0.1, "#.#");
	private RegularizationPanel	reg;
	private JComboBox<String>	cmbWav		= new JComboBox<String>(Wavelets.getWaveletsAsArray());
	private JComboBox<String>	cmbScale	= new JComboBox<String>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" });

	private FISTA				algo		= new FISTA(10, 1, 0.1, "Haar", 3);

	@Override
	public JPanel getPanelParameters() {
		AbstractWavelets wavdef = Wavelets.getDefaultWavelets();
		double[] params = algo.getDefaultParameters();

		reg = new RegularizationPanel(params[2]);
		spnIter.setPreferredSize(Constants.dimParameters);
		spnStep.setPreferredSize(Constants.dimParameters);
		cmbWav.setPreferredSize(Constants.dimParameters);
		cmbScale.setPreferredSize(Constants.dimParameters);

		GridPanel pn = new GridPanel(false);
		pn.place(1, 0, "<html><span \"nowrap\"><b>Iterations</b></span></html>");
		pn.place(1, 1, "<html><span \"nowrap\"><i>N</i></span></html>");
		pn.place(1, 2, spnIter);
		pn.place(1, 3, "<html><span \"nowrap\">Step <i>&gamma;</i></span></html>");
		pn.place(1, 4, spnStep);

		pn.place(2, 0, 5, 1, reg);

		pn.place(5, 0, "<html><span \"nowrap\"><b>Wavelets</b></span></html>");
		pn.place(5, 2, cmbWav);
		pn.place(5, 3, "<html>Scale</html>");
		pn.place(5, 4, cmbScale);

		Config.register("Algorithm." + algo.getShortnames()[0], "iterations", spnIter, params[0]);
		Config.register("Algorithm." + algo.getShortnames()[0], "step", spnStep, params[1]);
		Config.register("Algorithm." + algo.getShortnames()[0], "wavelets", cmbWav, wavdef.getName());
		Config.register("Algorithm." + algo.getShortnames()[0], "scale", cmbScale, wavdef.getScales());
		Config.register("Algorithm." + algo.getShortnames()[0], "reg", reg.getText(), "0.1");
		reg.getText().addKeyListener(this);
		reg.getSlider().addChangeListener(this);
		spnIter.addChangeListener(this);
		spnStep.addChangeListener(this);
		cmbWav.addActionListener(this);
		cmbScale.addActionListener(this);
		return pn;
	}

	@Override
	public String getCommand() {
		int iter = spnIter.get();
		double gamma = spnStep.get();
		double lambda = reg.getValue();
		String waveletsName = (String) cmbWav.getSelectedItem();
		int scale = Integer.parseInt((String) cmbScale.getSelectedItem());
		return iter + " " + NumFormat.nice(gamma) + " " + NumFormat.nice(lambda) + " " + waveletsName + " " + scale;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Command.buildCommand();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		reg.getText().removeKeyListener(this);
		reg.updateFromSlider();
		Command.buildCommand();
		reg.getText().addKeyListener(this);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		reg.getSlider().removeChangeListener(this);
		reg.updateFromText();
		Command.buildCommand();
		reg.getSlider().addChangeListener(this);
	}

	@Override
	public String getName() {
		return algo.getName();
	}

	@Override
	public String[] getShortnames() {
		return algo.getShortnames();
	}

	@Override
	public String getDocumentation() {
		String s = "";
		s += "<h1>" + getName();
		s += " [<span style=\"color:#FF3333;font-family:georgia\">FISTA</span>]</h1>";		
		s += "<p>FISTA exploits the sparsity of the wavelet domain to better preserve ";
		s += "image details and discontinuities. The associated cost function is: </p>";
		s += "<p>C(<b>x</b>) = | <b>y</b> - <b>H</b><b>x</b> | + &lambda; |<b>W</b><b>x</b>|<sub>1</sub> </p>";
		s += "<p>where <b>W</b> represents a wavelet transform. ";
		s += "Due to the non-smoothness of the l<sub>1</sub> norm, ";
		s += "the problem can be solved efficiently by fast iterative soft-thresholding </p>";
		s += "<p>Reference: A. Beck, M. Teboulle, A fast iterative shrinkage-thresholding algorithm for linear inverse problems, SIAM J. Imag. Sci. 2, 2009. ";
		return s;
	}

}
