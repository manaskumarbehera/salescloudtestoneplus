package dk.jyskit.salescloud.application.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class LocationBundleData implements Serializable {
	public static final String TEXT_INSTALLATION_NONE			= "Ingen";
	public static final String TEXT_INSTALLATION_TDC			= "TDC";
	public static final String TEXT_INSTALLATION_PARTNER		= "TDC Erhvervscenter";

	public static final int INSTALLATION_NONE 					= 0;
	public static final int INSTALLATION_TDC 					= 1;
	public static final int INSTALLATION_PARTNER				= 2;

	public static final String TEXT_HARDWARE_NONE				= "Ingen";
	public static final String TEXT_HARDWARE_TDC				= "TDC";
	public static final String TEXT_HARDWARE_PARTNER			= "TDC Erhvervscenter";

	public static final int HARDWARE_NONE 						= 0;
	public static final int HARDWARE_TDC 						= 1;
	public static final int HARDWARE_PARTNER					= 2;

	@SerializedName("ar") private String addressRoad;
	@SerializedName("az") private String addressZipCode;
	@SerializedName("ac") private String addressCity;

	@SerializedName("ty") private int accessType = AccessTypeEnum.FIBER.getId();

	@SerializedName("ip") private int installationProvider		= INSTALLATION_NONE;
	@SerializedName("hp") private int hardwareProvider			= HARDWARE_NONE;

	@SerializedName("cdm") private boolean cdmOk				= false;

	@Transient
	public String getAddress() {
		return addressRoad + ", " + addressZipCode + " " + addressCity; 
	}

	@Transient
	public static String getInstallationProviderAsString(int code) {
		if (INSTALLATION_NONE == code) {
			return TEXT_INSTALLATION_NONE;
		}
		if (INSTALLATION_TDC == code) {
			return TEXT_INSTALLATION_TDC;
		}
		if (INSTALLATION_PARTNER == code) {
			return TEXT_INSTALLATION_PARTNER;
		}
		return null;
	}

	@Transient
	public static int getInstallationProviderAsCode(String text) {
		if (TEXT_INSTALLATION_NONE.equals(text)) {
			return INSTALLATION_NONE;
		}
		if (TEXT_INSTALLATION_TDC.equals(text)) {
			return INSTALLATION_TDC;
		}
		if (TEXT_INSTALLATION_PARTNER.equals(text)) {
			return INSTALLATION_PARTNER;
		}
		return -1;
	}

	@Transient
	public boolean isTDCInstallationProvider() {
		return INSTALLATION_TDC == installationProvider;
	}

	@Transient
	public static String getHardwareProviderAsString(int code) {
		if (HARDWARE_NONE == code) {
			return TEXT_HARDWARE_NONE;
		}
		if (HARDWARE_TDC == code) {
			return TEXT_HARDWARE_TDC;
		}
		if (HARDWARE_PARTNER == code) {
			return TEXT_HARDWARE_PARTNER;
		}
		return null;
	}

	@Transient
	public static int getHardwareProviderAsCode(String text) {
		if (TEXT_HARDWARE_NONE.equals(text)) {
			return HARDWARE_NONE;
		}
		if (TEXT_HARDWARE_TDC.equals(text)) {
			return HARDWARE_TDC;
		}
		if (TEXT_HARDWARE_PARTNER.equals(text)) {
			return HARDWARE_PARTNER;
		}
		return -1;
	}

	@Transient
	public boolean isTDCHardwareProvider() {
		return HARDWARE_TDC == hardwareProvider;
	}
}
