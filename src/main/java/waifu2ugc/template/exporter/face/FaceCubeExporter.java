package waifu2ugc.template.exporter.face;

import waifu2ugc.image.ImageTooBigException;
import waifu2ugc.image.ImageWrapper;
import waifu2ugc.template.TemplateCube;
import waifu2ugc.template.TemplateFace;
import waifu2ugc.template.Cube;
import waifu2ugc.template.Face;
import waifu2ugc.template.exporter.CachedCubeExporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FaceCubeExporter extends CachedCubeExporter
{
	protected List<ImageWrapper> faces = new ArrayList<>();

	public FaceCubeExporter() {
		super();
	}

	public FaceCubeExporter(Consumer<Integer> progress) {
		super(progress);
	}

	@Override
	public boolean process(TemplateCube template, int quality) {
		faces.clear();
		ensureCapacity(template);
		return super.process(template, quality);
	}

	@Override
	protected boolean exportFace(TemplateFace templateFace, Face face, int quality) {
		boolean result;

		try
		{
			ImageWrapper tile = getFrameTile(quality, templateFace, face);
			assert (tile != null) : "tile == null";

			faces.add(tile);

			result = true;
		}
		catch (ImageTooBigException exception)
		{
			result = false;

			// TODO: Notify user!
			exception.printStackTrace();
		}

		return result;
	}

	protected void ensureCapacity(TemplateCube template) {
		long maxCount = template.explode()
		                        .filter(this::validateCube)
		                        .map(Cube::getFaces)
		                        .map(faceList -> faceList.collect(Collectors.toList()))
		                        .flatMap(Collection::stream)
		                        .filter(face -> validateFace(template.getFace(face.getIndex()), face))
		                        .count();

		((ArrayList) faces).ensureCapacity((int) maxCount);
	}

	public Stream<ImageWrapper> getFaces() {
		return faces.stream();
	}
}
