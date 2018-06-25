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
import edu.emory.mathcs.jtransforms.fft.FloatFFT_2D;
import edu.emory.mathcs.jtransforms.fft.FloatFFT_3D;
import fft.AbstractFFT;
import fft.Separability;

public class JTransforms extends AbstractFFT {

	private FloatFFT_3D fftXYZ = null;
	private FloatFFT_2D fftXY = null;

	public JTransforms() {
		super(Separability.XYZ);
	}

	@Override
	public void init(Monitors monitors, int nx, int ny, int nz) {					
		super.init(monitors, nx, ny, nz);
		try {
			if (nz > 1)
				fftXYZ = new FloatFFT_3D(nz, ny, nx);
			else 
				fftXY = new FloatFFT_2D(ny, nx);			
		}
		catch (Exception ex) {
			System.out.println("check " + ex + ". " + nx + " " + ny + " " + nz);			
		}
	}

	@Override
	public void transformInternal(RealSignal x, ComplexSignal X) {
		float[] interleave = x.getInterleaveXYZAtReal();
		if (fftXYZ != null)
			fftXYZ.complexForward(interleave);
		if (fftXY != null)
			fftXY.complexForward(interleave);
		X.setInterleaveXYZ(interleave);
	}
	
	@Override
	public void inverseInternal(ComplexSignal X, RealSignal x) {
		float[] interleave = X.getInterleaveXYZ();
		if (fftXYZ != null)
			fftXYZ.complexInverse(interleave, true);
		if (fftXY != null)
			fftXY.complexInverse(interleave, true);
		x.setInterleaveXYZAtReal(interleave);
	}

	@Override
	public String getName() {
		return "JTransforms";
	}

	@Override
	public boolean isMultithreadable() {
		return true;
	}

}
