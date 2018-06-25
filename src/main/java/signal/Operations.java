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

public class Operations {
	
	public static double epsilon	= 1e-6;

	public static RealSignal log(RealSignal s) {
		String name = "log(" + s.name + ")";
		int nx = s.nx;
		int ny = s.ny;
		int nz = s.nz;
		int nxy = nx * ny;
		RealSignal log = new RealSignal(name, nx, ny, nz);
		for(int k=0; k<nz; k++)
		for(int i=0; i< nxy; i++) {
			log.data[k][i] = (float) Math.log(s.data[k][i]);
		}
		return log;
	}

	public static void divide(RealSignal numerator, RealSignal denominator, RealSignal output) { 
		String name = numerator.name + "/" + denominator.name;
		if (output == null)
			 output = new RealSignal(name, numerator.nx, numerator.ny, numerator.nz);
		
		int nxy = numerator.nx * numerator.ny;
		for(int k=0; k<numerator.nz; k++)
		for(int i=0; i< nxy; i++) {
			if (denominator.data[k][i] < epsilon)
				output.data[k][i] = 0.0f;
			else
				output.data[k][i] = numerator.data[k][i] / denominator.data[k][i];
		}
	}

	public static RealSignal divide(RealSignal numerator, RealSignal denominator) {
		String name = numerator.name + "/" + denominator.name;
		RealSignal output = new RealSignal(name, numerator.nx, numerator.ny, numerator.nz);
		divide(numerator, denominator, output);
		return output;
	}

	public static ComplexSignal conjugate(ComplexSignal s) {
		String name = "conj(" + s.name + ")";
		int nx = s.nx;
		int ny = s.ny;
		int nz = s.nz;
		int nxy = nx * ny * 2;
		ComplexSignal result = new ComplexSignal(name, nx, ny, nz);
		for(int k=0; k<nz; k++)
		for(int i=0; i< nxy; i+=2) {
			result.data[k][i] = s.data[k][i];
			result.data[k][i+1] = -s.data[k][i+1];
		}
		return result;
	}

	public static void multiply(ComplexSignal a, ComplexSignal b, ComplexSignal output) {
		String name = a.name + "*" + b.name;
		if (output == null)
			 output = new ComplexSignal(name, a.nx, a.ny, a.nz);
		int nx = a.nx;
		int ny = a.ny;
		int nz = a.nz;
		double ar, ai, br, bi;
		int nxy = nx * ny * 2;
		for(int k=0; k<nz; k++)
		for(int i=0; i< nxy; i+=2) {
			ar = a.data[k][i];
			ai = a.data[k][i+1];
			br = b.data[k][i];
			bi = b.data[k][i+1];
			output.data[k][i] = (float)(ar*br - ai*bi);
			output.data[k][i+1] = (float)(ar*bi + br*ai);
		}
	}

	public static ComplexSignal multiply(ComplexSignal a, ComplexSignal b) {
		String name = a.name + "*" + b.name;
		ComplexSignal output = new ComplexSignal(name, a.nx, a.ny, a.nz);
		multiply(a, b, output);
		return output;
	}

	public static ComplexSignal multiplyConjugate(ComplexSignal aConjugate, ComplexSignal b) {
		String name = aConjugate.name + "* *" + b.name;
		ComplexSignal output = new ComplexSignal(name, b.nx, b.ny, b.nz);
		multiplyConjugate(aConjugate, b, output);
		return output;
	}

	public static void multiplyConjugate(ComplexSignal aConjugate, ComplexSignal b, ComplexSignal output) {
		String name = aConjugate.name + "* *" + b.name;
		if (output == null)
			 output = new ComplexSignal(name, b.nx, b.ny, b.nz);
		int nx = b.nx;
		int ny = b.ny;
		int nz = b.nz;
		double a1, a2, b1, b2;
		int nxy = nx * ny * 2;
		for(int k=0; k<nz; k++)
		for(int i=0; i< nxy; i+=2) {
			a1 = aConjugate.data[k][i];
			b1 = -aConjugate.data[k][i+1];
			a2 = b.data[k][i];
			b2 = b.data[k][i+1];
			output.data[k][i] = (float)(a1*a2 - b1*b2);
			output.data[k][i+1] = (float)(a1*b2 + a2*b1);
		}
	}
	
