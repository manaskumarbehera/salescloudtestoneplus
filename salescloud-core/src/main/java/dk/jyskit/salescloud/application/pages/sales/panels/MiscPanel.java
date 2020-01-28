package dk.jyskit.salescloud.application.pages.sales.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class MiscPanel extends Panel {
	public MiscPanel(String id, IModel<String> contentModel) {
		super(id);
		
		setOutputMarkupId(true);
		
		add(new Label("content", contentModel).setEscapeModelStrings(false));
	}
}
