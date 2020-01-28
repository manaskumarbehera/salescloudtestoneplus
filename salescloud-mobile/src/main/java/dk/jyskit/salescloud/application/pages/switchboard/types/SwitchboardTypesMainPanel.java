package dk.jyskit.salescloud.application.pages.switchboard.types;

import org.apache.wicket.markup.html.panel.Panel;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;

public class SwitchboardTypesMainPanel extends Panel {

	private TypeSelectionPanel typeSelectionPanel;

	public SwitchboardTypesMainPanel(String wicketId, final NotificationPanel notificationPanel) {
		super(wicketId);
		typeSelectionPanel = new TypeSelectionPanel("typeSelection", notificationPanel, null);
		add(typeSelectionPanel);
	}

	public boolean save() {
		return typeSelectionPanel.save();
	}

}
