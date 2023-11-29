package waifu2ugc.gui.models;

import waifu2ugc.image.ResamplingHint;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

public class ResamplingHintModel extends AbstractListModel<ResamplingHint> implements ComboBoxModel<ResamplingHint>
{
	private ResamplingHint currentFilter;

	public ResamplingHint getCurrentFilter() {
		return currentFilter;
	}

	public void setCurrentFilter(ResamplingHint filter) {
		setSelectedItem(filter);
	}

	@Override
	public void setSelectedItem(Object filter) {
		if (filter != null)
		{
			currentFilter = (ResamplingHint) filter;
			fireContentsChanged(this, -1, -1);
		}
	}

	@Override
	public Object getSelectedItem() {
		return currentFilter;
	}

	@Override
	public int getSize() {
		return ResamplingHint.values().length;
	}

	@Override
	public ResamplingHint getElementAt(int index) {
		return ResamplingHint.values()[index];
	}
}
