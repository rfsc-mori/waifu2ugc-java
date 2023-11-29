package waifu2ugc.template.exporter.template;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.function.Consumer;

public class CurrentDirectoryTemplateExporter extends DirectoryTemplateExporter
{
	public CurrentDirectoryTemplateExporter(String childDirectory) throws FileNotFoundException {
		super(getChildDirectory(childDirectory), true);
	}

	public CurrentDirectoryTemplateExporter(String childDirectory, Consumer<Integer> progress) throws FileNotFoundException {
		super(getChildDirectory(childDirectory), true, progress);
	}

	protected static File getChildDirectory(String directoryName) throws FileNotFoundException {
		File currentDirectory = new File(".");
		File target = new File(currentDirectory, directoryName);

		if (!target.exists())
		{
			if (!target.mkdir())
			{
				// TODO: Notify user!
				throw new FileNotFoundException(target.getAbsolutePath());
			}
		}

		return target;
	}
}
