package waifu2ugc.image;

import com.twelvemonkeys.image.ResampleOp;

import waifu2ugc.settings.DefaultsReader;
import waifu2ugc.settings.PropertyReader;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public enum ResamplingHint
{
	BLACKMAN_SINC(ResampleOp.FILTER_BLACKMAN_SINC, "Blackman-Sinc (Highest)"),
	BLACKMAN_BESSEL(ResampleOp.FILTER_BLACKMAN_BESSEL, "Blackman-Bessel"),
	LANCZOS(ResampleOp.FILTER_LANCZOS, "Lanczos (Recommended)"),
	MITCHELL(ResampleOp.FILTER_MITCHELL, "Mitchell"),
	CUBIC(ResampleOp.FILTER_CUBIC, "Cubic"),
	QUADRATIC(ResampleOp.FILTER_QUADRATIC, "Quadratic"),
	GAUSSIAN(ResampleOp.FILTER_GAUSSIAN, "Gaussian"),
	BLACKMAN(ResampleOp.FILTER_BLACKMAN, "Blackman"),
	HAMMING(ResampleOp.FILTER_HAMMING, "Hamming"),
	HANNING(ResampleOp.FILTER_HANNING, "Hanning"),
	HERMITE(ResampleOp.FILTER_HERMITE, "Hermite"),
	TRIANGLE(ResampleOp.FILTER_TRIANGLE, "Triangle (Acceptable)"),
	BOX(ResampleOp.FILTER_BOX, "Box"),
	POINT(ResampleOp.FILTER_POINT, "Point (Lowest)");

	private final int filter;
	private final String description;

	ResamplingHint(int filter, String description) {
		this.filter = filter;
		this.description = description;
	}

	public int asInt() {
		return filter;
	}

	public String getDescription() {
		return description;
	}

	public static ResamplingHint getRecommended() {
		PropertyReader options = new DefaultsReader(ResamplingHint.class.getName());
		return ResamplingHint.fromInt(options.getInt("recommended").orElse(ResampleOp.FILTER_LANCZOS));
	}

	public static ResamplingHint fromInt(int filter) {
		Optional<ResamplingHint> resamplingHint = Arrays.stream(ResamplingHint.values())
		                                                .filter(hint -> (hint.asInt() == filter))
		                                                .findFirst();

		if (!resamplingHint.isPresent())
		{
			throw new IllegalArgumentException("Filter not found.");
		}

		return resamplingHint.get();
	}

	public static Stream<ResamplingHint> stream() {
		return Arrays.stream(ResamplingHint.values());
	}

	@Override
	public String toString() {
		return String.format("%d: %s", filter, description);
	}
}
