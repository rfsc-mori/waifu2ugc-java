package waifu2ugc.template.exporter;

import waifu2ugc.image.ImageTooBigException;
import waifu2ugc.image.ImageWrapper;
import waifu2ugc.template.TemplateFace;
import waifu2ugc.template.TemplateCube;
import waifu2ugc.template.Cube;
import waifu2ugc.template.Face;

import java.awt.Rectangle;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractCubeExporter
{
	protected Consumer<Integer> progress;

	protected int total;
	protected int status;

	protected AbstractCubeExporter() {
	}

	protected AbstractCubeExporter(Consumer<Integer> progress) {
		this.progress = progress;
	}

	public boolean process(TemplateCube template, int quality) {
		if (progress != null)
		{
			status = 0;
			total = template.getTotalBlockCount();

			progress.accept(Math.round(100.0f * status / total));
		}

		List<Boolean> result = template.explode()
		                               .filter(this::validateCube)
		                               .map(cube -> processCube(template, cube, quality))
		                               .collect(Collectors.toList());

		return result.stream().anyMatch(Boolean::booleanValue);
	}

	protected boolean validateCube(Cube cube) {
		return true;
	}

	protected boolean processCube(TemplateCube template, Cube cube, int quality) {
		List<Boolean> result = template.getFaces()
		                               .filter(this::validateTemplateFace)
		                               .filter(face -> cube.hasFace(face.getIndex()))
		                               .map(face -> processFace(face, cube.getFace(face.getIndex()), quality))
		                               .collect(Collectors.toList());

		boolean one = result.stream().anyMatch(Boolean::booleanValue);

		if (progress != null && one)
		{
			progress.accept(Math.round(100.0f * ++status / total));
		}

		return one;
	}

	protected boolean validateTemplateFace(TemplateFace templateFace) {
		return templateFace.isEnabled();
	}

	protected boolean processFace(TemplateFace templateFace, Face face, int quality) {
		return validateFace(templateFace, face) &&
		       exportFace(templateFace, face, quality);
	}

	protected boolean validateFace(TemplateFace templateFace, Face face) {
		return true;
	}

	protected abstract boolean exportFace(TemplateFace templateFace, Face face, int quality);

	protected ImageWrapper getFaceFrame(int quality, TemplateFace templateFace) throws ImageTooBigException {
		ImageWrapper frame = templateFace.createFrame(quality);
		assert (frame != null) : "frame == null";

		return frame;
	}

	protected ImageWrapper getFrameTile(int quality, TemplateFace templateFace, Face face) throws ImageTooBigException {
		Rectangle tileRect = new Rectangle(templateFace.getSize());

		tileRect.x = (tileRect.width * face.getX2D());
		tileRect.y = (tileRect.height * face.getY2D());

		return getFaceFrame(quality, templateFace).crop(tileRect);
	}
}
