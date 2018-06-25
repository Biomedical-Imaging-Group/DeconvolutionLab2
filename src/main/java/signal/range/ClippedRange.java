package signal.range;

import deconvolutionlab.monitor.Monitors;
import signal.RealSignal;

public class ClippedRange extends AbstractRange {

	protected Monitors monitors;
	private float min = -Float.MAX_VALUE;
	private float max = Float.MAX_VALUE;
	
	public ClippedRange(Monitors monitors, RealSignal model) {
		super(monitors);
		if (model != null) {
			float stats[] = model.getStats();
			min = stats[1];
			max = stats[2];		
		}
	}

	public ClippedRange(Monitors monitors, float min, float max) {
		super(monitors);
		this.min = min;
		this.max = max;		
	}

	public void apply(RealSignal x) {
		if (monitors != null)
			monitors.log("Apply clipped constraint (" + min + " ..." + max + ")");
		int nxy = x.nx * x.ny;
		for(int k=0; k<x.nz; k++)
		for(int i=0; i<nxy; i++) {
			if (x.data[k][i] <= min)
				x.data[k][i] = min;
			if (x.data[k][i] >= max)
				x.data[k][i] = max;
		}
	}
}
