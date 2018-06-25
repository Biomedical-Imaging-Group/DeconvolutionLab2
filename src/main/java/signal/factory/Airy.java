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

import bilib.tools.Bessel;
import signal.RealSignal;

public class Airy extends SignalFactory {

	private double pupil = 5;
	private double lambda = 1;
	private double distanceAxial = 0.5;
	private double attenuationFactor = 3;

	public Airy(double pupil, double lambda, double distanceAxial, double attenuationFactor) {
		super(new double[] {pupil, lambda, distanceAxial, attenuationFactor});
		setParameters(new double[] {pupil, lambda, distanceAxial, attenuationFactor});
	}

	@Override
	public String getName() {
		return "Airy";
	}

	@Override
	public String[] getParametersName() {
		return new String[] { "Pupil (normalized unit)", "Wavelength (normalized unit)", "Axial distance (normalized unit)", "Attenuation Factor" };
	}

	@Override
	public void setParameters(double[] parameters) {
		if (parameters.length >= 1)
			this.pupil = parameters[0];
		if (parameters.length >= 2)
			this.lambda = parameters[1];
		if (parameters.length >= 3)
			this.distanceAxial = parameters[2];
		if (parameters.length >= 4)
			this.attenuationFactor = parameters[3];
		
	}

	@Override
	public double[] getParameters() {
		return new double[]  {pupil, lambda, distanceAxial, attenuationFactor};
	}

	@Override
	public void fill(RealSignal signal) {
		int xsize = nx / 2;
		int ysize = ny / 2;
		double d = Math.max(0.01, distanceAxial);
		double diag = Math.sqrt(xsize*xsize + ysize*ysize);
		double b = 2.0 * Math.PI * pupil / lambda;
		double epsilon = 0.01 / diag;
		for(int i=0; i<nx; i++)
		for(int j=0; j<ny; j++) {
			double r = Math.max(epsilon, Math.sqrt((i-xc)*(i-xc)+(j-yc)*(j-yc)) / diag);
			for(int k=0; k<nz; k++) {
				double dz = 1 - Math.abs(k-zc)/nz;
				double z1 =  d + dz;
				double jc = Bessel.J1(r * b / z1) / r;
				signal.data[k][i+j*nx] = (float)(jc*jc*Math.exp(-dz*attenuationFactor));
			}
		}
	}
}
