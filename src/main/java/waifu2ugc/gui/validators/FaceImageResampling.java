package waifu2ugc.gui.validators;

import waifu2ugc.template.TemplateCube;
import waifu2ugc.template.TemplateFace;

import javax.swing.JFrame;

public class FaceImageResampling extends FaceStateValidator
{
	public FaceImageResampling(JFrame parentFrame, TemplateCube template) {
		super(parentFrame, template);
	}

	@Override
	protected boolean validateTemplateFace(TemplateFace face) {
		return !face.isResamplingRequired() || face.isResamplingAllowed();
	}

	@Override
	protected String getMessageFormat() {
		return "The faces (%s) requires rescaling.\n" +
		       "Please mark the 'Allow resizing' checkbox or adjust the image externally.";
	}

	@Override
	protected String getTitle() {
		return "Resampling required";
	}
}
