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

package wavelets.haar;

import signal.RealSignal;
import wavelets.AbstractWavelets;

public class HaarWavelets extends AbstractWavelets {

	private double	q	= Math.sqrt(2);

	public HaarWavelets(int scales) {
		super(scales);
	}

	@Override
	public void setScale(int scale) {
		this.scales = scale;
	}

	@Override
	public String getName() {
		return "Haar";
	}

	@Override
	public String getDocumentation() {
		return "Haar Wavelets";
	}

	@Override
	public void analysis1(RealSignal in, RealSignal out) {
		int nx = in.nx;
		int ny = in.ny;
		int nz = in.nz;

		float ux[] = new float[nx];
		float vx[] = new float[nx];
		for (int z = 0; z < nz; z++)
			for (int y = 0; y < ny; y++) {
				ux = in.getX(y, z);
				split(ux, vx);
				out.setX(y, z, vx);
			}

		float uy[] = new float[ny];
		float vy[] = new float[ny];
		for (int z = 0; z < nz; z++)
			for (int x = 0; x < nx; x++) {
				uy = out.getY(x, z);
				split(uy, vy);
				out.setY(x, z, vy);
			}
		if (nz > 1) {
			float uz[] = new float[nz];
			float vz[] = new float[nz];
			for (int x = 0; x < nx; x++)
				for (int y = 0; y < ny; y++) {
					uz = out.getZ(x, y);
					split(uz, vz);
					out.setZ(x, y, vz);
				}
		}
	}

	@Override
	public void synthesis1(RealSignal in, RealSignal out) {

		int nx = in.nx;
		int ny = in.ny;
		int nz = in.nz;

		float ux[] = new float[nx];
		float vx[] = new float[nx];
		for (int z = 0; z < nz; z++)
			for (int y = 0; y < ny; y++) {
				ux = in.getX(y, z);
				merge(ux, vx);
				out.setX(y, z, vx);
			}

		float uy[] = new float[ny];
		float vy[] = new float[ny];
		for (int z = 0; z < nz; z++)
			for (int x = 0; x < nx; x++) {
				uy = out.getY(x, z);
				merge(uy, vy);
				out.setY(x, z, vy);
			}
		
		if (nz > 1) {
			float uz[] = new float[nz];
			float vz[] = new float[nz];
			for (int x = 0; x < nx; x++)
				for (int y = 0; y < ny; y++) {
					uz = out.getZ(x, y);
					merge(uz, vz);
					out.setZ(x, y, vz);
				}
		}
	}

	private void split(float vin[], float vout[]) {
		int n2 = vin.length / 2;
		int j;
		for (int i = 0; i < n2; i++) {
			j = 2 * i;
			vout[i] = (float) ((vin[j] + vin[j + 1]) / q);
			vout[i + n2] = (float) ((vin[j] - vin[j + 1]) / q);
		}

	}

	private void merge(float vin[], float vout[]) {
		int n2 = vin.length / 2;
		for (int i = 0; i < n2; i++) {
			vout[2 * i] = (float) ((vin[i] + vin[i + n2]) / q);
			vout[2 * i + 1] = (float) ((vin[i] - vin[i + n2]) / q);
		}
	}
}
