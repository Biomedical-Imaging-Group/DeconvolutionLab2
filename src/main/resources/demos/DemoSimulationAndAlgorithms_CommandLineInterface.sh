# 
# This demo exists in 5 different interfaces:
# - using ImageJ Macro 
# - using the command line interface (cshell)
# - in Java,  using the DeconvolutionLab2 as a Java Library
# - in Java,  using the command of DeconvolutionLab2
# - in Matlab, calling methods of the class DL2
#

java -jar DeconvolutionLab_2.jar Run -image synthetic CubeSphericalBeads 5.0 0.2 12.0 12.0  size 96 80 64 intensity 128  -psf synthetic Gaussian 2.0 2.0 2.0  size 64 64 64  -algorithm I -out mistackp noshow reference -out mip rescaled byte noshow reference_mip_8bits -out stack reference noshow -path current -monitor console
java -jar DeconvolutionLab_2.jar Run -image file reference.tif -psf synthetic Gaussian 2.0 2.0 2.0  size 64 64 64 -algorithm SIM 0 0 4 -out stack noshow simulation -out mip rescaled byte noshow simulation_mip_8bits  -out stack simulation noshow -path current -monitor console
java -jar DeconvolutionLab_2.jar Run -image file simulation.tif -reference file reference.tif -psf synthetic Gaussian 2.0 2.0 2.0 -algorithm RIF 0.01   -out stack noshow RIF -out mip rescaled byte noshow RIF_mip_8bits -stats RIF noshow -path current -monitor console
java -jar DeconvolutionLab_2.jar Run -image file simulation.tif -reference file reference.tif -psf synthetic Gaussian 2.0 2.0 2.0 -algorithm RL  50     -out stack noshow RL  -out mip rescaled byte noshow RL_mip_8bits  -stats RL noshow -path current -monitor console
java -jar DeconvolutionLab_2.jar Run -image file simulation.tif -reference file reference.tif -psf synthetic Gaussian 2.0 2.0 2.0 -algorithm LW  50 1.5 -out stack noshow LWs -out mip rescaled byte noshow LW_mip_8bits  -stats LW noshow -path current -monitor console
java -jar DeconvolutionLab_2.jar Run -image file simulation.tif -reference file reference.tif -psf synthetic Gaussian 2.0 2.0 2.0 -algorithm LW+ 50 1.5 -out stack noshow LW+ -out mip rescaled byte noshow LW+_mip_8bits -stats LW+ noshow -path current -monitor console


