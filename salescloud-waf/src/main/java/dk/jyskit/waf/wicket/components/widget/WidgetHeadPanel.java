package dk.jyskit.waf.wicket.components.widget;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

public class WidgetHeadPanel extends GenericPanel<String> {

	public WidgetHeadPanel(String id, IModel<String> model) {
		super(id, model);
		add(new Label("headLabel", model));
	}

}
