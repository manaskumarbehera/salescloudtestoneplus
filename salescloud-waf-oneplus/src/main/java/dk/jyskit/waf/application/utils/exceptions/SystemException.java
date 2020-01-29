package dk.jyskit.waf.application.utils.exceptions;

public class SystemException extends RuntimeException {
	private static final long serialVersionUID = 4049633512250160435L;
	
	public SystemException() {
	}

	public SystemException(String message) {
		super(message);
	}

	public SystemException(String message, Throwable cause) {
		super(message, cause);
	}

	public SystemException(Throwable cause) {
		super(cause);
	}
}