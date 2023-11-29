package waifu2ugc.gui;

import waifu2ugc.gui.models.AspectHintModel;
import waifu2ugc.gui.system.ImageChooser;
import waifu2ugc.gui.system.ImageChooserAWT;
import waifu2ugc.gui.system.ImageChooserSwing;
import waifu2ugc.image.AspectHint;
import waifu2ugc.image.ImageDimension;
import waifu2ugc.layout.Layout;
import waifu2ugc.settings.DefaultsReader;
import waifu2ugc.settings.PropertyReader;
import waifu2ugc.template.TemplateCube;
import waifu2ugc.template.TemplateFace;
import waifu2ugc.template.FaceIndex;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// Future revisions:
// Split FaceEditorUINode and add FacePreviewUINode
// Write a proper code for offset handling instead of shameless rushed code
class FaceEditorUINode extends AbstractUINode
{
	private TemplateFace currentFace = null;

	private final List<JComponent> uiEditorComponents;
	private final List<JComponent> uiDimensionComponents;

	private boolean isOpening;

	private Color warningColor;

	private boolean useImageChooserAWT;

	private ChangeListener updateLocation = this::updateLocation;
	private ChangeListener updateSize = this::updateSize;
	private ChangeListener updateBlockCount = this::updateBlockCount;
	private ItemListener updateResamplingOptions = this::updateResamplingOptions;
	private ChangeListener updateOffset = this::updateOffset;

	FaceEditorUINode(MainWindow parent) {
		super(parent);

		loadDefaults();

		uiEditorComponents = Arrays.asList(
				parent.panelFaceEditor,
				parent.labelNumberOfUGCBlocks,
				parent.labelFaceHorizontalCount, parent.spFaceHorizontalCount,
				parent.labelFaceVerticalCount, parent.spFaceVerticalCount,
				parent.labelFaceImageSource,
				parent.txtFaceImageSource, parent.btnFaceImageOpen,
				parent.cbAllowResampling,
				parent.cbPreserveAspectRatio, parent.comboAspectRatioHint,
				parent.labelOffset,
				parent.labelOffsetX, parent.spOffsetX,
				parent.labelOffsetY, parent.spOffsetY,
				parent.customFaceView,
				parent.labelFaceSourceInfo,
				parent.labelFaceRequiredSize
		);

		uiDimensionComponents = Arrays.asList(
				parent.labelPositionOnTemplate,
				parent.labelFaceX, parent.spFaceX,
				parent.labelFaceY, parent.spFaceY,
				parent.labelFaceWidth, parent.spFaceWidth,
				parent.labelFaceHeight, parent.spFaceHeight
		);

		parent.customFaceView.setOffsetUpdated(this::uiUpdateOffset);
	}

	@Override
	void windowOpened() {
		parent.comboAspectRatioHint.setModel(new AspectHintModel());

		uiSetDimensionComponentsEnabled(false);
		uiSetEditorComponentsEnabled(false);
	}

	@Override
	void setupListeners() {
		parent.txtFaceImageSource.addActionListener(this::openImageByPath);
		parent.btnFaceImageOpen.addActionListener(this::openImageByDialog);

		addFaceEditorListeners();
	}

	private void addFaceEditorListeners() {
		assert !isOpening : "setupListeners called while isOpening = true.";

		if (!isOpening)
		{
			parent.spFaceX.addChangeListener(updateLocation);
			parent.spFaceY.addChangeListener(updateLocation);

			parent.spFaceWidth.addChangeListener(updateSize);
			parent.spFaceHeight.addChangeListener(updateSize);

			parent.spFaceHorizontalCount.addChangeListener(updateBlockCount);
			parent.spFaceVerticalCount.addChangeListener(updateBlockCount);

			parent.cbAllowResampling.addItemListener(updateResamplingOptions);
			parent.cbPreserveAspectRatio.addItemListener(updateResamplingOptions);
			parent.comboAspectRatioHint.addItemListener(updateResamplingOptions);

			parent.spOffsetX.addChangeListener(updateOffset);
			parent.spOffsetY.addChangeListener(updateOffset);
		}
	}

	private void removeFaceEditorListeners() {
		assert isOpening : "removeListeners called while isOpening = false.";

		if (isOpening)
		{
			parent.spFaceX.removeChangeListener(updateLocation);
			parent.spFaceY.removeChangeListener(updateLocation);

			parent.spFaceWidth.removeChangeListener(updateSize);
			parent.spFaceHeight.removeChangeListener(updateSize);

			parent.spFaceHorizontalCount.removeChangeListener(updateBlockCount);
			parent.spFaceVerticalCount.removeChangeListener(updateBlockCount);

			parent.cbAllowResampling.removeItemListener(updateResamplingOptions);
			parent.cbPreserveAspectRatio.removeItemListener(updateResamplingOptions);
			parent.comboAspectRatioHint.removeItemListener(updateResamplingOptions);

			parent.spOffsetX.removeChangeListener(updateOffset);
			parent.spOffsetY.removeChangeListener(updateOffset);
		}
	}

