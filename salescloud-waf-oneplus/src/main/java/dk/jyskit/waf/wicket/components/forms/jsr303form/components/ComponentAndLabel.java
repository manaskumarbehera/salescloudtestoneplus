package dk.jyskit.waf.wicket.components.forms.jsr303form.components;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.html.form.FormComponent;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.panels.IPanelWrapable;


/**
 * Helper class to allow easier add of custom field panels to forms.
 * Example:
 * <pre>{@code
 *   TagsInputOptions tagsOptions = new TagsInputOptions().withTypeahead(true, "java", "cobol", "c++", "c#");
 *   form.addCustomField(new TagsInputField("tags").withOptions(tagsOptions));
 * }</pre>
 * @author palfred
 * @see Jsr303Form#addCustomField(FormComponent)
 * @see IPanelWrapable
 * @see TagsInputField
 * @param <T>
 * @param <F>
 */
public class ComponentAndLabel<T, F extends FormComponent<T> & IPanelWrapable> extends ComponentWithLabelAndValidationPanel<F> {
	private F field;

	public static <T1, F1 extends FormComponent<T1> & IPanelWrapable> ComponentAndLabel<T1, F1> create(
			ComponentContainerPanel<?> container, String fieldName, F1 field, Map<String, String> attributesMap) {
		return new ComponentAndLabel<T1, F1>(container, fieldName, field, attributesMap);
	}

	public static <T1, F1 extends FormComponent<T1> & IPanelWrapable> ComponentAndLabel<T1, F1> create(
			ComponentContainerPanel<?> container, String fieldName, F1 field) {
		return new ComponentAndLabel<T1, F1>(container, fieldName, field, new HashMap<String, String>());
	}

	public static <T1, F1 extends FormComponent<T1> & IPanelWrapable> ComponentAndLabel<T1, F1> create(
			ComponentContainerPanel<?> container, F1 field, Map<String, String> attributesMap) {
		return new ComponentAndLabel<T1, F1>(container, field.getId(), field, attributesMap);
	}

	public static <T1, F1 extends FormComponent<T1> & IPanelWrapable> ComponentAndLabel<T1, F1> create(
			ComponentContainerPanel<?> container, F1 field) {
		return new ComponentAndLabel<T1, F1>(container, field.getId(), field, new HashMap<String, String>());
	}

	public ComponentAndLabel(ComponentContainerPanel<?> container, String fieldName, F field, Map<String, String> attributesMap) {
		super(container, fieldName);
		this.field = field;
		field.setModel(propertyModel);
		init(field, attributesMap);
	}

	@Override
	public Markup getAssociatedMarkup() {
		return Markup.of(markupString());
	}

	public String markupString() {
		return "<wicket:panel><div wicket:id='controlGroup'>" + field.getPanelBodyMarkup(field.getId()) + "</div></wicket:panel>";
	}

}
