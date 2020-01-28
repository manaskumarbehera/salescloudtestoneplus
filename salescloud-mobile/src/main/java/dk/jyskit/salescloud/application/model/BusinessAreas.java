package dk.jyskit.salescloud.application.model;

public interface BusinessAreas {
	// Inactive:
	public final int MOBILE_VOICE 	= 1;
	public final int SWITCHBOARD 	= 2;
	
	// Active:
	public final int OLD_ONE		= 3;
	public final int FIBER			= 4;	// TDC Fiber Erhverv Plus
	public final int WIFI			= 5;
	public final int TDC_WORKS		= 6;
	public final int TDC_OFFICE		= 7;
	public final int FIBER_ERHVERV	= 8;	// TDC Fiber Erhverv

	public final int ONE_PLUS		= 9;

	public static boolean match(int requiredBusinessAreaCode, int actualBusinessAreaCode) {
		if (requiredBusinessAreaCode == actualBusinessAreaCode) {
			return true;
		}
//		if ((TDC_WORKS == requiredBusinessAreaCode) && (ONE_PLUS == actualBusinessAreaCode)) {
//			return true;
//		}
		return false;
	}

	public static boolean match(int requiredBusinessAreaCode, BusinessArea businessArea) {
		if (businessArea == null) {
			return false;
		} else {
			return match(requiredBusinessAreaCode, businessArea.getBusinessAreaId());
		}
	}
}
