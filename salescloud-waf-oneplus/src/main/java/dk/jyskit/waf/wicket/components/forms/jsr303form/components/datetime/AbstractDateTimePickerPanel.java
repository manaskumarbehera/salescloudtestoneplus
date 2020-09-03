package dk.jyskit.waf.wicket.components.forms.jsr303form.components.datetime;

import org.apache.wicket.markup.html.form.FormComponent;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;

public abstract class AbstractDateTimePickerPanel<T extends FormComponent<?>> extends ComponentWithLabelAndValidationPanel<T> {
	private String dataFormat = "dd-MM-yyyy";
	private String timeFormat = "HH:mm";

	public AbstractDateTimePickerPanel(ComponentContainerPanel<?> container, final String fieldName) {
		super(container, fieldName);
	}

	public String getTimeFormat() {
		return timeFormat;
	}

	public String getDateFormat() {
		return dataFormat;
	}

	public String getDataFormat() {
		return dataFormat;
	}

	public void setDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
	}

	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}

}