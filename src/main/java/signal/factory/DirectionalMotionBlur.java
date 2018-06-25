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

public class DirectionalMotionBlur extends SignalFactory {

	private double	sigma		= 3.0;
	private double	direction	= 30.0;
	private double	elongation	= 30.0;

	public DirectionalMotionBlur(double sigma, double direction, double elongation) {
		super(new double[] { sigma, direction, elongation });
		setParameters(new double[] { sigma, direction, elongation });
	}

	@Override
	public String getName() {
		return "Direction-Motion-Blur";
	}

	@Override
	public String[] getParametersName() {
		return new String[] { "Sigma", "Lateral Direction (Degree)", "Elongation" };
	}

	@Override
	public void setParameters(double[] parameters) {
		if (parameters.length >= 1)
			this.sigma = parameters[0];
		if (parameters.length >= 2)
			this.direction = parameters[1];
		if (parameters.length >= 3)
			this.elongation = parameters[2];
	}

	@Override
	public double[] getParameters() {
		return new double[] { sigma, direction, elongation };
	}

	@Override
	public void fill(RealSignal signal) {

		double cosa = Math.cos(Math.toRadians(direction));
		double sina = Math.sin(Math.toRadians(direction));

		double xe = elongation * cosa + xc;
		double ye = elongation * sina + yc;
		double dx = (xe - xc) / elongation;
		double dy = (ye - yc) / elongation;
		for(int k=0; k<elongation; k++) {
			double x = xc + k*dx;				
			double y = yc + k*dy;	
			spot(signal, x, y, amplitude);
		}
	}
	
	private void spot(RealSignal signal, double xc, double yc, double A) {
		double slope = 1;
		int x1 = (int) Math.max(0, Math.round(xc - sigma - 3 * slope));
		int x2 = (int) Math.min(nx - 1, Math.round(xc + sigma + 3 * slope));
		int y1 = (int) Math.max(0, Math.round(yc - sigma - 3 * slope));
		int y2 = (int) Math.min(ny - 1, Math.round(yc + sigma + 3 * slope));
		int z1 = (int) Math.max(0, Math.round(zc - sigma - 3 * slope));
		int z2 = (int) Math.min(nz - 1, Math.round(zc + sigma + 3 * slope));
		for (int x = x1; x <= x2; x++)
			for (int y = y1; y <= y2; y++) {
				double dr = Math.sqrt((x - xc) * (x - xc) + (y - yc) * (y - yc)) - sigma;
				float v = (float) (A - A / (1.0 + Math.exp(-dr / slope)));
				for (int z = z1; z <= z2; z++) {
					signal.data[z][x + nx * y] = Math.max(signal.data[z][x + nx * y], v);
				}
			}
	}
}
