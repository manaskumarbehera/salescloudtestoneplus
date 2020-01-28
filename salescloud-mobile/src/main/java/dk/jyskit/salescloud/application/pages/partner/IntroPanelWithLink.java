package dk.jyskit.salescloud.application.pages.partner;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.links.file.AnyFileLink;
import dk.jyskit.salescloud.application.model.PageInfo;
import dk.jyskit.salescloud.application.pages.sales.existingcontract.ExistingContractPage;
import dk.jyskit.salescloud.application.pages.sales.panels.IntroPanel;
import org.apache.wicket.markup.html.link.Link;

public class IntroPanelWithLink extends IntroPanel {
	public IntroPanelWithLink(String id, PageInfo pageInfo) {
		super(id, pageInfo);

		add(new AnyFileLink("link", "documents/udstyrsprisliste_headsets_IP_udstyr_24_06_2019.xlsx", "Udstyrsprisliste.xlsx", "application/xlsx"));
	}
}
