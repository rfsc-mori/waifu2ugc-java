package waifu2ugc.settings;

import java.awt.Color;
import java.util.Objects;
import java.util.Optional;

public class PropertyReader
{
	protected final PropertyProvider provider;

	public PropertyReader(PropertyProvider provider) {
		this.provider = Objects.requireNonNull(provider);
	}

	public Optional<String> getString(String key) {
		String value = getProperty(key);
		return (value != null) ? Optional.of(value) : Optional.empty();
	}

	public Optional<Integer> getInt(String key) {
		String value = getProperty(key);
		return (value != null) ? Optional.of(Integer.parseInt(value)) : Optional.empty();
	}

	public Optional<Boolean> getBoolean(String key) {
		String value = getProperty(key);
		return (value != null) ? Optional.of(Boolean.parseBoolean(value)) : Optional.empty();
	}

	public Optional<Color> getColor(String key) {
		String value = getProperty(key);
		return (value != null) ? Optional.of(Color.decode(value)) : Optional.empty();
	}

	public Optional<Float> getFloat(String key) {
		String value = getProperty(key);
		return (value != null) ? Optional.of(Float.parseFloat(value)) : Optional.empty();
	}

	protected String getProperty(String key) {
		return provider.getProperty(key);
	}
}
