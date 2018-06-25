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

package deconvolutionlab;

import ij.gui.Plot;
import imagej.IJImager;

import java.awt.geom.Line2D;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import plugins.sage.deconvolutionlab.IcyImager;
import signal.ComplexComponent;
import signal.ComplexSignal;
import signal.RealSignal;
import signal.factory.SignalFactory;
import signal.factory.Sphere;
import bilib.tools.Files;
import bilib.tools.NumFormat;
import bilib.tools.WebBrowser;
import deconvolutionlab.Imager.ContainerImage;
import deconvolutionlab.monitor.Monitors;
import deconvolutionlab.output.Output;
import fft.AbstractFFT;
import fft.AbstractFFTLibrary;
import fft.FFT;

/**
 * This class contains a collection of useful static methods to manage all the
 * peripherical aspects of the deconvolution, such as load, display, or save an
 * image.
 * <p>
 * At the construction of the class, the config is loaded. In practice, any
 * deconvolution program has to start with Lab.init(Platform).
 * 
 * @author Daniel Sage
 *
 */

public class Lab {

	private static Platform				platform;
	private static Imager				imaging;
	private static ArrayList<JFrame>		frames;
	private static ArrayList<JDialog>	dialogs;

	static {
		frames = new ArrayList<JFrame>();
		dialogs = new ArrayList<JDialog>();
		imaging = new IJImager();
		platform = Platform.IMAGEJ;
		Config.init(Files.getWorkingDirectory() + "DeconvolutionLab2.config");
	}

	/**
	 * Initializes the Lab with a give platform.
	 * 
	 * @param platform
	 *            The platform is ImageJ, ICY, Standalone, or Matlab.
	 */
	public static void init(Platform platform) {
		init(platform, Files.getWorkingDirectory() + "DeconvolutionLab2.config");
	}

	/**
	 * Initializes the Lab with a give platform and a given configuration file
	 * 
	 * @param platform
	 * @param configFilename
	 */
	public static void init(Platform platformInit, String configFilename) {
		platform = platformInit;
		switch (platform) {
		case IMAGEJ:
			imaging = new IJImager();
			break;
		case ICY:
			imaging = new IcyImager();
			break;
		case MATLAB:
			imaging = new IJImager();
			break;
		case STANDALONE:
			imaging = new IJImager();
			break;
		default: 
			imaging = new IJImager();
			break;
		}
		Config.init(configFilename);
	}

	/**
	 * Returns the platform.
	 * 
	 * @return
	 */
	public static Platform getPlatform() {
		return platform;
	}

	/**
	 * Open a web page on the DeconvolutionLab2.
	 */
	public static void help() {
		WebBrowser.open(Constants.url);
	}

	/**
	 * Checks the installed FFT libraries on a small (40, 30, 20) signal.
	 * 
	 * @param monitors
	 */
	public static void checkFFT(Monitors monitors) {
		ArrayList<AbstractFFTLibrary> libraries = FFT.getInstalledLibraries();
		for (AbstractFFTLibrary library : libraries) {
			RealSignal y = new Sphere(3, 1).generate(40, 30, 20);
			double chrono = System.nanoTime();
			AbstractFFT fft = library.getDefaultFFT();
			fft.init(monitors, y.nx, y.ny, y.nz);
			RealSignal x = fft.inverse(fft.transform(y));
			chrono = System.nanoTime() - chrono;
			double residu = y.getEnergy() - x.getEnergy();
			monitors.log("\t residu of reconstruction: " + residu);
			monitors.log("\t computation time (" + x.nx + "x" + x.ny + "x" + x.nz + ") " + NumFormat.time(chrono));
		}
	}

	public static ContainerImage createContainer(Monitors monitors, String title) {
		monitors.log("Create Live Real Signal " + title);
		return imaging.createContainer(title);
	}

	public static void append(Monitors monitors, ContainerImage container, RealSignal signal, String title) {
		imaging.append(container, signal, title, Imager.Type.FLOAT);
		monitors.log("Add Live Real Signal " + title);
	}
	
	public static void append(Monitors monitors, ContainerImage container, RealSignal signal, String title, Imager.Type type) {
		imaging.append(container, signal, title, type);
		monitors.log("Add Live Real Signal " + title);
	}
	
	public static void append(Monitors monitors, ContainerImage container, RealSignal signal, String title, Imager.Type type, ArrayList<Line2D.Double> overlayLines) {
		imaging.append(container, signal, title, type, overlayLines);
		monitors.log("Add Live Real Signal " + title);
	}


