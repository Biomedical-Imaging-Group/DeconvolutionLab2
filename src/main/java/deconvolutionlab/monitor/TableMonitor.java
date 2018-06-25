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

package deconvolutionlab.monitor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import bilib.table.CustomizedColumn;
import bilib.table.CustomizedTable;

public class TableMonitor implements AbstractMonitor, ActionListener {

	private CustomizedTable			table;
	private JButton					bnClear		= new JButton("Clear");
	private HashMap<Long, Color>	colors		= new HashMap<Long, Color>();
	private JPanel					panel;
	
	public TableMonitor(int width, int height) {
		ArrayList<CustomizedColumn> columns = new ArrayList<CustomizedColumn>();
		columns.add(new CustomizedColumn("#", Long.class, 60, false));
		columns.add(new CustomizedColumn("Time", String.class, 100, false));
		columns.add(new CustomizedColumn("Memory", String.class, 100, false));
		columns.add(new CustomizedColumn("Message", String.class, Math.max(60, width - 3 * 60), false));

		table = new CustomizedTable(columns, true);
		table.getColumnModel().getColumn(0).setMinWidth(60);
		table.getColumnModel().getColumn(0).setMaxWidth(60);
		table.getColumnModel().getColumn(1).setMaxWidth(100);
		table.getColumnModel().getColumn(1).setMinWidth(100);
		table.getColumnModel().getColumn(2).setMaxWidth(100);
		table.getColumnModel().getColumn(2).setMinWidth(100);

		RowRenderer renderer = new RowRenderer();
		for (int i = 0; i < 4; i++)
			table.getColumnModel().getColumn(i).setCellRenderer(renderer);

		JScrollPane scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(width, height));
		JPanel main = new JPanel(new BorderLayout());

		main.add(scroll, BorderLayout.CENTER);
		bnClear.addActionListener(this);
		panel = new JPanel(new BorderLayout());
		panel.add(main);
		panel.setBorder(BorderFactory.createEtchedBorder());
	}

	public JPanel getPanel() {
		return panel;
	}

	@Override
	public void clear() {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.setRowCount(0);
	}

	@Override
	public void add(Message message) {
		String msg[] = message.formatArray();
		int n = msg.length;
		Object[] row = new Object[n + 1];
		row[0] = message.getID();
		for (int i = 0; i < n; i++)
			row[i + 1] = msg[i];
		table.append(row);

		 Verbose level = message.getLevel();
		Color c = new Color(0, 0, 0);
		if (level ==  Verbose.Prolix)
			c = new Color(255, 0, 0);
		else if (level ==  Verbose.Quiet)
			c = new Color(200, 200, 0);
		colors.put(new Long(message.getID()), c);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bnClear) {
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			model.setRowCount(0);
		}
	}

	@Override
	public String getName() {
		return "table";
	}
	
	class RowRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			DefaultTableModel model = (DefaultTableModel) table.getModel();
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (row >= 0) {
				Long id = (Long) model.getValueAt(row, 0);
				Color color = colors.get(id);
				c.setForeground(color);
			}
			return c;
		}
	}

}
