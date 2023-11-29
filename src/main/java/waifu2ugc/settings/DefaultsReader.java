package waifu2ugc.settings;

public class DefaultsReader extends PropertyReader
{
	protected final String namespace;

	public DefaultsReader() {
		super(DefaultsProvider.getInstance());
		this.namespace = "";
	}

	public DefaultsReader(String namespace) {
		super(DefaultsProvider.getInstance());
		this.namespace = namespace;
	}

	@Override
	protected String getProperty(String key) {
		return super.getProperty(!namespace.isEmpty() ? namespace.concat(".").concat(key) : key);
	}
}
