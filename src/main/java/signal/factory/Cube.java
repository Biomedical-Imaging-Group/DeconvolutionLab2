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

public class Cube extends SignalFactory {

	private double side = 10;
	private double slope = 1;

	public Cube(double side, double slope) {
		super(new double[] {side, slope});
		setParameters(new double[] {side, slope});
	}
	
	@Override
	public String getName() {
		return "Cube";
	}

	@Override
	public String[] getParametersName() {
		return new String[] {"Side", "Sigmoid Curve Slope"};
	}	

	@Override
	public void setParameters(double[] parameters) {
		if (parameters.length >= 1)
			this.side = parameters[0];
		if (parameters.length >= 2)
			this.slope = parameters[1];
	}

	@Override
	public double[] getParameters() {
		return new double[] {side, slope};
	}

	@Override
	public void fill(RealSignal signal) {
		for(int x=0; x<nx; x++)
		for(int y=0; y<ny; y++)
		for(int z=0; z<nz; z++) {
			double dx = Math.sqrt((x-xc)*(x-xc)) - side;
			double dy = Math.sqrt((y-yc)*(y-yc)) - side;
			double dz = Math.sqrt((z-zc)*(z-zc)) - side;
			double rx = 1.0- 1.0 / (1.0 + Math.exp(-dx/slope));
			double ry = 1.0- 1.0 / (1.0 + Math.exp(-dy/slope));
			double rz = 1.0- 1.0 / (1.0 + Math.exp(-dz/slope));
			signal.data[z][x+nx*y] = (float)(amplitude * (rx *ry * rz));
		}

	}
}
