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

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bilib.component.GridPanel;
import bilib.component.SpinnerRangeInteger;
import bilib.tools.NumFormat;
import deconvolution.Command;
import deconvolution.RegularizationPanel;
import deconvolutionlab.Config;

public class RichardsonLucyTVPanel extends AlgorithmPanel implements KeyListener, ActionListener, ChangeListener {

	private SpinnerRangeInteger	spnIter	= new SpinnerRangeInteger(10, 1, 99999, 1);
	private RegularizationPanel	reg;

	private RichardsonLucyTV	algo	= new RichardsonLucyTV(10, 0.1);

	@Override
	public JPanel getPanelParameters() {
		double[] params = algo.getDefaultParameters();
		reg = new RegularizationPanel(params[1]);
		GridPanel pn = new GridPanel(false);
		pn.place(1, 0, "<html><span \"nowrap\"><b>Iterations</b></span></html>");
		pn.place(1, 1, "<html><span \"nowrap\"><i>N</i></span></html>");
		pn.place(1, 2, spnIter);
		pn.place(2, 0, 3, 1, reg);
		Config.register("Algorithm." + algo.getShortnames()[0], "reg", reg.getText(), "0.1");
		reg.getText().addKeyListener(this);
		reg.getSlider().addChangeListener(this);
		spnIter.addChangeListener(this);
		return pn;
	}

	@Override
	public String getCommand() {
		double lambda = reg.getValue();
		return spnIter.get() + " " + NumFormat.nice(lambda);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		reg.getText().removeKeyListener(this);
		reg.updateFromSlider();
		Command.buildCommand();
		reg.getText().addKeyListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Command.buildCommand();
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
		s += " [<span style=\"color:#FF3333;font-family:georgia\">RLTV</span>]</h1>";
		s += "<p>This algorithm is a combinaison of the Richardson–Lucy algorithm with a regularization constraint based on Total Variation, which tends to reduce unstable oscillations while preserving object edges.</p>";
		s += "<p>It is a iterative algorithm, relative slow to compute the Total Variation at every iteration.</p>";
		s += "<p>It has a weighted parameter &lambda; to control the effect of the total variation.</p>";
		s += "<p></p>";
		s += "<h3>Reference: N. Dey et al., Richardson–Lucy algorithm with total variation regularization for 3D confocal microscope deconvolution, Microsc. Res. Tech. 69, 2006. </p>";
		return s;
	}
}
