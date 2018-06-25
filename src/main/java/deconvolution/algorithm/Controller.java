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

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import bilib.tools.Files;
import bilib.tools.NumFormat;
import deconvolution.Stats;
import deconvolutionlab.Constants;
import deconvolutionlab.monitor.AbstractMonitor;
import deconvolutionlab.monitor.ConsoleMonitor;
import deconvolutionlab.monitor.Monitors;
import deconvolutionlab.monitor.TableMonitor;
import deconvolutionlab.monitor.Verbose;
import deconvolutionlab.output.Output;
import deconvolutionlab.system.SystemUsage;
import fft.AbstractFFT;
import fft.FFT;
import signal.Assessment;
import signal.ComplexSignal;
import signal.RealSignal;
import signal.apodization.Apodization;
import signal.padding.Padding;
import signal.range.AbstractRange;
import signal.range.ClippedRange;
import signal.range.IdentityRange;
import signal.range.NonNegativeRange;
import signal.range.NormalizedRange;
import signal.range.RescaledRange;

/**
 * This is an important class to manage all the common task of the algorithm.
 * The method start() is called before at the starting of the algorithm. The
 * method ends() is called at the end of every iterations for the iterative
 * algorithm. It returns true if one the stopping criteria is true. The method
 * finish() is called when the algorithm is completely terminated.
 * 
 * A timer is started to get the peak memory. 
 * 
 * @author Daniel Sage
 *
 */
public class Controller {

	private String				path;
	private boolean				system;
	private boolean				multithreading;
	private double				normalizationPSF;
	private double				epsilon;
	
	private Padding				padding;
	private Apodization			apodization;
	private ArrayList<Output>	outs;
	private Stats				stats;
	private Constraint			constraint;
	private AbstractRange		range;
	private double				residuMin;
	private double				timeLimit;
	private Monitors				monitors;
	private Verbose				verbose;
	private AbstractFFT			fft;

	private int					iterationsMax	= 100;

	private boolean				doResidu			= false;
	private boolean				doTime			= false;
	private boolean				doReference		= false;
	private boolean				doConstraint		= false;
	private boolean				abort			= false;

	private double				timeStarting	= 0;
	private double				memoryStarting	= 0;
	private double				residu			= 0;
	private int					iterations		= 0;
	private double				memoryPeak		= 0;
	private double				snr				= 0;
	private double				psnr				= 0;

	private RealSignal			refImage;
	private RealSignal			prevImage;
	private RealSignal			x;
	
	private Timer				timer;
	
	private String				algoName = "";

	/**
	 * Constructor.
	 * 
	 * One controller is always instantiated for every run of a algorithm.
	 */
	public Controller() {
		doResidu = false;
		doTime = false;
		doReference = false;
		doConstraint = false;
		timeStarting = System.nanoTime();
		
		setPath(Files.getWorkingDirectory());
		setSystem(true);
		setMultithreading(true);
		setFFT(FFT.getFastestFFT().getDefaultFFT());
		setNormalizationPSF(1);
		setEpsilon(1e-6);
		setPadding(new Padding());
		setApodization(new Apodization());

		monitors = new Monitors();
		monitors.add(new ConsoleMonitor());
		monitors.add(new TableMonitor(Constants.widthGUI, 240));

		setVerbose(Verbose.Log);
		setStats(new Stats(Stats.Mode.NO, "stats"));
		setConstraint(Constraint.NO);
		setResiduMin(-1);
		setTimeLimit(-1);
		setReference(null);
		setOuts(new ArrayList<Output>());
	}

	public void setAlgoName(String algoName) {
		this.algoName = algoName;
	}
	
	public void setFFT(AbstractFFT fft) {
		this.fft = fft;
	}

	public void abort() {
		this.abort = true;
	}

	public void setIterationsMax(int iterationsMax) {
		this.iterationsMax = iterationsMax;
	}

	public boolean needSpatialComputation() {
		return doConstraint || doResidu || doReference;
	}

	/**
	 * Call one time at the beginning of the algorithms
	 * 
	 * @param x
	 *            the input signal
	 */
	public void start(RealSignal x) {
		this.x = x;	
		
		stats.show();
		stats.addInput(x);
		
		iterations = 0;
		timer = new Timer();
		timer.schedule(new Updater(), 0, 100);
		timeStarting = System.nanoTime();
		memoryStarting = SystemUsage.getHeapUsed();
		
		doConstraint = true;
		if (constraint == Constraint.CLIPPED)
			range = new ClippedRange(monitors, x);
		else if (constraint == Constraint.NORMALIZED)
			range = new NormalizedRange(monitors, x);
		else if (constraint == Constraint.RESCALED)
			range = new RescaledRange(monitors, 0, 255);
		else if (constraint == Constraint.NONNEGATIVE)
			range = new NonNegativeRange(monitors);
		else {
			range = new IdentityRange(monitors);
			doConstraint = false;
		}
		
		if (doResidu)
			prevImage = x.duplicate();
	}

