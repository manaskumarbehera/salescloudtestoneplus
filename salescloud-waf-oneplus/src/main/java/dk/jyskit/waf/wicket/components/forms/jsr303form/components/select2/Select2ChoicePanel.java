package dk.jyskit.waf.wicket.components.forms.jsr303form.components.select2;

import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.request.resource.CssResourceReference;

import com.vaynberg.wicket.select2.ChoiceProvider;
import com.vaynberg.wicket.select2.Select2Choice;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;
import dk.jyskit.waf.wicket.utils.IAjaxCall;

public final class Select2ChoicePanel extends ComponentWithLabelAndValidationPanel<Select2Choice> {
	private IAjaxCall selectionChangeListener = null;

	@SuppressWarnings("unchecked")
	public Select2ChoicePanel(final ComponentContainerPanel<?> container, final String fieldName, 
			ChoiceProvider choiceProvider, Map<String, String> attributesMap) {
		super(container, fieldName);
		
		Select2Choice dropdown = new Select2Choice("editor", propertyModel, choiceProvider) {
			@Override
			public String getInputName() {
				return fieldName;
			}
		};
		
		dropdown.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (selectionChangeListener != null) {
					selectionChangeListener.invoke(target);;
				}
			}
		});
		
		init(dropdown, attributesMap);
	}

	public IAjaxCall getSelectionChangeListener() {
		return selectionChangeListener;
	}

	public void setSelectionChangeListener(IAjaxCall selectionChangeListener) {
		this.selectionChangeListener = selectionChangeListener;
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssReferenceHeaderItem.forReference(new CssResourceReference(Select2ChoicePanel.class, "select2-bootstrap.css")));
	}
}