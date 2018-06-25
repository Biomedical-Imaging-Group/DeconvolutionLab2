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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bilib.component.GridPanel;
import bilib.tools.NumFormat;
import deconvolution.Command;
import deconvolution.RegularizationPanel;
import deconvolutionlab.Config;

public class RegularizedInverseFilterPanel extends AlgorithmPanel implements KeyListener, ChangeListener {

	private RegularizationPanel			reg;
	private RegularizedInverseFilter	algo	= new RegularizedInverseFilter(0.1);

	@Override
	public JPanel getPanelParameters() {
		double[] params = algo.getDefaultParameters();
		reg = new RegularizationPanel(params[0]);
		GridPanel pn = new GridPanel(false);
		pn.place(0, 0, reg);
		Config.register("Algorithm." + algo.getShortnames()[0], "reg", reg.getText(), "0.1");
		reg.getText().addKeyListener(this);
		reg.getSlider().addChangeListener(this);
		return pn;
	}

	@Override
	public String getCommand() {
		return NumFormat.nice(reg.getValue());
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
		s += " [<span style=\"color:#FF3333;font-family:georgia\">RIF</span> | ";
		s += " <span style=\"color:#FF3333;font-family:georgia\">LRIF</span>] </h1>";
		s += "<p><i>Laplacian Regularized Inverse Filter</i></p>";
		s += "<p>This algorithm is a direct inverse filter with a Laplacian regularization following this formalization: ";
		s += "<b>x</b> = (<b>H</b><sup>T</sup><b>H</b> + &lambda; <b>L</b><sup>T</sup><b>L</b>)<sup>-1</sup> <b>H</b><sup>T</sup><b>y</b>";
		s += "<p> where <b>H</b> is the PSF and <b>L</b> is the discretization of the Laplacian operator. ";
		s += "This regularization tends to reduce high frequencies noisy and in the same time ";
		s += "it tends to blur the image. It is controlled by the regularization factor &lambda;. ";
		s += "</p>";
		s += "<p>RIF or LRIF is very fast. It is non-iterative algorithm</p>";
		s += "<p>When the filtering by <b>L</b><sup>T</sup><b>L</b> has a whitening effect on <b>x</b> ";
		s += "and &lambda; is defined as the inverse of the noise variance, RIF amounts to Wiener filtering.</p>";
		
		s += "<p>Reference: N. Wiener, Extrapolation, Interpolation, and Smoothing of Stationary Time Series, vol. 2, MIT press Cambridge, 1949.</p>";
		return s;
	}

}
