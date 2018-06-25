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

public class DoG extends SignalFactory {

	private double sigma1 = 3.0;
	private double sigma2 = 4.0;
	
	public DoG(double sigma1, double sigma2) {
		super(new double[] {sigma1, sigma2});
		setParameters(new double[] {sigma1, sigma2});
	}

	@Override
	public String getName() {
		return "DoG";
	}
	 
	@Override
	public String[] getParametersName() {
		return new String[] {"Sigma 1", "Sigma 2"};
	}	

	@Override
	public void setParameters(double[] parameters) {
		if (parameters.length >= 1)
			this.sigma1 = parameters[0];
		if (parameters.length >= 2)
			this.sigma2 = parameters[1];
	}

	@Override
	public double[] getParameters() {
		return new double[] {sigma1, sigma2};
	}

	@Override
	public void fill(RealSignal signal) {
		double K1 = 0.5 / (sigma1*sigma1);
		double K2 = 0.5 / (sigma2*sigma2);
		for(int x=0; x<nx; x++)
		for(int y=0; y<ny; y++)
		for(int z=0; z<nz; z++) {
			double r2 = (x-xc)*(x-xc) + (y-yc)*(y-yc) + (z-zc)*(z-zc);
			double v = Math.exp(-K2*r2) - Math.exp(-K1*r2);
			signal.data[z][x+nx*y] = (float)(amplitude * v);
		}
	}
}
