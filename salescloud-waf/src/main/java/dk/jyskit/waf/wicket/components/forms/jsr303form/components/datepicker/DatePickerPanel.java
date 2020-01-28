package dk.jyskit.waf.wicket.components.forms.jsr303form.components.datepicker;

import java.util.Map;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig.Day;
import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;

public class DatePickerPanel extends ComponentWithLabelAndValidationPanel<DateTextField> {
	public DatePickerPanel(ComponentContainerPanel container, final String fieldName, Map<String, String> attributesMap) {
		super(container, fieldName);
		
		// TODO: DOES NOT WORK WITH AJAX=TRUE IN THE JSR303FORM.
		
		// TODO: These are danish settings!
		DateTextFieldConfig config = new DateTextFieldConfig().withWeekStart(Day.Monday).withLanguage("da").withFormat("dd/MM/yyyy").allowKeyboardNavigation(true).autoClose(true);
		
		init(new DateTextField("editor", propertyModel, config) {
			@Override
			public String getInputName() {
				return fieldName;
			}
		}, attributesMap);
	}
}

