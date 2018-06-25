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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import deconvolutionlab.module.dropdownbuttons.AbstractDropDownButton;

public abstract class AbstractModule extends JPanel implements ActionListener {
	protected JButton			bnTitle			= new JButton("");
	protected JButton			bnSynopsis		= new JButton("");
	protected JButton			bnAction			= new JButton();
	protected JButton			bnExpand			= new JButton("\u25BA");
	private JLabel				lblCommand;
	protected CardLayout			card				= new CardLayout();
	private JPanel				space			= new JPanel(card);
	private boolean				expanded			= false;
	private GroupedModulePanel	mpns;
	private JPanel				pnExpanded;
	private JPanel				pnCollapsed;
	private String				name;
	private String				key;

	private int					heightButton	= 22;
	protected AbstractDropDownButton	button1;
	protected AbstractDropDownButton	button2;

	public void create(String name) {
		create(name, "", "", null, null);
	}

	public void create(String name, String key) {
		create(name, key, "", null, null);
	}

	public void create(String name, String key, AbstractDropDownButton button1) {
		create(name, key, "", button1, null);
	}
	
	public void create(String name, String key, AbstractDropDownButton button1, AbstractDropDownButton button2) {
		create(name, key, "", button1, button2);
	}
	
	public void create(String name, String key, String action, AbstractDropDownButton button1) {
		create(name, key, action, button1, null);
	}
	
	public void create(String name, String key, String action) {
		create(name, key, action, null, null);
	}

	public void create(String name, String key, String action, AbstractDropDownButton button1, AbstractDropDownButton button2) {
		this.name = name;
		this.key = key;
		pnCollapsed = buildCollapsedPanel();
		pnExpanded = buildExpandedPanel();
		setLayout(new BorderLayout());
		bnTitle.setText("<html><b>" + name + "</b></html>");
		bnTitle.setHorizontalAlignment(SwingConstants.LEFT);
		bnTitle.setPreferredSize(new Dimension(150, heightButton));
		bnTitle.setMaximumSize(new Dimension(250, heightButton));
		bnTitle.setMinimumSize(new Dimension(120, heightButton));
		bnTitle.setMargin(new Insets(1, 1, 1, 1));
		bnTitle.addActionListener(this);

		bnSynopsis.setHorizontalAlignment(SwingConstants.LEFT);
		bnSynopsis.setPreferredSize(new Dimension(200, heightButton));
		bnSynopsis.setMargin(new Insets(1, 1, 1, 1));
		bnSynopsis.addActionListener(this);

		Font font = bnExpand.getFont();
		bnExpand.setFont(new Font(font.getFamily(), font.getStyle(), font.getSize()-3));
		bnExpand.setPreferredSize(new Dimension(40, heightButton));
		bnExpand.setMaximumSize(new Dimension(40, heightButton));
		bnExpand.setMinimumSize(new Dimension(40, heightButton));
		bnExpand.setMargin(new Insets(1, 1, 1, 1));
		bnExpand.addActionListener(this);

		JPanel tool0 = new JPanel(new BorderLayout());
		JPanel tool1 = new JPanel(new BorderLayout());
		tool1.add(bnExpand, BorderLayout.WEST);
		tool1.add(bnTitle, BorderLayout.EAST);
		tool0.add(tool1, BorderLayout.WEST);
		tool0.add(bnSynopsis, BorderLayout.CENTER);

		JPanel toola = null;
		if (!action.equals("")) {
			bnAction.setText(action);
			bnAction.setPreferredSize(new Dimension(80, heightButton));
			bnAction.setMargin(new Insets(1, 1, 1, 1));
			if (toola == null)
				toola = new JPanel(new BorderLayout());
			toola.add(bnAction, BorderLayout.WEST);
		}
		if (button2 != null) {
			if (toola == null)
				toola = new JPanel(new BorderLayout());
			toola.add(button2, BorderLayout.WEST);
		}
		if (button1 != null) {
			if (toola == null)
				toola = new JPanel(new BorderLayout());
			toola.add(button1, BorderLayout.EAST);
		}

		if (toola != null)
			tool0.add(toola, BorderLayout.EAST);

		space.add(pnExpanded, "expand");
		space.add(pnCollapsed, "collapse");
		add(tool0, BorderLayout.NORTH);
		add(space, BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		if (expanded)
			expand();
		else
			collapse();
		setPreferredSize(pnCollapsed.getPreferredSize());
	}

	public JPanel buildCollapsedPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		lblCommand = new JLabel("");
		lblCommand.setBorder(BorderFactory.createEtchedBorder());
		lblCommand.setHorizontalAlignment(SwingConstants.LEFT);
		lblCommand.setPreferredSize(new Dimension(500, 32));
		panel.add(lblCommand, BorderLayout.NORTH);
		lblCommand.setMinimumSize(new Dimension(500, 36));
		return panel;
	}

	public abstract JPanel buildExpandedPanel();

	public abstract String getCommand();

	public abstract void close();

	public JButton getActionButton() {
		return bnAction;
	}
	
	@Override
	public String getName() {
		return name;
	}

	public String getKey() {
		return key;
	}

	public String getTitle() {
		return bnTitle.getText();
	}

	public void setCommand(String command) {
		lblCommand.setText("<html><p style=\"font-family: monospace\"><small>" + command + "</small></p></html>");
	}

	public void setSynopsis(String synopsis) {
		bnSynopsis.setText(synopsis);
	}

	public JPanel getCollapsedPanel() {
		return pnCollapsed;
	}

	public JPanel getExpandedPanel() {
		return pnExpanded;
	}

	public void setMultipleModulePanel(GroupedModulePanel mpns) {
		this.mpns = mpns;
	}

	@Override
	public Dimension getPreferredSize() {
		if (expanded)
			return pnExpanded.getPreferredSize();
		else
			return pnCollapsed.getPreferredSize();
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void collapse() {
		expanded = false;
		card.show(space, "collapse");
		bnExpand.setText("\u25BA");
	}

	public void expand() {
		expanded = true;
		card.show(space, "expand");
		bnExpand.setText("\u25BC");
	}

	public void open() {
		for (AbstractModule module : mpns.getModules())
			module.collapse();
		expand();
		mpns.organize();	
	}
		
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bnTitle || e.getSource() == bnExpand || e.getSource() == bnSynopsis) {
			if (expanded) {
				collapse();
			}
			else {
				for (AbstractModule module : mpns.getModules())
					module.collapse();
				expand();
			}
			mpns.organize();
		}
	}
}
