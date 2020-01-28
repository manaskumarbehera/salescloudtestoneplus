package dk.jyskit.salescloud.application.pages.sales.common;

import java.io.Serializable;

import lombok.Data;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.dao.PageInfoDao;
import dk.jyskit.salescloud.application.model.PageInfo;
import dk.jyskit.salescloud.application.pages.sales.panels.HelpPanel;
import dk.jyskit.salescloud.application.pages.sales.panels.IntroPanel;
import dk.jyskit.salescloud.application.pages.sales.panels.MiscPanel;
import dk.jyskit.waf.utils.guice.Lookup;

@Data
public abstract class PageInfoPanelFactory implements Serializable {
	private PageInfo pageInfo;
	
	public PageInfoPanelFactory(String pageId) {
		pageInfo = Lookup.lookup(PageInfoDao.class).findByPageId(CoreSession.get().getBusinessAreaEntityId(), pageId);
	}
	
	public IntroPanel getIntroPanel(String wicketId) {
		return (IntroPanel) (new IntroPanel(wicketId, pageInfo)).setOutputMarkupId(true);
	}
	
	public MiscPanel getMiscPanel(String wicketId) {
		return (MiscPanel) (new MiscPanel(wicketId, Model.of(pageInfo.getMiscHtml()))).setOutputMarkupId(true);
	}
	
	protected HelpPanel getHelpPanel(String wicketId) {
		return (HelpPanel) (new HelpPanel(wicketId, Model.of(pageInfo.getHelpHtml()))).setOutputMarkupId(true);
	}
	
	public abstract Panel getMainPanel(String wicketId);
	
	public abstract boolean save();
}
