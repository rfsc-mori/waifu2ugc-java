package waifu2ugc.gui.system;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.Optional;

public class ImageChooserSwing extends ImageChooser
{
	@Override
	public Optional<File> getImageFile(JFrame parent, String title, File currentDirectory) {
		Optional<File> selectedFile;

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(title);
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Images", ImageIO.getReaderFileSuffixes()));
		fileChooser.setAcceptAllFileFilterUsed(false);

		if (currentDirectory != null)
		{
			fileChooser.setCurrentDirectory(currentDirectory);
		}

		int ret = fileChooser.showOpenDialog(parent);

		if (ret == JFileChooser.APPROVE_OPTION)
		{
			selectedFile = Optional.of(fileChooser.getSelectedFile());
		}
		else
		{
			selectedFile = Optional.empty();
		}

		return selectedFile;
	}
}
