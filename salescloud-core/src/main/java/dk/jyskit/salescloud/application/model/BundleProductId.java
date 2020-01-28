package dk.jyskit.salescloud.application.model;

import java.io.Serializable;

public class BundleProductId implements Serializable {
	private long productGroupId;
	private long discountSchemeId;

	public int hashCode() {
		return (int) (productGroupId + discountSchemeId);
	}

	public boolean equals(Object object) {
		if (object instanceof BundleProductId) {
			BundleProductId otherId = (BundleProductId) object;
			return (otherId.productGroupId == this.productGroupId)
					&& (otherId.discountSchemeId == this.discountSchemeId);
		}
		return false;
	}
}
