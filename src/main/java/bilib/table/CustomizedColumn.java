/*
 * bilib --- Java Bioimaging Library ---
 * 
 * Author: Daniel Sage, Biomedical Imaging Group, EPFL, Lausanne, Switzerland
 * 
 * Conditions of use: You are free to use this software for research or
 * educational purposes. In addition, we expect you to include adequate
 * citations and acknowledgments whenever you present or publish results that
 * are based on it.
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

package bilib.table;

/**
 * This class allows to customized the columns of the CustomizedTable tables.
 * 
 * @author Daniel Sage
 * 
 */
public class CustomizedColumn {

	private Class<?>		columnClasse; // usually it is a String
	private String	 	header;
	private boolean		editable;
	private int	     	width;
	private String[]		choices;	  // ComboBox
	private String	 	button;	     // Button
	private String	 	tooltip;

	public CustomizedColumn(String header, Class<?> columnClasse, int width, boolean editable) {
		this.columnClasse = columnClasse;
		this.header = header;
		this.width = width;
		this.editable = editable;
	}

	public CustomizedColumn(String header, Class<?> classe, int width, String[] choices, String tooltip) {
		this.columnClasse = classe;
		this.header = header;
		this.width = width;
		this.editable = true;
		this.choices = choices;
		this.tooltip = tooltip;
	}

	public CustomizedColumn(String header, Class<?> columnClasse, int width, String button, String tooltip) {
		this.columnClasse = columnClasse;
		this.header = header;
		this.width = width;
		this.editable = false;
		this.button = button;
		this.tooltip = tooltip;
	}

	public Class<?> getColumnClass() {
		return columnClasse;
	}
	
	public String getHeader() {
		return header;
	}
	
	public boolean isEditable() {
		return editable;
	}
	
	public int	getWidth()  {
		return width;
	}
	
	public String[] getChoices()  {
		return choices;
	}
	
	public String getButton()  {
		return button;
	} 
	
	public String getTooltip()  {
		return tooltip;
	} 
}
