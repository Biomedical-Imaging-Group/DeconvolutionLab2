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

import deconvolution.algorithm.Landweber;
import deconvolution.algorithm.RegularizedInverseFilter;
import deconvolution.algorithm.RichardsonLucy;
import deconvolution.algorithm.Simulation;
import deconvolutionlab.output.SaveMIP;
import deconvolutionlab.output.SaveStack;
import deconvolutionlab.output.ShowMIP;
import signal.RealSignal;
import signal.factory.CubeSphericalBeads;
import signal.factory.Gaussian;

/** 
 * This demo exists in 5 different interfaces:
 * - using ImageJ Macro 
 * - using the command line interface (cshell)
 * - in Java,  using the DeconvolutionLab2 as a Java Library
 * - in Java,  using the command of DeconvolutionLab2
 * - in Matlab, calling methods of the class DL2
 */
public class DemoSimulationAndAlgorithms_JavaLibrary {

	private String path = System.getProperty("user.home") + File.separator + "Desktop"+ File.separator + "Demo" + File.separator + "JavaLibrary";
	
	public static void main(String[] arg) {
		new DemoSimulationAndAlgorithms_JavaLibrary();
	}

	public DemoSimulationAndAlgorithms_JavaLibrary() {
		
		RealSignal ref = new CubeSphericalBeads(5.0, 0.2, 12.0, 12.0).intensity(128).generate(96, 80, 64);
		RealSignal psf = new Gaussian(2.0, 2.0, 2.0).generate(96, 80, 64);
	
		Simulation simulation = new Simulation(0, 0, 4);
		simulation.setPath(path);
		simulation.addOutput(new ShowMIP("simulation").setSnapshot(1));
		simulation.addOutput(new SaveStack("simulation"));
		simulation.addOutput(new SaveMIP("simulation_mip_8bits").rescale().toByte());
		
		RealSignal y = simulation.run(ref, psf);
		
		RegularizedInverseFilter rif = new RegularizedInverseFilter(0.01);
		rif.setStats("RIF").setPath(path);
		rif.addOutput(new ShowMIP("RIF").setSnapshot(1));
		rif.addOutput(new SaveMIP("RIF_mip_8bits").rescale().toByte());
		rif.addOutput(new SaveStack("RIF"));
		rif.run(y, psf, ref);

		RichardsonLucy rl = new RichardsonLucy(50);
		rl.setStats("RL").setPath(path);
		rl.addOutput(new ShowMIP("RL").setSnapshot(1));
		rl.addOutput(new SaveStack("RL"));
		rl.addOutput(new SaveMIP("RL_mip_8bits").rescale().toByte());
		rl.run(y, psf, ref);

		Landweber lw = new Landweber(50, 1.5);
		lw.setStats("LW").setPath(path);
		lw.addOutput(new ShowMIP("LW").setSnapshot(1));
		lw.addOutput(new SaveStack("LW"));
		lw.addOutput(new SaveMIP("LW_mip_8bits").rescale().toByte());
		lw.run(y, psf, ref);

		Landweber lwp = new Landweber(50, 1.5);
		lwp.setStats("LW+").setPath("desktop");
		lwp.addOutput(new ShowMIP("LW+").setSnapshot(1));
		lwp.addOutput(new SaveStack("LW+"));
		lwp.addOutput(new SaveMIP("LW+_mip_8bits").rescale().toByte());
		lwp.run(y, psf, ref);
	}

}
