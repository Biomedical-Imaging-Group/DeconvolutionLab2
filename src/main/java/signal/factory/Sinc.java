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

import signal.Operations;
import signal.RealSignal;

public class Sinc extends SignalFactory {

	private double periodX = 3.0;
	private double periodY = 3.0;
	private double periodZ = 3.0;
	
	public Sinc(double periodX, double periodY, double periodZ) {
		super(new double[] {periodX, periodY, periodZ});
		setParameters(new double[] {periodX, periodY, periodZ});
	}

	@Override
	public String getName() {
		return "Sinc";
	}
	 
	@Override
	public String[] getParametersName() {
		return new String[] {"Period X", "Period Y", "Period Z"};
	}	

	@Override
	public void setParameters(double[] parameters) {
		if (parameters.length >= 1)
			this.periodX = parameters[0];
		if (parameters.length >= 2)
			this.periodY = parameters[1];
		if (parameters.length >= 3)
			this.periodZ = parameters[2];
	}

	@Override
	public double[] getParameters() {
		return new double[] {periodX, periodY, periodZ};
	}

	@Override
	public void fill(RealSignal signal) {
		double KX = 1. / (periodX*periodX);
		double KY = 1. / (periodY*periodY);
		double KZ = 1. / (periodZ*periodZ);
		for(int x=0; x<nx; x++)
		for(int y=0; y<ny; y++)
		for(int z=0; z<nz; z++) {
			
			double r = Math.sqrt(KX*(x-xc)*(x-xc) + KY*(y-yc)*(y-yc) + KZ*(z-zc)*(z-zc));
			double v = 1.0;
			if (r > Operations.epsilon)
				v = Math.sin(r) / r;
			signal.data[z][x+nx*y] = (float)(v);
		}
		signal.rescale(0, amplitude);
	}


}
