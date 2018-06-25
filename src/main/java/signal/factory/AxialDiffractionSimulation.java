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

import deconvolutionlab.monitor.Monitors;
import fft.AbstractFFT;
import fft.FFT;
import signal.ComplexSignal;
import signal.RealSignal;
import signal.factory.complex.ComplexSignalFactory;

public class AxialDiffractionSimulation extends SignalFactory {

	private double pupil = 10;
	private double defocusFactor = 10;
	private double waveNumberAxial = 2;

	public AxialDiffractionSimulation(double pupil, double defocusFactor, double waveNumberAxial) {
		super(new double[] {pupil, defocusFactor, waveNumberAxial});
		setParameters(new double[] {pupil, defocusFactor, waveNumberAxial});
	}

	@Override
	public String getName() {
		return "AxialDiffractionSimulation";
	}

	@Override
	public String[] getParametersName() {
		return new String[] { "Pupil Size", "Defocus Factor", "Wave Number (Axial)" };
	}

	@Override
	public void setParameters(double[] parameters) {
		if (parameters.length >= 1)
			this.pupil = parameters[0];
		if (parameters.length >= 2)
			this.defocusFactor = parameters[1];
		if (parameters.length >= 3)
			this.waveNumberAxial = parameters[2];

	}

	@Override
	public double[] getParameters() {
		return new double[]  {pupil, defocusFactor, waveNumberAxial};
	}

	@Override
	public void fill(RealSignal signal) {
		AbstractFFT fft = FFT.getFastestFFT().getDefaultFFT();
		fft.init(Monitors.createDefaultMonitor(), nx, ny, 1);

		double defocusTop = 2.0*Math.PI / (defocusFactor*pupil);
		double defocusCen = 2.0*Math.PI / pupil;
		int xsize = nx / 2;
		int ysize = ny / 2;
		int zsize = nz / 2;
		
		double diag = Math.sqrt(xsize*xsize + ysize*ysize);
		double wx, wy, wz;
		for (int z = 0; z <= zsize; z++) {
			float[][][] real = new float[xsize + 1][ysize + 1][1];
			float[][][] imag = new float[xsize + 1][ysize + 1][1];
			wz = waveNumberAxial*(zsize-z)*2.0*Math.PI / zsize;
			double cosz = Math.cos(wz);
			double sinz = Math.sin(wz);
			double fcz =  z * Math.abs(defocusTop-defocusCen) / zsize + defocusCen;
			double fcz2 = fcz*fcz;
			for (int y = 0; y <= ysize; y++)
			for (int x = 0; x <= xsize; x++) {
				wx = x * x;
				wy = y * y;
				double g = (wy + wx) / diag;
				if (g < fcz2) {
					real[x][y][0] = (float) (g * cosz);
					imag[x][y][0] = (float) (g * sinz);
				}
			}
			ComplexSignal c = ComplexSignalFactory.createHermitian(""+z, nx, ny, 1, real, imag);
			RealSignal pz = fft.inverse(c).circular();
			signal.setXY(z, pz.getXY(0));
			signal.setXY(nz-1-z, pz.duplicate().getXY(0));
		}
		signal.rescale(0, amplitude);
	}
}
