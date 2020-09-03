package dk.jyskit.waf.wicket.components.forms.jsr303form.components.moneyfield;

import java.util.Map;

import org.apache.wicket.markup.html.form.TextField;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;
import dk.jyskit.waf.wicket.components.money.BigDecimalMoneyValidator;
import dk.jyskit.waf.wicket.components.money.DefaultMoneyModel;

public class MoneyFieldPanel extends ComponentWithLabelAndValidationPanel<TextField> {
	public MoneyFieldPanel(ComponentContainerPanel container, final String fieldName, Map<String, String> attributesMap) {
		super(container, fieldName);
		
		// TODO: Consider using this: https://github.com/inventiLT/inventi-wicket/tree/master/inventi-wicket-numeric
		
		TextField<String> moneyField = new TextField<String>("editor", new DefaultMoneyModel(propertyModel)) {
			@Override
			public String getInputName() {
				return fieldName;
			}
		};
		moneyField.add(new BigDecimalMoneyValidator());
		
		init(moneyField, attributesMap);
	}
}
