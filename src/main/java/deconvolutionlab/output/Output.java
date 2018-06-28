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

package deconvolutionlab.output;

import java.awt.geom.Line2D;
import java.io.File;
import java.util.ArrayList;

import bilib.tools.NumFormat;
import deconvolution.algorithm.Controller;
import deconvolutionlab.Imager;
import deconvolutionlab.Imager.ContainerImage;
import deconvolutionlab.Lab;
import deconvolutionlab.monitor.Monitors;
import signal.RealSignal;
import signal.range.ClippedRange;
import signal.range.NormalizedRange;
import signal.range.RescaledRange;

public class Output {

	public enum View {
		STACK, SERIES, ORTHO, MIP, PLANAR, FIGURE
	};

	public enum Dynamic {
		INTACT, RESCALED, NORMALIZED, CLIPPED
	};

	public enum Action {
		SHOW, SAVE, SHOWSAVE;
	}

	private ContainerImage	container	= null;
	private int				ox			= 0;
	private int				oy			= 0;
	private int				oz			= 0;
	private boolean			center		= true;

	private String			name		= "";
	private boolean			save		= true;
	private boolean			show		= true;

	private View				view			= View.STACK;
	private Imager.Type		type			= Imager.Type.FLOAT;
	private Dynamic			dynamic		= Dynamic.INTACT;

	private String			customPath	= "$";

	private int				snapshot	= 0;

	public Output(View view, int snapshot, String param) {
		String[] tokens = param.trim().split(" ");
		this.view = view;
		this.snapshot = snapshot;
		this.name = "";
		this.center = true;
		this.save = true;
		this.show = true;
		this.container = Lab.createContainer(Monitors.createDefaultMonitor(), "");
		for (int i = 0; i < tokens.length; i++) {
			boolean found = false;
			String p = tokens[i].trim().toLowerCase();

			if (p.startsWith("@")) {
				found = true;
			}

			if (p.startsWith("noshow")) {
				show = false;
				found = true;
			}

			if (p.startsWith("nosave")) {
				save = false;
				found = true;
			}

			for (Dynamic d : Dynamic.values()) {
				if (p.toLowerCase().equals(d.name().toLowerCase())) {
					dynamic = d;
					found = true;
				}
			}
			for (View v : View.values()) {
				if (p.toLowerCase().equals(v.name().toLowerCase())) {
					view = v;
					found = true;
				}
			}
			for (Imager.Type t : Imager.Type.values()) {
				if (p.toLowerCase().equals(t.name().toLowerCase())) {
					type = t;
					found = true;
				}
			}
			if (p.startsWith("(") && p.endsWith(")")) {
				double pos[] = NumFormat.parseNumbers(p);
				if (pos.length > 0)
					ox = (int) Math.round(pos[0]);
				if (pos.length > 1)
					oy = (int) Math.round(pos[1]);
				if (pos.length > 2)
					oz = (int) Math.round(pos[2]);
				found = true;
				center = false;
			}
			if (!found)
				name += tokens[i] + " ";
			name = name.trim();
		}
	}

	public Output(View view, String name) {
		this.name = name;
		this.show = true;
		this.save = false;
		this.view = view;
		this.type = Imager.Type.FLOAT;
		this.dynamic = Dynamic.INTACT;
		this.center = true;
		this.snapshot = 0;
	}
	
	public void setAction(Action action) {
		this.show = action == Action.SHOW || action == Action.SHOWSAVE;
		this.save = action == Action.SAVE || action == Action.SHOWSAVE;		
	}

	public Output rescale() {
		this.dynamic = Dynamic.RESCALED;
		return this;
	}

	public Output clip() {
		this.dynamic = Dynamic.CLIPPED;
		return this;
	}

	public Output normalize() {
		this.dynamic = Dynamic.NORMALIZED;
		return this;
	}

	public Output toFloat() {
		this.type = Imager.Type.FLOAT;
		return this;
	}

	public Output toShort() {
		this.type = Imager.Type.SHORT;
		return this;
	}

	public Output toByte() {
		this.type = Imager.Type.BYTE;
		return this;
	}

	public Output setSnapshot(int snapshot) {
		this.snapshot = snapshot;
		return this;
	}

