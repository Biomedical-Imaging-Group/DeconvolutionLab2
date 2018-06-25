package signal;

import java.util.ArrayList;

import javax.swing.JScrollPane;

import bilib.table.CustomizedColumn;
import bilib.table.CustomizedTable;
import bilib.tools.NumFormat;
import deconvolutionlab.system.SystemUsage;

public class SignalCollector {

	private static long					bytesReal			= 0;
	private static int					countReal			= 0;
	private static long					bytesComplex		= 0;
	private static int					countComplex		= 0;
	private static double				chrono				= 0;
	private static CustomizedTable		table;
	private static double				progress			= 0;

	private static int					countPeakComplex = 0;
	private static int					countPeakReal = 0;
	private static long					bytesPeakComplex = 0;
	private static long					bytesPeakReal = 0;

	private static ArrayList<Signal>	signals;
	protected final static int			NOTIFICATION_RATE	= 25;

	static {
		bytesReal = 0;
		countReal = 0;
		bytesComplex = 0;
		countComplex = 0;
		signals = new ArrayList<Signal>();
		chrono = System.nanoTime();
		ArrayList<CustomizedColumn> columns = new ArrayList<CustomizedColumn>();
		columns.add(new CustomizedColumn("Time", String.class, 100, false));
		columns.add(new CustomizedColumn("Name", String.class, 600, false));
		columns.add(new CustomizedColumn("Dimension", String.class, 60, false));
		columns.add(new CustomizedColumn("Count", String.class, 100, false));
		columns.add(new CustomizedColumn("Total", String.class, 100, false));
		columns.add(new CustomizedColumn("Memory", String.class, 100, false));
		table = new CustomizedTable(columns, true);
		table.getColumnModel().getColumn(4).setMaxWidth(100);
		table.getColumnModel().getColumn(4).setMinWidth(100);
	}

	public static JScrollPane getPanel(int w, int h) {
		return table.getPane(w, h);
	}

	public static String sumarize() {
		return "" + NumFormat.bytes(bytesReal + bytesComplex);
	}

	public static void clear() {
		for(Signal signal : signals) {
			for (int z = 0; z < signal.nz; z++)
				signal.data[z] = new float[1];
		}
		signals.clear();
		table.removeRows();
	}

	public static double getProgress() {
		return progress;
	}

	public static void setProgress(double p) {
		progress = p;
	}

	public static void marker(String msg) {
		String row[] = { "", msg, "", "", "", "" };
		table.append(row);
	}

	public static void alloc(Signal signal) {
		if (signal == null) {
			marker("error in allocating");
			return;
		}
		signals.add(signal);
		addTable(signal, 1);
	}

	public static void free(Signal signal) { 
		if (signal == null) { 
			marker("error in freeing");
			return;
		}
		for (int z = 0; z < signal.nz; z++)
			signal.data[z] = new float[1];

		signals.remove(signal);
		addTable(signal, -1);
		signal = null;
	}

	public static void addTable(Signal signal, int sign) {
		boolean complex = signal instanceof ComplexSignal;
		int nx = signal.nx;
		int ny = signal.ny;
		int nz = signal.nz;
		long b = sign * (nx * ny * nz * 4 * (complex ? 2 : 1));

		if (complex) {
			bytesComplex += b;
			countComplex += sign;
		}
		else {
			bytesReal += b;
			countReal += sign;
		}
		
		bytesPeakComplex = Math.max(bytesPeakComplex, bytesComplex);
		bytesPeakReal = Math.max(bytesPeakReal, bytesReal);
		countPeakComplex = Math.max(countPeakComplex, countComplex);
		countPeakReal = Math.max(countPeakReal, countReal);
		String m = NumFormat.bytes(SystemUsage.getHeapUsed());
		String t = NumFormat.time(System.nanoTime() - chrono);
		String dim = "" + nx + "x" + ny + "x" + nz;
		String c = "" + (countReal + countComplex);
		String a = NumFormat.bytes(bytesReal + bytesComplex);
		String row[] = { t, (sign > 0 ? "+" : "-") + signal.name, dim, c, a, m };
		table.append(row);
	}
	
	public static int getCountSignals() {
		return countComplex + countReal;	
	}

	public static long getBytesSignals() {
		return bytesComplex + bytesReal;	
	}

	public static long getBytesPeakSignals() {
		return bytesPeakComplex + bytesPeakReal;	
	}

	public static int getCountPeakSignals() {
		return countPeakComplex + countPeakReal;	
	}

	public static void resetSignals() {
		countPeakComplex = 0;
		countPeakReal = 0;
		bytesPeakComplex = 0;
		bytesPeakReal = 0;
		countComplex = 0;
		countReal = 0;
		bytesComplex = 0;
		bytesReal = 0;
	}
}
