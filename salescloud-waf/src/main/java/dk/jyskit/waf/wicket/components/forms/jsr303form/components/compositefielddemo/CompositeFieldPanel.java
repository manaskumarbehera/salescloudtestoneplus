package dk.jyskit.waf.wicket.components.forms.jsr303form.components.compositefielddemo;

import java.util.Map;

import org.apache.wicket.markup.html.form.FormComponentPanel;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;

public class CompositeFieldPanel extends ComponentWithLabelAndValidationPanel<FormComponentPanel> {
	public CompositeFieldPanel(ComponentContainerPanel container, String fieldName, 
			Map<String, String> attributesMap) {
		super(container, fieldName);
		init(new CompositeField("editor", propertyModel), attributesMap);
	}
}
