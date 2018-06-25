package demos;
import deconvolution.algorithm.RichardsonLucy;
import deconvolution.algorithm.Simulation;
import deconvolutionlab.Lab;
import deconvolutionlab.Platform;
import signal.RealSignal;
import signal.factory.Constant;
import signal.factory.Cube;
import signal.factory.Gaussian;

public class RestartRichardsonLucy {

	public static void main(String arg[]) {
		new RestartRichardsonLucy();
	}
	
	public RestartRichardsonLucy() {
		Lab.init(Platform.STANDALONE);
		RealSignal psf = new Gaussian(3, 3, 3).generate(256, 256, 1);
		RealSignal ref = create();
		
		RealSignal in = new Simulation(0, 1, 1).run(ref, psf);
		
		RichardsonLucy rl1 = new RichardsonLucy(1);
		rl1.setReference(ref);
		
		RealSignal out0 = rl1.run(in,  psf);
		RealSignal out1 = rl1.run(out0,  psf);
		Lab.show(out1, "out1");
		
		RichardsonLucy rl2 = new RichardsonLucy(2);
		rl2.setReference(ref);
		RealSignal out2 = rl2.run(in,  psf);
		Lab.show(out2, "out2");
		
	}
	
	public RealSignal create() {
		RealSignal x = new Constant().intensity(100).generate(256, 256, 1);
		for(int i=0; i<=6; i++)
			x.plus(new Cube(16+i*16, 0.1).intensity(200).generate(256, 256, 1));
		return x;	
	}
}
