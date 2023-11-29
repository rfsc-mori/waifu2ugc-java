package waifu2ugc.gui.validators;

import waifu2ugc.template.TemplateCube;

import javax.swing.JFrame;

public class TemplateImagePresent extends TemplateStateValidator
{
	public TemplateImagePresent(JFrame parentFrame, TemplateCube template) {
		super(parentFrame, template);
	}

	@Override
	public boolean validate() {
		return template.hasImage();
	}

	@Override
	protected String getMessage() {
		return "No template image was selected.\n" +
		       "Please click the 'Load' button on the 'Template' section.";
	}

	@Override
	protected String getTitle() {
		return "No template image selected";
	}
}
