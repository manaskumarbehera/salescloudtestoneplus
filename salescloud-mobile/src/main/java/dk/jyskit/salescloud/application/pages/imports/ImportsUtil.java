package dk.jyskit.salescloud.application.pages.imports;

import com.jsoniter.any.Any;
import dk.jyskit.salescloud.application.dao.*;
import dk.jyskit.salescloud.application.model.*;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static java.util.Objects.isNull;

@Slf4j
public class ImportsUtil {
	public final static String NULL = "null";
	public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-hhmmss");

	public static MobileContract fromJson(String json) {
		Any any = com.jsoniter.JsonIterator.deserialize(json);
		MobileContract mc = new MobileContract();

		mc.setBusinessArea(BusinessAreaDao.lookup().findById(any.get("businessArea").get("id").toLong()));
		mc.setSalesperson(SalespersonRoleDao.lookup().findById(any.get("salesperson").get("id").toLong()));
		mc.setTitle(any.get("title").as(String.class));
		mc.setOfferIntroText(any.get("offerIntroText").as(String.class));
		mc.setCategory(any.get("category").as(ContractCategory.class));
		mc.setSeller(any.get("seller").as(BusinessEntity.class));
		mc.setCustomer(any.get("customer").as(BusinessEntity.class));
		mc.setUseNonNetworkOrderLines(any.get("useNonNetworkOrderLines").toBoolean());
		mc.setUseNetworkOrderLines(any.get("useNetworkOrderLines").toBoolean());
		mc.setEmphasisOnFixedMonthlyPrice(any.get("emphasisOnFixedMonthlyPrice").toInt());
		mc.setEmphasisOnDeviceSecurity(any.get("emphasisOnDeviceSecurity").toInt());
		mc.setEmphasisOnNewDevices(any.get("emphasisOnNewDevices").toInt());
		mc.setEmphasisOnTravelling(any.get("emphasisOnTravelling").toInt());
		mc.setEmphasisOnInputMobility(any.get("emphasisOnInputMobility").toInt());
		mc.setEmphasisOnOrdersByPhone(any.get("emphasisOnOrdersByPhone").toInt());
		mc.setEmphasisOnMainNumberWellKnown(any.get("emphasisOnMainNumberWellKnown").toInt());
		mc.setEmphasisOnGoodService(any.get("emphasisOnGoodService").toInt());
		mc.setEmphasisOnReceptionist(any.get("emphasisOnReceptionist").toInt());
		mc.setEmphasisOnTransferCalls(any.get("emphasisOnTransferCalls").toInt());
		mc.setConfidenceRating(any.get("confidenceRating").toInt());
		mc.setExtraProduct1Text(any.get("extraProduct1Text").as(String.class));
		mc.setExtraProduct1OneTimeFee(any.get("extraProduct1OneTimeFee").toInt());
		mc.setExtraProduct1InstallationFee(any.get("extraProduct1InstallationFee").toInt());
		mc.setExtraProduct1RecurringFee(any.get("extraProduct1RecurringFee").toInt());
		mc.setExtraProduct2Text(any.get("extraProduct2Text").as(String.class));
		mc.setExtraProduct2OneTimeFee(any.get("extraProduct2OneTimeFee").toInt());
		mc.setExtraProduct2InstallationFee(any.get("extraProduct2InstallationFee").toInt());
		mc.setExtraProduct2RecurringFee(any.get("extraProduct2RecurringFee").toInt());
		mc.setExtraProduct3Text(any.get("extraProduct3Text").as(String.class));
		mc.setExtraProduct3OneTimeFee(any.get("extraProduct3OneTimeFee").toInt());
		mc.setExtraProduct3InstallationFee(any.get("extraProduct3InstallationFee").toInt());
		mc.setExtraProduct3RecurringFee(any.get("extraProduct3RecurringFee").toInt());
		mc.setInstallationDate(getDate(any, "installationDate"));
		mc.setContractStartDate(getDate(any, "contractStartDate"));
		mc.setContractStartDateNetwork(getDate(any, "contractStartDateNetwork"));
		mc.setStatusChangedDate(getDate(any, "statusChangedDate"));
		mc.setStatus(any.get("status").object() == null ? null : ContractStatusEnum.valueOf(any.get("status").toString()));
		mc.setProductionOutputText(any.get("productionOutputText").as(String.class));
		mc.setContractLength(any.get("contractLength").as(Integer.class));
		mc.setContractLengthNetwork(any.get("contractLengthNetwork").as(Integer.class));
		mc.setContractType(any.get("contractType").object() == null ? null : MobileContractType.valueOf(any.get("contractType").toString()));
		mc.setContractMode(any.get("contractMode").object() == null ? null : MobileContractMode.valueOf(any.get("contractMode").toString()));
		mc.setInstallationType(any.get("installationType").object() == null ? null : InstallationType.valueOf(any.get("installationType").toString()));
		// mc.setSubscriptions(); TODO?
		mc.setSalesforceNo(any.get("salesforceNo").as(Long.class));
		mc.setSupportNoOfUsers(any.get("supportNoOfUsers").toLong());
		mc.setSupportPricePrUser(any.get("supportPricePrUser").toLong());
		mc.setSupportRecurringFee(any.get("supportRecurringFee").toLong());
		mc.setSupportMonths(any.get("supportMonths").toInt());
		mc.setRateNonRecurringFee(any.get("rateNonRecurringFee").toLong());
		mc.setRateMonths(any.get("rateMonths").toInt());
		mc.setUpFrontPayment(any.get("upFrontPayment").toInt());
		mc.setPbs(any.get("pbs").toBoolean());
		mc.setShowProvision(any.get("showProvision").toBoolean());
		mc.setInstallationFeeDiscount(any.get("installationFeeDiscount").toLong());
		mc.setOneTimeFeeDiscount(any.get("oneTimeFeeDiscount").toLong());
		mc.setTmNumber(any.get("tmNumber").as(String.class));
		mc.setTmNumberDate(getDate(any, "tmNumberDate"));
		mc.setVariableInstallationFees(any.get("variableInstallationFees").as(String.class));
		mc.setVariableRecurringFees(any.get("variableRecurringFees").as(String.class));
		mc.setVariableCategories(any.get("variableCategories").as(String.class));
		mc.setVariableProductNames(any.get("variableProductNames").as(String.class));
		try {
			mc.setSegment(SegmentDao.lookup().findById(any.get("segment").get("id").as(Long.class)));
		} catch (Exception e) {
		}
		mc.setContractSumMobile(any.get("contractSumMobile").toLong());
		mc.setContractSumFastnet(any.get("contractSumFastnet").toLong());
		mc.setContractSumBroadband(any.get("contractSumBroadband").toLong());
		mc.setAdditionToKontraktsum(any.get("additionToKontraktsum").toLong());
		mc.setAdditionToKontraktsumNetwork(any.get("additionToKontraktsumNetwork").toLong());
		mc.setOrderHandlingRemarks(any.get("orderHandlingRemarks").as(String.class));
		mc.setTechnicalSolution(any.get("technicalSolution").as(String.class));
		mc.setTechnicalContactName(any.get("technicalContactName").as(String.class));
		mc.setTechnicalContactEmail(any.get("technicalContactEmail").as(String.class));
		mc.setTechnicalContactPhone(any.get("technicalContactPhone").as(String.class));
		mc.setBuildingPlanAvailable(any.get("buildingPlanAvailable").toBoolean());
		mc.setNewAccount(any.get("newAccount").toBoolean());
		mc.setInvoicingType(any.get("invoicingType").object() == null ? null : InvoicingTypeEnum.valueOf(any.get("invoicingType").toString()));
		mc.setInvoicingInfo(any.get("invoicingInfo").as(String.class));
		mc.setAccountNo(any.get("accountNo").as(String.class));
		mc.setOrderConfirmationEmailAdresses(any.get("orderConfirmationEmailAdresses").as(String.class));
		mc.setEFakturaEmail(any.get("eFakturaEmail").as(String.class));
		mc.setConfigurationPassword(any.get("configurationPassword").as(String.class));
		mc.setPoolsMode(any.get("poolsMode").toBoolean());
		mc.setInstallationTypeBusinessEntityId(any.get("installationTypeBusinessEntityId").as(Long.class));
		mc.setInstallationTypeUserProfilesEntityId(any.get("installationTypeUserProfilesEntityId").as(Long.class));
		mc.setServiceLevelEntityId(any.get("serviceLevelEntityId").as(Long.class));
		mc.setAdditionalUserChanges(any.get("additionalUserChanges").as(Integer.class));
		mc.setCallFlowChanges(any.get("callFlowChanges").toBoolean());
		mc.setExistingFlexConnectSubscriptions(any.get("existingFlexConnectSubscriptions").toBoolean());
		mc.setAdslBundlesJson(any.get("adslBundlesJson").as(String.class));
		mc.setWiFiBundlesJson(any.get("wiFiBundlesJson").as(String.class));
		mc.setFiberErhvervBundlesJson(any.get("fiberErhvervBundlesJson").as(String.class));
		mc.setFiberErhvervPlusBundlesJson(any.get("fiberErhvervPlusBundlesJson").as(String.class));
		mc.setLocationBundlesJson(any.get("locationBundlesJson").as(String.class));
		mc.setCountExistingSubscriptions(any.get("countExistingSubscriptions").toInt());
		mc.setCountNewSubscriptions(any.get("countNewSubscriptions").toInt());

		mc = MobileContractDao.lookup().saveAndFlush(mc);

		if (any.get("productBundles").size() > 0) {
			for (Any anyPb : any.get("productBundles")) {
				MobileProductBundle pb = new MobileProductBundle();
				pb.updateDates();
				pb.setBundleType(anyPb.get("bundleType").object() == null ? null : MobileProductBundleEnum.valueOf(anyPb.get("bundleType").as(String.class)));
				pb.setPublicName(anyPb.get("publicName").as(String.class));
				pb.setInternalName(anyPb.get("internalName").as(String.class));
				pb.setSortIndex(anyPb.get("sortIndex").toLong());
				pb.setActive(anyPb.get("active").toBoolean());
				pb.setAddProductPrices(anyPb.get("addProductPrices").toBoolean());
				pb.setAddProductPricesToBundlePrice(anyPb.get("addProductPricesToBundlePrice").toBoolean());
				pb.setAddToContractDiscount(anyPb.get("addToContractDiscount").as(Integer.class));
				pb.setDiscountAmounts(anyPb.get("discountAmounts").as(Amounts.class));
				pb.setBaseAmounts(anyPb.get("baseAmounts").as(Amounts.class));
				pb.setProductId(anyPb.get("productId").as(String.class));
				pb.setKvikCode(anyPb.get("kvikCode").as(String.class));
				pb.setTextInOffer(anyPb.get("textInOffer").as(String.class));
				pb.setOutputSortIndex(anyPb.get("outputSortIndex").toLong());
				pb.setOfferSortIndex(anyPb.get("offerSortIndex").toLong());
				pb.setGks(anyPb.get("gks").toBoolean());
				pb.setExtraRowInOutput(anyPb.get("extraRowInOutput").toBoolean());
				pb.setExtraRowInOutputCode(anyPb.get("extraRowInOutputCode").as(String.class));
				pb.setExtraRowInOutputKvikCode(anyPb.get("extraRowInOutputKvikCode").as(String.class));
				pb.setExtraRowInOutputText(anyPb.get("extraRowInOutputText").as(String.class));
				pb.setFlags(anyPb.get("flags").as(String.class));
				pb.setExtraRowInOffer(anyPb.get("extraRowInOffer").toBoolean());
				pb.setExtraRowInOfferText(anyPb.get("extraRowInOfferText").as(String.class));

				pb = MobileProductBundleDao.lookup().saveAndFlush(pb);

				mc.addProductBundle(pb);

				for (Any anyP : anyPb.get("products")) {
					BundleProductRelation p = new BundleProductRelation();
					p.setProductBundle(pb);
					p.setProductBundleId(pb.getId());
					p.setProductId(anyP.get("productId").toLong());
					p.setProduct(MobileProductDao.lookup().findById(p.getProductId()));
					p.setProductAccessType(anyP.get("productAccessType").object() == null ? null : ProductAccessType.valueOf(anyP.get("productAccessType").as(String.class)));
					p.setSortIndex(anyP.get("sortIndex").toLong());
					p.setAddProductPrice(anyP.get("addProductPrice").toBoolean());
					pb.addProductRelation(p);
				}
			}
		}

		if (any.get("campaigns").size() > 0) {
			for (Any anyC : any.get("campaigns")) {
				mc.addCampaign(CampaignDao.lookup().findById(anyC.get("id").as(Long.class)));
			}
		}

//		if (any.get("discountSchemes").size() > 0) {
//			for (Any anyC : any.get("discountSchemes")) {
//				mc.addDiscountScheme(DiscountSchemeDao.lookup().findById(anyC.get("id").as(Long.class)));
//			}
//		}

		if (mc.getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CONTRACT_DISCOUNT)) {
			RabatAftaleDiscountScheme discountScheme = new RabatAftaleDiscountScheme();
			if (mc.getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER_ERHVERV) {
				discountScheme.setName("TDC Erhverv Rabataftale");
			} else {
				discountScheme.setName("TEM 5 kontraktrabat");
			}
			mc.addDiscountScheme(discountScheme);
		}