	public boolean ends(ComplexSignal X) {

		boolean out = false;
		for (Output output : outs)
			out = out | output.is(iterations);

		if (doConstraint || doResidu || doReference || out) {
			if (fft == null)
				fft = FFT.createDefaultFFT(monitors, X.nx, X.ny, X.nz);
			x = fft.inverse(X, x);
			return ends(x);
		}

		return ends((RealSignal) null);
	}

	public boolean ends(RealSignal x) {
		this.x = x;

		if (doConstraint || doResidu || doReference)
			compute(iterations, x, doConstraint, doResidu, doReference);

		for (Output out : outs)
			out.executeIterative(monitors, x, this, iterations);

		iterations++;
		double p = iterations * 100.0 / iterationsMax;
		monitors.progress("Iterative " + iterations + "/" + iterationsMax, p);
		double timeElapsed = getTimeSecond();
		boolean stopIter = (iterations > iterationsMax);
		boolean stopTime = doTime && (timeElapsed >= timeLimit);
		boolean stopResd = doResidu && (residu <= residuMin) && (iterations > 1);
		monitors.log("@" + iterations + " Time: " + NumFormat.seconds(timeElapsed*1e9));

		double pnsrText = doReference ? psnr : Double.NEGATIVE_INFINITY;
		double snrText = doReference ? snr : Double.NEGATIVE_INFINITY;
		//String residuText = doResidu ? "" + residu : "n/a";
		double residuText = residu;
		stats.add(x, ""+iterations, NumFormat.seconds(getTimeNano()), pnsrText, snrText, residuText);
		
		String prefix = "Stopped>> by ";
		if (abort)
			monitors.log(prefix + "abort");
		if (stopIter)
			monitors.log(prefix + "iteration " + iterations + " > " + iterationsMax);
		if (stopTime)
			monitors.log(prefix + "time " + timeElapsed + " > " + timeLimit);
		if (stopResd)
			monitors.log(prefix + "residu " + NumFormat.nice(residu) + " < " + NumFormat.nice(residuMin));

		return abort | stopIter | stopTime | stopResd;
	}

	public void finish(RealSignal x) {
		this.x = x;

		if (doReference || doConstraint || doResidu)
			compute(iterations, x, doConstraint, doResidu, doReference);

		double pnsrText = doReference ? psnr : Double.NEGATIVE_INFINITY;
		double snrText = doReference ? snr : Double.NEGATIVE_INFINITY;
		//String residuText = doResidu ? "" + residu : "n/a";
		double residuText = residu;
		stats.addOutput(x, algoName, NumFormat.seconds(getTimeNano()), pnsrText, snrText, residuText);
		
		stats.save(monitors, path);
		
		for (Output out : outs)
			out.executeFinal(monitors, x, this);

		monitors.log("Time: " + NumFormat.seconds(getTimeNano()) + " Peak:" + getMemoryAsString());
		if (timer != null)
			timer.cancel();
	}

	private void compute(int iterations, RealSignal x, boolean con, boolean res, boolean ref) {
		if (x == null)
			return;
		
		if (range != null)
			range.apply(x);
		
		if (ref && refImage != null) {
			String s = "";
			psnr = Assessment.psnr(x, refImage);
			snr = Assessment.snr(x, refImage);
			s += " PSNR: " + NumFormat.nice(psnr);
			s += " SNR: " + NumFormat.nice(snr);
			monitors.log("@" + iterations + " " + s);
		}

		residu = Double.MAX_VALUE;
		if (prevImage != null) {
			residu = Assessment.relativeResidu(x, prevImage);
			prevImage = x.duplicate();
			monitors.log("@" + iterations + " Residu: " + NumFormat.nice(residu));
		}
	}

	public double getTimeNano() {
		return (System.nanoTime() - timeStarting);
	}
	
	public double getTimeSecond() {
		return (System.nanoTime() - timeStarting) * 1e-9;
	}

	public String getConstraintAsString() {
		if (!doConstraint)
			return "no";
		return constraint.name().toLowerCase();
	}

	public String getStoppingCriteriaAsString(Algorithm algo) {
		String stop = algo.isIterative() ? "iterations limit=" + algo.getIterationsMax() + ", " : "direct, ";
		stop += doTime ? ", time limit=" + NumFormat.nice(timeLimit * 1e-9) : " no time limit" + ", ";
		stop += doResidu ? ", residu limit=" + NumFormat.nice(residuMin) : " no residu limit";
		return stop;
	}

	public double getMemory() {
		return memoryPeak - memoryStarting;
	}

	public String getMemoryAsString() {
		return NumFormat.bytes(getMemory());
	}

	public int getIterations() {
		return iterations;
	}
	
