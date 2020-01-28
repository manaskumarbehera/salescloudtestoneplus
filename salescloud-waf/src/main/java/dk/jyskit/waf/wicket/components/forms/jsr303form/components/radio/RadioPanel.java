package dk.jyskit.waf.wicket.components.forms.jsr303form.components.radio;

import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.Radio;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;

public class RadioPanel extends ComponentWithLabelAndValidationPanel<Radio> {
	public RadioPanel(ComponentContainerPanel container, String fieldName, final AjaxEventListener listener, Map<String, String> attributesMap) {
		super(container, fieldName);
		Radio radio = new Radio("editor", propertyModel);
		radio.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				listener.onAjaxEvent(target);
			}
		});
		radio.setOutputMarkupId(true);
		init(radio, attributesMap);
	}
}
