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
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

import bilib.table.CustomizedColumn;
import bilib.table.CustomizedTable;
import deconvolution.Deconvolution;
import deconvolutionlab.Constants;

public class BatchModule extends AbstractModule implements MouseListener, ActionListener {

	private CustomizedTable	table;
	private JButton			bnRun;
	private JButton			bnLaunch;

	public BatchModule() {
		create("Batch");
	}

	@Override
	public String getCommand() {
		return "";
	}

	@Override
	public JPanel buildExpandedPanel() {

		bnRun = new JButton("Run Jobs");
		bnLaunch = new JButton("Launch Jobs");

		ArrayList<CustomizedColumn> columns = new ArrayList<CustomizedColumn>();
		columns.add(new CustomizedColumn("Job", String.class, 120, false));
		columns.add(new CustomizedColumn("Command", String.class, Constants.widthGUI, false));
		columns.add(new CustomizedColumn("", String.class, 30, "\u232B", "Delete this job"));

		table = new CustomizedTable(columns, true);
		table.getColumnModel().getColumn(2).setMaxWidth(30);
		table.getColumnModel().getColumn(2).setMinWidth(30);
		table.addMouseListener(this);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		JToolBar pn = new JToolBar("Controls Batch");
		pn.setBorder(BorderFactory.createEmptyBorder());
		pn.setLayout(new GridLayout(1, 2));
		pn.setFloatable(false);
		pn.add(bnRun);
		pn.add(bnLaunch);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(table.getPane(100, 100), BorderLayout.CENTER);
		panel.add(pn, BorderLayout.SOUTH);
		getActionButton().addActionListener(this);

		bnRun.addActionListener(this);
		bnLaunch.addActionListener(this);
		return panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		if (e.getSource() == bnRun) {
			if (table.getSelectedRows().length == 0)
				table.setColumnSelectionInterval(0, table.getRowCount());
			int rows[] = table.getSelectedRows();
			for (int row : rows)
				new Deconvolution("Batch" + table.getCell(row, 0), table.getCell(row, 1)).deconvolve();
		}
		else if (e.getSource() == bnLaunch) {
			if (table.getSelectedRows().length == 0)
				table.setColumnSelectionInterval(0, table.getRowCount());
			int rows[] = table.getSelectedRows();
			for (int row : rows)
				new Deconvolution("Batch " + table.getCell(row, 0), table.getCell(row, 1)).launch();
		}
		update();
	}

	private void update() {
		setSynopsis("" + table.getRowCount() + " jobs");
	}

	public int getCountJob() {
		return table.getRowCount();
	}

	public void addJob(String name, String command) {
		table.append(new String[] { name, command, "" });
		update();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == table) {
			int row = table.getSelectedRow();
			if (row < 0)
				return;
			if (table.getSelectedColumn() == 2) {
				table.removeRow(row);
				if (table.getRowCount() > 0)
					table.setRowSelectionInterval(0, 0);
			}
		}
		update();
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
