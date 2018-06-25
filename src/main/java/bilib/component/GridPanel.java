/*
 * bilib --- Java Bioimaging Library ---
 * 
 * Author: Daniel Sage, Biomedical Imaging Group, EPFL, Lausanne, Switzerland
 * 
 * Conditions of use: You are free to use this software for research or
 * educational purposes. In addition, we expect you to include adequate
 * citations and acknowledgments whenever you present or publish results that
 * are based on it.
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

package bilib.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class extends the JPanel to create grid panel given the possibility to
 * place Java components in an organized manner in the dialog box.
 * 
 * @author Daniel Sage, Biomedical Imaging Group, EPFL, Lausanne, Switzerland.
 * 
 */
public class GridPanel extends JPanel {

	private static final long	serialVersionUID	= 1L;
	private GridBagLayout		layout				= new GridBagLayout();
	private GridBagConstraints	constraint			= new GridBagConstraints();
	private int					defaultSpace		= 3;

	/**
	 * Constructor.
	 */
	public GridPanel() {
		super();
		setLayout(layout);
		setBorder(BorderFactory.createEtchedBorder());
	}

	/**
	 * Constructor.
	 */
	public GridPanel(int defaultSpace) {
		super();
		setLayout(layout);
		this.defaultSpace = defaultSpace;
		setBorder(BorderFactory.createEtchedBorder());
	}

	/**
	 * Constructor.
	 */
	public GridPanel(boolean border) {
		super();
		setLayout(layout);
		if (border) {
			setBorder(BorderFactory.createEtchedBorder());
		}
	}

	/**
	 * Constructor.
	 */
	public GridPanel(String title) {
		super();
		setLayout(layout);
		setBorder(BorderFactory.createTitledBorder(title));
	}

	/**
	 * Constructor.
	 */
	public GridPanel(boolean border, int defaultSpace) {
		super();
		setLayout(layout);
		this.defaultSpace = defaultSpace;
		if (border) {
			setBorder(BorderFactory.createEtchedBorder());
		}
	}

	/**
	 * Constructor.
	 */
	public GridPanel(String title, int defaultSpace) {
		super();
		setLayout(layout);
		this.defaultSpace = defaultSpace;
		setBorder(BorderFactory.createTitledBorder(title));
	}

	/**
	 * Specify the defaultSpace.
	 */
	public void setSpace(int defaultSpace) {
		this.defaultSpace = defaultSpace;
	}

	/**
	 * Place a component in the northwest of the cell.
	 */
	public void place(int row, int col, String label) {
		place(row, col, 1, 1, defaultSpace, new JLabel(label));
	}

	/**
	 * Place a component in the northwest of the cell.
	 */
	public void place(int row, int col, int space, String label) {
		place(row, col, 1, 1, space, new JLabel(label));
	}

	/**
	 * Place a component in the northwest of the cell.
	 */
	public void place(int row, int col, int width, int height, String label) {
		place(row, col, width, height, defaultSpace, new JLabel(label));
	}

	/**
	 * Place a component in the northwest of the cell.
	 */
	public void place(int row, int col, JComponent comp) {
		place(row, col, 1, 1, defaultSpace, comp);
	}

	/**
	 * Place a component in the northwest of the cell.
	 */
	public void place(int row, int col, int space, JComponent comp) {
		place(row, col, 1, 1, space, comp);
	}

	/**
	 * Place a component in the northwest of the cell.
	 */
	public void place(int row, int col, int width, int height, JComponent comp) {
		place(row, col, width, height, defaultSpace, comp);
	}

	/**
	 * Place a component in the northwest of the cell.
	 */
	public void place(int row, int col, int width, int height, int space, JComponent comp) {
		if (comp == null)
			return;
		constraint.gridx = col;
		constraint.gridy = row;
		constraint.gridwidth = width;
		constraint.gridheight = height;
		constraint.anchor = GridBagConstraints.NORTHWEST;
		constraint.insets = new Insets(space, space, space, space);
		constraint.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(comp, constraint);
		add(comp);
	}

	/**
	 * Place a component in the northwest of the cell.
	 */
	public void place(int row, int col, int width, int height, int spaceHorizontal, int spaceVertical, JComponent comp) {
		if (comp == null)
			return;
		constraint.gridx = col;
		constraint.gridy = row;
		constraint.gridwidth = width;
		constraint.gridheight = height;
		constraint.anchor = GridBagConstraints.NORTHWEST;
		constraint.insets = new Insets(spaceVertical, spaceHorizontal, spaceHorizontal, spaceVertical);
		constraint.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(comp, constraint);
		add(comp);
	}
}
