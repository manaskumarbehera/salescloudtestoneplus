package dk.jyskit.salescloud.application.model;

import java.util.ArrayList;
import java.util.List;

import javax.management.relation.RelationType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import dk.jyskit.waf.application.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Entity(name="ProductBundle")
@Table(name = "productbundle")
@Data
@EqualsAndHashCode(callSuper=true, of={"publicName", "internalName"})
@RequiredArgsConstructor
@NoArgsConstructor
@ToString(of={"publicName"})
@Slf4j
public class ProductBundle extends BaseEntity {
	public final static int FIXED_DISCOUNT_CONTRIBUTION = 1;
	public final static int IPSA_DISCOUNT_CONTRIBUTION = 2;
	public final static int RABATAFTALE_DISCOUNT_CONTRIBUTION = 3;
	
	@NonNull @NotNull
	@Column(length=150)
	private String publicName;
	
	@NonNull @NotNull
	@Column(length=150)
	private String internalName;
	
	private long sortIndex;

	@JsonIgnore
	@ManyToOne(optional = true)
	private Contract contract;

	@JsonIgnore
	@ManyToOne(optional = true)
	private Campaign campaign;
	
	private boolean active = true;
	
	private boolean addProductPrices = true;
	
	/*
	 * If this flag is true, product prices will be added to the bundle price. 
	 * If not, product prices will be added as separate order line(s).
	 * This does not apply to CDM output.
	 */
	private boolean addProductPricesToBundlePrice = true;
	
	private Integer addToContractDiscount = 0;	// 0: None, 1: Fixed discount scheme, 2: IPSA discount scheme, 3: TEM5 contract discount scheme

	@Column(length=500)
	private String rabataftaleCampaignDiscountMatrix;

	@JsonIgnore
	public String getRabataftaleCampaignDiscountMatrix() {
		return rabataftaleCampaignDiscountMatrix;
	}

	public void setRabataftaleCampaignDiscountMatrix(String s) {
		rabataftaleCampaignDiscountMatrix = s;
	}

	@Column(length=500)
	private String rabataftaleCampaignDiscountMatrixNetwork;

	@JsonIgnore
	public String getRabataftaleCampaignDiscountMatrixNetwork() {
		return rabataftaleCampaignDiscountMatrixNetwork;
	}

	public void setRabataftaleCampaignDiscountMatrixNetwork(String s) {
		rabataftaleCampaignDiscountMatrixNetwork = s;
	}

	/*
	 * Calculation of bundle price:
	 * A bundles price (before discount) is the sum of the bundles base price + the price of any products tagged to be
	 * included in pricing of the bundle.     
	 * A bundles price (after discount) is the above mentioned bundle price - discount.
	 */
	
	// Campaign discounts
	private Amounts discountAmounts = new Amounts();	

	// NEW !!!
	@AttributeOverrides({
//	    @AttributeOverride(name="NUM_VALUES", column= @Column(name="p_num_values")),
	    @AttributeOverride(name="amounts", column= @Column(name="p_amounts"))
	  })
	@Embedded
	private Amounts baseAmounts = new Amounts();	
	
	/* Many-to-many relation */
	@OneToMany(mappedBy = "productBundle", cascade = CascadeType.ALL, orphanRemoval = true)
	protected List<BundleProductRelation> products = new ArrayList<>();
	
	// --------------------------------
	
	public void addProductRelation(BundleProductRelation relation) {
		relation.setProductBundle(this);  // Just to be sure
		products.add(relation);
	}
	
	public void removeProductRelation(Product product) {
		for (BundleProductRelation relation : products) {
			if (product.equals(relation.getProduct())) {
				products.remove(relation);
				break;
			}
		}
	}
	
	// --------------------------------
	
