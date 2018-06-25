/*  Program PsRandom
*
*   Class for obtaining a single decimal or binary pseudorandom number
*   or a sequence of decimal or binary pseudorandom numbers
*   Supplements the Java random class with the generation of
*   of lorentzian, poissonian, logistic, student's t, pareto,
*   exponential, gumbel, weibull, frechet, rayleigh,
*   beta distribution, gamma distribution, erlang distribution,
*   chi-square distribution, F-distribution, uniform,
*   gaussian (normal) and correlated gaussian pseudo-random deviates
*   and pseudorandom binary numbers (bits).
*   Also offers a choice of Knuth or Park-Miller generation methods.
*
*   Binary pseudorandom number calculations are adapted from
*   the Numerical Recipes methods written in the C language
*   based on the "primitive polynomials modulo 2" method:
*   Numerical Recipes in C, The Art of Scientific Computing,
*   W.H. Press, S.A. Teukolsky, W.T. Vetterling & B.P. Flannery,
*   Cambridge University Press, 2nd Edition (1992) pp 296 - 300.
*   (http://www.nr.com/).
*
*   AUTHOR: Dr Michael Thomas Flanagan
*   DATE:   22 April 2004
*   UPDATE: 21 November 2006, 31 December 2006, 14 April 2007, 19 October 2007, 16-29 March 2008, 3 July 2008
*           19 September 2008, 28 September 2008, 13 and 18 October 2009, 12-24 July 2010, 8 July 2011, 7 March 2012
*
*   DOCUMENTATION:
*   See Michael Thomas Flanagan's Java library on-line web page:
*   http://www.ee.ucl.ac.uk/~mflanaga/java/PsRandom.html
*   http://www.ee.ucl.ac.uk/~mflanaga/java/
*
* Copyright (c) 2004 - 2009  Michael Thomas Flanagan
*
* PERMISSION TO COPY:
*
* Permission to use, copy and modify this software and its documentation for NON-COMMERCIAL purposes is granted, without fee,
* provided that an acknowledgement to the author, Dr Michael Thomas Flanagan at www.ee.ucl.ac.uk/~mflanaga, appears in all copies
* and associated documentation or publications.
*
* Redistributions of the source code of this source code, or parts of the source codes, must retain the above copyright notice, this list of conditions
* and the following disclaimer and requires written permission from the Michael Thomas Flanagan:
*
* Redistribution in binary form of all or parts of this class must reproduce the above copyright notice, this list of conditions and
* the following disclaimer in the documentation and/or other materials provided with the distribution and requires written permission from the Michael Thomas Flanagan:
*
* Dr Michael Thomas Flanagan makes no representations about the suitability or fitness of the software for any or for a particular purpose.
* Dr Michael Thomas Flanagan shall not be liable for any damages suffered as a result of using, modifying or distributing this software
* or its derivatives.
*
***************************************************************************************/
package bilib.tools;

import java.io.Serializable;
import java.util.Random;

public class PsRandom implements Serializable {

    private static final long serialVersionUID = 1L;  // serial version unique identifier

    private long seed;          //  current seed value -   updated after each generation of a pseudorandom bit
                                //  initial seed supplied as either:
                                //              i.  as the current clock time (in milliseconds since 1970)
                                //                  (no argument given to the constructor)
                                //              ii. by the user as the constructor argument
                                //  method are available for resetting the value of the seed

    private long initialSeed;   // initial seed value

    private int methodOptionDecimal = 1;    // Method for calculating pseudorandom decimal numbers
                                 // = 1 Method -    Knuth - this class calls the Java Random class which
                                 //                 implements this method
                                 //                 See Numerical Recipes in C - W.H. Press et al. (Cambridge)
    		                     //                 1st edition 1988 p. 212, 2nd edition 1992 p283 for details
    		                     //                 This is the default option used in all methods in this class,
    		                     //                 Pseudorandom, generating a decimal random number, i.e. all but
    		                     //                 the methods to generate pseudorandom binary numbers.
                                 // = 2 Method -    Park and Miller random number generator with Bays-Durham shuffle
                                 //                 after 	ran1 	(Numerical Recipes in C - W.H. Press et al. (Cambridge)
    		                     //                 2nd edition 1992 p280.

    private int methodOptionInteger = 1;    // Method for calculating pseudorandom integer numbers
                                 // = 1 Method -    java Random nextInt(int(n)
                                 // = 2 Method -    next double scaled with rounding
                                 // = 3 Method -    next doubled scaled with flooring

    private Random rr = null;    // instance of java.util.Random if Knuth method (default method) used

