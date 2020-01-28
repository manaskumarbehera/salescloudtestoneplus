package dk.jyskit.salescloud.application.pages.admin.sorting;

public enum SortingType {
	TYPE_UI (1, "Sortering i brugergrænseflade", "sortIndex"),
	TYPE_OFFER (2, "Sortering i overslag og tilbud", "offerSortIndex"),
	TYPE_PRODUCTION	(3, "Sortering i ordreoutput", "outputSortIndex");
	
	private String text;
	private String property;
	private int id;

	private SortingType(int id, String text, String property) {
		this.id = id;
		this.text = text;
		this.property = property;
	}
	
	public String getProperty() {
		return property;
	}
	
	public String getText() {
		return text;
	}
	
	public int getId() {
		return id;
	}
}
