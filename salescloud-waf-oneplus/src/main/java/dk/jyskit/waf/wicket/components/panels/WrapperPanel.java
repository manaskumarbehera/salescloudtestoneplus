package dk.jyskit.waf.wicket.components.panels;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

/**
 * Wrapper panel that allows wrapping of another component (typically a panel).
 * The wrapped panel can have any wicket id. Allows insertion in a container where the container expects another wicket id.
 * @author palfred
 *
 */
public class WrapperPanel extends Panel implements IMarkupResourceStreamProvider {
	private static final long serialVersionUID = 1L;
	private Component wrapped;

	public WrapperPanel(String id, Component wrapped) {
		super(id);
		this.wrapped = wrapped;
		add(wrapped);
	}

	@Override
	public Markup getAssociatedMarkup() {
		return Markup.of(markupString());
	}

	public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
		return new StringResourceStream(markupString());
	}

	public String markupString() {
		return "<wicket:panel><div wicket:id='" + wrapped.getId() + "'/></wicket:panel>";
	}

	public Component getWrapped() {
		return wrapped;
	}

	public void setWrapped(Component wrapped) {
		addOrReplace(wrapped);
		this.wrapped = wrapped;
	}
}