    private int methodOptionBinary = 1;     // Method for calculating pseudorandom binary numbers
                                // = 1 Method -     Primitive polynomials modulo 2 method - version 1
                                //                  This method is the more cumbersome one in terms of coding (see method 2 below)
                                //                  but more readily lends itself to a shift register hardware implementation
                                // = 2 Method -     Primitive polynomials modulo 2 method - version 2
                                //                  This method is the more suited to software implementation (compare with method 1 above)
                                //                  but lends itself less readily to a shift register hardware implementation
                                // Method 1 is the default option

    // Park and Miller constants and variables
    private long ia     = 16807L;
    private long im     = 2147483647L;
    private double am   = 1.0D/im;
    private long iq     = 127773L;
    private long ir     = 2836L;
    private int ntab    = 32;
    private long ndiv   = (1L + (im - 1L)/ntab);
    private double eps  = 1.2e-7;
    private double rnmx = 1.0D - eps;
    private long iy     = 0L;
    private long[] iv = new long[ntab];

    // Box-Muller variables
	private int	    iset = 0;
	private double	gset = 0.0D;

    // Polynomial powers of 2 (used in calculation of psedorandom binary numbers)
    // See header reference (Numerical Recipes) above for polynomials other than (18, 5, 2, 1, 0)
    private long powTwo1    = 1;
    private long powTwo2    = 2;
    private long powTwo5    = 16;
    private long powTwo18   = 131072;
    private long mask       = powTwo1 + powTwo2 + powTwo5;

    // CONSTRUCTORS

    
    // Seed taken from the clock
    public PsRandom(){
        this.seed = System.currentTimeMillis();
        this.initialSeed = this.seed;
        this.rr = new Random(this.seed);
    }

    // Seed supplied by user
    public PsRandom(long seed){
        this.seed = seed;
        this.initialSeed = seed;
        this.rr = new Random(this.seed);
    }

    // METHODS

    // Resets the value of the seed
    public void setSeed(long seed){
        this.seed = seed;
        if(this.methodOptionDecimal==1)rr = new Random(this.seed);
    }

    // Returns the initial value of the seed
    public long getInitialSeed(){
        return this.initialSeed;
    }

    // Returns the current value of the seed
    public long getSeed(){
        return this.seed;
    }

    // Resets the method of calculation of a pseudorandom decimal number
    // argument = 1 -> Knuth; argument = 2 -> Parker-Miller
    // Default option = 1
    public void setMethodDecimal(int methodOpt){
        if(methodOpt<1 || methodOpt>2)throw new IllegalArgumentException("Argument to PsRandom.setMethodDecimal must 1 or 2\nValue transferred was"+methodOpt);
        this.methodOptionDecimal = methodOpt;
        if(methodOpt==1)rr = new Random(this.seed);
    }

    // Return the pseudorandom decimal number method option; 1 = Method 1 (Knuth), 2= Method 2 (Parker-Miller)
    public int getMethodDecimal(){
        return this.methodOptionDecimal;
    }

    // Resets the method of calculation of a pseudorandom integer number
    // argument = 1 -> Diego Moreira alternative 1; argument = 2 -> Diego Moreira alternative 2; 3 ->  Java Random class method nextInt
    // Default option = 1
    public void setMethodInteger(int methodOpt){
        if(methodOpt<1 || methodOpt>3)throw new IllegalArgumentException("Argument to PsRandom.setMethodInteger must 1, 2 or 3\nValue transferred was"+methodOpt);
        this.methodOptionInteger = methodOpt;
    }

    // Return the pseudorandom integer number method option; 1 = Method 1 (Diego Moreira alternative 1), 2= Method 2 (Diego Moreira alternative 2), 2 = ; 3 ->  Java Random class method nextInt
    public int getMethodInteger(){
        return this.methodOptionInteger;
    }

    // Resets the method of calculation of a pseudorandom binary number
    // argument = 1 -> method 1; argument = 2 -> Method 2
    // See above and Numerical Recipes reference (in program header) for method descriptions
    // Default option = 1
    public void setMethodBinary(int methodOpt){
        if(methodOpt<1 || methodOpt>2)throw new IllegalArgumentException("Argument to PsRandom.setMethodBinary must 1 or 2\nValue transferred was"+methodOpt);
        this.methodOptionBinary = methodOpt;
        if(methodOpt==1)rr = new Random(this.seed);
    }

    // Return the binary pseudorandom number method option; 1 = Method 1, 2= Method 2
    public int getMethodBinary(){
        return this.methodOptionBinary;
    }

    // Returns a pseudorandom double between 0.0 and 1.0
    public double nextDouble(){
        if(this.methodOptionDecimal==1){
            return this.rr.nextDouble();
        }
        else{
            return this.parkMiller();
        }
    }

    // Returns a pseudorandom double between 0.0 and top
    public double nextDouble(double top){
       return top*this.nextDouble();
    }

    // Returns a pseudorandom double between 0.0 and top
    public double nextSquare(double top){
       return top*Math.pow(nextDouble(), 2);
    }

