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

import deconvolutionlab.monitor.Monitors;

public class Constraint {
	
	public static enum Mode {NO, NONNEGATIVE, CLIPPED, RESCALED, NORMALIZED};
	
	private static float min = -Float.MAX_VALUE;
	private static float max = Float.MAX_VALUE;
	private static float mean;
	private static float stdev;
	
	private Monitors monitors = new Monitors();
	
	public Constraint(Monitors monitors) {
		this.monitors = monitors;
	}
	
	public static String[] getContraintsAsArray() {
		return new String[] {"no", "nonnegativity", "clipped"};
	}
	
	public static Constraint.Mode getByName(String c) {
		if (c.toLowerCase().equalsIgnoreCase("nonnegativity"))
			return Constraint.Mode.NONNEGATIVE;
		if (c.toLowerCase().equalsIgnoreCase("clipped"))
			return Constraint.Mode.CLIPPED;
		return Constraint.Mode.NO;
	}
	
	public static void setModel(RealSignal signal) {
		float 
		stats[] = signal.getStats();
		mean = stats[0];
		min = stats[1];
		max = stats[2];
		stdev = stats[3];
	}
	
	public void apply(RealSignal x, Constraint.Mode constraint) {
		if (constraint == null)
			return;
		switch(constraint) {
			case NONNEGATIVE: 
				nonnegative(x);
				break;
			case CLIPPED:
				clipped(x, min, max);
				break;
			case RESCALED:
				rescaled(x, 0, 255);
				break;
			case NORMALIZED:
				normalized(x, mean, stdev);
				break;
			default:
				return;
		}
	}
	
	public void nonnegative(RealSignal x) {
		monitors.log("Apply non-negative constraint");
		int nxy = x.nx * x.ny;
		for(int k=0; k<x.nz; k++)
		for(int i=0; i<nxy; i++) {
			if (x.data[k][i] < 0)
				x.data[k][i] = 0;
		}
	}

	public void clipped(RealSignal x, float ming, float maxg) {
		monitors.log("Apply clipped constraint (" + ming + " ..." + maxg + ")");
		int nxy = x.nx * x.ny;
		for(int k=0; k<x.nz; k++)
		for(int i=0; i<nxy; i++) {
			if (x.data[k][i] <= ming)
				x.data[k][i] = ming;
			if (x.data[k][i] >= maxg)
				x.data[k][i] = maxg;
		}
	}

	public void normalized(RealSignal x, float meang, float stdevg) {
		monitors.log("Apply normalized constraint (" + meang + ", " + stdevg + ")");
		float stats[] = x.getStats();
		int nxy = x.nx * x.ny;
		for(int k=0; k<x.nz; k++)
		for(int i=0; i<nxy; i++) {
			float d = (x.data[k][i] - stats[0]) / stats[3];
			x.data[k][i] = d * stdevg + meang;
		}
	}

	public void rescaled(RealSignal x, float ming, float maxg) {
		monitors.log("Apply rescaled constraint (" + ming + " ... " + maxg + ")");
		int nxy = x.nx * x.ny;
		float stats[] = x.getStats();
		float a = (maxg-ming) / (stats[2] - stats[1]);
		for(int k=0; k<x.nz; k++)
		for(int i=0; i<nxy; i++) {
			x.data[k][i] = a*(x.data[k][i] - stats[1]) + ming;
		}
	}

}
