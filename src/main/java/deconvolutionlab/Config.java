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

package deconvolutionlab;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import bilib.component.SpinnerRangeDouble;
import bilib.component.SpinnerRangeInteger;
import bilib.table.CustomizedTable;
import bilib.tools.NumFormat;
import deconvolutionlab.monitor.Monitors;

public class Config {

	private static String							project		= Constants.name;
	private static String							filename;
	private static HashMap<String, Object>			inits;
	private static HashMap<String, JComponent>		components	= new HashMap<String, JComponent>();
	private static HashMap<String, CustomizedTable>	tables		= new HashMap<String, CustomizedTable>();;
	private static HashMap<String, JDialog>			dialogs		= new HashMap<String, JDialog>();
	private static Monitors							monitors	= Monitors.createDefaultMonitor();

	public static Config init(String filename) {
		Config.filename = filename;
		Config.inits = new HashMap<String, Object>();
		Config.components = new HashMap<String, JComponent>();
		Config.tables = new HashMap<String, CustomizedTable>();
		Config.dialogs = new HashMap<String, JDialog>();
		return new Config();
	}

	public static void setFilename(String filename) {
		Config.filename = filename;
	}

	public static String getFilename() {
		return filename;
	}

	public static boolean check() {
		File file = new File(Config.filename);
		if (!file.exists())
			return false;
		if (file.isDirectory())
			return false;
		return true;
	}

	public static void register(String module, String key, JComponent component, Object init) {
		if (component == null)
			return;
		components.put(module + "." + key, component);
		inits.put(module + "." + key, init);
		setValue(key, init);
	}

	public static void registerTable(String module, String key, CustomizedTable table) {
		if (table == null)
			return;
		tables.put(module + "." + key, table);
	}

	public static void registerFrame(String module, String key, JDialog dialog) {
		if (dialog == null)
			return;
		dialogs.put(module + "." + key, dialog);
	}

	public static String getAsString(String key) {
		Object object = getValue(key);
		if (object instanceof String)
			return ((String) object);
		else if (object instanceof Double)
			return ((Double) object).toString();
		else if (object instanceof Integer)
			return ((Integer) object).toString();
		else if (object instanceof Boolean)
			return ((Boolean) object).toString();
		return null;
	}

	public static double getAsDouble(String key) {
		Object object = getValue(key);
		if (object instanceof Double)
			return ((Double) object).doubleValue();
		else if (object instanceof Integer)
			return ((Integer) object).intValue();
		return 0;
	}

	private static void setValue(String key, Object value) {
		JComponent component = components.get(key);
		if (component == null)
			return;

		if (value == null)
			value = inits.get(key);

		if (component instanceof JTextField) {
			JTextField txt = (JTextField) component;
			txt.setText(value.toString());
		}
		else if (component instanceof JComboBox) {
			JComboBox<String> cmb = (JComboBox<String>) component;
			cmb.setSelectedItem(value.toString());
		}
		else if (component instanceof JLabel) {
			JLabel lbl = (JLabel) component;
			lbl.setText(value.toString());
		}
		else if (component instanceof JCheckBox) {
			JCheckBox chk = (JCheckBox) component;
			chk.setSelected(value.toString().equals("true"));
		}
		else if (component instanceof SpinnerRangeDouble) {
			SpinnerRangeDouble spn = (SpinnerRangeDouble) component;
			if (value instanceof Number) {
				Number number = (Number) value;
				spn.set(number.doubleValue());
			}
			else if (value instanceof String) {
				try {
					spn.set(Double.parseDouble((String) value));
				}
				catch (NumberFormatException ex) {
				}
			}
		}
		else if (component instanceof SpinnerRangeInteger) {
			SpinnerRangeInteger spn = (SpinnerRangeInteger) component;
			if (value instanceof Number) {
				Number number = (Number) value;
				spn.set(number.intValue());
			}
			else if (value instanceof String) {
				try {
					spn.set((int) Double.parseDouble((String) value));
				}
				catch (NumberFormatException ex) {
				}
			}
		}
		else if (component instanceof JTabbedPane) {
			JTabbedPane tab = (JTabbedPane) component;
			String source = value.toString();
			for (int i = 0; i < tab.getTabCount(); i++)
				if (source.equals(tab.getTitleAt(i)))
					tab.setSelectedIndex(i);
		}
	}

	private static Object getValue(String key) {
		JComponent component = components.get(key);
		if (component == null)
			return inits.get(key);

		if (component instanceof JTextField)
			return ((JTextField) component).getText();
		else if (component instanceof JComboBox)
			return (String) ((JComboBox) component).getSelectedItem();
		else if (component instanceof JCheckBox)
			return ((JCheckBox) component).isSelected() ? "true" : "false";
		else if (component instanceof JLabel)
			return ((JLabel) component).getText();
		else if (component instanceof SpinnerRangeDouble)
			return new Double(((SpinnerRangeDouble) component).get());
		else if (component instanceof SpinnerRangeInteger)
			return new Integer(((SpinnerRangeInteger) component).get());
		else if (component instanceof JTabbedPane) {
			JTabbedPane tab = (JTabbedPane) component;
			return tab.getTitleAt(tab.getSelectedIndex());
		}
		return inits.get(key);
	}

