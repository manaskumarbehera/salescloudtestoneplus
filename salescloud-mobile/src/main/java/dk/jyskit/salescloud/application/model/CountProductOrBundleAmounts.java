package dk.jyskit.salescloud.application.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class CountProductOrBundleAmounts implements Serializable {
	private Integer subIndex;
	private int countNew;
	private int countExisting;
	private Object productOrBundle;
//	private Amounts amounts = new Amounts(0, 0, 0);
	private Amounts baseAmounts = new Amounts(0, 0, 0);
	private Amounts baseAmountsIncludingProducts = new Amounts(0, 0, 0);
	private Amounts campaignDiscountAmounts = new Amounts(0, 0, 0);
	private Amounts amountsAfterCampaignAndContractDiscounts = new Amounts(0, 0, 0);
//	private Amounts totalAmountsAfterCampaignAndContractDiscounts = new Amounts(0, 0, 0);
	
	private Amounts baseAmountsWithContractDiscountsDeducted = new Amounts(0, 0, 0);
	private Amounts campaignDiscountsWithContractDiscountsDeducted = new Amounts(0, 0, 0);
	
	public Amounts getAmounts() {
		Amounts amounts = baseAmounts.clone();
		amounts.subtract(campaignDiscountAmounts);
		return amounts;
	}
	
	public void addCounts(OrderLineCount counts) {
		countNew 		+= counts.getCountNew();
		countExisting 	+= counts.getCountExisting();
	}
	
	public OrderLineCount getCount() {
		OrderLineCount orderLineCount = new OrderLineCount();
		orderLineCount.setCountNew(countNew);
		orderLineCount.setCountExisting(countExisting);
		return orderLineCount;
	}
	
	public MobileProduct getProduct() {
		return (MobileProduct) productOrBundle;
	}
	
	public MobileProduct getProductSafely() {
		if (isProduct()) {
			return (MobileProduct) productOrBundle; 
		} else {
			return null;
		}
	}
	
	public MobileProductBundle getProductBundleSafely() {
		if (isProduct()) {
			return null;
		} else {
			return (MobileProductBundle) productOrBundle; 
		}
	}
	
	public void setProduct(MobileProduct product) {
		productOrBundle = product;
	}
	
	public MobileProductBundle getProductBundle() {
		return (MobileProductBundle) productOrBundle;
	}
	
	public void setProductBundle(MobileProductBundle productBundle) {
		productOrBundle = productBundle;
	}
	
	public boolean isProduct() {
		return (productOrBundle instanceof MobileProduct);
	}
	
	public String getProductId() {
		if (isProduct()) {
			return getProduct().getProductId();
		} else {
			return getProductBundle().getProductId();
		}
	}
	
	public String getInternalName() {
		if (isProduct()) {
			return getProduct().getInternalName();
		} else {
			return getProductBundle().getInternalName();
		}
	}
}