	public static ComplexSignal multiply(double w, ComplexSignal a, ComplexSignal b) {
		String name = a.name + "* w *" + b.name;
		int nx = a.nx;
		int ny = a.ny;
		int nz = a.nz;
		ComplexSignal result = new ComplexSignal(name, nx, ny, nz);
		double a1, a2, b1, b2;
		int nxy = nx * ny * 2;
		for(int k=0; k<nz; k++)
		for(int i=0; i< nxy; i+=2) {
			a1 = a.data[k][i];
			b1 = a.data[k][i+1];
			a2 = b.data[k][i];
			b2 = b.data[k][i+1];
			result.data[k][i] = (float)(w*(a1*a2 - b1*b2));
			result.data[k][i+1] = (float)(w*(a1*b2 + a2*b1));
		}
		return result;
	}

	public static ComplexSignal multiplyConjugate(double w, ComplexSignal aConjugate, ComplexSignal b) {
		String name = aConjugate.name + "* * w *" + b.name;
		int nx = b.nx;
		int ny = b.ny;
		int nz = b.nz;
		ComplexSignal result = new ComplexSignal(name, nx, ny, nz);
		double a1, a2, b1, b2;
		int nxy = nx * ny * 2;
		for(int k=0; k<nz; k++)
		for(int i=0; i< nxy; i+=2) {
			a1 = aConjugate.data[k][i];
			b1 = -aConjugate.data[k][i+1];
			a2 = b.data[k][i];
			b2 = b.data[k][i+1];
			result.data[k][i] = (float)(w*(a1*a2 - b1*b2));
			result.data[k][i+1] = (float)(w*(a1*b2 + a2*b1));
		}
		return result;
	}

	public static ComplexSignal divideStabilized(ComplexSignal numerator, ComplexSignal denominator) {
		String name = numerator.name + " / " + denominator.name;
		int nx = numerator.nx;
		int ny = numerator.ny;
		int nz = numerator.nz;
		ComplexSignal result = new ComplexSignal(name, nx, ny, nz);
		double a1, a2, b1, b2, mag;
		int nxy = nx * ny * 2;
		for(int k=0; k<nz; k++)
		for(int i=0; i< nxy; i+=2) {
			a1 = numerator.data[k][i];
			b1 = numerator.data[k][i+1];
			a2 = denominator.data[k][i];
			b2 = denominator.data[k][i+1];
			mag = Math.max(epsilon, a2*a2 + b2*b2);
			result.data[k][i] = (float)((a1*a2 + b1*b2) / mag);
			result.data[k][i+1] = (float)((b1*a2 - a1*b2) / mag);
		}
		return result;
	}
	
	public static ComplexSignal divideNotStabilized(ComplexSignal numerator, ComplexSignal denominator) {
		String name = numerator.name + " /0/ " + denominator.name;
		int nx = numerator.nx;
		int ny = numerator.ny;
		int nz = numerator.nz;
		ComplexSignal result = new ComplexSignal(name, nx, ny, nz);
		double a1, a2, b1, b2;
		int nxy = nx * ny * 2;
		for(int k=0; k<nz; k++)
		for(int i=0; i< nxy; i+=2) {
			a1 = numerator.data[k][i];
			b1 = numerator.data[k][i+1];
			a2 = denominator.data[k][i];
			b2 = denominator.data[k][i+1];
			double mag = a2*a2 + b2*b2;
			result.data[k][i] = (float)((a1*a2 + b1*b2) / mag);
			result.data[k][i+1] = (float)((b1*a2 - a1*b2) / mag);
		}
		return result;
	}

	public static ComplexSignal add(ComplexSignal s1, ComplexSignal s2) {
		String name = s1.name + " + " + s2.name;
		int nx = s1.nx;
		int ny = s1.ny;
		int nz = s1.nz;
		int nxy = nx * ny * 2;
		ComplexSignal result = new ComplexSignal(name, nx, ny, nz);
		for(int k=0; k<nz; k++)
		for(int i=0; i<nxy; i++)
			result.data[k][i] = s1.data[k][i] + s2.data[k][i];
		return result;
	}

