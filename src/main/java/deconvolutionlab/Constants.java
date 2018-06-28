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

package deconvolutionlab;

import java.awt.Dimension;

public class Constants {

	public static String name = "DeconvolutionLab2";
	public static String copyright = "\u00A9 2010-2018. Biomedical Imaging Group, EPFL.";
	
	public static String url = "http://bigwww.epfl.ch/deconvolution/";
	public static String reference = 
			"<br>D. Sage, L. Donati, F. Soulez, D. Fortun, A. Seitz, R. Guiet, C. Vonesch, M. Unser<br>" +
			"DeconvolutionLab2 : An open-source software for deconvolution microscopy<br>" +
			"Methods-Image Processing for Biologists, vol. 115, 2017.";
	
	public static String version = "2.1.2 (27.06.2018)";
	
	public static String authors = 
			"Daniel Sage, " + 
			"Cédric Vonesch, " +
			"Guillaume Schmit, " +
			"Pierre Besson, " +
			"Raquel Terrés Cristofani, " +
			"Alessandra Griffa";
				
	public static String help = 
			"<h1>" + name + "</h1>" +
			"<h2>" + version + "</h2>";

	public static int widthGUI = 600;
	public static Dimension dimParameters = new Dimension(88, 25);
	public static int widthLaunchGUI = 480;
	public static int heightLaunchGUI = 360;

}
