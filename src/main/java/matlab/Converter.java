package matlab;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ShortProcessor;
import signal.RealSignal;

public class Converter {

	public static boolean verbose;

	/**
	 * Get an image.
	 *
	 * @param imageplus	image
	 * @return an N x M array representing the input image
	 */
	public static Object get(ImagePlus imageplus) {
		if (imageplus == null)
			return null;
		int width = imageplus.getWidth();
		int height = imageplus.getHeight();
		int stackSize = imageplus.getStackSize();
		int counter = 0;
		ImageStack imagestack = imageplus.getStack();
		switch (imageplus.getType()) {

		case ImagePlus.COLOR_256: {
			;
		}
		case ImagePlus.GRAY8: {
			short[][][] is = new short[height][width][stackSize];
			for (int sz = 0; sz < stackSize; sz++) {
				ByteProcessor byteprocessor = (ByteProcessor) imagestack.getProcessor(sz + 1);
				byte[] pixels = (byte[]) byteprocessor.getPixels();
				counter = 0;
				int h = 0;
				while (h < height) {
					int w = 0;
					while (w < width) {
						is[h][w][sz] = (short)(pixels[counter]&0xff);
						w++;
						counter++;
					}
					counter = ++h * width;
				}
			}
			return is;
		}
		case ImagePlus.GRAY16: {
			int[][][] is = new int[height][width][stackSize];
			for (int sz = 0; sz < stackSize; sz++) {
				counter = 0;
				ShortProcessor shortprocessor = (ShortProcessor) imagestack.getProcessor(sz + 1);
				short[] spixels = (short[]) shortprocessor.getPixels();
				int h = 0;
				while (h < height) {
					int w = 0;
					while (w < width) {
						is[h][w][sz] = (int)(spixels[counter]&0xffff);
						w++;
						counter++;
					}
					counter = ++h * width;
				}
			}
			return is;
		}
		case ImagePlus.GRAY32: {
			double[][][] fs = new double[height][width][stackSize];
			for (int sz = 0; sz < stackSize; sz++) {
				FloatProcessor floatprocessor = (FloatProcessor) imagestack.getProcessor(sz + 1);
				float[] fpixels = (float[]) floatprocessor.getPixels();
				counter = 0;
				int i = 0;
				while (i < height) {
					int j = 0;
					while (j < width) {
						fs[i][j][sz] = (double) fpixels[counter];
						j++;
						counter++;
					}
					counter = ++i * width;
				}
			}
			return fs;
		}
		case ImagePlus.COLOR_RGB: {
			if (stackSize == 1) {
				short[][][] is = new short[height][width][3];
				ColorProcessor colorprocessor = (ColorProcessor) imagestack.getProcessor(1);
				byte[] red = new byte[width * height];
				byte[] green = new byte[width * height];
				byte[] blue = new byte[width * height];
				colorprocessor.getRGB(red, green, blue);
				counter = 0;
				int h = 0;
				while (h < height) {
					int w = 0;
					while (w < width) {
						is[h][w][0] = (short)(red[counter]&0xff);
						is[h][w][1] = (short)(green[counter]&0xff);
						is[h][w][2] = (short)(blue[counter]&0xff);
						w++;
						counter++;
					}
					counter = ++h * width;
				}
				return is;
			}
			short[][][][] is = new short[height][width][stackSize][3];
			for (int sz = 0; sz < stackSize; sz++) {
				ColorProcessor colorprocessor  = (ColorProcessor) imagestack.getProcessor(sz + 1);
				byte[] red = new byte[width * height];
				byte[] green = new byte[width * height];
				byte[] blue = new byte[width * height];
				colorprocessor.getRGB(red, green, blue);
				counter = 0;
				int h = 0;
				while (h < height) {
					int w = 0;
					while (w < width) {
						is[h][w][sz][0] = (short)red[counter];
						is[h][w][sz][1] = (short)green[counter];
						is[h][w][sz][2] = (short)blue[counter];
						w++;
						counter++;
					}
					counter = ++h * width;
				}
			}
			return is;
		}
		default:
			System.out.println("MIJ Error message: Unknow type of volumes.");
			return null;
		}
	}
	
	
	/**
	 * Create a new RealSignal from a Matlab variable with a specified
	 * title.
	 */
	public static RealSignal createRealSignal(Object object) {

		RealSignal signal = null;
		if (object instanceof byte[][]) {
			byte[][] data = (byte[][]) object;
			int h = data.length;
			int w = data[0].length;
			signal = new RealSignal("Matlab-byte2D", w, h, 1);
			for(int i=0; i<w; i++)
				for(int j=0; j<h; j++)
					signal.data[0][i+j*w] = data[j][i];
			return signal;
		}
		
		if (object instanceof short[][]) {
			short[][] data = (short[][]) object;
			int h = data.length;
			int w = data[0].length;
			signal = new RealSignal("Matlab-short2D", w, h, 1);
			for(int i=0; i<w; i++)
				for(int j=0; j<h; j++)
					signal.data[0][i+j*w] = data[j][i];
			return signal;
		}
		
		if (object instanceof int[][]) {
			int[][] data = (int[][]) object;
			int h = data.length;
			int w = data[0].length;
			signal = new RealSignal("Matlab-int2D", w, h, 1);
			for(int i=0; i<w; i++)
				for(int j=0; j<h; j++)
					signal.data[0][i+j*w] = data[j][i];
			return signal;
		}
		
		if (object instanceof float[][]) {
			float[][] data = (float[][]) object;
			int h = data.length;
			int w = data[0].length;
			signal = new RealSignal("Matlab-float2D", w, h, 1);
			for(int i=0; i<w; i++)
				for(int j=0; j<h; j++)
					signal.data[0][i+j*w] = data[j][i];
			return signal;
		}
		
		if (object instanceof double[][]) {
			double[][] data = (double[][]) object;
			int h = data.length;
			int w = data[0].length;
			signal = new RealSignal("Matlab-double2D", w, h, 1);
			for(int i=0; i<w; i++)
				for(int j=0; j<h; j++)
					signal.data[0][i+j*w] = (float)data[j][i];
			return signal;
		}
			
		if (object instanceof byte[][][]) {
			byte[][][] data = (byte[][][]) object;
			int h = data.length;
			int w = data[0].length;
			int d = data[0][0].length;
			signal = new RealSignal("Matlab-byte3D", w, h, d);
			for(int k=0; k<d; k++)
				for(int i=0; i<w; i++)
					for(int j=0; j<h; j++)
						signal.data[k][i+j*w] = (float)data[j][i][k];
			return signal;
		}
			
		if (object instanceof short[][][]) {
			short[][][] data = (short[][][]) object;
			int h = data.length;
			int w = data[0].length;
			int d = data[0][0].length;
			signal = new RealSignal("Matlab-short3D", w, h, d);
			for(int k=0; k<d; k++)
				for(int i=0; i<w; i++)
					for(int j=0; j<h; j++)
						signal.data[k][i+j*w] = (float)data[j][i][k];
			return signal;
		}
		
		if (object instanceof int[][][]) {
			int[][][] data = (int[][][]) object;
			int h = data.length;
			int w = data[0].length;
			int d = data[0][0].length;
			signal = new RealSignal("Matlab-int3D", w, h, d);
			for(int k=0; k<d; k++)
				for(int i=0; i<w; i++)
					for(int j=0; j<h; j++)
						signal.data[k][i+j*w] = (float)data[j][i][k];
			return signal;
		}
		
		if (object instanceof float[][][]) {
			float[][][] data = (float[][][]) object;
			int h = data.length;
			int w = data[0].length;
			int d = data[0][0].length;
			signal = new RealSignal("Matlab-float3D", w, h, d);
			for(int k=0; k<d; k++)
				for(int i=0; i<w; i++)
					for(int j=0; j<h; j++)
						signal.data[k][i+j*w] = data[j][i][k];
			return signal;
		}
		
		if (object instanceof double[][][]) {
			double[][][] data = (double[][][]) object;
			int h = data.length;
			int w = data[0].length;
			int d = data[0][0].length;
			signal = new RealSignal("Matlab-double3D", w, h, d);
			for(int k=0; k<d; k++)
				for(int i=0; i<w; i++)
					for(int j=0; j<h; j++)
						signal.data[k][i+j*w] = (float)data[j][i][k];
			return signal;
		}
		
		return null;
	}

	public static Object createMatlabMatrix(RealSignal signal) {
		if (signal == null)
			return null;
		int nx = signal.nx;
		int ny = signal.ny;
		int nz = signal.nz;
		double[][][] object = new double[ny][nx][nz];
		for(int k=0; k<nz; k++)
		for(int i=0; i<nx; i++)
		for(int j=0; j<ny; j++)
			object[j][i][k] = signal.data[k][i+j*nx];
		return object;
	}
}
