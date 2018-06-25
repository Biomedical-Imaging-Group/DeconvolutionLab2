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

package plugins.sage.deconvolutionlab;

import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JDialog;

import deconvolutionlab.Imager;
import icy.file.Saver;
import icy.image.IcyBufferedImage;
import icy.imagej.ImageJUtil;
import icy.main.Icy;
import icy.sequence.Sequence;
import icy.type.DataType;
import icy.type.collection.array.Array1DUtil;
import ij.ImagePlus;
import ij.io.Opener;
import signal.ComplexComponent;
import signal.ComplexSignal;
import signal.RealSignal;

public class IcyImager extends Imager {

	@Override
	public void setVisible(JDialog dialog, boolean modal) {
		
		/*
		IcyFrame icf = new IcyFrame();
		icf.addFrameListener(this);
		icf.setTitle(dialog.getTitle());
		//dialog.setModal(modal);
		icf.add(dialog.getContentPane());
		//icf.add(panel);
		icf.toFront();
		icf.addToDesktopPane();
		icf.setVisible(true);	
		*/
		//Lab.setVisible(dialog, true);
		
		dialog.pack();
		dialog.setLocation(30, 30);
		dialog.setVisible(true);

	}
	
	public static RealSignal create(Sequence seq) {
		int nx = seq.getSizeX();
		int ny = seq.getSizeY();
		int nz = seq.getSizeZ();
		RealSignal signal = new RealSignal("icy-" + seq.getName(), nx, ny, nz);
		for (int k = 0; k < nz; k++) {
			float pixels[] = new float[nx * ny];
			Array1DUtil.arrayToFloatArray(seq.getDataXY(0, k, 0), pixels, seq.isSignedDataType());
			signal.setXY(k, pixels);
		}
		return signal;
	}

	@Override
    public RealSignal getActiveImage() {
		return build(Icy.getMainInterface().getActiveSequence());
	}
	
	@Override
    public RealSignal getImageByName(String name) {
		ArrayList<Sequence> sequences = Icy.getMainInterface().getSequences(name);
		for(Sequence sequence : sequences)
			if (sequence.getName().equals(name))
				return build(sequence);
		return null;
	}

	@Override
    public RealSignal open(String filename) {
		Opener opener = new Opener();
		ImagePlus imp = opener.openImage(filename);
		Sequence seq = ImageJUtil.convertToIcySequence(imp, null);
		return build(seq);
	}

	@Override
    public void show(ComplexSignal signal, String title, ComplexComponent complex) {
		Sequence sequence = new Sequence();
		for (int k = 0; k < signal.nz; k++) {
			float[] plane = null;
			switch(complex) {
				case REAL: plane = signal.getRealXY(k); break;
				case IMAGINARY: plane = signal.getImagXY(k); break;
				case MODULE: plane = signal.getModuleXY(k); break;
				default: plane = signal.getModuleXY_dB(k);
			}
			IcyBufferedImage image = new IcyBufferedImage(signal.nx, signal.ny, 1, DataType.FLOAT);
			Array1DUtil.floatArrayToSafeArray(plane, image.getDataXY(0), image.isSignedDataType());
			image.dataChanged();
			sequence.setImage(0, k, image);
		}
		sequence.setName(title);
		Icy.getMainInterface().addSequence(sequence);
    }
	
	@Override
	public void save(RealSignal signal, String filename, Imager.Type type) {
		Sequence sequence = build(signal, type);
		File file = new File(filename);
		Saver.save(sequence, file, false, true);
	}
	
	private RealSignal build(Sequence seq) {
		int nx = seq.getSizeX();
		int ny = seq.getSizeY();
		int nz = seq.getSizeZ();
		RealSignal signal = new RealSignal("icy-" + seq.getName(), nx, ny, nz);
		for (int k = 0; k < nz; k++) {
			float pixels[] = new float[nx * ny];
			Array1DUtil.arrayToFloatArray(seq.getDataXY(0, k, 0), pixels, seq.isSignedDataType());
			signal.setXY(k, pixels);
		}
		return signal;
	}
	
	private Sequence build(RealSignal signal, Imager.Type type) {
		int nx = signal.nx;
		int ny = signal.ny;
		int nz = signal.nz;
		Sequence sequence = new Sequence();
		for (int z = 0; z < nz; z++) {
			if (type == Imager.Type.SHORT) {
				short[] plane = Array1DUtil.arrayToShortArray(signal.data[z], false);
				IcyBufferedImage image = new IcyBufferedImage(nx, ny, 1, DataType.USHORT);
				Array1DUtil.shortArrayToArray(plane, image.getDataXY(0), image.isSignedDataType());
				image.dataChanged();
				sequence.setImage(0, z, image);
			}
			else if (type == Imager.Type.BYTE) {
				byte[] plane = Array1DUtil.arrayToByteArray(signal.data[z]);
				IcyBufferedImage image = new IcyBufferedImage(nx, ny, 1, DataType.UBYTE);
				Array1DUtil.byteArrayToArray(plane, image.getDataXY(0), image.isSignedDataType());
				image.dataChanged();
				sequence.setImage(0, z, image);
			}
			else {
				IcyBufferedImage image = new IcyBufferedImage(nx, ny, 1, DataType.FLOAT);
				Array1DUtil.floatArrayToSafeArray(signal.data[z], image.getDataXY(0), image.isSignedDataType());
				image.dataChanged();
				sequence.setImage(0, z, image);
			}

		}
		return sequence;
	}
	
	@Override
	public String getName() {
		return "Icy";
	}

	@Override
	public ContainerImage createContainer(String title) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void append(ContainerImage container, RealSignal signal, String title, Type type) {
		append(container, signal, title, type, new ArrayList<Line2D.Double>());	
	}
	
	@Override
	public void append(ContainerImage container, RealSignal signal, String title, Type type, ArrayList<Line2D.Double> overlayLines) {
		// TODO Auto-generated method stub	
	}


	@Override
	public void show(RealSignal signal, String title, Type type, int z) {
		Sequence sequence = build(signal, type);
		sequence.setName(title);
		Icy.getMainInterface().addSequence(sequence);
	}
	
	@Override
	public void show(RealSignal signal, String title, Type type, int z, ArrayList<Double> overlayLines) {
		Sequence sequence = build(signal, type);
		sequence.setName(title);
		Icy.getMainInterface().addSequence(sequence);
	}
	

	@Override
	public String getSelectedImage() {
		return null;
	}

	@Override
	public boolean isSelectable() {
		return false;
	}


}
