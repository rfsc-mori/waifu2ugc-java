package waifu2ugc.gui.validators;

import waifu2ugc.settings.DefaultsReader;
import waifu2ugc.settings.PropertyReader;
import waifu2ugc.template.TemplateCube;
import waifu2ugc.template.TemplateFace;

import javax.swing.JFrame;

public class FaceBlockCount extends FaceStateValidator
{
	private int min;
	private int max;

	public FaceBlockCount(JFrame parentFrame, TemplateCube template) {
		super(parentFrame, template);
		loadDefaults();
	}

	@Override
	protected boolean validateTemplateFace(TemplateFace face) {
		int blockCount = face.getBlockCount();
		return (min <= blockCount) && (blockCount <= max);
	}

	@Override
	protected String getMessageFormat() {
		return String.format("The faces (%%s) have an invalid block count.\n" +
		                     "Accepted range: %d <= n <= %d.",
		                     min, max);
	}

	@Override
	protected String getTitle() {
		return "Invalid block count";
	}

	private void loadDefaults() {
		PropertyReader options = new DefaultsReader(this.getClass().getName());
		min = options.getInt("min").orElse(1);
		max = options.getInt("max").orElse(625);
	}
}
