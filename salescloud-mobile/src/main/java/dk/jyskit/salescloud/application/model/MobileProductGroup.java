package dk.jyskit.salescloud.application.model;

import javax.persistence.Entity;
import javax.persistence.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class MobileProductGroup extends ProductGroup implements MobileSortableItem {
	
	private long outputSortIndex;					// Sorting in CDM output
	private long offerSortIndex;  					// Sorting in Tilbud/overslag
	
	// --------------------------------
	
	public MobileProductGroup(String name) {
		super(name);
	}
	
	public MobileProductGroup(String name, String uniqueName) {
		super(name, uniqueName);
	}
	
	// --------------------------------
	
	@Transient
	public long getMaxSortIndexOfChildren(boolean inOutput) {
		long max = 0;
		for (ProductGroup childGroup : getChildProductGroups()) {
			if (inOutput) {
				max = Math.max(max, ((MobileProductGroup) childGroup).getOutputSortIndex());
			} else {
				max = Math.max(max, ((MobileProductGroup) childGroup).getOfferSortIndex());
			}
		}
		return max;
	}

	@Transient
	public String getTextForSorting() {
		return getFullPath();
	}

	// --------------------------------

	public boolean isOfType(MobileProductGroupEnum...types) {
		for (MobileProductGroupEnum t : types) {
			if (getUniqueName().equals(t.getKey())) {
				return true;
			}
		}
		return false;
	}
	// --------------------------------

	@Override
	public String toString() {
		return name;
	}
}