    // Returns a pseudorandom double between bottom and top
    public double nextDouble(double bottom, double top){
        return (top - bottom)*this.nextDouble() + bottom;
    }

    // Returns an array, of length arrayLength, of pseudorandom doubles between 0.0 and 1.0
    public double[] doubleArray(int arrayLength){
        double[] array = new double[arrayLength];
        for(int i=0; i<arrayLength; i++){
            array[i] = this.nextDouble();
        }
        return array;
    }

    // Returns an array, of length arrayLength, of pseudorandom doubles between 0.0 and top
    public double[] doubleArray(int arrayLength, double top){
        double[] array = new double[arrayLength];
        for(int i=0; i<arrayLength; i++){
            array[i] = top*this.nextDouble();
        }
        return array;
    }

    // Returns an array, of length arrayLength, of pseudorandom doubles between bottom and top
    public double[] doubleArray(int arrayLength, double bottom, double top){
        double[] array = new double[arrayLength];
        for(int i=0; i<arrayLength; i++){
            array[i] = (top - bottom)*this.nextDouble() + bottom;
        }
         return array;
    }


    //  Park and Miller random number generator with Bays-Durham shuffle
    //  after 	ran1 	Numerical Recipes in C - W.H. Press et al. (Cambridge)
    //		            2nd edition 1992 p280.
    // return a pseudorandom number between 0.0 and 1.0
    public double parkMiller(){
	    int jj  = 0;
	    long kk = 0L;
	    double temp = 0.0D;
	    this.iy = 0L;

	    if(this.seed <= 0L || iy!=0){
		    if(-this.seed < 1){
		        this.seed = 1;
		    }
		    else{
		        this.seed = -this.seed;
		    }
		    for(int j=ntab+7; j>=0; j--){
			    kk = this.seed/iq;
			    this.seed = ia*( this.seed - kk*iq)- ir*kk;
			    if(this.seed < 0L) this.seed += im;
			    if (j < ntab) iv[j] = this.seed;
		    }
		    iy = iv[0];
	    }
	    kk = this.seed/iq;
	    this.seed = ia*(this.seed - kk*iq)-ir*kk;
	    if(this.seed < 0)this.seed += im;
	    jj = (int)(iy/ndiv);
	    iy = iv[jj];
	    iv[jj] = this.seed;
	    if((temp = am*iy) > rnmx){
	        return rnmx;
	    }
	    else{
	        return temp;
	    }
	}

    // Returns a pseudorandom bit
    public int nextBit(){
        if(this.methodOptionBinary==1){
            return nextBitM1();
        }
        else{
            return  nextBitM2();
        }
    }

    // Returns an array, of length arrayLength, of pseudorandom bits
    public int[] bitArray(int arrayLength){
        int[] bitarray = new int[arrayLength];
        for(int i=0; i<arrayLength; i++){
             bitarray[i]=nextBit();
        }
        return bitarray;
     }

    // Returns a pseudorandom bit - Method 1
    // This method is the more cumbersome one in terms of coding (see method 2 below)
    // but more readily lends itself to a shift register hardware implementation
    public int nextBitM1(){
        long newBit;

	    newBit =  ((this.seed & this.powTwo18) >> 17L) ^ ((this.seed & this.powTwo5) >> 4L) ^ ((this.seed & this.powTwo2) >> 1L) ^ (this.seed & this.powTwo1);
	    this.seed=(this.seed << 1L) | newBit;
	    return (int) newBit;
    }

    // Returns a pseudorandom bit - Method 2
    // This method is the more suited to software implementation (compare with method 1 above)
    // but lends itself less readily to a shift register hardware implementation
    public int nextBitM2(){
        int randomBit = 0;
        if((this.seed & this.powTwo18)<=0L){
            this.seed = ((this.seed ^ this.mask) << 1L) | this.powTwo1;
            randomBit = 1;
        }
        else{
            this.seed <<= 1L;
            randomBit = 0;
        }

	    return randomBit;
    }

    // Returns a Gaussian (normal) random deviate
    // mean  =  the mean, sd = standard deviation
    public double nextGaussian(double mean, double sd){
        double ran = 0.0D;
        if(this.methodOptionDecimal==1){
            ran=this.rr.nextGaussian();
        }
        else{
            ran=this.boxMullerParkMiller();
        }
        ran = ran*sd+mean;
        return ran;
    }

    // Returns a Gaussian (normal) random deviate
    // mean  =  0.0, sd = 1.0
    public double nextGaussian(){
        double ran = 0.0D;
        if(this.methodOptionDecimal==1){
            ran=this.rr.nextGaussian();
        }
        else{
            ran=this.boxMullerParkMiller();
        }
        return ran;
    }

    public double nextNormal(double mean, double sd){
        return nextGaussian(mean, sd);
    }

