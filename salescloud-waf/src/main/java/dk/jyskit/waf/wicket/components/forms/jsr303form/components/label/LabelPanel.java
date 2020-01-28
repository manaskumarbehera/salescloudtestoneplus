package dk.jyskit.waf.wicket.components.forms.jsr303form.components.label;

import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;

public class LabelPanel extends ComponentWithLabelAndValidationPanel<Label> {
	public LabelPanel(ComponentContainerPanel container, final String fieldName, Map<String, String> attributesMap) {
		super(container, fieldName);
		init(new Label("editor", propertyModel), attributesMap);
	}
}

