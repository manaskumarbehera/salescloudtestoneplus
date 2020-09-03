package dk.jyskit.salescloud.application.pages.contractsummary;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.google.inject.Injector;
import dk.jyskit.salescloud.application.extensionpoints.CanOrderFilter;
import dk.jyskit.salescloud.application.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.github.rjeschke.txtmark.Processor;
import com.google.inject.Provider;
import com.x5.template.Chunk;
import com.x5.template.Theme;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.MobileProductDao;
import dk.jyskit.salescloud.application.pages.contractsummary.OfferReportDataSource.DataElement;
import dk.jyskit.salescloud.application.pages.contractsummary.OfferReportDataSource.Line;
import dk.jyskit.salescloud.application.pages.contractsummary.OfferReportDataSource.LineType;
import dk.jyskit.waf.utils.guice.Lookup;
import lombok.extern.slf4j.Slf4j;

import static dk.jyskit.salescloud.application.model.MobileProductGroupEnum.*;

@Slf4j
public abstract class AbstractContractReport implements Provider<String>, Serializable {

	boolean partnerDocument;

	public AbstractContractReport(boolean partnerDocument) {
		this.partnerDocument = partnerDocument;
	}

	@Override
	public String get() {
        Theme theme = new Theme("themes", "reports");
		theme.setJarContext(this.getClass());

		MobileContract contract = MobileSession.get().getContract();
        
//		if (contract.getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CONTRACT_DISCOUNT)) {
//			ContractFinansialInfo info = contract.getContractFinansialInfo();
//			CoreSession.get().setDiscountPoint(Lookup.lookup(SuperContractService.class).getDiscountPoint(contract.getBusinessArea(), new BigDecimal(info.getRabataftaleKontraktsum()), contract.getAdjustedContractLength()));
//		}
		
        Chunk html	= theme.makeChunk(getTemplateName());

		html.set("contract_sum_mobile", String.format(new Locale("da", "DK"), "%,.0f", Float.valueOf(contract.getContractSumMobile())));
		html.set("contract_sum_mobile", String.format(new Locale("da", "DK"), "%,.0f", Float.valueOf(contract.getContractSumMobile())));
		html.set("contract_sum_fastnet", String.format(new Locale("da", "DK"), "%,.0f", Float.valueOf(contract.getContractSumFastnet())));
		html.set("contract_sum_broadband", String.format(new Locale("da", "DK"), "%,.0f", Float.valueOf(contract.getContractSumBroadband())));

		html.set("mobile_show", (contract.getContractSumMobile() > 0));
		html.set("mobile_discount_show", (contract.getContractSumMobile() > 999) & (contract.getAdjustedContractLength() > 1));
		html.set("mobile_discount_indland", contract.getAdjustedContractLength() == 0 ? 0 : (new int[] {0, 25, 27, 29})[contract.getAdjustedContractLength()-1]);
		html.set("mobile_discount_udland", contract.getAdjustedContractLength() == 0 ? 0 : (new int[] {0, 22, 22, 22})[contract.getAdjustedContractLength()-1]);
		
		html.set("fastnet_show", (contract.getContractSumFastnet() > 0));
		html.set("fastnet_discount_show", contract.getAdjustedContractLength() == 0 ? 0 : (contract.getContractSumFastnet() > 999) & (contract.getAdjustedContractLength() > 1));
		html.set("fastnet_discount_fastnet_til_fastnet", contract.getAdjustedContractLength() == 0 ? 0 : (new int[] {0, 25, 30, 30})[contract.getAdjustedContractLength()-1]);
		html.set("fastnet_discount_fastnet_til_udland", contract.getAdjustedContractLength() == 0 ? 0 : (new int[] {0, 25, 30, 30})[contract.getAdjustedContractLength()-1]);
		html.set("fastnet_discount_fastnet_til_mobil", contract.getAdjustedContractLength() == 0 ? 0 : (new int[] {0, 5, 5, 5})[contract.getAdjustedContractLength()-1]);
		
		html.set("broadband_show", (contract.getContractSumBroadband() > 0));
		html.set("broadband_discount_show", (contract.getContractSumBroadband() > 999) & (contract.getAdjustedContractLength() > 1));
		html.set("broadband_discount", contract.getAdjustedContractLength() == 0 ? 0 : (new int[] {0, 4, 6, 8})[contract.getAdjustedContractLength()-1]);
		
		boolean isFiber = 
				((BusinessAreas.FIBER == contract.getBusinessArea().getBusinessAreaId()) || 
				  (BusinessAreas.FIBER_ERHVERV == contract.getBusinessArea().getBusinessAreaId()));
        html.set("is_mobile_voice", 	BusinessAreas.MOBILE_VOICE 	== contract.getBusinessArea().getBusinessAreaId());
        html.set("is_switchboard", 		BusinessAreas.SWITCHBOARD 	== contract.getBusinessArea().getBusinessAreaId());
        html.set("is_fiber", isFiber);
        html.set("is_fiber_erhverv_plus",	BusinessAreas.FIBER 	== contract.getBusinessArea().getBusinessAreaId());
        html.set("is_fiber_erhverv",	BusinessAreas.FIBER_ERHVERV == contract.getBusinessArea().getBusinessAreaId());
        html.set("is_wifi", 			BusinessAreas.WIFI			== contract.getBusinessArea().getBusinessAreaId());
		html.set("is_works", 			contract.getBusinessArea().isWorks());
        html.set("is_office", 			contract.getBusinessArea().isOffice());
        html.set("is_one_plus", 		contract.getBusinessArea().isOnePlus());

        html.set("has_feature_fiber", isFiber);
        html.set("is_skip_installation", isFiber);
        
        html.set("is_reduce_page_count", BusinessAreas.TDC_OFFICE == contract.getBusinessArea().getBusinessAreaId() || isFiber);
        
        html.set("has_feature_tem5_campaign_discount", contract.getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CAMPAIGN_DISCOUNT));
        
