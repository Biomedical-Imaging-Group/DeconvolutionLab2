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

package demos;

import java.io.File;

import deconvolution.Deconvolution;

/** 
 * This demo exists in 5 different interfaces:
 * - using ImageJ Macro 
 * - using the command line interface (cshell)
 * - in Java,  using the DeconvolutionLab2 as a Java Library
 * - in Java,  using the command of DeconvolutionLab2
 * - in Matlab, calling methods of the class DL2
 */

public class DemoSimulationAndAlgorithms_JavaCommand {

	private String path = System.getProperty("user.home") + File.separator + "Desktop"+ File.separator + "Demo" + File.separator + "JavaCommand";

	public static void main(String[] arg) {
		new DemoSimulationAndAlgorithms_JavaCommand();
	}

	public DemoSimulationAndAlgorithms_JavaCommand() {
		String image = " -image synthetic CubeSphericalBeads 5.0 0.2 12.0 12.0  size 96 80 64 intensity 128 ";
		String psf = " -psf synthetic Gaussian 2.0 2.0 2.0  size 96 80 64 ";
		
		String identity = image +
		" -algorithm I -out mistackp noshow reference -out mip rescaled byte noshow reference_mip_8bits -out stack reference noshow -path " + path;
		new Deconvolution("ref", identity + psf).deconvolve();
			
		String simulation = " -image file reference.tif " +
		" -algorithm SIM 0 0 4 -out stack noshow simulation -out mip rescaled byte noshow simulation_mip_8bits -out stack simulation noshow -path " + path;
		new Deconvolution("sim", simulation + psf).deconvolve();
	
		String deconv = " -image file simulation.tif -reference file reference.tif -path " + path + psf;
		new Deconvolution("RIF", " -algorithm RIF 0.01   -out stack noshow RIF -out mip rescaled byte noshow RIF_mip_8bits -stats RIF noshow "  + deconv).deconvolve();
		new Deconvolution("RL",  " -algorithm RL  50     -out stack noshow RL  -out mip rescaled byte noshow RL_mip_8bits  -stats RL noshow " + deconv).deconvolve();
		new Deconvolution("LW",  " -algorithm LW  50 1.5 -out stack noshow LWs -out mip rescaled byte noshow LW_mip_8bits  -stats LW noshow " + deconv).deconvolve();
		new Deconvolution("LW+", " -algorithm LW+ 50 1.5 -out stack noshow LW+ -out mip rescaled byte noshow LW+_mip_8bits -stats LW+ noshow  " + deconv).deconvolve();
	}

}
