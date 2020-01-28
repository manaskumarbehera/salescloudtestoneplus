package dk.jyskit.salescloud.application.model;

import java.util.Comparator;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class CountProductOrBundleComparatorOld implements Comparator<CountProductOrBundleAmounts> {
	private static final long BIGNUMBER = 9000000000l;
	private FeeCategory feeCategory;
	public enum Criteria {cdm, offer};
	private Criteria criteria;

	public CountProductOrBundleComparatorOld(Criteria criteria, FeeCategory feeCategory) {
		this.feeCategory = feeCategory;
		this.criteria = criteria;
		if (criteria == null) {
			throw new IllegalArgumentException("criteria must be specified");
		}
		if (criteria.equals(Criteria.offer) && (feeCategory == null)) {
			throw new IllegalArgumentException("feeCategory must be specified when user criteria offer");
		}
	}
	
	@Override
	public int compare(CountProductOrBundleAmounts o1, CountProductOrBundleAmounts o2) {
		Pair<Long, Long> data1 = getIndexAndAmount(o1);
		Pair<Long, Long> data2 = getIndexAndAmount(o2);
		if (data1.getLeft().equals(data2.getLeft())) {
			return (data1.getRight().compareTo(data2.getRight()));
		}
		return (data1.getLeft().compareTo(data2.getLeft()));
	}

	private Pair<Long, Long> getIndexAndAmount(CountProductOrBundleAmounts obj) {
		long index = 0;
		long amount = 0;
		
		if (obj.isProduct()) {
			
			MobileProduct product = obj.getProduct();
			
			switch(criteria) {
			case cdm:
				index = product.getGroupAndProductOutputSortIndex();
				break;
			case offer:
				amount = obj.getAmounts().sum(feeCategory);
				if (amount >= 0) {
					// big amount first, extra products last
//					index = BIGNUMBER - ((1000 * ((((MobileProductGroup) product.getProductGroup()).getOfferSortIndex()) + 1)) + product.getOfferSortIndex());
					index = BIGNUMBER - amount;
					if (!product.isExtraProduct()) {
						index -= (BIGNUMBER / 10);
					}
				} else {
					// Put discounts at the bottom
					index = BIGNUMBER + amount;
				}
				break;
			}
			
		} else {
			
			MobileProductBundle bundle = obj.getProductBundle();
			
			switch(criteria) {
			case cdm:
				index = bundle.getSortIndex();
				break;
			case offer:
				amount = obj.getAmounts().sum(feeCategory);
				if (amount >= 0) {
					// Put bundles at the top 
					index = bundle.getSortIndex();
				} else {
					// Put discounts at the bottom
					index = BIGNUMBER + amount;
				}
				break;
			}
			
		}
//		
//		long index = obj.isProduct() ?  
//				(((MobileProductGroup) obj.getProduct().getProductGroup()).getOfferSortIndex() * 1000) + ((MobileProductGroup) obj.getProduct().getProductGroup()).getOfferSortIndex() 
//				:
//				(obj.getProductBundle().getSortIndex()); 
//		if (!obj.isProduct()) {
//			MobileProductBundle bundle = obj.getProductBundle();
//			
//			switch (feeCategory) {
//			case RECURRING_FEE:
//				if (bundle.getDiscountAmounts().getRecurringFee() > 0) {
//					index = BIGNUMBER + bundle.getDiscountAmounts().getRecurringFee();
//				}
//				break;
//			case ONETIME_FEE:
//				if (bundle.getDiscountAmounts().getOneTimeFee() > 0) {
//					index = BIGNUMBER + bundle.getDiscountAmounts().getOneTimeFee();
//				}
//				break;
//			case INSTALLATION_FEE:
//				if (bundle.getDiscountAmounts().getInstallationFee() > 0) {
//					index = BIGNUMBER + bundle.getDiscountAmounts().getInstallationFee();
//				}
//				break;
//			case NON_RECURRING_FEE:
//				if (bundle.getDiscountAmounts().getNonRecurringFees() > 0) {
//					index = BIGNUMBER + bundle.getDiscountAmounts().getNonRecurringFees();
//				}
//				break;
//			}
//		}
		return new ImmutablePair<Long, Long>(index, amount);
	}
}
