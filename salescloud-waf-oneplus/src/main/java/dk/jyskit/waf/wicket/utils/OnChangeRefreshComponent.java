package dk.jyskit.waf.wicket.utils;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;

public final class OnChangeRefreshComponent extends OnChangeAjaxBehavior {
	private final Component component;

	public OnChangeRefreshComponent(Component component) {
		this.component = component;
		component.setOutputMarkupId(true);
	}

	@Override
	protected void onUpdate(AjaxRequestTarget target) {
		target.add(component);
	}
}