package waifu2ugc.settings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// Future revisions:
// Refactor defaults.properties file and its providers/readers.
// Also split it into more specific roles than "defaults" (view, validators, internal).
public class DefaultsProvider implements PropertyProvider
{
	private static final DefaultsProvider instance = new DefaultsProvider();

	private static final Properties properties = new Properties();
	private static final String path = "/defaults.properties";

	private DefaultsProvider() { }

	public static PropertyProvider getInstance() {
		PropertyProvider loader;

		if (!properties.isEmpty())
		{
			loader = instance;
		}
		else
		{
			try
			{
				instance.loadFromClasspath();
				loader = instance;
			}
			catch (IOException exception)
			{
				loader = new NullPropertyProvider();

				// Handled: Ignore and continue.
				exception.printStackTrace();
			}
		}

		return loader;
	}

	@Override
	public String getProperty(String key) {
		assert (properties.containsKey(key)) : "Default option not found: " + key;
		return properties.getProperty(key);
	}

	private void loadFromClasspath() throws IOException {
		try (InputStream resource = DefaultsProvider.class.getResourceAsStream(path))
		{
			assert (resource != null) : "resource == null.";
			properties.load(resource);
		}
	}
}
