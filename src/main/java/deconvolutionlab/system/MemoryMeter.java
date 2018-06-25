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
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import bilib.tools.NumFormat;

public class MemoryMeter extends AbstractMeter {
	
	private double peak = 0;
	
	public MemoryMeter(int width) {
		super(width);
	}
	
	public void reset() {
		peak = 0;
	}
	
	@Override
	public void update() {
		repaint();
	}	
	
	@Override
	public void paintComponent(Graphics g) {
		MemoryUsage mem = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		double used = mem.getUsed();
		double maxi = mem.getMax();
		peak = Math.max(used, peak);
	    super.paintComponent(g);
	    int w = getWidth();
	    g.setColor(colorBackground);
	    for(int i=0; i<w; i+=w/10)
	    	g.drawLine(i, 0, i, 30);
	    
	    int posu = (int)Math.round(w*used/maxi);
   	    int posp = (int)Math.round(w*peak/maxi);
		String u = NumFormat.bytes(used); 

   	    g.setColor(colorHot);
   	    g.fillRect(0, 0, posu, 30);
   	    g.fillRect(0, 0, posp, 30);
   	    g.setColor(colorText);
  	    g.drawString(prefix + u, 10, 17);
  }

	@Override
	public String getMeterName() {
		return "Memory";
	}

	@Override
	public void setDetail() {
		MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
		MemoryUsage heapU = mem.getHeapMemoryUsage();
		MemoryUsage nonhU = mem.getNonHeapMemoryUsage();
		Runtime runt = Runtime.getRuntime();
		int i = 0;
		add(i++, new String[] { "JVM", "Initial Memory (-Xms)", NumFormat.bytes(runt.freeMemory()) });
		add(i++, new String[] { "JVM", "Maximum Memory (-Xmx)", NumFormat.bytes(runt.maxMemory()) });
		add(i++, new String[] { "JVM", "Total Used Memory", NumFormat.bytes(runt.totalMemory()) });
		
		add(i++, new String[] { "Memory", "Heap Used", NumFormat.bytes(heapU.getUsed()) });
		add(i++, new String[] { "Memory", "Heap Init", NumFormat.bytes(heapU.getInit()) });
		add(i++, new String[] { "Memory", "Heap Max ", NumFormat.bytes(heapU.getMax()) });

		add(i++, new String[] { "Memory", "NonHeap Used", NumFormat.bytes(nonhU.getUsed()) });
		add(i++, new String[] { "Memory", "NonHeap Init", NumFormat.bytes(nonhU.getInit()) });
	}
}