package dk.jyskit.salescloud.application.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Changing sortIndex is OK. Changing names is NOT OK (without modifying the database). 
 * 
 * @author jan
 */
public enum FeatureType implements Serializable {
	BASE							(-1,  "Basis features")
	,SWITCHBOARD					(0,  "Omstilling")
	,MOBILE_BUNDLES_STANDARD		(1,  "Mobilpakker - standard")
	,MOBILE_BUNDLES_MIX				(2,  "Mobilpakker - mix")
	,XDSL							(3,  "XDSL")
	,WIFI							(4,  "Wi-Fi")
	,FIBER							(5,  "Fiber")	// Fiber Erhverv Plus
	,PARTNER_SETTINGS				(6,  "Partner settings")
	,FIXED_DISCOUNT_SPECIFIED		(7,  "Fast rabat (angivet i opsummering)")
	,FIXED_DISCOUNT_VARIABLE		(8,  "Fast rabat (afhængig af kontraktlængde)")
	,IPSA							(9,  "IPSA")
	,GKS							(10,  "GKS")
	,OUTPUT_CDM						(11,  "CDM output")
	,OUTPUT_AUTHORITY				(12,  "Fuldmagter")
	,OUTPUT_PROCESS					(13,  "Proces dokumenter")
	,OUTPUT_PARTNER_SUPPORT			(14,  "Partner support dokumenter")
	,RECURRING_FEE_SPLIT			(15,  "Vis installation og engangsbeløb separat")
	,NETWORK_COVERAGE_MAP			(16,  "Dækningskort")
	,FORDELSAFTALE					(17,  "Fordelsaftale")
	,SHOW_INSTALLATION_DATE			(18,  "Vis installationsdato på sammenfatning")	
	,SHOW_CONTRACT_START_DATE		(19,  "Vis kontraktdato på sammenfatning")
	,TEM5_PRODUCTS					(20,  "TEM5 produkter")  // TDC WORKS
	,RABATAFTALE_CONTRACT_DISCOUNT	(21,  "TDC Erhverv rabataftale")   
	,RABATAFTALE_CAMPAIGN_DISCOUNT  (22,  "TDC Erhverv kampagnepris aftale")
	,CONTRACT_ACCEPT_REPORT  		(23,  "Kontrakt accept dokument")
	,TDC_OFFICE 			 		(24,  "TDC Works")
	,FIBER_ERHVERV					(25,  "Fiber Erhverv")
	,SUBSCRIPTION_CONFIGURATION		(26,  "Konfigurering af brugere")
	,PREFERENCES					(27,  "Præferencer")
	,POOLS							(28,  "Puljer")
	,BUSINESS_FEATURES				(29,  "Tilvalg på virksomhedsniveau")
	,USER_PROFILES					(30,  "Brugerprofiler")
	,LOCATIONS						(31,  "Lokationer")
//	,HARDWARE_BUNDLES				(32,  "Hardware pakker")
	;
	
	private int sortIndex;
	private String name;

	private FeatureType(int sortIndex, String name) {
		this.sortIndex 	= sortIndex;
		this.name 		= name;
	}
	
	public int getSortIndex() {
		return sortIndex;
	}
	
	public String getKey() {
		return name;
	}
	
	public static List<FeatureType> valuesAsList() {
		List<FeatureType> list = new ArrayList<>();
		for (FeatureType type : values()) {
			list.add(type);
		}
		return list;
	}
	
	// --------------------------------
	
	public String toString() {
		return name;
	}
}
