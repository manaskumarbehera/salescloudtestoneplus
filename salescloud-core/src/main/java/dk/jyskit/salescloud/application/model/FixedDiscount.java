package dk.jyskit.salescloud.application.model;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class FixedDiscount extends DiscountScheme {
	@NonNull @NotNull
	private Amounts discountPercentages;
	
//	@Override
//	public Amounts calculateDiscountsForProduct(Product product, int count) {
//		if (product.isDiscountEligible()) {
//			return new Amounts(
//					count * product.getPrice().getAmounts()[0] * discountPercentages.getAmounts()[0] / 10000, 
//					count * product.getPrice().getAmounts()[1] * discountPercentages.getAmounts()[1] / 10000, 
//					count * product.getPrice().getAmounts()[2] * discountPercentages.getAmounts()[2] / 10000);
//		} else {
//			return new Amounts();
//		}
//	}
	
	@Override
	public Amounts calculateContractDiscountsForProduct(Product product, Amounts amountsBeforeCampaignDiscount, Amounts amountsAfterCampaignDiscount) {
		if (product.isDiscountEligible()) {
			return new Amounts(
					amountsAfterCampaignDiscount.getAmounts()[0] * discountPercentages.getAmounts()[0] / 10000, 
					amountsAfterCampaignDiscount.getAmounts()[1] * discountPercentages.getAmounts()[1] / 10000, 
					amountsAfterCampaignDiscount.getAmounts()[2] * discountPercentages.getAmounts()[2] / 10000);
		} else {
			return new Amounts();
		}
	}
	
//	public Amounts calculateDiscountsForProductBundle(ProductBundle productBundle, int count) {
//		if (productBundle.getAddToContractDiscount().intValue() == ProductBundle.FIXED_DISCOUNT_CONTRIBUTION) {
//			Amounts amounts = productBundle.getBaseAmounts().clone().subtract(productBundle.getDiscountAmounts());
//			return new Amounts(
//					count * amounts.getAmounts()[0] * discountPercentages.getAmounts()[0] / 10000, 
//					count * amounts.getAmounts()[1] * discountPercentages.getAmounts()[1] / 10000, 
//					count * amounts.getAmounts()[2] * discountPercentages.getAmounts()[2] / 10000);
//		} else {
//			return new Amounts(0, 0, 0);
//		}
//	}
	
	@Override
	public Amounts calculateContractDiscountsForProductBundle(ProductBundle productBundle, Amounts amountsBeforeCampaignDiscount, Amounts amountsAfterCampaignDiscount) {
		if (productBundle.getAddToContractDiscount().intValue() == ProductBundle.FIXED_DISCOUNT_CONTRIBUTION) {
			return new Amounts(
					round(amountsAfterCampaignDiscount.getAmounts()[0] * discountPercentages.getAmounts()[0] / 10000), 
					round(amountsAfterCampaignDiscount.getAmounts()[1] * discountPercentages.getAmounts()[1] / 10000), 
					round(amountsAfterCampaignDiscount.getAmounts()[2] * discountPercentages.getAmounts()[2] / 10000));
		} else {
			return new Amounts(0, 0, 0);
		}
	}
	
	// --------------------------------
	
	public DiscountScheme clone() {
		FixedDiscount discountScheme = new FixedDiscount();
		discountScheme.setName(getName());
		discountScheme.setDiscountPercentages(discountPercentages);
		return discountScheme;
	}
}
