package waifu2ugc.layout;

import waifu2ugc.template.FaceIndex;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class LayoutLoader
{
	private static final Properties properties = new Properties();
	private static final String path = "/layouts.properties";

	private String defaultLayout;

	boolean load() throws IOException {
		loadFromClasspath();
		return !properties.isEmpty();
	}

	List<Layout> getLayouts() {
		defaultLayout = properties.getProperty("default");

		assert (defaultLayout != null) : "No default layout defined in layouts.properties.";
		// If == null: First layout, sorted by name, is picked as default by LayoutProvider.

		return properties.stringPropertyNames()
		                 .stream()
		                 .map(this::buildLayout)
		                 .filter(Optional::isPresent)
		                 .flatMap(layout -> layout.map(Stream::of).orElseGet(Stream::empty))
		                 .collect(Collectors.toList());
	}

	private Optional<Layout> buildLayout(String name) {
		Optional<Layout> layout;

		if ("default".equalsIgnoreCase(name) ||
		    name.contains(".") ||
		    name.isEmpty())
		{
			layout = Optional.empty();
		}
		else
		{
			try
			{
				LayoutBuilder builder = new LayoutBuilder(properties.getProperty(name));

				builder.setDefault(name.equalsIgnoreCase(defaultLayout));
				builder.setCustom("custom".equalsIgnoreCase(name));

				for (FaceIndex face : FaceIndex.faces())
				{
					readRect(name, face).ifPresent(rect -> builder.addFace(face, rect));
				}

				builder.setImagePath(properties.getProperty(name.concat(".").concat("image")));

				layout = Optional.of(builder.getLayout());
			}
			catch (InvalidLayoutException exception)
			{
				layout = Optional.empty();

				// Handled: Ignore current layout and continue.
				exception.printStackTrace();
			}
		}

		return layout;
	}

	private void loadFromClasspath() throws IOException {
		if (properties.isEmpty())
		{
			try (InputStream resource = LayoutLoader.class.getResourceAsStream(path))
			{
				assert (resource != null) : "resource == null.";
				properties.load(resource);
			}
		}
	}

	private Optional<Rectangle> readRect(String name, FaceIndex index) throws InvalidLayoutException {
		Optional<Rectangle> rect;

		String faceProperty = name.concat(".").concat(index.name());

		if (properties.containsKey(faceProperty))
		{
			int[] dimensions = Arrays.stream(properties.getProperty(faceProperty).split(","))
			                         .map(String::trim)
			                         .mapToInt(Integer::parseInt)
			                         .toArray();

			if (dimensions.length == 4)
			{
				rect = Optional.of(new Rectangle(dimensions[0], dimensions[1], dimensions[2], dimensions[3]));
			}
			else
			{
				String message = "Layout %s loaded with wrong rect format.";
				throw new InvalidLayoutException(String.format(message, name));
			}
		}
		else
		{
			rect = Optional.empty();
		}

		return rect;
	}
}
