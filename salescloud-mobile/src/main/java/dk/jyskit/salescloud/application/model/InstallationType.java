package dk.jyskit.salescloud.application.model;

import java.util.ArrayList;
import java.util.List;

public enum InstallationType {
	TDC_REMOTE 			(0, 	"TDC Remote Installation","type_remote", true, false, true),
	TDC_ON_SITE 		(1, 	"TDC On-site", "type_onsite", true, false, true),
	PARTNER 			(2, 	"Partner Installation", "type_partner", true, true, false),
	NONE 				(3, 	"Ingen Installation", "type_oyo", false, false, false),
	TDC_ON_SITE_REMOTE 	(4, 	"TDC On-site", "type_remote_onsite", true, false, true);

	private int id;
	private String text;
	private String code;
	private final boolean anyInstallation;
	private final boolean partnerInstalls;
	private final boolean tdcInstalls;

	private InstallationType(int id, String text, String code, boolean anyInstallation, boolean partnerInstalls, boolean tdcInstalls) {
		this.id 				= id;
		this.text				= text;
		this.code 				= code;
		this.anyInstallation 	= anyInstallation;
		this.partnerInstalls 	= partnerInstalls;
		this.tdcInstalls 		= tdcInstalls;
	}

	public int getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public String getCode() {
		return code;
	}

	public static List<InstallationType> valuesAsList() {
		List<InstallationType> list = new ArrayList<InstallationType>(values().length);
		for (InstallationType value : values()) {
			list.add(value);
		}
		return list;
	}

	public static InstallationType getByProduct(MobileProduct product) {
		for (InstallationType value: values()) {
			if (value.getCode().equalsIgnoreCase(product.getFlags())) {
				return value;
			}
		}
		return null;
	}
}
