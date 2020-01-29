package dk.jyskit.waf.wicket.components.forms.annotations;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;

/**
 * Behavior to focus the component for which it is added. Copied from
 * http://old.nabble.com/Default-Focus-Behavior--td15934889.html and extended
 * with support for {@link AutoCompleteTextField} etc. by means of the
 * {@code Wicket.Focus.setFocusOnId()} method, cf.
 * {@link AjaxRequestTarget#focusComponent(Component)}.
 * 
 * The classic {@code .focus()} JavaScript method is used for anything except
 * instances of {@link AutoCompleteTextField} in order to cope with missing
 * {@link AjaxRequestTarget}s (guaranteed to exist for dropdowns only).
 * 
 * @author jgr
 */
public class DefaultFocusBehavior extends Behavior {
	private static final long serialVersionUID = 1L;

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);
		if (component != null) {
			AjaxRequestTarget target = component.getRequestCycle().find(AjaxRequestTarget.class);
			
			if (target == null) {
				String id = component.getMarkupId();
				if (id != null) {
					final boolean autoComplete = component instanceof AutoCompleteTextField<?>;
					if (autoComplete) {
						// TODO: Wicket 6 compatible?
						response.render(OnLoadHeaderItem.forScript("if (typeof Wicket.Focus == 'object') { Wicket.Focus.setFocusOnId('" + id + "'); }"));
					} else {
						// TODO: Wicket 6 compatible?
						response.render(OnLoadHeaderItem.forScript("document.getElementById('" + id + "').focus();"));
					}
				}
			} else {
				target.focusComponent(component);
			}
		}
	}
}
