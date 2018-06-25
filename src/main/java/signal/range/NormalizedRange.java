package signal.range;

import deconvolutionlab.monitor.Monitors;
import signal.RealSignal;

public class NormalizedRange extends AbstractRange {

	protected Monitors monitors;
	private float mean = 0.0f;
	private float stdev = 1.0f;
	
	public NormalizedRange(Monitors monitors, RealSignal model) {
		super(monitors);
		if (model != null) {
			float stats[] = model.getStats();
			mean = stats[0];
			stdev = stats[3];		
		}
	}

	public NormalizedRange(Monitors monitors, float mean, float stdev) {
		super(monitors);
		this.mean = mean;
		this.stdev = stdev;
	}

	public  void apply(RealSignal x) {
		if (monitors != null)
			monitors.log("Apply normalized constraint (" + mean + ", " + stdev + ")");
		float stats[] = x.getStats();
		int nxy = x.nx * x.ny;
		for(int k=0; k<x.nz; k++)
		for(int i=0; i<nxy; i++) {
			float d = (x.data[k][i] - stats[0]) / stats[3];
			x.data[k][i] = d * stdev + mean;
		}		
	}
}
