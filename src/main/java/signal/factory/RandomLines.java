package signal.factory;

import java.util.Random;

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

import signal.RealSignal;

public class RandomLines extends SignalFactory {

	private double number = 100.0;

	public RandomLines(double number) {
		super(new double[] {number});
		setParameters(new double[] {number});
	}

	@Override
	public String getName() {
		return "RandomLines";
	}

	@Override
	public String[] getParametersName() {
		return new String[] { "Number of lines" };
	}

	@Override
	public void setParameters(double[] parameters) {
		if (parameters.length >= 1) this.number = parameters[0];
	}

	@Override
	public double[] getParameters() {
		return new double[] { number };
	}

	@Override
	public void fill(RealSignal signal) {
		Random rand = new Random(12345);
		double Q = Math.sqrt(3)*1.5;
		for (int index = 0; index < number; index++) {
			double x1 = -rand.nextDouble() * nx;
			double x2 = nx + rand.nextDouble() * nx;
			double y1 = (-0.1 + rand.nextDouble() * 1.2) * ny;
			double y2 = (-0.1 + rand.nextDouble() * 1.2) * ny;
			double z1 = (-0.1 + rand.nextDouble() * 1.2) * nz;
			double z2 = (-0.1 + rand.nextDouble() * 1.2) * nz;
			double d = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2);
			d = Math.sqrt(d);
			int n = (int) (d / 0.3);
			double dx = (x2 - x1) / d;
			double dy = (y2 - y1) / d;
			double dz = (z2 - z1) / d;
			for (int s = 0; s < n; s++) {
				double x = x1 + s * dx;
				int i = (int) Math.round(x);
				if (i >= 1 && i < nx-1) {
					double y = y1 + s * dy;
					int j = (int) Math.round(y);
					if (j >= 1 && j < ny-1) {
						double z = z1 + s * dz;
						int k = (int) Math.round(z);
						if (k >= 1 && k < nz-1) {
							for(int ii=i-1; ii<=i+1; ii++)
							for(int jj=j-1; jj<=j+1; jj++)
							for(int kk=k-1; kk<=k+1; kk++) {
								double p = 1.0 - Math.sqrt((x - i) * (x - i) + (y - j) * (y - j) + (z - k) * (z - k))/Q;
								signal.data[k][i + nx * j] =  Math.max(signal.data[k][i + nx * j], (float)(p*amplitude));
							}
						}
					}
				}
			}
		}
	}

}
