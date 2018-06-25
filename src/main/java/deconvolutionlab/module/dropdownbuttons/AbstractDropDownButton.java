package deconvolutionlab.module.dropdownbuttons;

import deconvolutionlab.module.dropdownbuttons.gpl.swingcomponents.JSplitButton;

public class AbstractDropDownButton extends JSplitButton {

	protected String moduleName	= "";

	public AbstractDropDownButton(String moduleName, String text, boolean hidePopupOnText) {
		super(text, hidePopupOnText);
		this.moduleName = moduleName;
	}
}
