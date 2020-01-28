package dk.jyskit.salescloud.application;

import java.util.Iterator;
import java.util.List;

import dk.jyskit.salescloud.application.dao.ProductGroupDao;
import org.apache.wicket.util.lang.Objects;

import dk.jyskit.salescloud.application.dao.ContractDao;
import dk.jyskit.salescloud.application.extensions.MobileProductRelationTypeProvider;
import dk.jyskit.salescloud.application.model.Amounts;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.BusinessAreas;
import dk.jyskit.salescloud.application.model.Campaign;
import dk.jyskit.salescloud.application.model.Contract;
import dk.jyskit.salescloud.application.model.FeatureType;
import dk.jyskit.salescloud.application.model.MobileCampaign;
import dk.jyskit.salescloud.application.model.MobileProduct;
import dk.jyskit.salescloud.application.model.MobileProductGroupEnum;
import dk.jyskit.salescloud.application.model.OrderLine;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.salescloud.application.model.SystemUpdate;
import dk.jyskit.salescloud.application.pages.CorePageIds;
import dk.jyskit.salescloud.application.services.importdata.DataImporter;
import dk.jyskit.salescloud.application.services.importdata.ProductImportHandler;
import dk.jyskit.waf.application.model.EntityState;
import dk.jyskit.waf.utils.dataimport.DataImportException;
import dk.jyskit.waf.utils.guice.Lookup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FiberErhvervInitializer extends CopyInitializer {
	public FiberErhvervInitializer() {
		super("TDC Erhverv Fiber Initializer", BusinessAreas.FIBER_ERHVERV, "TDC Erhverv Fiber", "TDC Erhverv Fiber ...", BusinessAreas.FIBER);
	}
	
	@Override
	public void initBusinessArea() {
		super.initBusinessArea();
		
		businessArea.addFeature(FeatureType.FIBER_ERHVERV);
		businessArea.addFeature(FeatureType.OUTPUT_CDM);
		businessArea.addFeature(FeatureType.RECURRING_FEE_SPLIT);
		businessArea.addFeature(FeatureType.RABATAFTALE_CONTRACT_DISCOUNT);
		
		businessArea.setStandardDiscountMatrix("1300%,1400%,1600%,1700%,1900%,2000%#2500%,2800%,3100%,3400%,3700%,3900%#2700%,3000%,3300%,3600%,3900%,4200%");
	}
	
	@Override
	protected void addPages() {
		super.addPages();
		createPage(businessArea, CorePageIds.SALES_EXISTING_CONTRACTS, "Vælg kundesag", null, 
				"Velkommen til TDC Fiber Erhverv salgskonfigurator.\n\nKonfiguratoren skal hj\u00e6lpe dig til at lave det materiale, du skal bruge overfor dine kunder. Du kan danne et prisoverslag eller et egentligt tilbud, som kan bruges til at afstemme \u00f8konomi og l\u00f8sning med kunderne.\n\nDu kan altid f\u00e5 inspiration til din kundedialog ved at se n\u00e6rmere p\u00e5 TDC Perspektiv artiklerne som kan findes til h\u00f8jre.",
				"Du har to muligheder for at arbejde med kundesager:\n\n  * Du kan \u00e5bne en eksisterende kundesag ved at klikke \"V\u00e6lg\" til venstre\n\n  * Du kan oprette en ny salgssag ved at klikke p\u00e5 \"Ny kundesag\"\n\nDu kan med fordel arbejde med kategorier for dine kundesager for at skabe st\u00f8rre overblik. Anvend ikonet med den lille mand \u00f8verst i konfiguratoren til h\u00f8jre og klik p\u00e5 \"Kundesagskategorier\" i menuen.");
	}
	
	@Override
	protected void importProducts() {
		try {
			ProductImportHandler importHandler = new ProductImportHandler(businessArea);
			MobileSalescloudApplication.get().getInjector().injectMembers(importHandler);
			DataImporter dataImporter = new DataImporter(importHandler, getFileFromClasspath("import/init/excel/fiber_erhverv.xls"));
			dataImporter.getData();
		} catch (DataImportException e) {
			log.error("Failed to import excel file", e);
		}			
	}
	
	@Override
	public void initProducts(BusinessArea businessArea) {
		super.initProducts(businessArea);
		
		// Regel på side 2 pkt. 4:
		addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_1, "4142300", "5360700");
		addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_1, "4143000", "5360700");
		addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_1, "4143800", "5360700");
		addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_1, "4142300", "7622704");
		addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_1, "4143000", "7622704");
		addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_1, "4143800", "7622704");
	}

	protected void addProductRelationsToDefaultCampaign(Campaign campaign) {
		((MobileCampaign) campaign).setPrisaftaleTextMatrix(
				"13% rabat prisaftale nr. 14192,14% rabat prisaftale nr. 14193,16% rabat prisaftale nr. 14198,17% rabat prisaftale nr. 14199,19% rabat prisaftale nr. 14200,20% rabat prisaftale nr. 14201#" + 
				"25% rabat prisaftale nr. 14202,28% rabat prisaftale nr. 14203,31% rabat prisaftale nr. 14204,34% rabat prisaftale nr. 14205,37% rabat prisaftale nr. 14206,40% rabat prisaftale nr. 14207#" + 
				"27% rabat prisaftale nr. 14208,30% rabat prisaftale nr. 14209,33% rabat prisaftale nr. 14210,36% rabat prisaftale nr. 14211,39% rabat prisaftale nr. 14212,42% rabat prisaftale nr. 14213#");
	}
	
	protected void makeSystemUpdates(BusinessArea businessArea) {
		try {
			log.info("Checking for system updates");

			{
				String name = "Make inactive"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, BusinessAreas.FIBER_ERHVERV);
				if (update == null) {
					log.info("Update starting: '" + name + "' for businessarea FIBER_ERHVERV");
					
					List<BusinessArea> bas = businessAreaDao.findByField("name", "TDC Erhverv Fiber");
					if (bas.size() == 1) {
						BusinessArea ba = bas.get(0);
						ba.setEntityState(EntityState.INACTIVE);
						businessAreaDao.save(ba);
					}
					
					update = new SystemUpdate();
					update.setBusinessAreaId(BusinessAreas.FIBER_ERHVERV);
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}

			{
				String name = "Make active again"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, BusinessAreas.FIBER_ERHVERV);
				if (update == null) {
					log.info("Update starting: '" + name + "' for businessarea FIBER_ERHVERV");
					
					List<BusinessArea> bas = businessAreaDao.findByField("name", "TDC Erhverv Fiber");
					if (bas.size() == 1) {
						BusinessArea ba = bas.get(0);
						ba.setEntityState(EntityState.ACTIVE);
						businessAreaDao.save(ba);
					}
					
					update = new SystemUpdate();
					update.setBusinessAreaId(BusinessAreas.FIBER_ERHVERV);
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}

			{
				String name = "Product changes"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, BusinessAreas.FIBER_ERHVERV);
				if (update == null) {
					log.info("Update starting: '" + name + "' for businessarea " + BusinessAreas.FIBER_ERHVERV);

					try {
						ProductGroup group = createProductGroup(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_FIBER_NEW_BUNDLE_SPEED);
						for (String productId : new String[] {
								"6608301", 
								"6608302", 
								"6608303", 
								"6608304", 
								"6608305", 
								"6608307"
						}) {
							Product product = coreProductDao.findByProductGroupAndProductId(businessArea.getId(), group.getUniqueName(), productId);
							if (product == null) {
								log.warn("Product not found: " + productId);
							} else {
								Amounts amounts = product.getPrice();
								amounts.setOneTimeFee(0);
								product.setPrice(amounts);
								productDao.save(product);
							}
						}
					} catch (Exception e) {
						log.error("Some problem", e);
					}			
					
					update = new SystemUpdate();
					update.setBusinessAreaId(BusinessAreas.FIBER_ERHVERV);
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}
			
			{
				String name = "Add feature SHOW_CONTRACT_START_DATE"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, BusinessAreas.FIBER_ERHVERV);
				if (update == null) {
					log.info("Update starting: '" + name + "' for businessarea FIBER_ERHVERV");
					
					List<BusinessArea> bas = businessAreaDao.findByField("name", "TDC Erhverv Fiber");
					if (bas.size() == 1) {
						BusinessArea ba = bas.get(0);
						ba.addFeature(FeatureType.SHOW_CONTRACT_START_DATE);
						businessAreaDao.save(ba);
					}
					
					update = new SystemUpdate();
					update.setBusinessAreaId(BusinessAreas.FIBER_ERHVERV);
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}
			
			{
				String name = "Misc 1"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, BusinessAreas.FIBER_ERHVERV);
				if (update == null) {
					log.info("Update starting: '" + name + "' for businessarea FIBER_ERHVERV");
					
					String[] productIds = new String[] {"3243201", "0766198", "0675700"};
					for (String productId : productIds) {
						Product product = productDao.findByBusinessAreaAndProductId(businessArea.getId(), productId);
						
						if (product != null) {
							ContractDao cd = Lookup.lookup(ContractDao.class);
							
							long[] ids = new long[] {2647892, 2647894, 2647896, 2647898, 2647900, 2647902, 
									2647904, 2647906, 2647908, 2647910, 2648304, 2648337, 2648802, 2648901};
							for (long id : ids) {
								Contract contract = cd.findById(id);
								Iterator<OrderLine> iter = contract.getOrderLines().iterator();
								while (iter.hasNext()) {
									OrderLine orderLine = iter.next();
									if (Objects.equal(product, orderLine.getProduct())) {
										iter.remove();
									}
								}
								cd.save(contract);
							}

							productDao.delete(product);
						}
					}
					
					productIds = new String[] {"4401932", "0766296", "0011891", "0714700", "6931200", "0679200", "8256300", "8256400"};
					for (String productId : productIds) {
						MobileProduct product = (MobileProduct) productDao.findByBusinessAreaAndProductId(businessArea.getId(), productId);
						if (product != null) {
							product.setExcludeFromOffer(true);
							productDao.save(product);
						}
					}
					
					productIds = new String[] {"8256300", "8256400", "8256500", "8256600", "8256700", "8256800"};
					for (String productId : productIds) {
						Product product = productDao.findByBusinessAreaAndProductId(businessArea.getId(), productId);
						if (product != null) {
							product.setPublicName(product.getInternalName());
							productDao.save(product);
						}
					}
					
					update = new SystemUpdate();
					update.setBusinessAreaId(BusinessAreas.FIBER_ERHVERV);
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}
			
			{
				String name = "Misc 2"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, BusinessAreas.FIBER_ERHVERV);
				if (update == null) {
					log.info("Update starting: '" + name + "' for businessarea FIBER_ERHVERV");

					{
						MobileProduct product = (MobileProduct) productDao.findByBusinessAreaAndProductId(businessArea.getId(), "3242201");
						product.setPublicName("Redundans mobil 10GB");
						productDao.save(product);
					}
					{
						MobileProduct product = (MobileProduct) productDao.findByBusinessAreaAndProductId(businessArea.getId(), "3242202");
						product.setPublicName("Redundans mobil ekstra 30GB ");
						productDao.save(product);
					}
					{
						MobileProduct product = (MobileProduct) productDao.findByBusinessAreaAndProductId(businessArea.getId(), "3242203");
						product.setPublicName("Redundans mobil ekstra 200GB");
						productDao.save(product);
					}
					
					update = new SystemUpdate();
					update.setBusinessAreaId(BusinessAreas.FIBER_ERHVERV);
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}
			
			{
				String name = "Misc 3"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, BusinessAreas.FIBER_ERHVERV);
				if (update == null) {
					log.info("Update starting: '" + name + "' for businessarea FIBER_ERHVERV");

					try {
						MobileProduct product = (MobileProduct) productDao.findByBusinessAreaAndProductId(businessArea.getId(), "8962900");
						product.setProductId("8962800");
						productDao.save(product);
					} catch (Exception ex) {
						// nop.
					}
					
					update = new SystemUpdate();
					update.setBusinessAreaId(BusinessAreas.FIBER_ERHVERV);
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}

//			{
//				String name = "Infrastruktur"; // Don't change this name!
//				SystemUpdate update = systemUpdateDao.findByName(name, BusinessAreas.FIBER_ERHVERV);
//				if (update == null) {
//					log.info("Update starting: '" + name + "' for businessarea " + BusinessAreas.FIBER_ERHVERV);
//
//					ProductGroup group = createProductGroup(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_FIBER_NEW_BUNDLE_INFRASTRUCTURE);
//					Product product = coreProductDao.findByProductGroupAndProductId(businessArea.getId(), group.getUniqueName(), MobileProduct.PRODUCT_EXTRA_PREFIX + "1");
//					if (product == null) {
//						addProductWithProductId(extraGroup, MobileProduct.PRODUCT_EXTRA_PREFIX + "1", "", "", 0, 0, 0, false, false, false, true, 1, 1);
//						addProductWithProductId(extraGroup, MobileProduct.PRODUCT_EXTRA_PREFIX + "2", "", "", 0, 0, 0, false, false, false, true, 1, 1);
//						addProductWithProductId(extraGroup, MobileProduct.PRODUCT_EXTRA_PREFIX + "3", "", "", 0, 0, 0, false, false, false, true, 1, 1);
//					}
//
//					update = new SystemUpdate();
//					update.setBusinessAreaId(BusinessAreas.FIBER_ERHVERV);
//					update.setName(name);
//					systemUpdateDao.save(update);
//				}
//			}

			log.info("Done making system updates");
		} catch (Exception e) {
			log.error("Update failed!", e);
			throw e;
		}

	}
}
