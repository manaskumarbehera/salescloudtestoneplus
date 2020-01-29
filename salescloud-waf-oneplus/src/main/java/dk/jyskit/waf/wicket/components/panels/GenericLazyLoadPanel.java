package dk.jyskit.waf.wicket.components.panels;

import java.lang.reflect.Constructor;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.markup.html.basic.Label;

public final class GenericLazyLoadPanel extends AjaxLazyLoadPanel {

	private Class<? extends Component> componentClazz;

	public GenericLazyLoadPanel(String id, Class<? extends Component> componentClazz) {
		super(id);
		this.componentClazz = componentClazz;
	}

	@Override
	public Component getLazyLoadComponent(String markupId) {
		try {
			Constructor<? extends Component> panelConstructor = componentClazz.getConstructor(String.class);
			return panelConstructor.newInstance(markupId);
		} catch (Exception e) {
			// TODO refine exception tjek.
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Component getLoadingComponent(final String markupId) {
		return new Label(markupId, "<i class=\"fa fa-spinner fa-spin fa-4x\"></i> ").setEscapeModelStrings(false);
	}
}