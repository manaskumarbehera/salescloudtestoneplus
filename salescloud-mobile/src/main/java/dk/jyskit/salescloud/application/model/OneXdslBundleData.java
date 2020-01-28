package dk.jyskit.salescloud.application.model;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static dk.jyskit.salescloud.application.model.AccessConstants.NO_CHOICE_ENTITY_ID;

@Data
@NoArgsConstructor
public class OneXdslBundleData implements Serializable {
	public static final String PERIOD_12_MONTHS_NO_DISCOUNT 	= "12 mdr. uden rabat";
	public static final String PERIOD_12_MONTHS 				= "12 mdr. med TDC Erhverv rabataftale";
	public static final String PERIOD_24_MONTHS 				= "24 mdr. med TDC Erhverv rabataftale";
	public static final String PERIOD_36_MONTHS 				= "36 mdr. med TDC Erhverv rabataftale";

	public static final String PICK_ONE							= "Vælg en";

	public static final String TEXT_NYSALG 						= "Nysalg";
	public static final String TEXT_GENFORHANDLING 				= "Genforhandling";
	public static final String TEXT_KONVERTERING				= "Konvertering";

	public static final int MODE_NYSALG 						= 0;
	public static final int MODE_GENFORHANDLING 				= 1;
	public static final int MODE_KONVERTERING					= 2;

	public static final String TEXT_ROUTER_FIREWALL_TDC_DHCP		 			= "TDC Router med firewall og Wi-Fi - 1 DHCP fast WAN IP";
	public static final String TEXT_ROUTER_FIREWALL_OWN_FIREWALL_DHCP 			= "Kunden benytter egen firewall – 1 DHCP fast WAN IP";
	public static final String TEXT_ROUTER_FIREWALL_OWN_FIREWALL_STATIC_IP 	= "Kunden benytter egen firewall – 1 statisk fast WAN IP";

	public static final int MODE_ROUTER_FIREWALL_TDC_DHCP				= 0;
	public static final int MODE_ROUTER_FIREWALL_OWN_FIREWALL_DHCP		= 1;
	public static final int MODE_ROUTER_FIREWALL_OWN_FIREWALL_STATIC_IP	= 2;

	@SerializedName("a") private String addressRoad;
	@SerializedName("b") private String addressZipCode;
	@SerializedName("c") private String addressCity;
	
	@SerializedName("d") private int mode 						= MODE_NYSALG;
	@SerializedName("e") private String lineNo 					= "";
	@SerializedName("f") private Long speedEntityId 			= NO_CHOICE_ENTITY_ID;
	@SerializedName("g") private Long speechChannelEntityId 	= NO_CHOICE_ENTITY_ID;
	@SerializedName("h") private Long routerFirewallRule		= null;  // null, 0-2
	@SerializedName("i") private Boolean reuseHardware			= null;
	@SerializedName("j") private Long ipEntityId 				= NO_CHOICE_ENTITY_ID;
	@SerializedName("k") private Long redundancyEntityId 		= NO_CHOICE_ENTITY_ID;
	@SerializedName("l") private String sms 					= "";
	@SerializedName("m") private Boolean supervision			= null;
	@SerializedName("n") private String contactName 			= "";
	@SerializedName("o") private String contactPhone 			= "";
	@SerializedName("p") private String contactEmail 			= "";
	@SerializedName("q") private Long serviceEntityId 			= NO_CHOICE_ENTITY_ID;
	@SerializedName("r") private String techPhone 				= "";

	public boolean allowCdm() {
		return !StringUtils.isEmpty(lineNo);
	}

	@Transient
	public List<Long> getProductEntityIds() {
		List<Long> list = new ArrayList<>();
		addIfNotNull(list, speedEntityId);
		addIfNotNull(list, speechChannelEntityId);
		addIfNotNull(list, ipEntityId);
		addIfNotNull(list, redundancyEntityId);
		addIfNotNull(list, serviceEntityId);
		return list;
	}

	private void addIfNotNull(List<Long> list, Long id) {
		if (id != null) {
			list.add(id);
		}
	}

	@Transient
	public static List<String> getRouterFirewallStringOptions() {
		return Lists.newArrayList(
						TEXT_ROUTER_FIREWALL_TDC_DHCP
						,TEXT_ROUTER_FIREWALL_OWN_FIREWALL_DHCP
						,TEXT_ROUTER_FIREWALL_OWN_FIREWALL_STATIC_IP);
	}

	@Transient
	public String getRouterFirewallAsString() {
		Long code = routerFirewallRule;
		if (Long.valueOf(MODE_ROUTER_FIREWALL_TDC_DHCP).equals(code)) {
			return TEXT_ROUTER_FIREWALL_TDC_DHCP;
		}
		if (Long.valueOf(MODE_ROUTER_FIREWALL_OWN_FIREWALL_DHCP).equals(code)) {
			return TEXT_ROUTER_FIREWALL_OWN_FIREWALL_DHCP;
		}
		if (Long.valueOf(MODE_ROUTER_FIREWALL_OWN_FIREWALL_STATIC_IP).equals(code)) {
			return TEXT_ROUTER_FIREWALL_OWN_FIREWALL_STATIC_IP;
		}
		return null;
	}

	@Transient
	public static Long getRouterFirewallAsCode(String text) {
		if (TEXT_ROUTER_FIREWALL_TDC_DHCP.equals(text)) {
			return Long.valueOf(MODE_ROUTER_FIREWALL_TDC_DHCP);
		}
		if (TEXT_ROUTER_FIREWALL_OWN_FIREWALL_DHCP.equals(text)) {
			return Long.valueOf(MODE_ROUTER_FIREWALL_OWN_FIREWALL_DHCP);
		}
		if (TEXT_ROUTER_FIREWALL_OWN_FIREWALL_STATIC_IP.equals(text)) {
			return Long.valueOf(MODE_ROUTER_FIREWALL_OWN_FIREWALL_STATIC_IP);
		}
		return null;
	}

	@Transient
	public static String getModeAsString(int code) {
		if (MODE_NYSALG == code) {
			return TEXT_NYSALG;
		}
		if (MODE_GENFORHANDLING == code) {
			return TEXT_GENFORHANDLING;
		}
		if (MODE_KONVERTERING == code) {
			return TEXT_KONVERTERING;
		}
		return null;
	}

	@Transient
	public static int getModeAsCode(String text) {
		if (TEXT_NYSALG.equals(text)) {
			return MODE_NYSALG;
		}
		if (TEXT_GENFORHANDLING.equals(text)) {
			return MODE_GENFORHANDLING;
		}
		if (TEXT_KONVERTERING.equals(text)) {
			return MODE_KONVERTERING;
		}
		return -1;
	}
}
