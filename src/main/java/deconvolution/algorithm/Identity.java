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

package deconvolution.algorithm;

import java.util.concurrent.Callable;

import signal.RealSignal;

public class Identity extends Algorithm implements Callable<RealSignal> {

	public Identity() {
		super();
	}

	@Override
	public RealSignal call() {
		return y.duplicate();
	}

	@Override
	public String getName() {
		return "Identity (copy)";
	}
	
	@Override
	public String[] getShortnames() {
		return new String[] {"I"};
	}
	
	@Override
	public double getMemoryFootprintRatio() {
		return 5.0;
	}
	
	@Override
	public int getComplexityNumberofFFT() {
		return 1;
	}
	
	@Override
	public boolean isRegularized() {
		return false;
	}

	@Override
	public boolean isStepControllable() {
		return false;
	}

	@Override
	public boolean isIterative() {
		return false;
	}

	@Override
	public boolean isWaveletsBased() {
		return false;
	}

	@Override
	public Algorithm setParameters(double... params) {
		return this;
	}
	
	@Override
	public double[] getParameters() {
		return new double[] {};
	}
	
	@Override
	public double[] getDefaultParameters() {
		return new double[] {};
	}
	
	
	@Override
	public double getRegularizationFactor() {
		return 0.0;
	}
	
	@Override
	public double getStepFactor() {
		return 0;
	}

}
