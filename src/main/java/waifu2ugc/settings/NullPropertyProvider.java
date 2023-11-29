package waifu2ugc.settings;

public class NullPropertyProvider implements PropertyProvider
{
	NullPropertyProvider() { }

	@Override
	public String getProperty(String key) {
		return null;
	}
}
