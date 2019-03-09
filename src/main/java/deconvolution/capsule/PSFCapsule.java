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

import javax.swing.JSplitPane;

import bilib.table.CustomizedTable;
import bilib.tools.NumFormat;
import deconvolution.Deconvolution;
import deconvolution.Features;
import signal.RealSignal;
import signal.SignalCollector;

/**
 * This class is a information module for the PSF source.
 * 
 * @author Daniel Sage
 *
 */
public class PSFCapsule extends AbstractCapsule implements Runnable {

	private CustomizedTable	table;

	public PSFCapsule(Deconvolution deconvolution) {
		super(deconvolution);
		table = new CustomizedTable(new String[] { "Features", "Values" }, false);
		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, table.getPane(300, 300), null);
	}

	@Override
	public void update() {
		split.setDividerLocation(300);
		if (table == null)
			return;
		table.removeRows();
		table.append(new String[] { "PSF", "Waiting for loading ..." });
		Thread thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	@Override
	public String getID() {
		return "PSF";
	}

	@Override
	public void run() {
		Features features = new Features();

		if (deconvolution.getImage() == null) {
			startAsynchronousTimer("Open image", 200);
			deconvolution.setImage(deconvolution.openImage());
			stopAsynchronousTimer();
		}

		if (deconvolution.getController().getPadding() == null) {
			features.add("Padding", "No valid padding");
			return;
		}

		if (deconvolution.getController().getApodization() == null) {
			features.add("Apodization", "No valid apodization");
			return;
		}

		if (deconvolution.getPSF() == null) {
			startAsynchronousTimer("Open PSF", 200);
			deconvolution.setPSF(deconvolution.openPSF());
			stopAsynchronousTimer();
		}

		if (deconvolution.getPSF() == null) {
			features.add("PSF", "No valid PSF");
			return;
		}

		startAsynchronousTimer("Open PSF", 200);
		RealSignal psf = deconvolution.getPSF();
		float stati[] = psf.getStats();
		int sizi = psf.nx * psf.ny * psf.nz;
		float totali = stati[0] * sizi;
		features.add("<html><b>Orignal PSF</b></html>", "");
		features.add("Size", psf.dimAsString() + " " + NumFormat.bytes(sizi * 4));
		features.add("Mean (stdev)", NumFormat.nice(stati[0]) + " (" + NumFormat.nice(stati[3]) + ")");
		features.add("Extrema (min, max)", NumFormat.nice(stati[1]) + ", " + NumFormat.nice(stati[2]));
		features.add("Energy (integral)", NumFormat.nice(stati[5]) + " (" + NumFormat.nice(totali) + ")");
		table.removeRows();
		for (String[] feature : features)
			table.append(feature);

		RealSignal h;
		if (deconvolution.getImage() == null) {
			features.add("Image", "No valid input image to resize");
			h = psf.duplicate();
		}
		else
			h = psf.changeSizeAs(deconvolution.getImage());
		h.normalize(deconvolution.getController().getNormalizationPSF());
		float stats[] = h.getStats();
		int sizs = h.nx * h.ny * h.nz;
		float totals = stats[0] * sizs;
		double incpad = (double)((sizs-sizi)/sizi*100.0);
		features.add("<html><b>Working PSF</b></html>", "");
		features.add("Size", h.dimAsString() + " " + NumFormat.bytes(sizs * 4));
		features.add("Mean (stdev)", NumFormat.nice(stats[0]) + " (" + NumFormat.nice(stats[3]) + ")");
		features.add("Extrema (min, max)", NumFormat.nice(stats[1]) + ", " + NumFormat.nice(stats[2]));
		features.add("Energy (integral)", NumFormat.nice(stats[5]) + " (" + NumFormat.nice(totals) + ")");
		features.add("<html><b>Information</b></html>", "");
		features.add("Size increase (image)", NumFormat.nice(incpad) + "%");
		features.add("Energy lost", NumFormat.nice((stats[5] - stati[5]) / stati[5] * 100));
		SignalCollector.free(h);
		table.removeRows();
		for (String[] feature : features)
			table.append(feature);
		
		stopAsynchronousTimer();
	}

}