package deconvolutionlab.module.dropdownbuttons;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import deconvolution.Command;
import deconvolution.Deconvolution;
import deconvolution.DeconvolutionDialog;
import deconvolutionlab.Lab;
import deconvolutionlab.monitor.Monitors;

public class ShowImageDropDownButton extends AbstractDropDownButton implements ActionListener {

	private JMenuItem	mip			= new JMenuItem("Show MIP");
	private JMenuItem	ortho		= new JMenuItem("Show orthoview");
	private JMenuItem	planar		= new JMenuItem("Show planar");
	private JMenuItem	figure		= new JMenuItem("Show figure (XY,YZ)");
	private JMenuItem	stack		= new JMenuItem("Show as a z-stack (default)");
	private JMenuItem	stats		= new JMenuItem("Check statistics of image");

	private String		title		= "";

	public ShowImageDropDownButton(String moduleName, String text) {
		super(moduleName, text, false);
		this.moduleName = moduleName;
		setPreferredSize(new Dimension(80, 22));
		JPopupMenu popup = new JPopupMenu();
		popup.add(stack);
		popup.add(mip);
		popup.add(ortho);
		popup.add(planar);
		popup.add(figure);
		popup.add(stats);
		mip.addActionListener(this);
		ortho.addActionListener(this);
		planar.addActionListener(this);
		figure.addActionListener(this);
		stack.addActionListener(this);
		stats.addActionListener(this);
		setPopupMenu(popup);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		if (!getOnSplit()) {
			Monitors m = Monitors.createDefaultMonitor();
			boolean img = moduleName.equalsIgnoreCase("image");
			boolean psf = moduleName.equalsIgnoreCase("psf");
			boolean ref = moduleName.equalsIgnoreCase("ref");
			String cmd = Command.buildCommand();
			if (e.getSource() == mip) {
				Deconvolution deconvolution = new Deconvolution("Show", cmd);
				if (img) Lab.showMIP(m, deconvolution.openImage(), title);
				if (psf) Lab.showMIP(m, deconvolution.openPSF(), title);
				if (ref) Lab.showMIP(m, deconvolution.openReference(), title);
				return;
			}
			else if (e.getSource() == ortho) {
				Deconvolution deconvolution = new Deconvolution("Show", cmd);
				if (img) Lab.showOrthoview(m, deconvolution.openImage(), title);
				if (psf) Lab.showOrthoview(m, deconvolution.openPSF(), title);
				if (ref) Lab.showOrthoview(m, deconvolution.openReference(), title);
				return;
			}
			if (e.getSource() == planar) {
				Deconvolution deconvolution = new Deconvolution("Show", cmd);
				if (img) Lab.showPlanar(m, deconvolution.openImage(), title);
				if (psf) Lab.showPlanar(m, deconvolution.openPSF(), title);
				if (ref) Lab.showPlanar(m, deconvolution.openReference(), title);
				return;
			}
			else if (e.getSource() == figure) {
				Deconvolution deconvolution = new Deconvolution("Show", cmd);
				if (img) Lab.showFigure(m, deconvolution.openImage(), title);
				if (psf) Lab.showFigure(m, deconvolution.openPSF(), title);
				if (ref) Lab.showFigure(m, deconvolution.openReference(), title);
				return;
			}
			else if (e.getSource() == stack) {
				Deconvolution deconvolution = new Deconvolution("Show", cmd);
				if (img) Lab.show(m, deconvolution.openImage(), title);
				if (psf) Lab.show(m, deconvolution.openPSF(), title);
				if (ref) Lab.show(m, deconvolution.openReference(), title);
				return;
			}
			
			else if (e.getSource() == stats) {
				Deconvolution deconvolution = new Deconvolution("Check", cmd);
				if (img) {
					deconvolution.openImage();
					DeconvolutionDialog d = new DeconvolutionDialog(DeconvolutionDialog.Module.IMAGE, deconvolution);
					Lab.setVisible(d, false);
				}
				if (psf) {
					deconvolution.openPSF();
					DeconvolutionDialog d = new DeconvolutionDialog(DeconvolutionDialog.Module.PSF, deconvolution);
					Lab.setVisible(d, false);	
				}
				if (ref) {
					deconvolution.openReference();
					DeconvolutionDialog d = new DeconvolutionDialog(DeconvolutionDialog.Module.REFERENCE, deconvolution);
					Lab.setVisible(d, false);	
				}
				return;
			}
			Deconvolution deconvolution = new Deconvolution("Check", cmd);
			if (img) {
				deconvolution.openImage();
				DeconvolutionDialog d = new DeconvolutionDialog(DeconvolutionDialog.Module.IMAGE, deconvolution);
				Lab.setVisible(d, false);
			}
			if (psf) {
				deconvolution.openPSF();
				DeconvolutionDialog d = new DeconvolutionDialog(DeconvolutionDialog.Module.PSF, deconvolution);
				Lab.setVisible(d, false);	
			}
			if (ref) {
				deconvolution.openReference();
				DeconvolutionDialog d = new DeconvolutionDialog(DeconvolutionDialog.Module.REFERENCE, deconvolution);
				Lab.setVisible(d, false);	
			}
		}
	}
}
