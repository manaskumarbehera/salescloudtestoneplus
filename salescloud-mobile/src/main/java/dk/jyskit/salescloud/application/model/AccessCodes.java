package dk.jyskit.salescloud.application.model;

public interface AccessCodes {
	// Positive
	public final static String RENOGIATION 					= "genforhandling";
	public final static String VOICE_TO_SWITCHBOARD 		= "opgradering";
	public final static String FIBER_CONFIGURATOR			= "fiber_konfigurator";
	public final static String WIFI_CONFIGURATOR			= "wifi_konfigurator";
	public final static String SWITCHBOARD_CONFIGURATOR		= "omstilling_konfigurator";
	public final static String OFFICE365_CONFIGURATOR		= "office365_konfigurator";
	public final static String ONEPLUS_CONFIGURATOR			= "oneplus_konfigurator";

	// Negative
	public final static String XDSL_FEATURE_NO_ACCESS			= "xdsl_no_access";
	public final static String SWITCHBOARD_FEATURE_NO_ACCESS	= "switchboard_no_access";
}
