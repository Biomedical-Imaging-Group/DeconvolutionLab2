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

package fft;

import deconvolutionlab.monitor.Monitors;
import signal.ComplexSignal;
import signal.RealSignal;

public abstract class AbstractFFT {

	protected int	nx = 2;
	protected int	ny = 2;
	protected int	nz = 2;
	protected Separability sep;
	
	public AbstractFFT(Separability sep) {
		this.sep = sep;
	}
	
	public Separability getSeparability() {
		return sep;
	}
	
	public void init(Monitors monitors, int nx, int ny, int nz) {
		this.nx = nx;
		this.ny = ny;
		this.nz = nz;
		monitors.log(getName() + " initialized for [" + nx + "x" + ny + "x" + nz + "]");
	}
	
	public abstract void transformInternal(RealSignal xInput, ComplexSignal XAllocated);
	public abstract void inverseInternal(ComplexSignal XInput, RealSignal xAllocated);
	
	public ComplexSignal transform(RealSignal x, ComplexSignal XAllocated) {
		if (XAllocated == null)
			XAllocated = new ComplexSignal("fft(" + x.name + ")", nx, ny, nz);
		transformInternal(x, XAllocated);
		return XAllocated;
	}

	public ComplexSignal transform(RealSignal x) {
		ComplexSignal X = new ComplexSignal("fft(" + x.name + ")", nx, ny, nz);
		transformInternal(x, X);
		return X;
	}

	public RealSignal inverse(ComplexSignal X, RealSignal xAllocated) {
		if (xAllocated == null)
			xAllocated = new RealSignal("ifft(" + X.name + ")", nx, ny, nz);
		inverseInternal(X, xAllocated);
		return xAllocated;
	}
	public RealSignal inverse(ComplexSignal X) {
		RealSignal x = new RealSignal("ifft(" + X.name + ")", nx, ny, nz);
		inverseInternal(X, x);
		return x;
	}
	
	public abstract String getName();
	public abstract boolean isMultithreadable();

	public int getSizeX() {
		return nx;
	}

	public int getSizeY() {
		return ny;
	}

	public int getSizeZ() {
		return nz;
	}
}
