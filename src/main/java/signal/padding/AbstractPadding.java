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

package signal.padding;

public abstract class AbstractPadding {
	
	private int dimOriginal[] = new int[] {0, 0, 0};
	private int dimPadded[] = new int[] {0, 0, 0};
	
	public AbstractPadding() {
	}
	
	public int[] pad(int mx, int my, int mz) {
		dimOriginal[0] = mx;
		dimOriginal[1] = my;
		dimOriginal[2] = mz;
		dimPadded[0] = padding(dimOriginal[0]);
		dimPadded[1] = padding(dimOriginal[1]);
		dimPadded[2] = padding(dimOriginal[2]);
		return dimPadded;
	}
	
	public int[] getOriginalDimension() {
		return dimOriginal;
	}
	public int[] getPaddedDimension() {
		return dimPadded;
	}

	public abstract String getName();
	public abstract String getShortname();
	
	protected abstract int padding(int nx);
}
