package waifu2ugc.template.exporter.template;

import waifu2ugc.gui.MainWindow;
import waifu2ugc.gui.system.DirectoryChooser;
import waifu2ugc.gui.system.DirectoryChooserSwing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.function.Consumer;

public class PickDirectoryTemplateExporter extends DirectoryTemplateExporter
{
	public PickDirectoryTemplateExporter() throws FileNotFoundException {
		super(openDirectory(), true);
	}

	public PickDirectoryTemplateExporter(Consumer<Integer> progress) throws FileNotFoundException {
		super(openDirectory(), true, progress);
	}

	protected static File openDirectory() {
		DirectoryChooser dc = new DirectoryChooserSwing();
		String title = String.format("%s - Select a directory", MainWindow.getApplicationName());
		Optional<File> dir = dc.getDirectory(null, title);
		return dir.orElse(null);
	}
}