	public boolean hasRelationToProduct(Product product) {
		for (BundleProductRelation relation : products) {
			if (product.equals(relation.getProduct())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasRelationToProduct(String productId) {
		for (BundleProductRelation relation : products) {
			if ((relation.getProduct() != null) && productId.equals(relation.getProduct().getProductId())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasRelationToProduct(Long id) {
		for (BundleProductRelation relation : products) {
			if ((relation.getProduct() != null) && id.equals(relation.getProduct().getId())) {
				return true;
			}
		}
		return false;
	}
	
	@Transient
	@JsonIgnore
	public boolean isCampaignBundle() {
		return (campaign != null);
	}
	
	@Transient
	@JsonIgnore
	public boolean isContractBundle() {
		return (contract != null);
	}
	
	@Transient
	@JsonIgnore
	public long getOneTimeFeeDiscount() {
		return discountAmounts.getOneTimeFee();
	}

	/**
	 * @param oneTimeFeeDiscount amount * 100
	 */
	public void setOneTimeFeeDiscount(long oneTimeFeeDiscount) {
		discountAmounts.getAmounts()[FeeCategory.ONETIME_FEE.getFromIndex()] = oneTimeFeeDiscount;
	}

	@JsonIgnore
	@Transient
	public long getInstallationFeeDiscount() {
		return discountAmounts.getInstallationFee();
	}

	/**
	 * @param installationFeeDiscount amount * 100
	 */
	public void setInstallationFeeDiscount(long installationFeeDiscount) {
		discountAmounts.getAmounts()[FeeCategory.INSTALLATION_FEE.getFromIndex()] = installationFeeDiscount;
	}

	@Transient
	@JsonIgnore
	public long getRecurringFeeDiscount() {
		return discountAmounts.getRecurringFee();
	}

	/**
	 * @param recurringFeeDiscount amount * 100
	 */
	public void setRecurringFeeDiscount(long recurringFeeDiscount) {
		discountAmounts.getAmounts()[FeeCategory.RECURRING_FEE.getFromIndex()] = recurringFeeDiscount;
	}

	@Transient
	public Amounts getAmountsBeforeDiscounts(OrderLineCount count, Contract contract) {
		Amounts amounts = getBaseAmounts().clone();
		if (!amounts.isAllZero()) {
			for (FeeCategory category : new FeeCategory[] {FeeCategory.INSTALLATION_FEE, FeeCategory.ONETIME_FEE, FeeCategory.RECURRING_FEE}) {
				amounts.getAmounts()[category.getFromIndex()] 	*= count.getCountForFeeCategory(category); 
			}
		}
		if (isAddProductPrices()) {
			for (BundleProductRelation relation : products) {
				if (!relation.getProductAccessType().equals(ProductAccessType.SEPARATE_COUNT)) {  // ignore everything but INCLUDED?
					if ((relation.getProduct() != null) && relation.isAddProductPrice()) {
						amounts.add(relation.getProduct().getAmounts(count, false, false, contract));
					}
				}
			}
		}
		return amounts;
	}
	
	public Amounts getAmounts(OrderLineCount count, boolean bundleOnly, boolean deductCampaignDiscount, boolean deductContractDiscount, Contract contract) {
		Amounts beforeDiscountAmounts = getBaseAmounts().clone();
		
		if (!beforeDiscountAmounts.isAllZero()) {
			for (FeeCategory category : new FeeCategory[] {FeeCategory.INSTALLATION_FEE, FeeCategory.ONETIME_FEE, FeeCategory.RECURRING_FEE}) {
				beforeDiscountAmounts.getAmounts()[category.getFromIndex()] 	*= count.getCountForFeeCategory(category); 
			}
		}
		
		if (!bundleOnly && isAddProductPrices()) {
			for (BundleProductRelation relation : products) {
				if (!relation.getProductAccessType().equals(ProductAccessType.SEPARATE_COUNT)) {  // ignore everything but INCLUDED?
					if ((relation.getProduct() != null) && relation.isAddProductPrice()) {
						beforeDiscountAmounts.add(relation.getProduct().getAmounts(count, false, false, contract));
					}
				}
			}
		}
		Amounts totals = beforeDiscountAmounts.clone();
		
		Amounts campaignDiscounts;
		if (contract.getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CAMPAIGN_DISCOUNT)) {
			if (!StringUtils.isEmpty(rabataftaleCampaignDiscountMatrix)) {
				Amounts nonRecurringDiscounts = discountAmounts.clone();
				nonRecurringDiscounts.multiplyBy(count);
//			nonRecurringDiscounts.multiplyBy(count.getCountNew());
				campaignDiscounts = contract.getRabataftaleCampaignDiscounts(totals, rabataftaleCampaignDiscountMatrix, nonRecurringDiscounts, false);
			} else {
				campaignDiscounts = new Amounts();
			}
		} else {
			campaignDiscounts = discountAmounts.clone();    // Non-TEM5 campaign discount - i.e. "the old" campaign discount strategy 
		}
		
		if (deductCampaignDiscount) {
			totals.subtract(campaignDiscounts);
		}
		
		if (deductContractDiscount) {
			if (contract.getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CAMPAIGN_DISCOUNT) && !StringUtils.isEmpty(rabataftaleCampaignDiscountMatrix)) {
//				// Only apply contract discount if there is no campaign discount
//				if (campaignDiscounts.isAllZero()) {
					totals.subtract(getContractDiscounts(contract, count, beforeDiscountAmounts, beforeDiscountAmounts.clone().subtract(campaignDiscounts)));
//				}
			} else {
				totals.subtract(getContractDiscounts(contract, count, beforeDiscountAmounts, beforeDiscountAmounts.clone().subtract(campaignDiscounts)));
			}
		}
		
//		if (!totals.isAllZero()) {
//			System.out.println("Bundle - Added amounts: " + totals.toString());
//			System.out.println("");
//		}
		
		if (bundleOnly) {
			return totals;
		} else {
			return totals.nonNegative();
		}
	}

//	public Amounts getBundleOnlyAmounts(int count, boolean afterContractDiscounts, Contract contract) {
//		Amounts totals = getBaseAmounts().clone().subtract(getDiscountAmounts());
//		totals.multiplyBy(count);
//		
//		if (afterContractDiscounts) {
//			totals.subtract(getContractDiscountsBundleOnly(count, contract));
//		}
//		return totals;
//	}

	public Amounts getBundleOnlyBaseAmounts(OrderLineCount count) {
		Amounts totals = getBaseAmounts().clone();
		totals.multiplyBy(count);
		return totals;
	}

	public Amounts getBundleWithProductsBaseAmounts(OrderLineCount count) {
		Amounts totals = getBaseAmounts().clone();

		for (BundleProductRelation productRelation: getProducts()) {
			if (!productRelation.getProductAccessType().equals(ProductAccessType.SEPARATE_COUNT)) {
				totals.add(productRelation.getProduct().getPrice());
			}
		}

		totals.multiplyBy(count);
		return totals;
	}

	public Amounts getBundleOnlyCampaignDiscountAmounts(Contract contract, OrderLineCount count) {
		if (contract.getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CAMPAIGN_DISCOUNT)) {
			Amounts nonRecurringDiscounts = getDiscountAmounts().clone();
			nonRecurringDiscounts.multiplyBy(count);
			
//			Amounts amountsBeforeDiscounts = getAmountsBeforeDiscounts(count, contract);
			Amounts bundleOnlyAmountsBeforeCampaignDiscount = getBaseAmounts().clone();
			bundleOnlyAmountsBeforeCampaignDiscount.multiplyBy(count);
			return contract.getRabataftaleCampaignDiscounts(bundleOnlyAmountsBeforeCampaignDiscount, rabataftaleCampaignDiscountMatrix, nonRecurringDiscounts, false);
		} else {
			Amounts totals = getDiscountAmounts().clone();
			totals.multiplyBy(count);
			return totals;
		}
	}

	public Amounts getBundleWithProductsCampaignDiscountAmounts(Contract contract, OrderLineCount count) {
		if (contract.getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CAMPAIGN_DISCOUNT)) {
			Amounts nonRecurringDiscounts = getDiscountAmounts().clone();
			nonRecurringDiscounts.multiplyBy(count);

			Amounts bundleWithProductsAmountsBeforeCampaignDiscount = getBundleWithProductsBaseAmounts(count);

			bundleWithProductsAmountsBeforeCampaignDiscount.multiplyBy(count);
			return contract.getRabataftaleCampaignDiscounts(bundleWithProductsAmountsBeforeCampaignDiscount, rabataftaleCampaignDiscountMatrix, nonRecurringDiscounts, false);
		} else {
			Amounts totals = getDiscountAmounts().clone();
			totals.multiplyBy(count);
			return totals;
		}
	}

	public Amounts getBundleOnlyAmountsAfterAllDiscounts(OrderLineCount count, Contract contract) {
		if (contract.getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CAMPAIGN_DISCOUNT)) {
			Amounts bundleOnlyAmountsBeforeCampaignDiscount = getBaseAmounts().clone();
			bundleOnlyAmountsBeforeCampaignDiscount.multiplyBy(count);
			Amounts bundleOnlyCampaignDiscountAmounts = getBundleOnlyCampaignDiscountAmounts(contract, count);
//			Amounts compleBundleAmountsBeforeDiscounts = getAmountsBeforeDiscounts(count, contract);
			Amounts bundleOnlyContractDiscountAmounts = getContractDiscountsBundleOnly(contract, bundleOnlyAmountsBeforeCampaignDiscount.clone(), bundleOnlyAmountsBeforeCampaignDiscount.clone().subtract(bundleOnlyCampaignDiscountAmounts));
			return bundleOnlyAmountsBeforeCampaignDiscount.clone().subtract(bundleOnlyCampaignDiscountAmounts).subtract(bundleOnlyContractDiscountAmounts);
		} else {
//			Amounts totals = getDiscountAmounts().clone();
//			totals.multiplyBy(count);
//			return totals;
			Amounts amountsBeforeCampaignDiscount = getBaseAmounts().clone();
			Amounts amountsAfterCampaignDiscount = amountsBeforeCampaignDiscount.clone().subtract(getDiscountAmounts());  
			amountsAfterCampaignDiscount.multiplyBy(count);
			return amountsAfterCampaignDiscount.subtract(getContractDiscountsBundleOnly(contract, amountsBeforeCampaignDiscount, amountsAfterCampaignDiscount));
		}
	}
	
	public Amounts getTotalBundleAmountsAfterAllDiscounts(OrderLineCount count, Contract contract) {
		return getAmounts(count, false, true, true, contract);
//		if (contract.getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CAMPAIGN_DISCOUNT)) {
//			return getAmounts(count, false, true, true, contract);
////			Amounts totalBundleAmountsBeforeDiscounts = getAmountsBeforeDiscounts(count, contract);
////			Amounts bundleOnlyCampaignDiscountAmounts = getBundleOnlyCampaignDiscountAmounts(contract, count);
////			
////			Amounts bundleOnlyContractDiscountAmounts = getContractDiscountsBundleOnly(contract, bundleOnlyAmountsBeforeCampaignDiscount.clone(), bundleOnlyAmountsBeforeCampaignDiscount.clone().subtract(bundleOnlyCampaignDiscountAmounts));
////			return bundleOnlyAmountsBeforeCampaignDiscount.clone().subtract(bundleOnlyCampaignDiscountAmounts).subtract(bundleOnlyContractDiscountAmounts);
//		} else {
//			Amounts amountsBeforeCampaignDiscount = getBaseAmounts().clone();
//			Amounts amountsAfterCampaignDiscount = amountsBeforeCampaignDiscount.clone().subtract(getDiscountAmounts());  
//			amountsAfterCampaignDiscount.multiplyBy(count);
//			return amountsAfterCampaignDiscount.subtract(getContractDiscountsBundleOnly(contract, amountsBeforeCampaignDiscount, amountsAfterCampaignDiscount));
//		}
	}
	
//	/**
//	 * Calculate contract discounts for the amounts. Normally we will pass amounts
//	 * corresponding to base amounts - campaign amounts, but there are special cases where
//	 * call this method to calculate contract discounts on base amounts OR campaign amounts.
//	 * 
//	 * @param contract
//	 * @param amountsAfterCampaignDiscount
//	 * @return
//	 */
//	@Transient
//	// DUR IKKE!!!
//	public Amounts getContractDiscountsOld(Contract contract, Amounts amounts) {
//		Amounts contractDiscounts = new Amounts();
//		
//		
//		if (isAddProductPrices()) {
//			for (BundleProductRelation relation : products) {
//				if (relation.isAddProductPrice()) {
//					contractDiscounts.add(relation.getProduct().getContractDiscounts(contract, amounts));
//				}
//			}
//		}
//		
//		
//		for (DiscountScheme discountScheme : contract.getDiscountSchemes()) {
//			contractDiscounts.add(discountScheme.calculateContractDiscountsForProductBundle(this, amounts));
//		}
//		return contractDiscounts;
//	}
	
	@Transient
	public Amounts getContractDiscounts(Contract contract, OrderLineCount count, Amounts amountsBeforeCampaignDiscount, Amounts amountsAfterCampaignDiscount) {
		Amounts totalDiscounts = new Amounts();
		
		for (DiscountScheme discountScheme : contract.getDiscountSchemes()) {
			if (contract.getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CONTRACT_DISCOUNT)) {
				Amounts bundleOnlyAmounts = getBaseAmounts().clone();
				bundleOnlyAmounts.multiplyBy(count);
				totalDiscounts.add(discountScheme.calculateContractDiscountsForProductBundle(this, bundleOnlyAmounts, bundleOnlyAmounts));        
			} else {
//				Amounts bundleTotals = getBaseAmounts().clone().subtract(discountAmounts);	// This seems odd?
////			bundleTotals.multiplyBy(count.getCountTotal());
//			bundleTotals.multiplyBy(count);
				totalDiscounts.add(discountScheme.calculateContractDiscountsForProductBundle(this, amountsBeforeCampaignDiscount, amountsAfterCampaignDiscount));        
			}
		}

//		// I do not understand why this IF is necessary!!!!!!
//		if (!contract.getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CONTRACT_DISCOUNT)) {
			if (isAddProductPrices()) {
				for (BundleProductRelation relation : products) {
					if (relation.getProduct() == null) {
						log.error("Problem with a productrelation: " + relation.getProductBundleId() + "/" + relation.getProductId());
					} else {
						if (relation.isAddProductPrice()) {
							// Maybe this causes the addon to be disregarded entirely. If so get actual orderlines for subindex and use for count
							if (!relation.getProductAccessType().equals(ProductAccessType.SEPARATE_COUNT)) {
								Amounts beforeCampaignDiscountAmounts = new Amounts();
								Product product = relation.getProduct();
								beforeCampaignDiscountAmounts.add(product.getPrice());
								beforeCampaignDiscountAmounts.multiplyBy(count);

								Amounts afterCampaignDiscountAmounts = beforeCampaignDiscountAmounts.clone().subtract(product.getCampaignDiscounts(contract, count));

								totalDiscounts.add(product.getContractDiscounts(contract, beforeCampaignDiscountAmounts, afterCampaignDiscountAmounts));
							}
						}
					}
				}
			}
//		}
		return totalDiscounts;
	}

