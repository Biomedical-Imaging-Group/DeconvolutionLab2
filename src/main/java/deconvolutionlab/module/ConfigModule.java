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
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import bilib.table.CustomizedColumn;
import bilib.table.CustomizedTable;
import deconvolutionlab.Config;
import deconvolutionlab.Constants;
public class ConfigModule extends AbstractModule implements ActionListener {

	private JButton	 bnLoad;
	private JButton	 bnSave;
	private CustomizedTable table;
	private JLabel	 lblDefault;

	public ConfigModule() {
		create("Config");
	}

	@Override
	public String getCommand() {
		setSynopsis(new File(Config.getFilename()).getName());
		return "";
	}

	@Override
	public JPanel buildExpandedPanel() {
		
		ArrayList<CustomizedColumn> columns = new ArrayList<CustomizedColumn>();
		columns.add(new CustomizedColumn("Key", String.class, 180, false));
		columns.add(new CustomizedColumn("Value", String.class, Constants.widthGUI - 80, true));
		table = new CustomizedTable(columns, true);
	
		read();
		lblDefault = new JLabel(Config.getFilename());
		bnLoad = new JButton("Load");
		bnSave = new JButton("Save");
		lblDefault.setBorder(BorderFactory.createEtchedBorder());

		JPanel button = new JPanel();
		button.setLayout(new FlowLayout());
		button.add(bnLoad);
		button.add(bnSave);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(lblDefault, BorderLayout.NORTH);
		panel.add(table.getMinimumPane(100, 100), BorderLayout.CENTER);

		bnLoad.addActionListener(this);
		bnSave.addActionListener(this);
		setSynopsis(new File(Config.getFilename()).getName());
		return panel;
	}

	private void read() {
		String filename = Config.getFilename();
		File file = new File(filename);
		if (file.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null) {
					String entry = line.trim();
					if (!entry.startsWith("#")) {
						String[] parts = entry.split("=");
						if (parts.length == 2)
							table.append(parts);
					}
				}
				br.close();
			}
			catch (IOException e) {
				//e.printStackTrace();
			}
		}
	}

	@Override
	public void close() {
		bnLoad.removeActionListener(this);
		bnSave.removeActionListener(this);
	}
}
