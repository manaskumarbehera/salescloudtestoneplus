package dk.jyskit.waf.wicket.components.jquery.bootstrapselect;

import java.io.Serializable;
import java.util.Map;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.util.string.AppendingStringBuffer;

/**
 * Interface for rendering of option for BootstrapSelects. Support for custom attributes can be made using {@link DefaultOptionRenderer#extraAttributes(Object, boolean, boolean, String, Map)}.
 * @author palfred
 *
 * @param <T>
 */
public interface IOptionRenderer<T> extends Serializable {

	/**
	 * Renders one option element for the given coice to given buffer.
	 * @param buffer The buffer to append to 
	 * @param choice The choise to render option for.
	 * @param optionBody The body value given from {@link IChoiceRenderer}
	 * @param index The index of the option.
	 * @param isSelected
	 * @param isDisabled
	 * @param optionValue
	 */
	void render(AppendingStringBuffer buffer, T choice, String optionBody, int index, boolean isSelected, boolean isDisabled,
			String optionValue);


}