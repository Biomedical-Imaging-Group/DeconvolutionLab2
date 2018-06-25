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

import signal.ComplexSignal;
import signal.Operations;
import signal.RealSignal;
import signal.SignalCollector;
import wavelets.AbstractWavelets;
import wavelets.Wavelets;

public class ISTA extends Algorithm implements Callable<RealSignal> {
	
	private double gamma = 1.0;
	private double lambda = 1.0;
	private String waveletsName = "Haar";
	private int scale = 3;
	
	public ISTA(int iterMax, double gamma, double lambda, String waveletsName, int scale) {
		super();
		this.iterMax = iterMax;
		this.gamma = gamma;
		this.lambda = lambda;
		this.waveletsName = waveletsName;
		this.scale = scale;
	}

	@Override
	public RealSignal call() throws Exception {
		AbstractWavelets wavelets = Wavelets.getWaveletsByName(waveletsName);
		wavelets.setScale(scale);
		
		ComplexSignal Y = fft.transform(y);
		ComplexSignal H = fft.transform(h);
		ComplexSignal A = Operations.delta(gamma, H);
		ComplexSignal G = Operations.multiplyConjugate(gamma, H, Y);
		SignalCollector.free(Y);
		SignalCollector.free(H);
	
		ComplexSignal Z = G.duplicate();
		RealSignal x  = fft.inverse(G);
		RealSignal z = x.duplicate();
		float threshold = (float)(lambda*gamma*0.5);
		RealSignal buffer = y.duplicate();
		while(!controller.ends(x)) {
			fft.transform(x, Z);
			Z.times(A);
			Z.plus(G);
			fft.inverse(Z, z);
			wavelets.shrinkage(threshold, z, x, buffer);
		}
		SignalCollector.free(A);
		SignalCollector.free(Z);
		SignalCollector.free(G);
		SignalCollector.free(z);
		SignalCollector.free(buffer);
		return x;
	}
	
	public void update(RealSignal xprev, RealSignal x, double w, RealSignal s) {
		int nxy = x.nx * x.ny;
		for(int k=0; k<x.nz; k++)
		for(int i=0; i< nxy; i++) {
			float vx = x.data[k][i];
			s.data[k][i] = (float)(vx + w*(vx - xprev.data[k][i]));
			xprev.data[k][i] = vx;
		}
	}
	
	@Override
	public String getName() {
		return "Iterative Shrinkage-Thresholding";
	}
	
	@Override
	public String[] getShortnames() {
		return new String[] {"ISTA"};
	}


	@Override
	public int getComplexityNumberofFFT() {
		return 3 + 4 * iterMax;
	}
	
	@Override
	public double getMemoryFootprintRatio() {
		return 13.0;
	}

	@Override
	public boolean isRegularized() {
		return true;
	}
	
	@Override
	public boolean isStepControllable() {
		return true;
	}
	
	@Override
	public boolean isIterative() {
		return true;
	}
	
	@Override
	public boolean isWaveletsBased() {
		return true;
	}

	@Override
	public void setWavelets(String waveletsName) {
		this.waveletsName = waveletsName;
	}

	@Override
	public Algorithm setParameters(double... params) {
		if (params == null)
			return this;
		if (params.length > 0)
			iterMax = (int) Math.round(params[0]);
		if (params.length > 1)
			gamma = (float)params[1];
		if (params.length > 2)
			lambda = (float)params[2];
		if (params.length > 3)
			scale = (int)params[3];
		return this;
	}
	
	@Override
	public double[] getDefaultParameters() {
		return new double[] {10, 1, 0.1};
	}
	
	@Override
	public double[] getParameters() {
		return new double[] {iterMax, gamma, lambda};
	}
	
	@Override
	public double getRegularizationFactor() {
		return lambda;
	}
	
	@Override
	public double getStepFactor() {
		return gamma;
	}
}