	@Transient
	public Amounts getBaseAmountsWithContractDiscountsDeducted(Contract contract, OrderLineCount count) {
		Amounts amountsWithContractDiscountsDeducted = getBaseAmounts().clone();
//		amountsWithContractDiscountsDeducted.multiplyBy(count.getCountTotal());
		amountsWithContractDiscountsDeducted.multiplyBy(count);
		
		for (DiscountScheme discountScheme : contract.getDiscountSchemes()) {
			Amounts baseAmounts = getBaseAmounts();
			amountsWithContractDiscountsDeducted.subtract(discountScheme.calculateContractDiscountsForProductBundle(this, baseAmounts, baseAmounts));  
		}
		return amountsWithContractDiscountsDeducted;
	}

	@Transient
	public Amounts getCampaignDiscountsWithContractDiscountsDeducted(Contract contract, OrderLineCount count) {
		Amounts amountsWithContractDiscountsDeducted = getDiscountAmounts().clone();
//		amountsWithContractDiscountsDeducted.multiplyBy(count.getCountTotal());
		amountsWithContractDiscountsDeducted.multiplyBy(count);
		
		for (DiscountScheme discountScheme : contract.getDiscountSchemes()) {
			Amounts discountAmounts = getDiscountAmounts();
			amountsWithContractDiscountsDeducted.subtract(discountScheme.calculateContractDiscountsForProductBundle(this, discountAmounts, discountAmounts));    
		}
		return amountsWithContractDiscountsDeducted;
	}

