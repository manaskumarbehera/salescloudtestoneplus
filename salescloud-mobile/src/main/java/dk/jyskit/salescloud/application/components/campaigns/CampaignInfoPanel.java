package dk.jyskit.salescloud.application.components.campaigns;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class CampaignInfoPanel extends Panel {
	
	public CampaignInfoPanel(String wicketId, IModel<String> model)  {
		super(wicketId);
		add(new Label("text", model).setEscapeModelStrings(false));
	}

}
