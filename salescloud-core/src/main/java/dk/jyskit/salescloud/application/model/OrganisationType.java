package dk.jyskit.salescloud.application.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dk.jyskit.waf.wicket.utils.WicketUtils;

/**
 * Changing sortIndex is OK. Changing names is NOT OK (without modifying the database). 
 * 
 * @author jan
 */
public enum OrganisationType implements Serializable {
	TDC 				(0,  "TDC")
	,PARTNER_CENTER		(1,  "Partner Center")
	;
	
	private int sortIndex;
	private String name;

	private OrganisationType(int sortIndex, String name) {
		this.sortIndex 	= sortIndex;
		this.name 		= name;
	}
	
	public int getSortIndex() {
		return sortIndex;
	}
	
	public String getKey() {
		return name;
	}
	
	public static List<OrganisationType> valuesAsList() {
		List<OrganisationType> list = new ArrayList<>();
		for (OrganisationType type : values()) {
			list.add(type);
		}
		return list;
	}
	
	// --------------------------------
	
	public String toString() {
		return name;
	}
}
