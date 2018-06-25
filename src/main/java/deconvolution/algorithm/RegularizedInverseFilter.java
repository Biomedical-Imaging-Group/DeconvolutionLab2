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

public class RegularizedInverseFilter extends Algorithm implements Callable<RealSignal> {

	private double lambda = 0.001;

	public RegularizedInverseFilter(double lambda) {
		super();
		this.lambda = lambda;
	}

	@Override
	public RealSignal call() {
		if (optimizedMemoryFootprint)
			return runOptimizedMemoryFootprint();
		else
			return runTextBook();
	}

	public RealSignal runTextBook() {
		ComplexSignal Y = fft.transform(y);
		ComplexSignal H = fft.transform(h);
		ComplexSignal H2 = Operations.multiply(H, H);
		ComplexSignal L = ComplexSignalFactory.laplacian(Y.nx, Y.ny, Y.nz);
		ComplexSignal L2 = Operations.multiply(lambda, L, L);
		ComplexSignal FA = Operations.add(H2, L2);
		ComplexSignal FT = Operations.divideStabilized(H, FA);
		ComplexSignal X = Operations.multiply(Y, FT);
		RealSignal x = fft.inverse(X);
		SignalCollector.free(FT);
		SignalCollector.free(Y);
		SignalCollector.free(H);
		SignalCollector.free(FA);
		SignalCollector.free(L);
		SignalCollector.free(H2);
		SignalCollector.free(L2);
		SignalCollector.free(X);
		return x;
	}

	public RealSignal runOptimizedMemoryFootprint() {
		ComplexSignal Y = fft.transform(y);
		ComplexSignal H = fft.transform(h);
		ComplexSignal X = filter(Y, H);
		SignalCollector.free(Y);
		SignalCollector.free(H);
		RealSignal x = fft.inverse(X);
		SignalCollector.free(X);
		return x;
	}

	public ComplexSignal filter(ComplexSignal Y, ComplexSignal H) {
		ComplexSignal L = ComplexSignalFactory.laplacian(Y.nx, Y.ny, Y.nz);
		L.setName("Laplacian");
		float la, lb, ha, hb, fa, fb, ta, tb, ya, yb;
		int nxy = Y.nx * Y.ny * 2;
		float w = (float) lambda;
		float epsilon = (float) Operations.epsilon;
		for (int k = 0; k < Y.nz; k++)
			for (int i = 0; i < nxy; i += 2) {
				la = L.data[k][i];
				lb = L.data[k][i + 1];
				ha = H.data[k][i];
				hb = H.data[k][i + 1];
				fa = w * (la * la - lb * lb) + (ha * ha - hb * hb);
				fb = w * (2f * la * lb) + (2f * ha * hb);
				float mag = Math.max(epsilon, fa * fa + fb * fb);
				ta = (ha * fa + hb * fb) / mag;
				tb = (hb * fa - ha * fb) / mag;
				ya = Y.data[k][i];
				yb = Y.data[k][i + 1];
				L.data[k][i] = ya * ta - yb * tb;
				L.data[k][i + 1] = ya * tb + ta * yb;
			}
		return L;
	}

	@Override
	public String getName() {
		return "Regularized Inverse Filter";
	}

	@Override
	public String[] getShortnames() {
		return new String[] {"RIF", "LRIF"};
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
		return true;
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
			lambda = (float) params[0];
		return this;
	}

	@Override
	public double[] getDefaultParameters() {
		return new double[] { 0.1 };
	}

	@Override
	public double[] getParameters() {
		return new double[] { lambda };
	}

	@Override
	public double getRegularizationFactor() {
		return lambda;
	}

	@Override
	public double getStepFactor() {
		return 0;
	}

}
