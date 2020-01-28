package dk.jyskit.salescloud.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.BusinessAreaDao;
import dk.jyskit.salescloud.application.dao.CampaignDao;
import dk.jyskit.salescloud.application.dao.ProductBundleDao;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.dao.SystemUpdateDao;
import dk.jyskit.salescloud.application.extensions.MobileObjectFactory;
import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.salescloud.application.pages.CorePageIds;
import dk.jyskit.salescloud.application.pages.MobilePageIds;
import dk.jyskit.waf.application.dao.RoleDao;
import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.model.BaseUser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CopyInitializer extends BusinessAreaInitializer {
	@Inject protected BusinessAreaDao businessAreaDao;
	@Inject protected CampaignDao campaignDao;
	@Inject protected ProductBundleDao productBundleDao;
	@Inject protected ProductDao productDao;
	@Inject protected UserDao userDao;
	@Inject protected RoleDao roleDao;
	@Inject protected SystemUpdateDao systemUpdateDao;
	@Inject protected MobileObjectFactory objectFactory;
	
	private int sourceBusinessAreaId;

	public CopyInitializer(String name, int businessAreaId, String businessAreaName, String introText, int sourceBusinessAreaId) {
		super(name, businessAreaId, businessAreaName, introText);
		this.sourceBusinessAreaId = sourceBusinessAreaId;
	}
	
	@Override
	public void initialize() {
		super.initialize();
	}

	@Override
	public void makeUpgrades() {
		makeSystemUpdates(businessArea);
		businessAreaDao.save(businessArea);
	}

	protected void initGroups() {
		for (ProductGroup sourceProductGroup : getSourceBusinessArea().getProductGroups()) {
			addGroupAndChildrenFromSourceBusinessArea(sourceProductGroup, null, getGroupsToIgnore(), null);
		}
		addGroups();
	}
	
	protected BusinessArea getSourceBusinessArea() {
		return businessAreaDao.findUniqueByField("businessAreaId", sourceBusinessAreaId);
	}

	protected Map<MobileProductGroupEnum, MobileProductGroupEnum> getGroupMapper() {
		Map<MobileProductGroupEnum, MobileProductGroupEnum> map = new HashMap<>();
		return map;
	}

	protected ProductGroup addGroupAndChildrenFromSourceBusinessArea(ProductGroup sourceProductGroup, ProductGroup parentProductGroup,
							List<MobileProductGroupEnum> groupsToIgnore, Map<MobileProductGroupEnum, MobileProductGroupEnum> groupMapper) {
		return addGroupAndChildrenFromSourceBusinessArea(sourceProductGroup, parentProductGroup, groupsToIgnore, groupMapper, false);
	}

	protected ProductGroup addGroupAndChildrenFromSourceBusinessArea(ProductGroup sourceProductGroup, ProductGroup parentProductGroup,
							List<MobileProductGroupEnum> groupsToIgnore, Map<MobileProductGroupEnum, MobileProductGroupEnum> groupMapper, boolean copyProducts) {
		ProductGroup targetProductGroup = null;
		if (!groupsToIgnore.contains(MobileProductGroupEnum.getValueByKey(sourceProductGroup.getUniqueName()))) {
			MobileProductGroupEnum targetGroupEnum = null;
			if (groupMapper != null) {
				targetGroupEnum = groupMapper.get(MobileProductGroupEnum.getValueByKey(sourceProductGroup.getUniqueName()));
			}
			if (targetGroupEnum == null) {
				targetGroupEnum = MobileProductGroupEnum.getValueByKey(sourceProductGroup.getUniqueName());
			}
			if (parentProductGroup == null) {
				targetProductGroup = createProductGroup(businessArea, targetGroupEnum);
			} else {
				targetProductGroup = createProductGroup(parentProductGroup, targetGroupEnum);
			}
			targetProductGroup.setName(sourceProductGroup.getName());
			targetProductGroup.setUniqueName(sourceProductGroup.getUniqueName());
			targetProductGroup.setSortIndex(sourceProductGroup.getSortIndex());
			targetProductGroup.setHelpMarkdown(sourceProductGroup.getHelpMarkdown());
			targetProductGroup.setHelpHtml(sourceProductGroup.getHelpHtml());
			for (DiscountScheme discountScheme: sourceProductGroup.getDiscountSchemes()) {
				targetProductGroup.getDiscountSchemes().add(discountScheme);
			}
			if (copyProducts) {
				for (Product product: sourceProductGroup.getProducts()) {
					if (acceptToCopyProduct(product)) {
						targetProductGroup.addProduct(((MobileProduct) product).clone());
					}
				}
			}
		}
		
		for (ProductGroup childGroup: sourceProductGroup.getChildProductGroups()) {
			addGroupAndChildrenFromSourceBusinessArea(childGroup, targetProductGroup, groupsToIgnore, null, copyProducts);
		}
		
		return targetProductGroup;
	}

	protected boolean acceptToCopyProduct(Product product) {
		return true;
	}


	protected List<MobileProductGroupEnum> getGroupsToIgnore() {
		List<MobileProductGroupEnum> groupsToIgnore = new ArrayList<>();
		if (!businessArea.hasFeature(FeatureType.MOBILE_BUNDLES_STANDARD)) {
			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE);
			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_SPEECH);
			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_DATA);
			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_NON_DOMESTIC);
			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_ADDON);
			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_ADDON);
			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_ADDON_ROAMING);
			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_ADDON_FUNCTIONS);
		}
		if (!businessArea.hasFeature(FeatureType.MOBILE_BUNDLES_MIX)) {
			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE);
			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_SPEECH);
			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_SPEECH_TIME);
			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_DATA);
			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_DATA_AMOUNT);
			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_ADDON);
			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_INCLUDED);
		}
		if (!businessArea.hasFeature(FeatureType.XDSL)) {
			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE);
			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_SPEED);
