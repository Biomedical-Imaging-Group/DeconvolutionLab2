/*
 * bilib --- Java Bioimaging Library ---
 * 
 * Author: Daniel Sage, Biomedical Imaging Group, EPFL, Lausanne, Switzerland
 * 
 * Conditions of use: You are free to use this software for research or
 * educational purposes. In addition, we expect you to include adequate
 * citations and acknowledgments whenever you present or publish results that
 * are based on it.
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

package bilib.tools;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumFormat {

	public static double parseNumber(String line, double def) {
		double[] numbers = parseNumbers(line);
		if (numbers.length >= 1)
			return numbers[0];
		else
			return def;
	}

	public static double[] parseNumbersAfter(String keyword, String line) {
		String parts[] = line.trim().toLowerCase().split(keyword.toLowerCase());
		if (parts.length == 2)
			return parseNumbers(parts[1]);
		else
			return new double[0];
	}

	public static double[] parseNumbers(String line) {
		ArrayList<String> num = new ArrayList<String>();
		Pattern p = Pattern.compile("[-+]?[0-9]+[.]?[0-9]*([eE][-+]?[0-9]+)?");
		Matcher m = p.matcher(line);
		while (m.find()) {
			num.add(m.group());
		}
		double number[] = new double[num.size()];
		for (int i = 0; i < num.size(); i++)
			number[i] = Double.parseDouble(num.get(i));
		return number;
	}

	public static String nice(double a) {
		double b = Math.abs(a);
		String n = a < 0 ? "-" : "";
		if (a == 0)
			return "0.0";
		if (a >= Double.POSITIVE_INFINITY)
			return String.format("+\u221e");
		if (a <= Double.NEGATIVE_INFINITY)
			return String.format("-\u221e");
		if (a >= Double.MAX_VALUE)
			return String.format("+\u221e");
		if (a <= -Double.MAX_VALUE)
			return String.format("+\u221e");

		if (b > 3000.0)
			return String.format(n + "%6.3E", b);
		if (b > 300.0)
			return String.format(n + "%4.1f", b);
		if (b > 30.0)
			return String.format(n + "%3.2f", b);
		if (b > 3.0)
			return String.format(n + "%2.4f", b);
		if (b > 0.003)
			return String.format(n + "%1.4f", b);
		
		return String.format(n + "%6.3E", b).trim();
	}

	public static String seconds(double ns) {
		return String.format("%5.1f s", ns * 1e-9);
	}

	public static String time(double ns) {
		if (ns < 3000.0)
			return String.format("%3.2f ns", ns);
		ns *= 0.001;
		if (ns < 3000.0)
			return String.format("%3.2f us", ns);
		ns *= 0.001;
		if (ns < 3000.0)
			return String.format("%3.2f ms", ns);
		ns *= 0.001;
		if (ns < 3600.0 * 3)
			return String.format("%3.2f s", ns);
		ns /= 3600;
		return String.format("%3.2f h", ns);
	}

	public static String bytes(double bytes) {
		if (bytes < 3000)
			return String.format("%3.0f", bytes);
		bytes /= 1024.0;
		if (bytes < 3000)
			return String.format("%3.1f Kb", bytes);
		bytes /= 1024.0;
		if (bytes < 3000)
			return String.format("%3.1f Mb", bytes);
		bytes /= 1024.0;
		if (bytes < 3000)
			return String.format("%3.1f Gb", bytes);
		bytes /= 1024.0;
		return String.format("%3.1f Tb", bytes);
	}

	public static String toPercent(String value) {
		try {
			return toPercent(Double.parseDouble(value));
		}
		catch (Exception ex) {
		}
		return value;
	}

	public static String toPercent(double value) {
		return String.format("%5.3f", value * 100) + "%";
	}

}
