package dk.jyskit.waf.wicket.components.forms.jsr303form.components.readonly;

import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;

public class ReadonlyPanel extends ComponentWithLabelAndValidationPanel<Label> {
	public ReadonlyPanel(ComponentContainerPanel container, final String fieldName, Map<String, String> attributesMap) {
		super(container, fieldName);
		init(new Label("editor", propertyModel), attributesMap);
	}
}

