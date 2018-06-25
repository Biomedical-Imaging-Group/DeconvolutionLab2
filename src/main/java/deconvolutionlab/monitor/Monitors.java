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

package deconvolutionlab.monitor;

import java.util.ArrayList;

public class Monitors extends ArrayList<AbstractMonitor> {

	private double	start;
	private Verbose	verbose;
	private double	progress;

	public Monitors() {
		super();
		start = System.nanoTime();
		verbose = Verbose.Log;
		progress = 0;
	}

	public void setVerbose(Verbose v) {
		verbose = v;
	}

	public Verbose getVerbose() {
		return verbose;
	}

	public static Monitors createDefaultMonitor() {
		Monitors monitors = new Monitors();
		monitors.add(new ConsoleMonitor());
		return monitors;
	}

	public static Monitors create(AbstractMonitor monitor) {
		Monitors monitors = new Monitors();
		monitors.add(new ConsoleMonitor());
		monitors.add(monitor);
		return monitors;
	}
	
	public void detail(String msg) {
		if (verbose.ordinal() >= Verbose.Prolix.ordinal())
			sendMessage(new Message(Verbose.Prolix, msg, System.nanoTime() - start, 0));
	}

	public void progress(String msg, double p) {
		progress = Math.max(0, Math.min(100, p));
		sendProgress(new Message(Verbose.Prolix, msg, System.nanoTime() - start, progress));
	}

	public void log(String msg) {
		if (verbose.ordinal() >=  Verbose.Log.ordinal())
			sendMessage(new Message(Verbose.Log, msg, System.nanoTime() - start, progress));
	}

	public void error(String msg) {
		if (verbose.ordinal() >=  Verbose.Quiet.ordinal()) 
			sendMessage(new Message(Verbose.Quiet, msg, System.nanoTime() - start, 0));
	}

	private void sendMessage(Message message) {
		for (AbstractMonitor monitor : this)
			monitor.add(message);
	}
	
	private void sendProgress(Message message) {
		for (AbstractMonitor monitor : this)
			if (monitor instanceof StatusMonitor)
				monitor.add(message);
	}
}
