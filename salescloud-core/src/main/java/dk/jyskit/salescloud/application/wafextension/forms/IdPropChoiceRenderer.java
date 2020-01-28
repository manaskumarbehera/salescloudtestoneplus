package dk.jyskit.salescloud.application.wafextension.forms;

import org.apache.wicket.markup.html.form.ChoiceRenderer;

public final class IdPropChoiceRenderer extends ChoiceRenderer<Object> {
	public IdPropChoiceRenderer(String displayProp) {
		super(displayProp, "id");
	}
}