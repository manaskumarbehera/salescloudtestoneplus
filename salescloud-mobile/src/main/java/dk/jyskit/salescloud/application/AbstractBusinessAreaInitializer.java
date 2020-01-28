package dk.jyskit.salescloud.application;

import com.google.inject.Inject;
import dk.jyskit.salescloud.application.dao.*;
import dk.jyskit.salescloud.application.extensions.MobileObjectFactory;
import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.salescloud.application.pages.CorePageIds;
import dk.jyskit.salescloud.application.pages.MobilePageIds;
import dk.jyskit.waf.utils.dataimport.DataImportException;
import dk.jyskit.salescloud.application.services.importdata.DataImporter;
import dk.jyskit.salescloud.application.services.importdata.ProductImportHandler;
import dk.jyskit.waf.application.dao.RoleDao;
import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.model.BaseUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

import static dk.jyskit.salescloud.application.model.FeatureType.BASE;
import static dk.jyskit.salescloud.application.model.MobileProductGroupEnum.*;
import static dk.jyskit.salescloud.application.model.MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_INCLUDED;

@Slf4j
/*
This is a newer, better version of CopyInitializer, based on these principles:
Features are added after initialization - as upgrades
When a feature is added, the following is added:
- Groups relevant to features are added
- Products (from spreadsheet - products from a source business area can be exported in admin mode)
- Pages relevant to features are added
 */
public abstract class AbstractBusinessAreaInitializer extends BusinessAreaInitializer {
	@Inject protected BusinessAreaDao businessAreaDao;
	@Inject protected CampaignDao campaignDao;
	@Inject protected ProductBundleDao productBundleDao;
	@Inject protected UserDao userDao;
	@Inject protected RoleDao roleDao;
	@Inject protected SystemUpdateDao systemUpdateDao;
	@Inject protected MobileObjectFactory objectFactory;

	private String pathToSpreadsheet;

	public AbstractBusinessAreaInitializer(String name, int businessAreaId, String businessAreaName, String introText, String pathToSpreadsheet) {
		super(name, businessAreaId, businessAreaName, introText);
		this.pathToSpreadsheet = pathToSpreadsheet;
	}

	@Override
	public void initBusinessArea() {
		super.initBusinessArea();

		Campaign campaign;

		campaign = objectFactory.createCampaign();
		campaign.setName("Ingen kampagne");
		campaign.setFromDate(null);
		campaign.setToDate(null);
		campaign = campaignDao.save(campaign);

		businessArea.addCampaign(campaign);

		businessArea.setStandardDiscountMatrix(getStandardDiscountMatrix());
		businessArea.setStandardDiscountMatrixNetwork(getStandardDiscountMatrixNetwork());

		configureFeature(businessArea, BASE);

//		initTestContracts(businessArea);

//		return businessArea;
	}

	protected void configureFeature(BusinessArea businessArea, FeatureType featureType) {
		businessArea.addFeature(featureType);
		if (addGroups(businessArea, featureType)) {
			importProducts(businessArea, featureType);
		}

		addBundlesToDefaultCampaign(businessArea.getPermanentCampaign(), featureType);
		addPages(businessArea, featureType);
	}

	@Override
	public void initProducts(BusinessArea businessArea) {
		// Not used in this family of initializers
	}

	@Override
	public void initPages(BusinessArea businessArea) {
		// Not used in this family of initializers
	}

	private void importProducts(BusinessArea businessArea, FeatureType featureType) {
		// Get products relevant to feature from spreadsheet
		try {
			ProductImportHandler importHandler = new ProductImportHandler(businessArea);
			importHandler.setProductGroupsFilter(getProductGroupsByFeature(featureType));
			MobileSalescloudApplication.get().getInjector().injectMembers(importHandler);
			DataImporter dataImporter = new DataImporter(importHandler, getFileFromClasspath(pathToSpreadsheet));
			dataImporter.getData();
		} catch (DataImportException e) {
			log.error("Failed to import excel file", e);
		}
	}

	protected String getStandardDiscountMatrix() {
//		return "10000/130000%,140000%,160000%,170000%,190000%,200000%#250000%,280000%,310000%,340000%,370000%,400000%#270000%,300000%,330000%,360000%,390000%,420000%";
		return "1300%,1400%,1600%,1700%,1900%,2000%#2500%,2800%,3100%,3400%,3700%,4000%#2700%,3000%,3300%,3600%,3900%,4200%";
	}

