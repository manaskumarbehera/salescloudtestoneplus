package dk.jyskit.salescloud.application.services.supercontract;

import java.math.BigDecimal;

import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.DiscountPoint;
import dk.jyskit.salescloud.application.model.MobileContract;

public interface SuperContractService {
	DiscountPoint getDiscountPoint(BusinessArea businessArea, BigDecimal totalRecurringBeforeDiscounts,
								   MobileContract contract, boolean useNetworkMatrix);
}
