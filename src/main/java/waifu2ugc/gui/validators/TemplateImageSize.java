package waifu2ugc.gui.validators;

import waifu2ugc.image.ImageDimension;
import waifu2ugc.image.ImageWrapper;
import waifu2ugc.settings.DefaultsReader;
import waifu2ugc.settings.PropertyReader;
import waifu2ugc.template.TemplateCube;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.util.Optional;

public class TemplateImageSize extends TemplateStateValidator
{
	private ImageDimension min = new ImageDimension();
	private ImageDimension max = new ImageDimension();

	private ImageDimension imageSize = new ImageDimension();

	public TemplateImageSize(JFrame parentFrame, TemplateCube template) {
		super(parentFrame, template);
		loadDefaults();
	}

	public boolean validateImage(ImageWrapper image) {
		Optional<ImageDimension> imageSize = image.getSizeOptional();
		return imageSize.isPresent() && validateSize(imageSize.get());
	}

	public boolean validateSize(Dimension size) {
		imageSize.setSize(size);

		return (min.width <= size.width) && (size.width <= max.width) &&
		       (min.height <= size.height) && (size.height <= max.height);
	}

	@Override
	public boolean validate() {
		return validateImage(template.getImage());
	}

	@Override
	public String getMessage() {
		return String.format("The template image does not respect the allowed dimensions.\n" +
		                     "Accepted range:\n" +
		                     "%d <= width <= %d\n" +
		                     "%d <= height <= %d\n" +
		                     "width = %d, height = %d\n",
		                     min.width, max.width,
		                     min.height, max.height,
		                     imageSize.width, imageSize.height);
	}

	@Override
	public String getTitle() {
		return "Invalid template image size";
	}

	private void loadDefaults() {
		PropertyReader options = new DefaultsReader(this.getClass().getName());

		min.width = options.getInt("min.width").orElse(512);
		min.height = options.getInt("min.height").orElse(512);

		max.width = options.getInt("max.width").orElse(10240);
		max.height = options.getInt("max.height").orElse(10240);
	}
}
