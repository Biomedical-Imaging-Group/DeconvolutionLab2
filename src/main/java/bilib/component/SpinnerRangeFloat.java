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
 * float. It handles float type. The size can be control by the number of
 * visible chars or by a specific format (NumberEditor).
 * 
 * @author Daniel Sage, Biomedical Imaging Group, EPFL, Lausanne, Switzerland.
 * 
 */
public class SpinnerRangeFloat extends JSpinner {

	private SpinnerNumberModel	model;

	private float				defValue;
	private float				minValue;
	private float				maxValue;
	private float				incValue;

	/**
	 * Constructor.
	 */
	public SpinnerRangeFloat(float defValue, float minValue, float maxValue, float incValue) {
		super();
		this.defValue = defValue;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.incValue = incValue;

		Float def = new Float(defValue);
		Float min = new Float(minValue);
		Float max = new Float(maxValue);
		Float inc = new Float(incValue);
		model = new SpinnerNumberModel(def, min, max, inc);
		setModel(model);
		JFormattedTextField tf = ((JSpinner.DefaultEditor) getEditor()).getTextField();
		tf.setColumns(7);
	}

	/**
	 * Constructor.
	 */
	public SpinnerRangeFloat(float defValue, float minValue, float maxValue, float incValue, String format) {
		super();
		this.defValue = defValue;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.incValue = incValue;

		Double def = new Double(defValue);
		Double min = new Double(minValue);
		Double max = new Double(maxValue);
		Double inc = new Double(incValue);
		this.model = new SpinnerNumberModel(def, min, max, inc);
		setModel(model);
		setEditor(new JSpinner.NumberEditor(this, format));
		JFormattedTextField tf = ((JSpinner.DefaultEditor) getEditor()).getTextField();
		tf.setColumns(7);
	}

	/**
	 * Constructor.
	 */
	public SpinnerRangeFloat(float defValue, float minValue, float maxValue, float incValue, int visibleChars) {
		super();
		this.defValue = defValue;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.incValue = incValue;

		Float def = new Float(defValue);
		Float min = new Float(minValue);
		Float max = new Float(maxValue);
		Float inc = new Float(incValue);
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
	public void setLimit(float minValue, float maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		float value = get();
		Float min = new Float(minValue);
		Float max = new Float(maxValue);
		Float inc = new Float(incValue);
		defValue = (value > maxValue ? maxValue : (value < minValue ? minValue : value));
		Float def = new Float(defValue);
		model = new SpinnerNumberModel(def, min, max, inc);
		setModel(model);
	}

	/**
	 * Set the incremental step.
	 */
	public void setIncrement(float incValue) {
		this.incValue = incValue;
		Float def = (Float) getModel().getValue();
		Float min = new Float(minValue);
		Float max = new Float(maxValue);
		Float inc = new Float(incValue);
		model = new SpinnerNumberModel(def, min, max, inc);
		setModel(model);
	}

	/**
	 * Returns the incremental step.
	 */
	public float getIncrement() {
		return incValue;
	}

	/**
	 * Set the value in the JSpinner with clipping in the range [min..max].
	 */
	public void set(float value) {
		value = (value > maxValue ? maxValue : (value < minValue ? minValue : value));
		model.setValue(new Float(value));
	}

	/**
	 * Return the value without clipping the value in the range [min..max].
	 */
	public float get() {
		if (model.getValue() instanceof Integer) {
			Integer i = (Integer) model.getValue();
			float ii = (float) i.intValue();
			return (ii > maxValue ? maxValue : (ii < minValue ? minValue : ii));
		}
		else if (model.getValue() instanceof Double) {
			Double i = (Double) model.getValue();
			float ii = (float) i.doubleValue();
			return (ii > maxValue ? maxValue : (ii < minValue ? minValue : ii));
		}
		else if (model.getValue() instanceof Float) {
			Float i = (Float) model.getValue();
			float ii = i.floatValue();
			return (ii > maxValue ? maxValue : (ii < minValue ? minValue : ii));
		}
		return 0f;
	}
	
	public float getRangeMaximum() {
		return maxValue;
	}
	
	public float getRangeMinimum() {
		return minValue;
	}

}
