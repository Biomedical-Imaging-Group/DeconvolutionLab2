
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

import bilib.tools.Files;
import deconvolution.Deconvolution;
import deconvolutionlab.Constants;
import deconvolutionlab.Lab;
import deconvolutionlab.LabDialog;
import deconvolutionlab.Platform;
import deconvolutionlab.monitor.Monitors;
import ij.ImagePlus;
import ij.WindowManager;
import matlab.Converter;
import signal.RealSignal;

/**
 * This class allows Matlab interface for DeconvolutionLab2
 * 
 * A Matlab 3D variable in converted to a RealSignal and vice-versa.
 * 
 * @author Daniel Sage
 *
 */
public class DL2 {
	
	static {
		Lab.init(Platform.MATLAB, Files.getWorkingDirectory() + "DeconvolutionLab2.config");
		Monitors.createDefaultMonitor().log("DeconvolutionLab2 " + Constants.version + " on Matlab");
	}

	public static void lab() {
		Lab.init(Platform.MATLAB, Files.getWorkingDirectory() + "DeconvolutionLab2.config");
		LabDialog dlg = new LabDialog();
		Lab.setVisible(dlg, false);
	}

	public static Object open(String source, String arg, String path) {
		Monitors m = Monitors.createDefaultMonitor();
		String s = source.trim();
		if (s.equalsIgnoreCase("synthetic") || s.equalsIgnoreCase("platform") || s.equalsIgnoreCase("file") || s.equalsIgnoreCase("dir") || s.equalsIgnoreCase("directory")) {
			RealSignal psf = Deconvolution.createRealSignal(m, s, arg, path);
			return Converter.createMatlabMatrix(psf);
		}
		else {
			m.error("Unable to open the source image");
			m.error("First argument should be: file, dir, directory, platform, or synthetic");
			return null;
		}
	}
	
	public static void run(String command) {
		new Deconvolution("Matlab", command).deconvolve();
	}	
	
	public static void launch(String command) {
		new Deconvolution("Matlab", command).launch();
	}	

	public static Object get(String image) {
		ImagePlus imp = WindowManager.getCurrentImage();
		if (imp != null)
			return Converter.get(imp);
		return null;
	}	

	public static Object run(Object arrayImage, Object arrayPSF, String algo) {
		RealSignal image = Converter.createRealSignal(arrayImage);
		RealSignal psf = Converter.createRealSignal(arrayPSF);
		String command = " -image platform input -psf platform psf -algorithm " + algo;
		Deconvolution d = new Deconvolution("Matlab", command);
		RealSignal result = d.deconvolve(image, psf);
		return Converter.createMatlabMatrix(result);
	}
	
	public static void help() {
		Lab.help();
	}
	
	public static void clear() {
		int ids[] = WindowManager.getIDList();
		for(int id : ids) {
			ImagePlus imp = WindowManager.getImage(id);
			if (imp != null)
				imp.close();
		}
	}

	public static void show(Object arrayImage) {
		RealSignal image = Converter.createRealSignal(arrayImage);
		Lab.show(image);
	}
	
	public static void showMIP(Object arrayImage) {
		RealSignal image = Converter.createRealSignal(arrayImage);
		Lab.showMIP(image);
	}

	public static void showPlanar(Object arrayImage) {
		RealSignal image = Converter.createRealSignal(arrayImage);
		Lab.showPlanar(image);
	}
	
	public static void showOrthoview(Object arrayImage) {
		RealSignal image = Converter.createRealSignal(arrayImage);
		Lab.showOrthoview(image);
	}
	

	public static Object DIV(Object arrayImage, Object arrayPSF) {
		return DIV(arrayImage, arrayPSF, null, "");
	}
	
	public static Object DIV(Object arrayImage, Object arrayPSF, String options) {
		return DIV(arrayImage, arrayPSF, null, options);
	}

