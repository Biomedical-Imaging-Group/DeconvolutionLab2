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

package fft;

import java.util.ArrayList;

import deconvolutionlab.monitor.Monitors;
import fft.academic.Academic;
import fft.academic.AcademicLibrary;
import fft.fftw.FFTW3D;
import fft.fftw.FFTWLibrary;
import fft.jtransforms.JTransforms;
import fft.jtransforms.JTransformsLibrary;

public class FFT {

	private static ArrayList<AbstractFFTLibrary>	libraries	= new ArrayList<AbstractFFTLibrary>();
	private static ArrayList<AbstractFFTLibrary>	registers		= new ArrayList<AbstractFFTLibrary>();

	static {
		Monitors monitors = Monitors.createDefaultMonitor();
		AcademicLibrary academic = new AcademicLibrary();
		if (academic.isInstalled()) {
			libraries.add(academic);
			monitors.log("AcademicFFT Added");
		}
		else {
			monitors.log("AcademicFFT not found");
		}

		JTransformsLibrary jtransform = new JTransformsLibrary();
		if (jtransform.isInstalled()) {
			libraries.add(jtransform);
			monitors.log("JTransforms Added");
		}
		else {
			monitors.log("JTransforms not found");
		}

		FFTWLibrary jfftw = new FFTWLibrary(monitors);
		if (jfftw.isInstalled()) {
			libraries.add(jfftw);
			monitors.log("FFTW Added");
		}
		else {
			monitors.log("FFTW not found");
		}	
		registers.add(academic);
		registers.add(jtransform);
		registers.add(jfftw);

	}

	public static ArrayList<AbstractFFTLibrary> getRegisteredLibraries() {
		return registers;
	}

	public static ArrayList<AbstractFFTLibrary> getInstalledLibraries() {
		return libraries;
	}

	public static AbstractFFTLibrary getFastestFFT() {
		for (int i = 0; i < libraries.size(); i++)
			if (libraries.get(i).getLibraryName().equals("FFTW2"))
				return libraries.get(i);
		for (int i = 0; i < libraries.size(); i++)
			if (libraries.get(i).getLibraryName().equals("JTransforms"))
				return libraries.get(i);
		return libraries.get(0);
	}
	
	public static String[] getLibrariesAsArray() {
		String[] libs = new String[libraries.size()+1];
		libs[0] = "Fastest";
		for (int i = 0; i < libraries.size(); i++)
			libs[i+1] = libraries.get(i).getLibraryName();
		return libs;
	}

	public static AbstractFFT createFFT(Monitors monitors, AbstractFFTLibrary fftlib, int nx, int ny, int nz) {
		String name = fftlib.getLibraryName().toLowerCase();
		AbstractFFT fft = null;
		String n = name.trim().toLowerCase();
		if (n.equals("academic")) {
			fft = new Academic();
			fft.init(monitors, nx, ny, nz);
			return fft;
		}
		if (n.equals("jtransforms")) {
			fft = new JTransforms();
			fft.init(monitors, nx, ny, nz);
			return fft;
		}
		if (n.equals("fftw2")) {
			fft = new FFTW3D();
			fft.init(monitors, nx, ny, nz);
			return fft;
		}
		return createDefaultFFT(monitors, nx, ny, nz);
	}

	public static AbstractFFTLibrary getLibraryByName(String name) {
		if (name.equalsIgnoreCase("fastest"))
			return getFastestFFT();
		for (AbstractFFTLibrary library : getInstalledLibraries())
			if (library.getLibraryName().equals(name))
				return library;
		return new AcademicLibrary();
	}

	public static AbstractFFT createDefaultFFT(Monitors monitors, int nx, int ny, int nz) {
		AbstractFFT fft = new Academic();
		fft.init(monitors, nx, ny, nz);
		return fft;
	}

	public static String getLicence(String name) {
		for (AbstractFFTLibrary lib : getInstalledLibraries()) {
			for (AbstractFFT fft : lib.getFFTs())
				if (name.equals(fft.getName()))
					return lib.getLicence();
		}
		return "Unknown FFT library";
	}

}
