package dk.jyskit.salescloud.application.pages.admin.dashboard;

import java.util.ArrayList;
import java.util.List;

public enum ChartTypeEnum {
	BUSINESS_AREAS ("chart_type.business_areas", "Forretningsområder")
	,CONTRACT_ACTIVITY_ALL ("chart_type.contract_activity_all", "Kontraktaktivitet - total")
	,CONTRACT_ACTIVITY_ORGANISATION ("chart_type.contract_activity_organisation", "Kontraktaktivitet - organisation")
//	,CONTRACT_ACTIVITY_SALESPERSON ("chart_type.contract_activity_salesperson", "Kontraktaktivitet - sælger")
	;
	
	private String key;
	private String displayText;

	private ChartTypeEnum(String key, String displayText) {
		this.key = key;
		this.displayText = displayText;
	}

	public String getDisplayText() {
		return displayText;
	}
	
	public String getKey() {
		return key;
	}
	
	public static List<ChartTypeEnum> valuesAsList() {
		List<ChartTypeEnum> list = new ArrayList<ChartTypeEnum>(values().length);
		for (ChartTypeEnum value : values()) {
			list.add(value);
		}
		return list;
	}
}
