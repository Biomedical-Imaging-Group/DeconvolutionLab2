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

package fft.jtransforms;

import signal.ComplexSignal;
import signal.RealSignal;
import deconvolutionlab.monitor.Monitors;
import edu.emory.mathcs.jtransforms.fft.FloatFFT_1D;
import edu.emory.mathcs.jtransforms.fft.FloatFFT_2D;
import fft.AbstractFFT;
import fft.Separability;

public class JTransformsFFT_XY_Z extends AbstractFFT {

	private FloatFFT_2D	fftXY	= null;
	private FloatFFT_1D	fftZ	= null;

	public JTransformsFFT_XY_Z() {
		super(Separability.XY_Z);
	}

	@Override
	public void init(Monitors monitors, int nx, int ny, int nz) {					
		super.init(monitors, nx, ny, nz);
		try {
			fftXY = new FloatFFT_2D(ny, nx);
			if (nz > 1)
				fftZ = new FloatFFT_1D(nz);
		}
		catch (Exception ex) {
		}
	}

	@Override
	public void transformInternal(RealSignal x, ComplexSignal X) {
		for (int k = 0; k < nz; k++) {
			float interleave[] = x.getInterleaveXYAtReal(k);
			fftXY.complexForward(interleave);
			X.setInterleaveXY(k, interleave);
		}
		if (fftZ == null)
			return;
		for (int i = 0; i < nx; i++)
			for (int j = 0; j < ny; j++) {
				float interleave[] = X.getInterleaveZ(i, j);
				fftZ.complexForward(interleave);
				X.setInterleaveZ(i, j, interleave);
			}
	}

	@Override
	public void inverseInternal(ComplexSignal X, RealSignal x) {
		for (int k = 0; k < nz; k++) {
			float interleave[] = X.getInterleaveXY(k);
			fftXY.complexInverse(interleave, true);
			X.setInterleaveXY(k, interleave);
		}
		if (fftZ != null) {
			for (int i = 0; i < nx; i++)
				for (int j = 0; j < ny; j++) {
					float interleave[] = X.getInterleaveZ(i, j);
					fftZ.complexInverse(interleave, true);
					X.setInterleaveZ(i, j, interleave);
				}
		}
		x = X.getRealSignal();
	}

	@Override
	public String getName() {
		return "JTransforms XY_Z";
	}

	@Override
	public boolean isMultithreadable() {
		return true;
	}

}
