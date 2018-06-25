package deconvolutionlab.module;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JPanel;

import bilib.table.CustomizedColumn;
import bilib.table.CustomizedTable;
import bilib.tools.Files;
import deconvolutionlab.Constants;
import deconvolutionlab.Lab;
import deconvolutionlab.dialog.PatternDialog;
import deconvolutionlab.dialog.SyntheticDialog;
import signal.factory.SignalFactory;

public abstract class AbstractImageSelectionModule extends AbstractModule implements MouseListener {

	protected CustomizedTable	table;

	public AbstractImageSelectionModule(String name) {
		ArrayList<CustomizedColumn> columns = new ArrayList<CustomizedColumn>();
		columns.add(new CustomizedColumn("Name", String.class, 100, false));
		columns.add(new CustomizedColumn("Source", String.class, 100, false));
		columns.add(new CustomizedColumn("Command", String.class, Constants.widthGUI - 200, true));
		columns.add(new CustomizedColumn("", String.class, 30, "\u232B", "Delete this " + name  ));
		table = new CustomizedTable(columns, true);
		table.getColumnModel().getColumn(3).setMaxWidth(30);
		table.getColumnModel().getColumn(3).setMinWidth(30);	
		table.addMouseListener(this);
	}
	
	public abstract void edit();
	
	public abstract void update();
	
	public abstract JPanel buildExpandedPanel();

	public abstract String getCommand();

	public void platform() {
		String name = Lab.getActiveImage();
		if (name != "")
			table.insert(new String[] { name, "platform", name, "\u232B" });
	}

	public void addFromFile(String path) {
		File file = Files.browseFile(path);
		if (file == null)
			return;
		table.insert(new String[] { file.getName(), "file", file.getAbsolutePath(), "\u232B" });
	}

	public void addFromDirectory(String path) {
		File file = Files.browseDirectory(path);
		if (file == null)
			return;
		PatternDialog dlg = new PatternDialog(file);
		Lab.setVisible(dlg, true);

		if (dlg.wasCancel())
			return;
		table.insert(new String[] { dlg.getDirName(), "directory", dlg.getCommand(), "\u232B" });
	}

	public void addFromSynthetic(boolean edit, String source) {
		ArrayList<SignalFactory> list = SignalFactory.getImages();
		if (source.toLowerCase().startsWith("psf"))
			list = SignalFactory.getPSF();
		SyntheticDialog dlg = new SyntheticDialog(list);
		if (edit) {
			int row = table.getSelectedRow();
			if (row >= 0) {
				dlg.setParameters(table.getCell(row, 0), table.getCell(row, 2));
			}
		}
		Lab.setVisible(dlg, true);
		if (dlg.wasCancel())
			return;
		if (edit) {
			int row = table.getSelectedRow();
			if (row <= 0)
				table.removeRow(row);
		}
		table.insert(new String[] { dlg.getShapeName(), "synthetic", dlg.getCommand(), "\u232B" });
	}
	
	public void addFromPlatform() {
		String name = Lab.getActiveImage();
		if (name != "")
			table.insert(new String[] {name, "platform", name, "\u232B" });
	}

	public void addFromActive() {
		int row = -1;
		for(int i=0; i<table.getRowCount(); i++) {
			if (table.getCell(i, 0).equalsIgnoreCase("active"))
			if (table.getCell(i, 1).equalsIgnoreCase("platform"))
			if (table.getCell(i, 2).equalsIgnoreCase("active"))
				row = i;
		}
		if (row < 0) 
			table.insert(new String[] { "active", "platform", "active", "\u232B" });
		else
			table.setRowSelectionInterval(row, row);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == table) {
			int row = table.getSelectedRow();
			if (row < 0)
				return;
			if (table.getSelectedColumn() == 3) {
				table.removeRow(row);
				if (table.getRowCount() > 0)
					table.setRowSelectionInterval(0, 0);
			}
			update();
			if (e.getClickCount() == 2) {
				edit();
			}
		}
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
	
	public void close() {
		table.removeMouseListener(this);	
	}

}
