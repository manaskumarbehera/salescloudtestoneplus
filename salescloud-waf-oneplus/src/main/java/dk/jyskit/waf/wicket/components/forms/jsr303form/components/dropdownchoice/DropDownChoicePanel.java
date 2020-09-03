package dk.jyskit.waf.wicket.components.forms.jsr303form.components.dropdownchoice;

import java.util.List;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;

public class DropDownChoicePanel extends ComponentWithLabelAndValidationPanel<DropDownChoice> {
	public DropDownChoicePanel(final ComponentContainerPanel container, final String fieldName, List listOfChoices, IChoiceRenderer choiceRenderer, 
			Map<String, String> attributesMap) {
		super(container, fieldName);
		DropDownChoice dropDownChoice = null;
		if (choiceRenderer == null) {
			dropDownChoice = new DropDownChoice("editor", propertyModel, listOfChoices) {
				@Override
				public String getInputName() {
					return fieldName;
				}
				
			    @Override
			    protected String getNullKeyDisplayValue() {
			        return getString(container.getNameSpace() + "." + fieldName + ".null");
			    }
			};
		} else {
			dropDownChoice = new DropDownChoice("editor", propertyModel, listOfChoices, choiceRenderer) {
				@Override
				public String getInputName() {
					return fieldName;
				}
				
			    @Override
			    protected String getNullKeyDisplayValue() {
			        return getString(container.getNameSpace() + "." + fieldName + ".null");
			    }
			};
		}
		
		dropDownChoice.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// Do nothing. We just need to have the model updated
			}
		});
		init(dropDownChoice, attributesMap);
	}
}
