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

package signal.factory.complex;

import signal.ComplexSignal;

public class ComplexSignalFactory {

	public static ComplexSignal gaussian(int nx, int ny, int nz, double sigma) {
		double K = sigma * sigma / 2.0;
		int xsize = nx / 2;
		int ysize = ny / 2;
		int zsize = nz / 2;
		float[][][] function = new float[xsize + 1][ysize + 1][zsize + 1];
		double wx, wy, wz, wr;
		for (int z = 0; z <= zsize; z++)
			for (int y = 0; y <= ysize; y++)
				for (int x = 0; x <= xsize; x++) {
					wx = (xsize > 0 ? Math.PI * x / xsize : 0);
					wy = (ysize > 0 ? Math.PI * y / ysize : 0);
					wz = (zsize > 0 ? Math.PI * z / zsize : 0);
					wr = Math.sqrt(wx * wx + wy * wy + wz * wz);
					function[x][y][z] = (float) Math.exp(-wr * wr * K);
				}
		return createHermitian("Gaussian", nx, ny, nz, function);
	}

	public static ComplexSignal identity(int nx, int ny, int nz) {
		int xsize = nx / 2;
		int ysize = ny / 2;
		int zsize = nz / 2;
		float[][][] function = new float[xsize + 1][ysize + 1][zsize + 1];
		for (int z = 0; z <= zsize; z++)
			for (int y = 0; y <= ysize; y++)
				for (int x = 0; x <= xsize; x++)
					function[x][y][z] = 1.0f;
		return createHermitian("Identity", nx, ny, nz, function);
	}

	public static ComplexSignal laplacian(int nx, int ny, int nz) {
		int xsize = nx / 2;
		int ysize = ny / 2;
		int zsize = nz / 2;
		float[][][] function = new float[xsize + 1][ysize + 1][zsize + 1];
		double wx, wy, wz;
		for (int z = 0; z <= zsize; z++)
			for (int y = 0; y <= ysize; y++)
				for (int x = 0; x <= xsize; x++) {
					wx = (xsize > 0 ? Math.PI * x / xsize : 0);
					wy = (ysize > 0 ? Math.PI * y / ysize : 0);
					wz = (zsize > 0 ? Math.PI * z / zsize : 0);
					function[x][y][z] = (float) ((wx * wx + wy * wy + wz * wz));
				}
		return createHermitian("Laplacian", nx, ny, nz, function);
	}
	
	public static ComplexSignal directionalDerivative(int nx, int ny, int nz, double vx, double vy, double vz) {
		int xsize = nx / 2;
		int ysize = ny / 2;
		int zsize = nz / 2;
		float[][][] function = new float[xsize + 1][ysize + 1][zsize + 1];
		double wx, wy, wz;
		for (int z = 0; z <= zsize; z++)
			for (int y = 0; y <= ysize; y++)
				for (int x = 0; x <= xsize; x++) {
					wx = (xsize > 0 ? Math.PI * x / xsize : 0);
					wy = (ysize > 0 ? Math.PI * y / ysize : 0);
					wz = (zsize > 0 ? Math.PI * z / zsize : 0);
					function[x][y][z] = (float) ((wx * vx + vy * wy + vz * wz));
				}
		return createHermitian("Directional Derivative", nx, ny, nz, function);
	}

	public static ComplexSignal rings(int nx, int ny, int nz, double mu) {
		int xsize = nx / 2;
		int ysize = ny / 2;
		int zsize = nz / 2;
		double K = ysize/2;
		float[][][] function = new float[xsize + 1][ysize + 1][zsize + 1];
		double wx, wy, wz, wr;
		for (int z = 0; z <= zsize; z++)
			for (int y = 0; y <= ysize; y++)
				for (int x = 0; x <= xsize; x++) {
					wx = (xsize > 0 ? Math.PI * x / xsize : 0);
					wy = (ysize > 0 ? Math.PI * y / ysize : 0);
					wz = (zsize > 0 ? Math.PI * z / zsize : 0);
					wr = Math.sqrt(wx * wx + wy * wy + wz*wz);
					function[x][y][z] = 
							(float) (1.0 - 1.0 / (1.0 + Math.exp(-K * (wr - mu)))
							+ 1.0 / (1.0 + Math.exp(-K * (wr - 0.6 * mu))) 
							- 1.0 / (1.0 + Math.exp(-K * (wr - 0.4 * mu))));
				}
		return createHermitian("Airy", nx, ny, nz, function);
	}

