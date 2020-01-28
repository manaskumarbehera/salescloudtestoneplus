package dk.jyskit.salescloud.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.SubscriptionDao;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.BusinessAreas;
import dk.jyskit.salescloud.application.model.Campaign;
import dk.jyskit.salescloud.application.model.FeatureType;
import dk.jyskit.salescloud.application.model.MobileProduct;
import dk.jyskit.salescloud.application.model.MobileProductBundle;
import dk.jyskit.salescloud.application.model.MobileProductBundleEnum;
import dk.jyskit.salescloud.application.model.MobileProductGroupEnum;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.ProductAccessType;
import dk.jyskit.salescloud.application.model.ProductBundle;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.salescloud.application.model.Subscription;
import dk.jyskit.salescloud.application.model.SystemUpdate;
import dk.jyskit.salescloud.application.pages.CorePageIds;
import dk.jyskit.salescloud.application.pages.MobilePageIds;
import dk.jyskit.salescloud.application.services.importdata.DataImporter;
import dk.jyskit.salescloud.application.services.importdata.ProductImportHandler;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.utils.dataimport.DataImportException;
import dk.jyskit.waf.utils.dataimport.TabularReader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TdcOfficeInitializer extends CopyInitializer {
	@Inject
	private SubscriptionDao subscriptionDao;
	
	public TdcOfficeInitializer() {
		// Copy pages, etc. from Switchboard
		super("TDC Office Initializer", BusinessAreas.TDC_OFFICE, "Office in the Cloud", "Office in the Cloud ... TODO", BusinessAreas.TDC_WORKS);
	}
	
	@Override
	public void initBusinessArea() {
		super.initBusinessArea();

		businessArea.addFeature(FeatureType.TDC_OFFICE);
		businessArea.addFeature(FeatureType.RECURRING_FEE_SPLIT);
	}
	
	@Override
	protected void addPages() {
		super.addPages();
		createPage(businessArea, CorePageIds.SALES_EXISTING_CONTRACTS, "Vælg kundesag", null, 
				"Velkommen til TDC Office salgskonfigurator.\n\nKonfiguratoren skal hj\u00e6lpe dig til at lave det materiale, du skal bruge overfor dine kunder. Du kan danne et prisoverslag eller et egentligt tilbud, som kan bruges til at afstemme \u00f8konomi og l\u00f8sning med kunderne.\n\nDu kan altid f\u00e5 inspiration til din kundedialog ved at se n\u00e6rmere p\u00e5 TDC Perspektiv artiklerne som kan findes til h\u00f8jre.",
				"Du har to muligheder for at arbejde med kundesager:\n\n  * Du kan \u00e5bne en eksisterende kundesag ved at klikke \"V\u00e6lg\" til venstre\n\n  * Du kan oprette en ny salgssag ved at klikke p\u00e5 \"Ny kundesag\"\n\nDu kan med fordel arbejde med kategorier for dine kundesager for at skabe st\u00f8rre overblik. Anvend ikonet med den lille mand \u00f8verst i konfiguratoren til h\u00f8jre og klik p\u00e5 \"Kundesagskategorier\" i menuen.");
		createPage(businessArea, MobilePageIds.MOBILE_CONTRACT_SETTINGS, "Kampagne", null, 
				"Inden selve l\u00f8sningen, skal vi vide lidt om kunden og det pris-setup vi skal arbejde videre med.\n\nAnvend nedenst\u00e5ende sp\u00f8rgsm\u00e5l for at afd\u00e6kke kundens behov. Find evt. hj\u00e6lp til de enkelte emner i kassen til h\u00f8jre.\n\n**NB: Det er vigtigt du anvender mark\u00f8rerne ved sp\u00f8rgsm\u00e5lene, da vi anvender input aktivt for at blive klogere p\u00e5 vores kunder.**", 
				"**Kampagne:**\n\nAktive kampagner vil i kampagneperidoen kunne v\u00e6lges i drop down.\n\nOBS:\n\n\n  * To eller flere kampagner kan ikke kombineres\n\n  * V\u00e6lges kampagne er det udelukkende de i kampagnen indeholdte abonnementer som pr\u00e6senteres i konfiguratoren\n\n**Hj\u00e6lp til behovsafd\u00e6kning:**\n\nKunden er meget afh\u00E6ngig af sin telefon:\n\n  * Vores erfaringer viser, at 20 % af alle kald aldrig bliver svaret. Derfor er en omstillingsl\u00F8sning relevant\n\nKundens hovednummer er meget velkendt:\n\n  * Som en del af l\u00F8sningen i TDC kan du beholde dit velkendte hovednummer \n  * Du kan tilk\u00F8be flere hovednumre, hvis du har behov for flere kundeindgange\n\nDet er vigtigt at kunden oplever god service:\n\n  * K\u00F8 og derfor ventetid er ikke optimal service, hvorfor det er vigtigt, at du f\u00E5r sat k\u00F8grupper og telefonnumre rigtigt op. Derfor f\u00E5r du hj\u00E6lp til installation i TDC\n  * Den bedste service opleves gennem ved en menneskelig relation. Derfor anbefaler TDC et omstillingsbord hvor en medarbejder kan h\u00E5ndtere kald\n\nDet giver meget v\u00E6rdi at kald besvares personligt\n\n  * TDC har et af markedets bedste omstillingsborde. Du kan se status, b\u00E5de kalender og telefon, p\u00E5 alle medarbejdere samt automatisk se opkaldshistorik p\u00E5 indkommende kald fra kunder\n\nDet betyder meget, at der altid kan stilles videre til kollegaer\n\n  * De enkelte brugere kan stille aktive kald videre til kollegaer direkte p\u00E5 mobiltelefonen");
		createPage(businessArea, MobilePageIds.MOBILE_OFFICE_ADDITIONAL_PRODUCTS, "Tilkøb", null, "TODO ... ");
		createPage(businessArea, MobilePageIds.MOBILE_OFFICE_IMPLEMENTATION, "Implementeringsoplysninger", null, "TODO ... ");
		createPage(businessArea, MobilePageIds.MOBILE_SUBSCRIPTION_CONFIGURATION, "Konfigurering", null, "TODO ... ");
		createPage(businessArea, MobilePageIds.MOBILE_STANDARD_BUNDLES, "Office in the Cloud pakker", null, "TODO...");
	}

	protected List<String> getPagesToIgnore() {
		List<String> pageIds = super.getPagesToIgnore();
		pageIds.add(MobilePageIds.MOBILE_CONTRACT_SETTINGS);
		return pageIds;
	}
	
	protected void importProducts() {
		// Products specific for this business area
		try {
			ProductImportHandler importHandler = new ProductImportHandler(businessArea);
			MobileSalescloudApplication.get().getInjector().injectMembers(importHandler);
			DataImporter dataImporter = new DataImporter(importHandler, getFileFromClasspath("import/init/excel/tdc-office.xls")) {
//				protected TabularReader createReader(String fileName, String sheetName) throws IOException, InvalidFormatException {
//					return new GoogleSpreadsheetReader();
//				}
			};
			dataImporter.getData();
		} catch (DataImportException e) {
			log.error("Failed to import excel file", e);
		}
	}
	
	protected void addBundlesToDefaultCampaign(Campaign campaign) {
		super.addBundlesToDefaultCampaign(campaign);
		
		MutableInt sortIndex = new MutableInt(1);
		
		// ----------------------------
		// Office Bundles
		// ----------------------------
		
//		Office Desktop
//		Office Online
//		OneDrive For Business (fildeling)
//		Exchange Online (mail)
//		Skype For Business Ready
//		Avanceret Sikkerhed
//		Mobil adgang
		
		String OfficeDesktop					= "365_01_001";
		String OfficeOnline 					= "365_01_002";
		String OneDriveForBusiness				= "365_01_003";
		String ExchangeOnline 					= "365_01_004";
		String SkypeForBusinessReady 			= "365_01_005";
		String AvanceretSikkerhed 				= "365_01_006";
		String MobilAdgang						= "365_01_007";
		
		String LilleBegrænset					= "365_02_001";
		String LilleMedKontormedarbejdere		= "365_02_002";
		String StorBegrænset					= "365_02_003";
		String StorMedKontormedarbejdere		= "365_02_004";
		
		String UdenOfficePakken					= "365_03_001";
		String MedOfficePakken					= "365_03_002";
		
		{
			String officeProductId = "101832be";
			MobileProductBundle bundle = createOfficeBundle(campaign, sortIndex, officeProductId);
			
			Product mainProduct = addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE, officeProductId, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_TOP, LilleBegrænset, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_BOTTOM, UdenOfficePakken, true);
			
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED, OfficeOnline, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED, OneDriveForBusiness, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED, ExchangeOnline, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED, MobilAdgang, true);	
			
			bundle.setPublicName(mainProduct.getPublicName());
			bundle.setKvikCode(mainProduct.getPublicName());
			bundle.setInternalName(mainProduct.getInternalName());
			productBundleDao.save(bundle);
		}
		{
			String officeProductId = "101832bp";
			MobileProductBundle bundle = createOfficeBundle(campaign, sortIndex, officeProductId);
			
			Product mainProduct = addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE, officeProductId, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_TOP, LilleMedKontormedarbejdere, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_BOTTOM, MedOfficePakken, true);	
			
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED, OfficeDesktop, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED, OfficeOnline, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED, OneDriveForBusiness, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED, ExchangeOnline, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED, MobilAdgang, true);
			
			bundle.setPublicName(mainProduct.getPublicName());
			bundle.setKvikCode(mainProduct.getPublicName());
			bundle.setInternalName(mainProduct.getInternalName());
			productBundleDao.save(bundle);
		}
		{
			String officeProductId = "101832e1";
			MobileProductBundle bundle = createOfficeBundle(campaign, sortIndex, officeProductId);
			
			bundle.setFlags("linebreak");
			
			Product mainProduct = addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE, officeProductId, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_TOP, StorBegrænset, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_BOTTOM, UdenOfficePakken, true);
			
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED, OfficeOnline, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED, OneDriveForBusiness, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED, ExchangeOnline, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED, MobilAdgang, true);	
			
			bundle.setPublicName(mainProduct.getPublicName());
			bundle.setKvikCode(mainProduct.getPublicName());
			bundle.setInternalName(mainProduct.getInternalName());
			productBundleDao.save(bundle);
		}
		{
			String officeProductId = "101832e3";
			MobileProductBundle bundle = createOfficeBundle(campaign, sortIndex, officeProductId);
			
			Product mainProduct = addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE, officeProductId, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_TOP, StorMedKontormedarbejdere, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_BOTTOM, MedOfficePakken, true);	
			
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED, OfficeDesktop, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED, OfficeOnline, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED, OneDriveForBusiness, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED, ExchangeOnline, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED, SkypeForBusinessReady, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED, AvanceretSikkerhed, true);	
			addProductIdToBundle(bundle, ProductAccessType.INCLUDED, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BUNDLE_INCLUDED, MobilAdgang, true);	
			
			bundle.setPublicName(mainProduct.getPublicName());
			bundle.setKvikCode(mainProduct.getPublicName());
			bundle.setInternalName(mainProduct.getInternalName());
			productBundleDao.save(bundle);
		}
	}

	private MobileProductBundle createOfficeBundle(Campaign campaign, MutableInt sortIndex, String productId) {
		MobileProductBundle bundle = new MobileProductBundle();
		bundle.setBundleType(MobileProductBundleEnum.OFFICE_BUNDLE);
		bundle.setPublicName("x");
		bundle.setKvikCode("x");
		bundle.setInternalName("x");
		bundle.setProductId(productId);
		bundle.setAddToContractDiscount(ProductBundle.FIXED_DISCOUNT_CONTRIBUTION);
		bundle.setSortIndex(sortIndex.intValue());
		sortIndex.increment();
		productBundleDao.save(bundle);
		campaign.addProductBundle(bundle);
		return bundle;
	}
		
	protected void addProductRelationsToDefaultCampaign(Campaign campaign) {
	}

	protected void makeSystemUpdates(BusinessArea businessArea) {
		try {
			log.info("Checking for system updates");
			
			{
				String name = "Make Bent admin"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, 0);
				if (update == null) {
					log.info("Update starting: '" + name + "' for businessarea 0");
					
					List<BaseUser> users = userDao.findByEmail("bmla@tdc.dk");
					for (BaseUser user : users) {
						AdminRole adminRole = (AdminRole) user.getRole(AdminRole.class);
						if (adminRole == null) {
							user.addRole(new AdminRole());
						}
						userDao.save(user);
					}
					
					update = new SystemUpdate();
					update.setBusinessAreaId(0);
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}
			
			{
				String name = "Delete Cloud Academy product"; // Don't change this name!
				int businessAreaId = BusinessAreas.TDC_OFFICE;
				SystemUpdate update = systemUpdateDao.findByName(name, businessAreaId);
				if (update == null) {
					log.info("Update starting: '" + name + "' for businessarea " + businessAreaId);
					
					ProductGroup group = createProductGroup(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_ADDON);
					Product product = coreProductDao.findByProductGroupAndProductId(businessArea.getId(), group.getUniqueName(), "101603");
					if (product != null) {
						List<Subscription> subscriptions = subscriptionDao.findAll();
						for (Subscription subscription : subscriptions) {
							if (subscription.getContract().getBusinessArea().getBusinessAreaId() == businessAreaId) {
								for (MobileProduct p: subscription.getProducts()) {
									if (p.getId().equals(product.getId())) {
										subscription.getProducts().remove(product);
										log.info("Removed product: " + product.getPublicName());
										subscriptionDao.save(subscription);
										break;
									}
								}
							}
						}
					}
					
					update = new SystemUpdate();
					update.setBusinessAreaId(businessAreaId);
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}
			
			{
				String name = "Last update before production"; // Don't change this name!
				int businessAreaId = BusinessAreas.TDC_OFFICE;
				SystemUpdate update = systemUpdateDao.findByName(name, businessAreaId);
				if (update == null) {
					log.info("Update starting: '" + name + "' for businessarea " + businessAreaId);
					
					{
						MobileProduct product = (MobileProduct) coreProductDao.findByBusinessAreaAndProductId(businessArea.getId(), "101001");
						if (product != null) {
							product.setExcludeFromOffer(false);
							productDao.save(product);
						}
					}
					{
						MobileProduct product = (MobileProduct) coreProductDao.findByBusinessAreaAndProductId(businessArea.getId(), "101002");
						if (product != null) {
							product.setExcludeFromOffer(false);
							productDao.save(product);
						}
					}
					
					update = new SystemUpdate();
					update.setBusinessAreaId(businessAreaId);
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}
			
			log.info("Done making system updates");
		} catch (Exception e) {
			log.error("", e);
			handleInitializationException(e); 		}
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

