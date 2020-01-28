package dk.jyskit.salescloud.application.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class XdslBundleIds implements Serializable {
	private Long speedEntityId;
	private List<Long> productEntityIds = new ArrayList<>();
	
	private MobileProductGroupEnum deviceGroup;
	
	public boolean hasProduct(Product product) {
		return productEntityIds.contains(product.getId());
	}

	public void setDeviceGroup(MobileProductGroupEnum deviceGroup) {
		this.deviceGroup = deviceGroup;
	}
}
