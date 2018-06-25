package jfftw.real;

/**
 * Corresponds to an FFTW <code>rfftw_plan</code> (plan for real one dimensional
 * transforms) structure.
 * 
 * A plan is a choice of algorithm to optimally compute transforms for a given
 * length of data. Once you have a <code>Plan</code> you can use it to compute a
 * number of transforms for data of the same size. Creating a plan results in
 * collection of much information, that will be reused at succeding creation of
 * plans.
 * 
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
	 * I store <code>rfftw_plan</code> (the pointer) in this byte array. It is
	 * ugly, but I don't know the proper way.
	 */
	private byte plan[];

	/**
	 * Constructs a <code>Plan</code> for the given length
	 * 
	 * <code>n</code>, direction <code>dir</code>, initialization
	 * <code>flags</code> and actual input/output arrays. The contents of the
	 * arrays are not used (they can be uninitialized) but will be destroyed.
	 * 
	 * <p>
	 * (See FFTW documentation on <code>rfftw_create_plan_specific</code> for
	 * more insight.)
	 * 
	 * @param dir
	 *            One of {@link #REAL_TO_COMPLEX} and {@link #COMPLEX_TO_REAL}.
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
	public Plan(int n, int dir, int flags, float[] in, int idist, float[] out, int odist) {
		if (dir != REAL_TO_COMPLEX && dir != COMPLEX_TO_REAL)
			throw new IllegalArgumentException("dir must be either REAL_TO_COMPLEX or COMPLEX_TO_REAL");
		createPlanSpecific(n, dir, flags, in, idist, out, odist);
	}

	/**
	 * Constructs a <code>Plan</code> for the given length
	 * 
	 * <code>n</code>, direction <code>dir</code> and initialization
	 * <code>flags</code>.
	 * 
	 * @param dir
	 *            One of {@link #REAL_TO_COMPLEX} and {@link #COMPLEX_TO_REAL}.
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
	public Plan(int n, int dir, int flags) {
		if (dir != REAL_TO_COMPLEX && dir != COMPLEX_TO_REAL)
			throw new IllegalArgumentException("dir must be either REAL_TO_COMPLEX or COMPLEX_TO_REAL");
		createPlan(n, dir, flags);
	}

	/**
	 * Constructs a <code>Plan</code> for the given length
	 * 
	 * <code>n</code> and direction <code>dir</code> based on estimations (see
	 * <code>ESTIMATE</code>). Same as
	 * 
	 * <code>Plan( n, dir, Plan.ESTIMATE )</code>.
	 * 
	 * @param dir
	 *            One of {@link #REAL_TO_COMPLEX} and {@link #COMPLEX_TO_REAL}.
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
	public Plan(int n, int dir) {
		if (dir != REAL_TO_COMPLEX && dir != COMPLEX_TO_REAL)
			throw new IllegalArgumentException("dir must be either REAL_TO_COMPLEX or COMPLEX_TO_REAL");
		createPlan(n, dir, ESTIMATE);
	}

	/**
	 * Constructs a real-to-complex (see <code>REAL_TO_COMPLEX</code>)
	 * 
	 * <code>Plan</code> for the given length <code>n</code> based on
	 * 
	 * estimations (see <code>ESTIMATE</code>). Same as
	 * 
	 * <code>Plan( n, Plan.REAL_TO_COMPLEX, Plan.ESTIMATE )</code>.
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
	public Plan(int n) {
		createPlan(n, REAL_TO_COMPLEX, ESTIMATE);
	}

	/**
	 * Destroys the <code>rfftw_plan</code> structure.
	 */
	protected void finalize() {
		destroyPlan();
	}

	private native void createPlan(int n, int dir, int flags);

	private native void createPlanSpecific(int n, int dir, int flags, float[] in, int idist, float[] out, int odist);

	private native void destroyPlan();

	/**
	 * Calculates the Fourier transform of <code>in</code>.
	 * 
	 * <p>
	 * (See FFTW documentation for details. The function behind it is
	 * <code>rfftw_one</code>.)
	 * 
	 * @return the resulting array which is of the same size as <code>in</code>.
	 * @throws IndexOutOfBoundsException
	 *             if <code>in</code> is not of the size that was passed to the
	 *             constructor.
	 * 
	 */
	public native float[] transform(float[] in);

	/**
	 * Calculates multiple Fourier transforms.
	 * 
	 * <p>
	 * (See FFTW documentation for details. The function behind it is
	 * <code>rfftw</code>, the arguments are mapped to <code>rfftw</code>'s
	 * arguments of the same name.)
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if <code>in</code> or <code>out</code> are not of the proper
	 *             size.
	 */
	public native void transform(int howmany, float[] in, int istride, int idist, float[] out, int ostride, int odist);

	static {
		// System.loadLibrary("FFTWJNI");
		// FFTWLoader.run();
	}
}