	public static Object DIV(Object arrayImage, Object arrayPSF, Object arrayRef, String options) {
		RealSignal image = Converter.createRealSignal(arrayImage);
		RealSignal psf = Converter.createRealSignal(arrayPSF);
		RealSignal ref = Converter.createRealSignal(arrayRef);
		String command = " -algorithm DIV " + options;
		Deconvolution d = new Deconvolution("Matlab DIV", command);
		RealSignal result = d.deconvolve(image, psf, ref);
		return Converter.createMatlabMatrix(result);
	}

	public static Object CONV(Object arrayImage, Object arrayPSF) {
		return CONV(arrayImage, arrayPSF, null, "");
	}

	public static Object CONV(Object arrayImage, Object arrayPSF, String options) {
		return CONV(arrayImage, arrayPSF, null, options);
	}

	public static Object CONV(Object arrayImage, Object arrayPSF, Object arrayRef, String options) {
		RealSignal image = Converter.createRealSignal(arrayImage);
		RealSignal psf = Converter.createRealSignal(arrayPSF);
		RealSignal ref = Converter.createRealSignal(arrayRef);
		String command = " -algorithm CONV " + options;
		Deconvolution d = new Deconvolution("Matlab CONV", command);
		RealSignal result = d.deconvolve(image, psf, ref);
		return Converter.createMatlabMatrix(result);
	}
	
	public static Object SIM(Object arrayImage, Object arrayPSF, double meanGaussian, double stdevGaussian, double poisson) {
		return SIM(arrayImage, arrayPSF, null, meanGaussian, stdevGaussian, poisson, "");
	}
	
	public static Object SIM(Object arrayImage, Object arrayPSF, double meanGaussian, double stdevGaussian, double poisson, String options) {
		return SIM(arrayImage, arrayPSF, null, meanGaussian, stdevGaussian, poisson, options);
	}
	
	public static Object SIM(Object arrayImage, Object arrayPSF, Object arrayRef, double meanGaussian, double stdevGaussian, double poisson, String options) {
		RealSignal image = Converter.createRealSignal(arrayImage);
		RealSignal psf = Converter.createRealSignal(arrayPSF);
		RealSignal ref = Converter.createRealSignal(arrayRef);
		String command = " -algorithm SIM " + meanGaussian + " "  + " " + stdevGaussian  + " " + poisson + " " + options;
		Deconvolution d = new Deconvolution("Matlab SIM", command);
		RealSignal result = d.deconvolve(image, psf, ref);
		return Converter.createMatlabMatrix(result);
	}

	public static Object NIF(Object arrayImage, Object arrayPSF) {
		return NIF(arrayImage, arrayPSF, null, "");
	}
	
	public static Object NIF(Object arrayImage, Object arrayPSF, String options) {
		return NIF(arrayImage, arrayPSF, null, options);
	}

	public static Object NIF(Object arrayImage, Object arrayPSF, Object arrayRef, String options) {
		RealSignal image = Converter.createRealSignal(arrayImage);
		RealSignal psf = Converter.createRealSignal(arrayPSF);
		RealSignal ref = Converter.createRealSignal(arrayRef);
		String command = " -algorithm NIF " + options;
		Deconvolution d = new Deconvolution("Matlab NIF", command);
		RealSignal result = d.deconvolve(image, psf, ref);
		return Converter.createMatlabMatrix(result);
	}

	public static Object TRIF(Object arrayImage, Object arrayPSF, double regularizationFactor) {
		return TRIF(arrayImage, arrayPSF, null, regularizationFactor, "");
	}

	public static Object TRIF(Object arrayImage, Object arrayPSF, double regularizationFactor, String options) {
		return TRIF(arrayImage, arrayPSF, null, regularizationFactor, options);
	}
	
	public static Object TRIF(Object arrayImage, Object arrayPSF, Object arrayRef, double regularizationFactor, String options) {
		RealSignal image = Converter.createRealSignal(arrayImage);
		RealSignal psf = Converter.createRealSignal(arrayPSF);
		RealSignal ref = Converter.createRealSignal(arrayRef);
		String command = " -algorithm TRIF " + regularizationFactor + " " + options;
		Deconvolution d = new Deconvolution("Matlab TRIF", command);
		RealSignal result = d.deconvolve(image, psf, ref);
		return Converter.createMatlabMatrix(result);
	}
	
