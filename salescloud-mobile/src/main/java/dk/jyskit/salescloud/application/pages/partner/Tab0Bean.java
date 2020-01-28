package dk.jyskit.salescloud.application.pages.partner;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import dk.jyskit.salescloud.application.model.Segment;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Tab0Bean implements Serializable {
	private long supportNoOfUsers;
	
	@NotNull 
	private long supportPricePrUser;
	
	@NotNull
	private long supportRecurringFee;
	
//	@NotNull
//	private long installationFeeDiscount;
//
//	@NotNull
//	private long oneTimeFeeDiscount;

	@NotNull
	private int supportMonths;
	
//	private boolean rateAgreement;
	private long rateNonRecurringFee;
	
	@NotNull
	private int rateMonths;
	
	private boolean upFrontPayment;
	
	@NotNull
	private String payment;
	
	private Segment segment;
	
	private Integer pbsRegNo;
	
	private Integer pbsAccountNo;
}
