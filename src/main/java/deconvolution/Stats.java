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
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import bilib.table.CustomizedColumn;
import bilib.table.CustomizedTable;
import bilib.tools.NumFormat;
import deconvolutionlab.Constants;
import deconvolutionlab.Lab;
import deconvolutionlab.monitor.Monitors;
import deconvolutionlab.system.SystemUsage;
import signal.RealSignal;
import signal.SignalCollector;

public class Stats {

	public enum Mode {NO, SHOW, SAVE, SHOWSAVE};
	
	private CustomizedTable		table;
	private float[]				statsInput;
	private Mode mode;
	
	private boolean embedded = false;
	private boolean shown = false;
	private String name = "stats";
	
	public Stats(Mode mode, String name) {
		this.mode = mode;
		this.name = name;
		ArrayList<CustomizedColumn> columns = new ArrayList<CustomizedColumn>();
		columns.add(new CustomizedColumn("Signal", String.class, 200, false));
		columns.add(new CustomizedColumn("Mean", Double.class, 130, false));
		columns.add(new CustomizedColumn("Minimum", Double.class, 130, false));
		columns.add(new CustomizedColumn("Maximum", Double.class, 130, false));
		columns.add(new CustomizedColumn("Stdev", Double.class, 100, false));
		columns.add(new CustomizedColumn("Energy", Double.class, 100, false));
		columns.add(new CustomizedColumn("Time", Double.class, 100, false));
		columns.add(new CustomizedColumn("Memory", String.class, 100, false));
		columns.add(new CustomizedColumn("Allocated Signal", String.class, 100, false));
		columns.add(new CustomizedColumn("PSNR", Double.class, 100, false));
		columns.add(new CustomizedColumn("SNR", Double.class, 100, false));
		columns.add(new CustomizedColumn("Residu", Double.class, 100, false));
		table = new CustomizedTable(columns, true);
	}
	
	public void setEmbeddedInFrame(boolean embedded) {
		this.embedded = embedded;
	}
	
	/**
	 * Show the stats table in a frame if it is not yet embedded in another frame.
	 */
	public void show() {
		if (embedded)
			return;
		if (shown)
			return;
		if (mode == Mode.SHOW || mode == Mode.SHOWSAVE) {
			JFrame frame = new JFrame(name);
			frame.getContentPane().add(getPanel());
			frame.pack();
			Lab.setVisible(frame);
			shown = true;
		}
	}
	
	public boolean isShown() {
		return shown;
	}
	
	public void save(Monitors monitors, String path) {
		if (mode == Mode.SAVE || mode == Mode.SHOWSAVE) {
			String filename = path + File.separator + name + ".csv";
			monitors.log("Stats save " + filename);
			table.saveCSV(filename);
		}
	}
	
	public void addInput(RealSignal x) {
		statsInput = x.getStats();
		table.append(compute(x, "In: " + x.name, "", Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));	
	}

	public void add(RealSignal x, int iterations) {
		table.append(compute(x, "" + iterations, "", Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));	
	}

	public void add(RealSignal x, String iterations, String time, double psnr, double snr, double residu) {
		table.append(compute(x, "" + iterations, time, psnr, snr, residu));	
	}

	public void addOutput(RealSignal x, String algo, String time, double psnr, double snr, double residu) {
		table.append(compute(x, "Out: " + algo, time, psnr, snr, residu));	
	}
	
	public Object[] compute(RealSignal x, String iterations, String time, double psnr, double snr, double residu) {
		double params[] = null;
		Object[] row = new Object[12];
		if (x != null) 
			params = x.getStatsAsDouble();
		if (params == null)
			params = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		row[0] = iterations;
		row[1] = params[0];
		row[2] = params[1];
		row[3] = params[2];
		row[4] = params[3];
		row[5] = params[5];
		row[6] = time;
		row[7] = NumFormat.bytes(SystemUsage.getHeapUsed());
		row[8] = SignalCollector.sumarize();
		row[9] = new Double(psnr);
		row[10] = new Double(snr);
		row[11] = residu;
		return row;
	}

	public JPanel getPanel() {
		JScrollPane scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(Constants.widthGUI+200, 400));
		JPanel panel = new JPanel(new BorderLayout());
		panel = new JPanel(new BorderLayout());
		panel.add(scroll);
		panel.setBorder(BorderFactory.createEtchedBorder());
		return panel;
	}
	
	public String toStringStats() {
		if (mode == Mode.SHOW)
			return "show";
		if (mode == Mode.SAVE)
			return "save";
		if (mode == Mode.SHOWSAVE)
			return "show and save";
		return "no";
	}
	
	public Mode getMode() {
		return mode;
	}
	public float[] getStatsInput() {
		return statsInput;
	}

}
