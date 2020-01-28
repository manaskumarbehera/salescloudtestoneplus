package dk.jyskit.salescloud.application.pages.contractsummary;

import java.io.Serializable;
import java.util.Date;

import dk.jyskit.salescloud.application.model.ContractStatusEnum;
import dk.jyskit.salescloud.application.model.MobileContractType;
import dk.jyskit.salescloud.application.wafextension.forms.IdPropChoiceRenderer;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectOptions;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectSingle;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class ContractDetails implements Serializable {
	public static final String INGEN_RABATAFTALE = "Ingen rabataftale";

	private long fixedDiscountPercentage;
	private Date installationDate;

	@NonNull
	private Date contractStartDate;

	@NonNull
	private Date contractStartDateNetwork;

	@NonNull
	private Date contractStartDateFiberErhverv;
	
	private MobileContractType contractType;
	private ContractStatusEnum status;
	
	private String contractLength;
	private String contractSumInfo = "Årlig kontraktsum (før rabat og ekskl. moms - minimum 1000 kr. i omsætning pr. ben såfremt der skal kunne gives rabat)";

	private String contractLengthNetwork;

	private Long contractSumMobile;
	private Long contractSumFastnet;
	private Long contractSumBroadband;
	
	private Long additionToKontraktsum;
	private Long additionToKontraktsumFiberErhverv;
	private Long additionToKontraktsumNetwork;

	private Integer additionalUserChanges;

	private String callFlowChanges;
	private String existingFlexConnectSubscriptions;

	// --------------------------------

	public Date getContractStartDate() {
		if (contractStartDate != null) {
			return contractStartDate;
		}
		return contractStartDateFiberErhverv;
	}

	public Date getContractStartDateNetwork() {
		return contractStartDateNetwork;
	}
}
