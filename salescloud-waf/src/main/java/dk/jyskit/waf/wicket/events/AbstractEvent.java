package dk.jyskit.waf.wicket.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class AbstractEvent implements Event {
	private final AjaxRequestTarget target;

	public AbstractEvent(AjaxRequestTarget target) {
		this.target = target;
	}

	public AjaxRequestTarget getTarget() {
		return target;
	}
}