package dk.jyskit.salescloud.application.model;

import java.util.ArrayList;
import java.util.List;

public enum MobileContractType {
	STANDARD (0, "Ingen kontrakt")
//	,FORDELSAFTALE (1, "Fordelsaftale")
	,FIXED_DISCOUNT (2, "TDC Erhverv Rabataftale")
//	,ERHVERVSKONTRAKT (3, "Erhvervskontrakt")
//	,TEM5 (4, "TEM5 kontrakt");
	;
	
	private int id;
	private String text;

	private MobileContractType(int id, String text) {
		this.id 	= id;
		this.text	= text;
	}
	
	public int getId() {
		return id;
	}
	
	public String getText() {
		return text;
	}
	
	public static List<MobileContractType> valuesAsList() {
		List<MobileContractType> list = new ArrayList<MobileContractType>(values().length);
		for (MobileContractType numberTransferType : values()) {
			list.add(numberTransferType);
		}
		return list;
	}
}
