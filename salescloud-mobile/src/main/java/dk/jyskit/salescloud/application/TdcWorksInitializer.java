package dk.jyskit.salescloud.application;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.wicket.Application;

import dk.jyskit.salescloud.application.dao.ContractDao;
import dk.jyskit.salescloud.application.dao.SubscriptionDao;
import dk.jyskit.salescloud.application.extensionpoints.defaultimpl.CoreProductRelationTypeProvider;
import dk.jyskit.salescloud.application.extensions.MobileProductRelationTypeProvider;
import dk.jyskit.salescloud.application.model.Amounts;
import dk.jyskit.salescloud.application.model.BundleProductRelation;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.BusinessAreas;
import dk.jyskit.salescloud.application.model.Campaign;
import dk.jyskit.salescloud.application.model.CampaignProductRelation;
import dk.jyskit.salescloud.application.model.Contract;
import dk.jyskit.salescloud.application.model.FeatureType;
import dk.jyskit.salescloud.application.model.MobileCampaign;
import dk.jyskit.salescloud.application.model.MobileContract;
import dk.jyskit.salescloud.application.model.MobileProductBundle;
import dk.jyskit.salescloud.application.model.MobileProductBundleEnum;
import dk.jyskit.salescloud.application.model.MobileProductGroupEnum;
import dk.jyskit.salescloud.application.model.ProductAccessType;
import dk.jyskit.salescloud.application.model.ProductBundle;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.model.Subscription;
import dk.jyskit.salescloud.application.model.SystemUpdate;
import dk.jyskit.salescloud.application.pages.CorePageIds;
import dk.jyskit.salescloud.application.pages.MobilePageIds;
import dk.jyskit.salescloud.application.services.importdata.DataImporter;
import dk.jyskit.salescloud.application.services.importdata.ProductImportHandler;
import dk.jyskit.waf.application.Environment;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.utils.dataimport.DataImportException;
import dk.jyskit.waf.utils.guice.Lookup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TdcWorksInitializer extends CopyInitializer {
	public TdcWorksInitializer() {
		// Copy pages, etc. from Switchboard
		super("TDC Works Initializer", BusinessAreas.TDC_WORKS, "TDC Erhverv Works", "TDC Works...", BusinessAreas.SWITCHBOARD);
	}

	@Override
	public void initBusinessArea() {
		super.initBusinessArea();
		businessArea.addFeature(FeatureType.NETWORK_COVERAGE_MAP);
		businessArea.addFeature(FeatureType.OUTPUT_CDM);
		businessArea.addFeature(FeatureType.OUTPUT_AUTHORITY);
		businessArea.addFeature(FeatureType.OUTPUT_PROCESS);
		businessArea.addFeature(FeatureType.OUTPUT_PARTNER_SUPPORT);
		businessArea.addFeature(FeatureType.PARTNER_SETTINGS);
		businessArea.addFeature(FeatureType.RECURRING_FEE_SPLIT);
		businessArea.addFeature(FeatureType.SHOW_INSTALLATION_DATE);
		businessArea.addFeature(FeatureType.SHOW_CONTRACT_START_DATE);
		businessArea.addFeature(FeatureType.SWITCHBOARD);
		businessArea.addFeature(FeatureType.XDSL);
		businessArea.addFeature(FeatureType.TEM5_PRODUCTS);
		businessArea.addFeature(FeatureType.RABATAFTALE_CONTRACT_DISCOUNT);
		businessArea.addFeature(FeatureType.RABATAFTALE_CAMPAIGN_DISCOUNT);
		businessArea.addFeature(FeatureType.CONTRACT_ACCEPT_REPORT);
		businessArea.addFeature(FeatureType.MOBILE_BUNDLES_STANDARD);

		businessArea.setStandardDiscountMatrix("1300%,1400%,1600%,1700%,1900%,2000%#2500%,2800%,3100%,3400%,3700%,3900%#2700%,3000%,3300%,3600%,3900%,4200%");
	}

	@Override
	protected void addPages() {
		super.addPages();
		createPage(businessArea, CorePageIds.SALES_EXISTING_CONTRACTS, "Vælg kundesag", null,
				"Velkommen til TDC Works salgskonfigurator.\n\nKonfiguratoren skal hj\u00e6lpe dig til at lave det materiale, du skal bruge overfor dine kunder. Du kan danne et prisoverslag eller et egentligt tilbud, som kan bruges til at afstemme \u00f8konomi og l\u00f8sning med kunderne.\n\nDu kan altid f\u00e5 inspiration til din kundedialog ved at se n\u00e6rmere p\u00e5 TDC Perspektiv artiklerne som kan findes til h\u00f8jre.",
				"Du har to muligheder for at arbejde med kundesager:\n\n  * Du kan \u00e5bne en eksisterende kundesag ved at klikke \"V\u00e6lg\" til venstre\n\n  * Du kan oprette en ny salgssag ved at klikke p\u00e5 \"Ny kundesag\"\n\nDu kan med fordel arbejde med kategorier for dine kundesager for at skabe st\u00f8rre overblik. Anvend ikonet med den lille mand \u00f8verst i konfiguratoren til h\u00f8jre og klik p\u00e5 \"Kundesagskategorier\" i menuen.");
	}

	protected List<String> getPagesToIgnore() {
		List<String> pageIds = super.getPagesToIgnore();
		pageIds.add(CorePageIds.SALES_EXISTING_CONTRACTS);
		return pageIds;
	}

	protected void importProducts() {
		// Products specific for this business area
		try {
			ProductImportHandler importHandler = new ProductImportHandler(businessArea);
			MobileSalescloudApplication.get().getInjector().injectMembers(importHandler);
			DataImporter dataImporter = new DataImporter(importHandler, getFileFromClasspath("import/init/excel/tem5.xls"));
			dataImporter.getData();
		} catch (DataImportException e) {
			log.error("Failed to import excel file", e);
		}

		// Add product relations
		addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_N, "3234000", "4400281");		// "Musik på Hold" -> "Scale Installation Musik på Hold"
		addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_N, "3234700", "4400285");		// "Menuvalg" -> "Scale Installation Menuvalg"