	protected String getStandardDiscountMatrixNetwork() {
//		return "10000/130000%,140000%,160000%,170000%,190000%,200000%#250000%,280000%,310000%,340000%,370000%,400000%#270000%,300000%,330000%,360000%,390000%,420000%";
		return "1300%,1400%,1600%,1700%,1900%,2000%#2500%,2800%,3100%,3400%,3700%,4000%#2700%,3000%,3300%,3600%,3900%,4200%";
	}

	@Override
	public void makeUpgrades() {
		makeSystemUpdates(businessArea);
		businessAreaDao.save(businessArea);
	}

	protected Map<MobileProductGroupEnum, MobileProductGroupEnum> getGroupMapper() {
		Map<MobileProductGroupEnum, MobileProductGroupEnum> map = new HashMap<>();
		return map;
	}

	private void ignoreBundleGroups(List<MobileProductGroupEnum> groupsToIgnore, MobileProductGroupEnum bundleGroupType) {
		for (MobileProductGroupEnum childGroup: MobileProductGroupEnum.getByPrefix(bundleGroupType.name())) {
			groupsToIgnore.add(childGroup);
		}
	}

	protected void createGroupAndChildGroups(BusinessArea businessArea, MobileProductGroupEnum groupType) {
		ProductGroup bundleGroup = createProductGroup(businessArea, groupType);
		for (MobileProductGroupEnum childGroup: MobileProductGroupEnum.getByPrefix(groupType.name() + "_")) {
			createProductGroup(bundleGroup, childGroup);
		}
	}

	protected boolean addGroups(BusinessArea businessArea, FeatureType featureType) {
		boolean hasGroups = false;
		for (MobileProductGroupEnum group: getProductGroupsByFeature(featureType)) {
			if (group.getKey().indexOf(".") == -1) {
				hasGroups = true;
				createGroupAndChildGroups(businessArea, group);
			}
		}
		return hasGroups;
	}

