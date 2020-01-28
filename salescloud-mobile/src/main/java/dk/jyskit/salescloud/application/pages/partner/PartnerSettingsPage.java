package dk.jyskit.salescloud.application.pages.partner;

import dk.jyskit.salescloud.application.model.*;
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
import dk.jyskit.salescloud.application.pages.MobilePageIds;
import dk.jyskit.salescloud.application.pages.sales.content.ContentPage;
import dk.jyskit.salescloud.application.pages.sales.panels.HelpPanel;
import dk.jyskit.waf.utils.guice.Lookup;

@AuthorizeInstantiation({ AdminRole.ROLE_NAME, SalespersonRole.ROLE_NAME })
@SuppressWarnings("serial")
public class PartnerSettingsPage extends ContentPage {
	public PartnerSettingsPage(PageParameters parameters) {
		super(parameters, Lookup.lookup(PageInfoDao.class)
				.findByPageId(CoreSession.get().getBusinessAreaEntityId(), MobilePageIds.MOBILE_PARTNER_SETTINGS));
	}
	
	@Override
	protected Panel getMainPanel(String wicketId, PageParameters parameters, PageInfo pageInfo) {
		return new PartnerSettingsPanel(wicketId, getNotificationPanel());
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

	protected Panel getIntroPanel(String wicketId) {
		if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_WORKS) {
			return new IntroPanelWithLink(wicketId, pageInfo);
		} else {
			return super.getIntroPanel(wicketId);
		}
	}
}