	public double getSNR() {
		return snr;
	}

	public double getPSNR() {
		return psnr;
	}

	public double getResidu() {
		return residu;
	}

	private void update() {
		memoryPeak = Math.max(memoryPeak, SystemUsage.getHeapUsed());
	}

	public AbstractFFT getFFT() {
		return fft;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the system
	 */
	public boolean isSystem() {
		return system;
	}

	/**
	 * @param system
	 *            the system to set
	 */
	public void setSystem(boolean system) {
		this.system = system;
	}

	/**
	 * @return the multithreading
	 */
	public boolean isMultithreading() {
		return multithreading;
	}

	/**
	 * @param multithreading
	 *            the multithreading to set
	 */
	public void setMultithreading(boolean multithreading) {
		this.multithreading = multithreading;
	}

	/**
	 * @return the normalizationPSF
	 */
	public double getNormalizationPSF() {
		return normalizationPSF;
	}

	/**
	 * @param normalizationPSF
	 *            the normalizationPSF to set
	 */
	public void setNormalizationPSF(double normalizationPSF) {
		this.normalizationPSF = normalizationPSF;
	}

	/**
	 * @return the epsilon
	 */
	public double getEpsilon() {
		return epsilon;
	}

	/**
	 * @param epsilon
	 *            the epsilon to set
	 */
	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	/**
	 * @return the padding
	 */
	public Padding getPadding() {
		return padding;
	}

	/**
	 * @param padding
	 *            the padding to set
	 */
	public void setPadding(Padding padding) {
		this.padding = padding;
	}

	/**
	 * @return the apodization
	 */
	public Apodization getApodization() {
		return apodization;
	}

	/**
	 * @param apodization
	 *            the apodization to set
	 */
	public void setApodization(Apodization apodization) {
		this.apodization = apodization;
	}

	/**
	 * @return the monitors
	 */
	public Monitors getMonitors() {
		if (monitors == null)
			return Monitors.createDefaultMonitor();
		return monitors;
	}

	/**
	 * @param monitors
	 *            the monitors to set
	 */
	public void setMonitors(Monitors monitors) {
		this.monitors = monitors;
	}

	/**
	 * @return the verbose
	 */
	public Verbose getVerbose() {
		return verbose;
	}

	/**
	 * @param verbose
	 *            the verbose to set
	 */
	public void setVerbose(Verbose verbose) {
		this.verbose = verbose;
	}

	public Constraint getConstraint() {
		return constraint;
	}

	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}

	/**
	 * @return the stats
	 */
	public Stats getStats() {
		return stats;
	}

	/**
	 * @param stats
	 *            the stats to set
	 */
	public void setStats(Stats stats) {
		this.stats = stats;
	}

	/**
	 * @return the residuMin
	 */
	public double getResiduMin() {
		return residuMin;
	}

	/**
	 * @param residuMin
	 *            the residuMin to set
	 */
	public void setResiduMin(double residuMin) {
		doResidu = residuMin > 0;
		this.residuMin = residuMin;
	}

	/**
	 * @return the timeLimit
	 */
	public double getTimeLimit() {
		return timeLimit;
	}

	/**
	 * @param timeLimit
	 *            the timeLimit to set
	 */
	public void setTimeLimit(double timeLimit) {
		doTime = timeLimit > 0;
		this.timeLimit = timeLimit;
	}

	/**
	 * @return the reference
	 */
	public RealSignal getReference() {
		return refImage;
	}

	/**
	 * @param reference
	 *            the reference to set
	 */
	public void setReference(RealSignal refImage) {
		doReference = false;
		if (refImage == null)
			return;
		doReference = true;
		this.refImage = refImage;
	}

	/**
	 * @return the outs
	 */
	public ArrayList<Output> getOuts() {
		return outs;
	}

	/**
	 * @param outs
	 *            the outs to set
	 */
	public void setOuts(ArrayList<Output> outs) {
		this.outs = outs;
	}

	public void addOutput(Output out) {
		this.outs.add(out);
	}

	public String toStringMonitor() {
		String s = "[" + verbose.name().toLowerCase() + "] ";
		for (AbstractMonitor monitor : monitors) {
			s += "" + monitor.getName() + " ";
		}
		return s;
	}

	public Stats.Mode getStatsMode() {
		return stats.getMode();
	}

	public void setStatsMode(Stats.Mode mode, String name) {
		this.stats = new Stats(mode, name);
	}

	public String toStringPath() {
		File dir = new File(path);
		if (dir.exists()) {
			if (dir.isDirectory()) {
				if (dir.canWrite())
					return path + " (writable)";
				else
					return path + " (non-writable)";
			}
			else {
				return path + " (non-directory)";
			}
		}
		else {
			return path + " (not-valid)";
		}
	}

	private class Updater extends TimerTask {
		@Override
		public void run() {
			update();
		}
	}
}