	void updateInfo() {
		ImageDimension imageSize = new ImageDimension();
		ImageDimension requiredSize = new ImageDimension();
		ImageDimension finalSize = new ImageDimension();

		boolean resamplingRequired = false;

		if (hasValidFace() && !isOpening)
		{
			imageSize = currentFace.getImageSize();
			requiredSize = currentFace.getFrameSize();

			resamplingRequired = currentFace.isResamplingRequired();

			if (resamplingRequired && currentFace.hasImage() && currentFace.hasBlocks())
			{
				if (currentFace.isResamplingAllowed())
				{
					finalSize = currentFace.getFinalSize();
				}
			}
		}

		uiUpdateImageSize(imageSize, false);
		uiUpdateRequiredSize(requiredSize, resamplingRequired);
		uiUpdateFinalSize(finalSize, resamplingRequired);
	}

	TemplateFace getFace() { return currentFace; }
	FaceIndex getFaceIndex() { return (currentFace != null) ? currentFace.getIndex() : FaceIndex.INVALID; }

	boolean hasValidFace() { return (currentFace != null); }

	Color getWarningColor() { return warningColor; }
	void setWarningColor(Color color) { this.warningColor = color; }

	void open(FaceIndex index) {
		TemplateCube cube = parent.getTemplateCube();
		TemplateFace face = cube.getFace(index);

		open(face);
	}

	void open(TemplateFace face) {
		currentFace = face;

		if (hasValidFace() && currentFace.isEnabled())
		{
			TemplateUINode templateNode = parent.getTemplateNode();
			LayoutUINode layoutNode = parent.getLayoutNode();
			Layout layout = layoutNode.getLayout();

			uiSetDimensionComponentsEnabled(Layout.hasCustomBehavior(layout));
			uiSetEditorComponentsEnabled(true);

			uiSetTitle(String.format("Face %s (%d)", face.getIndex(), face.getIndex().asInt()));

			Rectangle rect = currentFace.getRect();

			int horizontalCount = currentFace.getHorizontalCount();
			int verticalCount = currentFace.getVerticalCount();

			String path = currentFace.getImageFilePath();

			boolean resamplingAllowed = currentFace.isResamplingAllowed();
			boolean aspectRatioPreserved = currentFace.isAspectRatioPreserved();

			AspectHint aspectHint = currentFace.getAspectHint();

			int offsetX = 0;
			int offsetY = 0;

			if (resamplingAllowed && aspectRatioPreserved)
			{
				if (aspectHint == AspectHint.FIT)
				{
					Point offset = face.getFitOffset();
					offsetX = offset.x;
					offsetY = offset.y;
				}
				else if (aspectHint == AspectHint.CROP)
				{
					Point offset = face.getCropOffset();
					offsetX = offset.x;
					offsetY = offset.y;
				}
			}

			// Ugly hack.
			//
			// Ultimately the issue is generating multiple unneeded repaint() calls on TemplateView and FaceView.
			//
			// There are many ways to address this, including:
			// - Just accept multiple repaint() calls as they are merged into one.
			// - Just repaint again anyway since the performance is fine if resampling is cached.
			// - Component models that maps to a centralized model that handles only changes and fire change events
			// that would be ultimately handled by TemplateView and FaceView, decoupling the repaint() calls.
			//
			// To be addressed on future revisions.
			// Refactoring will probably follow the centralized model route if nothing else comes up.
			isOpening = true;
			removeFaceEditorListeners();

			parent.spFaceX.setValue(rect.x);
			parent.spFaceY.setValue(rect.y);
			parent.spFaceWidth.setValue(rect.width);
			parent.spFaceHeight.setValue(rect.height);

			parent.spFaceHorizontalCount.setValue(horizontalCount);
			parent.spFaceVerticalCount.setValue(verticalCount);

			parent.txtFaceImageSource.setText(path);

			parent.cbAllowResampling.setSelected(resamplingAllowed);
			parent.cbPreserveAspectRatio.setSelected(aspectRatioPreserved);

			parent.comboAspectRatioHint.setSelectedItem(aspectHint);

			parent.spOffsetX.setValue(offsetX);
			parent.spOffsetY.setValue(offsetY);

			uiUpdateResamplingComponents();

			isOpening = false;
			addFaceEditorListeners();

			parent.customFaceView.setFace(currentFace);
			parent.customFaceView.repaintContent();

			updateInfo();

			templateNode.updateCurrentFace();

			parent.updateMinimumSize();
		}
		else
		{
			close();
		}
	}

