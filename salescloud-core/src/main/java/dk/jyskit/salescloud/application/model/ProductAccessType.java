package dk.jyskit.salescloud.application.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Changing sortIndex is OK. Changing texts is NOT OK (without modifying the database). 
 * 
 * @author jan
 */
public enum ProductAccessType implements Serializable {
	INCLUDED 				(0,  "Inkluderet")
	,OPTIONAL				(1,  "Option")
	,NON_OPTIONAL			(2,  "Ikke valgbar")
	// ------- One+ - for user profiles with a "variable" number of options (functions and roaming)
	,SEPARATE_COUNT			(3,  "Konfigurerbart antal")
	;
	
	private int sortIndex;
	private String text;

	private ProductAccessType(int sortIndex, String text) {
		this.sortIndex 	= sortIndex;
		this.text 		= text;
	}
	
	public int getSortIndex() {
		return sortIndex;
	}
	
	public String getText() {
		return text;
	}
	
	public static List<ProductAccessType> valuesAsList() {
		List<ProductAccessType> list = new ArrayList<>();
		for (ProductAccessType type : values()) {
			list.add(type);
		}
		return list;
	}
	
	// --------------------------------
	
	public String toString() {
		return text;
	}
}
