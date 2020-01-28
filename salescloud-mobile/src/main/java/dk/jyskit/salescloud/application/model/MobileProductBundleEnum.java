package dk.jyskit.salescloud.application.model;

public enum MobileProductBundleEnum {
	MOBILE_BUNDLE ("bundle.mobile", "Mobilpakke"),
	SWITCHBOARD_BUNDLE ("bundle.switchboard", "Omstilling"),
	XDSL_BUNDLE ("bundle.xdsl", "xDSL"),
	WIFI_BUNDLE ("bundle.wifi", "Wi-Fi"),
	FIBER_BUNDLE ("bundle.fiber", "Fiber"),
	OFFICE_BUNDLE ("bundle.fiber", "Office"),
	HARDWARE_BUNDLE ("bundle.hardware", "Hardware");
	
	private String key;
	private String displayText;

	private MobileProductBundleEnum(String key, String displayText) {
		this.key = key;
		this.displayText = displayText;
	}

	public String getDisplayText() {
		return displayText;
	}
	
	public String getKey() {
		return key;
	}
}
