package dk.jyskit.salescloud.application.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import dk.jyskit.salescloud.application.model.FeatureType;
import lombok.Data;

@Data
public class ContractLine implements Serializable {
	private boolean totalLine = true;
	private String date;
	private String customerName;
	
	private int mobileVoice;
	private int mobileVoiceSubscribers;
	private long mobileVoiceTotalRecurring;
	
	private int switchboard;
	private int switchboardSubscribers;
	private long switchboardTotalRecurring;
	
	private int wifi;
	private int wifiBundles;
	private long wifiTotalRecurring;
	
	private final static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
	
//	public ContractLine() {
//	}
//	
//	public ContractLine(MobileContract contract, ContractFinansialInfo contractFinansialInfo) {
//		this.totalLine = false;
//		this.date = sdf.format(contract.getCreationDate());
//		this.customerName = contract.getCustomer().getCompanyName();
//		
//		if (contract.getBusinessArea().hasFeature(FeatureType.MOBILE_BUNDLES_STANDARD)) {
//			mobileVoice = 1;
//			mobileVoiceSubscribers = contractFinansialInfo.getSubscriptionCount();
//			mobileVoiceTotalRecurring = (contractFinansialInfo.getContractTotalsAfterDiscounts().getRecurringFee() / 100);
//		} else if (contract.getBusinessArea().hasFeature(FeatureType.SWITCHBOARD)) {
//			switchboard = 1;
//			switchboardSubscribers = contractFinansialInfo.getSubscriptionCount();
//			switchboardTotalRecurring = (contractFinansialInfo.getContractTotalsAfterDiscounts().getRecurringFee() / 100);
//		}
//	}
}
