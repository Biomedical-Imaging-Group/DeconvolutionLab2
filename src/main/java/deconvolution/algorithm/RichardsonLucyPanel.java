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

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bilib.component.GridPanel;
import bilib.component.SpinnerRangeInteger;
import deconvolution.Command;
import deconvolutionlab.Config;

public class RichardsonLucyPanel extends AlgorithmPanel implements ChangeListener {

	private SpinnerRangeInteger	spnIter	= new SpinnerRangeInteger(10, 1, 99999, 1);

	private RichardsonLucy		algo	= new RichardsonLucy(10);

	@Override
	public JPanel getPanelParameters() {
		double[] params = algo.getDefaultParameters();
		GridPanel pn = new GridPanel(false);
		pn.place(1, 0, "<html><span \"nowrap\"><b>Iterations</b></span></html>");
		pn.place(1, 1, "<html><span \"nowrap\"><i>N</i></span></html>");
		pn.place(1, 2, spnIter);
		Config.register("Algorithm." + algo.getShortnames()[0], "iterations", spnIter, params[0]);
		spnIter.addChangeListener(this);
		return pn;
	}

	@Override
	public String getCommand() {
		return "" + spnIter.get();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Command.buildCommand();
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
		s += "<h1>" + getName() + "";
		s += " [<span style=\"color:#FF3333;font-family:georgia\">RL</span>]</h1>";
		s += "<p>This is the well-known Richardson-Lucy algorithm.</p>";
		s += "<p>It is an iterative with a slow convergence, it has only one parameter to tune: the maximum number of iterations</p>";
		s += "<p>RL assumes that the noise follows a Poisson distribution.</p>";
		s += "<p>It is a maximum likelihood estimator (MLE).</p>";
		s += "<p>Warning: the input image should have only positive values</p>";
		s += "<p>References:</p>";
		s += "<p>W.H. Richardson, Bayesian-based iterative method of image restoration, J. Optical Soc. Am. 62, 1972.";
		s += "<p>L.B. Lucy, An iterative technique for the rectification of observed distributions, Astrophys. J. 79 (6), 1974.";
		return s;
	}
}
