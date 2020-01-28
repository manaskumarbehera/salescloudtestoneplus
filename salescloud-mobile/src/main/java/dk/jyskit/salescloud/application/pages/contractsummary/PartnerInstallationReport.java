package dk.jyskit.salescloud.application.pages.contractsummary;

import com.x5.template.Chunk;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.model.BusinessAreas;
import dk.jyskit.salescloud.application.model.PartnerData;

public class PartnerInstallationReport extends PartnerReport {
	public PartnerInstallationReport(String documentHeader) {
		super("one_partner_godt_igang", documentHeader, "one_tilbud_2019.jpg", false, false);
	}

	@Override
	int getVariant() {
		return PartnerData.VARIANT_INSTALLATION;
	}
}

//public class PartnerInstallationReport extends AbstractContractReport {
//
//	public PartnerInstallationReport() {
//		super(true);
//	}
//
//	@Override
//	protected void setProperties(Chunk html) {
////        html.set("is_offer", isOffer);
//	}
//
//	@Override
//	protected String getTitle() {
//		return "Installation";
//	}
//
//	@Override
//	protected String getTemplateName() {
//		return "one_partner_godt_igang";
//	}
//}
