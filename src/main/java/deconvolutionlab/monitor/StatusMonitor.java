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

package deconvolutionlab.monitor;

import java.awt.FontMetrics;

import javax.swing.JProgressBar;

import deconvolutionlab.Constants;

public class StatusMonitor implements AbstractMonitor {

	private JProgressBar progress;
	
	public StatusMonitor(JProgressBar progress) {
		super();
		this.progress = progress;
		progress.setMinimum(0);
		progress.setMaximum(100);
		progress.setStringPainted(true);
	}

	@Override
	public void clear() {
		progress.setValue(0);
		progress.setString(Constants.copyright);
	}

	@Override
	public void add(Message message) {
		int w = progress.getWidth();
		if (w <= 0)
			return;
		String m = message.formatProgress();
		FontMetrics fm = progress.getFontMetrics(progress.getFont());
		int tw = fm.stringWidth(m);
		if (tw > w) {
			String ellipsis = "...";
			int ew = fm.stringWidth(ellipsis);
			while (tw + ew > w) {
				tw = fm.stringWidth(m);
				m = m.substring(0, m.length() - 1);
			}
			m += "...";
		}
		double p = message.getProgress() % 100;
		progress.setString(m);
		progress.setValue((int) Math.round(p));
	}
	
	@Override
	public String getName() {
		return "progress";
	}


}
