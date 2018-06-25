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
import signal.factory.complex.ComplexSignalFactory;

public class TikhonovMiller extends Algorithm implements Callable<RealSignal> {
	
	private double gamma = 1.0;
	private double lambda = 1.0;
	
	public TikhonovMiller(int iterMax, double gamma, double lambda) {
		super();
		this.iterMax = iterMax;
		this.gamma = gamma;
		this.lambda = lambda;
	}

	@Override
	public RealSignal call() throws Exception {
		ComplexSignal Y = fft.transform(y);
		ComplexSignal H = fft.transform(h);
		ComplexSignal A = Operations.delta(gamma, H);
		ComplexSignal L = ComplexSignalFactory.laplacian(Y.nx, Y.ny, Y.nz);
		ComplexSignal L2 = Operations.multiplyConjugate(lambda*gamma, L, L);
		SignalCollector.free(L);
		A.minus(L2);
		SignalCollector.free(L2);
		ComplexSignal G = Operations.multiplyConjugate(gamma, H, Y);
		SignalCollector.free(H);
		SignalCollector.free(Y);
		ComplexSignal X = G.duplicate();
		while(!controller.ends(X)) {
			X.times(A);
			X.plus(G);
		}
		SignalCollector.free(A);
		SignalCollector.free(G);
		RealSignal x = fft.inverse(X);
		SignalCollector.free(X);

		return x;
	}

	@Override
	public int getComplexityNumberofFFT() {
		return 3 + (controller.needSpatialComputation() ? 2 * iterMax : 0);
	}

	@Override
	public String getName() {
		return "Tikhonov-Miller";
	}

	@Override
	public String[] getShortnames() {
		return new String[] {"TM"};
	}

	@Override
	public double getMemoryFootprintRatio() {
		return 10.0;
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
		return false;
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