    public double nextNormal(){
        return nextGaussian();
    }

    // Box-Muller normal deviate generator
    //      after 	gasdev 	(Numerical Recipes in C - W.H. Press et al. (Cambridge)
    //		2nd edition 1992 p289
    // Uses Park and Miller method for generating pseudorandom numbers
    double boxMullerParkMiller(){
	    double fac = 0.0D, rsq = 0.0D, v1 = 0.0D, v2 = 0.0D;

	    if (iset==0){
		    do {
			    v1=2.0*parkMiller()-1.0D;
			    v2=2.0*parkMiller()-1.0D;
			    rsq=v1*v1+v2*v2;
		    }while (rsq >= 1.0D || rsq == 0.0D);
		    fac=Math.sqrt(-2.0D*Math.log(rsq)/rsq);
		    gset=v1*fac;
		    iset=1;
		    return v2*fac;
	    }else{
	    	iset=0;
		    return gset;
	    }
    }

    // Returns a Lorentzian pseudorandom deviate
    // mu  =  the mean, gamma = half-height widthy
    public double nextLorentzian (double mu, double gamma){
        double ran = Math.tan((this.nextDouble()-0.5)*Math.PI);
        ran = ran*gamma/2.0D+mu;

        return ran;
    }


    // Returns an array of Lorentzian pseudorandom deviates
    // mu  =  the mean, gamma = half-height width, n = length of array
    public double[] lorentzianArray (double mu, double gamma, int n){
        double[] ran = new double[n];
        for(int i=0; i<n; i++){
            ran[i]=Math.tan((this.nextDouble()-0.5)*Math.PI);
            ran[i] = ran[i]*gamma/2.0D+mu;
        }
        return ran;
    }

    // Returns a Poissonian pseudorandom deviate
    // follows the approach of Numerical Recipes, 2nd Edition, p 294
    public double nextPoissonian(double mean){
        double ran = 0.0D;
        double oldm = -1.0D;
        double expt = 0.0D;
        double em = 0.0D;
        double term = 0.0D;
        double sq = 0.0D;
        double lnMean = 0.0D;
        double yDev = 0.0D;

        if(mean < 12.0D){
            if(mean != oldm){
                oldm = mean;
                expt = Math.exp(-mean);
            }
            em = -1.0D;
            term = 1.0D;
            do{
                ++em;
                term *= this.nextDouble();
            }while(term>expt);
            ran = em;
        }
        else{
            if(mean != oldm){
                oldm = mean;
                sq = Math.sqrt(2.0D*mean);
                lnMean = Math.log(mean);
                expt = mean*lnMean - logGamma(mean+1.0D);
            }
            do{
                do{
                    yDev = Math.tan(Math.PI*this.nextDouble());
                    em = sq*yDev+mean;
                }while(em<0.0D);
                em = Math.floor(em);
                term = 0.9D*(1.0D+yDev*yDev)*Math.exp(em*lnMean - logGamma(em+1.0D)-expt);
            }while(this.nextDouble()>term);
            ran = em;
        }
        return ran;
    }

    // Returns an array of Poisson random deviates
    // follows the approach of Numerical Recipes, 2nd Edition, p 294
    public double[] poissonianArray(double mean, int n){
        double[] ran = new double[n];
        double oldm = -1.0D;
        double expt = 0.0D;
        double em = 0.0D;
        double term = 0.0D;
        double sq = 0.0D;
        double lnMean = 0.0D;
        double yDev = 0.0D;

        if(mean < 12.0D){
            for(int i=0; i<n; i++){
                if(mean != oldm){
                    oldm = mean;
                    expt = Math.exp(-mean);
                }
                em = -1.0D;
                term = 1.0D;
                do{
                    ++em;
                    term *= this.nextDouble();
                }while(term>expt);
                ran[i] = em;
            }
        }
        else{
            for(int i=0; i<n; i++){
                if(mean != oldm){
                    oldm = mean;
                    sq = Math.sqrt(2.0D*mean);
                    lnMean = Math.log(mean);
                    expt = mean*lnMean - logGamma(mean+1.0D);
                }
                do{
                    do{
                        yDev = Math.tan(Math.PI*this.nextDouble());
                        em = sq*yDev+mean;
                    }while(em<0.0D);
                    em = Math.floor(em);
                    term = 0.9D*(1.0D+yDev*yDev)*Math.exp(em*lnMean - logGamma(em+1.0D)-expt);
                }while(this.nextDouble()>term);
                ran[i] = em;
            }
        }
        return ran;
    }

