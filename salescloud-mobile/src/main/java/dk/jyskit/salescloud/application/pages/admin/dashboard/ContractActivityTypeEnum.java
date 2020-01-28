package dk.jyskit.salescloud.application.pages.admin.dashboard;

import java.util.ArrayList;
import java.util.List;

public enum ContractActivityTypeEnum {
	NEW ("contract_activity.new", "Nye")
	,CHANGED_AND_OPEN ("contract_activity.changed", "Genberegnet")	 
	,WON ("contract_activity.won", "Vundet")
	,LOST ("contract_activity.lost", "Tabt")
	,CLOSED_OTHER ("contract_activity.closed_other", "Lukket (anden årsag)")
	// ---
	,ALL ("contract_activity.all", "Alle")
	;
	
	private String key;
	private String displayText;

	private ContractActivityTypeEnum(String key, String displayText) {
		this.key = key;
		this.displayText = displayText;
	}

	public String getDisplayText() {
		return displayText;
	}
	
	public String getKey() {
		return key;
	}
	
	public static List<ContractActivityTypeEnum> valuesAsList() {
		List<ContractActivityTypeEnum> list = new ArrayList<ContractActivityTypeEnum>(values().length);
		for (ContractActivityTypeEnum value : values()) {
			list.add(value);
		}
		return list;
	}
}
