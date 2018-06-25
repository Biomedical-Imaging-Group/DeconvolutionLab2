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

package deconvolutionlab;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bilib.component.PanelImage;
import deconvolution.Command;
import deconvolution.Deconvolution;
import deconvolutionlab.dialog.BatchDialog;
import deconvolutionlab.module.AboutModule;
import deconvolutionlab.module.AbstractModule;
import deconvolutionlab.module.AlgorithmModule;
import deconvolutionlab.module.BatchModule;
import deconvolutionlab.module.CommandModule;
import deconvolutionlab.module.ConfigModule;
import deconvolutionlab.module.ControllerModule;
import deconvolutionlab.module.DirectoryModule;
import deconvolutionlab.module.GroupedModulePanel;
import deconvolutionlab.module.ImageModule;
import deconvolutionlab.module.LanguageModule;
import deconvolutionlab.module.LicenceModule;
import deconvolutionlab.module.OutputModule;
import deconvolutionlab.module.PSFModule;
import deconvolutionlab.module.PreprocessingModule;
import deconvolutionlab.module.ReferenceModule;
import deconvolutionlab.module.ResourcesModule;

/**
 * This class build the main panel for DeconvolutionLab2. It consists to a
 * series of collapse/expanded modules that are placed in different tabs. The size
 * of the panel is dynamically computed,
 * 
 * @author Daniel Sage
 * 
 */
public class LabPanel extends JPanel implements ActionListener, ChangeListener {

	private JTabbedPane	        tab	     = new JTabbedPane();
	private JButton	            bnHelp	 = new JButton("Help");
	private JButton	            bnQuit	 = new JButton("Quit");
	private JButton	            bnBatch	 = new JButton("Batch");
	private JButton	            bnRun	 = new JButton("Run");
	private JButton	            bnLaunch	= new JButton("Launch");
	private JButton	            bnClose;

	private ImageModule	        image;
	private PSFModule	        psf;
	private AlgorithmModule	    algo;
	private AboutModule	        about;
	private LicenceModule	    licence;
	private OutputModule	    output;
	private PreprocessingModule	preprocessing;
	private ConfigModule	    config;
	private BatchModule	        batch;
	private LanguageModule	    language;
	private CommandModule	    command;
	private DirectoryModule	    directory;
	private ResourcesModule	    resources;
	private ReferenceModule	    reference;

	private ControllerModule	controller;

	private GroupedModulePanel	panelDeconv;
	private GroupedModulePanel	panelAdvanc;
	private GroupedModulePanel	panelScript;
	private GroupedModulePanel	panelAbout;
	private AbstractModule	    modules[];

	public LabPanel(JButton bnClose) {
		this.bnClose = bnClose;
		image = new ImageModule();
		psf = new PSFModule();
		algo = new AlgorithmModule();
		output = new OutputModule();
		preprocessing = new PreprocessingModule();
		controller = new ControllerModule();
		batch = new BatchModule();
		language = new LanguageModule();
		about = new AboutModule();
		licence = new LicenceModule();
		config = new ConfigModule();
		command = new CommandModule();
		directory = new DirectoryModule();
		resources = new ResourcesModule();
		reference = new ReferenceModule();

		modules = new AbstractModule[] { image, psf, algo, output, controller, preprocessing, batch, directory, resources, reference };
		Command.active(modules, command, language);
		Command.buildCommand();

		panelDeconv = new GroupedModulePanel(buildDeconvolutionPanel(), this);
		panelAdvanc = new GroupedModulePanel(buildAdvancedPanel(), this);
		panelScript = new GroupedModulePanel(buildProgrammingPanel(), this);
		panelAbout = new GroupedModulePanel(buildAboutPanel(), this);
		Border border = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		PanelImage bottom = new PanelImage("celegans.jpg");
		bottom.setBorder(border);

		bottom.setLayout(new GridLayout(1, 6));
		bottom.setBorder(border);

		bottom.add(bnHelp);
		bottom.add(bnClose);
		bottom.add(bnBatch);
		bottom.add(bnRun);
		bottom.add(bnLaunch);

		tab.add("Deconvolution", panelDeconv);
		tab.add("Advanced", panelAdvanc);
		tab.add("Scripting", panelScript);
		tab.add("About", panelAbout);
		tab.addChangeListener(this);

		setLayout(new BorderLayout());
		add(tab, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);

		bnBatch.addActionListener(this);
		bnRun.addActionListener(this);
		bnLaunch.addActionListener(this);
		bnClose.addActionListener(this);
		bnQuit.addActionListener(this);
		bnHelp.addActionListener(this);

		((GroupedModulePanel) tab.getSelectedComponent()).organize();
		setMinimumSize(new Dimension(500, 500));

		Config.load();
		algo.open();
		controller.open();
		about.open();
		command.open();

		// sizeModule();
		Command.buildCommand();
		image.update();
		psf.update();
		output.update();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bnHelp)
			Lab.help();
		else if (e.getSource() == bnClose)
			Config.store();
		else if (e.getSource() == bnBatch) {
			tab.setSelectedIndex(2);
			batch.expand();
			sizeModule();
			BatchDialog dlg = new BatchDialog(batch);
			Lab.setVisible(dlg, true);
		}
		else if (e.getSource() == bnLaunch) {
			String job = language.getJobName() + " " + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
			Deconvolution d = new Deconvolution(job, Command.buildCommand(), Deconvolution.Finish.ALIVE);
			d.launch();
		}
		else if (e.getSource() == bnRun) {
			String job = language.getJobName() + " " + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
			Deconvolution d = new Deconvolution(job, Command.buildCommand());
			d.deconvolve();
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		((GroupedModulePanel) tab.getSelectedComponent()).organize();
		Command.buildCommand();
	}