	public static Object RIF(Object arrayImage, Object arrayPSF, double regularizationFactor, String options) {
		return RIF(arrayImage, arrayPSF, null, regularizationFactor, options);
	}
	
	public static Object RIF(Object arrayImage, Object arrayPSF, double regularizationFactor) {
		return RIF(arrayImage, arrayPSF, null, regularizationFactor, "");
	}
	
	public static Object RIF(Object arrayImage, Object arrayPSF, Object arrayRef, double regularizationFactor, String options) {
		RealSignal image = Converter.createRealSignal(arrayImage);
		RealSignal psf = Converter.createRealSignal(arrayPSF);
		RealSignal ref = Converter.createRealSignal(arrayRef);
		String command = " -algorithm RIF " + regularizationFactor + " " + options;
		Deconvolution d = new Deconvolution("Matlab RIF", command);
		RealSignal result = d.deconvolve(image, psf, ref);
		return Converter.createMatlabMatrix(result);
	}	
	
	public static Object RL(Object arrayImage, Object arrayPSF, double itmax) {
		return RL(arrayImage, arrayPSF, null, itmax, "");
	}
	
	public static Object RL(Object arrayImage, Object arrayPSF, double itmax, String options) {
		return RL(arrayImage, arrayPSF, null, itmax, options);
	}
	
	public static Object RL(Object arrayImage, Object arrayPSF, Object arrayRef, double itmax, String options) {
		RealSignal image = Converter.createRealSignal(arrayImage);
		RealSignal psf = Converter.createRealSignal(arrayPSF);
		RealSignal ref = Converter.createRealSignal(arrayRef);
		String command = " -algorithm RL " + itmax + " " + options;
		Deconvolution d = new Deconvolution("Matlab RL", command);
		RealSignal result = d.deconvolve(image, psf, ref);
		return Converter.createMatlabMatrix(result);
	}
	
	public static Object RLTV(Object arrayImage, Object arrayPSF, double itmax, double regularizationFactor) {
		return RLTV(arrayImage, arrayPSF, null, itmax, regularizationFactor, "");
	}
	
	public static Object RLTV(Object arrayImage, Object arrayPSF, double itmax, double regularizationFactor, String options) {
		return RLTV(arrayImage, arrayPSF, null, itmax, regularizationFactor, options);
	}
	
	public static Object RLTV(Object arrayImage, Object arrayPSF, Object arrayRef, double itmax, double regularizationFactor, String options) {
		RealSignal image = Converter.createRealSignal(arrayImage);
		RealSignal psf = Converter.createRealSignal(arrayPSF);
		RealSignal ref = Converter.createRealSignal(arrayRef);
		String command = " -algorithm RLTV " + itmax + " " + regularizationFactor + " " + options;
		Deconvolution d = new Deconvolution("Matlab RLTV", command);
		RealSignal result = d.deconvolve(image, psf, ref);
		return Converter.createMatlabMatrix(result);
	}	

	public static Object LW(Object arrayImage, Object arrayPSF, double itmax, double gamma) {
		return LW(arrayImage, arrayPSF, null, itmax, gamma, "");
	}

	public static Object LW(Object arrayImage, Object arrayPSF, double itmax, double gamma, String options) {
		return LW(arrayImage, arrayPSF, null, itmax, gamma, options);
	}
	
	public static Object LW(Object arrayImage, Object arrayPSF, Object arrayRef, double itmax, double gamma, String options) {
		RealSignal image = Converter.createRealSignal(arrayImage);
		RealSignal psf = Converter.createRealSignal(arrayPSF);
		RealSignal ref = Converter.createRealSignal(arrayRef);
		String command = " -algorithm LW " + itmax + " " + gamma + " " + options;
		Deconvolution d = new Deconvolution("Matlab LW", command);
		RealSignal result = d.deconvolve(image, psf, ref);
		return Converter.createMatlabMatrix(result);
	}

