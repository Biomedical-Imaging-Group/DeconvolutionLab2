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

import bilib.tools.NumFormat;

public class JavaMeter extends AbstractMeter {

	public JavaMeter(int width) {
		super(width);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(colorText);
		g.drawString(prefix + "Java "+ System.getProperty("java.version") , 10, 17);
	}

	@Override
	public void update() {
		repaint();
	}

	@Override
	public String getMeterName() {
		return "Java";
	}

	@Override
	public void setDetail() {
		int i = 0;
		add(i++, new String[] { "Properties", "OS", System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch") });
		add(i++, new String[] { "Properties", "Java Version", System.getProperty("java.version") });
		/*
		// Discar the RuntimeMXBean info: too long time
		RuntimeMXBean rt = ManagementFactory.getRuntimeMXBean();
		add(i++, new String[] { "Runtime", "LibraryPath", rt.getLibraryPath() });
		add(i++, new String[] { "Runtime", "Name", rt.getName() });
		add(i++, new String[] { "Runtime", "VmVersion", rt.getVmVersion() });
		for (int k = 0; k<rt.getInputArguments().size(); k++) {
			String input = rt.getInputArguments().get(k);
			add(i++, new String[] { "Runtime", "Input Arguments " + (k+1), input });
		}
		 */
		Runtime runt = Runtime.getRuntime();
		add(i++, new String[] { "JVM", "Available processors", "" + runt.availableProcessors() });
		add(i++, new String[] { "JVM", "Initial Memory (-Xms)", NumFormat.bytes(runt.freeMemory()) });
		add(i++, new String[] { "JVM", "Maximum Memory (-Xmx)", NumFormat.bytes(runt.maxMemory()) });
		add(i++, new String[] { "JVM", "Total Used Memory", NumFormat.bytes(runt.totalMemory()) });
	
	}
}