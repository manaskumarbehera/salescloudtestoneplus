package dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;

public interface AjaxSubmitListener extends Serializable {
	public void onSubmit(AjaxRequestTarget target);
}
