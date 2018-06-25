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

import java.awt.image.BufferedImage;

import deconvolutionlab.monitor.Monitors;
import signal.range.RescaledRange;

public class RealSignal extends Signal implements SignalListener {

	private BufferedImage preview;
	
	public RealSignal(String name, int nx, int ny, int nz) {
		super(name, nx, ny, nz);
		this.data = new float[nz][];
		int step = Math.max(1, nz / SignalCollector.NOTIFICATION_RATE);
		notify(name, 0);
		for (int k = 0; k < nz; k++) {
			data[k] = new float[nx * ny];
			if (k % step == 0)
				notify(name, k * 100.0 / nz);
		}
		notify(name, 100);
		SignalCollector.alloc(this);//name, nx, ny, ny, false);
	}

	@Override
	public void notify(String name, double progress) {
		SignalCollector.setProgress(progress);
	}

	public void copy(RealSignal source) {
		int nxy = nx * ny;
		for (int k = 0; k < nz; k++)
			for (int i = 0; i < nxy; i++) {
				data[k][i] = source.data[k][i];
			}
	}

	public void setSignal(RealSignal signal) {
		int sx = signal.nx;
		int mx = Math.min(nx, signal.nx);
		int my = Math.min(ny, signal.ny);
		int mz = Math.min(nz, signal.nz);
		for (int i = 0; i < mx; i++)
			for (int j = 0; j < my; j++)
				for (int k = 0; k < mz; k++)
					data[k][i + nx * j] = signal.data[k][i + sx * j];
	}

	public void getSignal(RealSignal signal) {
		int sx = signal.nx;
		int mx = Math.min(nx, signal.nx);
		int my = Math.min(ny, signal.ny);
		int mz = Math.min(nz, signal.nz);
		for (int i = 0; i < mx; i++)
			for (int j = 0; j < my; j++)
				for (int k = 0; k < mz; k++)
					signal.data[k][i + sx * j] = data[k][i + nx * j];
	}

	/**
	 * Applies a soft threshold (in-place processing)
	 * 
	 * @param inferiorLimit
	 * @param superiorLimit
	 * @return the instance of the calling object
	 */
	public RealSignal thresholdSoft(float inferiorLimit, float superiorLimit) {
		int nxy = nx * ny;
		for (int k = 0; k < nz; k++)
			for (int i = 0; i < nxy; i++) {
				if (data[k][i] <= inferiorLimit)
					data[k][i] += inferiorLimit;
				else if (data[k][i] >= superiorLimit)
					data[k][i] -= superiorLimit;
				else
					data[k][i] = 0f;
			}
		return this;
	}

	/**
	 * Multiplies by a signal pixelwise (in-place processing)
	 * 
	 * @param factor
	 * @return the instance of the calling object
	 */
	public RealSignal times(RealSignal factor) {
		int nxy = nx * ny;
		for (int k = 0; k < nz; k++)
			for (int i = 0; i < nxy; i++) {
				data[k][i] *= factor.data[k][i];
			}
		return this;
	}

	/**
	 * Multiplies by a scalar factor (in-place processing)
	 * 
	 * @param factor
	 * @return the instance of the calling object
	 */
	public RealSignal times(float factor) {
		int nxy = nx * ny;
		for (int k = 0; k < nz; k++)
			for (int i = 0; i < nxy; i++) {
				data[k][i] *= factor;
			}
		return this;
	}

	/**
	 * Adds a signal pixelwise (in-place processing)
	 * 
	 * @param factor
	 * @return the instance of the calling object
	 */
	public RealSignal plus(RealSignal factor) {
		int nxy = nx * ny;
		for (int k = 0; k < nz; k++)
			for (int i = 0; i < nxy; i++) {
				data[k][i] += factor.data[k][i];
			}
		return this;
	}

	/**
	 * Subtracts by a signal pixelwise (in-place processing)
	 * 
	 * @param factor
	 * @return the instance of the calling object
	 */
	public RealSignal minus(RealSignal factor) {
		int nxy = nx * ny;
		for (int k = 0; k < nz; k++)
			for (int i = 0; i < nxy; i++) {
				data[k][i] -= factor.data[k][i];
			}
		return this;
	}

