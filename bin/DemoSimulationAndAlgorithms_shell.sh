java -jar DeconvolutionLab_2.jar Run  -image synthetic CubeSphericalBeads 5.0 0.2 12.0 12.0  size 96 80 64 intensity 128  -psf synthetic Gaussian 2.0 2.0 2.0  size 64 64 64  -algorithm I -out mip reference_mip noshow -out stack reference noshow -path current -monitor console

java -jar DeconvolutionLab_2.jar Run  -image file reference.tif -psf synthetic Gaussian 2.0 2.0 2.0  size 64 64 64 -algorithm SIM 0 0 4 -out mip simulation_mip noshow -out stack simulation noshow -path current -monitor console
D
java -jar DeconvolutionLab_2.jar Run  -image file reference.tif -reference file reference.tif -psf synthetic Gaussian 2.0 2.0 2.0   -algorithm RIF 0.01 -out mip noshow RIF_mip_32bits -out mip byte noshow RIF_mip_8bits -stats RIF noshow -path current -monitor console


#run("DeconvolutionLab2 Run", " -algorithm RL  50     -out mip @1 nosave RL -stats RL" + deconv);
#run("DeconvolutionLab2 Run", " -algorithm LW  50 1.5 -out mip @1 nosave LW -stats LW" + deconv);
#run("DeconvolutionLab2 Run", " -algorithm LW+ 50 1.5 -out mip @1 nosave LW+ -stats LW+" + deconv);


