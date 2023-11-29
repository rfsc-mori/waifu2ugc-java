package waifu2ugc.template;

import waifu2ugc.image.AspectHint;
import waifu2ugc.image.ImageDimension;
import waifu2ugc.image.ImageTooBigException;
import waifu2ugc.image.ImageWrapper;
import waifu2ugc.settings.DefaultsReader;
import waifu2ugc.settings.PropertyReader;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class TemplateFace
{
	private final FaceIndex index;

	private boolean enabled;

	private Rectangle rect = new Rectangle();

	private int horizontalCount;
	private int verticalCount;

	private ImageWrapper image = new ImageWrapper();

	private boolean resamplingAllowed;
	private boolean aspectRatioPreserved;

	private AspectHint aspectHint;

	private Point fitOffset;
	private Point cropOffset;

	TemplateFace(FaceIndex index) {
		assert (index != FaceIndex.INVALID) : "An INVALID face should not be created.";

		loadDefaults();
		this.index = index;
	}

	public FaceIndex getIndex() { return index; }

	public boolean affectsXHorizontally() { return index.affectsXHorizontally(); }
	public boolean affectsXVertically() { return index.affectsXVertically(); }
	public boolean affectsYHorizontally() { return index.affectsYHorizontally(); }
	public boolean affectsYVertically() { return index.affectsYVertically(); }
	public boolean affectsZHorizontally() { return index.affectsZHorizontally(); }
	public boolean affectsZVertically() { return index.affectsZVertically(); }

	public boolean isEnabled() { return enabled; }
	public boolean isDisabled() { return !isEnabled(); }

	public void setEnabled(boolean enabled) { this.enabled = enabled; }

	public Rectangle getRect() { return new Rectangle(rect); }
	public Point getLocation() { return rect.getLocation(); }

	public boolean hasValidRect() { return (rect.x >= 0) && (rect.y >= 0) && (rect.width > 0) && (rect.height > 0); }
	public boolean hasInvalidRect() { return !hasValidRect(); }

	public void setRect(Rectangle rect) { this.rect.setRect(rect); }
	public void setX(int x) { rect.x = x; }
	public void setY(int y) { rect.y = y; }
	public void setWidth(int width) { rect.width = width; }
	public void setHeight(int height) { rect.height = height; }

	public int getHorizontalCount() { return horizontalCount; }
	public int getVerticalCount() { return verticalCount; }

	public int getBlockCount() { return getHorizontalCount() * getVerticalCount(); }
	public Dimension getBlockCountAsDimension() { return new Dimension(getHorizontalCount(), getVerticalCount()); }

	public boolean hasBlocks() { return (getBlockCount() > 0); }
	public boolean hasNoBlocks() { return !hasBlocks(); }

	public void setHorizontalCount(int count) { horizontalCount = count; }
	public void setVerticalCount(int count) { verticalCount = count; }

	public File getImageFile() { return image.getFile(); }
	public String getImageFilePath() { return image.getFilePathOptional().orElse(""); }

	public boolean isResamplingAllowed() { return resamplingAllowed; }
	public boolean isResamplingNotAllowed() { return !isResamplingAllowed(); }

	public void setResamplingAllowed(boolean allowed) { resamplingAllowed = allowed; }

	public boolean isAspectRatioPreserved() { return aspectRatioPreserved; }
	public void setAspectRatioPreserved(boolean preserved) { aspectRatioPreserved = preserved; }

	public AspectHint getAspectHint() { return aspectHint; }
	public void setAspectHint(AspectHint hint) { aspectHint = hint; }

	public boolean hasImage() { return image.hasImage(); }
	public boolean hasNoImage() { return image.hasNoImage(); }

	public ImageWrapper getImage() { return new ImageWrapper(image); }
	public void setImage(ImageWrapper image) {
		this.image.setImage(image);
	}

	public Point getFitOffset() {
		if (fitOffset == null)
		{
			fitOffset = getFrameSize().getCenteredLocation(getFinalSize());
		}

		adjustBounds(getFrameSize(), fitOffset, getFinalSize());
		return new Point(fitOffset);
	}

	public Point updateFitOffset(Point offset) {
		fitOffset = new Point(offset);

		adjustBounds(getFrameSize(), fitOffset, getFinalSize());
		return new Point(fitOffset);
	}

	public Point getCropOffset() {
		if (cropOffset == null)
		{
			cropOffset = getFinalSize().getCenteredLocation(getFrameSize());
		}

		adjustBounds(getFinalSize(), cropOffset, getFrameSize());
		return new Point(cropOffset);
	}

	public Point updateCropOffset(Point offset) {
		cropOffset = new Point(offset);

		adjustBounds(getFinalSize(), cropOffset, getFrameSize());
		return new Point(cropOffset);
	}

	public void loadImageFrom(File file) throws IOException {
		image.loadFrom(file);
	}

	public void loadImageFrom(InputStream input) throws IOException {
		image.loadFrom(input);
	}

	public boolean fits(Dimension templateSize) {
		return new Rectangle(templateSize).contains(rect);
	}

	public boolean overflows(Dimension templateSize) {
		return !fits(templateSize);
	}

	public ImageDimension getSize() {
		return new ImageDimension(rect.getSize());
	}

	public ImageDimension getImageSize() {
		return image.getSizeOptional().orElse(new ImageDimension());
	}

	public boolean isResamplingRequired() {
		return !getFrameSize().equals(getImageSize());
	}

	public boolean hasAspectRatioMismatch() {
		return !getFrameSize().hasSameAspectRatio(getFinalSize());
	}

	public ImageDimension getFrameSize() {
		return new ImageDimension(getSize()).growBy(getHorizontalCount(), getVerticalCount());
	}

	public ImageDimension getFinalSize() {
		ImageDimension size;

		if (isResamplingAllowed())
		{
			if (isAspectRatioPreserved())
			{
				if (aspectHint == AspectHint.FIT)
				{
					size = getImageSize().resizeToFit(getFrameSize());
				}
				else
				{
					size = getImageSize().resizeToCrop(getFrameSize());
				}
			}
			else
			{
				size = getFrameSize();
			}
		}
		else
		{
			size = getImageSize();
		}

		return size;
	}

	public ImageWrapper getFinalImage(int quality) throws ImageTooBigException {
		return image.getResizer().setQuality(quality).resize(getFinalSize());
	}

	public ImageWrapper createFrame(int quality) throws ImageTooBigException {
		ImageWrapper frame = new ImageWrapper(getFrameSize());
		ImageWrapper finalImage = getFinalImage(quality);

		ImageDimension frameSize = frame.getSize();

		Graphics2D g2d = frame.getImage().createGraphics();

		g2d.setComposite(AlphaComposite.Clear);
		g2d.fillRect(0, 0, frameSize.width, frameSize.height);

		g2d.setComposite(AlphaComposite.SrcOver);

		if (isResamplingAllowed() && isAspectRatioPreserved())
		{
			if (aspectHint == AspectHint.FIT)
			{
				Point origin = getFitOffset();
				g2d.drawImage(finalImage.getImage(), origin.x, origin.y, null);
			}
			else
			{
				Rectangle crop = new Rectangle(getCropOffset(), frameSize);
				g2d.drawImage(finalImage.crop(crop).getImage(), 0, 0, null);
			}
		}
		else
		{
			Point origin = frameSize.getCenteredLocation(getFinalSize());
			g2d.drawImage(finalImage.getImage(), origin.x, origin.y, null);
		}

		g2d.dispose();

		return frame;
	}

	private void adjustBounds(Dimension container, Point origin, Dimension movable) {
		if (origin.x < 0)
		{
			origin.x = 0;
		}
		else if ((origin.x + movable.width) > container.width)
		{
			origin.x = (container.width - movable.width);
		}

		if (origin.y < 0)
		{
			origin.y = 0;
		}
		else if ((origin.y + movable.height) > container.height)
		{
			origin.y = (container.height - movable.height);
		}
	}

	private void loadDefaults() {
		PropertyReader options = new DefaultsReader(this.getClass().getName());

		horizontalCount = options.getInt("horizontalCount").orElse(1);
		verticalCount = options.getInt("verticalCount").orElse(1);

		resamplingAllowed = options.getBoolean("resamplingAllowed").orElse(false);
		aspectRatioPreserved = options.getBoolean("aspectRatioPreserved").orElse(false);

		aspectHint = AspectHint.fromAlias(options.getString("aspectHint").orElse("FIT"));
	}
}
