package dk.jyskit.salescloud.application.model;

import javax.persistence.*;

import dk.jyskit.salescloud.application.CoreSession;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
//@NoArgsConstructor
@Slf4j
public class RabatAftaleDiscountScheme extends DiscountScheme {

	@Embedded
	private DiscountPoint discountPointNonNetwork;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name="discountPercentage", column=@Column(name = "DP")),
			@AttributeOverride(name="discountAmount", column=@Column(name = "DA")),
			@AttributeOverride(name="step", column=@Column(name = "ST")),
			@AttributeOverride(name="yearIndex", column=@Column(name = "YI"))
    })
	private DiscountPoint discountPointNetwork;

	private MobileContract mobileContract;
//	private long divisor = 100;

	public RabatAftaleDiscountScheme() {
	}

	public void setDiscountPoint(DiscountPoint discountPointNonNetwork, DiscountPoint discountPointNetwork) {
		this.discountPointNonNetwork = discountPointNonNetwork;
		this.discountPointNetwork = discountPointNetwork;
	}

	public void prepare(Contract contract) {
		mobileContract = (MobileContract) contract;
		discountPointNonNetwork  	= CoreSession.get().getDiscountPointNonNetwork();
		discountPointNetwork  		= CoreSession.get().getDiscountPointNetwork();
	}

	@Override
	public Amounts calculateContractDiscountsForProduct(Product product, Amounts amountsBeforeCampaignDiscount, Amounts amountsAfterCampaignDiscount) {
		boolean isNetworkProduct = ((MobileProduct) product).isNetworkProduct();
		DiscountPoint discountPoint = isNetworkProduct ? discountPointNetwork : discountPointNonNetwork;

		MobileCampaign campaign = (MobileCampaign) mobileContract.getCampaigns().get(0);
		if (!campaign.isDisableContractDiscount()) {
			if (((MobileProduct) product).isRabataftaleDiscountEligible() && (discountPoint != null)) {
//				return new Amounts(0, 0, amountsBeforeCampaignDiscount.getRecurringFee() * discountPoint.getDiscountPercentage() / (100 * divisor));
				return new Amounts(0, 0, amountsBeforeCampaignDiscount.getRecurringFee() * discountPoint.getDiscountPercentage() / 10000);
			}
		}
		return new Amounts(0, 0, 0);
	}

	@Override
	public Amounts calculateContractDiscountsForProductBundle(ProductBundle productBundle, Amounts amountsBeforeCampaignDiscount, Amounts amountsAfterCampaignDiscount) {
		boolean isNetworkProductBundle = ((MobileProductBundle) productBundle).isNetworkProductBundle();
		DiscountPoint discountPoint = isNetworkProductBundle ? discountPointNetwork : discountPointNonNetwork;

		MobileCampaign campaign = (MobileCampaign) mobileContract.getCampaigns().get(0);
		if (!campaign.isDisableContractDiscount()) {
			if ((productBundle.getAddToContractDiscount().intValue() == ProductBundle.RABATAFTALE_DISCOUNT_CONTRIBUTION) && (discountPoint != null)) {
//				return new Amounts(0, 0, round(amountsBeforeCampaignDiscount.getRecurringFee() * discountPoint.getDiscountPercentage() / (100 * divisor)));	// Correct to use before-campaign?
				return new Amounts(0, 0, round(amountsBeforeCampaignDiscount.getRecurringFee() * discountPoint.getDiscountPercentage() / 10000));	// Correct to use before-campaign?
			}
		}
		return new Amounts(0, 0, 0);
	}
}
