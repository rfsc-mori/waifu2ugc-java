package waifu2ugc.gui.validators;

import waifu2ugc.template.TemplateCube;

import javax.swing.JFrame;

public class EnabledFaces extends TemplateStateValidator
{
	public EnabledFaces(JFrame parentFrame, TemplateCube template) {
		super(parentFrame, template);
	}

	@Override
	public boolean validate() {
		return (template.getEnabledFaces().count() > 0);
	}

	@Override
	protected String getMessage() {
		return "No face enabled.\n" +
		       "Please enable the desired faces with the checkbox on the 'Faces' section.";
	}

	@Override
	protected String getTitle() {
		return "No face enabled";
	}
}
