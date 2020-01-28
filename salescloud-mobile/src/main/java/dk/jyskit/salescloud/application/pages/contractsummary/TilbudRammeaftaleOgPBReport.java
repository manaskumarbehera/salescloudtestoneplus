package dk.jyskit.salescloud.application.pages.contractsummary;

import com.x5.template.Chunk;
import dk.jyskit.salescloud.application.MobileSession;

public class TilbudRammeaftaleOgPBReport extends ContractAcceptReport {


	public TilbudRammeaftaleOgPBReport(boolean offer, boolean rammeaftale, boolean bilagOne, boolean bilagNetwork) {
		super(offer, rammeaftale, bilagOne, bilagNetwork);
	}

	@Override
	protected void setProperties(Chunk html) {
		super.setProperties(html);
	}

	@Override
	protected String getTitle() {
        if (showOffer) {
       		return "Tilbud - Rammeaftale - PB";
        } else {
        	return "Rammeaftale - PB";
        }
	}

	@Override
	protected String getTemplateName() {
//		if (MobileSession.get().isBusinessAreaOnePlus() && isOffer) {
//			return "tilbud_og_kontrakt";
//		}
		return "tilbud_rammeaftale_og_bilag";
	}
}
