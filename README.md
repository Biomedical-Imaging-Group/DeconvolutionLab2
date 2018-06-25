DeconvolutionLab2
=================
_Java software package for 3D deconvolution microscopy_

http://bigwww.epfl.ch/deconvolution/deconvolutionlab2/


## Outline
DeconvolutionLab2 is freely accessible and open-source for 3D deconvolution microscopy; it can be linked to well-known imaging software platforms, ImageJ, Fiji, ICY, Matlab, and it runs as a stand-alone application.
The backbone of our software architecture is a library that contains the number-crunching elements of the deconvolution task. It includes the tool for a complete validation pipeline. Inquisitive minds inclined to peruse the code will find it fosters the understanding of deconvolution.
At this stage, DeconvolutionLab2 includes a friendly user interface to run the following algortihms: Regularized Inverse Filter, Tikhonov Inverse Filter Naive Inverse Filter, Richardson-Lucy, Richardson-Lucy Total Variation, Landweber (Linear Least Squares), Non-negative Least Squares, Bounded-Variable Least Squares, Van Cittert, Tikhonov-Miller, Iterative Constraint Tikhonov-Miller, FISTA, ISTA.