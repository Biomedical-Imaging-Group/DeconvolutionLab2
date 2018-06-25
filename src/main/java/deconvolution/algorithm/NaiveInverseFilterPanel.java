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

import bilib.component.GridPanel;

public class NaiveInverseFilterPanel extends AlgorithmPanel {

	private NaiveInverseFilter algo = new NaiveInverseFilter();

	@Override
	public JPanel getPanelParameters() {
		GridPanel pn = new GridPanel(false);
		pn.place(1, 0, "<html><span \"nowrap\">NIF is parameter-free</span></html>");
		return pn;
	}

	@Override
	public String getCommand() {
		return "";
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
		s += " [<span style=\"color:#FF3333;font-family:georgia\">NIF</span> | ";
		s += " <span style=\"color:#FF3333;font-family:georgia\">IF</span>] </h1>";
		s += "<p>The simplest approach to deconvolution consists in minimizing a least-squares cost function. ";
		s += "Unfortunately, the NIF tends to amplify measurement noise, resulting in spurious high-frequency oscillations. ";
		s += "It corresponds to maximum-likelihood estimation in the presence of Gaussian noise. ";
		return s;
	}

}
