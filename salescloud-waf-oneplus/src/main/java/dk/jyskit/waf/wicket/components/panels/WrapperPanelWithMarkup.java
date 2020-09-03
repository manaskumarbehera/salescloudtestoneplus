package dk.jyskit.waf.wicket.components.panels;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

/**
 * Wrapper panel that allows wrapping of another component with a given need markup e.g. a form component need in table.
 * @author palfred
 *
 */
public class WrapperPanelWithMarkup extends Panel implements IMarkupResourceStreamProvider {
	private static final long serialVersionUID = 1L;
	private Component wrapped;
	private String panelMarkup;

	/**
	 * Creates a wrapper panel for a component e.g.
	 *
	 * <pre>{@code
	 * 	add(new WrapperPanelWithMarkup("panel", new TextField("fieldId"), "<input type='text' wicket:id='fieldId' />");
	 * }</pre>
	 * @param id
	 * @param wrapped
	 * @param panelMarkup
	 */
	public WrapperPanelWithMarkup(String id, Component wrapped, String panelMarkup) {
		super(id);
		this.wrapped = wrapped;
		this.panelMarkup = panelMarkup;
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
		return "<wicket:panel>" + panelMarkup + "</wicket:panel>";
	}

	public Component getWrapped() {
		return wrapped;
	}

	public void setWrapped(Component wrapped) {
		addOrReplace(wrapped);
		this.wrapped = wrapped;
	}
}