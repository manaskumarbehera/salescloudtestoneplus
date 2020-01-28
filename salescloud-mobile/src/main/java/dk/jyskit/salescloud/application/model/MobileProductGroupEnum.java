package dk.jyskit.salescloud.application.model;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public enum MobileProductGroupEnum {
	PRODUCT_GROUP_STANDARD_BUNDLE ("standard", "Mobilpakker"),
	PRODUCT_GROUP_STANDARD_BUNDLE_SPEECH ("standard.speech", "Tale"),
	PRODUCT_GROUP_STANDARD_BUNDLE_DATA ("standard.data", "Data"),
	PRODUCT_GROUP_STANDARD_BUNDLE_NON_DOMESTIC ("standard.nondomestic", "Udland"),
	// The following were added for One+,
	PRODUCT_GROUP_STANDARD_BUNDLE_INCLUDED ("standard.included", "Inkluderet"),
	PRODUCT_GROUP_STANDARD_BUNDLE_INSTALLATION ("standard.installation", "Installation"),
	PRODUCT_GROUP_STANDARD_BUNDLE_INSTALLATION_REMOTE ("standard.installation.remote", "Installation - remote"),

	PRODUCT_GROUP_STANDARD_BUNDLE_ADDON_ROAMING ("standard.add-on.roaming", "Roaming"),
	// The following were added for One+
	PRODUCT_GROUP_STANDARD_BUNDLE_ADDON ("standard.add-on", "Tilvalg"),
	PRODUCT_GROUP_STANDARD_BUNDLE_ADDON_FUNCTIONS ("standard.add-on.functions", "Funktioner"),

	// Works
	PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE ("tdcworks_mobile", "TDC Works Mobilpakker"),
	PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE_SPEECH ("tdcworks_mobile.speech", "Tale"),
	PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE_DATA ("tdcworks_mobile.data", "Data"),
	PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE_INCLUDED ("tdcworks_mobile.included", "Inkluderet"),
	
	PRODUCT_GROUP_TDC_WORKS_MOBILE_ADDON ("tdcworks_add-on", "TDC Works Tilvalg"),
	PRODUCT_GROUP_TDC_WORKS_MOBILE_ADDON_ROAMING ("tdcworks_add-on.roaming", "Roaming"),
	PRODUCT_GROUP_TDC_WORKS_MOBILE_ADDON_FUNCTIONS ("tdcworks_add-on.functions", "Funktioner"),

	// Office
	PRODUCT_GROUP_TDC_OFFICE_BASIC ("tdcoffice_basic", "TDC Office Basis", 1),
	
	PRODUCT_GROUP_TDC_OFFICE_BUNDLE ("tdcoffice_office365", "TDC Office Pakker", 0),
	PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED ("tdcoffice_office365.included", "Inkluderet", 2),
	PRODUCT_GROUP_TDC_OFFICE_BUNDLE_TOP ("tdcoffice_office365.top", "Top", 3),
	PRODUCT_GROUP_TDC_OFFICE_BUNDLE_BOTTOM ("tdcoffice_office365.bottom", "Bund", 4),
	
	PRODUCT_GROUP_TDC_OFFICE_ADDON ("tdcoffice_add-on", "TDC Office Tilvalg", 5),
	
	PRODUCT_GROUP_MIX_BUNDLE ("mix", "MobilMix"),
	PRODUCT_GROUP_MIX_BUNDLE_SPEECH ("mix.speech", "Tale"),
	PRODUCT_GROUP_MIX_BUNDLE_SPEECH_TIME ("mix.speech.time", "Taletid"),
	PRODUCT_GROUP_MIX_BUNDLE_DATA ("mix.data", "Data"),
	PRODUCT_GROUP_MIX_BUNDLE_DATA_AMOUNT ("mix.data.amount", "Datamængde"),
	PRODUCT_GROUP_MIX_BUNDLE_ADDON ("mix.add-on", "Tilvalg"),
	PRODUCT_GROUP_MIX_BUNDLE_INCLUDED ("mix.included", "Inkluderet"),
	
	PRODUCT_GROUP_XDSL_BUNDLE ("xdsl", "xDSL"),
	PRODUCT_GROUP_XDSL_BUNDLE_SPEED ("xdsl.speed", "Hastighed"),
//	PRODUCT_GROUP_XDSL_BUNDLE_MANAGED_DEVICES ("xdsl.manageddevices", "TDC managed udstyr"),
//	PRODUCT_GROUP_XDSL_BUNDLE_CUSTOMER_DEVICES ("xdsl.customerdevices", "Kundespecifikt udstyr"),
	PRODUCT_GROUP_XDSL_BUNDLE_INCLUDED ("xdsl.included", "Inkluderet"),
	PRODUCT_GROUP_XDSL_BUNDLE_MISC ("xdsl.misc", "Diverse"),
	PRODUCT_GROUP_XDSL_BUNDLE_SUPERVISION ("xdsl.supervision", "Overvågning"),

	PRODUCT_GROUP_WIFI_BUNDLE ("wifi", "Wi-Fi"),
	PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_AREA_SIZE ("wifi.areasize", "Lokation"),
	PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_AP ("wifi.ap", "AP"),  
	PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_INCLUDED ("wifi.included", "Inkluderet"),  
	PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_SWITCH ("wifi.switch", "Switch"),
	PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_SITE_SURVEY ("wifi.sitesurvey", "Site survey"),
	PRODUCT_GROUP_WIFI_BUNDLE_CABLING ("wifi.addon.cabling", "Kabling"),  
	PRODUCT_GROUP_WIFI_BUNDLE_GENERAL_SERVICE ("wifi.addon.service", "Servicegrad"),

	// Access (common)
	PRODUCT_GROUP_ACCESS (					"access", 				"Access"),
	PRODUCT_GROUP_ACCESS_INCLUDED (			"access.included", 	"Inkluderet"),
	PRODUCT_GROUP_ACCESS_QOS (				"access.qos", 			"QOS"),
	PRODUCT_GROUP_ACCESS_IP (				"access.ip", 			"IP adresser"),
	PRODUCT_GROUP_ACCESS_SERVICE (			"access.service", 		"Servicegrad"),
	PRODUCT_GROUP_ACCESS_REDUNDANCY (		"access.redundancy", 	"Redundans mobil"),
	PRODUCT_GROUP_ACCESS_SUPERVISION (		"access.supervision", 	"Overvågning"),

	// Fiber Erhverv Plus
	PRODUCT_GROUP_FIBER_BUNDLE (						"fiber", 						"Fiber"),
//	PRODUCT_GROUP_FIBER_BUNDLE_IP (						"fiber.ip", 					"IP"),
	PRODUCT_GROUP_FIBER_BUNDLE_FIBER_SPEED_STANDARD (	"fiber.fiber_speed_standard", 	"Fiber hastighed - Standard"),
	PRODUCT_GROUP_FIBER_BUNDLE_FIBER_SPEED_A (			"fiber.fiber_speed_a", 		"Fiber hastighed - Pris A"),
	PRODUCT_GROUP_FIBER_BUNDLE_FIBER_SPEED_B (			"fiber.fiber_speed_b", 		"Fiber hastighed - Pris B"),
	PRODUCT_GROUP_FIBER_BUNDLE_VLAN_SPEED (				"fiber.vlan_speed", 			"VLAN hastighed"),
	PRODUCT_GROUP_FIBER_BUNDLE_HARDWARE (				"fiber.hardware", 				"Hardware"),
	PRODUCT_GROUP_FIBER_BUNDLE_SUPERVISION (			"fiber.supervision", 			"Overvågning"),
	PRODUCT_GROUP_FIBER_BUNDLE_ITEMS (					"fiber.items", 				"Diverse"),
	PRODUCT_GROUP_FIBER_BUNDLE_ZONEOPRETTELSE (			"fiber.zoneoprettelse", 		"Zoneoprettelse"),
	PRODUCT_GROUP_FIBER_BUNDLE_ZONEDRIFT (				"fiber.zonedrift", 			"Zonedrift"),

	// Fiber Erhverv
	PRODUCT_GROUP_FIBER_NEW_BUNDLE (				"fiber_erhverv", 					"Fiber Erhverv"),
	PRODUCT_GROUP_FIBER_NEW_BUNDLE_SPEED (			"fiber_erhverv.speed", 			"Hastighed"),
	PRODUCT_GROUP_FIBER_NEW_BUNDLE_ROUTER_FIREWALL ("fiber_erhverv.router_firewall", 	"Router/Firewall"),
//	PRODUCT_GROUP_FIBER_NEW_BUNDLE_LAN_IP (			"fiber_erhverv.lan_ip", 			"LAN IP adresser"),
	PRODUCT_GROUP_FIBER_NEW_BUNDLE_IF_FIBER (		"fiber_erhverv.if_fiber", 			"Hvis fiber"),
	PRODUCT_GROUP_FIBER_NEW_BUNDLE_SUPERVISION (	"fiber_erhverv.supervision", 		"Overvågning"),
	PRODUCT_GROUP_FIBER_NEW_BUNDLE_INSTALLATION (	"fiber_erhverv.installation", 		"Installationskode"),
	PRODUCT_GROUP_FIBER_NEW_BUNDLE_INFRASTRUCTURE (	"fiber_erhverv.infrastructure", 	"Infrastruktur"),
	PRODUCT_GROUP_FIBER_NEW_BUNDLE_DIGGING (		"fiber_erhverv.digging", 			"Gravemeter"),
	PRODUCT_GROUP_FIBER_NEW_BUNDLE_INSPECTION (		"fiber_erhverv.inspection", 		"Besigtigelse"),
	PRODUCT_GROUP_FIBER_NEW_BUNDLE_COORDINATION (	"fiber_erhverv.coordination", 		"Leverancekoordinator"),
	PRODUCT_GROUP_FIBER_NEW_BUNDLE_MISC (			"fiber_erhverv.misc", 				"Diverse"),
//	PRODUCT_GROUP_FIBER_NEW_BUNDLE_INCLUDED (		"fiber_erhverv.included", 			"Inkluderet"),
	
	PRODUCT_GROUP_ADDON ("add-on", "Andre tilvalg"),
	PRODUCT_GROUP_ADDON_ROAMING ("add-on.roaming", "Roaming"),
	PRODUCT_GROUP_ADDON_FUNCTIONS ("add-on.functions", "Funktioner"),
	
	PRODUCT_GROUP_SWITCHBOARD ("switchboard", "Omstilling"),
	PRODUCT_GROUP_SWITCHBOARD_ADDON ("switchboard.addon", "Tilvalg"),  // Not a child of PRODUCT_GROUP_SWITCHBOARD
	// The following were added for One+, in order to make a new, less hard-coded switchboard configuration,
	PRODUCT_GROUP_SWITCHBOARD_INCLUDED ("switchboard.included", "Inkluderet"),

	PRODUCT_GROUP_SWITCHBOARD_INSTALLATION ("switchboard.installation", "Installation"),
	PRODUCT_GROUP_SWITCHBOARD_INSTALLATION_REMOTE ("switchboard.installation.remote", "Installation - remote"),
	PRODUCT_GROUP_SWITCHBOARD_INSTALLATION_ADDON ("switchboard.installation.addon", "Installation - tilkøb"),

	PRODUCT_GROUP_SWITCHBOARD_SERVICE ("switchboard.service", "Service"),

	PRODUCT_GROUP_PRODUCTION_OUTPUT ("production", "Produktion"),
	PRODUCT_GROUP_PRODUCTION_OUTPUT_CDM ("production.cdm", "CDM fortællenumre"),

	PRODUCT_GROUP_EXTRA ("extra", "Ekstra produkter"),

	// Partner settings
	PRODUCT_GROUP_PARTNER ("partner", "Partner"),
	PRODUCT_GROUP_PARTNER_INSTALLATION ("partner.installation", "Øvrige installationsydelser"),
	PRODUCT_GROUP_PARTNER_HARDWARE ("partner.hardware", "Hardware til rate"),
	PRODUCT_GROUP_PARTNER_HARDWARE_HEADSETS ("partner.hardware_headsets", "Hardware - headsets"),
	PRODUCT_GROUP_PARTNER_HARDWARE_IP_FASTNET ("partner.hardware_ip_fastnet", 	"Hardware - IP fastnet"),
	PRODUCT_GROUP_PARTNER_HARDWARE_FEATURE_PHONES ("partner.hardware_feature_phones", "Hardware - feature phones"),
	PRODUCT_GROUP_PARTNER_HARDWARE_MOBILT_BREDBAAND ("partner.hardware_mobilt_bredbaand", "Hardware - mobilt bredbånd"),
	PRODUCT_GROUP_PARTNER_BUNDLE ("partner.bundle", "Pakke"),
	PRODUCT_GROUP_PARTNER_BUNDLE_ELEMENTS ("partner.bundleelements", "Pakkeindhold"),

	// -- One+ --
	PRODUCT_GROUP_INSTALLATIONTYPE ("installation_type", "Installationstype"),

	// Løsningssiden i One+ bygges af "switchboard" grupper og "solution" grupper
	PRODUCT_GROUP_SOLUTION ("solution", "Virksomhed"),
	PRODUCT_GROUP_SOLUTION_ADDON_IDENTITY ("solution_addon_identity", "Tilvalg - id"),
	PRODUCT_GROUP_SOLUTION_ADDON_FEATURES ("solution_addon_features", "Tilvalg - funktionalitet"),
	PRODUCT_GROUP_SOLUTION_POOL_DATA ("solution_pool_data", "Puljevalg - data"),
	PRODUCT_GROUP_SOLUTION_POOL_ILD ("solution_pool_ild", "Puljevalg - ILD"),

	PRODUCT_GROUP_USER_ADDON ("user_add-on", "Brugertilvalg"),
	//	PRODUCT_GROUP_USER_ADDON_ROAMING ("user_add-on.roaming", "Roaming"),
	PRODUCT_GROUP_USER_ADDON_ROAMING_ILD ("user_add-on.roaming.ild", "Roaming - ILD"),
	PRODUCT_GROUP_USER_ADDON_FUNCTIONS ("user_add-on.functions", "Funktioner"),

	// Locations
	PRODUCT_GROUP_LOCATIONS ("locations", "Lokationer"),
	PRODUCT_GROUP_LOCATIONS_HARDWARE_SWITCHES ("locations.hardware_switches", "Hardware - switche"),
	PRODUCT_GROUP_LOCATIONS_HARDWARE_IP ("locations.hardware_ip", "Hardware - IP"),
	PRODUCT_GROUP_LOCATIONS_HARDWARE_MISC ("locations.hardware_misc", "Hardware - diverse"),
	PRODUCT_GROUP_LOCATIONS_INSTALLATION ("locations.installation", "Installation");

	private String key;
	private String displayText;
	private Integer sortIndex;
	
	private MobileProductGroupEnum(String key, String displayText) {
		this.key = key;
		this.displayText = displayText;
		this.sortIndex = 0;
	}

	private MobileProductGroupEnum(String key, String displayText, int sortIndex) {
		this.key = key;
		this.displayText = displayText;
		this.sortIndex = sortIndex;
	}
	
	public Integer getSortIndex() {
		return sortIndex;
	}

	public String getDisplayText() {
		return displayText;
	}
	
	public String getKey() {
		return key;
	}
	
	public static List<MobileProductGroupEnum> getByPrefix(String prefix) {
		List<MobileProductGroupEnum> list = new ArrayList<>();
		for(MobileProductGroupEnum value : values()) {
			if (value.name().startsWith(prefix)) {
				list.add(value);
			}
		}
		return list;
	}

	public static MobileProductGroupEnum getValueByKey(String key) {
		for(MobileProductGroupEnum value : values()) {
			if (key.equals(value.getKey())) {
				return value;
			}
		}
		return null;
	}

	public static MobileProductGroupEnum getValueByProductGroup(ProductGroup group) {
		for(MobileProductGroupEnum value : values()) {
			if (group.getUniqueName().equals(value.getKey())) {
				return value;
			}
		}
		return null;
	}

	@Transient
	public List<MobileProduct> getProducts(BusinessArea businessArea) {
		ProductGroup group = businessArea.getProductGroupByUniqueName(key);
		List<MobileProduct> list = new ArrayList<>();
		for (Product p: group.getProducts()) {
			list.add((MobileProduct) p);
		}
		return list;
	}

	@Transient
	public MobileProductGroup getProductGroup(BusinessArea businessArea) {
		return (MobileProductGroup) businessArea.getProductGroupByUniqueName(key);
	}

	@Transient
	public boolean isGroup(ProductGroup group) {
		return Objects.equals(group.getUniqueName(), key);
	}
}