	/**
	 * Adds a scalar term (in-place processing)
	 * 
	 * @param term
	 * @return the instance of the calling object
	 */
	public RealSignal plus(float term) {
		int nxy = nx * ny;
		for (int k = 0; k < nz; k++)
			for (int i = 0; i < nxy; i++) {
				data[k][i] += term;
			}
		return this;
	}

	/**
	 * Subtracts a scalar term (in-place processing)
	 * 
	 * @param term
	 * @return the instance of the calling object
	 */
	public RealSignal minus(float term) {
		int nxy = nx * ny;
		for (int k = 0; k < nz; k++)
			for (int i = 0; i < nxy; i++) {
				data[k][i] -= term;
			}
		return this;
	}

	/**
	 * Takes the maximum (in-place processing)
	 * 
	 * @param factor
	 * @return the instance of the calling object
	 */
	public RealSignal max(RealSignal factor) {
		int nxy = nx * ny;
		for (int k = 0; k < nz; k++)
			for (int i = 0; i < nxy; i++) {
				data[k][i] = Math.max(data[k][i], factor.data[k][i]);
			}
		return this;
	}

	/**
	 * Takes the minimum (in-place processing)
	 * 
	 * @param factor
	 * @return the instance of the calling object
	 */
	public RealSignal min(RealSignal factor) {
		int nxy = nx * ny;
		for (int k = 0; k < nz; k++)
			for (int i = 0; i < nxy; i++) {
				data[k][i] = Math.min(data[k][i], factor.data[k][i]);
			}
		return this;
	}

	public double[][][] get3DArrayAsDouble() {
		double[][][] ar = new double[nx][ny][nz];
		for (int k = 0; k < nz; k++) {
			float[] s = data[k];
			for (int i = 0; i < nx; i++)
				for (int j = 0; j < ny; j++) {
					ar[i][j][k] = s[i + j * nx];
				}
		}
		return ar;
	}

	public void set3DArrayAsDouble(double[][][] real) {
		for (int k = 0; k < nz; k++) {
			float[] s = data[k];
			for (int i = 0; i < nx; i++)
				for (int j = 0; j < ny; j++) {
					s[i + j * nx] = (float) real[i][j][k];
				}
		}
	}

	public RealSignal duplicate() {
		RealSignal out = new RealSignal("copy(" + name + ")", nx, ny, nz);
		int nxy = nx * ny;
		for (int k = 0; k < nz; k++)
			System.arraycopy(data[k], 0, out.data[k], 0, nxy);
		return out;
	}

	public float getEnergy() {
		int nxy = nx * ny;
		float energy = 0.f;
		for (int k = 0; k < nz; k++)
			for (int i = 0; i < nxy; i++)
				energy += data[k][i];
		return energy;
	}

	public float[] getStats() {
		double[] stats = getStatsAsDouble();
		float[] fstats = new float[stats.length];
		for(int i=0; i<fstats.length; i++)
			fstats[i] = (float)stats[i];
		return fstats;
	}
	
	public double[] getStatsAsDouble() {
		int nxy = nx * ny;
		float min = Float.MAX_VALUE;
		float max = -Float.MAX_VALUE;
		double norm1 = 0.0;
		double norm2 = 0.0;
		double mean = 0.0;
		
		for (int k = 0; k < nz; k++)
			for (int i = 0; i < nxy; i++) {
				float v = data[k][i];
				max = Math.max(max, v);
				min = Math.min(min, v);
				mean += v;
				norm1 += (v > 0 ? v : -v);
				norm2 += v * v;
			}
		mean = mean / (nz * nxy);
		norm1 = norm1 / (nz * nxy);
		norm2 = Math.sqrt(norm2 / (nz * nxy));
		double stdev = 0.0;
		for (int k = 0; k < nz; k++)
			for (int i = 0; i < nxy; i++) {
				stdev += (data[k][i] - mean) * (data[k][i] - mean);
			}
		stdev = Math.sqrt(stdev / (nz * nxy));
		return new double[] { mean, min, max, stdev, norm1, norm2 };
	}

	public float[] getExtrema() {
		int nxy = nx * ny;
		float min = Float.MAX_VALUE;
		float max = -Float.MAX_VALUE;

		for (int k = 0; k < nz; k++)
			for (int i = 0; i < nxy; i++) {
				float v = data[k][i];
				max = Math.max(max, v);
				min = Math.min(min, v);
			}
		return new float[] { min, max };
	}

