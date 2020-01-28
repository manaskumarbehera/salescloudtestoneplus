package dk.jyskit.salescloud.application.pages.bundles;

import java.io.Serializable;

import lombok.Data;
import dk.jyskit.salescloud.application.model.MobileProductBundle;

@Data
public class BundleCount implements Serializable {
	private static final long serialVersionUID = 1L;
	
	MobileProductBundle bundle;
	int countNew;
	int countExisting;
	int subIndex;
	boolean selected;

	public BundleCount(MobileProductBundle bundle, int subIndex, int countNew, int countExisting) {
		this.bundle = bundle;
		this.countNew = countNew;
		this.countExisting = countExisting;
		this.subIndex = subIndex;
		this.selected = (countNew > 0);
	}
}
