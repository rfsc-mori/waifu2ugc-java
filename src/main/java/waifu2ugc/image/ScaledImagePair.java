package waifu2ugc.image;

import com.twelvemonkeys.image.ResampleOp;

import java.awt.Dimension;

public class ScaledImagePair
{
	private int quality = ResampleOp.FILTER_TRIANGLE;

	private ImageWrapper source;
	private ImageWrapper scaled;

	public int getQuality() { return quality; }
	public void setQuality(int quality) { this.quality = quality; }

	public ImageWrapper getScaled(ImageWrapper source, Dimension size) throws ImageTooBigException {
		if (!isCached(source, size))
		{
			this.source = source;
			scaled = source.getResizer().setQuality(quality).resize(size);
		}
		else
		{
			assert (source != null) : "source == null";
		}

		return scaled;
	}

	private boolean isCached(ImageWrapper image, Dimension size) {
		return (source != null) &&
		       (scaled != null) &&
		       source.containsSameImageObject(image) &&
		       scaled.getSize().equals(size);
	}
}