	protected List<MobileProductGroupEnum> getProductGroupsByFeature(FeatureType featureType) {
		List<MobileProductGroupEnum> list = new ArrayList<>();

		if (featureType.equals(BASE)) {
			list.add(PRODUCT_GROUP_EXTRA);
			list.add(PRODUCT_GROUP_PRODUCTION_OUTPUT);
			list.add(PRODUCT_GROUP_PRODUCTION_OUTPUT_CDM);
			list.add(PRODUCT_GROUP_INSTALLATIONTYPE);
		} else if (featureType.equals(FeatureType.WIFI)) {
			list.add(PRODUCT_GROUP_WIFI_BUNDLE);
		} else if (featureType.equals(FeatureType.FIBER)) {
			list.add(PRODUCT_GROUP_FIBER_BUNDLE);
//			list.add(PRODUCT_GROUP_FIBER_BUNDLE_IP);
			list.add(PRODUCT_GROUP_FIBER_BUNDLE_FIBER_SPEED_STANDARD);
			list.add(PRODUCT_GROUP_FIBER_BUNDLE_FIBER_SPEED_A);
			list.add(PRODUCT_GROUP_FIBER_BUNDLE_FIBER_SPEED_B);
			list.add(PRODUCT_GROUP_FIBER_BUNDLE_VLAN_SPEED);
			list.add(PRODUCT_GROUP_FIBER_BUNDLE_HARDWARE);
			list.add(PRODUCT_GROUP_FIBER_BUNDLE_SUPERVISION);
			list.add(PRODUCT_GROUP_FIBER_BUNDLE_ITEMS);
			list.add(PRODUCT_GROUP_FIBER_BUNDLE_ZONEOPRETTELSE);
			list.add(PRODUCT_GROUP_FIBER_BUNDLE_ZONEDRIFT);
		} else if (featureType.equals(FeatureType.FIBER_ERHVERV)) {
			list.add(PRODUCT_GROUP_FIBER_NEW_BUNDLE);
			list.add(PRODUCT_GROUP_FIBER_NEW_BUNDLE_SPEED);
			list.add(PRODUCT_GROUP_FIBER_NEW_BUNDLE_ROUTER_FIREWALL);
//			list.add(PRODUCT_GROUP_FIBER_NEW_BUNDLE_LAN_IP);
			list.add(PRODUCT_GROUP_FIBER_NEW_BUNDLE_IF_FIBER);
//			list.add(PRODUCT_GROUP_FIBER_NEW_BUNDLE_REDUNDANCY);
			list.add(PRODUCT_GROUP_FIBER_NEW_BUNDLE_SUPERVISION);
//			list.add(PRODUCT_GROUP_FIBER_NEW_BUNDLE_SERVICE);
			list.add(PRODUCT_GROUP_FIBER_NEW_BUNDLE_INSTALLATION);
			list.add(PRODUCT_GROUP_FIBER_NEW_BUNDLE_INFRASTRUCTURE);
			list.add(PRODUCT_GROUP_FIBER_NEW_BUNDLE_DIGGING);
			list.add(PRODUCT_GROUP_FIBER_NEW_BUNDLE_INSPECTION);
			list.add(PRODUCT_GROUP_FIBER_NEW_BUNDLE_COORDINATION);
			list.add(PRODUCT_GROUP_FIBER_NEW_BUNDLE_MISC);
//			list.add(PRODUCT_GROUP_FIBER_NEW_BUNDLE_INCLUDED);
		} else if (featureType.equals(FeatureType.XDSL)) {
			list.add(PRODUCT_GROUP_XDSL_BUNDLE);
			list.add(PRODUCT_GROUP_XDSL_BUNDLE_SPEED);
//			list.add(PRODUCT_GROUP_XDSL_BUNDLE_MANAGED_DEVICES);
//			list.add(PRODUCT_GROUP_XDSL_BUNDLE_CUSTOMER_DEVICES);
			list.add(PRODUCT_GROUP_XDSL_BUNDLE_INCLUDED);
			list.add(PRODUCT_GROUP_XDSL_BUNDLE_MISC);
			list.add(PRODUCT_GROUP_XDSL_BUNDLE_SUPERVISION);
		} else if (featureType.equals(FeatureType.SWITCHBOARD)) {
			list.add(PRODUCT_GROUP_SWITCHBOARD);
			list.add(PRODUCT_GROUP_SWITCHBOARD_ADDON);
			// Added by One+
			list.add(PRODUCT_GROUP_SWITCHBOARD_INCLUDED);
			list.add(PRODUCT_GROUP_SWITCHBOARD_INSTALLATION);
			list.add(PRODUCT_GROUP_SWITCHBOARD_INSTALLATION_REMOTE);
			list.add(PRODUCT_GROUP_SWITCHBOARD_INSTALLATION_ADDON);
			list.add(PRODUCT_GROUP_SWITCHBOARD_SERVICE);
		} else if (featureType.equals(FeatureType.TEM5_PRODUCTS)) {
			list.add(PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE);
			list.add(PRODUCT_GROUP_TDC_WORKS_MOBILE_ADDON);
		} else if (featureType.equals(FeatureType.TDC_OFFICE)) {
			list.add(PRODUCT_GROUP_TDC_OFFICE_BUNDLE);
			list.add(PRODUCT_GROUP_TDC_OFFICE_ADDON);
			list.add(PRODUCT_GROUP_TDC_OFFICE_BASIC);
		} else if (featureType.equals(FeatureType.MOBILE_BUNDLES_STANDARD)) {
			// Only the One+ initializer handles this correctly, I think
			list.add(PRODUCT_GROUP_STANDARD_BUNDLE);
			list.add(PRODUCT_GROUP_STANDARD_BUNDLE_DATA);
			list.add(PRODUCT_GROUP_STANDARD_BUNDLE_SPEECH);
			list.add(PRODUCT_GROUP_STANDARD_BUNDLE_INCLUDED);
			list.add(PRODUCT_GROUP_STANDARD_BUNDLE_INSTALLATION_REMOTE);
		} else if (featureType.equals(FeatureType.MOBILE_BUNDLES_MIX)) {
			// Only the One+ initializer handles this correctly, I think
			list.add(PRODUCT_GROUP_MIX_BUNDLE);
			list.add(PRODUCT_GROUP_MIX_BUNDLE_SPEECH);
			list.add(PRODUCT_GROUP_MIX_BUNDLE_SPEECH_TIME);
			list.add(PRODUCT_GROUP_MIX_BUNDLE_DATA);
			list.add(PRODUCT_GROUP_MIX_BUNDLE_DATA_AMOUNT);
			list.add(PRODUCT_GROUP_MIX_BUNDLE_ADDON);
			list.add(PRODUCT_GROUP_MIX_BUNDLE_INCLUDED);
		} else if (featureType.equals(FeatureType.PARTNER_SETTINGS)) {
			list.add(PRODUCT_GROUP_PARTNER);
			list.add(PRODUCT_GROUP_PARTNER_BUNDLE);
			list.add(PRODUCT_GROUP_PARTNER_BUNDLE_ELEMENTS);
			list.add(PRODUCT_GROUP_PARTNER_HARDWARE);
			list.add(PRODUCT_GROUP_PARTNER_HARDWARE_FEATURE_PHONES);
			list.add(PRODUCT_GROUP_PARTNER_HARDWARE_HEADSETS);
			list.add(PRODUCT_GROUP_PARTNER_HARDWARE_MOBILT_BREDBAAND);
			list.add(PRODUCT_GROUP_PARTNER_HARDWARE_IP_FASTNET);
			list.add(PRODUCT_GROUP_PARTNER_INSTALLATION);
		} else if (featureType.equals(FeatureType.BUSINESS_FEATURES)) {
			list.add(PRODUCT_GROUP_SOLUTION);
			list.add(PRODUCT_GROUP_SOLUTION_ADDON_IDENTITY);
			list.add(PRODUCT_GROUP_SOLUTION_ADDON_FEATURES);
			list.add(PRODUCT_GROUP_SOLUTION_POOL_DATA);
			list.add(PRODUCT_GROUP_SOLUTION_POOL_ILD);
		} else if (featureType.equals(FeatureType.USER_PROFILES)) {
			list.add(PRODUCT_GROUP_USER_ADDON);
			list.add(PRODUCT_GROUP_USER_ADDON_FUNCTIONS);
//			list.add(PRODUCT_GROUP_USER_ADDON_ROAMING);
			list.add(PRODUCT_GROUP_USER_ADDON_ROAMING_ILD);
//		} else if (featureType.equals(FeatureType.HARDWARE_BUNDLES)) {
//			list.add(PRODUCT_GROUP_PARTNER_BUNDLE);
//			list.add(PRODUCT_GROUP_PARTNER_BUNDLE_ELEMENTS);
		} else if (featureType.equals(FeatureType.LOCATIONS)) {
			list.add(PRODUCT_GROUP_LOCATIONS);
			list.add(PRODUCT_GROUP_LOCATIONS_HARDWARE_SWITCHES);
			list.add(PRODUCT_GROUP_LOCATIONS_HARDWARE_IP);
			list.add(PRODUCT_GROUP_LOCATIONS_HARDWARE_MISC);
			list.add(PRODUCT_GROUP_LOCATIONS_INSTALLATION);
			list.add(PRODUCT_GROUP_ACCESS);
			list.add(PRODUCT_GROUP_ACCESS_INCLUDED);
			list.add(PRODUCT_GROUP_ACCESS_QOS);
			list.add(PRODUCT_GROUP_ACCESS_SERVICE);
			list.add(PRODUCT_GROUP_ACCESS_IP);
			list.add(PRODUCT_GROUP_ACCESS_REDUNDANCY);
			list.add(PRODUCT_GROUP_ACCESS_SUPERVISION);
		}
		return list;
	}

