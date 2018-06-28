package deconvolutionlab.module.dropdownbuttons;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import deconvolutionlab.Lab;
import deconvolutionlab.dialog.OutputDialog;
import deconvolutionlab.module.OutputModule;
import deconvolutionlab.output.Output;
import deconvolutionlab.output.Output.Action;
import deconvolutionlab.output.Output.View;

public class AddOutputDropDownButton extends AbstractDropDownButton implements ActionListener {

	private JMenuItem	stack	= new JMenuItem("Show stack");
	private JMenuItem	mip		= new JMenuItem("Show MIP");
	private JMenuItem	ortho	= new JMenuItem("Show orthoview");
	private JMenuItem	planar	= new JMenuItem("Show planar");
	private JMenuItem	figure	= new JMenuItem("Show figure (XY,YZ)");
	private JMenuItem	series	= new JMenuItem("Show series");

	private JMenuItem	stackSS		= new JMenuItem("Show & save stack");
	private JMenuItem	mipSS		= new JMenuItem("Show & save MIP");
	private JMenuItem	orthoSS		= new JMenuItem("Show & save orthoview)");
	private JMenuItem	planarSS	= new JMenuItem("Show & save planar");
	private JMenuItem	figureSS	= new JMenuItem("Show & save figure (XY,YZ)");
	private JMenuItem	seriesSS	= new JMenuItem("Show & save series");

	private OutputModule module = null;

	public AddOutputDropDownButton(String moduleName, String text, OutputModule module) {
		super(moduleName, text, true);
		this.moduleName = moduleName;
		this.module = module;
		setPreferredSize(new Dimension(80, 22));
		JPopupMenu popup = new JPopupMenu();
		popup.add(stack);
		popup.add(mip);
		popup.add(ortho);
		popup.add(planar);
		popup.add(figure);
		popup.add(series);
		popup.addSeparator();
		popup.add(stackSS);
		popup.add(mipSS);
		popup.add(orthoSS);
		popup.add(planarSS);
		popup.add(figureSS);
		popup.add(seriesSS);

		mip.addActionListener(this);
		ortho.addActionListener(this);
		planar.addActionListener(this);
		figure.addActionListener(this);
		stack.addActionListener(this);
		series.addActionListener(this);
		mipSS.addActionListener(this);
		orthoSS.addActionListener(this);
		planarSS.addActionListener(this);
		figureSS.addActionListener(this);
		stackSS.addActionListener(this);
		seriesSS.addActionListener(this);
		setPopupMenu(popup);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);

		View view = null;
		Action action = null;
		if (e.getSource() == stack) {
			view = View.STACK;
			action = Action.SHOW;
		}
		else if (e.getSource() == series) {
			view = View.SERIES;
			action = Action.SHOW;
		}
		else if (e.getSource() == mip) {
			view = View.MIP;
			action = Action.SHOW;
		}
		else if (e.getSource() == ortho) {
			view = View.ORTHO;
			action = Action.SHOW;
		}
		else if (e.getSource() == planar) {
			view = View.PLANAR;
			action = Action.SHOW;
		}
		else if (e.getSource() == figure) {
			view = View.FIGURE;
			action = Action.SHOW;
		}

		if (e.getSource() == stackSS) {
			view = View.STACK;
			action = Action.SHOWSAVE;
		}
		else if (e.getSource() == seriesSS) {
			view = View.SERIES;
			action = Action.SHOWSAVE;
		}
		else if (e.getSource() == mipSS) {
			view = View.MIP;
			action = Action.SHOWSAVE;
		}
		else if (e.getSource() == orthoSS) {
			view = View.ORTHO;
			action = Action.SHOWSAVE;
		}
		else if (e.getSource() == planarSS) {
			view = View.PLANAR;
			action = Action.SHOWSAVE;
		}
		else if (e.getSource() == figureSS) {
			view = View.FIGURE;
			action = Action.SHOWSAVE;
		}

		if (view != null) {
			OutputDialog dlg = new OutputDialog(view, 0);
			Lab.setVisible(dlg, true);
			if (dlg.wasCancel()) return;
			Output out = dlg.getOut();
			if (out == null) return;
			out.setAction(action);
			module.addOutput(dlg.getOut());
		}

	}
}
