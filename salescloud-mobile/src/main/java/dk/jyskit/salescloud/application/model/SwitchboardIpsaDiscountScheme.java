package dk.jyskit.salescloud.application.model;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@Slf4j
public class SwitchboardIpsaDiscountScheme extends DiscountScheme {
	
	@NonNull @NotNull
	private Amounts discountPercentages = new Amounts();
	
	private int step = 0;

	public void prepare(Contract contract) {
		MobileContract mobileContract = (MobileContract) contract;

		long ipsaSum = mobileContract.getIpsaSumPrYear();
		
		int yearsIndex;
		switch(mobileContract.getAdjustedContractLength()) {
			case 1 :
				yearsIndex = 0;
				break;
			case 2 :
				yearsIndex = 1;
				break;
			default :
				yearsIndex = 2;
				break;
		}
		
		if (ipsaSum > 11520000) {
			discountPercentages.setRecurringFee((new long[] {2400, 2700, 3000})[yearsIndex]);
			step = 7;
		} else if (ipsaSum > 8660000) {
			discountPercentages.setRecurringFee((new long[] {1700, 2000, 2300})[yearsIndex]);
			step = 6;
		} else if (ipsaSum > 5760000) {
			discountPercentages.setRecurringFee((new long[] {1200, 1500, 1800})[yearsIndex]);
			step = 5;
		} else if (ipsaSum > 2880000) {
			discountPercentages.setRecurringFee((new long[] {800, 1100, 1400})[yearsIndex]);
			step = 4;
		} else if (ipsaSum > 1440000) {
			discountPercentages.setRecurringFee((new long[] {600, 900, 1200})[yearsIndex]);
			step = 3;
		} else if (ipsaSum > 720000) {
			discountPercentages.setRecurringFee((new long[] {500, 800, 1100})[yearsIndex]);
			step = 2;
		} else if (ipsaSum > 360000) {
			discountPercentages.setRecurringFee((new long[] {400, 700, 1000})[yearsIndex]);
			step = 1;
		}
	}
	
//	public Amounts calculateDiscountsForProduct(Product product, int count) {
//		if (((MobileProduct) product).isIpsaDiscountEligible()) {
//			return new Amounts(0, 0, count * product.getPrice().getRecurringFee() * discountPercentages.getRecurringFee() / 10000);
//		} else {
//			return new Amounts(0, 0, 0);
//		}
//	}
	
	@Override
	public Amounts calculateContractDiscountsForProduct(Product product, Amounts amountsBeforeCampaignDiscount, Amounts amountsAfterCampaignDiscount) {
		if (((MobileProduct) product).isIpsaDiscountEligible()) {
			return new Amounts(0, 0, amountsBeforeCampaignDiscount.getRecurringFee() * discountPercentages.getRecurringFee() / 10000);
		} else {
			return new Amounts(0, 0, 0);
		}
	}
	
//	public Amounts calculateDiscountsForProductBundle(ProductBundle productBundle, int count) {
//		if (productBundle.getAddToContractDiscount().intValue() == ProductBundle.IPSA_DISCOUNT_CONTRIBUTION) {
//			Amounts amounts = productBundle.getBaseAmounts().clone().subtract(productBundle.getDiscountAmounts());
//			return new Amounts(0, 0, round(count * amounts.getRecurringFee() * discountPercentages.getRecurringFee() / 10000));
//		} else {
//			return new Amounts(0, 0, 0);
//		}
//	}
	
	@Override
	public Amounts calculateContractDiscountsForProductBundle(ProductBundle productBundle, Amounts amountsBeforeCampaignDiscount, Amounts amountsAfterCampaignDiscount) {
		if (productBundle.getAddToContractDiscount().intValue() == ProductBundle.IPSA_DISCOUNT_CONTRIBUTION) {
			return new Amounts(0, 0, round(amountsAfterCampaignDiscount.getRecurringFee() * discountPercentages.getRecurringFee() / 10000));
		} else {
			return new Amounts(0, 0, 0);
		}
	}

	// --------------------------------

	public DiscountScheme clone() {
		SwitchboardIpsaDiscountScheme discountScheme = new SwitchboardIpsaDiscountScheme();
		discountScheme.setName(getName());
		discountScheme.setDiscountPercentages(discountPercentages);
		return discountScheme;
	}
}