    // Returns a Binomial pseudorandom deviate from a binomial
    //  distribution of nTrial trials each of probablity, prob,
    //  after 	bndlev 	Numerical Recipes in C - W.H. Press et al. (Cambridge)
    //		            2nd edition 1992 p295.
    public double nextBinomial(double prob, int nTrials){

        if(prob<0.0D || prob>1.0D)throw new IllegalArgumentException("The probablity provided, " + prob + ", must lie between 0 and 1)");

        double binomialDeviate = 0.0D;                  // the binomial deviate to be returned
	    double deviateMean = 0.0D;                      // mean of deviate to be produced
	    double testDeviate = 0.0D;                      // test deviate
	    double workingProb = 0.0;                       // working value of the probability
	    double logProb = 0.0;                           // working value of the probability
	    double probOld = -1.0D;                         // previous value of the working probability
	    double probC = -1.0D;                           // complementary value of the working probability
	    double logProbC = -1.0D;                        // log of the complementary value of the working probability
	    int nOld= -1;                                   // previous value of trials counter
	    double enTrials = 0.0D;                         // (double) trials counter
	    double oldGamma = 0.0D;                         // a previous log Gamma function value
	    double tanW = 0.0D;                             // a working tangent
	    double hold0 = 0.0D;                            // a working holding variable
	    int jj;                                         // counter

	    workingProb=(prob <= 0.5D ? prob : 1.0-prob);    // distribution invariant on swapping prob for 1 - prob
	    deviateMean = nTrials*workingProb;

	    if(nTrials < 25) {
	        // if number of trials greater than 25 use direct method
		    binomialDeviate=0.0D;
		    for (jj=1;jj<=nTrials;jj++)if (this.nextDouble() < workingProb) ++binomialDeviate;
	    }
	    else if(deviateMean < 1.0D) {
	        // if fewer than 1 out of 25 events - Poisson approximation is accurate
		    double expOfMean=Math.exp(-deviateMean);
		    testDeviate=1.0D;
		    for(jj=0;jj<=nTrials;jj++) {
			    testDeviate *= this.nextDouble();
			    if (testDeviate < expOfMean) break;
		    }
		    binomialDeviate=(jj <= nTrials ? jj : nTrials);

	    }
	    else{
	        // use rejection method
		    if(nTrials != nOld) {
		        // if nTrials has changed compute useful quantities
			    enTrials = (double)nTrials;
			    oldGamma = logGamma(enTrials + 1.0D);
			    nOld = nTrials;
		    }
		    if(workingProb != probOld) {
		        // if workingProb has changed compute useful quantities
                probC = 1.0 - workingProb;
			    logProb = Math.log(workingProb);
			    logProbC = Math.log(probC);
			    probOld = workingProb;
		    }

		    double sq = Math.sqrt(2.0*deviateMean*probC);
		    do{
			    do{
				    double angle = Math.PI*this.nextDouble();
				    tanW = Math.tan(angle);
				    hold0 = sq*tanW + deviateMean;
			    }while(hold0 < 0.0D || hold0 >= (enTrials + 1.0D));                 // rejection test
			    hold0 = Math.floor(hold0);                                          // integer value distribution
			    testDeviate = 1.2D*sq*(1.0D + tanW*tanW)*Math.exp(oldGamma - logGamma(hold0 + 1.0D) - logGamma(enTrials - hold0 + 1.0D) + hold0*logProb + (enTrials - hold0)*logProbC);
		    }while(this.nextDouble() > testDeviate);                                // rejection test
		    binomialDeviate=hold0;
	    }

	    if(workingProb != prob) binomialDeviate = nTrials - binomialDeviate;        // symmetry transformation

	    return binomialDeviate;
    }