	public RealSignal normalize(double to) {
		if (to == 0)
			return this;
		int nxy = nx * ny;
		float sum = 0f;
		for (int k = 0; k < nz; k++)
			for (int i = 0; i < nxy; i++)
				sum += data[k][i];
		if (sum != 0f) {
			double r = to / sum;
			for (int k = 0; k < nz; k++)
				for (int i = 0; i < nxy; i++)
					data[k][i] *= r;
		}
		return this;
	}

	public void setSlice(int z, RealSignal slice) {
		int mx = slice.nx;
		int my = slice.ny;
		for (int j = 0; j < Math.min(ny, my); j++)
			for (int i = 0; i < Math.min(nx, mx); i++)
				data[z][i + nx * j] = slice.data[0][i + mx * j];
	}

	public RealSignal getSlice(int z) {
		RealSignal slice = new RealSignal(name + "_z=" + z, nx, ny, 1);
		for (int j = 0; j < nx * ny; j++)
			slice.data[0][j] = data[z][j];
		return slice;
	}

	public void multiply(double factor) {
		for (int k = 0; k < nz; k++)
			for (int i = 0; i < nx * ny; i++)
				data[k][i] *= factor;
	}

	public float[] getInterleaveXYZAtReal() {
		float[] interleave = new float[2 * nz * nx * ny];
		for (int k = 0; k < nz; k++)
			for (int j = 0; j < ny; j++)
				for (int i = 0; i < nx; i++)
					interleave[2 * (k * nx * ny + j * nx + i)] = data[k][i + j * nx];
		return interleave;
	}

	public void setInterleaveXYZAtReal(float[] interleave) {
		for (int k = 0; k < nz; k++)
			for (int j = 0; j < ny; j++)
				for (int i = 0; i < nx; i++)
					data[k][i + nx * j] = interleave[(k * nx * ny + j * nx + i) * 2];
	}

	public float[] getInterleaveXYAtReal(int k) {
		float real[] = new float[nx * ny * 2];
		for (int i = 0; i < nx; i++)
			for (int j = 0; j < ny; j++) {
				int index = i + j * nx;
				real[2 * index] = data[k][index];
			}
		return real;
	}

	public void setInterleaveXYAtReal(int k, float real[]) {
		for (int i = 0; i < nx; i++)
			for (int j = 0; j < ny; j++) {
				int index = i + j * nx;
				data[k][index] = real[2 * index];
			}
	}

	public float[] getXYZ() {
		int nxy = nx * ny;
		float[] d = new float[nz * nx * ny];
		for (int k = 0; k < nz; k++)
			for (int i = 0; i < nxy; i++)
				d[k * nxy + i] = data[k][i];
		return d;
	}

	public void setXYZ(float[] data) {
		if (nx * ny * nz != data.length)
			return;
		int nxy = nx * ny;
		for (int k = 0; k < nz; k++)
			for (int i = 0; i < nxy; i++)
				this.data[k][i] = data[k * nxy + i];
	}

	public float[] getXY(int k) {
		return data[k];
	}

	public void setXY(int k, float slice[]) {
		data[k] = slice;
	}

	public float[] getX(int j, int k) {
		float line[] = new float[nx];
		for (int i = 0; i < nx; i++)
			line[i] = data[k][i + j * nx];
		return line;
	}

	public float[] getZ(int i, int j) {
		float line[] = new float[nz];
		int index = i + j * nx;
		for (int k = 0; k < nz; k++)
			line[k] = data[k][index];
		return line;
	}

	public float[] getY(int i, int k) {
		float line[] = new float[ny];
		for (int j = 0; j < ny; j++)
			line[j] = data[k][i + j * nx];
		return line;
	}

	public void setX(int j, int k, float line[]) {
		for (int i = 0; i < nx; i++)
			data[k][i + j * nx] = line[i];
	}

	public void setY(int i, int k, float line[]) {
		for (int j = 0; j < ny; j++)
			data[k][i + j * nx] = line[j];
	}

	public void setZ(int i, int j, float line[]) {
		int index = i + j * nx;
		for (int k = 0; k < nz; k++)
			data[k][index] = line[k];
	}

