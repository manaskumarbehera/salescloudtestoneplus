package dk.jyskit.waf.wicket.components.jquery.bootstrapselect;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.string.Strings;

/**
 * Default option render for BootstrapSelects.
 * Have a lot of helper methods to control attributes of &lt;option&gt; element, which is called from {@link #extraAttributes(Object, boolean, boolean, String, Map)}.  
 * @author palfred
 *
 * @param <T> Choice type
 */
public class DefaultOptionRenderer<T> implements IOptionRenderer<T> {
	
	@Override
	public void render(AppendingStringBuffer buffer, T choice, String optionBody, int index, boolean isSelected, boolean isDisabled, String optionValue) {
		buffer.append("\n<option");
		Map<String, String> attributes = attributes(choice, isSelected, isDisabled, optionValue);
		for (String attrKey : attributes.keySet()) {
			CharSequence escapedValue = Strings.escapeMarkup(attributes.get(attrKey));
			buffer.append(' ').append(attrKey).append('=').append('"').append(escapedValue).append('"');
		}
		buffer.append(">");
		buffer.append(optionBody);
		buffer.append("</option>");
	}

	public  Map<String, String> attributes(T choice, boolean isSelected, boolean isDisabled, String optionValue) {
		Map<String, String> attributes = new LinkedHashMap<>();
		if (isSelected) {
			attributes.put("selected", "selected");
		}
		if (isDisabled) {
			attributes.put("disabled", "disabled");
		}
		attributes.put("value", optionValue);
		extraAttributes(choice, isSelected, isDisabled, optionValue, attributes);
		return attributes;
	}

	/**
	 * Adds extra attributes. Th default implementation calls helper methods for each extta attribute. 
	 * See for example {@link #icon(Object, boolean, boolean)} and {@link #cssStyle(Object, boolean, boolean)}.
	 * @param choice
	 * @param isSelected
	 * @param isDisabled
	 * @param optionValue
	 * @param attributes
	 */
	public  void extraAttributes(T choice, boolean isSelected, boolean isDisabled, String optionValue, Map<String, String> attributes) {
		safePutAttribute(attributes, "data-icon", icon(choice, isSelected, isDisabled));
		safePutAttribute(attributes, "data-content", customMarkup(choice, isSelected, isDisabled));
		safePutAttribute(attributes, "class", cssClass(choice, isSelected, isDisabled));
		safePutAttribute(attributes, "style", cssStyle(choice, isSelected, isDisabled));
		safePutAttribute(attributes, "data-subtext", subtext(choice, isSelected, isDisabled));
	}

	/**
	 * Sub text for the option.
	 * @param choice
	 * @return css class for an icon
	 */
	protected String subtext(T choice, boolean isSelected, boolean isDisabled) {
		return null;
	}
	
	/**
	 * Choice icon. E.g. "icon-heart"
	 * @param choice
	 * @return css class for an icon
	 */
	protected String icon(T choice, boolean isSelected, boolean isDisabled) {
		return null;
	}

	/**
	 * Choice css class.
	 * @param choice
	 * @param isSelected
	 * @param isDisabled
	 * @return
	 */
	protected String cssClass(T choice, boolean isSelected, boolean isDisabled) {
		return null;
	}

	/**
	 * Choice css style attribute.
	 * @param choice
	 * @param isSelected
	 * @param isDisabled
	 * @return
	 */
	protected String cssStyle(T choice, boolean isSelected, boolean isDisabled) {
		return null;
	}
	
	/**
	 * Custom markup for a choice. Can be used to customize content to be complex.
	 * @param choice
	 * @return
	 */
	protected String customMarkup(T choice, boolean isSelected, boolean isDisabled) {
		return null;
	}
	
	protected void  safePutAttribute(Map<String, String> attributes, String key, String value) {
		if (key != null) {
			if (!Strings.isEmpty(value)) {
				attributes.put(key, value);
			} else {
				attributes.remove(key);
			}
		} 
		
	}
}
