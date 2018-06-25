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

package imagej;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import deconvolutionlab.Imager;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.GUI;
import ij.gui.Overlay;
import ij.io.FileSaver;
import ij.io.Opener;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import signal.ComplexComponent;
import signal.ComplexSignal;
import signal.RealSignal;

public class IJImager extends Imager {
	
	@Override
	public void setVisible(JDialog dialog, boolean modal) {
		if (modal) {
			dialog.setModal(modal);
			GUI.center(dialog);
		}
		dialog.pack();
		dialog.setVisible(true);
	}

	public static RealSignal create(ImagePlus imp) {
		int nx = imp.getWidth();
		int ny = imp.getHeight();
		int nz = imp.getStackSize();
		RealSignal signal = new RealSignal(imp.getTitle(), nx, ny, nz);
		for (int k = 0; k < nz; k++) {
			ImageProcessor ip = imp.getStack().getProcessor(k + 1).convertToFloat();
			signal.setXY(k, (float[]) ip.getPixels());
		}
		return signal;
	}

	@Override
	public RealSignal getActiveImage() {
		return build(WindowManager.getCurrentImage());
	}

	@Override
	public RealSignal getImageByName(String name) {
		ImagePlus imp = null;
		if (name.equalsIgnoreCase("active"))
			imp = WindowManager.getCurrentImage();
		else
			imp = WindowManager.getImage(name);
		if (imp == null)
			return null;
		return build(imp);
	}

	@Override
	public RealSignal open(String filename) {
		Opener opener = new Opener();
		ImagePlus imp = opener.openImage(filename);
		if (imp == null)
			return null;
		return build(imp);
	}

	@Override
	public void show(RealSignal signal, String title, Imager.Type type, int z) {
		ImagePlus imp = build(signal, type);
		if (imp != null) {
			imp.setTitle(title);
			int nz = imp.getStackSize();
			imp.show();
			imp.setSlice(Math.max(1, Math.min(nz, z)));
			imp.getProcessor().resetMinAndMax();
		}
	}
	
	@Override
	public void show(RealSignal signal, String title, Imager.Type type, int z, ArrayList<Line2D.Double> overlayLines) {
		ImagePlus imp = build(signal, type);
		if (imp != null) {
			imp.setTitle(title);
			int nz = imp.getStackSize();
			imp.show();
			imp.setSlice(Math.max(1, Math.min(nz, z)));
			imp.getProcessor().resetMinAndMax();
		}
		
		if (imp != null) {
			Overlay overlay = imp.getOverlay() == null ? new Overlay() : imp.getOverlay();
			for(Line2D.Double line : overlayLines) {
				ij.gui.Line roi = new ij.gui.Line(round(line.x1), round(line.y1), round(line.x2), round(line.y2));
				overlay.add(roi);
			}
			imp.setOverlay(overlay);
		}
	}
	
	private int round(double a) {
		return (int)Math.round(a);
	}

	@Override
    public ContainerImage createContainer(String title) {
		return new ContainerImage();
	}

	@Override
	public void append(ContainerImage container, RealSignal signal, String title, Imager.Type type) {		
		append(container, signal, title, type, new ArrayList<Line2D.Double>());
	}
	
	@Override
	public void append(ContainerImage container, RealSignal signal, String title, Imager.Type type, ArrayList<Line2D.Double> overlayLines) {		
		ImagePlus cont = (ImagePlus) container.object;
		if (container.object == null) {
			ImageStack stack = new ImageStack(signal.nx, signal.ny);
			stack.addSlice(build(signal, type).getProcessor());
			stack.addSlice(build(signal, type).getProcessor());
			container.object = new ImagePlus(title, stack);
			((ImagePlus)container.object).show();
		}
		else {
			cont.getStack().addSlice(build(signal, type).getProcessor());
			cont.setSlice(cont.getStack().getSize());
			cont.updateAndDraw();
			cont.getProcessor().resetMinAndMax();
		}
		if (cont != null)  {
			Overlay overlay = cont.getOverlay() == null ? new Overlay() : cont.getOverlay();
			for(Line2D.Double line : overlayLines) {
				ij.gui.Line roi = new ij.gui.Line(round(line.x1), round(line.y1), round(line.x2), round(line.y2));
				overlay.add(roi);
			}
			cont.setOverlay(overlay);
		}		
	}

	@Override
	public void save(RealSignal signal, String filename, Imager.Type type) {
		ImagePlus imp = build(signal, type);
		if (imp != null) {
			if (imp.getStackSize() == 1) {
				new FileSaver(imp).saveAsTiff(filename);
			}
			else {
				new FileSaver(imp).saveAsTiffStack(filename);
			}
		}
	}