	public void clip(float min, float max) {
		for (int k = 0; k < nz; k++)
			for (int j = 0; j < ny * nx; j++)
				if (data[k][j] < min)
					data[k][j] = min;
				else if (data[k][j] > max)
					data[k][j] = max;
	}

	public void fill(float constant) {
		for (int k = 0; k < nz; k++)
			for (int j = 0; j < ny * nx; j++)
				data[k][j] = constant;
	}

	/**
	 * Get a interpolated pixel value at specific position without specific
	 * boundary conditions.
	 * 
	 * If the positions is not on the pixel grid, the method return a
	 * interpolated value of the pixel (linear interpolation). If the positions
	 * is outside of this signal, the method return 0.0.
	 * 
	 * @param x
	 *            position in the X axis
	 * @param y
	 *            position in the Y axis
	 * @param z
	 *            position in the Z axis
	 * @return an interpolated value
	 */
	public float getInterpolatedPixel(double x, double y, double z) {
		if (x > nx - 1)
			return 0.0f;
		if (y > ny - 1)
			return 0.0f;
		if (z > nz - 1)
			return 0.0f;
		if (x < 0)
			return 0.0f;
		if (y < 0)
			return 0.0f;
		if (z < 0)
			return 0.0f;
		int i = (x >= 0.0 ? ((int) x) : ((int) x - 1));
		int j = (y >= 0.0 ? ((int) y) : ((int) y - 1));
		int k = (z >= 0.0 ? ((int) z) : ((int) z - 1));
		boolean fi = (i == nx - 1);
		boolean fj = (j == ny - 1);
		boolean fk = (k == nz - 1);
		int index = i + j * nx;
		try {
		double v3_000 = data[k][index];
		double v3_100 = fi ? v3_000 : data[k][index + 1];
		double v3_010 = fj ? v3_000 : data[k][index + nx];
		double v3_110 = fi ? (fj ? v3_000 : v3_010) : data[k][index + 1 + nx];
		double v3_001 = fk ? v3_000 : data[k + 1][index];
		double v3_011 = fk ? (fj ? v3_000 : v3_010) : data[k + 1][index + 1];
		double v3_101 = fk ? (fi ? v3_000 : v3_100) : data[k + 1][index + nx];
		double v3_111 = fk ? (fj ? (fi ? v3_000 : v3_100) : v3_110) : data[k + 1][index + 1 + nx];
		double dx3 = x - (double) i;
		double dy3 = y - (double) j;
		double dz3 = z - (double) k;
		double z1 = (dx3 * (v3_110 * dy3 - v3_100 * (dy3 - 1.0)) - (dx3 - 1.0) * (v3_010 * dy3 - v3_000 * (dy3 - 1.0)));
		double z2 = (dx3 * (v3_111 * dy3 - v3_101 * (dy3 - 1.0)) - (dx3 - 1.0) * (v3_011 * dy3 - v3_001 * (dy3 - 1.0)));
		return (float)(z2 * dz3 - z1 * (dz3 - 1.0));
		}
		catch(Exception ex) {
			return 0f;
		}
	}

	public RealSignal changeSizeAs(RealSignal model) {
		return size(model.nx, model.ny, model.nz);
	}

	public RealSignal size(int mx, int my, int mz) {
		String n = "resize(" + name + ")";

		int ox = (mx - nx) / 2;
		int oy = (my - ny) / 2;
		int oz = (mz - nz) / 2;
		RealSignal signal = new RealSignal(n, mx, my, mz);
		int vx = Math.min(nx, mx);
		int vy = Math.min(ny, my);
		int vz = Math.min(nz, mz);
		for (int k = 0; k < vz; k++)
			for (int j = 0; j < vy; j++)
				for (int i = 0; i < vx; i++) {
					int pi = ox >= 0 ? i + ox : i;
					int qi = ox >= 0 ? i : i - ox;
					int pj = oy >= 0 ? j + oy : j;
					int qj = oy >= 0 ? j : j - oy;
					int pk = oz >= 0 ? k + oz : k;
					int qk = oz >= 0 ? k : k - oz;
					signal.data[pk][pi + pj * mx] = data[qk][qi + qj * nx];
				}
		return signal;
	}

	public RealSignal createOrthoview() {
		return createOrthoview(nx / 2, ny / 2, nz / 2);
	}

