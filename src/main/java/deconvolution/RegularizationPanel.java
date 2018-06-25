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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import bilib.tools.NumFormat;

public class RegularizationPanel extends JPanel {

	private JSlider		slider	= new JSlider();
	private JTextField	txt		= new JTextField(10);
	private double		base		= Math.pow(10, 1. / 3.0);
	private double		logbase	= Math.log(base);
	
	public RegularizationPanel(double value) {
		setLayout(new BorderLayout());
		slider.setMinimum(-54);
		slider.setMaximum(30);
		slider.setPreferredSize(new Dimension(200, 40));
		int p = (int) Math.round(Math.log(Math.abs(value)) / logbase);
		slider.setValue(p);

		Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
		JLabel lbl0 = new JLabel("Off");
		JLabel lbl1 = new JLabel("Low");
		JLabel lbl2 = new JLabel("High");
		JLabel lbl4 = new JLabel("1E-6");
		JLabel lbl5 = new JLabel("1.0");
		java.awt.Font font = lbl1.getFont();
		java.awt.Font small = new java.awt.Font(font.getFamily(), font.getStyle(), font.getSize() - 3);
		lbl0.setFont(small);
		lbl1.setFont(small);
		lbl2.setFont(small);
		lbl4.setFont(small);
		lbl5.setFont(small);
		labels.put(-54, lbl0);
		labels.put(-36, lbl1);
		labels.put(-18, lbl4);
		labels.put(0, lbl5);
		labels.put(27, lbl2);
		slider.setMinorTickSpacing(3);
		// js.setSnapToTicks(true);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setMajorTickSpacing(9);
		slider.setLabelTable(labels);
		add(new JLabel("<html>Reg. &lambda;</html>"), BorderLayout.WEST);
		add(slider, BorderLayout.CENTER);
		add(txt, BorderLayout.EAST);
	}
	
	public JSlider getSlider() {
		return slider;
	}
	
	public JTextField getText() {
		return txt;
	}
	
	public void updateFromSlider() {
		double d = Math.pow(base, slider.getValue());
		d = Math.min(d, Math.pow(base, slider.getMaximum()));
		d = Math.max(d, Math.pow(base, slider.getMinimum()));
		txt.setText(NumFormat.nice(d));
	}

	public void updateFromText() {
		String typed = txt.getText();
		double value = NumFormat.parseNumber(typed, 1);
		int p = (int) Math.round(Math.log(Math.abs(value)) / logbase);
		slider.setValue(p);
	}

	public double getValue() {
		return Math.pow(base, slider.getValue());	
	}
}
