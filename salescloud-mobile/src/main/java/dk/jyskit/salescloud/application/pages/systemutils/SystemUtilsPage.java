package dk.jyskit.salescloud.application.pages.systemutils;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsoniter.any.Any;
import dk.jyskit.salescloud.application.dao.*;
import dk.jyskit.salescloud.application.extensionpoints.ObjectFactory;
import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.salescloud.application.pages.imports.ImportsUtil;
import dk.jyskit.waf.wicket.components.containers.AjaxContainer;
import org.apache.commons.io.FileUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.pages.base.BasePage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@AuthorizeInstantiation({ AdminRole.ROLE_NAME })
@SuppressWarnings("serial")
@Slf4j
public class SystemUtilsPage extends BasePage {
	@Inject
	private ContractDao contractDao;
	@Inject
	private MobileContractDao mobileContractDao;
	@Inject
	private MobileContractSummaryDao mobileContractSummaryDao;
	@Inject
	private SystemUpdateDao systemUpdateDao;
	@Inject
	private BusinessAreaDao businessAreaDao;
	@Inject
	private ObjectFactory objectFactory;

	public SystemUtilsPage(PageParameters parameters) {
		super(parameters);

		AjaxContainer contractExportContainer = new AjaxContainer("contractExport", true);
		add(contractExportContainer);

		contractExportContainer.add(new ContractsZipFileLink("link").add(new Label("label", "Hent eksporterede kontrakter")));

		if ("backupdb".equals(parameters.get("cmd").toOptionalString())) {
			try {
				Date from = new SimpleDateFormat("yyyyMMdd").parse(parameters.get("from").toOptionalString());
				try {
					for (Contract c : contractDao.findNewerThan(from)) {
						MobileContract contract = (MobileContract) c;
						MobileSession.get().setContract(contract);

						// Export contract
						try {
							JacksonJava8Module module = new JacksonJava8Module();

							module.addSerializer(MobileContract.class,
									(obj, jgen) -> {
										jgen.writeObjectField("businessArea", obj.getBusinessArea());
										jgen.writeObjectField("salesperson", obj.getSalesperson());
										jgen.writeStringField("title", obj.getTitle());
										jgen.writeStringField("offerIntroText", obj.getOfferIntroText());
										jgen.writeObjectField("category", obj.getCategory());	// ContractCategory
										jgen.writeObjectField("seller", obj.getSeller()); // BusinessEntity
										jgen.writeObjectField("customer", obj.getCustomer());
										jgen.writeBooleanField("useNonNetworkOrderLines", obj.isUseNonNetworkOrderLines());
										jgen.writeBooleanField("useNetworkOrderLines", obj.isUseNetworkOrderLines());
										jgen.writeNumberField("emphasisOnFixedMonthlyPrice", obj.getEmphasisOnFixedMonthlyPrice());
										jgen.writeNumberField("emphasisOnDeviceSecurity", obj.getEmphasisOnDeviceSecurity());
										jgen.writeNumberField("emphasisOnNewDevices", obj.getEmphasisOnNewDevices());
										jgen.writeNumberField("emphasisOnTravelling", obj.getEmphasisOnTravelling());
										jgen.writeNumberField("emphasisOnInputMobility", obj.getEmphasisOnInputMobility());
										jgen.writeNumberField("emphasisOnOrdersByPhone", obj.getEmphasisOnOrdersByPhone());
										jgen.writeNumberField("emphasisOnMainNumberWellKnown", obj.getEmphasisOnMainNumberWellKnown());
										jgen.writeNumberField("emphasisOnGoodService", obj.getEmphasisOnGoodService());
										jgen.writeNumberField("emphasisOnReceptionist", obj.getEmphasisOnReceptionist());
										jgen.writeNumberField("emphasisOnTransferCalls", obj.getEmphasisOnTransferCalls());
										jgen.writeNumberField("confidenceRating", obj.getConfidenceRating());
										jgen.writeObjectField("extraProduct1Text", obj.getExtraProduct1Text());
										jgen.writeNumberField("extraProduct1OneTimeFee", obj.getExtraProduct1OneTimeFee());
										jgen.writeNumberField("extraProduct1InstallationFee", obj.getExtraProduct1InstallationFee());
										jgen.writeNumberField("extraProduct1RecurringFee", obj.getExtraProduct1RecurringFee());
										jgen.writeObjectField("extraProduct2Text", obj.getExtraProduct2Text());
										jgen.writeNumberField("extraProduct2OneTimeFee", obj.getExtraProduct2OneTimeFee());
										jgen.writeNumberField("extraProduct2InstallationFee", obj.getExtraProduct2InstallationFee());
										jgen.writeNumberField("extraProduct2RecurringFee", obj.getExtraProduct2RecurringFee());
										jgen.writeObjectField("extraProduct3Text", obj.getExtraProduct3Text());
										jgen.writeNumberField("extraProduct3OneTimeFee", obj.getExtraProduct3OneTimeFee());
										jgen.writeNumberField("extraProduct3InstallationFee", obj.getExtraProduct3InstallationFee());
										jgen.writeNumberField("extraProduct3RecurringFee", obj.getExtraProduct3RecurringFee());
										jgen.writeStringField("installationDate", obj.getInstallationDate() == null ? ImportsUtil.NULL : ImportsUtil.DATE_FORMAT.format(obj.getInstallationDate()));
										jgen.writeStringField("contractStartDate", obj.getContractStartDate() == null ? ImportsUtil.NULL : ImportsUtil.DATE_FORMAT.format(obj.getContractStartDate()));
										jgen.writeStringField("contractStartDateNetwork", obj.getContractStartDateNetwork() == null ? ImportsUtil.NULL : ImportsUtil.DATE_FORMAT.format(obj.getContractStartDateNetwork()));
										jgen.writeStringField("statusChangedDate", obj.getStatusChangedDate() == null ? ImportsUtil.NULL : ImportsUtil.DATE_FORMAT.format(obj.getStatusChangedDate()));
										jgen.writeObjectField("status", obj.getStatus());	//ContractStatusEnum
										jgen.writeObjectField("productionOutputText", obj.getProductionOutputText());
										jgen.writeObjectField("contractLength", obj.getContractLength());
										jgen.writeObjectField("contractLengthNetwork", obj.getContractLengthNetwork());
										jgen.writeObjectField("contractType", obj.getContractType());  // MObileContractType
										jgen.writeObjectField("contractMode", obj.getContractMode());
										jgen.writeObjectField("installationType", obj.getInstallationType()); // INstallationType
										jgen.writeObjectField("salesforceNo", obj.getSalesforceNo());
										jgen.writeNumberField("supportNoOfUsers", obj.getSupportNoOfUsers());
										jgen.writeNumberField("supportPricePrUser", obj.getSupportPricePrUser());
										jgen.writeNumberField("supportRecurringFee", obj.getSupportRecurringFee());
										jgen.writeNumberField("supportMonths", obj.getSupportMonths());
										jgen.writeNumberField("rateNonRecurringFee", obj.getRateNonRecurringFee());
										jgen.writeNumberField("rateMonths", obj.getRateMonths());
										jgen.writeNumberField("upFrontPayment", obj.getUpFrontPayment());
										jgen.writeBooleanField("pbs", obj.isPbs());
										jgen.writeBooleanField("showProvision", obj.isShowProvision());
										jgen.writeNumberField("installationFeeDiscount", obj.getInstallationFeeDiscount());
										jgen.writeNumberField("oneTimeFeeDiscount", obj.getOneTimeFeeDiscount());
										jgen.writeObjectField("tmNumber", obj.getTmNumber());
										jgen.writeStringField("tmNumberDate", obj.getTmNumberDate() == null ? ImportsUtil.NULL : ImportsUtil.DATE_FORMAT.format(obj.getTmNumberDate()));
										jgen.writeObjectField("variableInstallationFees", obj.getVariableInstallationFees());
										jgen.writeObjectField("variableRecurringFees", obj.getVariableRecurringFees());
										jgen.writeObjectField("variableCategories", obj.getVariableCategories());
										jgen.writeObjectField("variableProductNames", obj.getVariableProductNames());
										jgen.writeObjectField("segment", obj.getSegment());
										jgen.writeNumberField("contractSumMobile", obj.getContractSumMobile());
										jgen.writeNumberField("contractSumFastnet", obj.getContractSumFastnet());
										jgen.writeNumberField("contractSumBroadband", obj.getContractSumBroadband());
										jgen.writeNumberField("additionToKontraktsum", obj.getAdditionToKontraktsum());
										jgen.writeNumberField("additionToKontraktsumNetwork", obj.getAdditionToKontraktsumNetwork());
										jgen.writeObjectField("orderHandlingRemarks", obj.getOrderHandlingRemarks());
										jgen.writeObjectField("technicalSolution", obj.getTechnicalSolution());
										jgen.writeObjectField("technicalContactName", obj.getTechnicalContactName());
										jgen.writeObjectField("technicalContactEmail", obj.getTechnicalContactEmail());
										jgen.writeObjectField("technicalContactPhone", obj.getTechnicalContactPhone());
										jgen.writeBooleanField("buildingPlanAvailable", obj.isBuildingPlanAvailable());
										jgen.writeBooleanField("newAccount", obj.isNewAccount());
										jgen.writeObjectField("invoicingType", obj.getInvoicingType());
										jgen.writeObjectField("invoicingInfo", obj.getInvoicingInfo());
										jgen.writeObjectField("accountNo", obj.getAccountNo());
										jgen.writeObjectField("orderConfirmationEmailAdresses", obj.getOrderConfirmationEmailAdresses());
										jgen.writeObjectField("eFakturaEmail", obj.getEFakturaEmail());
										jgen.writeObjectField("configurationPassword", obj.getConfigurationPassword());
										jgen.writeBooleanField("poolsMode", obj.isPoolsMode());
										jgen.writeObjectField("installationTypeBusinessEntityId", obj.getInstallationTypeBusinessEntityId());
										jgen.writeObjectField("installationTypeUserProfilesEntityId", obj.getInstallationTypeUserProfilesEntityId());
										jgen.writeObjectField("serviceLevelEntityId", obj.getServiceLevelEntityId());
										jgen.writeObjectField("additionalUserChanges", obj.getAdditionalUserChanges());
										jgen.writeObjectField("callFlowChanges", obj.getCallFlowChanges());
										jgen.writeObjectField("existingFlexConnectSubscriptions", obj.getExistingFlexConnectSubscriptions());
										jgen.writeObjectField("adslBundlesJson", obj.getAdslBundlesJson());
										jgen.writeObjectField("wiFiBundlesJson", obj.getWiFiBundlesJson());
										jgen.writeObjectField("fiberErhvervBundlesJson", obj.getFiberErhvervBundlesJson());
										jgen.writeObjectField("fiberErhvervPlusBundlesJson", obj.getFiberErhvervPlusBundlesJson());
										jgen.writeObjectField("locationBundlesJson", obj.getLocationBundlesJson());
										jgen.writeNumberField("countExistingSubscriptions", obj.getCountExistingSubscriptions());
										jgen.writeNumberField("countNewSubscriptions", obj.getCountNewSubscriptions());

										jgen.writeObjectField("productBundles", obj.getProductBundles());
										jgen.writeObjectField("campaigns", obj.getCampaigns());
										jgen.writeObjectField("orderLines", obj.getOrderLines());
										jgen.writeObjectField("discountSchemes", obj.getDiscountSchemes());
//							// mc.setSubscriptions(); TODO?
									}
							);

							module.addSerializer(BusinessArea.class,
									(obj, jgen) -> {
										jgen.writeNumberField("id", obj.getId());
									}
							);

							module.addSerializer(MobileCampaign.class,
									(obj, jgen) -> {
										jgen.writeNumberField("id", obj.getId());
									}
							);

							module.addSerializer(SalespersonRole.class,
									(obj, jgen) -> {
										jgen.writeNumberField("id", obj.getId());
									}
							);

							module.addSerializer(DiscountScheme.class,
									(obj, jgen) -> {
										jgen.writeNumberField("id", obj.getId());
									}
							);

							module.addSerializer(BundleProductRelation.class,
									(obj, jgen) -> {
										jgen.writeNumberField("productBundleId", obj.getProductBundleId());
										jgen.writeNumberField("productId", obj.getProductId());
										jgen.writeNumberField("sortIndex", obj.getSortIndex());
										jgen.writeBooleanField("addProductPrice", obj.isAddProductPrice());
										jgen.writeObjectField("productAccessType", obj.getProductAccessType());
									}
							);

							module.addSerializer(OrderLine.class,
									(obj, jgen) -> {
										jgen.writeNumberField("productId", obj.getProduct() == null ? 0 : obj.getProduct().getId());
										jgen.writeStringField("productBundlePublicName", obj.getBundle() == null ? null : obj.getBundle().getPublicName());
										jgen.writeObjectField("bundleType", obj.getBundle() == null ? null : ((MobileProductBundle) obj.getBundle()).getBundleType());
										jgen.writeNumberField("bundleId", obj.getBundle() == null ? 0 : obj.getBundle().getId()); // Useless for contract-owned bundles!
										jgen.writeObjectField("subIndex", obj.getSubIndex());
										jgen.writeNumberField("countNew", obj.getCountNew());
										jgen.writeNumberField("countExisting", obj.getCountExisting());
										jgen.writeBooleanField("customFlag", obj.isCustomFlag());
										jgen.writeBooleanField("customFlag1", obj.isCustomFlag1());
									}
							);

							ObjectMapper mapper = new ObjectMapper();
							mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
							mapper.registerModule(module)
									.writer(new DefaultPrettyPrinter())
									.writeValue(new File(FileUtils.getTempDirectory() + "/" + contract.getId() + ".contract"), contract);
						} catch (IOException e) {
							log.error("", e);
						}
					}
				} catch (Exception e) {
					log.error("FAIL", e);
					handleInitializationException(e);
				}
			} catch (ParseException e) {
				log.error("", e);
			}
		}

	}

