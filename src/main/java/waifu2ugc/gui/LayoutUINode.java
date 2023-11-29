package waifu2ugc.gui;

import waifu2ugc.gui.models.LayoutModel;
import waifu2ugc.layout.Layout;
import waifu2ugc.layout.LayoutProvider;
import waifu2ugc.template.TemplateCube;

import javax.swing.JComponent;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class LayoutUINode extends AbstractUINode
{
	private final List<JComponent> uiComponents;

	private final LayoutProvider layoutProvider = new LayoutProvider();

	LayoutUINode(MainWindow parent) {
		super(parent);

		uiComponents = Arrays.asList(
				parent.comboTemplateLayout
		);
	}

	@Override
	void windowOpened() {
		try
		{
			layoutProvider.loadLayouts();
		}
		catch (IOException exception)
		{
			// Handled: Ignore and continue.
			exception.printStackTrace();
		}
		finally
		{
			layoutProvider.ensureLayoutAvailability();
		}

		parent.comboTemplateLayout.setModel(new LayoutModel(layoutProvider));
		uiSelectDefaultLayout(true);
	}

	@Override
	void setupListeners() {
		parent.comboTemplateLayout.addItemListener(this::uiLayoutChanged);
	}

	Layout getLayout() { return (Layout) parent.comboTemplateLayout.getSelectedItem(); }

	Layout getDefaultLayout() { return layoutProvider.getDefaultLayout(); }

	private void layoutChanged() {
		Layout layout = getLayout();

		TemplateUINode templateNode = parent.getTemplateNode();
		FaceInfoUINode faceInfoNode = parent.getFaceInfoNode();
		FaceEditorUINode faceEditor = parent.getFaceEditor();
		StateValidatorUINode validator = parent.getStateValidator();

		if (layout != null)
		{
			layout.readImage().ifPresent(templateNode::loadTemplateImage);
			updateLayoutRects(layout);
		}

		faceInfoNode.commitView();

		faceInfoNode.updateInfo();
		faceInfoNode.updateView();

		faceEditor.refreshCurrent();

		validator.update();
	}

	private void updateLayoutRects(Layout layout) {
		TemplateCube cube = parent.getTemplateCube();
		TemplateUINode templateNode = parent.getTemplateNode();

		templateNode.repaintEnabledFaces();
		layout.getFaces().forEach(index_rect -> cube.getFace(index_rect.getKey()).setRect(index_rect.getValue()));
		templateNode.repaintEnabledFaces();
	}

	private void uiSelectDefaultLayout(boolean manualUpdate) {
		uiChangeLayout(layoutProvider.getDefaultLayout());

		if (manualUpdate)
		{
			layoutChanged();
		}
	}

	private void uiChangeLayout(Layout type) {
		parent.comboTemplateLayout.getModel().setSelectedItem(type);
	}

	private void uiLayoutChanged(ItemEvent itemEvent) {
		int stateChange = itemEvent.getStateChange();

		if (stateChange == ItemEvent.SELECTED)
		{
			layoutChanged();
		}
	}
}
