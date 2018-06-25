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

package deconvolutionlab.system;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import bilib.table.CustomizedTable;

public abstract class AbstractMeter extends JButton {

	protected Color colorBackground = new Color(10, 10, 10, 30);
	protected Color colorText = new Color(10, 10, 10);
	protected Color colorHot = new Color(10, 10, 160, 30);

	protected CustomizedTable	    table;
	protected HashMap<String, Integer> features = new HashMap<String, Integer>();
	protected boolean initialized = false;
	protected String prefix = "\u25BA ";
	protected boolean collapse = true;
	
	public AbstractMeter(int width) {
		super("");;
		int h = 28;
		setBorder(BorderFactory.createEtchedBorder());
		setPreferredSize(new Dimension(width, h));
		setMinimumSize(new Dimension(width, h));
		table = new CustomizedTable(new String[] {"Tool", "Feature", "Value"}, false);
	}

	public boolean isExpanded() {
		return !collapse;
	}
	
	public void collapse() {
		collapse = true;
		prefix = " \u25BA ";
	}
	
	public void expand() {
		collapse = false;
		prefix = " \u25BC ";
	}
	
	public JPanel getPanel(int width, int height) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(table.getPane(width, height), BorderLayout.CENTER);
		setDetail();		
		initialized = true;
		return panel;
	}

	public abstract String getMeterName();
	public abstract void setDetail();
	
	public void update() {
		if (table == null)
			return;		
		setDetail();
	}
	
	protected void add(int i, String row[]) {	
		if (initialized) {
			int r = features.get(row[0]);
			if (i>=0 && i<table.getRowCount())
				table.setCell(r, 1, row[1]);	
		}
		else {
			table.append(row);
			features.put(row[0], i);
		}
	}

	protected String split(String name) {
		String func = name.substring(3);
		return func.replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2");
	}
}
