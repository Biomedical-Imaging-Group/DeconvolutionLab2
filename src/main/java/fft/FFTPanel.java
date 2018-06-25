package fft;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import bilib.component.HTMLPane;
import bilib.table.CustomizedColumn;
import bilib.table.CustomizedTable;
import deconvolutionlab.Constants;

public class FFTPanel extends JPanel implements MouseListener {
	
	private HTMLPane			info;
	private CustomizedTable		table;
	private ArrayList<AbstractFFTLibrary> libs;

	public FFTPanel(int w, int h) {
		super(new BorderLayout());
		ArrayList<CustomizedColumn> columns = new ArrayList<CustomizedColumn>();
		columns.add(new CustomizedColumn("Name", String.class, 120, false));
		columns.add(new CustomizedColumn("Installed", String.class, 120, false));
		columns.add(new CustomizedColumn("Multithreadable", String.class, 120, false));
		columns.add(new CustomizedColumn("Location", String.class, Constants.widthGUI, false));
		table = new CustomizedTable(columns, true);
		table.setRowSelectionAllowed(true);
		table.addMouseListener(this);
		libs = FFT.getRegisteredLibraries();
		for (AbstractFFTLibrary lib : libs) {
			String name = lib.getLibraryName();
			String installed = lib.isInstalled() ? "Yes" : "No";
			String multit = lib.getDefaultFFT().isMultithreadable() ? "Yes" : "No";
			String location = lib.getLocation();
			table.append(new String[] { name, installed, multit, location });
		}
		info = new HTMLPane(w, h-100);
		
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, table.getPane(w, 100), info.getPane());
		split.setDividerLocation(0.5);
		add(split, BorderLayout.CENTER);
		table.setRowSelectionInterval(0, 0);
		info.clear();
		info.append("p", libs.get(0).getLicence());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int i = table.getSelectedRow();
		if (i<0)
			return;
		if (i>=libs.size())
			return;
		info.clear();
		info.append("p", libs.get(i).getLicence());
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub	
	}
}