	public Output origin(int ox, int oy, int oz) {
		this.ox = ox;
		this.oy = oy;
		this.oz = oz;
		this.center = false;
		return this;
	}

	public boolean is(int iterations) {
		if (snapshot == 0)
			return false;
		return iterations % snapshot == 0;
	}

	public View getView() {
		return view;
	}

	public String getName() {
		return name;
	}

	public void setPath(String customPath) {
		this.customPath = customPath;
	}

	public int extractSnapshot(String param) {
		String line = param.trim();
		if (!line.startsWith("@"))
			line = "@0 " + line;
		String parts[] = line.split(" ");
		if (parts.length >= 1) {
			return (int) Math.round(NumFormat.parseNumber(parts[0], 0));
		}
		return 0;
	}

	public String[] getAsString() {
		String t = (type == Imager.Type.FLOAT ? "" : type.name().toLowerCase());
		String d = (dynamic == Dynamic.INTACT ? "" : dynamic.name().toLowerCase());
		String o = "";
		if (!center)
			o = " (" + ox + "," + oy + "," + oz + ")";
		String sa = save ? "\u2612" : "\u2610";
		String sh = show ? "\u2612" : "\u2610";
		String fr = snapshot > 0 ? " @" + snapshot : "";
		return new String[] { view.name().toLowerCase(), fr, name, d, t, o, sh, sa, "\u232B" };
	}
	
	public void executeFinal(Monitors monitors, RealSignal signal, Controller controller) {
		if (signal == null)
			return;
		execute(monitors, signal, controller, false, 0);
	}

	public void executeIterative(Monitors monitors, RealSignal signal, Controller controller, int iter) {
		if (signal == null)
			return;
		execute(monitors, signal, controller, true, iter);
	}

	private void execute(Monitors monitors, RealSignal signal, Controller controller, boolean live, int iter) {
		String title = name;
		if (live)
			if (!is(iter))
				return;

		if (controller != null && live) {
			if (controller.getIterations() > 0) {
				title += "@" + controller.getIterations();
			}
		}
		
		RealSignal x = null;
		switch (dynamic) {
		case RESCALED:
			x = new RescaledRange(monitors, 0, 255).process(signal);
			break;
		case CLIPPED:
			float[] stats = controller.getStats().getStatsInput();
			x = new ClippedRange(monitors, stats[1], stats[2]).process(signal);
			break;
		case NORMALIZED:
			x = signal.duplicate();
			float[] stats1 = controller.getStats().getStatsInput();
			x = new NormalizedRange(monitors, stats1[0], stats1[3]).process(signal);
			break;
		default:
			x = signal;
		}
		String path = (customPath.equals("$") ? controller.getPath() : customPath) + File.separator;
		String filename = path + title + ".tif";

		switch (view) {
		case STACK:
			if (show)
				Lab.show(monitors, x, title, type, (center ? x.nz / 2 : oz));
			if (save)
				Lab.save(monitors, x, filename, type);
			break;
		case SERIES:
			for (int k = 0; k < x.nz; k++) {
				RealSignal slice = x.getSlice(k);
				String z = "-z" + String.format("%06d", k);
				if (show)
					Lab.show(monitors, slice, title + z, type);
				if (save) {
					String zfilename = path + title + z + ".tif";
					Lab.save(monitors, slice, zfilename, type);
				}
			}
			break;
		case ORTHO:
			orthoview(monitors, x, title, filename, live);
			break;
		case FIGURE:
			figure(monitors, x, title, filename, live);
			break;
		case MIP:
			mip(monitors, x, title, filename, live);
			break;
		case PLANAR:
			planar(monitors, x, title, filename, live);
			break;
		default:
			break;
		}
	}

	private void mip(Monitors monitors, RealSignal signal, String title, String filename, boolean live) {
		RealSignal plane = signal.createMIP();
		if (show) {
			ArrayList<Line2D.Double> overlayLines = createOverlayXYZ(signal);
			if (live) {
				container = container == null ? Lab.createContainer(monitors, title) : container;
				Lab.append(monitors, container, plane, title, type, overlayLines);
			}
			else {
				Lab.show(monitors, plane, title, type, 0, overlayLines);
			}
		}
		if (save)
			Lab.save(monitors, plane, filename, type);
	}

