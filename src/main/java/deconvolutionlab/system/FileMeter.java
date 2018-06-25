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

import java.awt.Graphics;
import java.io.File;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;

import bilib.tools.NumFormat;

public class FileMeter extends AbstractMeter {

	public FileMeter(int width) {
		super(width);
	}
	
	@Override
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    
		double maxi = SystemUsage.getTotalSpace();
		double used = maxi - SystemUsage.getAvailableSpace();
		String space = NumFormat.bytes(used);
	    int w = getWidth();
	    g.setColor(colorBackground);
	    for(int i=0; i<w; i+=w/10)
	    	g.drawLine(i, 0, i, 30);
	    
	    int posu = (int)Math.round(w*used/maxi);
	 	    g.setColor(colorHot);
   	    g.fillRect(0, 0, posu, 30);
 
	    g.setColor(colorText);
	    g.drawString(prefix + space, 10, 17);
	}

	@Override
	public void update() {
		repaint();
	}

	@Override
	public String getMeterName() {
		return "File";
	}

	@Override
	public void setDetail() {
		File[] roots = File.listRoots();
		int i=0;
		add(i++, new String[] { "Properties", "java.class.path", System.getProperty("java.class.path") });
		add(i++, new String[] { "Properties", "java.home", System.getProperty("java.home") });
		add(i++, new String[] { "Properties", "user.dir", System.getProperty("user.dir") });
		add(i++, new String[] { "Properties", "user.home", System.getProperty("user.home") });
		add(i++, new String[] { "Properties", "user.name", System.getProperty("user.name") });

		for (File root : roots) {
			add(i++, new String[] { "FileSystem", "Root Path", root.getAbsolutePath() });
			add(i++, new String[] { "FileSystem", "Total Space", NumFormat.bytes(root.getTotalSpace()) });
			add(i++, new String[] { "FileSystem", "Usable Space", NumFormat.bytes(root.getUsableSpace()) });
		}		
		ClassLoadingMXBean loader = ManagementFactory.getClassLoadingMXBean();
		add(i++, new String[] { "ClassLoading", "Loaded Class", "" + loader.getLoadedClassCount() });
	}
	
}