package waifu2ugc.layout;

public class InvalidLayoutException extends Exception
{
	public InvalidLayoutException() {
	}

	public InvalidLayoutException(String message) {
		super(message);
	}

	public InvalidLayoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidLayoutException(Throwable cause) {
		super(cause);
	}
}
