package waifu2ugc.gui.validators;

import waifu2ugc.template.TemplateCube;
import waifu2ugc.template.TemplateFace;

import javax.swing.JFrame;

public class FaceImagePresent extends FaceStateValidator
{
	public FaceImagePresent(JFrame parentFrame, TemplateCube template) {
		super(parentFrame, template);
	}

	@Override
	protected boolean validateTemplateFace(TemplateFace face) {
		return face.hasImage();
	}

	@Override
	protected String getMessageFormat() {
		return "The faces (%s) does not have any image selected.\n" +
		       "Please select one image for each face before continuing.";
	}

	@Override
	protected String getTitle() {
		return "No face image";
	}
}
