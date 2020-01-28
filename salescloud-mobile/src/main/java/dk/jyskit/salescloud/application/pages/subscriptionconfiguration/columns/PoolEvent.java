package dk.jyskit.salescloud.application.pages.subscriptionconfiguration.columns;

import lombok.Data;

import org.apache.wicket.ajax.AjaxRequestTarget;

import dk.jyskit.waf.wicket.events.AbstractEvent;

@Data
public class PoolEvent extends AbstractEvent {
	private boolean allUsed;

	public PoolEvent(AjaxRequestTarget target, boolean allUsed) {
		super(target);
		this.allUsed = allUsed;
	}
}
