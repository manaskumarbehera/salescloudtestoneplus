package dk.jyskit.salescloud.application.services.supercontract;

import java.math.BigDecimal;

import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.DiscountPoint;
import dk.jyskit.salescloud.application.model.MobileCampaign;
import dk.jyskit.salescloud.application.model.MobileContract;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeSuperContractService implements SuperContractService {

	@Override
	public DiscountPoint getDiscountPoint(BusinessArea businessArea, BigDecimal totalRecurringBeforeDiscounts, 
										  MobileContract contract, boolean useNetworkMatrix) {
		if (totalRecurringBeforeDiscounts.longValue() < 4000000) {
			return createDiscountPoint(businessArea, contract, 0, useNetworkMatrix);
		} else if (totalRecurringBeforeDiscounts.longValue() < 8000000) {
			return createDiscountPoint(businessArea, contract, 1, useNetworkMatrix);
		} else if (totalRecurringBeforeDiscounts.longValue() < 18000000) {
			return createDiscountPoint(businessArea, contract, 2, useNetworkMatrix);
		} else if (totalRecurringBeforeDiscounts.longValue() < 35000000) {
			return createDiscountPoint(businessArea, contract, 3, useNetworkMatrix);
		} else if (totalRecurringBeforeDiscounts.longValue() < 70000000) {
			return createDiscountPoint(businessArea, contract, 4, useNetworkMatrix);
		} else {
			return createDiscountPoint(businessArea, contract, 5, useNetworkMatrix);
		}  
	}

	private DiscountPoint createDiscountPoint(BusinessArea businessArea, MobileContract contract, int step, 
											  boolean useNetworkMatrix) {
		DiscountPoint discountPoint = new DiscountPoint();
//		discountPoint.setDivisor(100);
		discountPoint.setStep(step);

		discountPoint.setYearIndex(Math.max(0, contract.getAdjustedContractLength()-1));

		try {
			String matrix = (useNetworkMatrix ? businessArea.getStandardDiscountMatrixNetwork() : businessArea.getStandardDiscountMatrix());
			String[] divisorAnMatrix = matrix.split("/");
			if (divisorAnMatrix.length == 2) {
//				discountPoint.setDivisor(Long.valueOf(divisorAnMatrix[0]));
				matrix = divisorAnMatrix[1];
			}

			int contractLength = contract.getAdjustedContractLength();
			if (useNetworkMatrix) {
				contractLength = contract.getAdjustedContractLengthNetwork();
			}
			String[] years = matrix.split("#");
			String discountExpression = contractLength==0 ? "0" : years[contractLength-1].split(",")[step].trim();
			if (discountExpression.endsWith("%")) {
				discountPoint.setDiscountPercentage(Long.valueOf(discountExpression.substring(0, discountExpression.length()-1)));
			} else {
				discountPoint.setDiscountPercentage(Long.valueOf(discountExpression));
			}
		} catch (Exception e) {
			log.error("Problem with standardDiscountMatrix", e);
		}
		return discountPoint;
	}
}
