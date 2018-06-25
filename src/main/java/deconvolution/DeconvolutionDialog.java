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

package deconvolution;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToolBar;

import bilib.component.BorderToggledButton;
import bilib.component.PanelImage;
import deconvolution.capsule.AlgorithmCapsule;
import deconvolution.capsule.ImageCapsule;
import deconvolution.capsule.PSFCapsule;
import deconvolution.capsule.RecapCapsule;
import deconvolution.capsule.ReportCapsule;
import deconvolution.capsule.ResourcesCapsule;
import deconvolutionlab.Config;
import deconvolutionlab.Constants;
import deconvolutionlab.Lab;
import deconvolutionlab.monitor.AbstractMonitor;
import deconvolutionlab.monitor.Monitors;
import deconvolutionlab.monitor.StatusMonitor;
import deconvolutionlab.monitor.TableMonitor;
import deconvolutionlab.system.RuntimeInfoPanel;

public class DeconvolutionDialog extends JDialog implements WindowListener, ActionListener, Runnable {

	public enum State {
		NOTDEFINED, READY, RUN, FINISH
	};

	public enum Module {
		ALL, RECAP, IMAGE, PSF, ALGO, RUN, REFERENCE
	};

	private JButton	bnStart	= new JButton("Run");
	private JButton	bnStop	= new JButton("Stop");
	private JButton	bnReset	= new JButton("Reset");
	private JButton	bnHelp	= new JButton("Help");
	private JButton	bnQuit	= new JButton("Quit");

	private BorderToggledButton	bnRecap		= new BorderToggledButton("Recap");
	private BorderToggledButton	bnResources	= new BorderToggledButton("Resources");
	private BorderToggledButton	bnImage		= new BorderToggledButton("Image");
	private BorderToggledButton	bnPSF		= new BorderToggledButton("PSF");
	private BorderToggledButton	bnAlgo		= new BorderToggledButton("Algorithm");
	private BorderToggledButton	bnReport		= new BorderToggledButton("Report");
	private BorderToggledButton	bnMonitor	= new BorderToggledButton("Monitor");
	private BorderToggledButton	bnStats		= new BorderToggledButton("Stats");
	private JToolBar			tool		= new JToolBar();

	private Thread thread = null;

	private Deconvolution	deconvolution;
	private JProgressBar	progress	= new JProgressBar();

	public static Point location = new Point(0, 0);

	private JPanel cards = new JPanel(new CardLayout());

	private boolean	flagMonitor	= false;
	private boolean	flagStats	= false;

	private ImageCapsule		image;
	private PSFCapsule			psf;
	private RecapCapsule		recap;
	private AlgorithmCapsule	algorithm;
	private ReportCapsule		report;
	private ResourcesCapsule	resources;

