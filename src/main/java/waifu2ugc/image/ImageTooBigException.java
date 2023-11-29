package waifu2ugc.image;

public class ImageTooBigException extends Exception
{
	public ImageTooBigException() {
	}

	public ImageTooBigException(String message) {
		super(message);
	}

	public ImageTooBigException(String message, Throwable cause) {
		super(message, cause);
	}

	public ImageTooBigException(Throwable cause) {
		super(cause);
	}
}
