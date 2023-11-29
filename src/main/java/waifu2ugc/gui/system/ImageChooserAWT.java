package waifu2ugc.gui.system;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.FileDialog;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;

public class ImageChooserAWT extends ImageChooser
{
	@Override
	public Optional<File> getImageFile(JFrame parent, String title, File currentDirectory) {
		Optional<File> selectedFile;

		FileDialog fileDialog = new FileDialog(parent, title, FileDialog.LOAD);

		fileDialog.setFilenameFilter((dir, name) -> {
			return Arrays.stream(ImageIO.getReaderFileSuffixes()).anyMatch(name::endsWith);
		});

		if (currentDirectory != null)
		{
			fileDialog.setDirectory(currentDirectory.getAbsolutePath());
		}

		fileDialog.setVisible(true);

		String directory = fileDialog.getDirectory();
		String filename = fileDialog.getFile();

		if ((directory != null) && (filename != null))
		{
			selectedFile = Optional.of(new File(directory, filename));
		}
		else
		{
			selectedFile = Optional.empty();
		}

		return selectedFile;
	}
}
