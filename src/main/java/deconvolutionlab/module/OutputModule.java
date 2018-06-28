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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import bilib.table.CustomizedColumn;
import bilib.table.CustomizedTable;
import bilib.tools.NumFormat;
import deconvolution.Command;
import deconvolutionlab.Config;
import deconvolutionlab.Constants;
import deconvolutionlab.Lab;
import deconvolutionlab.dialog.OutputDialog;
import deconvolutionlab.module.dropdownbuttons.AddOutputDropDownButton;
import deconvolutionlab.output.Output;
import deconvolutionlab.output.Output.Action;
import deconvolutionlab.output.Output.View;

public class OutputModule extends AbstractModule implements ActionListener, MouseListener {

	private CustomizedTable	table;
	
	private final int SHOW = 6;
	private final int SAVE = 7;
	private final int DEL = 8;
	private AddOutputDropDownButton add;
	
	public OutputModule() {
		add = new AddOutputDropDownButton("output", "Add...", this);
		create("Output", "", "Clear", add);
	}

	@Override
	public String getCommand() {
		String cmd = " ";
		if (table == null)
			return cmd;
		for (int i = 0; i < table.getRowCount(); i++) {
			String[] values = new String[table.getColumnCount()];

			for(int c=0; c<table.getColumnCount(); c++) {
				values[c] = table.getCell(i, c) == null ? "" : table.getCell(i, c).trim();
			}
			
			if (table.getComboBox(3) != null)
				values[3] = (String)table.getComboBox(3).getSelectedItem();
			if (values[3].startsWith("intact"))
				values[3] = "";
			if (values[3].startsWith("rescaled"))
				values[3] = "rescaled";
			if (values[3].startsWith("normalized"))
				values[3] = "normalized";
			if (values[3].startsWith("clipped"))
				values[3] = "clipped";
			
			if (table.getComboBox(4) != null)
				values[4] = (String)table.getComboBox(4).getSelectedItem();
			if (values[4].startsWith("float"))
				values[4] = "";
			if (values[4].startsWith("byte"))
				values[4] = "byte";
			if (values[4].startsWith("short"))
				values[4] = "short";
			
			cmd += " -out " + values[0] + " " + values[1] + " " + values[2] + " " + 
					values[3] + " " +  values[4] + " " + values[5] + " ";
			if (values[SHOW].equals("\u2610"))
				cmd += " noshow";
			if (values[SAVE].equals("\u2610"))
				cmd += " nosave";
		}
		return cmd;
	}

	public void update() {
		setCommand(getCommand());
		int count = table.getRowCount();
		setSynopsis(count + " output" + (count > 1 ? "s" : ""));
		Command.buildCommand();
		this.getActionButton().setEnabled(table.getRowCount() > 0);
	}

	@Override
	public JPanel buildExpandedPanel() {
		
		String[] dynamics = { 
				"intact (no change) [default]", 
				"rescaled  (linear scaling from min to max)", 
				"normalized (mean=0, stdev=1)", 
				"clipped  (clip to min, saturate to max)"
				};
		String[] types = { 
				"float (32-bits) [default]", 
				"short (16-bits)", 
				"byte (8-bits)" };

		ArrayList<CustomizedColumn> columns = new ArrayList<CustomizedColumn>();
		columns.add(new CustomizedColumn("Mode", String.class, 80, false));
		columns.add(new CustomizedColumn("Snapshot", String.class, 50, false));
		columns.add(new CustomizedColumn("Name", String.class, Constants.widthGUI, true));
		columns.add(new CustomizedColumn("Dynamic", String.class, 100, dynamics, "Select the dynamic range"));
		columns.add(new CustomizedColumn("Type", String.class, 100, types, "Select the type"));
		columns.add(new CustomizedColumn("Origin", String.class, 120, false));
		columns.add(new CustomizedColumn("Show", String.class, 50, false));
		columns.add(new CustomizedColumn("Save", String.class, 50, false));
		columns.add(new CustomizedColumn("Del", String.class, 30, "\u232B", "Delete this image source"));
		table = new CustomizedTable(columns, true);
		table.getColumnModel().getColumn(6).setMaxWidth(50);
		table.getColumnModel().getColumn(7).setMaxWidth(50);
		table.getColumnModel().getColumn(8).setMaxWidth(30);
		table.getColumnModel().getColumn(0).setMaxWidth(100);
		table.getColumnModel().getColumn(3).setMaxWidth(100);
		table.getColumnModel().getColumn(4).setMaxWidth(100);
		
		table.addMouseListener(this);
		
		JToolBar pn = new JToolBar("Controls Image");
		pn.setBorder(BorderFactory.createEmptyBorder());
		pn.setLayout(new GridLayout(1, 6));
		pn.setFloatable(false);
	
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEtchedBorder());
		panel.setLayout(new BorderLayout());
		panel.add(pn, BorderLayout.SOUTH);
		panel.add(table.getMinimumPane(100, 100), BorderLayout.CENTER);

		getActionButton().addActionListener(this);
		
		table.getComboBox(3).addActionListener(this);
		table.getComboBox(4).addActionListener(this);
	
		Config.registerTable(getName(), "output", table);
		return panel;
	}

	public void addOutput(Output out) {
		if (out != null) 
			table.insert(out.getAsString());
		update();
		Command.buildCommand();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		if (e.getSource() == table.getComboBox(3) || e.getSource() == table.getComboBox(2)) {
			update();
			Command.buildCommand();
		}
		
		if (e.getSource() == getActionButton()) {
			table.removeRows();
			update();
			Command.buildCommand();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int row = table.getSelectedRow();
		if (row >= 0 && table.getSelectedColumn() == SHOW) {
			String value = table.getValueAt(row, SHOW).toString();
			table.setValueAt(value.equals("\u2610") ? "\u2612" : "\u2610", row, SHOW);	
		}
		if (row >= 0 && table.getSelectedColumn() == SAVE) {
			String value = table.getValueAt(row, SAVE).toString();
			table.setValueAt(value.equals("\u2610") ? "\u2612" : "\u2610", row, SAVE);	
		}
		if (table.getSelectedColumn() == DEL) {
			table.removeRow(row);
			if (table.getRowCount() > 0)
				table.setRowSelectionInterval(0, 0);
		}
		if (e.getClickCount() == 2) {
			String mode = table.getCell(row, 0).trim().toLowerCase();
			String snapshot = table.getCell(row, 1).trim();
			boolean show = table.getCell(row, 6).trim().endsWith("2612");
			boolean save = table.getCell(row, 7).trim().endsWith("2612");
			Action action = Action.SHOWSAVE;
			if (show & !save)
				action = Action.SHOW;
			if (save & !show)
				action = Action.SAVE;
			
			View view = View.STACK;
			if (mode.startsWith("mip"))
				view = View.MIP;
			if (mode.startsWith("ortho"))
				view = View.ORTHO;
			if (mode.startsWith("fig"))
				view = View.FIGURE;
			if (mode.startsWith("planar"))
				view = View.PLANAR;
			if (mode.startsWith("serie"))
				view = View.SERIES;
			int s = 0;
			if (snapshot.startsWith("@"))
				s = (int)NumFormat.parseNumber(snapshot, 0);
			OutputDialog dlg = new OutputDialog(view, s);
			Lab.setVisible(dlg, true);
			if (dlg.wasCancel()) return;
			Output out = dlg.getOut();
			if (out == null) return;
			out.setAction(action);
			table.insert(out.getAsString());
		}

		update();
		Command.buildCommand();
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void close() {
		getActionButton().removeActionListener(this);
	}


	

}
