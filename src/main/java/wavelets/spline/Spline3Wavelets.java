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

package wavelets.spline;

import signal.RealSignal;
import wavelets.AbstractWavelets;

public class Spline3Wavelets extends AbstractWavelets {

	private SplineWaveletsTool tool;

	public Spline3Wavelets(int scale) {
	    super(scale);
		this.tool = new SplineWaveletsTool(scale, 3);
	}

	@Override
	public void setScale(int scale) {
		this.scales = scale;
		this.tool = new SplineWaveletsTool(scale, 1);
	}

	@Override
	public String getName() {
		return "Spline3";
	}

	@Override
	public String getDocumentation() {
		return "Spline Wavelets (order 3)";
	}

	@Override
    public void analysis1(RealSignal in, RealSignal out) {
		tool.analysis1(in, out);
	}
	
	@Override
    public void synthesis1(RealSignal in, RealSignal out) {
		tool.synthesis1(in, out);
	}
}
