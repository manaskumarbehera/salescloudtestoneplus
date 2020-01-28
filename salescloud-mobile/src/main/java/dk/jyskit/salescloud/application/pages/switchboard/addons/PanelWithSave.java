package dk.jyskit.salescloud.application.pages.switchboard.addons;

import org.apache.wicket.markup.html.panel.Panel;

public abstract class PanelWithSave extends Panel {
	public PanelWithSave(String id) {
		super(id);
	}

	abstract boolean save();
}
