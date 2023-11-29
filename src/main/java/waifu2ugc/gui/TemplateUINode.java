package waifu2ugc.gui;

import waifu2ugc.gui.system.ImageChooser;
import waifu2ugc.gui.system.ImageChooserAWT;
import waifu2ugc.gui.system.ImageChooserSwing;
import waifu2ugc.gui.validators.TemplateImageSize;
import waifu2ugc.image.ImageWrapper;
import waifu2ugc.settings.DefaultsReader;
import waifu2ugc.settings.PropertyReader;
import waifu2ugc.template.TemplateCube;
import waifu2ugc.template.TemplateFace;
import waifu2ugc.template.FaceIndex;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class TemplateUINode extends AbstractUINode
{
	private final List<JComponent> uiComponents;

	private boolean useImageChooserAWT;
	private boolean isUsingCustomImage;

	TemplateUINode(MainWindow parent) {
		super(parent);

		loadDefaults();

		uiComponents = Arrays.asList(
				parent.customTemplateView,
				parent.btnLoadTemplate
		);
	}

	@Override
	void windowOpened() {
		TemplateCube cube = parent.getTemplateCube();

		parent.customTemplateView.setTemplateCube(cube);
		parent.customTemplateView.repaintTemplate();
	}

	@Override
	void setupListeners() {
		parent.btnLoadTemplate.addActionListener(this::openTemplateImage);
	}

	void repaintFace(FaceIndex face) {
		parent.customTemplateView.repaintFace(face);
	}

	void repaintFace(TemplateFace face) {
		parent.customTemplateView.repaintFace(face.getIndex());
	}

	void repaintEnabledFaces() {
		TemplateCube cube = parent.getTemplateCube();
		cube.getEnabledFaces().forEach(this::repaintFace);
	}

	void updateCurrentFace() {
		FaceEditorUINode faceEditor = parent.getFaceEditor();
		FaceIndex index = parent.customTemplateView.getCurrentFace();
		FaceIndex current = faceEditor.getFaceIndex();

		if (index != FaceIndex.INVALID)
		{
			repaintFace(index);
		}

		if (index != current)
		{
			parent.customTemplateView.setCurrentFace(current);

			if (current != FaceIndex.INVALID)
			{
				repaintFace(current);
			}
		}
	}

	private void openTemplateImage(ActionEvent e) {
		ImageChooser imageChooser;

		if (useImageChooserAWT)
		{
			imageChooser = new ImageChooserAWT();
		}
		else
		{
			imageChooser = new ImageChooserSwing();
		}

		String title = String.format("%s - Select a template", MainWindow.getApplicationName());
		Optional<File> file = imageChooser.getImageFile(parent.getFrame(), title);

		if (file.isPresent())
		{
			try
			{
				loadTemplateImage(new ImageWrapper(file.get()), true);
				isUsingCustomImage = true;
			}
			catch (IOException exception)
			{
				// TODO: Notify user!
				exception.printStackTrace();
			}
		}
	}

	void loadTemplateImage(ImageWrapper image) {
		loadTemplateImage(image, false);
	}

	void loadTemplateImage(ImageWrapper image, boolean isUserRequest) {
		TemplateCube cube = parent.getTemplateCube();
		StateValidatorUINode stateValidator = parent.getStateValidator();

		if (image.hasImage())
		{
			if (!isUsingCustomImage || isUserRequest)
			{
				TemplateImageSize validator = new TemplateImageSize(parent.getFrame(), cube);
				boolean useImage = validator.validateImage(image);

				if (useImage)
				{
					cube.setImage(image);
				}
				else
				{
					String title = String.format("%s - %s", MainWindow.getApplicationName(), validator.getTitle());
					String msg = validator.getMessage();

					JOptionPane.showMessageDialog(parent.getFrame(), msg, title, JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}

		if (cube.hasNoImage())
		{
			loadDefaultTemplateImage();
		}

		parent.customTemplateView.repaintView();

		stateValidator.update();
	}

	void loadDefaultTemplateImage() {
		TemplateCube cube = parent.getTemplateCube();
		LayoutUINode layoutNode = parent.getLayoutNode();
		StateValidatorUINode stateValidator = parent.getStateValidator();

		layoutNode.getDefaultLayout().readImage().ifPresent(cube::setImage);
		parent.customTemplateView.repaintView();

		stateValidator.update();
	}

	private void loadDefaults() {
		PropertyReader options = new DefaultsReader(this.getClass().getName());
		useImageChooserAWT = "AWT".equalsIgnoreCase(options.getString("imageChooser").orElse("AWT"));
	}
}
