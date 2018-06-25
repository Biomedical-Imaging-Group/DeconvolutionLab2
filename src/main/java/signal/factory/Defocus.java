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

public class Defocus extends SignalFactory {

	private double fwhm = 3.0;
	private double low2fwhm = 10;
	private double up2fwhm = 10;

	public Defocus(double fwhm, double low2fwhm, double up2fwhm) {
		super(new double[] {fwhm, low2fwhm, up2fwhm});
		setParameters(new double[] { fwhm, low2fwhm, up2fwhm });
	}

	@Override
	public String getName() {
		return "Defocus";
	}
	 
	@Override
	public String[] getParametersName() {
		return new String[] {"FWHM at focus plane", "-Delta Z  (2xFWHM)", "+Delta Z (2xFWHM)"};
	}	

	@Override
	public void setParameters(double[] parameters) {
		if (parameters.length >= 1)
			this.fwhm = parameters[0];
		if (parameters.length >= 2)
			this.low2fwhm = parameters[1];
		if (parameters.length >= 3)
			this.up2fwhm = parameters[2];
	}

	@Override
	public double[] getParameters() {
		return new double[] {fwhm, low2fwhm, up2fwhm};
	}
	
	@Override
	public void fill(RealSignal signal) {
		double sigma = fwhm;
		double zup = zc - low2fwhm;
		double zlw = zc + up2fwhm;
		double Q = 2.0*Math.PI;
		
		double A0 = (sigma * sigma * Q);
		double aup = 2.0 / ((zup-zc)*(zup-zc));
		double alw = 2.0 / ((zlw-zc)*(zlw-zc));
		for(int z=0; z<nz; z++) {
			double sigmaZ = sigma;
			if (z > zc)
				sigmaZ += sigma * aup * (z-zc)*(z-zc);
			else
				sigmaZ += sigma * alw * (z-zc)*(z-zc);
				
			double K = 1.0 / (2.0*sigmaZ*sigmaZ);
			for(int x=0; x<nx; x++)
			for(int y=0; y<ny; y++) {
				double r2 = (x-xc)*(x-xc) + (y-yc)*(y-yc);
				signal.data[z][x+nx*y] = (float)(amplitude * Math.exp(-K*r2));
			}
		}
	}
}
