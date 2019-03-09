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

import bilib.tools.PsRandom;
import signal.ComplexSignal;
import signal.Operations;
import signal.RealSignal;
import signal.SignalCollector;

public class Simulation extends Algorithm implements Callable<RealSignal> {

	private static PsRandom rand = new PsRandom(1234);

	private double mean = 0.0;
	private double stdev = 10.0;
	private double poisson = 0.0;
		
	public Simulation(double mean, double stdev, double poisson) {
		super();
		this.mean = mean;
		this.stdev = stdev;
		this.poisson = poisson;
	}
	
	@Override
	public RealSignal call() {
		ComplexSignal Y = fft.transform(y);
		ComplexSignal H = fft.transform(h);
		ComplexSignal X = Operations.multiply(H, Y);
		SignalCollector.free(Y);
		SignalCollector.free(H);
		RealSignal x = fft.inverse(X);
		SignalCollector.free(X);
		gaussian(x, mean, stdev);
		poisson(x, poisson);
		return x;
	}

	public void gaussian(RealSignal x, double mean, double sd) {

		for (int k = 0; k < x.nz; k++) {
			float[] slice = x.getXY(k);
			for (int j = 0; j < x.ny * x.nx; j++) {
				slice[j] += (float) rand.nextGaussian(mean, sd);
			}
		}
	}

	public void poisson(RealSignal x, double factor) {
		if (factor < Operations.epsilon) 
			return;
		double f = 1.0/(factor);
		for (int k = 0; k < x.nz; k++) {
			float[] slice = x.getXY(k);
			for (int j = 0; j < x.ny * x.nx; j++)
				if (slice[j] > Operations.epsilon) {
					slice[j] = (float)(rand.nextPoissonian(f*(slice[j])) * factor);
				}
		}	
	}

	@Override
	public String getName() {
		return "Simulation with noise";
	}
	
	@Override
	public String[] getShortnames() {
		return new String[] {"SIM", "SIMU"};
	}


	@Override
	public int getComplexityNumberofFFT() {
		return 3;
	}

	@Override
	public double getMemoryFootprintRatio() {
		return 8.0;
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
		if (params == null)
			return this;
		if (params.length > 0)
			mean = params[0];
		if (params.length > 1)
			stdev = params[1];
		if (params.length > 2)
			poisson = params[2];
		return this;
	}
	
	@Override
	public double[] getDefaultParameters() {
		return new double[] {0, 1, 0};
	}
	
	@Override
	public double[] getParameters() {
		return new double[] {mean, stdev, poisson};
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
