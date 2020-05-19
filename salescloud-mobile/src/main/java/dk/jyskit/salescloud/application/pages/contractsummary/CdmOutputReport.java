package dk.jyskit.salescloud.application.pages.contractsummary;

import com.google.inject.Provider;
import com.x5.template.Chunk;
import com.x5.template.Theme;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.salescloud.application.services.supercontract.SuperContractService;
import dk.jyskit.salescloud.application.utils.QuarterUtils;
import dk.jyskit.waf.utils.guice.Lookup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static dk.jyskit.salescloud.application.model.LocationBundleData.INSTALLATION_ONSITE_REMOTE_TDC;
import static dk.jyskit.salescloud.application.model.LocationBundleData.INSTALLATION_TDC;
import static dk.jyskit.salescloud.application.model.PartnerData.VARIANT_TASTEBILAG;

@Slf4j
public class CdmOutputReport implements Provider<String>, Serializable {
	private final boolean partnerVersion;

	public CdmOutputReport(boolean partnerVersion) {
		this.partnerVersion = partnerVersion;
	}

	@Override
	public String get() {
        Theme theme = new Theme("themes", "reports");
        
        Chunk html = theme.makeChunk("cdm_output_report" +
				(MobileSession.get().isBusinessAreaOnePlus() ? "_oneplus_pages" : ""));
        
        MobileContract contract = MobileSession.get().getContract();

		ContractFinansialInfo infoNonNetwork 	= contract.getContractFinansialInfo(true, false, partnerVersion);
		ContractFinansialInfo infoNetwork 		= contract.getContractFinansialInfo(false, true, partnerVersion);

		html.set("partner_version", partnerVersion);

		if (BusinessAreas.FIBER_ERHVERV == contract.getBusinessArea().getBusinessAreaId()) {
	        html.set("contract_length", 		Math.max(1, contract.getContractLength()));
        	html.set("contract_sum", 			Amounts.getFormattedWithDecimals(infoNonNetwork.getRabataftaleKontraktsum()));
//        	html.set("contract_sum_mobile", 	contract.getContractSumMobile());
		} else {
        	html.set("contract_length", contract.getAdjustedContractLength());
        	html.set("contract_length_network", contract.getAdjustedContractLengthNetwork());
		}
        
    	try {
			html.set("contract_configuration_url", contract.getImplementationUrl());
			html.set("contract_configuration_username", contract.getConfigurationUsername());
			html.set("contract_configuration_password", contract.getConfigurationPassword());
		} catch (Exception e) {
			log.error("", e);
		}
    	
        html.set("is_mobile_voice", 	BusinessAreas.MOBILE_VOICE 	== contract.getBusinessArea().getBusinessAreaId());
        html.set("is_switchboard", 		BusinessAreas.SWITCHBOARD 	== contract.getBusinessArea().getBusinessAreaId());
        html.set("is_fiber", 			BusinessAreas.FIBER 		== contract.getBusinessArea().getBusinessAreaId());
        html.set("is_fiber_erhverv",	BusinessAreas.FIBER_ERHVERV	== contract.getBusinessArea().getBusinessAreaId());
        html.set("is_wifi", 			BusinessAreas.WIFI			== contract.getBusinessArea().getBusinessAreaId());
        html.set("is_one_plus", 		BusinessAreas.ONE_PLUS		== contract.getBusinessArea().getBusinessAreaId());
        html.set("is_works", 			BusinessAreas.match(BusinessAreas.TDC_WORKS, contract.getBusinessArea()));	// TODO for One
        boolean isOffice = BusinessAreas.TDC_OFFICE	== contract.getBusinessArea().getBusinessAreaId();
		html.set("is_office", 		isOffice);
        if (isOffice) {
        	html.set("version", "1");
        	html.set("efaktura_mail", contract.getEFakturaEmail());
        }
        
        if (contract.getBusinessArea().hasFeature(FeatureType.IPSA)) {
            String ipsaDiscountPercentage 	= "-";
            String ipsaDiscountStep 		= "-";
            for (DiscountScheme discountScheme : contract.getDiscountSchemes()) {
            	if (discountScheme instanceof SwitchboardIpsaDiscountScheme) {
            		ipsaDiscountPercentage 	= Amounts.getFormattedWithDecimals(((SwitchboardIpsaDiscountScheme) discountScheme).getDiscountPercentages().getRecurringFee()) + " %";
            		ipsaDiscountStep 		= "" + ((SwitchboardIpsaDiscountScheme) discountScheme).getStep();
            	}
            }
            html.set("contract_discount_pct", ipsaDiscountPercentage);
            html.set("contract_discount_step", ipsaDiscountStep);
            html.set("is_ipsa", true);
        } else {
        	html.set("is_ipsa", false);
        }

        if (contract.getBusinessArea().isOnePlus()) {
			html.set("contract_mode", contract.getContractMode().getText());
			html.set("solution_type", OnePlusSolutionType.get().toString());
			html.set("contract_sum", 			Amounts.getFormattedWithDecimals(infoNonNetwork.getRabataftaleKontraktsum()));
			html.set("contract_sum_network", 	Amounts.getFormattedWithDecimals(infoNetwork.getRabataftaleKontraktsum()));
//			{
//				Product product = ProductDao.lookup().findById(contract.getInstallationTypeBusinessEntityId());
//				html.set("installations_solution", product == null ? "" : product.getInternalName());
//			}
			if (Boolean.TRUE.equals(contract.getExistingFlexConnectSubscriptions())) {
				html.set("existing_flex_connect_subscriptions", "Ja");
			} else if (Boolean.FALSE.equals(contract.getExistingFlexConnectSubscriptions())) {
				html.set("existing_flex_connect_subscriptions", "Nej");
			} else {
				html.set("existing_flex_connect_subscriptions", "");
			}
			String installation_business = null;
			String installation_users = null;
			{
				if (contract.getLocationBundles().size() > 0) {
					LocationBundleData locationBundleData = contract.getLocationBundles().get(0);
					html.set("installation_locations", LocationBundleData.getInstallationProviderAsString(locationBundleData.getInstallationProvider()));
					if ((locationBundleData.getInstallationProvider() == INSTALLATION_TDC) || (locationBundleData.getInstallationProvider() == INSTALLATION_ONSITE_REMOTE_TDC)) {
						installation_business 	= LocationBundleData.getInstallationProviderAsString(locationBundleData.getInstallationProvider());
						installation_users 		= LocationBundleData.getInstallationProviderAsString(locationBundleData.getInstallationProvider());
					}
				} else {
					html.set("installation_locations", "");
				}
			}
			{
				if (contract.getLocationBundles().size() > 0) {
					LocationBundleData locationBundleData = contract.getLocationBundles().get(0);
					html.set("hardware_locations", LocationBundleData.getHardwareProviderAsString(locationBundleData.getHardwareProvider()));
				} else {
					html.set("hardware_locations", "");
				}
			}
			if (installation_business == null) {
				Product product = ProductDao.lookup().findById(contract.getInstallationTypeBusinessEntityId());
				installation_business = product == null ? "" : product.getInternalName();
			}
			if (installation_users == null) {
				Product product = ProductDao.lookup().findById(contract.getInstallationTypeUserProfilesEntityId());
				installation_users = product == null ? "" : product.getInternalName();
			}
			html.set("installation_business", installation_business);
			html.set("installation_users", installation_users);

			int noOfFiberPlusLocations 	= 0;
			int noOfFiberPlusQos	 	= 0;
			int noOfFiberLocations 		= 0;
			int noOfFiberQos	 		= 0;
			int noOfDSLLocations 		= 0;
			int noOfDSLQos		 		= 0;

			for (int i=0; i<contract.getLocationBundles().size(); i++) {
				LocationBundleData lbd = contract.getLocationBundles().get(i);

				if (lbd.getAccessType() == AccessTypeEnum.FIBER.getId()) {
					noOfFiberLocations++;
				}
				if (lbd.getAccessType() == AccessTypeEnum.FIBER_PLUS.getId()) {
					noOfFiberPlusLocations++;
				}
				if (lbd.getAccessType() == AccessTypeEnum.XDSL.getId()) {
					OneXdslBundleData dsl = contract.getOneXdslBundles().get(i);
					if (!AccessConstants.NON_ENTITY_ID.equals(dsl.getSpeechChannelEntityId()) &&
							!AccessConstants.NO_CHOICE_ENTITY_ID.equals(dsl.getSpeechChannelEntityId())) {
						noOfDSLQos++;
					}
					noOfDSLLocations++;
				}
			}
			html.set("access_fiber_no", 		"" + noOfFiberLocations + " - heraf 0 med QoS");
			html.set("access_fiber_plus_no", 	"" + noOfFiberPlusLocations + " - heraf 0 med QoS");
			html.set("access_dsl_no", 			"" + noOfDSLLocations + " - heraf " + noOfDSLQos + " med QoS");
			html.set("access_total_no", 		"" + (noOfFiberLocations + noOfFiberPlusLocations + noOfDSLLocations));
		} else {
			html.set("contract_sum", Amounts.getFormattedWithDecimals(infoNonNetwork.getRabataftaleKontraktsum())); // OK?
		}

        if (contract.getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CONTRACT_DISCOUNT)) {
            String discountPercentage 	= "-";
            int discountStep 			= 0;
            String discountStepAsText	= "-";
			String discountPercentageNetwork 	= "-";
			int discountStepNetwork 			= 0;
			String discountStepAsTextNetwork	= "-";
            for (DiscountScheme discountScheme : contract.getDiscountSchemes()) {
            	if (discountScheme instanceof RabatAftaleDiscountScheme) {
					DiscountPoint discountPoint = ((RabatAftaleDiscountScheme) discountScheme).getDiscountPointNonNetwork(); // CHECKMIG
            		discountPercentage 	= Amounts.getFormattedWithDecimals(discountPoint.getDiscountPercentage()) + " %";
            		discountStep 		= ((RabatAftaleDiscountScheme) discountScheme).getDiscountPointNonNetwork().getStep();	// CHECKMIG
            		discountStepAsText	= "" + (discountStep + 1);

					DiscountPoint discountPointNetwork = ((RabatAftaleDiscountScheme) discountScheme).getDiscountPointNetwork(); // CHECKMIG
					discountPercentageNetwork 	= Amounts.getFormattedWithDecimals(discountPointNetwork.getDiscountPercentage()) + " %";
					discountStepNetwork 		= ((RabatAftaleDiscountScheme) discountScheme).getDiscountPointNetwork().getStep();	// CHECKMIG
					discountStepAsTextNetwork	= "" + (discountStepNetwork + 1);
            	}
            }
//            if (((MobileCampaign) contract.getCampaigns().get(0)).isDisableContractDiscount()) {
//            	html.set("contract_discount_step", "0");
//            } else {
//            	html.set("contract_discount_step", discountStepAsText);
//            }
            if (contract.getAdjustedContractLength() == 0) {
                html.set("contract_discount_step", "-");
            } else {
                html.set("contract_discount_step", discountStepAsText);
            }
            html.set("contract_discount_pct", discountPercentage);

			if (contract.getAdjustedContractLengthNetwork() == 0) {
				html.set("contract_discount_step_network", "-");
			} else {
				html.set("contract_discount_step_network", discountStepAsTextNetwork);
			}
			html.set("contract_discount_pct_network", discountPercentageNetwork);

            MobileCampaign contractCampaign = (MobileCampaign) contract.getCampaigns().get(0);
            MobileCampaign permanentCampaign = (MobileCampaign) contract.getBusinessArea().getPermanentCampaign();
            if (contract.isPoolsMode()) {
				html.set("prisaftale_1_pool", StringUtils.defaultString(permanentCampaign.getPrisaftalePool(discountStep, contract.getAdjustedContractLength())));
				html.set("prisaftale_2_pool", StringUtils.defaultString(contractCampaign.getPrisaftalePool(discountStep, contract.getAdjustedContractLength())));
			} else {
				html.set("prisaftale_1_pool", "");
				html.set("prisaftale_2_pool", "");
			}

        	if (contractCampaign.isDisableContractDiscount()) {
        		html.set("prisaftale_1", "");
				html.set("prisaftale_1_network", "");
        	} else {
        		html.set("prisaftale_1", permanentCampaign.getPrisaftale(discountStep, contract.getAdjustedContractLength()));
				html.set("prisaftale_1_network", permanentCampaign.getPrisaftaleNetwork(discountStepNetwork, contract.getAdjustedContractLengthNetwork()));
        	}
            
            if (contractCampaign.equals(contract.getBusinessArea().getPermanentCampaign())) {
            	html.set("prisaftale_2", "");
				html.set("prisaftale_2_network", "");
            } else {
            	html.set("prisaftale_2", contractCampaign.getPrisaftale(discountStep, contract.getAdjustedContractLength()));
            	html.set("prisaftale_2_network", contractCampaign.getPrisaftaleNetwork(discountStepNetwork, contract.getAdjustedContractLengthNetwork()));
            }

            html.set("is_rabataftale", true);   // This name is going to be wrong in the future?
        } else {
        	html.set("is_rabataftale", false);
        }
		if (BusinessAreas.FIBER_ERHVERV	== contract.getBusinessArea().getBusinessAreaId()) {
			html.set("prisaftale_2", "FTTXQ" + QuarterUtils.getQuarterNo(LocalDate.now()));
		}

