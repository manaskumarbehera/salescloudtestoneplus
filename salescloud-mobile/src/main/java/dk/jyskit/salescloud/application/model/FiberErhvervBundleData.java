package dk.jyskit.salescloud.application.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;

import static dk.jyskit.salescloud.application.model.AccessConstants.NO_CHOICE_ENTITY_ID;

@Data
@NoArgsConstructor
public class FiberErhvervBundleData implements Serializable {
	public static final String PERIOD_12_MONTHS_NO_DISCOUNT 	= "12 mdr. uden rabat";
	public static final String PERIOD_12_MONTHS 				= "12 mdr. med TDC Erhverv rabataftale";
	public static final String PERIOD_24_MONTHS 				= "24 mdr. med TDC Erhverv rabataftale";
	public static final String PERIOD_36_MONTHS 				= "36 mdr. med TDC Erhverv rabataftale";
	
	@SerializedName("a") private String addressRoad;
	@SerializedName("b") private String addressZipCode;
	@SerializedName("c") private String addressCity;
	
//	@SerializedName("d") private Integer period;   // null, 99 (12 mdr. uden rabat), 12, 24, 36
	@SerializedName("e") private Long speedEntityId;
	@SerializedName("f") private Long fiberEntityId;
	@SerializedName("g") private Long routerFirewallRule;  // null, 0-2
	@SerializedName("h") private Long publicIpEntityId;
	@SerializedName("i") private Long redundancyEntityId;
	@SerializedName("j") private Long supervisionEntityId;
	@SerializedName("k") private Long serviceEntityId;
	@SerializedName("l") private Long installationEntityId;
	@SerializedName("n") private Long diggingMeters = null;
	@SerializedName("o") private Boolean inspection;
	@SerializedName("p") private Boolean coordination;

	@SerializedName("t") private Integer agreementTypeNo;	// Prisaftale nr. 14xxx
	
	@SerializedName("x") private String contactName = "";
	@SerializedName("z") private String contactPhone = "";
	@SerializedName("v") private String contactEmail = "";
	
	@SerializedName("aa") private String smsAlertNo 				= "";
	@SerializedName("ab") private String contactSupervision 		= "";
	@SerializedName("ac") private String contactSupervisionPhone 	= "";
	@SerializedName("ad") private String contactSupervisionEmail 	= "";
	@SerializedName("ae") private String contactInspection 			= "";
	@SerializedName("af") private String contactInspectionPhone 	= "";
	@SerializedName("ag") private String contactPhone30Minutes	 	= "";

	@SerializedName("ah") private Integer infrastructureTypeNo;	// 0: TDC, 1: ENIIG, 2: EWII

	public static final Integer INFRASTRUCTURE_CODE_TDC 	= 0;
	public static final Integer INFRASTRUCTURE_CODE_ENIIG	= 1;
	public static final Integer INFRASTRUCTURE_CODE_EWII	= 2;

	public static final String INFRASTRUCTURE_TDC 		= "TDC infrastruktur";
	public static final String INFRASTRUCTURE_ENIIG		= "ENIIG infrastruktur";
	public static final String INFRASTRUCTURE_EWII		= "EWII infrastruktur";

	@Transient
	public boolean isRedundancySelected() {
		return (!nullOrNoEntity(redundancyEntityId));
	}
	
	@Transient
	public boolean isSupervisionSelected() {
		return (!nullOrNoEntity(supervisionEntityId));
	}
	
	@Transient
	public boolean isInspectionSelected() {
		return Boolean.TRUE.equals(inspection);
	}
	
	@Transient
	public boolean isFiberSelected() {
		return (!nullOrNoEntity(fiberEntityId));
	}
	
	public boolean nullOrNoEntity(Long entityId) {
		return (entityId == null) || (NO_CHOICE_ENTITY_ID.equals(entityId));
	}

	public boolean nullOrNoEntity(Object value) {
		return (value == null) || (NO_CHOICE_ENTITY_ID.equals(value));
	}

	@Transient
	public String getAddress() {
		return addressRoad + ", " + addressZipCode + " " + addressCity; 
	}

	@Transient
	public static String getInfrastructureAsString(Integer code) {
		if (INFRASTRUCTURE_CODE_TDC.equals(code)) {
			return INFRASTRUCTURE_TDC;
		}
		if (INFRASTRUCTURE_CODE_ENIIG.equals(code)) {
			return INFRASTRUCTURE_ENIIG;
		}
		if (INFRASTRUCTURE_CODE_EWII.equals(code)) {
			return INFRASTRUCTURE_EWII;
		}
		return null;
	}

	@Transient
	public static Integer getInfrastructureAsCode(String value) {
		if (value == null) {
			return null;
		}
		switch (value) {
			case INFRASTRUCTURE_TDC: return INFRASTRUCTURE_CODE_TDC;
			case INFRASTRUCTURE_ENIIG: return INFRASTRUCTURE_CODE_ENIIG;
			case INFRASTRUCTURE_EWII: return INFRASTRUCTURE_CODE_EWII;
		}
		return null;
	}

	@Transient
	public List<Long> getProductEntityIds() {
		List<Long> list = new ArrayList<>();
		addIfNotNull(list, speedEntityId);
		addIfNotNull(list, fiberEntityId);
		addIfNotNull(list, publicIpEntityId);
		addIfNotNull(list, redundancyEntityId);
		addIfNotNull(list, supervisionEntityId);
		addIfNotNull(list, serviceEntityId);
		addIfNotNull(list, installationEntityId);
		return list;
	}
	
	private void addIfNotNull(List<Long> list, Long id) {
		if (id != null) {
			list.add(id);
		}
	}
}
