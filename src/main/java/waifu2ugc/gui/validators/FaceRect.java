package waifu2ugc.gui.validators;

import waifu2ugc.template.TemplateCube;
import waifu2ugc.template.TemplateFace;

import javax.swing.JFrame;

public class FaceRect extends FaceStateValidator
{
	public FaceRect(JFrame parentFrame, TemplateCube template) {
		super(parentFrame, template);
	}

	@Override
	protected boolean validateTemplateFace(TemplateFace face) {
		return face.hasValidRect() && face.fits(template.getImageSize());
	}

	@Override
	protected String getMessageFormat() {
		return "The faces (%s) have invalid dimensions.\n" +
		       "Please input the correct coordinates and dimensions or select another layout.";
	}

	@Override
	protected String getTitle() {
		return "Invalid face dimensions";
	}
}
