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

import deconvolutionlab.monitor.Monitors;
import fft.AbstractFFT;
import fft.Separability;
import signal.ComplexSignal;
import signal.RealSignal;

public class AcademicFFT_XYZ extends AbstractFFT {

	private AcademicFFT fftXYZ;

	public AcademicFFT_XYZ() {
		super(Separability.XYZ);
	}

	@Override
	public void init(Monitors monitors, int nx, int ny, int nz) {					
		super.init(monitors, nx, ny, nz);
		fftXYZ = new AcademicFFT(nx, ny, nz, 0, 0, 0);
	}

	@Override
	public void transformInternal(RealSignal x, ComplexSignal X) {
		float imag[] = new float[nx * ny * nz];
		float real[] = x.getXYZ();
		fftXYZ.directTransform(real, imag, null, null, AcademicFFT.InputDataType.REALINPUT);
		X.setXYZ(real, imag);
	}
	
	@Override
	public void inverseInternal(ComplexSignal X, RealSignal x) {
		float real[] = X.getRealXYZ();
		float imag[] = X.getImagXYZ();
		fftXYZ.inverseTransform(real, imag, null, null);
		x.setXYZ(real);
	}
	
	@Override
	public String getName() {
		return "AcademicFFT XYZ";
	}
	
	
	@Override
	public boolean isMultithreadable() {
		return true;
	}

}