	void openFirstAvailable() {
		TemplateCube cube = parent.getTemplateCube();
		open(cube.getEnabledFaces().findFirst().orElse(null));
	}

	void refreshCurrent() {
		if (hasValidFace() && currentFace.isEnabled())
		{
			open(currentFace);
		}
	}

	void close() {
		TemplateUINode templateNode = parent.getTemplateNode();

		currentFace = null;

		uiSetTitle("No face selected");

		uiSetDimensionComponentsEnabled(false);
		uiSetEditorComponentsEnabled(false);

		parent.customFaceView.setFace(null);
		parent.customFaceView.repaintContent();

		updateInfo();

		templateNode.updateCurrentFace();
	}

	private void updateLocation(ChangeEvent e) {
		LayoutUINode layoutNode = parent.getLayoutNode();
		Layout layout = layoutNode.getLayout();

		assert hasValidFace() : "updateLocation called with editor closed.";
		assert Layout.hasCustomBehavior(layout) : "updateLocation called with non-custom layout.";

		assert !isOpening : "updateLocation called while editor is opening";

		if (!isOpening)
		{
			TemplateUINode templateNode = parent.getTemplateNode();
			FaceInfoUINode faceInfoNode = parent.getFaceInfoNode();
			StateValidatorUINode stateValidator = parent.getStateValidator();

			templateNode.repaintFace(currentFace.getIndex());
			currentFace.setX((int) parent.spFaceX.getValue());
			currentFace.setY((int) parent.spFaceY.getValue());
			templateNode.repaintFace(currentFace.getIndex());

			faceInfoNode.updateInfo();
			stateValidator.update();
		}
	}

	private void updateSize(ChangeEvent e) {
		LayoutUINode layoutNode = parent.getLayoutNode();
		Layout layout = layoutNode.getLayout();

		assert hasValidFace() : "updateSize called with editor closed.";
		assert Layout.hasCustomBehavior(layout) : "updateSize called with non-custom layout.";

		assert !isOpening : "updateSize called while editor is opening";

		if (!isOpening)
		{
			TemplateUINode templateNode = parent.getTemplateNode();
			FaceInfoUINode faceInfoNode = parent.getFaceInfoNode();
			StateValidatorUINode stateValidator = parent.getStateValidator();

			templateNode.repaintFace(currentFace.getIndex());
			currentFace.setWidth((int) parent.spFaceWidth.getValue());
			currentFace.setHeight((int) parent.spFaceHeight.getValue());
			templateNode.repaintFace(currentFace.getIndex());

			uiUpdateOffset(null);
			parent.customFaceView.repaintContent();

			updateInfo();

			faceInfoNode.updateInfo();
			stateValidator.update();
		}
	}

	private void updateBlockCount(ChangeEvent e) {
		assert hasValidFace() : "updateBlockCount called with editor closed.";
		assert !isOpening : "updateBlockCount called while editor is opening";

		if (!isOpening)
		{
			FaceInfoUINode faceInfoNode = parent.getFaceInfoNode();
			StateValidatorUINode stateValidator = parent.getStateValidator();

			currentFace.setHorizontalCount((int) parent.spFaceHorizontalCount.getValue());
			currentFace.setVerticalCount((int) parent.spFaceVerticalCount.getValue());

			uiUpdateOffset(null);
			parent.customFaceView.repaintContent();

			updateInfo();

			faceInfoNode.updateInfo();
			stateValidator.update();
		}
	}

	private void updateResamplingOptions(ItemEvent itemEvent) {
		assert hasValidFace() : "updateResamplingOptions called with editor closed.";
		assert !isOpening : "updateResamplingOptions called while editor is opening";

		if (!isOpening)
		{
			int stateChange = itemEvent.getStateChange();

			if (stateChange == ItemEvent.SELECTED || stateChange == ItemEvent.DESELECTED)
			{
				StateValidatorUINode stateValidator = parent.getStateValidator();

				boolean resamplingAllowed = parent.cbAllowResampling.isSelected();
				boolean aspectRatioPreserved = parent.cbPreserveAspectRatio.isSelected();

				AspectHint aspectHint = (AspectHint) parent.comboAspectRatioHint.getSelectedItem();

				currentFace.setResamplingAllowed(resamplingAllowed);
				currentFace.setAspectRatioPreserved(aspectRatioPreserved);
				currentFace.setAspectHint(aspectHint);

				uiUpdateResamplingComponents();

				uiUpdateOffset(null);
				parent.customFaceView.repaintContent();

				updateInfo();

				stateValidator.update();
			}
		}
	}

