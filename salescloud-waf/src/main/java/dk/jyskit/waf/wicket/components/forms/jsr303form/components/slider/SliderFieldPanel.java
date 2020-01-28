package dk.jyskit.waf.wicket.components.forms.jsr303form.components.slider;

import java.util.Map;

import org.apache.wicket.model.PropertyModel;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;

public class SliderFieldPanel extends ComponentWithLabelAndValidationPanel<SliderField> {
	public SliderFieldPanel(ComponentContainerPanel container, final String fieldName, int min, int max, int step, Map<String, String> attributesMap) {
		super(container, fieldName);
		init(new SliderField("editor", new PropertyModel(container.getBeanModel(), fieldName), Integer.valueOf(propertyModel.getObject().toString()), min, max, step), attributesMap);
	}
}

