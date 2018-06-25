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

package signal.apodization;

import java.util.ArrayList;

import signal.RealSignal;
import deconvolutionlab.monitor.Monitors;

public class Apodization {
	
	private AbstractApodization apoX = getDefault();
	private AbstractApodization apoY = getDefault();
	private AbstractApodization apoZ = getDefault();

	public Apodization() {
		apoX = new UniformApodization();
		apoY = new UniformApodization();
		apoZ = new UniformApodization();
	}

	public Apodization(AbstractApodization apoX, AbstractApodization apoY, AbstractApodization apoZ) {
		this.apoX = apoX;
		this.apoY = apoY;
		this.apoZ = apoZ;
	}

	public Apodization(String nameX, String nameY, String nameZ) {
		for(AbstractApodization apo : getApodizations()) {
			if (apo.getName().equals(nameX))
				apoX = apo;
			if (apo.getName().equals(nameY))
				apoY = apo;
			if (apo.getName().equals(nameZ))
				apoZ = apo;
			if (apo.getShortname().equals(nameX))
				apoX = apo;
			if (apo.getShortname().equals(nameY))
				apoY = apo;
			if (apo.getShortname().equals(nameZ))
				apoZ = apo;
		}
	}
	
	public static ArrayList<AbstractApodization> getApodizations() {
		ArrayList<AbstractApodization> apos = new ArrayList<AbstractApodization>();
		apos.add(new UniformApodization());
		apos.add(new HammingApodization());
		apos.add(new HannApodization());
		apos.add(new RaisedCosineApodization(1));
		apos.add(new TukeyApodization(0.5));
		apos.add(new WelchApodization());
		return apos;
	}
	
	public static ArrayList<String> getApodizationsName() {
		ArrayList<AbstractApodization> apos = getApodizations();
		ArrayList<String> names = new ArrayList<String>();
		for(AbstractApodization apo : apos)
			names.add(apo.getName());
		return names;
	}
	
	public static String[] getApodizationsAsArray() {
		ArrayList<AbstractApodization> apos = getApodizations();
		String names[] = new String[apos.size()];
		for(int i=0; i<apos.size(); i++)
			names[i] = apos.get(i).getName();
		return names;
	}
	
	public static AbstractApodization getByName(String name) {
		ArrayList<AbstractApodization> apos = getApodizations();
		for(AbstractApodization apo : apos)
			if (name.equals(apo.getName()))
				return apo;
		return getDefault();
	}

	public static AbstractApodization getByShortname(String name) {
		ArrayList<AbstractApodization> apos = getApodizations();
		for(AbstractApodization apo : apos)
			if (name.equals(apo.getShortname()))
				return apo;
		return getDefault();
	}

	public static AbstractApodization getDefault() {
		return new UniformApodization();
	}
		
	public void apodize(Monitors monitors, RealSignal signal) {
		if (apoX instanceof UniformApodization && 
			apoY instanceof UniformApodization && 
			apoZ instanceof UniformApodization) {
			return;
		}
		if (monitors != null)
			monitors.log("Apodization (" + apoX.getName() + ", " + apoY.getName() + ", " + apoZ.getName() + ")");
		signal.setName("apo(" + signal.name + ")");
		for(int i=0; i<signal.nx; i++) {
			double cx = apoX.apodize(i, signal.nx);
			for(int j=0; j<signal.ny; j++) {
				double cy =  apoY.apodize(j, signal.ny);
				int index = i + signal.nx*j;
				for(int k=0; k<signal.nz; k++) {
					double cz = apoZ.apodize(k, signal.nz);
					signal.data[k][index] = (float)(cx * cy * cz * signal.data[k][index]);
				}
			}
		}
	}
	
	@Override
	public String toString() {
		String s = "";
		s += "lateral (XY)";
		if (apoX instanceof UniformApodization) 
			s += " keep unchanged";
		else
			s+= " " + apoX.getName();
		s += ", axial (Z)";
		if (apoZ instanceof UniformApodization) 
			s += " keep unchanged";
		else
			s+= " " + apoZ.getName();
		
		return s;
	}
}
