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

package deconvolutionlab.module;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;

import bilib.component.HTMLPane;
import deconvolution.Command;
import deconvolution.Deconvolution;
import deconvolution.DeconvolutionDialog;
import deconvolutionlab.Lab;

public class CommandModule extends AbstractModule {

	private HTMLPane window;
	
	public CommandModule() {
		create("Command");
	}

	public HTMLPane getPane() {
		return window;
	}
	
	@Override
	public JPanel buildExpandedPanel() {
		window = new HTMLPane("Monaco", 100, 100);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(window.getPane(), BorderLayout.CENTER);
		getActionButton().setToolTipText("Human readable of the command line");
		getActionButton().addActionListener(this);
		return panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		if (e.getSource() == getActionButton()) {
			Deconvolution deconvolution = new Deconvolution("Check Command", Command.buildCommand());
			DeconvolutionDialog d = new DeconvolutionDialog(DeconvolutionDialog.Module.RECAP, deconvolution);
			Lab.setVisible(d, false);
		}
	}

	@Override
	public void close() {
	}
	
	@Override
	public void setCommand(String command) {
		window.clear();
		window.append("p", command);
	}
	
	@Override
	public String getCommand() {
		return window.getText();
	}

}
