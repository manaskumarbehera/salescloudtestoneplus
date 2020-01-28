package dk.jyskit.salescloud.application.model;

import java.io.Serializable;

public class CampaignProductRelationId implements Serializable {
	private long campaignId;
	private long productId;

	public int hashCode() {
		return (int) (campaignId + productId);
	}

	public boolean equals(Object object) {
		if (object instanceof CampaignProductRelationId) {
			CampaignProductRelationId otherId = (CampaignProductRelationId) object;
			return (otherId.campaignId == this.campaignId) && (otherId.productId == this.productId);
		}
		return false;
	}
}
