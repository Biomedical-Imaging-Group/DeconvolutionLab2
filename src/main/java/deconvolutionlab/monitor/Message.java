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

package deconvolutionlab.monitor;

import bilib.tools.NumFormat;
import deconvolutionlab.system.SystemUsage;

public class Message {
	
	private static long id = 0; 
	private double chrono;
	private String mem;
	private Verbose level;
	private String message;
	private double progress;
	
	public Message(Verbose level, String message, double chrono, double progress) {
		id = id+1;
		this.chrono = chrono;
		this.mem = SystemUsage.getMemoryMB();
		this.message = message;
		this.level = level;
		this.progress = progress;
	}
	
	public long getID() {
		return id;
	}

	public String getMessage() {
		return message;
	}

	public Verbose getLevel() {
		return level;
	}

	public double getProgress() {
		return progress;
	}

	public String formatProgress() {
		return NumFormat.time(chrono) + " \t " + message + " (" + progress + "%)";
	}
	
	public String formatTab() {
		if (level.name().equalsIgnoreCase("quiet"))
			return "Error" + " \t " + NumFormat.time(chrono) + " \t " +  mem + " \t " + message;
		else
			return "Log  " + " \t " + NumFormat.time(chrono) + " \t " +  mem + " \t " + message;
	}
	
	public String[] formatArray() {
		return new String[] {NumFormat.time(chrono), mem, message};
	}
}
