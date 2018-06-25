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

package signal;

/**
 * This class is an abstract class to store a 3D signal in a float array 'data'.
 * The data are store in a 2D array, the first index represents the z direction
 * and the second store a 2D plane in row-major representation.
 * 
 * There are two implementations of this class: RealSignal to store a real volume
 * and ComplexSignal to store a complex signal in interleaving mode.
 * 
 * @author Daniel Sage
 *
 */
public class Signal {

	final public int nx;
	final public int ny;
	final public int nz;
	public float	data[][];
	public String	name = "untitled";
	
	public Signal(String name, int nx, int ny, int nz) {
		this.name = name;
		this.nx = nx;
		this.ny = ny;
		this.nz = nz;
	}

	public Signal setName(String name) {
		this.name = name;
		return this;
	}
	
	public String dimAsString() {
		return nx + "x" + ny + "x" + nz + " ";
	}
	
}
