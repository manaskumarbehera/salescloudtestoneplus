package dk.jyskit.salescloud.application.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class DiscountOptions implements Serializable {
	private static final long serialVersionUID = 1L;

	private int discountPercentage;
	private long discountAmount; 	// real amount * 100
}
