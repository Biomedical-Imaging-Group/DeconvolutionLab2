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

package fft.fftw;

import java.io.File;
import java.security.CodeSource;

import deconvolutionlab.monitor.Monitors;
import fft.AbstractFFT;
import fft.AbstractFFTLibrary;
import jfftw.complex.nd.Plan;

public class FFTWLibrary extends AbstractFFTLibrary {

	private String location;
	
	public FFTWLibrary(Monitors monitors) {

		String path = "";
		boolean found = false;
		if (!found) {
			try {
				CodeSource code = FFTWLibrary.class.getProtectionDomain().getCodeSource();
				File file = new File(code.getLocation().toURI().getPath());
				path = file.getParentFile().getPath() + File.separator + "FFTW" + File.separator;
				found = existsWidsom(monitors, path);
				location = path;
			}
			catch (Exception ex) {
				monitors.log("FFTW Widsom not found in : " + path);
				location = "Not found in " + path;
				found = false;
			}
		}

		if (!found) {
			try {
				CodeSource code = FFTWLibrary.class.getProtectionDomain().getCodeSource();
				File file = new File(code.getLocation().toURI().getPath());
				path = file.getParentFile().getPath() + File.separator;
				found = existsWidsom(monitors, path);
				location = path;
			}
			catch (Exception ex) {
				monitors.log("FFTW Widsom not found in : " + path);
				location = "Not found in " + path;
				found = false;
			}
		}

		if (!found) {
			try {
				path = System.getProperty("user.home") + File.separator + "FFTW" + File.separator;
				found = existsWidsom(monitors, path);
				location = path;
			}
			catch (Exception ex) {
				monitors.log("FFTW Widsom not found in : " + path);
				location = "Not found in " + path;
				found = false;
			}
		}

		if (!found) {
			try {
				path = System.getProperty("user.home") + File.separator;
				found = existsWidsom(monitors, path);
				location = path;
			}
			catch (Exception ex) {
				monitors.log("FFTW Widsom not found in : " + path);
				location = "Not found in " + path;
				found = false;
			}
		}

		if (!found)
			return;

		loadLibraries(monitors, path);

		try {
			new Plan(new int[] { 20, 20, 20 }, Plan.FORWARD, Plan.ESTIMATE | Plan.IN_PLACE | Plan.USE_WISDOM);
			ffts.add(new FFTW3D());
			installed = true;
		}
		catch (UnsatisfiedLinkError ex) {
			ex.printStackTrace();
			installed = false;
		}

	}

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public String getCredit() {
		return "http://www.fftw.org (FFTW Version 2)";
	}

	@Override
	public String getLibraryName() {
		return "FFTW2";
	}

	@Override
	public String getLicence() {
		return "<h1>FFTW Version 2, " + "<p>http://www.fftw.org" + "<p>FFTW is free software; you can redistribute it and/or modify it " + "under the terms of the GNU General Public License as published by "
		        + "the Free Software Foundation; either version 2 of the License, or " + "(at your option) any later version.";
	}

	private static void loadLibraries(Monitors monitors, String path) {
		try {
			String osname = System.getProperty("os.name");
			
			if (osname.startsWith("Linux")) {
                String osarch = System.getProperty("os.arch");
                if (osarch.contains("64")) {
                    System.load(path + "libFFTWJNI64.so");
                    monitors.log("Loading library FFTW for " + osarch + " " + osname + " " + path + "libFFTWJNI64.so");
                }
                else {
                    System.load(path + "libFFTWJNI32.so");
                    monitors.log("Loading library FFTW for " + osarch + " " + osname + " " + path + "libFFTWJNI32.so");
                }
			}
			else if (osname.startsWith("Windows")) {
				String osarch = System.getProperty("os.arch");
				if (osarch.contains("64")) {
					System.load(path + "FFTWJNIWin64.dll");
					monitors.log("Loading library FFTW for " + osarch + " " + osname + " " + path + "FFTWJNIWin64.dll");
				}
				else {
					System.load(path + "FFTWJNIWin32.dll");
					monitors.log("Loading library FFTW for " + osarch + " " + osname + " " + path + "FFTWJNIWin32.dll");
				}
			}
			else if (osname.startsWith("Mac")) {
				System.load(path + "libFFTWJNIMac.jnilib");
				monitors.log("Loading library FFTW for " + osname + " " + path + "libFFTWJNIMac.jnilib");
			}
			else {
				monitors.log("FFTW is not provided for the architecture : " + osname);
			}

		}
		catch (Exception ex) {
			monitors.log("FFTW not found in : " + path);
		}
	}

	private static boolean existsWidsom(Monitors monitors, String path) {
		boolean found = false;
		if (new File(path + "Wisdom").exists()) {
			monitors.log("FFTW found in : " + path);
			found = true;
		}
		else {
			monitors.log("FFTW Widsom not found in : " + path);
			found = false;
		}
		return found;
	}
	
	@Override
	public AbstractFFT getDefaultFFT() {
		return new FFTW3D();
	}
}
