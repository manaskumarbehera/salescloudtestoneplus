package dk.jyskit.waf.wicket.components.forms.jsr303form.exceptionhandling;


public class MessageContainsMatcher implements CauseMatcher {

	private final String contained;

	public MessageContainsMatcher(String contained) {
		this.contained = contained;
	}

	@Override
	public boolean matches(Throwable cause) {
		String msg = cause.getMessage();
		return msg != null && msg.contains(contained);
	}

	public String getContained() {
		return contained;
	}

}