	public RealSignal createOrthoview(int hx, int hy, int hz) {
		String n = "ortho(" + name + ")";
		if (nz==1) {
			RealSignal a = this.duplicate();
			a.name = n;
			return this.duplicate();
		}

		int vx = nx + nz;
		int vy = ny + nz;
		RealSignal view = new RealSignal(n, vx, vy, 1);
		float ext[] = getExtrema();
		view.fill(ext[0]);

		hx = Math.min(nx - 1, Math.max(0, hx));
		hy = Math.min(ny - 1, Math.max(0, hy));
		hz = Math.min(nz - 1, Math.max(0, hz));
		for (int x = 0; x < nx; x++)
			for (int y = 0; y < ny; y++)
				view.data[0][x + vx * y] = data[hz][x + nx * y];

		for (int z = 0; z < nz; z++)
			for (int y = 0; y < ny; y++)
				view.data[0][nx + z + vx * y] = data[z][hx + nx * y];

		for (int z = 0; z < nz; z++)
			for (int x = 0; x < nx; x++)
				view.data[0][x + vx * (ny + z)] = data[z][x + nx * hy];
		return view;
	}

	public RealSignal createFigure(int hx, int hy, int hz) {
		String n = "figure(" + name + ")";
		if (nz==1) {
			RealSignal a = this.duplicate();
			a.name = n;
			return this.duplicate();
		}
		int vx = nx + nz + 4;
		int vy = ny + 2;
		float max = this.getExtrema()[1];
		RealSignal view = new RealSignal(n, vx, vy, 1);
		for (int i = 0; i < vx * vy; i++)
			view.data[0][i] = max;

		hx = Math.min(nx - 1, Math.max(0, hx));
		hy = Math.min(ny - 1, Math.max(0, hy));
		hz = Math.min(nz - 1, Math.max(0, hz));
		for (int x = 0; x < nx; x++)
			for (int y = 0; y < ny; y++)
				view.data[0][x + 1 + vx * (y + 1)] = data[hz][x + nx * y];

		for (int z = 0; z < nz; z++)
			for (int y = 0; y < ny; y++)
				view.data[0][nx + 3 + z + vx * (y + 1)] = data[z][hx + nx * y];

		return view;
	}

	public RealSignal createMIP() {
		String n = "mip(" + name + ")";
		if (nz==1) {
			RealSignal a = this.duplicate();
			a.name = n;
			return this.duplicate();
		}
		int vx = nx + nz;
		int vy = ny + nz;
		RealSignal view = new RealSignal(n, vx, vy, 1);
		float ext[] = getExtrema();
		view.fill(ext[0]);
		
		for (int x = 0; x < nx; x++)
			for (int y = 0; y < ny; y++)
				for (int k = 0; k < nz; k++) {
					int index = x + vx * y;
					view.data[0][index] = Math.max(view.data[0][index], data[k][x + nx * y]);
				}

		for (int z = 0; z < nz; z++)
			for (int y = 0; y < ny; y++)
				for (int x = 0; x < nx; x++) {
					int index = nx + z + vx * y;
					view.data[0][index] = Math.max(view.data[0][index], data[z][x + nx * y]);
				}

		for (int z = 0; z < nz; z++)
			for (int x = 0; x < nx; x++)
				for (int y = 0; y < ny; y++) {
					int index = x + vx * (ny + z);
					view.data[0][index] = Math.max(view.data[0][index], data[z][x + nx * y]);
				}
		return view;
	}

	public RealSignal createPlanar() {
		String n = "planar(" + name + ")";
		if (nz==1) {
			RealSignal a = this.duplicate();
			a.name = n;
			return this.duplicate();
		}
		int nr = (int) Math.sqrt(nz);
		int nc = (int) Math.ceil(nz / nr);
		if (nc*nr < nz)
			nc++;
		int w = nx * nr;
		int h = ny * nc;
		RealSignal view = new RealSignal(n, w, h, 1);
		float ext[] = getExtrema();
		view.fill(ext[0]);

		for (int k = 0; k < nz; k++) {
			int col = k % nr;
			int row = k / nr;
			int offx = col * nx;
			int offy = row * ny;
			for (int x = 0; x < nx; x++)
				for (int y = 0; y < ny; y++)
					view.data[0][x + offx + w * (y + offy)] = data[k][x + nx * y];
		}
		return view;
	}

	
	public RealSignal circular() {
		for (int i = 0; i < nx; i++)
			for (int j = 0; j < ny; j++)
				setZ(i, j, rotate(getZ(i, j)));
		for (int i = 0; i < nx; i++)
			for (int k = 0; k < nz; k++)
				setY(i, k, rotate(getY(i, k)));
		for (int j = 0; j < ny; j++)
			for (int k = 0; k < nz; k++)
				setX(j, k, rotate(getX(j, k)));
		return this;
	}

