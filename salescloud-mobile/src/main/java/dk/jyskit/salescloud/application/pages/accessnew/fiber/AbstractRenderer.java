package dk.jyskit.salescloud.application.pages.accessnew.fiber;

import org.apache.wicket.markup.html.form.IChoiceRenderer;

public abstract class AbstractRenderer implements IChoiceRenderer {
	@Override
	public String getIdValue(Object object, int index) {
		return "" + index;
	}
}
