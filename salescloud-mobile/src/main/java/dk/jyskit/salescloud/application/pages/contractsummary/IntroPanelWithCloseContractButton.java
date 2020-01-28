package dk.jyskit.salescloud.application.pages.contractsummary;

import org.apache.wicket.markup.html.link.Link;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.model.PageInfo;
import dk.jyskit.salescloud.application.pages.sales.existingcontract.ExistingContractPage;
import dk.jyskit.salescloud.application.pages.sales.panels.IntroPanel;

public class IntroPanelWithCloseContractButton extends IntroPanel {
	public IntroPanelWithCloseContractButton(String id, PageInfo pageInfo) {
		super(id, pageInfo);
		
		add(new Link<Void>("leaveContract") {
			@Override
			public void onClick() {
				CoreSession.get().setContract(null);
				setResponsePage(ExistingContractPage.class);
			}
		});
	}
}
