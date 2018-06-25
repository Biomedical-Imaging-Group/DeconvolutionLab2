package plugins.sage.deconvolutionlab;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;

import deconvolution.DeconvolutionDialog;
import deconvolutionlab.Config;
import deconvolutionlab.Constants;
import deconvolutionlab.LabPanel;
import icy.gui.frame.IcyFrame;
import icy.gui.frame.IcyFrameEvent;
import icy.gui.frame.IcyFrameListener;

public class DeconvolutionLabIcyFrame extends IcyFrame implements ComponentListener, IcyFrameListener, ActionListener {
	
	private LabPanel panel = null;
	private JButton bnClose = new JButton("Close");
	
	public DeconvolutionLabIcyFrame() {
		super(Constants.name);	
		panel = new LabPanel(bnClose);
		getContentPane().add(panel);
		pack();
		addFrameListener(this);
		toFront();
		addToDesktopPane();
		setVisible(true);
		setResizable(true);
		bnClose.addActionListener(this);
		addComponentListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bnClose) {
			panel.close();
			dispose();
			return;
		}
	}

	@Override
	public void icyFrameOpened(IcyFrameEvent e) {
		System.out.println("not yet implemented " + e);
	}

	@Override
	public void icyFrameClosing(IcyFrameEvent e) {
		panel.close();
		Config.store();
		dispose();
	}

	@Override
	public void icyFrameClosed(IcyFrameEvent e) {
	}

	@Override
	public void icyFrameIconified(IcyFrameEvent e) {
	}

	@Override
	public void icyFrameDeiconified(IcyFrameEvent e) {
		panel.sizeModule();
	}

	@Override
	public void icyFrameActivated(IcyFrameEvent e) {
		panel.sizeModule();
	}

	@Override
	public void icyFrameDeactivated(IcyFrameEvent e) {
	}

	@Override
	public void icyFrameInternalized(IcyFrameEvent e) {
		panel.sizeModule();
	}

	@Override
	public void icyFrameExternalized(IcyFrameEvent e) {
		panel.sizeModule();
	}
	@Override
	public void componentResized(ComponentEvent e) {
		panel.sizeModule();
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		Point p = this.getLocation();
		p.x += this.getWidth();
		DeconvolutionDialog.setLocationLaunch(p);
	}

	@Override
	public void componentShown(ComponentEvent e) {
		panel.sizeModule();
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}
	
}
