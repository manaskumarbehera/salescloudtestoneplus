package dk.jyskit.waf.wicket.components.forms.jsr303form.components.checkbox;

import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.form.CheckBox;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;

public class CheckBoxPanel extends ComponentWithLabelAndValidationPanel<CheckBox> {
	public CheckBoxPanel(ComponentContainerPanel container, String fieldName, final AjaxEventListener listener, Map<String, String> attributesMap) {
		super(container, fieldName);
		AjaxCheckBox ajaxCheckBox = new AjaxCheckBox("editor", propertyModel) {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				listener.onAjaxEvent(target);
			}
		};
		ajaxCheckBox.setOutputMarkupId(true);
		String cssId = ajaxCheckBox.getMarkupId();
		init(ajaxCheckBox, attributesMap);
	}
	
	public CheckBoxPanel(ComponentContainerPanel container, String fieldName, Map<String, String> attributesMap) {
		super(container, fieldName);
		init(new CheckBox("editor", propertyModel), attributesMap);
	}
}
