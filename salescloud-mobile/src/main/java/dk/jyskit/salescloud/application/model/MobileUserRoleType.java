package dk.jyskit.salescloud.application.model;

import java.util.ArrayList;
import java.util.List;

public enum MobileUserRoleType {
	ADMIN (1, "Administrator"),
	PARTNER (2, "Partner");
	
	private int id;
	private String text;

	private MobileUserRoleType(int id, String text) {
		this.id 	= id;
		this.text	= text;
	}
	
	public int getId() {
		return id;
	}
	
	public String getText() {
		return text;
	}
	
	public static List<MobileUserRoleType> valuesAsList() {
		List<MobileUserRoleType> list = new ArrayList<MobileUserRoleType>(values().length);
		for (MobileUserRoleType numberTransferType : values()) {
			list.add(numberTransferType);
		}
		return list;
	}
}
