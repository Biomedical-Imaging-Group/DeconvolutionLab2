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

package signal.factory;

import java.util.ArrayList;

import javax.swing.SwingWorker;

import signal.RealSignal;
import bilib.tools.NumFormat;
import deconvolution.Command;

public abstract class SignalFactory {

	protected double	fractXC		= 0.5;
	protected double	fractYC		= 0.5;
	protected double	fractZC		= 0.5;
	protected double	amplitude	= 1.0;
	protected double	xc;
	protected double	yc;
	protected double	zc;
	protected int		nx;
	protected int		ny;
	protected int		nz;

	public SignalFactory() {
	}

	public SignalFactory(double[] parameters) {
		setParameters(parameters);
	}

	public static SignalFactory get(String name) {
		ArrayList<SignalFactory> list = getAll();
		for (SignalFactory factory : list) {
			if (factory.getName().equals(name))
				return factory;
		}
		return null;
	}
	
	public static RealSignal createFromCommand(String cmd) {
		String parts[] = cmd.split(" ");
		if (parts.length <= 0)
			return null;
		String shape = parts[0];
		for (String name : SignalFactory.getAllName()) {
			if (shape.equalsIgnoreCase(name.toLowerCase())) {
				double params[] = Command.parseNumeric(cmd);
				SignalFactory factory = SignalFactory.getFactoryByName(shape);
				if (factory == null)
					return null;
				
				int np = factory.getParameters().length;
				double parameters[] = new double[np];
				if (np >= 1 && params.length >= 1)
					parameters[0] = params[0];
				if (np >= 2 && params.length >= 2)
					parameters[1] = params[1];
				if (np >= 3 && params.length >= 3)
					parameters[2] = params[2];
				if (np >= 4 && params.length >= 4)
					parameters[3] = params[3];

				double size[] = NumFormat.parseNumbersAfter("size", cmd);
				int nx = 128;
				int ny = 128;
				int nz = 32;
				if (size.length > 0)
					nx = (int) size[0];
				if (size.length > 1)
					ny = (int) size[1];
				if (size.length > 2)
					nz = (int) size[2];

				double intensity[] = NumFormat.parseNumbersAfter("intensity", cmd);
				double amplitude = 255;
				if (intensity.length > 0)
					amplitude = intensity[0];
				
				double center[] = NumFormat.parseNumbersAfter("center", cmd);
				double cx = 0.5;
				double cy = 0.5;
				double cz = 0.5;
				if (center.length > 0)
					cx = center[0];
				if (center.length > 1)
					cy = center[1];
				if (center.length > 2)
					cz = center[2];
				
				factory.intensity(amplitude);
				factory.setParameters(parameters);
				factory = factory.center(cx, cy, cz);
				return factory.generate(nx, ny, nz);
			}
		}
		return null;

	}

	public static ArrayList<String> getAllName() {
		ArrayList<String> list = new ArrayList<String>();
		for (SignalFactory factory : getAll()) {
			list.add(factory.getName());
		}
		return list;
	}

	public static ArrayList<SignalFactory> getAll() {
		ArrayList<SignalFactory> list = new ArrayList<SignalFactory>();
		list.add(new Airy(5, 1, 0.5, 3));	
		list.add(new Astigmatism(5, 1));	
		list.add(new AxialDiffractionSimulation(10, 10, 2));	
		list.add(new BesselJ0(2, 5, 0.2, 0.2));	
		list.add(new Constant());
		list.add(new Cross(1, 1, 30));
		list.add(new Cube(10 ,1));
		list.add(new CubeSphericalBeads(3, 0.5, 8, 16));
		list.add(new Defocus(3, 10, 10));
		list.add(new DirectionalDerivative(1, 1, 0));	
		list.add(new DirectionalMotionBlur(3, 30, 3));
		list.add(new DoG(3, 4));
		list.add(new DoubleHelix(3, 30, 10));
		list.add(new Gaussian(3, 3, 3));
		list.add(new Impulse());
		list.add(new Laplacian());
		list.add(new Ramp(1, 1, 1));
		list.add(new RandomLines(100));
		list.add(new Sinc(3, 3, 3));
		list.add(new Sphere(10, 1));
		list.add(new Torus(30));
		return list;
	}
	
