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

public class ComplexSignal extends Signal implements SignalListener {

	public ComplexSignal(String name, int nx, int ny, int nz) {
		super(name, nx, ny, nz);
		int step = Math.max(1, nz/SignalCollector.NOTIFICATION_RATE);
		this.data = new float[nz][];
		notify(name, 0);
		for(int k=0; k<nz; k++) {
			data[k] = new float[nx * ny * 2];
			if (k % step == 0)
				notify(name, k*100.0/nz);
		}
		notify(name, 100);
		SignalCollector.alloc(this);//name, nx, ny, ny, true);
	}

	@Override
	public void notify(String name, double progress) {
		SignalCollector.setProgress(progress);
	}

	public void set(RealSignal real) {
		for(int k=0; k<nz; k++) {
			float[] s = data[k];
			float[] r = real.getXY(k);
			for(int i=0; i<nx*ny; i++) {
				s[2*i] = r[i];
			}
		}
	}

	public void set(RealSignal real, RealSignal imag) {
		for(int k=0; k<nz; k++) {
			float[] s = data[k];
			float[] re = real.getXY(k);
			float[] im = imag.getXY(k);
			for(int i=0; i<nx*ny; i++) {
				s[2*i] = re[i];
				s[2*i+1] = im[i];
			}
		}
	}

	public void divide(ComplexSignal denominator) {
		float a1, a2, b1, b2, mag;
		float epsilon2 = (float)(Operations.epsilon*Operations.epsilon);
		int nxy = nx * ny * 2;

		for(int k=0; k<nz; k++)
		for(int i=0; i< nxy; i+=2) {
			a1 = data[k][i];
			b1 = data[k][i+1];
			a2 = denominator.data[k][i];
			b2 = denominator.data[k][i+1];
			mag = a2*a2 + b2*b2;
			if (mag >= epsilon2) {
				data[k][i] = (a1*a2 + b1*b2) / mag;
				data[k][i+1] = (b1*a2 - a1*b2) / mag;
			}
			else {
				data[k][i] = (a1*a2 + b1*b2) / epsilon2;
				data[k][i+1] = (b1*a2 - a1*b2) / epsilon2;
			}
		}
	}

	public double[][][] getReal3DArrayAsDouble() {
		double[][][] ar = new double[nx][ny][nz];
		for(int k=0; k<nz; k++) {
			float[] s = data[k];
			for(int i=0; i<nx; i++)
			for(int j=0; j<ny; j++) {
				ar[i][j][k] = s[2*(i+j*nx)];
			}
		}
		return ar;
	}

	public double[][][] getImag3DArrayAsDouble() {
		double[][][] ar = new double[nx][ny][nz];
		for(int k=0; k<nz; k++) {
			float[] s = data[k];
			for(int i=0; i<nx; i++)
			for(int j=0; j<ny; j++) {
				ar[i][j][k] = s[2*(i+j*nx)+1];
			}
		}
		return ar;
	}

	public void set3DArrayAsDouble(double[][][] real, double imag[][][]) {
		for(int k=0; k<nz; k++) {
			float[] s = data[k];
			for(int i=0; i<nx; i++)
			for(int j=0; j<ny; j++) {
				s[2*(i+j*nx)] = (float)real[i][j][k];
				s[2*(i+j*nx)+1] = (float)imag[i][j][k];
			}
		}
	}
	
	public ComplexSignal times(float factor) {
		int nxy = nx * ny * 2;
		for (int k = 0; k < nz; k++)
		for (int i = 0; i < nxy; i++)
			data[k][i] *= factor;
		return this;
	}
	
	public ComplexSignal plus(float real, float imag) {
		int nxy = nx * ny * 2;
		for (int k = 0; k < nz; k++)
		for (int i = 0; i < nxy; i+=2) {
			data[k][i] += real;	
			data[k][i+1] += imag;
		}
		return this;
	}

	public ComplexSignal times(ComplexSignal factor) {
		float a1, a2, b1, b2;
		int nxy = nx * ny * 2;
		for(int k=0; k<nz; k++)
		for(int i=0; i< nxy; i+=2) {
			a1 = data[k][i];
			b1 = data[k][i+1];
			a2 = factor.data[k][i];
			b2 = factor.data[k][i+1];
			data[k][i] = a1*a2 - b1*b2;
			data[k][i+1] = a1*b2 + a2*b1;
		}
		return this;
	}
	
	// this <- Ht * this
	public void timesConjugate(ComplexSignal H) {
		float a1, a2, b1, b2;
		int nxy = nx * ny * 2;
		for(int k=0; k<nz; k++)
		for(int i=0; i< nxy; i+=2) {
			a1 = data[k][i];
			b1 = data[k][i+1];
			a2 = H.data[k][i];
			b2 = -H.data[k][i+1];
			data[k][i] = a1*a2 - b1*b2;
			data[k][i+1] = a1*b2 + a2*b1;
		}
	}

