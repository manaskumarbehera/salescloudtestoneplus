package dk.jyskit.salescloud.application.pages.admin.productbundles;

import lombok.Data;
import dk.jyskit.salescloud.application.model.Amounts;
import dk.jyskit.salescloud.application.model.MobileProductBundle;
import dk.jyskit.salescloud.application.model.ProductBundle;

@Data
public class ProductBundleExtended extends MobileProductBundle {
	private long baseOneTimeFee;
	private long baseInstallationFee;
	private long baseRecurringFee;
	private long discountOneTimeFee;
	private long discountInstallationFee;
	private long discountRecurringFee;
	private MobileProductBundle bundle;

	public ProductBundleExtended(MobileProductBundle bundle) {
		this.bundle = bundle;
		this.baseOneTimeFee 			= bundle.getBaseAmounts().getOneTimeFee();
		this.baseInstallationFee 		= bundle.getBaseAmounts().getInstallationFee();
		this.baseRecurringFee 			= bundle.getBaseAmounts().getRecurringFee();
		this.discountOneTimeFee 		= bundle.getDiscountAmounts().getOneTimeFee();
		this.discountInstallationFee 	= bundle.getDiscountAmounts().getInstallationFee();
		this.discountRecurringFee 		= bundle.getDiscountAmounts().getRecurringFee();
	}

	public ProductBundle getProductBundle() {
		bundle.setBaseAmounts(new Amounts(baseOneTimeFee, baseInstallationFee, baseRecurringFee));
		bundle.setDiscountAmounts(new Amounts(discountOneTimeFee, discountInstallationFee, discountRecurringFee));
		return bundle;
	}
}