	private void updateOffset(ChangeEvent e) {
		assert hasValidFace() : "updateOffset called with editor closed.";
		assert !isOpening : "updateOffset called while editor is opening";

		if (!isOpening && currentFace.isResamplingAllowed() && currentFace.isAspectRatioPreserved())
		{
			StateValidatorUINode stateValidator = parent.getStateValidator();

			Point offset = new Point();
			offset.x = (int) parent.spOffsetX.getValue();
			offset.y = (int) parent.spOffsetY.getValue();

			if (currentFace.getAspectHint() == AspectHint.FIT)
			{
				offset = currentFace.updateFitOffset(offset);
				parent.spOffsetX.setValue(offset.x);
				parent.spOffsetY.setValue(offset.y);
			}
			else if (currentFace.getAspectHint() == AspectHint.CROP)
			{
				offset = currentFace.updateCropOffset(offset);
				parent.spOffsetX.setValue(offset.x);
				parent.spOffsetY.setValue(offset.y);
			}

			if (!parent.customFaceView.isDragging())
			{
				parent.customFaceView.repaintContent();
			}

			updateInfo();

			stateValidator.update();
		}
	}

	private void uiUpdateOffset(Point ignored) {
		assert hasValidFace() : "callbackOffsetUpdated called with editor closed.";
		assert !isOpening : "callbackOffsetUpdated called while editor is opening";
		//assert currentFace.isResamplingAllowed() : "callbackOffsetUpdated with resampling disabled";
		//assert currentFace.isAspectRatioPreserved() : "callbackOffsetUpdated with aspect ratio not preserved";

		if (!isOpening && currentFace.isResamplingAllowed() && currentFace.isAspectRatioPreserved())
		{
			Point offset = new Point();

			if (currentFace.getAspectHint() == AspectHint.FIT)
			{
				offset = currentFace.getFitOffset();
			}
			else if (currentFace.getAspectHint() == AspectHint.CROP)
			{
				offset = currentFace.getCropOffset();
			}

			parent.spOffsetX.setValue(offset.x);
			parent.spOffsetY.setValue(offset.y);
		}
	}

	private void openImageByPath(ActionEvent e) {
		assert !isOpening : "openImageByPath called while editor is opening";

		if (!isOpening)
		{
			loadImage(new File(parent.txtFaceImageSource.getText()));
		}
	}

	private void openImageByDialog(ActionEvent e) {
		assert !isOpening : "openImageByDialog called while editor is opening";

		if (!isOpening)
		{
			ImageChooser imageChooser;

			if (useImageChooserAWT)
			{
				imageChooser = new ImageChooserAWT();
			}
			else
			{
				imageChooser = new ImageChooserSwing();
			}

			String title = String.format("%s - Select a face image", MainWindow.getApplicationName());
			Optional<File> file = imageChooser.getImageFile(parent.getFrame(), title);

			if (file.isPresent())
			{
				parent.txtFaceImageSource.setText(file.get().getAbsolutePath());
				loadImage(file.get());
			}
		}
	}

	private void loadImage(File file) {
		assert hasValidFace() : "loadImage called with editor closed.";
		assert !isOpening : "loadImage called while editor is opening";

		if (!isOpening)
		{
			try
			{
				TemplateUINode templateNode = parent.getTemplateNode();
				FaceInfoUINode faceInfoNode = parent.getFaceInfoNode();

				currentFace.loadImageFrom(file);
				templateNode.repaintFace(currentFace.getIndex());

				parent.customFaceView.repaintContent();

				updateInfo();

				faceInfoNode.updateInfo();
			}
			catch (IOException exception)
			{
				// TODO: Notify user!
				exception.printStackTrace();
			}
		}
	}

	private void uiSetDimensionComponentsEnabled(boolean enabled) {
		uiDimensionComponents.forEach(component -> component.setEnabled(enabled));
	}

	private void uiSetEditorComponentsEnabled(boolean enabled) {
		uiEditorComponents.forEach(component -> component.setEnabled(enabled));
	}

	private void uiSetTitle(String text) {
		Border border = BorderFactory.createTitledBorder(text);
		parent.panelFaceEditor.setBorder(border);
	}

