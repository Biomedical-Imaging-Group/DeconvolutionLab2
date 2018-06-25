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

package deconvolution.algorithm;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import bilib.tools.NumFormat;
import deconvolution.Stats;
import deconvolutionlab.Lab;
import deconvolutionlab.Platform;
import deconvolutionlab.monitor.Monitors;
import deconvolutionlab.monitor.Verbose;
import deconvolutionlab.output.Output;
import fft.AbstractFFT;
import fft.FFT;
import signal.RealSignal;
import signal.SignalCollector;
import signal.apodization.Apodization;
import signal.padding.Padding;

/**
 * This class is the common part of every algorithm of deconvolution.
 * 
 * @author Daniel Sage
 * 
 */
public abstract class Algorithm implements Callable<RealSignal> {

	/** y is the input signal of the deconvolution. */
	protected RealSignal			y;

	/** h is the PSF signal for the deconvolution. */
	protected RealSignal			h;

	protected boolean				threaded;

	/** Optimized implementation in term of memory footprint */
	protected boolean				optimizedMemoryFootprint;

	protected int iterMax = 0;
	
	protected AbstractFFT fft;
	protected Controller controller;
	
	public Algorithm() {
		setController(new Controller());
		optimizedMemoryFootprint = true;
		threaded = true;
		fft = FFT.getFastestFFT().getDefaultFFT();
	}
	
	public Algorithm(Controller controller) {
		this.controller = controller;
		optimizedMemoryFootprint = true;
		threaded = true;
		fft = FFT.getFastestFFT().getDefaultFFT();
	}

	public void setOptimizedMemoryFootprint(boolean optimizedMemoryFootprint) {
		this.optimizedMemoryFootprint = optimizedMemoryFootprint;
	}

	public abstract String getName();
	public abstract String[] getShortnames();
	public abstract double getMemoryFootprintRatio();
	public abstract int getComplexityNumberofFFT();
	public abstract boolean isRegularized();
	public abstract boolean isStepControllable();
	public abstract boolean isIterative();
	public abstract boolean isWaveletsBased();
	public abstract Algorithm setParameters(double... params);
	public abstract double getRegularizationFactor();
	public abstract double getStepFactor();
	public abstract double[] getParameters();

	public abstract double[] getDefaultParameters();

	public RealSignal run(RealSignal image, RealSignal psf, RealSignal ref) {
		if (ref!=null)
			setReference(ref);
		return run(image, psf);
	}

	public RealSignal run(RealSignal image, RealSignal psf) {

		String sn = getShortnames()[0];
		String algoParam = sn + "(" + getParametersAsString() + ")";
		//if (controller.isSystem())
		//	SystemInfo.activate();

		Padding pad = controller.getPadding();
		Apodization apo = controller.getApodization();
		double norm = controller.getNormalizationPSF();
		
		controller.setAlgoName(algoParam);
		fft = controller.getFFT();
		controller.setIterationsMax(iterMax);

		if (image == null)
			return null;
		
		if (psf == null)
			return null;
		// Prepare the controller and the outputs
		Monitors monitors = controller.getMonitors();
		monitors.setVerbose(controller.getVerbose());
		monitors.log("Path: " + controller.toStringPath());
		monitors.log("Algorithm: " + getName());
		
		// Prepare the signal and the PSF
		y = pad.pad(monitors, image);
		y.setName("y");
		apo.apodize(monitors, y);
		monitors.log("Input: " + y.dimAsString());
		h = psf.changeSizeAs(y);
		h.setName("h");
		h.normalize(norm);
		monitors.log("PSF: " + h.dimAsString() + " normalized " + (norm <= 0 ? "no" : norm));

		String iterations = (isIterative() ? iterMax + " iterations" : "direct");

		controller.setIterationsMax(iterMax);
		
		monitors.log(sn + " is starting (" + iterations + ")");
		controller.setMonitors(monitors);

		controller.start(y);
		h.circular();

		// FFT
		fft.init(monitors, y.nx, y.ny, y.nz);
		controller.setFFT(fft);
		
		monitors.log(sn + " data ready");
		monitors.log(algoParam);

		RealSignal x = null;

		try {
			if (threaded == true) {
				ExecutorService pool = Executors.newSingleThreadExecutor();
				Future<RealSignal> future = pool.submit(this);
				x = future.get();
			}
			else {
				x = call();
			}
		}
		catch (InterruptedException ex) {
			ex.printStackTrace();
			x = y.duplicate();
		}
		catch (ExecutionException ex) {
			ex.printStackTrace();
			x = y.duplicate();
		}
		catch (Exception e) {
			e.printStackTrace();
			x = y.duplicate();
		}
		SignalCollector.free(y);
		SignalCollector.free(h);
		x.setName("x");
		RealSignal result = pad.crop(monitors, x);
		
		controller.finish(result);
		monitors.log(getName() + " is finished");

		SignalCollector.free(x);
		
		if (controller.getOuts().size() == 0)
		if (Lab.getPlatform() == Platform.IMAGEJ || Lab.getPlatform() == Platform.ICY)
			Lab.show(monitors, result, "Final Display of " + sn);

		result.setName("Out of " + algoParam);
		
		monitors.log("End of " + sn + " in " + NumFormat.seconds(controller.getTimeNano()) + " and " + controller.getMemoryAsString());

		return result;
	}

	public Algorithm setController(Controller controller) {
		this.controller = controller;
		return this;
	}

	public Controller getController() {
		return controller;
	}

	public int getIterationsMax() {
		return iterMax;
	}

