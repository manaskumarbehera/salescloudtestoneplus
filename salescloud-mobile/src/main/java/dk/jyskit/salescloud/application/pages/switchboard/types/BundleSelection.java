package dk.jyskit.salescloud.application.pages.switchboard.types;

import java.io.Serializable;

import lombok.Data;
import dk.jyskit.salescloud.application.model.MobileProductBundle;

@Data
public class BundleSelection implements Serializable {
	private static final long serialVersionUID = 1L;
	
	MobileProductBundle bundle;
	boolean selected;
	
	public BundleSelection(MobileProductBundle bundle, boolean selected) {
		this.bundle 	= bundle;
		this.selected 	= selected;
	}
}