	@Transient
	public Amounts getContractDiscountsBundleOnly(Contract contract, Amounts amountsBeforeCampaignDiscount, Amounts amountsAfterCampaignDiscount) {
		Amounts totals = new Amounts();
		for (DiscountScheme discountScheme : contract.getDiscountSchemes()) {
			totals.add(discountScheme.calculateContractDiscountsForProductBundle(this, amountsBeforeCampaignDiscount, amountsAfterCampaignDiscount));
		}
		return totals;
	}

	@Transient
	public Amounts getCampaignDiscounts(Contract contract, OrderLineCount count) {
		if (contract.getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CAMPAIGN_DISCOUNT)) {
			if (!StringUtils.isEmpty(rabataftaleCampaignDiscountMatrix)) {
				Amounts nonRecurringDiscounts = discountAmounts.clone();
				nonRecurringDiscounts.multiplyBy(count);
				Amounts amountsBeforeDiscounts = getAmountsBeforeDiscounts(count, contract);
				Amounts discounts = contract.getRabataftaleCampaignDiscounts(amountsBeforeDiscounts, rabataftaleCampaignDiscountMatrix, nonRecurringDiscounts, false);
				return discounts;
			}
			return new Amounts();
		} else {
			// Non-TEM5 campaign discount - i.e. "the old" campaign discount strategy
			Amounts a = discountAmounts.clone();
			// Jeg troede at kampagne rabatter skulle beregnes ud fra NYE produkter/pakker, men iflg. Anja skal rabatten
			// beregnes på nye og genforhandlede.
//			a.multiplyBy(count.getCountNew());
//			a.multiplyBy(count.getCountTotal());
			a.multiplyBy(count);
			return a;
		}
	}

	@Transient
	public boolean isInstallationHandledByTdc(Integer subIndex) {
		return true;
	}
}
