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

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bilib.component.GridPanel;
import deconvolution.Command;
import deconvolution.algorithm.AlgorithmList;
import deconvolutionlab.Config;
import fft.FFT;

public class ComputationModule extends AbstractModule implements ActionListener, ChangeListener {

	private JComboBox<String>	cmbFFT;
	private JComboBox<String>	cmbEpsilon;
	boolean init = false;
	
	public ComputationModule() {
		create("Computation", "", "Default");
	}

	@Override
	public String getCommand() {
		String cmd = "";
		if (cmbFFT.getSelectedIndex() != 0)
			cmd += " -fft " + FFT.getLibraryByName((String) cmbFFT.getSelectedItem()).getLibraryName();
		if (cmbEpsilon.getSelectedIndex() != 6)
			cmd += " -epsilon " + (String) cmbEpsilon.getSelectedItem();
		return cmd;
	}

	@Override
	public JPanel buildExpandedPanel() {
		cmbFFT = new JComboBox<String>(FFT.getLibrariesAsArray());
		cmbEpsilon = new JComboBox<String>(new String[] { "1E-0", "1E-1", "1E-2", "1E-3", "1E-4", "1E-5", "1E-6", "1E-7", "1E-8", "1E-9", "1E-10", "1E-11", "1E-12" });
		cmbEpsilon.setSelectedItem("1E-6");
	
		GridPanel pnNumeric = new GridPanel(false, 2);
		pnNumeric.place(1, 0, "norm");
		pnNumeric.place(3, 0, new JLabel("fft"));
		pnNumeric.place(3, 1, cmbFFT);
		pnNumeric.place(3, 2, new JLabel("FFT library (Fourier)"));
		
		
		
		pnNumeric.place(9, 0, new JLabel("epsilon"));
		pnNumeric.place(9, 1, cmbEpsilon);
		pnNumeric.place(9, 2, new JLabel("<html>Machine Epsilon &epsilon;</html>"));
	
		JScrollPane scroll = new JScrollPane(pnNumeric);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEtchedBorder());
		panel.add(scroll, BorderLayout.CENTER);

		Config.register(getName(), "fft", cmbFFT, AlgorithmList.getDefaultAlgorithm());
		Config.register(getName(), "epsilon", cmbEpsilon, "1E-6");
	
		cmbFFT.addActionListener(this);
		cmbEpsilon.addActionListener(this);
		getActionButton().addActionListener(this);
		init = true;
		return panel;
	}

	private void update() {
		setCommand(getCommand());
		if (init)
			setSynopsis(" " + FFT.getLibraryByName((String) cmbFFT.getSelectedItem()).getLibraryName());
		Command.buildCommand();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		update();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		if (e.getSource() == getActionButton()) {
			cmbFFT.setSelectedIndex(0);
			cmbEpsilon.setSelectedIndex(0);
		}
		update();
	}

	@Override
	public void close() {
		getActionButton().removeActionListener(this);
		cmbFFT.removeActionListener(this);
		cmbEpsilon.removeActionListener(this);
	}
}
