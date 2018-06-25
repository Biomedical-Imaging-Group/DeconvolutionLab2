/*====================================================================
| Version: May 31, 2014
\===================================================================*/

/*====================================================================
| Philippe Thevenaz
| EPFL/STI/IMT-LS/LIB/BM.4.137
| Station 17
| CH-1015 Lausanne VD
| Switzerland
|
| phone (CET): +41(21)693.51.61
| fax: +41(21)693.37.01
| RFC-822: philippe.thevenaz@epfl.ch
| X-400: /C=ch/A=400net/P=switch/O=epfl/S=thevenaz/G=philippe/
| URL: http://bigwww.epfl.ch/
\===================================================================*/

package fft.academic;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.floor;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*====================================================================
|	AcademicFFT
\===================================================================*/
/*********************************************************************
 <p>
 The purpose of this class is to encapsulate operations related to the
 discrete Fourier transform of periodic sequences, images, and
 volumes. If <i>x</i> is a discrete sequence assumed to be periodic
 over <i>K</i> samples, then its discrete Fourier transform is <i>X</i>
 while the inverse discrete Fourier transform of <i>X</i> is <i>x</i>.
 The transforms are defined as the pair
 </p>
 <ul>
 <li>&#8704;<i>n</i> &#8712; [0&#8230;<i>K</i> &#8722; 1]:
 <i>X</i>[<i>n</i>] = &#8497;{<i>x</i>}[<i>n</i>]
 = &#8721;<sub><i>k</i> &#8712; [0&#8230;<i>K</i> &#8722; 1]</sub>
 <i>x</i>[<i>k</i>]
 e<sup>&#8722;j <i>n</i> (2 &#960; &#8725; <i>K</i>) <i>k</i></sup></li>
 <li>&#8704;<i>k</i> &#8712; [0&#8230;<i>K</i> &#8722; 1]:
 <i>x</i>[<i>k</i>] = &#8497;<sup>&#8722;1</sup>{<i>X</i>}[<i>k</i>]
 = (1 &#8725; <i>K</i>)
 &#8721;<sub><i>n</i> &#8712; [0&#8230;<i>K</i> &#8722; 1]</sub>
 <i>X</i>[<i>n</i>]
 e<sup>j <i>n</i> (2 &#960; &#8725; <i>K</i>) <i>k</i></sup></li>
 </ul>
 <p>
 A few relevant relations are
 </p>
 <ul>
 <li>j<sup>2</sup> = &#8722;1</li>
 <li>&#8704;<i>k</i>, <i>n</i> &#8712; [0&#8230;<i>K</i> &#8722; 1],
 <i>m</i> &#8712; &#8484;:
 <i>x</i>[<i>k</i>] = <i>x</i>[<i>k</i> + <i>m</i> <i>K</i>],
 <i>X</i>[<i>n</i>] = <i>X</i>[<i>n</i> + <i>m</i> <i>K</i>]</li>
 <li>&#8704;<i>&#955;</i> &#8712; &#8450;: &#8497;{<i>&#955;</i> <i>x</i> + <i>y</i>}
 = <i>&#955;</i> <i>X</i> + <i>Y</i></li>
 <li>&#8497;{<i>X</i>}[0] = <i>x</i>[0];
 <i>m</i> &#8712; [1&#8230;<i>K</i> &#8722; 1]:
 &#8497;{<i>X</i>}[<i>m</i>] = <i>x</i>[<i>K</i> &#8722; <i>m</i>]</li>
 <li><i>k</i><sub>0</sub> &#8712; [1&#8230;<i>K</i> &#8722; 1]:
 ((<i>k</i> &#8712; [0&#8230;<i>k</i><sub>0</sub> &#8722; 1]:
 <i>y</i>[<i>k</i>] = <i>x</i>[<i>K</i> + <i>k</i>
 &#8722; <i>k</i><sub>0</sub>]),
 (<i>k</i> &#8712; [<i>k</i><sub>0</sub>&#8230;<i>K</i> &#8722; 1]:
 <i>y</i>[<i>k</i>] = <i>x</i>[<i>k</i> &#8722; <i>k</i><sub>0</sub>]));
 <i>Y</i>[<i>n</i>] = e<sup>&#8722;j <i>k</i><sub>0</sub>
 (2 &#960; &#8725; <i>K</i>) <i>n</i></sup> <i>X</i>[<i>n</i>]</li>
 <li><i>n</i> &#8712; [0&#8230;<i>K</i> &#8722; 1]:
 (<i>k</i><sub>0</sub> &#8712; [0&#8230;<i>K</i> &#8722; 2]:
 (<i>x</i> &#8727; <i>y</i>)[<i>k</i><sub>0</sub>]
 = &#8721;<sub><i>k</i> &#8712; [0&#8230;<i>k</i><sub>0</sub>]</sub>
 <i>x</i>[<i>k</i>] <i>y</i>[<i>k</i><sub>0</sub> &#8722; <i>k</i>]
 + &#8721;<sub><i>k</i> &#8712; [<i>k</i><sub>0</sub> + 1&#8230;<i>K</i> &#8722; 1]</sub>
 <i>x</i>[<i>k</i>] <i>y</i>[<i>K</i> + <i>k</i><sub>0</sub> &#8722; <i>k</i>]),
 ((<i>x</i> &#8727; <i>y</i>)[<i>K</i> &#8722; 1]
 = &#8721;<sub><i>k</i> &#8712; [0&#8230;<i>K</i> &#8722; 1]</sub>
 <i>x</i>[<i>k</i>] <i>y</i>[<i>K</i> &#8722; 1 &#8722; <i>k</i>]);
 &#8497;{<i>x</i> &#8727; <i>y</i>}[<i>n</i>]
 = <i>X</i>[<i>n</i>] <i>Y</i>[<i>n</i>]</li>
 <li>&#12296;<i>x</i>, <i>y</i>&#12297;
 = &#8721;<sub><i>k</i> &#8712; [0&#8230;<i>K</i> &#8722; 1]</sub>
 <i>x</i>[<i>k</i>] (<i>y</i>[<i>k</i>])<sup>*</sup>
 = (1 &#8725; <i>K</i>) &#12296;<i>X</i>, <i>Y</i>&#12297;
 = (1 &#8725; <i>K</i>) &#8721;<sub><i>n</i> &#8712; [0&#8230;<i>K</i> &#8722; 1]</sub>
 <i>X</i>[<i>n</i>] (<i>Y</i>[<i>n</i>])<sup>*</sup></li>
 <li><i>x</i>[0] = 1, <i>k</i> &#8712; [1&#8230;<i>K</i> &#8722; 1]:
 <i>x</i>[<i>k</i>] = 0; <i>n</i> &#8712; [0&#8230;<i>K</i> &#8722; 1]:
 <i>X</i>[<i>n</i>] = 1</li>
 <li><i>k</i> &#8712; [0&#8230;<i>K</i> &#8722; 1]:
 <i>x</i>[<i>k</i>] = 1; <i>X</i>[0] = <i>K</i>,
 <i>n</i> &#8712; [1&#8230;<i>K</i> &#8722; 1]: <i>X</i>[<i>n</i>] = 0</li>
 <li><i>x</i> = &#8476;(<i>x</i>); &#8465;(<i>X</i>[0]) = 0,
 (2 &#8739; <i>K</i>: &#8465;(<i>X</i>[<i>K</i> &#8725; 2]) = 0),
 <i>n</i> &#8712; [1&#8230;<i>K</i> &#8722; 1]: <i>X</i>[<i>n</i>]
 = (<i>X</i>[<i>K</i> &#8722; <i>n</i>])<sup>*</sup></li>
 <li><i>x</i> = &#8476;(<i>x</i>); <i>y</i> = &#8476;(<i>y</i>);
 F = &#8497;{<i>x</i> + j <i>y</i>}; <i>X</i>[0] = &#8476;(<i>F</i>[0]);
 <i>Y</i>[0] = &#8465;(<i>F</i>[0]);
 <i>n</i> &#8712; [1&#8230;<i>K</i> &#8722; 1]: <i>X</i>[<i>n</i>]
 = (<i>F</i>[<i>n</i>] + (<i>F</i>[<i>K</i> &#8722; <i>n</i>])<sup>*</sup>)
 &#8725; 2, <i>Y</i>[<i>n</i>] = &#8722;j (<i>F</i>[<i>n</i>]
 &#8722; (<i>F</i>[<i>K</i> &#8722; <i>n</i>])<sup>*</sup>) &#8725; 2</li>
 </ul>
 <p>
 In two dimensions
 </p>
 <ul>
 <li>&#8497;{<i>x</i>}[<i>n</i><sub>1</sub>, <i>n</i><sub>2</sub>]
 = &#8497;<sub><i>k</i><sub>2</sub></sub>{&#8497;<sub><i>k</i><sub>1</sub></sub>{<i>x</i>[<i>k</i><sub>1</sub>,
 <i>k</i><sub>2</sub>]}[<i>n</i><sub>1</sub>, <i>k</i><sub>2</sub>]}[<i>n</i><sub>1</sub>,
 <i>n</i><sub>2</sub>]
 = &#8497;<sub><i>k</i><sub>1</sub></sub>{&#8497;<sub><i>k</i><sub>2</sub></sub>{<i>x</i>[<i>k</i><sub>1</sub>,
 <i>k</i><sub>2</sub>]}[<i>k</i><sub>1</sub>,
 <i>n</i><sub>2</sub>]}[<i>n</i><sub>1</sub>, <i>n</i><sub>2</sub>]</li>
 </ul>
 <p>
 In three dimensions
 </p>
 <ul>
 <li>&#8497;{<i>x</i>}[<i>n</i><sub>1</sub>, <i>n</i><sub>2</sub>, <i>n</i><sub>3</sub>]
 = &#8497;<sub><i>k</i><sub>3</sub></sub>{&#8497;<sub><i>k</i><sub>2</sub></sub>{&#8497;<sub><i>k</i><sub>1</sub></sub>{<i>x</i>[<i>k</i><sub>1</sub>,
 <i>k</i><sub>2</sub>, <i>k</i><sub>3</sub>]}[<i>n</i><sub>1</sub>,
 <i>k</i><sub>2</sub>, <i>k</i><sub>3</sub>]}[<i>n</i><sub>1</sub>,
 <i>n</i><sub>2</sub>, <i>k</i><sub>3</sub>]}[<i>n</i><sub>1</sub>,
 <i>n</i><sub>2</sub>, <i>n</i><sub>3</sub>]
 = &#8497;<sub><i>k</i><sub>3</sub></sub>{&#8497;<sub><i>k</i><sub>1</sub></sub>{&#8497;<sub><i>k</i><sub>2</sub></sub>{<i>x</i>[<i>k</i><sub>1</sub>,
 <i>k</i><sub>2</sub>, <i>k</i><sub>3</sub>]}[<i>k</i><sub>1</sub>,
 <i>n</i><sub>2</sub>, <i>k</i><sub>3</sub>]}[<i>n</i><sub>1</sub>,
 <i>n</i><sub>2</sub>, <i>k</i><sub>3</sub>]}[<i>n</i><sub>1</sub>,
 <i>n</i><sub>2</sub>, <i>n</i><sub>3</sub>]
 = &#8497;<sub><i>k</i><sub>2</sub></sub>{&#8497;<sub><i>k</i><sub>3</sub></sub>{&#8497;<sub><i>k</i><sub>1</sub></sub>{<i>x</i>[<i>k</i><sub>1</sub>,
 <i>k</i><sub>2</sub>, <i>k</i><sub>3</sub>]}[<i>n</i><sub>1</sub>,
 <i>k</i><sub>2</sub>, <i>k</i><sub>3</sub>]}[<i>n</i><sub>1</sub>,
 <i>k</i><sub>2</sub>, <i>n</i><sub>3</sub>]}[<i>n</i><sub>1</sub>,
 <i>n</i><sub>2</sub>, <i>n</i><sub>3</sub>]
 = &#8497;<sub><i>k</i><sub>2</sub></sub>{&#8497;<sub><i>k</i><sub>1</sub></sub>{&#8497;<sub><i>k</i><sub>3</sub></sub>{<i>x</i>[<i>k</i><sub>1</sub>,
 <i>k</i><sub>2</sub>, <i>k</i><sub>3</sub>]}[<i>k</i><sub>1</sub>,
 <i>k</i><sub>2</sub>, <i>n</i><sub>3</sub>]}[<i>n</i><sub>1</sub>,
 <i>k</i><sub>2</sub>, <i>n</i><sub>3</sub>]}[<i>n</i><sub>1</sub>,
 <i>n</i><sub>2</sub>, <i>n</i><sub>3</sub>]
 = &#8497;<sub><i>k</i><sub>1</sub></sub>{&#8497;<sub><i>k</i><sub>3</sub></sub>{&#8497;<sub><i>k</i><sub>2</sub></sub>{<i>x</i>[<i>k</i><sub>1</sub>,
 <i>k</i><sub>2</sub>, <i>k</i><sub>3</sub>]}[<i>k</i><sub>1</sub>,
 <i>n</i><sub>2</sub>, <i>k</i><sub>3</sub>]}[<i>k</i><sub>1</sub>,
 <i>n</i><sub>2</sub>, <i>n</i><sub>3</sub>]}[<i>n</i><sub>1</sub>,
 <i>n</i><sub>2</sub>, <i>n</i><sub>3</sub>]
 = &#8497;<sub><i>k</i><sub>1</sub></sub>{&#8497;<sub><i>k</i><sub>2</sub></sub>{&#8497;<sub><i>k</i><sub>3</sub></sub>{<i>x</i>[<i>k</i><sub>1</sub>,
 <i>k</i><sub>2</sub>, <i>k</i><sub>3</sub>]}[<i>k</i><sub>1</sub>,
 <i>k</i><sub>2</sub>, <i>n</i><sub>3</sub>]}[<i>k</i><sub>1</sub>,
 <i>n</i><sub>2</sub>, <i>n</i><sub>3</sub>]}[<i>n</i><sub>1</sub>,
 <i>n</i><sub>2</sub>, <i>n</i><sub>3</sub>]</li>
 </ul>
 <p>
 Data are provided as the pair (real part, imaginary part). The real
 part is assumed to be provided as an array that contains raster data,
 with the convention that the indexing for the horizontal dimension is
 faster than the indexing for the vertical dimension, itself faster
 than the indexing for the depth dimension. The imaginary part follows
 the same organization.
 </p>
 <p>
 The two buffers that are provided to some methods must have the same
 length as the data. They are used internally as temporary storage for
 the computations. They are there as a convenience to avoid repeated
 allocations.
 </p>
 ********************************************************************/
public class AcademicFFT

{ /* begin class AcademicFFT */

/*....................................................................
	AcademicFFT public enum constants
....................................................................*/
/*********************************************************************
 <p>
 This enumeration provides the constants that describe the type of
 input (complex or real) of a Fourier transform.
 </p>
 @see #makeHermitian(double[],double[],double[],double[])
 @see #makeHermitian(float[],float[],float[],float[])
 ********************************************************************/
public enum InputDataType {
/*********************************************************************
 <p>
 The input of the Fourier transform is complex, made of a real and an
 imaginary part. The two parts must be of equal length; moreover, this
 length must match the length inferred at creation time of this
 object.
 </p>
 ********************************************************************/
	COMPLEXINPUT,
/*********************************************************************
 <p>
 The input of the Fourier transform is real, although the storage for
 the imaginary part must still be provided to receive the output of
 the Fourier transform. The two parts must be of equal length;
 moreover, this length must match the length inferred at creation time
 of this object.
 </p>
 ********************************************************************/
	REALINPUT
}

/*....................................................................
	AcademicFFT protected variables
....................................................................*/
protected double[] imBufferDouble;
protected double[] imDataDouble;
protected double[] reBufferDouble;
protected double[] reDataDouble;
protected float[] imBufferFloat;
protected float[] imDataFloat;
protected float[] reBufferFloat;
protected float[] reDataFloat;

/*....................................................................
	AcademicFFT private variables
....................................................................*/
private Integer depth;
private Integer height;
private Integer width;
private int dataLength;
private int dimensions;
private int firstDimension;
private int fourierOrigin1;
private int fourierOrigin2;
private int fourierOrigin3;

/*....................................................................
	AcademicFFT private enum constants
....................................................................*/
private enum Algorithm {
	BRUTEFORCE,
	COPRIMEFACTOR,
	DUOREAL,
	EVENREAL,
	LENGTH1,
	LENGTH2,
	LENGTH3,
	LENGTH4,
	LENGTH5,
	LENGTH6,
	LENGTH8,
	MIXEDRADIX,
	PADDEDRADER,
	RADER,
	RADIX2,
	SPLITRADIX
}

private final boolean PARALLELPROCESSING = true;

/*....................................................................
	AcademicFFT inner classes
....................................................................*/
/*====================================================================
|	DFTDouble
\===================================================================*/
static class DFTDouble

{ /* begin class DFTDouble */

/*....................................................................
	DFTDouble variables
....................................................................*/
protected double[] imData;
protected double[] reData;
protected int startIndex;
protected int stride;

/*....................................................................
	DFTDouble constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTDouble (
	final double[] reData,
	final double[] imData,
	final int startIndex,
	final int stride
) {
	this.reData = reData;
	this.imData = imData;
	this.startIndex = startIndex;
	this.stride = stride;
} /* end DFTDouble */

} /* end class DFTDouble */

/*====================================================================
|	DFTFloat
\===================================================================*/
static class DFTFloat

{ /* begin class DFTFloat */

/*....................................................................
	DFTFloat variables
....................................................................*/
protected float[] imData;
protected float[] reData;
protected int startIndex;
protected int stride;

/*....................................................................
	DFTFloat constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTFloat (
	final float[] reData,
	final float[] imData,
	final int startIndex,
	final int stride
) {
	this.reData = reData;
	this.imData = imData;
	this.startIndex = startIndex;
	this.stride = stride;
} /* end DFTFloat */

} /* end class DFTFloat */

/*====================================================================
|	DFTBruteForce
\===================================================================*/
static class DFTBruteForce

{ /* begin class DFTBruteForce */

/*....................................................................
	DFTBruteForce static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
	final int transformLength
) {
	if (FFTSetup.taboos.contains(new Integer(transformLength))) {
		return(-1L);
	}
	final long K = (long)transformLength;
	return(FFTSetup.FLALLOC * 2L
		+ FFTSetup.FLOP * (K * 2L + (K - 1L) * (K - 1L) * 8L)
		+ FFTSetup.FLASSIGN * (2L + K * 2L + 2L + (K- 1L) * (2L
			+ (K - 1L) * 2L + 2L) + K * 2L)
		+ FFTSetup.INTALLOC * 9L
		+ FFTSetup.INTOP * (K * 3L + 1L + (K - 1L) * (3L + (K - 1L) * 7L + 1L)
			+ K * 3L)
		+ FFTSetup.INTASSIGN * (5L + K * 2L + 2L + (K - 1L) * (4L
			+ (K - 1L) * 4L + 1L) + 2L + K * 2L)
		+ FFTSetup.IDX * (K * 2L + 2L + (K - 1L) * (2L + (K - 1L) * 8L + 2L)
			+ K * 4L)
		+ FFTSetup.NEWOBJ * 0L
	);
} /* end cost */

} /* end class DFTBruteForce */

/*====================================================================
|	DFTBruteForceDouble
\===================================================================*/
static class DFTBruteForceDouble
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTBruteForceDouble */

/*....................................................................
	DFTBruteForceDouble private variables
....................................................................*/
private double[] imBuffer;
private double[] imUnitRoot;
private double[] reBuffer;
private double[] reUnitRoot;

/*....................................................................
	DFTBruteForceDouble constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTBruteForceDouble (
	final double[] reData,
	final double[] imData,
	final double[] reBuffer,
	final double[] imBuffer,
	final int startIndex,
	final int stride,
	final double[] reUnitRoot,
	final double[] imUnitRoot
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.reUnitRoot = reUnitRoot;
	this.imUnitRoot = imUnitRoot;
} /* end DFTBruteForceDouble */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int transformLength = reUnitRoot.length;
	final int d1 = stride;
	int n0 = startIndex;
	double re = 0.0;
	double im = 0.0;
	int k1 = startIndex;
	for (int n = 0; (n < transformLength); n++) {
		re += reData[k1];
		im += imData[k1];
		k1 += d1;
	}
	reBuffer[n0] = re;
	imBuffer[n0] = im;
	n0 += d1;
	for (int m = 1; (m < transformLength); m++) {
		re = reData[startIndex];
		im = imData[startIndex];
		int m1 = m;
		k1 = startIndex + d1;
		for (int n = 1; (n < transformLength); n++) {
			re += reData[k1] * reUnitRoot[m1] - imData[k1] * imUnitRoot[m1];
			im += reData[k1] * imUnitRoot[m1] + imData[k1] * reUnitRoot[m1];
			m1 += m;
			m1 -= transformLength * (m1 / transformLength);
			k1 += d1;
		}
		reBuffer[n0] = re;
		imBuffer[n0] = im;
		n0 += d1;
	}
	n0 = startIndex;
	for (int m = 0; (m < transformLength); m++) {
		reData[n0] = reBuffer[n0];
		imData[n0] = imBuffer[n0];
		n0 += d1;
	}
} /* end run */

/*....................................................................
	DFTBruteForceDouble static methods
....................................................................*/
/*------------------------------------------------------------------*/
static double[] getImUnitRoot (
	final int transformLength
) {
	final double[] imUnitRoot = new double[transformLength];
	final double angularStep = -2.0 * PI / (double)transformLength;
	for (int k = 0; (k < transformLength); k++) {
		imUnitRoot[k] = sin((double)k * angularStep);
	}
	return(imUnitRoot);
} /* end getImUnitRoot */

/*------------------------------------------------------------------*/
static double[] getReUnitRoot (
	final int transformLength
) {
	final double[] reUnitRoot = new double[transformLength];
	final double angularStep = -2.0 * PI / (double)transformLength;
	for (int k = 0; (k < transformLength); k++) {
		reUnitRoot[k] = cos((double)k * angularStep);
	}
	return(reUnitRoot);
} /* end getReUnitRoot */

} /* end class DFTBruteForceDouble */

/*====================================================================
|	DFTBruteForceFloat
\===================================================================*/
static class DFTBruteForceFloat
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTBruteForceFloat */

/*....................................................................
	DFTBruteForceFloat private variables
....................................................................*/
private float[] imBuffer;
private float[] imUnitRoot;
private float[] reBuffer;
private float[] reUnitRoot;

/*....................................................................
	DFTBruteForceFloat constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTBruteForceFloat (
	final float[] reData,
	final float[] imData,
	final float[] reBuffer,
	final float[] imBuffer,
	final int startIndex,
	final int stride,
	final float[] reUnitRoot,
	final float[] imUnitRoot
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.reUnitRoot = reUnitRoot;
	this.imUnitRoot = imUnitRoot;
} /* end DFTBruteForceFloat */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int transformLength = reUnitRoot.length;
	final int d1 = stride;
	int n0 = startIndex;
	float re = 0.0F;
	float im = 0.0F;
	int k1 = startIndex;
	for (int n = 0; (n < transformLength); n++) {
		re += reData[k1];
		im += imData[k1];
		k1 += d1;
	}
	reBuffer[n0] = re;
	imBuffer[n0] = im;
	n0 += d1;
	for (int m = 1; (m < transformLength); m++) {
		re = reData[startIndex];
		im = imData[startIndex];
		int m1 = m;
		k1 = startIndex + d1;
		for (int n = 1; (n < transformLength); n++) {
			re += reData[k1] * reUnitRoot[m1] - imData[k1] * imUnitRoot[m1];
			im += reData[k1] * imUnitRoot[m1] + imData[k1] * reUnitRoot[m1];
			m1 += m;
			m1 -= transformLength * (m1 / transformLength);
			k1 += d1;
		}
		reBuffer[n0] = re;
		imBuffer[n0] = im;
		n0 += d1;
	}
	n0 = startIndex;
	for (int m = 0; (m < transformLength); m++) {
		reData[n0] = reBuffer[n0];
		imData[n0] = imBuffer[n0];
		n0 += d1;
	}
} /* end run */

/*....................................................................
	DFTBruteForceFloat static methods
....................................................................*/
/*------------------------------------------------------------------*/
static float[] getImUnitRoot (
	final int transformLength
) {
	final float[] imUnitRoot = new float[transformLength];
	final float angularStep = -2.0F * (float)PI / (float)transformLength;
	for (int k = 0; (k < transformLength); k++) {
		imUnitRoot[k] = (float)sin((double)((float)k * angularStep));
	}
	return(imUnitRoot);
} /* end getImUnitRoot */

/*------------------------------------------------------------------*/
static float[] getReUnitRoot (
	final int transformLength
) {
	final float[] reUnitRoot = new float[transformLength];
	final float angularStep = -2.0F * (float)PI / (float)transformLength;
	for (int k = 0; (k < transformLength); k++) {
		reUnitRoot[k] = (float)cos((double)((float)k * angularStep));
	}
	return(reUnitRoot);
} /* end getReUnitRoot */

} /* end class DFTBruteForceFloat */

/*====================================================================
|	DFTBruteForceReal
\===================================================================*/
static class DFTBruteForceReal

{ /* begin class DFTBruteForceReal */

/*....................................................................
	DFTBruteForceReal static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
	final int transformLength
) {
	if (FFTSetup.taboos.contains(new Integer(transformLength))) {
		return(-1L);
	}
	final long K = (long)transformLength;
	final long k = (long)(transformLength >> 1);
	return(FFTSetup.FLALLOC * 2L
		+ FFTSetup.FLOP * (K * 1L + k * (K * 4L))
		+ FFTSetup.FLASSIGN * (2L + K * 1L + 1L + k * (2L + K * 2L + 2L)
			+ k * 2L + 2L)
		+ FFTSetup.INTALLOC * 11L
		+ FFTSetup.INTOP * (4L + K * 3L + k * (2L + K * 7L + 1L) + 1L + k * 3L)
		+ FFTSetup.INTASSIGN * (6L + K * 2L + 1L + k * (4L + K * 4L + 1L) + 2L
			+ k * 2L)
		+ FFTSetup.IDX * (K * 1L + 1L + k * (K * 4L + 2L) + k * 4L + 3L)
		+ FFTSetup.NEWOBJ * 0L
	);
} /* end cost */

} /* end class DFTBruteForceReal */

/*====================================================================
|	DFTBruteForceRealDouble
\===================================================================*/
static class DFTBruteForceRealDouble
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTBruteForceRealDouble */

/*....................................................................
	DFTBruteForceRealDouble private variables
....................................................................*/
private double[] imBuffer;
private double[] imUnitRoot;
private double[] reBuffer;
private double[] reUnitRoot;

/*....................................................................
	DFTBruteForceRealDouble constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTBruteForceRealDouble (
	final double[] reData,
	final double[] imData,
	final double[] reBuffer,
	final double[] imBuffer,
	final int startIndex,
	final int stride,
	final double[] reUnitRoot,
	final double[] imUnitRoot
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.reUnitRoot = reUnitRoot;
	this.imUnitRoot = imUnitRoot;
} /* end DFTBruteForceRealDouble */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	double re = 0.0;
	double im = 0.0;
	final int transformLength = reUnitRoot.length;
	final int halfTransformLength = transformLength >> 1;
	final int d1 = stride;
	int k1 = startIndex;
	int n0 = startIndex + d1;
	if (0 == (transformLength & 1)) {
		for (int n = 0; (n < halfTransformLength); n++) {
			re += reData[k1];
			im += reData[k1];
			k1 += d1;
			re += reData[k1];
			im -= reData[k1];
			k1 += d1;
		}
		reBuffer[startIndex] = re;
		int n1 = startIndex + halfTransformLength * d1;
		reBuffer[n1] = im;
		for (int m = 1; (m < halfTransformLength); m++) {
			re = 0.0;
			im = 0.0;
			int m1 = 0;
			k1 = startIndex;
			for (int n = 0; (n < transformLength); n++) {
				re += reData[k1] * reUnitRoot[m1];
				im += reData[k1] * imUnitRoot[m1];
				m1 += m;
				m1 -= transformLength * (m1 / transformLength);
				k1 += d1;
			}
			reBuffer[n0] = re;
			imBuffer[n0] = im;
			n0 += d1;
		}
		reData[n0] = reBuffer[n0];
		imData[n0] = 0.0;
		n0 -= d1;
		for (int m = 1; (m < halfTransformLength); m++) {
			reData[n0] = reBuffer[n0];
			imData[n0] = imBuffer[n0];
			n0 -= d1;
		}
		reData[n0] = reBuffer[n0];
		imData[n0] = 0.0;
	}
	else {
		for (int n = 0; (n < transformLength); n++) {
			re += reData[k1];
			k1 += d1;
		}
		reBuffer[startIndex] = re;
		for (int m = 1; (m <= halfTransformLength); m++) {
			re = 0.0;
			im = 0.0;
			int m1 = 0;
			k1 = startIndex;
			for (int n = 0; (n < transformLength); n++) {
				re += reData[k1] * reUnitRoot[m1];
				im += reData[k1] * imUnitRoot[m1];
				m1 += m;
				m1 -= transformLength * (m1 / transformLength);
				k1 += d1;
			}
			reBuffer[n0] = re;
			imBuffer[n0] = im;
			n0 += d1;
		}
		int n1 = n0 - d1;
		for (int m = 1; (m <= halfTransformLength); m++) {
			reData[n1] = reBuffer[n1];
			imData[n1] = imBuffer[n1];
			n1 -= d1;
		}
		reData[n1] = reBuffer[n1];
		imData[n1] = 0.0;
	}
} /* end run */

} /* end class DFTBruteForceRealDouble */

/*====================================================================
|	DFTBruteForceRealFloat
\===================================================================*/
static class DFTBruteForceRealFloat
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTBruteForceRealFloat */

/*....................................................................
	DFTBruteForceRealFloat private variables
....................................................................*/
private float[] imBuffer;
private float[] imUnitRoot;
private float[] reBuffer;
private float[] reUnitRoot;

/*....................................................................
	DFTBruteForceRealFloat constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTBruteForceRealFloat (
	final float[] reData,
	final float[] imData,
	final float[] reBuffer,
	final float[] imBuffer,
	final int startIndex,
	final int stride,
	final float[] reUnitRoot,
	final float[] imUnitRoot
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.reUnitRoot = reUnitRoot;
	this.imUnitRoot = imUnitRoot;
} /* end DFTBruteForceRealFloat */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	float re = 0.0F;
	float im = 0.0F;
	final int transformLength = reUnitRoot.length;
	final int halfTransformLength = transformLength >> 1;
	final int d1 = stride;
	int k1 = startIndex;
	int n0 = startIndex + d1;
	if (0 == (transformLength & 1)) {
		for (int n = 0; (n < halfTransformLength); n++) {
			re += reData[k1];
			im += reData[k1];
			k1 += d1;
			re += reData[k1];
			im -= reData[k1];
			k1 += d1;
		}
		reBuffer[startIndex] = re;
		int n1 = startIndex + halfTransformLength * d1;
		reBuffer[n1] = im;
		for (int m = 1; (m < halfTransformLength); m++) {
			re = 0.0F;
			im = 0.0F;
			int m1 = 0;
			k1 = startIndex;
			for (int n = 0; (n < transformLength); n++) {
				re += reData[k1] * reUnitRoot[m1];
				im += reData[k1] * imUnitRoot[m1];
				m1 += m;
				m1 -= transformLength * (m1 / transformLength);
				k1 += d1;
			}
			reBuffer[n0] = re;
			imBuffer[n0] = im;
			n0 += d1;
		}
		reData[n0] = reBuffer[n0];
		imData[n0] = 0.0F;
		n0 -= d1;
		for (int m = 1; (m < halfTransformLength); m++) {
			reData[n0] = reBuffer[n0];
			imData[n0] = imBuffer[n0];
			n0 -= d1;
		}
		reData[n0] = reBuffer[n0];
		imData[n0] = 0.0F;
	}
	else {
		for (int n = 0; (n < transformLength); n++) {
			re += reData[k1];
			k1 += d1;
		}
		reBuffer[startIndex] = re;
		for (int m = 1; (m <= halfTransformLength); m++) {
			re = 0.0F;
			im = 0.0F;
			int m1 = 0;
			k1 = startIndex;
			for (int n = 0; (n < transformLength); n++) {
				re += reData[k1] * reUnitRoot[m1];
				im += reData[k1] * imUnitRoot[m1];
				m1 += m;
				m1 -= transformLength * (m1 / transformLength);
				k1 += d1;
			}
			reBuffer[n0] = re;
			imBuffer[n0] = im;
			n0 += d1;
		}
		int n1 = n0 - d1;
		for (int m = 1; (m <= halfTransformLength); m++) {
			reData[n1] = reBuffer[n1];
			imData[n1] = imBuffer[n1];
			n1 -= d1;
		}
		reData[n1] = reBuffer[n1];
		imData[n1] = 0.0F;
	}
} /* end run */

} /* end class DFTBruteForceRealFloat */

/*====================================================================
|	DFTCoprimeFactor
\===================================================================*/
static class DFTCoprimeFactor

{ /* begin class DFTCoprimeFactor */

/*....................................................................
	DFTCoprimeFactor static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
	final int K1,
	final int K2
) {
	if (FFTSetup.taboos.contains(new Integer(K1))
		|| FFTSetup.taboos.contains(new Integer(K2))) {
		return(-1L);
	}
	final long K = (long)K1 * (long)K2;
	return(FFTSetup.FLALLOC * 0L
		+ FFTSetup.FLOP * 0L
		+ FFTSetup.FLASSIGN * (K * 2L + K * 2L)
		+ FFTSetup.INTALLOC * 11L
		+ FFTSetup.INTOP * (1L + K * 5L + 2L + K2 * 3L + 1L + K1 * 3L + K * 5L)
		+ FFTSetup.INTASSIGN * (4L + K * 3L + 4L + K2 * 2L + 2L + K1 * 2L + 2L
			+ K * 3L)
		+ FFTSetup.IDX * (K * 5L + K * 5L)
		+ FFTSetup.NEWOBJ * (K2 * 1L + K1 * 1L)
		+ (long)K2 * FFTSetup.cost(K1) + (long)K1 * FFTSetup.cost(K2)
	);
} /* end cost */

/*------------------------------------------------------------------*/
static int[] getChineseRemainderShuffling (
	final int K1,
	final int K2
) {
	final int K1K2 = K1 * K2;
	int[] chineseRemainderShuffling = new int[K1K2];
	final int p1 = modularMultiplicativeInverse(K1, K2);
	final int p2 = modularMultiplicativeInverse(K2, K1);
	int p = 0;
	for (int k2 = 0; (k2 < K2); k2++) {
		int q = p * p1;
		for (int k1 = 0; (k1 < K1); k1++) {
			chineseRemainderShuffling[q - K1K2 * (q / K1K2)] = p++;
			q += p2 * K2;
		}
	}
	return(chineseRemainderShuffling);
} /* end getChineseRemainderShuffling */

/*------------------------------------------------------------------*/
static int[] getRuritanianShuffling (
	final int K1,
	final int K2
) {
	final int K1K2 = K1 * K2;
	int[] ruritanianShuffling = new int[K1K2];
	int p = 0;
	for (int k2 = 0; (k2 < K2); k2++) {
		int q = p;
		for (int k1 = 0; (k1 < K1); k1++) {
			ruritanianShuffling[p++] = q - K1K2 * (q / K1K2);
			q += K2;
		}
	}
	return(ruritanianShuffling);
} /* end getRuritanianShuffling */

/*....................................................................
	DFTCoprimeFactor private methods
....................................................................*/
/*------------------------------------------------------------------*/
static private int[] extendedGreatestCommonDivisor (
	int m,
	int n
) {
	if ((m < 1) || (n < 1)) {
		return(null);
	}
	int a = 1;
	int b = 0;
	int p = 0;
	int q = 1;
	while (0 != n) {
		final int i = m;
		final int j = a;
		final int k = b;
		m = n;
		a = p;
		b = q;
		final int f = i / n;
		n = i - f * n;
		p = j - f * p;
		q = k - f * q;
	}
	return(new int[] { // a * m + b * n = gcd(m, n)
		m, // gcd
		a,
		b
	});
} /* end extendedGreatestCommonDivisor */

/*------------------------------------------------------------------*/
static private int modularMultiplicativeInverse (
	int n,
	final int modulo
) {
	if ((n < 1) || (modulo < 1)) {
		return(0);
	}
	n = extendedGreatestCommonDivisor(n, modulo)[1];
	return((n < 0) ? (n + modulo) : (n));
} /* end modularMultiplicativeInverse */

} /* end class DFTCoprimeFactor */

/*====================================================================
|	DFTCoprimeFactorDouble
\===================================================================*/
static class DFTCoprimeFactorDouble
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTCoprimeFactorDouble */

/*....................................................................
	DFTCoprimeFactorDouble private variables
....................................................................*/
private double[] imBuffer;
private double[] reBuffer;
private int[] ruritanian;
private int[] chinese;
private int K1;

/*....................................................................
	DFTCoprimeFactorDouble constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTCoprimeFactorDouble (
	final double[] reData,
	final double[] imData,
	final double[] reBuffer,
	final double[] imBuffer,
	final int startIndex,
	final int stride,
	final int[] ruritanian,
	final int[] chinese,
	final int K1
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.ruritanian = ruritanian;
	this.chinese = chinese;
	this.K1 = K1;
} /* end DFTCoprimeFactorDouble */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int transformLength = ruritanian.length;
	final int K2 = transformLength / K1;
	int p = startIndex;
	for (int k = 0; (k < transformLength); k++) {
		final int q = startIndex + ruritanian[k] * stride;
		reBuffer[p] = reData[q];
		imBuffer[p] = imData[q];
		p += stride;
	}
	final int d1 = stride;
	final int d2 = K1 * d1;
	p = startIndex;
	final FFTSetup fft1 = FFTSetup.transforms.get(new Integer(K1));
	switch (fft1.algorithm) {
		case BRUTEFORCE: {
			for (int n = 0; (n < K2); n++) {
				new DFTBruteForceDouble(reBuffer, imBuffer, reData, imData,
					p, d1,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble).run();
				p += d2;
			}
			break;
		}
		case COPRIMEFACTOR: {
			for (int n = 0; (n < K2); n++) {
				new DFTCoprimeFactorDouble(reBuffer, imBuffer, reData, imData,
					p, d1,
					fft1.ruritanian, fft1.chinese, fft1.K1).run();
				p += d2;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength2Double(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH3: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength3Double(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH4: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength4Double(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH5: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength5Double(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH6: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength6Double(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH8: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength8Double(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case MIXEDRADIX: {
			for (int n = 0; (n < K2); n++) {
				new DFTMixedRadixDouble(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootDouble, fft1.imUnitRootDouble,
					fft1.K1).run();
				p += d2;
			}
			break;
		}
		case PADDEDRADER: {
			for (int n = 0; (n < K2); n++) {
				new DFTPaddedRaderDouble(reBuffer, imBuffer,
					p, d1, fft1.reConvolverDouble, fft1.imConvolverDouble,
					fft1.modular, fft1.inverseModular).run();
				p += d2;
			}
			break;
		}
		case RADER: {
			for (int n = 0; (n < K2); n++) {
				new DFTRaderDouble(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reConvolverDouble, fft1.imConvolverDouble,
					fft1.modular, fft1.inverseModular).run();
				p += d2;
			}
			break;
		}
		case RADIX2: {
			for (int n = 0; (n < K2); n++) {
				new DFTRadix2Double(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootDouble, fft1.imUnitRootDouble).run();
				p += d2;
			}
			break;
		}
		case SPLITRADIX: {
			for (int n = 0; (n < K2); n++) {
				new DFTSplitRadixDouble(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootDouble, fft1.imUnitRootDouble).run();
				p += d2;
			}
			break;
		}
	}
	p = startIndex;
	final FFTSetup fft2 = FFTSetup.transforms.get(new Integer(K2));
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			for (int n = 0; (n < K1); n++) {
				new DFTBruteForceDouble(reBuffer, imBuffer, reData, imData,
					p, d2, fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
				p += d1;
			}
			break;
		}
		case COPRIMEFACTOR: {
			for (int n = 0; (n < K1); n++) {
				new DFTCoprimeFactorDouble(reBuffer, imBuffer, reData, imData,
					p, d2, fft2.ruritanian, fft2.chinese, fft2.K1).run();
				p += d1;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength2Double(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH3: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength3Double(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH4: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength4Double(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH5: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength5Double(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH6: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength6Double(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH8: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength8Double(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case MIXEDRADIX: {
			for (int n = 0; (n < K1); n++) {
				new DFTMixedRadixDouble(reBuffer, imBuffer, reData, imData,
					p, d2, fft2.reUnitRootDouble, fft2.imUnitRootDouble,
					fft2.K1).run();
				p += d1;
			}
			break;
		}
		case PADDEDRADER: {
			for (int n = 0; (n < K1); n++) {
				new DFTPaddedRaderDouble(reBuffer, imBuffer,
					p, d2, fft2.reConvolverDouble, fft2.imConvolverDouble,
					fft2.modular, fft2.inverseModular).run();
				p += d1;
			}
			break;
		}
		case RADER: {
			for (int n = 0; (n < K1); n++) {
				new DFTRaderDouble(reBuffer, imBuffer, reData, imData,
					p, d2, fft2.reConvolverDouble, fft2.imConvolverDouble,
					fft2.modular, fft2.inverseModular).run();
				p += d1;
			}
			break;
		}
		case RADIX2: {
			for (int n = 0; (n < K1); n++) {
				new DFTRadix2Double(reBuffer, imBuffer, reData, imData,
					p, d2, fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
				p += d1;
			}
			break;
		}
		case SPLITRADIX: {
			for (int n = 0; (n < K1); n++) {
				new DFTSplitRadixDouble(reBuffer, imBuffer, reData, imData,
					p, d2, fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
				p += d1;
			}
			break;
		}
	}
	p = startIndex;
	for (int k = 0; (k < transformLength); k++) {
		final int q = startIndex + chinese[k] * stride;
		reData[p] = reBuffer[q];
		imData[p] = imBuffer[q];
		p += stride;
	}
} /* end run */

} /* end class DFTCoprimeFactorDouble */

/*====================================================================
|	DFTCoprimeFactorFloat
\===================================================================*/
static class DFTCoprimeFactorFloat
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTCoprimeFactorFloat */

/*....................................................................
	DFTCoprimeFactorFloat private variables
....................................................................*/
private float[] imBuffer;
private float[] reBuffer;
private int[] ruritanian;
private int[] chinese;
private int K1;

/*....................................................................
	DFTCoprimeFactorFloat constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTCoprimeFactorFloat (
	final float[] reData,
	final float[] imData,
	final float[] reBuffer,
	final float[] imBuffer,
	final int startIndex,
	final int stride,
	final int[] ruritanian,
	final int[] chinese,
	final int K1
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.ruritanian = ruritanian;
	this.chinese = chinese;
	this.K1 = K1;
} /* end DFTCoprimeFactorFloat */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int transformLength = ruritanian.length;
	final int K2 = transformLength / K1;
	int p = startIndex;
	for (int k = 0; (k < transformLength); k++) {
		final int q = startIndex + ruritanian[k] * stride;
		reBuffer[p] = reData[q];
		imBuffer[p] = imData[q];
		p += stride;
	}
	final int d1 = stride;
	final int d2 = K1 * d1;
	p = startIndex;
	final FFTSetup fft1 = FFTSetup.transforms.get(new Integer(K1));
	switch (fft1.algorithm) {
		case BRUTEFORCE: {
			for (int n = 0; (n < K2); n++) {
				new DFTBruteForceFloat(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootFloat, fft1.imUnitRootFloat).run();
				p += d2;
			}
			break;
		}
		case COPRIMEFACTOR: {
			for (int n = 0; (n < K2); n++) {
				new DFTCoprimeFactorFloat(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.ruritanian, fft1.chinese, fft1.K1).run();
				p += d2;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength2Float(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH3: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength3Float(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH4: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength4Float(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH5: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength5Float(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH6: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength6Float(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH8: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength8Float(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case MIXEDRADIX: {
			for (int n = 0; (n < K2); n++) {
				new DFTMixedRadixFloat(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootFloat, fft1.imUnitRootFloat,
					fft1.K1).run();
				p += d2;
			}
			break;
		}
		case PADDEDRADER: {
			for (int n = 0; (n < K2); n++) {
				new DFTPaddedRaderFloat(reBuffer, imBuffer,
					p, d1, fft1.reConvolverFloat, fft1.imConvolverFloat,
					fft1.modular, fft1.inverseModular).run();
				p += d2;
			}
			break;
		}
		case RADER: {
			for (int n = 0; (n < K2); n++) {
				new DFTRaderFloat(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reConvolverFloat, fft1.imConvolverFloat,
					fft1.modular, fft1.inverseModular).run();
				p += d2;
			}
			break;
		}
		case RADIX2: {
			for (int n = 0; (n < K2); n++) {
				new DFTRadix2Float(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootFloat, fft1.imUnitRootFloat).run();
				p += d2;
			}
			break;
		}
		case SPLITRADIX: {
			for (int n = 0; (n < K2); n++) {
				new DFTSplitRadixFloat(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootFloat, fft1.imUnitRootFloat).run();
				p += d2;
			}
			break;
		}
	}
	p = startIndex;
	final FFTSetup fft2 = FFTSetup.transforms.get(new Integer(K2));
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			for (int n = 0; (n < K1); n++) {
				new DFTBruteForceFloat(reBuffer, imBuffer, reData, imData,
					p, d2, fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
				p += d1;
			}
			break;
		}
		case COPRIMEFACTOR: {
			for (int n = 0; (n < K1); n++) {
				new DFTCoprimeFactorFloat(reBuffer, imBuffer, reData, imData,
					p, d2, fft2.ruritanian, fft2.chinese, fft2.K1).run();
				p += d1;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength2Float(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH3: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength3Float(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH4: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength4Float(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH5: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength5Float(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH6: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength6Float(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH8: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength8Float(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case MIXEDRADIX: {
			for (int n = 0; (n < K1); n++) {
				new DFTMixedRadixFloat(reBuffer, imBuffer, reData, imData,
					p, d2, fft2.reUnitRootFloat, fft2.imUnitRootFloat,
					fft2.K1).run();
				p += d1;
			}
			break;
		}
		case PADDEDRADER: {
			for (int n = 0; (n < K1); n++) {
				new DFTPaddedRaderFloat(reBuffer, imBuffer,
					p, d2, fft2.reConvolverFloat, fft2.imConvolverFloat,
					fft2.modular, fft2.inverseModular).run();
				p += d1;
			}
			break;
		}
		case RADER: {
			for (int n = 0; (n < K1); n++) {
				new DFTRaderFloat(reBuffer, imBuffer, reData, imData,
					p, d2, fft2.reConvolverFloat, fft2.imConvolverFloat,
					fft2.modular, fft2.inverseModular).run();
				p += d1;
			}
			break;
		}
		case RADIX2: {
			for (int n = 0; (n < K1); n++) {
				new DFTRadix2Float(reBuffer, imBuffer, reData, imData,
					p, d2, fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
				p += d1;
			}
			break;
		}
		case SPLITRADIX: {
			for (int n = 0; (n < K1); n++) {
				new DFTSplitRadixFloat(reBuffer, imBuffer, reData, imData,
					p, d2, fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
				p += d1;
			}
			break;
		}
	}
	p = startIndex;
	for (int k = 0; (k < transformLength); k++) {
		final int q = startIndex + chinese[k] * stride;
		reData[p] = reBuffer[q];
		imData[p] = imBuffer[q];
		p += stride;
	}
} /* end run */

} /* end class DFTCoprimeFactorFloat */

/*====================================================================
|	DFTCoprimeFactorReal
\===================================================================*/
static class DFTCoprimeFactorReal

{ /* begin class DFTCoprimeFactorReal */

/*....................................................................
	DFTCoprimeFactorReal static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
	final int K1,
	final int K2
) {
	if (FFTSetup.taboos.contains(new Integer(K1))
		|| FFTSetup.taboos.contains(new Integer(K2))) {
		return(-1L);
	}
	final long K = (long)K1 * (long)K2;
	final long k = K >> 1L;
	final long k1 = (long)(K1 >> 1);
	final long k2 = (long)(K2 >> 1);
	return(FFTSetup.FLALLOC * 0L
		+ FFTSetup.FLOP * ((K >> 2L) * 1L)
		+ FFTSetup.FLASSIGN * (K * 2L + (k + 1L) * 2L)
		+ FFTSetup.INTALLOC * 12L
		+ FFTSetup.INTOP * (3L + K * 5L + 4L + (long)K1 * 3L + 1L
			+ (k2 + 1L) * 3L + 2L + (k + 1L) * 6L)
		+ FFTSetup.INTASSIGN * (5L + K * 3L + 4L + (long)K1 * 2L + 2L
			+ (k2 + 1L) * 2L + 3L + (k + 1L) * 4L)
		+ FFTSetup.IDX * (K * 5L + (k + 1L) * 5L)
		+ FFTSetup.NEWOBJ * ((long)K1 * 1L + (long)K2 * 1L)
		+ k1 * FFTSetupDuoReal.cost(K2) + (k2 + 1L) * FFTSetup.cost(K1)
	);
} /* end cost */

/*------------------------------------------------------------------*/
static int[] getTruncatedChineseRemainderShuffling (
	final int K1,
	final int K2
) {
	final int K1K2 = K1 * K2;
	final int halfK1K2 = (K1K2 >> 1) + 1;
	final int halfK2 = (K2 >> 1) + 1;
	int[] chineseRemainderShuffling = new int[halfK1K2];
	final int p1 = DFTCoprimeFactor.modularMultiplicativeInverse(K1, K2);
	final int p2 = DFTCoprimeFactor.modularMultiplicativeInverse(K2, K1);
	int n = 0;
	for (int k2 = 0; (k2 < K2); k2++) {
		int q = n * p1;
		for (int k1 = 0; (k1 < K1); k1++) {
			final int p = q - K1K2 * (q / K1K2);
			if (p < halfK1K2) {
				final int i0 = n / K1;
				final int j0 = n - K1 * i0;
				chineseRemainderShuffling[p] = (i0 < halfK2) ? (n)
					: ((0 == j0) ? (n - K1K2) : (n - K1K2 - K1));
			}
			q += p2 * K2;
			n++;
		}
	}
	return(chineseRemainderShuffling);
} /* end getTruncatedChineseRemainderShuffling */

} /* end class DFTCoprimeFactorReal */

/*====================================================================
|	DFTCoprimeFactorRealDouble
\===================================================================*/
static class DFTCoprimeFactorRealDouble
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTCoprimeFactorRealDouble */

/*....................................................................
	DFTCoprimeFactorRealDouble private variables
....................................................................*/
private double[] imBuffer;
private double[] reBuffer;
private int[] ruritanian;
private int[] chinese;
private int K1;

/*....................................................................
	DFTCoprimeFactorRealDouble constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTCoprimeFactorRealDouble (
	final double[] reData,
	final double[] imData,
	final double[] reBuffer,
	final double[] imBuffer,
	final int startIndex,
	final int stride,
	final int[] ruritanian,
	final int[] chinese,
	final int K1
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.ruritanian = ruritanian;
	this.chinese = chinese;
	this.K1 = K1;
} /* end DFTCoprimeFactorRealDouble */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int transformLength = ruritanian.length;
	final int K2 = transformLength / K1;
	final int halfK2 = (K2 >> 1) + 1;
	int p = startIndex;
	for (int k = 0; (k < transformLength); k++) {
		final int q = startIndex + ruritanian[k] * stride;
		reBuffer[p] = reData[q];
		imBuffer[p] = imData[q];
		p += stride;
	}
	final int d1 = stride;
	final int d2 = K1 * d1;
	p = startIndex;
	int k1 = 0;
	if (1 == (K1 & 1)) {
		final FFTSetupReal fft2 = FFTSetupReal.transforms.get(new Integer(K2));
		switch (fft2.algorithm) {
			case BRUTEFORCE: {
				new DFTBruteForceRealDouble(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
				break;
			}
			case COPRIMEFACTOR: {
				new DFTCoprimeFactorRealDouble(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.ruritanian, fft2.chinese, fft2.K1).run();
				break;
			}
			case DUOREAL: {
				throw(new IllegalStateException());
			}
			case EVENREAL: {
				new DFTEvenRealDouble(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
				break;
			}
			case LENGTH1: {
				throw(new IllegalStateException());
			}
			case LENGTH2: {
				new DFTLength2RealDouble(reBuffer, imBuffer, p, d2).run();
				break;
			}
			case LENGTH3: {
				new DFTLength3RealDouble(reBuffer, imBuffer, p, d2).run();
				break;
			}
			case LENGTH4: {
				new DFTLength4RealDouble(reBuffer, imBuffer, p, d2).run();
				break;
			}
			case LENGTH5: {
				new DFTLength5RealDouble(reBuffer, imBuffer, p, d2).run();
				break;
			}
			case LENGTH6: {
				new DFTLength6RealDouble(reBuffer, imBuffer, p, d2).run();
				break;
			}
			case LENGTH8: {
				new DFTLength8RealDouble(reBuffer, imBuffer, p, d2).run();
				break;
			}
			case MIXEDRADIX: {
				new DFTMixedRadixRealDouble(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble,
					fft2.K1).run();
				break;
			}
			case PADDEDRADER: {
				new DFTPaddedRaderRealDouble(reBuffer, imBuffer,
					p, d2, fft2.reConvolverDouble, fft2.imConvolverDouble,
					fft2.modular, fft2.inverseModular).run();
				break;
			}
			case RADER: {
				new DFTRaderRealDouble(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.reConvolverDouble, fft2.imConvolverDouble,
					fft2.modular, fft2.inverseModular).run();
				break;
			}
			case RADIX2: {
				new DFTRadix2RealDouble(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.reUnitRootEvenDouble, fft2.imUnitRootEvenDouble,
					fft2.reUnitRootOddDouble, fft2.imUnitRootOddDouble).run();
				break;
			}
			case SPLITRADIX: {
				new DFTSplitRadixRealDouble(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
				break;
			}
		}
		p += d1;
		k1++;
	}
	final FFTSetupDuoReal fft2 =
		FFTSetupDuoReal.transforms.get(new Integer(K2));
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			while (k1++ < K1) {
				new DFTBruteForceRealDouble(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
				p += d1;
			}
			break;
		}
		case COPRIMEFACTOR: {
			while (k1++ < K1) {
				new DFTCoprimeFactorRealDouble(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.ruritanian, fft2.chinese, fft2.K1).run();
				p += d1;
			}
			break;
		}
		case DUOREAL: {
			while (k1++ < K1) {
				new DFTDuoRealDouble(reBuffer, imBuffer,
					reData, imData, p, p + d1, d2, K2).run();
				p += 2 * d1;
				k1++;
			}
			break;
		}
		case EVENREAL: {
			while (k1++ < K1) {
				new DFTEvenRealDouble(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
				p += d1;
			}
			break;
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			while (k1++ < K1) {
				new DFTLength2RealDouble(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH3: {
			while (k1++ < K1) {
				new DFTLength3RealDouble(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH4: {
			while (k1++ < K1) {
				new DFTLength4RealDouble(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH5: {
			while (k1++ < K1) {
				new DFTLength5RealDouble(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH6: {
			while (k1++ < K1) {
				new DFTLength6RealDouble(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH8: {
			while (k1++ < K1) {
				new DFTLength8RealDouble(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case MIXEDRADIX: {
			while (k1++ < K1) {
				new DFTMixedRadixRealDouble(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble,
					fft2.K1).run();
				p += d1;
			}
			break;
		}
		case PADDEDRADER: {
			while (k1++ < K1) {
				new DFTPaddedRaderRealDouble(reBuffer, imBuffer,
					p, d2, fft2.reConvolverDouble, fft2.imConvolverDouble,
					fft2.modular, fft2.inverseModular).run();
				p += d1;
			}
			break;
		}
		case RADER: {
			while (k1++ < K1) {
				new DFTRaderRealDouble(reBuffer, imBuffer, reData, imData,
					p, d2, fft2.reConvolverDouble, fft2.imConvolverDouble,
					fft2.modular, fft2.inverseModular).run();
				p += d1;
			}
			break;
		}
		case RADIX2: {
			while (k1++ < K1) {
				new DFTRadix2RealDouble(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.reUnitRootEvenDouble, fft2.imUnitRootEvenDouble,
					fft2.reUnitRootOddDouble, fft2.imUnitRootOddDouble).run();
				p += d1;
			}
			break;
		}
		case SPLITRADIX: {
			while (k1++ < K1) {
				new DFTSplitRadixRealDouble(reBuffer, imBuffer, reData, imData,
					p, d2, fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
				p += d1;
			}
			break;
		}
	}
	p = startIndex;
	final FFTSetup fft1 = FFTSetup.transforms.get(new Integer(K1));
	switch (fft1.algorithm) {
		case BRUTEFORCE: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTBruteForceDouble(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootDouble, fft1.imUnitRootDouble).run();
				p += d2;
			}
			break;
		}
		case COPRIMEFACTOR: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTCoprimeFactorDouble(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.ruritanian, fft1.chinese, fft1.K1).run();
				p += d2;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength2Double(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH3: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength3Double(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH4: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength4Double(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH5: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength5Double(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH6: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength6Double(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH8: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength8Double(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case MIXEDRADIX: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTMixedRadixDouble(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootDouble, fft1.imUnitRootDouble,
					fft1.K1).run();
				p += d2;
			}
			break;
		}
		case PADDEDRADER: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTPaddedRaderDouble(reBuffer, imBuffer,
					p, d1, fft1.reConvolverDouble, fft1.imConvolverDouble,
					fft1.modular, fft1.inverseModular).run();
				p += d2;
			}
			break;
		}
		case RADER: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTRaderDouble(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reConvolverDouble, fft1.imConvolverDouble,
					fft1.modular, fft1.inverseModular).run();
				p += d2;
			}
			break;
		}
		case RADIX2: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTRadix2Double(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootDouble, fft1.imUnitRootDouble).run();
				p += d2;
			}
			break;
		}
		case SPLITRADIX: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTSplitRadixDouble(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootDouble, fft1.imUnitRootDouble).run();
				p += d2;
			}
			break;
		}
	}
	p = startIndex;
	for (int k = 0, K = (transformLength >> 1) + 1; (k < K); k++) {
		int q = chinese[k];
		if (q < 0) {
			q = startIndex - q * stride;
			reData[p] = reBuffer[q];
			imData[p] = -imBuffer[q];
		}
		else {
			q = startIndex + q * stride;
			reData[p] = reBuffer[q];
			imData[p] = imBuffer[q];
		}
		p += stride;
	}
} /* end run */

} /* end class DFTCoprimeFactorRealDouble */

/*====================================================================
|	DFTCoprimeFactorRealFloat
\===================================================================*/
static class DFTCoprimeFactorRealFloat
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTCoprimeFactorRealFloat */

/*....................................................................
	DFTCoprimeFactorRealFloat private variables
....................................................................*/
private float[] imBuffer;
private float[] reBuffer;
private int[] ruritanian;
private int[] chinese;
private int K1;

/*....................................................................
	DFTCoprimeFactorRealFloat constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTCoprimeFactorRealFloat (
	final float[] reData,
	final float[] imData,
	final float[] reBuffer,
	final float[] imBuffer,
	final int startIndex,
	final int stride,
	final int[] ruritanian,
	final int[] chinese,
	final int K1
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.ruritanian = ruritanian;
	this.chinese = chinese;
	this.K1 = K1;
} /* end DFTCoprimeFactorRealFloat */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int transformLength = ruritanian.length;
	final int K2 = transformLength / K1;
	final int halfK2 = (K2 >> 1) + 1;
	int p = startIndex;
	for (int k = 0; (k < transformLength); k++) {
		final int q = startIndex + ruritanian[k] * stride;
		reBuffer[p] = reData[q];
		imBuffer[p] = imData[q];
		p += stride;
	}
	final int d1 = stride;
	final int d2 = K1 * d1;
	p = startIndex;
	int k1 = 0;
	if (1 == (K1 & 1)) {
		final FFTSetupReal fft2 = FFTSetupReal.transforms.get(new Integer(K2));
		switch (fft2.algorithm) {
			case BRUTEFORCE: {
				new DFTBruteForceRealFloat(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
				break;
			}
			case COPRIMEFACTOR: {
				new DFTCoprimeFactorRealFloat(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.ruritanian, fft2.chinese, fft2.K1).run();
				break;
			}
			case DUOREAL: {
				throw(new IllegalStateException());
			}
			case EVENREAL: {
				new DFTEvenRealFloat(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
				break;
			}
			case LENGTH1: {
				throw(new IllegalStateException());
			}
			case LENGTH2: {
				new DFTLength2RealFloat(reBuffer, imBuffer, p, d2).run();
				break;
			}
			case LENGTH3: {
				new DFTLength3RealFloat(reBuffer, imBuffer, p, d2).run();
				break;
			}
			case LENGTH4: {
				new DFTLength4RealFloat(reBuffer, imBuffer, p, d2).run();
				break;
			}
			case LENGTH5: {
				new DFTLength5RealFloat(reBuffer, imBuffer, p, d2).run();
				break;
			}
			case LENGTH6: {
				new DFTLength6RealFloat(reBuffer, imBuffer, p, d2).run();
				break;
			}
			case LENGTH8: {
				new DFTLength8RealFloat(reBuffer, imBuffer, p, d2).run();
				break;
			}
			case MIXEDRADIX: {
				new DFTMixedRadixRealFloat(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat,
					fft2.K1).run();
				break;
			}
			case PADDEDRADER: {
				new DFTPaddedRaderRealFloat(reBuffer, imBuffer,
					p, d2, fft2.reConvolverFloat, fft2.imConvolverFloat,
					fft2.modular, fft2.inverseModular).run();
				break;
			}
			case RADER: {
				new DFTRaderRealFloat(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.reConvolverFloat, fft2.imConvolverFloat,
					fft2.modular, fft2.inverseModular).run();
				break;
			}
			case RADIX2: {
				new DFTRadix2RealFloat(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.reUnitRootEvenFloat, fft2.imUnitRootEvenFloat,
					fft2.reUnitRootOddFloat, fft2.imUnitRootOddFloat).run();
				break;
			}
			case SPLITRADIX: {
				new DFTSplitRadixRealFloat(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
				break;
			}
		}
		p += d1;
		k1++;
	}
	final FFTSetupDuoReal fft2 =
		FFTSetupDuoReal.transforms.get(new Integer(K2));
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			while (k1++ < K1) {
				new DFTBruteForceRealFloat(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
				p += d1;
			}
			break;
		}
		case COPRIMEFACTOR: {
			while (k1++ < K1) {
				new DFTCoprimeFactorRealFloat(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.ruritanian, fft2.chinese, fft2.K1).run();
				p += d1;
			}
			break;
		}
		case DUOREAL: {
			while (k1++ < K1) {
				new DFTDuoRealFloat(reBuffer, imBuffer,
					reData, imData, p, p + d1, d2, K2).run();
				p += 2 * d1;
				k1++;
			}
			break;
		}
		case EVENREAL: {
			while (k1++ < K1) {
				new DFTEvenRealFloat(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
				p += d1;
			}
			break;
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			while (k1++ < K1) {
				new DFTLength2RealFloat(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH3: {
			while (k1++ < K1) {
				new DFTLength3RealFloat(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH4: {
			while (k1++ < K1) {
				new DFTLength4RealFloat(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH5: {
			while (k1++ < K1) {
				new DFTLength5RealFloat(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH6: {
			while (k1++ < K1) {
				new DFTLength6RealFloat(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH8: {
			while (k1++ < K1) {
				new DFTLength8RealFloat(reBuffer, imBuffer, p, d2).run();
				p += d1;
			}
			break;
		}
		case MIXEDRADIX: {
			while (k1++ < K1) {
				new DFTMixedRadixRealFloat(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat,
					fft2.K1).run();
				p += d1;
			}
			break;
		}
		case PADDEDRADER: {
			while (k1++ < K1) {
				new DFTPaddedRaderRealFloat(reBuffer, imBuffer,
					p, d2, fft2.reConvolverFloat, fft2.imConvolverFloat,
					fft2.modular, fft2.inverseModular).run();
				p += d1;
			}
			break;
		}
		case RADER: {
			while (k1++ < K1) {
				new DFTRaderRealFloat(reBuffer, imBuffer, reData, imData,
					p, d2, fft2.reConvolverFloat, fft2.imConvolverFloat,
					fft2.modular, fft2.inverseModular).run();
				p += d1;
			}
			break;
		}
		case RADIX2: {
			while (k1++ < K1) {
				new DFTRadix2RealFloat(reBuffer, imBuffer,
					reData, imData, p, d2,
					fft2.reUnitRootEvenFloat, fft2.imUnitRootEvenFloat,
					fft2.reUnitRootOddFloat, fft2.imUnitRootOddFloat).run();
				p += d1;
			}
			break;
		}
		case SPLITRADIX: {
			while (k1++ < K1) {
				new DFTSplitRadixRealFloat(reBuffer, imBuffer, reData, imData,
					p, d2, fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
				p += d1;
			}
			break;
		}
	}
	p = startIndex;
	final FFTSetup fft1 = FFTSetup.transforms.get(new Integer(K1));
	switch (fft1.algorithm) {
		case BRUTEFORCE: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTBruteForceFloat(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootFloat, fft1.imUnitRootFloat).run();
				p += d2;
			}
			break;
		}
		case COPRIMEFACTOR: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTCoprimeFactorFloat(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.ruritanian, fft1.chinese, fft1.K1).run();
				p += d2;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength2Float(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH3: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength3Float(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH4: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength4Float(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH5: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength5Float(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH6: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength6Float(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH8: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength8Float(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case MIXEDRADIX: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTMixedRadixFloat(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootFloat, fft1.imUnitRootFloat,
					fft1.K1).run();
				p += d2;
			}
			break;
		}
		case PADDEDRADER: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTPaddedRaderFloat(reBuffer, imBuffer,
					p, d1, fft1.reConvolverFloat, fft1.imConvolverFloat,
					fft1.modular, fft1.inverseModular).run();
				p += d2;
			}
			break;
		}
		case RADER: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTRaderFloat(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reConvolverFloat, fft1.imConvolverFloat,
					fft1.modular, fft1.inverseModular).run();
				p += d2;
			}
			break;
		}
		case RADIX2: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTRadix2Float(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootFloat, fft1.imUnitRootFloat).run();
				p += d2;
			}
			break;
		}
		case SPLITRADIX: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTSplitRadixFloat(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootFloat, fft1.imUnitRootFloat).run();
				p += d2;
			}
			break;
		}
	}
	p = startIndex;
	for (int k = 0, K = (transformLength >> 1) + 1; (k < K); k++) {
		int q = chinese[k];
		if (q < 0) {
			q = startIndex - q * stride;
			reData[p] = reBuffer[q];
			imData[p] = -imBuffer[q];
		}
		else {
			q = startIndex + q * stride;
			reData[p] = reBuffer[q];
			imData[p] = imBuffer[q];
		}
		p += stride;
	}
} /* end run */

} /* end class DFTCoprimeFactorRealFloat */

/*====================================================================
|	DFTDuoReal
\===================================================================*/
static class DFTDuoReal

{ /* begin class DFTDuoReal */

/*....................................................................
	DFTDuoReal static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
	final int transformLength
) {
	if (FFTSetup.taboos.contains(new Integer(transformLength / 2))) {
		return(-1L);
	}
	final long K = (long)transformLength;
	final long k = K >> 1L;
	return(FFTSetup.FLALLOC * 0L
		+ FFTSetup.FLOP * (k * 8L)
		+ FFTSetup.FLASSIGN * (K * 2L + 4L + k * 4L)
		+ FFTSetup.INTALLOC * 6L
		+ FFTSetup.INTOP * (K * 4L + 9L + k * 5L)
		+ FFTSetup.INTASSIGN * (3L + K * 3L + 7L + k * 4L)
		+ FFTSetup.IDX * (K * 4L + 6L + k * 8L)
		+ FFTSetup.NEWOBJ * 1L
		+ FFTSetup.cost(transformLength)
	);
} /* end cost */

} /* end class DFTDuoReal */

/*====================================================================
|	DFTDuoRealDouble
\===================================================================*/
static class DFTDuoRealDouble
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTDuoRealDouble */

/*....................................................................
	DFTDuoRealDouble static variables
....................................................................*/
private double[] imBuffer;
private double[] reBuffer;
private int duoStartIndex;
private int transformLength;

/*....................................................................
	DFTDuoRealDouble constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTDuoRealDouble (
	final double[] reData,
	final double[] imData,
	final double[] reBuffer,
	final double[] imBuffer,
	final int startIndex,
	final int duoStartIndex,
	final int stride,
	final int transformLength
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.duoStartIndex = duoStartIndex;
	this.transformLength = transformLength;
} /* end DFTDuoRealDouble */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	int i0 = startIndex;
	int i1 = duoStartIndex;
	for (int m = 0; (m < transformLength); m++) {
		reBuffer[i0] = reData[i0];
		imBuffer[i0] = reData[i1];
		i0 += stride;
		i1 += stride;
	}
	final FFTSetup fft = FFTSetup.transforms.get(new Integer(transformLength));
	switch (fft.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceDouble(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorDouble(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.ruritanian, fft.chinese, fft.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			new DFTLength2Double(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case LENGTH3: {
			new DFTLength3Double(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case LENGTH4: {
			new DFTLength4Double(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case LENGTH5: {
			new DFTLength5Double(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case LENGTH6: {
			new DFTLength6Double(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8Double(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixDouble(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.reUnitRootDouble, fft.imUnitRootDouble,
				fft.K1).run();
			break;
		}
		case PADDEDRADER: {
			new DFTPaddedRaderDouble(reBuffer, imBuffer,
				startIndex, stride,
				fft.reConvolverDouble, fft.imConvolverDouble,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADER: {
			new DFTRaderDouble(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.reConvolverDouble, fft.imConvolverDouble,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADIX2: {
			new DFTRadix2Double(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixDouble(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
	}
	i0 = startIndex;
	i1 = duoStartIndex;
	reData[i0] = reBuffer[i0];
	imData[i0] = 0.0;
	reData[i1] = imBuffer[i0];
	imData[i1] = 0.0;
	i0 += stride;
	i1 += stride;
	int j0 = startIndex + (transformLength - 1) * stride;
	if (0 == (transformLength & 1)) {
		for (int m = 1, M = transformLength >> 1; (m < M); m++) {
			reData[i0] = 0.5 * (reBuffer[i0] + reBuffer[j0]);
			imData[i0] = 0.5 * (imBuffer[i0] - imBuffer[j0]);
			reData[i1] = 0.5 * (imBuffer[i0] + imBuffer[j0]);
			imData[i1] = -0.5 * (reBuffer[i0] - reBuffer[j0]);
			i0 += stride;
			i1 += stride;
			j0 -= stride;
		}
		reData[i0] = reBuffer[i0];
		imData[i0] = 0.0;
		reData[i1] = imBuffer[i0];
		imData[i1] = 0.0;
	}
	else {
		for (int m = 1, M = transformLength >> 1; (m <= M); m++) {
			reData[i0] = 0.5 * (reBuffer[i0] + reBuffer[j0]);
			imData[i0] = 0.5 * (imBuffer[i0] - imBuffer[j0]);
			reData[i1] = 0.5 * (imBuffer[i0] + imBuffer[j0]);
			imData[i1] = -0.5 * (reBuffer[i0] - reBuffer[j0]);
			i0 += stride;
			i1 += stride;
			j0 -= stride;
		}
	}
} /* end run */

} /* end class DFTDuoRealDouble */

/*====================================================================
|	DFTDuoRealFloat
\===================================================================*/
static class DFTDuoRealFloat
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTDuoRealFloat */

/*....................................................................
	DFTDuoRealFloat static variables
....................................................................*/
private float[] imBuffer;
private float[] reBuffer;
private int duoStartIndex;
private int transformLength;

/*....................................................................
	DFTDuoRealFloat constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTDuoRealFloat (
	final float[] reData,
	final float[] imData,
	final float[] reBuffer,
	final float[] imBuffer,
	final int startIndex,
	final int duoStartIndex,
	final int stride,
	final int transformLength
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.duoStartIndex = duoStartIndex;
	this.transformLength = transformLength;
} /* end DFTDuoRealFloat */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	int i0 = startIndex;
	int i1 = duoStartIndex;
	for (int m = 0; (m < transformLength); m++) {
		reBuffer[i0] = reData[i0];
		imBuffer[i0] = reData[i1];
		i0 += stride;
		i1 += stride;
	}
	final FFTSetup fft = FFTSetup.transforms.get(new Integer(transformLength));
	switch (fft.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceFloat(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorFloat(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.ruritanian, fft.chinese, fft.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			new DFTLength2Float(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case LENGTH3: {
			new DFTLength3Float(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case LENGTH4: {
			new DFTLength4Float(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case LENGTH5: {
			new DFTLength5Float(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case LENGTH6: {
			new DFTLength6Float(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8Float(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixFloat(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.reUnitRootFloat, fft.imUnitRootFloat,
				fft.K1).run();
			break;
		}
		case PADDEDRADER: {
			new DFTPaddedRaderFloat(reBuffer, imBuffer,
				startIndex, stride,
				fft.reConvolverFloat, fft.imConvolverFloat,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADER: {
			new DFTRaderFloat(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.reConvolverFloat, fft.imConvolverFloat,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADIX2: {
			new DFTRadix2Float(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixFloat(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
	}
	i0 = startIndex;
	i1 = duoStartIndex;
	reData[i0] = reBuffer[i0];
	imData[i0] = 0.0F;
	reData[i1] = imBuffer[i0];
	imData[i1] = 0.0F;
	i0 += stride;
	i1 += stride;
	int j0 = startIndex + (transformLength - 1) * stride;
	if (0 == (transformLength & 1)) {
		for (int m = 1, M = transformLength >> 1; (m < M); m++) {
			reData[i0] = 0.5F * (reBuffer[i0] + reBuffer[j0]);
			imData[i0] = 0.5F * (imBuffer[i0] - imBuffer[j0]);
			reData[i1] = 0.5F * (imBuffer[i0] + imBuffer[j0]);
			imData[i1] = -0.5F * (reBuffer[i0] - reBuffer[j0]);
			i0 += stride;
			i1 += stride;
			j0 -= stride;
		}
		reData[i0] = reBuffer[i0];
		imData[i0] = 0.0F;
		reData[i1] = imBuffer[i0];
		imData[i1] = 0.0F;
	}
	else {
		for (int m = 1, M = transformLength >> 1; (m <= M); m++) {
			reData[i0] = 0.5F * (reBuffer[i0] + reBuffer[j0]);
			imData[i0] = 0.5F * (imBuffer[i0] - imBuffer[j0]);
			reData[i1] = 0.5F * (imBuffer[i0] + imBuffer[j0]);
			imData[i1] = -0.5F * (reBuffer[i0] - reBuffer[j0]);
			i0 += stride;
			i1 += stride;
			j0 -= stride;
		}
	}
} /* end run */

} /* end class DFTDuoRealFloat */

/*====================================================================
|	DFTEvenReal
\===================================================================*/
static class DFTEvenReal

{ /* begin class DFTEvenReal */

/*....................................................................
	DFTEvenReal static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
	final int transformLength
) {
	if (FFTSetup.taboos.contains(new Integer(transformLength / 2))) {
		return(-1L);
	}
	final long k = (long)(transformLength >> 1);
	return(FFTSetup.FLALLOC * 4L
		+ FFTSetup.FLOP * (1L + (k - 1L) * 14L + 1L)
		+ FFTSetup.FLASSIGN * (k * 2L + 2L + (k - 1L) * 6L + 2L)
		+ FFTSetup.INTALLOC * 7L
		+ FFTSetup.INTOP * (1L + k * 5L + 5L + (k - 1L) * 4L)
		+ FFTSetup.INTASSIGN * (6L + k * 4L + 4L + (k - 1L) * 3L)
		+ FFTSetup.IDX * (k * 4L + 4L + (k - 1L) * 12L + 4L)
		+ FFTSetup.NEWOBJ * 1L
		+ FFTSetup.cost(transformLength >> 1)
	);
} /* end cost */

} /* end class DFTEvenReal */

/*====================================================================
|	DFTEvenRealDouble
\===================================================================*/
static class DFTEvenRealDouble
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTEvenRealDouble */

/*....................................................................
	DFTEvenRealDouble static variables
....................................................................*/
private double[] imBuffer;
private double[] imUnitRoot;
private double[] reBuffer;
private double[] reUnitRoot;

/*....................................................................
	DFTEvenRealDouble constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTEvenRealDouble (
	final double[] reData,
	final double[] imData,
	final double[] reBuffer,
	final double[] imBuffer,
	final int startIndex,
	final int stride,
	final double[] reUnitRoot,
	final double[] imUnitRoot
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.reUnitRoot = reUnitRoot;
	this.imUnitRoot = imUnitRoot;
} /* end DFTEvenRealDouble */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int halfTransformLength = reUnitRoot.length;
	final int doubleStride = stride << 1;
	int m0 = startIndex;
	int i0 = startIndex;
	int i1 = startIndex + stride;
	for (int m = 0; (m < halfTransformLength); m++) {
		reBuffer[m0] = reData[i0];
		imBuffer[m0] = reData[i1];
		m0 += stride;
		i0 += doubleStride;
		i1 += doubleStride;
	}
	final FFTSetup fft =
		FFTSetup.transforms.get(new Integer(halfTransformLength));
	switch (fft.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceDouble(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorDouble(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.ruritanian, fft.chinese, fft.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			new DFTLength2Double(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case LENGTH3: {
			new DFTLength3Double(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case LENGTH4: {
			new DFTLength4Double(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case LENGTH5: {
			new DFTLength5Double(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case LENGTH6: {
			new DFTLength6Double(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8Double(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixDouble(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.reUnitRootDouble, fft.imUnitRootDouble,
				fft.K1).run();
			break;
		}
		case PADDEDRADER: {
			new DFTPaddedRaderDouble(reBuffer, imBuffer,
				startIndex, stride,
				fft.reConvolverDouble, fft.imConvolverDouble,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADER: {
			new DFTRaderDouble(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.reConvolverDouble, fft.imConvolverDouble,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADIX2: {
			new DFTRadix2Double(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixDouble(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
	}
	i0 = startIndex;
	reData[i0] = reBuffer[i0] + imBuffer[i0];
	imData[i0] = 0.0;
	i0 += stride;
	i1 = startIndex + (halfTransformLength - 1) * stride;
	for (int m = 1; (m < halfTransformLength); m++) {
		final double re = reBuffer[i0] - reBuffer[i1];
		final double im = imBuffer[i0] + imBuffer[i1];
		final double reRoot = reUnitRoot[m];
		final double imRoot = imUnitRoot[m];
		reData[i0] = 0.5 * (reBuffer[i0] + reBuffer[i1]
			+ re * imRoot + im * reRoot);
		imData[i0] = 0.5 * (imBuffer[i0] - imBuffer[i1]
			- re * reRoot + im * imRoot);
		i0 += stride;
		i1 -= stride;
	}
	reData[i0] = reBuffer[startIndex] - imBuffer[startIndex];
	imData[i0] = 0.0;
} /* end run */

/*....................................................................
	DFTEvenRealDouble static methods
....................................................................*/
/*------------------------------------------------------------------*/
static double[] getImUnitRoot (
	final int transformLength
) {
	final int halfTransformLength = transformLength >> 1;
	final double[] imUnitRoot = new double[halfTransformLength];
	final double angularStep = -2.0 * PI / (double)transformLength;
	for (int k = 0; (k < halfTransformLength); k++) {
		imUnitRoot[k] = sin((double)k * angularStep);
	}
	return(imUnitRoot);
} /* end getImUnitRoot */

/*------------------------------------------------------------------*/
static double[] getReUnitRoot (
	final int transformLength
) {
	final int halfTransformLength = transformLength >> 1;
	final double[] reUnitRoot = new double[halfTransformLength];
	final double angularStep = -2.0 * PI / (double)transformLength;
	for (int k = 0; (k < halfTransformLength); k++) {
		reUnitRoot[k] = cos((double)k * angularStep);
	}
	return(reUnitRoot);
} /* end getReUnitRoot */

} /* end class DFTEvenRealDouble */

/*====================================================================
|	DFTEvenRealFloat
\===================================================================*/
static class DFTEvenRealFloat
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTEvenRealFloat */

/*....................................................................
	DFTEvenRealFloat static variables
....................................................................*/
private float[] imBuffer;
private float[] imUnitRoot;
private float[] reBuffer;
private float[] reUnitRoot;

/*....................................................................
	DFTEvenRealFloat constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTEvenRealFloat (
	final float[] reData,
	final float[] imData,
	final float[] reBuffer,
	final float[] imBuffer,
	final int startIndex,
	final int stride,
	final float[] reUnitRoot,
	final float[] imUnitRoot
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.reUnitRoot = reUnitRoot;
	this.imUnitRoot = imUnitRoot;
} /* end DFTEvenRealFloat */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int halfTransformLength = reUnitRoot.length;
	final int doubleStride = stride << 1;
	int m0 = startIndex;
	int i0 = startIndex;
	int i1 = startIndex + stride;
	for (int m = 0; (m < halfTransformLength); m++) {
		reBuffer[m0] = reData[i0];
		imBuffer[m0] = reData[i1];
		m0 += stride;
		i0 += doubleStride;
		i1 += doubleStride;
	}
	final FFTSetup fft =
		FFTSetup.transforms.get(new Integer(halfTransformLength));
	switch (fft.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceFloat(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorFloat(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.ruritanian, fft.chinese, fft.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			new DFTLength2Float(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case LENGTH3: {
			new DFTLength3Float(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case LENGTH4: {
			new DFTLength4Float(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case LENGTH5: {
			new DFTLength5Float(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case LENGTH6: {
			new DFTLength6Float(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8Float(reBuffer, imBuffer,
				startIndex, stride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixFloat(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.reUnitRootFloat, fft.imUnitRootFloat,
				fft.K1).run();
			break;
		}
		case PADDEDRADER: {
			new DFTPaddedRaderFloat(reBuffer, imBuffer,
				startIndex, stride,
				fft.reConvolverFloat, fft.imConvolverFloat,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADER: {
			new DFTRaderFloat(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.reConvolverFloat, fft.imConvolverFloat,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADIX2: {
			new DFTRadix2Float(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixFloat(reBuffer, imBuffer, reData, imData,
				startIndex, stride,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
	}
	i0 = startIndex;
	reData[i0] = reBuffer[i0] + imBuffer[i0];
	imData[i0] = 0.0F;
	i0 += stride;
	i1 = startIndex + (halfTransformLength - 1) * stride;
	for (int m = 1; (m < halfTransformLength); m++) {
		final float re = reBuffer[i0] - reBuffer[i1];
		final float im = imBuffer[i0] + imBuffer[i1];
		final float reRoot = reUnitRoot[m];
		final float imRoot = imUnitRoot[m];
		reData[i0] = 0.5F * (reBuffer[i0] + reBuffer[i1]
			+ re * imRoot + im * reRoot);
		imData[i0] = 0.5F * (imBuffer[i0] - imBuffer[i1]
			- re * reRoot + im * imRoot);
		i0 += stride;
		i1 -= stride;
	}
	reData[i0] = reBuffer[startIndex] - imBuffer[startIndex];
	imData[i0] = 0.0F;
} /* end run */

/*....................................................................
	DFTEvenRealFloat static methods
....................................................................*/
/*------------------------------------------------------------------*/
static float[] getImUnitRoot (
	final int transformLength
) {
	final int halfTransformLength = transformLength >> 1;
	final float[] imUnitRoot = new float[halfTransformLength];
	final float angularStep = -2.0F * (float)PI / (float)transformLength;
	for (int k = 0; (k < halfTransformLength); k++) {
		imUnitRoot[k] = (float)sin((double)((float)k * angularStep));
	}
	return(imUnitRoot);
} /* end getImUnitRoot */

/*------------------------------------------------------------------*/
static float[] getReUnitRoot (
	final int transformLength
) {
	final int halfTransformLength = transformLength >> 1;
	final float[] reUnitRoot = new float[halfTransformLength];
	final float angularStep = -2.0F * (float)PI / (float)transformLength;
	for (int k = 0; (k < halfTransformLength); k++) {
		reUnitRoot[k] = (float)cos((double)((float)k * angularStep));
	}
	return(reUnitRoot);
} /* end getReUnitRoot */

} /* end class DFTEvenRealFloat */

/*====================================================================
|	DFTLength2
\===================================================================*/
static class DFTLength2

{ /* begin class DFTLength2 */

/*....................................................................
	DFTLength2 static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
) {
	return(FFTSetup.FLALLOC * 3L
		+ FFTSetup.FLOP * 4L
		+ FFTSetup.FLASSIGN * 8L
		+ FFTSetup.INTALLOC * 1L
		+ FFTSetup.INTOP * 1L
		+ FFTSetup.INTASSIGN * 1L
		+ FFTSetup.IDX * 8L
		+ FFTSetup.NEWOBJ * 0L
	);
} /* end cost */

} /* end class DFTLength2 */

/*====================================================================
|	DFTLength2Double
\===================================================================*/
static class DFTLength2Double
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTLength2Double */

/*....................................................................
	DFTLength2Double constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength2Double (
	final double[] reData,
	final double[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength2Double */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int i1 = startIndex + stride;
	final double re1 = reData[i1];
	final double im1 = imData[i1];
	double butterfly = reData[startIndex] - re1;
	reData[startIndex] += re1;
	reData[i1] = butterfly;
	butterfly = imData[startIndex] - im1;
	imData[startIndex] += im1;
	imData[i1] = butterfly;
} /* end run */

} /* end class DFTLength2Double */

/*====================================================================
|	DFTLength2Float
\===================================================================*/
static class DFTLength2Float
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTLength2Float */

/*....................................................................
	DFTLength2Float constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength2Float (
	final float[] reData,
	final float[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength2Float */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int i1 = startIndex + stride;
	final float re1 = reData[i1];
	final float im1 = imData[i1];
	float butterfly = reData[startIndex] - re1;
	reData[startIndex] += re1;
	reData[i1] = butterfly;
	butterfly = imData[startIndex] - im1;
	imData[startIndex] += im1;
	imData[i1] = butterfly;
} /* end run */

} /* end class DFTLength2Float */

/*====================================================================
|	DFTLength2Real
\===================================================================*/
static class DFTLength2Real

{ /* begin class DFTLength2Real */

/*....................................................................
	DFTLength2Real static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
) {
	return(FFTSetup.FLALLOC * 2L
		+ FFTSetup.FLOP * 2L
		+ FFTSetup.FLASSIGN * 6L
		+ FFTSetup.INTALLOC * 1L
		+ FFTSetup.INTOP * 1L
		+ FFTSetup.INTASSIGN * 1L
		+ FFTSetup.IDX * 6L
		+ FFTSetup.NEWOBJ * 0L
	);
} /* end cost */

} /* end class DFTLength2Real */

/*====================================================================
|	DFTLength2RealDouble
\===================================================================*/
static class DFTLength2RealDouble
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTLength2RealDouble */

/*....................................................................
	DFTLength2RealDouble constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength2RealDouble (
	final double[] reData,
	final double[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength2RealDouble */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int i1 = startIndex + stride;
	final double re1 = reData[i1];
	double butterfly = reData[startIndex] - re1;
	reData[startIndex] += re1;
	reData[i1] = butterfly;
	imData[startIndex] = 0.0;
	imData[i1] = 0.0;
} /* end run */

} /* end class DFTLength2RealDouble */

/*====================================================================
|	DFTLength2RealFloat
\===================================================================*/
static class DFTLength2RealFloat
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTLength2RealFloat */

/*....................................................................
	DFTLength2RealFloat constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength2RealFloat (
	final float[] reData,
	final float[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength2RealFloat */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int i1 = startIndex + stride;
	final float re1 = reData[i1];
	float butterfly = reData[startIndex] - re1;
	reData[startIndex] += re1;
	reData[i1] = butterfly;
	imData[startIndex] = 0.0F;
	imData[i1] = 0.0F;
} /* end run */

} /* end class DFTLength2RealFloat */

/*====================================================================
|	DFTLength3
\===================================================================*/
static class DFTLength3

{ /* begin class DFTLength3 */

/*....................................................................
	DFTLength3 static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
) {
	return(FFTSetup.FLALLOC * 6L
		+ FFTSetup.FLOP * 20L
		+ FFTSetup.FLASSIGN * 16L
		+ FFTSetup.INTALLOC * 2L
		+ FFTSetup.INTOP * 2L
		+ FFTSetup.INTASSIGN * 2L
		+ FFTSetup.IDX * 12L
		+ FFTSetup.NEWOBJ * 0L
	);
} /* end cost */

} /* end class DFTLength3 */

/*====================================================================
|	DFTLength3Double
\===================================================================*/
static class DFTLength3Double
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTLength3Double */

/*....................................................................
	DFTLength3Double static variables
....................................................................*/
private static final double SQRT3 = sqrt(3.0);

/*....................................................................
	DFTLength3Double constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength3Double (
	final double[] reData,
	final double[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength3Double */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int i1 = startIndex + stride;
	final int i2 = i1 + stride;
	double reButterfly = reData[i1];
	final double imButterfly = imData[i1];
	double reDragonfly = reData[i2];
	final double imDragonfly = imData[i2];
	final double reLadybug = SQRT3 * (imDragonfly - imButterfly);
	final double imLadybug = SQRT3 * (reButterfly - reDragonfly);
	reButterfly += reDragonfly;
	reDragonfly = reData[startIndex];
	reData[i1] = reDragonfly - 0.5 * (reLadybug + reButterfly);
	reData[i2] = reDragonfly + 0.5 * (reLadybug - reButterfly);
	reData[startIndex] += reButterfly;
	reDragonfly = imData[startIndex];
	reButterfly = imButterfly + imDragonfly;
	imData[i1] = reDragonfly - 0.5 * (imLadybug + reButterfly);
	imData[i2] = reDragonfly + 0.5 * (imLadybug - reButterfly);
	imData[startIndex] += reButterfly;
} /* end run */

} /* end class DFTLength3Double */

/*====================================================================
|	DFTLength3Float
\===================================================================*/
static class DFTLength3Float
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTLength3Float */

/*....................................................................
	DFTLength3Float static variables
....................................................................*/
private static final float SQRT3 = (float)sqrt(3.0);

/*....................................................................
	DFTLength3Float constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength3Float (
	final float[] reData,
	final float[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength3Float */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int i1 = startIndex + stride;
	final int i2 = i1 + stride;
	float reButterfly = reData[i1];
	final float imButterfly = imData[i1];
	float reDragonfly = reData[i2];
	final float imDragonfly = imData[i2];
	final float reLadybug = SQRT3 * (imDragonfly - imButterfly);
	final float imLadybug = SQRT3 * (reButterfly - reDragonfly);
	reButterfly += reDragonfly;
	reDragonfly = reData[startIndex];
	reData[i1] = reDragonfly - 0.5F * (reLadybug + reButterfly);
	reData[i2] = reDragonfly + 0.5F * (reLadybug - reButterfly);
	reData[startIndex] += reButterfly;
	reDragonfly = imData[startIndex];
	reButterfly = imButterfly + imDragonfly;
	imData[i1] = reDragonfly - 0.5F * (imLadybug + reButterfly);
	imData[i2] = reDragonfly + 0.5F * (imLadybug - reButterfly);
	imData[startIndex] += reButterfly;
} /* end run */

} /* end class DFTLength3Float */

/*====================================================================
|	DFTLength3Real
\===================================================================*/
static class DFTLength3Real

{ /* begin class DFTLength3Real */

/*....................................................................
	DFTLength3Real static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
) {
	return(FFTSetup.FLALLOC * 2L
		+ FFTSetup.FLOP * 6L
		+ FFTSetup.FLASSIGN * 6L
		+ FFTSetup.INTALLOC * 2L
		+ FFTSetup.INTOP * 2L
		+ FFTSetup.INTASSIGN * 2L
		+ FFTSetup.IDX * 9L
		+ FFTSetup.NEWOBJ * 0L
	);
} /* end cost */

} /* end class DFTLength3Real */

/*====================================================================
|	DFTLength3RealDouble
\===================================================================*/
static class DFTLength3RealDouble
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTLength3RealDouble */

/*....................................................................
	DFTLength3RealDouble static variables
....................................................................*/
private static final double HALFSQRT3 = 0.5 * sqrt(3.0);

/*....................................................................
	DFTLength3RealDouble constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength3RealDouble (
	final double[] reData,
	final double[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength3RealDouble */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int i1 = startIndex + stride;
	final int i2 = i1 + stride;
	final double reButterfly = reData[i2] + reData[i1];
	final double imButterfly = HALFSQRT3 * (reData[i2] - reData[i1]);
	reData[i1] = reData[startIndex] - 0.5 * reButterfly;
	reData[startIndex] += reButterfly;
	imData[startIndex] = 0.0;
	imData[i1] = imButterfly;
} /* end run */

} /* end class DFTLength3RealDouble */

/*====================================================================
|	DFTLength3RealFloat
\===================================================================*/
static class DFTLength3RealFloat
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTLength3RealFloat */

/*....................................................................
	DFTLength3RealFloat static variables
....................................................................*/
private static final float HALFSQRT3 = 0.5F * (float)sqrt(3.0);

/*....................................................................
	DFTLength3RealFloat constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength3RealFloat (
	final float[] reData,
	final float[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength3RealFloat */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int i1 = startIndex + stride;
	final int i2 = i1 + stride;
	final float reButterfly = reData[i2] + reData[i1];
	final float imButterfly = HALFSQRT3 * (reData[i2] - reData[i1]);
	reData[i1] = reData[startIndex] - 0.5F * reButterfly;
	reData[startIndex] += reButterfly;
	imData[startIndex] = 0.0F;
	imData[i1] = imButterfly;
} /* end run */

} /* end class DFTLength3RealFloat */

/*====================================================================
|	DFTLength4
\===================================================================*/
static class DFTLength4

{ /* begin class DFTLength4 */

/*....................................................................
	DFTLength4 static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
) {
	return(FFTSetup.FLALLOC * 8L
		+ FFTSetup.FLOP * 16L
		+ FFTSetup.FLASSIGN * 24L
		+ FFTSetup.INTALLOC * 1L
		+ FFTSetup.INTOP * 11L
		+ FFTSetup.INTASSIGN * 10L
		+ FFTSetup.IDX * 16L
		+ FFTSetup.NEWOBJ * 0L
	);
} /* end cost */

} /* end class DFTLength4 */

/*====================================================================
|	DFTLength4Double
\===================================================================*/
static class DFTLength4Double
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTLength4Double */

/*....................................................................
	DFTLength4Double constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength4Double (
	final double[] reData,
	final double[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength4Double */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	double data0 = reData[startIndex];
	int k = startIndex + stride;
	double data1 = reData[k];
	k += stride;
	double data2 = reData[k];
	k += stride;
	double data3 = reData[k];
	double butterfly = data0 + data2;
	double dragonfly = data1 + data3;
	double ladybug = data0 - data2;
	double moth = data1 - data3;
	data3 = imData[k];
	k -= stride;
	data2 = imData[k];
	k -= stride;
	data1 = imData[k];
	k += stride;
	data0 = imData[startIndex];
	reData[startIndex] = butterfly + dragonfly;
	reData[k] = butterfly - dragonfly;
	k -= stride;
	butterfly = data0 - data2;
	dragonfly = data1 - data3;
	reData[k] = ladybug + dragonfly;
	k += stride + stride;
	reData[k] = ladybug - dragonfly;
	dragonfly = data0 + data2;
	ladybug = data1 + data3;
	imData[k] = butterfly + moth;
	k -= stride;
	imData[k] = dragonfly - ladybug;
	k -= stride;
	imData[k] = butterfly - moth;
	imData[startIndex] = dragonfly + ladybug;
} /* end run */

} /* end class DFTLength4Double */

/*====================================================================
|	DFTLength4Float
\===================================================================*/
static class DFTLength4Float
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTLength4Float */

/*....................................................................
	DFTLength4Float constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength4Float (
	final float[] reData,
	final float[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength4Float */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	float data0 = reData[startIndex];
	int k = startIndex + stride;
	float data1 = reData[k];
	k += stride;
	float data2 = reData[k];
	k += stride;
	float data3 = reData[k];
	float butterfly = data0 + data2;
	float dragonfly = data1 + data3;
	float ladybug = data0 - data2;
	float moth = data1 - data3;
	data3 = imData[k];
	k -= stride;
	data2 = imData[k];
	k -= stride;
	data1 = imData[k];
	k += stride;
	data0 = imData[startIndex];
	reData[startIndex] = butterfly + dragonfly;
	reData[k] = butterfly - dragonfly;
	k -= stride;
	butterfly = data0 - data2;
	dragonfly = data1 - data3;
	reData[k] = ladybug + dragonfly;
	k += stride + stride;
	reData[k] = ladybug - dragonfly;
	dragonfly = data0 + data2;
	ladybug = data1 + data3;
	imData[k] = butterfly + moth;
	k -= stride;
	imData[k] = dragonfly - ladybug;
	k -= stride;
	imData[k] = butterfly - moth;
	imData[startIndex] = dragonfly + ladybug;
} /* end run */

} /* end class DFTLength4Float */

/*====================================================================
|	DFTLength4Real
\===================================================================*/
static class DFTLength4Real

{ /* begin class DFTLength4Real */

/*....................................................................
	DFTLength4Real static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
) {
	return(FFTSetup.FLALLOC * 2L
		+ FFTSetup.FLOP * 6L
		+ FFTSetup.FLASSIGN * 8L
		+ FFTSetup.INTALLOC * 3L
		+ FFTSetup.INTOP * 3L
		+ FFTSetup.INTASSIGN * 3L
		+ FFTSetup.IDX * 14L
		+ FFTSetup.NEWOBJ * 0L
	);
} /* end cost */

} /* end class DFTLength4Real */

/*====================================================================
|	DFTLength4RealDouble
\===================================================================*/
static class DFTLength4RealDouble
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTLength4RealDouble */

/*....................................................................
	DFTLength4RealDouble constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength4RealDouble (
	final double[] reData,
	final double[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength4RealDouble */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int i1 = startIndex + stride;
	final int i2 = i1 + stride;
	final int i3 = i2 + stride;
	final double butterfly = reData[startIndex] + reData[i2];
	final double dragonfly = reData[i1] + reData[i3];
	imData[startIndex] = 0.0;
	imData[i1] = reData[i3] - reData[i1];
	imData[i2] = 0.0;
	reData[i1] = reData[startIndex] - reData[i2];
	reData[i2] = butterfly - dragonfly;
	reData[startIndex] = butterfly + dragonfly;
} /* end run */

} /* end class DFTLength4RealDouble */

/*====================================================================
|	DFTLength4RealFloat
\===================================================================*/
static class DFTLength4RealFloat
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTLength4RealFloat */

/*....................................................................
	DFTLength4RealFloat constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength4RealFloat (
	final float[] reData,
	final float[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength4RealFloat */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int i1 = startIndex + stride;
	final int i2 = i1 + stride;
	final int i3 = i2 + stride;
	final float butterfly = reData[startIndex] + reData[i2];
	final float dragonfly = reData[i1] + reData[i3];
	imData[startIndex] = 0.0F;
	imData[i1] = reData[i3] - reData[i1];
	imData[i2] = 0.0F;
	reData[i1] = reData[startIndex] - reData[i2];
	reData[i2] = butterfly - dragonfly;
	reData[startIndex] = butterfly + dragonfly;
} /* end run */

} /* end class DFTLength4RealFloat */

/*====================================================================
|	DFTLength5
\===================================================================*/
static class DFTLength5

{ /* begin class DFTLength5 */

/*....................................................................
	DFTLength5 static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
) {
	return(FFTSetup.FLALLOC * 18L
		+ FFTSetup.FLOP * 52L
		+ FFTSetup.FLASSIGN * 50L
		+ FFTSetup.INTALLOC * 1L
		+ FFTSetup.INTOP * 7L
		+ FFTSetup.INTASSIGN * 7L
		+ FFTSetup.IDX * 20L
		+ FFTSetup.NEWOBJ * 0L
	);
} /* end cost */

} /* end class DFTLength5 */

/*====================================================================
|	DFTLength5Double
\===================================================================*/
static class DFTLength5Double
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTLength5Double */

/*....................................................................
	DFTLength5Double static variables
....................................................................*/
private static final double IM5 = sqrt((5.0 - sqrt(5.0)) / 32.0);
private static final double RE5 = sqrt((5.0 + sqrt(5.0)) / 32.0);
private static final double S5 = sqrt(5.0 / 16.0);

/*....................................................................
	DFTLength5Double constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength5Double (
	final double[] reData,
	final double[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength5Double */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	int k = startIndex + stride;
	double re1 = reData[k];
	double im1 = imData[k];
	k += stride;
	double re2 = reData[k];
	double im2 = imData[k];
	k += stride;
	double re3 = reData[k];
	double im3 = imData[k];
	k += stride;
	double re4 = reData[k];
	double im4 = imData[k];
	double reDragonfly = re1 + re4;
	double imDragonfly = im1 + im4;
	double reLadybug = re1 - re4;
	double imLadybug = im1 - im4;
	double reMoth = re2 + re3;
	double imMoth = im2 + im3;
	double reBeetle = re2 - re3;
	double imBeetle = im2 - im3;
	final double reButterfly = reDragonfly + reMoth;
	final double imButterfly = imDragonfly + imMoth;
	re1 = imLadybug + reBeetle;
	im1 = reLadybug - imBeetle;
	re2 = IM5 * im1 - RE5 * re1;
	im2 = IM5 * re1 + RE5 * im1;
	re1 = -0.25 * reButterfly;
	im1 = -0.25 * imButterfly;
	re3 = reBeetle - imLadybug;
	im3 = imBeetle + reLadybug;
	re4 = RE5 * re3 - IM5 * im3;
	im4 = RE5 * im3 + IM5 * re3;
	re3 = S5 * (reDragonfly - reMoth);
	im3 = S5 * (imDragonfly - imMoth);
	reDragonfly = reData[startIndex] + re1;
	imDragonfly = imData[startIndex] + im1;
	reLadybug = reDragonfly - re3;
	imLadybug = imDragonfly - im3;
	reDragonfly += re3;
	imDragonfly += im3;
	reMoth = re2 + re4;
	imMoth = im2 + im4;
	reBeetle = re2 - re4;
	imBeetle = im2 - im4;
	reData[k] = reDragonfly + reMoth;
	imData[k] = imDragonfly + imMoth;
	k -= stride;
	reData[k] = reLadybug - imBeetle;
	imData[k] = imLadybug + reBeetle;
	k -= stride;
	reData[k] = reLadybug + imBeetle;
	imData[k] = imLadybug - reBeetle;
	k -= stride;
	reData[k] = reDragonfly - reMoth;
	imData[k] = imDragonfly - imMoth;
	reData[startIndex] += reButterfly;
	imData[startIndex] += imButterfly;
} /* end run */

} /* end class DFTLength5Double */

/*====================================================================
|	DFTLength5Float
\===================================================================*/
static class DFTLength5Float
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTLength5Float */

/*....................................................................
	DFTLength5Float static variables
....................................................................*/
private static final float IM5 = (float)sqrt((5.0 - sqrt(5.0)) / 32.0);
private static final float RE5 = (float)sqrt((5.0 + sqrt(5.0)) / 32.0);
private static final float S5 = (float)sqrt(5.0 / 16.0);

/*....................................................................
	DFTLength5Float constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength5Float (
	final float[] reData,
	final float[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength5Float */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	int k = startIndex + stride;
	float re1 = reData[k];
	float im1 = imData[k];
	k += stride;
	float re2 = reData[k];
	float im2 = imData[k];
	k += stride;
	float re3 = reData[k];
	float im3 = imData[k];
	k += stride;
	float re4 = reData[k];
	float im4 = imData[k];
	float reDragonfly = re1 + re4;
	float imDragonfly = im1 + im4;
	float reLadybug = re1 - re4;
	float imLadybug = im1 - im4;
	float reMoth = re2 + re3;
	float imMoth = im2 + im3;
	float reBeetle = re2 - re3;
	float imBeetle = im2 - im3;
	final float reButterfly = reDragonfly + reMoth;
	final float imButterfly = imDragonfly + imMoth;
	re1 = imLadybug + reBeetle;
	im1 = reLadybug - imBeetle;
	re2 = IM5 * im1 - RE5 * re1;
	im2 = IM5 * re1 + RE5 * im1;
	re1 = -0.25F * reButterfly;
	im1 = -0.25F * imButterfly;
	re3 = reBeetle - imLadybug;
	im3 = imBeetle + reLadybug;
	re4 = RE5 * re3 - IM5 * im3;
	im4 = RE5 * im3 + IM5 * re3;
	re3 = S5 * (reDragonfly - reMoth);
	im3 = S5 * (imDragonfly - imMoth);
	reDragonfly = reData[startIndex] + re1;
	imDragonfly = imData[startIndex] + im1;
	reLadybug = reDragonfly - re3;
	imLadybug = imDragonfly - im3;
	reDragonfly += re3;
	imDragonfly += im3;
	reMoth = re2 + re4;
	imMoth = im2 + im4;
	reBeetle = re2 - re4;
	imBeetle = im2 - im4;
	reData[k] = reDragonfly + reMoth;
	imData[k] = imDragonfly + imMoth;
	k -= stride;
	reData[k] = reLadybug - imBeetle;
	imData[k] = imLadybug + reBeetle;
	k -= stride;
	reData[k] = reLadybug + imBeetle;
	imData[k] = imLadybug - reBeetle;
	k -= stride;
	reData[k] = reDragonfly - reMoth;
	imData[k] = imDragonfly - imMoth;
	reData[startIndex] += reButterfly;
	imData[startIndex] += imButterfly;
} /* end run */

} /* end class DFTLength5Float */

/*====================================================================
|	DFTLength5Real
\===================================================================*/
static class DFTLength5Real

{ /* begin class DFTLength5Real */

/*....................................................................
	DFTLength5Real static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
) {
	return(FFTSetup.FLALLOC * 4L
		+ FFTSetup.FLOP * 18L
		+ FFTSetup.FLASSIGN * 13L
		+ FFTSetup.INTALLOC * 4L
		+ FFTSetup.INTOP * 4L
		+ FFTSetup.INTASSIGN * 4L
		+ FFTSetup.IDX * 15L
		+ FFTSetup.NEWOBJ * 0L
	);
} /* end cost */

} /* end class DFTLength5Real */

/*====================================================================
|	DFTLength5RealDouble
\===================================================================*/
static class DFTLength5RealDouble
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTLength5RealDouble */

/*....................................................................
	DFTLength5RealDouble static variables
....................................................................*/
private static final double IM5 = -sqrt((5.0 - sqrt(5.0)) / 8.0);
private static final double RE5 = -sqrt((5.0 + sqrt(5.0)) / 8.0);
private static final double S5 = sqrt(5.0 / 16.0);

/*....................................................................
	DFTLength5RealDouble constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength5RealDouble (
	final double[] reData,
	final double[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength5RealDouble */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int i1 = startIndex + stride;
	final int i2 = i1 + stride;
	final int i3 = i2 + stride;
	final int i4 = i3 + stride;
	double butterfly = reData[i1] - reData[i4];
	double dragonfly = reData[i2] - reData[i3];
	imData[startIndex] = 0.0;
	imData[i1] = RE5 * butterfly + IM5 * dragonfly;
	imData[i2] = IM5 * butterfly - RE5 * dragonfly;
	butterfly = reData[i1] + reData[i4];
	dragonfly = reData[i2] + reData[i3];
	final double ladybug = S5 * (butterfly - dragonfly);
	final double moth = butterfly + dragonfly;
	butterfly = reData[startIndex] - 0.25 * moth;
	reData[startIndex] += moth;
	reData[i1] = butterfly + ladybug;
	reData[i2] = butterfly - ladybug;
} /* end run */

} /* end class DFTLength5RealDouble */

/*====================================================================
|	DFTLength5RealFloat
\===================================================================*/
static class DFTLength5RealFloat
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTLength5RealFloat */

/*....................................................................
	DFTLength5RealFloat static variables
....................................................................*/
private static final float IM5 = -(float)sqrt((5.0 - sqrt(5.0)) / 8.0);
private static final float RE5 = -(float)sqrt((5.0 + sqrt(5.0)) / 8.0);
private static final float S5 = (float)sqrt(5.0 / 16.0);

/*....................................................................
	DFTLength5RealFloat constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength5RealFloat (
	final float[] reData,
	final float[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength5RealFloat */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int i1 = startIndex + stride;
	final int i2 = i1 + stride;
	final int i3 = i2 + stride;
	final int i4 = i3 + stride;
	float butterfly = reData[i1] - reData[i4];
	float dragonfly = reData[i2] - reData[i3];
	imData[startIndex] = 0.0F;
	imData[i1] = RE5 * butterfly + IM5 * dragonfly;
	imData[i2] = IM5 * butterfly - RE5 * dragonfly;
	butterfly = reData[i1] + reData[i4];
	dragonfly = reData[i2] + reData[i3];
	final float ladybug = S5 * (butterfly - dragonfly);
	final float moth = butterfly + dragonfly;
	butterfly = reData[startIndex] - 0.25F * moth;
	reData[startIndex] += moth;
	reData[i1] = butterfly + ladybug;
	reData[i2] = butterfly - ladybug;
} /* end run */

} /* end class DFTLength5RealFloat */

/*====================================================================
|	DFTLength6
\===================================================================*/
static class DFTLength6

{ /* begin class DFTLength6 */

/*....................................................................
	DFTLength6 static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
) {
	return(FFTSetup.FLALLOC * 24L
		+ FFTSetup.FLOP * 52L
		+ FFTSetup.FLASSIGN * 44L
		+ FFTSetup.INTALLOC * 1L
		+ FFTSetup.INTOP * 12L
		+ FFTSetup.INTASSIGN * 9L
		+ FFTSetup.IDX * 24L
		+ FFTSetup.NEWOBJ * 0L
	);
} /* end cost */

} /* end class DFTLength6 */

/*====================================================================
|	DFTLength6Double
\===================================================================*/
static class DFTLength6Double
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTLength6Double */

/*....................................................................
	DFTLength6Double static variables
....................................................................*/
private static final double SQRT3 = sqrt(3.0);

/*....................................................................
	DFTLength6Double constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength6Double (
	final double[] reData,
	final double[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength6Double */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	double reLadybug = reData[startIndex];
	int k = startIndex + 3 * stride;
	double reMoth = reData[k];
	final double reButterfly = reLadybug + reMoth;
	final double reDragonfly = reLadybug - reMoth;
	double imLadybug = imData[startIndex];
	double imMoth = imData[k];
	k -= stride << 1;
	final double imButterfly = imLadybug + imMoth;
	final double imDragonfly = imLadybug - imMoth;
	double re1 = reData[k];
	double im1 = imData[k];
	k += stride;
	double re2 = reData[k];
	double im2 = imData[k];
	k += stride << 1;
	double re4 = reData[k];
	double im4 = imData[k];
	k += stride;
	double re5 = reData[k];
	double im5 = imData[k];
	reLadybug = re1 + re2;
	imLadybug = im1 + im2;
	reMoth = re4 + re5;
	imMoth = im4 + im5;
	final double reAnt = reLadybug + reMoth;
	final double imAnt = imLadybug + imMoth;
	final double reBee = SQRT3 * (imMoth - imLadybug);
	final double imBee = SQRT3 * (reLadybug - reMoth);
	reLadybug = re1 - re2;
	imLadybug = im1 - im2;
	reMoth = re4 - re5;
	imMoth = im4 - im5;
	final double reGrasshopper = SQRT3 * (imMoth + imLadybug);
	final double imGrasshopper = SQRT3 * (reMoth + reLadybug);
	final double reBeetle = reMoth - reLadybug;
	final double imBeetle = imMoth - imLadybug;
	reData[k] = reDragonfly + 0.5 * (reBee - reBeetle);
	imData[k] = imDragonfly + 0.5 * (imBee - imBeetle);
	k -= stride;
	reData[k] = reButterfly - 0.5 * (reAnt + reGrasshopper);
	imData[k] = imButterfly - 0.5 * (imAnt - imGrasshopper);
	k -= stride;
	reData[k] = reDragonfly + reBeetle;
	imData[k] = imDragonfly + imBeetle;
	k -= stride;
	reData[k] = reButterfly + 0.5 * (reGrasshopper - reAnt);
	imData[k] = imButterfly - 0.5 * (imGrasshopper + imAnt);
	k -= stride;
	reData[k] = reDragonfly - 0.5 * (reBee + reBeetle);
	imData[k] = imDragonfly - 0.5 * (imBee + imBeetle);
	reData[startIndex] = reButterfly + reAnt;
	imData[startIndex] = imButterfly + imAnt;
} /* end run */

} /* end class DFTLength6Double */

/*====================================================================
|	DFTLength6Float
\===================================================================*/
static class DFTLength6Float
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTLength6Float */

/*....................................................................
	DFTLength6Float static variables
....................................................................*/
private static final float SQRT3 = (float)sqrt(3.0);

/*....................................................................
	DFTLength6Float constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength6Float (
	final float[] reData,
	final float[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength6Float */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	float reLadybug = reData[startIndex];
	int k = startIndex + 3 * stride;
	float reMoth = reData[k];
	final float reButterfly = reLadybug + reMoth;
	final float reDragonfly = reLadybug - reMoth;
	float imLadybug = imData[startIndex];
	float imMoth = imData[k];
	k -= stride << 1;
	final float imButterfly = imLadybug + imMoth;
	final float imDragonfly = imLadybug - imMoth;
	float re1 = reData[k];
	float im1 = imData[k];
	k += stride;
	float re2 = reData[k];
	float im2 = imData[k];
	k += stride << 1;
	float re4 = reData[k];
	float im4 = imData[k];
	k += stride;
	float re5 = reData[k];
	float im5 = imData[k];
	reLadybug = re1 + re2;
	imLadybug = im1 + im2;
	reMoth = re4 + re5;
	imMoth = im4 + im5;
	final float reAnt = reLadybug + reMoth;
	final float imAnt = imLadybug + imMoth;
	final float reBee = SQRT3 * (imMoth - imLadybug);
	final float imBee = SQRT3 * (reLadybug - reMoth);
	reLadybug = re1 - re2;
	imLadybug = im1 - im2;
	reMoth = re4 - re5;
	imMoth = im4 - im5;
	final float reGrasshopper = SQRT3 * (imMoth + imLadybug);
	final float imGrasshopper = SQRT3 * (reMoth + reLadybug);
	final float reBeetle = reMoth - reLadybug;
	final float imBeetle = imMoth - imLadybug;
	reData[k] = reDragonfly + 0.5F * (reBee - reBeetle);
	imData[k] = imDragonfly + 0.5F * (imBee - imBeetle);
	k -= stride;
	reData[k] = reButterfly - 0.5F * (reAnt + reGrasshopper);
	imData[k] = imButterfly - 0.5F * (imAnt - imGrasshopper);
	k -= stride;
	reData[k] = reDragonfly + reBeetle;
	imData[k] = imDragonfly + imBeetle;
	k -= stride;
	reData[k] = reButterfly + 0.5F * (reGrasshopper - reAnt);
	imData[k] = imButterfly - 0.5F * (imGrasshopper + imAnt);
	k -= stride;
	reData[k] = reDragonfly - 0.5F * (reBee + reBeetle);
	imData[k] = imDragonfly - 0.5F * (imBee + imBeetle);
	reData[startIndex] = reButterfly + reAnt;
	imData[startIndex] = imButterfly + imAnt;
} /* end run */

} /* end class DFTLength6Float */

/*====================================================================
|	DFTLength6Real
\===================================================================*/
static class DFTLength6Real

{ /* begin class DFTLength6Real */

/*....................................................................
	DFTLength6Real static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
) {
	return(FFTSetup.FLALLOC * 6L
		+ FFTSetup.FLOP * 18L
		+ FFTSetup.FLASSIGN * 16L
		+ FFTSetup.INTALLOC * 5L
		+ FFTSetup.INTOP * 5L
		+ FFTSetup.INTASSIGN * 5L
		+ FFTSetup.IDX * 20L
		+ FFTSetup.NEWOBJ * 0L
	);
} /* end cost */

} /* end class DFTLength6Real */

/*====================================================================
|	DFTLength6RealDouble
\===================================================================*/
static class DFTLength6RealDouble
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTLength6RealDouble */

/*....................................................................
	DFTLength6RealDouble static variables
....................................................................*/
private static final double NEGHALFSQRT3 = -0.5 * sqrt(3.0);

/*....................................................................
	DFTLength6RealDouble constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength6RealDouble (
	final double[] reData,
	final double[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength6RealDouble */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int i1 = startIndex + stride;
	final int i2 = i1 + stride;
	final int i3 = i2 + stride;
	final int i4 = i3 + stride;
	final int i5 = i4 + stride;
	double butterfly = reData[i1] + reData[i2];
	double dragonfly = reData[i1] - reData[i2];
	final double ladybug = reData[i4] + reData[i5];
	final double grasshopper = reData[i4] - reData[i5];
	imData[startIndex] = 0.0;
	imData[i1] = NEGHALFSQRT3 * (butterfly - ladybug);
	imData[i2] = NEGHALFSQRT3 * (dragonfly + grasshopper);
	imData[i3] = 0.0;
	final double beetle = butterfly + ladybug;
	final double ant = dragonfly - grasshopper;
	butterfly = reData[startIndex] + reData[i3];
	dragonfly = reData[startIndex] - reData[i3];
	reData[startIndex] = butterfly + beetle;
	reData[i1] = dragonfly + 0.5 * ant;
	reData[i2] = butterfly - 0.5 * beetle;
	reData[i3] = dragonfly - ant;
} /* end run */

} /* end class DFTLength6RealDouble */

/*====================================================================
|	DFTLength6RealFloat
\===================================================================*/
static class DFTLength6RealFloat
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTLength6RealFloat */

/*....................................................................
	DFTLength6RealFloat static variables
....................................................................*/
private static final float NEGHALFSQRT3 = -0.5F * (float)sqrt(3.0);

/*....................................................................
	DFTLength6RealFloat constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength6RealFloat (
	final float[] reData,
	final float[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength6RealFloat */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int i1 = startIndex + stride;
	final int i2 = i1 + stride;
	final int i3 = i2 + stride;
	final int i4 = i3 + stride;
	final int i5 = i4 + stride;
	float butterfly = reData[i1] + reData[i2];
	float dragonfly = reData[i1] - reData[i2];
	final float ladybug = reData[i4] + reData[i5];
	final float grasshopper = reData[i4] - reData[i5];
	imData[startIndex] = 0.0F;
	imData[i1] = NEGHALFSQRT3 * (butterfly - ladybug);
	imData[i2] = NEGHALFSQRT3 * (dragonfly + grasshopper);
	imData[i3] = 0.0F;
	final float beetle = butterfly + ladybug;
	final float ant = dragonfly - grasshopper;
	butterfly = reData[startIndex] + reData[i3];
	dragonfly = reData[startIndex] - reData[i3];
	reData[startIndex] = butterfly + beetle;
	reData[i1] = dragonfly + 0.5F * ant;
	reData[i2] = butterfly - 0.5F * beetle;
	reData[i3] = dragonfly - ant;
} /* end run */

} /* end class DFTLength6RealFloat */

/*====================================================================
|	DFTLength8
\===================================================================*/
static class DFTLength8

{ /* begin class DFTLength8 */

/*....................................................................
	DFTLength8 static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
) {
	return(FFTSetup.FLALLOC * 24L
		+ FFTSetup.FLOP * 56L
		+ FFTSetup.FLASSIGN * 68L
		+ FFTSetup.INTALLOC * 1L
		+ FFTSetup.INTOP * 15L
		+ FFTSetup.INTASSIGN * 14L
		+ FFTSetup.IDX * 32L
		+ FFTSetup.NEWOBJ * 0L
	);
} /* end cost */

} /* end class DFTLength8 */

/*====================================================================
|	DFTLength8Double
\===================================================================*/
static class DFTLength8Double
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTLength8Double */

/*....................................................................
	DFTLength8Double static variables
....................................................................*/
private static final double SQRTHALF = 1.0 / sqrt(2.0);

/*....................................................................
	DFTLength8Double constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength8Double (
	final double[] reData,
	final double[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength8Double */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	int k = startIndex;
	double reData0 = reData[k];
	double imData0 = imData[k];
	k += stride;
	double reData1 = reData[k];
	double imData1 = imData[k];
	k += stride;
	double reData2 = reData[k];
	double imData2 = imData[k];
	k += stride;
	double reData3 = reData[k];
	double imData3 = imData[k];
	k += stride;
	double reData4 = reData[k];
	double imData4 = imData[k];
	k += stride;
	double reData5 = reData[k];
	double imData5 = imData[k];
	k += stride;
	double reData6 = reData[k];
	double imData6 = imData[k];
	k += stride;
	double reData7 = reData[k];
	double imData7 = imData[k];
	double reButterfly = reData0 + reData4;
	double imButterfly = imData0 + imData4;
	double reDragonfly = reData2 + reData6;
	double imDragonfly = imData2 + imData6;
	final double re0 = reButterfly + reDragonfly;
	final double im0 = imButterfly + imDragonfly;
	final double re1 = reButterfly - reDragonfly;
	final double im1 = imButterfly - imDragonfly;
	reButterfly = reData0 - reData4;
	imButterfly = imData0 - imData4;
	reDragonfly = imData6 - imData2;
	imDragonfly = reData2 - reData6;
	reData0 = reButterfly + reDragonfly;
	imData0 = imButterfly + imDragonfly;
	reData2 = reButterfly - reDragonfly;
	imData2 = imButterfly - imDragonfly;
	reButterfly = reData1 + reData5;
	imButterfly = imData1 + imData5;
	reDragonfly = reData3 + reData7;
	imDragonfly = imData3 + imData7;
	reData4 = reButterfly + reDragonfly;
	imData4 = imButterfly + imDragonfly;
	reData6 = imDragonfly - imButterfly;
	imData6 = reButterfly - reDragonfly;
	reData5 -= reData1;
	imData5 -= imData1;
	reButterfly = SQRTHALF * (reData5 + imData5);
	imButterfly = SQRTHALF * (reData5 - imData5);
	reData7 -= reData3;
	imData7 -= imData3;
	reDragonfly = SQRTHALF * (imData7 - reData7);
	imDragonfly = SQRTHALF * (imData7 + reData7);
	reData1 = imButterfly - imDragonfly;
	imData1 = reDragonfly - reButterfly;
	reData3 = reDragonfly + reButterfly;
	imData3 = imButterfly + imDragonfly;
	reData[k] = reData0 - reData1;
	imData[k] = imData0 + imData1;
	k -= stride;
	reData[k] = re1 + reData6;
	imData[k] = im1 + imData6;
	k -= stride;
	reData[k] = reData2 + reData3;
	imData[k] = imData2 - imData3;
	k -= stride;
	reData[k] = re0 - reData4;
	imData[k] = im0 - imData4;
	k -= stride;
	reData[k] = reData0 + reData1;
	imData[k] = imData0 - imData1;
	k -= stride;
	reData[k] = re1 - reData6;
	imData[k] = im1 - imData6;
	k -= stride;
	reData[k] = reData2 - reData3;
	imData[k] = imData2 + imData3;
	k -= stride;
	reData[k] = re0 + reData4;
	imData[k] = im0 + imData4;
} /* end run */

} /* end class DFTLength8Double */

/*====================================================================
|	DFTLength8Float
\===================================================================*/
static class DFTLength8Float
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTLength8Float */

/*....................................................................
	DFTLength8Float static variables
....................................................................*/
private static final float SQRTHALF = 1.0F / (float)sqrt(2.0);

/*....................................................................
	DFTLength8Float constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength8Float (
	final float[] reData,
	final float[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength8Float */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	int k = startIndex;
	float reData0 = reData[k];
	float imData0 = imData[k];
	k += stride;
	float reData1 = reData[k];
	float imData1 = imData[k];
	k += stride;
	float reData2 = reData[k];
	float imData2 = imData[k];
	k += stride;
	float reData3 = reData[k];
	float imData3 = imData[k];
	k += stride;
	float reData4 = reData[k];
	float imData4 = imData[k];
	k += stride;
	float reData5 = reData[k];
	float imData5 = imData[k];
	k += stride;
	float reData6 = reData[k];
	float imData6 = imData[k];
	k += stride;
	float reData7 = reData[k];
	float imData7 = imData[k];
	float reButterfly = reData0 + reData4;
	float imButterfly = imData0 + imData4;
	float reDragonfly = reData2 + reData6;
	float imDragonfly = imData2 + imData6;
	final float re0 = reButterfly + reDragonfly;
	final float im0 = imButterfly + imDragonfly;
	final float re1 = reButterfly - reDragonfly;
	final float im1 = imButterfly - imDragonfly;
	reButterfly = reData0 - reData4;
	imButterfly = imData0 - imData4;
	reDragonfly = imData6 - imData2;
	imDragonfly = reData2 - reData6;
	reData0 = reButterfly + reDragonfly;
	imData0 = imButterfly + imDragonfly;
	reData2 = reButterfly - reDragonfly;
	imData2 = imButterfly - imDragonfly;
	reButterfly = reData1 + reData5;
	imButterfly = imData1 + imData5;
	reDragonfly = reData3 + reData7;
	imDragonfly = imData3 + imData7;
	reData4 = reButterfly + reDragonfly;
	imData4 = imButterfly + imDragonfly;
	reData6 = imDragonfly - imButterfly;
	imData6 = reButterfly - reDragonfly;
	reData5 -= reData1;
	imData5 -= imData1;
	reButterfly = SQRTHALF * (reData5 + imData5);
	imButterfly = SQRTHALF * (reData5 - imData5);
	reData7 -= reData3;
	imData7 -= imData3;
	reDragonfly = SQRTHALF * (imData7 - reData7);
	imDragonfly = SQRTHALF * (imData7 + reData7);
	reData1 = imButterfly - imDragonfly;
	imData1 = reDragonfly - reButterfly;
	reData3 = reDragonfly + reButterfly;
	imData3 = imButterfly + imDragonfly;
	reData[k] = reData0 - reData1;
	imData[k] = imData0 + imData1;
	k -= stride;
	reData[k] = re1 + reData6;
	imData[k] = im1 + imData6;
	k -= stride;
	reData[k] = reData2 + reData3;
	imData[k] = imData2 - imData3;
	k -= stride;
	reData[k] = re0 - reData4;
	imData[k] = im0 - imData4;
	k -= stride;
	reData[k] = reData0 + reData1;
	imData[k] = imData0 - imData1;
	k -= stride;
	reData[k] = re1 - reData6;
	imData[k] = im1 - imData6;
	k -= stride;
	reData[k] = reData2 - reData3;
	imData[k] = imData2 + imData3;
	k -= stride;
	reData[k] = re0 + reData4;
	imData[k] = im0 + imData4;
} /* end run */

} /* end class DFTLength8Float */

/*====================================================================
|	DFTLength8Real
\===================================================================*/
static class DFTLength8Real

{ /* begin class DFTLength8Real */

/*....................................................................
	DFTLength8Real static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
) {
	return(FFTSetup.FLALLOC * 8L
		+ FFTSetup.FLOP * 22L
		+ FFTSetup.FLASSIGN * 26L
		+ FFTSetup.INTALLOC * 7L
		+ FFTSetup.INTOP * 7L
		+ FFTSetup.INTASSIGN * 7L
		+ FFTSetup.IDX * 22L
		+ FFTSetup.NEWOBJ * 0L
	);
} /* end cost */

} /* end class DFTLength8Real */

/*====================================================================
|	DFTLength8RealDouble
\===================================================================*/
static class DFTLength8RealDouble
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTLength8RealDouble */

/*....................................................................
	DFTLength8RealDouble static variables
....................................................................*/
private static final double NEGSQRTHALF = -1.0 / sqrt(2.0);

/*....................................................................
	DFTLength8RealDouble constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength8RealDouble (
	final double[] reData,
	final double[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength8RealDouble */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int i1 = startIndex + stride;
	final int i2 = i1 + stride;
	final int i3 = i2 + stride;
	final int i4 = i3 + stride;
	final int i5 = i4 + stride;
	final int i6 = i5 + stride;
	final int i7 = i6 + stride;
	double re0 = reData[startIndex];
	double re2 = reData[i2];
	final double re4 = reData[i4];
	final double re6 = reData[i6];
	double butterfly = re0 + re4;
	double dragonfly = re2 + re6;
	double ladybug = butterfly + dragonfly;
	reData[i2] = butterfly - dragonfly;
	butterfly = reData[i1] + reData[i5];
	dragonfly = reData[i3] + reData[i7];
	double moth = dragonfly + butterfly;
	imData[i2] = dragonfly - butterfly;
	reData[startIndex] = ladybug + moth;
	imData[startIndex] = 0.0;
	reData[i4] = ladybug - moth;
	imData[i4] = 0.0;
	butterfly = re0 - re4;
	dragonfly = re2 - re6;
	re0 = reData[i1] - reData[i5];
	re2 = reData[i3] - reData[i7];
	ladybug = NEGSQRTHALF * (re0 - re2);
	moth = NEGSQRTHALF * (re0 + re2);
	reData[i1] = butterfly - ladybug;
	imData[i1] = moth - dragonfly;
	reData[i3] = butterfly + ladybug;
	imData[i3] = moth + dragonfly;
} /* end run */

} /* end class DFTLength8RealDouble */

/*====================================================================
|	DFTLength8RealFloat
\===================================================================*/
static class DFTLength8RealFloat
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTLength8RealFloat */

/*....................................................................
	DFTLength8RealFloat static variables
....................................................................*/
private static final float NEGSQRTHALF = -1.0F / (float)sqrt(2.0);

/*....................................................................
	DFTLength8RealFloat constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTLength8RealFloat (
	final float[] reData,
	final float[] imData,
	final int startIndex,
	final int stride
) {
	super(reData, imData, startIndex, stride);
} /* end DFTLength8RealFloat */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int i1 = startIndex + stride;
	final int i2 = i1 + stride;
	final int i3 = i2 + stride;
	final int i4 = i3 + stride;
	final int i5 = i4 + stride;
	final int i6 = i5 + stride;
	final int i7 = i6 + stride;
	float re0 = reData[startIndex];
	float re2 = reData[i2];
	final float re4 = reData[i4];
	final float re6 = reData[i6];
	float butterfly = re0 + re4;
	float dragonfly = re2 + re6;
	float ladybug = butterfly + dragonfly;
	reData[i2] = butterfly - dragonfly;
	butterfly = reData[i1] + reData[i5];
	dragonfly = reData[i3] + reData[i7];
	float moth = dragonfly + butterfly;
	imData[i2] = dragonfly - butterfly;
	reData[startIndex] = ladybug + moth;
	imData[startIndex] = 0.0F;
	reData[i4] = ladybug - moth;
	imData[i4] = 0.0F;
	butterfly = re0 - re4;
	dragonfly = re2 - re6;
	re0 = reData[i1] - reData[i5];
	re2 = reData[i3] - reData[i7];
	ladybug = NEGSQRTHALF * (re0 - re2);
	moth = NEGSQRTHALF * (re0 + re2);
	reData[i1] = butterfly - ladybug;
	imData[i1] = moth - dragonfly;
	reData[i3] = butterfly + ladybug;
	imData[i3] = moth + dragonfly;
} /* end run */

} /* end class DFTLength8RealFloat */

/*====================================================================
|	DFTMixedRadix
\===================================================================*/
static class DFTMixedRadix

{ /* begin class DFTMixedRadix */

/*....................................................................
	DFTMixedRadix static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
	final int K1,
	final int K2
) {
	if (FFTSetup.taboos.contains(new Integer(K1))
		|| FFTSetup.taboos.contains(new Integer(K2))) {
		return(-1L);
	}
	final long K = (long)K1 * (long)K2;
	return(FFTSetup.FLALLOC * 4L
		+ FFTSetup.FLOP * ((long)K2 * ((long)K1 * 6L))
		+ FFTSetup.FLASSIGN * ((long)K2 * ((long)K1 * 6L)
			+ (long)K2 * ((long)K1 * 2L) + K * 2L)
		+ FFTSetup.INTALLOC * 16L
		+ FFTSetup.INTOP * (3L + (long)K2 * 3L + (long)K2 * (3L
			+ (long)K1 * 7L) + 1L + (long)K1 * 3L + (long)K2 * (3L
			+ (long)K1 * 4L) + K * 3L)
		+ FFTSetup.INTASSIGN * (6L + (long)K2 * 2L + 2L + (long)K2 * (3L
			+ (long)K1 * 4L) + 2L + (long)K1 * 2L + 3L + (long)K2 * (4L
			+ (long)K1 * 3L) + 2L + K * 2L)
		+ FFTSetup.IDX * ((long)K2 * ((long)K1 * 6L)
			+ (long)K2 * ((long)K1 * 4L) + K * 4L)
		+ FFTSetup.NEWOBJ * ((long)K2 * 1L + (long)K1 * 1L)
		+ (long)K2 * FFTSetup.cost(K1) + (long)K1 * FFTSetup.cost(K2)
	);
} /* end cost */

} /* end class DFTMixedRadix */

/*====================================================================
|	DFTMixedRadixDouble
\===================================================================*/
static class DFTMixedRadixDouble
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTMixedRadixDouble */

/*....................................................................
	DFTMixedRadixDouble private variables
....................................................................*/
private double[] imBuffer;
private double[] imUnitRoot;
private double[] reBuffer;
private double[] reUnitRoot;
private int K1;

/*....................................................................
	DFTMixedRadixDouble constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTMixedRadixDouble (
	final double[] reData,
	final double[] imData,
	final double[] reBuffer,
	final double[] imBuffer,
	final int startIndex,
	final int stride,
	final double[] reUnitRoot,
	final double[] imUnitRoot,
	final int K1
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.reUnitRoot = reUnitRoot;
	this.imUnitRoot = imUnitRoot;
	this.K1 = K1;
} /* end DFTMixedRadixDouble */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int transformLength = reUnitRoot.length;
	final int K2 = transformLength / K1;
	final int d1 = stride;
	final int d2 = K2 * d1;
	int k2 = startIndex;
	final FFTSetup fft1 = FFTSetup.transforms.get(new Integer(K1));
	switch (fft1.algorithm) {
		case BRUTEFORCE: {
			for (int n = 0; (n < K2); n++) {
				new DFTBruteForceDouble(reData, imData, reBuffer, imBuffer,
					k2, d2, fft1.reUnitRootDouble, fft1.imUnitRootDouble).run();
				k2 += d1;
			}
			break;
		}
		case COPRIMEFACTOR: {
			for (int n = 0; (n < K2); n++) {
				new DFTCoprimeFactorDouble(reData, imData, reBuffer, imBuffer,
					k2, d2, fft1.ruritanian, fft1.chinese, fft1.K1).run();
				k2 += d1;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength2Double(reData, imData, k2, d2).run();
				k2 += d1;
			}
			break;
		}
		case LENGTH3: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength3Double(reData, imData, k2, d2).run();
				k2 += d1;
			}
			break;
		}
		case LENGTH4: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength4Double(reData, imData, k2, d2).run();
				k2 += d1;
			}
			break;
		}
		case LENGTH5: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength5Double(reData, imData, k2, d2).run();
				k2 += d1;
			}
			break;
		}
		case LENGTH6: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength6Double(reData, imData, k2, d2).run();
				k2 += d1;
			}
			break;
		}
		case LENGTH8: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength8Double(reData, imData, k2, d2).run();
				k2 += d1;
			}
			break;
		}
		case MIXEDRADIX: {
			for (int n = 0; (n < K2); n++) {
				new DFTMixedRadixDouble(reData, imData, reBuffer, imBuffer,
					k2, d2, fft1.reUnitRootDouble, fft1.imUnitRootDouble,
					fft1.K1).run();
				k2 += d1;
			}
			break;
		}
		case PADDEDRADER: {
			for (int n = 0; (n < K2); n++) {
				new DFTPaddedRaderDouble(reData, imData,
					k2, d2, fft1.reConvolverDouble, fft1.imConvolverDouble,
					fft1.modular, fft1.inverseModular).run();
				k2 += d1;
			}
			break;
		}
		case RADER: {
			for (int n = 0; (n < K2); n++) {
				new DFTRaderDouble(reData, imData, reBuffer, imBuffer,
					k2, d2, fft1.reConvolverDouble, fft1.imConvolverDouble,
					fft1.modular, fft1.inverseModular).run();
				k2 += d1;
			}
			break;
		}
		case RADIX2: {
			for (int n = 0; (n < K2); n++) {
				new DFTRadix2Double(reData, imData, reBuffer, imBuffer,
					k2, d2, fft1.reUnitRootDouble, fft1.imUnitRootDouble).run();
				k2 += d1;
			}
			break;
		}
		case SPLITRADIX: {
			for (int n = 0; (n < K2); n++) {
				new DFTSplitRadixDouble(reData, imData, reBuffer, imBuffer,
					k2, d2, fft1.reUnitRootDouble, fft1.imUnitRootDouble).run();
				k2 += d1;
			}
			break;
		}
	}
	k2 = startIndex;
	for (int n = 0; (n < K2); n++) {
		int m2 = 0;
		int m1 = k2;
		k2 += d1;
		for (int m = 0; (m < K1); m++) {
			final double re = reData[m1];
			final double im = imData[m1];
			final double reRoot = reUnitRoot[m2];
			final double imRoot = imUnitRoot[m2];
			reData[m1] = re * reRoot - im * imRoot;
			imData[m1] = re * imRoot + im * reRoot;
			m1 += d2;
			m2 += n;
			m2 -= transformLength * (m2 / transformLength);
		}
	}
	int k1 = startIndex;
	final FFTSetup fft2 = FFTSetup.transforms.get(new Integer(K2));
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			for (int n = 0; (n < K1); n++) {
				new DFTBruteForceDouble(reData, imData, reBuffer, imBuffer,
					k1, d1, fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
				k1 += d2;
			}
			break;
		}
		case COPRIMEFACTOR: {
			for (int n = 0; (n < K1); n++) {
				new DFTCoprimeFactorDouble(reData, imData, reBuffer, imBuffer,
					k1, d1, fft2.ruritanian, fft2.chinese, fft2.K1).run();
				k1 += d2;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength2Double(reData, imData, k1, d1).run();
				k1 += d2;
			}
			break;
		}
		case LENGTH3: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength3Double(reData, imData, k1, d1).run();
				k1 += d2;
			}
			break;
		}
		case LENGTH4: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength4Double(reData, imData, k1, d1).run();
				k1 += d2;
			}
			break;
		}
		case LENGTH5: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength5Double(reData, imData, k1, d1).run();
				k1 += d2;
			}
			break;
		}
		case LENGTH6: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength6Double(reData, imData, k1, d1).run();
				k1 += d2;
			}
			break;
		}
		case LENGTH8: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength8Double(reData, imData, k1, d1).run();
				k1 += d2;
			}
			break;
		}
		case MIXEDRADIX: {
			for (int n = 0; (n < K1); n++) {
				new DFTMixedRadixDouble(reData, imData, reBuffer, imBuffer,
					k1, d1, fft2.reUnitRootDouble, fft2.imUnitRootDouble,
					fft2.K1).run();
				k1 += d2;
			}
			break;
		}
		case PADDEDRADER: {
			for (int n = 0; (n < K1); n++) {
				new DFTPaddedRaderDouble(reData, imData,
					k1, d1, fft2.reConvolverDouble, fft2.imConvolverDouble,
					fft2.modular, fft2.inverseModular).run();
				k1 += d2;
			}
			break;
		}
		case RADER: {
			for (int n = 0; (n < K1); n++) {
				new DFTRaderDouble(reData, imData, reBuffer, imBuffer,
					k1, d1, fft2.reConvolverDouble, fft2.imConvolverDouble,
					fft2.modular, fft2.inverseModular).run();
				k1 += d2;
			}
			break;
		}
		case RADIX2: {
			for (int n = 0; (n < K1); n++) {
				new DFTRadix2Double(reData, imData, reBuffer, imBuffer,
					k1, d1, fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
				k1 += d2;
			}
			break;
		}
		case SPLITRADIX: {
			for (int n = 0; (n < K1); n++) {
				new DFTSplitRadixDouble(reData, imData, reBuffer, imBuffer,
					k1, d1, fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
				k1 += d2;
			}
			break;
		}
	}
	k1 = startIndex;
	k2 = startIndex;
	for (int m2 = 0; (m2 < K2); m2++) {
		int n2 = k1;
		for (int m1 = 0; (m1 < K1); m1++) {
			reBuffer[k2] = reData[n2];
			imBuffer[k2] = imData[n2];
			k2 += d1;
			n2 += d2;
		}
		k1 += d1;
	}
	k1 = startIndex;
	for (int m = 0; (m < transformLength); m++) {
		reData[k1] = reBuffer[k1];
		imData[k1] = imBuffer[k1];
		k1 += d1;
	}
} /* end run */

/*....................................................................
	DFTMixedRadixDouble static methods
....................................................................*/
/*------------------------------------------------------------------*/
static double[] getImUnitRoot (
	final int transformLength
) {
	final double[] imUnitRoot = new double[transformLength];
	final double angularStep = -2.0 * PI / (double)transformLength;
	for (int k = 0; (k < transformLength); k++) {
		imUnitRoot[k] = sin((double)k * angularStep);
	}
	return(imUnitRoot);
} /* end getImUnitRoot */

/*------------------------------------------------------------------*/
static double[] getReUnitRoot (
	final int transformLength
) {
	final double[] reUnitRoot = new double[transformLength];
	final double angularStep = -2.0 * PI / (double)transformLength;
	for (int k = 0; (k < transformLength); k++) {
		reUnitRoot[k] = cos((double)k * angularStep);
	}
	return(reUnitRoot);
} /* end getReUnitRoot */

} /* end class DFTMixedRadixDouble */

/*====================================================================
|	DFTMixedRadixFloat
\===================================================================*/
static class DFTMixedRadixFloat
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTMixedRadixFloat */

/*....................................................................
	DFTMixedRadixFloat private variables
....................................................................*/
private float[] imBuffer;
private float[] imUnitRoot;
private float[] reBuffer;
private float[] reUnitRoot;
private int K1;

/*....................................................................
	DFTMixedRadixFloat constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTMixedRadixFloat (
	final float[] reData,
	final float[] imData,
	final float[] reBuffer,
	final float[] imBuffer,
	final int startIndex,
	final int stride,
	final float[] reUnitRoot,
	final float[] imUnitRoot,
	final int K1
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.reUnitRoot = reUnitRoot;
	this.imUnitRoot = imUnitRoot;
	this.K1 = K1;
} /* end DFTMixedRadixFloat */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int transformLength = reUnitRoot.length;
	final int K2 = transformLength / K1;
	final int d1 = stride;
	final int d2 = K2 * d1;
	int k2 = startIndex;
	final FFTSetup fft1 = FFTSetup.transforms.get(new Integer(K1));
	switch (fft1.algorithm) {
		case BRUTEFORCE: {
			for (int n = 0; (n < K2); n++) {
				new DFTBruteForceFloat(reData, imData, reBuffer, imBuffer,
					k2, d2, fft1.reUnitRootFloat, fft1.imUnitRootFloat).run();
				k2 += d1;
			}
			break;
		}
		case COPRIMEFACTOR: {
			for (int n = 0; (n < K2); n++) {
				new DFTCoprimeFactorFloat(reData, imData, reBuffer, imBuffer,
					k2, d2, fft1.ruritanian, fft1.chinese, fft1.K1).run();
				k2 += d1;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength2Float(reData, imData, k2, d2).run();
				k2 += d1;
			}
			break;
		}
		case LENGTH3: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength3Float(reData, imData, k2, d2).run();
				k2 += d1;
			}
			break;
		}
		case LENGTH4: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength4Float(reData, imData, k2, d2).run();
				k2 += d1;
			}
			break;
		}
		case LENGTH5: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength5Float(reData, imData, k2, d2).run();
				k2 += d1;
			}
			break;
		}
		case LENGTH6: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength6Float(reData, imData, k2, d2).run();
				k2 += d1;
			}
			break;
		}
		case LENGTH8: {
			for (int n = 0; (n < K2); n++) {
				new DFTLength8Float(reData, imData, k2, d2).run();
				k2 += d1;
			}
			break;
		}
		case MIXEDRADIX: {
			for (int n = 0; (n < K2); n++) {
				new DFTMixedRadixFloat(reData, imData, reBuffer, imBuffer,
					k2, d2, fft1.reUnitRootFloat, fft1.imUnitRootFloat,
					fft1.K1).run();
				k2 += d1;
			}
			break;
		}
		case PADDEDRADER: {
			for (int n = 0; (n < K2); n++) {
				new DFTPaddedRaderFloat(reData, imData,
					k2, d2, fft1.reConvolverFloat, fft1.imConvolverFloat,
					fft1.modular, fft1.inverseModular).run();
				k2 += d1;
			}
			break;
		}
		case RADER: {
			for (int n = 0; (n < K2); n++) {
				new DFTRaderFloat(reData, imData, reBuffer, imBuffer,
					k2, d2, fft1.reConvolverFloat, fft1.imConvolverFloat,
					fft1.modular, fft1.inverseModular).run();
				k2 += d1;
			}
			break;
		}
		case RADIX2: {
			for (int n = 0; (n < K2); n++) {
				new DFTRadix2Float(reData, imData, reBuffer, imBuffer,
					k2, d2, fft1.reUnitRootFloat, fft1.imUnitRootFloat).run();
				k2 += d1;
			}
			break;
		}
		case SPLITRADIX: {
			for (int n = 0; (n < K2); n++) {
				new DFTSplitRadixFloat(reData, imData, reBuffer, imBuffer,
					k2, d2, fft1.reUnitRootFloat, fft1.imUnitRootFloat).run();
				k2 += d1;
			}
			break;
		}
	}
	k2 = startIndex;
	for (int n = 0; (n < K2); n++) {
		int m2 = 0;
		int m1 = k2;
		k2 += d1;
		for (int m = 0; (m < K1); m++) {
			final float re = reData[m1];
			final float im = imData[m1];
			final float reRoot = reUnitRoot[m2];
			final float imRoot = imUnitRoot[m2];
			reData[m1] = re * reRoot - im * imRoot;
			imData[m1] = re * imRoot + im * reRoot;
			m1 += d2;
			m2 += n;
			m2 -= transformLength * (m2 / transformLength);
		}
	}
	int k1 = startIndex;
	final FFTSetup fft2 = FFTSetup.transforms.get(new Integer(K2));
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			for (int n = 0; (n < K1); n++) {
				new DFTBruteForceFloat(reData, imData, reBuffer, imBuffer,
					k1, d1, fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
				k1 += d2;
			}
			break;
		}
		case COPRIMEFACTOR: {
			for (int n = 0; (n < K1); n++) {
				new DFTCoprimeFactorFloat(reData, imData, reBuffer, imBuffer,
					k1, d1, fft2.ruritanian, fft2.chinese, fft2.K1).run();
				k1 += d2;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength2Float(reData, imData, k1, d1).run();
				k1 += d2;
			}
			break;
		}
		case LENGTH3: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength3Float(reData, imData, k1, d1).run();
				k1 += d2;
			}
			break;
		}
		case LENGTH4: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength4Float(reData, imData, k1, d1).run();
				k1 += d2;
			}
			break;
		}
		case LENGTH5: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength5Float(reData, imData, k1, d1).run();
				k1 += d2;
			}
			break;
		}
		case LENGTH6: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength6Float(reData, imData, k1, d1).run();
				k1 += d2;
			}
			break;
		}
		case LENGTH8: {
			for (int n = 0; (n < K1); n++) {
				new DFTLength8Float(reData, imData, k1, d1).run();
				k1 += d2;
			}
			break;
		}
		case MIXEDRADIX: {
			for (int n = 0; (n < K1); n++) {
				new DFTMixedRadixFloat(reData, imData, reBuffer, imBuffer,
					k1, d1, fft2.reUnitRootFloat, fft2.imUnitRootFloat,
					fft2.K1).run();
				k1 += d2;
			}
			break;
		}
		case PADDEDRADER: {
			for (int n = 0; (n < K1); n++) {
				new DFTPaddedRaderFloat(reData, imData,
					k1, d1, fft2.reConvolverFloat, fft2.imConvolverFloat,
					fft2.modular, fft2.inverseModular).run();
				k1 += d2;
			}
			break;
		}
		case RADER: {
			for (int n = 0; (n < K1); n++) {
				new DFTRaderFloat(reData, imData, reBuffer, imBuffer,
					k1, d1, fft2.reConvolverFloat, fft2.imConvolverFloat,
					fft2.modular, fft2.inverseModular).run();
				k1 += d2;
			}
			break;
		}
		case RADIX2: {
			for (int n = 0; (n < K1); n++) {
				new DFTRadix2Float(reData, imData, reBuffer, imBuffer,
					k1, d1, fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
				k1 += d2;
			}
			break;
		}
		case SPLITRADIX: {
			for (int n = 0; (n < K1); n++) {
				new DFTSplitRadixFloat(reData, imData, reBuffer, imBuffer,
					k1, d1, fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
				k1 += d2;
			}
			break;
		}
	}
	k1 = startIndex;
	k2 = startIndex;
	for (int m2 = 0; (m2 < K2); m2++) {
		int n2 = k1;
		for (int m1 = 0; (m1 < K1); m1++) {
			reBuffer[k2] = reData[n2];
			imBuffer[k2] = imData[n2];
			k2 += d1;
			n2 += d2;
		}
		k1 += d1;
	}
	k1 = startIndex;
	for (int m = 0; (m < transformLength); m++) {
		reData[k1] = reBuffer[k1];
		imData[k1] = imBuffer[k1];
		k1 += d1;
	}
} /* end run */

/*....................................................................
	DFTMixedRadixFloat static methods
....................................................................*/
/*------------------------------------------------------------------*/
static float[] getImUnitRoot (
	final int transformLength
) {
	final float[] imUnitRoot = new float[transformLength];
	final float angularStep = -2.0F * (float)PI / (float)transformLength;
	for (int k = 0; (k < transformLength); k++) {
		imUnitRoot[k] = (float)sin((double)((float)k * angularStep));
	}
	return(imUnitRoot);
} /* end getImUnitRoot */

/*------------------------------------------------------------------*/
static float[] getReUnitRoot (
	final int transformLength
) {
	final float[] reUnitRoot = new float[transformLength];
	final float angularStep = -2.0F * (float)PI / (float)transformLength;
	for (int k = 0; (k < transformLength); k++) {
		reUnitRoot[k] = (float)cos((double)((float)k * angularStep));
	}
	return(reUnitRoot);
} /* end getReUnitRoot */

} /* end class DFTMixedRadixFloat */

/*====================================================================
|	DFTMixedRadixReal
\===================================================================*/
static class DFTMixedRadixReal

{ /* begin class DFTMixedRadixReal */

/*....................................................................
	DFTMixedRadixReal static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
	final int K1,
	final int K2
) {
	if (FFTSetup.taboos.contains(new Integer(K1))
		|| FFTSetup.taboos.contains(new Integer(K2))) {
		return(-1L);
	}
	final long K = (long)K1 * (long)K2;
	final long k = K >> 1L;
	final long k1 = (long)(K1 >> 1);
	final long k2 = (long)(K2 >> 1);
	return(FFTSetup.FLALLOC * 4L
		+ FFTSetup.FLOP * ((k2 + 1L) * ((long)K1 * 6L) + k1 * k2 * 1L)
		+ FFTSetup.FLASSIGN * ((k2 + 1L) * ((long)K1 * 6L) + 2L + K * 1L)
		+ FFTSetup.INTALLOC * 14L
		+ FFTSetup.INTOP * (7L + (long)K1 * 3L + (k2 + 1L) * (2L
			+ (long)K1 * 4L) + 1L + (k2 + 1L) * 3L + 4L + (K * 2L + K1 * 5L)
			+ 2L)
		+ FFTSetup.INTASSIGN * (7L + (long)K1 * 2L + 1L + (k2 + 1L) * (3L
			+ (long)K1 * 3L) + 2L + (k2 + 1L) * 2L + 5L + (k * 3L + K1 * 3L))
		+ FFTSetup.IDX * ((k2 + 1L) * ((long)K1 * 6L) + 3L + (K * 2L))
		+ FFTSetup.NEWOBJ * ((long)K1 * 1L + (k2 + 1L) * 1L)
		+ k1 * FFTSetupDuoReal.cost(K2) + (k2 + 1L) * FFTSetup.cost(K1)
	);
} /* end cost */

} /* end class DFTMixedRadixReal */

/*====================================================================
|	DFTMixedRadixRealDouble
\===================================================================*/
static class DFTMixedRadixRealDouble
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTMixedRadixRealDouble */

/*....................................................................
	DFTMixedRadixRealDouble private variables
....................................................................*/
private double[] imBuffer;
private double[] imUnitRoot;
private double[] reBuffer;
private double[] reUnitRoot;
private int K1;

/*....................................................................
	DFTMixedRadixRealDouble constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTMixedRadixRealDouble (
	final double[] reData,
	final double[] imData,
	final double[] reBuffer,
	final double[] imBuffer,
	final int startIndex,
	final int stride,
	final double[] reUnitRoot,
	final double[] imUnitRoot,
	final int K1
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.reUnitRoot = reUnitRoot;
	this.imUnitRoot = imUnitRoot;
	this.K1 = K1;
} /* end DFTMixedRadixDouble */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int transformLength = reUnitRoot.length;
	final int K2 = transformLength / K1;
	final int halfK2 = (K2 >> 1) + 1;
	final int d1 = stride;
	final int d2 = K1 * d1;
	int p = startIndex;
	int k1 = 0;
	if (1 == (K1 & 1)) {
		final FFTSetupReal fft2 = FFTSetupReal.transforms.get(new Integer(K2));
		switch (fft2.algorithm) {
			case BRUTEFORCE: {
				new DFTBruteForceRealDouble(reData, imData,
					reBuffer, imBuffer, p, d2,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
				break;
			}
			case COPRIMEFACTOR: {
				new DFTCoprimeFactorRealDouble(reData, imData,
					reBuffer, imBuffer, p, d2,
					fft2.ruritanian, fft2.chinese, fft2.K1).run();
				break;
			}
			case DUOREAL: {
				throw(new IllegalStateException());
			}
			case EVENREAL: {
				new DFTEvenRealDouble(reData, imData,
					reBuffer, imBuffer, p, d2,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
				break;
			}
			case LENGTH1: {
				throw(new IllegalStateException());
			}
			case LENGTH2: {
				new DFTLength2RealDouble(reData, imData, p, d2).run();
				break;
			}
			case LENGTH3: {
				new DFTLength3RealDouble(reData, imData, p, d2).run();
				break;
			}
			case LENGTH4: {
				new DFTLength4RealDouble(reData, imData, p, d2).run();
				break;
			}
			case LENGTH5: {
				new DFTLength5RealDouble(reData, imData, p, d2).run();
				break;
			}
			case LENGTH6: {
				new DFTLength6RealDouble(reData, imData, p, d2).run();
				break;
			}
			case LENGTH8: {
				new DFTLength8RealDouble(reData, imData, p, d2).run();
				break;
			}
			case MIXEDRADIX: {
				new DFTMixedRadixRealDouble(reData, imData,
					reBuffer, imBuffer, p, d2,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble,
					fft2.K1).run();
				break;
			}
			case PADDEDRADER: {
				new DFTPaddedRaderRealDouble(reData, imData,
					p, d2, fft2.reConvolverDouble, fft2.imConvolverDouble,
					fft2.modular, fft2.inverseModular).run();
				break;
			}
			case RADER: {
				new DFTRaderRealDouble(reData, imData,
					reBuffer, imBuffer, p, d2,
					fft2.reConvolverDouble, fft2.imConvolverDouble,
					fft2.modular, fft2.inverseModular).run();
				break;
			}
			case RADIX2: {
				new DFTRadix2RealDouble(reData, imData,
					reBuffer, imBuffer, p, d2,
					fft2.reUnitRootEvenDouble, fft2.imUnitRootEvenDouble,
					fft2.reUnitRootOddDouble, fft2.imUnitRootOddDouble).run();
				break;
			}
			case SPLITRADIX: {
				new DFTSplitRadixRealDouble(reData, imData,
					reBuffer, imBuffer, p, d2,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
				break;
			}
		}
		p += d1;
		k1++;
	}
	final FFTSetupDuoReal fft2 =
		FFTSetupDuoReal.transforms.get(new Integer(K2));
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			while (k1++ < K1) {
				new DFTBruteForceRealDouble(reData, imData,
					reBuffer, imBuffer, p, d2,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
				p += d1;
			}
			break;
		}
		case COPRIMEFACTOR: {
			while (k1++ < K1) {
				new DFTCoprimeFactorRealDouble(reData, imData,
					reBuffer, imBuffer, p, d2,
					fft2.ruritanian, fft2.chinese, fft2.K1).run();
				p += d1;
			}
			break;
		}
		case DUOREAL: {
			while (k1++ < K1) {
				new DFTDuoRealDouble(reData, imData,
					reBuffer, imBuffer, p, p + d1, d2, K2).run();
				p += 2 * d1;
				k1++;
			}
			break;
		}
		case EVENREAL: {
			while (k1++ < K1) {
				new DFTEvenRealDouble(reData, imData,
					reBuffer, imBuffer, p, d2,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
				p += d1;
			}
			break;
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			while (k1++ < K1) {
				new DFTLength2RealDouble(reData, imData, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH3: {
			while (k1++ < K1) {
				new DFTLength3RealDouble(reData, imData, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH4: {
			while (k1++ < K1) {
				new DFTLength4RealDouble(reData, imData, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH5: {
			while (k1++ < K1) {
				new DFTLength5RealDouble(reData, imData, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH6: {
			while (k1++ < K1) {
				new DFTLength6RealDouble(reData, imData, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH8: {
			while (k1++ < K1) {
				new DFTLength8RealDouble(reData, imData, p, d2).run();
				p += d1;
			}
			break;
		}
		case MIXEDRADIX: {
			while (k1++ < K1) {
				new DFTMixedRadixRealDouble(reData, imData,
					reBuffer, imBuffer, p, d2,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble,
					fft2.K1).run();
				p += d1;
			}
			break;
		}
		case PADDEDRADER: {
			while (k1++ < K1) {
				new DFTPaddedRaderRealDouble(reData, imData,
					p, d2, fft2.reConvolverDouble, fft2.imConvolverDouble,
					fft2.modular, fft2.inverseModular).run();
				p += d1;
			}
			break;
		}
		case RADER: {
			while (k1++ < K1) {
				new DFTRaderRealDouble(reData, imData, reBuffer, imBuffer,
					p, d2, fft2.reConvolverDouble, fft2.imConvolverDouble,
					fft2.modular, fft2.inverseModular).run();
				p += d1;
			}
			break;
		}
		case RADIX2: {
			while (k1++ < K1) {
				new DFTRadix2RealDouble(reData, imData, reBuffer, imBuffer,
					p, d2, fft2.reUnitRootEvenDouble, fft2.imUnitRootEvenDouble,
					fft2.reUnitRootOddDouble, fft2.imUnitRootOddDouble).run();
				p += d1;
			}
			break;
		}
		case SPLITRADIX: {
			while (k1++ < K1) {
				new DFTSplitRadixRealDouble(reData, imData, reBuffer, imBuffer,
					p, d2, fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
				p += d1;
			}
			break;
		}
	}
	p = startIndex;
	for (int n2 = 0; (n2 < halfK2); n2++) {
		int n = 0;
		for (k1 = 0; (k1 < K1); k1++) {
			final double re = reData[p];
			final double im = imData[p];
			final double reRoot = reUnitRoot[n];
			final double imRoot = imUnitRoot[n];
			reBuffer[p] = re * reRoot - im * imRoot;
			imBuffer[p] = re * imRoot + im * reRoot;
			p += d1;
			n += n2;
		}
	}
	p = startIndex;
	final FFTSetup fft1 = FFTSetup.transforms.get(new Integer(K1));
	switch (fft1.algorithm) {
		case BRUTEFORCE: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTBruteForceDouble(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootDouble, fft1.imUnitRootDouble).run();
				p += d2;
			}
			break;
		}
		case COPRIMEFACTOR: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTCoprimeFactorDouble(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.ruritanian, fft1.chinese, fft1.K1).run();
				p += d2;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength2Double(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH3: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength3Double(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH4: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength4Double(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH5: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength5Double(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH6: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength6Double(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH8: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength8Double(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case MIXEDRADIX: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTMixedRadixDouble(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootDouble, fft1.imUnitRootDouble,
					fft1.K1).run();
				p += d2;
			}
			break;
		}
		case PADDEDRADER: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTPaddedRaderDouble(reBuffer, imBuffer,
					p, d1, fft1.reConvolverDouble, fft1.imConvolverDouble,
					fft1.modular, fft1.inverseModular).run();
				p += d2;
			}
			break;
		}
		case RADER: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTRaderDouble(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reConvolverDouble, fft1.imConvolverDouble,
					fft1.modular, fft1.inverseModular).run();
				p += d2;
			}
			break;
		}
		case RADIX2: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTRadix2Double(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootDouble, fft1.imUnitRootDouble).run();
				p += d2;
			}
			break;
		}
		case SPLITRADIX: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTSplitRadixDouble(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootDouble, fft1.imUnitRootDouble).run();
				p += d2;
			}
			break;
		}
	}
	p = startIndex;
	reData[p] = reBuffer[p];
	imData[p] = 0.0;
	p += d1;
	int progressive = startIndex;
	int regressive = startIndex + (K1 - 1) * d1;
	int k2 = 1;
	while (progressive < regressive) {
		int q = progressive + d2;
		while (k2 < halfK2) {
			reData[p] = reBuffer[q];
			imData[p] = imBuffer[q];
			k2++;
			p += d1;
			q += d2;
		}
		k2 -= 2 - (K2 & 1);
		progressive += d1;
		q = regressive + k2 * d2;
		while (0 <= k2) {
			reData[p] = reBuffer[q];
			imData[p] = -imBuffer[q];
			k2--;
			p += d1;
			q -= d2;
		}
		k2 += 2;
		regressive -= d1;
	}
	if (1 == (K1 & 1)) {
		int q = progressive + k2 * d2;
		while (k2 < halfK2) {
			reData[p] = reBuffer[q];
			imData[p] = imBuffer[q];
			k2++;
			p += d1;
			q += d2;
		}
	}
} /* end run */

} /* end class DFTMixedRadixRealDouble */

/*====================================================================
|	DFTMixedRadixRealFloat
\===================================================================*/
static class DFTMixedRadixRealFloat
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTMixedRadixRealFloat */

/*....................................................................
	DFTMixedRadixRealFloat private variables
....................................................................*/
private float[] imBuffer;
private float[] imUnitRoot;
private float[] reBuffer;
private float[] reUnitRoot;
private int K1;

/*....................................................................
	DFTMixedRadixRealFloat constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTMixedRadixRealFloat (
	final float[] reData,
	final float[] imData,
	final float[] reBuffer,
	final float[] imBuffer,
	final int startIndex,
	final int stride,
	final float[] reUnitRoot,
	final float[] imUnitRoot,
	final int K1
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.reUnitRoot = reUnitRoot;
	this.imUnitRoot = imUnitRoot;
	this.K1 = K1;
} /* end DFTMixedRadixRealFloat */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int transformLength = reUnitRoot.length;
	final int K2 = transformLength / K1;
	final int halfK2 = (K2 >> 1) + 1;
	final int d1 = stride;
	final int d2 = K1 * d1;
	int p = startIndex;
	int k1 = 0;
	if (1 == (K1 & 1)) {
		final FFTSetupReal fft2 = FFTSetupReal.transforms.get(new Integer(K2));
		switch (fft2.algorithm) {
			case BRUTEFORCE: {
				new DFTBruteForceRealFloat(reData, imData,
					reBuffer, imBuffer, p, d2,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
				break;
			}
			case COPRIMEFACTOR: {
				new DFTCoprimeFactorRealFloat(reData, imData,
					reBuffer, imBuffer, p, d2,
					fft2.ruritanian, fft2.chinese, fft2.K1).run();
				break;
			}
			case DUOREAL: {
				throw(new IllegalStateException());
			}
			case EVENREAL: {
				new DFTEvenRealFloat(reData, imData,
					reBuffer, imBuffer, p, d2,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
				break;
			}
			case LENGTH1: {
				throw(new IllegalStateException());
			}
			case LENGTH2: {
				new DFTLength2RealFloat(reData, imData, p, d2).run();
				break;
			}
			case LENGTH3: {
				new DFTLength3RealFloat(reData, imData, p, d2).run();
				break;
			}
			case LENGTH4: {
				new DFTLength4RealFloat(reData, imData, p, d2).run();
				break;
			}
			case LENGTH5: {
				new DFTLength5RealFloat(reData, imData, p, d2).run();
				break;
			}
			case LENGTH6: {
				new DFTLength6RealFloat(reData, imData, p, d2).run();
				break;
			}
			case LENGTH8: {
				new DFTLength8RealFloat(reData, imData, p, d2).run();
				break;
			}
			case MIXEDRADIX: {
				new DFTMixedRadixRealFloat(reData, imData,
					reBuffer, imBuffer, p, d2,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat,
					fft2.K1).run();
				break;
			}
			case PADDEDRADER: {
				new DFTPaddedRaderRealFloat(reData, imData,
					p, d2, fft2.reConvolverFloat, fft2.imConvolverFloat,
					fft2.modular, fft2.inverseModular).run();
				break;
			}
			case RADER: {
				new DFTRaderRealFloat(reData, imData,
					reBuffer, imBuffer, p, d2,
					fft2.reConvolverFloat, fft2.imConvolverFloat,
					fft2.modular, fft2.inverseModular).run();
				break;
			}
			case RADIX2: {
				new DFTRadix2RealFloat(reData, imData,
					reBuffer, imBuffer, p, d2,
					fft2.reUnitRootEvenFloat, fft2.imUnitRootEvenFloat,
					fft2.reUnitRootOddFloat, fft2.imUnitRootOddFloat).run();
				break;
			}
			case SPLITRADIX: {
				new DFTSplitRadixRealFloat(reData, imData,
					reBuffer, imBuffer, p, d2,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
				break;
			}
		}
		p += d1;
		k1++;
	}
	final FFTSetupDuoReal fft2 =
		FFTSetupDuoReal.transforms.get(new Integer(K2));
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			while (k1++ < K1) {
				new DFTBruteForceRealFloat(reData, imData,
					reBuffer, imBuffer, p, d2,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
				p += d1;
			}
			break;
		}
		case COPRIMEFACTOR: {
			while (k1++ < K1) {
				new DFTCoprimeFactorRealFloat(reData, imData,
					reBuffer, imBuffer, p, d2,
					fft2.ruritanian, fft2.chinese, fft2.K1).run();
				p += d1;
			}
			break;
		}
		case DUOREAL: {
			while (k1++ < K1) {
				new DFTDuoRealFloat(reData, imData,
					reBuffer, imBuffer, p, p + d1, d2, K2).run();
				p += 2 * d1;
				k1++;
			}
			break;
		}
		case EVENREAL: {
			while (k1++ < K1) {
				new DFTEvenRealFloat(reData, imData,
					reBuffer, imBuffer, p, d2,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
				p += d1;
			}
			break;
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			while (k1++ < K1) {
				new DFTLength2RealFloat(reData, imData, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH3: {
			while (k1++ < K1) {
				new DFTLength3RealFloat(reData, imData, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH4: {
			while (k1++ < K1) {
				new DFTLength4RealFloat(reData, imData, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH5: {
			while (k1++ < K1) {
				new DFTLength5RealFloat(reData, imData, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH6: {
			while (k1++ < K1) {
				new DFTLength6RealFloat(reData, imData, p, d2).run();
				p += d1;
			}
			break;
		}
		case LENGTH8: {
			while (k1++ < K1) {
				new DFTLength8RealFloat(reData, imData, p, d2).run();
				p += d1;
			}
			break;
		}
		case MIXEDRADIX: {
			while (k1++ < K1) {
				new DFTMixedRadixRealFloat(reData, imData,
					reBuffer, imBuffer, p, d2,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat,
					fft2.K1).run();
				p += d1;
			}
			break;
		}
		case PADDEDRADER: {
			while (k1++ < K1) {
				new DFTPaddedRaderRealFloat(reData, imData,
					p, d2, fft2.reConvolverFloat, fft2.imConvolverFloat,
					fft2.modular, fft2.inverseModular).run();
				p += d1;
			}
			break;
		}
		case RADER: {
			while (k1++ < K1) {
				new DFTRaderRealFloat(reData, imData, reBuffer, imBuffer,
					p, d2, fft2.reConvolverFloat, fft2.imConvolverFloat,
					fft2.modular, fft2.inverseModular).run();
				p += d1;
			}
			break;
		}
		case RADIX2: {
			while (k1++ < K1) {
				new DFTRadix2RealFloat(reData, imData, reBuffer, imBuffer,
					p, d2, fft2.reUnitRootEvenFloat, fft2.imUnitRootEvenFloat,
					fft2.reUnitRootOddFloat, fft2.imUnitRootOddFloat).run();
				p += d1;
			}
			break;
		}
		case SPLITRADIX: {
			while (k1++ < K1) {
				new DFTSplitRadixRealFloat(reData, imData, reBuffer, imBuffer,
					p, d2, fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
				p += d1;
			}
			break;
		}
	}
	p = startIndex;
	for (int n2 = 0; (n2 < halfK2); n2++) {
		int n = 0;
		for (k1 = 0; (k1 < K1); k1++) {
			final float re = reData[p];
			final float im = imData[p];
			final float reRoot = reUnitRoot[n];
			final float imRoot = imUnitRoot[n];
			reBuffer[p] = re * reRoot - im * imRoot;
			imBuffer[p] = re * imRoot + im * reRoot;
			p += d1;
			n += n2;
		}
	}
	p = startIndex;
	final FFTSetup fft1 = FFTSetup.transforms.get(new Integer(K1));
	switch (fft1.algorithm) {
		case BRUTEFORCE: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTBruteForceFloat(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootFloat, fft1.imUnitRootFloat).run();
				p += d2;
			}
			break;
		}
		case COPRIMEFACTOR: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTCoprimeFactorFloat(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.ruritanian, fft1.chinese, fft1.K1).run();
				p += d2;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength2Float(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH3: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength3Float(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH4: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength4Float(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH5: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength5Float(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH6: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength6Float(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case LENGTH8: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTLength8Float(reBuffer, imBuffer, p, d1).run();
				p += d2;
			}
			break;
		}
		case MIXEDRADIX: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTMixedRadixFloat(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootFloat, fft1.imUnitRootFloat,
					fft1.K1).run();
				p += d2;
			}
			break;
		}
		case PADDEDRADER: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTPaddedRaderFloat(reBuffer, imBuffer,
					p, d1, fft1.reConvolverFloat, fft1.imConvolverFloat,
					fft1.modular, fft1.inverseModular).run();
				p += d2;
			}
			break;
		}
		case RADER: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTRaderFloat(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reConvolverFloat, fft1.imConvolverFloat,
					fft1.modular, fft1.inverseModular).run();
				p += d2;
			}
			break;
		}
		case RADIX2: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTRadix2Float(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootFloat, fft1.imUnitRootFloat).run();
				p += d2;
			}
			break;
		}
		case SPLITRADIX: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				new DFTSplitRadixFloat(reBuffer, imBuffer, reData, imData,
					p, d1, fft1.reUnitRootFloat, fft1.imUnitRootFloat).run();
				p += d2;
			}
			break;
		}
	}
	p = startIndex;
	reData[p] = reBuffer[p];
	imData[p] = 0.0F;
	p += d1;
	int progressive = startIndex;
	int regressive = startIndex + (K1 - 1) * d1;
	int k2 = 1;
	while (progressive < regressive) {
		int q = progressive + d2;
		while (k2 < halfK2) {
			reData[p] = reBuffer[q];
			imData[p] = imBuffer[q];
			k2++;
			p += d1;
			q += d2;
		}
		k2 -= 2 - (K2 & 1);
		progressive += d1;
		q = regressive + k2 * d2;
		while (0 <= k2) {
			reData[p] = reBuffer[q];
			imData[p] = -imBuffer[q];
			k2--;
			p += d1;
			q -= d2;
		}
		k2 += 2;
		regressive -= d1;
	}
	if (1 == (K1 & 1)) {
		int q = progressive + k2 * d2;
		while (k2 < halfK2) {
			reData[p] = reBuffer[q];
			imData[p] = imBuffer[q];
			k2++;
			p += d1;
			q += d2;
		}
	}
} /* end run */

} /* end class DFTMixedRadixRealFloat */

/*====================================================================
|	DFTPaddedRader
\===================================================================*/
static class DFTPaddedRader

{ /* begin class DFTPaddedRader */

/*....................................................................
	DFTPaddedRader static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
	final int prime,
	final int paddedLength
) {
	if (FFTSetup.taboos.contains(new Integer(paddedLength))) {
		return(-1L);
	}
	final long K = (long)prime;
	final long P = (long)paddedLength;
	return(FFTSetup.FLALLOC * (P * 4L + 8L)
		+ FFTSetup.FLOP * (P * 6L + (K - 1L) * 2L + 2L)
		+ FFTSetup.FLASSIGN * (P * 4L + 2L + (K - 1L) * 2L + 2L + P * 6L
			+ (K - 1L) * 2L + 2L)
		+ FFTSetup.INTALLOC * 9L
		+ FFTSetup.INTOP * (2L + (K - 1L) * 5L + P * 2L + 1L + (K - 1L) * 3L)
		+ FFTSetup.INTASSIGN * (4L + (K - 1L) * 3L + 1L + P * 1L + 2L
			+ (K - 1L) * 3L)
		+ FFTSetup.IDX * (2L + (K - 1L) * 5L + 2L + P * 6L + (K - 1L) * 5L + 2L)
		+ 1L * (FFTSetup.NEWOBJ
			+ FFTSetup.FLALLOC * 0L
			+ FFTSetup.FLOP * 0L
			+ FFTSetup.FLASSIGN * 0L
			+ FFTSetup.INTALLOC * 0L
			+ FFTSetup.INTOP * 1L
			+ FFTSetup.INTASSIGN * 6L
			+ FFTSetup.IDX * 0L
			+ FFTSetup.NEWOBJ * 6L)
		+ 2L * (FFTSetup.cost(paddedLength)
			+ FFTSetup.FLALLOC * 0L
			+ FFTSetup.FLOP * 0L
			+ FFTSetup.FLASSIGN * 0L
			+ FFTSetup.INTALLOC * 0L
			+ FFTSetup.INTOP * (10L + 2L)
			+ FFTSetup.INTASSIGN * 0L
			+ FFTSetup.IDX * 0L
			+ FFTSetup.NEWOBJ * 1L)
	);
} /* end cost */

/*------------------------------------------------------------------*/
static int[] getInverseModularPowerShuffling (
	final int[] modularShuffling,
	final int paddedLength
) {
	final int prime = modularShuffling.length;
	int[] inverseShuffling = new int[prime];
	inverseShuffling[modularShuffling[prime - 3]] = 0;
	inverseShuffling[modularShuffling[prime - 2]] = 1;
	inverseShuffling[modularShuffling[prime - 1]] = paddedLength - prime + 3;
	for (int k = 4; (k < prime); k++) {
		inverseShuffling[modularShuffling[k - 3]] = paddedLength - prime + k;
	}
	return(inverseShuffling);
} /* end getInverseModularPowerShuffling */

} /* end class DFTPaddedRader */

/*====================================================================
|	DFTPaddedRaderDouble
\===================================================================*/
static class DFTPaddedRaderDouble
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTPaddedRaderDouble */

/*....................................................................
	DFTPaddedRaderDouble private variables
....................................................................*/
private double[] imConvolver;
private double[] reConvolver;
private int[] inverseModular;
private int[] modular;

/*....................................................................
	DFTPaddedRaderDouble constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTPaddedRaderDouble (
	final double[] reData,
	final double[] imData,
	final int startIndex,
	final int stride,
	final double[] reConvolver,
	final double[] imConvolver,
	final int[] modular,
	final int[] inverseModular
) {
	super(reData, imData, startIndex, stride);
	this.reConvolver = reConvolver;
	this.imConvolver = imConvolver;
	this.modular = modular;
	this.inverseModular = inverseModular;
} /* end DFTPaddedRaderDouble */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int prime = modular.length;
	final int paddedLength = reConvolver.length;
	final double[] rePadded = new double[paddedLength];
	final double[] imPadded = new double[paddedLength];
	final double[] reBuffer = new double[paddedLength];
	final double[] imBuffer = new double[paddedLength];
	final double reBaseline = reData[startIndex];
	final double imBaseline = imData[startIndex];
	for (int k = paddedLength - prime + 1, m = 1; (k < paddedLength); k++) {
		final int q = startIndex + modular[m++] * stride;
		rePadded[k] = reData[q];
		imPadded[k] = imData[q];
	}
	final AcademicFFT fft = new AcademicFFT(paddedLength, 0);
	fft.directTransform(rePadded, imPadded, reBuffer, imBuffer,
		InputDataType.COMPLEXINPUT);
	final double reSum = rePadded[0];
	final double imSum = imPadded[0];
	for (int k = 0; (k < paddedLength); k++) {
		final double re = rePadded[k];
		final double im = imPadded[k];
		final double reWeight = reConvolver[k];
		final double imWeight = imConvolver[k];
		rePadded[k] = re * reWeight - im * imWeight;
		imPadded[k] = re * imWeight + im * reWeight;
	}
	fft.directTransform(rePadded, imPadded, reBuffer, imBuffer,
		InputDataType.COMPLEXINPUT);
	int p = startIndex + stride;
	for (int k = 1; (k < prime); k++) {
		final int q = inverseModular[k];
		reData[p] = rePadded[q] + reBaseline;
		imData[p] = imPadded[q] + imBaseline;
		p += stride;
	}
	reData[startIndex] += reSum;
	imData[startIndex] += imSum;
} /* end run */

/*....................................................................
	DFTPaddedRaderDouble static methods
....................................................................*/
/*------------------------------------------------------------------*/
static double[][] getConvolverReAndIm (
	final int[] modular,
	final int paddedLength
) {
	final int prime = modular.length;
	final double[] reConvolver = new double[paddedLength];
	final double[] imConvolver = new double[paddedLength];
	final double angularStep = -2.0 * PI / (double)prime;
	final double norm = 1.0 / (double)paddedLength;
	int n = 0;
	for (int k = prime - 2; (1 <= k); k--) {
		final double shuffled = (double)modular[k];
		reConvolver[n] = norm * cos(shuffled * angularStep);
		imConvolver[n] = norm * sin(shuffled * angularStep);
		n++;
	}
	reConvolver[n] = norm * cos(angularStep);
	imConvolver[n] = norm * sin(angularStep);
	while (++n < paddedLength) {
		reConvolver[n] = reConvolver[n - prime + 1];
		imConvolver[n] = imConvolver[n - prime + 1];
	}
	final AcademicFFT fft = new AcademicFFT(paddedLength, 0);
	fft.directTransform(reConvolver, imConvolver, null, null,
		InputDataType.COMPLEXINPUT);
	return(new double[][] {
		reConvolver,
		imConvolver
	});
} /* end getConvolverReAndIm */

} /* end class DFTPaddedRaderDouble */

/*====================================================================
|	DFTPaddedRaderFloat
\===================================================================*/
static class DFTPaddedRaderFloat
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTPaddedRaderFloat */

/*....................................................................
	DFTPaddedRaderFloat private variables
....................................................................*/
private float[] imConvolver;
private float[] reConvolver;
private int[] inverseModular;
private int[] modular;

/*....................................................................
	DFTPaddedRaderFloat constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTPaddedRaderFloat (
	final float[] reData,
	final float[] imData,
	final int startIndex,
	final int stride,
	final float[] reConvolver,
	final float[] imConvolver,
	final int[] modular,
	final int[] inverseModular
) {
	super(reData, imData, startIndex, stride);
	this.reConvolver = reConvolver;
	this.imConvolver = imConvolver;
	this.modular = modular;
	this.inverseModular = inverseModular;
} /* end DFTPaddedRaderFloat */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int prime = modular.length;
	final int paddedLength = reConvolver.length;
	final float[] rePadded = new float[paddedLength];
	final float[] imPadded = new float[paddedLength];
	final float[] reBuffer = new float[paddedLength];
	final float[] imBuffer = new float[paddedLength];
	final float reBaseline = reData[startIndex];
	final float imBaseline = imData[startIndex];
	for (int k = paddedLength - prime + 1, m = 1; (k < paddedLength); k++) {
		final int q = startIndex + modular[m++] * stride;
		rePadded[k] = reData[q];
		imPadded[k] = imData[q];
	}
	final AcademicFFT fft = new AcademicFFT(paddedLength, 0);
	fft.directTransform(rePadded, imPadded, reBuffer, imBuffer,
		InputDataType.COMPLEXINPUT);
	final float reSum = rePadded[0];
	final float imSum = imPadded[0];
	for (int k = 0; (k < paddedLength); k++) {
		final float re = rePadded[k];
		final float im = imPadded[k];
		final float reWeight = reConvolver[k];
		final float imWeight = imConvolver[k];
		rePadded[k] = re * reWeight - im * imWeight;
		imPadded[k] = re * imWeight + im * reWeight;
	}
	fft.directTransform(rePadded, imPadded, reBuffer, imBuffer,
		InputDataType.COMPLEXINPUT);
	int p = startIndex + stride;
	for (int k = 1; (k < prime); k++) {
		final int q = inverseModular[k];
		reData[p] = rePadded[q] + reBaseline;
		imData[p] = imPadded[q] + imBaseline;
		p += stride;
	}
	reData[startIndex] += reSum;
	imData[startIndex] += imSum;
} /* end run */

/*....................................................................
	DFTPaddedRaderFloat static methods
....................................................................*/
/*------------------------------------------------------------------*/
static float[][] getConvolverReAndIm (
	final int[] modular,
	final int paddedLength
) {
	final int prime = modular.length;
	final float[] reConvolver = new float[paddedLength];
	final float[] imConvolver = new float[paddedLength];
	final float angularStep = -2.0F * (float)PI / (float)prime;
	final float norm = 1.0F / (float)paddedLength;
	int n = 0;
	for (int k = prime - 2; (1 <= k); k--) {
		final float shuffled = (float)modular[k];
		reConvolver[n] = norm * (float)cos((double)(shuffled * angularStep));
		imConvolver[n] = norm * (float)sin((double)(shuffled * angularStep));
		n++;
	}
	reConvolver[n] = norm * (float)cos((double)angularStep);
	imConvolver[n] = norm * (float)sin((double)angularStep);
	while (++n < paddedLength) {
		reConvolver[n] = reConvolver[n - prime + 1];
		imConvolver[n] = imConvolver[n - prime + 1];
	}
	final AcademicFFT fft = new AcademicFFT(paddedLength, 0);
	fft.directTransform(reConvolver, imConvolver, null, null,
		InputDataType.COMPLEXINPUT);
	return(new float[][] {
		reConvolver,
		imConvolver
	});
} /* end getConvolverReAndIm */

} /* end class DFTPaddedRaderFloat */

/*====================================================================
|	DFTPaddedRaderReal
\===================================================================*/
static class DFTPaddedRaderReal

{ /* begin class DFTPaddedRaderReal */

/*....................................................................
	DFTPaddedRaderReal static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
	final int prime,
	final int paddedLength
) {
	if (FFTSetup.taboos.contains(new Integer(paddedLength))) {
		return(-1L);
	}
	final long K = (long)prime;
	final long k = K >> 1L;
	final long P = (long)paddedLength;
	return(FFTSetup.FLALLOC * (P * 4L + 6L)
		+ FFTSetup.FLOP * (P * 6L + k * 1L + 1L)
		+ FFTSetup.FLASSIGN * (P * 4L + 1L + (K - 1L) * 1L + 1L + P * 6L
			+ k * 2L + 2L)
		+ FFTSetup.INTALLOC * 8L
		+ FFTSetup.INTOP * (4L + (K - 1L) * 5L + P * 2L + 1L + k * 3L)
		+ FFTSetup.INTASSIGN * (5L + (K - 1L) * 2L + 1L + P * 1L + 1L
			+ k * 3L)
		+ FFTSetup.IDX * (1L + (K - 1L) * 3L + 1L + P * 6L + k * 5L + 2L)
		+ 1L * (FFTSetup.NEWOBJ
			+ FFTSetup.FLALLOC * 0L
			+ FFTSetup.FLOP * 0L
			+ FFTSetup.FLASSIGN * 0L
			+ FFTSetup.INTALLOC * 0L
			+ FFTSetup.INTOP * 1L
			+ FFTSetup.INTASSIGN * 6L
			+ FFTSetup.IDX * 0L
			+ FFTSetup.NEWOBJ * 6L)
		+ 1L * (FFTSetupReal.cost(paddedLength)
			+ FFTSetup.FLALLOC * 0L
			+ FFTSetup.FLOP * (k * 1L)
			+ FFTSetup.FLASSIGN * (k * 2L)
			+ FFTSetup.INTALLOC * 2L
			+ FFTSetup.INTOP * (10L + 2L + 1L + k * 3L)
			+ FFTSetup.INTASSIGN * (2L + k * 2L)
			+ FFTSetup.IDX * (k * 4L)
			+ FFTSetup.NEWOBJ * 1L)
		+ 1L * (FFTSetup.cost(paddedLength)
			+ FFTSetup.FLALLOC * 0L
			+ FFTSetup.FLOP * 0L
			+ FFTSetup.FLASSIGN * 0L
			+ FFTSetup.INTALLOC * 0L
			+ FFTSetup.INTOP * (10L + 2L)
			+ FFTSetup.INTASSIGN * 0L
			+ FFTSetup.IDX * 0L
			+ FFTSetup.NEWOBJ * 1L)
	);
} /* end cost */

} /* end class DFTPaddedRaderReal */

/*====================================================================
|	DFTPaddedRaderRealDouble
\===================================================================*/
static class DFTPaddedRaderRealDouble
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTPaddedRaderRealDouble */

/*....................................................................
	DFTPaddedRaderRealDouble private variables
....................................................................*/
private double[] imConvolver;
private double[] reConvolver;
private int[] inverseModular;
private int[] modular;

/*....................................................................
	DFTPaddedRaderRealDouble constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTPaddedRaderRealDouble (
	final double[] reData,
	final double[] imData,
	final int startIndex,
	final int stride,
	final double[] reConvolver,
	final double[] imConvolver,
	final int[] modular,
	final int[] inverseModular
) {
	super(reData, imData, startIndex, stride);
	this.reConvolver = reConvolver;
	this.imConvolver = imConvolver;
	this.modular = modular;
	this.inverseModular = inverseModular;
} /* end DFTPaddedRaderRealDouble */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int prime = modular.length;
	final int halfPrime = (prime >> 1) + 1;
	final int paddedLength = reConvolver.length;
	final double[] rePadded = new double[paddedLength];
	final double[] imPadded = new double[paddedLength];
	final double[] reBuffer = new double[paddedLength];
	final double[] imBuffer = new double[paddedLength];
	final double reBaseline = reData[startIndex];
	for (int k = paddedLength - prime + 1, m = 1; (k < paddedLength); k++) {
		rePadded[k] = reData[startIndex + modular[m++] * stride];
	}
	final AcademicFFT fft = new AcademicFFT(paddedLength, 0);
	fft.directTransform(rePadded, imPadded, reBuffer, imBuffer,
		InputDataType.REALINPUT);
	final double reSum = rePadded[0];
	for (int k = 0; (k < paddedLength); k++) {
		final double re = rePadded[k];
		final double im = imPadded[k];
		final double reWeight = reConvolver[k];
		final double imWeight = imConvolver[k];
		rePadded[k] = re * reWeight - im * imWeight;
		imPadded[k] = re * imWeight + im * reWeight;
	}
	fft.directTransform(rePadded, imPadded, reBuffer, imBuffer,
		InputDataType.COMPLEXINPUT);
	int p = startIndex + stride;
	for (int k = 1; (k < halfPrime); k++) {
		final int q = inverseModular[k];
		reData[p] = rePadded[q] + reBaseline;
		imData[p] = imPadded[q];
		p += stride;
	}
	reData[startIndex] += reSum;
	imData[startIndex] = 0.0;
} /* end run */

} /* end class DFTPaddedRaderRealDouble */

/*====================================================================
|	DFTPaddedRaderRealFloat
\===================================================================*/
static class DFTPaddedRaderRealFloat
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTPaddedRaderRealFloat */

/*....................................................................
	DFTPaddedRaderRealFloat private variables
....................................................................*/
private float[] imConvolver;
private float[] reConvolver;
private int[] inverseModular;
private int[] modular;

/*....................................................................
	DFTPaddedRaderRealFloat constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTPaddedRaderRealFloat (
	final float[] reData,
	final float[] imData,
	final int startIndex,
	final int stride,
	final float[] reConvolver,
	final float[] imConvolver,
	final int[] modular,
	final int[] inverseModular
) {
	super(reData, imData, startIndex, stride);
	this.reConvolver = reConvolver;
	this.imConvolver = imConvolver;
	this.modular = modular;
	this.inverseModular = inverseModular;
} /* end DFTPaddedRaderRealFloat */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int prime = modular.length;
	final int halfPrime = (prime >> 1) + 1;
	final int paddedLength = reConvolver.length;
	final float[] rePadded = new float[paddedLength];
	final float[] imPadded = new float[paddedLength];
	final float[] reBuffer = new float[paddedLength];
	final float[] imBuffer = new float[paddedLength];
	final float reBaseline = reData[startIndex];
	for (int k = paddedLength - prime + 1, m = 1; (k < paddedLength); k++) {
		rePadded[k] = reData[startIndex + modular[m++] * stride];
	}
	final AcademicFFT fft = new AcademicFFT(paddedLength, 0);
	fft.directTransform(rePadded, imPadded, reBuffer, imBuffer,
		InputDataType.REALINPUT);
	final float reSum = rePadded[0];
	for (int k = 0; (k < paddedLength); k++) {
		final float re = rePadded[k];
		final float im = imPadded[k];
		final float reWeight = reConvolver[k];
		final float imWeight = imConvolver[k];
		rePadded[k] = re * reWeight - im * imWeight;
		imPadded[k] = re * imWeight + im * reWeight;
	}
	fft.directTransform(rePadded, imPadded, reBuffer, imBuffer,
		InputDataType.COMPLEXINPUT);
	int p = startIndex + stride;
	for (int k = 1; (k < halfPrime); k++) {
		final int q = inverseModular[k];
		reData[p] = rePadded[q] + reBaseline;
		imData[p] = imPadded[q];
		p += stride;
	}
	reData[startIndex] += reSum;
	imData[startIndex] = 0.0F;
} /* end run */

} /* end class DFTPaddedRaderRealFloat */

/*====================================================================
|	DFTRader
\===================================================================*/
static class DFTRader

{ /* begin class DFTRader */

/*....................................................................
	DFTRader static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
	final int prime
) {
	if (FFTSetup.taboos.contains(new Integer(prime))
		|| FFTSetup.taboos.contains(new Integer(prime - 1))) {
		return(-1L);
	}
	final long K = (long)prime;
	return(FFTSetup.FLALLOC * 6L
		+ FFTSetup.FLOP * ((K - 1L) * 6L + (K - 1L) * 2L + 2L)
		+ FFTSetup.FLASSIGN * (2L + (K - 1L) * 2L + 2L + (K - 1L) * 6L
			+ (K - 1L) * 2L + 2L)
		+ FFTSetup.INTALLOC * 8L
		+ FFTSetup.INTOP * (1L + (K - 1L) * 5L + 4L + (K- 1L) * 3L + 2L
			+ (K - 1L) * 5L)
		+ FFTSetup.INTASSIGN * (3L + (K - 1L) * 3L + 3L + (K - 1L) * 2L + 2L
			+ (K - 1L) * 3L)
		+ FFTSetup.IDX * (2L + (K - 1L) * 5L + 4L + (K - 1L) * 6L
			+ (K - 1L) * 5L + 4L)
		+ FFTSetup.NEWOBJ * 2L
		+ 2L * FFTSetup.cost(prime - 1)
	);
} /* end cost */

/*------------------------------------------------------------------*/
static int[] getInverseModularPowerShuffling (
	final int[] modularShuffling
) {
	final int prime = modularShuffling.length;
	int[] inverseShuffling = new int[prime];
	inverseShuffling[0] = 0;
	inverseShuffling[modularShuffling[prime - 3]] = 1;
	inverseShuffling[modularShuffling[prime - 2]] = 2;
	inverseShuffling[modularShuffling[prime - 1]] = 3;
	for (int k = 4; (k < prime); k++) {
		inverseShuffling[modularShuffling[k - 3]] = k;
	}
	return(inverseShuffling);
} /* end getInverseModularPowerShuffling */

/*------------------------------------------------------------------*/
static int[] getModularPowerShuffling (
	final int prime
) {
	final int g = smallestPrimitiveRootOfPrime(prime);
	int[] modularShuffling = new int[prime];
	modularShuffling[0] = 0;
	for (int k = 1; (k < prime); k++) {
		modularShuffling[k] = modularPositivePower(g, k, prime);
	}
	return(modularShuffling);
} /* end getModularPowerShuffling */

/*....................................................................
	DFTRader private methods
....................................................................*/
/*------------------------------------------------------------------*/
static private TreeMap<Integer, Integer> factorize (
	int number
) {
	final TreeMap<Integer, Integer> factors =
		new TreeMap<Integer, Integer>();
	for (int divisor = 2; (divisor <= number); divisor++) {
		if ((divisor * (number / divisor)) == number) {
			number /= divisor;
			int multiplicity = 1;
			while ((divisor * (number / divisor)) == number) {
				number /= divisor;
				multiplicity++;
			}
			factors.put(new Integer(divisor), new Integer(multiplicity));
		}
	}
	return(factors);
} /* end factorize */

/*------------------------------------------------------------------*/
static private int modularPositivePower (
	int number,
	int exponent,
	final int modulo
) {
	int result = 1;
	number -= modulo * (number / modulo);
	while (0 < exponent) {
		if (1 == (exponent & 1)) {
			result *= number;
			result -= modulo * (result / modulo);
		}
		exponent >>= 1;
		number *= number;
		number -= modulo * (number / modulo);
	}
	return(result);
} /* end modularPositivePower */

/*------------------------------------------------------------------*/
static private int smallestPrimitiveRootOfPrime (
	final int prime
) {
	if (2 == prime) {
		return(1);
	}
	final TreeMap<Integer, Integer> factors = factorize(prime - 1);
	int result = -1;
	for (int candidate = 1; (candidate < prime); candidate++) {
		result = candidate;
		for (Integer primeDivisor: factors.navigableKeySet()) {
			if (1 == modularPositivePower(candidate, (prime - 1)
				/ primeDivisor.intValue(), prime)) {
				result = -1;
				break;
			}
		}
		if (0 < result) {
			break;
		}
	}
	return(result);
} /* end smallestPrimitiveRootOfPrime */

} /* end class DFTRader */

/*====================================================================
|	DFTRaderDouble
\===================================================================*/
static class DFTRaderDouble
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTRaderDouble */

/*....................................................................
	DFTRaderDouble private variables
....................................................................*/
private double[] imBuffer;
private double[] imConvolver;
private double[] reBuffer;
private double[] reConvolver;
private int[] inverseModular;
private int[] modular;

/*....................................................................
	DFTRaderDouble constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTRaderDouble (
	final double[] reData,
	final double[] imData,
	final double[] reBuffer,
	final double[] imBuffer,
	final int startIndex,
	final int stride,
	final double[] reConvolver,
	final double[] imConvolver,
	final int[] modular,
	final int[] inverseModular
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.reConvolver = reConvolver;
	this.imConvolver = imConvolver;
	this.modular = modular;
	this.inverseModular = inverseModular;
} /* end DFTRaderDouble */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int prime = modular.length;
	final double reBaseline = reData[startIndex];
	final double imBaseline = imData[startIndex];
	int p = startIndex + stride;
	for (int k = 1; (k < prime); k++) {
		final int q = startIndex + modular[k] * stride;
		reBuffer[p] = reData[q];
		imBuffer[p] = imData[q];
		p += stride;
	}
	final FFTSetup fft = FFTSetup.transforms.get(new Integer(prime - 1));
	p = startIndex + stride;
	switch (fft.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceDouble(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorDouble(reBuffer, imBuffer, reData, imData,
				p, stride, fft.ruritanian, fft.chinese, fft.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			new DFTLength2Double(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH3: {
			throw(new IllegalStateException());
		}
		case LENGTH4: {
			new DFTLength4Double(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH5: {
			throw(new IllegalStateException());
		}
		case LENGTH6: {
			new DFTLength6Double(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8Double(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixDouble(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootDouble, fft.imUnitRootDouble,
				fft.K1).run();
			break;
		}
		case PADDEDRADER: {
			throw(new IllegalStateException());
		}
		case RADER: {
			throw(new IllegalStateException());
		}
		case RADIX2: {
			new DFTRadix2Double(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixDouble(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
	}
	reBuffer[startIndex] = reBuffer[p];
	imBuffer[startIndex] = imBuffer[p];
	for (int k = 0, K = prime - 1; (k < K); k++) {
		final double re = reBuffer[p];
		final double im = imBuffer[p];
		final double reWeight = reConvolver[k];
		final double imWeight = imConvolver[k];
		reBuffer[p] = re * reWeight - im * imWeight;
		imBuffer[p] = re * imWeight + im * reWeight;
		p += stride;
	}
	p = startIndex + stride;
	switch (fft.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceDouble(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorDouble(reBuffer, imBuffer, reData, imData,
				p, stride, fft.ruritanian, fft.chinese, fft.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			new DFTLength2Double(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH3: {
			throw(new IllegalStateException());
		}
		case LENGTH4: {
			new DFTLength4Double(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH5: {
			throw(new IllegalStateException());
		}
		case LENGTH6: {
			new DFTLength6Double(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8Double(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixDouble(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootDouble, fft.imUnitRootDouble,
				fft.K1).run();
			break;
		}
		case PADDEDRADER: {
			throw(new IllegalStateException());
		}
		case RADER: {
			throw(new IllegalStateException());
		}
		case RADIX2: {
			new DFTRadix2Double(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixDouble(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
	}
	for (int k = 1; (k < prime); k++) {
		final int q = startIndex + inverseModular[k] * stride;
		reData[p] = reBuffer[q] + reBaseline;
		imData[p] = imBuffer[q] + imBaseline;
		p += stride;
	}
	reData[startIndex] += reBuffer[startIndex];
	imData[startIndex] += imBuffer[startIndex];
} /* end run */

/*....................................................................
	DFTRaderDouble static methods
....................................................................*/
/*------------------------------------------------------------------*/
static double[][] getConvolverReAndIm (
	final int[] modular
) {
	final int prime = modular.length;
	final double[] reConvolver = new double[prime - 1];
	final double[] imConvolver = new double[prime - 1];
	final double angularStep = -2.0 * PI / (double)prime;
	final double norm = 1.0 / (double)(prime - 1);
	int n = 0;
	for (int k = prime - 2; (1 <= k); k--) {
		final double shuffled = (double)modular[k];
		reConvolver[n] = norm * cos(shuffled * angularStep);
		imConvolver[n] = norm * sin(shuffled * angularStep);
		n++;
	}
	reConvolver[n] = norm * cos(angularStep);
	imConvolver[n] = norm * sin(angularStep);
	final AcademicFFT fft = new AcademicFFT(prime - 1, 0);
	fft.directTransform(reConvolver, imConvolver, null, null,
		InputDataType.COMPLEXINPUT);
	return(new double[][] {
		reConvolver,
		imConvolver
	});
} /* end getConvolverReAndIm */

} /* end class DFTRaderDouble */

/*====================================================================
|	DFTRaderFloat
\===================================================================*/
static class DFTRaderFloat
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTRaderFloat */

/*....................................................................
	DFTRaderFloat private variables
....................................................................*/
private float[] imBuffer;
private float[] imConvolver;
private float[] reBuffer;
private float[] reConvolver;
private int[] inverseModular;
private int[] modular;

/*....................................................................
	DFTRaderFloat constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTRaderFloat (
	final float[] reData,
	final float[] imData,
	final float[] reBuffer,
	final float[] imBuffer,
	final int startIndex,
	final int stride,
	final float[] reConvolver,
	final float[] imConvolver,
	final int[] modular,
	final int[] inverseModular
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.reConvolver = reConvolver;
	this.imConvolver = imConvolver;
	this.modular = modular;
	this.inverseModular = inverseModular;
} /* end DFTRaderFloat */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int prime = modular.length;
	final float reBaseline = reData[startIndex];
	final float imBaseline = imData[startIndex];
	int p = startIndex + stride;
	for (int k = 1; (k < prime); k++) {
		final int q = startIndex + modular[k] * stride;
		reBuffer[p] = reData[q];
		imBuffer[p] = imData[q];
		p += stride;
	}
	final FFTSetup fft = FFTSetup.transforms.get(new Integer(prime - 1));
	p = startIndex + stride;
	switch (fft.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceFloat(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorFloat(reBuffer, imBuffer, reData, imData,
				p, stride, fft.ruritanian, fft.chinese, fft.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			new DFTLength2Float(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH3: {
			throw(new IllegalStateException());
		}
		case LENGTH4: {
			new DFTLength4Float(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH5: {
			throw(new IllegalStateException());
		}
		case LENGTH6: {
			new DFTLength6Float(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8Float(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixFloat(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootFloat, fft.imUnitRootFloat,
				fft.K1).run();
			break;
		}
		case PADDEDRADER: {
			throw(new IllegalStateException());
		}
		case RADER: {
			throw(new IllegalStateException());
		}
		case RADIX2: {
			new DFTRadix2Float(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixFloat(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
	}
	reBuffer[startIndex] = reBuffer[p];
	imBuffer[startIndex] = imBuffer[p];
	for (int k = 0, K = prime - 1; (k < K); k++) {
		final float re = reBuffer[p];
		final float im = imBuffer[p];
		final float reWeight = reConvolver[k];
		final float imWeight = imConvolver[k];
		reBuffer[p] = re * reWeight - im * imWeight;
		imBuffer[p] = re * imWeight + im * reWeight;
		p += stride;
	}
	p = startIndex + stride;
	switch (fft.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceFloat(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorFloat(reBuffer, imBuffer, reData, imData,
				p, stride, fft.ruritanian, fft.chinese, fft.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			new DFTLength2Float(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH3: {
			throw(new IllegalStateException());
		}
		case LENGTH4: {
			new DFTLength4Float(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH5: {
			throw(new IllegalStateException());
		}
		case LENGTH6: {
			new DFTLength6Float(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8Float(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixFloat(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootFloat, fft.imUnitRootFloat,
				fft.K1).run();
			break;
		}
		case PADDEDRADER: {
			throw(new IllegalStateException());
		}
		case RADER: {
			throw(new IllegalStateException());
		}
		case RADIX2: {
			new DFTRadix2Float(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixFloat(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
	}
	for (int k = 1; (k < prime); k++) {
		final int q = startIndex + inverseModular[k] * stride;
		reData[p] = reBuffer[q] + reBaseline;
		imData[p] = imBuffer[q] + imBaseline;
		p += stride;
	}
	reData[startIndex] += reBuffer[startIndex];
	imData[startIndex] += imBuffer[startIndex];
} /* end run */

/*....................................................................
	DFTRaderFloat static methods
....................................................................*/
/*------------------------------------------------------------------*/
static float[][] getConvolverReAndIm (
	final int[] modular
) {
	final int prime = modular.length;
	final float[] reConvolver = new float[prime - 1];
	final float[] imConvolver = new float[prime - 1];
	final float angularStep = -2.0F * (float)PI / (float)prime;
	final float norm = 1.0F / (float)(prime - 1);
	int n = 0;
	for (int k = prime - 2; (1 <= k); k--) {
		final float shuffled = (float)modular[k];
		reConvolver[n] = norm * (float)cos((double)(shuffled * angularStep));
		imConvolver[n] = norm * (float)sin((double)(shuffled * angularStep));
		n++;
	}
	reConvolver[n] = norm * (float)cos((double)angularStep);
	imConvolver[n] = norm * (float)sin((double)angularStep);
	final AcademicFFT fft = new AcademicFFT(prime - 1, 0);
	fft.directTransform(reConvolver, imConvolver, null, null,
		InputDataType.COMPLEXINPUT);
	return(new float[][] {
		reConvolver,
		imConvolver
	});
} /* end getConvolverReAndIm */

} /* end class DFTRaderFloat */

/*====================================================================
|	DFTRaderReal
\===================================================================*/
static class DFTRaderReal

{ /* begin class DFTRaderReal */

/*....................................................................
	DFTRaderReal static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
	final int prime
) {
	if (FFTSetup.taboos.contains(new Integer(prime))
		|| FFTSetup.taboos.contains(new Integer(prime - 1))) {
		return(-1L);
	}
	final long K = (long)prime;
	final long k = K >> 1L;
	return(FFTSetup.FLALLOC * 5L
		+ FFTSetup.FLOP * (k * 1L + (K - 1L) * 6L + k * 1L + 1L)
		+ FFTSetup.FLASSIGN * (1L + (K - 1L) * 1L + k * 2L + 1L + (K - 1L) * 6L
			+ k * 2L + 2L)
		+ FFTSetup.INTALLOC * 11L
		+ FFTSetup.INTOP * (3L + (K - 1L) * 5L + 7L + k * 3L + 1L
			+ (K- 1L) * 3L + 3L + k * 5L)
		+ FFTSetup.INTASSIGN * (4L + (K - 1L) * 3L + 3L + k * 2L + 2L
			+ (K - 1L) * 2L + 2L + k * 3L)
		+ FFTSetup.IDX * (1L + (K - 1L) * 3L + k * 4L + 2L + (K - 1L) * 6L
			+ k * 5L + 3L)
		+ FFTSetup.NEWOBJ * 2L
		+ FFTSetupReal.cost(prime - 1) + FFTSetup.cost(prime - 1)
	);
} /* end cost */

} /* end class DFTRaderReal */

/*====================================================================
|	DFTRaderRealDouble
\===================================================================*/
static class DFTRaderRealDouble
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTRaderRealDouble */

/*....................................................................
	DFTRaderRealDouble private variables
....................................................................*/
private double[] imBuffer;
private double[] imConvolver;
private double[] reBuffer;
private double[] reConvolver;
private int[] inverseModular;
private int[] modular;

/*....................................................................
	DFTRaderRealDouble constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTRaderRealDouble (
	final double[] reData,
	final double[] imData,
	final double[] reBuffer,
	final double[] imBuffer,
	final int startIndex,
	final int stride,
	final double[] reConvolver,
	final double[] imConvolver,
	final int[] modular,
	final int[] inverseModular
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.reConvolver = reConvolver;
	this.imConvolver = imConvolver;
	this.modular = modular;
	this.inverseModular = inverseModular;
} /* end DFTRaderRealDouble */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int prime = modular.length;
	final int halfPrime = (prime >> 1) + 1;
	final double reBaseline = reData[startIndex];
	int p = startIndex + stride;
	for (int k = 1; (k < prime); k++) {
		final int q = startIndex + modular[k] * stride;
		reBuffer[p] = reData[q];
		p += stride;
	}
	final FFTSetupReal fftReal =
		FFTSetupReal.transforms.get(new Integer(prime - 1));
	p = startIndex + stride;
	switch (fftReal.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceRealDouble(reBuffer, imBuffer, reData, imData,
				p, stride,
				fftReal.reUnitRootDouble, fftReal.imUnitRootDouble).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorRealDouble(reBuffer, imBuffer, reData, imData,
				p, stride,
				fftReal.ruritanian, fftReal.chinese, fftReal.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			new DFTEvenRealDouble(reBuffer, imBuffer, reData, imData,
				p, stride,
				fftReal.reUnitRootDouble, fftReal.imUnitRootDouble).run();
			break;
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			new DFTLength2RealDouble(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH3: {
			throw(new IllegalStateException());
		}
		case LENGTH4: {
			new DFTLength4RealDouble(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH5: {
			throw(new IllegalStateException());
		}
		case LENGTH6: {
			new DFTLength6RealDouble(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8RealDouble(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixRealDouble(reBuffer, imBuffer, reData, imData,
				p, stride,
				fftReal.reUnitRootDouble, fftReal.imUnitRootDouble,
				fftReal.K1).run();
			break;
		}
		case PADDEDRADER: {
			throw(new IllegalStateException());
		}
		case RADER: {
			throw(new IllegalStateException());
		}
		case RADIX2: {
			new DFTRadix2RealDouble(reBuffer, imBuffer, reData, imData,
				p, stride,
				fftReal.reUnitRootEvenDouble, fftReal.imUnitRootEvenDouble,
				fftReal.reUnitRootOddDouble, fftReal.imUnitRootOddDouble).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixRealDouble(reBuffer, imBuffer, reData, imData,
				p, stride,
				fftReal.reUnitRootDouble, fftReal.imUnitRootDouble).run();
			break;
		}
	}
	int progressive = p + stride;
	int regressive = p + (prime - 2) * stride;
	while (progressive < regressive) {
		reBuffer[regressive] = reBuffer[progressive];
		imBuffer[regressive] = -imBuffer[progressive];
		progressive += stride;
		regressive -= stride;
	}
	reBuffer[startIndex] = reBuffer[p];
	for (int k = 0, K = prime - 1; (k < K); k++) {
		final double re = reBuffer[p];
		final double im = imBuffer[p];
		final double reWeight = reConvolver[k];
		final double imWeight = imConvolver[k];
		reBuffer[p] = re * reWeight - im * imWeight;
		imBuffer[p] = re * imWeight + im * reWeight;
		p += stride;
	}
	p = startIndex + stride;
	final FFTSetup fft = FFTSetup.transforms.get(new Integer(prime - 1));
	switch (fft.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceDouble(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorDouble(reBuffer, imBuffer, reData, imData,
				p, stride, fft.ruritanian, fft.chinese, fft.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			new DFTLength2Double(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH3: {
			throw(new IllegalStateException());
		}
		case LENGTH4: {
			new DFTLength4Double(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH5: {
			throw(new IllegalStateException());
		}
		case LENGTH6: {
			new DFTLength6Double(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8Double(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixDouble(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootDouble, fft.imUnitRootDouble,
				fft.K1).run();
			break;
		}
		case PADDEDRADER: {
			throw(new IllegalStateException());
		}
		case RADER: {
			throw(new IllegalStateException());
		}
		case RADIX2: {
			new DFTRadix2Double(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixDouble(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
	}
	for (int k = 1; (k < halfPrime); k++) {
		final int q = startIndex + inverseModular[k] * stride;
		reData[p] = reBuffer[q] + reBaseline;
		imData[p] = imBuffer[q];
		p += stride;
	}
	reData[startIndex] += reBuffer[startIndex];
	imData[startIndex] = 0.0;
} /* end run */

} /* end class DFTRaderRealDouble */

/*====================================================================
|	DFTRaderRealFloat
\===================================================================*/
static class DFTRaderRealFloat
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTRaderRealFloat */

/*....................................................................
	DFTRaderRealFloat private variables
....................................................................*/
private float[] imBuffer;
private float[] imConvolver;
private float[] reBuffer;
private float[] reConvolver;
private int[] inverseModular;
private int[] modular;

/*....................................................................
	DFTRaderRealFloat constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTRaderRealFloat (
	final float[] reData,
	final float[] imData,
	final float[] reBuffer,
	final float[] imBuffer,
	final int startIndex,
	final int stride,
	final float[] reConvolver,
	final float[] imConvolver,
	final int[] modular,
	final int[] inverseModular
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.reConvolver = reConvolver;
	this.imConvolver = imConvolver;
	this.modular = modular;
	this.inverseModular = inverseModular;
} /* end DFTRaderRealFloat */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int prime = modular.length;
	final int halfPrime = (prime >> 1) + 1;
	final float reBaseline = reData[startIndex];
	int p = startIndex + stride;
	for (int k = 1; (k < prime); k++) {
		final int q = startIndex + modular[k] * stride;
		reBuffer[p] = reData[q];
		p += stride;
	}
	final FFTSetupReal fftReal =
		FFTSetupReal.transforms.get(new Integer(prime - 1));
	p = startIndex + stride;
	switch (fftReal.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceRealFloat(reBuffer, imBuffer, reData, imData,
				p, stride,
				fftReal.reUnitRootFloat, fftReal.imUnitRootFloat).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorRealFloat(reBuffer, imBuffer, reData, imData,
				p, stride,
				fftReal.ruritanian, fftReal.chinese, fftReal.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			new DFTEvenRealFloat(reBuffer, imBuffer, reData, imData,
				p, stride,
				fftReal.reUnitRootFloat, fftReal.imUnitRootFloat).run();
			break;
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			new DFTLength2RealFloat(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH3: {
			throw(new IllegalStateException());
		}
		case LENGTH4: {
			new DFTLength4RealFloat(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH5: {
			throw(new IllegalStateException());
		}
		case LENGTH6: {
			new DFTLength6RealFloat(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8RealFloat(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixRealFloat(reBuffer, imBuffer, reData, imData,
				p, stride,
				fftReal.reUnitRootFloat, fftReal.imUnitRootFloat,
				fftReal.K1).run();
			break;
		}
		case PADDEDRADER: {
			throw(new IllegalStateException());
		}
		case RADER: {
			throw(new IllegalStateException());
		}
		case RADIX2: {
			new DFTRadix2RealFloat(reBuffer, imBuffer, reData, imData,
				p, stride,
				fftReal.reUnitRootEvenFloat, fftReal.imUnitRootEvenFloat,
				fftReal.reUnitRootOddFloat, fftReal.imUnitRootOddFloat).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixRealFloat(reBuffer, imBuffer, reData, imData,
				p, stride,
				fftReal.reUnitRootFloat, fftReal.imUnitRootFloat).run();
			break;
		}
	}
	int progressive = p + stride;
	int regressive = p + (prime - 2) * stride;
	while (progressive < regressive) {
		reBuffer[regressive] = reBuffer[progressive];
		imBuffer[regressive] = -imBuffer[progressive];
		progressive += stride;
		regressive -= stride;
	}
	reBuffer[startIndex] = reBuffer[p];
	for (int k = 0, K = prime - 1; (k < K); k++) {
		final float re = reBuffer[p];
		final float im = imBuffer[p];
		final float reWeight = reConvolver[k];
		final float imWeight = imConvolver[k];
		reBuffer[p] = re * reWeight - im * imWeight;
		imBuffer[p] = re * imWeight + im * reWeight;
		p += stride;
	}
	p = startIndex + stride;
	final FFTSetup fft = FFTSetup.transforms.get(new Integer(prime - 1));
	switch (fft.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceFloat(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorFloat(reBuffer, imBuffer, reData, imData,
				p, stride, fft.ruritanian, fft.chinese, fft.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			new DFTLength2Float(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH3: {
			throw(new IllegalStateException());
		}
		case LENGTH4: {
			new DFTLength4Float(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH5: {
			throw(new IllegalStateException());
		}
		case LENGTH6: {
			new DFTLength6Float(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8Float(reBuffer, imBuffer, p, stride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixFloat(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootFloat, fft.imUnitRootFloat,
				fft.K1).run();
			break;
		}
		case PADDEDRADER: {
			throw(new IllegalStateException());
		}
		case RADER: {
			throw(new IllegalStateException());
		}
		case RADIX2: {
			new DFTRadix2Float(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixFloat(reBuffer, imBuffer, reData, imData,
				p, stride, fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
	}
	for (int k = 1; (k < halfPrime); k++) {
		final int q = startIndex + inverseModular[k] * stride;
		reData[p] = reBuffer[q] + reBaseline;
		imData[p] = imBuffer[q];
		p += stride;
	}
	reData[startIndex] += reBuffer[startIndex];
	imData[startIndex] = 0.0F;
} /* end run */

} /* end class DFTRaderRealFloat */

/*====================================================================
|	DFTRadix2
\===================================================================*/
static class DFTRadix2

{ /* begin class DFTRadix2 */

/*....................................................................
	DFTRadix2 static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
	final int transformLength
) {
	if (FFTSetup.taboos.contains(new Integer(transformLength / 2))) {
		return(-1L);
	}
	final long k = (long)(transformLength >> 1);
	return(FFTSetup.FLALLOC * 4L
		+ FFTSetup.FLOP * (k * 6L + k * 4L)
		+ FFTSetup.FLASSIGN * (k * 4L + k * 8L + k * 4L)
		+ FFTSetup.INTALLOC * 8L
		+ FFTSetup.INTOP * (5L + k * 6L + k * 5L + k * 4L)
		+ FFTSetup.INTASSIGN * (6L + k * 5L + 2L + k * 4L + 1L + k * 3L)
		+ FFTSetup.IDX * (k * 8L + k * 10L + k * 4L)
		+ FFTSetup.NEWOBJ * 2L
		+ 2L * FFTSetup.cost(transformLength / 2)
	);
} /* end cost */

} /* end class DFTRadix2 */

/*====================================================================
|	DFTRadix2Double
\===================================================================*/
static class DFTRadix2Double
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTRadix2Double */

/*....................................................................
	DFTRadix2Double static variables
....................................................................*/
private double[] imBuffer;
private double[] imUnitRoot;
private double[] reBuffer;
private double[] reUnitRoot;

/*....................................................................
	DFTRadix2Double constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTRadix2Double (
	final double[] reData,
	final double[] imData,
	final double[] reBuffer,
	final double[] imBuffer,
	final int startIndex,
	final int stride,
	final double[] reUnitRoot,
	final double[] imUnitRoot
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.reUnitRoot = reUnitRoot;
	this.imUnitRoot = imUnitRoot;
} /* end DFTRadix2Double */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int K2 = reUnitRoot.length;
	final int doubleStride = stride << 1;
	final FFTSetup fft = FFTSetup.transforms.get(new Integer(K2));
	switch (fft.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			new DFTBruteForceDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.ruritanian, fft.chinese, fft.K1).run();
			new DFTCoprimeFactorDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.ruritanian, fft.chinese, fft.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			new DFTLength2Double(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength2Double(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case LENGTH3: {
			new DFTLength3Double(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength3Double(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case LENGTH4: {
			new DFTLength4Double(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength4Double(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case LENGTH5: {
			new DFTLength5Double(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength5Double(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case LENGTH6: {
			new DFTLength6Double(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength6Double(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8Double(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength8Double(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.reUnitRootDouble, fft.imUnitRootDouble,
				fft.K1).run();
			new DFTMixedRadixDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.reUnitRootDouble, fft.imUnitRootDouble,
				fft.K1).run();
			break;
		}
		case PADDEDRADER: {
			new DFTPaddedRaderDouble(reData, imData,
				startIndex, doubleStride,
				fft.reConvolverDouble, fft.imConvolverDouble,
				fft.modular, fft.inverseModular).run();
			new DFTPaddedRaderDouble(reData, imData,
				startIndex + stride, doubleStride,
				fft.reConvolverDouble, fft.imConvolverDouble,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADER: {
			new DFTRaderDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.reConvolverDouble, fft.imConvolverDouble,
				fft.modular, fft.inverseModular).run();
			new DFTRaderDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.reConvolverDouble, fft.imConvolverDouble,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADIX2: {
			new DFTRadix2Double(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			new DFTRadix2Double(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			new DFTSplitRadixDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
	}
	int m0 = startIndex;
	int i0 = startIndex;
	int i1 = startIndex + K2 * stride;
	for (int m = 0; (m < K2); m++) {
		reBuffer[i0] = reData[m0];
		imBuffer[i0] = imData[m0];
		m0 += stride;
		reBuffer[i1] = reData[m0];
		imBuffer[i1] = imData[m0];
		m0 += stride;
		i0 += stride;
		i1 += stride;
	}
	m0 = K2;
	for (int m = 0; (m < K2); m++) {
		m0--;
		i1 -= stride;
		i0 -= stride;
		reData[i0] = reBuffer[i0];
		imData[i0] = imBuffer[i0];
		final double re = reBuffer[i1];
		final double im = imBuffer[i1];
		final double reRoot = reUnitRoot[m0];
		final double imRoot = imUnitRoot[m0];
		reData[i1] = re * reRoot - im * imRoot;
		imData[i1] = re * imRoot + im * reRoot;
	}
	for (int m = 0; (m < K2); m++) {
		reData[i0] -= reData[i1];
		imData[i0] -= imData[i1];
		reData[i1] += reBuffer[i0];
		imData[i1] += imBuffer[i0];
		i0 += stride;
		i1 += stride;
	}
} /* end run */

/*....................................................................
	DFTRadix2Double static methods
....................................................................*/
/*------------------------------------------------------------------*/
static double[] getImUnitRoot (
	final int transformLength
) {
	final int halfTransformLength = transformLength >> 1;
	final double[] imUnitRoot = new double[halfTransformLength];
	final double angularStep = -2.0 * PI / (double)transformLength;
	for (int k = 0; (k < halfTransformLength); k++) {
		imUnitRoot[k] = sin((double)(halfTransformLength + k) * angularStep);
	}
	return(imUnitRoot);
} /* end getImUnitRoot */

/*------------------------------------------------------------------*/
static double[] getReUnitRoot (
	final int transformLength
) {
	final int halfTransformLength = transformLength >> 1;
	final double[] reUnitRoot = new double[halfTransformLength];
	final double angularStep = -2.0 * PI / (double)transformLength;
	for (int k = 0; (k < halfTransformLength); k++) {
		reUnitRoot[k] = cos((double)(halfTransformLength + k) * angularStep);
	}
	return(reUnitRoot);
} /* end getReUnitRoot */

} /* end class DFTRadix2Double */

/*====================================================================
|	DFTRadix2Float
\===================================================================*/
static class DFTRadix2Float
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTRadix2Float */

/*....................................................................
	DFTRadix2Float static variables
....................................................................*/
private float[] imBuffer;
private float[] imUnitRoot;
private float[] reBuffer;
private float[] reUnitRoot;

/*....................................................................
	DFTRadix2Float constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTRadix2Float (
	final float[] reData,
	final float[] imData,
	final float[] reBuffer,
	final float[] imBuffer,
	final int startIndex,
	final int stride,
	final float[] reUnitRoot,
	final float[] imUnitRoot
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.reUnitRoot = reUnitRoot;
	this.imUnitRoot = imUnitRoot;
} /* end DFTRadix2Float */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int K2 = reUnitRoot.length;
	final int doubleStride = stride << 1;
	final FFTSetup fft = FFTSetup.transforms.get(new Integer(K2));
	switch (fft.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			new DFTBruteForceFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.ruritanian, fft.chinese,
				fft.K1).run();
			new DFTCoprimeFactorFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.ruritanian, fft.chinese,
				fft.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			new DFTLength2Float(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength2Float(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case LENGTH3: {
			new DFTLength3Float(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength3Float(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case LENGTH4: {
			new DFTLength4Float(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength4Float(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case LENGTH5: {
			new DFTLength5Float(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength5Float(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case LENGTH6: {
			new DFTLength6Float(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength6Float(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8Float(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength8Float(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.reUnitRootFloat, fft.imUnitRootFloat,
				fft.K1).run();
			new DFTMixedRadixFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.reUnitRootFloat, fft.imUnitRootFloat,
				fft.K1).run();
			break;
		}
		case PADDEDRADER: {
			new DFTPaddedRaderFloat(reData, imData,
				startIndex, doubleStride,
				fft.reConvolverFloat, fft.imConvolverFloat,
				fft.modular, fft.inverseModular).run();
			new DFTPaddedRaderFloat(reData, imData,
				startIndex + stride, doubleStride,
				fft.reConvolverFloat, fft.imConvolverFloat,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADER: {
			new DFTRaderFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.reConvolverFloat, fft.imConvolverFloat,
				fft.modular, fft.inverseModular).run();
			new DFTRaderFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.reConvolverFloat, fft.imConvolverFloat,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADIX2: {
			new DFTRadix2Float(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			new DFTRadix2Float(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			new DFTSplitRadixFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
	}
	int m0 = startIndex;
	int i0 = startIndex;
	int i1 = startIndex + K2 * stride;
	for (int m = 0; (m < K2); m++) {
		reBuffer[i0] = reData[m0];
		imBuffer[i0] = imData[m0];
		m0 += stride;
		reBuffer[i1] = reData[m0];
		imBuffer[i1] = imData[m0];
		m0 += stride;
		i0 += stride;
		i1 += stride;
	}
	m0 = K2;
	for (int m = 0; (m < K2); m++) {
		m0--;
		i1 -= stride;
		i0 -= stride;
		reData[i0] = reBuffer[i0];
		imData[i0] = imBuffer[i0];
		final float re = reBuffer[i1];
		final float im = imBuffer[i1];
		final float reRoot = reUnitRoot[m0];
		final float imRoot = imUnitRoot[m0];
		reData[i1] = re * reRoot - im * imRoot;
		imData[i1] = re * imRoot + im * reRoot;
	}
	for (int m = 0; (m < K2); m++) {
		reData[i0] -= reData[i1];
		imData[i0] -= imData[i1];
		reData[i1] += reBuffer[i0];
		imData[i1] += imBuffer[i0];
		i0 += stride;
		i1 += stride;
	}
} /* end run */

/*....................................................................
	DFTRadix2Float static methods
....................................................................*/
/*------------------------------------------------------------------*/
static float[] getImUnitRoot (
	final int transformLength
) {
	final int halfTransformLength = transformLength >> 1;
	final float[] imUnitRoot = new float[halfTransformLength];
	final float angularStep = -2.0F * (float)PI / (float)transformLength;
	for (int k = 0; (k < halfTransformLength); k++) {
		imUnitRoot[k] = (float)sin((double)((float)(halfTransformLength + k)
			* angularStep));
	}
	return(imUnitRoot);
} /* end getImUnitRoot */

/*------------------------------------------------------------------*/
static float[] getReUnitRoot (
	final int transformLength
) {
	final int halfTransformLength = transformLength >> 1;
	final float[] reUnitRoot = new float[halfTransformLength];
	final float angularStep = -2.0F * (float)PI / (float)transformLength;
	for (int k = 0; (k < halfTransformLength); k++) {
		reUnitRoot[k] = (float)cos((double)((float)(halfTransformLength + k)
			* angularStep));
	}
	return(reUnitRoot);
} /* end getReUnitRoot */

} /* end class DFTRadix2Float */

/*====================================================================
|	DFTRadix2Real
\===================================================================*/
static class DFTRadix2Real

{ /* begin class DFTRadix2Real */

/*....................................................................
	DFTRadix2Real static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
	final int transformLength
) {
	if (FFTSetup.taboos.contains(new Integer(transformLength / 2))) {
		return(-1L);
	}
	final long k2 = (long)(transformLength >> 2);
	return(FFTSetup.FLALLOC * 8L
		+ FFTSetup.FLOP * (1L + k2 * 8L + k2 * 9L + 1L)
		+ FFTSetup.FLASSIGN * ((k2 + 1L) * 4L + 2L + k2 * 6L + k2 * 6L + 2L)
		+ FFTSetup.INTALLOC * 9L
		+ FFTSetup.INTOP * (6L + (k2 + 1L) * 6L + 6L + k2 * 4L + 2L + k2 * 5L)
		+ FFTSetup.INTASSIGN * (7L + (k2 + 1L) * 5L + 4L + k2 * 3L + 4L
			+ k2 * 4L)
		+ FFTSetup.IDX * ((k2 + 1L) * 8L + 4L + k2 * 8L + k2 * 8L + 4L)
		+ FFTSetup.NEWOBJ * 2L
		+ FFTSetupDuoReal.cost(transformLength / 2)
	);
} /* end cost */

} /* end class DFTRadix2Real */

/*====================================================================
|	DFTRadix2RealDouble
\===================================================================*/
static class DFTRadix2RealDouble
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTRadix2RealDouble */

/*....................................................................
	DFTRadix2RealDouble static variables
....................................................................*/
private double[] imBuffer;
private double[] imUnitRootEven;
private double[] imUnitRootOdd;
private double[] reBuffer;
private double[] reUnitRootEven;
private double[] reUnitRootOdd;

/*....................................................................
	DFTRadix2RealDouble constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTRadix2RealDouble (
	final double[] reData,
	final double[] imData,
	final double[] reBuffer,
	final double[] imBuffer,
	final int startIndex,
	final int stride,
	final double[] reUnitRootEven,
	final double[] imUnitRootEven,
	final double[] reUnitRootOdd,
	final double[] imUnitRootOdd
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.reUnitRootEven = reUnitRootEven;
	this.imUnitRootEven = imUnitRootEven;
	this.reUnitRootOdd = reUnitRootOdd;
	this.imUnitRootOdd = imUnitRootOdd;
} /* end DFTRadix2RealDouble */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int K2 = reUnitRootEven.length;
	final int K4 = K2 >> 1;
	final int doubleStride = stride << 1;
	final FFTSetupDuoReal fft =
		FFTSetupDuoReal.transforms.get(new Integer(K2));
	switch (fft.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			new DFTBruteForceRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.ruritanian, fft.chinese, fft.K1).run();
			new DFTCoprimeFactorRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.ruritanian, fft.chinese, fft.K1).run();
			break;
		}
		case DUOREAL: {
			new DFTDuoRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex, startIndex + stride, doubleStride, K2).run();
			break;
		}
		case EVENREAL: {
			new DFTEvenRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			new DFTEvenRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
		case LENGTH1: {
			imData[startIndex] = 0.0;
			imData[startIndex + stride] = 0.0;
			break;
		}
		case LENGTH2: {
			new DFTLength2RealDouble(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength2RealDouble(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case LENGTH3: {
			new DFTLength3RealDouble(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength3RealDouble(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case LENGTH4: {
			new DFTLength4RealDouble(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength4RealDouble(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case LENGTH5: {
			new DFTLength5RealDouble(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength5RealDouble(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case LENGTH6: {
			new DFTLength6RealDouble(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength6RealDouble(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8RealDouble(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength8RealDouble(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.reUnitRootDouble, fft.imUnitRootDouble,
				fft.K1).run();
			new DFTMixedRadixRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.reUnitRootDouble, fft.imUnitRootDouble,
				fft.K1).run();
			break;
		}
		case PADDEDRADER: {
			new DFTPaddedRaderRealDouble(reData, imData,
				startIndex, doubleStride,
				fft.reConvolverDouble, fft.imConvolverDouble,
				fft.modular, fft.inverseModular).run();
			new DFTPaddedRaderRealDouble(reData, imData,
				startIndex + stride, doubleStride,
				fft.reConvolverDouble, fft.imConvolverDouble,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADER: {
			new DFTRaderRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.reConvolverDouble, fft.imConvolverDouble,
				fft.modular, fft.inverseModular).run();
			new DFTRaderRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.reConvolverDouble, fft.imConvolverDouble,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADIX2: {
			new DFTRadix2RealDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.reUnitRootEvenDouble, fft.imUnitRootEvenDouble,
				fft.reUnitRootOddDouble, fft.imUnitRootOddDouble).run();
			new DFTRadix2RealDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.reUnitRootEvenDouble, fft.imUnitRootEvenDouble,
				fft.reUnitRootOddDouble, fft.imUnitRootOddDouble).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			new DFTSplitRadixRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
	}
	int m0 = startIndex;
	int i0 = startIndex;
	int i1 = startIndex + K2 * stride;
	for (int m = 0; (m <= K4); m++) {
		reBuffer[i0] = reData[m0];
		imBuffer[i0] = imData[m0];
		m0 += stride;
		reBuffer[i1] = reData[m0];
		imBuffer[i1] = imData[m0];
		m0 += stride;
		i0 += stride;
		i1 += stride;
	}
	i1 = startIndex + K2 * stride;
	reData[startIndex] = reBuffer[startIndex] + reBuffer[i1];
	imData[startIndex] = 0.0;
	i0 = startIndex + stride;
	i1 += stride;
	if (0 == (K2 & 1)) {
		for (int m = 1; (m < K4); m++) {
			final double re = reBuffer[i1];
			final double im = imBuffer[i1];
			final double reRoot = reUnitRootEven[m];
			final double imRoot = imUnitRootEven[m];
			reData[i0] = reBuffer[i0] + re * reRoot - im * imRoot;
			imData[i0] = imBuffer[i0] + re * imRoot + im * reRoot;
			i0 += stride;
			i1 += stride;
		}
		reData[i0] = reBuffer[i0];
		imData[i0] = -reBuffer[i1];
		m0 = i0 + stride;
		i0 -= stride;
		i1 -= stride;
		for (int m = 1; (m < K4); m++) {
			final double re = imBuffer[i1];
			final double im = reBuffer[i1];
			final double reRoot = reUnitRootEven[m];
			final double imRoot = imUnitRootEven[m];
			reData[m0] = reBuffer[i0] - re * reRoot + im * imRoot;
			imData[m0] = -imBuffer[i0] - re * imRoot - im * reRoot;
			m0 += stride;
			i0 -= stride;
			i1 -= stride;
		}
	}
	else {
		for (int m = 1; (m <= K4); m++) {
			final double re = reBuffer[i1];
			final double im = imBuffer[i1];
			final double reRoot = reUnitRootEven[m];
			final double imRoot = imUnitRootEven[m];
			reData[i0] = reBuffer[i0] + re * reRoot - im * imRoot;
			imData[i0] = imBuffer[i0] + re * imRoot + im * reRoot;
			i0 += stride;
			i1 += stride;
		}
		m0 = i0;
		i0 -= stride;
		i1 -= stride;
		for (int m = 0; (m < K4); m++) {
			final double re = imBuffer[i1];
			final double im = reBuffer[i1];
			final double reRoot = reUnitRootOdd[m];
			final double imRoot = imUnitRootOdd[m];
			reData[m0] = reBuffer[i0] - re * reRoot + im * imRoot;
			imData[m0] = -imBuffer[i0] - re * imRoot - im * reRoot;
			i0 -= stride;
			i1 -= stride;
			m0 += stride;
		}
	}
	reData[m0] = reBuffer[i0] - reBuffer[i1];
	imData[m0] = 0.0;
} /* end run */

/*....................................................................
	DFTRadix2RealDouble static methods
....................................................................*/
/*------------------------------------------------------------------*/
static double[] getImUnitRootEven (
	final int transformLength
) {
	final int halfTransformLength = transformLength >> 1;
	final double[] imUnitRoot = new double[halfTransformLength];
	final double angularStep = -2.0 * PI / (double)transformLength;
	for (int k = 0; (k < halfTransformLength); k++) {
		imUnitRoot[k] = sin((double)k * angularStep);
	}
	return(imUnitRoot);
} /* end getImUnitRootEven */

/*------------------------------------------------------------------*/
static double[] getReUnitRootEven (
	final int transformLength
) {
	final int halfTransformLength = transformLength >> 1;
	final double[] reUnitRoot = new double[halfTransformLength];
	final double angularStep = -2.0 * PI / (double)transformLength;
	for (int k = 0; (k < halfTransformLength); k++) {
		reUnitRoot[k] = cos((double)k * angularStep);
	}
	return(reUnitRoot);
} /* end getReUnitRoot */

/*------------------------------------------------------------------*/
static double[] getImUnitRootOdd (
	final int transformLength
) {
	final int halfTransformLength = transformLength >> 1;
	final double[] imUnitRoot = new double[halfTransformLength];
	final double angularStep = -2.0 * PI / (double)transformLength;
	for (int k = 0; (k < halfTransformLength); k++) {
		imUnitRoot[k] = sin(((double)k + 0.5) * angularStep);
	}
	return(imUnitRoot);
} /* end getImUnitRootOdd */

/*------------------------------------------------------------------*/
static double[] getReUnitRootOdd (
	final int transformLength
) {
	final int halfTransformLength = transformLength >> 1;
	final double[] reUnitRoot = new double[halfTransformLength];
	final double angularStep = -2.0 * PI / (double)transformLength;
	for (int k = 0; (k < halfTransformLength); k++) {
		reUnitRoot[k] = cos(((double)k + 0.5) * angularStep);
	}
	return(reUnitRoot);
} /* end getReUnitRoot */

} /* end class getReUnitRootOdd */

/*====================================================================
|	DFTRadix2RealFloat
\===================================================================*/
static class DFTRadix2RealFloat
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTRadix2RealFloat */

/*....................................................................
	DFTRadix2RealFloat static variables
....................................................................*/
private float[] imBuffer;
private float[] imUnitRootEven;
private float[] imUnitRootOdd;
private float[] reBuffer;
private float[] reUnitRootEven;
private float[] reUnitRootOdd;

/*....................................................................
	DFTRadix2RealFloat constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTRadix2RealFloat (
	final float[] reData,
	final float[] imData,
	final float[] reBuffer,
	final float[] imBuffer,
	final int startIndex,
	final int stride,
	final float[] reUnitRootEven,
	final float[] imUnitRootEven,
	final float[] reUnitRootOdd,
	final float[] imUnitRootOdd
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.reUnitRootEven = reUnitRootEven;
	this.imUnitRootEven = imUnitRootEven;
	this.reUnitRootOdd = reUnitRootOdd;
	this.imUnitRootOdd = imUnitRootOdd;
} /* end DFTRadix2RealFloat */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int K2 = reUnitRootEven.length;
	final int K4 = K2 >> 1;
	final int doubleStride = stride << 1;
	final FFTSetupDuoReal fft =
		FFTSetupDuoReal.transforms.get(new Integer(K2));
	switch (fft.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			new DFTBruteForceRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.ruritanian, fft.chinese, fft.K1).run();
			new DFTCoprimeFactorRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.ruritanian, fft.chinese, fft.K1).run();
			break;
		}
		case DUOREAL: {
			new DFTDuoRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex, startIndex + stride, doubleStride, K2).run();
			break;
		}
		case EVENREAL: {
			new DFTEvenRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			new DFTEvenRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
		case LENGTH1: {
			imData[startIndex] = 0.0F;
			imData[startIndex + stride] = 0.0F;
			break;
		}
		case LENGTH2: {
			new DFTLength2RealFloat(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength2RealFloat(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case LENGTH3: {
			new DFTLength3RealFloat(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength3RealFloat(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case LENGTH4: {
			new DFTLength4RealFloat(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength4RealFloat(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case LENGTH5: {
			new DFTLength5RealFloat(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength5RealFloat(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case LENGTH6: {
			new DFTLength6RealFloat(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength6RealFloat(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8RealFloat(reData, imData,
				startIndex, doubleStride).run();
			new DFTLength8RealFloat(reData, imData,
				startIndex + stride, doubleStride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.reUnitRootFloat, fft.imUnitRootFloat,
				fft.K1).run();
			new DFTMixedRadixRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.reUnitRootFloat, fft.imUnitRootFloat,
				fft.K1).run();
			break;
		}
		case PADDEDRADER: {
			new DFTPaddedRaderRealFloat(reData, imData,
				startIndex, doubleStride,
				fft.reConvolverFloat, fft.imConvolverFloat,
				fft.modular, fft.inverseModular).run();
			new DFTPaddedRaderRealFloat(reData, imData,
				startIndex + stride, doubleStride,
				fft.reConvolverFloat, fft.imConvolverFloat,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADER: {
			new DFTRaderRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.reConvolverFloat, fft.imConvolverFloat,
				fft.modular, fft.inverseModular).run();
			new DFTRaderRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.reConvolverFloat, fft.imConvolverFloat,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADIX2: {
			new DFTRadix2RealFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.reUnitRootEvenFloat, fft.imUnitRootEvenFloat,
				fft.reUnitRootOddFloat, fft.imUnitRootOddFloat).run();
			new DFTRadix2RealFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.reUnitRootEvenFloat, fft.imUnitRootEvenFloat,
				fft.reUnitRootOddFloat, fft.imUnitRootOddFloat).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			new DFTSplitRadixRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, doubleStride,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
	}
	int m0 = startIndex;
	int i0 = startIndex;
	int i1 = startIndex + K2 * stride;
	for (int m = 0; (m <= K4); m++) {
		reBuffer[i0] = reData[m0];
		imBuffer[i0] = imData[m0];
		m0 += stride;
		reBuffer[i1] = reData[m0];
		imBuffer[i1] = imData[m0];
		m0 += stride;
		i0 += stride;
		i1 += stride;
	}
	i1 = startIndex + K2 * stride;
	reData[startIndex] = reBuffer[startIndex] + reBuffer[i1];
	imData[startIndex] = 0.0F;
	i0 = startIndex + stride;
	i1 += stride;
	if (0 == (K2 & 1)) {
		for (int m = 1; (m < K4); m++) {
			final float re = reBuffer[i1];
			final float im = imBuffer[i1];
			final float reRoot = reUnitRootEven[m];
			final float imRoot = imUnitRootEven[m];
			reData[i0] = reBuffer[i0] + re * reRoot - im * imRoot;
			imData[i0] = imBuffer[i0] + re * imRoot + im * reRoot;
			i0 += stride;
			i1 += stride;
		}
		reData[i0] = reBuffer[i0];
		imData[i0] = -reBuffer[i1];
		m0 = i0 + stride;
		i0 -= stride;
		i1 -= stride;
		for (int m = 1; (m < K4); m++) {
			final float re = imBuffer[i1];
			final float im = reBuffer[i1];
			final float reRoot = reUnitRootEven[m];
			final float imRoot = imUnitRootEven[m];
			reData[m0] = reBuffer[i0] - re * reRoot + im * imRoot;
			imData[m0] = -imBuffer[i0] - re * imRoot - im * reRoot;
			m0 += stride;
			i0 -= stride;
			i1 -= stride;
		}
	}
	else {
		for (int m = 1; (m <= K4); m++) {
			final float re = reBuffer[i1];
			final float im = imBuffer[i1];
			final float reRoot = reUnitRootEven[m];
			final float imRoot = imUnitRootEven[m];
			reData[i0] = reBuffer[i0] + re * reRoot - im * imRoot;
			imData[i0] = imBuffer[i0] + re * imRoot + im * reRoot;
			i0 += stride;
			i1 += stride;
		}
		m0 = i0;
		i0 -= stride;
		i1 -= stride;
		for (int m = 0; (m < K4); m++) {
			final float re = imBuffer[i1];
			final float im = reBuffer[i1];
			final float reRoot = reUnitRootOdd[m];
			final float imRoot = imUnitRootOdd[m];
			reData[m0] = reBuffer[i0] - re * reRoot + im * imRoot;
			imData[m0] = -imBuffer[i0] - re * imRoot - im * reRoot;
			i0 -= stride;
			i1 -= stride;
			m0 += stride;
		}
	}
	reData[m0] = reBuffer[i0] - reBuffer[i1];
	imData[m0] = 0.0F;
} /* end run */

/*....................................................................
	DFTRadix2RealFloat static methods
....................................................................*/
/*------------------------------------------------------------------*/
static float[] getImUnitRootEven (
	final int transformLength
) {
	final int halfTransformLength = transformLength >> 1;
	final float[] imUnitRoot = new float[halfTransformLength];
	final float angularStep = -2.0F * (float)PI / (float)transformLength;
	for (int k = 0; (k < halfTransformLength); k++) {
		imUnitRoot[k] = (float)sin((double)((float)k * angularStep));
	}
	return(imUnitRoot);
} /* end getImUnitRootEven */

/*------------------------------------------------------------------*/
static float[] getReUnitRootEven (
	final int transformLength
) {
	final int halfTransformLength = transformLength >> 1;
	final float[] reUnitRoot = new float[halfTransformLength];
	final float angularStep = -2.0F * (float)PI / (float)transformLength;
	for (int k = 0; (k < halfTransformLength); k++) {
		reUnitRoot[k] = (float)cos((double)((float)k * angularStep));
	}
	return(reUnitRoot);
} /* end getReUnitRoot */

/*------------------------------------------------------------------*/
static float[] getImUnitRootOdd (
	final int transformLength
) {
	final int halfTransformLength = transformLength >> 1;
	final float[] imUnitRoot = new float[halfTransformLength];
	final float angularStep = -2.0F * (float)PI / (float)transformLength;
	for (int k = 0; (k < halfTransformLength); k++) {
		imUnitRoot[k] = (float)sin((double)(((float)k + 0.5F)
			* angularStep));
	}
	return(imUnitRoot);
} /* end getImUnitRootOdd */

/*------------------------------------------------------------------*/
static float[] getReUnitRootOdd (
	final int transformLength
) {
	final int halfTransformLength = transformLength >> 1;
	final float[] reUnitRoot = new float[halfTransformLength];
	final float angularStep = -2.0F * (float)PI / (float)transformLength;
	for (int k = 0; (k < halfTransformLength); k++) {
		reUnitRoot[k] = (float)cos((double)(((float)k + 0.5F)
			* angularStep));
	}
	return(reUnitRoot);
} /* end getReUnitRoot */

} /* end class getReUnitRootOdd */

/*====================================================================
|	DFTSplitRadix
\===================================================================*/
static class DFTSplitRadix

{ /* begin class DFTSplitRadix */

/*....................................................................
	DFTSplitRadix static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
	final int transformLength
) {
	if (FFTSetup.taboos.contains(new Integer(transformLength / 2))
		|| FFTSetup.taboos.contains(new Integer(transformLength / 4))) {
		return(-1L);
	}
	final long k = (long)(transformLength >> 1);
	final long k2 = k >> 1L;
	return(FFTSetup.FLALLOC * 12L
		+ FFTSetup.FLOP * (k2 * 24L)
		+ FFTSetup.FLASSIGN * (k * 2L + k2 * 2L + k2 * 2L + k2 * 28L)
		+ FFTSetup.INTALLOC * 15L
		+ FFTSetup.INTOP * (9L + k * 4L + 1L + k2 * 4L + 1L + k2 * 4L + 6L
			+ k2 * 7L)
		+ FFTSetup.INTASSIGN * (8L + k * 3L + 2L + k2 * 3L + 2L + k2 * 3L + 6L
			+ k2 * 6L)
		+ FFTSetup.IDX * (k * 4L + k2 * 4L + k2 * 4L + k2 * 20L)
		+ FFTSetup.NEWOBJ * 3L
		+ FFTSetup.cost(transformLength >> 1)
		+ 2L * FFTSetup.cost(transformLength >> 2)
	);
} /* end cost */

} /* end class DFTSplitRadix */

/*====================================================================
|	DFTSplitRadixDouble
\===================================================================*/
static class DFTSplitRadixDouble
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTSplitRadixDouble */

/*....................................................................
	DFTSplitRadixDouble static variables
....................................................................*/
private double[] imBuffer;
private double[] imUnitRoot;
private double[] reBuffer;
private double[] reUnitRoot;

/*....................................................................
	DFTSplitRadixDouble constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTSplitRadixDouble (
	final double[] reData,
	final double[] imData,
	final double[] reBuffer,
	final double[] imBuffer,
	final int startIndex,
	final int stride,
	final double[] reUnitRoot,
	final double[] imUnitRoot
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.reUnitRoot = reUnitRoot;
	this.imUnitRoot = imUnitRoot;
} /* end DFTSplitRadixDouble */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int K4 = reUnitRoot.length / 3;
	final int K2 = K4 << 1;
	final int doubleStride = stride << 1;
	final int tripleStride = doubleStride + stride;
	final int quadrupleStride = tripleStride + stride;
	final FFTSetup fft2 = FFTSetup.transforms.get(new Integer(K2));
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.ruritanian, fft2.chinese, fft2.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			new DFTLength2Double(reData, imData,
				startIndex, doubleStride).run();
			break;
		}
		case LENGTH3: {
			throw(new IllegalStateException());
		}
		case LENGTH4: {
			new DFTLength4Double(reData, imData,
				startIndex, doubleStride).run();
			break;
		}
		case LENGTH5: {
			throw(new IllegalStateException());
		}
		case LENGTH6: {
			new DFTLength6Double(reData, imData,
				startIndex, doubleStride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8Double(reData, imData,
				startIndex, doubleStride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.reUnitRootDouble, fft2.imUnitRootDouble,
				fft2.K1).run();
			break;
		}
		case PADDEDRADER: {
			new DFTPaddedRaderDouble(reData, imData,
				startIndex, doubleStride,
				fft2.reConvolverDouble, fft2.imConvolverDouble,
				fft2.modular, fft2.inverseModular).run();
			break;
		}
		case RADER: {
			new DFTRaderDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.reConvolverDouble, fft2.imConvolverDouble,
				fft2.modular, fft2.inverseModular).run();
			break;
		}
		case RADIX2: {
			new DFTRadix2Double(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
			break;
		}
	}
	final FFTSetup fft4 = FFTSetup.transforms.get(new Integer(K4));
	switch (fft4.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.reUnitRootDouble, fft4.imUnitRootDouble).run();
			new DFTBruteForceDouble(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.reUnitRootDouble, fft4.imUnitRootDouble).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.ruritanian, fft4.chinese,
				fft4.K1).run();
			new DFTCoprimeFactorDouble(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.ruritanian, fft4.chinese,
				fft4.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			new DFTLength2Double(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength2Double(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case LENGTH3: {
			new DFTLength3Double(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength3Double(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case LENGTH4: {
			new DFTLength4Double(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength4Double(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case LENGTH5: {
			new DFTLength5Double(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength5Double(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case LENGTH6: {
			new DFTLength6Double(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength6Double(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8Double(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength8Double(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.reUnitRootDouble, fft4.imUnitRootDouble,
				fft4.K1).run();
			new DFTMixedRadixDouble(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.reUnitRootDouble, fft4.imUnitRootDouble,
				fft4.K1).run();
			break;
		}
		case PADDEDRADER: {
			new DFTPaddedRaderDouble(reData, imData,
				startIndex + stride, quadrupleStride,
				fft4.reConvolverDouble, fft4.imConvolverDouble,
				fft4.modular, fft4.inverseModular).run();
			new DFTPaddedRaderDouble(reData, imData,
				startIndex + tripleStride, quadrupleStride,
				fft4.reConvolverDouble, fft4.imConvolverDouble,
				fft4.modular, fft4.inverseModular).run();
			break;
		}
		case RADER: {
			new DFTRaderDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.reConvolverDouble, fft4.imConvolverDouble,
				fft4.modular, fft4.inverseModular).run();
			new DFTRaderDouble(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.reConvolverDouble, fft4.imConvolverDouble,
				fft4.modular, fft4.inverseModular).run();
			break;
		}
		case RADIX2: {
			new DFTRadix2Double(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.reUnitRootDouble, fft4.imUnitRootDouble).run();
			new DFTRadix2Double(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.reUnitRootDouble, fft4.imUnitRootDouble).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.reUnitRootDouble, fft4.imUnitRootDouble).run();
			new DFTSplitRadixDouble(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.reUnitRootDouble, fft4.imUnitRootDouble).run();
			break;
		}
	}
	int m0 = startIndex;
	int i0 = startIndex;
	for (int m = 0; (m < K2); m++) {
		reBuffer[i0] = reData[m0];
		imBuffer[i0] = imData[m0];
		m0 += doubleStride;
		i0 += stride;
	}
	m0 = startIndex + stride;
	for (int m = 0; (m < K4); m++) {
		reBuffer[i0] = reData[m0];
		imBuffer[i0] = imData[m0];
		m0 += quadrupleStride;
		i0 += stride;
	}
	m0 = startIndex + tripleStride;
	for (int m = 0; (m < K4); m++) {
		reBuffer[i0] = reData[m0];
		imBuffer[i0] = imData[m0];
		m0 += quadrupleStride;
		i0 += stride;
	}
	i0 = startIndex;
	int i1 = i0 + K4 * stride;
	int i2 = i1 + K4 * stride;
	int i3 = i2 + K4 * stride;
	for (int m1 = 0, m3 = 0; (m1 < K4); m1++, m3 += 3) {
		double re = reBuffer[i2];
		double im = imBuffer[i2];
		double reRoot = reUnitRoot[m1];
		double imRoot = imUnitRoot[m1];
		double reButterfly = re * reRoot - im * imRoot;
		double imButterfly = re * imRoot + im * reRoot;
		re = reBuffer[i3];
		im = imBuffer[i3];
		reRoot = reUnitRoot[m3];
		imRoot = imUnitRoot[m3];
		double reDragonfly = re * reRoot - im * imRoot;
		double imDragonfly = re * imRoot + im * reRoot;
		final double reLadybug = reButterfly + reDragonfly;
		final double imLadybug = imButterfly + imDragonfly;
		final double reMoth = reButterfly - reDragonfly;
		final double imMoth = imButterfly - imDragonfly;
		reButterfly = reBuffer[i0];
		imButterfly = imBuffer[i0];
		reDragonfly = reBuffer[i1];
		imDragonfly = imBuffer[i1];
		reData[i0] = reButterfly + reLadybug;
		imData[i0] = imButterfly + imLadybug;
		reData[i1] = reDragonfly + imMoth;
		imData[i1] = imDragonfly - reMoth;
		reData[i2] = reButterfly - reLadybug;
		imData[i2] = imButterfly - imLadybug;
		reData[i3] = reDragonfly - imMoth;
		imData[i3] = imDragonfly + reMoth;
		i0 += stride;
		i1 += stride;
		i2 += stride;
		i3 += stride;
	}
} /* end run */

/*....................................................................
	DFTSplitRadixDouble static methods
....................................................................*/
/*------------------------------------------------------------------*/
static double[] getImUnitRoot (
	final int transformLength
) {
	final int fourthTransformLength = transformLength >> 2;
	final int threeFourthTransformLength = 3 * fourthTransformLength;
	final double[] imUnitRoot = new double[threeFourthTransformLength];
	final double angularStep = -2.0 * PI / (double)transformLength;
	for (int k = 0; (k < threeFourthTransformLength); k++) {
		imUnitRoot[k] = sin((double)k * angularStep);
	}
	return(imUnitRoot);
} /* end getImUnitRoot */

/*------------------------------------------------------------------*/
static double[] getReUnitRoot (
	final int transformLength
) {
	final int fourthTransformLength = transformLength >> 2;
	final int threeFourthTransformLength = 3 * fourthTransformLength;
	final double[] reUnitRoot = new double[threeFourthTransformLength];
	final double angularStep = -2.0 * PI / (double)transformLength;
	for (int k = 0; (k < threeFourthTransformLength); k++) {
		reUnitRoot[k] = cos((double)k * angularStep);
	}
	return(reUnitRoot);
} /* end getReUnitRoot */

} /* end class DFTSplitRadixDouble */

/*====================================================================
|	DFTSplitRadixFloat
\===================================================================*/
static class DFTSplitRadixFloat
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTSplitRadixFloat */

/*....................................................................
	DFTSplitRadixFloat static variables
....................................................................*/
private float[] imBuffer;
private float[] imUnitRoot;
private float[] reBuffer;
private float[] reUnitRoot;

/*....................................................................
	DFTSplitRadixFloat constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTSplitRadixFloat (
	final float[] reData,
	final float[] imData,
	final float[] reBuffer,
	final float[] imBuffer,
	final int startIndex,
	final int stride,
	final float[] reUnitRoot,
	final float[] imUnitRoot
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.reUnitRoot = reUnitRoot;
	this.imUnitRoot = imUnitRoot;
} /* end DFTSplitRadixFloat */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int K4 = reUnitRoot.length / 3;
	final int K2 = K4 << 1;
	final int doubleStride = stride << 1;
	final int tripleStride = doubleStride + stride;
	final int quadrupleStride = tripleStride + stride;
	final FFTSetup fft2 = FFTSetup.transforms.get(new Integer(K2));
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.ruritanian, fft2.chinese,
				fft2.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			new DFTLength2Float(reData, imData,
				startIndex, doubleStride).run();
			break;
		}
		case LENGTH3: {
			throw(new IllegalStateException());
		}
		case LENGTH4: {
			new DFTLength4Float(reData, imData,
				startIndex, doubleStride).run();
			break;
		}
		case LENGTH5: {
			throw(new IllegalStateException());
		}
		case LENGTH6: {
			new DFTLength6Float(reData, imData,
				startIndex, doubleStride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8Float(reData, imData,
				startIndex, doubleStride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.reUnitRootFloat, fft2.imUnitRootFloat,
				fft2.K1).run();
			break;
		}
		case PADDEDRADER: {
			new DFTPaddedRaderFloat(reData, imData,
				startIndex, doubleStride,
				fft2.reConvolverFloat, fft2.imConvolverFloat,
				fft2.modular, fft2.inverseModular).run();
			break;
		}
		case RADER: {
			new DFTRaderFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.reConvolverFloat, fft2.imConvolverFloat,
				fft2.modular, fft2.inverseModular).run();
			break;
		}
		case RADIX2: {
			new DFTRadix2Float(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
			break;
		}
	}
	final FFTSetup fft4 = FFTSetup.transforms.get(new Integer(K4));
	switch (fft4.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.reUnitRootFloat, fft4.imUnitRootFloat).run();
			new DFTBruteForceFloat(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.reUnitRootFloat, fft4.imUnitRootFloat).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.ruritanian, fft4.chinese,
				fft4.K1).run();
			new DFTCoprimeFactorFloat(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.ruritanian, fft4.chinese,
				fft4.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			new DFTLength2Float(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength2Float(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case LENGTH3: {
			new DFTLength3Float(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength3Float(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case LENGTH4: {
			new DFTLength4Float(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength4Float(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case LENGTH5: {
			new DFTLength5Float(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength5Float(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case LENGTH6: {
			new DFTLength6Float(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength6Float(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8Float(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength8Float(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.reUnitRootFloat, fft4.imUnitRootFloat,
				fft4.K1).run();
			new DFTMixedRadixFloat(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.reUnitRootFloat, fft4.imUnitRootFloat,
				fft4.K1).run();
			break;
		}
		case PADDEDRADER: {
			new DFTPaddedRaderFloat(reData, imData,
				startIndex + stride, quadrupleStride,
				fft4.reConvolverFloat, fft4.imConvolverFloat,
				fft4.modular, fft4.inverseModular).run();
			new DFTPaddedRaderFloat(reData, imData,
				startIndex + tripleStride, quadrupleStride,
				fft4.reConvolverFloat, fft4.imConvolverFloat,
				fft4.modular, fft4.inverseModular).run();
			break;
		}
		case RADER: {
			new DFTRaderFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.reConvolverFloat, fft4.imConvolverFloat,
				fft4.modular, fft4.inverseModular).run();
			new DFTRaderFloat(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.reConvolverFloat, fft4.imConvolverFloat,
				fft4.modular, fft4.inverseModular).run();
			break;
		}
		case RADIX2: {
			new DFTRadix2Float(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.reUnitRootFloat, fft4.imUnitRootFloat).run();
			new DFTRadix2Float(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.reUnitRootFloat, fft4.imUnitRootFloat).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.reUnitRootFloat, fft4.imUnitRootFloat).run();
			new DFTSplitRadixFloat(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.reUnitRootFloat, fft4.imUnitRootFloat).run();
			break;
		}
	}
	int m0 = startIndex;
	int i0 = startIndex;
	for (int m = 0; (m < K2); m++) {
		reBuffer[i0] = reData[m0];
		imBuffer[i0] = imData[m0];
		m0 += doubleStride;
		i0 += stride;
	}
	m0 = startIndex + stride;
	for (int m = 0; (m < K4); m++) {
		reBuffer[i0] = reData[m0];
		imBuffer[i0] = imData[m0];
		m0 += quadrupleStride;
		i0 += stride;
	}
	m0 = startIndex + tripleStride;
	for (int m = 0; (m < K4); m++) {
		reBuffer[i0] = reData[m0];
		imBuffer[i0] = imData[m0];
		m0 += quadrupleStride;
		i0 += stride;
	}
	i0 = startIndex;
	int i1 = i0 + K4 * stride;
	int i2 = i1 + K4 * stride;
	int i3 = i2 + K4 * stride;
	for (int m1 = 0, m3 = 0; (m1 < K4); m1++, m3 += 3) {
		float re = reBuffer[i2];
		float im = imBuffer[i2];
		float reRoot = reUnitRoot[m1];
		float imRoot = imUnitRoot[m1];
		float reButterfly = re * reRoot - im * imRoot;
		float imButterfly = re * imRoot + im * reRoot;
		re = reBuffer[i3];
		im = imBuffer[i3];
		reRoot = reUnitRoot[m3];
		imRoot = imUnitRoot[m3];
		float reDragonfly = re * reRoot - im * imRoot;
		float imDragonfly = re * imRoot + im * reRoot;
		final float reLadybug = reButterfly + reDragonfly;
		final float imLadybug = imButterfly + imDragonfly;
		final float reMoth = reButterfly - reDragonfly;
		final float imMoth = imButterfly - imDragonfly;
		reButterfly = reBuffer[i0];
		imButterfly = imBuffer[i0];
		reDragonfly = reBuffer[i1];
		imDragonfly = imBuffer[i1];
		reData[i0] = reButterfly + reLadybug;
		imData[i0] = imButterfly + imLadybug;
		reData[i1] = reDragonfly + imMoth;
		imData[i1] = imDragonfly - reMoth;
		reData[i2] = reButterfly - reLadybug;
		imData[i2] = imButterfly - imLadybug;
		reData[i3] = reDragonfly - imMoth;
		imData[i3] = imDragonfly + reMoth;
		i0 += stride;
		i1 += stride;
		i2 += stride;
		i3 += stride;
	}
} /* end run */

/*....................................................................
	DFTSplitRadixFloat static methods
....................................................................*/
/*------------------------------------------------------------------*/
static float[] getImUnitRoot (
	final int transformLength
) {
	final int fourthTransformLength = transformLength >> 2;
	final int threeFourthTransformLength = 3 * fourthTransformLength;
	final float[] imUnitRoot = new float[threeFourthTransformLength];
	final float angularStep = -2.0F * (float)PI / (float)transformLength;
	for (int k = 0; (k < threeFourthTransformLength); k++) {
		imUnitRoot[k] = (float)sin((double)((float)k * angularStep));
	}
	return(imUnitRoot);
} /* end getImUnitRoot */

/*------------------------------------------------------------------*/
static float[] getReUnitRoot (
	final int transformLength
) {
	final int fourthTransformLength = transformLength >> 2;
	final int threeFourthTransformLength = 3 * fourthTransformLength;
	final float[] reUnitRoot = new float[threeFourthTransformLength];
	final float angularStep = -2.0F * (float)PI / (float)transformLength;
	for (int k = 0; (k < threeFourthTransformLength); k++) {
		reUnitRoot[k] = (float)cos((double)((float)k * angularStep));
	}
	return(reUnitRoot);
} /* end getReUnitRoot */

} /* end class DFTSplitRadixFloat */

/*====================================================================
|	DFTSplitRadixReal
\===================================================================*/
static class DFTSplitRadixReal

{ /* begin class DFTSplitRadixReal */

/*....................................................................
	DFTSplitRadixReal static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
	final int transformLength
) {
	if (FFTSetup.taboos.contains(new Integer(transformLength / 2))
		|| FFTSetup.taboos.contains(new Integer(transformLength / 4))) {
		return(-1L);
	}
	final long k = (long)(transformLength >> 1);
	final long k2 = k >> 1L;
	final long k4 = k2 >> 1L;
	return(FFTSetup.FLALLOC * 8L
		+ FFTSetup.FLOP * ((k4 + 1L) * 16L + 1L + k4 * 2L + (k2 - k4) * 2L + 1L
			+ k4 * 3L + (k2 - k4) * 2L + 1L)
		+ FFTSetup.FLASSIGN * ((k2 + 1L) * 2L + (k4 + 1L) * 16L + 2L + k4 * 2L
			+ (k2 - k4) * 2L + 2L + k4 * 2L + (k2 - k4) * 2L + 2L)
		+ FFTSetup.INTALLOC * 18L
		+ FFTSetup.INTOP * (12L + (k2 + 1L) * 4L + 6L + (k4 + 1L) * 7L + 4L
			+ k4 * 4L + 4L + (k2 - k4) * 4L + 5L + k4 * 5L + 4L
			+ (k2 - k4) * 5L)
		+ FFTSetup.INTASSIGN * (10L + (k2 + 1L) * 3L + 6L + (k4 + 1L) * 6L + 5L
			+ k4 * 3L + 2L + (k2 - k4) * 3L + 4L + k4 * 4L + 2L
			+ (k2 - k4) * 4L)
		+ FFTSetup.IDX * ((k2 + 1L) * 4L + (k4 + 1L) * 12L + 4L + k4 * 6L
			+ (k2 - k4) * 6L + 4L + k4 * 6L + (k2 - k4) * 6L + 4L)
		+ FFTSetup.NEWOBJ * 3L
		+ FFTSetupReal.cost(transformLength >> 1)
		+ FFTSetupDuoReal.cost(transformLength >> 2)
	);
} /* end cost */

} /* end class DFTSplitRadixReal */

/*====================================================================
|	DFTSplitRadixRealDouble
\===================================================================*/
static class DFTSplitRadixRealDouble
	extends
		DFTDouble
	implements
		Runnable

{ /* begin class DFTSplitRadixRealDouble */

/*....................................................................
	DFTSplitRadixRealDouble static variables
....................................................................*/
private double[] imBuffer;
private double[] imUnitRoot;
private double[] reBuffer;
private double[] reUnitRoot;

/*....................................................................
	DFTSplitRadixRealDouble constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTSplitRadixRealDouble (
	final double[] reData,
	final double[] imData,
	final double[] reBuffer,
	final double[] imBuffer,
	final int startIndex,
	final int stride,
	final double[] reUnitRoot,
	final double[] imUnitRoot
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.reUnitRoot = reUnitRoot;
	this.imUnitRoot = imUnitRoot;
} /* end DFTSplitRadixRealDouble */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int K4 = reUnitRoot.length / 3;
	final int halfK4 = (K4 >> 1) + 1;
	final int K2 = K4 << 1;
	final int halfK2 = K4 + 1;
	final int doubleStride = stride << 1;
	final int tripleStride = doubleStride + stride;
	final int quadrupleStride = tripleStride + stride;
	final FFTSetupReal fft2 = FFTSetupReal.transforms.get(new Integer(K2));
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.ruritanian, fft2.chinese, fft2.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			new DFTEvenRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
			break;
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			new DFTLength2RealDouble(reData, imData,
				startIndex, doubleStride).run();
			break;
		}
		case LENGTH3: {
			throw(new IllegalStateException());
		}
		case LENGTH4: {
			new DFTLength4RealDouble(reData, imData,
				startIndex, doubleStride).run();
			break;
		}
		case LENGTH5: {
			throw(new IllegalStateException());
		}
		case LENGTH6: {
			new DFTLength6RealDouble(reData, imData,
				startIndex, doubleStride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8RealDouble(reData, imData,
				startIndex, doubleStride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.reUnitRootDouble, fft2.imUnitRootDouble,
				fft2.K1).run();
			break;
		}
		case PADDEDRADER: {
			new DFTPaddedRaderRealDouble(reData, imData,
				startIndex, doubleStride,
				fft2.reConvolverDouble, fft2.imConvolverDouble,
				fft2.modular, fft2.inverseModular).run();
			break;
		}
		case RADER: {
			new DFTRaderRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.reConvolverDouble, fft2.imConvolverDouble,
				fft2.modular, fft2.inverseModular).run();
			break;
		}
		case RADIX2: {
			new DFTRadix2RealDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.reUnitRootEvenDouble, fft2.imUnitRootEvenDouble,
				fft2.reUnitRootOddDouble, fft2.imUnitRootOddDouble).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.reUnitRootDouble, fft2.imUnitRootDouble).run();
			break;
		}
	}
	final FFTSetupDuoReal fft4 =
		FFTSetupDuoReal.transforms.get(new Integer(K4));
	switch (fft4.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.reUnitRootDouble, fft4.imUnitRootDouble).run();
			new DFTBruteForceRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.reUnitRootDouble, fft4.imUnitRootDouble).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.ruritanian, fft4.chinese,
				fft4.K1).run();
			new DFTCoprimeFactorRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.ruritanian, fft4.chinese,
				fft4.K1).run();
			break;
		}
		case DUOREAL: {
			new DFTDuoRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, startIndex + tripleStride,
				quadrupleStride, K4).run();
			break;
		}
		case EVENREAL: {
			new DFTEvenRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.reUnitRootDouble, fft4.imUnitRootDouble).run();
			new DFTEvenRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.reUnitRootDouble, fft4.imUnitRootDouble).run();
			break;
		}
		case LENGTH1: {
			imData[startIndex + stride] = 0.0;
			imData[startIndex + tripleStride] = 0.0;
			break;
		}
		case LENGTH2: {
			new DFTLength2RealDouble(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength2RealDouble(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case LENGTH3: {
			new DFTLength3RealDouble(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength3RealDouble(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case LENGTH4: {
			new DFTLength4RealDouble(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength4RealDouble(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case LENGTH5: {
			new DFTLength5RealDouble(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength5RealDouble(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case LENGTH6: {
			new DFTLength6RealDouble(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength6RealDouble(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8RealDouble(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength8RealDouble(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.reUnitRootDouble, fft4.imUnitRootDouble,
				fft4.K1).run();
			new DFTMixedRadixRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.reUnitRootDouble, fft4.imUnitRootDouble,
				fft4.K1).run();
			break;
		}
		case PADDEDRADER: {
			new DFTPaddedRaderRealDouble(reData, imData,
				startIndex + stride, quadrupleStride,
				fft4.reConvolverDouble, fft4.imConvolverDouble,
				fft4.modular, fft4.inverseModular).run();
			new DFTPaddedRaderRealDouble(reData, imData,
				startIndex + tripleStride, quadrupleStride,
				fft4.reConvolverDouble, fft4.imConvolverDouble,
				fft4.modular, fft4.inverseModular).run();
			break;
		}
		case RADER: {
			new DFTRaderRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.reConvolverDouble, fft4.imConvolverDouble,
				fft4.modular, fft4.inverseModular).run();
			new DFTRaderRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.reConvolverDouble, fft4.imConvolverDouble,
				fft4.modular, fft4.inverseModular).run();
			break;
		}
		case RADIX2: {
			new DFTRadix2RealDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.reUnitRootEvenDouble, fft4.imUnitRootEvenDouble,
				fft4.reUnitRootOddDouble, fft4.imUnitRootOddDouble).run();
			new DFTRadix2RealDouble(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.reUnitRootEvenDouble, fft4.imUnitRootEvenDouble,
				fft4.reUnitRootOddDouble, fft4.imUnitRootOddDouble).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.reUnitRootDouble, fft4.imUnitRootDouble).run();
			new DFTSplitRadixRealDouble(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.reUnitRootDouble, fft4.imUnitRootDouble).run();
			break;
		}
	}
	int p = startIndex;
	int q = startIndex;
	for (int m = 0; (m < halfK2); m++) {
		reBuffer[p] = reData[q];
		imBuffer[p] = imData[q];
		q += doubleStride;
		p += stride;
	}
	p = startIndex + K2 * stride;
	q = p + K4 * stride;
	int r = startIndex + stride;
	int s = r + doubleStride;
	for (int m = 0, n = 0; (m < halfK4); m++, n += 3) {
		double re = reData[r];
		double im = imData[r];
		double reRoot = reUnitRoot[m];
		double imRoot = imUnitRoot[m];
		double reButterfly = re * reRoot - im * imRoot;
		double imButterfly = re * imRoot + im * reRoot;
		re = reData[s];
		im = imData[s];
		reRoot = reUnitRoot[n];
		imRoot = imUnitRoot[n];
		final double reDragonfly = re * reRoot - im * imRoot;
		final double imDragonfly = re * imRoot + im * reRoot;
		reBuffer[p] = reButterfly + reDragonfly;
		imBuffer[p] = imButterfly + imDragonfly;
		reBuffer[q] = reButterfly - reDragonfly;
		imBuffer[q] = imButterfly - imDragonfly;
		r += quadrupleStride;
		s += quadrupleStride;
		p += stride;
		q += stride;
	};
	p = startIndex;
	q = startIndex + K2 * stride;
	reData[p] = reBuffer[p] + reBuffer[q];
	imData[p] = 0.0;
	p += stride;
	q += stride;
	for (int n = 1; (n < halfK4); n++) {
		reData[p] = reBuffer[p] + reBuffer[q];
		imData[p] = imBuffer[p] + imBuffer[q];
		p += stride;
		q += stride;
	}
	q = startIndex + (2 * K2 - halfK4) * stride;
	for (int n = halfK4; (n < K4); n++) {
		reData[p] = reBuffer[p] - imBuffer[q];
		imData[p] = imBuffer[p] - reBuffer[q];
		p += stride;
		q -= stride;
	}
	reData[p] = reBuffer[p];
	imData[p] = -reBuffer[q];
	p += stride;
	q += stride;
	r = startIndex + (K4 - 1) * stride;
	for (int n = 1; (n < halfK4); n++) {
		reData[p] = reBuffer[r] + imBuffer[q];
		imData[p] = -imBuffer[r] - reBuffer[q];
		p += stride;
		q += stride;
		r -= stride;
	}
	q = startIndex + (K2 + K4 - halfK4) * stride;
	for (int n = halfK4; (n < K4); n++) {
		reData[p] = reBuffer[r] - reBuffer[q];
		imData[p] = imBuffer[q] - imBuffer[r];
		p += stride;
		q -= stride;
		r -= stride;
	}
	reData[p] = reBuffer[startIndex] - reBuffer[q];
	imData[p] = 0.0;
} /* end run */

} /* end class DFTSplitRadixRealDouble */

/*====================================================================
|	DFTSplitRadixRealFloat
\===================================================================*/
static class DFTSplitRadixRealFloat
	extends
		DFTFloat
	implements
		Runnable

{ /* begin class DFTSplitRadixRealFloat */

/*....................................................................
	DFTSplitRadixRealFloat static variables
....................................................................*/
private float[] imBuffer;
private float[] imUnitRoot;
private float[] reBuffer;
private float[] reUnitRoot;

/*....................................................................
	DFTSplitRadixRealFloat constructors
....................................................................*/
/*------------------------------------------------------------------*/
DFTSplitRadixRealFloat (
	final float[] reData,
	final float[] imData,
	final float[] reBuffer,
	final float[] imBuffer,
	final int startIndex,
	final int stride,
	final float[] reUnitRoot,
	final float[] imUnitRoot
) {
	super(reData, imData, startIndex, stride);
	this.reBuffer = reBuffer;
	this.imBuffer = imBuffer;
	this.reUnitRoot = reUnitRoot;
	this.imUnitRoot = imUnitRoot;
} /* end DFTSplitRadixRealFloat */

/*....................................................................
	Runnable methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public void run (
) {
	final int K4 = reUnitRoot.length / 3;
	final int halfK4 = (K4 >> 1) + 1;
	final int K2 = K4 << 1;
	final int halfK2 = K4 + 1;
	final int doubleStride = stride << 1;
	final int tripleStride = doubleStride + stride;
	final int quadrupleStride = tripleStride + stride;
	final FFTSetupReal fft2 = FFTSetupReal.transforms.get(new Integer(K2));
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.ruritanian, fft2.chinese, fft2.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			new DFTEvenRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
			break;
		}
		case LENGTH1: {
			throw(new IllegalStateException());
		}
		case LENGTH2: {
			new DFTLength2RealFloat(reData, imData,
				startIndex, doubleStride).run();
			break;
		}
		case LENGTH3: {
			throw(new IllegalStateException());
		}
		case LENGTH4: {
			new DFTLength4RealFloat(reData, imData,
				startIndex, doubleStride).run();
			break;
		}
		case LENGTH5: {
			throw(new IllegalStateException());
		}
		case LENGTH6: {
			new DFTLength6RealFloat(reData, imData,
				startIndex, doubleStride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8RealFloat(reData, imData,
				startIndex, doubleStride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.reUnitRootFloat, fft2.imUnitRootFloat,
				fft2.K1).run();
			break;
		}
		case PADDEDRADER: {
			new DFTPaddedRaderRealFloat(reData, imData,
				startIndex, doubleStride,
				fft2.reConvolverFloat, fft2.imConvolverFloat,
				fft2.modular, fft2.inverseModular).run();
			break;
		}
		case RADER: {
			new DFTRaderRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.reConvolverFloat, fft2.imConvolverFloat,
				fft2.modular, fft2.inverseModular).run();
			break;
		}
		case RADIX2: {
			new DFTRadix2RealFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.reUnitRootEvenFloat, fft2.imUnitRootEvenFloat,
				fft2.reUnitRootOddFloat, fft2.imUnitRootOddFloat).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex, doubleStride,
				fft2.reUnitRootFloat, fft2.imUnitRootFloat).run();
			break;
		}
	}
	final FFTSetupDuoReal fft4 =
		FFTSetupDuoReal.transforms.get(new Integer(K4));
	switch (fft4.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.reUnitRootFloat, fft4.imUnitRootFloat).run();
			new DFTBruteForceRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.reUnitRootFloat, fft4.imUnitRootFloat).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.ruritanian, fft4.chinese,
				fft4.K1).run();
			new DFTCoprimeFactorRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.ruritanian, fft4.chinese,
				fft4.K1).run();
			break;
		}
		case DUOREAL: {
			new DFTDuoRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, startIndex + tripleStride,
				quadrupleStride, K4).run();
			break;
		}
		case EVENREAL: {
			new DFTEvenRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.reUnitRootFloat, fft4.imUnitRootFloat).run();
			new DFTEvenRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.reUnitRootFloat, fft4.imUnitRootFloat).run();
			break;
		}
		case LENGTH1: {
			imData[startIndex + stride] = 0.0F;
			imData[startIndex + tripleStride] = 0.0F;
			break;
		}
		case LENGTH2: {
			new DFTLength2RealFloat(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength2RealFloat(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case LENGTH3: {
			new DFTLength3RealFloat(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength3RealFloat(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case LENGTH4: {
			new DFTLength4RealFloat(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength4RealFloat(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case LENGTH5: {
			new DFTLength5RealFloat(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength5RealFloat(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case LENGTH6: {
			new DFTLength6RealFloat(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength6RealFloat(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8RealFloat(reData, imData,
				startIndex + stride, quadrupleStride).run();
			new DFTLength8RealFloat(reData, imData,
				startIndex + tripleStride, quadrupleStride).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.reUnitRootFloat, fft4.imUnitRootFloat,
				fft4.K1).run();
			new DFTMixedRadixRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.reUnitRootFloat, fft4.imUnitRootFloat,
				fft4.K1).run();
			break;
		}
		case PADDEDRADER: {
			new DFTPaddedRaderRealFloat(reData, imData,
				startIndex + stride, quadrupleStride,
				fft4.reConvolverFloat, fft4.imConvolverFloat,
				fft4.modular, fft4.inverseModular).run();
			new DFTPaddedRaderRealFloat(reData, imData,
				startIndex + tripleStride, quadrupleStride,
				fft4.reConvolverFloat, fft4.imConvolverFloat,
				fft4.modular, fft4.inverseModular).run();
			break;
		}
		case RADER: {
			new DFTRaderRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.reConvolverFloat, fft4.imConvolverFloat,
				fft4.modular, fft4.inverseModular).run();
			new DFTRaderRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.reConvolverFloat, fft4.imConvolverFloat,
				fft4.modular, fft4.inverseModular).run();
			break;
		}
		case RADIX2: {
			new DFTRadix2RealFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.reUnitRootEvenFloat, fft4.imUnitRootEvenFloat,
				fft4.reUnitRootOddFloat, fft4.imUnitRootOddFloat).run();
			new DFTRadix2RealFloat(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.reUnitRootEvenFloat, fft4.imUnitRootEvenFloat,
				fft4.reUnitRootOddFloat, fft4.imUnitRootOddFloat).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex + stride, quadrupleStride,
				fft4.reUnitRootFloat, fft4.imUnitRootFloat).run();
			new DFTSplitRadixRealFloat(reData, imData, reBuffer, imBuffer,
				startIndex + tripleStride, quadrupleStride,
				fft4.reUnitRootFloat, fft4.imUnitRootFloat).run();
			break;
		}
	}
	int p = startIndex;
	int q = startIndex;
	for (int m = 0; (m < halfK2); m++) {
		reBuffer[p] = reData[q];
		imBuffer[p] = imData[q];
		q += doubleStride;
		p += stride;
	}
	p = startIndex + K2 * stride;
	q = p + K4 * stride;
	int r = startIndex + stride;
	int s = r + doubleStride;
	for (int m = 0, n = 0; (m < halfK4); m++, n += 3) {
		float re = reData[r];
		float im = imData[r];
		float reRoot = reUnitRoot[m];
		float imRoot = imUnitRoot[m];
		float reButterfly = re * reRoot - im * imRoot;
		float imButterfly = re * imRoot + im * reRoot;
		re = reData[s];
		im = imData[s];
		reRoot = reUnitRoot[n];
		imRoot = imUnitRoot[n];
		final float reDragonfly = re * reRoot - im * imRoot;
		final float imDragonfly = re * imRoot + im * reRoot;
		reBuffer[p] = reButterfly + reDragonfly;
		imBuffer[p] = imButterfly + imDragonfly;
		reBuffer[q] = reButterfly - reDragonfly;
		imBuffer[q] = imButterfly - imDragonfly;
		r += quadrupleStride;
		s += quadrupleStride;
		p += stride;
		q += stride;
	};
	p = startIndex;
	q = startIndex + K2 * stride;
	reData[p] = reBuffer[p] + reBuffer[q];
	imData[p] = 0.0F;
	p += stride;
	q += stride;
	for (int n = 1; (n < halfK4); n++) {
		reData[p] = reBuffer[p] + reBuffer[q];
		imData[p] = imBuffer[p] + imBuffer[q];
		p += stride;
		q += stride;
	}
	q = startIndex + (2 * K2 - halfK4) * stride;
	for (int n = halfK4; (n < K4); n++) {
		reData[p] = reBuffer[p] - imBuffer[q];
		imData[p] = imBuffer[p] - reBuffer[q];
		p += stride;
		q -= stride;
	}
	reData[p] = reBuffer[p];
	imData[p] = -reBuffer[q];
	p += stride;
	q += stride;
	r = startIndex + (K4 - 1) * stride;
	for (int n = 1; (n < halfK4); n++) {
		reData[p] = reBuffer[r] + imBuffer[q];
		imData[p] = -imBuffer[r] - reBuffer[q];
		p += stride;
		q += stride;
		r -= stride;
	}
	q = startIndex + (K2 + K4 - halfK4) * stride;
	for (int n = halfK4; (n < K4); n++) {
		reData[p] = reBuffer[r] - reBuffer[q];
		imData[p] = imBuffer[q] - imBuffer[r];
		p += stride;
		q -= stride;
		r -= stride;
	}
	reData[p] = reBuffer[startIndex] - reBuffer[q];
	imData[p] = 0.0F;
} /* end run */

} /* end class DFTSplitRadixRealFloat */

/*====================================================================
|	FFTSetup
\===================================================================*/
static class FFTSetup

{ /* begin class FFTSetup */

/*....................................................................
	FFTSetup static private variables
....................................................................*/
private static final HashSet<Integer> composites = new HashSet<Integer>();
private static final HashSet<Integer> primes = new HashSet<Integer>();

/*....................................................................
	FFTSetup static protected variables
....................................................................*/
protected static final HashMap<Integer, Algorithm> algorithms =
	new HashMap<Integer, Algorithm>();
protected static final HashMap<Integer, Integer> lengths =
	new HashMap<Integer, Integer>();
protected static final HashMap<Integer, FFTSetup> transforms =
	new HashMap<Integer, FFTSetup>();
protected static final HashMap<Integer, Long> costs =
	new HashMap<Integer, Long>();
protected static final HashSet<Integer> taboos =
	new HashSet<Integer>();
protected static final long FLASSIGN = 2L;
protected static final long FLOP = 4L;
protected static final long IDX = 1L;
protected static final long INTASSIGN = 1L;
protected static final long INTOP = 2L;
protected static final long NEWOBJ = 50L;

protected static final long FLALLOC = FLOP + FLASSIGN;
protected static final long INTALLOC = INTOP + INTASSIGN;

/*....................................................................
	FFTSetup static private variables
....................................................................*/
private static int futurePrime = 7;

/*....................................................................
	FFTSetup protected variables
....................................................................*/
protected Algorithm algorithm;
protected double[] imConvolverDouble;
protected double[] imUnitRootDouble;
protected double[] reConvolverDouble;
protected double[] reUnitRootDouble;
protected float[] imConvolverFloat;
protected float[] imUnitRootFloat;
protected float[] reConvolverFloat;
protected float[] reUnitRootFloat;
protected int[] chinese;
protected int[] inverseModular;
protected int[] modular;
protected int[] ruritanian;
protected int K1;

/*....................................................................
	FFTSetup inner classes
....................................................................*/
/*====================================================================
|	FFTCostPrediction
\===================================================================*/
static class FFTCostPrediction

{ /* begin class FFTCostPrediction */

/*....................................................................
	FFTCostPrediction variables
....................................................................*/
protected Algorithm algorithm;
protected int length;
protected long cost;

/*....................................................................
	FFTCostPrediction constructors
....................................................................*/
/*------------------------------------------------------------------*/
FFTCostPrediction (
	final Algorithm algorithm,
	final int length,
	final long cost
) {
	this.algorithm = algorithm;
	this.length = length;
	this.cost = cost;
} /* end FFTCostPrediction */

/*....................................................................
	Object methods
....................................................................*/
/*------------------------------------------------------------------*/
@Override
public boolean equals (
	final Object o
) {
	return((((FFTCostPrediction)o).algorithm == algorithm)
		&& (((FFTCostPrediction)o).length == length));
} /* end equals */

} /* end class FFTCostPrediction */

/*....................................................................
	FFTSetup static initialization block
....................................................................*/
static {
	initialize();
}

/*....................................................................
	FFTSetup constructors
....................................................................*/
/*------------------------------------------------------------------*/
FFTSetup (
	final int transformLength
) {
	if (transforms.containsKey(new Integer(transformLength))) {
		return;
	}
	transforms.put(new Integer(transformLength), this);
	cost(transformLength);
	algorithm = algorithms.get(new Integer(transformLength));
	switch (algorithm) {
		case BRUTEFORCE: {
			reUnitRootDouble =
				DFTBruteForceDouble.getReUnitRoot(transformLength);
			imUnitRootDouble =
				DFTBruteForceDouble.getImUnitRoot(transformLength);
			reUnitRootFloat =
				DFTBruteForceFloat.getReUnitRoot(transformLength);
			imUnitRootFloat =
				DFTBruteForceFloat.getImUnitRoot(transformLength);
			break;
		}
		case COPRIMEFACTOR: {
			K1 = lengths.get(new Integer(transformLength)).intValue();
			final int K2 = transformLength / K1;
			ruritanian = DFTCoprimeFactor.getRuritanianShuffling(K1, K2);
			chinese = DFTCoprimeFactor.getChineseRemainderShuffling(K1, K2);
			new FFTSetup(K1);
			new FFTSetup(K2);
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			break;
		}
		case LENGTH3: {
			break;
		}
		case LENGTH4: {
			break;
		}
		case LENGTH5: {
			break;
		}
		case LENGTH6: {
			break;
		}
		case LENGTH8: {
			break;
		}
		case MIXEDRADIX: {
			K1 = lengths.get(new Integer(transformLength)).intValue();
			final int K2 = transformLength / K1;
			reUnitRootDouble =
				DFTMixedRadixDouble.getReUnitRoot(transformLength);
			imUnitRootDouble =
				DFTMixedRadixDouble.getImUnitRoot(transformLength);
			reUnitRootFloat =
				DFTMixedRadixFloat.getReUnitRoot(transformLength);
			imUnitRootFloat =
				DFTMixedRadixFloat.getImUnitRoot(transformLength);
			new FFTSetup(K1);
			new FFTSetup(K2);
			break;
		}
		case PADDEDRADER: {
			final int paddedLength =
				lengths.get(new Integer(transformLength)).intValue();
			modular = DFTRader.getModularPowerShuffling(transformLength);
			inverseModular =
				DFTPaddedRader.getInverseModularPowerShuffling(modular,
				paddedLength);
			new FFTSetup(paddedLength);
			final double[][] convolverD =
				DFTPaddedRaderDouble.getConvolverReAndIm(modular,
				paddedLength);
			reConvolverDouble = convolverD[0];
			imConvolverDouble = convolverD[1];
			final float[][] convolverF =
				DFTPaddedRaderFloat.getConvolverReAndIm(modular,
				paddedLength);
			reConvolverFloat = convolverF[0];
			imConvolverFloat = convolverF[1];
			break;
		}
		case RADER: {
			modular = DFTRader.getModularPowerShuffling(transformLength);
			inverseModular = DFTRader.getInverseModularPowerShuffling(modular);
			new FFTSetup(transformLength - 1);
			final double[][] convolverD =
				DFTRaderDouble.getConvolverReAndIm(modular);
			reConvolverDouble = convolverD[0];
			imConvolverDouble = convolverD[1];
			final float[][] convolverF =
				DFTRaderFloat.getConvolverReAndIm(modular);
			reConvolverFloat = convolverF[0];
			imConvolverFloat = convolverF[1];
			break;
		}
		case RADIX2: {
			reUnitRootDouble =
				DFTRadix2Double.getReUnitRoot(transformLength);
			imUnitRootDouble =
				DFTRadix2Double.getImUnitRoot(transformLength);
			reUnitRootFloat =
				DFTRadix2Float.getReUnitRoot(transformLength);
			imUnitRootFloat =
				DFTRadix2Float.getImUnitRoot(transformLength);
			new FFTSetup(transformLength >> 1);
			break;
		}
		case SPLITRADIX: {
			reUnitRootDouble =
				DFTSplitRadixDouble.getReUnitRoot(transformLength);
			imUnitRootDouble =
				DFTSplitRadixDouble.getImUnitRoot(transformLength);
			reUnitRootFloat =
				DFTSplitRadixFloat.getReUnitRoot(transformLength);
			imUnitRootFloat =
				DFTSplitRadixFloat.getImUnitRoot(transformLength);
			new FFTSetup(transformLength >> 1);
			new FFTSetup(transformLength >> 2);
			break;
		}
	}
} /* end FFTSetup */

/*....................................................................
	FFTSetup static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
	final int transformLength
) {
	if (costs.containsKey(new Integer(transformLength))) {
		return(costs.get(new Integer(transformLength)).longValue());
	}
	final Vector<FFTCostPrediction> costPredictions =
		new Vector<FFTCostPrediction>();
	switch (transformLength) {
		case 1: {
			costPredictions.add(new FFTCostPrediction(
				Algorithm.LENGTH1, -1, 0L));
			break;
		}
		case 2: {
			costPredictions.add(new FFTCostPrediction(
				Algorithm.LENGTH2, -1, DFTLength2.cost()));
			break;
		}
		case 3: {
			costPredictions.add(new FFTCostPrediction(
				Algorithm.LENGTH3, -1, DFTLength3.cost()));
			break;
		}
		case 4: {
			costPredictions.add(new FFTCostPrediction(
				Algorithm.LENGTH4, -1, DFTLength4.cost()));
			break;
		}
		case 5: {
			costPredictions.add(new FFTCostPrediction(
				Algorithm.LENGTH5, -1, DFTLength5.cost()));
			break;
		}
		case 6: {
			costPredictions.add(new FFTCostPrediction(
				Algorithm.LENGTH6, -1, DFTLength6.cost()));
			break;
		}
		case 8: {
			costPredictions.add(new FFTCostPrediction(
				Algorithm.LENGTH8, -1, DFTLength8.cost()));
			break;
		}
		default: {
			final HashSet<Integer> divisors = divisors(transformLength);
			if (divisors.isEmpty()) {
				costPredictions.add(new FFTCostPrediction(
					Algorithm.RADER, -1, DFTRader.cost(transformLength)));
				while (futurePrime < transformLength) {
					cost(futurePrime);
				}
				do {
					futurePrime +=2;
				} while (!isPrime(futurePrime));
				int minPaddedLength = 2 * transformLength - 3;
				int maxPaddedLength = 1;
				int n = minPaddedLength;
				while (0 < n) {
					n >>= 1;
					maxPaddedLength <<= 1;
				}
				for (n = transformLength; (n < minPaddedLength); n += 2) {
					if (costs.containsKey(new Integer(n))) {
						continue;
					}
					if (isPrime(n)) {
						taboos.add(new Integer(n));
						taboos.add(new Integer(2 * n));
						taboos.add(new Integer(3 * n));
					}
				}
				for (n = minPaddedLength; (n <= maxPaddedLength); n += 2) {
					if (costs.containsKey(new Integer(n))) {
						continue;
					}
					if (isPrime(n)) {
						taboos.add(new Integer(n));
						taboos.add(new Integer(2 * n));
					}
				}
				for (n = minPaddedLength; (n <= maxPaddedLength); n++) {
					costPredictions.add(new FFTCostPrediction(
						Algorithm.PADDEDRADER, n,
						DFTPaddedRader.cost(transformLength, n)));
				}
				taboos.clear();
			}
			else {
				for (Integer d: divisors) {
					final int K1 = d.intValue();
					final int K2 = transformLength / K1;
					costPredictions.add(new FFTCostPrediction(
						Algorithm.MIXEDRADIX, K1,
						DFTMixedRadix.cost(K1, K2)));
					if (1 == greatestCommonDivisor(K1, K2)) {
						costPredictions.add(new FFTCostPrediction(
							Algorithm.COPRIMEFACTOR, K1,
							DFTCoprimeFactor.cost(K1, K2)));
					}
					if (2 == K1) {
						costPredictions.add(new FFTCostPrediction(
							Algorithm.RADIX2, -1,
							DFTRadix2.cost(transformLength)));
					}
					if (4 == K1) {
						costPredictions.add(new FFTCostPrediction(
							Algorithm.SPLITRADIX, -1,
							DFTSplitRadix.cost(transformLength)));
					}
				}
			}
		}
	}
	FFTCostPrediction best = new FFTCostPrediction(
		Algorithm.BRUTEFORCE, -1, DFTBruteForce.cost(transformLength));
	long cheapest = best.cost;
	for (FFTCostPrediction predicted: costPredictions) {
		final long current = predicted.cost;
		if (0L <= current) {
			if (current < cheapest) {
				cheapest = current;
				best = predicted;
			}
		}
	}
	algorithms.put(new Integer(transformLength), best.algorithm);
	lengths.put(new Integer(transformLength), new Integer(best.length));
	costs.put(new Integer(transformLength), new Long(cheapest));
	return(cheapest);
} /* end cost */

/*------------------------------------------------------------------*/
static void reset (
) {
	composites.clear();
	primes.clear();
	algorithms.clear();
	lengths.clear();
	transforms.clear();
	costs.clear();
	taboos.clear();
	initialize();
} /* end reset */

/*....................................................................
	FFTSetup private methods
....................................................................*/
/*------------------------------------------------------------------*/
private static HashSet<Integer> divisors (
	int number
) {
	final HashSet<Integer> divisors = new HashSet<Integer>();
	for (int k = (int)floor(sqrt(0.5 + (double)number)); (2 <= k); k--) {
		if (number == (k * (number / k))) {
			divisors.add(new Integer(k));
			divisors.add(new Integer(number / k));
		}
	}
	return(divisors);
} /* end divisors */

/*------------------------------------------------------------------*/
private static int greatestCommonDivisor (
	int m,
	int n
) {
	m = (m < 0) ? (-m) : (m);
	n = (n < 0) ? (-n) : (n);
	while (0 != n) {
		final int p = n;
		n = m - n * (m / n);
		m = p;
	}
	return(m);
} /* end greatestCommonDivisor */

/*------------------------------------------------------------------*/
static private void initialize (
) {
	primes.add(new Integer(2));
	primes.add(new Integer(3));
	primes.add(new Integer(5));
	new FFTSetup(2);
	new FFTSetup(3);
	new FFTSetup(5);
	futurePrime = 7;
} /* end initialize */

/*------------------------------------------------------------------*/
static private boolean isPrime (
	final int number
) {
	if (composites.contains(new Integer(number))) {
		return(false);
	}
	if (primes.contains(new Integer(number))) {
		return(true);
	}
	boolean isComposite = false;
	for (int k = (int)floor(sqrt(0.5 + (double)number)); (1 < k); k--) {
		if (number == (k * (number / k))) {
			isComposite = true;
			break;
		}
	}
	if (isComposite) {
		composites.add(new Integer(number));
		return(false);
	}
	primes.add(new Integer(number));
	return(true);
} /* end isPrime */

} /* end class FFTSetup */

/*====================================================================
|	FFTSetupDuoReal
\===================================================================*/
static class FFTSetupDuoReal
	extends FFTSetupReal

{ /* begin class FFTSetupDuoReal */

/*....................................................................
	FFTSetupDuoReal static protected variables
....................................................................*/
protected static final HashMap<Integer, Algorithm> algorithms =
	new HashMap<Integer, Algorithm>();
protected static final HashMap<Integer, Integer> lengths =
	new HashMap<Integer, Integer>();
protected static final HashMap<Integer, FFTSetupDuoReal> transforms =
	new HashMap<Integer, FFTSetupDuoReal>();
protected static final HashMap<Integer, Long> costs =
	new HashMap<Integer, Long>();

/*....................................................................
	FFTSetupDuoReal protected variables
....................................................................*/
protected Algorithm algorithm;

/*....................................................................
	FFTSetupDuoReal static initialization block
....................................................................*/
static {
	initialize();
}

/*....................................................................
	FFTSetupDuoReal constructors
....................................................................*/
/*------------------------------------------------------------------*/
FFTSetupDuoReal (
	final int transformLength
) {
	super(transformLength);
	if (transforms.containsKey(new Integer(transformLength))) {
		return;
	}
	new FFTSetupReal(transformLength);
	algorithm = cheapest(transformLength);
	FFTSetupReal fft =
		FFTSetupReal.transforms.get(new Integer(transformLength));
	imConvolverDouble = fft.imConvolverDouble;
	imUnitRootDouble = fft.imUnitRootDouble;
	imUnitRootEvenDouble = fft.imUnitRootEvenDouble;
	imUnitRootOddDouble = fft.imUnitRootOddDouble;
	reConvolverDouble = fft.reConvolverDouble;
	reUnitRootDouble = fft.reUnitRootDouble;
	reUnitRootEvenDouble = fft.reUnitRootEvenDouble;
	reUnitRootOddDouble = fft.reUnitRootOddDouble;
	imConvolverFloat = fft.imConvolverFloat;
	imUnitRootEvenFloat = fft.imUnitRootEvenFloat;
	imUnitRootFloat = fft.imUnitRootFloat;
	imUnitRootOddFloat = fft.imUnitRootOddFloat;
	reConvolverFloat = fft.reConvolverFloat;
	reUnitRootEvenFloat = fft.reUnitRootEvenFloat;
	reUnitRootFloat = fft.reUnitRootFloat;
	reUnitRootOddFloat = fft.reUnitRootOddFloat;
	chinese = fft.chinese;
	inverseModular = fft.inverseModular;
	modular = fft.modular;
	ruritanian = fft.ruritanian;
	K1 = fft.K1;
	transforms.put(new Integer(transformLength), this);
} /* end FFTSetupDuoReal */

/*....................................................................
	FFTSetupDuoReal static methods
....................................................................*/
/*------------------------------------------------------------------*/
static Algorithm cheapest (
	final int transformLength
) {
	if (transforms.containsKey(new Integer(transformLength))) {
		return(transforms.get(new Integer(transformLength)).algorithm);
	}
	final Vector<FFTCostPrediction> costPredictions =
		new Vector<FFTCostPrediction>();
	costPredictions.add(new FFTCostPrediction(
		FFTSetupReal.algorithms.get(new Integer(transformLength)),
		FFTSetupReal.lengths.get(new Integer(transformLength)).intValue(),
		2L * FFTSetupReal.costs.get(new Integer(transformLength)).longValue()));
	FFTCostPrediction best = new FFTCostPrediction(
		Algorithm.DUOREAL, -1, DFTDuoReal.cost(transformLength));
	long cheapest = best.cost;
	for (FFTCostPrediction predicted: costPredictions) {
		final long current = predicted.cost;
		if (0L <= current) {
			if (current < cheapest) {
				cheapest = current;
				best = predicted;
			}
		}
	}
	algorithms.put(new Integer(transformLength), best.algorithm);
	lengths.put(new Integer(transformLength), new Integer(best.length));
	costs.put(new Integer(transformLength), new Long(cheapest));
	return(best.algorithm);
} /* end cheapest */

/*------------------------------------------------------------------*/
static void reset (
) {
	transforms.clear();
	initialize();
} /* end reset */

/*------------------------------------------------------------------*/
static private void initialize (
) {
	new FFTSetupDuoReal(2);
	new FFTSetupDuoReal(3);
	new FFTSetupDuoReal(5);
} /* end initialize */

} /* end class FFTSetupDuoReal */

/*====================================================================
|	FFTSetupReal
\===================================================================*/
static class FFTSetupReal
	extends FFTSetup

{ /* begin class FFTSetupReal */

/*....................................................................
	FFTSetupReal static protected variables
....................................................................*/
protected static final HashMap<Integer, Algorithm> algorithms =
	new HashMap<Integer, Algorithm>();
protected static final HashMap<Integer, Integer> lengths =
	new HashMap<Integer, Integer>();
protected static final HashMap<Integer, FFTSetupReal> transforms =
	new HashMap<Integer, FFTSetupReal>();
protected static final HashMap<Integer, Long> costs =
	new HashMap<Integer, Long>();

/*....................................................................
	FFTSetupReal static private variables
....................................................................*/
private static int futurePrime = 7;

/*....................................................................
	FFTSetupReal protected variables
....................................................................*/
protected Algorithm algorithm;
protected double[] imConvolverDouble;
protected double[] imUnitRootDouble;
protected double[] imUnitRootEvenDouble;
protected double[] imUnitRootOddDouble;
protected double[] reConvolverDouble;
protected double[] reUnitRootDouble;
protected double[] reUnitRootEvenDouble;
protected double[] reUnitRootOddDouble;
protected float[] imConvolverFloat;
protected float[] imUnitRootEvenFloat;
protected float[] imUnitRootFloat;
protected float[] imUnitRootOddFloat;
protected float[] reConvolverFloat;
protected float[] reUnitRootEvenFloat;
protected float[] reUnitRootFloat;
protected float[] reUnitRootOddFloat;
protected int[] chinese;
protected int[] inverseModular;
protected int[] modular;
protected int[] ruritanian;
protected int K1;

/*....................................................................
	FFTSetupReal static initialization block
....................................................................*/
static {
	initialize();
}

/*....................................................................
	FFTSetupReal constructors
....................................................................*/
/*------------------------------------------------------------------*/
FFTSetupReal (
	final int transformLength
) {
	super(transformLength);
	if (transforms.containsKey(new Integer(transformLength))) {
		return;
	}
	transforms.put(new Integer(transformLength), this);
	cost(transformLength);
	algorithm = algorithms.get(new Integer(transformLength));
	switch (algorithm) {
		case BRUTEFORCE: {
			reUnitRootDouble =
				DFTBruteForceDouble.getReUnitRoot(transformLength);
			imUnitRootDouble =
				DFTBruteForceDouble.getImUnitRoot(transformLength);
			reUnitRootFloat =
				DFTBruteForceFloat.getReUnitRoot(transformLength);
			imUnitRootFloat =
				DFTBruteForceFloat.getImUnitRoot(transformLength);
			break;
		}
		case COPRIMEFACTOR: {
			K1 = lengths.get(new Integer(transformLength)).intValue();
			final int K2 = transformLength / K1;
			ruritanian = DFTCoprimeFactor.getRuritanianShuffling(K1, K2);
			chinese =
				DFTCoprimeFactorReal.getTruncatedChineseRemainderShuffling(
				K1, K2);
			new FFTSetup(K1);
			new FFTSetupReal(K2);
			new FFTSetupDuoReal(K2);
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			reUnitRootDouble =
				DFTEvenRealDouble.getReUnitRoot(transformLength);
			imUnitRootDouble =
				DFTEvenRealDouble.getImUnitRoot(transformLength);
			reUnitRootFloat =
				DFTEvenRealFloat.getReUnitRoot(transformLength);
			imUnitRootFloat =
				DFTEvenRealFloat.getImUnitRoot(transformLength);
			new FFTSetup(transformLength >> 1);
			break;
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			break;
		}
		case LENGTH3: {
			break;
		}
		case LENGTH4: {
			break;
		}
		case LENGTH5: {
			break;
		}
		case LENGTH6: {
			break;
		}
		case LENGTH8: {
			break;
		}
		case MIXEDRADIX: {
			K1 = lengths.get(new Integer(transformLength)).intValue();
			final int K2 = transformLength / K1;
			reUnitRootDouble =
				DFTMixedRadixDouble.getReUnitRoot(transformLength);
			imUnitRootDouble =
				DFTMixedRadixDouble.getImUnitRoot(transformLength);
			reUnitRootFloat =
				DFTMixedRadixFloat.getReUnitRoot(transformLength);
			imUnitRootFloat =
				DFTMixedRadixFloat.getImUnitRoot(transformLength);
			new FFTSetup(K1);
			new FFTSetupReal(K2);
			new FFTSetupDuoReal(K2);
			break;
		}
		case PADDEDRADER: {
			final int paddedLength =
				lengths.get(new Integer(transformLength)).intValue();
			modular = DFTRader.getModularPowerShuffling(transformLength);
			inverseModular =
				DFTPaddedRader.getInverseModularPowerShuffling(modular,
				paddedLength);
			new FFTSetup(paddedLength);
			new FFTSetupReal(paddedLength);
			final double[][] convolverD =
				DFTPaddedRaderDouble.getConvolverReAndIm(modular,
				paddedLength);
			reConvolverDouble = convolverD[0];
			imConvolverDouble = convolverD[1];
			final float[][] convolverF =
				DFTPaddedRaderFloat.getConvolverReAndIm(modular,
				paddedLength);
			reConvolverFloat = convolverF[0];
			imConvolverFloat = convolverF[1];
			break;
		}
		case RADER: {
			modular = DFTRader.getModularPowerShuffling(transformLength);
			inverseModular = DFTRader.getInverseModularPowerShuffling(modular);
			new FFTSetup(transformLength - 1);
			new FFTSetupReal(transformLength - 1);
			final double[][] convolverD =
				DFTRaderDouble.getConvolverReAndIm(modular);
			reConvolverDouble = convolverD[0];
			imConvolverDouble = convolverD[1];
			final float[][] convolverF =
				DFTRaderFloat.getConvolverReAndIm(modular);
			reConvolverFloat = convolverF[0];
			imConvolverFloat = convolverF[1];
			break;
		}
		case RADIX2: {
			reUnitRootEvenDouble =
				DFTRadix2RealDouble.getReUnitRootEven(transformLength);
			imUnitRootEvenDouble =
				DFTRadix2RealDouble.getImUnitRootEven(transformLength);
			reUnitRootOddDouble =
				DFTRadix2RealDouble.getReUnitRootOdd(transformLength);
			imUnitRootOddDouble =
				DFTRadix2RealDouble.getImUnitRootOdd(transformLength);
			reUnitRootEvenFloat =
				DFTRadix2RealFloat.getReUnitRootEven(transformLength);
			imUnitRootEvenFloat =
				DFTRadix2RealFloat.getImUnitRootEven(transformLength);
			reUnitRootOddFloat =
				DFTRadix2RealFloat.getReUnitRootOdd(transformLength);
			imUnitRootOddFloat =
				DFTRadix2RealFloat.getImUnitRootOdd(transformLength);
			new FFTSetupDuoReal(transformLength >> 1);
			break;
		}
		case SPLITRADIX: {
			reUnitRootDouble =
				DFTSplitRadixDouble.getReUnitRoot(transformLength);
			imUnitRootDouble =
				DFTSplitRadixDouble.getImUnitRoot(transformLength);
			reUnitRootFloat =
				DFTSplitRadixFloat.getReUnitRoot(transformLength);
			imUnitRootFloat =
				DFTSplitRadixFloat.getImUnitRoot(transformLength);
			new FFTSetupReal(transformLength >> 1);
			new FFTSetupDuoReal(transformLength >> 2);
			break;
		}
	}
} /* end FFTSetupReal */

/*....................................................................
	FFTSetupReal static methods
....................................................................*/
/*------------------------------------------------------------------*/
static long cost (
	final int transformLength
) {
	if (costs.containsKey(new Integer(transformLength))) {
		return(costs.get(new Integer(transformLength)).longValue());
	}
	final Vector<FFTCostPrediction> costPredictions =
		new Vector<FFTCostPrediction>();
	switch (transformLength) {
		case 1: {
			costPredictions.add(new FFTCostPrediction(
				Algorithm.LENGTH1, -1, 0L));
			break;
		}
		case 2: {
			costPredictions.add(new FFTCostPrediction(
				Algorithm.LENGTH2, -1, DFTLength2Real.cost()));
			break;
		}
		case 3: {
			costPredictions.add(new FFTCostPrediction(
				Algorithm.LENGTH3, -1, DFTLength3Real.cost()));
			break;
		}
		case 4: {
			costPredictions.add(new FFTCostPrediction(
				Algorithm.LENGTH4, -1, DFTLength4Real.cost()));
			break;
		}
		case 5: {
			costPredictions.add(new FFTCostPrediction(
				Algorithm.LENGTH5, -1, DFTLength5Real.cost()));
			break;
		}
		case 6: {
			costPredictions.add(new FFTCostPrediction(
				Algorithm.LENGTH6, -1, DFTLength6Real.cost()));
			break;
		}
		case 8: {
			costPredictions.add(new FFTCostPrediction(
				Algorithm.LENGTH8, -1, DFTLength8Real.cost()));
			break;
		}
		default: {
			final HashSet<Integer> divisors =
				FFTSetup.divisors(transformLength);
			if (divisors.isEmpty()) {
				costPredictions.add(new FFTCostPrediction(
					Algorithm.RADER, -1, DFTRaderReal.cost(transformLength)));
				while (futurePrime < transformLength) {
					cost(futurePrime);
				}
				do {
					futurePrime +=2;
				} while (!FFTSetup.isPrime(futurePrime));
				int minPaddedLength = 2 * transformLength - 3;
				int maxPaddedLength = 1;
				int n = minPaddedLength;
				while (0 < n) {
					n >>= 1;
					maxPaddedLength <<= 1;
				}
				for (n = transformLength; (n < minPaddedLength); n += 2) {
					if (costs.containsKey(new Integer(n))) {
						continue;
					}
					if (FFTSetup.isPrime(n)) {
						taboos.add(new Integer(n));
						taboos.add(new Integer(2 * n));
						taboos.add(new Integer(3 * n));
					}
				}
				for (n = minPaddedLength; (n <= maxPaddedLength); n += 2) {
					if (costs.containsKey(new Integer(n))) {
						continue;
					}
					if (FFTSetup.isPrime(n)) {
						taboos.add(new Integer(n));
						taboos.add(new Integer(2 * n));
					}
				}
				for (n = minPaddedLength; (n <= maxPaddedLength); n++) {
					costPredictions.add(new FFTCostPrediction(
						Algorithm.PADDEDRADER, n,
						DFTPaddedRaderReal.cost(transformLength, n)));
				}
				taboos.clear();
			}
			else {
				for (Integer d: divisors) {
					final int K1 = d.intValue();
					final int K2 = transformLength / K1;
					costPredictions.add(new FFTCostPrediction(
						Algorithm.MIXEDRADIX, K1,
						DFTMixedRadixReal.cost(K1, K2)));
					if (1 == FFTSetup.greatestCommonDivisor(K1, K2)) {
						costPredictions.add(new FFTCostPrediction(
							Algorithm.COPRIMEFACTOR, K1,
							DFTCoprimeFactorReal.cost(K1, K2)));
					}
					if (2 == K1) {
						costPredictions.add(new FFTCostPrediction(
							Algorithm.EVENREAL, -1,
							DFTEvenReal.cost(transformLength)));
						costPredictions.add(new FFTCostPrediction(
							Algorithm.RADIX2, -1,
							DFTRadix2Real.cost(transformLength)));
					}
					if (4 == K1) {
						costPredictions.add(new FFTCostPrediction(
							Algorithm.SPLITRADIX, -1,
							DFTSplitRadixReal.cost(transformLength)));
					}
				}
			}
		}
	}
	FFTCostPrediction best = new FFTCostPrediction(
		Algorithm.BRUTEFORCE, -1, DFTBruteForceReal.cost(transformLength));
	long cheapest = best.cost;
	for (FFTCostPrediction predicted: costPredictions) {
		final long current = predicted.cost;
		if (0L <= current) {
			if (current < cheapest) {
				cheapest = current;
				best = predicted;
			}
		}
	}
	algorithms.put(new Integer(transformLength), best.algorithm);
	lengths.put(new Integer(transformLength), new Integer(best.length));
	costs.put(new Integer(transformLength), new Long(cheapest));
	return(cheapest);
} /* end cost */

/*------------------------------------------------------------------*/
static void reset (
) {
	algorithms.clear();
	lengths.clear();
	transforms.clear();
	costs.clear();
	initialize();
} /* end reset */

/*------------------------------------------------------------------*/
static private void initialize (
) {
	new FFTSetupReal(2);
	new FFTSetupReal(3);
	new FFTSetupReal(5);
	futurePrime = 7;
} /* end initialize */

} /* end class FFTSetupReal */

/*....................................................................
	AcademicFFT constructors
....................................................................*/
/*------------------------------------------------------------------*/
/*********************************************************************
 <p>
 This constructor prepares a one-dimensional Fourier transform. It
 depends on the length of the sequence but not on the actual data
 being transformed, so that it can be reused for different data.
 It specifies the convention to adopt for locating the origin of the
 Fourier transform.
 </p>
 @param width The length of the sequence.
 @param fourierOrigin1 The origin of the Fourier transform in the
 Fourier domain.
 ********************************************************************/
public AcademicFFT (
	final int width,
	final int fourierOrigin1
) {
	if (width <= 0) {
		throw(new IllegalArgumentException());
	}
	this.width = new Integer(width);
	this.height = new Integer(1);
	this.depth = new Integer(1);
	this.fourierOrigin1 = fourierOrigin1;
	this.fourierOrigin2 = 0;
	this.fourierOrigin3 = 0;
	dimensions = 1;
	dataLength = width;
	new FFTSetup(width);
	new FFTSetupReal(width);
	new FFTSetupDuoReal(width);
	firstDimension = 1;
} /* end AcademicFFT */

/*------------------------------------------------------------------*/
/*********************************************************************
 <p>
 This constructor prepares a two-dimensional Fourier transform. It
 depends on the width and height of the image but not on the actual
 data being transformed, so that it can be reused for different data.
 It specifies the convention to adopt for locating the origin of the
 Fourier transform.
 </p>
 @param width The width of the image.
 @param height The height of the image.
 @param fourierOrigin1 The horizontal component of the origin of the
 Fourier transform in the Fourier domain.
 @param fourierOrigin2 The vertical component of the origin of the
 Fourier transform in the Fourier domain.
 ********************************************************************/
public AcademicFFT (
	final int width,
	final int height,
	final int fourierOrigin1,
	final int fourierOrigin2
) {
	if ((width <= 0) || (height <= 0)) {
		throw(new IllegalArgumentException());
	}
	this.width = new Integer(width);
	this.height = new Integer(height);
	this.depth = new Integer(1);
	this.fourierOrigin1 = fourierOrigin1;
	this.fourierOrigin2 = fourierOrigin2;
	this.fourierOrigin3 = 0;
	dimensions = 2;
	dataLength = width * height;
	new FFTSetup(width);
	new FFTSetupReal(width);
	new FFTSetupDuoReal(width);
	new FFTSetup(height);
	new FFTSetupReal(height);
	new FFTSetupDuoReal(height);
	final long K1 = (long)width;
	final long K2 = (long)height;
	final long k1 = K1 >> 1L;
	final long k2 = K2 >> 1L;
	final long costColumnFirst = FFTSetup.FLALLOC * 0L
		+ FFTSetup.FLOP * (k2 * 1L + k2 * ((K1 - 1L) * 1L))
		+ FFTSetup.FLASSIGN * (k2 * 2L + k2 * ((K1 - 1L) * 2L))
		+ FFTSetup.INTALLOC * 10L
		+ FFTSetup.INTOP * (5L + K1 * 2L + 1L + (k2 + 1L) * 3L + 2L
			+ k2 * 3L + 3L + k2 * (4L + (K1 - 1L) * 4L))
		+ FFTSetup.INTASSIGN * (4L + K1 * 1L + 2L + (k2 + 1L) * 2L + 2L
			+ k2 * 2L + 3L + k2 * (6L + (K1 - 1L) * 3L))
		+ FFTSetup.IDX * (k2 * 4L + k2 * ((K1 - 1L) * 4L))
		+ FFTSetup.NEWOBJ * (K1 + (k2 + 1L))
		+ k1 * FFTSetupDuoReal.cost(height)
		+ (k2 + 1L) * FFTSetup.cost(width);
	final long costRowFirst = FFTSetup.FLALLOC * 0L
		+ FFTSetup.FLOP * (k1 * 1L + (K2 - 1L) * (k1 * 1L))
		+ FFTSetup.FLASSIGN * (k1 * 2L + (K2 - 1L) * (k1 * 2L))
		+ FFTSetup.INTALLOC * 9L
		+ FFTSetup.INTOP * (5L + K2 * 3L + 1L + (k1 + 1L) * 2L
			+ k1 * 3L + 3L + (K2 - 1L) * (4L + k1 * 4L))
		+ FFTSetup.INTASSIGN * (5L + K2 * 2L + 1L + (k1 + 1L) * 1L + 2L
			+ k1 * 2L + 3L + (K2 - 1L) * (6L + k1 * 3L))
		+ FFTSetup.IDX * (k1 * 4L + (K2 - 1L) * (k1 * 4L))
		+ FFTSetup.NEWOBJ * (K2 + (k1 + 1L))
		+ k2 * FFTSetupDuoReal.cost(width)
		+ (k1 + 1L) * FFTSetup.cost(height);
	firstDimension = (costRowFirst < costColumnFirst) ? (1) : (2);
} /* end AcademicFFT */

/*------------------------------------------------------------------*/
/*********************************************************************
 <p>
 This constructor prepares a three-dimensional Fourier transform. It
 depends on the width, height, and depth of the volume but not on the
 actual data being transformed, so that it can be reused for different
 data. It specifies the convention to adopt for locating the origin of
 the Fourier transform.
 </p>
 @param width The width of the volume.
 @param height The height of the volume.
 @param depth The depth of the volume.
 @param fourierOrigin1 The horizontal component of the origin of the
 Fourier transform in the Fourier domain.
 @param fourierOrigin2 The vertical component of the origin of the
 Fourier transform in the Fourier domain.
 @param fourierOrigin3 The depth component of the origin of the
 Fourier transform in the Fourier domain.
 ********************************************************************/
public AcademicFFT (
	final int width,
	final int height,
	final int depth,
	final int fourierOrigin1,
	final int fourierOrigin2,
	final int fourierOrigin3
) {
	if ((width <= 0) || (height <= 0) || (depth <= 0)) {
		throw(new IllegalArgumentException());
	}
	this.width = new Integer(width);
	this.height = new Integer(height);
	this.depth = new Integer(depth);
	this.fourierOrigin1 = fourierOrigin1;
	this.fourierOrigin2 = fourierOrigin2;
	this.fourierOrigin3 = fourierOrigin3;
	dimensions = 3;
	dataLength = width * height* depth;
	new FFTSetup(width);
	new FFTSetupReal(width);
	new FFTSetupDuoReal(width);
	new FFTSetup(height);
	new FFTSetupReal(height);
	new FFTSetupDuoReal(height);
	new FFTSetup(depth);
	new FFTSetupReal(depth);
	new FFTSetupDuoReal(depth);
	final long K1 = (long)width;
	final long K2 = (long)height;
	final long K3 = (long)depth;
	final long k1 = K1 >> 1L;
	final long k2 = K2 >> 1L;
	final long k3 = K3 >> 1L;
	final long costAcrossFirst = FFTSetup.FLALLOC * 0L
		+ FFTSetup.FLOP * (k3 * 1L + k3  * ((K2 - 1L) * 1L)
			+ k3 * ((K1 - 1L) * 1L) + k3 * ((K2 - 1L) * ((K1 - 1L) * 1L)))
		+ FFTSetup.FLASSIGN * (k3 * 2L + k3  * ((K2 - 1L) * 2L)
			+ k3 * ((K1 - 1L) * 2L) + k3 * ((K2 - 1L) * ((K1 - 1L) * 2L)))
		+ FFTSetup.INTALLOC * 23L
		+ FFTSetup.INTOP * (6L + K2 * K1 * 2L + 1L + (k3 + 1L) * (2L + K2 * 3L)
			+ 1L + (k3 + 1L) * (3L + K1 * 3L) + 1L + k3 * 3L + 2L
			+ k3 * (4L + (K2 - 1L) * 4L) + 5L + k3 * (4L + (K1 - 1L) * 4L) + 3L
			+ k3 * (4L + (K2 - 1L) * (4L + (K1 - 1L) * 4L)))
		+ FFTSetup.INTASSIGN * (6L + K2 * K1 * 1L + 2L
			+ (k3 + 1L) * (2L + K2 * 2L) + 2L + (k3 + 1L) * (3L + K1 * 1L) + 2L
			+ k3 * 2L + 3L + k3 * (6L + (K2 - 1L) * 3L) + 3L
			+ k3 * (6L + (K1 - 1L) * 3L) + 3L
			+ k3 * (4L + (K2 - 1L) * (4L + (K1 - 1L) * 3L)))
		+ FFTSetup.IDX * (k3 * 4L + k3 * ((K2 - 1L) * 4L)
			+ k3 * ((K1 - 1L) * 4L) + k3 * ((K2 - 1L) * ((K1 - 1L) * 4L)))
		+ FFTSetup.NEWOBJ * (K2 * K1 + (k3 + 1L) * K2+ (k3 + 1L) * K1)
		+ ((K2 * K1) >> 1) * FFTSetupDuoReal.cost(depth)
		+ (k3 + 1L) * K2 * FFTSetup.cost(width)
		+ (k3 + 1L) * K1 * FFTSetup.cost(height);
	final long costColumnFirst = FFTSetup.FLALLOC * 0L
		+ FFTSetup.FLOP * (k2 * 1L + k2  * ((K2 - 1L) * 1L)
			+ (K3 - 1L) * (k2 * 1L) + (K3 - 1L) * (k2 * ((K1 - 1L) * 1L)))
		+ FFTSetup.FLASSIGN * (k2 * 2L + k2 * ((K1 - 1L) * 2L)
			+ (K3 - 1L) * (k2 * 2L) + (K3 - 1L) * (k2 * ((K1 - 1L) * 2L)))
		+ FFTSetup.INTALLOC * 26L
		+ FFTSetup.INTOP * (8L + K3 * K1 * 5L + 3L + K3 * (3L + (k2 + 1L) * 3L)
			+ 1L + (k2 + 1L) * (2L + K1 * 3L) + 1L + k2 * 3L + 2L
			+ k2 * (4L + (K1 - 1L) * 4L) + 3L + (K3 - 1L) * (4L + k2 * 4L) + 3L
			+ (K3 - 1L) * (6L + k2 * (4L + (K1 - 1L) * 4L)))
		+ FFTSetup.INTASSIGN * (8L + K3 * K1 * 1L + 3L
			+ K3 * (3L + (k2 + 1L) * 2L) + 2L + (k2 + 1L) * (2L + K1 * 2L) + 2L
			+ k2 * 2L + 3L + k2 * (6L + (K1 - 1L) * 3L) + 3L
			+ (K3 - 1L) * (6L + k2 * 3L) + 3L
			+ (K3 - 1L) * (4L + k2 * (4L + (K1 - 1L) * 3L)))
		+ FFTSetup.IDX * (k2 * 4L + k2 * ((K1 - 1L) * 4L)
			+ (K3 - 1L) * (k2 * 4L) + (K3 - 1L) * (k2 * ((K1 - 1L) * 4L)))
		+ FFTSetup.NEWOBJ * (K3 * K1 + K3 * (k2 + 1L) + (k2 + 1L) * K1)
		+ ((K3 * K1) >> 1) * FFTSetupDuoReal.cost(height)
		+ K3 * (k2 + 1L) * FFTSetup.cost(width)
		+ (k2 + 1L) * K1 * FFTSetup.cost(depth);
	final long costRowFirst = FFTSetup.FLALLOC * 0L
		+ FFTSetup.FLOP * (k1 * 1L + (K2 - 1L) * (k1 * 1L)
			+ (K3 - 1L) * (k1 * 1L) + (K3 - 1L) * ((K2 - 1L) * (k1 * 1L)))
		+ FFTSetup.FLASSIGN * (k1 * 2L + (K2 - 1L) * (k1 * 2L)
			+ (K3 - 1L) * (k1 * 2L) + (K3 - 1L) * ((K2 - 1L) * (k1 * 2L)))
		+ FFTSetup.INTALLOC * 22L
		+ FFTSetup.INTOP * (7L + K3 * K2 * 3L + 1L + K3 * (3L + (k1 + 1L) * 3L)
			+ 1L + K2 * (3L + (k1 + 1L) * 3L) + k1 * 3L + 2L
			+ (K2 - 1L) * (4L + k1 * 4L) + 5L + (K3 - 1L) * (4L + k1 * 4L) + 3L
			+ (K3 - 1L) * (4L + (K2 - 1L) * (4L + k1 * 4L)))
		+ FFTSetup.INTASSIGN * (8L + K3 * K2 * 2L + 2L
			+ K3 * (3L + (k1 + 1L) * 1L) + 2L + K2 * (3L + (k1 + 1L) * 1L) + 2L
			+ k1 * 2L + 3L + (K2 - 1L) * (6L + k1 * 3L) + 3L
			+ (K3 - 1L) * (6L + k1 * 3L) + 3L
			+ (K3 - 1L) * (4L + (K2 - 1L) * (4L + k1 * 3L)))
		+ FFTSetup.IDX * (k1 * 4L + (K2 - 1L) * (k1 * 4L)
			+ (K3 - 1L) * (k1 * 4L) + (K3 - 1L) * ((K2 - 1L) * (k1 * 4L)))
		+ FFTSetup.NEWOBJ * (K3 * K2 + K3 * (k1 + 1L) + K2 * (k1 + 1L))
		+ ((K3 * K2) >> 1) * FFTSetupDuoReal.cost(width)
		+ K3 * (k1 + 1L) * FFTSetup.cost(height)
		+ K2 * (k1 + 1L) * FFTSetup.cost(depth);
	firstDimension = (costRowFirst < costColumnFirst)
		? ((costRowFirst < costAcrossFirst) ? (1) : (3))
		: ((costColumnFirst < costAcrossFirst) ? (2) : (3));
} /* end AcademicFFT */

/*....................................................................
	AcademicFFT static methods
....................................................................*/
/*------------------------------------------------------------------*/
/*********************************************************************
 <p>
 This method converts complex data from an amplitude-phase
 representation to a real-imaginary representation. The phase is
 assumed to be provided in radian units.
 </p>
 @param amToRe At input, the amplitude of the data; at output, the
 real part of the data. The processing is in-place.
 @param phToIm At input, the phase of the data; at output, the
 imaginary part of the data. The processing is in-place.
 ********************************************************************/
static public void amplitudePhaseToRealImaginary (
	final double[] amToRe,
	final double[] phToIm
) {
	if (amToRe.length != phToIm.length) {
		throw(new IllegalArgumentException());
	}
	for (int k = 0, K = amToRe.length; (k < K); k++) {
		final double am = amToRe[k];
		final double ph = phToIm[k];
		amToRe[k] = am * cos(ph);
		phToIm[k] = am * sin(ph);
	}
} /* end amplitudePhaseToRealImaginary */

/*------------------------------------------------------------------*/
/*********************************************************************
 <p>
 This method converts complex data from an amplitude-phase
 representation to a real-imaginary representation. The phase is
 assumed to be provided in radian units.
 </p>
 @param amToRe At input, the amplitude of the data; at output, the
 real part of the data. The processing is in-place.
 @param phToIm At input, the phase of the data; at output, the
 imaginary part of the data. The processing is in-place.
 ********************************************************************/
static public void amplitudePhaseToRealImaginary (
	final float[] amToRe,
	final float[] phToIm
) {
	if (amToRe.length != phToIm.length) {
		throw(new IllegalArgumentException());
	}
	for (int k = 0, K = amToRe.length; (k < K); k++) {
		final float am = amToRe[k];
		final float ph = phToIm[k];
		amToRe[k] = am * (float)cos((double)ph);
		phToIm[k] = am * (float)sin((double)ph);
	}
} /* end amplitudePhaseToRealImaginary */

/*------------------------------------------------------------------*/
/*********************************************************************
 <p>
 This method converts complex data from a real-imaginary
 representation to an amplitude-phase representation. The resulting
 phase is provided in radian units in the range [&#8722;&#960;,
 &#960;].
 </p>
 @param reToAm At input, the real part of the data; at output, the
 amplitude of the data. The processing is in-place.
 @param imToPh At input, the imaginary part of the data; at output,
 the phase of the data. The processing is in-place.
 ********************************************************************/
static public void realImaginaryToAmplitudePhase (
	final double[] reToAm,
	final double[] imToPh
) {
	if (reToAm.length != imToPh.length) {
		throw(new IllegalArgumentException());
	}
	for (int k = 0, K = reToAm.length; (k < K); k++) {
		final double am = sqrt(reToAm[k] * reToAm[k] + imToPh[k] * imToPh[k]);
		final double ph = atan2(imToPh[k], reToAm[k]);
		reToAm[k] = am;
		imToPh[k] = ph;
	}
} /* end realImaginaryToAmplitudePhase */

/*------------------------------------------------------------------*/
/*********************************************************************
 <p>
 This method converts complex data from a real-imaginary
 representation to an amplitude-phase representation. The resulting
 phase is provided in radian units in the range [&#8722;&#960;,
 &#960;].
 </p>
 @param reToAm At input, the real part of the data; at output, the
 amplitude of the data. The processing is in-place.
 @param imToPh At input, the imaginary part of the data; at output,
 the phase of the data. The processing is in-place.
 ********************************************************************/
static public void realImaginaryToAmplitudePhase (
	final float[] reToAm,
	final float[] imToPh
) {
	if (reToAm.length != imToPh.length) {
		throw(new IllegalArgumentException());
	}
	for (int k = 0, K = reToAm.length; (k < K); k++) {
		final float am = (float)sqrt((double)(reToAm[k] * reToAm[k]
			+ imToPh[k] * imToPh[k]));
		final float ph = (float)atan2((double)imToPh[k], (double)reToAm[k]);
		reToAm[k] = am;
		imToPh[k] = ph;
	}
} /* end realImaginaryToAmplitudePhase */

/*------------------------------------------------------------------*/
/*********************************************************************
 <p>
 Several <code>static</code> auxiliary data are stored by this class,
 as needs arise, to be reused by subsequent instances of this class;
 this method frees the associated memory.
 </p>
 ********************************************************************/
static public void reset (
) {
	FFTSetup.reset();
	FFTSetupReal.reset();
	FFTSetupDuoReal.reset();
} /* end reset */

/*....................................................................
	AcademicFFT public methods
....................................................................*/
/*------------------------------------------------------------------*/
/*********************************************************************
 <p>
 This method computes the circular convolution of data provided in a
 real-imaginary representation. The number of dimensions of the data
 is determined by the constructor of this object. Likewise, the
 dimensions themselves must match those provided to the constructor of
 this object.
 </p>
 @param reData1 At input, the real part of the first operand; at
 output, the real part of the convolution result. The processing is
 in-place.
 @param imData1 At input, the imaginary part of the first operand; at
 output, the imaginary part of the convolution result. The processing
 is in-place.
 @param reData2 At input, the real part of the second operand; at
 output, the real part of the Fourier transform of the second operand.
 The origin of the Fourier transform of the second operand follows the
 conventions for this object. The processing is in-place.
 @param imData2 At input, the imaginary part of the second operand; at
 output, the imaginary part of the Fourier transform of the second
 operand. The origin of the Fourier transform of the second operand
 follows the conventions for this object. The processing is in-place.
 @param reBuffer Garbage in, garbage out. A temporary buffer of length
 <code>reData1.length</code> is created internally if
 <code>reBuffer</code> is <code>null</code>.
 @param imBuffer Garbage in, garbage out. A temporary buffer of length
 <code>imData1.length</code> is created internally if
 <code>imBuffer</code> is <code>null</code>.
 ********************************************************************/
public void circularConvolution (
	final double[] reData1,
	final double[] imData1,
	final double[] reData2,
	final double[] imData2,
	double[] reBuffer,
	double[] imBuffer
) {
	if (null == reBuffer) {
		reBuffer = new double[reData1.length];
	}
	if (null == imBuffer) {
		imBuffer = new double[imData1.length];
	}
	if ((reData1.length != dataLength)
		|| (imData1.length != dataLength)
		|| (reData2.length != dataLength)
		|| (imData2.length != dataLength)
		|| (reBuffer.length != dataLength)
		|| (imBuffer.length != dataLength)) {
		throw(new IllegalArgumentException());
	}
	this.reBufferDouble = reBuffer;
	this.imBufferDouble = imBuffer;
	this.reDataDouble = reData2;
	this.imDataDouble = imData2;
	transformDouble(InputDataType.COMPLEXINPUT);
	this.reDataDouble = reData1;
	this.imDataDouble = imData1;
	transformDouble(InputDataType.COMPLEXINPUT);
	final double norm = 1.0 / (double)dataLength;
	for (int k = 0; (k < dataLength); k++) {
		final double re1 = norm * reData1[k];
		final double im1 = norm * imData1[k];
		reData1[k] = re1 * reData2[k] - im1 * imData2[k];
		imData1[k] = re1 * imData2[k] + im1 * reData2[k];
	}
	reverseDouble();
	transformDouble(InputDataType.COMPLEXINPUT);
	this.reDataDouble = reData2;
	this.imDataDouble = imData2;
	this.reBufferDouble = reBuffer;
	this.imBufferDouble = imBuffer;
	switch (dimensions) {
		case 1: {
			shiftDouble(fourierOrigin1);
			break;
		}
		case 2: {
			shiftDouble(fourierOrigin1, fourierOrigin2);
			break;
		}
		case 3: {
			shiftDouble(fourierOrigin1, fourierOrigin2, fourierOrigin3);
			break;
		}
		default: {
			throw(new IllegalStateException());
		}
	}
} /* end circularConvolution */

/*------------------------------------------------------------------*/
/*********************************************************************
 <p>
 This method computes the circular convolution of data provided in a
 real-imaginary representation. The number of dimensions of the data
 is determined by the constructor of this object. Likewise, the
 dimensions themselves must match those provided to the constructor of
 this object.
 </p>
 @param reData1 At input, the real part of the first operand; at
 output, the real part of the convolution result. The processing is
 in-place.
 @param imData1 At input, the imaginary part of the first operand; at
 output, the imaginary part of the convolution result. The processing
 is in-place.
 @param reData2 At input, the real part of the second operand; at
 output, the real part of the Fourier transform of the second operand.
 The origin of the Fourier transform of the second operand follows the
 conventions for this object. The processing is in-place.
 @param imData2 At input, the imaginary part of the second operand; at
 output, the imaginary part of the Fourier transform of the second
 operand. The origin of the Fourier transform of the second operand
 follows the conventions for this object. The processing is in-place.
 @param reBuffer Garbage in, garbage out. A temporary buffer of length
 <code>reData1.length</code> is created internally if
 <code>reBuffer</code> is <code>null</code>.
 @param imBuffer Garbage in, garbage out. A temporary buffer of length
 <code>imData1.length</code> is created internally if
 <code>imBuffer</code> is <code>null</code>.
 ********************************************************************/
public void circularConvolution (
	final float[] reData1,
	final float[] imData1,
	final float[] reData2,
	final float[] imData2,
	float[] reBuffer,
	float[] imBuffer
) {
	if (null == reBuffer) {
		reBuffer = new float[reData1.length];
	}
	if (null == imBuffer) {
		imBuffer = new float[imData1.length];
	}
	if ((reData1.length != dataLength)
		|| (imData1.length != dataLength)
		|| (reData2.length != dataLength)
		|| (imData2.length != dataLength)
		|| (reBuffer.length != dataLength)
		|| (imBuffer.length != dataLength)) {
		throw(new IllegalArgumentException());
	}
	this.reBufferFloat = reBuffer;
	this.imBufferFloat = imBuffer;
	this.reDataFloat = reData2;
	this.imDataFloat = imData2;
	transformFloat(InputDataType.COMPLEXINPUT);
	this.reDataFloat = reData1;
	this.imDataFloat = imData1;
	transformFloat(InputDataType.COMPLEXINPUT);
	final float norm = 1.0F / (float)dataLength;
	for (int k = 0; (k < dataLength); k++) {
		final float re1 = norm * reData1[k];
		final float im1 = norm * imData1[k];
		reData1[k] = re1 * reData2[k] - im1 * imData2[k];
		imData1[k] = re1 * imData2[k] + im1 * reData2[k];
	}
	reverseFloat();
	transformFloat(InputDataType.COMPLEXINPUT);
	this.reDataFloat = reData2;
	this.imDataFloat = imData2;
	this.reBufferFloat = reBuffer;
	this.imBufferFloat = imBuffer;
	switch (dimensions) {
		case 1: {
			shiftFloat(fourierOrigin1);
			break;
		}
		case 2: {
			shiftFloat(fourierOrigin1, fourierOrigin2);
			break;
		}
		case 3: {
			shiftFloat(fourierOrigin1, fourierOrigin2, fourierOrigin3);
			break;
		}
		default: {
			throw(new IllegalStateException());
		}
	}
} /* end circularConvolution */

/*------------------------------------------------------------------*/
/*********************************************************************
 <p>
 This method computes the circular convolution of data; the first
 operand is provided in a real-imaginary representation while the
 second operand is provided in a real-imaginary Fourier
 representation. The number of dimensions of the data is determined by
 the constructor of this object. Likewise, the dimensions themselves
 must match those provided to the constructor of this object.
 </p>
 @param reData1 At input, the real part of the first operand; at
 output, the real part of the convolution result. The processing is
 in-place.
 @param imData1 At input, the imaginary part of the first operand; at
 output, the imaginary part of the convolution result. The processing
 is in-place.
 @param reFourierData2 Real part of the Fourier transform of the
 second operand. The origin of the Fourier transform of the second
 operand follows the conventions for this object.
 @param imFourierData2 Imaginary part of the Fourier transform of the
 second operand. The origin of the Fourier transform of the second
 operand follows the conventions for this object.
 @param reBuffer Garbage in, garbage out. A temporary buffer of length
 <code>reData1.length</code> is created internally if
 <code>reBuffer</code> is <code>null</code>.
 @param imBuffer Garbage in, garbage out. A temporary buffer of length
 <code>imData1.length</code> is created internally if
 <code>imBuffer</code> is <code>null</code>.
 ********************************************************************/
public void circularFourierConvolution (
	final double[] reData1,
	final double[] imData1,
	final double[] reFourierData2,
	final double[] imFourierData2,
	double[] reBuffer,
	double[] imBuffer
) {
	if (null == reBuffer) {
		reBuffer = new double[reData1.length];
	}
	if (null == imBuffer) {
		imBuffer = new double[imData1.length];
	}
	if ((reData1.length != dataLength)
		|| (imData1.length != dataLength)
		|| (reFourierData2.length != dataLength)
		|| (imFourierData2.length != dataLength)
		|| (reBuffer.length != dataLength)
		|| (imBuffer.length != dataLength)) {
		throw(new IllegalArgumentException());
	}
	this.reBufferDouble = reBuffer;
	this.imBufferDouble = imBuffer;
	this.reDataDouble = reData1;
	this.imDataDouble = imData1;
	transformDouble(InputDataType.COMPLEXINPUT);
	switch (dimensions) {
		case 1: {
			shiftDouble(fourierOrigin1);
			break;
		}
		case 2: {
			shiftDouble(fourierOrigin1, fourierOrigin2);
			break;
		}
		case 3: {
			shiftDouble(fourierOrigin1, fourierOrigin2, fourierOrigin3);
			break;
		}
		default: {
			throw(new IllegalStateException());
		}
	}
	final double norm = 1.0 / (double)dataLength;
	for (int k = 0; (k < dataLength); k++) {
		final double re1 = norm * reData1[k];
		final double im1 = norm * imData1[k];
		reData1[k] = re1 * reFourierData2[k] - im1 * imFourierData2[k];
		imData1[k] = re1 * imFourierData2[k] + im1 * reFourierData2[k];
	}
	switch (dimensions) {
		case 1: {
			shiftDouble(-fourierOrigin1);
			break;
		}
		case 2: {
			shiftDouble(-fourierOrigin1, -fourierOrigin2);
			break;
		}
		case 3: {
			shiftDouble(-fourierOrigin1, -fourierOrigin2, -fourierOrigin3);
			break;
		}
		default: {
			throw(new IllegalStateException());
		}
	}
	reverseDouble();
	transformDouble(InputDataType.COMPLEXINPUT);
} /* circularFourierConvolution */

/*------------------------------------------------------------------*/
/*********************************************************************
 <p>
 This method computes the circular convolution of data; the first
 operand is provided in a real-imaginary representation while the
 second operand is provided in a real-imaginary Fourier
 representation. The number of dimensions of the data is determined by
 the constructor of this object. Likewise, the dimensions themselves
 must match those provided to the constructor of this object.
 </p>
 @param reData1 At input, the real part of the first operand; at
 output, the real part of the convolution result. The processing is
 in-place.
 @param imData1 At input, the imaginary part of the first operand; at
 output, the imaginary part of the convolution result. The processing
 is in-place.
 @param reFourierData2 Real part of the Fourier transform of the
 second operand. The origin of the Fourier transform of the second
 operand follows the conventions for this object.
 @param imFourierData2 Imaginary part of the Fourier transform of the
 second operand. The origin of the Fourier transform of the second
 operand follows the conventions for this object.
 @param reBuffer Garbage in, garbage out. A temporary buffer of length
 <code>reData1.length</code> is created internally if
 <code>reBuffer</code> is <code>null</code>.
 @param imBuffer Garbage in, garbage out. A temporary buffer of length
 <code>imData1.length</code> is created internally if
 <code>imBuffer</code> is <code>null</code>.
 ********************************************************************/
public void circularFourierConvolution (
	final float[] reData1,
	final float[] imData1,
	final float[] reFourierData2,
	final float[] imFourierData2,
	float[] reBuffer,
	float[] imBuffer
) {
	if (null == reBuffer) {
		reBuffer = new float[reData1.length];
	}
	if (null == imBuffer) {
		imBuffer = new float[imData1.length];
	}
	if ((reData1.length != dataLength)
		|| (imData1.length != dataLength)
		|| (reFourierData2.length != dataLength)
		|| (imFourierData2.length != dataLength)
		|| (reBuffer.length != dataLength)
		|| (imBuffer.length != dataLength)) {
		throw(new IllegalArgumentException());
	}
	this.reBufferFloat = reBuffer;
	this.imBufferFloat = imBuffer;
	this.reDataFloat = reData1;
	this.imDataFloat = imData1;
	transformFloat(InputDataType.COMPLEXINPUT);
	switch (dimensions) {
		case 1: {
			shiftFloat(fourierOrigin1);
			break;
		}
		case 2: {
			shiftFloat(fourierOrigin1, fourierOrigin2);
			break;
		}
		case 3: {
			shiftFloat(fourierOrigin1, fourierOrigin2, fourierOrigin3);
			break;
		}
		default: {
			throw(new IllegalStateException());
		}
	}
	final float norm = 1.0F / (float)dataLength;
	for (int k = 0; (k < dataLength); k++) {
		final float re1 = norm * reData1[k];
		final float im1 = norm * imData1[k];
		reData1[k] = re1 * reFourierData2[k] - im1 * imFourierData2[k];
		imData1[k] = re1 * imFourierData2[k] + im1 * reFourierData2[k];
	}
	switch (dimensions) {
		case 1: {
			shiftFloat(-fourierOrigin1);
			break;
		}
		case 2: {
			shiftFloat(-fourierOrigin1, -fourierOrigin2);
			break;
		}
		case 3: {
			shiftFloat(-fourierOrigin1, -fourierOrigin2, -fourierOrigin3);
			break;
		}
		default: {
			throw(new IllegalStateException());
		}
	}
	reverseFloat();
	transformFloat(InputDataType.COMPLEXINPUT);
} /* circularFourierConvolution */

/*------------------------------------------------------------------*/
/*********************************************************************
 <p>
 This method computes the Fourier transform of data provided in a
 real-imaginary representation. The number of dimensions of the data
 is determined by the constructor of this object. Likewise, the
 dimensions themselves must match those provided to the constructor of
 this object.
 </p>
 @param reData At input, the real part of the data; at output, the
 real part of the Fourier transform of the data. The processing is
 in-place.
 @param imData At input, the imaginary part of the data; at output,
 the imaginary part of the Fourier transform of the data. The
 processing is in-place.
 @param reBuffer Garbage in, garbage out. A temporary buffer of length
 <code>reData.length</code> is created internally if
 <code>reBuffer</code> is <code>null</code>.
 @param imBuffer Garbage in, garbage out. A temporary buffer of length
 <code>imData.length</code> is created internally if
 <code>imBuffer</code> is <code>null</code>.
 @param inputDataType When set to
 <code>AcademicFFT.InputDataType.REALINPUT</code>, disregards
 the values provided in <code>imData</code> and assumes that every
 element of <code>imData</code> is <code>0.0</code>; when set to
 <code>AcademicFFT.InputDataType.COMPLEXINPUT</code>, honors
 the values provided in <code>imData</code>.
 ********************************************************************/
public void directTransform (
	final double[] reData,
	final double[] imData,
	double[] reBuffer,
	double[] imBuffer,
	final InputDataType inputDataType
) {
	if (null == reBuffer) {
		reBuffer = new double[reData.length];
	}
	if (null == imBuffer) {
		imBuffer = new double[imData.length];
	}
	if ((reData.length != dataLength)
		|| (imData.length != dataLength)
		|| (reBuffer.length != dataLength)
		|| (imBuffer.length != dataLength)) {
		throw(new IllegalArgumentException());
	}
	this.reDataDouble = reData;
	this.imDataDouble = imData;
	this.reBufferDouble = reBuffer;
	this.imBufferDouble = imBuffer;
	transformDouble(inputDataType);
	switch (dimensions) {
		case 1: {
			shiftDouble(fourierOrigin1);
			break;
		}
		case 2: {
			shiftDouble(fourierOrigin1, fourierOrigin2);
			break;
		}
		case 3: {
			shiftDouble(fourierOrigin1, fourierOrigin2, fourierOrigin3);
			break;
		}
		default: {
			throw(new IllegalStateException());
		}
	}
} /* end directTransform */

/*------------------------------------------------------------------*/
/*********************************************************************
 <p>
 This method computes the Fourier transform of data provided in a
 real-imaginary representation. The number of dimensions of the data
 is determined by the constructor of this object. Likewise, the
 dimensions themselves must match those provided to the constructor of
 this object.
 </p>
 @param reData At input, the real part of the data; at output, the
 real part of the Fourier transform of the data. The processing is
 in-place.
 @param imData At input, the imaginary part of the data; at output,
 the imaginary part of the Fourier transform of the data. The
 processing is in-place.
 @param reBuffer Garbage in, garbage out. A temporary buffer of length
 <code>reData.length</code> is created internally if
 <code>reBuffer</code> is <code>null</code>.
 @param imBuffer Garbage in, garbage out. A temporary buffer of length
 <code>imData.length</code> is created internally if
 <code>imBuffer</code> is <code>null</code>.
 @param inputDataType When set to
 <code>AcademicFFT.InputDataType.REALINPUT</code>, disregards
 the values provided in <code>imData</code> and assumes that every
 element of <code>imData</code> is <code>0.0</code>; when set to
 <code>AcademicFFT.InputDataType.COMPLEXINPUT</code>, honors
 the values provided in <code>imData</code>.
 ********************************************************************/
public void directTransform (
	final float[] reData,
	final float[] imData,
	float[] reBuffer,
	float[] imBuffer,
	final InputDataType inputDataType
) {
	if (null == reBuffer) {
		reBuffer = new float[reData.length];
	}
	if (null == imBuffer) {
		imBuffer = new float[imData.length];
	}
	if ((reData.length != dataLength)
		|| (imData.length != dataLength)
		|| (reBuffer.length != dataLength)
		|| (imBuffer.length != dataLength)) {
		throw(new IllegalArgumentException());
	}
	this.reDataFloat = reData;
	this.imDataFloat = imData;
	this.reBufferFloat = reBuffer;
	this.imBufferFloat = imBuffer;
	transformFloat(inputDataType);
	switch (dimensions) {
		case 1: {
			shiftFloat(fourierOrigin1);
			break;
		}
		case 2: {
			shiftFloat(fourierOrigin1, fourierOrigin2);
			break;
		}
		case 3: {
			shiftFloat(fourierOrigin1, fourierOrigin2, fourierOrigin3);
			break;
		}
		default: {
			throw(new IllegalStateException());
		}
	}
} /* end directTransform */

/*------------------------------------------------------------------*/
/*********************************************************************
 <p>
 This method computes the inverse Fourier transform of data provided
 in a real-imaginary representation. The number of dimensions of the
 data is determined by the constructor of this object. Likewise, the
 dimensions themselves must match those provided to the constructor of
 this object.
 </p>
 @param reData At input, the real part of the data; at output, the
 real part of the Fourier transform of the data. The processing is
 in-place.
 @param imData At input, the imaginary part of the data; at output,
 the imaginary part of the Fourier transform of the data. The
 processing is in-place.
 @param reBuffer Garbage in, garbage out. A temporary buffer of length
 <code>reData.length</code> is created internally if
 <code>reBuffer</code> is <code>null</code>.
 @param imBuffer Garbage in, garbage out. A temporary buffer of length
 <code>imData.length</code> is created internally if
 <code>imBuffer</code> is <code>null</code>.
 ********************************************************************/
public void inverseTransform (
	final double[] reData,
	final double[] imData,
	double[] reBuffer,
	double[] imBuffer
) {
	if (null == reBuffer) {
		reBuffer = new double[reData.length];
	}
	if (null == imBuffer) {
		imBuffer = new double[imData.length];
	}
	if ((reData.length != dataLength)
		|| (imData.length != dataLength)
		|| (reBuffer.length != dataLength)
		|| (imBuffer.length != dataLength)) {
		throw(new IllegalArgumentException());
	}
	this.reDataDouble = reData;
	this.imDataDouble = imData;
	this.reBufferDouble = reBuffer;
	this.imBufferDouble = imBuffer;
	final double norm = 1.0 / (double)dataLength;
	for (int k = 0; (k < dataLength); k++) {
		reData[k] *= norm;
		imData[k] *= norm;
	}
	switch (dimensions) {
		case 1: {
			shiftDouble(-fourierOrigin1);
			break;
		}
		case 2: {
			shiftDouble(-fourierOrigin1, -fourierOrigin2);
			break;
		}
		case 3: {
			shiftDouble(-fourierOrigin1, -fourierOrigin2, -fourierOrigin3);
			break;
		}
		default: {
			throw(new IllegalStateException());
		}
	}
	reverseDouble();
	transformDouble(InputDataType.COMPLEXINPUT);
} /* end inverseTransform */

/*------------------------------------------------------------------*/
/*********************************************************************
 <p>
 This method computes the inverse Fourier transform of data provided
 in a real-imaginary representation. The number of dimensions of the
 data is determined by the constructor of this object. Likewise, the
 dimensions themselves must match those provided to the constructor of
 this object.
 </p>
 @param reData At input, the real part of the data; at output, the
 real part of the Fourier transform of the data. The processing is
 in-place.
 @param imData At input, the imaginary part of the data; at output,
 the imaginary part of the Fourier transform of the data. The
 processing is in-place.
 @param reBuffer Garbage in, garbage out. A temporary buffer of length
 <code>reData.length</code> is created internally if
 <code>reBuffer</code> is <code>null</code>.
 @param imBuffer Garbage in, garbage out. A temporary buffer of length
 <code>imData.length</code> is created internally if
 <code>imBuffer</code> is <code>null</code>.
 ********************************************************************/
public void inverseTransform (
	final float[] reData,
	final float[] imData,
	float[] reBuffer,
	float[] imBuffer
) {
	if (null == reBuffer) {
		reBuffer = new float[reData.length];
	}
	if (null == imBuffer) {
		imBuffer = new float[imData.length];
	}
	if ((reData.length != dataLength)
		|| (imData.length != dataLength)
		|| (reBuffer.length != dataLength)
		|| (imBuffer.length != dataLength)) {
		throw(new IllegalArgumentException());
	}
	this.reDataFloat = reData;
	this.imDataFloat = imData;
	this.reBufferFloat = reBuffer;
	this.imBufferFloat = imBuffer;
	final float norm = 1.0F / (float)dataLength;
	for (int k = 0; (k < dataLength); k++) {
		reData[k] *= norm;
		imData[k] *= norm;
	}
	switch (dimensions) {
		case 1: {
			shiftFloat(-fourierOrigin1);
			break;
		}
		case 2: {
			shiftFloat(-fourierOrigin1, -fourierOrigin2);
			break;
		}
		case 3: {
			shiftFloat(-fourierOrigin1, -fourierOrigin2, -fourierOrigin3);
			break;
		}
		default: {
			throw(new IllegalStateException());
		}
	}
	reverseFloat();
	transformFloat(InputDataType.COMPLEXINPUT);
} /* end inverseTransform */

/*------------------------------------------------------------------*/
/*********************************************************************
 <p>
 This method enforces Hermitian symmetry to Fourier data provided in a
 real-imaginary representation. The number of dimensions of the data
 is determined by the constructor of this object. Likewise, the
 dimensions themselves must match those provided to the constructor of
 this object. The output Fourier data <i>Y</i> satisfies
 &#8476;(<i>Y</i>[0]) = &#8476;(<i>X</i>[0]), &#8465;(<i>Y</i>[0]) = 0,
 <i>Y</i>[<i>n</i>] = &#8476;(<i>X</i>[<i>n</i>] + <i>X</i>[<i>K</i>
 &#8722; <i>n</i>]) &#8725; 2 + j &#8465;(<i>X</i>[<i>n</i>] &#8722;
 <i>X</i>[<i>K</i> &#8722; <i>n</i>]) &#8725; 2 for <i>n</i> &#8712;
 [1&#8230;<i>K</i> &#8722; 1], where <i>X</i> is the input Fourier
 data.
 </p>
 @param reData At input, the real part of the data; at output, the
 real part is symmetric with respect to the origin. The processing is
 in-place.
 @param imData At input, the imaginary part of the data; at output,
 the imaginary part is antisymmetric with respect to the origin. The
 processing is in-place.
 @param reBuffer Garbage in, garbage out. A temporary buffer of length
 <code>reData.length</code> is created internally if
 <code>reBuffer</code> is <code>null</code>.
 @param imBuffer Garbage in, garbage out. A temporary buffer of length
 <code>imData.length</code> is created internally if
 <code>imBuffer</code> is <code>null</code>.
 ********************************************************************/
public void makeHermitian (
	final double[] reData,
	final double[] imData,
	double[] reBuffer,
	double[] imBuffer
) {
	if (null == reBuffer) {
		reBuffer = new double[reData.length];
	}
	if (null == imBuffer) {
		imBuffer = new double[imData.length];
	}
	if ((reData.length != dataLength)
		|| (imData.length != dataLength)
		|| (reBuffer.length != dataLength)
		|| (imBuffer.length != dataLength)) {
		throw(new IllegalArgumentException());
	}
	this.reDataDouble = reData;
	this.imDataDouble = imData;
	this.reBufferDouble = reBuffer;
	this.imBufferDouble = imBuffer;
	switch (dimensions) {
		case 1: {
			shiftDouble(-fourierOrigin1);
			break;
		}
		case 2: {
			shiftDouble(-fourierOrigin1, -fourierOrigin2);
			break;
		}
		case 3: {
			shiftDouble(-fourierOrigin1, -fourierOrigin2, -fourierOrigin3);
			break;
		}
		default: {
			throw(new IllegalStateException());
		}
	}
	System.arraycopy(reData, 0, reBuffer, 0, dataLength);
	System.arraycopy(imData, 0, imBuffer, 0, dataLength);
	reverseDouble();
	for (int k = 0; (k < dataLength); k++) {
		reData[k] = 0.5 * (reBuffer[k] + reData[k]);
		imData[k] = 0.5 * (imBuffer[k] - imData[k]);
	}
	switch (dimensions) {
		case 1: {
			shiftDouble(fourierOrigin1);
			break;
		}
		case 2: {
			shiftDouble(fourierOrigin1, fourierOrigin2);
			break;
		}
		case 3: {
			shiftDouble(fourierOrigin1, fourierOrigin2, fourierOrigin3);
			break;
		}
		default: {
			throw(new IllegalStateException());
		}
	}
} /* end makeHermitian */

/*------------------------------------------------------------------*/
/*********************************************************************
 <p>
 This method enforces Hermitian symmetry to Fourier data provided in a
 real-imaginary representation. The number of dimensions of the data
 is determined by the constructor of this object. Likewise, the
 dimensions themselves must match those provided to the constructor of
 this object. The output Fourier data <i>Y</i> satisfies
 &#8476;(<i>Y</i>[0]) = &#8476;(<i>X</i>[0]), &#8465;(<i>Y</i>[0]) = 0,
 <i>Y</i>[<i>n</i>] = &#8476;(<i>X</i>[<i>n</i>] + <i>X</i>[<i>K</i>
 &#8722; <i>n</i>]) &#8725; 2 + j &#8465;(<i>X</i>[<i>n</i>] &#8722;
 <i>X</i>[<i>K</i> &#8722; <i>n</i>]) &#8725; 2 for <i>n</i> &#8712;
 [1&#8230;<i>K</i> &#8722; 1], where <i>X</i> is the input Fourier
 data.
 </p>
 @param reData At input, the real part of the data; at output, the
 real part is symmetric with respect to the origin. The processing is
 in-place.
 @param imData At input, the imaginary part of the data; at output,
 the imaginary part is antisymmetric with respect to the origin. The
 processing is in-place.
 @param reBuffer Garbage in, garbage out. A temporary buffer of length
 <code>reData.length</code> is created internally if
 <code>reBuffer</code> is <code>null</code>.
 @param imBuffer Garbage in, garbage out. A temporary buffer of length
 <code>imData.length</code> is created internally if
 <code>imBuffer</code> is <code>null</code>.
 ********************************************************************/
public void makeHermitian (
	final float[] reData,
	final float[] imData,
	float[] reBuffer,
	float[] imBuffer
) {
	if (null == reBuffer) {
		reBuffer = new float[reData.length];
	}
	if (null == imBuffer) {
		imBuffer = new float[imData.length];
	}
	if ((reData.length != dataLength)
		|| (imData.length != dataLength)
		|| (reBuffer.length != dataLength)
		|| (imBuffer.length != dataLength)) {
		throw(new IllegalArgumentException());
	}
	this.reDataFloat = reData;
	this.imDataFloat = imData;
	this.reBufferFloat = reBuffer;
	this.imBufferFloat = imBuffer;
	switch (dimensions) {
		case 1: {
			shiftFloat(-fourierOrigin1);
			break;
		}
		case 2: {
			shiftFloat(-fourierOrigin1, -fourierOrigin2);
			break;
		}
		case 3: {
			shiftFloat(-fourierOrigin1, -fourierOrigin2, -fourierOrigin3);
			break;
		}
		default: {
			throw(new IllegalStateException());
		}
	}
	System.arraycopy(reData, 0, reBuffer, 0, dataLength);
	System.arraycopy(imData, 0, imBuffer, 0, dataLength);
	reverseFloat();
	for (int k = 0; (k < dataLength); k++) {
		reData[k] = 0.5F * (reBuffer[k] + reData[k]);
		imData[k] = 0.5F * (imBuffer[k] - imData[k]);
	}
	switch (dimensions) {
		case 1: {
			shiftFloat(fourierOrigin1);
			break;
		}
		case 2: {
			shiftFloat(fourierOrigin1, fourierOrigin2);
			break;
		}
		case 3: {
			shiftFloat(fourierOrigin1, fourierOrigin2, fourierOrigin3);
			break;
		}
		default: {
			throw(new IllegalStateException());
		}
	}
} /* end makeHermitian */

/*....................................................................
	AcademicFFT private methods
....................................................................*/
/*------------------------------------------------------------------*/
private void reverseDouble (
) {
	final int K1 = width.intValue();
	final int K2 = height.intValue();
	final int K3 = depth.intValue();
	final int K1K2 = K1 * K2;
	if (1 < K1) {
		int p = 0;
		for (int k3 = 0; (k3 < K3); k3++) {
			for (int k2 = 0; (k2 < K2); k2++) {
				int progressive = p;
				int regressive = p + K1;
				while (++progressive < --regressive) {
					final double reSwap = reDataDouble[progressive];
					reDataDouble[progressive] = reDataDouble[regressive];
					reDataDouble[regressive] = reSwap;
					final double imSwap = imDataDouble[progressive];
					imDataDouble[progressive] = imDataDouble[regressive];
					imDataDouble[regressive] = imSwap;
				}
				p += K1;
			}
		}
	}
	if (1 < K2) {
		int p = 0;
		for (int k3 = 0; (k3 < K3); k3++) {
			int q = p;
			for (int k1 = 0; (k1 < K1); k1++) {
				int progressive = q + K1;
				int regressive = q + K1 * (K2 - 1);
				while (progressive < regressive) {
					final double reSwap = reDataDouble[progressive];
					reDataDouble[progressive] = reDataDouble[regressive];
					reDataDouble[regressive] = reSwap;
					final double imSwap = imDataDouble[progressive];
					imDataDouble[progressive] = imDataDouble[regressive];
					imDataDouble[regressive] = imSwap;
					progressive += K1;
					regressive -= K1;
				}
				q++;
			}
			p += K1K2;
		}
	}
	if (1 < K3) {
		int p = 0;
		for (int k2 = 0; (k2 < K2); k2++) {
			for (int k1 = 0; (k1 < K1); k1++) {
				int progressive = p + K1K2;
				int regressive = p + K1K2 * (K3 - 1);
				while (progressive < regressive) {
					final double reSwap = reDataDouble[progressive];
					reDataDouble[progressive] = reDataDouble[regressive];
					reDataDouble[regressive] = reSwap;
					final double imSwap = imDataDouble[progressive];
					imDataDouble[progressive] = imDataDouble[regressive];
					imDataDouble[regressive] = imSwap;
					progressive += K1K2;
					regressive -= K1K2;
				}
				p++;
			}
		}
	}
} /* end reverseDouble */

/*------------------------------------------------------------------*/
private void reverseFloat (
) {
	final int K1 = width.intValue();
	final int K2 = height.intValue();
	final int K3 = depth.intValue();
	final int K1K2 = K1 * K2;
	if (1 < K1) {
		int p = 0;
		for (int k3 = 0; (k3 < K3); k3++) {
			for (int k2 = 0; (k2 < K2); k2++) {
				int progressive = p;
				int regressive = p + K1;
				while (++progressive < --regressive) {
					final float reSwap = reDataFloat[progressive];
					reDataFloat[progressive] = reDataFloat[regressive];
					reDataFloat[regressive] = reSwap;
					final float imSwap = imDataFloat[progressive];
					imDataFloat[progressive] = imDataFloat[regressive];
					imDataFloat[regressive] = imSwap;
				}
				p += K1;
			}
		}
	}
	if (1 < K2) {
		int p = 0;
		for (int k3 = 0; (k3 < K3); k3++) {
			int q = p;
			for (int k1 = 0; (k1 < K1); k1++) {
				int progressive = q + K1;
				int regressive = q + K1 * (K2 - 1);
				while (progressive < regressive) {
					final float reSwap = reDataFloat[progressive];
					reDataFloat[progressive] = reDataFloat[regressive];
					reDataFloat[regressive] = reSwap;
					final float imSwap = imDataFloat[progressive];
					imDataFloat[progressive] = imDataFloat[regressive];
					imDataFloat[regressive] = imSwap;
					progressive += K1;
					regressive -= K1;
				}
				q++;
			}
			p += K1K2;
		}
	}
	if (1 < K3) {
		int p = 0;
		for (int k2 = 0; (k2 < K2); k2++) {
			for (int k1 = 0; (k1 < K1); k1++) {
				int progressive = p + K1K2;
				int regressive = p + K1K2 * (K3 - 1);
				while (progressive < regressive) {
					final float reSwap = reDataFloat[progressive];
					reDataFloat[progressive] = reDataFloat[regressive];
					reDataFloat[regressive] = reSwap;
					final float imSwap = imDataFloat[progressive];
					imDataFloat[progressive] = imDataFloat[regressive];
					imDataFloat[regressive] = imSwap;
					progressive += K1K2;
					regressive -= K1K2;
				}
				p++;
			}
		}
	}
} /* end reverseFloat */

/*------------------------------------------------------------------*/
private void shiftDouble (
	int o1
) {
	final int K1 = width.intValue();
	o1 = (o1 < 0) ? (o1 + K1 * ((K1 - 1 - o1) / K1)) : (o1 - K1 * (o1 / K1));
	if (0 == o1) {
		return;
	}
	final int range1 = K1 - o1;
	System.arraycopy(reDataDouble, 0, reBufferDouble, o1,
		range1);
	System.arraycopy(imDataDouble, 0, imBufferDouble, o1,
		range1);
	System.arraycopy(reDataDouble, range1, reBufferDouble, 0,
		o1);
	System.arraycopy(imDataDouble, range1, imBufferDouble, 0,
		o1);
	System.arraycopy(reBufferDouble, 0, reDataDouble, 0,
		K1);
	System.arraycopy(imBufferDouble, 0, imDataDouble, 0,
		K1);
} /* end shiftDouble */

/*------------------------------------------------------------------*/
private void shiftDouble (
	int o1,
	int o2
) {
	final int K1 = width.intValue();
	final int K2 = height.intValue();
	o1 = (o1 < 0) ? (o1 + K1 * ((K1 - 1 - o1) / K1)) : (o1 - K1 * (o1 / K1));
	o2 = (o2 < 0) ? (o2 + K2 * ((K2 - 1 - o2) / K2)) : (o2 - K2 * (o2 / K2));
	if ((0 == o1) && (0 == o2)) {
		return;
	}
	final int range1 = K1 - o1;
	final int range2 = K2 - o2;
	int p = 0;
	for (int k2 = 0; (k2 < K2); k2++) {
		System.arraycopy(reDataDouble, p, reBufferDouble, p + o1,
			range1);
		System.arraycopy(imDataDouble, p, imBufferDouble, p + o1,
			range1);
		System.arraycopy(reDataDouble, p + range1, reBufferDouble, p,
			o1);
		System.arraycopy(imDataDouble, p + range1, imBufferDouble, p,
			o1);
		p += K1;
	}
	System.arraycopy(reBufferDouble, 0, reDataDouble, o2 * K1,
		range2 * K1);
	System.arraycopy(imBufferDouble, 0, imDataDouble, o2 * K1,
		range2 * K1);
	System.arraycopy(reBufferDouble, range2 * K1, reDataDouble, 0,
		o2 * K1);
	System.arraycopy(imBufferDouble, range2 * K1, imDataDouble, 0,
		o2 * K1);
} /* end shiftDouble */

/*------------------------------------------------------------------*/
private void shiftDouble (
	int o1,
	int o2,
	int o3
) {
	final int K1 = width.intValue();
	final int K2 = height.intValue();
	final int K3 = depth.intValue();
	o1 = (o1 < 0) ? (o1 + K1 * ((K1 - 1 - o1) / K1)) : (o1 - K1 * (o1 / K1));
	o2 = (o2 < 0) ? (o2 + K2 * ((K2 - 1 - o2) / K2)) : (o2 - K2 * (o2 / K2));
	o3 = (o3 < 0) ? (o3 + K3 * ((K3 - 1 - o3) / K3)) : (o3 - K3 * (o3 / K3));
	if ((0 == o1) && (0 == o2) && (0 == o3)) {
		return;
	}
	final int K1K2 = K1 * K2;
	final int range1 = K1 - o1;
	final int range2 = K2 - o2;
	final int range3 = K3 - o3;
	int p = 0;
	for (int k3 = 0; (k3 < K3); k3++) {
		for (int k2 = 0; (k2 < K2); k2++) {
			System.arraycopy(reDataDouble, p, reBufferDouble, p + o1,
				range1);
			System.arraycopy(imDataDouble, p, imBufferDouble, p + o1,
				range1);
			System.arraycopy(reDataDouble, p + range1, reBufferDouble, p,
				o1);
			System.arraycopy(imDataDouble, p + range1, imBufferDouble, p,
				o1);
			p += K1;
		}
	}
	p = 0;
	for (int k3 = 0; (k3 < K3); k3++) {
		System.arraycopy(reBufferDouble, p, reDataDouble, o2 * K1,
			range2 * K1);
		System.arraycopy(imBufferDouble, p, imDataDouble, o2 * K1,
			range2 * K1);
		System.arraycopy(reBufferDouble, range2 * K1, reDataDouble, p,
			o2 * K1);
		System.arraycopy(imBufferDouble, range2 * K1, imDataDouble, p,
			fourierOrigin2 * K1);
		p += K1K2;
	}
	System.arraycopy(reDataDouble, 0, reBufferDouble, o3 * K1K2,
		range3 * K1K2);
	System.arraycopy(imDataDouble, 0, imBufferDouble, o3 * K1K2,
		range3 * K1K2);
	System.arraycopy(reDataDouble, range3 * K1K2, reBufferDouble, 0,
		o3 * K1K2);
	System.arraycopy(imDataDouble, range3 * K1K2, imBufferDouble, 0,
		o3 * K1K2);
	System.arraycopy(reBufferDouble, 0, reDataDouble, 0,
		dataLength);
	System.arraycopy(imBufferDouble, 0, imDataDouble, 0,
		dataLength);
} /* end shiftDouble */

/*------------------------------------------------------------------*/
private void shiftFloat (
	int o1
) {
	final int K1 = width.intValue();
	o1 = (o1 < 0) ? (o1 + K1 * ((K1 - 1 - o1) / K1)) : (o1 - K1 * (o1 / K1));
	if (0 == o1) {
		return;
	}
	final int range1 = K1 - o1;
	System.arraycopy(reDataFloat, 0, reBufferFloat, o1,
		range1);
	System.arraycopy(imDataFloat, 0, imBufferFloat, o1,
		range1);
	System.arraycopy(reDataFloat, range1, reBufferFloat, 0,
		o1);
	System.arraycopy(imDataFloat, range1, imBufferFloat, 0,
		o1);
	System.arraycopy(reBufferFloat, 0, reDataFloat, 0,
		K1);
	System.arraycopy(imBufferFloat, 0, imDataFloat, 0,
		K1);
} /* end shiftFloat */

/*------------------------------------------------------------------*/
private void shiftFloat (
	int o1,
	int o2
) {
	final int K1 = width.intValue();
	final int K2 = height.intValue();
	o1 = (o1 < 0) ? (o1 + K1 * ((K1 - 1 - o1) / K1)) : (o1 - K1 * (o1 / K1));
	o2 = (o2 < 0) ? (o2 + K2 * ((K2 - 1 - o2) / K2)) : (o2 - K2 * (o2 / K2));
	if ((0 == o1) && (0 == o2)) {
		return;
	}
	final int range1 = K1 - o1;
	final int range2 = K2 - o2;
	int p = 0;
	for (int k2 = 0; (k2 < K2); k2++) {
		System.arraycopy(reDataFloat, p, reBufferFloat, p + o1,
			range1);
		System.arraycopy(imDataFloat, p, imBufferFloat, p + o1,
			range1);
		System.arraycopy(reDataFloat, p + range1, reBufferFloat, p,
			o1);
		System.arraycopy(imDataFloat, p + range1, imBufferFloat, p,
			o1);
		p += K1;
	}
	System.arraycopy(reBufferFloat, 0, reDataFloat, o2 * K1,
		range2 * K1);
	System.arraycopy(imBufferFloat, 0, imDataFloat, o2 * K1,
		range2 * K1);
	System.arraycopy(reBufferFloat, range2 * K1, reDataFloat, 0,
		o2 * K1);
	System.arraycopy(imBufferFloat, range2 * K1, imDataFloat, 0,
		o2 * K1);
} /* end shiftFloat */

/*------------------------------------------------------------------*/
private void shiftFloat (
	int o1,
	int o2,
	int o3
) {
	final int K1 = width.intValue();
	final int K2 = height.intValue();
	final int K3 = depth.intValue();
	o1 = (o1 < 0) ? (o1 + K1 * ((K1 - 1 - o1) / K1)) : (o1 - K1 * (o1 / K1));
	o2 = (o2 < 0) ? (o2 + K2 * ((K2 - 1 - o2) / K2)) : (o2 - K2 * (o2 / K2));
	o3 = (o3 < 0) ? (o3 + K3 * ((K3 - 1 - o3) / K3)) : (o3 - K3 * (o3 / K3));
	if ((0 == o1) && (0 == o2) && (0 == o3)) {
		return;
	}
	final int K1K2 = K1 * K2;
	final int range1 = K1 - o1;
	final int range2 = K2 - o2;
	final int range3 = K3 - o3;
	int p = 0;
	for (int k3 = 0; (k3 < K3); k3++) {
		for (int k2 = 0; (k2 < K2); k2++) {
			System.arraycopy(reDataFloat, p, reBufferFloat, p + o1,
				range1);
			System.arraycopy(imDataFloat, p, imBufferFloat, p + o1,
				range1);
			System.arraycopy(reDataFloat, p + range1, reBufferFloat, p,
				o1);
			System.arraycopy(imDataFloat, p + range1, imBufferFloat, p,
				o1);
			p += K1;
		}
	}
	p = 0;
	for (int k3 = 0; (k3 < K3); k3++) {
		System.arraycopy(reBufferFloat, p, reDataFloat, o2 * K1,
			range2 * K1);
		System.arraycopy(imBufferFloat, p, imDataFloat, o2 * K1,
			range2 * K1);
		System.arraycopy(reBufferFloat, range2 * K1, reDataFloat, p,
			o2 * K1);
		System.arraycopy(imBufferFloat, range2 * K1, imDataFloat, p,
			fourierOrigin2 * K1);
		p += K1K2;
	}
	System.arraycopy(reDataFloat, 0, reBufferFloat, o3 * K1K2,
		range3 * K1K2);
	System.arraycopy(imDataFloat, 0, imBufferFloat, o3 * K1K2,
		range3 * K1K2);
	System.arraycopy(reDataFloat, range3 * K1K2, reBufferFloat, 0,
		o3 * K1K2);
	System.arraycopy(imDataFloat, range3 * K1K2, imBufferFloat, 0,
		o3 * K1K2);
	System.arraycopy(reBufferFloat, 0, reDataFloat, 0,
		dataLength);
	System.arraycopy(imBufferFloat, 0, imDataFloat, 0,
		dataLength);
} /* end shiftFloat */

/*------------------------------------------------------------------*/
private void transformDouble (
	final InputDataType inputDataType
) {
	switch (inputDataType) {
		case COMPLEXINPUT: {
			switch (dimensions) {
				case 1: {
					transformDouble1D();
					break;
				}
				case 2: {
					transformDouble2D();
					break;
				}
				case 3: {
					transformDouble3D();
					break;
				}
				default: {
					throw(new IllegalStateException());
				}
			}
			break;
		}
		case REALINPUT: {
			switch (dimensions) {
				case 1: {
					transformRealDouble1D();
					break;
				}
				case 2: {
					switch (firstDimension) {
						case 1: {
							transformRealDouble2DRowFirst();
							break;
						}
						case 2: {
							transformRealDouble2DColumnFirst();
							break;
						}
						default: {
							throw(new IllegalStateException());
						}
					}
					break;
				}
				case 3: {
					switch (firstDimension) {
						case 1: {
							transformRealDouble3DRowFirst();
							break;
						}
						case 2: {
							transformRealDouble3DColumnFirst();
							break;
						}
						case 3: {
							transformRealDouble3DAcrossFirst();
							break;
						}
						default: {
							throw(new IllegalStateException());
						}
					}
					break;
				}
				default: {
					throw(new IllegalStateException());
				}
			}
			break;
		}
	}
} /* end transformDouble */

/*------------------------------------------------------------------*/
private void transformDouble1D (
) {
	final FFTSetup fft = FFTSetup.transforms.get(width);
	switch (fft.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceDouble(reDataDouble, imDataDouble,
				reBufferDouble, imBufferDouble, 0, 1,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorDouble(reDataDouble, imDataDouble,
				reBufferDouble, imBufferDouble, 0, 1,
				fft.ruritanian, fft.chinese, fft.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			new DFTLength2Double(reDataDouble, imDataDouble, 0, 1).run();
			break;
		}
		case LENGTH3: {
			new DFTLength3Double(reDataDouble, imDataDouble, 0, 1).run();
			break;
		}
		case LENGTH4: {
			new DFTLength4Double(reDataDouble, imDataDouble, 0, 1).run();
			break;
		}
		case LENGTH5: {
			new DFTLength5Double(reDataDouble, imDataDouble, 0, 1).run();
			break;
		}
		case LENGTH6: {
			new DFTLength6Double(reDataDouble, imDataDouble, 0, 1).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8Double(reDataDouble, imDataDouble, 0, 1).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixDouble(reDataDouble, imDataDouble,
				reBufferDouble, imBufferDouble, 0, 1,
				fft.reUnitRootDouble, fft.imUnitRootDouble, fft.K1).run();
			break;
		}
		case PADDEDRADER: {
			new DFTPaddedRaderDouble(reDataDouble, imDataDouble, 0, 1,
				fft.reConvolverDouble, fft.imConvolverDouble,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADER: {
			new DFTRaderDouble(reDataDouble, imDataDouble,
				reBufferDouble, imBufferDouble, 0, 1,
				fft.reConvolverDouble, fft.imConvolverDouble,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADIX2: {
			new DFTRadix2Double(reDataDouble, imDataDouble,
				reBufferDouble, imBufferDouble, 0, 1,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixDouble(reDataDouble, imDataDouble,
				reBufferDouble, imBufferDouble, 0, 1,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
	}
} /* end transformDouble1D */

/*------------------------------------------------------------------*/
private void transformDouble2D (
) {
	final int K1 = width.intValue();
	final int K2 = height.intValue();
	// Rows
	final FFTSetup fft1 = FFTSetup.transforms.get(width);
	final ExecutorService executor1 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft1.algorithm) {
		case BRUTEFORCE: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTBruteForceDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble));
				p += K1;
			}
			break;
		}
		case COPRIMEFACTOR: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTCoprimeFactorDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft1.ruritanian, fft1.chinese, fft1.K1));
				p += K1;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTLength2Double(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH3: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTLength3Double(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH4: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTLength4Double(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH5: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTLength5Double(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH6: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTLength6Double(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH8: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTLength8Double(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case MIXEDRADIX: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTMixedRadixDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble, fft1.K1));
				p += K1;
			}
			break;
		}
		case PADDEDRADER: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTPaddedRaderDouble(
					reDataDouble, imDataDouble, p, 1,
					fft1.reConvolverDouble, fft1.imConvolverDouble,
					fft1.modular, fft1.inverseModular));
				p += K1;
			}
			break;
		}
		case RADER: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTRaderDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft1.reConvolverDouble, fft1.imConvolverDouble,
					fft1.modular, fft1.inverseModular));
				p += K1;
			}
			break;
		}
		case RADIX2: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTRadix2Double(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble));
				p += K1;
			}
			break;
		}
		case SPLITRADIX: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTSplitRadixDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble));
				p += K1;
			}
			break;
		}
	}
	try {
		executor1.shutdown();
		executor1.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	// Columns
	final FFTSetup fft2 = FFTSetup.transforms.get(height);
	final ExecutorService executor2 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTBruteForceDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1, K1,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble));
			}
			break;
		}
		case COPRIMEFACTOR: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTCoprimeFactorDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1, K1,
					fft2.ruritanian, fft2.chinese, fft2.K1));
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTLength2Double(
					reDataDouble, imDataDouble, k1, K1));
			}
			break;
		}
		case LENGTH3: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTLength3Double(
					reDataDouble, imDataDouble, k1, K1));
			}
			break;
		}
		case LENGTH4: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTLength4Double(
					reDataDouble, imDataDouble, k1, K1));
			}
			break;
		}
		case LENGTH5: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTLength5Double(
					reDataDouble, imDataDouble, k1, K1));
			}
			break;
		}
		case LENGTH6: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTLength6Double(
					reDataDouble, imDataDouble, k1, K1));
			}
			break;
		}
		case LENGTH8: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTLength8Double(
					reDataDouble, imDataDouble, k1, K1));
			}
			break;
		}
		case MIXEDRADIX: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTMixedRadixDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1, K1,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble, fft2.K1));
			}
			break;
		}
		case PADDEDRADER: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTPaddedRaderDouble(
					reDataDouble, imDataDouble, k1, K1,
					fft2.reConvolverDouble, fft2.imConvolverDouble,
					fft2.modular, fft2.inverseModular));
			}
			break;
		}
		case RADER: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTRaderDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1, K1,
					fft2.reConvolverDouble, fft2.imConvolverDouble,
					fft2.modular, fft2.inverseModular));
			}
			break;
		}
		case RADIX2: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTRadix2Double(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1, K1,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble));
			}
			break;
		}
		case SPLITRADIX: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTSplitRadixDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1, K1,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble));
			}
			break;
		}
	}
	try {
		executor2.shutdown();
		executor2.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
} /* end transformDouble2D */

/*------------------------------------------------------------------*/
private void transformDouble3D (
) {
	final int K1 = width.intValue();
	final int K2 = height.intValue();
	final int K3 = depth.intValue();
	final int K1K2 = K1 * K2;
	// Rows
	final FFTSetup fft1 = FFTSetup.transforms.get(width);
	final ExecutorService executor1 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft1.algorithm) {
		case BRUTEFORCE: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTBruteForceDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, 1,
						fft1.reUnitRootDouble, fft1.imUnitRootDouble));
					p += K1;
				}
			}
			break;
		}
		case COPRIMEFACTOR: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTCoprimeFactorDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, 1,
						fft1.ruritanian, fft1.chinese, fft1.K1));
					p += K1;
				}
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTLength2Double(
						reDataDouble, imDataDouble, p, 1));
					p += K1;
				}
			}
			break;
		}
		case LENGTH3: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTLength3Double(
						reDataDouble, imDataDouble, p, 1));
					p += K1;
				}
			}
			break;
		}
		case LENGTH4: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTLength4Double(
						reDataDouble, imDataDouble, p, 1));
					p += K1;
				}
			}
			break;
		}
		case LENGTH5: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTLength5Double(
						reDataDouble, imDataDouble, p, 1));
					p += K1;
				}
			}
			break;
		}
		case LENGTH6: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTLength6Double(
						reDataDouble, imDataDouble, p, 1));
					p += K1;
				}
			}
			break;
		}
		case LENGTH8: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTLength8Double(
						reDataDouble, imDataDouble, p, 1));
					p += K1;
				}
			}
			break;
		}
		case MIXEDRADIX: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTMixedRadixDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, 1,
						fft1.reUnitRootDouble, fft1.imUnitRootDouble, fft1.K1));
					p += K1;
				}
			}
			break;
		}
		case PADDEDRADER: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTPaddedRaderDouble(
						reDataDouble, imDataDouble, p, 1,
						fft1.reConvolverDouble, fft1.imConvolverDouble,
						fft1.modular, fft1.inverseModular));
					p += K1;
				}
			}
			break;
		}
		case RADER: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTRaderDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, 1,
						fft1.reConvolverDouble, fft1.imConvolverDouble,
						fft1.modular, fft1.inverseModular));
					p += K1;
				}
			}
			break;
		}
		case RADIX2: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTRadix2Double(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, 1,
						fft1.reUnitRootDouble, fft1.imUnitRootDouble));
					p += K1;
				}
			}
			break;
		}
		case SPLITRADIX: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTSplitRadixDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, 1,
						fft1.reUnitRootDouble, fft1.imUnitRootDouble));
					p += K1;
				}
			}
			break;
		}
	}
	try {
		executor1.shutdown();
		executor1.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	// Columns
	final FFTSetup fft2 = FFTSetup.transforms.get(height);
	final ExecutorService executor2 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTBruteForceDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1,
						fft2.reUnitRootDouble, fft2.imUnitRootDouble));
				}
				p += K1K2;
			}
			break;
		}
		case COPRIMEFACTOR: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTCoprimeFactorDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1,
						fft2.ruritanian, fft2.chinese, fft2.K1));
				}
				p += K1K2;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTLength2Double(
						reDataDouble, imDataDouble, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH3: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTLength3Double(
						reDataDouble, imDataDouble, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH4: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTLength4Double(
						reDataDouble, imDataDouble, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH5: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTLength5Double(
						reDataDouble, imDataDouble, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH6: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTLength6Double(
						reDataDouble, imDataDouble, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH8: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTLength8Double(
						reDataDouble, imDataDouble, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case MIXEDRADIX: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTMixedRadixDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1,
						fft2.reUnitRootDouble, fft2.imUnitRootDouble, fft2.K1));
				}
				p += K1K2;
			}
			break;
		}
		case PADDEDRADER: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTPaddedRaderDouble(
						reDataDouble, imDataDouble, p + k1, K1,
						fft2.reConvolverDouble, fft2.imConvolverDouble,
						fft2.modular, fft2.inverseModular));
				}
				p += K1K2;
			}
			break;
		}
		case RADER: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTRaderDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1,
						fft2.reConvolverDouble, fft2.imConvolverDouble,
						fft2.modular, fft2.inverseModular));
				}
				p += K1K2;
			}
			break;
		}
		case RADIX2: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTRadix2Double(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1,
						fft2.reUnitRootDouble, fft2.imUnitRootDouble));
				}
				p += K1K2;
			}
			break;
		}
		case SPLITRADIX: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTSplitRadixDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1,
						fft2.reUnitRootDouble, fft2.imUnitRootDouble));
				}
				p += K1K2;
			}
			break;
		}
	}
	try {
		executor2.shutdown();
		executor2.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	// Across
	final FFTSetup fft3 = FFTSetup.transforms.get(depth);
	final ExecutorService executor3 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft3.algorithm) {
		case BRUTEFORCE: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTBruteForceDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, K1K2,
						fft3.reUnitRootDouble, fft3.imUnitRootDouble));
					p++;
				}
			}
			break;
		}
		case COPRIMEFACTOR: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTCoprimeFactorDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, K1K2,
						fft3.ruritanian, fft3.chinese, fft3.K1));
					p++;
				}
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength2Double(
						reDataDouble, imDataDouble, p, K1K2));
					p++;
				}
			}
			break;
		}
		case LENGTH3: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength3Double(
						reDataDouble, imDataDouble, p, K1K2));
					p++;
				}
			}
			break;
		}
		case LENGTH4: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength4Double(
						reDataDouble, imDataDouble, p, K1K2));
					p++;
				}
			}
			break;
		}
		case LENGTH5: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength5Double(
						reDataDouble, imDataDouble, p, K1K2));
					p++;
				}
			}
			break;
		}
		case LENGTH6: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength6Double(
						reDataDouble, imDataDouble, p, K1K2));
					p++;
				}
			}
			break;
		}
		case LENGTH8: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength8Double(
						reDataDouble, imDataDouble, p, K1K2));
					p++;
				}
			}
			break;
		}
		case MIXEDRADIX: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTMixedRadixDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, K1K2,
						fft3.reUnitRootDouble, fft3.imUnitRootDouble, fft3.K1));
					p++;
				}
			}
			break;
		}
		case PADDEDRADER: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTPaddedRaderDouble(
						reDataDouble, imDataDouble, p, K1K2,
						fft3.reConvolverDouble, fft3.imConvolverDouble,
						fft3.modular, fft3.inverseModular));
					p++;
				}
			}
			break;
		}
		case RADER: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTRaderDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, K1K2,
						fft3.reConvolverDouble, fft3.imConvolverDouble,
						fft3.modular, fft3.inverseModular));
					p++;
				}
			}
			break;
		}
		case RADIX2: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTRadix2Double(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, K1K2,
						fft3.reUnitRootDouble, fft3.imUnitRootDouble));
					p++;
				}
			}
			break;
		}
		case SPLITRADIX: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTSplitRadixDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, K1K2,
						fft3.reUnitRootDouble, fft3.imUnitRootDouble));
					p++;
				}
			}
			break;
		}
	}
	try {
		executor3.shutdown();
		executor3.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
} /* end transformDouble3D */

/*------------------------------------------------------------------*/
private void transformFloat (
	final InputDataType inputDataType
) {
	switch (inputDataType) {
		case COMPLEXINPUT: {
			switch (dimensions) {
				case 1: {
					transformFloat1D();
					break;
				}
				case 2: {
					transformFloat2D();
					break;
				}
				case 3: {
					transformFloat3D();
					break;
				}
				default: {
					throw(new IllegalStateException());
				}
			}
			break;
		}
		case REALINPUT: {
			switch (dimensions) {
				case 1: {
					transformRealFloat1D();
					break;
				}
				case 2: {
					switch (firstDimension) {
						case 1: {
							transformRealFloat2DRowFirst();
							break;
						}
						case 2: {
							transformRealFloat2DColumnFirst();
							break;
						}
						default: {
							throw(new IllegalStateException());
						}
					}
					break;
				}
				case 3: {
					switch (firstDimension) {
						case 1: {
							transformRealFloat3DRowFirst();
							break;
						}
						case 2: {
							transformRealFloat3DColumnFirst();
							break;
						}
						case 3: {
							transformRealFloat3DAcrossFirst();
							break;
						}
						default: {
							throw(new IllegalStateException());
						}
					}
					break;
				}
				default: {
					throw(new IllegalStateException());
				}
			}
			break;
		}
	}
} /* end transformFloat */

/*------------------------------------------------------------------*/
private void transformFloat1D (
) {
	final FFTSetup fft = FFTSetup.transforms.get(width);
	switch (fft.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceFloat(reDataFloat, imDataFloat,
				reBufferFloat, imBufferFloat, 0, 1,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorFloat(reDataFloat, imDataFloat,
				reBufferFloat, imBufferFloat, 0, 1,
				fft.ruritanian, fft.chinese, fft.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			new DFTLength2Float(reDataFloat, imDataFloat, 0, 1).run();
			break;
		}
		case LENGTH3: {
			new DFTLength3Float(reDataFloat, imDataFloat, 0, 1).run();
			break;
		}
		case LENGTH4: {
			new DFTLength4Float(reDataFloat, imDataFloat, 0, 1).run();
			break;
		}
		case LENGTH5: {
			new DFTLength5Float(reDataFloat, imDataFloat, 0, 1).run();
			break;
		}
		case LENGTH6: {
			new DFTLength6Float(reDataFloat, imDataFloat, 0, 1).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8Float(reDataFloat, imDataFloat, 0, 1).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixFloat(reDataFloat, imDataFloat,
				reBufferFloat, imBufferFloat, 0, 1,
				fft.reUnitRootFloat, fft.imUnitRootFloat, fft.K1).run();
			break;
		}
		case PADDEDRADER: {
			new DFTPaddedRaderFloat(reDataFloat, imDataFloat, 0, 1,
				fft.reConvolverFloat, fft.imConvolverFloat,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADER: {
			new DFTRaderFloat(reDataFloat, imDataFloat,
				reBufferFloat, imBufferFloat, 0, 1,
				fft.reConvolverFloat, fft.imConvolverFloat,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADIX2: {
			new DFTRadix2Float(reDataFloat, imDataFloat,
				reBufferFloat, imBufferFloat, 0, 1,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixFloat(reDataFloat, imDataFloat,
				reBufferFloat, imBufferFloat, 0, 1,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
	}
} /* end transformFloat1D */

/*------------------------------------------------------------------*/
private void transformFloat2D (
) {
	final int K1 = width.intValue();
	final int K2 = height.intValue();
	// Rows
	final FFTSetup fft1 = FFTSetup.transforms.get(width);
	final ExecutorService executor1 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft1.algorithm) {
		case BRUTEFORCE: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTBruteForceFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat));
				p += K1;
			}
			break;
		}
		case COPRIMEFACTOR: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTCoprimeFactorFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft1.ruritanian, fft1.chinese, fft1.K1));
				p += K1;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTLength2Float(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH3: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTLength3Float(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH4: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTLength4Float(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH5: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTLength5Float(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH6: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTLength6Float(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH8: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTLength8Float(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case MIXEDRADIX: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTMixedRadixFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat, fft1.K1));
				p += K1;
			}
			break;
		}
		case PADDEDRADER: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTPaddedRaderFloat(
					reDataFloat, imDataFloat, p, 1,
					fft1.reConvolverFloat, fft1.imConvolverFloat,
					fft1.modular, fft1.inverseModular));
				p += K1;
			}
			break;
		}
		case RADER: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTRaderFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft1.reConvolverFloat, fft1.imConvolverFloat,
					fft1.modular, fft1.inverseModular));
				p += K1;
			}
			break;
		}
		case RADIX2: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTRadix2Float(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat));
				p += K1;
			}
			break;
		}
		case SPLITRADIX: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				executor1.execute(new DFTSplitRadixFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat));
				p += K1;
			}
			break;
		}
	}
	try {
		executor1.shutdown();
		executor1.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	// Columns
	final FFTSetup fft2 = FFTSetup.transforms.get(height);
	final ExecutorService executor2 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTBruteForceFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1, K1,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat));
			}
			break;
		}
		case COPRIMEFACTOR: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTCoprimeFactorFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1, K1,
					fft2.ruritanian, fft2.chinese, fft2.K1));
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTLength2Float(
					reDataFloat, imDataFloat, k1, K1));
			}
			break;
		}
		case LENGTH3: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTLength3Float(
					reDataFloat, imDataFloat, k1, K1));
			}
			break;
		}
		case LENGTH4: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTLength4Float(
					reDataFloat, imDataFloat, k1, K1));
			}
			break;
		}
		case LENGTH5: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTLength5Float(
					reDataFloat, imDataFloat, k1, K1));
			}
			break;
		}
		case LENGTH6: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTLength6Float(
					reDataFloat, imDataFloat, k1, K1));
			}
			break;
		}
		case LENGTH8: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTLength8Float(
					reDataFloat, imDataFloat, k1, K1));
			}
			break;
		}
		case MIXEDRADIX: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTMixedRadixFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1, K1,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat, fft2.K1));
			}
			break;
		}
		case PADDEDRADER: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTPaddedRaderFloat(
					reDataFloat, imDataFloat, k1, K1,
					fft2.reConvolverFloat, fft2.imConvolverFloat,
					fft2.modular, fft2.inverseModular));
			}
			break;
		}
		case RADER: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTRaderFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1, K1,
					fft2.reConvolverFloat, fft2.imConvolverFloat,
					fft2.modular, fft2.inverseModular));
			}
			break;
		}
		case RADIX2: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTRadix2Float(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1, K1,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat));
			}
			break;
		}
		case SPLITRADIX: {
			for (int k1 = 0; (k1 < K1); k1++) {
				executor2.execute(new DFTSplitRadixFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1, K1,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat));
			}
			break;
		}
	}
	try {
		executor2.shutdown();
		executor2.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
} /* end transformFloat2D */

/*------------------------------------------------------------------*/
private void transformFloat3D (
) {
	final int K1 = width.intValue();
	final int K2 = height.intValue();
	final int K3 = depth.intValue();
	final int K1K2 = K1 * K2;
	// Rows
	final FFTSetup fft1 = FFTSetup.transforms.get(width);
	final ExecutorService executor1 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft1.algorithm) {
		case BRUTEFORCE: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTBruteForceFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, 1,
						fft1.reUnitRootFloat, fft1.imUnitRootFloat));
					p += K1;
				}
			}
			break;
		}
		case COPRIMEFACTOR: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTCoprimeFactorFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, 1,
						fft1.ruritanian, fft1.chinese, fft1.K1));
					p += K1;
				}
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTLength2Float(
						reDataFloat, imDataFloat, p, 1));
					p += K1;
				}
			}
			break;
		}
		case LENGTH3: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTLength3Float(
						reDataFloat, imDataFloat, p, 1));
					p += K1;
				}
			}
			break;
		}
		case LENGTH4: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTLength4Float(
						reDataFloat, imDataFloat, p, 1));
					p += K1;
				}
			}
			break;
		}
		case LENGTH5: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTLength5Float(
						reDataFloat, imDataFloat, p, 1));
					p += K1;
				}
			}
			break;
		}
		case LENGTH6: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTLength6Float(
						reDataFloat, imDataFloat, p, 1));
					p += K1;
				}
			}
			break;
		}
		case LENGTH8: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTLength8Float(
						reDataFloat, imDataFloat, p, 1));
					p += K1;
				}
			}
			break;
		}
		case MIXEDRADIX: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTMixedRadixFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, 1,
						fft1.reUnitRootFloat, fft1.imUnitRootFloat,
						fft1.K1));
					p += K1;
				}
			}
			break;
		}
		case PADDEDRADER: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTPaddedRaderFloat(
						reDataFloat, imDataFloat, p, 1,
						fft1.reConvolverFloat, fft1.imConvolverFloat,
						fft1.modular, fft1.inverseModular));
					p += K1;
				}
			}
			break;
		}
		case RADER: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTRaderFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, 1,
						fft1.reConvolverFloat, fft1.imConvolverFloat,
						fft1.modular, fft1.inverseModular));
					p += K1;
				}
			}
			break;
		}
		case RADIX2: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTRadix2Float(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, 1,
						fft1.reUnitRootFloat, fft1.imUnitRootFloat));
					p += K1;
				}
			}
			break;
		}
		case SPLITRADIX: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor1.execute(new DFTSplitRadixFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, 1,
						fft1.reUnitRootFloat, fft1.imUnitRootFloat));
					p += K1;
				}
			}
			break;
		}
	}
	try {
		executor1.shutdown();
		executor1.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	// Columns
	final FFTSetup fft2 = FFTSetup.transforms.get(height);
	final ExecutorService executor2 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTBruteForceFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1,
						fft2.reUnitRootFloat, fft2.imUnitRootFloat));
				}
				p += K1K2;
			}
			break;
		}
		case COPRIMEFACTOR: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTCoprimeFactorFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1,
						fft2.ruritanian, fft2.chinese, fft2.K1));
				}
				p += K1K2;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTLength2Float(
						reDataFloat, imDataFloat, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH3: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTLength3Float(
						reDataFloat, imDataFloat, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH4: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTLength4Float(
						reDataFloat, imDataFloat, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH5: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTLength5Float(
						reDataFloat, imDataFloat, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH6: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTLength6Float(
						reDataFloat, imDataFloat, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH8: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTLength8Float(
						reDataFloat, imDataFloat, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case MIXEDRADIX: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTMixedRadixFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1,
						fft2.reUnitRootFloat, fft2.imUnitRootFloat, fft2.K1));
				}
				p += K1K2;
			}
			break;
		}
		case PADDEDRADER: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTPaddedRaderFloat(
						reDataFloat, imDataFloat, p + k1, K1,
						fft2.reConvolverFloat, fft2.imConvolverFloat,
						fft2.modular, fft2.inverseModular));
				}
				p += K1K2;
			}
			break;
		}
		case RADER: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTRaderFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1,
						fft2.reConvolverFloat, fft2.imConvolverFloat,
						fft2.modular, fft2.inverseModular));
				}
				p += K1K2;
			}
			break;
		}
		case RADIX2: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTRadix2Float(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1,
						fft2.reUnitRootFloat, fft2.imUnitRootFloat));
				}
				p += K1K2;
			}
			break;
		}
		case SPLITRADIX: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor2.execute(new DFTSplitRadixFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1,
						fft2.reUnitRootFloat, fft2.imUnitRootFloat));
				}
				p += K1K2;
			}
			break;
		}
	}
	try {
		executor2.shutdown();
		executor2.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	// Across
	final FFTSetup fft3 = FFTSetup.transforms.get(depth);
	final ExecutorService executor3 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft3.algorithm) {
		case BRUTEFORCE: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTBruteForceFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, K1K2,
						fft3.reUnitRootFloat, fft3.imUnitRootFloat));
					p++;
				}
			}
			break;
		}
		case COPRIMEFACTOR: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTCoprimeFactorFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, K1K2,
						fft3.ruritanian, fft3.chinese, fft3.K1));
					p++;
				}
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength2Float(
						reDataFloat, imDataFloat, p, K1K2));
					p++;
				}
			}
			break;
		}
		case LENGTH3: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength3Float(
						reDataFloat, imDataFloat, p, K1K2));
					p++;
				}
			}
			break;
		}
		case LENGTH4: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength4Float(
						reDataFloat, imDataFloat, p, K1K2));
					p++;
				}
			}
			break;
		}
		case LENGTH5: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength5Float(
						reDataFloat, imDataFloat, p, K1K2));
					p++;
				}
			}
			break;
		}
		case LENGTH6: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength6Float(
						reDataFloat, imDataFloat, p, K1K2));
					p++;
				}
			}
			break;
		}
		case LENGTH8: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength8Float(
						reDataFloat, imDataFloat, p, K1K2));
					p++;
				}
			}
			break;
		}
		case MIXEDRADIX: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTMixedRadixFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, K1K2,
						fft3.reUnitRootFloat, fft3.imUnitRootFloat, fft3.K1));
					p++;
				}
			}
			break;
		}
		case PADDEDRADER: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTPaddedRaderFloat(
						reDataFloat, imDataFloat, p, K1K2,
						fft3.reConvolverFloat, fft3.imConvolverFloat,
						fft3.modular, fft3.inverseModular));
					p++;
				}
			}
			break;
		}
		case RADER: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTRaderFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, K1K2,
						fft3.reConvolverFloat, fft3.imConvolverFloat,
						fft3.modular, fft3.inverseModular));
					p++;
				}
			}
			break;
		}
		case RADIX2: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTRadix2Float(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, K1K2,
						fft3.reUnitRootFloat, fft3.imUnitRootFloat));
					p++;
				}
			}
			break;
		}
		case SPLITRADIX: {
			int p = 0;
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTSplitRadixFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, K1K2,
						fft3.reUnitRootFloat, fft3.imUnitRootFloat));
					p++;
				}
			}
			break;
		}
	}
	try {
		executor3.shutdown();
		executor3.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
} /* end transformFloat3D */

/*------------------------------------------------------------------*/
private void transformRealDouble1D (
) {
	final FFTSetupReal fft = FFTSetupReal.transforms.get(width);
	switch (fft.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceRealDouble(reDataDouble, imDataDouble,
				reBufferDouble, imBufferDouble, 0, 1,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorRealDouble(reDataDouble, imDataDouble,
				reBufferDouble, imBufferDouble, 0, 1,
				fft.ruritanian, fft.chinese, fft.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			new DFTEvenRealDouble(reDataDouble, imDataDouble,
				reBufferDouble, imBufferDouble, 0, 1,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
		}
		case LENGTH1: {
			imDataDouble[0] = 0.0;
			break;
		}
		case LENGTH2: {
			new DFTLength2RealDouble(reDataDouble, imDataDouble, 0, 1).run();
			break;
		}
		case LENGTH3: {
			new DFTLength3RealDouble(reDataDouble, imDataDouble, 0, 1).run();
			break;
		}
		case LENGTH4: {
			new DFTLength4RealDouble(reDataDouble, imDataDouble, 0, 1).run();
			break;
		}
		case LENGTH5: {
			new DFTLength5RealDouble(reDataDouble, imDataDouble, 0, 1).run();
			break;
		}
		case LENGTH6: {
			new DFTLength6RealDouble(reDataDouble, imDataDouble, 0, 1).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8RealDouble(reDataDouble, imDataDouble, 0, 1).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixRealDouble(reDataDouble, imDataDouble,
				reBufferDouble, imBufferDouble, 0, 1,
				fft.reUnitRootDouble, fft.imUnitRootDouble, fft.K1).run();
			break;
		}
		case PADDEDRADER: {
			new DFTPaddedRaderRealDouble(reDataDouble, imDataDouble, 0, 1,
				fft.reConvolverDouble, fft.imConvolverDouble,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADER: {
			new DFTRaderRealDouble(reDataDouble, imDataDouble,
				reBufferDouble, imBufferDouble, 0, 1,
				fft.reConvolverDouble, fft.imConvolverDouble,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADIX2: {
			new DFTRadix2RealDouble(reDataDouble, imDataDouble,
				reBufferDouble, imBufferDouble, 0, 1,
				fft.reUnitRootEvenDouble, fft.imUnitRootEvenDouble,
				fft.reUnitRootOddDouble, fft.imUnitRootOddDouble).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixRealDouble(reDataDouble, imDataDouble,
				reBufferDouble, imBufferDouble, 0, 1,
				fft.reUnitRootDouble, fft.imUnitRootDouble).run();
			break;
		}
	}
	int progressive = 0;
	int regressive = width.intValue();
	while (++progressive < --regressive) {
		reDataDouble[regressive] = reDataDouble[progressive];
		imDataDouble[regressive] = -imDataDouble[progressive];
	}
} /* end transformRealDouble1D */

/*------------------------------------------------------------------*/
private void transformRealDouble2DColumnFirst (
) {
	final int K1 = width.intValue();
	final int K2 = height.intValue();
	final int halfK2 = (K2 >> 1) + 1;
	int k1 = -1;
	// First column for an odd width
	if (1 == (K1 & 1)) {
		final FFTSetupReal fft = FFTSetupReal.transforms.get(height);
		switch (fft.algorithm) {
			case BRUTEFORCE: {
				new DFTBruteForceRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, K1,
					fft.reUnitRootDouble, fft.imUnitRootDouble).run();
				break;
			}
			case COPRIMEFACTOR: {
				new DFTCoprimeFactorRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, K1,
					fft.ruritanian, fft.chinese, fft.K1).run();
				break;
			}
			case DUOREAL: {
				throw(new IllegalStateException());
			}
			case EVENREAL: {
				new DFTEvenRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, K1,
					fft.reUnitRootDouble, fft.imUnitRootDouble).run();
				break;
			}
			case LENGTH1: {
				imDataDouble[0] = 0.0;
				break;
			}
			case LENGTH2: {
				new DFTLength2RealDouble(reDataDouble, imDataDouble,
					0, K1).run();
				break;
			}
			case LENGTH3: {
				new DFTLength3RealDouble(reDataDouble, imDataDouble,
					0, K1).run();
				break;
			}
			case LENGTH4: {
				new DFTLength4RealDouble(reDataDouble, imDataDouble,
					0, K1).run();
				break;
			}
			case LENGTH5: {
				new DFTLength5RealDouble(reDataDouble, imDataDouble,
					0, K1).run();
				break;
			}
			case LENGTH6: {
				new DFTLength6RealDouble(reDataDouble, imDataDouble,
					0, K1).run();
				break;
			}
			case LENGTH8: {
				new DFTLength8RealDouble(reDataDouble, imDataDouble,
					0, K1).run();
				break;
			}
			case MIXEDRADIX: {
				new DFTMixedRadixRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, K1,
					fft.reUnitRootDouble, fft.imUnitRootDouble, fft.K1).run();
				break;
			}
			case PADDEDRADER: {
				new DFTPaddedRaderRealDouble(reDataDouble, imDataDouble, 0, K1,
					fft.reConvolverDouble, fft.imConvolverDouble,
					fft.modular, fft.inverseModular).run();
				break;
			}
			case RADER: {
				new DFTRaderRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, K1,
					fft.reConvolverDouble, fft.imConvolverDouble,
					fft.modular, fft.inverseModular).run();
				break;
			}
			case RADIX2: {
				new DFTRadix2RealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, K1,
					fft.reUnitRootEvenDouble, fft.imUnitRootEvenDouble,
					fft.reUnitRootOddDouble, fft.imUnitRootOddDouble).run();
				break;
			}
			case SPLITRADIX: {
				new DFTSplitRadixRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, K1,
					fft.reUnitRootDouble, fft.imUnitRootDouble).run();
				break;
			}
		}
		k1 = 0;
	}
	// Remaining columns
	final FFTSetupDuoReal fft1 = FFTSetupDuoReal.transforms.get(height);
	final ExecutorService executor1 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft1.algorithm) {
		case BRUTEFORCE: {
			while (++k1 < K1) {
				executor1.execute(new DFTBruteForceRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1, K1,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble));
			}
			break;
		}
		case COPRIMEFACTOR: {
			while (++k1 < K1) {
				executor1.execute(new DFTCoprimeFactorRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1, K1,
					fft1.ruritanian, fft1.chinese, fft1.K1));
			}
			break;
		}
		case DUOREAL: {
			while (++k1 < K1) {
				executor1.execute(new DFTDuoRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1, k1 + 1, K1, K2));
				++k1;
			}
			break;
		}
		case EVENREAL: {
			while (++k1 < K1) {
				executor1.execute(new DFTEvenRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1, K1,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble));
			}
			break;
		}
		case LENGTH1: {
			while (++k1 < K1) {
				imDataDouble[k1] = 0.0;
			}
			break;
		}
		case LENGTH2: {
			while (++k1 < K1) {
				executor1.execute(new DFTLength2RealDouble(
					reDataDouble, imDataDouble, k1, K1));
			}
			break;
		}
		case LENGTH3: {
			while (++k1 < K1) {
				executor1.execute(new DFTLength3RealDouble(
					reDataDouble, imDataDouble, k1, K1));
			}
			break;
		}
		case LENGTH4: {
			while (++k1 < K1) {
				executor1.execute(new DFTLength4RealDouble(
					reDataDouble, imDataDouble, k1, K1));
			}
			break;
		}
		case LENGTH5: {
			while (++k1 < K1) {
				executor1.execute(new DFTLength5RealDouble(
					reDataDouble, imDataDouble, k1, K1));
			}
			break;
		}
		case LENGTH6: {
			while (++k1 < K1) {
				executor1.execute(new DFTLength6RealDouble(
					reDataDouble, imDataDouble, k1, K1));
			}
			break;
		}
		case LENGTH8: {
			while (++k1 < K1) {
				executor1.execute(new DFTLength8RealDouble(
					reDataDouble, imDataDouble, k1, K1));
			}
			break;
		}
		case MIXEDRADIX: {
			while (++k1 < K1) {
				executor1.execute(new DFTMixedRadixRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1, K1,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble, fft1.K1));
			}
			break;
		}
		case PADDEDRADER: {
			while (++k1 < K1) {
				executor1.execute(new DFTPaddedRaderRealDouble(
					reDataDouble, imDataDouble, k1, K1,
					fft1.reConvolverDouble, fft1.imConvolverDouble,
					fft1.modular, fft1.inverseModular));
			}
			break;
		}
		case RADER: {
			while (++k1 < K1) {
				executor1.execute(new DFTRaderRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1, K1,
					fft1.reConvolverDouble, fft1.imConvolverDouble,
					fft1.modular, fft1.inverseModular));
			}
			break;
		}
		case RADIX2: {
			while (++k1 < K1) {
				executor1.execute(new DFTRadix2RealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1, K1,
					fft1.reUnitRootEvenDouble, fft1.imUnitRootEvenDouble,
					fft1.reUnitRootOddDouble, fft1.imUnitRootOddDouble));
			}
			break;
		}
		case SPLITRADIX: {
			while (++k1 < K1) {
				executor1.execute(new DFTSplitRadixRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1, K1,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble));
			}
			break;
		}
	}
	try {
		executor1.shutdown();
		executor1.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	// Rows
	final FFTSetup fft2 = FFTSetup.transforms.get(width);
	final ExecutorService executor2 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	int p = 0;
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTBruteForceDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble));
				p += K1;
			}
			break;
		}
		case COPRIMEFACTOR: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTCoprimeFactorDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft2.ruritanian, fft2.chinese, fft2.K1));
				p += K1;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTLength2Double(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH3: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTLength3Double(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH4: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTLength4Double(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH5: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTLength5Double(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH6: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTLength6Double(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH8: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTLength8Double(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case MIXEDRADIX: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTMixedRadixDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble, fft2.K1));
				p += K1;
			}
			break;
		}
		case PADDEDRADER: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTPaddedRaderDouble(
					reDataDouble, imDataDouble, p, 1,
					fft2.reConvolverDouble, fft2.imConvolverDouble,
					fft2.modular, fft2.inverseModular));
				p += K1;
			}
			break;
		}
		case RADER: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTRaderDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft2.reConvolverDouble, fft2.imConvolverDouble,
					fft2.modular, fft2.inverseModular));
				p += K1;
			}
			break;
		}
		case RADIX2: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTRadix2Double(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble));
				p += K1;
			}
			break;
		}
		case SPLITRADIX: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTSplitRadixDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble));
				p += K1;
			}
			break;
		}
	}
	try {
		executor2.shutdown();
		executor2.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	int progressive = K1;
	int regressive = K1 * (K2 - 1);
	while (progressive < regressive) {
		reDataDouble[regressive] = reDataDouble[progressive];
		imDataDouble[regressive] = -imDataDouble[progressive];
		progressive += K1;
		regressive -= K1;
	}
	p = K1 + 1;
	int q = K1 * K2 - 1;
	for (int k2 = halfK2; (k2 < K2); k2++) {
		progressive = p;
		regressive = q;
		for (k1 = 1; (k1 < K1); k1++) {
			reDataDouble[regressive] = reDataDouble[progressive];
			imDataDouble[regressive] = -imDataDouble[progressive];
			progressive++;
			regressive--;
		}
		p += K1;
		q -= K1;
	}
} /* end transformRealDouble2DColumnFirst */

/*------------------------------------------------------------------*/
private void transformRealDouble2DRowFirst (
) {
	final int K1 = width.intValue();
	final int K2 = height.intValue();
	final int halfK1 = (K1 >> 1) + 1;
	int p = 0;
	int k2 = 0;
	// First row for an odd height
	if (1 == (K2 & 1)) {
		final FFTSetupReal fft = FFTSetupReal.transforms.get(width);
		switch (fft.algorithm) {
			case BRUTEFORCE: {
				new DFTBruteForceRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, 1,
					fft.reUnitRootDouble, fft.imUnitRootDouble).run();
				break;
			}
			case COPRIMEFACTOR: {
				new DFTCoprimeFactorRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, 1,
					fft.ruritanian, fft.chinese, fft.K1).run();
				break;
			}
			case DUOREAL: {
				throw(new IllegalStateException());
			}
			case EVENREAL: {
				new DFTEvenRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, 1,
					fft.reUnitRootDouble, fft.imUnitRootDouble).run();
				break;
			}
			case LENGTH1: {
				imDataDouble[0] = 0.0;
				break;
			}
			case LENGTH2: {
				new DFTLength2RealDouble(reDataDouble, imDataDouble,
					0, 1).run();
				break;
			}
			case LENGTH3: {
				new DFTLength3RealDouble(reDataDouble, imDataDouble,
					0, 1).run();
				break;
			}
			case LENGTH4: {
				new DFTLength4RealDouble(reDataDouble, imDataDouble,
					0, 1).run();
				break;
			}
			case LENGTH5: {
				new DFTLength5RealDouble(reDataDouble, imDataDouble,
					0, 1).run();
				break;
			}
			case LENGTH6: {
				new DFTLength6RealDouble(reDataDouble, imDataDouble,
					0, 1).run();
				break;
			}
			case LENGTH8: {
				new DFTLength8RealDouble(reDataDouble, imDataDouble,
					0, 1).run();
				break;
			}
			case MIXEDRADIX: {
				new DFTMixedRadixRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, 1,
					fft.reUnitRootDouble, fft.imUnitRootDouble, fft.K1).run();
				break;
			}
			case PADDEDRADER: {
				new DFTPaddedRaderRealDouble(reDataDouble, imDataDouble, 0, 1,
					fft.reConvolverDouble, fft.imConvolverDouble,
					fft.modular, fft.inverseModular).run();
				break;
			}
			case RADER: {
				new DFTRaderRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, 1,
					fft.reConvolverDouble, fft.imConvolverDouble,
					fft.modular, fft.inverseModular).run();
				break;
			}
			case RADIX2: {
				new DFTRadix2RealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, 1,
					fft.reUnitRootEvenDouble, fft.imUnitRootEvenDouble,
					fft.reUnitRootOddDouble, fft.imUnitRootOddDouble).run();
				break;
			}
			case SPLITRADIX: {
				new DFTSplitRadixRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, 1,
					fft.reUnitRootDouble, fft.imUnitRootDouble).run();
				break;
			}
		}
		p = K1;
		k2 = 1;
	}
	// Remaining rows
	final FFTSetupDuoReal fft1 = FFTSetupDuoReal.transforms.get(width);
	final ExecutorService executor1 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft1.algorithm) {
		case BRUTEFORCE: {
			while (k2++ < K2) {
				executor1.execute(new DFTBruteForceRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble));
				p += K1;
			}
			break;
		}
		case COPRIMEFACTOR: {
			while (k2++ < K2) {
				executor1.execute(new DFTCoprimeFactorRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft1.ruritanian, fft1.chinese, fft1.K1));
				p += K1;
			}
			break;
		}
		case DUOREAL: {
			while (k2++ < K2) {
				executor1.execute(new DFTDuoRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, p + K1, 1, K1));
				p += 2 * K1;
				k2++;
			}
			break;
		}
		case EVENREAL: {
			while (k2++ < K2) {
				executor1.execute(new DFTEvenRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble));
				p += K1;
			}
			break;
		}
		case LENGTH1: {
			while (k2++ < K2) {
				imDataDouble[p] = 0.0;
				p += K1;
			}
			break;
		}
		case LENGTH2: {
			while (k2++ < K2) {
				executor1.execute(new DFTLength2RealDouble(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH3: {
			while (k2++ < K2) {
				executor1.execute(new DFTLength3RealDouble(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH4: {
			while (k2++ < K2) {
				executor1.execute(new DFTLength4RealDouble(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH5: {
			while (k2++ < K2) {
				executor1.execute(new DFTLength5RealDouble(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH6: {
			while (k2++ < K2) {
				executor1.execute(new DFTLength6RealDouble(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH8: {
			while (k2++ < K2) {
				executor1.execute(new DFTLength8RealDouble(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case MIXEDRADIX: {
			while (k2++ < K2) {
				executor1.execute(new DFTMixedRadixRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble, fft1.K1));
				p += K1;
			}
			break;
		}
		case PADDEDRADER: {
			while (k2++ < K2) {
				executor1.execute(new DFTPaddedRaderRealDouble(
					reDataDouble, imDataDouble, p, 1,
					fft1.reConvolverDouble, fft1.imConvolverDouble,
					fft1.modular, fft1.inverseModular));
				p += K1;
			}
			break;
		}
		case RADER: {
			while (k2++ < K2) {
				executor1.execute(new DFTRaderRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft1.reConvolverDouble, fft1.imConvolverDouble,
					fft1.modular, fft1.inverseModular));
				p += K1;
			}
			break;
		}
		case RADIX2: {
			while (k2++ < K2) {
				executor1.execute(new DFTRadix2RealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft1.reUnitRootEvenDouble, fft1.imUnitRootEvenDouble,
					fft1.reUnitRootOddDouble, fft1.imUnitRootOddDouble));
				p += K1;
			}
			break;
		}
		case SPLITRADIX: {
			while (k2++ < K2) {
				executor1.execute(new DFTSplitRadixRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble));
				p += K1;
			}
			break;
		}
	}
	try {
		executor1.shutdown();
		executor1.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	// Columns
	final FFTSetup fft2 = FFTSetup.transforms.get(height);
	final ExecutorService executor2 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTBruteForceDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1, K1,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble));
			}
			break;
		}
		case COPRIMEFACTOR: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTCoprimeFactorDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1, K1,
					fft2.ruritanian, fft2.chinese, fft2.K1));
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTLength2Double(
					reDataDouble, imDataDouble, k1, K1));
			}
			break;
		}
		case LENGTH3: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTLength3Double(
					reDataDouble, imDataDouble, k1, K1));
			}
			break;
		}
		case LENGTH4: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTLength4Double(
					reDataDouble, imDataDouble, k1, K1));
			}
			break;
		}
		case LENGTH5: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTLength5Double(
					reDataDouble, imDataDouble, k1, K1));
			}
			break;
		}
		case LENGTH6: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTLength6Double(
					reDataDouble, imDataDouble, k1, K1));
			}
			break;
		}
		case LENGTH8: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTLength8Double(
					reDataDouble, imDataDouble, k1, K1));
			}
			break;
		}
		case MIXEDRADIX: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTMixedRadixDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1, K1,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble, fft2.K1));
			}
			break;
		}
		case PADDEDRADER: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTPaddedRaderDouble(
					reDataDouble, imDataDouble, k1, K1,
					fft2.reConvolverDouble, fft2.imConvolverDouble,
					fft2.modular, fft2.inverseModular));
			}
			break;
		}
		case RADER: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTRaderDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1, K1,
					fft2.reConvolverDouble, fft2.imConvolverDouble,
					fft2.modular, fft2.inverseModular));
			}
			break;
		}
		case RADIX2: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTRadix2Double(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1, K1,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble));
			}
			break;
		}
		case SPLITRADIX: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTSplitRadixDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1, K1,
					fft2.reUnitRootDouble, fft2.imUnitRootDouble));
			}
			break;
		}
	}
	try {
		executor2.shutdown();
		executor2.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	int progressive = 0;
	int regressive = K1;
	while (++progressive < --regressive) {
		reDataDouble[regressive] = reDataDouble[progressive];
		imDataDouble[regressive] = -imDataDouble[progressive];
	}
	p = K1 + 1;
	int q = K1 * K2 - 1;
	for (k2 = 1; (k2 < K2); k2++) {
		progressive = p;
		regressive = q;
		for (int k1 = halfK1; (k1 < K1); k1++) {
			reDataDouble[regressive] = reDataDouble[progressive];
			imDataDouble[regressive] = -imDataDouble[progressive];
			progressive++;
			regressive--;
		}
		p += K1;
		q -= K1;
	}
} /* end transformRealDouble2DRowFirst */

/*------------------------------------------------------------------*/
private void transformRealDouble3DAcrossFirst (
) {
	final int K1 = width.intValue();
	final int K2 = height.intValue();
	final int K3 = depth.intValue();
	final int K1K2 = K1 * K2;
	final int halfK3 = (K3 >> 1) + 1;
	int k = -1;
	// First across for an odd (width * height)
	if (1 == (K1K2 & 1)) {
		final FFTSetupReal fft = FFTSetupReal.transforms.get(depth);
		switch (fft.algorithm) {
			case BRUTEFORCE: {
				new DFTBruteForceRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, K1K2,
					fft.reUnitRootDouble, fft.imUnitRootDouble).run();
				break;
			}
			case COPRIMEFACTOR: {
				new DFTCoprimeFactorRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, K1K2,
					fft.ruritanian, fft.chinese, fft.K1).run();
				break;
			}
			case DUOREAL: {
				throw(new IllegalStateException());
			}
			case EVENREAL: {
				new DFTEvenRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, K1K2,
					fft.reUnitRootDouble, fft.imUnitRootDouble).run();
				break;
			}
			case LENGTH1: {
				imDataDouble[0] = 0.0;
				break;
			}
			case LENGTH2: {
				new DFTLength2RealDouble(reDataDouble, imDataDouble,
					0, K1K2).run();
				break;
			}
			case LENGTH3: {
				new DFTLength3RealDouble(reDataDouble, imDataDouble,
					0, K1K2).run();
				break;
			}
			case LENGTH4: {
				new DFTLength4RealDouble(reDataDouble, imDataDouble,
					0, K1K2).run();
				break;
			}
			case LENGTH5: {
				new DFTLength5RealDouble(reDataDouble, imDataDouble,
					0, K1K2).run();
				break;
			}
			case LENGTH6: {
				new DFTLength6RealDouble(reDataDouble, imDataDouble,
					0, K1K2).run();
				break;
			}
			case LENGTH8: {
				new DFTLength8RealDouble(reDataDouble, imDataDouble,
					0, K1K2).run();
				break;
			}
			case MIXEDRADIX: {
				new DFTMixedRadixRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, K1K2,
					fft.reUnitRootDouble, fft.imUnitRootDouble, fft.K1).run();
				break;
			}
			case PADDEDRADER: {
				new DFTPaddedRaderRealDouble(
					reDataDouble, imDataDouble, 0, K1K2,
					fft.reConvolverDouble, fft.imConvolverDouble,
					fft.modular, fft.inverseModular).run();
				break;
			}
			case RADER: {
				new DFTRaderRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, K1K2,
					fft.reConvolverDouble, fft.imConvolverDouble,
					fft.modular, fft.inverseModular).run();
				break;
			}
			case RADIX2: {
				new DFTRadix2RealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, K1K2,
					fft.reUnitRootEvenDouble, fft.imUnitRootEvenDouble,
					fft.reUnitRootOddDouble, fft.imUnitRootOddDouble).run();
				break;
			}
			case SPLITRADIX: {
				new DFTSplitRadixRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, K1K2,
					fft.reUnitRootDouble, fft.imUnitRootDouble).run();
				break;
			}
		}
		k = 0;
	}
	// Remaining across
	final FFTSetupDuoReal fft1 = FFTSetupDuoReal.transforms.get(depth);
	final ExecutorService executor1 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft1.algorithm) {
		case BRUTEFORCE: {
			while (++k < K1K2) {
				executor1.execute(new DFTBruteForceRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k, K1K2,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble));
			}
			break;
		}
		case COPRIMEFACTOR: {
			while (++k < K1K2) {
				executor1.execute(new DFTCoprimeFactorRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k, K1K2,
					fft1.ruritanian, fft1.chinese, fft1.K1));
			}
			break;
		}
		case DUOREAL: {
			while (++k < K1K2) {
				executor1.execute(new DFTDuoRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k, k + 1, K1K2, K3));
				k++;
			}
			break;
		}
		case EVENREAL: {
			while (++k < K1K2) {
				executor1.execute(new DFTEvenRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k, K1K2,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble));
			}
			break;
		}
		case LENGTH1: {
			while (++k < K1K2) {
				imDataDouble[k] = 0.0;
			}
			break;
		}
		case LENGTH2: {
			while (++k < K1K2) {
				executor1.execute(new DFTLength2RealDouble(
					reDataDouble, imDataDouble, k, K1K2));
			}
			break;
		}
		case LENGTH3: {
			while (++k < K1K2) {
				executor1.execute(new DFTLength3RealDouble(
					reDataDouble, imDataDouble, k, K1K2));
			}
			break;
		}
		case LENGTH4: {
			while (++k < K1K2) {
				executor1.execute(new DFTLength4RealDouble(
					reDataDouble, imDataDouble, k, K1K2));
			}
			break;
		}
		case LENGTH5: {
			while (++k < K1K2) {
				executor1.execute(new DFTLength5RealDouble(
					reDataDouble, imDataDouble, k, K1K2));
			}
			break;
		}
		case LENGTH6: {
			while (++k < K1K2) {
				executor1.execute(new DFTLength6RealDouble(
					reDataDouble, imDataDouble, k, K1K2));
			}
			break;
		}
		case LENGTH8: {
			while (++k < K1K2) {
				executor1.execute(new DFTLength8RealDouble(
					reDataDouble, imDataDouble, k, K1K2));
			}
			break;
		}
		case MIXEDRADIX: {
			while (++k < K1K2) {
				executor1.execute(new DFTMixedRadixRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k, K1K2,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble, fft1.K1));
			}
			break;
		}
		case PADDEDRADER: {
			while (++k < K1K2) {
				executor1.execute(new DFTPaddedRaderRealDouble(
					reDataDouble, imDataDouble, k, K1K2,
					fft1.reConvolverDouble, fft1.imConvolverDouble,
					fft1.modular, fft1.inverseModular));
			}
			break;
		}
		case RADER: {
			while (++k < K1K2) {
				executor1.execute(new DFTRaderRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k, K1K2,
					fft1.reConvolverDouble, fft1.imConvolverDouble,
					fft1.modular, fft1.inverseModular));
			}
			break;
		}
		case RADIX2: {
			while (++k < K1K2) {
				executor1.execute(new DFTRadix2RealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k, K1K2,
					fft1.reUnitRootEvenDouble, fft1.imUnitRootEvenDouble,
					fft1.reUnitRootOddDouble, fft1.imUnitRootOddDouble));
			}
			break;
		}
		case SPLITRADIX: {
			while (++k < K1K2) {
				executor1.execute(new DFTSplitRadixRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k, K1K2,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble));
			}
			break;
		}
	}
	try {
		executor1.shutdown();
		executor1.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	// Rows
	final FFTSetup fft2 = FFTSetup.transforms.get(width);
	final ExecutorService executor2 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTBruteForceDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, 1,
						fft2.reUnitRootDouble, fft2.imUnitRootDouble));
					p += K1;
				}
			}
			break;
		}
		case COPRIMEFACTOR: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTCoprimeFactorDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, 1,
						fft2.ruritanian, fft2.chinese, fft2.K1));
					p += K1;
				}
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTLength2Double(
						reDataDouble, imDataDouble, p, 1));
					p += K1;
				}
			}
			break;
		}
		case LENGTH3: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTLength3Double(
						reDataDouble, imDataDouble, p, 1));
					p += K1;
				}
			}
			break;
		}
		case LENGTH4: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTLength4Double(
						reDataDouble, imDataDouble, p, 1));
					p += K1;
				}
			}
			break;
		}
		case LENGTH5: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTLength5Double(
						reDataDouble, imDataDouble, p, 1));
					p += K1;
				}
			}
			break;
		}
		case LENGTH6: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTLength6Double(
						reDataDouble, imDataDouble, p, 1));
					p += K1;
				}
			}
			break;
		}
		case LENGTH8: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTLength8Double(
						reDataDouble, imDataDouble, p, 1));
					p += K1;
				}
			}
			break;
		}
		case MIXEDRADIX: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTMixedRadixDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, 1,
						fft2.reUnitRootDouble, fft2.imUnitRootDouble,
						fft2.K1));
					p += K1;
				}
			}
			break;
		}
		case PADDEDRADER: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTPaddedRaderDouble(
						reDataDouble, imDataDouble, p, 1,
						fft2.reConvolverDouble, fft2.imConvolverDouble,
						fft2.modular, fft2.inverseModular));
					p += K1;
				}
			}
			break;
		}
		case RADER: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTRaderDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, 1,
						fft2.reConvolverDouble, fft2.imConvolverDouble,
						fft2.modular, fft2.inverseModular));
					p += K1;
				}
			}
			break;
		}
		case RADIX2: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTRadix2Double(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, 1,
						fft2.reUnitRootDouble, fft2.imUnitRootDouble));
					p += K1;
				}
			}
			break;
		}
		case SPLITRADIX: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTSplitRadixDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, 1,
						fft2.reUnitRootDouble, fft2.imUnitRootDouble));
					p += K1;
				}
			}
			break;
		}
	}
	try {
		executor2.shutdown();
		executor2.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	// Columns
	final FFTSetup fft3 = FFTSetup.transforms.get(height);
	final ExecutorService executor3 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft3.algorithm) {
		case BRUTEFORCE: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTBruteForceDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1,
						fft3.reUnitRootDouble, fft3.imUnitRootDouble));
				}
				p += K1K2;
			}
			break;
		}
		case COPRIMEFACTOR: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTCoprimeFactorDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1,
						fft3.ruritanian, fft3.chinese, fft3.K1));
				}
				p += K1K2;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength2Double(
						reDataDouble, imDataDouble, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH3: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength3Double(
						reDataDouble, imDataDouble, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH4: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength4Double(
						reDataDouble, imDataDouble, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH5: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength5Double(
						reDataDouble, imDataDouble, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH6: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength6Double(
						reDataDouble, imDataDouble, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH8: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength8Double(
						reDataDouble, imDataDouble, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case MIXEDRADIX: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTMixedRadixDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1,
						fft3.reUnitRootDouble, fft3.imUnitRootDouble, fft3.K1));
				}
				p += K1K2;
			}
			break;
		}
		case PADDEDRADER: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTPaddedRaderDouble(
						reDataDouble, imDataDouble, p + k1, K1,
						fft3.reConvolverDouble, fft3.imConvolverDouble,
						fft3.modular, fft3.inverseModular));
				}
				p += K1K2;
			}
			break;
		}
		case RADER: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTRaderDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1,
						fft3.reConvolverDouble, fft3.imConvolverDouble,
						fft3.modular, fft3.inverseModular));
				}
				p += K1K2;
			}
			break;
		}
		case RADIX2: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTRadix2Double(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1,
						fft3.reUnitRootDouble, fft3.imUnitRootDouble));
				}
				p += K1K2;
			}
			break;
		}
		case SPLITRADIX: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTSplitRadixDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1,
						fft3.reUnitRootDouble, fft3.imUnitRootDouble));
				}
				p += K1K2;
			}
			break;
		}
	}
	try {
		executor3.shutdown();
		executor3.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	int progressive = K1K2;
	int regressive = dataLength - K1K2;
	while (progressive < regressive) {
		reDataDouble[regressive] = reDataDouble[progressive];
		imDataDouble[regressive] = -imDataDouble[progressive];
		progressive += K1K2;
		regressive -= K1K2;
	}
	int p = K1K2 + K1;
	int q = dataLength - K1;
	for (int k3 = halfK3; (k3 < K3); k3++) {
		progressive = p;
		regressive = q;
		for (int k2 = 1; (k2 < K2); k2++) {
			reDataDouble[regressive] = reDataDouble[progressive];
			imDataDouble[regressive] = -imDataDouble[progressive];
			progressive += K1;
			regressive -= K1;
		}
		p += K1K2;
		q -= K1K2;
	}
	p = K1K2 + 1;
	q = K1K2 * (K3 - 1) + K1 - 1;
	for (int k3 = halfK3; (k3 < K3); k3++) {
		progressive = p;
		regressive = q;
		for (int k1 = 1; (k1 < K1); k1++) {
			reDataDouble[regressive] = reDataDouble[progressive];
			imDataDouble[regressive] = -imDataDouble[progressive];
			progressive++;
			regressive--;
		}
		p += K1K2;
		q -= K1K2;
	}
	progressive = K1K2 + K1 + 1;
	regressive = dataLength - 1;
	for (int k3 = halfK3; (k3 < K3); k3++) {
		for (int k2 = 1; (k2 < K2); k2++) {
			for (int k1 = 1; (k1 < K1); k1++) {
				reDataDouble[regressive] = reDataDouble[progressive];
				imDataDouble[regressive] = -imDataDouble[progressive];
				progressive++;
				regressive--;
			}
			progressive++;
			regressive--;
		}
		progressive += K1;
		regressive -= K1;
	}
} /* end transformRealDouble3DAcrossFirst */

/*------------------------------------------------------------------*/
private void transformRealDouble3DColumnFirst (
) {
	final int K1 = width.intValue();
	final int K2 = height.intValue();
	final int K3 = depth.intValue();
	final int K1K2 = K1 * K2;
	final int k1K2 = K1K2 - K1;
	final int K1K3 = K1 * K3;
	final int halfK2 = (K2 >> 1) + 1;
	int k = -1;
	// First column for an odd (width * depth)
	if (1 == (K1K3 & 1)) {
		final FFTSetupReal fft = FFTSetupReal.transforms.get(height);
		switch (fft.algorithm) {
			case BRUTEFORCE: {
				new DFTBruteForceRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, K1,
					fft.reUnitRootDouble, fft.imUnitRootDouble).run();
				break;
			}
			case COPRIMEFACTOR: {
				new DFTCoprimeFactorRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, K1,
					fft.ruritanian, fft.chinese, fft.K1).run();
				break;
			}
			case DUOREAL: {
				throw(new IllegalStateException());
			}
			case EVENREAL: {
				new DFTEvenRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, K1,
					fft.reUnitRootDouble, fft.imUnitRootDouble).run();
				break;
			}
			case LENGTH1: {
				imDataDouble[0] = 0.0;
				break;
			}
			case LENGTH2: {
				new DFTLength2RealDouble(
					reDataDouble, imDataDouble, 0, K1).run();
				break;
			}
			case LENGTH3: {
				new DFTLength3RealDouble(
					reDataDouble, imDataDouble, 0, K1).run();
				break;
			}
			case LENGTH4: {
				new DFTLength4RealDouble(
					reDataDouble, imDataDouble, 0, K1).run();
				break;
			}
			case LENGTH5: {
				new DFTLength5RealDouble(
					reDataDouble, imDataDouble, 0, K1).run();
				break;
			}
			case LENGTH6: {
				new DFTLength6RealDouble(
					reDataDouble, imDataDouble, 0, K1).run();
				break;
			}
			case LENGTH8: {
				new DFTLength8RealDouble(
					reDataDouble, imDataDouble, 0, K1).run();
				break;
			}
			case MIXEDRADIX: {
				new DFTMixedRadixRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, K1,
					fft.reUnitRootDouble, fft.imUnitRootDouble, fft.K1).run();
				break;
			}
			case PADDEDRADER: {
				new DFTPaddedRaderRealDouble(reDataDouble, imDataDouble, 0, K1,
					fft.reConvolverDouble, fft.imConvolverDouble,
					fft.modular, fft.inverseModular).run();
				break;
			}
			case RADER: {
				new DFTRaderRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, K1,
					fft.reConvolverDouble, fft.imConvolverDouble,
					fft.modular, fft.inverseModular).run();
				break;
			}
			case RADIX2: {
				new DFTRadix2RealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, K1,
					fft.reUnitRootEvenDouble, fft.imUnitRootEvenDouble,
					fft.reUnitRootOddDouble, fft.imUnitRootOddDouble).run();
				break;
			}
			case SPLITRADIX: {
				new DFTSplitRadixRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, K1,
					fft.reUnitRootDouble, fft.imUnitRootDouble).run();
				break;
			}
		}
		k = 0;
	}
	// Remaining columns
	final FFTSetupDuoReal fft1 = FFTSetupDuoReal.transforms.get(height);
	final ExecutorService executor1 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft1.algorithm) {
		case BRUTEFORCE: {
			while (++k < K1K3) {
				executor1.execute(new DFTBruteForceRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1K2 * (k / K1) + k, K1,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble));
			}
			break;
		}
		case COPRIMEFACTOR: {
			while (++k < K1K3) {
				executor1.execute(new DFTCoprimeFactorRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1K2 * (k / K1) + k, K1,
					fft1.ruritanian, fft1.chinese, fft1.K1));
			}
			break;
		}
		case DUOREAL: {
			while (++k < K1K3) {
				final int p = k1K2 * (k / K1) + k;
				k++;
				final int q = k1K2 * (k / K1) + k;
				executor1.execute(new DFTDuoRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, q, K1, K2));
			}
			break;
		}
		case EVENREAL: {
			while (++k < K1K3) {
				executor1.execute(new DFTEvenRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1K2 * (k / K1) + k, K1,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble));
			}
			break;
		}
		case LENGTH1: {
			while (++k < K1K3) {
				imDataDouble[k] = 0.0;
			}
			break;
		}
		case LENGTH2: {
			while (++k < K1K3) {
				executor1.execute(new DFTLength2RealDouble(
					reDataDouble, imDataDouble, k1K2 * (k / K1) + k, K1));
			}
			break;
		}
		case LENGTH3: {
			while (++k < K1K3) {
				executor1.execute(new DFTLength3RealDouble(
					reDataDouble, imDataDouble, k1K2 * (k / K1) + k, K1));
			}
			break;
		}
		case LENGTH4: {
			while (++k < K1K3) {
				executor1.execute(new DFTLength4RealDouble(
					reDataDouble, imDataDouble, k1K2 * (k / K1) + k, K1));
			}
			break;
		}
		case LENGTH5: {
			while (++k < K1K3) {
				executor1.execute(new DFTLength5RealDouble(
					reDataDouble, imDataDouble, k1K2 * (k / K1) + k, K1));
			}
			break;
		}
		case LENGTH6: {
			while (++k < K1K3) {
				executor1.execute(new DFTLength6RealDouble(
					reDataDouble, imDataDouble, k1K2 * (k / K1) + k, K1));
			}
			break;
		}
		case LENGTH8: {
			while (++k < K1K3) {
				executor1.execute(new DFTLength8RealDouble(
					reDataDouble, imDataDouble, k1K2 * (k / K1) + k, K1));
			}
			break;
		}
		case MIXEDRADIX: {
			while (++k < K1K3) {
				executor1.execute(new DFTMixedRadixRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1K2 * (k / K1) + k, K1,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble, fft1.K1));
			}
			break;
		}
		case PADDEDRADER: {
			while (++k < K1K3) {
				executor1.execute(new DFTPaddedRaderRealDouble(
					reDataDouble, imDataDouble, k1K2 * (k / K1) + k, K1,
					fft1.reConvolverDouble, fft1.imConvolverDouble,
					fft1.modular, fft1.inverseModular));
			}
			break;
		}
		case RADER: {
			while (++k < K1K3) {
				executor1.execute(new DFTRaderRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1K2 * (k / K1) + k, K1,
					fft1.reConvolverDouble, fft1.imConvolverDouble,
					fft1.modular, fft1.inverseModular));
			}
			break;
		}
		case RADIX2: {
			while (++k < K1K3) {
				executor1.execute(new DFTRadix2RealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1K2 * (k / K1) + k, K1,
					fft1.reUnitRootEvenDouble, fft1.imUnitRootEvenDouble,
					fft1.reUnitRootOddDouble, fft1.imUnitRootOddDouble));
			}
			break;
		}
		case SPLITRADIX: {
			while (++k < K1K3) {
				executor1.execute(new DFTSplitRadixRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, k1K2 * (k / K1) + k, K1,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble));
			}
			break;
		}
	}
	try {
		executor1.shutdown();
		executor1.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	// Rows
	final FFTSetup fft2 = FFTSetup.transforms.get(width);
	final ExecutorService executor2 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	final int skippedRows = (K2 - halfK2) * K1;
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTBruteForceDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, 1,
						fft2.reUnitRootDouble, fft2.imUnitRootDouble));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case COPRIMEFACTOR: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTCoprimeFactorDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, 1,
						fft2.ruritanian, fft2.chinese, fft2.K1));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTLength2Double(
						reDataDouble, imDataDouble, p, 1));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case LENGTH3: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTLength3Double(
						reDataDouble, imDataDouble, p, 1));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case LENGTH4: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTLength4Double(
						reDataDouble, imDataDouble, p, 1));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case LENGTH5: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTLength5Double(
						reDataDouble, imDataDouble, p, 1));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case LENGTH6: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTLength6Double(
						reDataDouble, imDataDouble, p, 1));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case LENGTH8: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTLength8Double(
						reDataDouble, imDataDouble, p, 1));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case MIXEDRADIX: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTMixedRadixDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, 1,
						fft2.reUnitRootDouble, fft2.imUnitRootDouble,
						fft2.K1));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case PADDEDRADER: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTPaddedRaderDouble(
						reDataDouble, imDataDouble, p, 1,
						fft2.reConvolverDouble, fft2.imConvolverDouble,
						fft2.modular, fft2.inverseModular));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case RADER: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTRaderDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, 1,
						fft2.reConvolverDouble, fft2.imConvolverDouble,
						fft2.modular, fft2.inverseModular));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case RADIX2: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTRadix2Double(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, 1,
						fft2.reUnitRootDouble, fft2.imUnitRootDouble));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case SPLITRADIX: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTSplitRadixDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, 1,
						fft2.reUnitRootDouble, fft2.imUnitRootDouble));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
	}
	try {
		executor2.shutdown();
		executor2.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	// Across
	final FFTSetup fft3 = FFTSetup.transforms.get(depth);
	final ExecutorService executor3 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft3.algorithm) {
		case BRUTEFORCE: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTBruteForceDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, K1K2,
						fft3.reUnitRootDouble, fft3.imUnitRootDouble));
					p++;
				}
			}
			break;
		}
		case COPRIMEFACTOR: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTCoprimeFactorDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, K1K2,
						fft3.ruritanian, fft3.chinese, fft3.K1));
					p++;
				}
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength2Double(
						reDataDouble, imDataDouble, p, K1K2));
					p++;
				}
			}
			break;
		}
		case LENGTH3: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength3Double(
						reDataDouble, imDataDouble, p, K1K2));
					p++;
				}
			}
			break;
		}
		case LENGTH4: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength4Double(
						reDataDouble, imDataDouble, p, K1K2));
					p++;
				}
			}
			break;
		}
		case LENGTH5: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength5Double(
						reDataDouble, imDataDouble, p, K1K2));
					p++;
				}
			}
			break;
		}
		case LENGTH6: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength6Double(
						reDataDouble, imDataDouble, p, K1K2));
					p++;
				}
			}
			break;
		}
		case LENGTH8: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength8Double(
						reDataDouble, imDataDouble, p, K1K2));
					p++;
				}
			}
			break;
		}
		case MIXEDRADIX: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTMixedRadixDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, K1K2,
						fft3.reUnitRootDouble, fft3.imUnitRootDouble, fft3.K1));
					p++;
				}
			}
			break;
		}
		case PADDEDRADER: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTPaddedRaderDouble(
						reDataDouble, imDataDouble, p, K1K2,
						fft3.reConvolverDouble, fft3.imConvolverDouble,
						fft3.modular, fft3.inverseModular));
					p++;
				}
			}
			break;
		}
		case RADER: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTRaderDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, K1K2,
						fft3.reConvolverDouble, fft3.imConvolverDouble,
						fft3.modular, fft3.inverseModular));
					p++;
				}
			}
			break;
		}
		case RADIX2: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTRadix2Double(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, K1K2,
						fft3.reUnitRootDouble, fft3.imUnitRootDouble));
					p++;
				}
			}
			break;
		}
		case SPLITRADIX: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTSplitRadixDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p, K1K2,
						fft3.reUnitRootDouble, fft3.imUnitRootDouble));
					p++;
				}
			}
			break;
		}
	}
	try {
		executor3.shutdown();
		executor3.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	int progressive = K1;
	int regressive = K1K2 - K1;
	while (progressive < regressive) {
		reDataDouble[regressive] = reDataDouble[progressive];
		imDataDouble[regressive] = -imDataDouble[progressive];
		progressive += K1;
		regressive -= K1;
	}
	int p = K1 + 1;
	int q = K1K2 - 1;
	for (int k2 = halfK2; (k2 < K2); k2++) {
		progressive = p;
		regressive = q;
		for (int k1 = 1; (k1 < K1); k1++) {
			reDataDouble[regressive] = reDataDouble[progressive];
			imDataDouble[regressive] = -imDataDouble[progressive];
			progressive++;
			regressive--;
		}
		p += K1;
		q -= K1;
	}
	p = K1K2 + K1;
	q = dataLength - K1;
	for (int k3 = 1; (k3 < K3); k3++) {
		progressive = p;
		regressive = q;
		for (int k2 = halfK2; (k2 < K2); k2++) {
			reDataDouble[regressive] = reDataDouble[progressive];
			imDataDouble[regressive] = -imDataDouble[progressive];
			progressive += K1;
			regressive -= K1;
		}
		p += K1K2;
		q -= K1K2;
	}
	progressive = K1K2 + K1 + 1;
	regressive = dataLength - 1;
	for (int k3 = 1; (k3 < K3); k3++) {
		for (int k2 = halfK2; (k2 < K2); k2++) {
			for (int k1 = 1; (k1 < K1); k1++) {
				reDataDouble[regressive] = reDataDouble[progressive];
				imDataDouble[regressive] = -imDataDouble[progressive];
				progressive++;
				regressive--;
			}
			progressive++;
			regressive--;
		}
		progressive += halfK2 * K1;
		regressive -= halfK2 * K1;
	}
} /* end transformRealDouble3DColumnFirst */

/*------------------------------------------------------------------*/
private void transformRealDouble3DRowFirst (
) {
	final int K1 = width.intValue();
	final int K2 = height.intValue();
	final int K3 = depth.intValue();
	final int K1K2 = K1 * K2;
	final int K2K3 = K2 * K3;
	final int halfK1 = (K1 >> 1) + 1;
	int k = 0;
	int p = 0;
	// First row for an odd (height * depth)
	if (1 == (K2K3 & 1)) {
		final FFTSetupReal fft = FFTSetupReal.transforms.get(width);
		switch (fft.algorithm) {
			case BRUTEFORCE: {
				new DFTBruteForceRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, 1,
					fft.reUnitRootDouble, fft.imUnitRootDouble).run();
				break;
			}
			case COPRIMEFACTOR: {
				new DFTCoprimeFactorRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, 1,
					fft.ruritanian, fft.chinese, fft.K1).run();
				break;
			}
			case DUOREAL: {
				throw(new IllegalStateException());
			}
			case EVENREAL: {
				new DFTEvenRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, 1,
					fft.reUnitRootDouble, fft.imUnitRootDouble).run();
				break;
			}
			case LENGTH1: {
				imDataDouble[0] = 0.0;
				break;
			}
			case LENGTH2: {
				new DFTLength2RealDouble(reDataDouble, imDataDouble, 0, 1).run();
				break;
			}
			case LENGTH3: {
				new DFTLength3RealDouble(reDataDouble, imDataDouble, 0, 1).run();
				break;
			}
			case LENGTH4: {
				new DFTLength4RealDouble(reDataDouble, imDataDouble, 0, 1).run();
				break;
			}
			case LENGTH5: {
				new DFTLength5RealDouble(reDataDouble, imDataDouble, 0, 1).run();
				break;
			}
			case LENGTH6: {
				new DFTLength6RealDouble(reDataDouble, imDataDouble, 0, 1).run();
				break;
			}
			case LENGTH8: {
				new DFTLength8RealDouble(reDataDouble, imDataDouble, 0, 1).run();
				break;
			}
			case MIXEDRADIX: {
				new DFTMixedRadixRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, 1,
					fft.reUnitRootDouble, fft.imUnitRootDouble, fft.K1).run();
				break;
			}
			case PADDEDRADER: {
				new DFTPaddedRaderRealDouble(reDataDouble, imDataDouble, 0, 1,
					fft.reConvolverDouble, fft.imConvolverDouble,
					fft.modular, fft.inverseModular).run();
				break;
			}
			case RADER: {
				new DFTRaderRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, 1,
					fft.reConvolverDouble, fft.imConvolverDouble,
					fft.modular, fft.inverseModular).run();
				break;
			}
			case RADIX2: {
				new DFTRadix2RealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, 1,
					fft.reUnitRootEvenDouble, fft.imUnitRootEvenDouble,
					fft.reUnitRootOddDouble, fft.imUnitRootOddDouble).run();
				break;
			}
			case SPLITRADIX: {
				new DFTSplitRadixRealDouble(reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, 0, 1,
					fft.reUnitRootDouble, fft.imUnitRootDouble).run();
				break;
			}
		}
		k = 1;
		p = K1;
	}
	// Remaining rows
	final FFTSetupDuoReal fft1 = FFTSetupDuoReal.transforms.get(width);
	final ExecutorService executor1 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft1.algorithm) {
		case BRUTEFORCE: {
			while (k++ < K2K3) {
				executor1.execute(new DFTBruteForceRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble));
				p += K1;
			}
			break;
		}
		case COPRIMEFACTOR: {
			while (k++ < K2K3) {
				executor1.execute(new DFTCoprimeFactorRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft1.ruritanian, fft1.chinese, fft1.K1));
				p += K1;
			}
			break;
		}
		case DUOREAL: {
			while (k++ < K2K3) {
				executor1.execute(new DFTDuoRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, p + K1, 1, K1));
				p += 2 * K1;
				k++;
			}
			break;
		}
		case EVENREAL: {
			while (k++ < K2K3) {
				executor1.execute(new DFTEvenRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble));
				p += K1;
			}
			break;
		}
		case LENGTH1: {
			while (k++ < K2K3) {
				imDataDouble[p] = 0.0;
				p += K1;
			}
			break;
		}
		case LENGTH2: {
			while (k++ < K2K3) {
				executor1.execute(new DFTLength2RealDouble(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH3: {
			while (k++ < K2K3) {
				executor1.execute(new DFTLength3RealDouble(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH4: {
			while (k++ < K2K3) {
				executor1.execute(new DFTLength4RealDouble(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH5: {
			while (k++ < K2K3) {
				executor1.execute(new DFTLength5RealDouble(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH6: {
			while (k++ < K2K3) {
				executor1.execute(new DFTLength6RealDouble(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH8: {
			while (k++ < K2K3) {
				executor1.execute(new DFTLength8RealDouble(
					reDataDouble, imDataDouble, p, 1));
				p += K1;
			}
			break;
		}
		case MIXEDRADIX: {
			while (k++ < K2K3) {
				executor1.execute(new DFTMixedRadixRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble, fft1.K1));
				p += K1;
			}
			break;
		}
		case PADDEDRADER: {
			while (k++ < K2K3) {
				executor1.execute(new DFTPaddedRaderRealDouble(
					reDataDouble, imDataDouble, p, 1,
					fft1.reConvolverDouble, fft1.imConvolverDouble,
					fft1.modular, fft1.inverseModular));
				p += K1;
			}
			break;
		}
		case RADER: {
			while (k++ < K2K3) {
				executor1.execute(new DFTRaderRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft1.reConvolverDouble, fft1.imConvolverDouble,
					fft1.modular, fft1.inverseModular));
				p += K1;
			}
			break;
		}
		case RADIX2: {
			while (k++ < K2K3) {
				executor1.execute(new DFTRadix2RealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft1.reUnitRootEvenDouble, fft1.imUnitRootEvenDouble,
					fft1.reUnitRootOddDouble, fft1.imUnitRootOddDouble));
				p += K1;
			}
			break;
		}
		case SPLITRADIX: {
			while (k++ < K2K3) {
				executor1.execute(new DFTSplitRadixRealDouble(
					reDataDouble, imDataDouble,
					reBufferDouble, imBufferDouble, p, 1,
					fft1.reUnitRootDouble, fft1.imUnitRootDouble));
				p += K1;
			}
			break;
		}
	}
	try {
		executor1.shutdown();
		executor1.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	// Columns
	final FFTSetup fft2 = FFTSetup.transforms.get(height);
	final ExecutorService executor2 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	p = 0;
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTBruteForceDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1,
						fft2.reUnitRootDouble, fft2.imUnitRootDouble));
				}
				p += K1K2;
			}
			break;
		}
		case COPRIMEFACTOR: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTCoprimeFactorDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1,
						fft2.ruritanian, fft2.chinese, fft2.K1));
				}
				p += K1K2;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTLength2Double(
						reDataDouble, imDataDouble, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH3: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTLength3Double(
						reDataDouble, imDataDouble, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH4: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTLength4Double(
						reDataDouble, imDataDouble, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH5: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTLength5Double(
						reDataDouble, imDataDouble, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH6: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTLength6Double(
						reDataDouble, imDataDouble, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH8: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTLength8Double(
						reDataDouble, imDataDouble, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case MIXEDRADIX: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTMixedRadixDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1,
						fft2.reUnitRootDouble, fft2.imUnitRootDouble, fft2.K1));
				}
				p += K1K2;
			}
			break;
		}
		case PADDEDRADER: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTPaddedRaderDouble(
						reDataDouble, imDataDouble, p + k1, K1,
						fft2.reConvolverDouble, fft2.imConvolverDouble,
						fft2.modular, fft2.inverseModular));
				}
				p += K1K2;
			}
			break;
		}
		case RADER: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTRaderDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1,
						fft2.reConvolverDouble, fft2.imConvolverDouble,
						fft2.modular, fft2.inverseModular));
				}
				p += K1K2;
			}
			break;
		}
		case RADIX2: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTRadix2Double(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1,
						fft2.reUnitRootDouble, fft2.imUnitRootDouble));
				}
				p += K1K2;
			}
			break;
		}
		case SPLITRADIX: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTSplitRadixDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1,
						fft2.reUnitRootDouble, fft2.imUnitRootDouble));
				}
				p += K1K2;
			}
			break;
		}
	}
	try {
		executor2.shutdown();
		executor2.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	// Across
	final FFTSetup fft3 = FFTSetup.transforms.get(depth);
	final ExecutorService executor3 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	p = 0;
	switch (fft3.algorithm) {
		case BRUTEFORCE: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTBruteForceDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1K2,
						fft3.reUnitRootDouble, fft3.imUnitRootDouble));
				}
				p += K1;
			}
			break;
		}
		case COPRIMEFACTOR: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTCoprimeFactorDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1K2,
						fft3.ruritanian, fft3.chinese, fft3.K1));
				}
				p += K1;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTLength2Double(
						reDataDouble, imDataDouble, p + k1, K1K2));
				}
				p += K1;
			}
			break;
		}
		case LENGTH3: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTLength3Double(
						reDataDouble, imDataDouble, p + k1, K1K2));
				}
				p += K1;
			}
			break;
		}
		case LENGTH4: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTLength4Double(
						reDataDouble, imDataDouble, p + k1, K1K2));
				}
				p += K1;
			}
			break;
		}
		case LENGTH5: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTLength5Double(
						reDataDouble, imDataDouble, p + k1, K1K2));
				}
				p += K1;
			}
			break;
		}
		case LENGTH6: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTLength6Double(
						reDataDouble, imDataDouble, p + k1, K1K2));
				}
				p += K1;
			}
			break;
		}
		case LENGTH8: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTLength8Double(
						reDataDouble, imDataDouble, p + k1, K1K2));
				}
				p += K1;
			}
			break;
		}
		case MIXEDRADIX: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTMixedRadixDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1K2,
						fft3.reUnitRootDouble, fft3.imUnitRootDouble, fft3.K1));
				}
				p += K1;
			}
			break;
		}
		case PADDEDRADER: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTPaddedRaderDouble(
						reDataDouble, imDataDouble, p + k1, K1K2,
						fft3.reConvolverDouble, fft3.imConvolverDouble,
						fft3.modular, fft3.inverseModular));
				}
				p += K1;
			}
			break;
		}
		case RADER: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTRaderDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1K2,
						fft3.reConvolverDouble, fft3.imConvolverDouble,
						fft3.modular, fft3.inverseModular));
				}
				p += K1;
			}
			break;
		}
		case RADIX2: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTRadix2Double(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1K2,
						fft3.reUnitRootDouble, fft3.imUnitRootDouble));
				}
				p += K1;
			}
			break;
		}
		case SPLITRADIX: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTSplitRadixDouble(
						reDataDouble, imDataDouble,
						reBufferDouble, imBufferDouble, p + k1, K1K2,
						fft3.reUnitRootDouble, fft3.imUnitRootDouble));
				}
				p += K1;
			}
			break;
		}
	}
	try {
		executor3.shutdown();
		executor3.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	int progressive = 0;
	int regressive = K1;
	while (++progressive < --regressive) {
		reDataDouble[regressive] = reDataDouble[progressive];
		imDataDouble[regressive] = -imDataDouble[progressive];
	}
	p = K1 + 1;
	int q = K1K2 - 1;
	for (int k2 = 1; (k2 < K2); k2++) {
		progressive = p;
		regressive = q;
		for (int k1 = halfK1; (k1 < K1); k1++) {
			reDataDouble[regressive] = reDataDouble[progressive];
			imDataDouble[regressive] = -imDataDouble[progressive];
			progressive++;
			regressive--;
		}
		p += K1;
		q -= K1;
	}
	p = K1K2 + 1;
	q = K1K2 * (K3 - 1) + K1 - 1;
	for (int k3 = 1; (k3 < K3); k3++) {
		progressive = p;
		regressive = q;
		for (int k1 = halfK1; (k1 < K1); k1++) {
			reDataDouble[regressive] = reDataDouble[progressive];
			imDataDouble[regressive] = -imDataDouble[progressive];
			progressive++;
			regressive--;
		}
		p += K1K2;
		q -= K1K2;
	}
	progressive = K1K2 + K1 + 1;
	regressive = dataLength - 1;
	for (int k3 = 1; (k3 < K3); k3++) {
		for (int k2 = 1; (k2 < K2); k2++) {
			for (int k1 = halfK1; (k1 < K1); k1++) {
				reDataDouble[regressive] = reDataDouble[progressive];
				imDataDouble[regressive] = -imDataDouble[progressive];
				progressive++;
				regressive--;
			}
			progressive += halfK1;
			regressive -= halfK1;
		}
		progressive += K1;
		regressive -= K1;
	}
} /* end transformRealDouble3DRowFirst */

/*------------------------------------------------------------------*/
private void transformRealFloat1D (
) {
	final FFTSetupReal fft = FFTSetupReal.transforms.get(width);
	switch (fft.algorithm) {
		case BRUTEFORCE: {
			new DFTBruteForceRealFloat(reDataFloat, imDataFloat,
				reBufferFloat, imBufferFloat, 0, 1,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
		case COPRIMEFACTOR: {
			new DFTCoprimeFactorRealFloat(reDataFloat, imDataFloat,
				reBufferFloat, imBufferFloat, 0, 1,
				fft.ruritanian, fft.chinese, fft.K1).run();
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			new DFTEvenRealFloat(reDataFloat, imDataFloat,
				reBufferFloat, imBufferFloat, 0, 1,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
		}
		case LENGTH1: {
			imDataFloat[0] = 0.0F;
			break;
		}
		case LENGTH2: {
			new DFTLength2RealFloat(reDataFloat, imDataFloat, 0, 1).run();
			break;
		}
		case LENGTH3: {
			new DFTLength3RealFloat(reDataFloat, imDataFloat, 0, 1).run();
			break;
		}
		case LENGTH4: {
			new DFTLength4RealFloat(reDataFloat, imDataFloat, 0, 1).run();
			break;
		}
		case LENGTH5: {
			new DFTLength5RealFloat(reDataFloat, imDataFloat, 0, 1).run();
			break;
		}
		case LENGTH6: {
			new DFTLength6RealFloat(reDataFloat, imDataFloat, 0, 1).run();
			break;
		}
		case LENGTH8: {
			new DFTLength8RealFloat(reDataFloat, imDataFloat, 0, 1).run();
			break;
		}
		case MIXEDRADIX: {
			new DFTMixedRadixRealFloat(reDataFloat, imDataFloat,
				reBufferFloat, imBufferFloat, 0, 1,
				fft.reUnitRootFloat, fft.imUnitRootFloat, fft.K1).run();
			break;
		}
		case PADDEDRADER: {
			new DFTPaddedRaderRealFloat(reDataFloat, imDataFloat, 0, 1,
				fft.reConvolverFloat, fft.imConvolverFloat,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADER: {
			new DFTRaderRealFloat(reDataFloat, imDataFloat,
				reBufferFloat, imBufferFloat, 0, 1,
				fft.reConvolverFloat, fft.imConvolverFloat,
				fft.modular, fft.inverseModular).run();
			break;
		}
		case RADIX2: {
			new DFTRadix2RealFloat(reDataFloat, imDataFloat,
				reBufferFloat, imBufferFloat, 0, 1,
				fft.reUnitRootEvenFloat, fft.imUnitRootEvenFloat,
				fft.reUnitRootOddFloat, fft.imUnitRootOddFloat).run();
			break;
		}
		case SPLITRADIX: {
			new DFTSplitRadixRealFloat(reDataFloat, imDataFloat,
				reBufferFloat, imBufferFloat, 0, 1,
				fft.reUnitRootFloat, fft.imUnitRootFloat).run();
			break;
		}
	}
	int progressive = 0;
	int regressive = width.intValue();
	while (++progressive < --regressive) {
		reDataFloat[regressive] = reDataFloat[progressive];
		imDataFloat[regressive] = -imDataFloat[progressive];
	}
} /* end transformRealFloat1D */

/*------------------------------------------------------------------*/
private void transformRealFloat2DColumnFirst (
) {
	final int K1 = width.intValue();
	final int K2 = height.intValue();
	final int halfK2 = (K2 >> 1) + 1;
	int k1 = -1;
	// First column for an odd width
	if (1 == (K1 & 1)) {
		final FFTSetupReal fft = FFTSetupReal.transforms.get(height);
		switch (fft.algorithm) {
			case BRUTEFORCE: {
				new DFTBruteForceRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, K1,
					fft.reUnitRootFloat, fft.imUnitRootFloat).run();
				break;
			}
			case COPRIMEFACTOR: {
				new DFTCoprimeFactorRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, K1,
					fft.ruritanian, fft.chinese, fft.K1).run();
				break;
			}
			case DUOREAL: {
				throw(new IllegalStateException());
			}
			case EVENREAL: {
				new DFTEvenRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, K1,
					fft.reUnitRootFloat, fft.imUnitRootFloat).run();
				break;
			}
			case LENGTH1: {
				imDataFloat[0] = 0.0F;
				break;
			}
			case LENGTH2: {
				new DFTLength2RealFloat(reDataFloat, imDataFloat, 0, K1).run();
				break;
			}
			case LENGTH3: {
				new DFTLength3RealFloat(reDataFloat, imDataFloat, 0, K1).run();
				break;
			}
			case LENGTH4: {
				new DFTLength4RealFloat(reDataFloat, imDataFloat, 0, K1).run();
				break;
			}
			case LENGTH5: {
				new DFTLength5RealFloat(reDataFloat, imDataFloat, 0, K1).run();
				break;
			}
			case LENGTH6: {
				new DFTLength6RealFloat(reDataFloat, imDataFloat, 0, K1).run();
				break;
			}
			case LENGTH8: {
				new DFTLength8RealFloat(reDataFloat, imDataFloat, 0, K1).run();
				break;
			}
			case MIXEDRADIX: {
				new DFTMixedRadixRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, K1,
					fft.reUnitRootFloat, fft.imUnitRootFloat, fft.K1).run();
				break;
			}
			case PADDEDRADER: {
				new DFTPaddedRaderRealFloat(reDataFloat, imDataFloat, 0, K1,
					fft.reConvolverFloat, fft.imConvolverFloat,
					fft.modular, fft.inverseModular).run();
				break;
			}
			case RADER: {
				new DFTRaderRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, K1,
					fft.reConvolverFloat, fft.imConvolverFloat,
					fft.modular, fft.inverseModular).run();
				break;
			}
			case RADIX2: {
				new DFTRadix2RealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, K1,
					fft.reUnitRootEvenFloat, fft.imUnitRootEvenFloat,
					fft.reUnitRootOddFloat, fft.imUnitRootOddFloat).run();
				break;
			}
			case SPLITRADIX: {
				new DFTSplitRadixRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, K1,
					fft.reUnitRootFloat, fft.imUnitRootFloat).run();
				break;
			}
		}
		k1 = 0;
	}
	// Remaining columns
	final FFTSetupDuoReal fft1 = FFTSetupDuoReal.transforms.get(height);
	final ExecutorService executor1 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft1.algorithm) {
		case BRUTEFORCE: {
			while (++k1 < K1) {
				executor1.execute(new DFTBruteForceRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1, K1,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat));
			}
			break;
		}
		case COPRIMEFACTOR: {
			while (++k1 < K1) {
				executor1.execute(new DFTCoprimeFactorRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1, K1,
					fft1.ruritanian, fft1.chinese, fft1.K1));
			}
			break;
		}
		case DUOREAL: {
			while (++k1 < K1) {
				executor1.execute(new DFTDuoRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1, k1 + 1, K1, K2));
				++k1;
			}
			break;
		}
		case EVENREAL: {
			while (++k1 < K1) {
				executor1.execute(new DFTEvenRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1, K1,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat));
			}
			break;
		}
		case LENGTH1: {
			while (++k1 < K1) {
				imDataFloat[k1] = 0.0F;
			}
			break;
		}
		case LENGTH2: {
			while (++k1 < K1) {
				executor1.execute(new DFTLength2RealFloat(
					reDataFloat, imDataFloat, k1, K1));
			}
			break;
		}
		case LENGTH3: {
			while (++k1 < K1) {
				executor1.execute(new DFTLength3RealFloat(
					reDataFloat, imDataFloat, k1, K1));
			}
			break;
		}
		case LENGTH4: {
			while (++k1 < K1) {
				executor1.execute(new DFTLength4RealFloat(
					reDataFloat, imDataFloat, k1, K1));
			}
			break;
		}
		case LENGTH5: {
			while (++k1 < K1) {
				executor1.execute(new DFTLength5RealFloat(
					reDataFloat, imDataFloat, k1, K1));
			}
			break;
		}
		case LENGTH6: {
			while (++k1 < K1) {
				executor1.execute(new DFTLength6RealFloat(
					reDataFloat, imDataFloat, k1, K1));
			}
			break;
		}
		case LENGTH8: {
			while (++k1 < K1) {
				executor1.execute(new DFTLength8RealFloat(
					reDataFloat, imDataFloat, k1, K1));
			}
			break;
		}
		case MIXEDRADIX: {
			while (++k1 < K1) {
				executor1.execute(new DFTMixedRadixRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1, K1,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat, fft1.K1));
			}
			break;
		}
		case PADDEDRADER: {
			while (++k1 < K1) {
				executor1.execute(new DFTPaddedRaderRealFloat(
					reDataFloat, imDataFloat, k1, K1,
					fft1.reConvolverFloat, fft1.imConvolverFloat,
					fft1.modular, fft1.inverseModular));
			}
			break;
		}
		case RADER: {
			while (++k1 < K1) {
				executor1.execute(new DFTRaderRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1, K1,
					fft1.reConvolverFloat, fft1.imConvolverFloat,
					fft1.modular, fft1.inverseModular));
			}
			break;
		}
		case RADIX2: {
			while (++k1 < K1) {
				executor1.execute(new DFTRadix2RealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1, K1,
					fft1.reUnitRootEvenFloat, fft1.imUnitRootEvenFloat,
					fft1.reUnitRootOddFloat, fft1.imUnitRootOddFloat));
			}
			break;
		}
		case SPLITRADIX: {
			while (++k1 < K1) {
				executor1.execute(new DFTSplitRadixRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1, K1,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat));
			}
			break;
		}
	}
	try {
		executor1.shutdown();
		executor1.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	// Rows
	final FFTSetup fft2 = FFTSetup.transforms.get(width);
	final ExecutorService executor2 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	int p = 0;
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTBruteForceFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat));
				p += K1;
			}
			break;
		}
		case COPRIMEFACTOR: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTCoprimeFactorFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft2.ruritanian, fft2.chinese, fft2.K1));
				p += K1;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTLength2Float(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH3: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTLength3Float(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH4: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTLength4Float(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH5: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTLength5Float(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH6: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTLength6Float(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH8: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTLength8Float(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case MIXEDRADIX: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTMixedRadixFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat, fft2.K1));
				p += K1;
			}
			break;
		}
		case PADDEDRADER: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTPaddedRaderFloat(
					reDataFloat, imDataFloat, p, 1,
					fft2.reConvolverFloat, fft2.imConvolverFloat,
					fft2.modular, fft2.inverseModular));
				p += K1;
			}
			break;
		}
		case RADER: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTRaderFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft2.reConvolverFloat, fft2.imConvolverFloat,
					fft2.modular, fft2.inverseModular));
				p += K1;
			}
			break;
		}
		case RADIX2: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTRadix2Float(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat));
				p += K1;
			}
			break;
		}
		case SPLITRADIX: {
			for (int k2 = 0; (k2 < halfK2); k2++) {
				executor2.execute(new DFTSplitRadixFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat));
				p += K1;
			}
			break;
		}
	}
	try {
		executor2.shutdown();
		executor2.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	int progressive = K1;
	int regressive = K1 * (K2 - 1);
	while (progressive < regressive) {
		reDataFloat[regressive] = reDataFloat[progressive];
		imDataFloat[regressive] = -imDataFloat[progressive];
		progressive += K1;
		regressive -= K1;
	}
	p = K1 + 1;
	int q = K1 * K2 - 1;
	for (int k2 = halfK2; (k2 < K2); k2++) {
		progressive = p;
		regressive = q;
		for (k1 = 1; (k1 < K1); k1++) {
			reDataFloat[regressive] = reDataFloat[progressive];
			imDataFloat[regressive] = -imDataFloat[progressive];
			progressive++;
			regressive--;
		}
		p += K1;
		q -= K1;
	}
} /* transformRealFloat2DColumnFirst */

/*------------------------------------------------------------------*/
private void transformRealFloat2DRowFirst (
) {
	final int K1 = width.intValue();
	final int K2 = height.intValue();
	final int halfK1 = (K1 >> 1) + 1;
	int p = 0;
	int k2 = 0;
	// First row for an odd height
	if (1 == (K2 & 1)) {
		final FFTSetupReal fft = FFTSetupReal.transforms.get(width);
		switch (fft.algorithm) {
			case BRUTEFORCE: {
				new DFTBruteForceRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, 1,
					fft.reUnitRootFloat, fft.imUnitRootFloat).run();
				break;
			}
			case COPRIMEFACTOR: {
				new DFTCoprimeFactorRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, 1,
					fft.ruritanian, fft.chinese, fft.K1).run();
				break;
			}
			case DUOREAL: {
				throw(new IllegalStateException());
			}
			case EVENREAL: {
				new DFTEvenRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, 1,
					fft.reUnitRootFloat, fft.imUnitRootFloat).run();
				break;
			}
			case LENGTH1: {
				imDataFloat[0] = 0.0F;
				break;
			}
			case LENGTH2: {
				new DFTLength2RealFloat(reDataFloat, imDataFloat, 0, 1).run();
				break;
			}
			case LENGTH3: {
				new DFTLength3RealFloat(reDataFloat, imDataFloat, 0, 1).run();
				break;
			}
			case LENGTH4: {
				new DFTLength4RealFloat(reDataFloat, imDataFloat, 0, 1).run();
				break;
			}
			case LENGTH5: {
				new DFTLength5RealFloat(reDataFloat, imDataFloat, 0, 1).run();
				break;
			}
			case LENGTH6: {
				new DFTLength6RealFloat(reDataFloat, imDataFloat, 0, 1).run();
				break;
			}
			case LENGTH8: {
				new DFTLength8RealFloat(reDataFloat, imDataFloat, 0, 1).run();
				break;
			}
			case MIXEDRADIX: {
				new DFTMixedRadixRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, 1,
					fft.reUnitRootFloat, fft.imUnitRootFloat, fft.K1).run();
				break;
			}
			case PADDEDRADER: {
				new DFTPaddedRaderRealFloat(reDataFloat, imDataFloat, 0, 1,
					fft.reConvolverFloat, fft.imConvolverFloat,
					fft.modular, fft.inverseModular).run();
				break;
			}
			case RADER: {
				new DFTRaderRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, 1,
					fft.reConvolverFloat, fft.imConvolverFloat,
					fft.modular, fft.inverseModular).run();
				break;
			}
			case RADIX2: {
				new DFTRadix2RealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, 1,
					fft.reUnitRootEvenFloat, fft.imUnitRootEvenFloat,
					fft.reUnitRootOddFloat, fft.imUnitRootOddFloat).run();
				break;
			}
			case SPLITRADIX: {
				new DFTSplitRadixRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, 1,
					fft.reUnitRootFloat, fft.imUnitRootFloat).run();
				break;
			}
		}
		p = K1;
		k2 = 1;
	}
	// Remaining rows
	final FFTSetupDuoReal fft1 = FFTSetupDuoReal.transforms.get(width);
	final ExecutorService executor1 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft1.algorithm) {
		case BRUTEFORCE: {
			while (k2++ < K2) {
				executor1.execute(new DFTBruteForceRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat));
				p += K1;
			}
			break;
		}
		case COPRIMEFACTOR: {
			while (k2++ < K2) {
				executor1.execute(new DFTCoprimeFactorRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft1.ruritanian, fft1.chinese, fft1.K1));
				p += K1;
			}
			break;
		}
		case DUOREAL: {
			while (k2++ < K2) {
				executor1.execute(new DFTDuoRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, p + K1, 1, K1));
				p += 2 * K1;
				k2++;
			}
			break;
		}
		case EVENREAL: {
			while (k2++ < K2) {
				executor1.execute(new DFTEvenRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat));
				p += K1;
			}
			break;
		}
		case LENGTH1: {
			while (k2++ < K2) {
				imDataFloat[p] = 0.0F;
				p += K1;
			}
			break;
		}
		case LENGTH2: {
			while (k2++ < K2) {
				executor1.execute(new DFTLength2RealFloat(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH3: {
			while (k2++ < K2) {
				executor1.execute(new DFTLength3RealFloat(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH4: {
			while (k2++ < K2) {
				executor1.execute(new DFTLength4RealFloat(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH5: {
			while (k2++ < K2) {
				executor1.execute(new DFTLength5RealFloat(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH6: {
			while (k2++ < K2) {
				executor1.execute(new DFTLength6RealFloat(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH8: {
			while (k2++ < K2) {
				executor1.execute(new DFTLength8RealFloat(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case MIXEDRADIX: {
			while (k2++ < K2) {
				executor1.execute(new DFTMixedRadixRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat, fft1.K1));
				p += K1;
			}
			break;
		}
		case PADDEDRADER: {
			while (k2++ < K2) {
				executor1.execute(new DFTPaddedRaderRealFloat(
					reDataFloat, imDataFloat, p, 1,
					fft1.reConvolverFloat, fft1.imConvolverFloat,
					fft1.modular, fft1.inverseModular));
				p += K1;
			}
			break;
		}
		case RADER: {
			while (k2++ < K2) {
				executor1.execute(new DFTRaderRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft1.reConvolverFloat, fft1.imConvolverFloat,
					fft1.modular, fft1.inverseModular));
				p += K1;
			}
			break;
		}
		case RADIX2: {
			while (k2++ < K2) {
				executor1.execute(new DFTRadix2RealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft1.reUnitRootEvenFloat, fft1.imUnitRootEvenFloat,
					fft1.reUnitRootOddFloat, fft1.imUnitRootOddFloat));
				p += K1;
			}
			break;
		}
		case SPLITRADIX: {
			while (k2++ < K2) {
				executor1.execute(new DFTSplitRadixRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat));
				p += K1;
			}
			break;
		}
	}
	try {
		executor1.shutdown();
		executor1.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	// Columns
	final FFTSetup fft2 = FFTSetup.transforms.get(height);
	final ExecutorService executor2 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTBruteForceFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1, K1,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat));
			}
			break;
		}
		case COPRIMEFACTOR: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTCoprimeFactorFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1, K1,
					fft2.ruritanian, fft2.chinese, fft2.K1));
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTLength2Float(
					reDataFloat, imDataFloat, k1, K1));
			}
			break;
		}
		case LENGTH3: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTLength3Float(
					reDataFloat, imDataFloat, k1, K1));
			}
			break;
		}
		case LENGTH4: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTLength4Float(
					reDataFloat, imDataFloat, k1, K1));
			}
			break;
		}
		case LENGTH5: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTLength5Float(
					reDataFloat, imDataFloat, k1, K1));
			}
			break;
		}
		case LENGTH6: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTLength6Float(
					reDataFloat, imDataFloat, k1, K1));
			}
			break;
		}
		case LENGTH8: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTLength8Float(
					reDataFloat, imDataFloat, k1, K1));
			}
			break;
		}
		case MIXEDRADIX: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTMixedRadixFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1, K1,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat, fft2.K1));
			}
			break;
		}
		case PADDEDRADER: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTPaddedRaderFloat(
					reDataFloat, imDataFloat, k1, K1,
					fft2.reConvolverFloat, fft2.imConvolverFloat,
					fft2.modular, fft2.inverseModular));
			}
			break;
		}
		case RADER: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTRaderFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1, K1,
					fft2.reConvolverFloat, fft2.imConvolverFloat,
					fft2.modular, fft2.inverseModular));
			}
			break;
		}
		case RADIX2: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTRadix2Float(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1, K1,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat));
			}
			break;
		}
		case SPLITRADIX: {
			for (int k1 = 0; (k1 < halfK1); k1++) {
				executor2.execute(new DFTSplitRadixFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1, K1,
					fft2.reUnitRootFloat, fft2.imUnitRootFloat));
			}
			break;
		}
	}
	try {
		executor2.shutdown();
		executor2.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	int progressive = 0;
	int regressive = K1;
	while (++progressive < --regressive) {
		reDataFloat[regressive] = reDataFloat[progressive];
		imDataFloat[regressive] = -imDataFloat[progressive];
	}
	p = K1 + 1;
	int q = K1 * K2 - 1;
	for (k2 = 1; (k2 < K2); k2++) {
		progressive = p;
		regressive = q;
		for (int k1 = halfK1; (k1 < K1); k1++) {
			reDataFloat[regressive] = reDataFloat[progressive];
			imDataFloat[regressive] = -imDataFloat[progressive];
			progressive++;
			regressive--;
		}
		p += K1;
		q -= K1;
	}
} /* end transformRealFloat2DRowFirst */

/*------------------------------------------------------------------*/
private void transformRealFloat3DAcrossFirst (
) {
	final int K1 = width.intValue();
	final int K2 = height.intValue();
	final int K3 = depth.intValue();
	final int K1K2 = K1 * K2;
	final int halfK3 = (K3 >> 1) + 1;
	int k = -1;
	// First across for an odd (width * height)
	if (1 == (K1K2 & 1)) {
		final FFTSetupReal fft = FFTSetupReal.transforms.get(depth);
		switch (fft.algorithm) {
			case BRUTEFORCE: {
				new DFTBruteForceRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, K1K2,
					fft.reUnitRootFloat, fft.imUnitRootFloat).run();
				break;
			}
			case COPRIMEFACTOR: {
				new DFTCoprimeFactorRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, K1K2,
					fft.ruritanian, fft.chinese, fft.K1).run();
				break;
			}
			case DUOREAL: {
				throw(new IllegalStateException());
			}
			case EVENREAL: {
				new DFTEvenRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, K1K2,
					fft.reUnitRootFloat, fft.imUnitRootFloat).run();
				break;
			}
			case LENGTH1: {
				imDataFloat[0] = 0.0F;
				break;
			}
			case LENGTH2: {
				new DFTLength2RealFloat(reDataFloat, imDataFloat,
					0, K1K2).run();
				break;
			}
			case LENGTH3: {
				new DFTLength3RealFloat(reDataFloat, imDataFloat,
					0, K1K2).run();
				break;
			}
			case LENGTH4: {
				new DFTLength4RealFloat(reDataFloat, imDataFloat,
					0, K1K2).run();
				break;
			}
			case LENGTH5: {
				new DFTLength5RealFloat(reDataFloat, imDataFloat,
					0, K1K2).run();
				break;
			}
			case LENGTH6: {
				new DFTLength6RealFloat(reDataFloat, imDataFloat,
					0, K1K2).run();
				break;
			}
			case LENGTH8: {
				new DFTLength8RealFloat(reDataFloat, imDataFloat,
					0, K1K2).run();
				break;
			}
			case MIXEDRADIX: {
				new DFTMixedRadixRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, K1K2,
					fft.reUnitRootFloat, fft.imUnitRootFloat, fft.K1).run();
				break;
			}
			case PADDEDRADER: {
				new DFTPaddedRaderRealFloat(reDataFloat, imDataFloat, 0, K1K2,
					fft.reConvolverFloat, fft.imConvolverFloat,
					fft.modular, fft.inverseModular).run();
				break;
			}
			case RADER: {
				new DFTRaderRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, K1K2,
					fft.reConvolverFloat, fft.imConvolverFloat,
					fft.modular, fft.inverseModular).run();
				break;
			}
			case RADIX2: {
				new DFTRadix2RealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, K1K2,
					fft.reUnitRootEvenFloat, fft.imUnitRootEvenFloat,
					fft.reUnitRootOddFloat, fft.imUnitRootOddFloat).run();
				break;
			}
			case SPLITRADIX: {
				new DFTSplitRadixRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, K1K2,
					fft.reUnitRootFloat, fft.imUnitRootFloat).run();
				break;
			}
		}
		k = 0;
	}
	// Remaining across
	final FFTSetupDuoReal fft1 = FFTSetupDuoReal.transforms.get(depth);
	final ExecutorService executor1 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft1.algorithm) {
		case BRUTEFORCE: {
			while (++k < K1K2) {
				executor1.execute(new DFTBruteForceRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k, K1K2,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat));
			}
			break;
		}
		case COPRIMEFACTOR: {
			while (++k < K1K2) {
				executor1.execute(new DFTCoprimeFactorRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k, K1K2,
					fft1.ruritanian, fft1.chinese, fft1.K1));
			}
			break;
		}
		case DUOREAL: {
			while (++k < K1K2) {
				executor1.execute(new DFTDuoRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k, k + 1, K1K2, K3));
				k++;
			}
			break;
		}
		case EVENREAL: {
			while (++k < K1K2) {
				executor1.execute(new DFTEvenRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k, K1K2,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat));
			}
			break;
		}
		case LENGTH1: {
			while (++k < K1K2) {
				imDataFloat[k] = 0.0F;
			}
			break;
		}
		case LENGTH2: {
			while (++k < K1K2) {
				executor1.execute(new DFTLength2RealFloat(
					reDataFloat, imDataFloat, k, K1K2));
			}
			break;
		}
		case LENGTH3: {
			while (++k < K1K2) {
				executor1.execute(new DFTLength3RealFloat(
					reDataFloat, imDataFloat, k, K1K2));
			}
			break;
		}
		case LENGTH4: {
			while (++k < K1K2) {
				executor1.execute(new DFTLength4RealFloat(
					reDataFloat, imDataFloat, k, K1K2));
			}
			break;
		}
		case LENGTH5: {
			while (++k < K1K2) {
				executor1.execute(new DFTLength5RealFloat(
					reDataFloat, imDataFloat, k, K1K2));
			}
			break;
		}
		case LENGTH6: {
			while (++k < K1K2) {
				executor1.execute(new DFTLength6RealFloat(
					reDataFloat, imDataFloat, k, K1K2));
			}
			break;
		}
		case LENGTH8: {
			while (++k < K1K2) {
				executor1.execute(new DFTLength8RealFloat(
					reDataFloat, imDataFloat, k, K1K2));
			}
			break;
		}
		case MIXEDRADIX: {
			while (++k < K1K2) {
				executor1.execute(new DFTMixedRadixRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k, K1K2,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat, fft1.K1));
			}
			break;
		}
		case PADDEDRADER: {
			while (++k < K1K2) {
				executor1.execute(new DFTPaddedRaderRealFloat(
					reDataFloat, imDataFloat, k, K1K2,
					fft1.reConvolverFloat, fft1.imConvolverFloat,
					fft1.modular, fft1.inverseModular));
			}
			break;
		}
		case RADER: {
			while (++k < K1K2) {
				executor1.execute(new DFTRaderRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k, K1K2,
					fft1.reConvolverFloat, fft1.imConvolverFloat,
					fft1.modular, fft1.inverseModular));
			}
			break;
		}
		case RADIX2: {
			while (++k < K1K2) {
				executor1.execute(new DFTRadix2RealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k, K1K2,
					fft1.reUnitRootEvenFloat, fft1.imUnitRootEvenFloat,
					fft1.reUnitRootOddFloat, fft1.imUnitRootOddFloat));
			}
			break;
		}
		case SPLITRADIX: {
			while (++k < K1K2) {
				executor1.execute(new DFTSplitRadixRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k, K1K2,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat));
			}
			break;
		}
	}
	try {
		executor1.shutdown();
		executor1.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	// Rows
	final FFTSetup fft2 = FFTSetup.transforms.get(width);
	final ExecutorService executor2 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTBruteForceFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, 1,
						fft2.reUnitRootFloat, fft2.imUnitRootFloat));
					p += K1;
				}
			}
			break;
		}
		case COPRIMEFACTOR: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTCoprimeFactorFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, 1,
						fft2.ruritanian, fft2.chinese, fft2.K1));
					p += K1;
				}
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTLength2Float(
						reDataFloat, imDataFloat, p, 1));
					p += K1;
				}
			}
			break;
		}
		case LENGTH3: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTLength3Float(
						reDataFloat, imDataFloat, p, 1));
					p += K1;
				}
			}
			break;
		}
		case LENGTH4: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTLength4Float(
						reDataFloat, imDataFloat, p, 1));
					p += K1;
				}
			}
			break;
		}
		case LENGTH5: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTLength5Float(
						reDataFloat, imDataFloat, p, 1));
					p += K1;
				}
			}
			break;
		}
		case LENGTH6: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTLength6Float(
						reDataFloat, imDataFloat, p, 1));
					p += K1;
				}
			}
			break;
		}
		case LENGTH8: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTLength8Float(
						reDataFloat, imDataFloat, p, 1));
					p += K1;
				}
			}
			break;
		}
		case MIXEDRADIX: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTMixedRadixFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, 1,
						fft2.reUnitRootFloat, fft2.imUnitRootFloat,
						fft2.K1));
					p += K1;
				}
			}
			break;
		}
		case PADDEDRADER: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTPaddedRaderFloat(
						reDataFloat, imDataFloat, p, 1,
						fft2.reConvolverFloat, fft2.imConvolverFloat,
						fft2.modular, fft2.inverseModular));
					p += K1;
				}
			}
			break;
		}
		case RADER: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTRaderFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, 1,
						fft2.reConvolverFloat, fft2.imConvolverFloat,
						fft2.modular, fft2.inverseModular));
					p += K1;
				}
			}
			break;
		}
		case RADIX2: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTRadix2Float(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, 1,
						fft2.reUnitRootFloat, fft2.imUnitRootFloat));
					p += K1;
				}
			}
			break;
		}
		case SPLITRADIX: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k2 = 0; (k2 < K2); k2++) {
					executor2.execute(new DFTSplitRadixFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, 1,
						fft2.reUnitRootFloat, fft2.imUnitRootFloat));
					p += K1;
				}
			}
			break;
		}
	}
	try {
		executor2.shutdown();
		executor2.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	// Columns
	final FFTSetup fft3 = FFTSetup.transforms.get(height);
	final ExecutorService executor3 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft3.algorithm) {
		case BRUTEFORCE: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTBruteForceFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1,
						fft3.reUnitRootFloat, fft3.imUnitRootFloat));
				}
				p += K1K2;
			}
			break;
		}
		case COPRIMEFACTOR: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTCoprimeFactorFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1,
						fft3.ruritanian, fft3.chinese, fft3.K1));
				}
				p += K1K2;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength2Float(
						reDataFloat, imDataFloat, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH3: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength3Float(
						reDataFloat, imDataFloat, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH4: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength4Float(
						reDataFloat, imDataFloat, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH5: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength5Float(
						reDataFloat, imDataFloat, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH6: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength6Float(
						reDataFloat, imDataFloat, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH8: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength8Float(
						reDataFloat, imDataFloat, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case MIXEDRADIX: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTMixedRadixFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1,
						fft3.reUnitRootFloat, fft3.imUnitRootFloat, fft3.K1));
				}
				p += K1K2;
			}
			break;
		}
		case PADDEDRADER: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTPaddedRaderFloat(
						reDataFloat, imDataFloat, p + k1, K1,
						fft3.reConvolverFloat, fft3.imConvolverFloat,
						fft3.modular, fft3.inverseModular));
				}
				p += K1K2;
			}
			break;
		}
		case RADER: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTRaderFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1,
						fft3.reConvolverFloat, fft3.imConvolverFloat,
						fft3.modular, fft3.inverseModular));
				}
				p += K1K2;
			}
			break;
		}
		case RADIX2: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTRadix2Float(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1,
						fft3.reUnitRootFloat, fft3.imUnitRootFloat));
				}
				p += K1K2;
			}
			break;
		}
		case SPLITRADIX: {
			int p = 0;
			for (int k3 = 0; (k3 < halfK3); k3++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTSplitRadixFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1,
						fft3.reUnitRootFloat, fft3.imUnitRootFloat));
				}
				p += K1K2;
			}
			break;
		}
	}
	try {
		executor3.shutdown();
		executor3.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	int progressive = K1K2;
	int regressive = dataLength - K1K2;
	while (progressive < regressive) {
		reDataFloat[regressive] = reDataFloat[progressive];
		imDataFloat[regressive] = -imDataFloat[progressive];
		progressive += K1K2;
		regressive -= K1K2;
	}
	int p = K1K2 + K1;
	int q = dataLength - K1;
	for (int k3 = halfK3; (k3 < K3); k3++) {
		progressive = p;
		regressive = q;
		for (int k2 = 1; (k2 < K2); k2++) {
			reDataFloat[regressive] = reDataFloat[progressive];
			imDataFloat[regressive] = -imDataFloat[progressive];
			progressive += K1;
			regressive -= K1;
		}
		p += K1K2;
		q -= K1K2;
	}
	p = K1K2 + 1;
	q = K1K2 * (K3 - 1) + K1 - 1;
	for (int k3 = halfK3; (k3 < K3); k3++) {
		progressive = p;
		regressive = q;
		for (int k1 = 1; (k1 < K1); k1++) {
			reDataFloat[regressive] = reDataFloat[progressive];
			imDataFloat[regressive] = -imDataFloat[progressive];
			progressive++;
			regressive--;
		}
		p += K1K2;
		q -= K1K2;
	}
	progressive = K1K2 + K1 + 1;
	regressive = dataLength - 1;
	for (int k3 = halfK3; (k3 < K3); k3++) {
		for (int k2 = 1; (k2 < K2); k2++) {
			for (int k1 = 1; (k1 < K1); k1++) {
				reDataFloat[regressive] = reDataFloat[progressive];
				imDataFloat[regressive] = -imDataFloat[progressive];
				progressive++;
				regressive--;
			}
			progressive++;
			regressive--;
		}
		progressive += K1;
		regressive -= K1;
	}
} /* end transformRealFloat3DAcrossFirst */

/*------------------------------------------------------------------*/
private void transformRealFloat3DColumnFirst (
) {
	final int K1 = width.intValue();
	final int K2 = height.intValue();
	final int K3 = depth.intValue();
	final int K1K2 = K1 * K2;
	final int k1K2 = K1K2 - K1;
	final int K1K3 = K1 * K3;
	final int halfK2 = (K2 >> 1) + 1;
	int k = -1;
	// First column for an odd (width * depth)
	if (1 == (K1K3 & 1)) {
		final FFTSetupReal fft = FFTSetupReal.transforms.get(height);
		switch (fft.algorithm) {
			case BRUTEFORCE: {
				new DFTBruteForceRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, K1,
					fft.reUnitRootFloat, fft.imUnitRootFloat).run();
				break;
			}
			case COPRIMEFACTOR: {
				new DFTCoprimeFactorRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, K1,
					fft.ruritanian, fft.chinese, fft.K1).run();
				break;
			}
			case DUOREAL: {
				throw(new IllegalStateException());
			}
			case EVENREAL: {
				new DFTEvenRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, K1,
					fft.reUnitRootFloat, fft.imUnitRootFloat).run();
				break;
			}
			case LENGTH1: {
				imDataFloat[0] = 0.0F;
				break;
			}
			case LENGTH2: {
				new DFTLength2RealFloat(reDataFloat, imDataFloat, 0, K1).run();
				break;
			}
			case LENGTH3: {
				new DFTLength3RealFloat(reDataFloat, imDataFloat, 0, K1).run();
				break;
			}
			case LENGTH4: {
				new DFTLength4RealFloat(reDataFloat, imDataFloat, 0, K1).run();
				break;
			}
			case LENGTH5: {
				new DFTLength5RealFloat(reDataFloat, imDataFloat, 0, K1).run();
				break;
			}
			case LENGTH6: {
				new DFTLength6RealFloat(reDataFloat, imDataFloat, 0, K1).run();
				break;
			}
			case LENGTH8: {
				new DFTLength8RealFloat(reDataFloat, imDataFloat, 0, K1).run();
				break;
			}
			case MIXEDRADIX: {
				new DFTMixedRadixRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, K1,
					fft.reUnitRootFloat, fft.imUnitRootFloat, fft.K1).run();
				break;
			}
			case PADDEDRADER: {
				new DFTPaddedRaderRealFloat(reDataFloat, imDataFloat, 0, K1,
					fft.reConvolverFloat, fft.imConvolverFloat,
					fft.modular, fft.inverseModular).run();
				break;
			}
			case RADER: {
				new DFTRaderRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, K1,
					fft.reConvolverFloat, fft.imConvolverFloat,
					fft.modular, fft.inverseModular).run();
				break;
			}
			case RADIX2: {
				new DFTRadix2RealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, K1,
					fft.reUnitRootEvenFloat, fft.imUnitRootEvenFloat,
					fft.reUnitRootOddFloat, fft.imUnitRootOddFloat).run();
				break;
			}
			case SPLITRADIX: {
				new DFTSplitRadixRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, K1,
					fft.reUnitRootFloat, fft.imUnitRootFloat).run();
				break;
			}
		}
		k = 0;
	}
	// Remaining columns
	final FFTSetupDuoReal fft1 = FFTSetupDuoReal.transforms.get(height);
	final ExecutorService executor1 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft1.algorithm) {
		case BRUTEFORCE: {
			while (++k < K1K3) {
				executor1.execute(new DFTBruteForceRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1K2 * (k / K1) + k, K1,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat));
			}
			break;
		}
		case COPRIMEFACTOR: {
			while (++k < K1K3) {
				executor1.execute(new DFTCoprimeFactorRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1K2 * (k / K1) + k, K1,
					fft1.ruritanian, fft1.chinese, fft1.K1));
			}
			break;
		}
		case DUOREAL: {
			while (++k < K1K3) {
				final int p = k1K2 * (k / K1) + k;
				k++;
				final int q = k1K2 * (k / K1) + k;
				executor1.execute(new DFTDuoRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, q, K1, K2));
			}
			break;
		}
		case EVENREAL: {
			while (++k < K1K3) {
				executor1.execute(new DFTEvenRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1K2 * (k / K1) + k, K1,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat));
			}
			break;
		}
		case LENGTH1: {
			while (++k < K1K3) {
				imDataFloat[k] = 0.0F;
			}
			break;
		}
		case LENGTH2: {
			while (++k < K1K3) {
				executor1.execute(new DFTLength2RealFloat(
					reDataFloat, imDataFloat, k1K2 * (k / K1) + k, K1));
			}
			break;
		}
		case LENGTH3: {
			while (++k < K1K3) {
				executor1.execute(new DFTLength3RealFloat(
					reDataFloat, imDataFloat, k1K2 * (k / K1) + k, K1));
			}
			break;
		}
		case LENGTH4: {
			while (++k < K1K3) {
				executor1.execute(new DFTLength4RealFloat(
					reDataFloat, imDataFloat, k1K2 * (k / K1) + k, K1));
			}
			break;
		}
		case LENGTH5: {
			while (++k < K1K3) {
				executor1.execute(new DFTLength5RealFloat(
					reDataFloat, imDataFloat, k1K2 * (k / K1) + k, K1));
			}
			break;
		}
		case LENGTH6: {
			while (++k < K1K3) {
				executor1.execute(new DFTLength6RealFloat(
					reDataFloat, imDataFloat, k1K2 * (k / K1) + k, K1));
			}
			break;
		}
		case LENGTH8: {
			while (++k < K1K3) {
				executor1.execute(new DFTLength8RealFloat(
					reDataFloat, imDataFloat, k1K2 * (k / K1) + k, K1));
			}
			break;
		}
		case MIXEDRADIX: {
			while (++k < K1K3) {
				executor1.execute(new DFTMixedRadixRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1K2 * (k / K1) + k, K1,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat, fft1.K1));
			}
			break;
		}
		case PADDEDRADER: {
			while (++k < K1K3) {
				executor1.execute(new DFTPaddedRaderRealFloat(
					reDataFloat, imDataFloat, k1K2 * (k / K1) + k, K1,
					fft1.reConvolverFloat, fft1.imConvolverFloat,
					fft1.modular, fft1.inverseModular));
			}
			break;
		}
		case RADER: {
			while (++k < K1K3) {
				executor1.execute(new DFTRaderRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1K2 * (k / K1) + k, K1,
					fft1.reConvolverFloat, fft1.imConvolverFloat,
					fft1.modular, fft1.inverseModular));
			}
			break;
		}
		case RADIX2: {
			while (++k < K1K3) {
				executor1.execute(new DFTRadix2RealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1K2 * (k / K1) + k, K1,
					fft1.reUnitRootEvenFloat, fft1.imUnitRootEvenFloat,
					fft1.reUnitRootOddFloat, fft1.imUnitRootOddFloat));
			}
			break;
		}
		case SPLITRADIX: {
			while (++k < K1K3) {
				executor1.execute(new DFTSplitRadixRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, k1K2 * (k / K1) + k, K1,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat));
			}
			break;
		}
	}
	try {
		executor1.shutdown();
		executor1.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	// Rows
	final FFTSetup fft2 = FFTSetup.transforms.get(width);
	final ExecutorService executor2 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	final int skippedRows = (K2 - halfK2) * K1;
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTBruteForceFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, 1,
						fft2.reUnitRootFloat, fft2.imUnitRootFloat));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case COPRIMEFACTOR: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTCoprimeFactorFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, 1,
						fft2.ruritanian, fft2.chinese, fft2.K1));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTLength2Float(
						reDataFloat, imDataFloat, p, 1));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case LENGTH3: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTLength3Float(
						reDataFloat, imDataFloat, p, 1));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case LENGTH4: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTLength4Float(
						reDataFloat, imDataFloat, p, 1));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case LENGTH5: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTLength5Float(
						reDataFloat, imDataFloat, p, 1));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case LENGTH6: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTLength6Float(
						reDataFloat, imDataFloat, p, 1));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case LENGTH8: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTLength8Float(
						reDataFloat, imDataFloat, p, 1));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case MIXEDRADIX: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTMixedRadixFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, 1,
						fft2.reUnitRootFloat, fft2.imUnitRootFloat,
						fft2.K1));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case PADDEDRADER: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTPaddedRaderFloat(
						reDataFloat, imDataFloat, p, 1,
						fft2.reConvolverFloat, fft2.imConvolverFloat,
						fft2.modular, fft2.inverseModular));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case RADER: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTRaderFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, 1,
						fft2.reConvolverFloat, fft2.imConvolverFloat,
						fft2.modular, fft2.inverseModular));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case RADIX2: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTRadix2Float(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, 1,
						fft2.reUnitRootFloat, fft2.imUnitRootFloat));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
		case SPLITRADIX: {
			int p = 0;
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k2 = 0; (k2 < halfK2); k2++) {
					executor2.execute(new DFTSplitRadixFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, 1,
						fft2.reUnitRootFloat, fft2.imUnitRootFloat));
					p += K1;
				}
				p += skippedRows;
			}
			break;
		}
	}
	try {
		executor2.shutdown();
		executor2.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	// Across
	final FFTSetup fft3 = FFTSetup.transforms.get(depth);
	final ExecutorService executor3 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft3.algorithm) {
		case BRUTEFORCE: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTBruteForceFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, K1K2,
						fft3.reUnitRootFloat, fft3.imUnitRootFloat));
					p++;
				}
			}
			break;
		}
		case COPRIMEFACTOR: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTCoprimeFactorFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, K1K2,
						fft3.ruritanian, fft3.chinese, fft3.K1));
					p++;
				}
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength2Float(
						reDataFloat, imDataFloat, p, K1K2));
					p++;
				}
			}
			break;
		}
		case LENGTH3: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength3Float(
						reDataFloat, imDataFloat, p, K1K2));
					p++;
				}
			}
			break;
		}
		case LENGTH4: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength4Float(
						reDataFloat, imDataFloat, p, K1K2));
					p++;
				}
			}
			break;
		}
		case LENGTH5: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength5Float(
						reDataFloat, imDataFloat, p, K1K2));
					p++;
				}
			}
			break;
		}
		case LENGTH6: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength6Float(
						reDataFloat, imDataFloat, p, K1K2));
					p++;
				}
			}
			break;
		}
		case LENGTH8: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTLength8Float(
						reDataFloat, imDataFloat, p, K1K2));
					p++;
				}
			}
			break;
		}
		case MIXEDRADIX: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTMixedRadixFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, K1K2,
						fft3.reUnitRootFloat, fft3.imUnitRootFloat, fft3.K1));
					p++;
				}
			}
			break;
		}
		case PADDEDRADER: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTPaddedRaderFloat(
						reDataFloat, imDataFloat, p, K1K2,
						fft3.reConvolverFloat, fft3.imConvolverFloat,
						fft3.modular, fft3.inverseModular));
					p++;
				}
			}
			break;
		}
		case RADER: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTRaderFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, K1K2,
						fft3.reConvolverFloat, fft3.imConvolverFloat,
						fft3.modular, fft3.inverseModular));
					p++;
				}
			}
			break;
		}
		case RADIX2: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTRadix2Float(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, K1K2,
						fft3.reUnitRootFloat, fft3.imUnitRootFloat));
					p++;
				}
			}
			break;
		}
		case SPLITRADIX: {
			int p = 0;
			for (int k2 = 0; (k2 < halfK2); k2++) {
				for (int k1 = 0; (k1 < K1); k1++) {
					executor3.execute(new DFTSplitRadixFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p, K1K2,
						fft3.reUnitRootFloat, fft3.imUnitRootFloat));
					p++;
				}
			}
			break;
		}
	}
	try {
		executor3.shutdown();
		executor3.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	int progressive = K1;
	int regressive = K1K2 - K1;
	while (progressive < regressive) {
		reDataFloat[regressive] = reDataFloat[progressive];
		imDataFloat[regressive] = -imDataFloat[progressive];
		progressive += K1;
		regressive -= K1;
	}
	int p = K1 + 1;
	int q = K1K2 - 1;
	for (int k2 = halfK2; (k2 < K2); k2++) {
		progressive = p;
		regressive = q;
		for (int k1 = 1; (k1 < K1); k1++) {
			reDataFloat[regressive] = reDataFloat[progressive];
			imDataFloat[regressive] = -imDataFloat[progressive];
			progressive++;
			regressive--;
		}
		p += K1;
		q -= K1;
	}
	p = K1K2 + K1;
	q = dataLength - K1;
	for (int k3 = 1; (k3 < K3); k3++) {
		progressive = p;
		regressive = q;
		for (int k2 = halfK2; (k2 < K2); k2++) {
			reDataFloat[regressive] = reDataFloat[progressive];
			imDataFloat[regressive] = -imDataFloat[progressive];
			progressive += K1;
			regressive -= K1;
		}
		p += K1K2;
		q -= K1K2;
	}
	progressive = K1K2 + K1 + 1;
	regressive = dataLength - 1;
	for (int k3 = 1; (k3 < K3); k3++) {
		for (int k2 = halfK2; (k2 < K2); k2++) {
			for (int k1 = 1; (k1 < K1); k1++) {
				reDataFloat[regressive] = reDataFloat[progressive];
				imDataFloat[regressive] = -imDataFloat[progressive];
				progressive++;
				regressive--;
			}
			progressive++;
			regressive--;
		}
		progressive += halfK2 * K1;
		regressive -= halfK2 * K1;
	}
} /* end transformRealFloat3DColumnFirst */

/*------------------------------------------------------------------*/
private void transformRealFloat3DRowFirst (
) {
	final int K1 = width.intValue();
	final int K2 = height.intValue();
	final int K3 = depth.intValue();
	final int K1K2 = K1 * K2;
	final int K2K3 = K2 * K3;
	final int halfK1 = (K1 >> 1) + 1;
	int k = 0;
	int p = 0;
	// First row for an odd (height * depth)
	if (1 == (K2K3 & 1)) {
		final FFTSetupReal fft = FFTSetupReal.transforms.get(width);
		switch (fft.algorithm) {
			case BRUTEFORCE: {
				new DFTBruteForceRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, 1,
					fft.reUnitRootFloat, fft.imUnitRootFloat).run();
				break;
			}
			case COPRIMEFACTOR: {
				new DFTCoprimeFactorRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, 1,
					fft.ruritanian, fft.chinese, fft.K1).run();
				break;
			}
			case DUOREAL: {
				throw(new IllegalStateException());
			}
			case EVENREAL: {
				new DFTEvenRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, 1,
					fft.reUnitRootFloat, fft.imUnitRootFloat).run();
				break;
			}
			case LENGTH1: {
				imDataFloat[0] = 0.0F;
				break;
			}
			case LENGTH2: {
				new DFTLength2RealFloat(reDataFloat, imDataFloat, 0, 1).run();
				break;
			}
			case LENGTH3: {
				new DFTLength3RealFloat(reDataFloat, imDataFloat, 0, 1).run();
				break;
			}
			case LENGTH4: {
				new DFTLength4RealFloat(reDataFloat, imDataFloat, 0, 1).run();
				break;
			}
			case LENGTH5: {
				new DFTLength5RealFloat(reDataFloat, imDataFloat, 0, 1).run();
				break;
			}
			case LENGTH6: {
				new DFTLength6RealFloat(reDataFloat, imDataFloat, 0, 1).run();
				break;
			}
			case LENGTH8: {
				new DFTLength8RealFloat(reDataFloat, imDataFloat, 0, 1).run();
				break;
			}
			case MIXEDRADIX: {
				new DFTMixedRadixRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, 1,
					fft.reUnitRootFloat, fft.imUnitRootFloat, fft.K1).run();
				break;
			}
			case PADDEDRADER: {
				new DFTPaddedRaderRealFloat(reDataFloat, imDataFloat, 0, 1,
					fft.reConvolverFloat, fft.imConvolverFloat,
					fft.modular, fft.inverseModular).run();
				break;
			}
			case RADER: {
				new DFTRaderRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, 1,
					fft.reConvolverFloat, fft.imConvolverFloat,
					fft.modular, fft.inverseModular).run();
				break;
			}
			case RADIX2: {
				new DFTRadix2RealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, 1,
					fft.reUnitRootEvenFloat, fft.imUnitRootEvenFloat,
					fft.reUnitRootOddFloat, fft.imUnitRootOddFloat).run();
				break;
			}
			case SPLITRADIX: {
				new DFTSplitRadixRealFloat(reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, 0, 1,
					fft.reUnitRootFloat, fft.imUnitRootFloat).run();
				break;
			}
		}
		k = 1;
		p = K1;
	}
	// Remaining rows
	final FFTSetupDuoReal fft1 = FFTSetupDuoReal.transforms.get(width);
	final ExecutorService executor1 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	switch (fft1.algorithm) {
		case BRUTEFORCE: {
			while (k++ < K2K3) {
				executor1.execute(new DFTBruteForceRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat));
				p += K1;
			}
			break;
		}
		case COPRIMEFACTOR: {
			while (k++ < K2K3) {
				executor1.execute(new DFTCoprimeFactorRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft1.ruritanian, fft1.chinese, fft1.K1));
				p += K1;
			}
			break;
		}
		case DUOREAL: {
			while (k++ < K2K3) {
				executor1.execute(new DFTDuoRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, p + K1, 1, K1));
				p += 2 * K1;
				k++;
			}
			break;
		}
		case EVENREAL: {
			while (k++ < K2K3) {
				executor1.execute(new DFTEvenRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat));
				p += K1;
			}
			break;
		}
		case LENGTH1: {
			while (k++ < K2K3) {
				imDataFloat[p] = 0.0F;
				p += K1;
			}
			break;
		}
		case LENGTH2: {
			while (k++ < K2K3) {
				executor1.execute(new DFTLength2RealFloat(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH3: {
			while (k++ < K2K3) {
				executor1.execute(new DFTLength3RealFloat(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH4: {
			while (k++ < K2K3) {
				executor1.execute(new DFTLength4RealFloat(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH5: {
			while (k++ < K2K3) {
				executor1.execute(new DFTLength5RealFloat(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH6: {
			while (k++ < K2K3) {
				executor1.execute(new DFTLength6RealFloat(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case LENGTH8: {
			while (k++ < K2K3) {
				executor1.execute(new DFTLength8RealFloat(
					reDataFloat, imDataFloat, p, 1));
				p += K1;
			}
			break;
		}
		case MIXEDRADIX: {
			while (k++ < K2K3) {
				executor1.execute(new DFTMixedRadixRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat, fft1.K1));
				p += K1;
			}
			break;
		}
		case PADDEDRADER: {
			while (k++ < K2K3) {
				executor1.execute(new DFTPaddedRaderRealFloat(
					reDataFloat, imDataFloat, p, 1,
					fft1.reConvolverFloat, fft1.imConvolverFloat,
					fft1.modular, fft1.inverseModular));
				p += K1;
			}
			break;
		}
		case RADER: {
			while (k++ < K2K3) {
				executor1.execute(new DFTRaderRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft1.reConvolverFloat, fft1.imConvolverFloat,
					fft1.modular, fft1.inverseModular));
				p += K1;
			}
			break;
		}
		case RADIX2: {
			while (k++ < K2K3) {
				executor1.execute(new DFTRadix2RealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft1.reUnitRootEvenFloat, fft1.imUnitRootEvenFloat,
					fft1.reUnitRootOddFloat, fft1.imUnitRootOddFloat));
				p += K1;
			}
			break;
		}
		case SPLITRADIX: {
			while (k++ < K2K3) {
				executor1.execute(new DFTSplitRadixRealFloat(
					reDataFloat, imDataFloat,
					reBufferFloat, imBufferFloat, p, 1,
					fft1.reUnitRootFloat, fft1.imUnitRootFloat));
				p += K1;
			}
			break;
		}
	}
	try {
		executor1.shutdown();
		executor1.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	// Columns
	final FFTSetup fft2 = FFTSetup.transforms.get(height);
	final ExecutorService executor2 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	p = 0;
	switch (fft2.algorithm) {
		case BRUTEFORCE: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTBruteForceFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1,
						fft2.reUnitRootFloat, fft2.imUnitRootFloat));
				}
				p += K1K2;
			}
			break;
		}
		case COPRIMEFACTOR: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTCoprimeFactorFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1,
						fft2.ruritanian, fft2.chinese, fft2.K1));
				}
				p += K1K2;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTLength2Float(
						reDataFloat, imDataFloat, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH3: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTLength3Float(
						reDataFloat, imDataFloat, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH4: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTLength4Float(
						reDataFloat, imDataFloat, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH5: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTLength5Float(
						reDataFloat, imDataFloat, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH6: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTLength6Float(
						reDataFloat, imDataFloat, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case LENGTH8: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTLength8Float(
						reDataFloat, imDataFloat, p + k1, K1));
				}
				p += K1K2;
			}
			break;
		}
		case MIXEDRADIX: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTMixedRadixFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1,
						fft2.reUnitRootFloat, fft2.imUnitRootFloat, fft2.K1));
				}
				p += K1K2;
			}
			break;
		}
		case PADDEDRADER: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTPaddedRaderFloat(
						reDataFloat, imDataFloat, p + k1, K1,
						fft2.reConvolverFloat, fft2.imConvolverFloat,
						fft2.modular, fft2.inverseModular));
				}
				p += K1K2;
			}
			break;
		}
		case RADER: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTRaderFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1,
						fft2.reConvolverFloat, fft2.imConvolverFloat,
						fft2.modular, fft2.inverseModular));
				}
				p += K1K2;
			}
			break;
		}
		case RADIX2: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTRadix2Float(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1,
						fft2.reUnitRootFloat, fft2.imUnitRootFloat));
				}
				p += K1K2;
			}
			break;
		}
		case SPLITRADIX: {
			for (int k3 = 0; (k3 < K3); k3++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor2.execute(new DFTSplitRadixFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1,
						fft2.reUnitRootFloat, fft2.imUnitRootFloat));
				}
				p += K1K2;
			}
			break;
		}
	}
	try {
		executor2.shutdown();
		executor2.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	// Across
	final FFTSetup fft3 = FFTSetup.transforms.get(depth);
	final ExecutorService executor3 =
		(PARALLELPROCESSING) ? (Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors()))
		: (Executors.newSingleThreadExecutor());
	p = 0;
	switch (fft3.algorithm) {
		case BRUTEFORCE: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTBruteForceFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1K2,
						fft3.reUnitRootFloat, fft3.imUnitRootFloat));
				}
				p += K1;
			}
			break;
		}
		case COPRIMEFACTOR: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTCoprimeFactorFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1K2,
						fft3.ruritanian, fft3.chinese, fft3.K1));
				}
				p += K1;
			}
			break;
		}
		case DUOREAL: {
			throw(new IllegalStateException());
		}
		case EVENREAL: {
			throw(new IllegalStateException());
		}
		case LENGTH1: {
			break;
		}
		case LENGTH2: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTLength2Float(
						reDataFloat, imDataFloat, p + k1, K1K2));
				}
				p += K1;
			}
			break;
		}
		case LENGTH3: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTLength3Float(
						reDataFloat, imDataFloat, p + k1, K1K2));
				}
				p += K1;
			}
			break;
		}
		case LENGTH4: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTLength4Float(
						reDataFloat, imDataFloat, p + k1, K1K2));
				}
				p += K1;
			}
			break;
		}
		case LENGTH5: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTLength5Float(
						reDataFloat, imDataFloat, p + k1, K1K2));
				}
				p += K1;
			}
			break;
		}
		case LENGTH6: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTLength6Float(
						reDataFloat, imDataFloat, p + k1, K1K2));
				}
				p += K1;
			}
			break;
		}
		case LENGTH8: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTLength8Float(
						reDataFloat, imDataFloat, p + k1, K1K2));
				}
				p += K1;
			}
			break;
		}
		case MIXEDRADIX: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTMixedRadixFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1K2,
						fft3.reUnitRootFloat, fft3.imUnitRootFloat, fft3.K1));
				}
				p += K1;
			}
			break;
		}
		case PADDEDRADER: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTPaddedRaderFloat(
						reDataFloat, imDataFloat, p + k1, K1K2,
						fft3.reConvolverFloat, fft3.imConvolverFloat,
						fft3.modular, fft3.inverseModular));
				}
				p += K1;
			}
			break;
		}
		case RADER: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTRaderFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1K2,
						fft3.reConvolverFloat, fft3.imConvolverFloat,
						fft3.modular, fft3.inverseModular));
				}
				p += K1;
			}
			break;
		}
		case RADIX2: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTRadix2Float(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1K2,
						fft3.reUnitRootFloat, fft3.imUnitRootFloat));
				}
				p += K1;
			}
			break;
		}
		case SPLITRADIX: {
			for (int k2 = 0; (k2 < K2); k2++) {
				for (int k1 = 0; (k1 < halfK1); k1++) {
					executor3.execute(new DFTSplitRadixFloat(
						reDataFloat, imDataFloat,
						reBufferFloat, imBufferFloat, p + k1, K1K2,
						fft3.reUnitRootFloat, fft3.imUnitRootFloat));
				}
				p += K1;
			}
			break;
		}
	}
	try {
		executor3.shutdown();
		executor3.awaitTermination((long)(Integer.MAX_VALUE), TimeUnit.DAYS);
	}
	catch (InterruptedException ignored) {
	}
	int progressive = 0;
	int regressive = K1;
	while (++progressive < --regressive) {
		reDataFloat[regressive] = reDataFloat[progressive];
		imDataFloat[regressive] = -imDataFloat[progressive];
	}
	p = K1 + 1;
	int q = K1K2 - 1;
	for (int k2 = 1; (k2 < K2); k2++) {
		progressive = p;
		regressive = q;
		for (int k1 = halfK1; (k1 < K1); k1++) {
			reDataFloat[regressive] = reDataFloat[progressive];
			imDataFloat[regressive] = -imDataFloat[progressive];
			progressive++;
			regressive--;
		}
		p += K1;
		q -= K1;
	}
	p = K1K2 + 1;
	q = K1K2 * (K3 - 1) + K1 - 1;
	for (int k3 = 1; (k3 < K3); k3++) {
		progressive = p;
		regressive = q;
		for (int k1 = halfK1; (k1 < K1); k1++) {
			reDataFloat[regressive] = reDataFloat[progressive];
			imDataFloat[regressive] = -imDataFloat[progressive];
			progressive++;
			regressive--;
		}
		p += K1K2;
		q -= K1K2;
	}
	progressive = K1K2 + K1 + 1;
	regressive = dataLength - 1;
	for (int k3 = 1; (k3 < K3); k3++) {
		for (int k2 = 1; (k2 < K2); k2++) {
			for (int k1 = halfK1; (k1 < K1); k1++) {
				reDataFloat[regressive] = reDataFloat[progressive];
				imDataFloat[regressive] = -imDataFloat[progressive];
				progressive++;
				regressive--;
			}
			progressive += halfK1;
			regressive -= halfK1;
		}
		progressive += K1;
		regressive -= K1;
	}
} /* end transformRealFloat3DRowFirst */

} /* end class AcademicFFT */
