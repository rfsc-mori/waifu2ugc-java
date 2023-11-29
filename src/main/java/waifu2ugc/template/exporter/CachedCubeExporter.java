package waifu2ugc.template.exporter;

import waifu2ugc.image.ImageTooBigException;
import waifu2ugc.image.ImageWrapper;
import waifu2ugc.template.TemplateFace;
import waifu2ugc.template.FaceIndex;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class CachedCubeExporter extends AbstractCubeExporter
{
	protected final Map<FaceIndex, ImageWrapper> faceCache = new EnumMap<>(FaceIndex.class);

	protected CachedCubeExporter() {
		super();
	}

	protected CachedCubeExporter(Consumer<Integer> progress) {
		super(progress);
	}

	@Override
	protected ImageWrapper getFaceFrame(int quality, TemplateFace templateFace) throws ImageTooBigException {
		return getCachedFaceFrame(quality, templateFace);
	}

	protected ImageWrapper getCachedFaceFrame(int quality, TemplateFace templateFace) throws ImageTooBigException {
		ImageWrapper image;

		if (!faceCache.containsKey(templateFace.getIndex()))
		{
			image = super.getFaceFrame(quality, templateFace);
			faceCache.put(templateFace.getIndex(), image);
		}
		else
		{
			image = faceCache.get(templateFace.getIndex());
		}

		return image;
	}
}
