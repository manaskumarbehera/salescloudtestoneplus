package dk.jyskit.salescloud.application.utils;

import org.apache.wicket.markup.html.form.IChoiceRenderer;

public class SimpleChoiceRenderer implements IChoiceRenderer {
	@Override
	public Object getDisplayValue(Object value) {
		return value;
	}

	@Override
	public String getIdValue(Object object, int index) {
		return "" + index;
	}
}
