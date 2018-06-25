package signal.range;

import deconvolutionlab.monitor.Monitors;
import signal.RealSignal;

public class NonNegativeRange extends AbstractRange {

	protected Monitors monitors;
	
	public NonNegativeRange(Monitors monitors) {
		super(monitors);
	}
	
	public void apply(RealSignal x) {
		if (monitors != null)
			monitors.log("Apply non-negative constraint");
		int nxy = x.nx * x.ny;
		for(int k=0; k<x.nz; k++)
		for(int i=0; i<nxy; i++) {
			if (x.data[k][i] < 0)
				x.data[k][i] = 0;
		}
	}
}
