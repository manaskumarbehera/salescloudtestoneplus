package dk.jyskit.waf.wicket.components.forms.jsr303form.components.datetime;

import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.util.collections.MicroMap;

import com.googlecode.wicket.kendo.ui.form.datetime.TimePicker;

import dk.jyskit.waf.components.jquery.kendo.KendoBehavior;
import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;

public final class KendoTimePickerPanel extends AbstractDateTimePickerPanel<TimePicker> {
	public KendoTimePickerPanel(ComponentContainerPanel<?> container, final String fieldName) {
		this(container, fieldName, new MicroMap<String, String>());
	}

	@SuppressWarnings("unchecked")
	public KendoTimePickerPanel(ComponentContainerPanel<?> container, final String fieldName, Map<String, String> attributesMap) {
		super(container, fieldName);

		init(new TimePicker("editor", propertyModel, getTimeFormat()) {
			@Override
			public String getInputName() {
				return fieldName;
			}
		}, attributesMap);
		add(new KendoBehavior());
	}

}