	public void plus(ComplexSignal term) {
		int nxy = nx * ny * 2;
		for(int k=0; k<nz; k++)
		for(int i=0; i< nxy; i+=2) {
			data[k][i] += term.data[k][i];
			data[k][i+1] += term.data[k][i+1];
		}
	}

	public void minus(ComplexSignal term) {
		int nxy = nx * ny * 2;
		for(int k=0; k<nz; k++)
		for(int i=0; i< nxy; i+=2) {
			data[k][i] -= term.data[k][i];
			data[k][i+1] -= term.data[k][i+1];
		}
	}

	public void conjugate() {
		int nxy = nx * ny * 2;
		for(int k=0; k<nz; k++)
		for(int i=0; i< nxy; i+=2)
			data[k][i+1] = -data[k][i+1];		
	}

	public float[] getValue(int i, int j, int k) {
		return new float[] {data[k][2*(j*nx+i)], data[k][2*(j*nx+i)+1]};
	}
	
	public void setValue(int i, int j, int k, float[] value) {
		data[k][2*(j*nx+i)] = value[0];
		data[k][2*(j*nx+i)+1] = value[1];
	}

	public float[] getInterleaveXYZ() {
		int nxy = nx*ny*2;
		float[] interleave = new float[nz*nxy];
		for (int k = 0; k < nz; k++)
		for (int i = 0; i < nx; i++)
		for (int j = 0; j < ny; j++) {
			int index = 2*(k*nx*ny + j*nx + i);
			interleave[index] = data[k][(i+j*nx)*2];
			interleave[index + 1] = data[k][(i+j*nx)*2+1];
		}
		return interleave;
	}
	
	public void setInterleaveXYZ(float[] interleave) {
		int nxy = nx * ny * 2;
		this.data = new float[nz][nxy];		
		for (int i = 0; i < nx; i++)	
		for (int j = 0; j < ny; j++) 
		for (int k = 0; k < nz; k++) {
			int index = 2*(k*nx*ny + j*nx + i);
			data[k][(i+j*nx)*2] = interleave[index];
			data[k][(i+j*nx)*2 + 1] = interleave[index+1];
		}
	}

	public RealSignal getRealSignal() {
		String n = "real(" + name + ")";
		RealSignal real = new RealSignal(n, nx, ny, nz);
		int nxy = nx * ny;
		for (int k = 0; k < nz; k++) {
			float[] r = real.getXY(k);
			for (int i = 0; i < nxy; i++)
				r[i] = data[k][2*i];
		}
		return real;
	}

	public RealSignal getImagSignal() {
		String n = "imag(" + name + ")";
		RealSignal real = new RealSignal(n, nx, ny, nz);
		int nxy = nx * ny;
		for (int k = 0; k < nz; k++) {
			float[] r = real.getXY(k);
			for (int i = 0; i < nxy; i++)
				r[i] = data[k][2*i+1];
		}
		return real;
	}
	
	
	public RealSignal getModule() {
		String n = "mod(" + name + ")";
		RealSignal module = new RealSignal(n, nx, ny, nz);
		for (int k = 0; k < nz; k++)
			module.setXY(k, getModuleXY(k));
		return module;
	}
	
	public float[] getModuleXY(int k) {
		float[] m = new float[nx*ny];
		for (int i = 0; i < nx * ny; i++)
			m[i] = data[k][2*i]*data[k][2*i] + data[k][2*i+1]*data[k][2*i+1];
		return m;
	}

	public float[] getModuleXY_dB(int k) {
		float[] m = new float[nx*ny];
		for (int i = 0; i < nx * ny; i++)
			m[i] = (float)(10f*Math.log10(data[k][2*i]*data[k][2*i] + data[k][2*i+1]*data[k][2*i+1]));
		return m;
	}

	public ComplexSignal duplicate() {
		ComplexSignal out = new ComplexSignal("copy(" + name + ")", nx, ny, nz);
		int nxy = nx * ny * 2;
		for (int k = 0; k < nz; k++)
			System.arraycopy(data[k], 0, out.data[k], 0, nxy);
		return out;
	}

	public ComplexSignal replicate() {
		return new ComplexSignal(">" + name, nx, ny, nz);
	}

	public float getEnergy() {
		int nxy = nx * ny;
		float energy = 0.f;
		for (int k = 0; k < nz; k++)
			for (int i = 0; i < nxy; i++)
				energy += modulus(k, i);
		return energy;
	}

	private float modulus(int k, int index) {
		int i = index * 2;
		return (float)Math.sqrt(data[k][i] * data[k][i] + data[k][i + 1] * data[k][i + 1]);
	}