//		addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_N, "3234100", "4400282");		// "PC Omstilling" -> "Scale Installation PC Omstilling"
//		addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_N, "3236600", "4400618");		// "PC Opkaldsklient" -> "Scale Installation PC Opkaldsklient"
//		addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_N, "3236100", "3266500");		// "Voquant Omstilling Ekstra" -> "Voquant Inst. 1-50 Bruger Ekstra"
		addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_N, "3234600", "4400284");		// "Udvidet Søgegruppe" -> "Scale Installation Udvidet Søgegr."
		addProductRelation(businessArea, "Service", CoreProductRelationTypeProvider.ALTERNATIVE_PRODUCTS, "0371000", "0311100", "0311200", "0311300");
	}

	protected void addBundlesToDefaultCampaign(Campaign campaign) {
		super.addBundlesToDefaultCampaign(campaign);

		int sortIndex = 1;

		// ----------------------------
		// Mobile Bundles
		// ----------------------------
		{
			MobileProductBundle bundle = new MobileProductBundle();
			bundle.setBundleType(MobileProductBundleEnum.MOBILE_BUNDLE);
			bundle.setPublicName("TDC Erhverv Works Mobil Minut");
			bundle.setProductId("3239524");
			bundle.setKvikCode(bundle.getPublicName());
			bundle.setInternalName(bundle.getKvikCode());
			bundle.setAddToContractDiscount(ProductBundle.RABATAFTALE_DISCOUNT_CONTRIBUTION);
//			bundle.setRabataftaleCampaignDiscountMatrix("13%,14%,16%,17%,19%,20%#25%,28%,31%,34%,37%,39%#27%,30%,33%,36%,39%,42%");
			bundle.setSortIndex(sortIndex++);
			productBundleDao.save(bundle);
			campaign.addProductBundle(bundle);

			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, bundle.getProductId(), true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_voice_1", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_data_1", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3283500", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_tilvalg_2", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3268000", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3236100", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3236600", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "0371000", true);
		}
		{
			MobileProductBundle bundle = new MobileProductBundle();
			bundle.setBundleType(MobileProductBundleEnum.MOBILE_BUNDLE);
			bundle.setPublicName("TDC Erhverv Works Mobil 1GB");
			bundle.setProductId("3239525");
			bundle.setKvikCode(bundle.getPublicName());
			bundle.setInternalName(bundle.getKvikCode());
			bundle.setAddToContractDiscount(ProductBundle.RABATAFTALE_DISCOUNT_CONTRIBUTION);
//			bundle.setRabataftaleCampaignDiscountMatrix("13%,14%,16%,17%,19%,20%#25%,28%,31%,34%,37%,39%#27%,30%,33%,36%,39%,42%");
			bundle.setSortIndex(sortIndex++);
			productBundleDao.save(bundle);
			campaign.addProductBundle(bundle);

			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, bundle.getProductId(), true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_voice_2", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_data_2", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_tilvalg_1", true);	// Yousee Music
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3283500", true);			// 4G
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_tilvalg_2", true);	// VoLTE for business
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3268000", true);			// Scale Mobil
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3236100", true);			// Beskedsvar
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3236600", true);			// PC opkaldsklient
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "0371000", true);			// Scale service
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3266200", true);			// Statusvisning
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "9988826", true);			// VoLTE Wi-Fi Erhverv

			addProductIdToBundle(bundle, ProductAccessType.OPTIONAL, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3242301", true);			// Datadeling-1
			addProductIdToBundle(bundle, ProductAccessType.OPTIONAL, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3242302", true);			// Datadeling-2
			addProductIdToBundle(bundle, ProductAccessType.NON_OPTIONAL, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3242303", true);			// Datadeling-3
		}
		{
			MobileProductBundle bundle = new MobileProductBundle();
			bundle.setBundleType(MobileProductBundleEnum.MOBILE_BUNDLE);
			bundle.setPublicName("TDC Erhverv Works Mobil 3GB");
			bundle.setProductId("3239526");
			bundle.setKvikCode(bundle.getPublicName());
			bundle.setInternalName(bundle.getKvikCode());
			bundle.setAddToContractDiscount(ProductBundle.RABATAFTALE_DISCOUNT_CONTRIBUTION);
//			bundle.setRabataftaleCampaignDiscountMatrix("13%,14%,16%,17%,19%,20%#25%,28%,31%,34%,37%,39%#27%,30%,33%,36%,39%,42%");
			bundle.setSortIndex(sortIndex++);
			productBundleDao.save(bundle);
			campaign.addProductBundle(bundle);

			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, bundle.getProductId(), true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_voice_2", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_data_3", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_tilvalg_1", true);	// Yousee Music
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3283500", true);			// 4G
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_tilvalg_2", true);	// VoLTE for business
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3268000", true);			// Scale Mobil
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3236100", true);			// Beskedsvar
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3236600", true);			// PC opkaldsklient
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "0371000", true);			// Scale service
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3266200", true);			// Statusvisning
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "9988826", true);			// VoLTE Wi-Fi Erhverv
		}
		{
			MobileProductBundle bundle = new MobileProductBundle();
			bundle.setBundleType(MobileProductBundleEnum.MOBILE_BUNDLE);
			bundle.setPublicName("TDC Erhverv Works Mobil 5GB");
			bundle.setProductId("3239527");
			bundle.setKvikCode(bundle.getPublicName());
			bundle.setInternalName(bundle.getKvikCode());
			bundle.setAddToContractDiscount(ProductBundle.RABATAFTALE_DISCOUNT_CONTRIBUTION);
//			bundle.setRabataftaleCampaignDiscountMatrix("13%,14%,16%,17%,19%,20%#25%,28%,31%,34%,37%,39%#27%,30%,33%,36%,39%,42%");
			bundle.setSortIndex(sortIndex++);
			productBundleDao.save(bundle);
			campaign.addProductBundle(bundle);

			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, bundle.getProductId(), true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_voice_2", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_data_4", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_tilvalg_1", true);	// Yousee Music
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3283500", true);			// 4G
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_tilvalg_2", true);	// VoLTE for business
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3268000", true);			// Scale Mobil
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3236100", true);			// Beskedsvar
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3236600", true);			// PC opkaldsklient
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "0371000", true);			// Scale service
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3266200", true);			// Statusvisning
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "9988826", true);			// VoLTE Wi-Fi Erhverv
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3232500", true);			// Call recording
		}
		{
			MobileProductBundle bundle = new MobileProductBundle();
			bundle.setBundleType(MobileProductBundleEnum.MOBILE_BUNDLE);
			bundle.setPublicName("TDC Erhverv Works Mobil 30GB Std");
			bundle.setProductId("3239528");
			bundle.setKvikCode(bundle.getPublicName());
			bundle.setInternalName(bundle.getKvikCode());
			bundle.setAddToContractDiscount(ProductBundle.RABATAFTALE_DISCOUNT_CONTRIBUTION);
//			bundle.setRabataftaleCampaignDiscountMatrix("13%,14%,16%,17%,19%,20%#25%,28%,31%,34%,37%,39%#27%,30%,33%,36%,39%,42%");
			bundle.setSortIndex(sortIndex++);
			bundle.setActive(false);
			productBundleDao.save(bundle);
			campaign.addProductBundle(bundle);

			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, bundle.getProductId(), true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_voice_2", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_data_7", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_tilvalg_1", true);	// Yousee Music
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3283500", true);			// 4G
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_tilvalg_2", true);	// VoLTE for business
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3268000", true);			// Scale Mobil
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3236100", true);			// Beskedsvar
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3236600", true);			// PC opkaldsklient
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "0371000", true);			// Scale service
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3266200", true);			// Statusvisning
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "9988826", true);			// VoLTE Wi-Fi Erhverv
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3232500", true);			// Call recording
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3242301", true);			// Datadeling-1
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3242302", true);			// Datadeling-2
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3242303", true);			// Datadeling-3
		}
		{
			MobileProductBundle bundle = new MobileProductBundle();
			bundle.setBundleType(MobileProductBundleEnum.MOBILE_BUNDLE);
			bundle.setPublicName("TDC Erhverv Works Mobil 15GB");
			bundle.setProductId("3239529");
			bundle.setKvikCode(bundle.getPublicName());
			bundle.setInternalName(bundle.getKvikCode());
			bundle.setAddToContractDiscount(ProductBundle.RABATAFTALE_DISCOUNT_CONTRIBUTION);
//			bundle.setRabataftaleCampaignDiscountMatrix("13%,14%,16%,17%,19%,20%#25%,28%,31%,34%,37%,39%#27%,30%,33%,36%,39%,42%");
			bundle.setSortIndex(sortIndex++);
			productBundleDao.save(bundle);
			campaign.addProductBundle(bundle);

			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, bundle.getProductId(), true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_voice_2", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_data_5", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_tilvalg_1", true);	// Yousee Music
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3283500", true);			// 4G
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_tilvalg_2", true);	// VoLTE for business
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3268000", true);			// Scale Mobil
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3236100", true);			// Beskedsvar
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3236600", true);			// PC opkaldsklient
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "0371000", true);			// Scale service
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3266200", true);			// Statusvisning
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "9988826", true);			// VoLTE Wi-Fi Erhverv
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3232500", true);			// Call recording
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3242301", true);			// Datadeling-1
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3233900", true);			// Job/privat zone
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3266300", true);			// Kalender integration
		}
		{
			MobileProductBundle bundle = new MobileProductBundle();
			bundle.setBundleType(MobileProductBundleEnum.MOBILE_BUNDLE);
			bundle.setPublicName("TDC Erhverv Works Mobil 20GB");
			bundle.setProductId("3239530");
			bundle.setKvikCode(bundle.getPublicName());
			bundle.setInternalName(bundle.getKvikCode());
			bundle.setAddToContractDiscount(ProductBundle.RABATAFTALE_DISCOUNT_CONTRIBUTION);
//			bundle.setRabataftaleCampaignDiscountMatrix("13%,14%,16%,17%,19%,20%#25%,28%,31%,34%,37%,39%#27%,30%,33%,36%,39%,42%");
			bundle.setSortIndex(sortIndex++);
			productBundleDao.save(bundle);
			campaign.addProductBundle(bundle);

			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, bundle.getProductId(), true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_voice_2", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_data_6", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_tilvalg_1", true);	// Yousee Music
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3283500", true);			// 4G
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_tilvalg_2", true);	// VoLTE for business
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3268000", true);			// Scale Mobil
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3236100", true);			// Beskedsvar
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3236600", true);			// PC opkaldsklient
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "0371000", true);			// Scale service
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3266200", true);			// Statusvisning
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "9988826", true);			// VoLTE Wi-Fi Erhverv
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3232500", true);			// Call recording
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3289201", true);			// Partner net USA og Canada 1 GB
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3242301", true);			// Datadeling-1
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3233900", true);			// Job/privat zone
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3266300", true);			// Kalender integration
		}
		{
			MobileProductBundle bundle = new MobileProductBundle();
			bundle.setBundleType(MobileProductBundleEnum.MOBILE_BUNDLE);
			bundle.setPublicName("TDC Erhverv Works Mobil 30GB");
			bundle.setProductId("3239531");
			bundle.setKvikCode(bundle.getPublicName());
			bundle.setInternalName(bundle.getKvikCode());
			bundle.setAddToContractDiscount(ProductBundle.RABATAFTALE_DISCOUNT_CONTRIBUTION);
//			bundle.setRabataftaleCampaignDiscountMatrix("13%,14%,16%,17%,19%,20%#25%,28%,31%,34%,37%,39%#27%,30%,33%,36%,39%,42%");
			bundle.setSortIndex(sortIndex++);
			productBundleDao.save(bundle);
			campaign.addProductBundle(bundle);

			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, bundle.getProductId(), true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_voice_2", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_data_7", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_tilvalg_1", true);	// Yousee Music
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3283500", true);			// 4G
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "T5_tilvalg_2", true);	// VoLTE for business
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3268000", true);			// Scale Mobil
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3236100", true);			// Beskedsvar
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3236600", true);			// PC opkaldsklient
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "0371000", true);			// Scale service
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3266200", true);			// Statusvisning
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "9988826", true);			// VoLTE Wi-Fi Erhverv
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3232500", true);			// Call recording
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3289202", true);			// Partner net Verden 1 GB
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3242301", true);			// Datadeling-1
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3242302", true);			// Datadeling-2
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3242303", true);			// Datadeling-3
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3233900", true);			// Job/privat zone
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE, "3266300", true);			// Kalender integration
		}

		// Omstillingspakker
		{
			MobileProductBundle bundle = new MobileProductBundle();
			bundle.setBundleType(MobileProductBundleEnum.SWITCHBOARD_BUNDLE);
			bundle.setPublicName("Omstilling");
			bundle.setInternalName("TDC Omstilling");
//			bundle.setRabataftaleCampaignDiscountMatrix("13%,14%,16%,17%,19%,20%#25%,28%,31%,34%,37%,39%#27%,30%,33%,36%,39%,42%");
			bundle.setSortIndex(sortIndex++);
			productBundleDao.save(bundle);
			campaign.addProductBundle(bundle);

			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD, "3239617", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD, "_omst_HOVEDNR", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD, "_omst_VELKOMST", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD, "_omst_KØGRUPPE", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD, "_omst_IVR", true);
		}
		{
			MobileProductBundle bundle = new MobileProductBundle();
			bundle.setBundleType(MobileProductBundleEnum.SWITCHBOARD_BUNDLE);
			bundle.setPublicName("Omstilling Ekstra");
			bundle.setInternalName("TDC Omstilling Ekstra");
//			bundle.setRabataftaleCampaignDiscountMatrix("13%,14%,16%,17%,19%,20%#25%,28%,31%,34%,37%,39%#27%,30%,33%,36%,39%,42%");
			bundle.setSortIndex(30);
			productBundleDao.save(bundle);
			businessArea.getPermanentCampaign().addProductBundle(bundle);

			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD, "3239618", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD, "_omst_HOVEDNR", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD, "_omst_VELKOMST", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD, "_omst_KØGRUPPE", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD, "_omst_IVR", true);
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD, "_omst_OMSTILLINGSBORD", true);
		}
	}

	protected void addProductRelationsToDefaultCampaign(Campaign campaign) {
		((MobileCampaign) campaign).setPrisaftaleTextMatrix(
				"13% rabat mobil prisaftale nr. 14536,14% rabat mobil prisaftale nr. 14574,16% rabat mobil prisaftale nr. 14575,17% rabat mobil prisaftale nr. 14576,19% rabat mobil prisaftale nr. 14577,20% rabat mobil prisaftale nr. 14578#" +
				"25% rabat mobil prisaftale nr. 14579,28% rabat mobil prisaftale nr. 14580,31% rabat mobil prisaftale nr. 14581,34% rabat mobil prisaftale nr. 14582,37% rabat mobil prisaftale nr. 14583,40% rabat mobil prisaftale nr. 14584#" +
				"27% rabat mobil prisaftale nr. 14585,30% rabat mobil prisaftale nr. 14586,33% rabat mobil prisaftale nr. 14587,36% rabat mobil prisaftale nr. 14588,39% rabat mobil prisaftale nr. 14589,42% rabat mobil prisaftale nr. 14590#");

		ProductGroup group = businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_ADDON.getKey());
		addCampaignProductRelation(campaign, group, "0311100");
		addCampaignProductRelation(campaign, group, "0311200");
		addCampaignProductRelation(campaign, group, "0311300");
		addCampaignProductRelation(campaign, group, "0371000");
		addCampaignProductRelation(campaign, group, "3234000");
