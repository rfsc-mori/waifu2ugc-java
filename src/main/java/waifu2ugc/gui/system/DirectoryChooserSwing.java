package waifu2ugc.gui.system;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.io.File;
import java.util.Optional;

public class DirectoryChooserSwing extends DirectoryChooser
{
	public Optional<File> getDirectory(JFrame parent, String title, File currentDirectory) {
		Optional<File> directory;

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(title);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);

		if (currentDirectory != null)
		{
			fileChooser.setCurrentDirectory(currentDirectory);
		}

		int ret = fileChooser.showOpenDialog(null);

		if (ret == JFileChooser.APPROVE_OPTION)
		{
			directory = Optional.of(fileChooser.getSelectedFile());
		}
		else
		{
			directory = Optional.empty();
		}

		return directory;
	}
}