	private void orthoview(Monitors monitors, RealSignal signal, String title, String filename, boolean live) {
		int cx = ox;
		int cy = oy;
		int cz = oz;
		if (center) {
			cx = signal.nx / 2;
			cy = signal.ny / 2;
			cz = signal.nz / 2;
		}
		RealSignal plane = signal.createOrthoview(cx, cy, cz);
		if (show) {
			ArrayList<Line2D.Double> overlayLines = createOverlayXYZ(signal);
			if (live) {
				container = container == null ? Lab.createContainer(monitors, title) : container;
				Lab.append(monitors, container, plane, title, type, overlayLines);
			}
			else {
				Lab.show(monitors, plane, title, type, 0, overlayLines);
			}
		}
		if (save)
			Lab.save(monitors, plane, filename, type);
	}

	private void figure(Monitors monitors, RealSignal signal, String title, String filename, boolean live) {
		int cx = ox;
		int cy = oy;
		int cz = oz;
		if (center) {
			cx = signal.nx / 2;
			cy = signal.ny / 2;
			cz = signal.nz / 2;
		}
		RealSignal plane = signal.createFigure(cx, cy, cz);
		if (show && live) {
			container = container == null ? Lab.createContainer(monitors, title) : container;
			Lab.append(monitors, container, plane, title, type);
		}
		if (show && !live)
			Lab.show(monitors, plane, title, type);
		if (save)
			Lab.save(monitors, plane, filename, type);
	}

	private void planar(Monitors monitors, RealSignal signal, String title, String filename, boolean live) {
		RealSignal plane = signal.createPlanar();
		if (show) {
			ArrayList<Line2D.Double> overlayLines = createOverlayPlanar(signal);
			if (live) {
				container = container == null ? Lab.createContainer(monitors, title) : container;
				Lab.append(monitors, container, plane, title, type, overlayLines);
			}
			else {
				Lab.show(monitors, plane, title, type, 0, overlayLines);
			}
		}
		if (save)
			Lab.save(monitors, plane, filename, type);
	}

	public static  ArrayList<Line2D.Double> createOverlayXYZ(RealSignal signal) {
		ArrayList<Line2D.Double> overlayLines = new ArrayList<Line2D.Double>();
		if (signal.nz <= 1)
			return overlayLines;
		overlayLines.add(new Line2D.Double(signal.nx, 0, signal.nx, signal.ny + signal.nz));
		overlayLines.add(new Line2D.Double(0, signal.ny, signal.nx + signal.nz, signal.ny));
		if (signal.nz > 3)
			for (int z = 2; z < 2 * signal.nz; z += 02)
				overlayLines.add(new Line2D.Double(signal.nx + z, signal.ny, signal.nx, signal.ny + z));
		return overlayLines;
	}

	public static ArrayList<Line2D.Double> createOverlayPlanar(RealSignal signal) {
		ArrayList<Line2D.Double> overlayLines = new ArrayList<Line2D.Double>();
		if (signal.nz <= 1)
			return overlayLines;
		int nr = (int) Math.sqrt(signal.nz);
		int nc = (int) Math.ceil(signal.nz / nr);
		if (nc * nr < signal.nz)
			nc++;
			for (int k = 0; k < signal.nz; k++) {
			int col = k % nr;
			int row = k / nr;
			int offx = col * signal.nx;
			int offy = row * signal.ny;
			overlayLines.add(new Line2D.Double(offx, offy, offx + signal.nx, offy));
			overlayLines.add(new Line2D.Double(offx + signal.nx, offy, offx + signal.nx, offy + signal.ny));
			overlayLines.add(new Line2D.Double(offx + signal.nx, offy + signal.ny, offx, offy + signal.ny));
			overlayLines.add(new Line2D.Double(offx, offy + signal.ny, offx, offy));
		}
		return overlayLines;
	}

	@Override
	public String toString() {
		String t = type.name().toLowerCase();
		String v = view.name().toLowerCase();
		String d = dynamic.name().toLowerCase();
		String f = snapshot > 0 ? " every " + snapshot + " iterations" : "";
		String k = (center ? "" : " keypoint = (" + ox + "," + oy + "," + oz + ")");
		return v + " " + name + " format = (" + d + ", " + t + ") " + k + f;
	}
}
