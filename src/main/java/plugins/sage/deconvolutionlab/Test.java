package plugins.sage.deconvolutionlab;

import bilib.tools.Files;
import deconvolutionlab.Lab;
import deconvolutionlab.Platform;
import deconvolutionlab.dialog.OutputDialog;
import deconvolutionlab.output.Output.View;
import icy.gui.frame.IcyFrame;
import icy.plugin.abstract_.PluginActionable;

public class Test extends PluginActionable {

	@Override
	public void run() {
		
		Lab.init(Platform.ICY, Files.getWorkingDirectory() + "DeconvolutionLab2.config");
		IcyFrame icf = new IcyFrame();
		icf.add(new OutputDialog(View.FIGURE, 0).getContentPane());	
		icf.pack();
		icf.toFront();
		icf.addToDesktopPane();
		icf.setVisible(true);
		//icf.setResizable(true);

		
	}

}
