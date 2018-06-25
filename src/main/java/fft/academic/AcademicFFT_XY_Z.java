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

package fft.academic;

import deconvolutionlab.monitor.Monitors;
import fft.AbstractFFT;
import fft.Separability;
import signal.ComplexSignal;
import signal.RealSignal;

public class AcademicFFT_XY_Z extends AbstractFFT {

	private AcademicFFT	fftXY;
	private AcademicFFT	fftZ;

	public AcademicFFT_XY_Z() {
		super(Separability.XY_Z);
	}
	
	@Override
	public void init(Monitors monitors, int nx, int ny, int nz) {					
		super.init(monitors, nx, ny, nz);
		fftXY = new AcademicFFT(nx, ny, 0, 0);
		fftZ = new AcademicFFT(nz, 0);
	}

	@Override
	public void transformInternal(RealSignal x, ComplexSignal X) {
		for (int k = 0; k < nz; k++) {
			float real[] = new float[nx * ny];
			float imag[] = new float[nx * ny];
			System.arraycopy(x.data[k], 0, real, 0, nx * ny);
			fftXY.directTransform(real, imag, null, null, AcademicFFT.InputDataType.REALINPUT);
			X.setRealXY(k, real);
			X.setImagXY(k, imag);
		}
		
		for (int i = 0; i < nx; i++)
		for (int j = 0; j < ny; j++) {
			float real[] = X.getRealZ(i, j);
			float imag[] = X.getImagZ(i, j);
			fftZ.directTransform(real, imag, null, null, AcademicFFT.InputDataType.COMPLEXINPUT);
			X.setRealZ(i, j, real);
			X.setImagZ(i, j, imag);
		}
	}
	
	@Override
	public void inverseInternal(ComplexSignal X, RealSignal x) {
		for (int k = 0; k < nz; k++) {
			float real[] = X.getRealXY(k);
			float imag[] = X.getImagXY(k);
			fftXY.inverseTransform(real, imag, null, null);
			X.setRealXY(k, real);
			X.setImagXY(k, imag);
		}
		for (int i = 0; i < nx; i++)
		for (int j = 0; j < ny; j++) {
			float real[] = X.getRealZ(i, j);
			float imag[] = X.getImagZ(i, j);
			fftZ.inverseTransform(real, imag, null, null);
			x.setZ(i, j, real);
		}
	}
	
	@Override
	public String getName() {
		return "AcademicFFT XY and Z";
	}
	
	@Override
	public boolean isMultithreadable() {
		return true;
	}

}
