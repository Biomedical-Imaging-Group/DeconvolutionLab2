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

package bilib.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import resources.ImageLoader;

public class PanelImage extends JPanel {

	private Image	image;
	private int		w	= -1;
	private int		h	= -1;

	public PanelImage() {
		super();
	}
	
	public PanelImage(String filename) {
		super();
		image = ImageLoader.get(filename);
	}
	
	public PanelImage(int w, int h) {
		super();
		image = null;
		this.w = w;
		this.h = h;
	}

	public PanelImage(String filename, int w, int h) {
		super();
		image = ImageLoader.get(filename);
		this.w = w;
		this.h = h;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			if (w < 0)
				g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
			else {
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, getWidth(), getHeight());
				g.drawImage(image, (getWidth()-w)/2, 0, w, h, null);
			}
		}
		else {
			g.setColor(Color.DARK_GRAY);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}

}
