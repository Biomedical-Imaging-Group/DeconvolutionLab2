package jfftw.complex.nd;

/**
 * Corresponds to an FFTW <code>fftwnd_plan</code> (plan for complex
 * multi-dimensional transforms) structure.
 * 
 * A plan is a choice of algorithm to optimally compute transforms for a given
 * size of data. Once you have a <code>Plan</code> you can use it to compute a
 * number of transforms for data of the same size. Creating a plan results in
 * collection of much information, that will be reused at succeding creation of
 * plans.
 * 
 * <p>
 * N-dimensional data is thought of by FFTW as a continuous line of M units of
 * data, where M is the product of the N dimensions. Unfortunately
 * multi-dimensional arrays in Java are done the way discussed in the FFTW
 * documentation in the topic <b>'Dynamic Arrays in C--The Wrong Way'</b>.
 * <p>
 * The suggested way is to pass a single one dimensional array to
 * {@link #transform(float[])}, as described in the FFTW documentation.
 * <p>
 * We represent a complex array of length N as a real (<code>float</code>) array
 * of length 2*N, where the <i>(2*i)</i>th element (of the real array)
 * corresponds to the real part of the <i>i</i>th element (of the complex array)
 * and the <i>(2*i+1)</i>th element (of the real array) corresponds to the
 * imaginary part of the <i>i</i>th element (of the complex array).
 * <p>
 * For more information see the <a href="http://www.fftw.org">FFTW</a>
 * documentation. The FFTW library is work of MIT. The jfftw package is work of
 * Daniel Darabos
 * 
 * (<a href="mailto:cyhawk@sch.bme.hu">cyhawk@sch.bme.hu</a>).
 * 
 * Both have GPL licenses.
 */
public class Plan implements jfftw.Plan {
	/**
	 * I store <code>fftwnd_plan</code> (the pointer) in this byte array. It is
	 * ugly, but I don't know the proper way.
	 */
	private byte plan[];

	/**
	 * Constructs a <code>Plan</code> for the given dimensions <code>dim</code>,
	 * direction <code>dir</code>, initialization <code>flags</code> and actual
	 * input/output arrays. The contents of the arrays are not used (they can be
	 * uninitialized) but will be destroyed.
	 * 
	 * <p>
	 * (See FFTW documentation on <code>fftwnd_create_plan_specific</code> for
	 * more insight.)
	 * 
	 * @param dim
	 *            Array of dimensions. They are expected to be in the order
	 *            described in the FFTW documentation (row-major, or C-style).
	 * @param dir
	 *            One of {@link #FORWARD} and {@link #BACKWARD}.
	 * @param flags
	 *            One of {@link #ESTIMATE} (default) and {@link #MEASURE}
	 *            optionally <code>OR</code>'ed with {@link #USE_WISDOM},
	 *            {@link #IN_PLACE} and {@link #READONLY}. Any other flag
	 *            constants from <code>fftw.h</code> might be working, but jfftw
	 *            has not been tested with them.
	 * @throws RuntimeException
	 *             if the float type of Java is not of the same size as
	 *             <code>fftw_real</code>. (If this is the case, the library
	 *             will need to be modified and recompiled. If FFTW was not
	 *             compiled with
	 * 
	 *             <code>FFTW_ENABLE_FLOAT</code> all float types should be
	 *             changed to double in the Java and native source -- or
	 *             recompile FFTW with <code>FFTW_ENABLE_FLOAT</code>.)
	 */
	public Plan(int[] dim, int dir, int flags, float[] in, int idist, float[] out, int odist) {
		if (dir != FORWARD && dir != BACKWARD)
			throw new IllegalArgumentException("dir must be either FORWARD or BACKWARD");
		createPlanSpecific(dim, dir, flags, in, idist, out, odist);
	}

	/**
	 * Constructs a <code>Plan</code> for the given dimensions <code>dim</code>,
	 * direction <code>dir</code> and initialization <code>flags</code>.
	 * 
	 * @param dim
	 *            Array of dimensions. They are expected to be in the order
	 *            described in the FFTW documentation (row-major, or C-style).
	 * @param dir
	 *            One of {@link #FORWARD} and {@link #BACKWARD}.
	 * @param flags
	 *            One of {@link #ESTIMATE} (default) and {@link #MEASURE}
	 *            optionally <code>OR</code>'ed with {@link #USE_WISDOM},
	 *            {@link #IN_PLACE} and {@link #READONLY}. Any other flag
	 *            constants from <code>fftw.h</code> might be working, but jfftw
	 *            has not been tested with them.
	 * @throws RuntimeException
	 *             if the float type of Java is not of the same size as
	 *             <code>fftw_real</code>. (If this is the case, the library
	 *             will need to be modified and recompiled. If FFTW was not
	 *             compiled with
	 * 
	 *             <code>FFTW_ENABLE_FLOAT</code> all float types should be
	 *             changed to double in the Java and native source -- or
	 *             recompile FFTW with <code>FFTW_ENABLE_FLOAT</code>.)
	 */
	public Plan(int[] dim, int dir, int flags) {
		if (dir != FORWARD && dir != BACKWARD)
			throw new IllegalArgumentException("dir must be either FORWARD or BACKWARD");
		createPlan(dim, dir, flags);
	}