	/**
	 * Displays a the module of complex signal.
	 * 
	 * @param monitors
	 * @param signal
	 * @param title
	 */
	public static void show(Monitors monitors, ComplexSignal signal, String title) {
		if (signal == null) {
			monitors.error("Show " + title + " this image does not exist.");
			return;
		}
		monitors.log("Show Real Signal " + title);
		imaging.show(signal, title, ComplexComponent.MODULE);
	}

	/**
	 * Displays a real 3D signal a z-stack of images.
	 * 
	 * @param signal
	 */
	public static void show(RealSignal signal) {
		if (signal == null) {
			return;
		}
		imaging.show(signal, signal.name, Imager.Type.FLOAT, signal.nz / 2);
	}

	/**
	 * Displays a real 3D signal a z-stack of images.
	 * 
	 * @param signal
	 */
	public static void show(RealSignal signal, String title) {
		if (signal == null) {
			return;
		}
		imaging.show(signal, title, Imager.Type.FLOAT, signal.nz / 2);
	}

	/**
	 * Displays a real 3D signal a z-stack of images.
	 * 
	 * @param monitors
	 * @param signal
	 */
	public static void show(Monitors monitors, RealSignal signal) {
		if (signal == null) {
			monitors.error("This image does not exist.");
			return;
		}
		monitors.log("Show Real Signal " + signal.name);
		imaging.show(signal, signal.name, Imager.Type.FLOAT, signal.nz / 2);
	}

	/**
	 * Displays a real 3D signal a z-stack of images.
	 * 
	 * @param monitors
	 * @param signal
	 * @param title
	 */
	public static void show(Monitors monitors, RealSignal signal, String title) {
		if (signal == null) {
			monitors.error("Show " + title + " this image does not exist.");
			return;
		}
		monitors.log("Show Real Signal " + title);
		imaging.show(signal, title, Imager.Type.FLOAT, signal.nz / 2);
	}

	/**
	 * Displays a real 3D signal a z-stack of images using a given type.
	 * 
	 * @param monitors
	 * @param signal
	 * @param title
	 * @param type
	 */
	public static void show(Monitors monitors, RealSignal signal, String title, Imager.Type type) {
		if (signal == null) {
			monitors.error("Show " + title + " this image does not exist.");
			return;
		}
		monitors.log("Show Real Signal " + title);
		imaging.show(signal, title, type, signal.nz / 2);
	}

	/**
	 * Displays a real 3D signal a z-stack of images using a given type and shows
	 * the slice number z.
	 * 
	 * @param monitors
	 * @param signal
	 * @param title
	 * @param type
	 * @param z
	 */
	public static void show(Monitors monitors, RealSignal signal, String title, Imager.Type type, int z) {
		if (signal == null) {
			monitors.error("Show " + title + " this image does not exist.");
			return;
		}
		monitors.log("Show Real Signal " + title);
		imaging.show(signal, title, type, z);
	}
	
	public static void show(Monitors monitors, RealSignal signal, String title, Imager.Type type, int z, ArrayList<Line2D.Double> overlayLines) {
		if (signal == null) {
			monitors.error("Show " + title + " this image does not exist.");
			return;
		}
		monitors.log("Show Real Signal " + title);
		imaging.show(signal, title, type, z, overlayLines);
	}

	public static void save(Monitors monitors, RealSignal signal, String path, String name) {
		save(monitors, signal, path + File.separator + name + ".tif", Imager.Type.FLOAT);
	}

	public static void save(Monitors monitors, RealSignal signal, String path, String name, Imager.Type type) {
		save(monitors, signal, path + File.separator + name + ".tif", type);
	}

	public static void save(Monitors monitors, RealSignal signal, String filename, Imager.Type type) {
		imaging.save(signal, filename, type);
		monitors.log("Save Real Signal " + filename);
	}

	public static void save(Monitors monitors, RealSignal signal, String filename) {
		imaging.save(signal, filename, Imager.Type.FLOAT);
		monitors.log("Save Real Signal " + filename);
	}

	public static void save(RealSignal signal, String path, String name) {
		save(signal, path + File.separator + name + ".tif", Imager.Type.FLOAT);
	}

	public static void save(RealSignal signal, String path, String name, Imager.Type type) {
		save(signal, path + File.separator + name + ".tif", type);
	}

	public static void save(RealSignal signal, String filename) {
		imaging.save(signal, filename, Imager.Type.FLOAT);
	}

	public static void save(RealSignal signal, String filename, Imager.Type type) {
		imaging.save(signal, filename, type);
	}

	public static RealSignal createSynthetic(Monitors monitors, String cmd) {
		RealSignal signal = SignalFactory.createFromCommand(cmd);
		if (signal == null)
			monitors.error("Unable to create " + cmd);
		else
			monitors.log("Create " + cmd);
		return signal;
	}

