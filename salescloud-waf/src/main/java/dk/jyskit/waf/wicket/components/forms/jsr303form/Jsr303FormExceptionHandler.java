package dk.jyskit.waf.wicket.components.forms.jsr303form;

import java.io.Serializable;

public interface Jsr303FormExceptionHandler extends Serializable {
	void onException(Exception e);
}
