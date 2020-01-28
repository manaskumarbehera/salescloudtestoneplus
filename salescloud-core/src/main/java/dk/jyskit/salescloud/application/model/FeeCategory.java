package dk.jyskit.salescloud.application.model;

public enum FeeCategory {
	ONETIME_FEE("fee_category.onetime", 0, 0),
	INSTALLATION_FEE("fee_category.installation", 1, 1),
	RECURRING_FEE("fee_category.recurring", 2, 2),
	NON_RECURRING_FEE("fee_category.nonrecurring", 0, 1);
	
	private String key;
	private int fromIndex;
	private int toIndex;

	private FeeCategory(String key, int fromIndex, int toIndex) {
		this.key = key;
		this.fromIndex = fromIndex;
		this.toIndex = toIndex;
	}
	
	public int getFromIndex() {
		return fromIndex;
	}
	
	public int getToIndex() {
		return toIndex;
	}
	
	public String getKey() {
		return key;
	}
}
