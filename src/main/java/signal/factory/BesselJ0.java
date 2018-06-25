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

public class BesselJ0 extends SignalFactory {

	private double sizeCenter = 2;
	private double sizeTop = 5;
	private double attenuationAxial = 0.2;
	private double attenuationLateral = 0.2;

	public BesselJ0(double sizeCenter, double sizeTop, double attenuationLateral, double attenuationAxial) {
		super(new double[] {sizeCenter, sizeTop, attenuationLateral, attenuationAxial});
		setParameters(new double[] {sizeCenter, sizeTop, attenuationLateral, attenuationAxial});
	}

	@Override
	public String getName() {
		return "BesselJ0";
	}

	@Override
	public String[] getParametersName() {
		return new String[] { "Size Center", "Size Top/Bottom", "Attenuation Lateral (<1)", "Attenuation Axial (<1)" };
	}

	@Override
	public void setParameters(double[] parameters) {
		if (parameters.length >= 1)
			this.sizeCenter = parameters[0];
		if (parameters.length >= 2)
			this.sizeTop = parameters[1];
		if (parameters.length >= 3)
			this.attenuationLateral = parameters[2];
		if (parameters.length >= 4)
			this.attenuationAxial = parameters[3];
	}

	@Override
	public double[] getParameters() {
		return new double[]  {sizeCenter, sizeTop, attenuationLateral, attenuationAxial};
	}

	@Override
	public void fill(RealSignal signal) {
		double axial[] = new double[nz];
		double s[] = new double[nz];
		double tauAxial = - Math.log(attenuationAxial);
		double tauLateral = - Math.log(attenuationLateral);
		double diag = Math.sqrt(nx*nx+ny*ny) * 0.5;
		double A = amplitude;
		for(int k=0; k<nz; k++) {
			double dz = Math.abs(k-zc)/(0.5*nz);
			s[k] =  1.0 / (sizeCenter + dz*(sizeTop-sizeCenter));
			axial[k] =  A*Math.exp(-tauAxial*dz);
		}
		for(int i=0; i<nx; i++)
		for(int j=0; j<ny; j++) {
			double r = Math.sqrt((i-xc)*(i-xc)+(j-yc)*(j-yc));
			double att = A*Math.exp(-tauLateral*r/diag);
			for(int k=0; k<nz; k++) {
				double jc = Bessel.J0(r * s[k]);
				signal.data[k][i+j*nx] = (float)(jc*jc*axial[k]*att);
			}
		}
	}
}
