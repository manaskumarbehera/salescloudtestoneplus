package dk.jyskit.salescloud.application.pages.standardbundles;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.github.rjeschke.txtmark.Processor;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.components.campaigns.CampaignInfoPanel;
import dk.jyskit.salescloud.application.dao.PageInfoDao;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.MobileCampaign;
import dk.jyskit.salescloud.application.model.PageInfo;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.pages.MobilePageIds;
import dk.jyskit.salescloud.application.pages.sales.content.ContentPage;
import dk.jyskit.salescloud.application.pages.sales.panels.HelpPanel;
import dk.jyskit.waf.utils.guice.Lookup;

@AuthorizeInstantiation({ AdminRole.ROLE_NAME, SalespersonRole.ROLE_NAME })
@SuppressWarnings("serial")
public class StandardBundlesPage extends ContentPage {
	public StandardBundlesPage(PageParameters parameters) {
		super(parameters, Lookup.lookup(PageInfoDao.class)
				.findByPageId(CoreSession.get().getBusinessAreaEntityId(), MobilePageIds.MOBILE_STANDARD_BUNDLES));
	}
	
	@Override
	protected Panel getMainPanel(String wicketId, PageParameters parameters, PageInfo pageInfo) {
		return new StandardBundlesPanel(wicketId, getNotificationPanel());
	}

	protected Panel getHelpPanel(String wicketId) {
		return new HelpPanel(wicketId, Model.of(pageInfo.getHelpHtml())) {
			protected Component getPreContentPanel(String wicketId) {
				String campaignHelp = ((MobileCampaign) MobileSession.get().getContract().getCampaigns().get(0)).getCampaignBundleHelpText();
				if (StringUtils.isEmpty(campaignHelp)) {
					return new EmptyPanel(wicketId);
				}
				campaignHelp = Processor.process(campaignHelp);
				return new CampaignInfoPanel(wicketId, new Model<String>(campaignHelp));
			}
		};
	}

}
