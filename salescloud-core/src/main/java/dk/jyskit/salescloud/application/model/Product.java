package dk.jyskit.salescloud.application.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import dk.jyskit.waf.application.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity(name="Product")
@Table(name = "product")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@EqualsAndHashCode(callSuper=true, of={})
@RequiredArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity {
	@Enumerated(EnumType.STRING)
	private EntityState state;	 
	
	@NonNull @NotNull
	protected String publicName;
	
	@NonNull @NotNull
	protected String internalName;
	
	@Column(length=40)
	protected String productId;
	
	private long sortIndex;  // Sorting in UI
	
	@Embedded
	protected Amounts price;
	
	@ManyToOne(optional = false)
	private BusinessArea businessArea;
	
	@ManyToOne(optional = false)
	private ProductGroup productGroup;
	
	// NOT USED!!!
	@Deprecated
	@ElementCollection
	private List<ProductionItem> productionItems = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	private PaymentFrequency paymentFrequency = PaymentFrequency.MONTHLY;
	
	private int defaultCount;
	private Integer minCount;
	private Integer maxCount;
	
	private boolean discountEligible;
	
	// NOT USED!!!
	@Deprecated
	@ManyToMany
	@JoinTable(name = "product_discountscheme", joinColumns = { @JoinColumn(name = "product_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "discountscheme_id", referencedColumnName = "id") })
	private List<DiscountScheme> discountSchemes;

	// Rabataftale 
	
	private boolean rabataftaleDiscountEligible = false;
	
	// --------------------------------
	/**
	 * Pick next available sort index  
	 */
	public void initSortIndex() {
		sortIndex = 1;
		for (Product p : productGroup.getProducts()) {
			if (!equals(p)) {
				sortIndex = Math.max(sortIndex, p.getSortIndex());
			}
		}
		sortIndex++;
	}

	@Transient
	public Amounts getAmountsBeforeDiscounts(OrderLineCount count, Contract contract) {
		Amounts amounts = new Amounts();
		amounts.add(getActualPrice());
		for (FeeCategory category : new FeeCategory[] {FeeCategory.INSTALLATION_FEE, FeeCategory.ONETIME_FEE, FeeCategory.RECURRING_FEE}) {
			amounts.getAmounts()[category.getFromIndex()] 	*= count.getCountForFeeCategory(category); 
		}
		return amounts;
	}
	
	@Transient
	public Amounts getAmounts(OrderLineCount count, boolean deductCampaignDiscount, boolean deductContractDiscount, Contract contract) {
		Amounts amountsBeforeCampaignDiscount = getAmountsBeforeDiscounts(count, contract);
		
//		if (!amountsBeforeCampaignDiscount.isAllZero()) {
//			System.out.println("Product - Added amounts: " + amountsBeforeCampaignDiscount.toString());
//			System.out.println("");
//		}
		
		Amounts amountsAfterCampaignDiscount = amountsBeforeCampaignDiscount.clone();
		
		if (deductCampaignDiscount) {
			Amounts campaignDiscounts = getCampaignDiscounts(contract, count);  
			amountsAfterCampaignDiscount.subtract(campaignDiscounts);
		}
		if (deductContractDiscount) {
			amountsAfterCampaignDiscount.subtract(getContractDiscounts(contract, amountsBeforeCampaignDiscount, amountsAfterCampaignDiscount));
		}
		return amountsAfterCampaignDiscount;
	}

	@Transient
	public Amounts getActualPrice() {
		return price;  // TODO : Look up price for partner stuff
	}
	
	/**
	 * Calculate contract discounts for the amounts. Normally we will pass amounts
	 * corresponding to base amounts - campaign amounts, but there are special cases where we
	 * call this method to calculate contract discounts on base amounts OR campaign amounts.
	 * 
	 * @param contract
	 * @param amounts
	 * @return
	 */
	@Transient
	public Amounts getContractDiscounts(Contract contract, Amounts amountsBeforeCampaignDiscount, Amounts amountsAfterCampaignDiscount) {
		Amounts contractDiscounts = new Amounts();
		for (DiscountScheme discountScheme : contract.getDiscountSchemes()) {
			// This causes a stack overflow exception for IPSA discount. This is only used for Switchboard business area, which is not in use anymore.
			discountScheme.prepare(contract);
		}
		for (DiscountScheme discountScheme : contract.getDiscountSchemes()) {
			if (contract.getBusinessArea().isCumulativeDiscounts()) {
				contractDiscounts.add(discountScheme.calculateContractDiscountsForProduct(this, amountsBeforeCampaignDiscount, amountsAfterCampaignDiscount));
			} else {
				contractDiscounts = Amounts.max(
						contractDiscounts, 
						discountScheme.calculateContractDiscountsForProduct(this, amountsBeforeCampaignDiscount, amountsAfterCampaignDiscount));
			}
		}
		return contractDiscounts;
	}
	
	@Transient
	public Amounts getCampaignDiscounts(Contract contract, OrderLineCount count) {
		return new Amounts();
	}

	@Transient
	public boolean isInstallationHandledByTdc(Integer subIndex) {
		return true;
	}

	// --------------------------------

	@Override
	public String toString() {
		return publicName;
	}
}
