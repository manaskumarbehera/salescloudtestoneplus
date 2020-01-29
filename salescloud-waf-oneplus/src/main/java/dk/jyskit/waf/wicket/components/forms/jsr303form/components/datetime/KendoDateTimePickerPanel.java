package dk.jyskit.waf.wicket.components.forms.jsr303form.components.datetime;

import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.util.collections.MicroMap;

import com.googlecode.wicket.kendo.ui.form.datetime.DateTimePicker;

import dk.jyskit.waf.components.jquery.kendo.KendoBehavior;
import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;

public final class KendoDateTimePickerPanel extends AbstractDateTimePickerPanel<DateTimePicker> {
	public KendoDateTimePickerPanel(ComponentContainerPanel<?> container, final String fieldName) {
		this(container, fieldName, new MicroMap<String, String>());
	}

	@SuppressWarnings("unchecked")
	public KendoDateTimePickerPanel(final ComponentContainerPanel<?> container, final String fieldName, Map<String, String> attributesMap) {
		super(container, fieldName);
		init(new DateTimePicker("editor", propertyModel, getDateFormat(), getTimeFormat()) {
			@Override
			public String getInputName() {
				return fieldName;
			}
		}, attributesMap);
		getComponent().add(AttributeModifier.append("class", "hide"));  // Add .hide {display: none} to your stylesheet
		add(new KendoBehavior());
	}

}