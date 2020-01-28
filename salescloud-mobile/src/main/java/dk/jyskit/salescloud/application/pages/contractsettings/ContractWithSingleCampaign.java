package dk.jyskit.salescloud.application.pages.contractsettings;

import java.io.Serializable;

import dk.jyskit.salescloud.application.model.MobileContract;
import dk.jyskit.salescloud.application.model.Product;
import lombok.Data;
import dk.jyskit.salescloud.application.model.Campaign;
import dk.jyskit.salescloud.application.model.Contract;

@Data
public class ContractWithSingleCampaign implements Serializable {
	private Contract contract;
	private Campaign campaign;

	// One+
	private Product installationTypeBusiness;
	private Product installationTypeUserProfiles;
	private Product serviceLevel;
}
