package waifu2ugc.template.exporter.face;

import waifu2ugc.image.ImageTooBigException;
import waifu2ugc.image.ImageWrapper;
import waifu2ugc.template.FaceIndex;
import waifu2ugc.template.TemplateFace;
import waifu2ugc.template.Face;
import waifu2ugc.template.exporter.CachedCubeExporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.function.Consumer;

public class DirectoryFaceExporter extends CachedCubeExporter
{
	protected final File directory;
	protected final boolean overwrite;

	public DirectoryFaceExporter(File directory, boolean overwrite) throws FileNotFoundException {
		super();

		assert (directory != null) : "directory == null";

		if (directory.isDirectory())
		{
			this.directory = directory;
			this.overwrite = overwrite;
		}
		else
		{
			// TODO: Notify user!
			throw new FileNotFoundException(String.format("Argument @directory[%s] must be a directory.",
			                                              directory.getAbsolutePath()));
		}
	}

	public DirectoryFaceExporter(File directory, boolean overwrite, Consumer<Integer> progress) throws FileNotFoundException {
		super(progress);

		assert (directory != null) : "directory == null";

		if (directory.isDirectory())
		{
			this.directory = directory;
			this.overwrite = overwrite;
		}
		else
		{
			// TODO: Notify user!
			throw new FileNotFoundException(String.format("Argument @directory[%s] must be a directory.",
			                                              directory.getAbsolutePath()));
		}
	}

	@Override
	protected boolean exportFace(TemplateFace templateFace, Face face, int quality) {
		boolean result;

		try
		{
			ImageWrapper tile = getFrameTile(quality, templateFace, face);
			assert (tile != null) : "tile == null";

			try
			{
				File file = getFile(face);

				try
				{
					tile.saveTo(file);

					result = true;
				}
				catch (IOException exception)
				{
					result = false;

					// TODO: Notify progress!
					exception.printStackTrace();
				}
			}
			catch (FileAlreadyExistsException exception)
			{
				result = false;

				exception.printStackTrace();
			}
		}
		catch (ImageTooBigException exception)
		{
			result = false;

			exception.printStackTrace();
		}

		return result;
	}

	protected File getFile(Face face) throws FileAlreadyExistsException {
		File file = new File(directory, getFileName(face));

		if (file.exists() && !overwrite)
		{
			throw new FileAlreadyExistsException(file.getAbsolutePath());
		}

		return file;
	}

	protected String getFileName(Face face) {
		FaceIndex index = face.getIndex();
		return String.format("%d-%s_%d,%d.png", index.asInt(), index.getAlias(), face.getX2D() + 1, face.getY2D() + 1);
	}
}
