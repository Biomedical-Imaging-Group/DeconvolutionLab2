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

public class Assessment {

	public static double snr(RealSignal test, RealSignal ref) {
		double measures[] = measure(test, ref);
		return measures[0];
	}

	public static double psnr(RealSignal test, RealSignal ref) {
		double measures[] = measure(test, ref);
		return measures[1];
	}

	public static double rmse(RealSignal test, RealSignal ref) {
		double measures[] = measure(test, ref);
		return measures[3];
	}
	
	public static double relativeResidu(RealSignal test, RealSignal ref) {
		double measures[] = measure(test, ref);
		return measures[3] / Math.max(measures[2], Operations.epsilon);
	}

	/**
	 * 
	 * @param test	RealSignal
	 * @param ref	RealSignal
	 * @return	an array of double containing
	 */
	public static double[] measure(RealSignal test, RealSignal ref) {
		int nxy = Math.min(test.nx * test.ny, ref.nx * ref.ny);
		int nz = Math.min(test.nz, ref.nz);
		double max = -Double.MAX_VALUE;
		double sref = 0.0;
		double rmse = 0.0;
		double a = 0.0;
		for (int k = 0; k < nz; k++)
		for (int i = 0; i < nxy; i++) {
if (ref.name.equals("y"))
	System.out.println("" + i + " " + k + " : " + ref.data[0].length + " " + ref.data.length);
			a = ref.data[k][i];
			if (max < a)
				max = a;
			sref += a * a;
			rmse += (a-test.data[k][i]) * (a-test.data[k][i]); 
		}
		sref = Math.sqrt(sref / (nz*nxy));
		rmse = Math.sqrt(rmse / (nz*nxy));
		if (rmse <= 0)
			return new double[] {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, sref, rmse, max};
		else
			return new double[] {20*Math.log10(sref/rmse), 20*Math.log10(max/rmse), sref, rmse, max};
	}
}
