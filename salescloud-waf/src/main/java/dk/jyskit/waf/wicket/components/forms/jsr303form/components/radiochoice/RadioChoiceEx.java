package dk.jyskit.waf.wicket.components.forms.jsr303form.components.radiochoice;

import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IOnChangeListener;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.value.IValueMap;

/**
 * Fixed markup to match bootstrap
 * @author Palfred
 * @param <T>
 */
public class RadioChoiceEx<T> extends RadioChoice<T> {

	public RadioChoiceEx(String id, IModel<T> model, List<? extends T> choices) {
		super(id, model, choices);
		setPrefix("<div class='radio'>");
		setSuffix("</div>");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void appendOptionHtml(final AppendingStringBuffer buffer, final T choice, int index,
		final String selected)
	{
		Object displayValue = getChoiceRenderer().getDisplayValue(choice);
		Class<?> objectClass = (displayValue == null ? null : displayValue.getClass());

		// Get label for choice
		String label = "";

		if (objectClass != null && objectClass != String.class)
		{
			@SuppressWarnings("rawtypes")
			final IConverter converter = getConverter(objectClass);
			label = converter.convertToString(displayValue, getLocale());
		}
		else if (displayValue != null)
		{
			label = displayValue.toString();
		}

		// If there is a display value for the choice, then we know that the
		// choice is automatic in some way. If label is /null/ then we know
		// that the choice is a manually created radio tag at some random
		// location in the page markup!
		if (label != null)
		{
			// Append option suffix
			buffer.append(getPrefix(index, choice));

			String id = getChoiceRenderer().getIdValue(choice, index);
			final String idAttr = getMarkupId() + "-" + id;

			boolean enabled = isEnabledInHierarchy() && !isDisabled(choice, index, selected);

			// PALFRED label start
			buffer.append("<label for=\"").append(idAttr).append("\">");

			// Add radio tag
			buffer.append("<input name=\"")
				.append(getInputName())
				.append('"')
				.append(" type=\"radio\"")
				.append((isSelected(choice, index, selected) ? " checked=\"checked\"" : ""))
				.append((enabled ? "" : " disabled=\"disabled\""))
				.append(" value=\"")
				.append(id)
				.append("\" id=\"")
				.append(idAttr)
				.append('"');

			// Should a roundtrip be made (have onSelectionChanged called)
			// when the option is clicked?
			if (wantOnSelectionChangedNotifications())
			{
				CharSequence url = urlFor(IOnChangeListener.INTERFACE, new PageParameters());

				Form<?> form = findParent(Form.class);
				if (form != null)
				{
					buffer.append(" onclick=\"")
						.append(form.getJsForInterfaceUrl(url))
						.append(";\"");
				}
				else
				{
					// NOTE: do not encode the url as that would give
					// invalid JavaScript
					buffer.append(" onclick=\"window.location.href='")
						.append(url)
						.append((url.toString().indexOf('?') > -1 ? '&' : '?') + getInputName())
						.append('=')
						.append(id)
						.append("';\"");
				}
			}

			// Allows user to add attributes to the <input..> tag
			{
				IValueMap attrs = getAdditionalAttributes(index, choice);
				if (attrs != null)
				{
					for (Map.Entry<String, Object> attr : attrs.entrySet())
					{
						buffer.append(' ')
							.append(attr.getKey())
							.append("=\"")
							.append(attr.getValue())
							.append('"');
					}
				}
			}

			if (getApplication().getDebugSettings().isOutputComponentPath())
			{
				CharSequence path = getPageRelativePath();
				path = Strings.replaceAll(path, "_", "__");
				path = Strings.replaceAll(path, ":", "_");
				buffer.append(" wicketpath=\"")
					.append(path)
					.append("_input_")
					.append(index)
					.append('"');
			}

			buffer.append("/>");

			// Add label for radio button
			String display = label;
			if (localizeDisplayValues())
			{
				display = getLocalizer().getString(label, this, label);
			}

			CharSequence escaped = display;
			if (getEscapeModelStrings())
			{
				escaped = Strings.escapeMarkup(display);
			}

			// PALFRED label end
			buffer.append(escaped).append("</label>");

			// Append option suffix
			buffer.append(getSuffix(index, choice));
		}
	}


}
