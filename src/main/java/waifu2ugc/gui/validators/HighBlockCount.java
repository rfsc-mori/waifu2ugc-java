package waifu2ugc.gui.validators;

import waifu2ugc.settings.DefaultsReader;
import waifu2ugc.settings.PropertyReader;
import waifu2ugc.template.TemplateCube;

import javax.swing.JFrame;

public class HighBlockCount extends TemplateStateValidator
{
	private int recommended;

	private int count;

	public HighBlockCount(JFrame parentFrame, TemplateCube template) {
		super(parentFrame, template);
		loadDefaults();
	}

	@Override
	public boolean isInteractive() {
		return true;
	}

	@Override
	public boolean validate() {
		count = template.getTotalBlockCount();
		return (count <= recommended);
	}

	@Override
	protected String getMessage() {
		return String.format("This configuration generates a high block count.\n" +
		                     "Suggested range: n <= %d.\n" +
		                     "n = %d\n" +
		                     "\n" +
		                     "This may generate too many files which have to be imported manually into the game.\n" +
		                     "\n" +
		                     "Do you still wish to continue?",
		                     recommended, count);
	}

	@Override
	protected String getTitle() {
		return "High block count";
	}

	private void loadDefaults() {
		PropertyReader options = new DefaultsReader(this.getClass().getName());
		recommended = options.getInt("recommended").orElse(125);
	}
}
