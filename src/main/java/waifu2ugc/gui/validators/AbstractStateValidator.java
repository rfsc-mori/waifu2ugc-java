package waifu2ugc.gui.validators;

import waifu2ugc.gui.MainWindow;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

abstract public class AbstractStateValidator
{
	protected JFrame parentFrame;

	protected AbstractStateValidator(JFrame parentFrame) {
		this.parentFrame = parentFrame;
	}

	abstract public boolean validate();

	abstract protected String getMessage();
	abstract protected String getTitle();

	public boolean validate(boolean alert) {
		boolean result = validate();

		if (!result)
		{
			if (isInteractive())
			{
				result = !alert || (showQuestion() == getQuestionResult());
			}
			else if (alert)
			{
				showMessage();
			}
		}

		return result;
	}

	public boolean isInteractive() {
		return false;
	}

	public boolean isNotInteractive() {
		return !isInteractive();
	}

	protected String getTitlePrefix() {
		return MainWindow.getApplicationName();
	}

	protected int getMessageType() {
		return JOptionPane.INFORMATION_MESSAGE;
	}

	protected void showMessage() {
		String title = String.format("%s - %s", getTitlePrefix(), getTitle());
		String message = getMessage();
		int type = getMessageType();

		JOptionPane.showMessageDialog(parentFrame, message, title, type);
	}

	protected int getQuestionType() {
		return JOptionPane.YES_NO_OPTION;
	}

	protected int getQuestionResult() {
		return JOptionPane.YES_OPTION;
	}

	protected int showQuestion() {
		String title = String.format("%s - %s", getTitlePrefix(), getTitle());
		String message = getMessage();
		int type = getQuestionType();

		return JOptionPane.showConfirmDialog(parentFrame, message, title, type);
	}
}
