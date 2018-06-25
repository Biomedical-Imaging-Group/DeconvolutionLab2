package demos;

import deconvolution.Deconvolution;
import deconvolutionlab.Lab;
import signal.RealSignal;
import signal.factory.CubeSphericalBeads;
import signal.factory.Defocus;

public class CompareFFTs {

	public static void main(String[] arg) {
		new CompareFFTs();
	}

	public CompareFFTs() {
		
		compareAcademic();
	}

	private void compareAcademic() {
		
		String simu = "" +
		" -image synthetic CubeSphericalBeads 5.0 0.2 12.0 12.0  size 96 80 64 intensity 122 " +
		" -psf synthetic Gaussian 2.0 2.0 2.0  size 64 64 64  " +
		" -algorithm SIM 0 0 0 -out mip simu  -out stack simulation -fft Academic -path desktop";
		new Deconvolution("CompareFFT(Academic)", simu).run();
	
		RealSignal simul = Lab.getImage("simulation");
		float[] stats = simul.getStats();
		
		System.out.println("mean:" + stats[0] + "  min:" + stats[1] + " max:" + stats[2] + " " + stats[3]);
		// two bugs
		
		// no name in stats
		
		String deconv = "" +
		" -image file /Users/dsage/Desktop/simulation.tif" +
		" -psf synthetic Gaussian 2.0 2.0 2.0  size 64 64 64  " +
		" -reference synthetic CubeSphericalBeads 5.0 0.2 12.0 12.0  size 96 80 64 intensity 122 " +
		" -algorithm BVLS 10 1 -out mip lw+ -out stack lAndweber+ -fft Academic -path desktop -stats save  cA B2";
		new Deconvolution("CompareFFT(Academic)", deconv).run();
		
	}

	private void compareJTransforms(RealSignal ref, RealSignal psf) {

	}

	private void compareFFTW(RealSignal ref, RealSignal psf) {

	}
}
