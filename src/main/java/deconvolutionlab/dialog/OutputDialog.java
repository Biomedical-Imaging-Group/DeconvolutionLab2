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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;

import bilib.component.GridPanel;
import bilib.component.HTMLPane;
import bilib.component.SpinnerRangeInteger;
import deconvolutionlab.output.Output;
import deconvolutionlab.output.Output.View;
import ij.gui.GUI;

public class OutputDialog extends JDialog implements ActionListener {

	private JComboBox<String>	cmbSnapshot	= new JComboBox<String>(new String[] { 
			"At the end of processing", "1", "2", "5", "10", "20", "50", "100", "200" });

	private SpinnerRangeInteger	spnX		= new SpinnerRangeInteger(128, 0, 99999, 1);
	private SpinnerRangeInteger	spnY		= new SpinnerRangeInteger(128, 0, 99999, 1);
	private SpinnerRangeInteger	spnZ		= new SpinnerRangeInteger(32, 0, 99999, 1);
	private JTextField			txtName		= new JTextField("Noname", 18);

	private JCheckBox			chkCenter	= new JCheckBox("Center of the volume", true);
	private JButton				bnOK		= new JButton("OK");
	private JButton				bnCancel	= new JButton("Cancel");
	private boolean				cancel		= false;
	private Output				out;
	private View					view;
	private GridPanel			pnOrtho;
	private HTMLPane				info = new HTMLPane(200, 200);
	
	private static int count	= 1;
	
	public OutputDialog(View view, int snapshot) {
		super(new JFrame(), "Create a new output");
		this.view = view;

		txtName.setText(view.name().substring(0, 2) + (count++));
		cmbSnapshot.setEditable(true);
		if (snapshot == 0)
			cmbSnapshot.setSelectedIndex(0);
		else
			cmbSnapshot.setSelectedItem(""+snapshot);
		GridPanel pn = new GridPanel(true);
		pn.place(0, 0, "Name");
		pn.place(0, 1, txtName);	
		pn.place(4, 0,  "Snapshot");
		pn.place(4, 1,  cmbSnapshot);


		GridPanel main = new GridPanel(false);
		main.place(1, 0, 2, 1, pn);
	
		if (view == View.ORTHO || view == View.FIGURE) {
			pn.place(9, 1, 3, 1, chkCenter);
			pnOrtho = new GridPanel("Keypoint");
			pnOrtho.place(4, 0, "Position in X");
			pnOrtho.place(4, 1, spnX);
			pnOrtho.place(4, 2, "[pixel]");

			pnOrtho.place(5, 0, "Position in Y");
			pnOrtho.place(5, 1, spnY);
			pnOrtho.place(5, 2, "[pixel]");

			pnOrtho.place(6, 0, "Position in Z");
			pnOrtho.place(6, 1, spnZ);
			pnOrtho.place(5, 2, "[pixel]");
			main.place(2, 0, 2, 1, pnOrtho);
		}

		pn.place(10, 0, 2, 1, info.getPane());
		
		main.place(3, 0, bnCancel);
		main.place(3, 1, bnOK);

		info();
		cmbSnapshot.addActionListener(this);
		chkCenter.addActionListener(this);
		bnOK.addActionListener(this);
		bnCancel.addActionListener(this);
		add(main);
		update();
		pack();
		GUI.center(this);
		setModal(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == chkCenter) {
			update();
		}
		else if (e.getSource() == cmbSnapshot) {
			update();
		}
		else if (e.getSource() == bnCancel) {
			dispose();
			cancel = true;
			return;
		}
		else if (e.getSource() == bnOK) {
			String name = txtName.getText();
			String s = cmbSnapshot.getSelectedItem().toString();
			int snapshot = s.matches("-?\\d+") ? Integer.parseInt(s) : 0;
			out = new Output(view, name).setSnapshot(snapshot);
			if (!chkCenter.isSelected()) 
				out.origin(spnX.get(), spnY.get(), spnZ.get());
			cancel = false;
			dispose();
		}
	}

	private void update() {
		boolean b = !chkCenter.isSelected();
		if (pnOrtho != null) {
			pnOrtho.setEnabled(b);
			for (Component c : pnOrtho.getComponents())
				c.setEnabled(b);
		}
		pack();
	}

	public Output getOut() {
		return out;
	}

	public boolean wasCancel() {
		return cancel;
	}
	
	private void info() {
		if (view == View.FIGURE) {
			info.append("h1", "figure");
			info.append("h2", "Create a view with 2 panels (XY) and (YZ) with a border.");
		}
		if (view == View.MIP) {
			info.append("h1", "mip");
			info.append("h2", "Create a view 3 orthogonal projections.");
		}
		if (view == View.ORTHO) {
			info.append("h1", "ortho");
			info.append("h2", "Create a view 3 orthogonal section centered on the origin.");
		}
		if (view == View.PLANAR) {
			info.append("h1", "planar");
			info.append("h2", "Create a montage of all Z-slice in one large flatten plane.");
		}
		if (view == View.STACK) {
			info.append("h1", "stack");
			info.append("h2", "Create a z-stack of image.");
		}
		if (view == View.SERIES) {
			info.append("h1", "series");
			info.append("h2", "Create a series of z-slices.");
		}

		info.append("p", "<b>Name:</b> This text will be used as title of the window in <i>show</i> "
				+ "mode or a filename in <i>save</i> mode.");
		info.append("p", "<b>Snapshot:</b> The output is computed (i.e. shown or/and saved) at the "
				+ "end of the processing. Optionally, for iteration algorithms, it is possible to specify "
				+ "a number of iteration (integer) to process intermediate snapshot.");
		info.append("p", "<b>Dynamic:</b>The dynamic range is used for the display. "
				+ "The default value is <i>intact</i> which preserves the true computed values.");
		info.append("p", "<b>Type:</b> The default value is <i>float</i> which preserves "
				+ "the true values without loosing precision.");
	}

}
