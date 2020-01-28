package dk.jyskit.waf.wicket.components.jquery.bootstrapselect;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.string.AppendingStringBuffer;

import com.googlecode.wicket.jquery.core.IJQueryWidget;
import com.googlecode.wicket.jquery.core.JQueryBehavior;

public class BootstrapSelectSingle<T> extends DropDownChoice<T> implements IJQueryWidget {
	private IOptionRenderer<T> optionRenderer = new DefaultOptionRenderer<>();

	private BootstrapSelectOptions options = new BootstrapSelectOptions();

	public BootstrapSelectSingle(String id, IModel<? extends List<? extends T>> choices, IChoiceRenderer<? super T> renderer) {
		super(id, choices, renderer);
	}

	public BootstrapSelectSingle(String id, IModel<? extends List<? extends T>> choices) {
		super(id, choices);
	}

	public BootstrapSelectSingle(String id, IModel<T> model, IModel<? extends List<? extends T>> choices, IChoiceRenderer<? super T> renderer) {
		super(id, model, choices, renderer);
	}

	public BootstrapSelectSingle(String id, IModel<T> model, IModel<? extends List<? extends T>> choices) {
		super(id, model, choices);
	}

	public BootstrapSelectSingle(String id, IModel<T> model, List<? extends T> choices, IChoiceRenderer<? super T> renderer) {
		super(id, model, choices, renderer);
	}

	public BootstrapSelectSingle(String id, IModel<T> model, List<? extends T> choices) {
		super(id, model, choices);
	}

	public BootstrapSelectSingle(String id, List<? extends T> choices, IChoiceRenderer<? super T> renderer) {
		super(id, choices, renderer);
	}

	public BootstrapSelectSingle(String id, List<? extends T> choices) {
		super(id, choices);
	}

	public BootstrapSelectSingle(String id) {
		super(id);
	}

	// Events //
	@Override
	protected void onInitialize() {
		super.onInitialize();
		this.add((Behavior)JQueryWidget.newWidgetBehavior(this));
	}

	@Override
	protected CharSequence getDefaultChoice(String selectedValue) {
		if (isNullValid()) {
			String selected = "".equals(selectedValue) ? " selected='selected'" : "";
			final StringBuilder buffer = new StringBuilder(128);
			buffer.append("\n<option").append(selected).append(" value=''>").append(getNullValidDisplayValue()).append("</option>");
			return buffer;
		} else {
			// Null is not valid. Is it selected anyway?
			if ("".equals(selectedValue)) {
				return "\n<option selected='selected' value=''>" + getNullKeyDisplayValue() + "</option>";
			}
		}
		return "";
	}

	@Override
	protected void appendOptionHtml(AppendingStringBuffer buffer, T choice, int index, String selected) {
		boolean isSelected = isSelected(choice, index, selected);
		boolean isDisabled = isDisabled(choice, index, selected);
		String optionBody = optionBody(choice);
		String optionValue = optionValue(choice, index);
		optionRenderer.render(buffer, choice, optionBody, index, isSelected, isDisabled, optionValue);
	}
	
	protected String optionValue(T choice, int index) {
		return getChoiceRenderer().getIdValue(choice, index).toString();
	}

	protected String optionBody(T choice) {
		String displayValue = optionDisplayValue(choice);
		String display = localizeDisplayValues() ? getLocalizer().getString(displayValue, this, displayValue) : displayValue;
		CharSequence optionBody = getEscapeModelStrings() ? escapeOptionHtml(display) : display;
		return optionBody.toString();
	}

	protected String optionDisplayValue(T choice) {
		Object objectValue = getChoiceRenderer().getDisplayValue(choice);
		Class<?> objectClass = (objectValue == null ? null : objectValue.getClass());

		if (objectClass != null && objectClass != String.class) {
			return optionConvert(objectValue, objectClass);
		} else if (objectValue != null) {
			return objectValue.toString();
		} else {
			return "";
		}
	}

	@SuppressWarnings("unchecked")
	protected String optionConvert(Object objectValue, Class<?> objectClass) {
		IConverter<Object> converter = (IConverter<Object>) getConverter(objectClass);
		String converted = converter.convertToString(objectValue, getLocale());
		return converted;
	}



	@Override
	public JQueryBehavior newWidgetBehavior(String selector) {
		return new BootstrapSelectBehavior(selector, options);
	}

	public BootstrapSelectOptions getOptions() {
		return options;
	}

	public void setOptions(BootstrapSelectOptions options) {
		this.options = options;
	}

	@Override
	public void onBeforeRender(JQueryBehavior behavior) {
	}

	@Override
	public void onConfigure(JQueryBehavior behavior) {
		behavior.setOptions(options);
	}

	public IOptionRenderer<T> getOptionRenderer() {
		return optionRenderer;
	}

	public void setOptionRenderer(IOptionRenderer<T> optionRenderer) {
		this.optionRenderer = optionRenderer;
	}

	public void ajaxRefreshDropdown(AjaxRequestTarget target) {
		if (isVisible()) {
			modelChanged();
			// TODO repair new drop down ...
			target.prependJavaScript("$(\"[data-id='" + getMarkupId() + "']\").parent().remove()");
			target.add(this);
		}
	}

	public BootstrapSelectSingle<T> withShowTick() {
		add(AttributeAppender.append("class", "show-tick"));
		return this;
	}

}
