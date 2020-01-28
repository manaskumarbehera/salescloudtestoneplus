package dk.jyskit.waf.wicket.components.forms.jsr303form.components.bootstrapselect;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import dk.jyskit.waf.components.jquery.kendo.KendoBehavior;
import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.ComponentWithLabelAndValidationPanel;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectOptions;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectSingle;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.IOptionRenderer;
import dk.jyskit.waf.wicket.utils.IAjaxCall;

public final class BootstrapSelectSinglePanel extends ComponentWithLabelAndValidationPanel<BootstrapSelectSingle> {
	private IAjaxCall selectionChangeListener = null;

//	public static <E extends Enum<E>> BootstrapSelectSinglePanel<E> forEnum(ComponentContainerPanel<?> container, final String fieldName, Class<E> enumClass) {
//		List<E> choices = Arrays.asList(enumClass.getEnumConstants());
//		return new BootstrapSelectSinglePanel<E>(container, fieldName, choices, new HashMap<String,String>())
//				.withChoiceRenderer(new EnumChoiceRenderer<E>());
//	}

	public BootstrapSelectSinglePanel(ComponentContainerPanel<?> container, final String fieldName, 
			List choices, BootstrapSelectOptions options, Map<String, String> attributesMap) {
		this(container, fieldName, options, Model.ofList(choices), attributesMap);
	}

	public BootstrapSelectSinglePanel(ComponentContainerPanel<?> container, final String fieldName, 
				IModel<List> choices, BootstrapSelectOptions options, Map<String, String> attributesMap) {
		this(container, fieldName, options, choices, attributesMap);
	}

	@SuppressWarnings("unchecked")
	public BootstrapSelectSinglePanel(final ComponentContainerPanel<?> container, final String fieldName, 
			BootstrapSelectOptions options, IModel<List> choices, Map<String, String> attributesMap) {
		super(container, fieldName);
		BootstrapSelectSingle dropdown = new BootstrapSelectSingle("editor", propertyModel, choices) {
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
		
		if (BooleanUtils.isTrue(options.getKendoUI())) {
			add(new KendoBehavior());
		}
	}

	public BootstrapSelectSinglePanel withChoiceRenderer(IChoiceRenderer renderer) {
		getComponent().setChoiceRenderer(renderer);
		return this;
	}

	public BootstrapSelectSinglePanel withOptionRenderer(IOptionRenderer renderer) {
		getComponent().setOptionRenderer(renderer);
		return this;
	}

	public BootstrapSelectSinglePanel withOptions(BootstrapSelectOptions options) {
		getComponent().setOptions(options);
		return this;
	}


	public IAjaxCall getSelectionChangeListener() {
		return selectionChangeListener;
	}


	public void setSelectionChangeListener(IAjaxCall selectionChangeListener) {
		this.selectionChangeListener = selectionChangeListener;
	}

}