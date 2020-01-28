package dk.jyskit.salescloud.application.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dk.jyskit.salescloud.application.pages.admin.sorting.SortingType;

public class SortingHelper {

	public static List<Product> sort(List<Product> products, final SortingType sortingType) {
		Collections.sort(products, new Comparator<Product>() {
			@Override
			public int compare(Product o1, Product o2) {
				MobileProduct p1 = (MobileProduct) o1;
				MobileProduct p2 = (MobileProduct) o2;
				if (SortingType.TYPE_UI.equals(sortingType)) {
					return Long.valueOf(p1.getSortIndex()).compareTo(Long.valueOf(p2.getSortIndex()));
				} else if (SortingType.TYPE_OFFER.equals(sortingType)) {
					return Long.valueOf(p1.getOfferSortIndex()).compareTo(Long.valueOf(p2.getOfferSortIndex()));
				} else if (SortingType.TYPE_PRODUCTION.equals(sortingType)) {
					return Long.valueOf(p1.getOutputSortIndex()).compareTo(Long.valueOf(p2.getOutputSortIndex()));
				}
				return 0;
			}
		});
		return products;
	}
}
