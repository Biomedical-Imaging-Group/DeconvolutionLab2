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

import deconvolutionlab.monitor.Monitors;
import signal.RealSignal;

public class Padding {
	
	private AbstractPadding padX = getDefault();
	private AbstractPadding padY = getDefault();
	private AbstractPadding padZ = getDefault();

	private int minExtensionX = 0;
	private int minExtensionY = 0;
	private int minExtensionZ = 0;

	private int nx = -1;
	private int ny = -1;
	private int nz = -1;

	public Padding() {
		padX = new NoPadding();
		padY = new NoPadding();
		padZ = new NoPadding();	
		this.minExtensionX = 0;
		this.minExtensionY = 0;
		this.minExtensionZ = 0;
	}

	public Padding(int minExtensionX, int minExtensionY, int minExtensionZ) {
		padX = new NoPadding();
		padY = new NoPadding();
		padZ = new NoPadding();	
		this.minExtensionX = minExtensionX;
		this.minExtensionY = minExtensionY;
		this.minExtensionZ = minExtensionZ;
	}
	
	public Padding(AbstractPadding padX, AbstractPadding padY, AbstractPadding padZ, int minExtensionX, int minExtensionY, int minExtensionZ) {
		this.padX = padX;
		this.padY = padY;
		this.padZ = padZ;
		this.minExtensionX = minExtensionX;
		this.minExtensionY = minExtensionY;
		this.minExtensionZ = minExtensionZ;
	}

	public Padding(AbstractPadding pad, int extension) {
		this.padX = pad;
		this.padY = pad;
		this.padZ = pad;
		this.minExtensionX = extension;
		this.minExtensionY = extension;
		this.minExtensionZ = extension;
	}

	public Padding(String nameX, String nameY, String nameZ, int minExtensionX, int minExtensionY, int minExtensionZ) {
		for(AbstractPadding pad : getPaddings()) {
			if (pad.getName().equals(nameX))
				padX = pad;
			if (pad.getName().equals(nameY))
				padY = pad;
			if (pad.getName().equals(nameZ))
				padZ = pad;
			if (pad.getShortname().equals(nameX))
				padX = pad;
			if (pad.getShortname().equals(nameY))
				padY = pad;
			if (pad.getShortname().equals(nameZ))
				padZ = pad;
			this.minExtensionX = minExtensionX;
			this.minExtensionY = minExtensionY;
			this.minExtensionZ = minExtensionZ;
		}
	}
	
	public static ArrayList<AbstractPadding> getPaddings() {
		ArrayList<AbstractPadding> pads = new ArrayList<AbstractPadding>();		
		pads.add(new NoPadding());
		pads.add(new Multiple2Padding());
		pads.add(new Power2Padding());
		pads.add(new Multiple23Padding());
		pads.add(new Multiple235Padding());
		return pads;
	}
	
	public static ArrayList<String> getPaddingsName() {
		ArrayList<AbstractPadding> pads = getPaddings();	
		ArrayList<String> names = new ArrayList<String>();
		for(AbstractPadding pad : pads)
			names.add(pad.getName());
		return names;
	}
	
	public static String[] getPaddingsAsArray() {
		ArrayList<AbstractPadding> pads = getPaddings();	
		String names[] = new String[pads.size()];
		for(int i=0; i<pads.size(); i++)
			names[i] = pads.get(i).getName();
		return names;
	}

	public static AbstractPadding getByName(String name) {
		ArrayList<AbstractPadding> pads = getPaddings();
		for(AbstractPadding pad : pads)
			if (name.equals(pad.getName()))
				return pad;
		return getDefault();
	}
	
	public static AbstractPadding getByShortname(String name) {
		ArrayList<AbstractPadding> pads = getPaddings();
		for(AbstractPadding pad : pads)
			if (name.equals(pad.getShortname()))
				return pad;
		return getDefault();
	}


	public static AbstractPadding getDefault() {
		return new NoPadding();
	}

	public int[] pad(int nx, int ny, int nz) {
		return new int[] { padX.padding(nx+minExtensionX), padY.padding(ny+minExtensionY), padZ.padding(nz+minExtensionZ)};
	}

	public RealSignal pad(Monitors monitors, RealSignal input) {
		if (padX instanceof NoPadding && padY instanceof NoPadding && 
			padZ instanceof NoPadding && (minExtensionX + minExtensionY + minExtensionZ == 0)) {
			nx = ny = nz = -1;
			return input.duplicate();
		}
		nx = input.nx;
		ny = input.ny;
		nz = input.nz;
		
		int lx = padX.padding(nx+minExtensionX);
		int ly = padY.padding(ny+minExtensionY);
		int lz = padZ.padding(nz+minExtensionZ);
		monitors.log("Padding to (" + lx + ", " + ly + ", " + lz + ")");
		if (lx == nx)
		if (ly == ny)
		if (lz == nz)
			return input.duplicate();
		
		int ox = (lx - nx) / 2;
		int oy = (ly - ny) / 2;
		int oz = (lz - nz) / 2;
		String name = "pad(" + input.name + ")";
		RealSignal large = new RealSignal(name, lx, ly, lz);
		for(int k=0; k<nz; k++) {
			float in[] = input.data[k];
			float lpix[] = large.data[k+oz];
			for(int i=0; i<nx; i++)
			for(int j=0; j<ny; j++) 
				lpix[(i+ox) + lx*(j+oy)] = in[i + nx*j];
		}
		return large;
	}
	
	public RealSignal crop(Monitors monitors, RealSignal large) {
		if (nx == -1 || ny == -1  || nz == -1) {
			return large.duplicate();
		}
		int lx = large.nx;
		int ly = large.ny;
		int lz = large.nz;
		int ox = (lx - nx) / 2;
		int oy = (ly - ny) / 2;
		int oz = (lz - nz) / 2;
		
		if (lx == nx)
		if (ly == ny)
		if (lz == nz)
			return large;

		String name = " crop( " + large.name + ")";

		RealSignal output = new RealSignal(name, nx, ny, nz);
		monitors.log("Cropping to (" + nx + ", " + ny + ", " + nz + ")");
		
		for(int k=0; k<nz; k++) {
			float lpix[] = large.data[k+oz];
			float out[] = output.data[k];
			for(int i=0; i<nx; i++)
			for(int j=0; j<ny; j++) 
				out[i + nx*j] = lpix[(i+ox) + lx*(j+oy)];
		}
		return output;
	}

	@Override
	public String toString() {
		String s = "";
		s += "lateral (XY)";
		if (padX instanceof NoPadding) {
			if (minExtensionX == 0)
				s += " no padding";
			else
				s += " extension of " + minExtensionX;
		}
		else {
			s += " enforced to  " + padX.getName();
		}
		
		s += ", axial (Z)";
		if (padZ instanceof NoPadding) {
			if (minExtensionZ == 0)
				s += " no padding";
			else
				s += " extension of " + minExtensionZ;
		}
		else {
			s += " enforced to  " + padZ.getName();
		}

		return s;
	}
}
