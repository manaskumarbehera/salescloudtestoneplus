package dk.jyskit.waf.wicket.components.forms.jsr303form.components.imagefield;

import java.util.Map;

import org.apache.wicket.markup.html.form.FormComponentPanel;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;

public class ImageFieldPanel extends ComponentWithLabelAndValidationPanel<FormComponentPanel> {
	public ImageFieldPanel(ComponentContainerPanel container, String fieldName,
			Integer imageWidth, Integer imageHeight, Map<String, String> attributesMap) {
		super(container, fieldName);
		init(new ImageField("editor", propertyModel, imageWidth, imageHeight), attributesMap);
	}
}
