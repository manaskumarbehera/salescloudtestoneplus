package dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;

public interface AjaxEventListener extends Serializable {
	public void onAjaxEvent(AjaxRequestTarget target);
}
