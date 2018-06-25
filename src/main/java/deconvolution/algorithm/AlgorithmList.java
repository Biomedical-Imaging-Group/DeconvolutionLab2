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

package deconvolution.algorithm;

import java.util.ArrayList;

public class AlgorithmList {

	/** This is the static list of all available algorithms. */
	private static ArrayList<AlgorithmPanel> list;

	static {
		list = new ArrayList<AlgorithmPanel>();
		list.add(new RegularizedInverseFilterPanel());
		list.add(new TikhonovRegularizedInverseFilterPanel());
		list.add(new NaiveInverseFilterPanel());
		list.add(new FISTAPanel());
		list.add(new ISTAPanel());
		list.add(new LandweberPanel());
		list.add(new LandweberPositivityPanel());
		list.add(new StarkParkerPanel());
		list.add(new RichardsonLucyPanel());
		list.add(new RichardsonLucyTVPanel());
		list.add(new TikhonovMillerPanel());
		list.add(new ICTMPanel());
		list.add(new VanCittertPanel());
		list.add(new IdentityPanel());
		list.add(new ConvolutionPanel());
		list.add(new SimulationPanel());
		list.add(new NonStabilizedDivisionPanel());
	}

	public static ArrayList<AlgorithmPanel> getAvailableAlgorithms() {
		return list;
	}
	
	public static Algorithm getDefaultAlgorithm() {
		return new Identity();
	}

	/** 
	 * This static method return the algorithm specify by one of its shortname,
	 * 
	 * @param shortname
	 * @return an algorithm
	 */
	public static Algorithm createAlgorithm(String shortname) {
		Algorithm algo = getDefaultAlgorithm();
		String n = shortname.trim().toLowerCase();
		int i = 0;

		if (list.get(i++).isNamed(n))
			algo = new RegularizedInverseFilter(0.1);
		else if (list.get(i++).isNamed(n))
			algo =  new TikhonovRegularizedInverseFilter(1.0);
		else if (list.get(i++).isNamed(n))
			algo =  new NaiveInverseFilter();
		else if (list.get(i++).isNamed(n))
			algo =  new FISTA(10, 1, 1, "Haar", 3);
		else if (list.get(i++).isNamed(n))
			algo =  new ISTA(10, 1, 1, "Haar", 3);
		else if (list.get(i++).isNamed(n))
			algo =  new Landweber(10, 1);
		else if (list.get(i++).isNamed(n))
			algo =  new LandweberPositivity(10, 1);
		else if (list.get(i++).isNamed(n))
			algo =  new StarkParker(10, 1);
		else if (list.get(i++).isNamed(n))
			algo =  new RichardsonLucy(10);
		else if (list.get(i++).isNamed(n))
			algo =  new RichardsonLucyTV(10, 1);
		else if (list.get(i++).isNamed(n))
			algo =  new TikhonovMiller(10, 1, 0.1);
		else if (list.get(i++).isNamed(n))
			algo =  new ICTM(10, 1, 0.1);
		else if (list.get(i++).isNamed(n))
			algo =  new VanCittert(10, 1);
		else if (list.get(i++).isNamed(n))
			algo =  new Identity();
		else if (list.get(i++).isNamed(n))
			algo =  new Convolution();
		else if (list.get(i++).isNamed(n))
			algo =  new Simulation(0, 1, 0);
		else if (list.get(i++).isNamed(n))
			algo =  new NonStabilizedDivision();
		else
			algo =  getDefaultAlgorithm();
		
		return algo;
	}

	/** 
	 * This static method return the panel associated with 
	 * the algorithm specify by one of its shortname,
	 * 
	 * @param shortname
	 * @return an algorithm's panel
	 */
	public static AlgorithmPanel getPanel(String shortname) {
		for (AlgorithmPanel panel : getAvailableAlgorithms()) {
			for(String sn : panel.getShortnames())
				if (sn.equals(shortname.trim()))
					return panel;
			if (panel.getName().equals(shortname.trim()))
				return panel;

		}
		return null;
	}

	public static ArrayList<String> getShortnames() {
		ArrayList<String> list = new ArrayList<String>();
		for (AlgorithmPanel algo : getAvailableAlgorithms()) {
			for(String sn : algo.getShortnames())
				list.add(sn);
		}
		return list;
	}

	public static String getDocumentation(String name) {
		for (AlgorithmPanel algo : getAvailableAlgorithms()) {
			if (name.equals(algo.getName()))
				return algo.getDocumentation();
		}
		return "Unknown Algorithm";
	}
}
