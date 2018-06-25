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

import java.awt.Color;
import java.awt.Graphics;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import bilib.tools.NumFormat;

public class ProcessorMeter extends AbstractMeter {
		
	public ProcessorMeter(int width) {
		super(width);
	}
	
	@Override
	public void update() {
		repaint();
	}	
	
	@Override
	public void paintComponent(Graphics g) {
		double used = SystemUsage.getLoad();
		double maxi = 100;
	    super.paintComponent(g);
	    int w = getWidth();
	    g.setColor(new Color(10, 10, 10, 30));
	    for(int i=0; i<w; i+=w/10)
	    	g.drawLine(i, 0, i, 30);
	    
	    int posu = (int)Math.round(w*used/maxi);
	    String u = String.format("%3.3f", used); 

   	    g.setColor(colorHot);
   	    g.fillRect(0, 0, posu, 30);
   	    g.setColor(colorText);
  	    g.drawString(prefix + u + "%", 10, 17);
 	}

	@Override
	public String getMeterName() {
		return "Processor";
	}

	@Override
	public void setDetail() {
		Runtime runt = Runtime.getRuntime();
		int i = 0;
		RuntimeMXBean rt = ManagementFactory.getRuntimeMXBean();
		add(i++, new String[] { "JVM", "Available processors", "" + runt.availableProcessors() });
		add(i++, new String[] { "Runtime", "Uptime", "" + NumFormat.time(rt.getUptime()*1e6) });
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
		for (Method method : os.getClass().getDeclaredMethods()) {
			method.setAccessible(true);
			if (method.getName().startsWith("get") && Modifier.isPublic(method.getModifiers())) {
				try {
					String name = split(method.getName());
					if (name.contains("Size"))
						add(i++, new String[] { "OS", name, NumFormat.bytes(Double.parseDouble(method.invoke(os).toString())) });
					else if (name.contains("Time"))
						add(i++, new String[] { "OS", name, NumFormat.time(Double.parseDouble(method.invoke(os).toString())) });
					else if (name.contains("Load"))
						add(i++, new String[] { "OS", name, NumFormat.nice(100 * Double.parseDouble(method.invoke(os).toString()))+"%" });
					else
						add(i++, new String[] { "OS", name, "" + method.invoke(os).toString() });
				}
				catch (Exception e) {
				}
			}
		}		
	}


}