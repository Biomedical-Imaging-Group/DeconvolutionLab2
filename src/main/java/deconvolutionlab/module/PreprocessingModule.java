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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bilib.component.GridPanel;
import bilib.component.SpinnerRangeInteger;
import bilib.tools.NumFormat;
import deconvolution.Command;
import deconvolution.Deconvolution;
import deconvolutionlab.Config;
import deconvolutionlab.monitor.Monitors;
import signal.RealSignal;
import signal.apodization.AbstractApodization;
import signal.apodization.Apodization;
import signal.apodization.UniformApodization;
import signal.padding.AbstractPadding;
import signal.padding.NoPadding;
import signal.padding.Padding;

public class PreprocessingModule extends AbstractModule implements ActionListener, ChangeListener {

	private JComboBox<String>	cmbPadXY;
	private JComboBox<String>	cmbPadZ;
	private JComboBox<String>	cmbApoXY;
	private JComboBox<String>	cmbApoZ;
	private SpinnerRangeInteger	spnExtensionXY;
	private SpinnerRangeInteger	spnExtensionZ;
	private JLabel		        lblPad;
	private JLabel		        lblApo;
	private JComboBox<String>	cmbNormalization;
	private JButton		        bnTest;
	
	public PreprocessingModule() {
		create("Preprocessing", "", "Default");
	}

	@Override
	public String getCommand() {
		AbstractPadding pxy = Padding.getByName((String) cmbPadXY.getSelectedItem());
		AbstractPadding paz = Padding.getByName((String) cmbPadZ.getSelectedItem());
		AbstractApodization axy = Apodization.getByName((String) cmbApoXY.getSelectedItem());
		AbstractApodization apz = Apodization.getByName((String) cmbApoZ.getSelectedItem());
		boolean ext = spnExtensionXY.get() + spnExtensionZ.get() > 0;
		String extXY = (ext ? "" + spnExtensionXY.get() : "") + " ";
		String extZ = ext ? "" + spnExtensionZ.get() : "";
		String cmd = "";
		if (!(pxy instanceof NoPadding) || !(paz instanceof NoPadding) || spnExtensionXY.get() > 0 || spnExtensionZ.get() > 0)
			cmd += " -pad " + pxy.getShortname() + " " + paz.getShortname() + " " + extXY + extZ;
		if (!(axy instanceof UniformApodization) || !(apz instanceof UniformApodization))
			cmd += " -apo " + axy.getShortname() + " " + apz.getShortname() + " ";
		if (cmbNormalization.getSelectedIndex() != 0)
			cmd += " -norm  " + NumFormat.parseNumber((String)cmbNormalization.getSelectedItem(), 1);
		return cmd;
	}

	@Override
	public JPanel buildExpandedPanel() {
		bnTest = new JButton("Test");
		
		lblPad = new JLabel("Information on padding size");
		lblPad.setBorder(BorderFactory.createEtchedBorder());
		lblApo = new JLabel("Information on apodization energy");
		lblApo.setBorder(BorderFactory.createEtchedBorder());
		cmbPadXY = new JComboBox<String>(Padding.getPaddingsAsArray());
		cmbPadZ = new JComboBox<String>(Padding.getPaddingsAsArray());
		cmbApoXY = new JComboBox<String>(Apodization.getApodizationsAsArray());
		cmbApoZ = new JComboBox<String>(Apodization.getApodizationsAsArray());
		spnExtensionXY = new SpinnerRangeInteger(0, 0, 99999, 1);
		spnExtensionZ = new SpinnerRangeInteger(0, 0, 99999, 1);
		cmbNormalization = new JComboBox<String>(new String[] { "1", "10", "1000", "1E+6", "1E+9", "no" });
		
		cmbNormalization.addActionListener(this);
		cmbNormalization.setSelectedIndex(0);
		cmbNormalization.removeActionListener(this);
	
		GridPanel pnBorder = new GridPanel(false, 2);
		pnBorder.place(0, 0, "<html><b>Image</b>");
		pnBorder.place(0, 1, "Lateral (XY)");
		pnBorder.place(0, 2, "Axial (Z)");
		pnBorder.place(1, 0, "Apodization");
		pnBorder.place(1, 1, cmbApoXY);
		pnBorder.place(1, 2, cmbApoZ);

		pnBorder.place(2, 0, "Padding Extension");
		pnBorder.place(2, 1, spnExtensionXY);
		pnBorder.place(2, 2, spnExtensionZ);

		pnBorder.place(3, 0, "Padding Constraint");
		pnBorder.place(3, 1, cmbPadXY);
		pnBorder.place(3, 2, cmbPadZ);
		pnBorder.place(4, 0, 3, 1, lblPad);
		pnBorder.place(5, 0, 3, 1, lblApo);
		pnBorder.place(6, 2, 3, 1, bnTest);
		pnBorder.place(7, 0, "  ");
		pnBorder.place(8, 0, "<html><b>PSF</b>");
		pnBorder.place(9, 0, "Normalization");
		pnBorder.place(9, 1, cmbNormalization);
		pnBorder.place(9, 2, "recommended: 1");

		JScrollPane scroll = new JScrollPane(pnBorder);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEtchedBorder());
		panel.add(scroll, BorderLayout.CENTER);
	
