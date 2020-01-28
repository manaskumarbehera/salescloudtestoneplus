package dk.jyskit.salescloud.application.model;

import dk.jyskit.salescloud.application.MobileSession;
import lombok.Data;

@Data
public class OnePlusSolutionType {
	private boolean pools;
	private boolean mobileOnly;

	public static OnePlusSolutionType get() {
		OnePlusSolutionType type = new OnePlusSolutionType();
		type.pools = MobileSession.get().getContract().isPoolsMode();
		type.mobileOnly = true;
		for (OrderLine orderLine: MobileSession.get().getContract().getOrderLines()) {
			MobileProductBundle bundle = (MobileProductBundle) orderLine.getBundle();
			if (bundle != null) {
				if (bundle.getBundleType().equals(MobileProductBundleEnum.SWITCHBOARD_BUNDLE)) {
					if (bundle.getPublicName().indexOf("mstilling") != -1) {
						type.mobileOnly = false;
					}
				}
			}
		}
		return type;
	}

	public String toString() {
		if (pools) {
			if (mobileOnly) {
				return "mobil only pulje";
			} else {
				return "løsning pulje";
			}
		} else {
			if (mobileOnly) {
				return "mobil only individuel";
			} else {
				return "løsning individuel";
			}
		}
	}
}
