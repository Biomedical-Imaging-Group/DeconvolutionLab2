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
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import bilib.component.GridPanel;
import bilib.table.CustomizedColumn;
import bilib.tools.NumFormat;
import deconvolution.algorithm.AlgorithmList;
import deconvolutionlab.Config;
import deconvolutionlab.Constants;
import deconvolutionlab.system.SystemInfo;
import deconvolutionlab.system.SystemUsage;
import fft.AbstractFFTLibrary;
import fft.FFT;

public class ResourcesModule extends AbstractModule implements ActionListener {

	private JComboBox<String>	cmbFFT;
	private JComboBox<String>	cmbEpsilon;
	private JComboBox<String>	cmbMultithreading;
	private JComboBox<String>	cmbSystem;
	private JLabel				lblFile;
	private JLabel				lblJava;
	
	private JButton				bnSystem;
	
	public ResourcesModule() {
		create("Resources", "", "Default");
	}

	@Override
	public String getCommand() {
		String cmd = " ";
		if (cmbFFT.getSelectedIndex() != 0)
			cmd += " -fft " + FFT.getLibraryByName((String) cmbFFT.getSelectedItem()).getLibraryName();
		if (cmbEpsilon.getSelectedIndex() != 6)
			cmd += " -epsilon " + (String) cmbEpsilon.getSelectedItem();
		if (cmbMultithreading.getSelectedIndex() != 0)
			cmd += " -multithreading no";
		if (cmbSystem.getSelectedIndex() != 0)
			cmd += " -system no";
		return cmd;
	}

	public void update() {
		AbstractFFTLibrary library = FFT.getLibraryByName((String) cmbFFT.getSelectedItem());
		setCommand(getCommand());
		setSynopsis(library.getLibraryName());
	}

	@Override
	public JPanel buildExpandedPanel() {

		bnSystem = new JButton("Show Resources Panel");
		
		lblJava = new JLabel("Version: " + System.getProperty("java.version"));
		lblJava.setBorder(BorderFactory.createEtchedBorder());
		double maxi = SystemUsage.getTotalSpace();
		double used = maxi - SystemUsage.getAvailableSpace();

		lblFile = new JLabel("Space: " + NumFormat.bytes(used));
		lblFile.setBorder(BorderFactory.createEtchedBorder());

		cmbFFT = new JComboBox<String>(FFT.getLibrariesAsArray());
		cmbEpsilon = new JComboBox<String>(new String[] { "1E-0", "1E-1", "1E-2", "1E-3", "1E-4", "1E-5", "1E-6", "1E-7", "1E-8", "1E-9", "1E-10", "1E-11", "1E-12" });
		cmbEpsilon.setSelectedItem("1E-6");
		cmbMultithreading = new JComboBox<String>(new String[] { "yes", "no" });
		cmbSystem = new JComboBox<String>(new String[] { "yes", "no" });

		ArrayList<CustomizedColumn> columns = new ArrayList<CustomizedColumn>();
		columns.add(new CustomizedColumn("FFT Library", String.class, 100, false));
		columns.add(new CustomizedColumn("Installed", String.class, 40, false));
		columns.add(new CustomizedColumn("Installation", String.class, Constants.widthGUI, false));
		
		GridPanel pn = new GridPanel(false, 4);
		pn.place(0, 0, new JLabel("fft"));
		pn.place(0, 1, cmbFFT);
		pn.place(0, 2, new JLabel("FFT Library"));

		pn.place(1, 0, new JLabel("espilon"));
		pn.place(1, 1, cmbEpsilon);
		pn.place(1, 2, new JLabel("Machine epsilon"));

		pn.place(2, 0, new JLabel("Java"));
		pn.place(2, 1, 2, 1, lblJava);

		pn.place(3, 0, new JLabel("File"));
		pn.place(3, 1, 2, 1, lblFile);
		pn.place(4, 1, 2, 1, bnSystem);
		
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEtchedBorder());
		panel.setLayout(new BorderLayout());
		panel.add(pn, BorderLayout.CENTER);
	
		Config.register(getName(), "fft", cmbFFT, AlgorithmList.getDefaultAlgorithm());
		Config.register(getName(), "epsilon", cmbEpsilon, "1E-6");
		Config.register(getName(), "multithreading", cmbMultithreading, cmbMultithreading.getItemAt(0));
		Config.register(getName(), "system", cmbSystem, cmbSystem.getItemAt(0));

		cmbMultithreading.addActionListener(this);
		cmbFFT.addActionListener(this);
		cmbEpsilon.addActionListener(this);
		cmbSystem.addActionListener(this);
		getActionButton().addActionListener(this);
		bnSystem.addActionListener(this);
		
		update();
		return panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		if (e.getSource() == getActionButton()) {
			cmbFFT.setSelectedIndex(0);
			cmbEpsilon.setSelectedItem("1E-6");
			cmbMultithreading.setSelectedIndex(0);
			cmbSystem.setSelectedIndex(0);
		}
		else if (e.getSource() == bnSystem)
			SystemInfo.activate();

		update();
	}

	@Override
	public void close() {
		cmbSystem.removeActionListener(this);
		cmbFFT.removeActionListener(this);
		cmbEpsilon.removeActionListener(this);
		cmbMultithreading.removeActionListener(this);
		getActionButton().removeActionListener(this);
		bnSystem.removeActionListener(this);
	}
}
