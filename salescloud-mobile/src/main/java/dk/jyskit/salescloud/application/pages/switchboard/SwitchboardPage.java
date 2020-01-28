package dk.jyskit.salescloud.application.pages.switchboard;

import dk.jyskit.salescloud.application.MobileSession;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.pages.MobilePageIds;
import dk.jyskit.salescloud.application.pages.sales.common.MultiPageInfoPage;
import dk.jyskit.salescloud.application.pages.sales.common.PageInfoPanelFactory;
import dk.jyskit.salescloud.application.pages.switchboard.addons.SwitchboardAddonsPanelFactory;
import dk.jyskit.salescloud.application.pages.switchboard.types.SwitchboardTypesPanelFactory;

@AuthorizeInstantiation({ AdminRole.ROLE_NAME, SalespersonRole.ROLE_NAME })
@SuppressWarnings("serial")
public class SwitchboardPage extends MultiPageInfoPage {
	public SwitchboardPage(PageParameters parameters) {
		super(parameters, MobilePageIds.MOBILE_SWITCHBOARD);
	}

	@Override
	public PageInfoPanelFactory[] getChildPages() {
		if (MobileSession.get().isBusinessAreaOnePlus()) {
			if (MobileSession.get().getContract().isPoolsMode()) {
				if (MobileSession.get().getContract().isMobileOnlySolution()) {
					return new PageInfoPanelFactory[] {
							new SwitchboardTypesPanelFactory(getNotificationPanel()),
							new SwitchboardAddonsPanelFactory(2, false)};
				} else {
					return new PageInfoPanelFactory[] {
							new SwitchboardTypesPanelFactory(getNotificationPanel()),
							new SwitchboardAddonsPanelFactory(1, true),
							new SwitchboardAddonsPanelFactory(2, false)};
				}
			} else {
//				return new PageInfoPanelFactory[] {
//						new SwitchboardTypesPanelFactory(getNotificationPanel()),
//						new SwitchboardAddonsPanelFactory(1, true)};

				if (MobileSession.get().getContract().isMobileOnlySolution()) {
					return new PageInfoPanelFactory[] {
							new SwitchboardTypesPanelFactory(getNotificationPanel())};
				} else {
					return new PageInfoPanelFactory[] {
							new SwitchboardTypesPanelFactory(getNotificationPanel()),
							new SwitchboardAddonsPanelFactory(1, true)};
				}
			}
		} else {
			return new PageInfoPanelFactory[] {
					new SwitchboardTypesPanelFactory(getNotificationPanel()),
					new SwitchboardAddonsPanelFactory(-1, true)};
		}
	}
}
