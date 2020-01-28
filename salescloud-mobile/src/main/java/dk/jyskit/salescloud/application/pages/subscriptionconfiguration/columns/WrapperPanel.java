package dk.jyskit.salescloud.application.pages.subscriptionconfiguration.columns;

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
 * @author jan (palfred)
 *
 */
public class WrapperPanel extends Panel implements IMarkupResourceStreamProvider {
	private static final long serialVersionUID = 1L;
	private Component wrapped;
	private String tag;
	private String[] attributes;

	public WrapperPanel(String id, Component wrapped) {
		this(id, "div", wrapped);
	}

	public WrapperPanel(String id, String tag, Component wrapped, String ... attributes) {
		super(id);
		this.tag = tag;
		this.wrapped = wrapped;
		this.attributes = attributes;
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
		StringBuilder sb = new StringBuilder();
		sb.append("<wicket:panel><");
		sb.append(tag);
		sb.append(" wicket:id='");
		sb.append(wrapped.getId());
		sb.append("' ");
		for (String attr : attributes) {
			sb.append(attr);
			sb.append(' ');
		}
		sb.append("/></wicket:panel>");
		return sb.toString();
	}

	public Component getWrapped() {
		return wrapped;
	}

	public void setWrapped(Component wrapped) {
		addOrReplace(wrapped);
		this.wrapped = wrapped;
	}
}