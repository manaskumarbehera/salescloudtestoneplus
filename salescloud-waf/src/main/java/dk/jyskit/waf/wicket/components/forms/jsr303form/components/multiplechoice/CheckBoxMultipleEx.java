package dk.jyskit.waf.wicket.components.forms.jsr303form.components.multiplechoice;

import java.util.Collection;
import java.util.List;

import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.string.Strings;

/**
 * Controls better markup.
 *
 * @author m43634
 *
 * @param <T>
 */
public class CheckBoxMultipleEx<T> extends CheckBoxMultipleChoice<T> {

	private String checkCss;

	public CheckBoxMultipleEx(String id, IModel<? extends Collection<T>> model, List<? extends T> choices, String checkCss) {
		super(id, model, choices);
		this.checkCss = checkCss;
		setSuffix("");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void appendOptionHtml(final AppendingStringBuffer buffer, final T choice, int index, final String selected) {
		Object displayValue = getChoiceRenderer().getDisplayValue(choice);
		Class<?> objectClass = displayValue == null ? null : displayValue.getClass();
		// Get label for choice
		String label = "";
		if (objectClass != null && objectClass != String.class) {
			@SuppressWarnings("rawtypes")
			IConverter converter = getConverter(objectClass);
			label = converter.convertToString(displayValue, getLocale());
		} else if (displayValue != null) {
			label = displayValue.toString();
		}

		// If there is a display value for the choice, then we know that the
		// choice is automatic in some way. If label is /null/ then we know
		// that the choice is a manually created checkbox tag at some random
		// location in the page markup!
		if (label != null) {
			// Append option suffix
			buffer.append(getPrefix(index, choice));

			String id = getChoiceRenderer().getIdValue(choice, index);
			final String idAttr = getCheckBoxMarkupId(id);

			// Add checkbox element
			buffer.append("<label class='" + checkCss + " " + getChoiceCss(choice) + "'>");
			buffer.append("<input name=\"");
			buffer.append(getInputName());
			buffer.append("\"");
			buffer.append(" type=\"checkbox\"");
			if (isSelected(choice, index, selected)) {
				buffer.append(" checked=\"checked\"");
			}
			if (isDisabled(choice, index, selected) || !isEnabledInHierarchy()) {
				buffer.append(" disabled=\"disabled\"");
			}
			buffer.append(" value=\"");
			buffer.append(id);
			buffer.append("\" id=\"");
			buffer.append(idAttr);
			buffer.append("\"/>");

			// Add label for checkbox
			String display = label;
			if (localizeDisplayValues()) {
				display = getLocalizer().getString(label, this, label);
			}

			final CharSequence escaped = (getEscapeModelStrings() ? Strings.escapeMarkup(display) : display);

			buffer.append(' ').append(escaped).append("</label>");

			// Append option suffix
			buffer.append(getSuffix(index, choice));
		}
	}

	@Override
	protected boolean isDisabled(T choice, int index, String selected) {
		if (getChoiceRenderer() instanceof IChoiceEnabler) {
			@SuppressWarnings("unchecked")
			IChoiceEnabler<T> cr = (IChoiceEnabler<T>)getChoiceRenderer();
			return !cr.isEnabled(choice, index, selected);
		}
		return super.isDisabled(choice, index, selected);
	}

	protected String getChoiceCss(T choice) {
		return "";
	}
}
