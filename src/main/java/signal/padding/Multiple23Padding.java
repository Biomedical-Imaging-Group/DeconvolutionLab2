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

import java.util.ArrayList;
import java.util.Collections;

public class Multiple23Padding extends AbstractPadding {

	private ArrayList<Integer> m23 = new ArrayList<Integer>();

	public Multiple23Padding() {
		for(int m2=0; m2<12; m2++)
			for(int m3=0; m3<11; m3++) {
				double p = Math.pow(2, m2) * Math.pow(3, m3);
				if (p<20000.0)
					m23.add(new Integer((int)p));
			}
		Collections.sort(m23);		
	}

	@Override
	public String getName() {
		return "Multiple {2, 3}";
	}

	@Override
	public String getShortname() {
		return "X2X3";
	}

	@Override
	public int padding(int x) {
		for(Integer m : m23)
			if (m >= x)
				return m.intValue();
		return x;
	}
}
