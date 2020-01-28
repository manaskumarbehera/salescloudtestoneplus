package dk.jyskit.salescloud.application.wafextension.forms;

import org.apache.wicket.markup.html.form.ChoiceRenderer;

public final class KeyPropChoiceRenderer extends ChoiceRenderer<Object> {
	public KeyPropChoiceRenderer(String displayProp) {
		super(displayProp, "key");
	}
}