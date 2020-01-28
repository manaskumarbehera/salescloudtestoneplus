package dk.jyskit.salescloud.application.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class MobileSortableItemImpl implements MobileSortableItem, Serializable {
	private long sortIndex;
	private long outputSortIndex;
	private long offerSortIndex;
	private String textForSorting;
	private MobileSortableItem originalEntity;
}
