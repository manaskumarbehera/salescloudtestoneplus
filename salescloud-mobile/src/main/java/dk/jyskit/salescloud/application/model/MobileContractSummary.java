package dk.jyskit.salescloud.application.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Index;

import dk.jyskit.waf.application.model.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name="MobileContractSummary")
@Table(name = "t_mobilecontractsummary")
@NoArgsConstructor
@Data
public class MobileContractSummary extends BaseEntity {
	@Index
	private Long contractId;
	
	private Long salespersonId;
	
	@Column(length=100)
	private String salespersonFullName;
	
	private boolean deleted;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date contractCreationDate;
	
	@Column(length=120)
	private String customerName;
	
	@Column(length=100)
	private String division;
	
	private long totalRecurring;
	
	private int businessAreaId;
	
	private int subscriberCount;
	
	private int wifiBundles;
	
	private final static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
	
	public static MobileContractSummary create(MobileContract contract) {
		MobileContractSummary mobileContractSummary = new MobileContractSummary();
		
		mobileContractSummary.contractId = contract.getId();
		mobileContractSummary.contractCreationDate = contract.getCreationDate();
//		mobileContractSummary.contractCreationDate = sdf.format(contract.getCreationDate());
		
		mobileContractSummary.customerName = contract.getCustomer().getCompanyName();
		mobileContractSummary.deleted = contract.getDeleted();
		
		mobileContractSummary.division = contract.getSalesperson().getDivision();
		mobileContractSummary.salespersonId 		= contract.getSalesperson().getId();
		mobileContractSummary.salespersonFullName 	= contract.getSalesperson().getUser().getFullName();
		
		ContractFinansialInfo contractFinansialInfo = contract.getContractFinansialInfo(true, false, false);
		
		mobileContractSummary.totalRecurring = (contractFinansialInfo.getContractTotalsAfterDiscounts().getRecurringFee() / 100);
		
		mobileContractSummary.businessAreaId = contract.getBusinessArea().getBusinessAreaId();
		mobileContractSummary.subscriberCount = contractFinansialInfo.getSubscriptionCount();
		
		if (contract.getBusinessArea().getBusinessAreaId() == BusinessAreas.WIFI) {
			for (OrderLine orderLine : contract.getOrderLines()) {
				if ((orderLine.getBundle() != null) && (((MobileProductBundle) orderLine.getBundle()).getBundleType().equals(MobileProductBundleEnum.WIFI_BUNDLE))) {
					mobileContractSummary.wifiBundles++;
				}
			}
		} 
		return mobileContractSummary;
	}
	
	public ContractLine toContractLine() {
		ContractLine contractLine = new ContractLine();
		contractLine.setCustomerName(customerName);
		contractLine.setDate(sdf.format(contractCreationDate));
		
		contractLine.setWifiBundles(wifiBundles);
		
		if (businessAreaId == BusinessAreas.MOBILE_VOICE) {
			contractLine.setMobileVoice(1);
			contractLine.setMobileVoiceTotalRecurring(totalRecurring);
			contractLine.setMobileVoiceSubscribers(subscriberCount);
		} else if ((businessAreaId == BusinessAreas.SWITCHBOARD) ||
				(businessAreaId == BusinessAreas.TDC_WORKS) ||
				(businessAreaId == BusinessAreas.ONE_PLUS)) {
			contractLine.setSwitchboard(1);
			contractLine.setSwitchboardTotalRecurring(totalRecurring);
			contractLine.setSwitchboardSubscribers(subscriberCount);
		} else if (businessAreaId == BusinessAreas.WIFI) {
			contractLine.setWifi(1);
			contractLine.setWifiTotalRecurring(totalRecurring);
		}
		
		return contractLine;
	}
}
