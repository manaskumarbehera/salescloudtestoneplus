package dk.jyskit.waf.application.utils.exceptions;

public class UserErrorException extends RuntimeException {
	private static final long serialVersionUID = 3834875762963855154L;

	public UserErrorException() {
	}

	public UserErrorException(String message) {
		super(message);
	}

	public UserErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserErrorException(Throwable cause) {
		super(cause);
	}
}