	public float[] getRealXYZ() {
		float[] real = new float[nx*ny*nz];
		int nxy = nx * ny;
		for (int i = 0; i < nx; i++)	
		for (int j = 0; j < ny; j++) {
			int index = i + nx*j;
			for (int k = 0; k < nz; k++)
				real[k*nxy + index] = data[k][2*index];
		}
		return real;
	}

	public float[] getImagXYZ() {
		float[] imag = new float[nx*ny*nz];
		int nxy = nx * ny;
		for (int i = 0; i < nx; i++)	
		for (int j = 0; j < ny; j++) {
			int index = i + nx*j;
			for (int k = 0; k < nz; k++)
				imag[k*nxy + index] = data[k][2*index+1];
		}
		return imag;
	}

	public void setXYZ(float[] real, float[] imag) {
		int nxy = nx * ny;
		for (int i = 0; i < nx; i++)	
		for (int j = 0; j < ny; j++) {
			int index = i + nx*j;
			for (int k = 0; k < nz; k++) {
				data[k][2*index] = real[k*nxy + index];
				data[k][2*index+1] = imag[k*nxy + index];
			}
		}
	}

	public float[] getInterleaveZ(int i, int j) {
		float line[] = new float[nz*2];
		int index = 2 * (i + j * nx);
		for (int k = 0; k < nz; k++) {
			line[2*k] = data[k][index];
			line[2*k+1] = data[k][index+1];
		}
		return line;
	}

	public float[] getRealZ(int i, int j) {
		float line[] = new float[nz];
		int index = 2 * (i + j * nx);
		for (int k = 0; k < nz; k++)
			line[k] = data[k][index];
		return line;
	}
	
	public float[] getImagZ(int i, int j) {
		float line[] = new float[nz];
		int index = 2 * (i + j * nx) + 1;
		for (int k = 0; k < nz; k++)
			line[k] = data[k][index];
		return line;
	}

	public float[] getRealY(int i, int k) {
		float line[] = new float[ny];
		for (int j = 0; j < ny; j++)
			line[j] = data[k][2 * (i + j * nx)];
		return line;
	}
	
	public float[] getImagY(int i, int k) {
		float line[] = new float[ny];
		for (int j = 0; j < ny; j++)
			line[j] = data[k][2 * (i + j * nx) + 1];
		return line;
	}

	public float[] getRealX(int j, int k) {
		float line[] = new float[nx];
		for (int i = 0; i < nx; i++)
			line[i] = data[k][2 * (i + j * nx)];
		return line;
	}
	
	public float[] getImagX(int j, int k) {
		float line[] = new float[nx];
		for (int i = 0; i < nx; i++)
			line[i] = data[k][2 * (i + j * nx) + 1];
		return line;
	}

	public float[] getInterleaveXY(int k) {
		float slice[] = new float[nx*ny*2];
		for (int i = 0; i < nx; i++)
		for (int j = 0; j < ny; j++) {
			int index = 2*(i + j * nx);
			slice[index] = data[k][index];
			slice[index + 1] = data[k][index + 1];
		}
		return slice;
	}

	public float[] getInterleaveYX(int k) {
		float slice[] = new float[nx*ny*2];
		for (int i = 0; i < nx; i++)
		for (int j = 0; j < ny; j++) {
			int index = 2*(j + i * ny);
			slice[index] = data[k][index];
			slice[index + 1] = data[k][index + 1];
		}
		return slice;
	}

	public float[] getRealXY(int k) {
		float slice[] = new float[nx*ny];
		for (int i = 0; i < nx; i++)
		for (int j = 0; j < ny; j++) {
			int index = i + j * nx;
			slice[index] = data[k][2*index];
		}
		return slice;
	}
	
	public float[] getImagXY(int k) {
		float slice[] = new float[nx*ny];
		for (int i = 0; i < nx; i++)
		for (int j = 0; j < ny; j++)
			slice[i + j * nx] = data[k][2 * (i + j * nx)+1];
		return slice;
	}
	
	
	public void setInterleaveZ(int i, int j, float line[]) {
		int index = 2 * (i + j * nx);
		for (int k = 0; k < nz; k++) {
			data[k][index] = line[2*k];
			data[k][index+1] = line[2*k+1];
		}
	}

	public void setRealZ(int i, int j, float line[]) {
		int index = 2 * (i + j * nx);
		for (int k = 0; k < nz; k++)
			data[k][index] = line[k];
	}
	
	public void setImagZ(int i, int j, float line[]) {
		int index = 2 * (i + j * nx) + 1;
		for (int k = 0; k < nz; k++)
			data[k][index] = line[k];
	}

