package dk.jyskit.salescloud.application.model;

import java.util.ArrayList;
import java.util.List;

public enum NumberTransferType {
	NEW 		("Nyt telefonnummer"),
	MOVE 		("Flytning til TDC"),
	EXISTING 	("Eksisterende TDC nr.");
	
	private String text;

	private NumberTransferType(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public static List<NumberTransferType> valuesAsList() {
		List<NumberTransferType> list = new ArrayList<NumberTransferType>(values().length);
		for (NumberTransferType numberTransferType : values()) {
			list.add(numberTransferType);
		}
		return list;
	}
	
	@Override
	public String toString() {
		return text;
	}
}
