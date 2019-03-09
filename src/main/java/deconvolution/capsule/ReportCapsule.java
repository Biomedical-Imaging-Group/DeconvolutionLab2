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

import javax.swing.JSplitPane;

import bilib.table.CustomizedTable;
import deconvolution.Deconvolution;
import signal.RealSignal;

/**
 * This class is a information module for report the output.
 * 
 * @author Daniel Sage
 *
 */
public class ReportCapsule extends AbstractCapsule {

	private CustomizedTable table;
	
	public ReportCapsule(Deconvolution deconvolution) {
		super(deconvolution);
		table = new CustomizedTable(new String[] { "Output", "Values" }, false);
		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, table.getPane(300, 300), null);
	}
	
	@Override
	public void update() {
		if (table == null)
			return;
		table.removeRows();
		
		for (String[] feature : deconvolution.getDeconvolutionReports())
			table.append(feature);
		RealSignal image = deconvolution.getOutput();
		if (image == null) {
			table.append(new String[] {"Output", "No yet run"});
			return;
		}
		for (String[] feature : deconvolution.checkOutput())
			table.append(feature);
		split.setDividerLocation(300);
	}

	@Override
	public String getID() {
		return "Report";
	}
}