        String fixedDiscountPercentage = "-";
        for (DiscountScheme discountScheme : contract.getDiscountSchemes()) {
        	if (discountScheme instanceof FixedDiscount) {
        		fixedDiscountPercentage = Amounts.getFormattedWithDecimals(((FixedDiscount) discountScheme).getDiscountPercentages().getRecurringFee()) + " %";
        	}
        }
        html.set("fixed_discount", fixedDiscountPercentage);
        
        html.set("installation_type", contract.getInstallationType().getText());
        
        if (contract.getCustomer().getCompanyName() != null) {
        	html.set("customer_name", contract.getCustomer().getCompanyName().replace("&", "&amp;"));
        }
        html.set("customer_address", contract.getCustomer().getAddress() + ", " + contract.getCustomer().getZipCode() + " " + contract.getCustomer().getCity());
        html.set("customer_contact_name", contract.getCustomer().getName());
        html.set("technical_contact_name", contract.getTechnicalContactName());
        html.set("technical_contact_email", contract.getTechnicalContactEmail());
        html.set("technical_contact_phone", contract.getTechnicalContactPhone());
        html.set("customer_vatno", contract.getCustomer().getCompanyId());
        html.set("customer_phone", contract.getCustomer().getPhone());
        html.set("customer_email", contract.getCustomer().getEmail());
		html.set("seller_name", contract.getSeller().getName());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        
        html.set("date", dateFormat.format(new Date()));
        if (contract.getInstallationDate() == null) {
            html.set("installation_date", "Ikke valgt");
        } else {
            html.set("installation_date", dateFormat.format(contract.getInstallationDate()));
        }
        