	public RealSignal rescale(Monitors monitors) {
		new RescaledRange(monitors, 0, 255).apply(this);
		return this;
	}

	public float[] rotate(float[] buffer) {
		int len = buffer.length;
		if (len <= 1)
			return buffer;
		int count = 0;
		int offset = 0;
		int start = len / 2;
		while (count < len) {
			int index = offset;
			float tmp = buffer[index];
			int index2 = (start + index) % len;
			while (index2 != offset) {
				buffer[index] = buffer[index2];
				count++;
				index = index2;
				index2 = (start + index) % len;
			}
			buffer[index] = tmp;
			count++;
			offset++;
		}
		return buffer;
	}

	@Override
	public String toString() {
		return "Real Signal [" + nx + ", " + ny + ", " + nz + "]";
	}
	
	public BufferedImage preview() {
		if (preview != null)
			return preview;
		int nxy = nx*ny;
		float[] pixels = new float[nx*ny];
		for (int i = 0; i < nxy; i++) 
			for (int k = 0; k < nz; k++) {
				pixels[i] = Math.max(pixels[i], data[k][i]);
		}
		float max = -Float.MAX_VALUE;
		float min = Float.MAX_VALUE;
		for (int i = 0; i < nxy; i++) {
			if (pixels[i] > max)
				max = pixels[i];
			if (pixels[i] < min)
				min = pixels[i];
		}
		float a = 255f / Math.max(max-min, (float)Operations.epsilon);
		preview = new BufferedImage(nx, ny, BufferedImage.TYPE_INT_ARGB);
		int alpha = (255 << 24);
		for (int i = 0; i < nx; i++)
		for (int j = 0; j < ny; j++) {
			int v = (int)(a*(pixels[i+j*nx] - min));
			preview.setRGB(i, j, alpha | (v << 16) | (v << 8) | v);
		}
		return preview;
	}

	public RealSignal log10() {
		int nxy = nx * ny;
		for (int k = 0; k < nz; k++)
		for (int i = 0; i < nxy; i++) {
			data[k][i] = (float)Math.log10(data[k][i]);
		}
		return this;
	}

	public RealSignal log() {
		int nxy = nx * ny;
		for (int k = 0; k < nz; k++)
		for (int i = 0; i < nxy; i++) {
			data[k][i] = (float)Math.log(data[k][i]);
		}
		return this;
	}
	
	public RealSignal exp() {
		int nxy = nx * ny;
		for (int k = 0; k < nz; k++)
		for (int i = 0; i < nxy; i++) {
			data[k][i] = (float)Math.exp(data[k][i]);
		}
		return this;
	}
	
	public RealSignal abs() {
		int nxy = nx * ny;
		for (int k = 0; k < nz; k++)
		for (int i = 0; i < nxy; i++) {
			data[k][i] = (float)Math.abs(data[k][i]);
		}
		return this;
	}
	
	public RealSignal sqrt() {
		int nxy = nx * ny;
		for (int k = 0; k < nz; k++)
		for (int i = 0; i < nxy; i++) {
			data[k][i] = (float)Math.sqrt(data[k][i]);
		}
		return this;
	}
	
	public RealSignal sqr() {
		int nxy = nx * ny;
		for (int k = 0; k < nz; k++)
		for (int i = 0; i < nxy; i++) {
			data[k][i] = (float)(data[k][i]*data[k][i]);
		}
		return this;
	}

	public RealSignal rescale(double min, double max) {
		int nxy = nx * ny;
		float minf = (float)min;
		float stats[] = getStats();
		float a = ((float)max-minf) / (stats[2] - stats[1]);
		for(int k=0; k<nz; k++)
		for(int i=0; i<nxy; i++) {
			data[k][i] = a*(data[k][i] - stats[1]) + minf;
		}
		return this;
	}
}
