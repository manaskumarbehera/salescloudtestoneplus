package dk.jyskit.salescloud.application.pages.contractsummary;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.BusinessAreas;
import dk.jyskit.salescloud.application.model.PartnerData;

public class PartnerSupportOgRateAftaleReport extends PartnerReport {
	public PartnerSupportOgRateAftaleReport(String documentHeader, boolean includeSupport, boolean includeHardware) {
		super(includeSupport ? "partner_support_og_rate_aftale" : "rate_hardware", documentHeader, getFileName(MobileSession.get().getBusinessArea()), includeSupport, includeHardware);
	}
	
	private static String getFileName(BusinessArea businessArea) {
		switch (businessArea.getBusinessAreaId()) {
		case BusinessAreas.MOBILE_VOICE:
			return "support_og_rate_aftale_mobile.png";
		case BusinessAreas.SWITCHBOARD:
			return "support_og_rate_aftale_omstilling.png";
		case BusinessAreas.FIBER:
			return "support_og_rate_aftale_fiber.png";
		case BusinessAreas.FIBER_ERHVERV:
			return "support_og_rate_aftale_fiber.png";
		case BusinessAreas.WIFI:
			return "support_og_rate_aftale_wifi.png";
		case BusinessAreas.TDC_WORKS:
			return "support_og_rate_aftale_works.png";
		case BusinessAreas.ONE_PLUS:
			return "one_tilbud_2019.jpg";
//			return "support_og_rate_aftale_one.png";
		}
		return "??";
	}

	@Override
	int getVariant() {
		return PartnerData.VARIANT_SUPPORT_OG_RATEAFTALE;
	}
}
