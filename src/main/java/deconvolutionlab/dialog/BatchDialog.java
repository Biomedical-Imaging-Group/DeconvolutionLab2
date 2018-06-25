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

package deconvolutionlab.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import bilib.component.GridPanel;
import bilib.component.HTMLPane;
import deconvolution.Command;
import deconvolution.Deconvolution;
import deconvolutionlab.Constants;
import deconvolutionlab.module.BatchModule;

public class BatchDialog extends JDialog implements ActionListener, WindowListener {

	private JTextField	txt			= new JTextField("job", 10);
	private HTMLPane	pnCommand;

	private JButton		bnAdd		= new JButton("Add Job");
	private JButton		bnCancel	= new JButton("Cancel");

	private BatchModule	module;

	public BatchDialog(BatchModule module) {
		super(new JFrame(), "Batch");

		this.module = module;

		txt.setText("job" + module.getCountJob());

		Deconvolution deconvolution = new Deconvolution(txt.getText(), Command.buildCommand());
		pnCommand = new HTMLPane("Monaco", Constants.widthGUI, 100);
		pnCommand.append("p", deconvolution.getCommand());
		pnCommand.setEditable(true);

		GridPanel pn = new GridPanel(true, 5);
		pn.place(1, 0, "Job Name");
		pn.place(1, 1, txt);

		GridPanel bn = new GridPanel(false);
		bn.place(11, 0, bnCancel);
		bn.place(11, 1, bnAdd);
		pn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(pn, BorderLayout.NORTH);
		panel.add(pnCommand.getPane(), BorderLayout.CENTER);
		panel.add(bn, BorderLayout.SOUTH);

		bnAdd.addActionListener(this);
		bnCancel.addActionListener(this);

		add(panel);
		pack();
		addWindowListener(this);
		setMinimumSize(new Dimension(400, 300));
	}

	private void addJob() {
		module.addJob(txt.getText(), pnCommand.getText());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bnAdd) {
			addJob();
			bnAdd.removeActionListener(this);
			bnCancel.removeActionListener(this);
			dispose();
			return;
		}
		else if (e.getSource() == bnCancel) {
			bnAdd.removeActionListener(this);
			bnCancel.removeActionListener(this);
			dispose();
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		addJob();
		dispose();
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
