package dk.jyskit.waf.wicket.components.forms.jsr303form.exceptionhandling;

import java.io.Serializable;

public interface CauseMatcher extends Serializable {

	public abstract boolean matches(Throwable cause);

}
