package dk.jyskit.salescloud.application.pages.bundles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public enum SpanType implements Serializable {
	XS 				("xs")
	,SM 			("sm")
	,MD 			("md")
	,LG 			("lg")
	;
	
	private String text;

	private SpanType(String text) {
		this.text 		= text;
	}
	
	public String getText() {
		return text;
	}
	
	public static List<SpanType> valuesAsList() {
		List<SpanType> list = new ArrayList<>();
		for (SpanType type : values()) {
			list.add(type);
		}
		return list;
	}
	
	// --------------------------------
	
	public String toString() {
		return text;
	}
}
