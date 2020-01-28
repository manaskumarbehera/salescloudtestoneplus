package dk.jyskit.salescloud.application.pages.sales.panels;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class HelpPanel extends Panel {
	public HelpPanel(String id, IModel<String> contentModel) {
		super(id);
		setOutputMarkupId(true);
		add(getPreContentPanel("pre"));
		add(new Label("content", contentModel).setEscapeModelStrings(false));
		add(getPostContentPanel("post"));
	}

	protected Component getPreContentPanel(String wicketId) {
		return new EmptyPanel(wicketId);
	}

	protected Component getPostContentPanel(String wicketId) {
		return new EmptyPanel(wicketId);
	}
}
