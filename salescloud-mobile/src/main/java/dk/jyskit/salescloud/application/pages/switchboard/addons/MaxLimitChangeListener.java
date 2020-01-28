package dk.jyskit.salescloud.application.pages.switchboard.addons;

import org.apache.wicket.ajax.AjaxRequestTarget;

public interface MaxLimitChangeListener {
	void onChange(int newValue, AjaxRequestTarget target);
}
