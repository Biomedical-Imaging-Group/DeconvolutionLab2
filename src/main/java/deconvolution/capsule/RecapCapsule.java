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

package deconvolution.capsule;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JSplitPane;
import javax.swing.JViewport;
import javax.swing.text.BadLocationException;

import bilib.component.HTMLPane;
import bilib.table.CustomizedColumn;
import bilib.table.CustomizedTable;
import deconvolution.Deconvolution;

/**
 * This class is a information module for a recapitulation of the command line.
 * 
 * @author Daniel Sage
 * 
 */
public class RecapCapsule extends AbstractCapsule implements KeyListener {

	private HTMLPane	    pnCommand;
	private CustomizedTable	table;

	public RecapCapsule(Deconvolution deconvolution) {
		super(deconvolution);
		// Panel command
		pnCommand = new HTMLPane("Monaco", "#10FF10", "100020", 80, 80);
		pnCommand.append("p", deconvolution.getCommand());
		pnCommand.setEditable(true);
		pnCommand.addKeyListener(this);

		ArrayList<CustomizedColumn> columns = new ArrayList<CustomizedColumn>();
		columns.add(new CustomizedColumn("Feature", String.class, 150, false));
		columns.add(new CustomizedColumn("Value", String.class, 550, false));

		table = new CustomizedTable(columns, false);
		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, table.getPane(200, 200), pnCommand.getPane());
	}

	@Override
	public void update() {
		if (table == null)
			return;
		startAsynchronousTimer("Recap", 200);
		table.removeRows();
		for (String[] feature : deconvolution.recap())
			table.append(feature);
		Rectangle rect = table.getCellRect(0, 0, true);
		Point pt = ((JViewport) table.getParent()).getViewPosition();
		rect.setLocation(rect.x - pt.x, rect.y - pt.y);

		table.scrollRectToVisible(rect);
		split.setDividerLocation(0.7);
		split.repaint();
		stopAsynchronousTimer();
	}

	public String getCommand() {
		return pnCommand.getText();
	}

	@Override
	public String getID() {
		return "Recap";
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		try {
			int len = pnCommand.getDocument().getLength();
			String command = pnCommand.getDocument().getText(0, len);
			deconvolution.setCommand(command);
			table.removeRows();
			for (String[] feature : deconvolution.recap())
				table.append(feature);
		}
		catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}

}