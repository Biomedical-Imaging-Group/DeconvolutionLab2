/** 
 * This demo exists in 5 different interfaces:
 * - using ImageJ Macro 
 * - using the command line interface (cshell)
 * - in Java,  using the DeconvolutionLab2 as a Java Library
 * - in Java,  using the command of DeconvolutionLab2
 * - in Matlab, calling methods of the class DL2
 */
 
path  = " -path /Users/sage/Desktop/Demo/ImageJMacro/ ";
image = " -image synthetic CubeSphericalBeads 5.0 0.2 12.0 12.0  size 96 80 64 intensity 128 ";
psf   = " -psf synthetic Gaussian 2.0 2.0 2.0  size 96 80 64 ";

// create the reference image 		
identity = image + " -algorithm I -out mip reference nosave -out stack reference noshow " + path;
run("DeconvolutionLab2 Run", identity + psf);
waitForUser("Demo", "reference");

// create the simulation
simulation = " -image file reference.tif -algorithm SIM 0 0 4 -out mip simulation nosave -out stack simulation noshow " + path;
run("DeconvolutionLab2 Run", simulation + psf)
waitForUser("Demo", "simulation");

// test various algorithms
deconv = " -image file simulation.tif -reference file reference.tif "+ path + psf;
run("DeconvolutionLab2 Run", " -algorithm RIF 0.01   " + out("RIF")  + deconv);
run("DeconvolutionLab2 Run", " -algorithm RL  50     " + out("RL") + deconv);
run("DeconvolutionLab2 Run", " -algorithm LW  50 1.5 " + out("LW") + deconv);
run("DeconvolutionLab2 Run", " -algorithm LW+ 50 1.5 " + out("LW+")  + deconv);

function out(name) {
	return "-out mip @1 nosave " + name + " -out mip rescaled byte noshow " + name + "_mip_8bits -out stack noshow " + name + " -stats noshow " + name + " ";
}