	public static Object NNLS(Object arrayImage, Object arrayPSF, double itmax, double gamma) {
		return NNLS(arrayImage, arrayPSF, null, itmax, gamma, "");
	}

	public static Object NNLS(Object arrayImage, Object arrayPSF, double itmax, double gamma, String options) {
		return NNLS(arrayImage, arrayPSF, null, itmax, gamma, options);
	}
	
	public static Object NNLS(Object arrayImage, Object arrayPSF, Object arrayRef, double itmax, double gamma, String options) {
		RealSignal image = Converter.createRealSignal(arrayImage);
		RealSignal psf = Converter.createRealSignal(arrayPSF);
		RealSignal ref = Converter.createRealSignal(arrayRef);
		String command = " -algorithm NNLS " + itmax + " " + options;
		Deconvolution d = new Deconvolution("Matlab NNLS", command);
		RealSignal result = d.deconvolve(image, psf, ref);
		return Converter.createMatlabMatrix(result);
	}
	
	public static Object BVLS(Object arrayImage, Object arrayPSF, double itmax, double gamma) {
		return BVLS(arrayImage, arrayPSF, null, itmax, gamma, "");
	}
	
	public static Object BVLS(Object arrayImage, Object arrayPSF, double itmax, double gamma, String options) {
		return BVLS(arrayImage, arrayPSF, null, itmax, gamma, options);
	}
	
	public static Object BVLS(Object arrayImage, Object arrayPSF, Object arrayRef, double itmax, double gamma, String options) {
		RealSignal image = Converter.createRealSignal(arrayImage);
		RealSignal psf = Converter.createRealSignal(arrayPSF);
		RealSignal ref = Converter.createRealSignal(arrayRef);
		String command = " -algorithm BVLS " + itmax + " " + options;
		Deconvolution d = new Deconvolution("Matlab BVLS", command);
		RealSignal result = d.deconvolve(image, psf, ref);
		return Converter.createMatlabMatrix(result);
	}

	public static Object TM(Object arrayImage, Object arrayPSF, double itmax, double gamma, double lambda) {
		return TM(arrayImage, arrayPSF, null, itmax, gamma, lambda, "");
	}

	public static Object TM(Object arrayImage, Object arrayPSF, double itmax, double gamma, double lambda, String options) {
		return TM(arrayImage, arrayPSF, null, itmax, gamma, lambda, options);
	}
	
	public static Object TM(Object arrayImage, Object arrayPSF, Object arrayRef, double itmax, double gamma, double lambda, String options) {
		RealSignal image = Converter.createRealSignal(arrayImage);
		RealSignal psf = Converter.createRealSignal(arrayPSF);
		RealSignal ref = Converter.createRealSignal(arrayRef);
		String command = " -algorithm TM " + itmax + " " + gamma + " " + lambda + " " + options;
		Deconvolution d = new Deconvolution("Matlab TM", command);
		RealSignal result = d.deconvolve(image, psf, ref);
		return Converter.createMatlabMatrix(result);
	}

	public static Object ICTM(Object arrayImage, Object arrayPSF, double itmax, double gamma, double lambda) {
		return ICTM(arrayImage, arrayPSF, null, itmax, gamma, lambda, "");
	}

	public static Object ICTM(Object arrayImage, Object arrayPSF, double itmax, double gamma, double lambda, String options) {
		return ICTM(arrayImage, arrayPSF, null, itmax, gamma, lambda, options);
	}
	
	public static Object ICTM(Object arrayImage, Object arrayPSF, Object arrayRef, double itmax, double gamma, double lambda, String options) {
		RealSignal image = Converter.createRealSignal(arrayImage);
		RealSignal psf = Converter.createRealSignal(arrayPSF);
		RealSignal ref = Converter.createRealSignal(arrayRef);
		String command = " -algorithm ICTM " + itmax + " " + gamma + " " + lambda + " " + options;
		Deconvolution d = new Deconvolution("Matlab ICTM", command);
		RealSignal result = d.deconvolve(image, psf, ref);
		return Converter.createMatlabMatrix(result);
	}
}
