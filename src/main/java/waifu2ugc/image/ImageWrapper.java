package waifu2ugc.image;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class ImageWrapper
{
	private BufferedImage image;
	private File file;

	public ImageWrapper() {
		image = null;
		file = null;
	}

	public ImageWrapper(BufferedImage image) {
		this.image = image;
		file = null;
	}

	public ImageWrapper(BufferedImage image, File file) {
		this.image = image;
		this.file = file;
	}

	public ImageWrapper(ImageWrapper image) {
		this.image = image.image;
		this.file = image.file;
	}

	public ImageWrapper(Dimension size) {
		create(size);
	}

	public ImageWrapper(File file) throws IOException {
		loadFrom(file);
	}

	public ImageWrapper(InputStream input) throws IOException {
		loadFrom(input);
	}

	public BufferedImage getImage() { return image; }
	public Optional<BufferedImage> getImageOptional() { return Optional.ofNullable(image); }

	public File getFile() { return file; }
	public Optional<File> getFileOptional() { return Optional.ofNullable(file); }

	public boolean hasImage() { return (image != null); }
	public boolean hasNoImage() { return !hasImage(); }

	public boolean hasFile() { return (file != null); }
	public boolean hasNoFile() { return !hasFile(); }

	public String getFilePath() { return file.getAbsolutePath(); }
	public Optional<String> getFilePathOptional() { return hasFile() ? Optional.of(getFilePath()) : Optional.empty(); }

	public ImageDimension getSize() { return new ImageDimension(image); }
	public Optional<ImageDimension> getSizeOptional() { return hasImage() ? Optional.of(getSize()) : Optional.empty(); }

	public Rectangle getRect() { return new Rectangle(getSize()); }
	public Optional<Rectangle> getRectOptional() { return hasImage() ? Optional.of(getRect()) : Optional.empty(); }

	public int getWidth() { return image.getWidth(); }
	public Optional<Integer> getWidthOptional() { return hasImage() ? Optional.of(getWidth()) : Optional.empty(); }

	public int getHeight() { return image.getHeight(); }
	public Optional<Integer> getHeightOptional() { return hasImage() ? Optional.of(getHeight()) : Optional.empty(); }

	public ImageResizer getResizer() { return new ImageResizer(this); }

	public Optional<ImageResizer> getResizerOptional() {
		return hasImage() ?
		       Optional.of(getResizer()) :
		       Optional.empty();
	}

	public void setImage(BufferedImage image) {
		this.image = image;
		file = null;
	}

	public void setImage(BufferedImage image, File file) {
		this.image = image;
		this.file = file;
	}

	public void setImage(ImageWrapper image) {
		this.image = image.getImage();
		file = image.getFile();
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void create(Dimension size) {
		image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		file = null;
	}

	public void loadFrom(File file) throws IOException {
		image = ImageIO.read(file);
		this.file = file;
	}

	public void loadFrom(InputStream input) throws IOException {
		image = ImageIO.read(input);
		file = null;
	}

	public void reload() throws IOException {
		loadFrom(file);
	}

	public ImageWrapper copy() {
		return new ImageWrapper().copyFrom(this);
	}

	public ImageWrapper copyFrom(ImageWrapper source) {
		this.copyFrom(source.image);
		return this;
	}

	public void copyFrom(BufferedImage source) {
		ColorModel colorModel = source.getColorModel();
		WritableRaster writableRaster = source.copyData(source.getRaster().createCompatibleWritableRaster());

		image = new BufferedImage(colorModel, writableRaster, colorModel.isAlphaPremultiplied(), null);
		file = null;
	}

	public void copyInto(ImageWrapper output) {
		output.copyFrom(image);
	}

	public ImageWrapper crop(Rectangle input) {
		Rectangle rect = getRect().intersection(input);

		ImageWrapper cropped;

		if (!getRect().equals(rect))
		{
			cropped = new ImageWrapper(image.getSubimage(rect.x, rect.y, rect.width, rect.height));
		}
		else
		{
			cropped = new ImageWrapper(this);
		}

		return cropped;
	}

	public Optional<ImageWrapper> cropOptional(Rectangle input) {
		return hasImage() ? Optional.of(crop(input)) : Optional.empty();
	}

	public void save() throws IOException {
		saveTo(file);
	}

	public void saveTo(File file) throws IOException {
		ImageIO.write(image, "png", file);
	}

	@Override
	public boolean equals(Object obj) {
		boolean equal;

		if (obj instanceof ImageWrapper)
		{
			ImageWrapper other = (ImageWrapper) obj;
			equal = containsSameImageObject(other);
		}
		else
		{
			equal = super.equals(obj);
		}

		return equal;
	}

	public boolean containsSameImageObject(ImageWrapper other) {
		return (other != null) && (image == other.image);
	}

	public void reset() {
		image = null;
		file = null;
	}
}
