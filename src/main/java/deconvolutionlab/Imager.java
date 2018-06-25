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

import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JDialog;

import imagej.IJImager;
import plugins.sage.deconvolutionlab.IcyImager;
import signal.ComplexComponent;
import signal.ComplexSignal;
import signal.RealSignal;

public abstract class Imager {

	public enum Type {FLOAT, SHORT, BYTE};

	public class ContainerImage {	
		public Object object;
	}

	public abstract void setVisible(JDialog dialog, boolean modal);
	public abstract RealSignal getActiveImage();
	public abstract RealSignal getImageByName(String name);
	public abstract ContainerImage createContainer(String title);
	public abstract void show(ComplexSignal signal, String title, ComplexComponent complex);
	public abstract void show(RealSignal signal, String title, Imager.Type type, int z);
	public abstract void show(RealSignal signal, String title, Imager.Type type, int z, ArrayList<Line2D.Double> overlayLines);
	public abstract void append(ContainerImage container,  RealSignal signal, String title, Type type);
	public abstract void append(ContainerImage container,  RealSignal signal, String title, Type type, ArrayList<Line2D.Double> overlayLines);
	public abstract void save(RealSignal signal, String filename, Type type);
	public abstract RealSignal open(String filename);
	public abstract String getName();
	public abstract String getSelectedImage();
	public abstract boolean isSelectable();
	
	public static ArrayList<Imager> getImagings() {
		ArrayList<Imager> imagings = new ArrayList<Imager>();
		try {
			Imager imaging = new IJImager();
			if (imaging != null) {
				imagings.add(imaging);
			}
		}
		catch (NoClassDefFoundError ex) {
		}

		try {
			Imager imaging = new IcyImager();
			if (imaging != null) {
				imagings.add(imaging);
			}
		}
		catch (NoClassDefFoundError ex) {
		}
		return imagings;
	}

	public static ArrayList<String> getImagingByName() {
		ArrayList<Imager> imagings = getImagings();
		ArrayList<String> names = new ArrayList<String>();
		for (Imager imaging : imagings)
			names.add(imaging.getName());
		return names;
	}
}
