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
import bilib.component.SpinnerRangeDouble;
import bilib.component.SpinnerRangeInteger;
import bilib.tools.NumFormat;
import deconvolution.Command;
import deconvolutionlab.Config;
import deconvolutionlab.Constants;

public class VanCittertPanel extends AlgorithmPanel implements ChangeListener {

	private SpinnerRangeInteger	spnIter	= new SpinnerRangeInteger(10, 1, 99999, 1);
	private SpinnerRangeDouble	spnStep	= new SpinnerRangeDouble(1, 0, 2, 0.1);

	private VanCittert			algo	= new VanCittert(10, 1);

	@Override
	public JPanel getPanelParameters() {
		double[] params = algo.getDefaultParameters();

		spnIter.setPreferredSize(Constants.dimParameters);
		spnStep.setPreferredSize(Constants.dimParameters);
		GridPanel pn = new GridPanel(false);
		pn.place(1, 0, "<html><span \"nowrap\"><b>Iterations</b></span></html>");
		pn.place(1, 1, "<html><span \"nowrap\"><i>N</i></span></html>");
		pn.place(1, 2, spnIter);
		pn.place(1, 3, "<html><span \"nowrap\">Step <i>&gamma;</i></span></html>");
		pn.place(1, 4, spnStep);

		Config.register("Algorithm." + algo.getShortnames()[0], "iterations", spnIter, params[0]);
		Config.register("Algorithm." + algo.getShortnames()[0], "step", spnStep, params[1]);
		spnIter.addChangeListener(this);
		spnStep.addChangeListener(this);
		return pn;
	}

	@Override
	public String getCommand() {
		int iter = spnIter.get();
		double gamma = spnStep.get();
		return iter + " " + NumFormat.nice(gamma);
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
		s += "<h1>" + getName();
		s += " [<span style=\"color:#FF3333;font-family:georgia\">VC</span>]</h1>";
		s += "<p>The simplest iterative algorithm for image deconvolution is the Van Cittert algorithm (1930)</p>";
		s += "<p>The algorithm is sensitive to the presence of noise and tends to increase the noise </p>";
		return s;
	}
}
