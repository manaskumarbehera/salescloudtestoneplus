package dk.jyskit.salescloud.application.services.importdata;

import dk.jyskit.salescloud.application.model.MobileProduct;
import lombok.Data;

@Data
public abstract class Col {
	private String key;
	private boolean foundInHeader;
	private boolean mandatory;
	
	public Col(String key, boolean mandatory) {
		this.key = key;
		this.mandatory = mandatory;
	}
	
	public abstract void process(MobileProduct product, Object value, int pass);
} 
