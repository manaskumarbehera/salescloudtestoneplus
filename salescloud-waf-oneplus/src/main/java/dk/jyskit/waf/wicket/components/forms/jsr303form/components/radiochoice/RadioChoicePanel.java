package dk.jyskit.waf.wicket.components.forms.jsr303form.components.radiochoice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Value;
import lombok.experimental.Wither;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;

public class RadioChoicePanel<T> extends ComponentWithLabelAndValidationPanel<RadioChoice<T>> {
	@Value
	@Wither
	public static class Builder<T> {
		private ComponentContainerPanel<?> container;
		private String fieldName;
		private Map<String, String> attributesMap;
		private List<? extends T> choices;
		private IChoiceRenderer<? super T> renderer;
		private AjaxEventListener listener;


		public RadioChoicePanel<T> build() {
			return new RadioChoicePanel<T>(container, fieldName, attributesMap, choices, renderer, listener);
		}


	}

	public static <T> Builder<T> builder(Class<T> infer, ComponentContainerPanel<?> container, String fieldName) {
		return new Builder<T>(container, fieldName, new HashMap<String, String>(), new ArrayList<T>(), null, null);
	}


	public RadioChoicePanel(ComponentContainerPanel<?> container, String fieldName, Map<String, String> attributesMap,
			List<? extends T> choices, IChoiceRenderer<? super T> renderer, final AjaxEventListener listener) {
		super(container, fieldName);

		@SuppressWarnings("unchecked")
		RadioChoice<T> choice= new RadioChoiceEx<T>("editor", propertyModel, choices);
		if (listener != null) {
			choice.add(new AjaxFormChoiceComponentUpdatingBehavior() {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					listener.onAjaxEvent(target);
				}

			});
		}
		if (renderer != null) {
			choice.setChoiceRenderer(renderer);
		}
		init(choice, attributesMap);
	}

}
