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

package deconvolutionlab.module;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bilib.component.HTMLPane;
import deconvolution.Command;
import deconvolution.Deconvolution;
import deconvolution.DeconvolutionDialog;
import deconvolution.algorithm.AlgorithmPanel;
import deconvolution.algorithm.AlgorithmList;
import deconvolutionlab.Config;
import deconvolutionlab.Lab;

public class AlgorithmModule extends AbstractModule implements ActionListener, ChangeListener {

	private JComboBox<String>	cmb;
	private HTMLPane				doc;
	private JPanel				cards;
	private JLabel				shortname;
	
	public AlgorithmModule() {
		create("Algorithm", "-algorithm", "Check");
		ArrayList<AlgorithmPanel> deconv = AlgorithmList.getAvailableAlgorithms();
		for (AlgorithmPanel panel : deconv)
			cmb.addItem(panel.getName());
		cmb.addActionListener(this);
	}

	@Override
	public String getCommand() {
		String name = (String) cmb.getSelectedItem();
		AlgorithmPanel algo = AlgorithmList.getPanel(name);
		String cmd = "-algorithm " + algo.getShortnames()[0] + " " + algo.getCommand();
		String synopsis = algo.getShortnames()[0] + " " + algo.getCommand();
		setSynopsis(synopsis);
		setCommand(cmd);
		return cmd;
	}

	@Override
	public JPanel buildExpandedPanel() {
		shortname = new JLabel("-------");
		Border bl1 = BorderFactory.createEtchedBorder();
		Border bl2 = BorderFactory.createEmptyBorder(0, 10, 0, 10);
		shortname.setBorder(BorderFactory.createCompoundBorder(bl1, bl2));
		cmb = new JComboBox<String>();
		cmb.setBorder(BorderFactory.createEtchedBorder());
		
		JPanel pnc = new JPanel();
		pnc.add(cmb);
		doc = new HTMLPane(100, 1000);
		cards = new JPanel(new CardLayout());
		ArrayList<AlgorithmPanel> panels = AlgorithmList.getAvailableAlgorithms();
		
		for (AlgorithmPanel panel : panels) {
			JScrollPane scroll = new JScrollPane(panel.getPanelParameters());
			scroll.setBorder(BorderFactory.createEmptyBorder());
			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			cards.add(panel.getName(), scroll);
		}
		cmb.setMaximumRowCount(panels.size());

		JPanel top = new JPanel(new BorderLayout());
		top.add(cmb, BorderLayout.CENTER);
		top.add(shortname, BorderLayout.EAST);

		JPanel control = new JPanel();
		control.setLayout(new BoxLayout(control, BoxLayout.PAGE_AXIS));
		Border b1 = BorderFactory.createEtchedBorder();
		Border b2 = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		control.setBorder(BorderFactory.createCompoundBorder(b1, b2));
		cards.setBorder(BorderFactory.createEtchedBorder());
		
		control.add(top);
		control.add(cards);

		doc.append("h1", "Documentation");

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(control, BorderLayout.NORTH);
		panel.add(doc.getPane(), BorderLayout.CENTER);
		// cmb.addActionListener(this);
		getActionButton().setToolTipText("Human readable of the command line");
		getActionButton().addActionListener(this);
		Config.register(getName(), "algorithm", cmb, AlgorithmList.getDefaultAlgorithm());
		panel.setBorder(BorderFactory.createEtchedBorder());

		return panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		if (e.getSource() == cmb) {
			doc.clear();
			String name = (String) cmb.getSelectedItem();
			AlgorithmPanel algo = AlgorithmList.getPanel(name);
			doc.append(algo.getDocumentation());
			CardLayout cl = (CardLayout) (cards.getLayout());
			cl.show(cards, name);
			String s = "<html><b><p style =\"font-family:georgia\">";
			for(int i=0; i<algo.getShortnames().length; i++)
				s += (i==0 ? "" : " | ") + algo.getShortnames()[i] ;
			shortname.setText(s + "</b></html>");
		}
		if (e.getSource() == getActionButton()) {
			Deconvolution deconvolution = new Deconvolution("Check Algorithm", Command.buildCommand());
			DeconvolutionDialog d = new DeconvolutionDialog(DeconvolutionDialog.Module.ALGO, deconvolution);
			Lab.setVisible(d, false);
		}
		setSynopsis((String) cmb.getSelectedItem());
		setCommand(getCommand());
		Command.buildCommand();
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		setSynopsis((String) cmb.getSelectedItem());
		setCommand(getCommand());
		Command.buildCommand();
	}

	@Override
	public void close() {
	}
}
