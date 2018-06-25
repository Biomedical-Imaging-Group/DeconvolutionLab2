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

package deconvolution.capsule;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import deconvolution.Deconvolution;
import deconvolutionlab.Lab;
import deconvolutionlab.monitor.Monitors;

public abstract class AbstractCapsule {

	protected Deconvolution		deconvolution;
	protected JSplitPane			split;
	private AsynchronousTimer	signalTimer;
	private Timer				timer	= new Timer();
	private int					count	= 0;
	private Monitors				monitors;
	private String				message	= "";

	public AbstractCapsule(Deconvolution deconvolution) {
		this.deconvolution = deconvolution;
		this.monitors = deconvolution.getMonitors();
	}

	public JSplitPane getPane() {
		return split;
	}

	public void show(String name) {
		JFrame frame = new JFrame(name);
		update();
		frame.getContentPane().add(split);
		frame.pack();
		Lab.setVisible(frame);
	}

	public abstract void update();

	public abstract String getID();

	public void stopAsynchronousTimer() {
		if (monitors != null)
			monitors.progress(message, 100);
		if (signalTimer != null) {
			signalTimer.cancel();
			signalTimer = null;
		}
	}

	public void startAsynchronousTimer(String message, long refreshTime) {
		if (signalTimer != null) {
			signalTimer.cancel();
			signalTimer = null;
		}
		this.message = message;
		signalTimer = new AsynchronousTimer(message);
		timer.schedule(signalTimer, 0, refreshTime);
	}

	public void signal(String message) {
		if (monitors != null)
			monitors.progress(message, count += 2);
	}

	private class AsynchronousTimer extends TimerTask {
		private String message;

		public AsynchronousTimer(String message) {
			this.message = message;
		}

		@Override
		public void run() {
			signal(message);
		}
	}

	@Override
	public void finalize() {
		stopAsynchronousTimer();
	}

}