//		addCampaignProductRelation(campaign, group, "3234100");
		addCampaignProductRelation(campaign, group, "3234600");
		addCampaignProductRelation(campaign, group, "3234700");
		addCampaignProductRelation(campaign, group, "3234800");
		addCampaignProductRelation(campaign, group, "3236100");
//		addCampaignProductRelation(campaign, group, "3236600");
	}

	private void addCampaignProductRelation(Campaign campaign, ProductGroup group, String productId) {
		CampaignProductRelation rel = new CampaignProductRelation();
		rel.setCampaign(campaign);
		rel.setProduct(group.getProductByProductId(productId));
//		rel.setRabataftaleCampaignDiscountMatrix("13%,14%,16%,17%,19%,20%#25%,28%,31%,34%,37%,39%#27%,30%,33%,36%,39%,42%");
		rel.setCampaignPriceAmounts(new Amounts());
		rel.setCampaignDiscountAmounts(new Amounts());
		campaign.addCampaignProductRelation(rel);
	}

	protected void makeSystemUpdates(BusinessArea businessArea) {
		try {
			log.info("Checking for system updates");

			{
				String name = "Set accesscodes 1"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
				if (update == null) {
					log.info("Update starting: " + name);

					{
						//	+---------------+---------------+----------------------------------------------------+
						//	| email         | EMAIL         | ACCESSCODES                                        |
						//	+---------------+---------------+----------------------------------------------------+
						//	| aldi@tdc.dk   | aldi@tdc.dk   | tem5_konfigurator                                  |
						//	| larsa@tdc.dk  | larsa@tdc.dk  | tem5_konfigurator                                  |
						//	| HEKAU@tdc.dk  | hekau@tdc.dk  | wifi_konfigurator,tem5_konfigurator                |
						//	| thber@tdc.dk  | thber@tdc.dk  | genforhandling,tem5_konfigurator,wifi_konfigurator |
						//	| jan@escapetech.dk | jan@escapetech.dk | genforhandling,tem5_konfigurator,wifi_konfigurator |
						//	| whe@tdc.dk    | whe@tdc.dk    | wifi_konfigurator,tem5_konfigurator                |
						//	| mhasl@tdc.dk  | NULL          | genforhandling,tem5_konfigurator,wifi_konfigurator |
						//	| arsk@tdc.dk   | NULL          | genforhandling                                     |
						//	| arsk@tdc.dk   | NULL          | genforhandling                                     |
						//	| mhasl@tdc.dk  | NULL          | genforhandling,tem5_konfigurator                   |
						//	+---------------+---------------+----------------------------------------------------+
						if (Environment.isOneOf("prod-low")) {
							BaseUser user;
							setAccessCodes("aldi@tdc.dk", 0, "tem5_konfigurator");
							setAccessCodes("larsa@tdc.dk", 0, "tem5_konfigurator");
							setAccessCodes("HEKAU@tdc.dk", 0, "wifi_konfigurator,tem5_konfigurator");
							setAccessCodes("thber@tdc.dk", 0, "genforhandling,tem5_konfigurator,wifi_konfigurator");
							setAccessCodes("jan@escapetech.dk", 0, "genforhandling,tem5_konfigurator,wifi_konfigurator");
							setAccessCodes("whe@tdc.dk", 0, "wifi_konfigurator,tem5_konfigurator");
							setAccessCodes("mhasl@tdc.dk", 0, "genforhandling,tem5_konfigurator,wifi_konfigurator");
							setAccessCodes("mhasl@tdc.dk", 1, "genforhandling,tem5_konfigurator");
							setAccessCodes("arsk@tdc.dk", 0, "genforhandling");
							setAccessCodes("arsk@tdc.dk", 1, "genforhandling");
						}
					}

					log.info("Update done: " + name);
					update = new SystemUpdate();
					update.setBusinessAreaId(businessArea.getBusinessAreaId());
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}

			{
				String name = "Remove bad product relations"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
				if (update == null) {
					log.info("Update starting: " + name);

					try {
						for (Campaign c : businessArea.getCampaigns()) {
							MobileCampaign campaign = (MobileCampaign) c;
							for (ProductBundle bundle : campaign.getProductBundles()) {
								Iterator<BundleProductRelation> iterator = bundle.getProducts().iterator();
								while (iterator.hasNext()) {
									BundleProductRelation productRelation = iterator.next();
									if (productRelation.getProduct() == null) {
										iterator.remove();
									}
								}
							}
						}
						businessAreaDao.save(businessArea);
					} catch (Exception e) {
						log.error("FAIL", e);
					}

					log.info("Update done: " + name);
					update = new SystemUpdate();
					update.setBusinessAreaId(businessArea.getBusinessAreaId());
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}

			{
				String name = "TDC Works name"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
				if (update == null) {
					log.info("Update starting: " + name);

					try {
						businessArea.setName("TDC Erhverv Works");
						businessAreaDao.save(businessArea);
					} catch (Exception e) {
						log.error("FAIL", e);
					}

					log.info("Update done: " + name);
					update = new SystemUpdate();
					update.setBusinessAreaId(businessArea.getBusinessAreaId());
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}

			{
				String name = "Removed network coverage"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
				if (update == null) {
					log.info("Update starting: " + name);

					try {
						Iterator<FeatureType> iter = businessArea.getFeatures().iterator();
						while (iter.hasNext()) {
							FeatureType t = iter.next();
							if (t.equals(FeatureType.NETWORK_COVERAGE_MAP)) {
								iter.remove();
								break;
							}
						}
						businessAreaDao.save(businessArea);
					} catch (Exception e) {
						log.error("FAIL", e);
					}

					log.info("Update done: " + name);
					update = new SystemUpdate();
					update.setBusinessAreaId(businessArea.getBusinessAreaId());
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}

			// ----------------------------------------------------------------------------------------------------------------------------
			// DEV: Always delete old contracts
			// ----------------------------------------------------------------------------------------------------------------------------
			if (Application.get().usesDevelopmentConfig()) {
				String name = "Get rid of old contracts"; 
				log.info("Update starting: " + name);
				try {
					ContractDao contractDao = Lookup.lookup(ContractDao.class);
					contractDao.em().getTransaction().begin();
					SubscriptionDao subscriptionDao = Lookup.lookup(SubscriptionDao.class);
					for (Contract c : contractDao.findOlderThan(DateUtils.addYears(new Date(), -1))) {
						System.out.println("x");
						MobileContract mc = (MobileContract) c;
						Iterator<Subscription> iter = mc.getSubscriptions().iterator();
						while (iter.hasNext()) {
							System.out.print("-");
							Subscription subscription = iter.next();
							iter.remove();
							subscriptionDao.delete(subscription);
						}
						contractDao.delete(c);
					}
					contractDao.em().getTransaction().commit();
				} catch (Exception e) {
					log.error("FAIL", e);
				}
				log.info("Update done: " + name);
			}
			
//			{
//				String name = "Contract status default value"; // Don't change this name!
//				SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
//				if (update == null) {
//					log.info("Update starting: " + name);
//					
//					try {
//						MobileContractDao mobileContractDao = Lookup.lookup(MobileContractDao.class);
//						for (MobileContract c : mobileContractDao.findAll()) {
//							if (c.getStatus() == null) {
//								c.setStatus(ContractStatusEnum.OPEN);
//								mobileContractDao.save(c);
//							}
//						}
//					} catch (Exception e) {
//						log.error("FAIL", e);
//					}
//					
//					log.info("Update done: " + name);
//					update = new SystemUpdate();
//					update.setBusinessAreaId(businessArea.getBusinessAreaId());
//					update.setName(name);
//					systemUpdateDao.save(update);
//				}
//			}
			
			log.info("Done making system updates");
		} catch (Exception e) {
			log.error("", e);
			handleInitializationException(e); 		}
	}
	
	private void setAccessCodes(String email, int index, String accessCodes) {
		try {
			BaseUser user = userDao.findByEmail(email).get(index);	
			((SalespersonRole) user.getRole(SalespersonRole.class)).setAccessCodes(accessCodes);
			userDao.save(user);
		} catch (Exception e) {
			log.warn("Failed to set accesscodes for: " + email);
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
			log.error("I'm guessing this is the problem: \n"
					+ "An object of type '" + constraintViolation.getLeafBean().getClass().getSimpleName() 
					+ "' has a property '" + constraintViolation.getPropertyPath() + "' which has value '" 
					+ constraintViolation.getInvalidValue() + "'. The problem is: '" + constraintViolation.getMessage() + "'");
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

