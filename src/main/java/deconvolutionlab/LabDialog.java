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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import deconvolution.DeconvolutionDialog;
import deconvolutionlab.system.SystemInfo;

public class LabDialog extends JDialog implements ComponentListener, ActionListener, WindowListener {

	private LabPanel	panel;
	private JButton		bnClose	= new JButton("Close");

	public LabDialog() {
		super(new JFrame(), Constants.name);
		Config.registerFrame("DeconvolutionLab", "MainDialog", this);
		panel = new LabPanel(bnClose);
		getContentPane().add(panel);
		pack();
		addWindowListener(this);
		addComponentListener(this);
		bnClose.addActionListener(this);
		
		Rectangle rect = Config.getDialog("DeconvolutionLab.MainDialog");
		if (rect.x > 0 && rect.y > 0)
			setLocation(rect.x, rect.y);
		if (rect.width > 0 && rect.height > 0)
			setPreferredSize(new Dimension(rect.width, rect.height));
		this.setMinimumSize(new Dimension(560, 480));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bnClose) {
			Config.store();
			SystemInfo.close();
			panel.close();
			dispose();
			return;
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		panel.close();
		Config.store();
		dispose();
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

	@Override
	public void dispose() {
		super.dispose();
		if (Lab.getPlatform() == Platform.STANDALONE)
			System.exit(0);
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
		pack();
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

}
