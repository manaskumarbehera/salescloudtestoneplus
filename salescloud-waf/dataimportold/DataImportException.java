package dk.jyskit.waf.utils.dataimport;

public class DataImportException extends Exception {
	private static final long serialVersionUID = 1L;

	public DataImportException(String msg) {
		super(msg);
	}

	public DataImportException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