        html.set("is_partnerinstallation", InstallationType.PARTNER.equals(contract.getInstallationType()));
        
        html.set("business_area", contract.getBusinessArea().getName());
        html.set("intro_text", contract.getOfferIntroText() == null ? "" : Processor.process(contract.getOfferIntroText()));

		if (contract.getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_WORKS) {
			html.set("document_header", "TDC Erhverv Works og Mobilt Bredbånd");
		} else {
			if (partnerDocument) {
				html.set("document_header", "TDC Erhverv Rabataftale");
			} else {
				html.set("document_header", contract.getBusinessArea().getName());
			}
		}

        html.set("document_footer", contract.getSalesperson().getUser().getEmail());
        
        html.set("document_title", getTitle());
        
        html.set("date", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

		html.set("installation_fee_discount", contract.getInstallationFeeDiscount());
		html.set("one_time_fee_discount", contract.getOneTimeFeeDiscount());

        if (BusinessAreas.WIFI	== contract.getBusinessArea().getBusinessAreaId()) {
        	html.set("main_image", "/images/wifi_intelligence_overslag.jpg");
        	html.set("wifi_ap_image", "/images/wifi_ap.png");
        } else if (BusinessAreas.ONE_PLUS	== contract.getBusinessArea().getBusinessAreaId()) {
        	html.set("main_image", "/images/one_tilbud_2019.jpg");
        } else if (BusinessAreas.TDC_WORKS	== contract.getBusinessArea().getBusinessAreaId()) {
        	html.set("main_image", "/images/overslag_tilbud_works.jpg");
        } else if (isFiber) {
        	html.set("main_image", "/images/fiber_billede.jpg");
        } else if (BusinessAreas.TDC_OFFICE == contract.getBusinessArea().getBusinessAreaId()) {
        	html.set("main_image", "/images/overslag_tilbud_office365.jpg");
        } else {
        	html.set("main_image", "/images/overslag_tilbud.jpg");
        }

        html.set("checkbox_image", "/images/checkbox-icon.png");
        
        html.set("seller_name", fix(contract.getSalesperson().getUser().getFullName()));
        html.set("seller_companyName", "TDC Erhverv A/S"); // fix(contract.getSeller().getCompanyName()));
        html.set("seller_address", "Teglholmsgade 3"); // fix(contract.getSeller().getAddress()));
        html.set("seller_zipCode", "2450"); // fix(contract.getSeller().getZipCode()));
        html.set("seller_city", "København SV"); // fix(contract.getSeller().getCity()));
        html.set("seller_phone", fix(contract.getSalesperson().getUser().getSmsPhone())); 
        html.set("seller_company_id", "40075291");   // contract.getSeller().getCompanyId());

        html.set("customer_name", fix(contract.getCustomer().getName()));
        html.set("customer_companyName", fix(contract.getCustomer().getCompanyName()));
        html.set("customer_address", fix(contract.getCustomer().getAddress()));
		html.set("customer_full_address", fix(contract.getCustomer().getFullAddress()));
        html.set("customer_zipCode", fix(contract.getCustomer().getZipCode()));
        html.set("customer_city", fix(contract.getCustomer().getCity()));
        html.set("customer_phone", fix(contract.getCustomer().getPhone()));
        html.set("customer_company_id", contract.getCustomer().getCompanyId());

        String campaignInfo = ((MobileCampaign) contract.getCampaigns().get(0)).getOfferText();
        if (campaignInfo == null) {
            html.set("campaign_info", "");
        } else {
            html.set("campaign_info", Processor.process(campaignInfo));
        }
        if (contract.getBusinessArea().isOnePlus()) {
			if (contract.getContractLength() == null || contract.getContractLength() == 0) {
				html.set("contract_info_one", "");
			} else {
				html.set("contract_info_one", " Forudsætter samtidig indgåelse af " + contract.getContractLength() + "-årig TDC Erhverv Rabataftale for TDC Erhverv One+");
				html.set("contract_info1_one", " og forudsætter samtidig indgåelse af " + contract.getContractLength() + "-årig TDC Erhverv Rabataftale for TDC Erhverv One+ og mobilt bredbånd");
			}
			if (contract.getContractMode().equals(MobileContractMode.NEW_SALE)) {
				html.set("contractaftale_bullet", "");
			} else {
				html.set("contractaftale_bullet", "<li>Aftalen om TDC Erhverv Rabataftale indgås ved nedenstående underskrift og træder i kraft ved modtagelse af ordrebekræftelse på One+ løsningen. Evt. forudgående indplacering i TDC Erhverv rabataftale, vil blive annulleret og erstattet af ny rabat jf. denne aftale.</li>");
			}
			if (contract.getContractLengthNetwork() == null || contract.getContractLengthNetwork() == 0) {
				html.set("contract_info_network", contract.getContractLength() == null || contract.getContractLength() == 0 ? "" : ".");
			} else {
				if (contract.getContractLength() == null || contract.getContractLength() == 0) {
					html.set("contract_info1_network", " og forudsætter samtidig indgåelse af " + contract.getContractLengthNetwork() + "-årig aftale på TDC Erhverv Netværk");
				} else {
					html.set("contract_info1_network", " og " + contract.getContractLengthNetwork() + "-årig aftale på TDC Erhverv Netværk");
				}
				html.set("contract_info_network", (contract.getContractLength() == null || contract.getContractLength() == 0 ? " Forudsætter samtidig indgåelse af " : " og ")
						+ contract.getContractLengthNetwork() + "-årig TDC Erhverv Rabataftale for Netværk.");
				html.set("contract_info2_network", "<li>Aftalen om TDC Erhverv Rabataftale indgås ved nedenstående underskrift og træder i kraft ved modtagelse af ordrebekræftelse på One+ løsningen. Evt. forudgående indplacering i TDC Erhverv rabataftale, vil blive annulleret og erstattet af ny rabat jf. denne aftale.</li>");
			}

			html.set("is_pools_mode", contract.isPoolsMode());
			html.set("is_mobile_only", contract.isMobileOnlySolution());
			html.set("is_unity", contract.isUnityReceptionistSelected());

			{
				String summaryMobileBundles = "<table><tr><td style=\"padding-right:10px;\">Navn</td><td style=\"padding-right:10px;\">Oprettelse</td><td>Månedspris</td></tr>";
				List<ProductBundle> productBundles;
				if (contract.getBusinessArea().hasFeature(FeatureType.USER_PROFILES)) {
					productBundles = contract.getProductBundles();
					productBundles.forEach(productBundle -> log.info(productBundle.getPublicName()));
				} else {
					productBundles = contract.getCampaigns().get(0).getProductBundles();
				}
				for (ProductBundle pb : productBundles) {
					if (!Lookup.lookup(CanOrderFilter.class).accept(pb)) {
						continue;
					}

					MobileProductBundle productBundle = (MobileProductBundle) pb;
					if (contract.isPoolsMode() && !productBundle.useInPoolsMode()) {
						continue;
					} else if (!contract.isPoolsMode() && !productBundle.useInNonPoolsMode()) {
						continue;
					}
					if (productBundle.getPublicName().equals("Mobil minut One+")) {
						if ((contract.getOrderLine(productBundle) == null) || (contract.getOrderLine(productBundle).getTotalCount() == 0)) {
							continue;
						}
					}

					Amounts amounts = pb.getTotalBundleAmountsAfterAllDiscounts(OrderLineCount.one(), contract);
					summaryMobileBundles += "<tr><td style=\"padding-right:10px;\">" + pb.getPublicName() +
							"</td><td style=\"text-align:right; padding-right:10px;\">" + Amounts.getFormattedWithDecimals(amounts.getNonRecurringFees()) +
							"</td><td style=\"text-align:right;\">" + Amounts.getFormattedWithDecimals(amounts.getRecurringFee()) +
							"</td></tr>";
				}
				summaryMobileBundles += "</table>";
				html.set("summary-mobile-bundles", summaryMobileBundles);
			}

			{
				int FREE = Integer.MAX_VALUE;
				int poolDkTilDk = 0;
				for (OrderLine orderLine : contract.getOrderLines(MobileProductBundleEnum.MOBILE_BUNDLE)) {
					if (orderLine.getTotalCount() > 0) {
						if (poolDkTilDk != FREE) {
							if ("Kontorbruger One+".equals(orderLine.getBundle().getPublicName())) {
								poolDkTilDk = 50 * orderLine.getTotalCount();
							} else {
								poolDkTilDk = FREE;
							}
						}
					}
				}
				int poolDkTilEu 	= 0;
				int poolDkTilReu 	= 0;
				int poolDkTilUsa 	= 0;
				int poolDkTilVerden	= 0;
				int gb = 0;
				for (OrderLine orderLine : contract.getOrderLines(MobileProductBundleEnum.MOBILE_BUNDLE)) {
					MobileProductBundle bundle = (MobileProductBundle) orderLine.getBundle();
					if ((orderLine.getTotalCount() > 0) && (bundle.getPublicName().indexOf("pool") != -1)) {
						gb += orderLine.getTotalCount() * 30;
						if ((bundle.getPublicName().indexOf("30GB") != -1)) {
							poolDkTilEu += orderLine.getTotalCount();
						}
					}
				}
				for (OrderLine orderLine : contract.getOrderLines(PRODUCT_GROUP_SOLUTION_POOL_DATA)) {
					if ((orderLine.getTotalCount() > 0)) {
						Integer value = getNumberByPattern(orderLine.getProduct().getPublicName(), "([0-9]+) GB datapulje");
						if (value != null) {
							gb += value * orderLine.getTotalCount();
						}
					}
				}
				for (OrderLine orderLine : contract.getOrderLines(PRODUCT_GROUP_SOLUTION_POOL_ILD)) {
					if ((orderLine.getTotalCount() > 0)) {
						{
							Integer value = getNumberByPattern(orderLine.getProduct().getPublicName(), "DK til EU ([0-9]+) timer");
							if (value != null) {
								poolDkTilEu += value * orderLine.getTotalCount();
							}
						}
						{
							Integer value = getNumberByPattern(orderLine.getProduct().getPublicName(), "DK til REU ([0-9]+) timer");
							if (value != null) {
								poolDkTilReu += value * orderLine.getTotalCount();
							}
						}
						{
							Integer value = getNumberByPattern(orderLine.getProduct().getPublicName(), "DK til USA ([0-9]+) timer");
							if (value != null) {
								poolDkTilUsa += value * orderLine.getTotalCount();
							}
						}
						{
							Integer value = getNumberByPattern(orderLine.getProduct().getPublicName(), "DK til Verden ([0-9]+) timer");
							if (value != null) {
								poolDkTilVerden += value * orderLine.getTotalCount();
							}
						}
					}
				}

				String summaryPools = "<p><strong>Tale:</strong></p>" +
						"<table>" +
						"<tr><td style=\"white-space:nowrap; padding-right:10px;\">DK til DK</td><td>" + (poolDkTilDk == FREE ? "Fri tale" : poolDkTilDk + " timer") + "</td></tr>\n";
				if (poolDkTilEu > 0) {
					summaryPools += "<tr><td style=\"white-space:nowrap; padding-right:10px;\">DK til EU</td><td>" + (poolDkTilEu == FREE ? "Fri tale" : poolDkTilEu + " timer") + "</td></tr>\n";
				}
				if (poolDkTilReu > 0) {
					summaryPools += "<tr><td style=\"white-space:nowrap; padding-right:10px;\">DK til REU</td><td>" + (poolDkTilReu == FREE ? "Fri tale" : poolDkTilReu + " timer") + "</td></tr>\n";
				}
				if (poolDkTilUsa > 0) {
					summaryPools += "<tr><td style=\"white-space:nowrap; padding-right:10px;\">DK til USA</td><td>" + (poolDkTilUsa == FREE ? "Fri tale" : poolDkTilUsa + " timer") + "</td></tr>\n";
				}
				if (poolDkTilVerden > 0) {
					summaryPools += "<tr><td style=\"white-space:nowrap; padding-right:10px;\">DK til Verden</td><td>" + (poolDkTilVerden == FREE ? "Fri tale" : poolDkTilVerden + " timer") + "</td></tr>\n";
				}
				summaryPools += "</table>" +
						"<p><strong>Mobildata:</strong></p>" +
						"<table>" +
						"<tr>" +
						"<td style=\"white-space:nowrap; padding-right:10px;\">Mobildata til brug i DK/EU</td>" +
						"<td>" + gb + " GB</td>" +
						"</tr>" +
						"</table>";
				html.set("summary-pools", summaryPools);
			}

			ContractFinansialInfo contractFinansialInfo = contract.getContractFinansialInfo(false, true, partnerDocument);	// CHECKMIG

			html.set("rabataftale_kontraktsum_network", Amounts.getFormattedNoDecimals(contractFinansialInfo.getRabataftaleKontraktsum()));
			for (DiscountScheme discountScheme : contract.getDiscountSchemes()) {
				if (discountScheme instanceof RabatAftaleDiscountScheme) {
					DiscountPoint discountPoint = ((RabatAftaleDiscountScheme) discountScheme).getDiscountPointNetwork();
					html.set("contract_discount_pct_network", Amounts.getFormattedWithDecimals(discountPoint.getDiscountPercentage()));
				}
			}

			List<Line> mobileBundleLines = new ArrayList<>();
			for (ProductBundle bundle: contract.getProductBundles()) {
				if (Lookup.lookup(CanOrderFilter.class).accept(bundle)) {
					String amountStr = Amounts.getFormattedWithDecimals(bundle.getTotalBundleAmountsAfterAllDiscounts(OrderLineCount.one(), contract).getRecurringFee());
					mobileBundleLines.add(new Line("", "", bundle.getPublicName(), amountStr, amountStr));
				}
			}
			html.set("mobileBundleLines", mobileBundleLines);
		}
		if (BusinessAreas.FIBER_ERHVERV == contract.getBusinessArea().getBusinessAreaId()) {
	        html.set("is_fiber_erhverv_rabataftale", contract.getAdjustedContractLength() > 0);
	        html.set("contract_length", Math.max(1, contract.getAdjustedContractLength()));
	        for (DiscountScheme discountScheme : contract.getDiscountSchemes()) {
	        	if (discountScheme instanceof RabatAftaleDiscountScheme) {
					DiscountPoint discountPoint = ((RabatAftaleDiscountScheme) discountScheme).getDiscountPointNonNetwork(); 	// CHECKMIG
	        		if (BusinessAreas.FIBER_ERHVERV == contract.getBusinessArea().getBusinessAreaId()) {
//	        			html.set("contract_discount_pct", Amounts.getFormattedNoDecimals(discountPoint.getDiscountPercentage() * 100 / discountPoint.getDivisor()) + "%");
	        			html.set("contract_discount_pct", Amounts.getFormattedNoDecimals(discountPoint.getDiscountPercentage()) + "%");
	        		} else {
//	        			html.set("contract_discount_pct", Amounts.getFormattedWithDecimals(discountPoint.getDiscountPercentage() * 100 / discountPoint.getDivisor()));
	        			html.set("contract_discount_pct", Amounts.getFormattedWithDecimals(discountPoint.getDiscountPercentage()));
	        		}
	        	}
	        }
			ContractFinansialInfo contractFinansialInfo = contract.getContractFinansialInfo(true, false, partnerDocument);	// CHECKMIG
	        html.set("rabataftale_kontraktsum", Amounts.getFormattedNoDecimals(contractFinansialInfo.getRabataftaleKontraktsum()));
		} else {
	        html.set("contract_length", contract.getAdjustedContractLength());
	        html.set("contract_length_network", contract.getAdjustedContractLengthNetwork());
		}
        html.set("contract_date", new SimpleDateFormat("d. MMMM yyyy", CoreSession.get().getLocale()).format(contract.getLastModificationDate()));
        if (contract.getContractStartDate() == null) {
        	html.set("contract_start_date", "");
        	html.set("contract_end_date", "");
        } else {
        	html.set("contract_start_date", new SimpleDateFormat("d. MMMM yyyy", CoreSession.get().getLocale()).format(contract.getContractStartDate()));
    		if (BusinessAreas.FIBER_ERHVERV == contract.getBusinessArea().getBusinessAreaId()) {
    			Date date;
    			if (contract.getAdjustedContractLength() > 0) {
    				date = DateUtils.addDays(DateUtils.addYears(contract.getContractStartDate(), contract.getAdjustedContractLength()), -1);
    			} else {
    				date = contract.getContractStartDate();
    			}
            	html.set("contract_end_date", new SimpleDateFormat("d. MMMM yyyy", CoreSession.get().getLocale()).format(date));
    		} else {
            	html.set("contract_end_date", new SimpleDateFormat("d. MMMM yyyy", 
            			CoreSession.get().getLocale()).format(DateUtils.addYears(contract.getContractStartDate(), contract.getAdjustedContractLength())));
    		}
        }

		if (contract.getContractStartDateNetwork() == null) {
			html.set("contract_start_date_network", "");
			html.set("contract_end_date_network", "");
		} else {
			html.set("contract_start_date_network", new SimpleDateFormat("d. MMMM yyyy", CoreSession.get().getLocale()).format(contract.getContractStartDateNetwork()));
			html.set("contract_end_date_network", new SimpleDateFormat("d. MMMM yyyy",
					CoreSession.get().getLocale()).format(DateUtils.addYears(contract.getContractStartDateNetwork(), contract.getAdjustedContractLengthNetwork())));
		}

		OfferReportDataSource.Data data = new OfferReportDataSource().getData(contract, partnerDocument);

		html.set("installation_total", 	Amounts.getFormattedWithDecimals(data.getTotal(FeeCategory.INSTALLATION_FEE)));
		html.set("onetime_total", 		Amounts.getFormattedWithDecimals(data.getTotal(FeeCategory.ONETIME_FEE)));
		html.set("recurring_total", 	Amounts.getFormattedWithDecimals(data.getTotal(FeeCategory.RECURRING_FEE)));

		html.set("installation_is_zero", 	data.isTotalZero(FeeCategory.INSTALLATION_FEE));
		html.set("onetime_is_zero", 		data.isTotalZero(FeeCategory.ONETIME_FEE));
		html.set("recurring_is_zero", 		data.isTotalZero(FeeCategory.RECURRING_FEE));

		// Detail pages
        
		{
	        List<Line> lines = new ArrayList<>();
	        for (DataElement element : data.getCategoryToLines().get(FeeCategory.RECURRING_FEE)) {
	        	lines.add(element.asLine());
			}
			html.set("recurringLines", lines);
		}
        
		if (contract.getBusinessArea().hasFeature(FeatureType.RECURRING_FEE_SPLIT)) {
			{
		        List<Line> lines = new ArrayList<>();
		        for (DataElement element : data.getCategoryToLines().get(FeeCategory.ONETIME_FEE)) {
		        	lines.add(element.asLine());
				}
				html.set("oneTimeFeeLines", lines);
			}
			{
		        List<Line> lines = new ArrayList<>();
				for (DataElement element : data.getCategoryToLines().get(FeeCategory.INSTALLATION_FEE)) {
					lines.add(element.asLine());
				}
				html.set("installationFeeLines", lines);
			}
		} else {
	        {
		        List<Line> lines = new ArrayList<>();
		        for (DataElement element : data.getCategoryToLines().get(FeeCategory.NON_RECURRING_FEE)) {
		        	lines.add(element.asLine());
				}
				html.set("nonRecurringLines", lines);
	        }
		}

		if (contract.getBusinessArea().hasFeature(FeatureType.LOCATIONS)) {
			html.set("has_feature_locations", true);
			html.set("has_any_locations", contract.getLocationBundles().size() > 0);
			List<Line> lines = new ArrayList<>();

			int locationIndex = -1;
			for (LocationBundleData location : contract.getLocationBundles()) {
				locationIndex++;

				if (AccessTypeEnum.NONE.equals(AccessTypeEnum.getById(location.getAccessType()))) {
					lines.add(new Line("header", "", location.getAddress(), "", ""));
				} else {
					lines.add(new Line("header", "", AccessTypeEnum.getById(location.getAccessType()).getText() + " - " + location.getAddress(),
							"", ""));
				}

				boolean hardwareHeaderPrinted 		= false;
				boolean installationHeaderPrinted 	= false;

				String[] productGroupKeys = new String[] {
						PRODUCT_GROUP_XDSL_BUNDLE.getKey(),
						PRODUCT_GROUP_ACCESS.getKey(),
						PRODUCT_GROUP_LOCATIONS.getKey()};
				for (String productGroupKey : productGroupKeys) {
					for (ProductGroup group : contract.getBusinessArea().getProductGroupChildren(productGroupKey)) {
						for (OrderLine orderLine: contract.getOrderLinesBySubIndex(locationIndex)) {
							Product product = orderLine.getProduct();
							if (Objects.equals(product.getProductGroup(), group)) {
								if ((product != null) && (!((MobileProduct) product).isExcludeFromOffer())) {
									if (PRODUCT_GROUP_LOCATIONS_HARDWARE_SWITCHES.getKey().equals(group.getUniqueName()) ||
											PRODUCT_GROUP_LOCATIONS_HARDWARE_MISC.getKey().equals(group.getUniqueName()) ||
											PRODUCT_GROUP_LOCATIONS_HARDWARE_IP.getKey().equals(group.getUniqueName())) {
										if (partnerDocument != location.isTDCHardwareProvider() ||
												(!partnerDocument &&
														(PRODUCT_GROUP_LOCATIONS_HARDWARE_SWITCHES.getKey().equals(group.getUniqueName()) ||
																"Forsendelsesgebyr".equals(product.getPublicName()))
												)) {
											if (!hardwareHeaderPrinted) {
												hardwareHeaderPrinted = true;
												lines.add(new Line("", "", "-- Hardware --", "", ""));
											}
											lines.add(new Line("", "" + orderLine.getCountNew(), product.getPublicName(), "", ""));
										}
									} else if (PRODUCT_GROUP_LOCATIONS_INSTALLATION.getKey().equals(group.getUniqueName())) {
										if (partnerDocument != location.isTDCInstallationProvider()) {
											if (!installationHeaderPrinted) {
												installationHeaderPrinted = true;
												lines.add(new Line("", "", "-- Installation --", "", ""));
											}
											lines.add(new Line("", "" + orderLine.getCountNew(), product.getPublicName(), "", ""));
										}
									} else {
										lines.add(new Line("", "" + orderLine.getCountNew(), product.getPublicName(), "", ""));
									}
								}
							}
						}
					}
				}
				lines.add(new Line("space", "", "", "", ""));
			}
			html.set("locationLines", lines);
		}

		if (contract.getBusinessArea().hasFeature(FeatureType.WIFI)) {
	        html.set("has_feature_wifi", true);
	        List<Line> lines = new ArrayList<>();
	        
	        for (WiFiBundleIds wifi : contract.getWiFiBundles()) {
	        	lines.add(new Line("header", "", wifi.getAddress(), "", ""));
	        	
	        	MobileProduct accessPoint = ((MobileProductDao) Lookup.lookup(MobileProductDao.class)).findById(wifi.getAccessPointEntityId()); 
	        	lines.add(new Line("", String.valueOf(wifi.getAccessPointCount()), accessPoint.getPublicName(), "", ""));
	        	
	        	Long areaSizeEntityId = wifi.getAreaSizeEntityId();
	        	if (areaSizeEntityId != null) {
	        		MobileProduct areaSizeSurvey = ((MobileProductDao) Lookup.lookup(MobileProductDao.class)).findById(areaSizeEntityId); 
	        		lines.add(new Line("", "1", areaSizeSurvey.getPublicName(), "", ""));
	        	}
	        	
	        	Long switchEntityId = wifi.getSwitchEntityId();
	        	if (switchEntityId != null) {
	        		MobileProduct switchProduct = ((MobileProductDao) Lookup.lookup(MobileProductDao.class)).findById(switchEntityId); 
	        		lines.add(new Line("", "1", switchProduct.getPublicName(), "", ""));
	        	}
	        	
	        	Long siteSurveyEntityId = wifi.getSiteSurveyEntityId();
	        	if (siteSurveyEntityId != null) {
	        		MobileProduct siteSurvey = ((MobileProductDao) Lookup.lookup(MobileProductDao.class)).findById(siteSurveyEntityId); 
	        		lines.add(new Line("", "1", siteSurvey.getPublicName(), "", ""));
	        	}
	        	
	        	lines.add(new Line("space", "", "", "", ""));
			}
			html.set("wifiLines", lines);
		} else {
	        html.set("has_feature_wifi", false);
		}

		if (BusinessAreas.FIBER == contract.getBusinessArea().getBusinessAreaId()) {
			BusinessArea businessArea = contract.getBusinessArea();
	        List<Line> lines = new ArrayList<>();
	        String footnotes = "";

			for (int locationIndex = 0; locationIndex < contract.getFiberErhvervPlusBundles().size(); locationIndex++) {
				FiberErhvervPlusBundleData bundleData = contract.getFiberErhvervPlusBundles().get(locationIndex);
				// MobileSession.get().setPricingSubIndex(locationIndex);  // Unnecessary
	        	lines.add(new Line("header", "", bundleData.getAddress(), "", ""));

				MobileProduct product = (MobileProduct) businessArea.getProductById(bundleData.getFiberSpeedEntityId());
				if (product == null) {
					continue;
				}
				
				Map<MobileProduct, Integer> productToCountMap = new HashMap<>();
				
				MobileProductGroup mainGroup = (MobileProductGroup) businessArea.getProductGroupByUniqueName(
						MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE.getKey());
				for (ProductGroup mobileProductGroup : mainGroup.getChildProductGroups()) {
					for (OrderLine orderLine: contract.getOrderLines(mobileProductGroup, locationIndex)) {
						productToCountMap.put((MobileProduct) orderLine.getProduct(), orderLine.getCountNew());
					}
				}

				for (Product p : businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE_ITEMS.getKey()).getProducts()) {
					if (p.getMinCount().intValue() == 1 && p.getMaxCount().intValue() == 1) {
						productToCountMap.put((MobileProduct) p, 1);
					}
				}

				String s = sortAndAddProductLines(lines, productToCountMap);
				if (!StringUtils.isEmpty(s) && StringUtils.isEmpty(footnotes)) {
					footnotes = s;
				}

				lines.add(new Line("space", "", "", "", ""));
			}
			html.set("fiberLines", lines);
			html.set("footnotes", footnotes);
		}

