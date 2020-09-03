package dk.jyskit.waf.wicket.components.forms.jsr303form.components.numbertextfield;

import java.util.Map;

import org.apache.wicket.markup.html.form.NumberTextField;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;

public class NumberTextFieldPanel extends ComponentWithLabelAndValidationPanel<NumberTextField> {
	public NumberTextFieldPanel(ComponentContainerPanel container, final String fieldName, Map<String, String> attributesMap) {
		super(container, fieldName);
		init(new NumberTextField("editor", propertyModel) {
			@Override
			public String getInputName() {
				return fieldName;  // To avoid a random name generated by Wicket
			}
		}, attributesMap);
	}
}
