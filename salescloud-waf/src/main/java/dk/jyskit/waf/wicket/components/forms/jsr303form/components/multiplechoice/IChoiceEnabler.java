package dk.jyskit.waf.wicket.components.forms.jsr303form.components.multiplechoice;

import org.apache.wicket.markup.html.form.IChoiceRenderer;

/**
 * Helper interface to allow controlling of enabling of choices from {@link IChoiceRenderer}
 * @author palfred
 *
 */
public interface IChoiceEnabler<T> {
	boolean isEnabled(T choice, int index, String selected);
}