		if (any.get("orderLines").size() > 0) {
			for (Any anyO : any.get("orderLines")) {
				OrderLine o = new OrderLine();
				long productId = anyO.get("productId").toLong();
				if (productId > 0) {
					o.setProduct(MobileProductDao.lookup().findById(productId));
				} else {
					MobileProductBundleEnum bundleType = MobileProductBundleEnum.valueOf(anyO.get("bundleType").toString());
					String bundlePublicName = anyO.get("productBundlePublicName").as(String.class);
					if (!isNull(bundlePublicName)) {
						MobileProductBundle bundle = null;
						for (ProductBundle mpb : mc.getProductBundles()) {
							if (bundlePublicName.equals(mpb.getPublicName())) {
								bundle = (MobileProductBundle) mpb;
								break;
							}
						}
						for (ProductBundle mpb : mc.getCampaigns().get(0).getProductBundles()) {
							if (bundlePublicName.equals(mpb.getPublicName())) {
								bundle = (MobileProductBundle) mpb;
								break;
							}
						}
						if (isNull(bundle)) {
							log.warn("Bundle not found: " + bundlePublicName);
						} else {
							o.setBundle(bundle);
						}
					}
				}
				o.setSubIndex(anyO.get("subIndex").as(Integer.class));
				o.setCountNew(anyO.get("countNew").toInt());
				o.setCountExisting(anyO.get("countExisting").toInt());
				o.setCustomFlag(anyO.get("customFlag").toBoolean());
				o.setCustomFlag1(anyO.get("customFlag1").toBoolean());
				mc.addOrderLine(o);
			}
		}

		return mc;
	}

	private static Date getDate(Any any, String key) {
		String s = any.get(key).as(String.class);
		if (NULL.equals(s)) {
			return null;
		}
		try {
			return DATE_FORMAT.parse(s);
		} catch (ParseException e) {
			log.error("Date format for key: " + key, e);
			return null;
		}
	}
}
