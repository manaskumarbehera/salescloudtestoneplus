package dk.jyskit.salescloud.application.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FiberErhvervPlusBundleData implements Serializable {
	public final static int IP_ADDRESS_ALL_OPTIONS 	= 1;
	public final static int IP_ADDRESS_4_OR_8 		= 2;
	
	public final static String CAMPAIGN_STANDARD 		= "Normal";
	public final static String CAMPAIGN_PRICE_A 		= "Pris A Øvrige";
	public final static String CAMPAIGN_PRICE_B 		= "Pris B Dong/Højvækst";
	
	@SerializedName("a") private String addressRoad;
	@SerializedName("b") private String addressZipCode;
	@SerializedName("c") private String addressCity;
	@SerializedName("d") private Long fiberSpeedEntityId;
	@SerializedName("e") private Boolean inSolution;
	@SerializedName("f") private Boolean tdcRouter;
//	@SerializedName("g") private Boolean firewall;
	@SerializedName("g") private Long firewallEntityId;
	@SerializedName("h") private Boolean wiFi;
	@SerializedName("i") private Long ipAddressEntityId;
	@SerializedName("j") private Long zoneOprettelseEntityId;
	@SerializedName("k") private Long zoneDriftEntityId;
	@SerializedName("l") private Long supervisionEntityId;
	@SerializedName("m") private Boolean coordination;
	@SerializedName("n") private Long floors 				= Long.valueOf(0);
//	@SerializedName("o") private Integer ipAddressOptionState = Integer.valueOf(0);	// 0 (4=*, 5=T, 6=T) - 1 (4=T, 5=F, 6=F) - 2 (4=F, 5=F, 6=F)
	@SerializedName("p") private Integer ipAddressOptionState = Integer.valueOf(IP_ADDRESS_ALL_OPTIONS);	// IP_ADDRESS_ALL_OPTIONS (TDC Router=T, Firewall=T) - ellers IP_ADDRESS_4_OR_8
	@SerializedName("q") private String campaign = CAMPAIGN_STANDARD;
	@SerializedName("r") private Long noOfAccesspoints 		= Long.valueOf(0);
	@SerializedName("s") private Long noOfAccesspointPsus 	= Long.valueOf(0);
	@SerializedName("t") private Boolean guestNetwork;

	@SerializedName("x") private String contactName = "";
	@SerializedName("z") private String contactPhone = "";
	@SerializedName("v") private String contactEmail = "";
	
	@Transient
	public String getAddress() {
		return addressRoad + ", " + addressZipCode + " " + addressCity; 
	}
	
	@Transient
	public List<Long> getProductEntityIds() {
		List<Long> list = new ArrayList<>();
		addIfNotNull(list, fiberSpeedEntityId);
		addIfNotNull(list, ipAddressEntityId);
		addIfNotNull(list, zoneOprettelseEntityId);
		addIfNotNull(list, zoneDriftEntityId);
		addIfNotNull(list, supervisionEntityId);
		addIfNotNull(list, firewallEntityId);
		return list;
	}
	
	private void addIfNotNull(List<Long> list, Long id) {
		if (id != null) {
			list.add(id);
		}
	}
}
