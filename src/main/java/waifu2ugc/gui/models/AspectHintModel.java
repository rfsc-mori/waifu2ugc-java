package waifu2ugc.gui.models;

import waifu2ugc.image.AspectHint;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

public class AspectHintModel extends AbstractListModel<AspectHint> implements ComboBoxModel<AspectHint>
{
	private AspectHint currentHint;

	public AspectHint getCurrentHint() {
		return currentHint;
	}

	public void setCurrentHint(AspectHint hint) {
		setSelectedItem(hint);
	}

	@Override
	public void setSelectedItem(Object hint) {
		if (hint != null)
		{
			currentHint = (AspectHint) hint;
			fireContentsChanged(this, -1, -1);
		}
	}

	@Override
	public Object getSelectedItem() {
		return currentHint;
	}

	@Override
	public int getSize() {
		return AspectHint.values().length;
	}

	@Override
	public AspectHint getElementAt(int index) {
		return AspectHint.values()[index];
	}
}