		if (BusinessAreas.FIBER_ERHVERV == contract.getBusinessArea().getBusinessAreaId()) {
			BusinessArea businessArea = contract.getBusinessArea();
	        List<Line> lines = new ArrayList<>();
			String footnotes = "";

			for (int locationIndex = 0; locationIndex < contract.getFiberErhvervBundles().size(); locationIndex++) {
				FiberErhvervBundleData bundleData = contract.getFiberErhvervBundles().get(locationIndex);
				// MobileSession.get().setPricingSubIndex(locationIndex);  // Unnecessary
	        	lines.add(new Line("header", "", bundleData.getAddress(), "", ""));

				Map<MobileProduct, Integer> productToCountMap = new HashMap<>();
				
				MobileProductGroup mainGroup = (MobileProductGroup) businessArea.getProductGroupByUniqueName(
						MobileProductGroupEnum.PRODUCT_GROUP_FIBER_NEW_BUNDLE.getKey());
				for (ProductGroup mobileProductGroup : mainGroup.getChildProductGroups()) {
					for (OrderLine orderLine: contract.getOrderLines(mobileProductGroup, locationIndex)) {
						productToCountMap.put((MobileProduct) orderLine.getProduct(), orderLine.getCountNew());
					}
				}

				for (ProductGroup group: mainGroup.getChildProductGroups()) {
					for (Product p : group.getProducts()) {
						if (p.getMinCount().intValue() == 1 && p.getMaxCount().intValue() == 1) {
							productToCountMap.put((MobileProduct) p, 1);
						}
					}
				}

				String s = sortAndAddProductLines(lines, productToCountMap);
				if (!StringUtils.isEmpty(s) && StringUtils.isEmpty(footnotes)) {
					footnotes = s;
				}
				html.set("footnotes", footnotes);
				lines.add(new Line("space", "", "", "", ""));
			}
			html.set("fiberLines", lines);
			html.set("footnotes", footnotes);
		}
		