//			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_MANAGED_DEVICES);
//			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_CUSTOMER_DEVICES);
			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_INCLUDED);
			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_MISC);
			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_SUPERVISION);
		}
		if (!businessArea.hasFeature(FeatureType.WIFI)) {
			ignoreBundleGroups(groupsToIgnore, MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE);
//			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE);
//			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_SWITCH);
//			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_AREA_SIZE);
//			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_CABLING);
//			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_SITE_SURVEY);
//			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_ADDON);
//			groupsToIgnore.add(MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_GENERAL_ADDON);
		}
		if (!businessArea.hasFeature(FeatureType.FIBER)) {
			ignoreBundleGroups(groupsToIgnore, MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE);
		}
		if (!businessArea.hasFeature(FeatureType.FIBER_ERHVERV)) {
			ignoreBundleGroups(groupsToIgnore, MobileProductGroupEnum.PRODUCT_GROUP_FIBER_NEW_BUNDLE);
		}
		if (!businessArea.hasFeature(FeatureType.SWITCHBOARD)) {
			ignoreBundleGroups(groupsToIgnore, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD);
			ignoreBundleGroups(groupsToIgnore, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_ADDON);
		}
		if (!businessArea.hasFeature(FeatureType.TEM5_PRODUCTS)) {
			ignoreBundleGroups(groupsToIgnore, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE);
			ignoreBundleGroups(groupsToIgnore, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE_SPEECH);
			ignoreBundleGroups(groupsToIgnore, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE_DATA);
			ignoreBundleGroups(groupsToIgnore, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE_INCLUDED);
			ignoreBundleGroups(groupsToIgnore, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_ADDON);
			ignoreBundleGroups(groupsToIgnore, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_ADDON_ROAMING);
			ignoreBundleGroups(groupsToIgnore, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_ADDON_FUNCTIONS);
		}
		if (!businessArea.hasFeature(FeatureType.TDC_OFFICE)) {
			ignoreBundleGroups(groupsToIgnore, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE);
			ignoreBundleGroups(groupsToIgnore, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED);
			ignoreBundleGroups(groupsToIgnore, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_BOTTOM);
			ignoreBundleGroups(groupsToIgnore, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_TOP);
			ignoreBundleGroups(groupsToIgnore, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_ADDON);
			ignoreBundleGroups(groupsToIgnore, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BASIC);
		}
		// TODO: add more as needed
		
		return groupsToIgnore;
	}

	private void ignoreBundleGroups(List<MobileProductGroupEnum> groupsToIgnore, MobileProductGroupEnum bundleGroupType) {
		for (MobileProductGroupEnum childGroup: MobileProductGroupEnum.getByPrefix(bundleGroupType.name())) {
			groupsToIgnore.add(childGroup);
		}
	}

	protected void createGroupAndChildGroups(MobileProductGroupEnum groupType) {
		ProductGroup bundleGroup = createProductGroup(businessArea, groupType);
		for (MobileProductGroupEnum childGroup: MobileProductGroupEnum.getByPrefix(groupType.name() + "_")) {
			createProductGroup(bundleGroup, childGroup);
		}
	}

	protected void addGroups() {
		if (businessArea.hasFeature(FeatureType.WIFI)) {
			createGroupAndChildGroups(MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE);
		}
		if (businessArea.hasFeature(FeatureType.FIBER)) {
			createGroupAndChildGroups(MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE);
		}
		if (businessArea.hasFeature(FeatureType.FIBER_ERHVERV)) {
			createGroupAndChildGroups(MobileProductGroupEnum.PRODUCT_GROUP_FIBER_NEW_BUNDLE);
		}
		if (businessArea.hasFeature(FeatureType.XDSL)) {
			createGroupAndChildGroups(MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE);
		}
		if (businessArea.hasFeature(FeatureType.SWITCHBOARD)) {
			createGroupAndChildGroups(MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD);
			createGroupAndChildGroups(MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_ADDON);
			// Added by One+
			createGroupAndChildGroups(MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_INCLUDED);
			createGroupAndChildGroups(MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_INSTALLATION);
			createGroupAndChildGroups(MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_SERVICE);
		}
		if (businessArea.hasFeature(FeatureType.TEM5_PRODUCTS)) {
			createGroupAndChildGroups(MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE);
			createGroupAndChildGroups(MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_ADDON);
		}
		if (businessArea.hasFeature(FeatureType.TDC_OFFICE)) {
			createGroupAndChildGroups(MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE);
			createGroupAndChildGroups(MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_ADDON);
			createGroupAndChildGroups(MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BASIC);
		}
	}

	@Override
	public void initProducts(BusinessArea businessArea) {
		// ----------------------------
		// Products
		// ----------------------------
		{
			// Groups
			initGroups();

			// Products
			initProducts();
			importProducts();
		}
		
		// ----------------------------
		// Campaigns
		// ----------------------------
		{
			if (cloneDefaultCampaign()) {
				for (Campaign campaign: getSourceBusinessArea().getCampaigns()) {
					if ("Ingen kampagne".equalsIgnoreCase(campaign.getName())) {
						businessArea.addCampaign(((MobileCampaign) campaign).clone(businessArea));
					}
				}
			} else {
				Campaign campaign;

				campaign = objectFactory.createCampaign();
				campaign.setName("Ingen kampagne");
				campaign.setFromDate(null);
				campaign.setToDate(null);
				campaign = campaignDao.save(campaign);		// Bundles needs an ID

				businessArea.addCampaign(campaign);
				addBundlesToDefaultCampaign(campaign);
				addProductRelationsToDefaultCampaign(campaign);
			}
		}

		businessAreaDao.save(businessArea); 
	}

	protected boolean cloneDefaultCampaign() {
		return false;
	}

	protected void addProductRelationsToDefaultCampaign(Campaign campaign) {
	}

	protected void addBundlesToDefaultCampaign(Campaign campaign) {
		if (businessArea.hasFeature(FeatureType.XDSL)) {
			ProductGroup productGroup = businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_SPEED.getKey());
			for(Product product: productGroup.getProducts()) {
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
		
		if (businessArea.hasFeature(FeatureType.WIFI)) {
			ProductGroup productGroup = businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_AREA_SIZE.getKey());
			for(Product product: productGroup.getProducts()) {
				MobileProductBundle bundle = new MobileProductBundle();
				bundle.setBundleType(MobileProductBundleEnum.WIFI_BUNDLE);
				bundle.setPublicName(product.getPublicName());
				bundle.setInternalName(product.getInternalName());
				bundle.setSortIndex(product.getSortIndex());
				
				productBundleDao.save(bundle);
				
				campaign.addProductBundle(bundle);
//				addProductToBundle(bundle, MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_AREA_SIZE.getKey(), product, true);	
//				
//				for (Product includedProduct : businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_INCLUDED.getKey()).getProducts()) {
//					addProductToBundle(bundle, MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_INCLUDED.getKey(), includedProduct, true);	
//				}
			}
		}
	}

	protected void initProducts() {
	}

	protected void importProducts() {
	}

	@Override
	public void initTestContracts(BusinessArea businessArea) {
		for (BaseUser user : userDao.findAll()) {
			SalespersonRole salesperson = (SalespersonRole) user.getRole(SalespersonRole.class);
			if ((salesperson != null) && (salesperson.getUser().getUsername().startsWith("partner"))) {
				MobileContract contract = (MobileContract) objectFactory.createAndSaveContract(businessArea, salesperson);
				salesperson.addContract(contract);
				
				contract.setTitle("Test kontrakt");
				
//				// Lets say the user orders 5 of each bundle associated with
//				// the campaign.
//				Map<MobileProductBundle, BundleCount> bundleToCountMap = new HashMap<>(); 
//				for (ProductBundle productBundle : contract.getCampaigns().get(0).getProductBundles()) {
//					if (MobileProductBundleEnum.MOBILE_BUNDLE.equals(((MobileProductBundle) productBundle).getBundleType())) {
//						BundleCount bc = new BundleCount((MobileProductBundle) productBundle, 5, 0);
//						bundleToCountMap.put((MobileProductBundle) productBundle, bc);
//					}
//				}

//				// If the user wants to adjust what he has ordered (numbers, not type of bundles)
//				// we first check if this is ok. You can't take away subscriptions that are already
//				// fully or partly configured.
//				if (contract.adjustSubscriptions(bundleToCountMap, true)) {
//					// The change was ok, so adjust orderlines as well
//					contract.adjustOrderLinesForBundles(bundleToCountMap, true, MobileProductBundleEnum.MOBILE_BUNDLE);
//				}
				roleDao.save(salesperson);
			}
		}
	}

	@Override
	public void initPages(BusinessArea businessArea) {
		{
			for (PageInfo sourcepage : getSourceBusinessArea().getPages()) {
				if (!getPagesToIgnore().contains(sourcepage.getPageId())) {
					createPage(businessArea, sourcepage.getPageId(), sourcepage.getTitle(), sourcepage.getSubTitle(), sourcepage.getIntroMarkdown(), sourcepage.getHelpMarkdown()); 
				}
			}
			addPages();
		}
	}

	protected List<String> getPagesToIgnore() {
		ArrayList<String> pageIds = new ArrayList<>();
		if (!businessArea.hasFeature(FeatureType.SWITCHBOARD)) {
			pageIds.add(MobilePageIds.MOBILE_SWITCHBOARD);
			pageIds.add(MobilePageIds.MOBILE_SWITCHBOARD_TYPE);
			pageIds.add(MobilePageIds.MOBILE_SWITCHBOARD_ADDONS);
			pageIds.add(CorePageIds.SALES_EXISTING_CONTRACTS);
		}
		if (!businessArea.hasFeature(FeatureType.XDSL)) {
			pageIds.add(MobilePageIds.MOBILE_XDSL_BUNDLES);
		}
		if (!businessArea.hasFeature(FeatureType.LOCATIONS)) {
			pageIds.add(MobilePageIds.MOBILE_LOCATIONS);
		}
		if (!businessArea.hasFeature(FeatureType.PARTNER_SETTINGS)) {
			pageIds.add(MobilePageIds.MOBILE_PARTNER_SETTINGS);
		}
		if (!businessArea.hasFeature(FeatureType.MOBILE_BUNDLES_STANDARD)) {
			pageIds.add(MobilePageIds.MOBILE_MIX_BUNDLES);
			pageIds.add(MobilePageIds.MOBILE_PRODUCT_SELECTION);
			pageIds.add(MobilePageIds.MOBILE_STANDARD_BUNDLES);
		}
		return pageIds;
	}

	protected void addPages() {
		if (businessArea.hasFeature(FeatureType.WIFI)) {
			createPage(businessArea, MobilePageIds.MOBILE_WIFI_BUNDLES, "Wi-Fi", null, 
					"SKAL SKRIVES OM... Ved angivelse af diverse partner relaterede indstillinger for partner tekniker, support ydelser, udstyr og kundesegment, er det muligt at konfigurere "
					+ "separate partner tilbud, i till\u00e6g til tilbuddet p\u00e5 l\u00f8sningen, samt ikke mindst beregne vejledende provision for l\u00f8sningen.",
					"**Indstillinger**  \n" + 
					"\n" + 
					"Grundindstillinger for support, rate og provision.  \n" + 
					"\n" + 
					"Antallet af brugere bestemmes p\u00e5 baggrund af de valgte profiler under fanen ”Pakker”. Pris pr. bruger og \n" + 
					"grundpris for support aftale inds\u00e6ttes. Disse priser fastl\u00e6gges p\u00e5 baggrund af lokal governance i de enkelte TDC Erhvervscentre.  \n" + 
					"\n" + 
					"L\u00f8betiden i m\u00e5neder f\u00f8lger aftalel\u00e6ngden p\u00e5 l\u00f8sningen – 3 \u00e5rs binding p\u00e5 l\u00f8sningen = 36 m\u00e5neder p\u00e5 support osv. \n" + 
					"\n" + 
					"Etableringspris og l\u00f8betid i m\u00e5neder for rate aftale angives. Disse priser fastl\u00e6gges p\u00e5 baggrund af lokal governance i de enkelte TDC Erhvervscentre.  \n" + 
					"\n" + 
					"Ved valg af upfront betaling p\u00e5 rate aftale, lyder f\u00f8rste afdrag p\u00e5 kr. 1.500,-. Den resterende del af aftalesummen afregnes over den resterende l\u00f8betid.  \n" + 
					"\n" + 
					"Under fakturering tages der stilling til om kunden skal tilmeldes PBS eller email fakturering. Denne indstilling g\u00e6lder kun for partnerydelser og styres p\u00e5 baggrund af lokal governance.  \n" +
					"\n" + 
					"Angiv hvilket segment kunder har. Feltet er styrende for beregning af provision.  \n" + 
					"\n" + 
					"**Øvrige installationsydelser**  \n" + 
					"\n" + 
					"Afkryds relevante installationsydelser  \n" + 
					"\n" + 
					"Diverse installationsydelser angives. Evt. rabat p\u00e5 egne installationsydelser angives.\n" + 
					"\n" + 
					"**Hardware til rate**  \n" + 
					"\n" + 
					"Antal og m\u00e5nedspris for hardware angives. \n" + 
					"\n" + 
					"**Sammenfatning partner**  \n" + 
					"\n" + 
					"Hent regneark med vejledende partnerprovision.");
			createPage(businessArea, MobilePageIds.MOBILE_WIFI_ADDITIONAL_INFO, "Aftalepapir", null, 
					"SKAL SKRIVES OM... Ved angivelse af diverse partner relaterede indstillinger for partner tekniker, support ydelser, udstyr og kundesegment, er det muligt at konfigurere "
					+ "separate partner tilbud, i till\u00e6g til tilbuddet p\u00e5 l\u00f8sningen, samt ikke mindst beregne vejledende provision for l\u00f8sningen.",
					"**Indstillinger**  \n" + 
					"\n" + 
					"Grundindstillinger for support, rate og provision.  \n" + 
					"\n" + 
					"Antallet af brugere bestemmes p\u00e5 baggrund af de valgte profiler under fanen ”Pakker”. Pris pr. bruger og \n" + 
					"grundpris for support aftale inds\u00e6ttes. Disse priser fastl\u00e6gges p\u00e5 baggrund af lokal governance i de enkelte TDC Erhvervscentre.  \n" + 
					"\n" + 
					"L\u00f8betiden i m\u00e5neder f\u00f8lger aftalel\u00e6ngden p\u00e5 l\u00f8sningen – 3 \u00e5rs binding p\u00e5 l\u00f8sningen = 36 m\u00e5neder p\u00e5 support osv. \n" + 
					"\n" + 
					"Etableringspris og l\u00f8betid i m\u00e5neder for rate aftale angives. Disse priser fastl\u00e6gges p\u00e5 baggrund af lokal governance i de enkelte TDC Erhvervscentre.  \n" + 
					"\n" + 
					"Ved valg af upfront betaling p\u00e5 rate aftale, lyder f\u00f8rste afdrag p\u00e5 kr. 1.500,-. Den resterende del af aftalesummen afregnes over den resterende l\u00f8betid.  \n" + 
					"\n" + 
					"Under fakturering tages der stilling til om kunden skal tilmeldes PBS eller email fakturering. Denne indstilling g\u00e6lder kun for partnerydelser og styres p\u00e5 baggrund af lokal governance.  \n" +
					"\n" + 
					"Angiv hvilket segment kunder har. Feltet er styrende for beregning af provision.  \n" + 
					"\n" + 
					"**Øvrige installationsydelser**  \n" + 
					"\n" + 
					"Afkryds relevante installationsydelser  \n" + 
					"\n" + 
					"Diverse installationsydelser angives. Evt. rabat p\u00e5 egne installationsydelser angives.\n" + 
					"\n" + 
					"**Hardware til rate**  \n" + 
					"\n" + 
					"Antal og m\u00e5nedspris for hardware angives. \n" + 
					"\n" + 
					"**Sammenfatning partner**  \n" + 
					"\n" + 
					"Hent regneark med vejledende partnerprovision.");
		}
		if (businessArea.hasFeature(FeatureType.FIBER)) {
			createPage(businessArea, MobilePageIds.MOBILE_FIBER_BUNDLES, "Fiber", null, 
					"SKAL SKRIVES OM... Ved angivelse af diverse partner relaterede indstillinger for partner tekniker, support ydelser, udstyr og kundesegment, er det muligt at konfigurere "
					+ "separate partner tilbud, i till\u00e6g til tilbuddet p\u00e5 l\u00f8sningen, samt ikke mindst beregne vejledende provision for l\u00f8sningen.",
					"**Indstillinger**  \n" + 
					"\n" + 
					"Grundindstillinger for support, rate og provision.  \n" + 
					"\n" + 
					"Antallet af brugere bestemmes p\u00e5 baggrund af de valgte profiler under fanen ”Pakker”. Pris pr. bruger og \n" + 
					"grundpris for support aftale inds\u00e6ttes. Disse priser fastl\u00e6gges p\u00e5 baggrund af lokal governance i de enkelte TDC Erhvervscentre.  \n" + 
					"\n" + 
					"L\u00f8betiden i m\u00e5neder f\u00f8lger aftalel\u00e6ngden p\u00e5 l\u00f8sningen – 3 \u00e5rs binding p\u00e5 l\u00f8sningen = 36 m\u00e5neder p\u00e5 support osv. \n" + 
					"\n" + 
					"Etableringspris og l\u00f8betid i m\u00e5neder for rate aftale angives. Disse priser fastl\u00e6gges p\u00e5 baggrund af lokal governance i de enkelte TDC Erhvervscentre.  \n" + 
					"\n" + 
					"Ved valg af upfront betaling p\u00e5 rate aftale, lyder f\u00f8rste afdrag p\u00e5 kr. 1.500,-. Den resterende del af aftalesummen afregnes over den resterende l\u00f8betid.  \n" + 
					"\n" + 
					"Under fakturering tages der stilling til om kunden skal tilmeldes PBS eller email fakturering. Denne indstilling g\u00e6lder kun for partnerydelser og styres p\u00e5 baggrund af lokal governance.  \n" +
					"\n" + 
					"Angiv hvilket segment kunder har. Feltet er styrende for beregning af provision.  \n" + 
					"\n" + 
					"**Øvrige installationsydelser**  \n" + 
					"\n" + 
					"Afkryds relevante installationsydelser  \n" + 
					"\n" + 
					"Diverse installationsydelser angives. Evt. rabat p\u00e5 egne installationsydelser angives.\n" + 
					"\n" + 
					"**Hardware til rate**  \n" + 
					"\n" + 
					"Antal og m\u00e5nedspris for hardware angives. \n" + 
					"\n" + 
					"**Sammenfatning partner**  \n" + 
					"\n" + 
					"Hent regneark med vejledende partnerprovision.");
//			createPage(businessArea, MobilePageIds.MOBILE_FIBER_ADDITIONAL_INFO, "Aftalepapir", null, 
//					"SKAL SKRIVES OM... Ved angivelse af diverse partner relaterede indstillinger for partner tekniker, support ydelser, udstyr og kundesegment, er det muligt at konfigurere "
//					+ "separate partner tilbud, i till\u00e6g til tilbuddet p\u00e5 l\u00f8sningen, samt ikke mindst beregne vejledende provision for l\u00f8sningen.",
//					"**Indstillinger**  \n" + 
//					"\n" + 
//					"Grundindstillinger for support, rate og provision.  \n" + 
//					"\n" + 
//					"Antallet af brugere bestemmes p\u00e5 baggrund af de valgte profiler under fanen ”Pakker”. Pris pr. bruger og \n" + 
//					"grundpris for support aftale inds\u00e6ttes. Disse priser fastl\u00e6gges p\u00e5 baggrund af lokal governance i de enkelte TDC Erhvervscentre.  \n" + 
//					"\n" + 
//					"L\u00f8betiden i m\u00e5neder f\u00f8lger aftalel\u00e6ngden p\u00e5 l\u00f8sningen – 3 \u00e5rs binding p\u00e5 l\u00f8sningen = 36 m\u00e5neder p\u00e5 support osv. \n" + 
//					"\n" + 
//					"Etableringspris og l\u00f8betid i m\u00e5neder for rate aftale angives. Disse priser fastl\u00e6gges p\u00e5 baggrund af lokal governance i de enkelte TDC Erhvervscentre.  \n" + 
//					"\n" + 
//					"Ved valg af upfront betaling p\u00e5 rate aftale, lyder f\u00f8rste afdrag p\u00e5 kr. 1.500,-. Den resterende del af aftalesummen afregnes over den resterende l\u00f8betid.  \n" + 
//					"\n" + 
//					"Under fakturering tages der stilling til om kunden skal tilmeldes PBS eller email fakturering. Denne indstilling g\u00e6lder kun for partnerydelser og styres p\u00e5 baggrund af lokal governance.  \n" +
//					"\n" + 
//					"Angiv hvilket segment kunder har. Feltet er styrende for beregning af provision.  \n" + 
//					"\n" + 
//					"**Øvrige installationsydelser**  \n" + 
//					"\n" + 
//					"Afkryds relevante installationsydelser  \n" + 
//					"\n" + 
//					"Diverse installationsydelser angives. Evt. rabat p\u00e5 egne installationsydelser angives.\n" + 
//					"\n" + 
//					"**Hardware til rate**  \n" + 
//					"\n" + 
//					"Antal og m\u00e5nedspris for hardware angives. \n" + 
//					"\n" + 
//					"**Sammenfatning partner**  \n" + 
//					"\n" + 
//					"Hent regneark med vejledende partnerprovision.");
		}
//		if (businessArea.hasFeature(FeatureType.TDC_OFFICE)) {
//			createPage(businessArea, MobilePageIds.MOBILE_OFFICE_BUNDLES, "Office pakker", null, 
//					"SKAL SKRIVES OM... Ved angivelse af diverse partner relaterede indstillinger for partner tekniker, support ydelser, udstyr og kundesegment, er det muligt at konfigurere "
//					+ "separate partner tilbud, i till\u00e6g til tilbuddet p\u00e5 l\u00f8sningen, samt ikke mindst beregne vejledende provision for l\u00f8sningen.",
//					"**Indstillinger**  \n" + 
//					"\n" + 
//					"Grundindstillinger for support, rate og provision.  \n" + 
//					"\n" + 
//					"Antallet af brugere bestemmes p\u00e5 baggrund af de valgte profiler under fanen ”Pakker”. Pris pr. bruger og \n" + 
//					"grundpris for support aftale inds\u00e6ttes. Disse priser fastl\u00e6gges p\u00e5 baggrund af lokal governance i de enkelte TDC Erhvervscentre.  \n" + 
//					"\n" + 
//					"L\u00f8betiden i m\u00e5neder f\u00f8lger aftalel\u00e6ngden p\u00e5 l\u00f8sningen – 3 \u00e5rs binding p\u00e5 l\u00f8sningen = 36 m\u00e5neder p\u00e5 support osv. \n" + 
//					"\n" + 
//					"Etableringspris og l\u00f8betid i m\u00e5neder for rate aftale angives. Disse priser fastl\u00e6gges p\u00e5 baggrund af lokal governance i de enkelte TDC Erhvervscentre.  \n" + 
//					"\n" + 
//					"Ved valg af upfront betaling p\u00e5 rate aftale, lyder f\u00f8rste afdrag p\u00e5 kr. 1.500,-. Den resterende del af aftalesummen afregnes over den resterende l\u00f8betid.  \n" + 
//					"\n" + 
//					"Under fakturering tages der stilling til om kunden skal tilmeldes PBS eller email fakturering. Denne indstilling g\u00e6lder kun for partnerydelser og styres p\u00e5 baggrund af lokal governance.  \n" +
//					"\n" + 
//					"Angiv hvilket segment kunder har. Feltet er styrende for beregning af provision.  \n" + 
//					"\n" + 
//					"**Øvrige installationsydelser**  \n" + 
//					"\n" + 
//					"Afkryds relevante installationsydelser  \n" + 
//					"\n" + 
//					"Diverse installationsydelser angives. Evt. rabat p\u00e5 egne installationsydelser angives.\n" + 
//					"\n" + 
//					"**Hardware til rate**  \n" + 
//					"\n" + 
//					"Antal og m\u00e5nedspris for hardware angives. \n" + 
//					"\n" + 
//					"**Sammenfatning partner**  \n" + 
//					"\n" + 
//					"Hent regneark med vejledende partnerprovision.");
//		}
	}

	@Override
	protected String getMiscMarkdown() {
		return "**Artikler fra TDC Perspektiv**\n\n" +
				"  1. <a href=\"http://perspektiv.tdc.dk/personlig-telefonservice-er-penge-vaerd/\" target=\"_blank\">Personlig telefonservice er penge værd</a>\n" +
				"  2. <a href=\"http://perspektiv.tdc.dk/nar-kunderne-ringer-kommer-20-aldrig-igennem/\" target=\"_blank\">Når kunderne ringer kommer 20 % aldrig igennem</a>\n" +
				"  3. <a href=\"http://perspektiv.tdc.dk/styrk-virksomheden-med-4g-daekning/\" target=\"_blank\">Styrk virksomheden med 4G dækning</a>\n";
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

}