	public int getIterations() {
		return controller.getIterations();
	}

	public double getTime() {
		return controller.getTimeNano();
	}

	public double getMemory() {
		return controller.getMemory();
	}
	
	public double getResidu() {
		return controller.getResidu();	
	}
	
	public double getSNR() {
		return controller.getSNR();	
	}

	public double getPSNR() {
		return controller.getPSNR();	
	}

	public void setWavelets(String waveletsName) {
	}

	@Override
	public String toString() {
		String s = "";
		s += getName();
		s += (isIterative() ? ", " + iterMax + " iterations" : " (direct)");
		s += (isRegularized() ? ", &lambda;=" + NumFormat.nice(getRegularizationFactor()) : "");
		s += (isStepControllable() ? ", &gamma;=" + NumFormat.nice(getStepFactor()) : "");
		return s;
	}

	public String getParametersAsString() {
		double p[] = getParameters();
		String param = "";
		for (int i = 0; i < p.length; i++)
			if (i == p.length - 1)
				param += NumFormat.nice(p[i]);
			else
				param += NumFormat.nice(p[i]) + ", ";
		return param;
	}

	public AbstractFFT getFFT() {
		return controller.getFFT();
	}

	public Algorithm setFFT(AbstractFFT fft) {
		this.fft = fft;
		controller.setFFT(fft);
		return this;
	}

	public String getPath() {
		return controller.getPath();
	}

	public Algorithm setPath(String path) {
		controller.setPath(path);
		return this;
	}

	public boolean isSystem() {
		return controller.isSystem();
	}

	public Algorithm enableSystem() {
		controller.setSystem(true);
		return this;
	}

	public Algorithm disableSystem() {
		controller.setSystem(false);
		return this;
	}

	public boolean isMultithreading() {
		return controller.isMultithreading();
	}

	public Algorithm enableMultithreading() {
		controller.setMultithreading(true);
		return this;
	}

	public Algorithm disableMultithreading() {
		controller.setMultithreading(false);
		return this;
	}

	public double getNormalizationPSF() {
		return controller.getNormalizationPSF();
	}

	public Algorithm setNormalizationPSF(double normalizationPSF) {
		controller.setNormalizationPSF(normalizationPSF);
		return this;
	}

	public double getEpsilon() {
		return controller.getEpsilon();
	}

	public Algorithm setEpsilon(double epsilon) {
		controller.setEpsilon(epsilon);
		return this;
	}

	public Padding getPadding() {
		return controller.getPadding();
	}

	public Algorithm setPadding(Padding padding) {
		controller.setPadding(padding);
		return this;
	}

	public Apodization getApodization() {
		return controller.getApodization();
	}

	public Algorithm setApodization(Apodization apodization) {
		controller.setApodization(apodization);
		return this;
	}

	public Monitors getMonitors() {
		return controller.getMonitors();
	}

	public Algorithm setMonitors(Monitors monitors) {
		controller.setMonitors(monitors);
		return this;
	}

	public Verbose getVerbose() {
		return controller.getVerbose();
	}

	public Algorithm setVerbose(Verbose verbose) {
		controller.setVerbose(verbose);
		return this;
	}

	public Constraint getConstraint() {
		return controller.getConstraint();
	}

	public Algorithm setConstraint(Constraint constraint) {
		controller.setConstraint(constraint);
		return this;
	}

	public Stats getStats() {
		return controller.getStats();
	}

	public Algorithm setStats(Stats stats) {
		controller.setStats(stats);
		return this;
	}
	
	public Algorithm showStats() {
		controller.setStats(new Stats(Stats.Mode.SHOW, "stats"));
		return this;
	}
	
	public Algorithm saveStats(Stats stats) {
		controller.setStats(new Stats(Stats.Mode.SAVE, "stats"));
		return this;
	}
	
	public Algorithm setStats() {
		controller.setStats(new Stats(Stats.Mode.SHOWSAVE, "stats"));
		return this;
	}

	public Algorithm showStats(String name) {
		controller.setStats(new Stats(Stats.Mode.SHOW, name));
		return this;
	}
	
	public Algorithm saveStats(Stats stats, String name) {
		controller.setStats(new Stats(Stats.Mode.SAVE, name));
		return this;
	}
	
	public Algorithm setStats(String name) {
		controller.setStats(new Stats(Stats.Mode.SHOWSAVE, name));
		return this;
	}

	public double getResiduMin() {
		return controller.getResiduMin();
	}

	public Algorithm setResiduMin(double residuMin) {
		controller.setResiduMin(residuMin);
		return this;
	}

	public double getTimeLimit() {
		return controller.getTimeLimit();
	}

	public Algorithm setTimeLimit(double timeLimit) {
		controller.setTimeLimit(timeLimit);
		return this;
	}

	public RealSignal getReference() {
		return controller.getReference();
	}

	public Algorithm setReference(RealSignal ref) {
		controller.setReference(ref);
		return this;
	}

	public ArrayList<Output> getOuts() {
		return controller.getOuts();
	}

	public Algorithm setOuts(ArrayList<Output> outs) {
		controller.setOuts(outs);
		return this;
	}

	public Algorithm addOutput(Output out) {
		controller.addOutput(out);
		return this;
	}
	
	public String getParametersToString() {
		double params[] = getParameters();
		if (params != null) {
			if (params.length > 0) {
				String s = " ";
				for (double param : params)
					s += NumFormat.nice(param) + " ";
				return s;
			}
		}
		return "parameter-free";
	}
}
