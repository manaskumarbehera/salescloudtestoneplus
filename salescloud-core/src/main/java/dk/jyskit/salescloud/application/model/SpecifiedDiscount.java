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
public class SpecifiedDiscount extends DiscountScheme {
	@NonNull @NotNull
	private Amounts discountPercentages;
	
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
	
	@Override
	public Amounts calculateContractDiscountsForProductBundle(ProductBundle productBundle, Amounts amountsBeforeCampaignDiscount, Amounts amountsAfterCampaignDiscount) {
		if (productBundle.getAddToContractDiscount().intValue() == ProductBundle.FIXED_DISCOUNT_CONTRIBUTION) {
			return new Amounts(
					amountsAfterCampaignDiscount.getAmounts()[0] * discountPercentages.getAmounts()[0] / 10000, 
					amountsAfterCampaignDiscount.getAmounts()[1] * discountPercentages.getAmounts()[1] / 10000, 
					amountsAfterCampaignDiscount.getAmounts()[2] * discountPercentages.getAmounts()[2] / 10000);
		} else {
			return new Amounts(0, 0, 0);
		}
	}
	
	// --------------------------------
	
	public DiscountScheme clone() {
		SpecifiedDiscount discountScheme = new SpecifiedDiscount();
		discountScheme.setName(getName());
		discountScheme.setDiscountPercentages(discountPercentages);
		return discountScheme;
	}
}