	public static void load() {
		Properties props = new Properties();
		try {
			FileInputStream in = new FileInputStream(filename);
			props.load(in);
		}
		catch (Exception e) {
			props = new Properties();
		}
		for (String key : components.keySet()) {
			Object init = inits.get(key);
			String value = props.getProperty(key, init.toString());
			setValue(key, value);
		}

		for (String key : tables.keySet()) {
			CustomizedTable table = tables.get(key);
			for (int i = 0; i < 100; i++) {
				String keyrow = key + ".row" + i;
				String value = props.getProperty(keyrow, "???");
				if (!value.equals("???"))
					table.append(value.split(";"));
			}
			String selected[] = props.getProperty(key + ".selected", "???").split(";");
			int ncol = Math.min(table.getColumnCount(), selected.length) - 1;
			for (int i = 0; i < table.getRowCount(); i++) {
				int n = 0;
				for (int j = 0; j < ncol; j++) {
					n += table.getCell(i, j).trim().equals(selected[j].trim()) ? 1 : 0;
				}
				if (n == ncol)
					table.setRowSelectionInterval(i, i);
			}
		}

		for (String key : dialogs.keySet()) {
			int x = (int) NumFormat.parseNumber(props.getProperty(key + ".location.x", "" + 0), 0);
			int y = (int) NumFormat.parseNumber(props.getProperty(key + ".location.y", "" + 0), 0);
			int w = (int) NumFormat.parseNumber(props.getProperty(key + ".location.w", "" + 400), 400);
			int h = (int) NumFormat.parseNumber(props.getProperty(key + ".location.h", "" + 400), 400);
			dialogs.get(key).setSize(w, h);
			dialogs.get(key).setLocation(x, y);
		}

		monitors.log("Load Config from " + filename + " (" + components.size() + " items)");
	}

	public static boolean store() {
		Properties props = new Properties();
		for (String key : components.keySet()) {
			String s = getAsString(key);
			if (s != null)
				props.setProperty(key, s);
		}

		for (String key : tables.keySet()) {
			CustomizedTable table = tables.get(key);
			int nrows = table.getRowCount();
			for (int row = 0; row < nrows; row++)
				props.setProperty(key + ".row" + row, table.getRowCSV(row, ";"));
			int row = table.getSelectedRow();
			if (row >= 0)
				props.setProperty(key + ".selected", table.getRowCSV(row, ";"));
		}

		for (String key : dialogs.keySet()) {
			JDialog dialog = dialogs.get(key);
			props.setProperty(key + ".location.x", "" + dialog.getLocation().x);
			props.setProperty(key + ".location.y", "" + dialog.getLocation().y);
			props.setProperty(key + ".location.w", "" + dialog.getSize().width);
			props.setProperty(key + ".location.h", "" + dialog.getSize().height);
		}

		try {
			FileOutputStream out = new FileOutputStream(filename);
			props.store(out, project);
		}
		catch (Exception e) {
			monitors.error("Store Config to " + filename + " (" + components.size() + " items)");
			return false;
		}
		monitors.log("Store Config to " + filename + " (" + components.size() + " items)");

		File file = new File(filename);
		if (file.exists()) {
			String line = "";
			try {
				BufferedReader br = new BufferedReader(new FileReader(filename));
				ArrayList<String> keys = new ArrayList<String>();
				ArrayList<String> headers = new ArrayList<String>();
				line = br.readLine();
				if (line != null)
					if (!line.startsWith("#"))
						keys.add(line);
					else
						headers.add(line);
				while (line != null) {
					if (!line.startsWith("#"))
						keys.add(line);
					else
						headers.add(line);
					line = br.readLine();
				}
				br.close();
				Collections.sort(keys);
				BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
				for (String ln : headers)
					bw.write(ln + "\n");
				for (String ln : keys)
					bw.write(ln + "\n");
				bw.close();
			}
			catch (Exception ex) {
				monitors.error("ERROR " + filename + " line: " + line);
				return false;
			}
		}
		return true;
	}

	public static ArrayList<String> list() {
		ArrayList<String> list = new ArrayList<String>();
		for (String key : components.keySet()) {
			list.add(key + " = " + getValue(key));
		}
		return list;
	}

	public static void print() {
		ArrayList<String> list = list();
		System.out.println("--");
		for (String line : list)
			System.out.println(line);
		System.out.println("--");
	}

	public static void printInit() {
		for (String key : inits.keySet()) {
			Object object = inits.get(key);
			if (object != null)
				System.out.println("Default " + key + " = " + (object.toString()));
		}
	}
	
	public static Rectangle getDialog(String key) {
		Properties props = new Properties();
		try {
			FileInputStream in = new FileInputStream(filename);
			props.load(in);
		}
		catch (Exception e) {
			props = new Properties();
		}

		int x = (int) NumFormat.parseNumber(props.getProperty(key + ".location.x", "-1"), -1);
		int y = (int) NumFormat.parseNumber(props.getProperty(key + ".location.y", "-1"), -1);
		int w = (int) NumFormat.parseNumber(props.getProperty(key + ".location.w", "-1"), -1);
		int h = (int) NumFormat.parseNumber(props.getProperty(key + ".location.h", "-1"), -1);
		return new Rectangle(x, y, w, h);
	}

}