    // Returns an array of n Binomial pseudorandom deviates from a binomial
    //  distribution of nTrial trials each of probablity, prob,
    //  after 	bndlev 	Numerical Recipes in C - W.H. Press et al. (Cambridge)
    //		            2nd edition 1992 p295.
    public double[] binomialArray(double prob, int nTrials, int n){

        if(nTrials<n)throw new IllegalArgumentException("Number of deviates requested, " + n + ", must be less than the number of trials, " + nTrials);
        if(prob<0.0D || prob>1.0D)throw new IllegalArgumentException("The probablity provided, " + prob + ", must lie between 0 and 1)");

        double[] ran = new double[n];                   // array of deviates to be returned

	    double binomialDeviate = 0.0D;                  // the binomial deviate to be returned
	    double deviateMean = 0.0D;                      // mean of deviate to be produced
	    double testDeviate = 0.0D;                      // test deviate
	    double workingProb = 0.0;                       // working value of the probability
	    double logProb = 0.0;                           // working value of the probability
	    double probOld = -1.0D;                         // previous value of the working probability
	    double probC = -1.0D;                           // complementary value of the working probability
	    double logProbC = -1.0D;                        // log of the complementary value of the working probability
	    int nOld= -1;                                   // previous value of trials counter
	    double enTrials = 0.0D;                         // (double) trials counter
	    double oldGamma = 0.0D;                         // a previous log Gamma function value
	    double tanW = 0.0D;                             // a working tangent
	    double hold0 = 0.0D;                            // a working holding variable
	    int jj;                                         // counter

        double probOriginalValue = prob;
        for(int i=0; i<n; i++){
            prob = probOriginalValue;
	        workingProb=(prob <= 0.5D ? prob : 1.0-prob);    // distribution invariant on swapping prob for 1 - prob
	        deviateMean = nTrials*workingProb;

	        if(nTrials < 25) {
	            // if number of trials greater than 25 use direct method
		        binomialDeviate=0.0D;
		        for(jj=1;jj<=nTrials;jj++)if (this.nextDouble() < workingProb) ++binomialDeviate;
	        }
	        else if(deviateMean < 1.0D) {
	            // if fewer than 1 out of 25 events - Poisson approximation is accurate
		        double expOfMean=Math.exp(-deviateMean);
		        testDeviate=1.0D;
		        for (jj=0;jj<=nTrials;jj++) {
			        testDeviate *= this.nextDouble();
			        if (testDeviate < expOfMean) break;
		        }
		        binomialDeviate=(jj <= nTrials ? jj : nTrials);

	        }
	        else{
	            // use rejection method
		        if(nTrials != nOld) {
		            // if nTrials has changed compute useful quantities
			        enTrials = (double)nTrials;
			        oldGamma = logGamma(enTrials + 1.0D);
			        nOld = nTrials;
		        }
		        if(workingProb != probOld) {
		            // if workingProb has changed compute useful quantities
                    probC = 1.0 - workingProb;
			        logProb = Math.log(workingProb);
			        logProbC = Math.log(probC);
			        probOld = workingProb;
		        }

		        double sq = Math.sqrt(2.0*deviateMean*probC);
		        do{
			        do{
				        double angle = Math.PI*this.nextDouble();
				        tanW = Math.tan(angle);
				        hold0 = sq*tanW + deviateMean;
			        }while(hold0 < 0.0D || hold0 >= (enTrials + 1.0D));                 // rejection test
			        hold0 = Math.floor(hold0);                                          // integer value distribution
			        testDeviate = 1.2D*sq*(1.0D + tanW*tanW)*Math.exp(oldGamma - logGamma(hold0 + 1.0D) - logGamma(enTrials - hold0 + 1.0D) + hold0*logProb + (enTrials - hold0)*logProbC);
		        }while(this.nextDouble() > testDeviate);                                // rejection test
		        binomialDeviate=hold0;
	        }

	        if(workingProb != prob) binomialDeviate = nTrials - binomialDeviate;       // symmetry transformation

	        ran[i] = binomialDeviate;
	    }

	    return ran;
    }


    // Returns a Pareto pseudorandom deviate
    public double nextPareto(double alpha, double beta){
        return Math.pow(1.0D-this.nextDouble(), -1.0D/alpha)*beta;
    }

    // Returns an array, of Pareto pseudorandom deviates, of length n
    public double[] paretoArray (double alpha, double beta, int n){
        double[] ran = new double[n];
        for(int i=0; i<n; i++){
            ran[i] = Math.pow(1.0D-this.nextDouble(), -1.0D/alpha)*beta;
        }
        return ran;
    }

    // Returns an exponential pseudorandom deviate
    public double nextExponential(double mu, double sigma){
        return mu - Math.log(1.0D-this.nextDouble())*sigma;
     }

    // Returns an array, of exponential pseudorandom deviates, of length n
    public double[] exponentialArray (double mu, double sigma, int n){
        double[] ran = new double[n];
        for(int i=0; i<n; i++){
            ran[i] = mu - Math.log(1.0D-this.nextDouble())*sigma;
        }
        return ran;
    }

   // Returns a Rayleigh pseudorandom deviate
   public double nextRayleigh(double sigma){
        return Math.sqrt(-2.0D*Math.log(1.0D-this.nextDouble()))*sigma;
    }

   // Returns an array, of Rayleigh pseudorandom deviates, of length n
   public double[] rayleighArray (double sigma, int n){
        double[] ran = new double[n];
        for(int i=0; i<n; i++){
            ran[i] = Math.sqrt(-2.0D*Math.log(1.0D-this.nextDouble()))*sigma;
        }
        return ran;
    }

    // Returns a minimal Gumbel (Type I EVD) random deviate
    // mu  =  location parameter, sigma = scale parameter
    public double nextMinimalGumbel(double mu, double sigma){
        return Math.log(Math.log(1.0D/(1.0D-this.nextDouble())))*sigma+mu;
    }

