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

public class Cross extends SignalFactory {

	private double	thickness			= 1.0;
	private double	slope				= 1.0;
	private double	lengthPercentage	= 30.0;

	public Cross(double thickness, double slope, double lengthPercentage) {
		super(new double[] {thickness, slope, lengthPercentage});
		setParameters(new double[] {thickness, slope, lengthPercentage});
	}

	@Override
	public void setParameters(double[] parameters) {
		if (parameters.length >= 1)
			this.thickness = parameters[0];
		if (parameters.length >= 2)
			this.slope = parameters[1];
		if (parameters.length >= 3)
			this.lengthPercentage = parameters[2];
	}

	@Override
	public String getName() {
		return "Cross";
	}

	@Override
	public double[] getParameters() {
		return new double[] { thickness, slope, lengthPercentage };
	}

	@Override
	public String[] getParametersName() {
		return new String[] { "Thickness", "Sigmoid Curve Slope", "Length (percentage)" };
	}

	@Override
	public void fill(RealSignal signal) {
		double length = lengthPercentage * 0.01 * 0.5;
		int x1 = (int) Math.round(xc - nx * length);
		int x2 = (int) Math.round(xc + nx * length);
		int y1 = (int) Math.round(yc - ny * length);
		int y2 = (int) Math.round(yc + ny * length);
		int z1 = (int) Math.round(zc - nz * length);
		int z2 = (int) Math.round(zc + nz * length);
		for (int z = 0; z < nz; z++) {
			for (int x = 0; x < nx; x++)
				for (int y = 0; y < ny; y++) {
					double px = (x - xc) * (x - xc);
					double py = (y - yc) * (y - yc);
					double pz = (z - zc) * (z - zc);

					double dx = Double.MAX_VALUE;
					for (int i = x1; i < x2; i++)
						dx = Math.min(dx, (i - x) * (i - x) + py + pz);
					dx = Math.sqrt(dx) - thickness;
					double ax = (1.0 - 1.0 / (1.0 + Math.exp(-dx / slope)));

					double dy = Double.MAX_VALUE;
					for (int j = y1; j < y2; j++)
						dy = Math.min(dy, px + (j - y) * (j - y) + pz);
					dy = Math.sqrt(dy) - thickness;
					double ay = (1.0 - 1.0 / (1.0 + Math.exp(-dy / slope)));

					double dz = Double.MAX_VALUE;
					for (int k = z1; k < z2; k++)
						dz = Math.min(dz, px + py + (k - z) * (k - z));
					dz = Math.sqrt(dz) - thickness;
					double az = (1.0 - 1.0 / (1.0 + Math.exp(-dz / slope)));

					signal.data[z][x + nx * y] = (float) (Math.max(Math.max(ax, ay), az));
				}
		}
		signal.rescale(0, amplitude);
	}

}