	@Override
	public void show(ComplexSignal signal, String title, ComplexComponent complex) {
		ImageStack stack = new ImageStack(signal.nx, signal.ny);
		for (int k = 0; k < signal.nz; k++) {
			float[] plane = null;
			switch (complex) {
			case REAL:
				plane = signal.getRealXY(k);
				break;
			case IMAGINARY:
				plane = signal.getImagXY(k);
				break;
			case MODULE:
				plane = signal.getModuleXY(k);
				break;
			default:
				plane = signal.getModuleXY_dB(k);
			}
			stack.addSlice(new FloatProcessor(signal.nx, signal.ny, plane));
		}
		new ImagePlus(title, stack).show();
	}	

	private RealSignal build(ImagePlus imp) {
		if (imp == null)
			return null;
		int nx = imp.getWidth();
		int ny = imp.getHeight();
		int nz = imp.getStackSize();
		RealSignal signal = new RealSignal("ij-" + imp.getTitle(), nx, ny, nz);
		for (int k = 0; k < nz; k++) {
			ImageProcessor ip = imp.getStack().getProcessor(k + 1).convertToFloat();
			signal.setXY(k, (float[]) ip.getPixels());
		}
		return signal;
	}

	private ImagePlus build(RealSignal signal, Imager.Type type) {
		if (signal == null)
			return null;

		ImageStack stack = new ImageStack(signal.nx, signal.ny);
		for (int k = 0; k < signal.nz; k++) {
			ImageProcessor ip = new FloatProcessor(signal.nx, signal.ny, signal.getXY(k));
			switch (type) {
			case BYTE:
				stack.addSlice(ip.convertToByteProcessor(false));
				break;
			case SHORT:
				stack.addSlice(ip.convertToShortProcessor(false));
				break;
			case FLOAT:
				stack.addSlice(ip);
			default:
				break;
			}
		}
		return new ImagePlus("", stack);
	}

	@Override
	public String getName() {
		return "ImageJ";
	}
	
	@Override
	public boolean isSelectable() {
		return true;
	}

	@Override
	public String getSelectedImage() {
		Dialog dialog = new Dialog();
		dialog.setVisible(true);
		if (dialog.wasCancel())
			return "";
		return dialog.getName();
	}
	

	public class Dialog extends JDialog implements ActionListener, WindowListener {

		private JList<String>	list;
		private JButton			bnOK		= new JButton("OK");
		private JButton			bnCancel	= new JButton("Cancel");
		private boolean			cancel		= false;
		private String			name		= "";

		public Dialog() {
			super(new JFrame(), "Image Selection");

			JPanel bn = new JPanel(new GridLayout(1, 2));
			bn.add(bnCancel);
			bn.add(bnOK);

			JPanel panel = new JPanel(new BorderLayout());
			int[] ids = WindowManager.getIDList();
			
			if (ids != null) {
				DefaultListModel<String> listModel = new DefaultListModel<String>();
				list = new JList<String>(listModel);
				for (int id : ids) {
					ImagePlus idp = WindowManager.getImage(id);
					if (idp != null) {
						((DefaultListModel<String>) listModel).addElement((String)idp.getTitle());
					}
				}
				list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
				JScrollPane listScroller = new JScrollPane(list);
				listScroller.setPreferredSize(new Dimension(250, 80));
				panel.add(listScroller, BorderLayout.CENTER);
			}
			else {
				panel.add(new JLabel("No open images."));
			}
			panel.add(bn, BorderLayout.SOUTH);
			panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			bnOK.addActionListener(this);
			bnCancel.addActionListener(this);
			add(panel);
			pack();
			addWindowListener(this);
			GUI.center(this);
			setModal(true);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			bnOK.removeActionListener(this);
			bnCancel.removeActionListener(this);
			if (e.getSource() == bnCancel) {
				cancel = true;
				name = "";
				dispose();
				return;
			}
			else if (e.getSource() == bnOK) {
				cancel = false;
				name = (String) list.getSelectedValue();
				dispose();
			}
		}

		@Override
        public String getName() {
			return name;
		}

		public boolean wasCancel() {
			return cancel;
		}

		@Override
		public void windowOpened(WindowEvent e) {
		}

		@Override
		public void windowClosing(WindowEvent e) {
			dispose();
			cancel = true;
			name = "";
			return;
		}

		@Override
		public void windowClosed(WindowEvent e) {
		}

		@Override
		public void windowIconified(WindowEvent e) {
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
		}

		@Override
		public void windowActivated(WindowEvent e) {
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
		}
	}

}