	private void handleInitializationException(Exception e) {
		if (e instanceof RollbackException) {
			if (((RollbackException) e).getCause() instanceof ConstraintViolationException) {
				handleConstraintViolationException((ConstraintViolationException) ((RollbackException) e).getCause());
			} else {
				log.error("A problem occured during initialization", e);
			}
		} else if (e instanceof ConstraintViolationException) {
			handleConstraintViolationException((ConstraintViolationException) e);
		} else if (e instanceof PersistenceException) {
			handlePersistenceException((PersistenceException) e);
		} else {
			log.error("A problem occured during initialization", e);
		}
	}

	private void handleConstraintViolationException(ConstraintViolationException e) {
		for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
			log.error(constraintViolation.getMessage());
			log.error("I'm guessing this is the problem: \n" + "An object of type '"
					+ constraintViolation.getLeafBean().getClass().getSimpleName() + "' has a property '"
					+ constraintViolation.getPropertyPath() + "' which has value '"
					+ constraintViolation.getInvalidValue() + "'. The problem is: '" + constraintViolation.getMessage()
					+ "'");
		}
	}

	private void handlePersistenceException(PersistenceException e) {
		if (e.getCause() instanceof ConstraintViolationException) {
			handleConstraintViolationException((ConstraintViolationException) e.getCause());
		} else {
			log.error("We may need to improve logging here!", e);
		}
	}
}
