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

public class Astigmatism extends SignalFactory {

	private double fwhm = 5.0;
	private double factor = 1;
	
	public Astigmatism(double fwhm, double factor) {
		super(new double[] {fwhm, factor});
		setParameters(new double[] {fwhm, factor});
	}

	@Override
	public String getName() {
		return "Astigmatism";
	}
	 
	@Override
	public String[] getParametersName() {
		return new String[] {"FWHM at focus plane", "Astigmatism factor"};
	}	

	@Override
	public void setParameters(double[] parameters) {
		if (parameters.length >= 1)
			this.fwhm = parameters[0];
		if (parameters.length >= 2)
			this.factor = parameters[1];
	}

	@Override
	public double[] getParameters() {
		return new double[] {fwhm, factor};
	}
	
	@Override
	public void fill(RealSignal signal) {
		double sigma = 0.8493218 * fwhm; // 1/sqrt(-2*log(0.5))
		factor = 1;
		for(int z=0; z<nz; z++) {
			double dx = 1.0;
			double dy = 1.0;
			if (z>zc) {
				dy = (1.0-factor*(z-zc)/(double)nz);
				dx = 1.0 / dy;
			}
			if (z<zc) {
				dx = (1.0-factor*(zc-z)/(double)nz);
				dy = 1.0 / dx;
			}
			double kx = 1.0 / (dx * dx * sigma * sigma * 2.0);
			double ky = 1.0 / (dy * dy * sigma * sigma * 2.0);
			for(int x=0; x<nx; x++)
			for(int y=0; y<ny; y++) {
				double r2 = kx*(x-xc)*(x-xc) + ky*(y-yc)*(y-yc);
				signal.data[z][x+nx*y] = (float)(amplitude * Math.exp(-r2));
			}
		}
	}


}
