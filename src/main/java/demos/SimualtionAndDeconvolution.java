package demos;

import deconvolution.algorithm.LandweberPositivity;
import deconvolution.algorithm.Simulation;
import deconvolution.algorithm.StarkParker;
import deconvolutionlab.output.OutMIP;
import signal.RealSignal;
import signal.factory.Cube;
import signal.factory.Gaussian;

public class SimualtionAndDeconvolution {

	public static void main(String arg[]) {
		new SimualtionAndDeconvolution();
	}
	
	public SimualtionAndDeconvolution() {
		RealSignal ref = new Cube(30, 1).intensity(100).generate(200, 200, 1);
		RealSignal psf = new Gaussian(2, 2, 2).generate(200, 200, 1);
	
		Simulation simu = new Simulation(0, 0, 1);
		simu.addOutput(new OutMIP("mip simu"));
		RealSignal y = simu.run(ref, psf);
		
		LandweberPositivity lw = new LandweberPositivity(50, 1);
		lw.setStats("LandweberPositivity");
		lw.addOutput(new OutMIP("mip LandweberPositivity"));
		lw.run(y, psf, ref);

		StarkParker sp = new StarkParker(50, 1);
		sp.addOutput(new OutMIP("mip StarkParker"));
		sp.setStats("StarkParker");
		sp.run(y, psf, ref);
	}

}