	public static ComplexSignal subtract(ComplexSignal s1, ComplexSignal s2) {
		String name = s1.name + " - " + s2.name;
		int nx = s1.nx;
		int ny = s1.ny;
		int nz = s1.nz;
		int nxy = nx * ny * 2;
		ComplexSignal result = new ComplexSignal(name, nx, ny, nz);
		for(int k=0; k<nz; k++)
		for(int i=0; i<nxy; i++)
			result.data[k][i] = s1.data[k][i] - s2.data[k][i];
		return result;
	}

	public static void subtract(RealSignal s1, RealSignal s2, RealSignal output) {
		String name = s1.name + " - " + s2.name;
		if (output == null)
			output = new RealSignal(name, s1.nx, s1.ny, s1.nz);
		int nxy = s1.nx * s1.ny;
		for(int k=0; k<s1.nz; k++)
		for(int i=0; i<nxy; i++)
			output.data[k][i] = s1.data[k][i] - s2.data[k][i];
	}
	
	public static RealSignal subtract(RealSignal s1, RealSignal s2) {
		String name = s1.name + " - " + s2.name;
		RealSignal output = new RealSignal(name, s1.nx, s1.ny, s1.nz);
		subtract(s1, s2, output);
		return output;
	}

	public static ComplexSignal computeHtH(double w, ComplexSignal h) {
		String name = " w * HtH( " + h.name + ")";
		int nx = h.nx;
		int ny = h.ny;
		int nz = h.nz;
		ComplexSignal result = new ComplexSignal(name, nx, ny, nz);
		double a, b;
		int nxy = nx * ny * 2;
		for(int k=0; k<nz; k++)
		for(int i=0; i< nxy; i+=2) {
			a = h.data[k][i];
			b = h.data[k][i+1];
			result.data[k][i] = (float)(w*(a*a + b*b));
			//result.data[k][i+1] = 0f;
		}
		return result;
	}

	// I - gamma * Ht * H
	public static ComplexSignal delta(double w, ComplexSignal h) {
		String name = " w * Delta2( " + h.name + ")";
		int nx = h.nx;
		int ny = h.ny;
		int nz = h.nz;
		ComplexSignal result = new ComplexSignal(name, nx, ny, nz);
		double a, b;
		int nxy = nx * ny * 2;
		for(int k=0; k<nz; k++)
		for(int i=0; i< nxy; i+=2) {
			a = h.data[k][i];
			b = h.data[k][i+1];
			result.data[k][i] = (float)(1.0 - w*(a*a + b*b));
			//result.data[k][i+1] = 0f;
		}
		return result;
	}

	// I - gamma * H
	public static ComplexSignal delta1(double w, ComplexSignal h) {
		String name = " w * Delta1( " + h.name + ")";
		int nx = h.nx;
		int ny = h.ny;
		int nz = h.nz;
		ComplexSignal result = new ComplexSignal(name, nx, ny, nz);
		double a, b;
		int nxy = nx * ny * 2;
		for(int k=0; k<nz; k++)
		for(int i=0; i< nxy; i+=2) {
			a = h.data[k][i];
			b = h.data[k][i+1];
			result.data[k][i] = (float)(1.0 - w*a);
			result.data[k][i+1] = (float)(- w*b);
		}
		return result;
	}

	public static ComplexSignal multiply(double factor, ComplexSignal s) {
		String name = " w * ( " + s.name + ")";
		int nx = s.nx;
		int ny = s.ny;
		int nz = s.nz;
		int nxy = nx * ny * 2;
		ComplexSignal result = new ComplexSignal(name, nx, ny, nz);
		for(int k=0; k<nz; k++)
		for(int i=0; i<nxy; i++)
			result.data[k][i] = (float)(factor * s.data[k][i]);
		return result;
	}
	
	public static RealSignal circularShift(RealSignal signal) {
		RealSignal out = signal.duplicate();
		out.circular();
		return out;
	}
}
