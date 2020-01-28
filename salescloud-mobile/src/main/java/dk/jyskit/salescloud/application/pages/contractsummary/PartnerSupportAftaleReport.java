package dk.jyskit.salescloud.application.pages.contractsummary;

import dk.jyskit.salescloud.application.model.PartnerData;

public class PartnerSupportAftaleReport extends PartnerReport {
	public PartnerSupportAftaleReport(String documentHeader) {
		super("partner_support_aftale", documentHeader, "one_tilbud_2019.jpg", true, true);
	}

	@Override
	int getVariant() {
		return PartnerData.VARIANT_SUPPORTAFTALE;
	}
}
