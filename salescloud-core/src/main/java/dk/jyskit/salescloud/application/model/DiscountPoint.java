package dk.jyskit.salescloud.application.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Embeddable
@Data
public class DiscountPoint implements Serializable {
//	private long divisor = 100;

	@Column
	private long discountPercentage;  	// Real percentage * 100

	@Column
	private long discountAmount;  		// Real amount * 100

	@Column
	private int step;			// First step is 0

	@Column
	private int yearIndex;		// First index is 0
}
