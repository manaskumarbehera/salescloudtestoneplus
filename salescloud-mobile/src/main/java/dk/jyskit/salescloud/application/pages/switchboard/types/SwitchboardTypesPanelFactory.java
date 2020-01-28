package dk.jyskit.salescloud.application.pages.switchboard.types;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.github.rjeschke.txtmark.Processor;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.components.campaigns.CampaignInfoPanel;
import dk.jyskit.salescloud.application.model.MobileCampaign;
import dk.jyskit.salescloud.application.pages.MobilePageIds;
import dk.jyskit.salescloud.application.pages.sales.common.PageInfoPanelFactory;
import dk.jyskit.salescloud.application.pages.sales.panels.HelpPanel;

public class SwitchboardTypesPanelFactory extends PageInfoPanelFactory {

	private NotificationPanel notificationPanel;
	private SwitchboardTypesMainPanel switchboardTypesMainPanel;

	public SwitchboardTypesPanelFactory(NotificationPanel notificationPanel) {
		super(MobilePageIds.MOBILE_SWITCHBOARD_TYPE);
		this.notificationPanel = notificationPanel;
	}

	@Override
	public Panel getMainPanel(String wicketId) {
		switchboardTypesMainPanel = new SwitchboardTypesMainPanel(wicketId, notificationPanel);
		return switchboardTypesMainPanel;
	}

	@Override
	protected HelpPanel getHelpPanel(String wicketId) {
		return new HelpPanel(wicketId, Model.of(getPageInfo().getHelpHtml())) {
			protected Component getPreContentPanel(String wicketId) {
				String campaignHelp = ((MobileCampaign) MobileSession.get().getContract().getCampaigns().get(0)).getSwitchboardHelpText();
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
		return switchboardTypesMainPanel.save();
	}
	
}
