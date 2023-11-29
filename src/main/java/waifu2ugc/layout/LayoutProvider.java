package waifu2ugc.layout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class LayoutProvider
{
	private List<Layout> layouts;

	private Layout defaultLayout;
	private Layout customLayout;

	public int getLayoutCount() { return layouts.size(); }
	public Layout getLayoutByIndex(int i) { return layouts.get(i); }

	public Stream<Layout> getLayouts() { return layouts.stream(); }
	public Stream<String> getLayoutNames() { return layouts.stream().map(Layout::getName); }

	public Layout getDefaultLayout() { return defaultLayout; }
	public Layout getCustomLayout() { return customLayout; }

	public Layout getLayoutByName(String name) {
		return getLayouts().filter(layout -> layout.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public Stream<Layout> getLayoutsMatching(String regex) {
		return getLayouts().filter(layout -> layout.getName().matches(regex));
	}

	public boolean loadLayouts() throws IOException {
		LayoutLoader loader = new LayoutLoader();

		if (loader.load())
		{
			layouts = new ArrayList<>(loader.getLayouts());

			long defaultCount = layouts.stream().filter(Layout::isDefault).count();
			assert (defaultCount == 1) : "Default layout count is different than 1.";

			layouts.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));

			if (defaultCount < 1)
			{
				layouts.stream().findFirst().ifPresent(Layout::setDefault);
			}
			else if (defaultCount > 1)
			{
				layouts.stream()
				       .filter(Layout::isDefault)
				       .skip(1)
				       .forEach(Layout::unsetDefault);
			}

			defaultLayout = layouts.stream().filter(Layout::isDefault).findFirst().orElse(null);
			assert (defaultLayout != null) : "No default layout defined.";

			Optional<Layout> custom = layouts.stream().filter(Layout::isCustom).findFirst();

			if (custom.isPresent())
			{
				customLayout = custom.get();
			}
			else
			{
				LayoutBuilder builder = new LayoutBuilder("Custom");
				builder.setCustom();
				customLayout = builder.getLayout();
				layouts.add(customLayout);
			}

			assert (customLayout != null) : "No custom layout defined.";
		}
		else
		{
			layouts = null;
			defaultLayout = null;
			customLayout = null;
		}

		return (layouts != null) && !layouts.isEmpty() && (defaultLayout != null) && (customLayout != null);
	}

	public void ensureLayoutAvailability() {
		if (layouts == null)
		{
			LayoutBuilder builder = new LayoutBuilder("Custom");
			builder.setCustom();
			layouts = Collections.singletonList(defaultLayout = customLayout = builder.getLayout());
		}
	}
}
