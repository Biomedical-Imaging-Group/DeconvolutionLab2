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

package wavelets;

import java.util.ArrayList;

import wavelets.haar.HaarWavelets;
import wavelets.spline.Spline1Wavelets;
import wavelets.spline.Spline3Wavelets;
import wavelets.spline.Spline5Wavelets;

public class Wavelets {

	public static ArrayList<AbstractWavelets> getWavelets() {
		ArrayList<AbstractWavelets> list = new ArrayList<AbstractWavelets>();
		list.add(new HaarWavelets(3));
		list.add(new Spline1Wavelets(3));
		list.add(new Spline3Wavelets(3));
		list.add(new Spline5Wavelets(3));
		return list;
	}

	public static ArrayList<String> getWaveletsName() {
		ArrayList<AbstractWavelets> wavelets = getWavelets();
		ArrayList<String> names = new ArrayList<String>();
		for(AbstractWavelets wavelet : wavelets)
			names.add(wavelet.getName());
		return names;
	}

	public static AbstractWavelets getWaveletsByName(String name) {
		for (AbstractWavelets w : getWavelets()) {
			if (name.equals(w.getName()))
				return w;
		}
		return getDefaultWavelets();
	}

	public static String[] getWaveletsAsArray() {
		ArrayList<AbstractWavelets> wavelets = getWavelets();
		String names[] = new String[wavelets.size()];
		for(int i=0; i<wavelets.size(); i++)
			names[i] = wavelets.get(i).getName();
		return names;
	}
	
	public static AbstractWavelets getDefaultWavelets() {
		return new HaarWavelets(3);
	}

	public static String getDocumentation(String name) {
		for (AbstractWavelets wavelets : getWavelets()) {
			if (name.equals(wavelets.getName()))
				return wavelets.getDocumentation();
		}
		return "Unknown FFT library";
	}

}
