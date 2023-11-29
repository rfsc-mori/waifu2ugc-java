package waifu2ugc.gui;

import waifu2ugc.gui.view.FaceView;
import waifu2ugc.gui.view.TemplateView;
import waifu2ugc.image.AspectHint;
import waifu2ugc.image.ResamplingHint;
import waifu2ugc.layout.Layout;
import waifu2ugc.settings.DefaultsReader;
import waifu2ugc.settings.PropertyReader;
import waifu2ugc.template.TemplateCube;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;

public class MainWindow
{
	private JFrame mainWindowFrame;
	private JPanel mainPanel;

	TemplateView customTemplateView;
	JButton btnLoadTemplate;
	JComboBox<Layout> comboTemplateLayout;
	JCheckBox cbFront;
	JCheckBox cbTop;
	JCheckBox cbRight;
	JCheckBox cbBack;
	JCheckBox cbBottom;
	JCheckBox cbLeft;
	JLabel labelInfoFront;
	JLabel labelInfoTop;
	JLabel labelInfoRight;
	JLabel labelInfoBack;
	JLabel labelInfoBottom;
	JLabel labelInfoLeft;
	JLabel labelCountFront;
	JLabel labelCountTop;
	JLabel labelCountRight;
	JLabel labelCountBack;
	JLabel labelCountBottom;
	JLabel labelCountLeft;
	JButton btnEditFront;
	JButton btnEditTop;
	JButton btnEditRight;
	JButton btnEditBack;
	JButton btnEditBottom;
	JButton btnEditLeft;
	JPanel panelFaceEditor;
	JLabel labelPositionOnTemplate;
	JLabel labelFaceX;
	JSpinner spFaceX;
	JLabel labelFaceY;
	JSpinner spFaceY;
	JLabel labelFaceWidth;
	JSpinner spFaceWidth;
	JLabel labelFaceHeight;
	JSpinner spFaceHeight;
	JLabel labelNumberOfUGCBlocks;
	JLabel labelFaceHorizontalCount;
	JSpinner spFaceHorizontalCount;
	JLabel labelFaceVerticalCount;
	JSpinner spFaceVerticalCount;
	JLabel labelFaceImageSource;
	JTextField txtFaceImageSource;
	JButton btnFaceImageOpen;
	JCheckBox cbAllowResampling;
	JCheckBox cbPreserveAspectRatio;
	JComboBox<AspectHint> comboAspectRatioHint;
	JLabel labelOffset;
	JLabel labelOffsetX;
	JLabel labelOffsetY;
	JSpinner spOffsetX;
	JSpinner spOffsetY;
	FaceView customFaceView;
	JLabel labelFaceSourceInfo;
	JLabel labelFaceRequiredSize;
	JLabel labelFaceFinalSize;
	JTextField txtOutputDirectory;
	JButton btnOutputDirectoryBrowse;
	JButton btnExport;
	JButton btnValidationHelp;
	JComboBox<ResamplingHint> comboExportQuality;
	JProgressBar pbExport;
	JLabel labelProgress;

	private TemplateCube templateCube = new TemplateCube();

	private TemplateUINode templateNode = new TemplateUINode(this);
	private LayoutUINode layoutNode = new LayoutUINode(this);
	private FaceInfoUINode faceInfoNode = new FaceInfoUINode(this);
	private FaceEditorUINode faceEditor = new FaceEditorUINode(this);
	private OutputUINode outputNode = new OutputUINode(this);
	private StateValidatorUINode stateValidator = new StateValidatorUINode(this);

	private List<AbstractUINode> uiNodes = Arrays.asList(
			templateNode,
			layoutNode,
			faceInfoNode,
			faceEditor,
			outputNode,
			stateValidator
	);

	private MainWindow(JFrame frame) {
		mainWindowFrame = frame;

		setupFrame();
		showFrame();
	}

	public static String getApplicationName() { return "waifu2ugc"; }

	JFrame getFrame() { return mainWindowFrame; }

	TemplateCube getTemplateCube() { return templateCube; }

	LayoutUINode getLayoutNode() { return layoutNode; }
	TemplateUINode getTemplateNode() { return templateNode; }
	FaceInfoUINode getFaceInfoNode() { return faceInfoNode; }
	FaceEditorUINode getFaceEditor() { return faceEditor; }
	OutputUINode getOutputNode() { return outputNode; }
	StateValidatorUINode getStateValidator() { return stateValidator; }

	private void setupFrame() {
		mainWindowFrame.setContentPane(mainPanel);

		mainWindowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindowFrame.setResizable(true);
	}

	private void showFrame() {
		mainWindowFrame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowOpened(WindowEvent e) {
				uiNodes.forEach(AbstractUINode::windowOpened);
				uiNodes.forEach(AbstractUINode::setupListeners);

				updateMinimumSize();

				mainWindowFrame.pack();
				mainWindowFrame.setLocationRelativeTo(null);
			}
		});

		mainWindowFrame.setVisible(true);
	}

	void updateMinimumSize() {
		updateMinimumSize(mainWindowFrame);
	}

	private void updateMinimumSize(JFrame frame) {
		// Ugly hack.
		//
		// Ensures the user can't resize the window to the point it would clip the contents.
		//
		// This is ugly in many ways, specifically speaking:
		// - May produce inconsistent results because of timing and/or call order.
		// - Have to be explicitly done.
		// - Not intuitive to the reader.
		frame.setMinimumSize(frame.getLayout().minimumLayoutSize(frame));
	}

	public static void main(String[] args) {
		PropertyReader options = new DefaultsReader(MainWindow.class.getName());

		String systemLaF = UIManager.getSystemLookAndFeelClassName();
		String crossPlatformLaF = UIManager.getCrossPlatformLookAndFeelClassName();
		String nonSystemLaF = options.getString("nonSystemLaF").orElse("javax.swing.plaf.nimbus.NimbusLookAndFeel");

		boolean forceNonSystemLaF = options.getBoolean("forceNonSystemLaF").orElse(false);

		try
		{
			if (systemLaF.equals(crossPlatformLaF) || forceNonSystemLaF)
			{
				UIManager.setLookAndFeel(nonSystemLaF);
			}
			else
			{
				UIManager.setLookAndFeel(systemLaF);
			}
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			// Handled: Ignore and continue.
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(MainWindow::launch);
	}

	private static void launch() {
		new MainWindow(new JFrame(MainWindow.getApplicationName()));
	}
}