        if (contract.getContractStartDate() == null) {
            html.set("contract_start_date", "Ikke valgt");
        } else {
            html.set("contract_start_date", dateFormat.format(contract.getContractStartDate()));
        }
		if (contract.getContractStartDateNetwork() == null) {
			html.set("contract_start_date_network", "Ikke valgt");
		} else {
			html.set("contract_start_date_network", dateFormat.format(contract.getContractStartDateNetwork()));
		}

        String campaignCode = "";
        String campaignCodeType = "";
	    MobileCampaign campaign = (MobileCampaign) MobileSession.get().getContract().getCampaigns().get(0);
        if (contract.getBusinessArea().hasFeature(FeatureType.SWITCHBOARD) || contract.getBusinessArea().hasFeature(FeatureType.MOBILE_BUNDLES_STANDARD)) {
        	if ((campaign.getFromDate() != null) || (campaign.getToDate() != null)) {
        		if (contract.hasOrderLineFor(contract.getBusinessArea().getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD.getKey()))) {
        			campaignCode = campaign.getCdmCode();  
        			campaignCodeType = "Kampagnekode (100% mobil)";  
        		} else {
        			campaignCode = campaign.getProductId();
        			campaignCodeType = "Kampagnekode (omstilling)";  
        		}
        	}
        }
        html.set("campaign_code", campaignCode);
        html.set("campaign_code_type", campaignCodeType);

	    html.set("cdm_code", campaign.getCdmCode() == null ? "" : campaign.getCdmCode());

	    // ---------------------------------------------------
	    
		List<String[]> cdmLines = new ArrayList<>();

		if (partnerVersion) {
			PartnerData data = MobileSession.get().getContract().getPartnerData(VARIANT_TASTEBILAG);
			for (String key : data.getValues().keySet()) {
				Object value = data.getValues().get(key);
				html.set(key, value);
			}

			List<PartnerData.TypeCountTextAmount> partnerInstallationLinesKontant =
					(List<PartnerData.TypeCountTextAmount>) data.values.get("partnerInstallationLinesKontant");
			for (PartnerData.TypeCountTextAmount typeCountTextAmount : partnerInstallationLinesKontant) {
				if (typeCountTextAmount.getProduct() == null) {
					cdmLines.add(new String[] {
							"BUNDLE_TITLE", typeCountTextAmount.getText(), "", "", "", ""
					});
				} else {
					cdmLines.add(new String[] {
							"" + typeCountTextAmount.getCount() + " stk.",
							typeCountTextAmount.getProduct().getProductId(),
							typeCountTextAmount.getProduct().getInternalName(),
							typeCountTextAmount.getAmount(),
							"",
							""
					});
				}
			}

			cdmLines.add(new String[] {
					"BUNDLE_TITLE", "Hardware til rate", "", "", "", ""
			});
			List<PartnerData.HardwareInfo> hardwareLines = (List<PartnerData.HardwareInfo>) data.values.get("hardwareLines");
			for (PartnerData.HardwareInfo hardwareLine : hardwareLines) {
				cdmLines.add(new String[] {
						"" + hardwareLine.getCount() + " stk.",
						hardwareLine.getProduct().getProductId(),
						hardwareLine.getProduct().getInternalName(),
						Amounts.getFormattedWithDecimals(hardwareLine.getKontantpris()),
						"",
						""
				});
			}

		} else {
			cdmLines = contract.getCdmOutputLines(partnerVersion);
		}
		
	    // ---------------------------------------------------
		
        List<CdmLine> lines = new ArrayList<>();
        // Chunk can't handle lists of arrays for some reason
        for (String[] strings : cdmLines) {
        	lines.add(new CdmLine(strings[0], strings[1], strings[2], strings[3], strings[4], strings[5]));
		}
		html.set("cdmLines", lines);
		
		List<KeyValueLine> aftalepapirLines = new ArrayList<>();
		
		if (BusinessAreas.WIFI == contract.getBusinessArea().getBusinessAreaId()) {
			aftalepapirLines.add(new KeyValueLine("Generelt", 	"Teknisk kontaktperson - Navn", contract.getTechnicalContactName()));
			aftalepapirLines.add(new KeyValueLine("", 			"Teknisk kontaktperson - Telefon", contract.getTechnicalContactPhone()));
			aftalepapirLines.add(new KeyValueLine("", 			"Teknisk kontaktperson - Email", contract.getTechnicalContactEmail()));
			aftalepapirLines.add(new KeyValueLine("", 			"Bemærkninger til ordrehåndtering", contract.getOrderHandlingRemarks()));
			aftalepapirLines.add(new KeyValueLine("", 			"Løsningsbeskrivelse til tekniker", contract.getTechnicalSolution()));
			aftalepapirLines.add(new KeyValueLine("", 			"Bygningstegning foreligger", contract.isBuildingPlanAvailable() ? "Ja" : "Nej"));
			aftalepapirLines.add(new KeyValueLine("", 			"Fakturering - ny konto", contract.isNewAccount() ? "Ja" : "Nej"));
			if (contract.isNewAccount()) {
				aftalepapirLines.add(new KeyValueLine("", 			"Fakturering - Faktureringsmetode", contract.getInvoicingType().toString()));
				aftalepapirLines.add(new KeyValueLine("", 			"Fakturering - Nødvendig information (Email, etc.)", contract.getInvoicingInfo()));
			} else {
				aftalepapirLines.add(new KeyValueLine("", 			"Fakturering - Kontonummer", contract.getAccountNo()));
			}
			aftalepapirLines.add(new KeyValueLine("", 			"Ordrebekræftelse pr. email", contract.getOrderConfirmationEmailAdresses()));
			for (WiFiBundleIds location : contract.getWiFiBundles()) {
				aftalepapirLines.add(new KeyValueLine("Lokation 1",	"Adresse", location.getAddress()));
				aftalepapirLines.add(new KeyValueLine("",			"Access (LID ID)", location.getLidId()));
				aftalepapirLines.add(new KeyValueLine("",			"Kontaktperson på inst. adr. - Navn", location.getContactName()));
				aftalepapirLines.add(new KeyValueLine("",			"Kontaktperson på inst. adr. - Tlf.", location.getContactPhone()));
			}
		}
		
