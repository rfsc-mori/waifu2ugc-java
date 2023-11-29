package waifu2ugc.gui;

import waifu2ugc.layout.Layout;
import waifu2ugc.template.TemplateCube;
import waifu2ugc.template.TemplateFace;
import waifu2ugc.template.FaceIndex;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.Map;

class FaceInfoUINode extends AbstractUINode
{
	private final Map<FaceIndex, JCheckBox> uiCheckBoxes = new HashMap<>();
	private final Map<FaceIndex, JLabel> uiInfoLabels = new HashMap<>();
	private final Map<FaceIndex, JLabel> uiCountLabels = new HashMap<>();
	private final Map<FaceIndex, JButton> uiEditButtons = new HashMap<>();

	FaceInfoUINode(MainWindow parent) {
		super(parent);

		uiCheckBoxes.put(FaceIndex.FRONT, parent.cbFront);
		uiCheckBoxes.put(FaceIndex.TOP, parent.cbTop);
		uiCheckBoxes.put(FaceIndex.RIGHT, parent.cbRight);
		uiCheckBoxes.put(FaceIndex.BACK, parent.cbBack);
		uiCheckBoxes.put(FaceIndex.BOTTOM, parent.cbBottom);
		uiCheckBoxes.put(FaceIndex.LEFT, parent.cbLeft);

		uiInfoLabels.put(FaceIndex.FRONT, parent.labelInfoFront);
		uiInfoLabels.put(FaceIndex.TOP, parent.labelInfoTop);
		uiInfoLabels.put(FaceIndex.RIGHT, parent.labelInfoRight);
		uiInfoLabels.put(FaceIndex.BACK, parent.labelInfoBack);
		uiInfoLabels.put(FaceIndex.BOTTOM, parent.labelInfoBottom);
		uiInfoLabels.put(FaceIndex.LEFT, parent.labelInfoLeft);

		uiCountLabels.put(FaceIndex.FRONT, parent.labelCountFront);
		uiCountLabels.put(FaceIndex.TOP, parent.labelCountTop);
		uiCountLabels.put(FaceIndex.RIGHT, parent.labelCountRight);
		uiCountLabels.put(FaceIndex.BACK, parent.labelCountBack);
		uiCountLabels.put(FaceIndex.BOTTOM, parent.labelCountBottom);
		uiCountLabels.put(FaceIndex.LEFT, parent.labelCountLeft);

		uiEditButtons.put(FaceIndex.FRONT, parent.btnEditFront);
		uiEditButtons.put(FaceIndex.TOP, parent.btnEditTop);
		uiEditButtons.put(FaceIndex.RIGHT, parent.btnEditRight);
		uiEditButtons.put(FaceIndex.BACK, parent.btnEditBack);
		uiEditButtons.put(FaceIndex.BOTTOM, parent.btnEditBottom);
		uiEditButtons.put(FaceIndex.LEFT, parent.btnEditLeft);
	}

	@Override
	void windowOpened() { }

	@Override
	void setupListeners() {
		FaceEditorUINode faceEditor = parent.getFaceEditor();

		uiCheckBoxes.forEach((face, checkBox) -> {
			checkBox.addItemListener(itemEvent -> {
				int stateChange = itemEvent.getStateChange();

				if (stateChange == ItemEvent.SELECTED)
				{
					setFaceEnabled(face, true);
				}
				else if (stateChange == ItemEvent.DESELECTED)
				{
					setFaceEnabled(face, false);
				}
			});
		});

		uiEditButtons.forEach((face, button) -> button.addActionListener(e -> faceEditor.open(face)));
	}

	void updateInfo() {
		TemplateCube cube = parent.getTemplateCube();

		cube.getFaces().forEach(face -> {
			uiUpdateFaceRect(face.getIndex(), face.getRect());
			uiUpdateFaceCount(face.getIndex(), face.getBlockCountAsDimension());
		});

		parent.updateMinimumSize();
	}

	void updateView() {
		LayoutUINode layoutNode = parent.getLayoutNode();
		Layout layout = layoutNode.getLayout();

		uiSetComponentsEnabled(layout, true);
	}

	void commitView() {
		TemplateCube cube = parent.getTemplateCube();
		LayoutUINode layoutNode = parent.getLayoutNode();
		Layout layout = layoutNode.getLayout();

		cube.getFaces().forEach(face -> {
			boolean isSelected = uiCheckBoxes.get(face.getIndex()).isSelected();
			boolean isLayoutFace = Layout.hasFace(layout, face.getIndex());

			face.setEnabled(isSelected && isLayoutFace);
		});
	}

	private void setFaceEnabled(FaceIndex index, boolean enabled) {
		TemplateCube cube = parent.getTemplateCube();
		StateValidatorUINode stateValidator = parent.getStateValidator();

		TemplateFace face = cube.getFace(index);
		TemplateUINode templateNode = parent.getTemplateNode();
		FaceEditorUINode faceEditor = parent.getFaceEditor();

		updateView();

		face.setEnabled(enabled);
		templateNode.repaintFace(face.getIndex());

		if (!faceEditor.hasValidFace() || !faceEditor.getFace().isEnabled())
		{
			faceEditor.openFirstAvailable();
		}

		stateValidator.update();
	}

	private void uiSetComponentsEnabled(Layout layout, boolean enabled) {
		FaceIndex.stream().forEach(index -> {
			boolean state = enabled && Layout.hasFace(layout, index);

			uiCheckBoxes.get(index).setEnabled(state);
			uiInfoLabels.get(index).setEnabled(state);
			uiCountLabels.get(index).setEnabled(state);

			uiEditButtons.get(index).setEnabled(state && uiCheckBoxes.get(index).isSelected());
		});
	}

	private void uiUpdateFaceRect(FaceIndex index, Rectangle rect) {
		String info = String.format("x:%d, y:%d, w:%d, h:%d", rect.x, rect.y, rect.width, rect.height);
		uiInfoLabels.get(index).setText(info);
	}

	private void uiUpdateFaceCount(FaceIndex index, Dimension size) {
		String info = String.format("%dx%d", size.width, size.height);
		uiCountLabels.get(index).setText(info);
	}
}
