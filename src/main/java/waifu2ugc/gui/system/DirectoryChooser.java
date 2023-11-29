package waifu2ugc.gui.system;

import javax.swing.JFrame;
import java.io.File;
import java.util.Optional;

abstract public class DirectoryChooser
{
	public Optional<File> getDirectory(JFrame parent, String title) {
		return getDirectory(parent, title, null);
	}

	abstract public Optional<File> getDirectory(JFrame parent, String title, File currentDirectory);
}
