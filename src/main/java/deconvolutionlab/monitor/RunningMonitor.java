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
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;

import bilib.table.CustomizedColumn;
import bilib.table.CustomizedTable;
import bilib.tools.NumFormat;
import deconvolutionlab.system.SystemUsage;

public class RunningMonitor implements AbstractMonitor {

	private CustomizedTable			table;
	private JPanel					panel;

	private double chrono			= System.nanoTime();
	private double peak = 0;
	
	public RunningMonitor(int width, int height) {
		ArrayList<CustomizedColumn> columns = new ArrayList<CustomizedColumn>();
		columns.add(new CustomizedColumn("Time", String.class, 100, false));
		columns.add(new CustomizedColumn("Memory", String.class, 100, false));
		columns.add(new CustomizedColumn("Iteration", String.class, 60, false));
		columns.add(new CustomizedColumn("Message", String.class, Math.max(60, width - 4*100), false));

		table = new CustomizedTable(columns, true);
		table.getColumnModel().getColumn(0).setMinWidth(40);
		table.getColumnModel().getColumn(0).setMaxWidth(80);
		table.getColumnModel().getColumn(1).setMinWidth(40);
		table.getColumnModel().getColumn(1).setMaxWidth(80);
		table.getColumnModel().getColumn(2).setMinWidth(40);
		table.getColumnModel().getColumn(2).setMaxWidth(80);

		JScrollPane scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(width, height));

		JToolBar main = new JToolBar();
		main.setFloatable(true);
		main.setLayout(new BorderLayout());

		main.add(scroll, BorderLayout.CENTER);
		panel = new JPanel(new BorderLayout());
		panel.add(main);
		panel.setBorder(BorderFactory.createEtchedBorder());
	}
	
	public void reset() {
		chrono = System.nanoTime();
		peak = 0;
	}

	public JPanel getPanel() {
		return panel;
	}

	public void show(String title) {
		JFrame frame = new JFrame(title);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void add(Message message) {
		String msg = message.getMessage();
		String iteration = "";
		if (msg.startsWith("@")) {
			String parts[] = msg.split(" ");
			if (parts.length >= 1)
				iteration = parts[0];
			msg = msg.substring(parts[0].length(), msg.length()).trim();
		}
		
		String time = NumFormat.seconds(System.nanoTime()-chrono);
		peak = Math.max(peak, SystemUsage.getHeapUsed());
		String mem = NumFormat.bytes(peak);
		
		String[] row = new String[] {time, mem, iteration, msg};
		table.append(row);
	}

	@Override
	public void clear() {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.setRowCount(0);
	}
	
	@Override
	public String getName() {
		return "running";
	}


}
