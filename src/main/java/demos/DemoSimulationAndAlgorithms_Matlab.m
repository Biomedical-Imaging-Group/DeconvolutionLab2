function DemoSimulationAndAlgorithms_Matlab

    javaaddpath([matlabroot filesep 'java' filesep 'DeconvolutionLab_2.jar'])

    path  = '/Users/sage/Desktop/Demo/Matlab/';

    psf = DL2.open('synthetic', 'Gaussian 2.0 2.0 2.0  size 64 64 64', path); 

    ref = DL2.open('synthetic', 'CubeSphericalBeads 5.0 0.2 12.0 12.0  size 96 80 64 intensity 128', path); 

    y = DL2.SIM(ref, psf, 0, 0, 4, ['-out mip simulation nosave -out stack simulation noshow -path ' path]);

    x = DL2.RIF( y, psf, ref, 0.01, [out('RIF') ' -path ' path]);
    x = DL2.RL(  y, psf, ref, 50, [out('RL') ' -path ' path]);
    x = DL2.LW(  y, psf, ref, 50, 1.5, [out('LW') ' -path ' path]);
    x = DL2.NNLS(y, psf, ref, 50, 1.5, [out('LW+') ' -path ' path]);

   end

function o = out(name) 
    o= ['-out mip @1 nosave ' name ' -out mip rescaled byte noshow ' name '_mip_8bits -out stack noshow ' name ' -stats noshow ' name ' '];
end
