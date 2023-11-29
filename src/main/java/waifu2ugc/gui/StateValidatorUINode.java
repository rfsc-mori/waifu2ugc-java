package waifu2ugc.gui;

import waifu2ugc.gui.validators.AbstractStateValidator;
import waifu2ugc.gui.validators.EnabledFaces;
import waifu2ugc.gui.validators.FaceBlockCount;
import waifu2ugc.gui.validators.FaceImagePresent;
import waifu2ugc.gui.validators.FaceImageResampling;
import waifu2ugc.gui.validators.FaceImageSize;
import waifu2ugc.gui.validators.FaceRect;
import waifu2ugc.gui.validators.HighBlockCount;
import waifu2ugc.gui.validators.TemplateImagePresent;
import waifu2ugc.gui.validators.TemplateImageSize;
import waifu2ugc.template.TemplateCube;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

class StateValidatorUINode extends AbstractUINode
{
	private final List<JComponent> uiComponents;
	private final List<JComponent> uiHelpComponents;

	private List<AbstractStateValidator> stateValidators;

	StateValidatorUINode(MainWindow parent) {
		super(parent);

		uiComponents = Arrays.asList(
				parent.btnExport
		);

		uiHelpComponents = Arrays.asList(
				parent.btnValidationHelp
		);

		setupValidators();
	}

	private void setupValidators() {
		JFrame parentFrame = parent.getFrame();
		TemplateCube templateCube = parent.getTemplateCube();

		stateValidators = Arrays.asList(
				new TemplateImagePresent(parentFrame, templateCube),
				new TemplateImageSize(parentFrame, templateCube),
				new EnabledFaces(parentFrame, templateCube),
				new FaceBlockCount(parentFrame, templateCube),
				new FaceRect(parentFrame, templateCube),
				new FaceImagePresent(parentFrame, templateCube),
				new FaceImageSize(parentFrame, templateCube),
				new FaceImageResampling(parentFrame, templateCube),
				new HighBlockCount(parentFrame, templateCube)
		);
	}

	@Override
	void windowOpened() { }

	@Override
	void setupListeners() {
		parent.btnValidationHelp.addActionListener(this::showErrors);
	}

	void update() {
		uiUpdate(validate(false));
	}

	boolean validate(boolean alert) {
		boolean result = stateValidators.stream()
		                                .filter(AbstractStateValidator::isNotInteractive)
		                                .allMatch(validator -> validator.validate(alert));

		// Future revisions: Limit the amount of showConfirmDialog() exposed to user.
		result = result && stateValidators.stream()
		                                  .filter(AbstractStateValidator::isInteractive)
		                                  .allMatch(validator -> validator.validate(alert));

		return result;
	}

	void disableComponents() {
		uiComponents.forEach(component -> component.setEnabled(false));
		uiHelpComponents.forEach(component -> component.setEnabled(true));
	}

	private void showErrors(ActionEvent e) {
		OutputUINode outputNode = parent.getOutputNode();

		if (!outputNode.isExporting())
		{
			validate(true);
		}
		else
		{
			JOptionPane.showMessageDialog(parent.getFrame(),
			                              "The template is being exported, please wait.",
			                              "Busy",
			                              JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void uiUpdate(boolean state) {
		OutputUINode outputNode = parent.getOutputNode();

		uiComponents.forEach(component -> component.setEnabled(state && !outputNode.isExporting()));
		uiHelpComponents.forEach(component -> component.setEnabled(!state));
	}
}
