package waifu2ugc.image;

import com.twelvemonkeys.image.ResampleOp;

import waifu2ugc.settings.DefaultsReader;
import waifu2ugc.settings.PropertyReader;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class ImageResizer
{
	private final BufferedImage image;

	private ImageDimension targetDimension;
	private int quality;

	private ImageDimension maxSize = new ImageDimension();

	public ImageResizer(BufferedImage source) {
		loadDefaults();

		image = source;
		targetDimension = new ImageDimension(image);
	}

	public ImageResizer(ImageWrapper source) {
		loadDefaults();

		this.image = source.getImage();
		targetDimension = new ImageDimension(image);
	}

	public ImageResizer setWidth(int width) {
		targetDimension.setWidth(width);
		return this;
	}

	public ImageResizer setHeight(int height) {
		targetDimension.setHeight(height);
		return this;
	}

	public ImageResizer setSize(Dimension size) {
		targetDimension.setSize(size);
		return this;
	}

	public ImageResizer grow(int factor) {
		targetDimension = targetDimension.growBy(factor);
		return this;
	}

	public ImageResizer grow(double factor) {
		targetDimension = targetDimension.growBy(factor);
		return this;
	}

	public ImageResizer grow(int widthFactor, int heightFactor) {
		targetDimension = targetDimension.growBy(widthFactor, heightFactor);
		return this;
	}

	public ImageResizer grow(double widthFactor, double heightFactor) {
		targetDimension = targetDimension.growBy(widthFactor, heightFactor);
		return this;
	}

	public ImageResizer shrink(int factor) {
		targetDimension = targetDimension.shrinkBy(factor);
		return this;
	}

	public ImageResizer shrink(double factor) {
		targetDimension = targetDimension.shrinkBy(factor);
		return this;
	}

	public ImageResizer shrink(int widthFactor, int heightFactor) {
		targetDimension = targetDimension.shrinkBy(widthFactor, heightFactor);
		return this;
	}

	public ImageResizer shrink(double widthFactor, double heightFactor) {
		targetDimension = targetDimension.shrinkBy(widthFactor, heightFactor);
		return this;
	}

	public ImageResizer setQuality(int hint) {
		quality = hint;
		return this;
	}

	public ImageResizer setMaxSize(Dimension maxSize) {
		this.maxSize.setSize(maxSize);
		return this;
	}

	public ImageWrapper resizeToFit() throws ImageTooBigException {
		return resizeToFit(targetDimension);
	}

	public ImageWrapper resizeToFit(Dimension size) throws ImageTooBigException {
		ImageDimension source = new ImageDimension(image);
		return resize(source.resizeToFit(size));
	}

	public ImageWrapper resizeToCrop() throws ImageTooBigException {
		return resizeToCrop(targetDimension);
	}

	public ImageWrapper resizeToCrop(Dimension size) throws ImageTooBigException {
		ImageDimension source = new ImageDimension(image);
		return resize(source.resizeToCrop(size));
	}

	public ImageWrapper resize() throws ImageTooBigException {
		return resize(targetDimension);
	}

	public ImageWrapper resize(Dimension size) throws ImageTooBigException {
		ImageWrapper resized;

		if ((image.getWidth() == size.width) && (image.getHeight() == size.height))
		{
			resized = new ImageWrapper(image);
		}
		else if ((size.width > maxSize.width) && (size.height > maxSize.height))
		{
			String format = "Image size[%d x %d] is higher than allowed [%d x %d].";
			String message = String.format(format, size.width, size.height, maxSize.width, maxSize.height);
			throw new ImageTooBigException(message);
		}
		else
		{
			BufferedImageOp resampleOp = new ResampleOp(size.width, size.height, quality);
			resized = new ImageWrapper(resampleOp.filter(image, null));
		}

		return resized;
	}

	private void loadDefaults() {
		PropertyReader options = new DefaultsReader(ImageResizer.class.getName());

		quality = options.getInt("quality").orElse(ResampleOp.FILTER_TRIANGLE);

		maxSize.width = options.getInt("maxSize.width").orElse(12800);
		maxSize.height = options.getInt("maxSize.height").orElse(12800);
	}
}
