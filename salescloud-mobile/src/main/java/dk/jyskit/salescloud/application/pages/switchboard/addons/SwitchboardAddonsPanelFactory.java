package dk.jyskit.salescloud.application.pages.switchboard.addons;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.github.rjeschke.txtmark.Processor;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.components.campaigns.CampaignInfoPanel;
import dk.jyskit.salescloud.application.model.MobileCampaign;
import dk.jyskit.salescloud.application.pages.MobilePageIds;
import dk.jyskit.salescloud.application.pages.sales.common.PageInfoPanelFactory;
import dk.jyskit.salescloud.application.pages.sales.panels.HelpPanel;

public class SwitchboardAddonsPanelFactory extends PageInfoPanelFactory {

	private final int tabNo;
	private PanelWithSave switchboardAddonsMainPanel;

	public SwitchboardAddonsPanelFactory(int tabNo, boolean bundleAddOns) {
		super(MobileSession.get().isBusinessAreaOnePlus()
				? (bundleAddOns ? MobilePageIds.MOBILE_SOLUTION_ADDONS : MobilePageIds.MOBILE_POOL_ADDONS)
				: MobilePageIds.MOBILE_SWITCHBOARD_ADDONS);
		this.tabNo = tabNo;
	}

	@Override
	public Panel getMainPanel(String wicketId) {
		if (MobileSession.get().isBusinessAreaOnePlus()) {
			switchboardAddonsMainPanel = new SwitchboardAddonsOneMainPanel(wicketId, tabNo == 2);
		} else {
			switchboardAddonsMainPanel = new SwitchboardAddonsMainPanel(wicketId);
		}
		return switchboardAddonsMainPanel;
	}

	@Override
	protected HelpPanel getHelpPanel(String wicketId) {
		return new HelpPanel(wicketId, Model.of(getPageInfo().getHelpHtml())) {
			protected Component getPreContentPanel(String wicketId) {
				String campaignHelp = ((MobileCampaign) MobileSession.get().getContract().getCampaigns().get(0)).getSwitchboardAddonHelpText();
				if (StringUtils.isEmpty(campaignHelp)) {
					return new EmptyPanel(wicketId);
				}
				campaignHelp = Processor.process(campaignHelp);
				return new CampaignInfoPanel(wicketId, new Model<String>(campaignHelp));
			}
		};
	}

	@Override
	public boolean save() {
		return switchboardAddonsMainPanel.save();
	}
}
