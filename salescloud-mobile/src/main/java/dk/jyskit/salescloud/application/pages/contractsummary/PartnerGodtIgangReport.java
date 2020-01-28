package dk.jyskit.salescloud.application.pages.contractsummary;

import dk.jyskit.salescloud.application.model.PartnerData;

public class PartnerGodtIgangReport extends PartnerReport {
	public PartnerGodtIgangReport(String documentHeader) {
		super("partner_godt_igang", documentHeader, "partner_godt_igang.png", true, true);
	}

	@Override
	int getVariant() {
		return PartnerData.VARIANT_GODT_IGANG;
	}
}
