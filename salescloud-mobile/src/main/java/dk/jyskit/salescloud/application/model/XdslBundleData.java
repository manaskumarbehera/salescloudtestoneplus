package dk.jyskit.salescloud.application.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class XdslBundleData implements Serializable {
	@SerializedName("a") private Long speedEntityId;

	private List<Long> productEntityIds = new ArrayList<>();
	
	private MobileProductGroupEnum deviceGroup;
	
	public boolean hasProduct(Product product) {
		return productEntityIds.contains(product.getId());
	}

	public void setDeviceGroup(MobileProductGroupEnum deviceGroup) {
		this.deviceGroup = deviceGroup;
	}
}