		// Summary pages

		html.set("summaryLines", data.getSummaryLines());
		html.set("wifiLocationLines", data.getWifiLocationLines());
		
		setProperties(html);
		
		return html.toString();
	}

	private Integer getNumberByPattern(String s, String patternStr) {
		Pattern p = Pattern.compile(patternStr);
		Matcher m = p.matcher(s);
		if (m.find()) {
			return Integer.valueOf(m.group(1));
		}
		return null;
	}

	private String sortAndAddProductLines(List<Line> lines, Map<MobileProduct, Integer> productToCountMap) {
		String footnotes = "";
		MobileProduct product;
		List<MobileProduct> sortedProducts = new ArrayList<>(productToCountMap.keySet());
		Collections.sort(sortedProducts, new Comparator<MobileProduct>() {
			@Override
			public int compare(MobileProduct a, MobileProduct b) {
				if (((MobileProductGroup) a.getProductGroup()).getOutputSortIndex() == ((MobileProductGroup) b.getProductGroup()).getOutputSortIndex()) {
					return (Long.valueOf(a.getOutputSortIndex()).compareTo(b.getOutputSortIndex()));
				} else {
					return (Long.valueOf(((MobileProductGroup) a.getProductGroup()).getOutputSortIndex()).compareTo(((MobileProductGroup) b.getProductGroup()).getOutputSortIndex()));
				}
			}
		});
		
		for (Product p : sortedProducts) {
			product = (MobileProduct) p;
			int countForLocation = productToCountMap.get(product);
			if (!product.isExcludeFromOffer()) {
				String name = product.getPublicName();
				if ("6940024".equals(product.getProductId())) {
					name = name + " *";

					footnotes = "* TDC Erhverv konfigurerer wifi accespunktet. TDC datatekniker tilbyder at skrue accespunktet op på væggen eller i loftet, hvis de interne kablings-/strømforhold er til det. Kabling skal være Cat5 og max afstand fra router er 100m. TDC udfører ikke intern kabling hos kunden og der udføres ikke dækningsprøve.<br/><br/>";
				} else if ("4401714".equals(product.getProductId())) {
					name = name + " **";
					footnotes += "** Opsætning af ekstra wifi accespunkter, ud over det første, udføres på regning. Timepris kr. 1.500,-.\n";
				}
				lines.add(new Line("", "" + countForLocation, name, "", ""));
			}
		}
		return footnotes;
	}

	protected abstract void setProperties(Chunk html);

	protected abstract String getTitle();

	protected abstract String getTemplateName();

	private String fix(String text) {
		if (text == null) {
			return "";
		} else {
			return text.replace("&", "&amp;");
		}
	}
}
