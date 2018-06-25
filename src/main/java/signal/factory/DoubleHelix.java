package signal.factory;

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

public class DoubleHelix extends SignalFactory {

	private double fwhm = 3.0;
	private double dist = 30;
	private double delta = 10;

	public DoubleHelix(double fwhm, double dist, double delta) {
		super(new double[] {fwhm, dist, delta});
		setParameters(new double[] {fwhm, dist, delta});
	}

	@Override
	public String getName() {
		return "Double-Helix";
	}
	 
	@Override
	public String[] getParametersName() {
		return new String[] {"FWHM at focus plane", "Distance", "Delta Z (rotation PI)"};
	}	

	@Override
	public void setParameters(double[] parameters) {
		if (parameters.length >= 1)
			this.fwhm = parameters[0];
		if (parameters.length >= 2)
			this.dist = parameters[1];
		if (parameters.length >= 3)
			this.delta = parameters[2];
	}

	@Override
	public double[] getParameters() {
		return new double[] {fwhm, dist, delta};
	}
	
	@Override
	public void fill(RealSignal signal) {
		double K = 0.5 / (fwhm*fwhm);
		double T = Math.PI/(delta*2.0);
		for(int z=0; z<nz; z++) {
			double cosa = Math.cos((z-zc)*T);
			double sina = Math.sin((z-zc)*T);
			for(int x=0; x<nx; x++)
			for(int y=0; y<ny; y++) {
				double u = (x-xc) * cosa + (y-yc) * sina;
				double v = -(x-xc) * sina + (y-yc) * cosa;
				double u1 = (u - dist * 0.5);
				double u2 = (u + dist * 0.5);
				double p = Math.exp(-((u1 * u1 + v * v) * K)) + Math.exp(-((u2 * u2 + v * v) * K));
				signal.data[z][x+nx*y] = (float)(amplitude * p);
			}
		}
	}


}
