package dk.jyskit.waf.application.model;

import org.apache.wicket.markup.html.form.IChoiceRenderer;

public class EntityStateChoiceRenderer implements IChoiceRenderer{
	@Override
	public Object getDisplayValue(Object object) {
		return ((EntityState)object).getDisplayModel().getObject();
	}

	@Override
	public String getIdValue(Object object, int index) {
		return "" + ((EntityState)object).getEntityState();
	}
}

	