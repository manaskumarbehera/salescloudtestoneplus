package dk.jyskit.waf.wicket.components.forms.jsr303form.components.multiplechoice;

import java.util.*;

import lombok.Value;
import lombok.experimental.Wither;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;

public class MultipleCheckBoxPanel<T> extends ComponentWithLabelAndValidationPanel<CheckBoxMultipleChoice<T>> {
	private final CheckBoxMultipleEx<T> multiCheck;

	@Value
	@Wither
	public static class Builder<T> {
		private ComponentContainerPanel<?> container;
		private String fieldName;
		private Map<String, String> attributesMap;
		private List<? extends T> choices;
		private IChoiceRenderer<? super T> renderer;
		private String checkCss;
		private AjaxEventListener listener;

		public MultipleCheckBoxPanel<T> build() {
			return new MultipleCheckBoxPanel<T>(container, fieldName, attributesMap, choices, renderer, checkCss, listener);
		}

	}

	public static <T> Builder<T> builder(Class<T> infer, ComponentContainerPanel<?> container, String fieldName) {
		return new Builder<T>(container, fieldName, new HashMap<String, String>(), new ArrayList<T>(), null, "", null);
	}

	// TODO consider where attributes should be applied on group or on single
	// choice ?

	public MultipleCheckBoxPanel(ComponentContainerPanel<?> container, String fieldName, Map<String, String> attributesMap,
			List<? extends T> choices, IChoiceRenderer<? super T> renderer, String checkCss, final AjaxEventListener listener) {
		super(container, fieldName);

		@SuppressWarnings("unchecked")
		IModel<? extends Collection<T>> choiceModel = propertyModel;
		multiCheck = new CheckBoxMultipleEx<T>("editor", choiceModel, choices, checkCss);
		if (listener != null) {
			multiCheck.add(new AjaxFormChoiceComponentUpdatingBehavior() {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					listener.onAjaxEvent(target);
				}

			});
		}
		if (renderer != null) {
			multiCheck.setChoiceRenderer(renderer);
		}
		init(multiCheck, attributesMap);
	}

	public CheckBoxMultipleEx<T> getMultiCheck() {
		return multiCheck;
	}

}
