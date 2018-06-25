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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bilib.component.GridPanel;
import deconvolution.Command;
import deconvolution.algorithm.Constraint;
import deconvolutionlab.Config;

public class ControllerModule extends AbstractModule implements ActionListener, ChangeListener, KeyListener {

	private JTextField			txtResidu;
	private JTextField			txtTime;
	private JTextField			txtIterations;

	private JComboBox<String>	cmbConstraint;
	private JComboBox<String>	cmbStats;
	private JComboBox<String>	cmbMonitor;
	private JComboBox<String>	cmbVerbose;

	private JCheckBox			chkResidu;
	private JCheckBox			chkTime;

	public ControllerModule() {
		create("Controller", "", "Default");
	}

	@Override
	public String getCommand() {
		String cmd = "";
		if (cmbMonitor.getSelectedIndex() != 0)
			cmd += "-monitor " + cmbMonitor.getSelectedItem() + " ";
		if (cmbVerbose.getSelectedIndex() != 0)
			cmd += "-verbose " + cmbVerbose.getSelectedItem() + " ";
		if (cmbStats.getSelectedIndex() != 0)
			cmd += "-stats " + cmbStats.getSelectedItem() + " ";
		if (cmbConstraint.getSelectedIndex() != 0)
			cmd += "-constraint " + cmbConstraint.getSelectedItem() + " ";
		if (chkResidu.isSelected())
			cmd += "-residu " + txtResidu.getText() + " ";
		if (chkTime.isSelected())
			cmd += "-time " + txtTime.getText() + " ";
		return cmd;
	}

	@Override
	public JPanel buildExpandedPanel() {

		chkTime = new JCheckBox("");
		chkResidu = new JCheckBox("");

		txtResidu = new JTextField("0.01");
		txtTime = new JTextField("3600");
		txtIterations = new JTextField("Iteration max (mandatory)");
		txtIterations.setEditable(false);
			
		cmbMonitor = new JComboBox<String>(new String[] {"console table", "console", "table", "no" });
		cmbVerbose = new JComboBox<String>(new String[] {"log", "quiet", "mute", "prolix" });
		cmbConstraint = new JComboBox<String>(Constraint.getContraintsAsArray());
		cmbStats = new JComboBox<String>(new String[] {"no", "show", "show + save", "save"});
	
		GridPanel pn = new GridPanel(true, 2);

		pn.place(0, 0, "monitor");
		pn.place(0, 2, cmbMonitor);
		pn.place(0, 3, "Monitoring message");
		
		pn.place(1, 0, "verbose");
		pn.place(1, 2, cmbVerbose);
		pn.place(1, 3, "");

		pn.place(3, 0, "stats");
		pn.place(3, 2, cmbStats);
		pn.place(3, 3, "Signal's statistics");

		pn.place(4, 0, "constraint");
		pn.place(4, 2, cmbConstraint);
		pn.place(4, 3, "Additional constraint");

		pn.place(5, 0, "residu");
		pn.place(5, 1, chkResidu);
		pn.place(5, 2, txtResidu);
		pn.place(5, 3, "Additional stopping criteria");
		
		pn.place(6, 0, "time");
		pn.place(6, 1, chkTime);
		pn.place(6, 2, txtTime);
		pn.place(6, 3, "Additional stopping criteria");
		
		JScrollPane scroll = new JScrollPane(pn);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(scroll, BorderLayout.CENTER);


		Config.register(getName(), "residu.enable", chkResidu, false);
		Config.register(getName(), "time.enable", chkTime, false);
		
		Config.register(getName(), "residu.value", txtResidu, "0.01");
		Config.register(getName(), "time.value", txtTime, "3600");
		Config.register(getName(), "constraint", cmbConstraint, cmbConstraint.getItemAt(0));
		Config.register(getName(), "stats", cmbStats, cmbStats.getItemAt(0));
		Config.register(getName(), "monitor", cmbMonitor, cmbMonitor.getItemAt(0));
		Config.register(getName(), "verbose", cmbVerbose, cmbVerbose.getItemAt(0));
		
		chkResidu.addChangeListener(this);
		chkTime.addChangeListener(this);

		txtResidu.addKeyListener(this);
		txtTime.addKeyListener(this);
		cmbConstraint.addActionListener(this);
		cmbMonitor.addActionListener(this);
		cmbVerbose.addActionListener(this);
		getActionButton().addActionListener(this);

		return panel;
	}

	private void update() {
		
		setCommand(getCommand());
		int count = 0;
		count += (chkResidu.isSelected() ? 1 : 0);
		count += (chkTime.isSelected() ? 1 : 0);
		setSynopsis("" + count + " controls");
		
		Command.buildCommand();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		if (e.getSource() == getActionButton()) {
			chkResidu.removeChangeListener(this);
			chkTime.removeChangeListener(this);
			
			chkResidu.setSelected(false);
			chkTime.setSelected(false);
			
			txtResidu.setText("0.01");
			txtTime.setText("3600");
			cmbConstraint.setSelectedIndex(0);
			cmbStats.setSelectedIndex(0);
			cmbMonitor.setSelectedIndex(0);
			cmbVerbose.setSelectedIndex(0);
	
			chkResidu.addChangeListener(this);
			chkTime.addChangeListener(this);	
		}
		update();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		update();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		update();
	}
	
	@Override
	public void close() {
		chkResidu.removeChangeListener(this);
		cmbVerbose.removeActionListener(this);
		cmbMonitor.removeActionListener(this);
		cmbConstraint.removeActionListener(this);
		chkTime.removeChangeListener(this);
		getActionButton().removeChangeListener(this);
	}
}
