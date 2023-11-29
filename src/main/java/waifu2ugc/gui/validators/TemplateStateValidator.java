package waifu2ugc.gui.validators;

import waifu2ugc.template.TemplateCube;

import javax.swing.JFrame;

abstract class TemplateStateValidator extends AbstractStateValidator
{
	protected TemplateCube template;

	protected TemplateStateValidator(JFrame parentFrame, TemplateCube template) {
		super(parentFrame);
		this.template = template;
	}
}
