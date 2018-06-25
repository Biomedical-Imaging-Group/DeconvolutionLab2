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

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

public class Files {

	public static String getWorkingDirectory() {
		return System.getProperty("user.dir") + File.separator;
	}

	public static String getHomeDirectory() {
		return FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath() + File.separator;	
	}
	
	public static String getDesktopDirectory() {
		return getHomeDirectory() + "Desktop" + File.separator;
	}
	
	public static File browseFile(String path) {
		JFileChooser fc = new JFileChooser(); 
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		File dir = new File(path);
		if (dir.exists())
			fc.setCurrentDirectory(dir);
		
		int ret = fc.showOpenDialog(null); 
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = new File(fc.getSelectedFile().getAbsolutePath());
			if (file.exists())
				return file;
		}
		return null;
	}
	
	public static File browseDirectory(String path) {
		JFileChooser fc = new JFileChooser(); 
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		File dir = new File(path);
		if (dir.exists())
			fc.setCurrentDirectory(dir);

		int ret = fc.showOpenDialog(null); 
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = new File(fc.getSelectedFile().getAbsolutePath());
			if (file.exists())
				return file;
		}
		return null;
	}
}
