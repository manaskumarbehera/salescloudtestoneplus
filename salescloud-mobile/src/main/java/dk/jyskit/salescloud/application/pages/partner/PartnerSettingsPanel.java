package dk.jyskit.salescloud.application.pages.partner;

import org.apache.wicket.markup.html.panel.Panel;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;

public class PartnerSettingsPanel extends Panel {
	private static final long serialVersionUID = 1L;
	
	public PartnerSettingsPanel(String id, final NotificationPanel notificationPanel) {
		super(id);
		
		add(new PartnerSettingsTabPanel("tabPanel"));
	}
}
