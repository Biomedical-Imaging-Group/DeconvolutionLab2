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

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import deconvolutionlab.LabPanel;

public class GroupedModulePanel extends JPanel {
	private ArrayList<AbstractModule>	modules;
	private LabPanel				parent;
	private Dimension				min;

	public GroupedModulePanel(ArrayList<AbstractModule> modules, LabPanel parent) {
		this.modules = modules;
		this.parent = parent;
		
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		int minWidth = 0;
		int minHeight = 0;
		for (AbstractModule mpn : modules) {
			mpn.setMultipleModulePanel(this);
			add(mpn);
			Dimension dim = mpn.getCollapsedPanel().getPreferredSize();
			minWidth = Math.max(minWidth, dim.width);
			minHeight += dim.height;
		}
		min = new Dimension(minWidth, minHeight);
	}

	public Dimension getMinimumSize() {
		return min;
	}

	public void organize() {
		parent.sizeModule();
	}

	public ArrayList<AbstractModule> getModules() {
		return modules;
	}

	public Dimension getVisibleSize(Dimension goal) {
		int height = 0;
		for (AbstractModule mpn : modules) {
			height += mpn.getPreferredSize().height;
		}
		if (height > goal.height) {
			return new Dimension(goal.width, height);
		}
		else {
			ArrayList<AbstractModule> visibles = new ArrayList<AbstractModule>();
			for (AbstractModule mpn : modules) {
				if (mpn.isExpanded())
					visibles.add(mpn);
			}
			if (visibles.size() > 0) {
				int supp = (goal.height - height) / visibles.size();
				for (AbstractModule mpn : modules) {
					Dimension dim = mpn.getPreferredSize();
					mpn.setPreferredSize(new Dimension(dim.width, dim.height + supp));
				}
			}
			return new Dimension(goal.width, goal.height);
		}
	}
}