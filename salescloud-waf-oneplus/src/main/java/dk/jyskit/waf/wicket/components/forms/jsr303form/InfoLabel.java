package dk.jyskit.waf.wicket.components.forms.jsr303form;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class InfoLabel extends Panel {
	private static final long serialVersionUID = 1L;
	
	public static final String TYPE_INFO = "label-info";
	public static final String TYPE_IMPORTANT = "label-important";
	
	/**
	 * @param id
	 * @param model
	 * @see org.apache.wicket.Component#Component(String, IModel)
	 */
	public InfoLabel(final String id, IModel<?> model, String type) {
		super(id, model);
		Label label = new Label("text", model);
		label.add(AttributeModifier.append("class", type));
		add(label);
	}
}
