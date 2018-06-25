/*
 * DeconvolutionLab2
 * 
 * Conditions of use: You are free to use this software for research or
 * educational purposes. In addition, we expect you to include adequate
 * citations and acknowledgments whenever you present or publish results that
 * are based on it.
 * 
 * Reference: DeconvolutionLab2: An Open-Source Software for Deconvolution
 * Microscopy D. Sage, L. Donati, F. Soulez, D. Fortun, G. Schmit, A. Seitz,
 * R. Guiet, C. Vonesch, M Unser, Methods of Elsevier, 2017.
 */

/*
 * Copyright 2010-2017 Biomedical Imaging Group at the EPFL.
 * 
 * This file is part of DeconvolutionLab2 (DL2).
 * 
 * DL2 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * DL2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * DL2. If not, see <http://www.gnu.org/licenses/>.
 */

package fft.jtransforms;

import fft.AbstractFFT;
import fft.AbstractFFTLibrary;

public class JTransformsLibrary extends AbstractFFTLibrary {

	public JTransformsLibrary() {
		try {
			Class.forName("org.jtransforms.fft.FloatFFT_3D");
			Class.forName("org.jtransforms.fft.FloatFFT_1D");
			Class.forName("org.jtransforms.fft.FloatFFT_2D");
			installed = true;
		}
		catch (ClassNotFoundException ex) {
			installed = false;
		}
		if (installed) {
			ffts.add(new JTransforms());
			ffts.add(new JTransformsFFT_XYZ());
			ffts.add(new JTransformsFFT_XY_Z());
		}
	}
	
	@Override
	public String getLocation() {
		return JTransformsLibrary.class.getCanonicalName();
	}

	@Override
	public String getCredit() {
		return "JTransforms of Piotr Wendykier";
	}

	@Override
	public String getLibraryName() {
		return "JTransforms";
	}

	@Override
	public String getLicence() {
		return "<h1>JTransforms of Piotr Wendykier</h1>" + "<p>https://sites.google.com/site/piotrwendykier/software/jtransforms" + "<p>JTransforms is distributed under the terms of the BSD-2-Clause license." + "<p>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS " + "AS IS AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT " + "LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS" + "FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT " + "HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, " + "SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED " + "TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; " + "OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, " + "WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR " + "OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED " + "OF THE POSSIBILITY OF SUCH DAMAGE.";
	}

	@Override
	public AbstractFFT getDefaultFFT() {
		return new JTransforms();
	}
}
