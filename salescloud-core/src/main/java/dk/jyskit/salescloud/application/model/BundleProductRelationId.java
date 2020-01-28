package dk.jyskit.salescloud.application.model;

import java.io.Serializable;

public class BundleProductRelationId implements Serializable {
	private long productBundleId;
	private long productId;

	public int hashCode() {
		return (int) (productBundleId + productId);
	}

	public boolean equals(Object object) {
		if (object instanceof BundleProductRelationId) {
			BundleProductRelationId otherId = (BundleProductRelationId) object;
			return (otherId.productBundleId == this.productBundleId) && (otherId.productId == this.productId);
		}
		return false;
	}
}
