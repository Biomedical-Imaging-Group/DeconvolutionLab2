	
## Version 2.1.2 of DeconvolutionLab2 (27 June 2018)
- Allow snapshot for stack output and series ouptut

## Version 2.1.1 of DeconvolutionLab2 (21 June 2018)

- Add the command SIM and several show commands in Matlab interface class DL2.
- Fix the bug: no Matlab return (run in ExecutorService for Matlab only).
- Remove the debug message (exception): JTransform not found.
- Fix a bug: fourth parameters of the "synthetic" dialog box was not reset.
- Add overlay on mip, ortho, and planar outputs.
- Output mip, ortho, planar with nz=1, return the duplicate image itself, no overlay.
- Remove the option display. The Final Display is performed on ImageJ and ICY only if there is no other output.
- Simplify the output panel, direct select of dynamic and type in the Output table
- New check and choose button on Image/PSF/Reference panels.
- Stop criteria on iteration is not strictly or equal.
- Add demo: RestartRichardsonLucy, SimulationAndDeconvolution
- Add run() with a ref
- Create a panel for the reference
- Dynamic range constraints are implementations of the AbstractRange
- Compilation in Java 1.8


## Version 2.0.0 of DeconvolutionLab2 (08 May 2017)
- First public release