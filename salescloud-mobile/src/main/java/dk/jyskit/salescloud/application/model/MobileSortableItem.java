package dk.jyskit.salescloud.application.model;

public interface MobileSortableItem {
	long getSortIndex();
	void setSortIndex(long index);
	
	long getOutputSortIndex();
	void setOutputSortIndex(long index);
	
	long getOfferSortIndex();
	void setOfferSortIndex(long index);
	
	String getTextForSorting();
}
