package dk.jyskit.waf.wicket.components.forms.jsr303form.components.panel;

import org.apache.wicket.markup.html.panel.Panel;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;

public class PanelPanel extends Panel {
	public PanelPanel(ComponentContainerPanel container, Panel panel) {
		super("panel");
		add(panel);
	}
}