		Config.register(getName(), "padxy", cmbPadXY, Padding.getDefault().getName());
		Config.register(getName(), "padz", cmbPadZ, Padding.getDefault().getName());
		Config.register(getName(), "apoxy", cmbApoXY, Apodization.getDefault().getName());
		Config.register(getName(), "apoz", cmbApoZ, Apodization.getDefault().getName());
		Config.register(getName(), "extxy", spnExtensionXY, "0");
		Config.register(getName(), "extz", spnExtensionZ, "0");
		Config.register(getName(), "normalization", cmbNormalization, cmbNormalization.getItemAt(0));
		
		cmbNormalization.addActionListener(this);
		spnExtensionXY.addChangeListener(this);
		spnExtensionZ.addChangeListener(this);
		cmbPadXY.addActionListener(this);
		cmbPadZ.addActionListener(this);
		cmbApoXY.addActionListener(this);
		cmbApoZ.addActionListener(this);
		bnTest.addActionListener(this);
		getActionButton().addActionListener(this);
		return panel;
	}

	private void update() {
		setCommand(getCommand());
		boolean ext = spnExtensionXY.get() + spnExtensionZ.get() > 0;
		boolean pad = cmbPadXY.getSelectedIndex() + cmbPadZ.getSelectedIndex() > 0;
		boolean apo = cmbApoXY.getSelectedIndex() + cmbApoZ.getSelectedIndex() > 0;
		if (pad || apo || ext) {
			setSynopsis("" + " " + (pad ? "Padding" : "") + " " + (ext ? "Extension" : "") + " " + (apo ? "Apodization" : ""));
		}
		else {
			setSynopsis("Default options");
		}

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
			cmbPadXY.removeActionListener(this);
			cmbPadZ.removeActionListener(this);
			cmbApoXY.removeActionListener(this);
			cmbApoZ.removeActionListener(this);
			cmbPadXY.setSelectedIndex(0);
			cmbPadZ.setSelectedIndex(0);
			cmbApoXY.setSelectedIndex(0);
			cmbApoZ.setSelectedIndex(0);
			spnExtensionXY.set(0);
			spnExtensionZ.set(0);
			cmbNormalization.setSelectedIndex(0);
			cmbPadXY.addActionListener(this);
			cmbPadZ.addActionListener(this);
			cmbApoXY.addActionListener(this);
			cmbApoZ.addActionListener(this);
			update();
			return;
		}
		if (e.getSource() == bnTest) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					getActionButton().setEnabled(false);
					Deconvolution d = new Deconvolution("CheckImage", Command.buildCommand());
					Apodization apo = d.getController().getApodization();
					if (apo == null) {
						lblApo.setText("Error in Apodization");
						return;
					}
					Padding pad = d.getController().getPadding();
					if (pad == null) {
						lblPad.setText("Error in Padding");
						return;
					}
					RealSignal x = d.openImage();
					if (x == null) {
						lblPad.setText("Error in input image");
						lblApo.setText("Error in input image");
						return;
					}
					Monitors m = Monitors.createDefaultMonitor();
					RealSignal y = pad.pad(m, x);
					apo.apodize(m, y);
					lblPad.setText(x.dimAsString() + " > " + y.dimAsString());
					lblApo.setText(NumFormat.nice(x.getStats()[5]) + " > " + NumFormat.nice(y.getStats()[5]));
					getActionButton().setEnabled(true);
				}
			});
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
			return;
		}
		update();
	}

	@Override
	public void close() {
		cmbPadXY.removeActionListener(this);
		cmbPadZ.removeActionListener(this);
		cmbApoXY.removeActionListener(this);
		cmbApoZ.removeActionListener(this);
		getActionButton().removeActionListener(this);
		spnExtensionXY.removeChangeListener(this);
		spnExtensionZ.removeChangeListener(this);
		cmbNormalization.removeActionListener(this);
		bnTest.removeActionListener(this);
	}


}
