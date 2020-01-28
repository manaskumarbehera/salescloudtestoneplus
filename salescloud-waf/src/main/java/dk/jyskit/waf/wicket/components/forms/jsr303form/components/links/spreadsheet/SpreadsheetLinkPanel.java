package dk.jyskit.waf.wicket.components.forms.jsr303form.components.links.spreadsheet;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.inject.Provider;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.ControlGroupWithLabelControl;
import dk.jyskit.waf.wicket.components.spreadsheets.SpreadsheetLink;
import dk.jyskit.waf.wicket.utils.BootstrapUtils;

public class SpreadsheetLinkPanel extends Panel {
	private SpreadsheetLink link;
	private ControlGroupWithLabelControl group;

	/**
	 * Submitting link with text.
	 *
	 * @param container
	 * @param labelKey
	 * @param callback
	 */
	public SpreadsheetLinkPanel(final ComponentContainerPanel container, String labelKey, String fileName, IModel<String> linkLabelModel, Provider<Workbook> workbookProvider) {
		super("panel");
		
		IModel<String> labelModel = container.getLabelStrategy().linkLabel(labelKey);

		link = new SpreadsheetLink("link", fileName, workbookProvider);
		link.add(new Label("linkLabel", linkLabelModel));
		
		group = BootstrapUtils.createControlGroup("controlGroup", labelModel, link, null, container.isUsingInlineHelp(), container.isUsingInlineHelp(), false, Model.of(""));
		if (container.getLabelSpans() != null && container.getLabelSpans().length > 0) {
			group.setLabelSpans(container.getLabelSpans());
		}
		if (container.getEditorSpans() != null && container.getEditorSpans().length > 0) {
			group.setEditorSpans(container.getEditorSpans());
		}
		if (container.getHelpSpans() != null && container.getHelpSpans().length > 0) {
			group.setHelpSpans(container.getHelpSpans());
		}
		add(group);
	}

	public SpreadsheetLink getLink() {
		return link;
	}
}
