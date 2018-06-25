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

package deconvolutionlab.system;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import deconvolutionlab.Config;
import deconvolutionlab.Constants;

public class RuntimeInfoPanel extends JPanel {

	private Timer				timer		= new Timer();
	private TimerTask			updater		= new Updater();
	private MemoryMeter			memory;
	private ProcessorMeter		processor;
	private int					width		= Constants.widthGUI;
	private ArrayList<AbstractMeter> meters = new ArrayList<AbstractMeter>();
	private long					rate = 0;
	

	public RuntimeInfoPanel(long rate) {
	
		this.rate = rate;
		memory = new MemoryMeter(100);
		processor = new ProcessorMeter(100);
		
		meters.add(memory);
		meters.add(processor);

		JPanel meters = new JPanel(new GridLayout(1, 3));			
		meters.add(memory);
		meters.add(processor);
		restart();

		// Panel Compact
		JPanel pnCompact = new JPanel();
		pnCompact.setPreferredSize(new Dimension(width, 20));

		JPanel top = new JPanel(new BorderLayout());
		top.add(meters, BorderLayout.CENTER);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(top, BorderLayout.NORTH);
	
		add(panel);
		setMinimumSize(new Dimension(width, 70));
		Rectangle rect = Config.getDialog("System.Frame");
		if (rect.x > 0 && rect.y > 0)
			setLocation(rect.x, rect.y);
		
		this.setBorder(BorderFactory.createEmptyBorder());
	}

	public void update() {
		processor.update();
		memory.update();
	}

	public void restart() {
		long refreshTime = rate;

		if (updater != null) {
			updater.cancel();
			updater = null;
		}
		updater = new Updater();
		timer.schedule(updater, 0, refreshTime);
	}

	private class Updater extends TimerTask {
		@Override
		public void run() {
			update();
		}
	}
}
