package waifu2ugc.gui;

abstract class AbstractUINode
{
	protected final MainWindow parent;

	AbstractUINode(MainWindow parent) {
		this.parent = parent;
	}

	MainWindow getParent() {
		return parent;
	}

	abstract void windowOpened();

	abstract void setupListeners();
}
