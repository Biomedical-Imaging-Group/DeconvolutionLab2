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
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import bilib.tools.NumFormat;
import deconvolutionlab.Config;
import deconvolutionlab.Constants;
import deconvolutionlab.Lab;
import fft.FFTPanel;
import signal.SignalCollector;

public class SystemInfo extends JDialog implements WindowListener, ActionListener, MouseListener {

	private JPanel				cards;

	private String[]			rates		= new String[] { "1 s.", "0.5 s.", "0.2 s.", "0.1 s.", "10 s.", "5 s.", "2 s." };
	private JButton				bnRate		= new JButton("1 s.");
	private JButton				bnClear		= new JButton("Clear");

	private Timer				timer		= new Timer();
	private TimerTask			updater		= new Updater();

	private MemoryMeter			memory;
	private ProcessorMeter		processor;
	private SignalMeter			signal;
	private FFTMeter			fft;
	private JavaMeter			java;
	private FileMeter			file;

	private int					width		= Constants.widthGUI;

	private static SystemInfo	instance;
	private int					rate = 0;
	
	private ArrayList<AbstractMeter> meters = new ArrayList<AbstractMeter>();
	
	public static void activate() {
		if (instance == null) {
			instance = new SystemInfo();
			Lab.setVisible(instance, false);
			Config.registerFrame("System", "Frame", instance);
			return;
		}
		if (!instance.isVisible())
			Lab.setVisible(instance, false);
		instance.toFront();
	}
	
	public static void close() {
		if (instance == null)
			return;
		if (instance.isVisible())
			instance.dispose();
	}

	private SystemInfo() {
		super(new JFrame(), "DeconvolutionLab2 System");

		double chrono = System.nanoTime();
		memory = new MemoryMeter(width/3);
		processor = new ProcessorMeter(width/3);
		signal = new SignalMeter(width/3);
		fft = new FFTMeter(width/3);
		java = new JavaMeter(width/3);
		file = new FileMeter(width/3);
		meters.add(memory);
		meters.add(processor);
		meters.add(signal);
		meters.add(fft);
		meters.add(java);
		meters.add(file);
	
		// Panel meters on top
		JPanel meters = new JPanel(new GridLayout(2, 3));			
		meters.add(file);
		meters.add(memory);
		meters.add(processor);
		meters.add(java);
		meters.add(signal);
		meters.add(fft);

		bnClear.setToolTipText("Clear all the entries");
		bnRate.setToolTipText("Choose the rate of refreshing the information");
		JPanel pan = new JPanel(new GridLayout(2, 1));
		pan.add(bnRate);
		pan.add(bnClear);

		restart();

		// Panel Compact
		JPanel pnCompact = new JPanel();
		pnCompact.setPreferredSize(new Dimension(width, 20));

		// Panel cards, compact is visible
		cards = new JPanel(new CardLayout());
		cards.add("collapse", pnCompact);
		cards.add(signal.getMeterName(), SignalCollector.getPanel(width, 200));
		cards.add(memory.getMeterName(), memory.getPanel(width, 200));
		cards.add(processor.getMeterName(), processor.getPanel(width, 200));
		cards.add(fft.getMeterName(), new FFTPanel(width, 200));
		cards.add(java.getMeterName(), java.getPanel(width, 200));
		cards.add(file.getMeterName(), file.getPanel(width, 200));

		cards.setVisible(false);

		JPanel top = new JPanel(new BorderLayout());
		top.add(meters, BorderLayout.CENTER);
		top.add(pan, BorderLayout.EAST);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(top, BorderLayout.NORTH);
		panel.add(cards, BorderLayout.CENTER);

		getContentPane().add(panel);
		bnClear.addActionListener(this);
		signal.addMouseListener(this);
		memory.addMouseListener(this);
		processor.addMouseListener(this);
		java.addMouseListener(this);
		file.addMouseListener(this);
		fft.addMouseListener(this);
		bnRate.addActionListener(this);
		setMinimumSize(new Dimension(width, 70));
		pack();
		bnClear.setEnabled(signal.isExpanded());
		Rectangle rect = Config.getDialog("System.Frame");
		if (rect.x > 0 && rect.y > 0)
			setLocation(rect.x, rect.y);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == bnRate) {
			rate++;
			if (rate >= rates.length)
				rate = 0;
			bnRate.setText(rates[rate]);
			restart();
		}

		if (e.getSource() == bnClear) {
			SignalCollector.clear();
		}

		pack();
	}

	public void update() {
		for(AbstractMeter meter : meters)
			meter.update();
	}

	public void restart() {
		long refreshTime = (long) (NumFormat.parseNumber(bnRate.getText(), 1) * 1000);

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

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() instanceof AbstractMeter) {
			AbstractMeter meter = (AbstractMeter) e.getSource();
			if (meter.isExpanded()) {
				meter.collapse();
				cards.setVisible(false);
			}
			else for(AbstractMeter m : meters) {
				if (m.isExpanded())
					m.collapse(); 
				meter.expand();
				cards.setVisible(true);
			}
			((CardLayout) (cards.getLayout())).show(cards, meter.getMeterName());
			pack();
		}
		bnClear.setEnabled(signal.isExpanded());
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		timer.cancel();
		timer = null;
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
}