	/**
	 * Constructs a <code>Plan</code> for the given dimensions <code>dim</code>
	 * and direction <code>dir</code> based on estimations (see
	 * <code>ESTIMATE</code>). Same as
	 * 
	 * <code>Plan( dim, dir, Plan.ESTIMATE )</code>.
	 * 
	 * @param dir
	 *            One of {@link #FORWARD} and {@link #BACKWARD}.
	 * @throws RuntimeException
	 *             if the float type of Java is not of the same size as
	 *             <code>fftw_real</code>. (If this is the case, the library
	 *             will need to be modified and recompiled. If FFTW was not
	 *             compiled with
	 * 
	 *             <code>FFTW_ENABLE_FLOAT</code> all float types should be
	 *             changed to double in the Java and native source -- or
	 *             recompile FFTW with <code>FFTW_ENABLE_FLOAT</code>.)
	 */
	public Plan(int[] dim, int dir) {
		if (dir != FORWARD && dir != BACKWARD)
			throw new IllegalArgumentException("dir must be either FORWARD or BACKWARD");
		createPlan(dim, dir, ESTIMATE);
	}

	/**
	 * Constructs a forward (see <code>FORWARD</code>)
	 * 
	 * <code>Plan</code> for the given dimensions <code>dim</code> based on
	 * 
	 * estimations (see <code>ESTIMATE</code>). Same as
	 * 
	 * <code>Plan( dim, Plan.FORWARD, Plan.ESTIMATE )</code>.
	 * 
	 * @throws RuntimeException
	 *             if the float type of Java is not of the same size as
	 *             <code>fftw_real</code>. (If this is the case, the library
	 *             will need to be modified and recompiled. If FFTW was not
	 *             compiled with
	 * 
	 *             <code>FFTW_ENABLE_FLOAT</code> all float types should be
	 *             changed to double in the Java and native source -- or
	 *             recompile FFTW with <code>FFTW_ENABLE_FLOAT</code>.)
	 */
	public Plan(int[] dim) {
		createPlan(dim, FORWARD, ESTIMATE);
	}

	/**
	 * Constructs a <code>Plan</code> for the given <code>width</code> and
	 * <code>height</code> (two dimensional) and direction <code>dir</code>
	 * based on estimations (see <code>ESTIMATE</code>).
	 * 
	 * @param dir
	 *            One of {@link #FORWARD} and {@link #BACKWARD}.
	 * @throws RuntimeException
	 *             if the float type of Java is not of the same size as
	 *             <code>fftw_real</code>. (If this is the case, the library
	 *             will need to be modified and recompiled. If FFTW was not
	 *             compiled with
	 * 
	 *             <code>FFTW_ENABLE_FLOAT</code> all float types should be
	 *             changed to double in the Java and native source -- or
	 *             recompile FFTW with <code>FFTW_ENABLE_FLOAT</code>.)
	 */
	public Plan(int width, int height, int dir) {
		if (dir != FORWARD && dir != BACKWARD)
			throw new IllegalArgumentException("dir must be either FORWARD or BACKWARD");
		int[] dim = new int[2];
		dim[0] = width;
		dim[1] = height;
		createPlan(dim, dir, ESTIMATE);
	}

	/**
	 * Constructs a <code>Plan</code> for the given <code>width</code>,
	 * <code>height</code> and <code>depth</code> (three dimensional) and
	 * direction <code>dir</code> based on estimations (see
	 * <code>ESTIMATE</code>).
	 * 
	 * @param dir
	 *            One of {@link #FORWARD} and {@link #BACKWARD}.
	 * @throws RuntimeException
	 *             if the float type of Java is not of the same size as
	 *             <code>fftw_real</code>. (If this is the case, the library
	 *             will need to be modified and recompiled. If FFTW was not
	 *             compiled with
	 * 
	 *             <code>FFTW_ENABLE_FLOAT</code> all float types should be
	 *             changed to double in the Java and native source -- or
	 *             recompile FFTW with <code>FFTW_ENABLE_FLOAT</code>.)
	 */
	public Plan(int width, int height, int depth, int dir) {
		if (dir != FORWARD && dir != BACKWARD)
			throw new IllegalArgumentException("dir must be either FORWARD or BACKWARD");
		int[] dim = new int[3];
		dim[0] = width;
		dim[1] = height;
		dim[2] = depth;
		createPlan(dim, dir, ESTIMATE);
	}

	/**
	 * Destroys the <code>fftwnd_plan</code> structure.
	 */
	protected void finalize() {
		destroyPlan();
	}

	private native void createPlan(int[] dim, int dir, int flags);

	private native void createPlanSpecific(int[] dim, int dir, int flags, float[] in, int idist, float[] out, int odist);

	private native void destroyPlan();

	/**
	 * Calculates the Fourier transform of <code>in</code>.
	 * 
	 * <p>
	 * (See FFTW documentation for details. The function behind it is
	 * <code>fftwnd_one</code>.)
	 * 
	 * @param in
	 *            complex multi-dimensional array.
	 * @return the resulting array of complex numbers which is of the same size
	 *         as <code>in</code>.
	 * @throws IndexOutOfBoundsException
	 *             if <code>in</code> is not float the size that was passed to
	 *             the constructor.
	 */
	public native float[] transform(float[] in);

	/**
	 * Calculates multiple Fourier transforms.
	 * 
	 * <p>
	 * (See FFTW documentation for details. The function behind it is
	 * <code>fftwnd</code>, the arguments are mapped to <code>fftwnd</code>'s
	 * arguments of the same name.)
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if <code>in</code> or <code>out</code>
	 * 
	 *             are not of the proper size.
	 */
	public native void transform(int howmany, float[] in, int istride, int idist, float[] out, int ostride, int odist);

	static {
		// System.loadLibrary("FFTWJNI");
		// FFTWLoader.run();
	}
}