	public static ComplexSignal createHermitian(String name, int nx, int ny, int nz, float[][][] firstQuadrantReal, float[][][] firstQuadrantImag) {
		ComplexSignal signal = new ComplexSignal(name, nx, ny, nz);
		int xsize = firstQuadrantReal.length - 1;
		int ysize = firstQuadrantReal[0].length - 1;
		int zsize = firstQuadrantReal[0][0].length - 1;
		if (xsize >= 1 && ysize >= 1 && zsize >= 1) {
			for (int z = 0; z <= zsize; z++)
				for (int y = 0; y <= ysize; y++)
					for (int x = 0; x <= xsize; x++) {
						signal.data[z][2 * (x + nx * y)] = firstQuadrantReal[x][y][z];
						signal.data[z][2 * (x + nx * y)+1] = firstQuadrantImag[x][y][z];
					}
			for (int z = 0; z < zsize; z++)
				for (int y = 0; y < ysize; y++)
					for (int x = 0; x < xsize; x++) {
						int a = nx - 1 - x;
						int b = nx * (ny - 1 - y);
						signal.data[z][2 * (a + nx * y)] = firstQuadrantReal[x + 1][y][z];
						signal.data[z][2 * (a + b)] = firstQuadrantReal[x + 1][y + 1][z];
						signal.data[z][2 * (x + b)] = firstQuadrantReal[x][y + 1][z];
						signal.data[z][1 + 2 * (a + nx * y)] = firstQuadrantImag[x + 1][y][z];
						signal.data[z][1 + 2 * (a + b)] = firstQuadrantImag[x + 1][y + 1][z];
						signal.data[z][1 + 2 * (x + b)] = firstQuadrantImag[x][y + 1][z];
						int c = nz - 1 - z;
						signal.data[c][2 * (x + nx * y)] = firstQuadrantReal[x][y][z + 1];
						signal.data[c][2 * (a + nx * y)] = firstQuadrantReal[x + 1][y][z + 1];
						signal.data[c][2 * (a + b)] = firstQuadrantReal[x + 1][y + 1][z + 1];
						signal.data[c][2 * (x + b)] = firstQuadrantReal[x][y + 1][z + 1];
						signal.data[c][1 + 2 * (x + nx * y)] = firstQuadrantImag[x][y][z + 1];
						signal.data[c][1 + 2 * (a + nx * y)] = firstQuadrantImag[x + 1][y][z + 1];
						signal.data[c][1 + 2 * (a + b)] = firstQuadrantImag[x + 1][y + 1][z + 1];
						signal.data[c][1 + 2 * (x + b)] = firstQuadrantImag[x][y + 1][z + 1];
					}
		}
		if (zsize == 0) {
			for (int y = 0; y <= ysize; y++)
				for (int x = 0; x <= xsize; x++) {
					signal.data[0][2 * (x + nx * y)] = firstQuadrantReal[x][y][0];
					signal.data[0][1 + 2 * (x + nx * y)] = firstQuadrantImag[x][y][0];
				}
			for (int y = 0; y < ysize; y++)
				for (int x = 0; x < xsize; x++) {
					int a = nx - 1 - x;
					int b = nx * (ny - 1 - y);
					signal.data[0][2 * (a + nx * y)] = firstQuadrantReal[x + 1][y][0];
					signal.data[0][2 * (a + b)] = firstQuadrantReal[x + 1][y + 1][0];
					signal.data[0][2 * (x + b)] = firstQuadrantReal[x][y + 1][0];
					signal.data[0][1 + 2 * (a + nx * y)] = firstQuadrantImag[x + 1][y][0];
					signal.data[0][1 + 2 * (a + b)] = firstQuadrantImag[x + 1][y + 1][0];
					signal.data[0][1 + 2 * (x + b)] = firstQuadrantImag[x][y + 1][0];
				}
		}
		return signal;
	}

	public static ComplexSignal createHermitian(String name, int nx, int ny, int nz, float[][][] firstQuadrant) {
		ComplexSignal signal = new ComplexSignal(name, nx, ny, nz);
		int xsize = firstQuadrant.length - 1;
		int ysize = firstQuadrant[0].length - 1;
		int zsize = firstQuadrant[0][0].length - 1;
		if (xsize >= 1 && ysize >= 1 && zsize >= 1) {
			for (int z = 0; z <= zsize; z++)
				for (int y = 0; y <= ysize; y++)
					for (int x = 0; x <= xsize; x++) {
						signal.data[z][2 * (x + nx * y)] = firstQuadrant[x][y][z];
					}
			for (int z = 0; z < zsize; z++)
				for (int y = 0; y < ysize; y++)
					for (int x = 0; x < xsize; x++) {
						int a = nx - 1 - x;
						int b = nx * (ny - 1 - y);
						signal.data[z][2 * (a + nx * y)] = firstQuadrant[x + 1][y][z];
						signal.data[z][2 * (a + b)] = firstQuadrant[x + 1][y + 1][z];
						signal.data[z][2 * (x + b)] = firstQuadrant[x][y + 1][z];
						int c = nz - 1 - z;
						signal.data[c][2 * (x + nx * y)] = firstQuadrant[x][y][z + 1];
						signal.data[c][2 * (a + nx * y)] = firstQuadrant[x + 1][y][z + 1];
						signal.data[c][2 * (a + b)] = firstQuadrant[x + 1][y + 1][z + 1];
						signal.data[c][2 * (x + b)] = firstQuadrant[x][y + 1][z + 1];
					}
		}
		if (zsize == 0) {
			for (int y = 0; y <= ysize; y++)
				for (int x = 0; x <= xsize; x++) {
					signal.data[0][2 * (x + nx * y)] = firstQuadrant[x][y][0];
				}
			for (int y = 0; y < ysize; y++)
				for (int x = 0; x < xsize; x++) {
					int a = nx - 1 - x;
					int b = nx * (ny - 1 - y);
					signal.data[0][2 * (a + nx * y)] = firstQuadrant[x + 1][y][0];
					signal.data[0][2 * (a + b)] = firstQuadrant[x + 1][y + 1][0];
					signal.data[0][2 * (x + b)] = firstQuadrant[x][y + 1][0];
				}
		}
		return signal;
	}
}
