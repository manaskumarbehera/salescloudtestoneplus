package dk.jyskit.salescloud.application.model;

import java.util.Comparator;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import dk.jyskit.salescloud.application.MobileSession;

public class CountProductOrBundleComparator implements Comparator<CountProductOrBundleAmounts> {
	private static final long BIGNUMBER = 9000000000l;
	private FeeCategory feeCategory;
	public enum Criteria {cdm, offer};
	private Criteria criteria;

	public CountProductOrBundleComparator(Criteria criteria, FeeCategory feeCategory) {
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
		if (Criteria.cdm == criteria) {
			if (o1.isProduct() && o2.isProduct()) {
				if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_OFFICE) {
					if (!MobileProductGroupEnum.getValueByKey(o1.getProduct().getProductGroup().getUniqueName()).getSortIndex().equals(MobileProductGroupEnum.getValueByKey(o2.getProduct().getProductGroup().getUniqueName()).getSortIndex())) {
						return (MobileProductGroupEnum.getValueByKey(o1.getProduct().getProductGroup().getUniqueName()).getSortIndex().compareTo(MobileProductGroupEnum.getValueByKey(o2.getProduct().getProductGroup().getUniqueName()).getSortIndex()));
					}
				}
				return Long.valueOf(o1.getProduct().getSortIndex()).compareTo(Long.valueOf(o2.getProduct().getSortIndex()));
			} else if (!o1.isProduct() && !o2.isProduct()) {
				return Long.valueOf(o1.getProductBundle().getSortIndex()).compareTo(o2.getProductBundle().getSortIndex());
			} else {
				if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_OFFICE) {
					if (o1.isProduct()) {
						return 10000;
					} else {
						return -10000;
					}
				} else {
					if (o1.isProduct()) {
						return -10000;
					} else {
						return 10000;
					}
				}
			}
		} else {
			Pair<Long, Long> data1 = getIndexAndAmount(o1);
			Pair<Long, Long> data2 = getIndexAndAmount(o2);
			if (data1.getLeft().equals(data2.getLeft())) {
				return (data1.getRight().compareTo(data2.getRight()));
			}
			return (data1.getLeft().compareTo(data2.getLeft()));
		}
	}

	private Pair<Long, Long> getIndexAndAmount(CountProductOrBundleAmounts obj) {
		long index = 0;
		long amount = 0;
		
		if (obj.isProduct()) {
			MobileProduct product = obj.getProduct();
			
			amount = obj.getAmounts().sum(feeCategory);
			if (amount >= 0) {
				// big amount first, extra products last
				index = BIGNUMBER - amount;
				if (!product.isExtraProduct()) {
					index -= (BIGNUMBER / 10);
				}
			} else {
				// Put discounts at the bottom
				index = BIGNUMBER + amount;
			}
		} else {
			MobileProductBundle bundle = obj.getProductBundle();
			amount = obj.getAmounts().sum(feeCategory);
			if (amount >= 0) {
				// Put bundles at the top 
				index = bundle.getSortIndex();
			} else {
				// Put discounts at the bottom
				index = BIGNUMBER + amount;
			}
		}
		return new ImmutablePair<Long, Long>(index, amount);
	}
}
