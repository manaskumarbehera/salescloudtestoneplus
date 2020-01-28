package dk.jyskit.waf.wicket.components.forms.jsr303form.exceptionhandling;


public class ExceptionTypeMatcher implements CauseMatcher {

	private final Class<? extends Throwable> type;

	public ExceptionTypeMatcher(Class<? extends Throwable> type) {
		this.type = type;
	}

	@Override
	public boolean matches(Throwable cause) {
		String msg = cause.getMessage();
		return msg != null && type.isAssignableFrom(cause.getClass());
	}

	public Class<? extends Throwable> getType() {
		return type;
	}

}
