package dk.jyskit.waf.wicket.components.forms.jsr303form.components.multiplechoice;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;

public class ListMultipleChoicePanel<T> extends ComponentWithLabelAndValidationPanel<ListMultipleChoice<T>> {

	public ListMultipleChoicePanel(ComponentContainerPanel<?> container, String fieldName, IModel<List<? extends T>> choices,
			IChoiceRenderer<? super T> renderer, final AjaxEventListener listener) {
		this(container, fieldName, new HashMap<String,String>(), choices, renderer,  listener);
	}

	public ListMultipleChoicePanel(ComponentContainerPanel<?> container, String fieldName, Map<String, String> attributesMap,
			IModel<List<? extends T>> choices, IChoiceRenderer<?> renderer, final AjaxEventListener listener) {
		super(container, fieldName);
		@SuppressWarnings("unchecked")
		IModel<? extends Collection<T>> listModel = propertyModel;
		@SuppressWarnings("unchecked")
		IChoiceRenderer<? super T> renderer2 = (IChoiceRenderer<? super T>) renderer;
		ListMultipleChoice<T> multiChoice = new ListMultipleChoice<T>("editor", listModel, choices, renderer2);
		if (listener != null) {
			multiChoice.add(new OnChangeAjaxBehavior() {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					listener.onAjaxEvent(target);
				}

			});
		}
		init(multiChoice, attributesMap);
	}

}