	public static ArrayList<SignalFactory> getImages() {
		ArrayList<SignalFactory> list = new ArrayList<SignalFactory>();
		list.add(new Cube(10, 1));
		list.add(new CubeSphericalBeads(3, 0.5, 8, 16));
		list.add(new Sphere(10, 1));
		list.add(new Constant());
		list.add(new Cross(1, 1, 30));
		list.add(new Gaussian(3, 3, 3));
		list.add(new Impulse());
		list.add(new Ramp(1, 0, 0));
		list.add(new RandomLines(3));
		list.add(new Torus(10));
		return list;
	}

	public static ArrayList<SignalFactory> getPSF() {
		ArrayList<SignalFactory> list = new ArrayList<SignalFactory>();
		list.add(new Airy(5, 1, 0.5, 3));	
		list.add(new Astigmatism(5, 1));	
		list.add(new AxialDiffractionSimulation(10, 10, 2));	
		list.add(new BesselJ0(2, 5, 0.2, 0.2));	
		list.add(new Cross(1, 1, 30));
		list.add(new CubeSphericalBeads(3, 0.5, 8, 16));
		list.add(new Defocus(3, 10, 10));
		list.add(new DirectionalDerivative(1, 1, 0));	
		list.add(new DirectionalMotionBlur(3, 30, 3));
		list.add(new DoG(3, 4));
		list.add(new DoubleHelix(3, 30, 10));
		list.add(new Gaussian(3, 3, 3));
		list.add(new Impulse());
		list.add(new Laplacian());
		list.add(new Sinc(3, 3, 3));
		list.add(new Sphere(10, 1));
		return list;
	}

	public static SignalFactory getFactoryByName(String name) {
		ArrayList<SignalFactory> list = getAll();
		for (SignalFactory factory : list)
			if (name.toLowerCase().equals(factory.getName().toLowerCase())) {
				return factory;
			}
		return null;
	}
	
	public SignalFactory center(double fractXC, double fractYC, double fractZC) {
		this.fractXC = fractXC;
		this.fractYC = fractYC;
		this.fractZC = fractZC;
		return this;
	}

	public SignalFactory intensity(double amplitude) {
		this.amplitude = amplitude;
		return this;
	}

	public String params() {
		String name[] = getParametersName();
		double params[] = getParameters();
		if (params.length == 1)
			return name[0] + "=" + params[0];
		else if (params.length == 2)
			return name[0] + "=" + params[0] + " " + name[1] + "=" + params[1];
		else
			return name[0] + "=" + params[0] + " " + name[1] + "=" + params[2] + " " + name[2] + "=" + params[2];
	}

	public RealSignal generate(int nx, int ny, int nz) {
		this.nx = nx;
		this.ny = ny;
		this.nz = nz;
		xc = fractXC * nx;
		yc = fractYC * ny;
		zc = fractZC * nz;
		RealSignal signal = new RealSignal(getName(), nx, ny, nz);
		fill(signal);

		return signal;
	}

	public abstract String getName();

	public abstract void setParameters(double[] parameters);

	public abstract double[] getParameters();

	public abstract String[] getParametersName();

	public abstract void fill(RealSignal signal);

	public class Worker extends SwingWorker<RealSignal, String> {
		private RealSignal signal;
		public boolean done=false;
		public Worker(RealSignal signal) {
			this.signal = signal;
			done = false;
		}
		
		@Override
		protected RealSignal doInBackground() throws Exception {
			fill(signal);
			done = true;
			return signal;
		}

		@Override
		protected void done() {
			done = true;
		}
	
	}
}
