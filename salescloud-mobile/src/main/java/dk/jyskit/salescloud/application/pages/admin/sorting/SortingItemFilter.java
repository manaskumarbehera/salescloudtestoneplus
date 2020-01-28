package dk.jyskit.salescloud.application.pages.admin.sorting;

import java.io.Serializable;

import dk.jyskit.salescloud.application.model.MobileSortableItem;

public interface SortingItemFilter<T extends MobileSortableItem> extends Serializable {
	boolean includeItem(MobileSortableItem item);
}
