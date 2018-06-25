package jfftw;

/**
 * Common interface for the Plan classes. See the documentation of the
 * individual classes for documentation of their workings and arguments.
 * <p>
 * <b>Note:</b> The constructors of the implementing classes synchronize on this
 * interface so that no two <code>..._create_plan</code> functions are called at
 * the same time.
 * 
 * @see jfftw.complex.Plan
 * @see jfftw.complex.nd.Plan
 * @see jfftw.real.Plan
 * @see jfftw.real.nd.Plan
 */
public interface Plan {
	/**
	 * Flag requesting plan choice based on estimations. This is the quick way.
	 * 
	 * <p>
	 * (The constant must have the same value as <code>FFTW_ESTIMATE</code> in
	 * <code>fftw.h</code>.)
	 */
	public static final int	ESTIMATE		= 0;
	/**
	 * Flag requesting plan choice based on measurements. Using this option FFTW
	 * actually tries all possible algorithms and chooses the fastest. This way
	 * all speed factors are taken into account, but measurements take a long
	 * time.
	 * 
	 * <p>
	 * (The constant must have the same value as <code>FFTW_MEASURE</code> in
	 * <code>fftw.h</code>.)
	 */
	public static final int	MEASURE			= 1;
	/**
	 * Flag requesting plan for in-place transforms. {@link #transform(float[])}
	 * returns its argument when this flag is provided.
	 * <p>
	 * (The constant must have the same value as <code>FFTW_IN_PLACE</code> in
	 * <code>fftw.h</code>.)
	 */
	public static final int	IN_PLACE		= 8;
	/**
	 * Flag requesting storage of gathered data.
	 * <p>
	 * (The constant must have the same value as <code>FFTW_USE_WISDOM</code> in
	 * <code>fftw.h</code>.)
	 * 
	 * @see jfftw.WisdomJFFTW
	 */
	public static final int	USE_WISDOM		= 16;
	/**
	 * Flag requesting a read-only plan. If this flag is given the plan can be
	 * used to do multiple transforms at the same time. If it is not given and
	 * the function is called from two threads at the same time, one of them
	 * will be blocked until the other transform completes.
	 * <p>
	 * (The constant must have the same value as <code>FFTW_THREADSAFE</code> in
	 * <code>fftw.h</code>.)
	 */
	public static final int	READONLY		= 128;
	/**
	 * Prepare a plan for real to half-complex transforms.
	 * 
	 * <p>
	 * (The constant must have the same value as
	 * <code>FFTW_REAL_TO_COMPLEX</code> in <code>rfftw.h</code>.)
	 */
	public static final int	REAL_TO_COMPLEX	= -1;
	/**
	 * Prepare a plan for half-complex to real transforms.
	 * 
	 * <p>
	 * (Input values for compex-to-real transforms are overwritten with scratch
	 * values. Not for real-to-complex transforms.)
	 * 
	 * <p>
	 * (The constant must have the same value as
	 * <code>FFTW_COMPLEX_TO_REAL</code> in <code>rfftw.h</code>.)
	 */
	public static final int	COMPLEX_TO_REAL	= 1;
	/**
	 * Prepare a plan for forward transforms. (See the FFTW documentation to
	 * learn what 'forward' means.)
	 * <p>
	 * (The constant must have the same value as <code>FFTW_FORWARD</code> in
	 * <code>fftw.h</code>.)
	 */
	public static final int	FORWARD			= -1;
	/**
	 * Prepare a plan for backward transforms. (See the FFTW documentation to
	 * learn what 'backward' means.)
	 * <p>
	 * (The constant must have the same value as <code>FFTW_BACKWARD</code> in
	 * <code>fftw.h</code>.)
	 */
	public static final int	BACKWARD		= 1;

	/**
	 * Simple transform routine.
	 */
	public float[] transform(float[] in);

	/**
	 * Multiple transform routine.
	 */
	public void transform(int howmany, float[] in, int istride, int idist, float[] out, int ostride, int odist);
}