	private ArrayList<AbstractModule> buildDeconvolutionPanel() {
		ArrayList<AbstractModule> list = new ArrayList<AbstractModule>();
		list.add(image);
		list.add(psf);
		list.add(algo);
		list.add(directory);
		return list;
	}

	private ArrayList<AbstractModule> buildAdvancedPanel() {
		ArrayList<AbstractModule> list = new ArrayList<AbstractModule>();
		list.add(output);
		list.add(controller);
		list.add(reference);
		list.add(preprocessing);
		list.add(resources);
		return list;
	}

	private ArrayList<AbstractModule> buildProgrammingPanel() {
		ArrayList<AbstractModule> list = new ArrayList<AbstractModule>();
		list.add(batch);
		list.add(command);
		list.add(language);
		return list;
	}

	private ArrayList<AbstractModule> buildAboutPanel() {
		ArrayList<AbstractModule> list = new ArrayList<AbstractModule>();
		list.add(about);
		list.add(licence);
		list.add(config);
		return list;
	}

	public void close() {
		for (AbstractModule module : modules)
			module.close();
		bnLaunch.removeActionListener(this);
		bnRun.removeActionListener(this);
		bnBatch.removeActionListener(this);
		bnClose.removeActionListener(this);
		bnHelp.removeActionListener(this);

		Lab.close();
	}

	public void sizeModule() {
		if (tab.getSelectedIndex() == 0)
			sizePanel(panelDeconv);
		if (tab.getSelectedIndex() == 1)
			sizePanel(panelAdvanc);
		if (tab.getSelectedIndex() == 2)
			sizePanel(panelScript);
		if (tab.getSelectedIndex() == 3)
			sizePanel(panelAbout);
	}

	private void sizePanel(GroupedModulePanel panel) {
		Dimension dim = getSize();
		int hpc = 70;
		int npc = hpc * panel.getModules().size();
		Dimension small = new Dimension(dim.width, hpc);
		Dimension large = new Dimension(dim.width, dim.height - npc);
		setMinimumSize(new Dimension(Constants.widthGUI, 4 * hpc));
		for (AbstractModule module : panel.getModules()) {
			if (module.isExpanded()) {
				module.setPreferredSize(large);
				module.setMaximumSize(large);
				module.setMinimumSize(small);
				module.getExpandedPanel().setPreferredSize(large);
				module.getExpandedPanel().setMaximumSize(large);
				module.getExpandedPanel().setMinimumSize(small);
			}
			else {
				module.setPreferredSize(small);
				module.setMaximumSize(small);
				module.setMinimumSize(small);
				module.getCollapsedPanel().setPreferredSize(small);
				module.getCollapsedPanel().setMaximumSize(small);
				module.getCollapsedPanel().setMinimumSize(small);
			}
		}
	}
}