	/**
	 * Return the active image.
	 * 
	 * @return
	 */
	public static RealSignal getImage() {
		return getImager().getActiveImage();
	}

	/**
	 * Return an image from the platform with a specified name.
	 * 
	 * @param name
	 * @return
	 */
	public static RealSignal getImage(String name) {
		return getImager().getImageByName(name);
	}

	/**
	 * Return an image from the platform with a specified name.
	 * 
	 * @param monitors
	 * @param name
	 * @return
	 */
	public static RealSignal getImage(Monitors monitors, String name) {
		RealSignal signal = getImager().getImageByName(name);
		if (signal == null)
			monitors.error("Unable to get " + name);
		else
			monitors.log("Load " + name);
		return signal;
	}

	/**
	 * Open an image from the disk.
	 * 
	 * @param filename
	 * @return
	 */
	public static RealSignal openFile(String filename) {
		return imaging.open(filename);
	}

	/**
	 * Open an image from the disk.
	 * 
	 * @param monitors
	 * @param filename
	 * @return
	 */
	public static RealSignal openFile(Monitors monitors, String filename) {
		RealSignal signal = imaging.open(filename);
		if (signal == null)
			monitors.error("Unable to open " + filename);
		else
			monitors.log("Load " + filename);
		return signal;
	}

	/**
	 * Open a series of image from a directory.
	 * 
	 * @param path
	 * @return
	 */
	public static RealSignal openDir(String path) {
		return openDir(Monitors.createDefaultMonitor(), path);
	}

	/**
	 * Open a series of image from a directory.
	 * 
	 * @param monitors
	 * @param path
	 * @return
	 */
	public static RealSignal openDir(Monitors monitors, String path) {
		String parts[] = path.split(" pattern ");
		String dirname = path;
		String regex = "";
		if (parts.length == 2) {
			dirname = parts[0].trim();
			regex = parts[1].trim();
		}
		File file = new File(dirname + File.separator);
		if (!file.isDirectory()) {
			monitors.error("Dir " + dirname + " is not a directory.");
			return null;
		}
		String[] list = file.list();
		ArrayList<RealSignal> slices = new ArrayList<RealSignal>();
		int nx = 0;
		int ny = 0;
		Pattern pattern = Pattern.compile(regex);
		for (String filename : list) {
			if (pattern.matcher(filename).find()) {
				RealSignal slice = imaging.open(dirname + File.separator + filename);
				if (slice != null) {
					slices.add(slice);
					nx = Math.max(nx, slice.nx);
					ny = Math.max(ny, slice.ny);
					monitors.log("Image " + path + File.separator + filename + " is loaded.");
				}
			}
			else {
				monitors.error("Error in loading image " + path + File.separator + filename);
			}
		}
		int nz = slices.size();
		if (nz <= 0) {
			monitors.error("Dir " + path + " do no contain valid images.");
			return null;
		}
		RealSignal signal = new RealSignal(file.getName(), nx, ny, nz);
		for (int z = 0; z < slices.size(); z++)
			signal.setSlice(z, slices.get(z));
		return signal;
	}

	public static void showOrthoview(RealSignal signal, int hx, int hy, int hz) {
		showOrthoview(signal, signal.name, hx, hy, hz);
	}

	public static void showOrthoview(RealSignal signal, String title, int hx, int hy, int hz) {
		if (signal == null) {
			return;
		}
		ArrayList<Line2D.Double> lines = Output.createOverlayXYZ(signal);
		imaging.show(signal.createOrthoview(hx, hy, hz), title, Imager.Type.FLOAT, 0, lines);
	}

	public static void showOrthoview(Monitors monitors, RealSignal signal, String title, int hx, int hy, int hz) {
		if (signal == null) {
			monitors.error("Show Orthoview " + title + " this image does not exist.");
			return;
		}
		ArrayList<Line2D.Double> lines = Output.createOverlayXYZ(signal);
		imaging.show(signal.createOrthoview(hx, hy, hz), title, Imager.Type.FLOAT, 0, lines);
	}

	public static void showOrthoview(RealSignal signal) {
		showOrthoview(signal, signal.name);
	}

	public static void showOrthoview(RealSignal signal, String title) {
		if (signal == null) {
			return;
		}
		int hx = signal.nx / 2;
		int hy = signal.ny / 2;
		int hz = signal.nz / 2;
		ArrayList<Line2D.Double> lines = Output.createOverlayXYZ(signal);
		imaging.show(signal.createOrthoview(hx, hy, hz), title, Imager.Type.FLOAT, 0, lines);
	}

