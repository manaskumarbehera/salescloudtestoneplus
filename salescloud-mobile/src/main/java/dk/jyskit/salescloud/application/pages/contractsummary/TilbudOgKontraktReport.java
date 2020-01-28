package dk.jyskit.salescloud.application.pages.contractsummary;

import com.x5.template.Chunk;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.model.BusinessAreas;

public class TilbudOgKontraktReport extends AbstractContractReport {

	private boolean isOffer;

	public TilbudOgKontraktReport(boolean isOffer) {
		super(false);
		this.isOffer = isOffer;
	}

	@Override
	protected void setProperties(Chunk html) {
        html.set("is_offer", isOffer);
	}

	@Override
	protected String getTitle() {
        if (isOffer) {
       		return "Tilbud";
        } else {
        	if (BusinessAreas.WIFI == MobileSession.get().getContract().getBusinessArea().getBusinessAreaId()) {
        		return "Tilbud på TDC Erhverv Wi-Fi Intelligence";
        	} else {
            	return "Prisoverslag";
        	}
        }
	}

	@Override
	protected String getTemplateName() {
		if (MobileSession.get().isBusinessAreaOnePlus()) {
			return "tilbud_rammeaftale_og_bilag";
		} else if (BusinessAreas.MOBILE_VOICE == MobileSession.get().getContract().getBusinessArea().getBusinessAreaId()) {
			return "contract_finance_report";
		} else if (BusinessAreas.WIFI == MobileSession.get().getContract().getBusinessArea().getBusinessAreaId()) {
			return "tilbud_wifi";
		} else {
			return "tilbud_og_kontrakt";
		}
	}
}
