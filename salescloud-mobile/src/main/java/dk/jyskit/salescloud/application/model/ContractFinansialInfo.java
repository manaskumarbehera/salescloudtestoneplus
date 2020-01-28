package dk.jyskit.salescloud.application.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableInt;

import lombok.Data;

@Data
public class ContractFinansialInfo {
	private Amounts contractTotalsBeforeDiscounts = new Amounts();
	private Amounts contractTotalsAfterDiscounts = new Amounts();

	private int noOfDiscountSchemes;
	
	private long fixedDiscountPct;  // x100 %
	private long SwitchboardIpsaDiscountPct;  // x100 %
	
	private Amounts campaignDiscounts 		= new Amounts();
	private Amounts contractDiscounts 		= new Amounts();
	
	private Amounts subscriptionTotals 		= new Amounts();
	private Amounts switchboardTotals 		= new Amounts();
	
	private Amounts switchboardAmounts		= new Amounts();
	private Amounts switchboardAddonAmounts	= new Amounts();
	
	private Amounts adslAmounts				= new Amounts();
	private Amounts wiFiAmounts				= new Amounts();
	private Amounts fiberAmounts			= new Amounts();
	
	private long totalDiscount;
//	private long totalBeforeDiscount;
	
	private long contractDiscount;
	private long campaignDiscount;
	
	private int subscriptionCount;
	private Map<String, MutableInt> bundleNameToSubscriptionCount = new HashMap<>();
	
	private Amounts roamingAmounts			= new Amounts();
	private Amounts functionsAmounts		= new Amounts();
	private Amounts extraProductsAmounts	= new Amounts();
	
	private long gksSumPrYear;
	private long ipsaSumPrYear;
	private long mobileSumPrYear;
	
	private long rabataftaleKontraktsum;

	@Override
	public ContractFinansialInfo clone() {
		ContractFinansialInfo info = new ContractFinansialInfo();

		info.contractTotalsBeforeDiscounts = contractTotalsBeforeDiscounts.clone();
		info.contractTotalsAfterDiscounts = contractTotalsAfterDiscounts.clone();
		info.noOfDiscountSchemes 		= noOfDiscountSchemes;

		info.fixedDiscountPct 			= fixedDiscountPct;
		info.SwitchboardIpsaDiscountPct = SwitchboardIpsaDiscountPct;

		info.campaignDiscounts 			= campaignDiscounts.clone();
		info.contractDiscounts 			= contractDiscounts.clone();

		info.subscriptionTotals 		= subscriptionTotals.clone();
		info.switchboardTotals 			= switchboardTotals.clone();

		info.switchboardAmounts			= switchboardAmounts.clone();
		info.switchboardAddonAmounts	= switchboardAddonAmounts.clone();

		info.adslAmounts				= adslAmounts.clone();
		info.wiFiAmounts				= wiFiAmounts.clone();
		info.fiberAmounts				= fiberAmounts.clone();

		info.totalDiscount				= totalDiscount;
//		info.totalBeforeDiscount		= totalBeforeDiscount;

		info.contractDiscount			= contractDiscount;
		info.campaignDiscount			= campaignDiscount;

		info.subscriptionCount			= subscriptionCount;
		info.bundleNameToSubscriptionCount = new HashMap<>(bundleNameToSubscriptionCount);

		info.roamingAmounts				= roamingAmounts.clone();
		info.functionsAmounts			= functionsAmounts.clone();
		info.extraProductsAmounts		= extraProductsAmounts.clone();

		info.gksSumPrYear				= gksSumPrYear;
		info.ipsaSumPrYear				= ipsaSumPrYear;
		info.mobileSumPrYear			= mobileSumPrYear;

		info.rabataftaleKontraktsum		= rabataftaleKontraktsum;
		return info;
	}

	public void add(ContractFinansialInfo other) {
		contractTotalsAfterDiscounts.add(other.contractTotalsAfterDiscounts);
		contractTotalsBeforeDiscounts.add(other.contractTotalsBeforeDiscounts);

		campaignDiscounts.add(other.campaignDiscounts);
		contractDiscounts.add(other.contractDiscounts);

		subscriptionTotals.add(other.subscriptionTotals);
		switchboardTotals.add(other.switchboardTotals);

		switchboardAmounts.add(other.switchboardAmounts);
		switchboardAddonAmounts.add(other.switchboardAddonAmounts);

		adslAmounts.add(other.adslAmounts);
		wiFiAmounts.add(other.wiFiAmounts);
		fiberAmounts.add(other.fiberAmounts);

		totalDiscount += other.totalDiscount;
//		totalBeforeDiscount += other.totalBeforeDiscount;

		contractDiscount += other.contractDiscount;
		campaignDiscount += other.campaignDiscount;

		subscriptionCount += other.subscriptionCount;
		for (String key: other.bundleNameToSubscriptionCount.keySet()) {
			MutableInt value = other.bundleNameToSubscriptionCount.get(key);
			bundleNameToSubscriptionCount.put(key, value);
		}

		roamingAmounts.add(other.roamingAmounts);
		functionsAmounts.add(other.functionsAmounts);
		extraProductsAmounts.add(other.extraProductsAmounts);

		gksSumPrYear += other.gksSumPrYear;
		ipsaSumPrYear += other.ipsaSumPrYear;
		mobileSumPrYear += other.mobileSumPrYear;

		rabataftaleKontraktsum += other.rabataftaleKontraktsum;
	}
}
