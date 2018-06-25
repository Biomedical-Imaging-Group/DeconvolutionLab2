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

package bilib.component;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * This class extends the Java Swing JSpinner to make a easy to use spinner for
 * integer. It handles int type. The size can be control by the number of
 * visible chars or by a specific format (NumberEditor).
 * 
 * @author Daniel Sage, Biomedical Imaging Group, EPFL, Lausanne, Switzerland.
 * 
 */
public class SpinnerRangeInteger extends JSpinner {

	private SpinnerNumberModel	model;

	private int					defValue;
	private int					minValue;
	private int					maxValue;
	private int					incValue;

	/**
	 * Constructor.
	 */
	public SpinnerRangeInteger(int defValue, int minValue, int maxValue, int incValue) {
		super();
		this.defValue = defValue;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.incValue = incValue;

		Integer def = new Integer(defValue);
		Integer min = new Integer(minValue);
		Integer max = new Integer(maxValue);
		Integer inc = new Integer(incValue);
		model = new SpinnerNumberModel(def, min, max, inc);
		setModel(model);
		JFormattedTextField tf = ((JSpinner.DefaultEditor) getEditor()).getTextField();
		tf.setColumns(7);
	}

	/**
	 * Constructor.
	 */
	public SpinnerRangeInteger(int defValue, int minValue, int maxValue, int incValue, String format) {
		super();
		this.defValue = defValue;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.incValue = incValue;

		Integer def = new Integer(defValue);
		Integer min = new Integer(minValue);
		Integer max = new Integer(maxValue);
		Integer inc = new Integer(incValue);
		model = new SpinnerNumberModel(def, min, max, inc);
		setModel(model);
		setEditor(new JSpinner.NumberEditor(this, format));
		JFormattedTextField tf = ((JSpinner.DefaultEditor) getEditor()).getTextField();
		tf.setColumns(format.length());
	}

	/**
	 * Constructor.
	 */
	public SpinnerRangeInteger(int defValue, int minValue, int maxValue, int incValue, int visibleChars) {
		super();
		this.defValue = defValue;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.incValue = incValue;

		Integer def = new Integer(defValue);
		Integer min = new Integer(minValue);
		Integer max = new Integer(maxValue);
		Integer inc = new Integer(incValue);
		model = new SpinnerNumberModel(def, min, max, inc);
		setModel(model);
		JFormattedTextField tf = ((JSpinner.DefaultEditor) getEditor()).getTextField();
		tf.setColumns(visibleChars);
	}

	/**
	 * Set the format of the numerical value.
	 */
	public void setFormat(String format) {
		setEditor(new JSpinner.NumberEditor(this, format));
	}

	/**
	 * Set the minimal and the maximal limit.
	 */
	public void setLimit(int minValue, int maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		int value = get();
		Integer min = new Integer(minValue);
		Integer max = new Integer(maxValue);
		Integer inc = new Integer(incValue);
		defValue = (value > maxValue ? maxValue : (value < minValue ? minValue : value));
		Integer def = new Integer(defValue);
		model = new SpinnerNumberModel(def, min, max, inc);
		setModel(model);
	}

	/**
	 * Set the incremental step.
	 */
	public void setIncrement(int incValue) {
		this.incValue = incValue;
		Integer def = (Integer) getModel().getValue();
		Integer min = new Integer(minValue);
		Integer max = new Integer(maxValue);
		Integer inc = new Integer(incValue);
		model = new SpinnerNumberModel(def, min, max, inc);
		setModel(model);
	}

	/**
	 * Returns the incremental step.
	 */
	public int getIncrement() {
		return incValue;
	}

	/**
	 * Set the value in the JSpinner with clipping in the range [min..max].
	 */
	public void set(int value) {
		value = (value > maxValue ? maxValue : (value < minValue ? minValue : value));
		model.setValue(new Integer(value));
	}

	/**
	 * Return the value without clipping the value in the range [min..max].
	 */
	public int get() {
		if (model.getValue() instanceof Integer) {
			Integer i = (Integer) model.getValue();
			int ii = i.intValue();
			return (ii > maxValue ? maxValue : (ii < minValue ? minValue : ii));
		}
		else if (model.getValue() instanceof Double) {
			Double i = (Double) model.getValue();
			int ii = (int) i.doubleValue();
			return (ii > maxValue ? maxValue : (ii < minValue ? minValue : ii));
		}
		else if (model.getValue() instanceof Float) {
			Float i = (Float) model.getValue();
			int ii = (int) i.floatValue();
			return (ii > maxValue ? maxValue : (ii < minValue ? minValue : ii));
		}
		return 0;
	}
	
	public int getRangeMaximum() {
		return maxValue;
	}
	
	public int getRangeMinimum() {
		return minValue;
	}
}
