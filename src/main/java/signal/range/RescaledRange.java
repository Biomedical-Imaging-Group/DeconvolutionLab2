package signal.range;

import deconvolutionlab.monitor.Monitors;
import signal.RealSignal;

public class RescaledRange extends AbstractRange {

	protected Monitors monitors;
	private float min = 0;
	private float max = 255;
	
	public RescaledRange(Monitors monitors, double min, double max) {
		super(monitors);
	}
	
	public void apply(RealSignal x) {
		if (monitors != null)
			monitors.log("Apply rescaled constraint (" + min + " ... " + max + ")");
		int nxy = x.nx * x.ny;
		float stats[] = x.getStats();
		if (stats[2] == stats[1])
			return;
		float a = (max-min) / (stats[2] - stats[1]);
		for(int k=0; k<x.nz; k++)
		for(int i=0; i<nxy; i++) {
			x.data[k][i] = a*(x.data[k][i] - stats[1]) + min;
		}
	}
}
