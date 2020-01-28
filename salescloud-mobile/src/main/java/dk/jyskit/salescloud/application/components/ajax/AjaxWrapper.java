package dk.jyskit.salescloud.application.components.ajax;

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
public class AjaxWrapper extends Panel implements IMarkupResourceStreamProvider {
	private static final long serialVersionUID = 1L;
	private Component wrapped;
	private boolean useWicketContainer = false;
	private String htmlTag = "div";

	public AjaxWrapper(String id, Component wrapped) {
		this(id, wrapped, "div");
		setOutputMarkupId(true);
	}

	public AjaxWrapper(String id, Component wrapped, boolean useWicketContainer) {
		super(id);
		this.wrapped = wrapped;
		this.useWicketContainer = useWicketContainer;
		add(wrapped);
		setOutputMarkupId(true);
	}

	public AjaxWrapper(String id, Component wrapped, String htmlTag) {
		super(id);
		this.wrapped = wrapped;
		this.htmlTag = htmlTag;
		add(wrapped);
		setOutputMarkupId(true);
	}

	@Override
	public Markup getAssociatedMarkup() {
		return Markup.of(markupString());
	}

	public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
		return new StringResourceStream(markupString());
	}

	public String markupString() {
		if (useWicketContainer) {
			return "<wicket:panel><wicket:container wicket:id='" + wrapped.getId() + "'/></wicket:panel>";
		} else {
			return "<wicket:panel><" + htmlTag + " wicket:id='" + wrapped.getId() + "'/></wicket:panel>";
		}
	}

	public Component getWrapped() {
		return wrapped;
	}

	public void setWrapped(Component wrapped) {
		addOrReplace(wrapped);
		this.wrapped = wrapped;
	}
}