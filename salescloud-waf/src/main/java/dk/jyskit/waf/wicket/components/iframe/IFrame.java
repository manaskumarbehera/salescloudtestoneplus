package dk.jyskit.waf.wicket.components.iframe;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

public class IFrame extends WebMarkupContainer {
	private static final long serialVersionUID = 1L;
	private IModel<String> urlModel;

	/**
	 * 
	 * Constructor receiving an IResourceStream.
	 * 
	 * @param id
	 * @param stream
	 */
	public IFrame(String id, IModel<String> urlModel) {
		super(id);
		this.urlModel = urlModel;
	}

	/**
	 * Handles this frame's tag.
	 * 
	 * @param tag
	 *            the component tag
	 * @see org.apache.wicket.Component#onComponentTag(ComponentTag)
	 */
	@Override
	protected final void onComponentTag(final ComponentTag tag) {
		checkComponentTag(tag, "iframe");
		tag.put("src", urlModel.getObject());
		super.onComponentTag(tag);
	}

	@Override
	protected boolean getStatelessHint() {
		return false;
	}
}