package dk.jyskit.waf.wicket.components.forms.jsr303form.components.links.excel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import dk.jyskit.waf.utils.dataexport.spreadsheets.ExcelLink;
import dk.jyskit.waf.utils.dataexport.spreadsheets.ExcelLinkCallback;
import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;

public class ExcelLinkPanel extends Panel {
	private ExcelLink link;

	/**
	 * Submitting link with text.
	 *
	 * @param container
	 * @param labelKey
	 * @param callback
	 */
	public ExcelLinkPanel(final ComponentContainerPanel container, String labelKey, ExcelLinkCallback callback) {
		super("panel");

		link = new ExcelLink("link", callback);
		link.add(new Label("label", getString(callback.getTitleKey())));
		add(link);

//		link.add(AttributeModifier.replace("class", labelKey));

		Fragment fragment = new Fragment("fragment", "f_text", this);
		IModel<String> labelModel = container.getLabelStrategy().linkLabel(labelKey);
		fragment.add(new Label("text", labelModel));
        link.add(fragment);
	}

	public ExcelLink getLink() {
		return link;
	}
}
