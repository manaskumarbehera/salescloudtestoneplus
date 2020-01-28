package dk.jyskit.salescloud.application.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import dk.jyskit.salescloud.application.extensionpoints.OrderLineCountModifier;
import dk.jyskit.waf.application.model.BaseEntity;
import dk.jyskit.waf.utils.guice.Lookup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Entity(name="OrderLine")
@Table(name = "orderline")
@Data
@EqualsAndHashCode(callSuper=true, of={})
@NoArgsConstructor
@Slf4j
public class OrderLine extends BaseEntity {
	/* An orderline refers either to a specific product or a bundle */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCT_ID")
	private Product product;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BUNDLE_ID")
	private ProductBundle bundle;
	
	// Both product and bundle-based orderlines can use a subIndex for advanced calculation of price, etc.
	private Integer subIndex;
	
//	@Column(name="itemcount")   // Fixes problem with reserved word: "count"
//	private int count;

	// "Input counts":
	
	@Column(name="count_new")   
	private int countNew;
	
	@Column(name="count_existing")   
	private int countExisting;
	
//	// "Output" counts:
//	
//	@Column(name="count_onetime")   
//	private int countOneTime;
//	
//	@Column(name="count_installation")   
//	private int countInstallation;
//	
//	@Column(name="count_nonrecurring")   
//	private int countRecurring;
	
	private boolean customFlag; 		// What "customFlag" represents is application specific
	private boolean customFlag1; 		// What "customFlag" represents is application specific - partner installation
	
	@NonNull
	@ManyToOne(optional = false)
	private Contract contract;
	
	// --------------------------------

//	public OrderLine(Product product, int countNew, int countExisting) {
//		this.product = product;
//		this.countNew = countNew;
//		this.countExisting = countExisting;
//	}
//
//	public OrderLine(ProductBundle productBundle, int countNew, int countExisting) {
//		this.bundle = productBundle;
//		this.countNew = countNew;
//		this.countExisting = countExisting;
//	}
	
	public OrderLine(Product product, Integer subIndex, int countNew, int countExisting) {
		this.product = product;
		this.countNew = countNew;
		this.countExisting = countExisting;
		this.subIndex = subIndex;
	}

	public OrderLine(ProductBundle productBundle, int Integer, int countNew, int countExisting) {
		this.bundle = productBundle;
		this.countNew = countNew;
		this.countExisting = countExisting;
		this.subIndex = subIndex;
	}
	
	// --------------------------------
	
	@Transient
	public OrderLineCount getDeferredCount() {
		return Lookup.lookup(OrderLineCountModifier.class).modifyCount(this);
	}
	
	@Transient
	public int getTotalCount() {
		return countNew + countExisting;
	}

//	@Transient
//	public int getDeferredOneTimeCount() {
//		return getDeferredCount().getCountNew();
//	}
//
//	@Transient
//	public int getDeferredInstallationCount() {
//		return getDeferredCount().getCountNew();
//	}
//
//	@Transient
//	public int getDeferredRecurringCount() {
//		return getDeferredCount().getCountTotal();
//	}

	@Transient
	public Amounts getAmounts(boolean afterCampaignDiscounts, boolean afterContractDiscounts, ProductAndBundleFilter filter) {
		Amounts amounts = new Amounts();
		if (getTotalCount() != 0) {
			if (product != null && filter.acceptProduct(product)) {
				amounts.add(product.getAmounts(getDeferredCount(), afterCampaignDiscounts, afterContractDiscounts, contract));
			} else if (bundle != null && filter.acceptProductBundle(bundle)) {
				amounts.add(bundle.getAmounts(getDeferredCount(), false, afterCampaignDiscounts, afterContractDiscounts, contract));
			}
		}
		return amounts;
	}

	@Transient
	public Amounts getContractDiscounts() {
		Amounts discounts = new Amounts();
		if (getTotalCount() != 0) {
			if (product != null) {
				Amounts amountsBeforeCampaignDiscounts = product.getAmounts(getDeferredCount(), false, false, contract);
				Amounts amountsAfterCampaignDiscounts = amountsBeforeCampaignDiscounts.subtract(product.getCampaignDiscounts(contract, getDeferredCount()));
				discounts.add(product.getContractDiscounts(contract, amountsBeforeCampaignDiscounts, amountsAfterCampaignDiscounts));
			} else if (bundle != null) {
//				Amounts amountsBeforeCampaignDiscounts 	= bundle.getAmounts(getDeferredCount(), false, false, false, contract);
//				Amounts amountsAfterCampaignDiscounts 	= bundle.getAmounts(getDeferredCount(), false, true, false, contract);
				Amounts amountsBeforeCampaignDiscounts 	= bundle.getAmounts(getDeferredCount(), true, false, false, contract);
				Amounts amountsAfterCampaignDiscounts 	= bundle.getAmounts(getDeferredCount(), true, true, false, contract);

//				if (getDeferredCount().getCountNew() > 0) {
//					log.info("");
//				}
				discounts.add(bundle.getContractDiscounts(contract, getDeferredCount(), amountsBeforeCampaignDiscounts, amountsAfterCampaignDiscounts));
			} else {
				log.warn("orderline with neither product or bundle!");
			}
		}
		return discounts;
	}

	@Transient
	public Amounts getCampaignDiscounts() {
		Amounts discounts = new Amounts();
		if (getTotalCount() != 0) {
			if (product != null) {
				discounts.add(product.getCampaignDiscounts(contract, getDeferredCount()));
			} else if (bundle != null) {
				discounts.add(bundle.getCampaignDiscounts(contract, getDeferredCount()));
			} else {
				log.warn("orderline with neither product or bundle!");
			}
		}
		return discounts;
	}

	@Transient
	public Amounts getAmountsBeforeDiscounts(ProductAndBundleFilter filter) {
		Amounts amounts = new Amounts();
		if (getTotalCount() != 0) {
			if (product != null && filter.acceptProduct(product)) {
				amounts.add(product.getAmountsBeforeDiscounts(getDeferredCount(), contract));
			} else if (bundle != null && filter.acceptProductBundle(bundle)) {
				amounts.add(bundle.getAmountsBeforeDiscounts(getDeferredCount(), contract));
			} else {
				if (product == null && bundle == null) {
					log.warn("orderline with neither product or bundle!");
				}
			}
		}
		return amounts;
	}

	// Return the product group associated with the orderline
	public ProductGroup getProductGroup() {
		if (product != null) {
			return product.getProductGroup();
		} else {
			// I hope this is ALWAYS ok to do?!
			return bundle.getProducts().get(0).getProduct().getProductGroup();
		}
	}

	@Transient
	public boolean isInstallationHandledByTdc() {
		if (bundle == null) {
			return product.isInstallationHandledByTdc(subIndex);
		} else {
			return bundle.isInstallationHandledByTdc(subIndex);
		}
	}
}