	public void setRealX(int j, int k, float line[]) {
		for (int i = 0; i < nx; i++)
			data[k][2 * (i + j * nx)] = line[i];
	}
	
	public void setImagX(int j, int k, float line[]) {
		for (int i = 0; i < nx; i++)
			data[k][2 * (i + j * nx) + 1] = line[i];
	}

	public void setRealY(int i, int k, float line[]) {
		for (int j = 0; j < ny; j++)
			data[k][2 * (i + j * nx)] = line[j];
	}
	
	public void setImagY(int i, int k, float line[]) {
		for (int j = 0; j < ny; j++)
			data[k][2 * (i + j * nx) + 1] = line[j];
	}

	public void setInterleaveXY(int k, float slice[]) {
		for (int i = 0; i < nx; i++)
		for (int j = 0; j < ny; j++) {
			data[k][2 * (i + j * nx)] = slice[2*(i + j * nx)];
			data[k][2 * (i + j * nx) + 1] = slice[2*(i + j * nx) + 1];
		}
	}

	public void setInterleaveYX(int k, float slice[]) {
		for (int i = 0; i < nx; i++)
		for (int j = 0; j < ny; j++) {
			data[k][2 * (i + j * nx)] = slice[2*(j + i * ny)];
			data[k][2 * (i + j * nx) + 1] = slice[2*(j + i * ny) + 1];
		}
	}

	public void setRealXY(int k, float slice[]) {
		for (int i = 0; i < nx; i++)
		for (int j = 0; j < ny; j++)
			data[k][2 * (i + j * nx)] = slice[i + j * nx];
	}
	
	public void setImagXY(int k, float slice[]) {
		for (int i = 0; i < nx; i++)
		for (int j = 0; j < ny; j++)
			data[k][2 * (i + j * nx)+1] = slice[i + j * nx];
	}

	public float[] getRealY(int i, int k, int imag) {
		float line[] = new float[ny];
		int off = 2 * i + imag;
		for (int j = 0; j < ny; j++)
			line[j] = data[k][2 * j * nx + off];
		return line;
	}
	
	public void swap() {
		int hx = nx / 2;
		int hy = ny / 2;
		int hz = nz / 2;
		if (nx > 1) {
			float rx[] = new float[nx];
			float ix[] = new float[nx];
			for (int j = 0; j < ny; j++)
				for (int k = 0; k < nz; k++) {
					float real[] = getRealX(j, k);
					float imag[] = getImagX(j, k);
					for (int i = 0; i < hx; i++) {
						rx[i] = real[i + hx];
						rx[i + hx] = real[i];
						ix[i] = imag[i + hx];
						ix[i + hx] = imag[i];
					}
					setRealX(j, k, rx);
					setImagX(j, k, ix);
				}
		}
		
		if (ny > 1) {
			float ry[] = new float[ny];
			float iy[] = new float[ny];
			for (int i = 0; i < nx; i++)
				for (int k = 0; k < nz; k++) {
					float real[] = getRealY(i, k);
					float imag[] = getImagY(i, k);
					for (int j = 0; j < hy; j++) {
						ry[j] = real[j + hy];
						ry[j + hy] = real[j];
						iy[j] = imag[j + hy];
						iy[j + hy] = imag[j];
					}
					setRealY(i, k, ry);
					setImagY(i, k, iy);
				}
		}
		if (nz > 1) {
			float rz[] = new float[nz];
			float iz[] = new float[nz];
			for (int i = 0; i < nx; i++)
				for (int j = 0; j < ny; j++) {
					float real[] = getRealZ(i, j);
					float imag[] = getImagZ(i, j);
					for (int k = 0; k < hz; k++) {
						rz[k] = real[k + hz];
						rz[k + hz] = real[k];
						iz[k] = imag[k + hz];
						iz[k + hz] = imag[k];
					}
					setRealZ(i, j, rz);
					setImagZ(i, j, iz);
				}
		}
	}

	public void circularShift() {
		for(int i=0; i<nx; i++)
		for(int j=0; j<ny; j++) {
			setImagZ(i, j, rotate(getImagZ(i, j)));
			setRealZ(i, j, rotate(getRealZ(i, j)));
		}
		for(int i=0; i<nx; i++)
		for(int k=0; k<nz; k++) {
			setImagY(i, k, rotate(getImagY(i, k)));
			setRealY(i, k, rotate(getRealY(i, k)));
		}
		for(int j=0; j<ny; j++)
		for(int k=0; k<nz; k++) {
			setImagX(j, k, rotate(getImagX(j, k)));
			setRealX(j, k, rotate(getRealX(j, k)));
		}
	}
	
	private float[] rotate(float[] buffer) {
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
		return "Complex Signal [" + nx + ", " + ny + ", " + nz + "]";
	}
}