package dk.jyskit.waf.wicket.components.containers;

import org.apache.wicket.markup.html.WebMarkupContainer;

public class AjaxContainer extends WebMarkupContainer {

	public AjaxContainer(String id) {
		super(id);
		setOutputMarkupId(true);
	}
	
	public AjaxContainer(String id, boolean visible) {
		this(id);
		if (!visible) {
			setOutputMarkupPlaceholderTag(true);
			setVisible(false);
		}
	}
}