	public static void showOrthoview(Monitors monitors, RealSignal signal, String title) {
		if (signal == null) {
			monitors.error("Show Orthoview " + title + " this image does not exist.");
			return;
		}
		int hx = signal.nx / 2;
		int hy = signal.ny / 2;
		int hz = signal.nz / 2;
		ArrayList<Line2D.Double> lines = Output.createOverlayXYZ(signal);
		imaging.show(signal.createOrthoview(hx, hy, hz), title, Imager.Type.FLOAT, 0, lines);
	}

	public static void showMIP(RealSignal signal) {
		showMIP(signal, signal.name);
	}

	public static void showMIP(RealSignal signal, String title) {
		if (signal == null) {
			return;
		}
		ArrayList<Line2D.Double> lines = Output.createOverlayXYZ(signal);
		imaging.show(signal.createMIP(), title, Imager.Type.FLOAT, 0, lines);
	}

	public static void showMIP(Monitors monitors, RealSignal signal, String title) {
		if (signal == null) {
			monitors.error("Show MIP " + title + " this image does not exist.");
			return;
		}
		ArrayList<Line2D.Double> lines = Output.createOverlayXYZ(signal);
		imaging.show(signal.createMIP(), title, Imager.Type.FLOAT, 0, lines);
	}

	public static void showFigure(RealSignal signal) {
		showFigure(signal, signal.name);
	}

	public static void showFigure(RealSignal signal, String title) {
		if (signal == null) {
			return;
		}
		ArrayList<Line2D.Double> lines = Output.createOverlayXYZ(signal);
		imaging.show(signal.createMIP(), title, Imager.Type.FLOAT, 0, lines);
	}

	public static void showFigure(Monitors monitors, RealSignal signal, String title) {
		if (signal == null) {
			monitors.error("Show MIP " + title + " this image does not exist.");
			return;
		}
		int hx = signal.nx / 2;
		int hy = signal.ny / 2;
		int hz = signal.nz / 2;
		imaging.show(signal.createFigure(hx, hy, hz), title, Imager.Type.FLOAT, 0);
	}

	public static void showPlanar(RealSignal signal) {
		showPlanar(signal, signal.name);
	}

	public static void showPlanar(RealSignal signal, String title) {
		if (signal == null) {
			return;
		}
		ArrayList<Line2D.Double> lines = Output.createOverlayPlanar(signal);
		imaging.show(signal.createPlanar(), title, Imager.Type.FLOAT, 0, lines);
	}

	public static void showPlanar(Monitors monitors, RealSignal signal, String title) {
		if (signal == null) {
			monitors.error("Show Planar " + title + " this image does not exist.");
			return;
		}
		ArrayList<Line2D.Double> lines = Output.createOverlayPlanar(signal);
		imaging.show(signal.createPlanar(), title, Imager.Type.FLOAT, 0, lines);
	}

	public static void plotProfile(RealSignal signal, String name, int x1, int y1, int z1, int x2, int y2, int z2) {
		if (signal == null) {
			return;
		}
		double dx = x2 - x1;
		double dy = y2 - y1;
		double dz = z2 - z1;
		double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
		int n = (int) Math.round(len * 2);
		double ds = len / n;
		dx = (double) (x2 - x1) / n;
		dy = (double) (y2 - y1) / n;
		dz = (double) (z2 - z1) / n;
		double value[] = new double[n];
		double dist[] = new double[n];
		for (int s = 0; s < n; s++) {
			double x = x1 + s * dx;
			double y = y1 + s * dy;
			double z = z1 + s * dz;
			dist[s] = s * ds;
			value[s] = signal.getInterpolatedPixel(x, y, z);
		}
		Plot plot = new Plot(name, "distance", "intensity", dist, value);
		plot.show();
	}

	public static Imager getImager() {
		return imaging;
	}

	public static String getActiveImage() {
		if (imaging.isSelectable())
			return imaging.getSelectedImage();
		return "";
	}

	public static void setVisible(JDialog dialog, boolean modal) {
		if (dialog == null)
			return;
		dialogs.add(dialog);
		imaging.setVisible(dialog, modal);
	}

	public static void setVisible(JPanel panel, String name, int x, int y) {
		JFrame frame = new JFrame(name);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setLocation(x, y);
		frame.setVisible(true);
		frames.add(frame);
	}

	public static void setVisible(JFrame frame) {
		frames.add(frame);
		frame.setVisible(true);
	}

	public static void close() {
		for (JFrame frame : frames)
			if (frame != null)
				frame.dispose();
		for (JDialog dialog : dialogs)
			if (dialog != null)
				dialog.dispose();
	}

}
