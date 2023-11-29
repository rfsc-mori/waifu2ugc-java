package waifu2ugc.layout;

import waifu2ugc.image.ImageWrapper;
import waifu2ugc.template.FaceIndex;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class Layout
{
	private final String name;

	private final boolean isCustom;
	private boolean isDefault;

	private final Map<FaceIndex, Rectangle> faces;

	private final String imagePath;

	Layout(String name, boolean isCustom, boolean isDefault, Map<FaceIndex, Rectangle> faces, String imagePath) {
		this.name = name;
		this.isCustom = isCustom;
		this.isDefault = isDefault;
		this.faces = faces;
		this.imagePath = imagePath;
	}

	public String getName() { return name; }

	public boolean isCustom() { return isCustom; }
	public boolean isDefault() { return isDefault; }

	// For LayoutProvider's bad property file error handling
	void setDefault() { isDefault = true; }
	void unsetDefault() { isDefault = false; }

	public boolean hasFace(FaceIndex index) { return (isCustom || faces.containsKey(index)); }

	public String getImagePath() { return imagePath; }

	public Optional<ImageWrapper> readImage() {
		Optional<ImageWrapper> image;

		if ((imagePath != null) && !imagePath.isEmpty())
		{
			try (InputStream resource = LayoutLoader.class.getResourceAsStream(imagePath))
			{
				assert (resource != null) : "resource == null.";
				image = Optional.of(new ImageWrapper(resource));
			}
			catch (IOException e)
			{
				image = Optional.empty();

				// Handled: Ignore and continue.
				e.printStackTrace();
			}
		}
		else
		{
			image = Optional.empty();
		}

		return image;
	}

	public static boolean hasCustomBehavior(Layout layout) {
		return (layout == null) || layout.isCustom();
	}

	public static boolean hasFace(Layout layout, FaceIndex index) {
		return (layout != null) && layout.hasFace(index);
	}

	public Stream<Map.Entry<FaceIndex, Rectangle>> getFaces() { return faces.entrySet().stream(); }

	@Override
	public String toString() {
		return getName();
	}
}
