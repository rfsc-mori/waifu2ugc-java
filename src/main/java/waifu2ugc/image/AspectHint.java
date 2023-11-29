package waifu2ugc.image;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public enum AspectHint
{
	FIT("Fit"),
	CROP("Crop");

	private final String alias;

	AspectHint(String alias) {
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	public static Stream<AspectHint> stream() {
		return Arrays.stream(AspectHint.values());
	}

	public static AspectHint fromAlias(String alias) {
		Optional<AspectHint> aspectHint = Arrays.stream(AspectHint.values())
		                                        .filter(hint -> (hint.getAlias().equalsIgnoreCase(alias)))
		                                        .findFirst();

		if (!aspectHint.isPresent())
		{
			throw new IllegalArgumentException("Alias not found.");
		}

		return aspectHint.get();
	}

	@Override
	public String toString() {
		return getAlias();
	}
}
