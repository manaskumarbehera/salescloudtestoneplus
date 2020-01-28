package dk.jyskit.salescloud.application.pages.sales.common;

import lombok.Data;

import org.apache.wicket.markup.html.panel.Panel;

import dk.jyskit.salescloud.application.model.PageInfo;

/**
 * This is the main content panel for a "logical page", as represented by PageInfo.
 * 
 * TODO: We may be able to improve this. Why not have a PageInfoPanelFactory that can
 * create all panels for a PageInfo based page? What I don't like about this is you would
 * have to implement two classes: PageInfoPanelFactory and the main Panel.
 * 
 * @author jan
 *
 */
@Data
public class PageInfoPanel extends Panel {
	private PageInfo pageInfo;

	public PageInfoPanel(String wicketId, PageInfo pageInfo) {
		super(wicketId);
		this.pageInfo = pageInfo;
	}

	public void save() {
	}
}
