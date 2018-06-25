package signal.range;

import deconvolutionlab.monitor.Monitors;
import signal.RealSignal;

public abstract class AbstractRange {

	protected Monitors monitors;
	
	public AbstractRange(Monitors monitors) {
		this.monitors = monitors;
	}
	
	public abstract void apply(RealSignal x);
	
	public RealSignal process(RealSignal x) {
		if (x == null)
			return null;
		RealSignal y = x.duplicate();
		apply(y);
		return y;
		
	}
}
