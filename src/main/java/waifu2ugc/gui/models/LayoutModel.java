package waifu2ugc.gui.models;

import waifu2ugc.layout.Layout;
import waifu2ugc.layout.LayoutProvider;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

public class LayoutModel extends AbstractListModel<Layout> implements ComboBoxModel<Layout>
{
	private final LayoutProvider provider;
	private Layout layout;

	public LayoutModel(LayoutProvider provider) {
		this.provider = provider;
	}

	public Layout getCurrentLayout() {
		return layout;
	}

	public void setCurrentLayout(Layout type) {
		setSelectedItem(type);
	}

	@Override
	public void setSelectedItem(Object layout) {
		if (layout != null)
		{
			this.layout = (Layout) layout;
			fireContentsChanged(this, -1, -1);
		}
	}

	@Override
	public Object getSelectedItem() {
		return layout;
	}

	@Override
	public int getSize() {
		return provider.getLayoutCount();
	}

	@Override
	public Layout getElementAt(int index) {
		return provider.getLayoutByIndex(index);
	}
}
