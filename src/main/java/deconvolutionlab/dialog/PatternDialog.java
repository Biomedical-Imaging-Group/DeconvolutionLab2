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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import bilib.component.GridPanel;
import ij.gui.GUI;

public class PatternDialog extends JDialog implements ActionListener, WindowListener, KeyListener {

	private JTextField	txt			= new JTextField("");
	private JLabel		lblDir		= new JLabel("");
	private JLabel		lbl1		= new JLabel(".tif (only tif files)");
	private JLabel		lbl2		= new JLabel("Empty to take all files");
	private JLabel		lblCount	= new JLabel("count files");

	private JButton		bnCount		= new JButton("Count");
	private JButton		bnOK		= new JButton("OK");
	private JButton		bnCancel	= new JButton("Cancel");
	private boolean		cancel		= false;

	private File		file;
	private String		command		= "";
	private String		name		= "";

	public PatternDialog(File file) {
		super(new JFrame(), "Pattern");
		lblDir.setText(file.getAbsolutePath());
		lblDir.setBorder(BorderFactory.createEtchedBorder());
		lbl1.setBorder(BorderFactory.createEtchedBorder());
		lbl2.setBorder(BorderFactory.createEtchedBorder());
		lblCount.setBorder(BorderFactory.createEtchedBorder());
		this.file = file;
		GridPanel pn = new GridPanel(true, 5);
		pn.place(0, 0, "Directory");
		pn.place(0, 1, lblDir);
		pn.place(1, 0, "Pattern");
		pn.place(1, 1, txt);
		pn.place(2, 0, "Example 1");
		pn.place(2, 1, lbl1);
		pn.place(3, 0, "Example 2");
		pn.place(3, 1, lbl2);
		pn.place(4, 0, "Count");
		pn.place(4, 1, lblCount);

		GridPanel bn = new GridPanel(false);
		bn.place(11, 0, bnCancel);
		bn.place(11, 1, bnCount);
		bn.place(11, 2, bnOK);

		pn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		bnCount.addActionListener(this);
		bnOK.addActionListener(this);
		bnCancel.addActionListener(this);
		txt.addKeyListener(this);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(pn, BorderLayout.CENTER);
		panel.add(bn, BorderLayout.SOUTH);
		add(panel);
		pack();
		update();

		addWindowListener(this);
		GUI.center(this);
		setModal(true);

	}

	private void update() {
		command = "";
		name = "";
		if (file == null)
			return;
		if (!file.exists())
			return;
		if (!file.isDirectory())
			return;

		String list[] = file.list();
		int count = 0;
		int n = list.length;
		String regex = txt.getText().trim();
		Pattern pattern = Pattern.compile(regex);
		for (int i = 0; i < n; i++)
			if (pattern.matcher(list[i]).find())
				count++;
		if (!regex.trim().equals(""))
			command = file.getAbsolutePath() + " pattern " + regex;
		else
			command = file.getAbsolutePath();
		name = file.getName();
		lblCount.setText("" + n + " files in dir, " + count + " matched files");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bnCount) {
			update();
		}
		else if (e.getSource() == bnCancel) {
			bnCount.removeActionListener(this);
			bnOK.removeActionListener(this);
			bnCancel.removeActionListener(this);
			txt.removeKeyListener(this);
			dispose();
			cancel = true;
			command = "";
			name = "";

			return;
		}
		else if (e.getSource() == bnOK) {
			update();
			bnCount.removeActionListener(this);
			bnOK.removeActionListener(this);
			bnCancel.removeActionListener(this);
			txt.removeKeyListener(this);
			dispose();
			cancel = false;
		}
	}

	public String getCommand() {
		return command;
	}

	public String getDirName() {
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
		command = "";
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

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		update();
	}
}
