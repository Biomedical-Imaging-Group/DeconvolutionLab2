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

package deconvolution.capsule;

import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import bilib.table.CustomizedTable;
import deconvolution.Deconvolution;
import deconvolutionlab.system.AbstractMeter;
import deconvolutionlab.system.FFTMeter;
import deconvolutionlab.system.FileMeter;
import deconvolutionlab.system.JavaMeter;
import deconvolutionlab.system.MemoryMeter;
import deconvolutionlab.system.ProcessorMeter;
import deconvolutionlab.system.SignalMeter;
import fft.FFTPanel;

/**
 * This class is a information module about the resources of the machine
 * (memory, processor, allocated signals).
 * 
 * @author Daniel Sage
 * 
 */
public class ResourcesCapsule extends AbstractCapsule implements ActionListener {

	private ArrayList<AbstractMeter>	meters;
	private JPanel	                 cards;

	private MemoryMeter	             memory;
	private ProcessorMeter	         processor;
	private SignalMeter	             signal;
	private FFTMeter	             fft;
	private JavaMeter	             java;
	private FileMeter	             file;

	private JPanel					buttons;

	private JButton					bnMemory;
	private JButton					bnProcessor;
	private JButton					bnSignal;
	private JButton					bnFFT;
	private JButton					bnJava;
	private JButton					bnFile;

	public ResourcesCapsule(Deconvolution deconvolution) {
		super(deconvolution);
		bnMemory = new JButton("Memory");
		bnProcessor = new JButton("Processor");
		bnSignal = new JButton("Signal");
		bnFFT = new JButton("FFT");
		bnJava = new JButton("Java");
		bnFile = new JButton("File");
		
		buttons = new JPanel(new GridLayout(6, 1));
		buttons.add(bnMemory);
		buttons.add(bnProcessor);
		buttons.add(bnSignal);
		buttons.add(bnFFT);
		buttons.add(bnJava);
		buttons.add(bnFile);
		
		int width = 100;
		memory = new MemoryMeter(width / 3);
		processor = new ProcessorMeter(width / 3);
		signal = new SignalMeter(width / 3);
		fft = new FFTMeter(width / 3);
		java = new JavaMeter(width / 3);
		file = new FileMeter(width / 3);

		meters = new ArrayList<AbstractMeter>();
		meters.add(processor);
		meters.add(memory);
		meters.add(signal);
		meters.add(fft);
		meters.add(java);
		meters.add(file);

		cards = new JPanel(new CardLayout());
		cards.add(memory.getMeterName(), memory.getPanel(400, 200));
		cards.add(processor.getMeterName(), processor.getPanel(400, 200));
		cards.add(signal.getMeterName(), file.getPanel(400, 200));
		cards.add(fft.getMeterName(), new FFTPanel(400, 200));
		cards.add(java.getMeterName(), java.getPanel(400, 200));
		cards.add(file.getMeterName(), file.getPanel(400, 200));

		bnFile.addActionListener(this);
		bnFFT.addActionListener(this);
		bnJava.addActionListener(this);
		bnSignal.addActionListener(this);
		bnProcessor.addActionListener(this);
		bnMemory.addActionListener(this);
	
		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buttons, cards);
		split.setDividerLocation(0.2);
	}

	@Override
	public void update() {
	}

	@Override
	public String getID() {
		return "Resources";
	}

	
	@Override
    public void actionPerformed(ActionEvent e) {
	    if (e.getSource() == bnJava)
	    	((CardLayout) (cards.getLayout())).show(cards, java.getMeterName());
	    if (e.getSource() == bnMemory)
	    	((CardLayout) (cards.getLayout())).show(cards, memory.getMeterName());
	    if (e.getSource() == bnProcessor)
	    	((CardLayout) (cards.getLayout())).show(cards, processor.getMeterName());
	    if (e.getSource() == bnFFT)
	    	((CardLayout) (cards.getLayout())).show(cards, fft.getMeterName());
	    if (e.getSource() == bnSignal)
	    	((CardLayout) (cards.getLayout())).show(cards, signal.getMeterName());
	    if (e.getSource() == bnFile)
	    	((CardLayout) (cards.getLayout())).show(cards, file.getMeterName());
   
    }


}