    // Returns an array of minimal Gumbel (Type I EVD) random deviates
    // mu  =  location parameter, sigma = scale parameter, n = length of array
    public double[] minimalGumbelArray(double mu, double sigma,  int n){
        double[] ran = new double[n];
        for(int i=0; i<n; i++){
            ran[i] = Math.log(Math.log(1.0D/(1.0D-this.nextDouble())))*sigma+mu;
        }
        return ran;
    }

    // Returns a maximal Gumbel (Type I EVD) random deviate
    // mu  =  location parameter, sigma = scale parameter
    public double nextMaximalGumbel(double mu, double sigma){
        return mu-Math.log(Math.log(1.0D/(1.0D-this.nextDouble())))*sigma;
    }

    // Returns an array of maximal Gumbel (Type I EVD) random deviates
    // mu  =  location parameter, sigma = scale parameter, n = length of array
    public double[] maximalGumbelArray(double mu, double sigma,  int n){
        double[] ran = new double[n];
        for(int i=0; i<n; i++){
            ran[i] = mu-Math.log(Math.log(1.0D/(1.0D-this.nextDouble())))*sigma;
        }
        return ran;
    }

    // Returns a Frechet (Type II EVD) random deviate
    // mu  =  location parameter, sigma = scale parameter, gamma = shape parameter
    public double nextFrechet(double mu, double sigma, double gamma){
        return Math.pow((1.0D/(Math.log(1.0D/this.nextDouble()))),1.0D/gamma)*sigma + mu;
    }

    // Returns an array of Frechet (Type II EVD) random deviates
    // mu  =  location parameter, sigma = scale parameter, gamma = shape parameter, n = length of array
    public double[] frechetArray(double mu, double sigma,  double gamma, int n){
        double[] ran = new double[n];
        for(int i=0; i<n; i++){
            ran[i] = Math.pow((1.0D/(Math.log(1.0D/this.nextDouble()))),1.0D/gamma)*sigma + mu;
         }
         return ran;
    }

    // Returns a Weibull (Type III EVD) random deviate
    // mu  =  location parameter, sigma = scale parameter, gamma = shape parameter
    public double nextWeibull(double mu, double sigma, double gamma){
        return  Math.pow(-Math.log(1.0D-this.nextDouble()),1.0D/gamma)*sigma + mu;
     }

    // Returns an array of Weibull (Type III EVD)  random deviates
    // mu  =  location parameter, sigma = scale parameter, gamma = shape parameter, n = length of array
    public double[] weibullArray(double mu, double sigma,  double gamma, int n){
        double[] ran = new double[n];
        for(int i=0; i<n; i++){
            ran[i] = Math.pow(-Math.log(1.0D-this.nextDouble()),1.0D/gamma)*sigma + mu;
        }
        return ran;
    }

    // Returns an array of Logistic distribution random deviates
    // mu = location parameter, scale = scale parameter
    public double nextLogistic(double mu, double scale){
        return 2.0D*scale*atanh(2.0D*this.nextDouble() - 1.0D) + mu;
    }

    // Returns an array of Logistic distribution random deviate
    // mu = location parameter, scale = scale parameter, n is the length of the returned array
    public double[] logisticArray(double mu, double scale, int n){
        double[] ran = new double[n];
        for(int i=0; i<n; i++) ran[i] = 2.0D*scale*atanh(2.0D*this.nextDouble() - 1.0D) + mu;
        return ran;
    }


 

    // PSEUDORANDOM INTEGERS
    // Returns a pseudorandom int integer between 0.0 and top
    public int nextInteger(int bottom, int top){
        int randint = 0;
        switch(this.methodOptionInteger){
             case 1: // Java Random class nextInt()
                    randint = this.rr.nextInt(++top - bottom) + bottom;
                    break;
            case 2: randint = (int)Math.round(this.nextDouble()*(top - bottom)) + bottom;
                    break;
            case 3: randint = (int)Math.floor(this.nextDouble()*(++top - bottom)) + bottom;
                    break;
            default: throw new IllegalArgumentException("methodOptionInteger, " + this.methodOptionInteger + " is not recognised");
        }

        return randint;
    }

    // Returns a pseudorandom int integer between bottom (inclusive) and top (inclusive)
    public int nextInteger(int top){
        return this.nextInteger(0, top);
    }

    // Returns an array, of length arraylength, of pseudorandom int integers between 0.0 (inclusive) and top (inclusive)
    public int[] integerArray(int arrayLength, int top){
        int[] array = new int[arrayLength];
        for(int i=0; i<arrayLength; i++){
            array[i] = this.nextInteger(top);
        }
        return array;
    }

    // Returns an array, of length arraylength, of pseudorandom int integers between bottom and top
    public int[] integerArray(int arrayLength, int bottom, int top){
        int[] array = new int[arrayLength];
        for(int i=0; i<arrayLength; i++){
            array[i] = this.nextInteger(top - bottom) + bottom;
        }
        return array;
    }

