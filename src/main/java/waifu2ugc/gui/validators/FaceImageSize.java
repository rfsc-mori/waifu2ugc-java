package waifu2ugc.gui.validators;

import waifu2ugc.image.ImageDimension;
import waifu2ugc.settings.DefaultsReader;
import waifu2ugc.settings.PropertyReader;
import waifu2ugc.template.TemplateCube;
import waifu2ugc.template.TemplateFace;

import javax.swing.JFrame;

public class FaceImageSize extends FaceStateValidator
{
	private ImageDimension min = new ImageDimension();
	private ImageDimension max = new ImageDimension();

	public FaceImageSize(JFrame parentFrame, TemplateCube template) {
		super(parentFrame, template);
		loadDefaults();
	}

	@Override
	protected boolean validateTemplateFace(TemplateFace face) {
		ImageDimension finalSize = face.getFinalSize();

		return (min.width <= finalSize.width) && (finalSize.width <= max.width) &&
		       (min.height <= finalSize.height) && (finalSize.height <= max.height);
	}

	@Override
	protected String getMessageFormat() {
		return String.format("The images of faces (%%s) cannot be resized.\n" +
		                     "Accepted range:\n" +
		                     "%d <= width <= %d\n" +
		                     "%d <= height <= %d",
		                     min.width, max.width,
		                     min.height, max.height);
	}

	@Override
	protected String getTitle() {
		return "Invalid face image size";
	}

	private void loadDefaults() {
		PropertyReader options = new DefaultsReader(this.getClass().getName());

		min.width = options.getInt("min.width").orElse(1);
		min.height = options.getInt("min.height").orElse(1);

		max.width = options.getInt("max.width").orElse(12800);
		max.height = options.getInt("max.height").orElse(12800);
	}
}
