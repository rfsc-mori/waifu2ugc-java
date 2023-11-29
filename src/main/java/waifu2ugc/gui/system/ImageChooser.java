package waifu2ugc.gui.system;

import javax.swing.JFrame;
import java.io.File;
import java.util.Optional;

public abstract class ImageChooser
{
	public Optional<File> getImageFile(JFrame parent, String title) {
		return getImageFile(parent, title, null);
	}

	abstract public Optional<File> getImageFile(JFrame parent, String title, File directory);
}
