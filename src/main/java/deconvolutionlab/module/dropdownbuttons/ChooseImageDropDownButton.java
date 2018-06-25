package deconvolutionlab.module.dropdownbuttons;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import deconvolution.Command;
import deconvolution.Deconvolution;
import deconvolutionlab.Lab;
import deconvolutionlab.Platform;
import deconvolutionlab.module.AbstractImageSelectionModule;
import deconvolutionlab.module.ReferenceModule;
import deconvolutionlab.monitor.Monitors;

public class ChooseImageDropDownButton extends AbstractDropDownButton implements ActionListener {

	private JMenuItem	remove		= new JMenuItem("Do not use any reference image");
	private JMenuItem	file			= new JMenuItem("Get from a file: 2D or 3D");
	private JMenuItem	directory	= new JMenuItem("Get directory: series of 2D images");
	private JMenuItem	synthetic	= new JMenuItem("Get synthetic: computed image");
	private JMenuItem	platform		= new JMenuItem("Get platform: image from the platform ");
	private JMenuItem	active		= new JMenuItem("Get platform active: selected window");
	
	private JMenuItem	fileOpen			= new JMenuItem("Open file: 2D or 3D");
	private JMenuItem	directoryOpen	= new JMenuItem("Open directory: series of 2D images");
	private JMenuItem	syntheticOpen	= new JMenuItem("Open synthetic: computed image");

	private AbstractImageSelectionModule module;
	private String		title		= "";

	public ChooseImageDropDownButton(String moduleName, AbstractImageSelectionModule module, String text) {
		super(moduleName, text, true);
		this.moduleName = moduleName;
		this.module = module;
		setPreferredSize(new Dimension(80, 22));
		JPopupMenu popup = new JPopupMenu();
		if (module instanceof ReferenceModule) {
			popup.add(remove);
			popup.addSeparator();					
			remove.addActionListener(this);
		}
			
		popup.add(file);
		popup.add(directory);
		popup.add(synthetic);
		if (Lab.getPlatform() == Platform.IMAGEJ) {
			popup.add(platform);
			popup.add(active);
			platform.addActionListener(this);
			active.addActionListener(this);
		}
		popup.addSeparator();
		popup.add(fileOpen);
		popup.add(directoryOpen);
		popup.add(syntheticOpen);

		file.addActionListener(this);
		directory.addActionListener(this);
		synthetic.addActionListener(this);
		fileOpen.addActionListener(this);
		directoryOpen.addActionListener(this);
		syntheticOpen.addActionListener(this);

		setPopupMenu(popup);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);

		if (e.getSource() == file) {
			module.addFromFile(Command.getPath());
			setReference(true);
		}
		else if (e.getSource() == directory) {
			module.addFromDirectory(Command.getPath());
			setReference(true);
		}
		if (e.getSource() == synthetic) {
			module.addFromSynthetic(false, moduleName);
			setReference(true);
		}
		else if (e.getSource() == platform) {
			module.addFromPlatform();
			setReference(true);
		}
		else if (e.getSource() == active) {
			module.addFromActive();
			setReference(true);
		}
		if (e.getSource() == fileOpen) {
			module.addFromFile(Command.getPath());
			setReference(true);
			show(moduleName);
		}
		else if (e.getSource() == directoryOpen) {
			module.addFromDirectory(Command.getPath());
			setReference(true);
			show(moduleName);
		}
		if (e.getSource() == syntheticOpen) {
			module.addFromSynthetic(false, moduleName);
			setReference(true);
			show(moduleName);
		}
		if (e.getSource() == remove) {
			setReference(false);
		}
		module.update();
	}
	
	private void setReference(boolean doReference) {
		if (module instanceof ReferenceModule) {
			ReferenceModule rm = (ReferenceModule)module;
			rm.setReference(doReference);
		}
		
	}
	private void show(String source) {
		Deconvolution deconvolution = new Deconvolution("Show", Command.buildCommand());
		Monitors m = Monitors.createDefaultMonitor();
		if (source.toLowerCase().startsWith("image")) Lab.show(m, deconvolution.openImage(), title);
		if (source.toLowerCase().startsWith("psf")) Lab.show(m, deconvolution.openPSF(), title);
		if (source.toLowerCase().startsWith("ref")) Lab.show(m, deconvolution.openReference(), title);
	}
}
