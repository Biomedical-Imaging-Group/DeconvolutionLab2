package signal.range;

import deconvolutionlab.monitor.Monitors;
import signal.RealSignal;

public class IdentityRange extends AbstractRange {

	protected Monitors monitors;
	
	public IdentityRange(Monitors monitors) {
		super(monitors);
	}
	
	public void apply(RealSignal x) {

	}
}
