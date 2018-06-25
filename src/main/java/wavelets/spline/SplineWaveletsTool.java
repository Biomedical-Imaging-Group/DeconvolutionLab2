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

public class SplineWaveletsTool {

	private SplineFilter	filters;

	public SplineWaveletsTool(int scale, int order) {
		this.filters = new SplineFilter(order);
	}

	public void analysis1(RealSignal in, RealSignal out) {
		int nx = in.nx;
		int ny = in.ny;
		int nz = in.nz;
		float ux[] = new float[nx];
		float vx[] = new float[nx];
		for (int z = 0; z < nz; z++)
			for (int y = 0; y < ny; y++) {
				ux = in.getX(y, z);
				splitMirror(ux, vx, filters.h, filters.g);
				out.setX(y, z, vx);
			}

		float uy[] = new float[ny];
		float vy[] = new float[ny];
		for (int z = 0; z < nz; z++)
			for (int x = 0; x < nx; x++) {
				uy = out.getY(x, z);
				splitMirror(uy, vy, filters.h, filters.g);
				out.setY(x, z, vy);
			}

		if (nz > 1) {
			float uz[] = new float[nz];
			float vz[] = new float[nz];
			for (int x = 0; x < nx; x++)
				for (int y = 0; y < ny; y++) {
					uz = out.getZ(x, y);
					splitMirror(uz, vz, filters.h, filters.g);
					out.setZ(x, y, vz);
				}
		}
	}

	public void synthesis1(RealSignal in, RealSignal out) {
		int nx = in.nx;
		int ny = in.ny;
		int nz = in.nz;
		float ux[] = new float[nx];
		float vx[] = new float[nx];
		for (int z = 0; z < nz; z++)
			for (int y = 0; y < ny; y++) {
				ux = in.getX(y, z);
				mergeMirror(ux, vx, filters.h, filters.g);
				out.setX(y, z, vx);
			}

		float uy[] = new float[ny];
		float vy[] = new float[ny];
		for (int z = 0; z < nz; z++)
			for (int x = 0; x < nx; x++) {
				uy = out.getY(x, z);
				mergeMirror(uy, vy, filters.h, filters.g);
				out.setY(x, z, vy);
			}

		if (nz > 1) {
			float uz[] = new float[nz];
			float vz[] = new float[nz];
			for (int x = 0; x < nx; x++)
				for (int y = 0; y < ny; y++) {
					uz = out.getZ(x, y);
					mergeMirror(uz, vz, filters.h, filters.g);
					out.setZ(x, y, vz);
				}
		}
	}

	static private void splitMirror(float vin[], float vout[], double h[], double g[]) {
		int n = vin.length;
		int n2 = n / 2;
		int nh = h.length;
		int ng = g.length;

		double pix;
		int j, k, j1, j2;
		int period = 2 * n - 2; // period for mirror boundary conditions

		for (int i = 0; i < n2; i++) {
			j = i * 2;
			pix = vin[j] * h[0];
			for (k = 1; k < nh; k++) { // Low pass part
				j1 = j - k;
				if (j1 < 0) { // Mirror conditions
					while (j1 < 0)
						j1 += period; // Periodize
					if (j1 >= n)
						j1 = period - j1; // Symmetrize
				}
				j2 = j + k;
				if (j2 >= n) { // Mirror conditions
					while (j2 >= n)
						j2 -= period; // Periodize
					if (j2 < 0)
						j2 = -j2; // Symmetrize
				}
				pix = pix + h[k] * (vin[j1] + vin[j2]);
			}
			vout[i] = (float) pix;

			j = j + 1;
			pix = vin[j] * g[0]; // High pass part
			for (k = 1; k < ng; k++) {
				j1 = j - k;
				if (j1 < 0) { // Mirror conditions
					while (j1 < 0)
						j1 += period; // Periodize
					if (j1 >= n)
						j1 = period - j1; // Symmetrize
				}
				j2 = j + k;
				if (j2 >= n) { // Mirror conditions
					while (j2 >= n)
						j2 -= period; // Periodize
					if (j2 < 0)
						j2 = -j2; // Symmetrize
				}
				pix = pix + g[k] * (vin[j1] + vin[j2]);
			}
			vout[i + n2] = (float) pix;
		}
	}

	static private void mergeMirror(float vin[], float vout[], double h[], double g[]) {
		int n = vin.length;
		int n2 = n / 2;
		int nh = h.length;
		int ng = g.length;

		double pix1, pix2;
		int j, k, kk, i1, i2;
		int k01 = (nh / 2) * 2 - 1;
		int k02 = (ng / 2) * 2 - 1;

		int period = 2 * n2 - 1; // period for mirror boundary conditions

		for (int i = 0; i < n2; i++) {
			j = 2 * i;
			pix1 = h[0] * vin[i];
			for (k = 2; k < nh; k += 2) {
				i1 = i - (k / 2);
				if (i1 < 0) {
					i1 = (-i1) % period;
					if (i1 >= n2)
						i1 = period - i1;
				}
				i2 = i + (k / 2);
				if (i2 > n2 - 1) {
					i2 = i2 % period;
					if (i2 >= n2)
						i2 = period - i2;
				}
				pix1 = pix1 + h[k] * (vin[i1] + vin[i2]);
			}

			pix2 = 0.;
			for (k = -k02; k < ng; k += 2) {
				kk = Math.abs(k);
				i1 = i + (k - 1) / 2;
				if (i1 < 0) {
					i1 = (-i1 - 1) % period;
					if (i1 >= n2)
						i1 = period - 1 - i1;
				}
				if (i1 >= n2) {
					i1 = i1 % period;
					if (i1 >= n2)
						i1 = period - 1 - i1;
				}
				pix2 = pix2 + g[kk] * vin[i1 + n2];
			}

			vout[j] = (float) (pix1 + pix2);

			j = j + 1;
			pix1 = 0.;
			for (k = -k01; k < nh; k += 2) {
				kk = Math.abs(k);
				i1 = i + (k + 1) / 2;
				if (i1 < 0) {
					i1 = (-i1) % period;
					if (i1 >= n2)
						i1 = period - i1;
				}
				if (i1 >= n2) {
					i1 = (i1) % period;
					if (i1 >= n2)
						i1 = period - i1;
				}
				pix1 = pix1 + h[kk] * vin[i1];
			}
			pix2 = g[0] * vin[i + n2];
			for (k = 2; k < ng; k += 2) {
				i1 = i - (k / 2);
				if (i1 < 0) {
					i1 = (-i1 - 1) % period;
					if (i1 >= n2)
						i1 = period - 1 - i1;
				}
				i2 = i + (k / 2);
				if (i2 > n2 - 1) {
					i2 = i2 % period;
					if (i2 >= n2)
						i2 = period - 1 - i2;
				}
				pix2 = pix2 + g[k] * (vin[i1 + n2] + vin[i2 + n2]);
			}
			vout[j] = (float) (pix1 + pix2);
		}
	}

}
