package waifu2ugc.image;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class ImageDimension extends Dimension
{
	protected int aspectHorizontal;
	protected int aspectVertical;

	public ImageDimension() {
		super();
	}

	public ImageDimension(int width, int height) {
		super(width, height);

		calculateAspectRatio();
	}

	public ImageDimension(Dimension dimension) {
		super(dimension);

		calculateAspectRatio();
	}

	public ImageDimension(Rectangle rectangle) {
		super(rectangle.getSize());

		calculateAspectRatio();
	}

	public ImageDimension(ImageDimension imageDimension) {
		super(imageDimension);

		this.aspectHorizontal = imageDimension.aspectHorizontal;
		this.aspectVertical = imageDimension.aspectVertical;
	}

	public ImageDimension(BufferedImage image) {
		super((image != null) ?
		      new Dimension(image.getWidth(), image.getHeight()) :
		      new Dimension());

		calculateAspectRatio();
	}

	public ImageDimension(ImageWrapper image) {
		super((image != null) ?
		      new Dimension(image.getWidth(), image.getHeight()) :
		      new Dimension());

		calculateAspectRatio();
	}

	public int getAspectHorizontal() {
		return aspectHorizontal;
	}

	public int getAspectVertical() {
		return aspectVertical;
	}

	public double getAspectRatio() {
		return ((width > 0) && (height > 0)) ?
		       getWidth() / getHeight() :
		       0;
	}

	public String getAspectRatioAsString() {
		return getAspectRatioAsString("%d : %d");
	}

	public String getAspectRatioAsString(String format) {
		return String.format(format, aspectHorizontal, aspectVertical);
	}

	public boolean hasSameAspectRatio(Dimension size) {
		ImageDimension other = new ImageDimension(size);
		return Math.abs(getAspectRatio() - other.getAspectRatio()) < 0.000001;
	}

	public boolean hasValidAspectRatio() {
		return (aspectHorizontal > 0) && (aspectVertical > 0);
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);

		calculateAspectRatio();
	}

	@Override
	public void setSize(Dimension dimension) {
		super.setSize(dimension);

		calculateAspectRatio();
	}

	public void setSize(Rectangle rectangle) {
		super.setSize(rectangle.getSize());

		calculateAspectRatio();
	}

	public void setSize(ImageDimension dimension) {
		super.setSize(dimension);

		this.aspectHorizontal = dimension.aspectHorizontal;
		this.aspectVertical = dimension.aspectVertical;
	}

	public void setSize(BufferedImage image) {
		if (image != null)
		{
			super.setSize(image.getWidth(), image.getHeight());
		}
		else
		{
			super.setSize(new Dimension());
		}

		calculateAspectRatio();
	}

	public void setSize(ImageWrapper image) {
		if (image != null)
		{
			super.setSize(image.getWidth(), image.getHeight());
		}
		else
		{
			super.setSize(new Dimension());
		}

		calculateAspectRatio();
	}

	public void setWidth(int width) {
		this.width = width;

		calculateAspectRatio();
	}

	public void setHeight(int height) {
		this.height = height;

		calculateAspectRatio();
	}

	public void calculateAspectRatio() {
		if (width > 0 && height > 0)
		{
			int gcd = calculateGreatestCommonDivisor();
			assert (gcd != 0) : "GreatestCommonDivisor is 0";

			aspectHorizontal = width / gcd;
			aspectVertical = height / gcd;
		}
		else
		{
			aspectHorizontal = 0;
			aspectVertical = 0;
		}
	}

	public Point getCenter() {
		return new Point(width / 2, height / 2);
	}

	public Point getCenteredLocation(Dimension cell) {
		Point centered = getCenter();
		centered.translate(-(cell.width / 2), -(cell.height / 2));

		return centered;
	}

	public Rectangle getCenteredRect(Dimension cell) {
		return new Rectangle(getCenteredLocation(cell), cell);
	}

	public static Rectangle getCenteredRect(Rectangle container, Dimension cell) {
		Rectangle centered = new ImageDimension(container).getCenteredRect(cell);
		centered.translate(container.x, container.y);

		return centered;
	}

	public ImageDimension growBy(int factor) {
		return growBy(factor, factor);
	}

	public ImageDimension growBy(double factor) {
		return growBy(factor, factor);
	}

	public ImageDimension growBy(int widthFactor, int heightFactor) {
		return new ImageDimension(width * widthFactor, height * heightFactor);
	}

	public ImageDimension growBy(double widthFactor, double heightFactor) {
		return new ImageDimension((int) Math.round(getWidth() * widthFactor),
		                          (int) Math.round(getHeight() * heightFactor));
	}

	public ImageDimension shrinkBy(int factor) {
		return shrinkBy(factor, factor);
	}

	public ImageDimension shrinkBy(double factor) {
		return shrinkBy(factor, factor);
	}

	public ImageDimension shrinkBy(int widthFactor, int heightFactor) {
		return new ImageDimension(width / widthFactor, height / heightFactor);
	}

	public ImageDimension shrinkBy(double widthFactor, double heightFactor) {
		return new ImageDimension((int) Math.round(getWidth() / widthFactor),
		                          (int) Math.round(getHeight() / heightFactor));
	}

	public ImageDimension grow(int size) {
		return growBy(size, size);
	}

	public ImageDimension grow(double size) {
		return growBy(size, size);
	}

	public ImageDimension grow(int width, int height) {
		return new ImageDimension(this.width + width, this.height + height);
	}

	public ImageDimension grow(double width, double height) {
		return new ImageDimension((int) Math.round(getWidth() + width), (int) Math.round(getHeight() + height));
	}

	public ImageDimension shrink(int size) {
		return shrinkBy(size, size);
	}

	public ImageDimension shrink(double size) {
		return shrinkBy(size, size);
	}

	public ImageDimension shrink(int width, int height) {
		return new ImageDimension(this.width - width, this.height - height);
	}

	public ImageDimension shrink(double width, double height) {
		return new ImageDimension((int) Math.round(getWidth() - width), (int) Math.round(getHeight() - height));
	}

	public ImageDimension resizeToFit(Rectangle targetRect) {
		return resizeToFit(targetRect.getSize());
	}

	public ImageDimension resizeToFit(Dimension targetSize) {
		ImageDimension fit = new ImageDimension(this);
		ImageDimension target = new ImageDimension(targetSize);

		if (fit.getAspectRatio() >= target.getAspectRatio())
		{
			fit.resizeToWidth(target);
		}
		else
		{
			fit.resizeToHeight(target);
		}

		return fit;
	}

	public ImageDimension resizeToCrop(Rectangle targetRect) {
		return resizeToCrop(targetRect.getSize());
	}

	public ImageDimension resizeToCrop(Dimension targetSize) {
		ImageDimension crop = new ImageDimension(this);
		ImageDimension target = new ImageDimension(targetSize);

		if (this.getAspectRatio() >= target.getAspectRatio())
		{
			crop.resizeToHeight(target);
		}
		else
		{
			crop.resizeToWidth(target);
		}

		return crop;
	}

	private void resizeToWidth(Dimension target) {
		this.height = (int) Math.round(target.getWidth() * (this.getHeight() / this.getWidth()));
		this.width = target.width;

		calculateAspectRatio();
	}

	private void resizeToHeight(Dimension target) {
		this.width = (int) Math.round(target.getHeight() * (this.getWidth() / this.getHeight()));
		this.height = target.height;

		calculateAspectRatio();
	}

	private int calculateGreatestCommonDivisor() {
		return (width >= height) ?
		       calculateGreatestCommonDivisor(width, height) :
		       calculateGreatestCommonDivisor(height, width);
	}

	private int calculateGreatestCommonDivisor(int numerator, int denominator) {
		return (denominator == 0) ?
		       numerator :
		       calculateGreatestCommonDivisor(denominator, numerator % denominator);
	}
}
