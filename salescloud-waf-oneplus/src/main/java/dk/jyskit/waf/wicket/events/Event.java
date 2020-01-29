package dk.jyskit.waf.wicket.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

public interface Event {
	public AjaxRequestTarget getTarget();
}
