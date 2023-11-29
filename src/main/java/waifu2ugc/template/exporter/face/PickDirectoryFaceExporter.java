package waifu2ugc.template.exporter.face;

import waifu2ugc.gui.MainWindow;
import waifu2ugc.gui.system.DirectoryChooser;
import waifu2ugc.gui.system.DirectoryChooserSwing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.function.Consumer;

public class PickDirectoryFaceExporter extends DirectoryFaceExporter
{
	public PickDirectoryFaceExporter() throws FileNotFoundException {
		super(openDirectory(), true);
	}

	public PickDirectoryFaceExporter(Consumer<Integer> progress) throws FileNotFoundException {
		super(openDirectory(), true, progress);
	}

	protected static File openDirectory() {
		DirectoryChooser directoryChooser = new DirectoryChooserSwing();
		String title = String.format("%s - Select a directory", MainWindow.getApplicationName());
		Optional<File> directory = directoryChooser.getDirectory(null, title);
		return directory.orElse(null);
	}
}
