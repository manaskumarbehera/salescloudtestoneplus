package dk.jyskit.waf.utils.dataimport;

public class ColumnNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public ColumnNotFoundException(String msg) {
		super(msg);
	}

	public ColumnNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
