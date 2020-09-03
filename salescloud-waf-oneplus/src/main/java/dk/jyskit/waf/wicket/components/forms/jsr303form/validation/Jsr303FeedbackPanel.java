package dk.jyskit.waf.wicket.components.forms.jsr303form.validation;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;

public class Jsr303FeedbackPanel extends ComponentFeedbackPanel {
	/**
	 * @see org.apache.wicket.Component#Component(String)
	 * 
	 * @param id
	 * @param component
	 */
	public Jsr303FeedbackPanel(final String id, Component component) {
		super(id, component);
	}
} 