    // Returns an array, of length arraylength, of pseudorandom int integers between bottom and top with no repeats
    public int[] noRepeatIntegerArray(int arrayLength, int bottom, int top){
        int[] array = new int[arrayLength];
        boolean test = true;
        int hold = -1;
        int nFound = 0;
        boolean repeatTest = true;
        while(test){
            hold = this.nextInteger(top - bottom) + bottom;
            if(nFound==0){
                array[0] = hold;
                nFound = 1;
            }
            else{
                repeatTest = true;
                for(int i=0; i<nFound; i++)if(array[i]==hold)repeatTest = false;
                if(repeatTest){
                    array[nFound] = hold;
                    nFound++;
                }
            }
            if(nFound==arrayLength)test = false;
        }

        return array;
    }

    // Returns an array, of length top+1, of unique pseudorandom integers between bottom and top
    // i.e. no integer is repeated and all integers between bottom and top inclusive are present
    public int[] uniqueIntegerArray(int bottom, int top){
        int range = top - bottom;
        int[] array = uniqueIntegerArray(range);
        for(int i=0; i<range+1; i++)array[i] += bottom;
        return array;
    }


    // Returns an array, of length top+1, of unique pseudorandom integers between 0 and top
    // i.e. no integer is repeated and all integers between 0 and top inclusive are present
    public int[] uniqueIntegerArray(int top){
        int numberOfIntegers = top + 1;                     // number of unique pseudorandom integers returned
        int[] array = new int[numberOfIntegers];            // array to contain returned unique pseudorandom integers
        boolean allFound = false;                           // will equal true when all required integers found
        int nFound = 0;                                     // number of required pseudorandom integers found
        boolean[] found = new boolean[numberOfIntegers];    // = true when integer corresponding to its index is found
        for(int i=0; i<numberOfIntegers; i++)found[i] = false;

        while(!allFound){
            int ii = this.nextInteger(top);
            if(!found[ii]){
                array[nFound] = ii;
                found[ii] = true;
                nFound++;
                if(nFound==numberOfIntegers)allFound = true;
            }
        }
        return array;
    }



    // Return the serial version unique identifier
    public static long getSerialVersionUID(){
        return PsRandom.serialVersionUID;
    }
    
    // Inverse hyperbolic tangent of a double number
    private static double atanh(double a){
        double sgn = 1.0D;
        if(a<0.0D){
            sgn = -1.0D;
            a = -a;
        }
        if(a>1.0D) throw new IllegalArgumentException("atanh real number argument (" + sgn*a + ") must be >= -1 and <= 1");
        return 0.5D*sgn*(Math.log(1.0D + a)-Math.log(1.0D - a));
    }

    // log to base e of the Gamma function
    // Lanczos approximation (6 terms)
    //  Lanczos Gamma Function approximation - N (number of coefficients -1)
    private static int lgfN = 6;
    private static double lgfGamma = 5.0;
    //  Lanczos Gamma Function approximation - Coefficients
    private static double[] lgfCoeff = {1.000000000190015, 76.18009172947146, -86.50532032941677, 24.01409824083091, -1.231739572450155, 0.1208650973866179E-2, -0.5395239384953E-5};
    // log to base e of the Gamma function
    // Lanczos approximation (6 terms)
    // Retained for backward compatibility
    public static double logGamma(double x){
            double xcopy = x;
            double fg = 0.0D;
            double first = x + lgfGamma + 0.5;
            double second = lgfCoeff[0];

            if(x>=0.0){
                    first -= (x + 0.5)*Math.log(first);
                    for(int i=1; i<=lgfN; i++)second += lgfCoeff[i]/++xcopy;
                    fg = Math.log(Math.sqrt(2.0*Math.PI)*second/x) - first;
            }
            else{
                    fg = Math.PI/(gamma(1.0D-x)*Math.sin(Math.PI*x));

                    if(fg!=1.0/0.0 && fg!=-1.0/0.0){
                            if(fg<0){
                                     throw new IllegalArgumentException("\nThe gamma function is negative");
                            }
                            else{
                                    fg = Math.log(fg);
                            }
                    }
            }
            return fg;
    }
    
    // Gamma function
    // Lanczos approximation (6 terms)
    // retained for backward compatibity
    private static double gamma(double x){

            double xcopy = x;
            double first = x + lgfGamma + 0.5;
            double second = lgfCoeff[0];
            double fg = 0.0D;

            if(x>=0.0){
                    first = Math.pow(first, x + 0.5)*Math.exp(-first);
                    for(int i=1; i<=lgfN; i++)second += lgfCoeff[i]/++xcopy;
                    fg = first*Math.sqrt(2.0*Math.PI)*second/x;
            }
            else{
                     fg = -Math.PI/(x*gamma(-x)*Math.sin(Math.PI*x));
            }
            return fg;
    }
}

