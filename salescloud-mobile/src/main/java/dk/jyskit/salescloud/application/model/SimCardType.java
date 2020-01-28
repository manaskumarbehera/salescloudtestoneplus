package dk.jyskit.salescloud.application.model;

import java.util.ArrayList;
import java.util.List;

public enum SimCardType {
	NA ("-"),
	COMBI ("Kombi"),
	NANO ("Nano-SIM");
	
	private String text;

	private SimCardType(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public static List<SimCardType> valuesAsList() {
		List<SimCardType> list = new ArrayList<SimCardType>(values().length);
		for (SimCardType numberTransferType : values()) {
			list.add(numberTransferType);
		}
		return list;
	}
	
	@Override
	public String toString() {
		return text;
	}
}