//		if (BusinessAreas.FIBER == contract.getBusinessArea().getBusinessAreaId()) {
//			aftalepapirLines.add(new KeyValueLine("Generelt", 	"Teknisk kontaktperson - Navn", contract.getTechnicalContactName()));
//			aftalepapirLines.add(new KeyValueLine("", 			"Teknisk kontaktperson - Telefon", contract.getTechnicalContactPhone()));
//			aftalepapirLines.add(new KeyValueLine("", 			"Teknisk kontaktperson - Email", contract.getTechnicalContactEmail()));
//			aftalepapirLines.add(new KeyValueLine("", 			"Bemærkninger til ordrehåndtering", contract.getOrderHandlingRemarks()));
//			aftalepapirLines.add(new KeyValueLine("", 			"Løsningsbeskrivelse til tekniker", contract.getTechnicalSolution()));
//			aftalepapirLines.add(new KeyValueLine("", 			"Fakturering - ny konto", contract.isNewAccount() ? "Ja" : "Nej"));
//			if (contract.isNewAccount()) {
//				aftalepapirLines.add(new KeyValueLine("", 			"Fakturering - Faktureringsmetode", contract.getInvoicingType().toString()));
//				aftalepapirLines.add(new KeyValueLine("", 			"Fakturering - Nødvendig information (Email, etc.)", contract.getInvoicingInfo()));
//			} else {
//				aftalepapirLines.add(new KeyValueLine("", 			"Fakturering - Kontonummer", contract.getAccountNo()));
//			}
//			aftalepapirLines.add(new KeyValueLine("", 			"Ordrebekræftelse pr. email", contract.getOrderConfirmationEmailAdresses()));
//			for (int i = 0; i < contract.getFiberBundles().size(); i++) {
//				FiberBundleData location = contract.getFiberBundles().get(i);
//				aftalepapirLines.add(new KeyValueLine("Lokation " + (i+1),	"Adresse", location.getAddress()));
//				aftalepapirLines.add(new KeyValueLine("",			"Kontaktperson på inst. adr. - Navn", location.getContactName()));
//				aftalepapirLines.add(new KeyValueLine("",			"Kontaktperson på inst. adr. - Tlf.", location.getContactPhone()));
//				aftalepapirLines.add(new KeyValueLine("",			"Kontaktperson på inst. adr. - Email", location.getContactEmail()));
//			}
//		}
		
		html.set("aftalepapirLines", aftalepapirLines);
		
		html.set("production_output_text", StringUtils.isEmpty(contract.getProductionOutputText()) ? "" : contract.getProductionOutputText());
        
		return html.toString();
	}
	
	@Data
	@AllArgsConstructor
	class CdmLine {
		String count;
		String productId;
		String description;
		String nonRecurringAmount;
		String recurringAmount;
		String remark;
	}
	
	@Data
	@AllArgsConstructor
	class KeyValueLine {
		String category;
		String key;
		String value;
	}
}