	private void uiUpdateResamplingComponents() {
		boolean resamplingAllowed = parent.cbAllowResampling.isSelected();
		boolean aspectRatioPreserved = parent.cbPreserveAspectRatio.isSelected();

		parent.cbPreserveAspectRatio.setEnabled(resamplingAllowed);
		parent.comboAspectRatioHint.setEnabled(resamplingAllowed && aspectRatioPreserved);

		parent.labelOffset.setEnabled(resamplingAllowed && aspectRatioPreserved);
		parent.labelOffsetX.setEnabled(resamplingAllowed && aspectRatioPreserved);
		parent.spOffsetX.setEnabled(resamplingAllowed && aspectRatioPreserved);
		parent.labelOffsetY.setEnabled(resamplingAllowed && aspectRatioPreserved);
		parent.spOffsetY.setEnabled(resamplingAllowed && aspectRatioPreserved);
	}

	private void uiUpdateImageSize(ImageDimension size, boolean warning) {
		String info;

		if ((size.width > 0) && (size.height > 0))
		{
			info = String.format("Source: %dx%d (%s)", size.width, size.height, size.getAspectRatioAsString());
		}
		else
		{
			info = "Source: 0x0";
		}

		parent.labelFaceSourceInfo.setText(info);
		parent.labelFaceSourceInfo.setForeground(warning ? warningColor : uiGetDefaultLabelColor());
	}

	private void uiUpdateRequiredSize(ImageDimension size, boolean warning) {
		String info;

		if ((size.width > 0) && (size.height > 0))
		{
			info = String.format("Required: %dx%d (%s)", size.width, size.height, size.getAspectRatioAsString());
		}
		else
		{
			info = " ";
		}

		parent.labelFaceRequiredSize.setText(info);
		parent.labelFaceRequiredSize.setForeground(warning ? warningColor : uiGetDefaultLabelColor());
	}

	private void uiUpdateFinalSize(ImageDimension size, boolean warning) {
		String info;

		if ((size.width > 0) && (size.height > 0))
		{
			info = String.format("Resized: %dx%d (%s)", size.width, size.height, size.getAspectRatioAsString());
		}
		else
		{
			info = " ";
		}

		parent.labelFaceFinalSize.setText(info);
		parent.labelFaceFinalSize.setForeground(warning ? warningColor : uiGetDefaultLabelColor());
	}

	private Color uiGetDefaultLabelColor() {
		return parent.labelFaceSourceInfo.getForeground();
	}

	private void loadDefaults() {
		PropertyReader options = new DefaultsReader(this.getClass().getName());

		warningColor = options.getColor("warningColor").orElse(Color.RED);

		int minX = options.getInt("minX").orElse(0);
		int maxX = options.getInt("maxX").orElse(10240);
		int minY = options.getInt("minY").orElse(0);
		int maxY = options.getInt("maxY").orElse(10240);

		int minWidth = options.getInt("minWidth").orElse(1);
		int maxWidth = options.getInt("maxWidth").orElse(10240);
		int minHeight = options.getInt("minHeight").orElse(1);
		int maxHeight = options.getInt("maxHeight").orElse(10240);

		int minHCount = options.getInt("minHorizontalCount").orElse(0);
		int maxHCount = options.getInt("maxHorizontalCount").orElse(25);

		int minVCount = options.getInt("minHorizontalCount").orElse(0);
		int maxVCount = options.getInt("maxHorizontalCount").orElse(25);

		int minOffsetX = options.getInt("minOffsetX").orElse(0);
		int maxOffsetX = options.getInt("maxOffsetX").orElse(10240);
		int minOffsetY = options.getInt("minOffsetY").orElse(0);
		int maxOffsetY = options.getInt("maxOffsetY").orElse(10240);

		useImageChooserAWT = "AWT".equalsIgnoreCase(options.getString("imageChooser").orElse("AWT"));

		parent.spFaceX.setModel(new SpinnerNumberModel(minX, minX, maxX, 1));
		parent.spFaceY.setModel(new SpinnerNumberModel(minX, minY, maxY, 1));

		parent.spFaceWidth.setModel(new SpinnerNumberModel(minWidth, minWidth, maxWidth, 1));
		parent.spFaceHeight.setModel(new SpinnerNumberModel(minHeight, minHeight, maxHeight, 1));

		parent.spFaceHorizontalCount.setModel(new SpinnerNumberModel(minHCount, minHCount, maxHCount, 1));
		parent.spFaceVerticalCount.setModel(new SpinnerNumberModel(minVCount, minVCount, maxVCount, 1));

		parent.spOffsetX.setModel(new SpinnerNumberModel(minOffsetX, minOffsetX, maxOffsetX, 1));
		parent.spOffsetY.setModel(new SpinnerNumberModel(minOffsetY, minOffsetY, maxOffsetY, 1));
	}
}
