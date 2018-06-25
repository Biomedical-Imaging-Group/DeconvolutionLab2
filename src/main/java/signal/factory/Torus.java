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

package signal.factory;

import signal.RealSignal;

public class Torus extends SignalFactory {

	private double radius = 10.0;
	
	public Torus(double radius) {
		super(new double[] {radius});
		setParameters(new double[] {radius});
	}

	@Override
	public String getName() {
		return "Torus";
	}
	 
	@Override
	public String[] getParametersName() {
		return new String[] {"Radius"};
	}	

	@Override
	public void setParameters(double[] parameters) {
		if (parameters.length >= 1)
			this.radius = parameters[0];
	}

	@Override
	public double[] getParameters() {
		return new double[] {radius};
	}

	@Override
	public void fill(RealSignal signal) {
		double thick = radius * 0.2;
		double thick2 = thick * thick;
			for(int i=0; i<nx; i++)
		for(int j=0; j<ny; j++) {
			double dxy = Math.abs(radius - dist(i, j, xc, yc));
			if (dxy < thick) {
				for(int k=0; k<nz; k++) {
					double dz = k - zc;
					if (dxy*dxy + dz*dz < thick2)
						signal.data[k][i+j*nx] = (float)(amplitude * (1.0-(dxy*dxy + dz*dz)/thick2));
				}
			}
		}
	}
	
	private static double dist(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}
}
