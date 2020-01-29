package dk.jyskit.waf.wicket.components.forms.jsr303form.components;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import dk.jyskit.waf.wicket.components.forms.annotations.DefaultFocusBehavior;
import dk.jyskit.waf.wicket.components.forms.annotations.Focus;
import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.ControlGroupWithLabelControl;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.readonly.ReadonlyPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.validation.Jsr303PropertyValidator;
import dk.jyskit.waf.wicket.utils.BootstrapUtils;

@Slf4j
public class ComponentWithLabelAndValidationPanel<T extends Component> extends Panel {
	private static final long serialVersionUID = 1L;
	private ComponentContainerPanel container;
	private String fieldName;
	protected PropertyModel propertyModel;
	protected T component;
	private IModel<String> label;

	private ControlGroupWithLabelControl group;

	public String getFieldName() {
		return fieldName;
	}

	public ComponentWithLabelAndValidationPanel(ComponentContainerPanel container, String fieldName) {
		super("panel");
		this.container = container;
		this.fieldName = fieldName;
		setOutputMarkupId(true);
		propertyModel = new PropertyModel<Void>(container.getBeanModel(), fieldName);
	}

	protected void init(T component, Map<String, String> keyValueMap) {
		label = container.getLabelStrategy().fieldLabel(fieldName);
		this.init(component, label, keyValueMap);
	}

	/**
	 * This method makes it possible to have non-standard label components.
	 *
	 * @param component
	 * @param label
	 * @param keyValueMap
	 */
	protected void init(final T component, IModel<String> label, Map<String, String> keyValueMap) {
		this.component = component;

		// Construct a new propertymodel. This is necessary in case a child property (x.y) is to be validated
		PropertyModel<Object> propertyModel = new PropertyModel<>(container.getBeanModel(), fieldName);
		String[] fieldNameElements = StringUtils.split(fieldName, ".");
		Field propertyField = propertyModel.getPropertyField();
		boolean required = false;
		if (propertyField != null) {
			if (component instanceof FormComponent) {
				((FormComponent) component).add(new Jsr303PropertyValidator(propertyField.getDeclaringClass(), fieldNameElements[fieldNameElements.length-1]));
			}

			for (Annotation annotation : propertyField.getAnnotations()) {
				if ((annotation instanceof Nonnull) || (annotation instanceof NotNull) || (annotation instanceof NotEmpty) || (annotation instanceof NotBlank)) {
					required = true;
				}
				if (annotation instanceof Focus) {
					component.add(new DefaultFocusBehavior());
				}
			}
			if (keyValueMap.containsKey("required")) {
				required = true;
			}
			if (component instanceof FormComponent) {
				((FormComponent) component).setRequired(required);
			}
		} else {
			// try getter
			Method propertyGetter = propertyModel.getPropertyGetter();
			if (propertyGetter != null) {
				if (component instanceof FormComponent) {
					((FormComponent) component).add(new Jsr303PropertyValidator(propertyGetter.getDeclaringClass(), fieldNameElements[fieldNameElements.length-1]));
				}
				
				for (Annotation annotation : propertyGetter.getAnnotations()) {
					if ((annotation instanceof Nonnull) || (annotation instanceof NotNull) || (annotation instanceof NotEmpty) || (annotation instanceof NotBlank)) {
						required = true;
					}
					if (annotation instanceof Focus) {
						component.add(new DefaultFocusBehavior());
					}
				}
				if (keyValueMap.containsKey("required")) {
					required = true;
				}
				if (component instanceof FormComponent) {
					((FormComponent) component).setRequired(required);
				}
			}
		}
		if (keyValueMap != null) {
			for (String key : keyValueMap.keySet()) {
				component.add(new AttributeModifier(key, Model.of(keyValueMap.get(key))));
			}
		}
		
		IModel<String> helpModel = new PropertyModel(keyValueMap, "help");
		
		if (this instanceof ReadonlyPanel) {
			required = false; // Don't show as required
		}
		group = BootstrapUtils.createControlGroup("controlGroup", label, component, getValidateOnEvent(), container.isUsingInlineHelp(), container.isUsingInlineHelp(), required, helpModel);
		if (container.getLabelSpans() != null && container.getLabelSpans().length > 0) {
			group.setLabelSpans(container.getLabelSpans());
		}
		if (container.getEditorSpans() != null && container.getEditorSpans().length > 0) {
			group.setEditorSpans(container.getEditorSpans());
		}
		if (container.getHelpSpans() != null && container.getHelpSpans().length > 0) {
			group.setHelpSpans(container.getHelpSpans());
		}
		add(group);
	}

	public ControlGroupWithLabelControl getGroup() {
		return group;
	}

	public T getComponent() {
		return component;
	}

	public String getValidateOnEvent() {
		if (container.isAjaxValidate()) {
			return "onblur"; // default ajax event to validate on
		} else {
			return null;
		}
	}

	public IModel<String> getLabelModel() {
		return label;
	}


}