	public DeconvolutionDialog(Module module, Deconvolution deconvolution) {
		super(new JFrame(), deconvolution.getName());

		this.deconvolution = deconvolution;

		image = new ImageCapsule(deconvolution);
		psf = new PSFCapsule(deconvolution);
		recap = new RecapCapsule(deconvolution);
		algorithm = new AlgorithmCapsule(deconvolution);
		report = new ReportCapsule(deconvolution);
		resources = new ResourcesCapsule(deconvolution);

		RuntimeInfoPanel info = new RuntimeInfoPanel(250);
		// Panel status bar on bottom
		progress.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		progress.setBorder(BorderFactory.createLoweredBevelBorder());
		JToolBar statusBar = new JToolBar();
		statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
		statusBar.setFloatable(false);
		statusBar.setLayout(new BorderLayout());
		statusBar.add(info, BorderLayout.WEST);
		statusBar.add(progress, BorderLayout.CENTER);
		statusBar.add(bnQuit, BorderLayout.EAST);

		// Panel bottom
		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.PAGE_AXIS));

		// Panel buttons
		if (module == Module.ALL) {
			PanelImage buttons = new PanelImage("celegans.jpg");
			buttons.setLayout(new FlowLayout());
			buttons.setBorder(BorderFactory.createEtchedBorder());
			buttons.add(bnReset);
			buttons.add(bnStop);
			buttons.add(bnStart);
			bottom.add(buttons);
		}
		bottom.add(statusBar);

		// Panel Image
		cards.add(recap.getID(), recap.getPane());
		cards.add(resources.getID(), resources.getPane());
		cards.add(image.getID(), image.getPane());
		cards.add(psf.getID(), psf.getPane());
		cards.add(algorithm.getID(), algorithm.getPane());
		cards.add(report.getID(), report.getPane());

		// Panel Main
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(cards, BorderLayout.CENTER);
		panel.add(bottom, BorderLayout.SOUTH);

		// Panel tool with all buttons
		if (module == Module.ALL) {
			tool.setFloatable(false);
			tool.setLayout(new GridLayout(1, 6));
			tool.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			tool.add(bnRecap);
			tool.add(bnResources);
			tool.add(bnAlgo);
			tool.add(bnImage);
			tool.add(bnPSF);
			tool.add(bnReport);
			panel.add(tool, BorderLayout.NORTH);
		}

		add(panel);
		bnReset.addActionListener(this);
		bnQuit.addActionListener(this);
		bnStart.addActionListener(this);
		bnStop.addActionListener(this);
		bnHelp.addActionListener(this);

		bnImage.addActionListener(this);
		bnResources.addActionListener(this);
		bnPSF.addActionListener(this);
		bnAlgo.addActionListener(this);
		bnRecap.addActionListener(this);
		bnReport.addActionListener(this);

		this.addWindowListener(this);
		setMinimumSize(new Dimension(Constants.widthLaunchGUI, Constants.heightLaunchGUI));
		setPreferredSize(new Dimension(Constants.widthLaunchGUI, Constants.heightLaunchGUI));
		pack();
		Config.registerFrame("DeconvolutionLab", "DeconvolutionDialog", this);

		Rectangle rect = Config.getDialog("DeconvolutionLab.DeconvolutionDialog");
		if (rect.x > 0 && rect.y > 0) setLocation(rect.x, rect.y);

		bnStop.setEnabled(false);

		if (module == Module.ALL) {
			toggle(bnRecap);
			recap.update();
		}
		if (module == Module.IMAGE) {
			toggle(bnImage);
			image.update();
		}
		if (module == Module.PSF) {
			toggle(bnPSF);
			psf.update();
		}
		if (module == Module.ALGO) {
			toggle(bnAlgo);
			algorithm.update();
		}
		if (module == Module.RECAP) {
			toggle(bnRecap);
			recap.update();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bnStart) {
			if (flagMonitor)
				toggle(bnMonitor);
			else if (flagStats) toggle(bnStats);

			if (thread == null) {
				thread = new Thread(this);
				thread.setPriority(Thread.MIN_PRIORITY);
				thread.start();
			}
		}
		else if (e.getSource() == bnStop) {
			toggle(bnReport);
			if (deconvolution != null) deconvolution.abort();
		}
		else if (e.getSource() == bnResources) {
			toggle(bnResources);
			resources.update();
		}
		else if (e.getSource() == bnImage) {
			toggle(bnImage);
			image.update();
		}
		else if (e.getSource() == bnPSF) {
			toggle(bnPSF);
			psf.update();
		}
		else if (e.getSource() == bnAlgo) {
			toggle(bnAlgo);
			algorithm.update();
		}
		else if (e.getSource() == bnRecap) {
			toggle(bnRecap);
			recap.update();
		}
		else if (e.getSource() == bnReport) {
			toggle(bnReport);
			report.update();
		}
		else if (e.getSource() == bnReset) {
			toggle(bnRecap);
			bnStart.setEnabled(true);
		}
		else if (e.getSource() == bnQuit) {
			deconvolution.close();
			deconvolution = null;
			dispose();
		}
		else if (e.getSource() == bnHelp)
			Lab.help();
		else if (e.getSource() == bnMonitor)
			toggle(bnMonitor);
		else if (e.getSource() == bnStats) toggle(bnStats);

		addProgress();

	}

	@Override
	public void run() {
		bnStart.setEnabled(false);
		bnStop.setEnabled(true);

		deconvolution.setCommand(recap.getCommand());
		addProgress();
		deconvolution.run();
		toggle(bnReport);

		bnStop.setEnabled(false);
		report.update();
		thread = null;
	}

	private void addProgress() {
		Monitors monitors = Monitors.createDefaultMonitor();
		if (deconvolution != null)
			if (deconvolution.getController() != null)
				monitors = deconvolution.getController().getMonitors();
		boolean found = false;
		for (AbstractMonitor monitor : monitors) {
			if (monitor instanceof StatusMonitor) found = true;
		}
		if (!found) {
			monitors.add(new StatusMonitor(progress));
			deconvolution.getController().setMonitors(monitors);
		}
	}

	public void addStats(Stats stats) {
		if (stats != null) {
			cards.add("Stats", stats.getPanel());
			tool.add(bnStats);
			pack();
			bnStats.addActionListener(this);
		}
	}

	public void addMonitor(TableMonitor tableMonitor) {
		if (tableMonitor != null) {
			cards.add("Monitor", tableMonitor.getPanel());
			tool.add(bnMonitor);
			pack();
			bnMonitor.addActionListener(this);
		}
	}

	public static void setLocationLaunch(Point l) {
		location = l;
	}

	private void toggle(BorderToggledButton bn) {
		((CardLayout) (cards.getLayout())).show(cards, bn.getText());
		bnRecap.setSelected(false);
		bnResources.setSelected(false);
		bnImage.setSelected(false);
		bnPSF.setSelected(false);
		bnAlgo.setSelected(false);
		bnMonitor.setSelected(false);
		bnStats.setSelected(false);
		bnReport.setSelected(false);
		bn.setSelected(true);
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		deconvolution.close();
		deconvolution = null;
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