	protected void addBundlesToDefaultCampaign(Campaign campaign, FeatureType featureType) {
		((MobileCampaign) campaign).setPrisaftaleTextMatrix(
				"13% rabat mobil prisaftale nr. 14536,14% rabat mobil prisaftale nr. 14574,16% rabat mobil prisaftale nr. 14575,17% rabat mobil prisaftale nr. 14576,19% rabat mobil prisaftale nr. 14577,20% rabat mobil prisaftale nr. 14578#25% rabat mobil prisaftale nr. 14579,28% rabat mobil prisaftale nr. 14580,31% rabat mobil prisaftale nr. 14581,34% rabat mobil prisaftale nr. 14582,37% rabat mobil prisaftale nr. 14583,40% rabat mobil prisaftale nr. 14584#27% rabat mobil prisaftale nr. 14585,30% rabat mobil prisaftale nr. 14586,33% rabat mobil prisaftale nr. 14587,36% rabat mobil prisaftale nr. 14588,39% rabat mobil prisaftale nr. 14589,42% rabat mobil prisaftale nr. 14590#");
		((MobileCampaign) campaign).setPrisaftaleTextMatrixNetwork(
				"13% rabat netværk prisaftale nr. 14192,14% rabat netværk prisaftale nr. 14193,16% rabat netværk prisaftale nr. 14198,17% rabat netværk prisaftale nr. 14199,19% rabat netværk prisaftale nr. 14200,20% rabat netværk prisaftale nr. 14201#25% rabat netværk prisaftale nr. 14202,28% rabat netværk prisaftale nr. 14203,31% rabat netværk prisaftale nr. 14204,34% rabat netværk prisaftale nr. 14205,37% rabat netværk prisaftale nr. 14206,40% rabat netværk prisaftale nr. 14207#27% rabat netværk prisaftale nr. 14208,30% rabat netværk prisaftale nr. 14209,33% rabat netværk prisaftale nr. 14210,36% rabat netværk prisaftale nr. 14211,39% rabat netværk prisaftale nr. 14212,42% rabat netværk prisaftale nr. 14213#");
		if (featureType.equals(FeatureType.XDSL)) {
			ProductGroup productGroup = businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_SPEED.getKey());
			for (Product product: productGroup.getProducts()) {
				MobileProductBundle bundle = new MobileProductBundle();
				bundle.setBundleType(MobileProductBundleEnum.XDSL_BUNDLE);
				bundle.setPublicName(product.getPublicName());
				// bundle.setProductId(product.getProductId());   NO, do not!
				bundle.setInternalName(product.getInternalName());
				bundle.setSortIndex(product.getSortIndex());

				productBundleDao.save(bundle);

				campaign.addProductBundle(bundle);
				addProductToBundle(bundle, product, true);

				for (Product includedProduct : businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_INCLUDED.getKey()).getProducts()) {
					addProductToBundle(bundle, includedProduct, true);
				}
			}
		}

		if (featureType.equals(FeatureType.WIFI)) {
			ProductGroup productGroup = businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_AREA_SIZE.getKey());
			for(Product product: productGroup.getProducts()) {
				MobileProductBundle bundle = new MobileProductBundle();
				bundle.setBundleType(MobileProductBundleEnum.WIFI_BUNDLE);
				bundle.setPublicName(product.getPublicName());
				bundle.setInternalName(product.getInternalName());
				bundle.setSortIndex(product.getSortIndex());

				productBundleDao.save(bundle);

				campaign.addProductBundle(bundle);
			}
		}

		/*
		The following bundle creation sections are based on product ids in the "flags" property
		 */

		if (featureType.equals(FeatureType.MOBILE_BUNDLES_STANDARD)) {
			// Mobilpakker
			ProductGroup productGroup = businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE.getKey());
			for (Product mainProduct : productGroup.getProducts()) {
				MobileProductBundle bundle = new MobileProductBundle();
				bundle.setBundleType(MobileProductBundleEnum.MOBILE_BUNDLE);
				bundle.setPublicName(mainProduct.getPublicName());

				// bundle.setProductId(product.getProductId());   NO, do not!
				// Why not?????? I need it for CDM!!!

				bundle.setProductId(mainProduct.getProductId());

				bundle.setInternalName(mainProduct.getInternalName());
				bundle.setSortIndex(mainProduct.getSortIndex());
				if (businessArea.isOnePlus() || businessArea.isWorks()) {
					bundle.setAddToContractDiscount(ProductBundle.RABATAFTALE_DISCOUNT_CONTRIBUTION);
				}

				productBundleDao.save(bundle);

				campaign.addProductBundle(bundle);
				addProductToBundle(bundle, mainProduct, true);

				for (MobileProductGroupEnum mpge: new MobileProductGroupEnum[] {
						PRODUCT_GROUP_STANDARD_BUNDLE_INCLUDED, PRODUCT_GROUP_STANDARD_BUNDLE_DATA, PRODUCT_GROUP_STANDARD_BUNDLE_SPEECH,
						PRODUCT_GROUP_STANDARD_BUNDLE_INSTALLATION_REMOTE}) {
					for (MobileProduct product : mpge.getProducts(businessArea)) {
						if (!StringUtils.isEmpty(product.getFlags()) && product.getFlags().indexOf(mainProduct.getProductId()) != -1) {
							addProductToBundle(bundle, product, true);
						}
					}
				}
			}
		}

		if (businessArea.isOnePlus() && featureType.equals(FeatureType.PARTNER_SETTINGS)) {
			// Partner hardware bundles
			ProductGroup productGroup = businessArea.getProductGroupByUniqueName(PRODUCT_GROUP_PARTNER_BUNDLE.getKey());
			for (Product mainProduct : productGroup.getProducts()) {
				MobileProductBundle bundle = new MobileProductBundle();
				bundle.setBundleType(MobileProductBundleEnum.HARDWARE_BUNDLE);
				bundle.setPublicName(mainProduct.getPublicName());

				// bundle.setProductId(product.getProductId());   NO, do not!
				// Why not?????? I need it for CDM!!!

				bundle.setProductId(mainProduct.getProductId());

				bundle.setInternalName(mainProduct.getInternalName());
				bundle.setSortIndex(mainProduct.getSortIndex());
				bundle.setAddToContractDiscount(ProductBundle.RABATAFTALE_DISCOUNT_CONTRIBUTION);

				productBundleDao.save(bundle);

				campaign.addProductBundle(bundle);
				addProductToBundle(bundle, mainProduct, true);

				for (MobileProductGroupEnum mpge: new MobileProductGroupEnum[] {
						PRODUCT_GROUP_PARTNER_HARDWARE,
						PRODUCT_GROUP_PARTNER_HARDWARE_HEADSETS,
						PRODUCT_GROUP_PARTNER_HARDWARE_IP_FASTNET,
						PRODUCT_GROUP_PARTNER_HARDWARE_FEATURE_PHONES,
						PRODUCT_GROUP_PARTNER_HARDWARE_MOBILT_BREDBAAND,
						PRODUCT_GROUP_PARTNER_BUNDLE_ELEMENTS}) {
					for (MobileProduct product : mpge.getProducts(businessArea)) {
						if (!StringUtils.isEmpty(product.getFlags()) && product.getFlags().indexOf(mainProduct.getProductId()) != -1) {
							addProductToBundle(bundle, product, true);
						}
					}
				}
			}
		}

		if (featureType.equals(FeatureType.USER_PROFILES)) {
			campaign.getProductBundles().stream().map(productBundle -> (MobileProductBundle) productBundle)
					.filter(mobileProductBundle -> mobileProductBundle.getBundleType().equals(MobileProductBundleEnum.MOBILE_BUNDLE))
					.forEach(mobileProductBundle -> {
						Optional<MobileProduct> mainProduct = mobileProductBundle.getAnyProduct(mobileProduct -> mobileProduct.isInGroup(PRODUCT_GROUP_STANDARD_BUNDLE));
						if (mainProduct.isPresent()) {
							for (MobileProductGroupEnum mpge: new MobileProductGroupEnum[] {
									PRODUCT_GROUP_USER_ADDON_FUNCTIONS,
//									PRODUCT_GROUP_USER_ADDON_ROAMING,
									PRODUCT_GROUP_USER_ADDON_ROAMING_ILD}) {
								for (MobileProduct product : mpge.getProducts(businessArea)) {
									if (!StringUtils.isEmpty(product.getFlags()) && product.getFlags().indexOf(
											mainProduct.get().getProductId()) != -1) {
										addProductToBundle(mobileProductBundle, product, false, ProductAccessType.SEPARATE_COUNT);
									}
								}
							}
						} else {
							log.warn("This looks like a configuration problem");
						}
					});
		}

		if (featureType.equals(FeatureType.SWITCHBOARD)) {
			ProductGroup productGroup = businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD.getKey());
			for (Product mainProduct: productGroup.getProducts()) {
				MobileProductBundle bundle = new MobileProductBundle();
				bundle.setBundleType(MobileProductBundleEnum.SWITCHBOARD_BUNDLE);
				bundle.setPublicName(mainProduct.getPublicName());
				// bundle.setProductId(product.getProductId());   NO, do not!
				bundle.setInternalName(mainProduct.getInternalName());
				bundle.setSortIndex(mainProduct.getSortIndex());
				if (businessArea.isOnePlus() || businessArea.isWorks()) {
					bundle.setAddToContractDiscount(ProductBundle.RABATAFTALE_DISCOUNT_CONTRIBUTION);
				}

				productBundleDao.save(bundle);

				campaign.addProductBundle(bundle);
				addProductToBundle(bundle, mainProduct, true);

				for (MobileProductGroupEnum group : new MobileProductGroupEnum[] {
						PRODUCT_GROUP_SWITCHBOARD_INCLUDED, PRODUCT_GROUP_SWITCHBOARD_INSTALLATION_REMOTE}) {
					for (MobileProduct product : group.getProducts(businessArea)) {
						if (!StringUtils.isEmpty(product.getFlags()) && product.getFlags().indexOf(mainProduct.getProductId()) != -1) {
							addProductToBundle(bundle, product, true);
						}
					}
				}
//				for (MobileProduct product : PRODUCT_GROUP_SWITCHBOARD_INCLUDED.getProducts(businessArea)) {
//					if (!StringUtils.isEmpty(product.getFlags()) && product.getFlags().indexOf(mainProduct.getProductId()) != -1) {
//						addProductToBundle(bundle, PRODUCT_GROUP_SWITCHBOARD_INCLUDED.getKey(), product, true);
//					}
//				}
			}
		}
	}

	@Override
	public void initTestContracts(BusinessArea businessArea) {
		for (BaseUser user : userDao.findAll()) {
			SalespersonRole salesperson = (SalespersonRole) user.getRole(SalespersonRole.class);
			if ((salesperson != null) && (salesperson.getUser().getUsername().startsWith("partner"))) {
				MobileContract contract = (MobileContract) objectFactory.createAndSaveContract(businessArea, salesperson);
				salesperson.addContract(contract);

				contract.setTitle("Test kontrakt");
				roleDao.save(salesperson);
			}
		}
	}

	protected void addPages(BusinessArea businessArea, FeatureType featureType) {
		if (featureType.equals(BASE)) {
			createPage(businessArea, CorePageIds.SALES_EXISTING_CONTRACTS, "Vælg kundesag", null,
					"Velkommen til " + businessArea.getName() + " salgskonfigurator.\n\nKonfiguratoren skal hj\u00e6lpe dig til at lave det materiale, du skal bruge overfor dine kunder. Du kan danne et prisoverslag eller et egentligt tilbud, som kan bruges til at afstemme \u00f8konomi og l\u00f8sning med kunderne.\n\nDu kan altid f\u00e5 inspiration til din kundedialog ved at se n\u00e6rmere p\u00e5 TDC Perspektiv artiklerne som kan findes til h\u00f8jre.",
					"Du har to muligheder for at arbejde med kundesager:\n\n  * Du kan \u00e5bne en eksisterende kundesag ved at klikke \"V\u00e6lg\" til venstre\n\n  * Du kan oprette en ny salgssag ved at klikke p\u00e5 \"Ny kundesag\"\n\nDu kan med fordel arbejde med kategorier for dine kundesager for at skabe st\u00f8rre overblik. Anvend ikonet med den lille mand \u00f8verst i konfiguratoren til h\u00f8jre og klik p\u00e5 \"Kundesagskategorier\" i menuen.");
		}

		List<String> pageIds = new ArrayList<>();
		switch (featureType) {
			case BASE:
				pageIds.add(CorePageIds.SALES_MASTER_DATA);
				pageIds.add(MobilePageIds.MOBILE_PRODUCT_SELECTION);
				pageIds.add(MobilePageIds.MOBILE_CONTRACT_SETTINGS);
				pageIds.add(MobilePageIds.MOBILE_CONTRACT_SUMMARY);
				break;
			case WIFI:
				pageIds.add(MobilePageIds.MOBILE_WIFI_BUNDLES);
				pageIds.add(MobilePageIds.MOBILE_WIFI_ADDITIONAL_INFO);
				break;
			case LOCATIONS:
				pageIds.add(MobilePageIds.MOBILE_LOCATIONS);
				break;
			case XDSL:
				if (!businessArea.isOnePlus()) {
					pageIds.add(MobilePageIds.MOBILE_XDSL_BUNDLES);
				}
				break;
			case FIBER:
			case FIBER_ERHVERV:
				if (!businessArea.isOnePlus()) {
					pageIds.add(MobilePageIds.MOBILE_FIBER_BUNDLES);
				}
				break;
			case SWITCHBOARD:
				pageIds.add(MobilePageIds.MOBILE_SWITCHBOARD);
				pageIds.add(MobilePageIds.MOBILE_SWITCHBOARD_TYPE);
				pageIds.add(MobilePageIds.MOBILE_SWITCHBOARD_ADDONS);
				break;
			case BUSINESS_FEATURES:
				pageIds.add(MobilePageIds.MOBILE_SOLUTION_ADDONS);
				pageIds.add(MobilePageIds.MOBILE_POOL_ADDONS);
				break;
			case PARTNER_SETTINGS:
				pageIds.add(MobilePageIds.MOBILE_PARTNER_SETTINGS);
				break;
			case MOBILE_BUNDLES_STANDARD:
				pageIds.add(MobilePageIds.MOBILE_STANDARD_BUNDLES);
				break;
			case MOBILE_BUNDLES_MIX:
				pageIds.add(MobilePageIds.MOBILE_MIX_BUNDLES);
				break;
			case SUBSCRIPTION_CONFIGURATION:
				pageIds.add(MobilePageIds.MOBILE_SUBSCRIPTION_CONFIGURATION);
				break;
			case TDC_OFFICE:
				pageIds.add(MobilePageIds.MOBILE_OFFICE_IMPLEMENTATION);
				pageIds.add(MobilePageIds.MOBILE_OFFICE_ADDITIONAL_PRODUCTS);
				break;
		}
		modifyListOfPageIds(pageIds);
		for (String pageId: pageIds) {
			Optional<PageInfo> optionalPage = PageInfoDao.lookup().findByField("pageId", pageId)
					.stream()
					.sorted(Comparator.comparingLong(value -> value.getBusinessArea().getId()))
					.findFirst();
			if (optionalPage.isPresent()) {
				createPage(businessArea, pageId, optionalPage.get().getTitle(), optionalPage.get().getSubTitle(), optionalPage.get().getIntroMarkdown(), optionalPage.get().getHelpMarkdown());
			} else {
				createPage(businessArea, pageId, "Page title", "Page subtitle", "Intro TODO...", "Help TODO...");
			}
		}
	}

	protected abstract void modifyListOfPageIds(List<String> pageIds);

	@Override
	protected String getMiscMarkdown() {
		return "**Artikler fra TDC Perspektiv**\n\n" +
				"  1. <a href=\"//perspektiv.tdc.dk/personlig-telefonservice-er-penge-vaerd/\" target=\"_blank\">Personlig telefonservice er penge værd</a>\n" +
				"  2. <a href=\"//perspektiv.tdc.dk/nar-kunderne-ringer-kommer-20-aldrig-igennem/\" target=\"_blank\">Når kunderne ringer kommer 20 % aldrig igennem</a>\n" +
				"  3. <a href=\"//perspektiv.tdc.dk/styrk-virksomheden-med-4g-daekning/\" target=\"_blank\">Styrk virksomheden med 4G dækning</a>\n";
	}

	protected abstract void makeSystemUpdates(BusinessArea businessArea);
	
	protected Product addProductIdToBundle(MobileProductBundle bundle, ProductAccessType productAccessType, MobileProductGroupEnum parentGroupType, String productId, boolean addProductPrice) {
		ProductGroup group = bundle.getCampaign().getBusinessArea().getProductGroupByUniqueName(parentGroupType.getKey());
		Product product = group.getProductByProductId(productId);
		if (product == null) {
			log.error("Failed to find product by productId: " + productId);
		} else {
			BundleProductRelation relation = new BundleProductRelation();
			relation.setProduct(product);
			relation.setAddProductPrice(addProductPrice);
			relation.setSortIndex(1l);
			relation.setProductAccessType(productAccessType);
			bundle.addProductRelation(relation);
		}
		return product;
	}

	protected void handleInitializationException(Exception e) {
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

	protected void handleConstraintViolationException(ConstraintViolationException e) {
		for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
			log.error(constraintViolation.getMessage());
			log.error("I'm guessing this is the problem: \n"
					+ "An object of type '" + constraintViolation.getLeafBean().getClass().getSimpleName()
					+ "' has a property '" + constraintViolation.getPropertyPath() + "' which has value '"
					+ constraintViolation.getInvalidValue() + "'. The problem is: '" + constraintViolation.getMessage() + "'");
		}
	}

	protected void handlePersistenceException(PersistenceException e) {
		if (e.getCause() instanceof ConstraintViolationException) {
			handleConstraintViolationException((ConstraintViolationException) e.getCause());
		} else {
			log.error("We may need to improve logging here!", e);
		}
	}
}
