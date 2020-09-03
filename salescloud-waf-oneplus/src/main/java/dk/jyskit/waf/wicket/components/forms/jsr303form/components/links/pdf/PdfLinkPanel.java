package dk.jyskit.waf.wicket.components.forms.jsr303form.components.links.pdf;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import dk.jyskit.waf.utils.dataexport.pdf.PdfLink;
import dk.jyskit.waf.utils.dataexport.pdf.PdfLinkCallback;
import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;

public class PdfLinkPanel extends Panel {
	private PdfLink link;

	/**
	 * Submitting link with text.
	 *
	 * @param container
	 * @param labelKey
	 * @param callback
	 */
	public PdfLinkPanel(final ComponentContainerPanel container, String labelKey, PdfLinkCallback callback) {
		super("panel");

		link = new PdfLink("link", callback);
		link.add(new Label("label", getString(callback.getTitleKey())));
		add(link);

//		link.add(AttributeModifier.replace("class", labelKey));

		Fragment fragment = new Fragment("fragment", "f_text", this);
		IModel<String> labelModel = container.getLabelStrategy().linkLabel(labelKey);
		fragment.add(new Label("text", labelModel));
        link.add(fragment);
	}

	public PdfLink getLink() {
		return link;
	}
}
