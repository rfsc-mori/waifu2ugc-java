package waifu2ugc.template.exporter.template;

import waifu2ugc.template.TemplateCube;
import waifu2ugc.template.Cube;
import waifu2ugc.template.Face;
import waifu2ugc.template.FaceIndex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.function.Consumer;

public class DirectoryTemplateExporter extends TemplateCubeExporter
{
	protected final File directory;
	protected final boolean overwrite;

	public DirectoryTemplateExporter(File directory, boolean overwrite) throws FileNotFoundException {
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

	public DirectoryTemplateExporter(File directory, boolean overwrite, Consumer<Integer> progress) throws FileNotFoundException {
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
	protected boolean processCube(TemplateCube template, Cube cube, int quality) {
		boolean result;

		try
		{
			File file = getFile(cube);
			result = super.processCube(template, cube, quality);

			if (result)
			{
				try
				{
					getProcessedTemplate().saveTo(file);
				}
				catch (IOException exception)
				{
					result = false;

					// TODO: Notify progress!
					exception.printStackTrace();
				}
			}
		}
		catch (FileAlreadyExistsException exception)
		{
			result = false;

			exception.printStackTrace();
		}

		return result;
	}

	protected File getFile(Cube cube) throws FileAlreadyExistsException {
		File file = new File(directory, getFileName(cube));

		if (file.exists() && !overwrite)
		{
			// TODO: Notify user!
			throw new FileAlreadyExistsException(file.getAbsolutePath());
		}

		return file;
	}

	protected String getFileName(Cube cube) {
		FaceIndex index = cube.getOuterFace();
		Face face = cube.getFace(index);

		return String.format("%d-%s_%d,%d.png", index.asInt(), index.getAlias(), face.getX2D() + 1, face.getY2D() + 1);
	}
}
