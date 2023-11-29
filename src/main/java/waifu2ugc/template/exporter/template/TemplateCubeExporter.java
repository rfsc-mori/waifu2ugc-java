package waifu2ugc.template.exporter.template;

import waifu2ugc.image.ImageTooBigException;
import waifu2ugc.image.ImageWrapper;
import waifu2ugc.template.TemplateCube;
import waifu2ugc.template.TemplateFace;
import waifu2ugc.template.Face;
import waifu2ugc.template.Cube;
import waifu2ugc.template.exporter.CachedCubeExporter;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.function.Consumer;

public class TemplateCubeExporter extends CachedCubeExporter
{
	protected ImageWrapper templateImage;
	protected Graphics2D g2d;

	public TemplateCubeExporter() {
		super();
	}

	public TemplateCubeExporter(Consumer<Integer> progress) {
		super(progress);
	}

	@Override
	protected boolean processCube(TemplateCube template, Cube cube, int quality) {
		boolean result;

		templateImage = template.getImage().copy();
		assert (templateImage != null) : "templateImage == null";

		g2d = templateImage.getImage().createGraphics();
		result = super.processCube(template, cube, quality);
		g2d.dispose();

		return result;
	}

	@Override
	protected boolean exportFace(TemplateFace templateFace, Face face, int quality) {
		boolean result;

		try
		{
			ImageWrapper tile = getFrameTile(quality, templateFace, face);
			assert (tile != null) : "tile == null";

			Point location = templateFace.getLocation();
			g2d.drawImage(tile.getImage(), location.x, location.y, null);

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

	public ImageWrapper getProcessedTemplate() {
		return templateImage;
	}
}
