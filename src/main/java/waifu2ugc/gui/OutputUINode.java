package waifu2ugc.gui;

import waifu2ugc.gui.models.ResamplingHintModel;
import waifu2ugc.gui.system.DirectoryChooser;
import waifu2ugc.gui.system.DirectoryChooserSwing;
import waifu2ugc.image.ResamplingHint;
import waifu2ugc.template.TemplateCube;
import waifu2ugc.template.exporter.template.DirectoryTemplateExporter;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class OutputUINode extends AbstractUINode
{
	private final List<JComponent> uiComponents;
	private boolean isExporting;

	private File directory;

	OutputUINode(MainWindow parent) {
		super(parent);

		uiComponents = Arrays.asList(
				parent.txtOutputDirectory, parent.btnOutputDirectoryBrowse,
				parent.btnExport, parent.comboExportQuality,
				parent.labelProgress, parent.pbExport
		);
	}

	@Override
	void windowOpened() {
		parent.comboExportQuality.setModel(new ResamplingHintModel());
		uiSelectDefaultQuality();
		uiSetExporting(false);
	}

	@Override
	void setupListeners() {
		parent.btnExport.addActionListener(this::export);
		parent.txtOutputDirectory.addActionListener(this::openDirectoryByPath);
		parent.btnOutputDirectoryBrowse.addActionListener(this::browseForDirectory);
	}

	boolean isExporting() {
		return isExporting;
	}

	private void export(ActionEvent e) {
		StateValidatorUINode stateValidator = parent.getStateValidator();

		if (directory == null || !directory.isDirectory())
		{
			JOptionPane.showMessageDialog(parent.getFrame(), "Please select a directory!");
			return;
		}

		if (stateValidator.validate(true))
		{
			SwingWorker<Void, Void> exporter = new SwingWorker<Void, Void>()
			{
				@Override
				protected Void doInBackground() {
					TemplateCube template = parent.getTemplateCube();

					ResamplingHintModel model = (ResamplingHintModel) parent.comboExportQuality.getModel();
					ResamplingHint quality = model.getCurrentFilter();

					try
					{
						DirectoryTemplateExporter exporter;
						exporter = new DirectoryTemplateExporter(directory, true, this::setProgress);
						exporter.process(template, quality.asInt());
					}
					catch (IOException exception)
					{
						// TODO: Notify user!
						exception.printStackTrace();
					}

					return null;
				}

				@Override
				protected void done() {
					uiSetExporting(false);
					parent.pbExport.setValue(0);
					stateValidator.update();

					System.gc();

					JOptionPane.showMessageDialog(parent.getFrame(), "done!");
				}
			};

			exporter.addPropertyChangeListener(changeEvent -> {
				if ("progress".equals(changeEvent.getPropertyName()))
				{
					parent.pbExport.setValue((int) changeEvent.getNewValue());
				}
			});

			uiSetExporting(true);
			stateValidator.update();

			exporter.execute();
		}
	}

	private void openDirectoryByPath(ActionEvent e) {
		File dir = new File(parent.txtOutputDirectory.getText());

		try
		{
			if (!Files.list(Paths.get(directory.toURI())).findAny().isPresent())
			{
				this.directory = dir;
			}
			else
			{
				JOptionPane.showMessageDialog(parent.getFrame(), "Please select an empty directory.");
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	private void browseForDirectory(ActionEvent e) {
		DirectoryChooser directoryChooser = new DirectoryChooserSwing();
		String title = String.format("%s - Select a directory", MainWindow.getApplicationName());

		Optional<File> dir = directoryChooser.getDirectory(null, title);

		if (dir.isPresent())
		{
			File directory = dir.get();

			try
			{
				if (!Files.list(Paths.get(directory.toURI())).findAny().isPresent())
				{
					this.directory = directory;
					parent.txtOutputDirectory.setText(this.directory.getAbsolutePath());
				}
				else
				{
					JOptionPane.showMessageDialog(parent.getFrame(), "Please select an empty directory.");
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
	}

	private void uiSetExporting(boolean exporting) {
		isExporting = exporting;

		parent.labelProgress.setEnabled(isExporting);
		parent.pbExport.setEnabled(isExporting);
	}

	private void uiSelectDefaultQuality() {
		parent.comboExportQuality.getModel().setSelectedItem(ResamplingHint.getRecommended());
	}
}
