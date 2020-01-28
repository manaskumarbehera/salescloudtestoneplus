package dk.jyskit.salescloud.application.pages.sales.common;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.dao.PageInfoDao;
import dk.jyskit.salescloud.application.model.PageInfo;
import dk.jyskit.salescloud.application.pages.sales.content.ContentPage;
import dk.jyskit.salescloud.application.pages.sales.panels.HelpPanel;
import dk.jyskit.salescloud.application.pages.sales.panels.IntroPanel;
import dk.jyskit.salescloud.application.pages.sales.panels.MiscPanel;
import dk.jyskit.waf.utils.guice.Lookup;

@SuppressWarnings("serial")
public abstract class MultiPageInfoPage extends ContentPage {
	private PageInfoPanelFactory currentChildPage;
	private PageInfoPanelFactory[] childPages;
	
	public MultiPageInfoPage(PageParameters parameters, String parentPageId) {
		super(parameters, Lookup.lookup(PageInfoDao.class).findByPageId(CoreSession.get().getBusinessAreaEntityId(), parentPageId));
	}
	
	@Override
	protected void initPage() {
		childPages = getChildPages();
		currentChildPage = childPages[0];
	}
	
	@Override
	protected Panel getMainPanel(String wicketId, PageParameters parameters, PageInfo pageInfo) {
		return new MultiPageTabPanel(wicketId, this, childPages);
	}
	
	@Override
	protected IntroPanel getIntroPanel(String wicketId) {
		return currentChildPage.getIntroPanel(wicketId);
	}

	@Override
	protected MiscPanel getMiscPanel(String wicketId) {
		return currentChildPage.getMiscPanel(wicketId);
	}

	@Override
	protected HelpPanel getHelpPanel(String wicketId) {
		return currentChildPage.getHelpPanel(wicketId);
	}

	public void save() {
		currentChildPage.save();
	}

	public void updateCurrentChildPage(AjaxRequestTarget target, PageInfoPanelFactory childPage) {
		this.currentChildPage = childPage;
		replaceIntroPanel(currentChildPage.getIntroPanel("introPanel"));   // TODO: codesmell
		replaceMiscPanel(currentChildPage.getMiscPanel("miscPanel"));   // TODO: codesmell
		replaceHelpPanel(currentChildPage.getHelpPanel("helpPanel"));   // TODO: codesmell
		target.add(this);
	}

	public abstract PageInfoPanelFactory[] getChildPages();
}
