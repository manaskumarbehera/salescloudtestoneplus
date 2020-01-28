package dk.jyskit.salescloud.application;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import dk.jyskit.salescloud.application.dao.BusinessAreaDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import dk.jyskit.salescloud.application.dao.ContractDao;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.Amounts;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.BusinessAreas;
import dk.jyskit.salescloud.application.model.Contract;
import dk.jyskit.salescloud.application.model.FeatureType;
import dk.jyskit.salescloud.application.model.MobileProduct;
import dk.jyskit.salescloud.application.model.MobileProductGroupEnum;
import dk.jyskit.salescloud.application.model.Organisation;
import dk.jyskit.salescloud.application.model.OrganisationType;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.model.SystemUpdate;
import dk.jyskit.salescloud.application.pages.CorePageIds;
import dk.jyskit.salescloud.application.pages.MobilePageIds;
import dk.jyskit.salescloud.application.services.importdata.DataImporter;
import dk.jyskit.salescloud.application.services.importdata.ProductImportHandler;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.application.model.EntityState;
import dk.jyskit.waf.utils.dataimport.DataImportException;
import dk.jyskit.waf.utils.guice.Lookup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FiberErhvervPlusInitializer extends CopyInitializer {
	public FiberErhvervPlusInitializer() {
		super("TDC Erhverv Fiber Plus Initializer", BusinessAreas.FIBER, "TDC Erhverv Fiber Plus", "TDC Erhverv Fiber Plus ...", BusinessAreas.SWITCHBOARD);
	}
	
	@Override
	public void initBusinessArea() {
		super.initBusinessArea();
		
		businessArea.addFeature(FeatureType.FIBER);
		businessArea.addFeature(FeatureType.OUTPUT_CDM);
		businessArea.addFeature(FeatureType.RECURRING_FEE_SPLIT);
	}
	
	@Override
	protected void addPages() {
		super.addPages();
		createPage(businessArea, CorePageIds.SALES_EXISTING_CONTRACTS, "Vælg kundesag", null, 
				"Velkommen til TDC Fiber salgskonfigurator.\n\nKonfiguratoren skal hj\u00e6lpe dig til at lave det materiale, du skal bruge overfor dine kunder. Du kan danne et prisoverslag eller et egentligt tilbud, som kan bruges til at afstemme \u00f8konomi og l\u00f8sning med kunderne.\n\nDu kan altid f\u00e5 inspiration til din kundedialog ved at se n\u00e6rmere p\u00e5 TDC Perspektiv artiklerne som kan findes til h\u00f8jre.",
				"Du har to muligheder for at arbejde med kundesager:\n\n  * Du kan \u00e5bne en eksisterende kundesag ved at klikke \"V\u00e6lg\" til venstre\n\n  * Du kan oprette en ny salgssag ved at klikke p\u00e5 \"Ny kundesag\"\n\nDu kan med fordel arbejde med kategorier for dine kundesager for at skabe st\u00f8rre overblik. Anvend ikonet med den lille mand \u00f8verst i konfiguratoren til h\u00f8jre og klik p\u00e5 \"Kundesagskategorier\" i menuen.");
//		createPage(businessArea, MobilePageIds.MOBILE_CONTRACT_SETTINGS, "Kampagne", null, 
//				"Inden selve l\u00f8sningen, skal vi vide lidt om kunden og det pris-setup vi skal arbejde videre med.\n\nAnvend nedenst\u00e5ende sp\u00f8rgsm\u00e5l for at afd\u00e6kke kundens behov. Find evt. hj\u00e6lp til de enkelte emner i kassen til h\u00f8jre.\n\n**NB: Det er vigtigt du anvender mark\u00f8rerne ved sp\u00f8rgsm\u00e5lene, da vi anvender input aktivt for at blive klogere p\u00e5 vores kunder.**", 
//				"**Kampagne:**\n\nAktive kampagner vil i kampagneperidoen kunne v\u00e6lges i drop down.\n\nOBS:\n\n\n  * To eller flere kampagner kan ikke kombineres\n\n  * V\u00e6lges kampagne er det udelukkende de i kampagnen indeholdte abonnementer som pr\u00e6senteres i konfiguratoren\n\n**Hj\u00e6lp til behovsafd\u00e6kning:**\n\nKunden er meget afh\u00E6ngig af sin telefon:\n\n  * Vores erfaringer viser, at 20 % af alle kald aldrig bliver svaret. Derfor er en omstillingsl\u00F8sning relevant\n\nKundens hovednummer er meget velkendt:\n\n  * Som en del af l\u00F8sningen i TDC kan du beholde dit velkendte hovednummer \n  * Du kan tilk\u00F8be flere hovednumre, hvis du har behov for flere kundeindgange\n\nDet er vigtigt at kunden oplever god service:\n\n  * K\u00F8 og derfor ventetid er ikke optimal service, hvorfor det er vigtigt, at du f\u00E5r sat k\u00F8grupper og telefonnumre rigtigt op. Derfor f\u00E5r du hj\u00E6lp til installation i TDC\n  * Den bedste service opleves gennem ved en menneskelig relation. Derfor anbefaler TDC et omstillingsbord hvor en medarbejder kan h\u00E5ndtere kald\n\nDet giver meget v\u00E6rdi at kald besvares personligt\n\n  * TDC har et af markedets bedste omstillingsborde. Du kan se status, b\u00E5de kalender og telefon, p\u00E5 alle medarbejdere samt automatisk se opkaldshistorik p\u00E5 indkommende kald fra kunder\n\nDet betyder meget, at der altid kan stilles videre til kollegaer\n\n  * De enkelte brugere kan stille aktive kald videre til kollegaer direkte p\u00E5 mobiltelefonen");
//		createPage(businessArea, MobilePageIds.MOBILE_FIBER_BUNDLES, "Pakker", null, "TODO ... ");
	}
	
	@Override
	protected void importProducts() {
//		// Some products are copied from the switchboard business area
//		try {
//			ProductImportHandler importHandler = new ProductImportHandler(businessArea);
//			MobileSalescloudApplication.get().getInjector().injectMembers(importHandler);
//			DataImporter dataImporter = new DataImporter(importHandler, getFileFromClasspath("import/init/excel/switchboard.xls"));
//			dataImporter.getData();
//		} catch (DataImportException e) {
//			log.error("Failed to import excel file", e);
//		}
		
		// Products specific for this business area
		try {
			ProductImportHandler importHandler = new ProductImportHandler(businessArea);
			MobileSalescloudApplication.get().getInjector().injectMembers(importHandler);
			DataImporter dataImporter = new DataImporter(importHandler, getFileFromClasspath("import/init/excel/fiber_erhverv_plus.xls"));
			dataImporter.getData();
		} catch (DataImportException e) {
			log.error("Failed to import excel file", e);
		}			
	}
	
	protected void makeSystemUpdates(BusinessArea businessArea) {
		try {
			log.info("Checking for system updates");
			
			{
				String name = "Fiber tilbudstekst"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, 0);
				if (update == null) {
					log.info("Update starting: '" + name + "' for businessarea 0");
					
					ContractDao contractDao = (ContractDao) Lookup.lookup(ContractDao.class);
					
					List<Contract> contracts = contractDao.findNewerThan(DateUtils.addDays(new Date(), -2));
					for (Contract contract : contracts) {
						if (BusinessAreas.FIBER == contract.getBusinessArea().getBusinessAreaId()) {
							log.info("Fixing contract " + contract.getId());
							contract.setOfferIntroText(
									"Tak for en behagelig samtale.\n\n" +
											"Som lovet sender jeg dig her et tilbud på TDC Erhverv Fiber Plus.\n\n" +
											"Dette tilbud er udarbejdet på baggrund af de oplysninger, som er modtaget fra jer og jeg håber, at tilbuddet matcher jeres behov for en professionel fiberløsning, tilpasset præcis jeres virksomhed.\n\n" +
											"Hvis du har ønsker til ændringer eller spørgsmål er du naturligvis velkommen til at kontakte mig.\n\n" +
									"Venlig hilsen");
							contractDao.save(contract);
						}
					}
					update = new SystemUpdate();
					update.setBusinessAreaId(0);
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}
			
			{
				String name = "Fiber - Extra products"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, BusinessAreas.FIBER);
				if (update == null) {
					log.info("Update starting: '" + name + "' for businessarea " + BusinessAreas.FIBER);
					
					ProductGroup extraGroup = createProductGroup(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_EXTRA);
					Product product = coreProductDao.findByProductGroupAndProductId(businessArea.getId(), extraGroup.getUniqueName(), MobileProduct.PRODUCT_EXTRA_PREFIX + "1");
					if (product == null) {
						addProductWithProductId(extraGroup, MobileProduct.PRODUCT_EXTRA_PREFIX + "1", "", "", 0, 0, 0, false, false, false, true, 1, 1);
						addProductWithProductId(extraGroup, MobileProduct.PRODUCT_EXTRA_PREFIX + "2", "", "", 0, 0, 0, false, false, false, true, 1, 1);
						addProductWithProductId(extraGroup, MobileProduct.PRODUCT_EXTRA_PREFIX + "3", "", "", 0, 0, 0, false, false, false, true, 1, 1);
					}

					update = new SystemUpdate();
					update.setBusinessAreaId(BusinessAreas.FIBER);
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}

			{
				String name = "Fiber modifikation"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, BusinessAreas.FIBER);
				if (update == null) {
					log.info("Update starting: '" + name + "' for businessarea " + BusinessAreas.FIBER);
					
					try {
						// Make a number of products inactive
						ProductGroup group = createProductGroup(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE_HARDWARE);
						for (String productId : new String[] {"4401702", "4401707", "6940007", "6940009"}) {
							Product product = coreProductDao.findByProductGroupAndProductId(businessArea.getId(), group.getUniqueName(), productId);
							if (product == null) {
								log.warn("Product not found: " + productId);
							} else {
								product.setEntityState(EntityState.INACTIVE);
								productDao.save(product);
							}
						}
						
						group = createProductGroup(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE_ITEMS);
						String productId = "4401718";
						Product product = coreProductDao.findByProductGroupAndProductId(businessArea.getId(), group.getUniqueName(), productId);
						if (product == null) {
							log.warn("Product not found: " + productId);
						} else {
							product.setPrice(new Amounts(95040, 0, 0));
							productDao.save(product);
						}
						
						// Add new products
						ProductImportHandler importHandler = new ProductImportHandler(businessArea);
						MobileSalescloudApplication.get().getInjector().injectMembers(importHandler);
						DataImporter dataImporter = new DataImporter(importHandler, getFileFromClasspath("import/init/excel/fiber1-new.xls"));
						dataImporter.getData();
					} catch (Exception e) {
						log.error("Some problem", e);
					}			

					update = new SystemUpdate();
					update.setBusinessAreaId(BusinessAreas.FIBER);
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}

			{
				String name = "Fiber modifikation 2"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, BusinessAreas.FIBER);
				if (update == null) {
					log.info("Update starting: '" + name + "' for businessarea " + BusinessAreas.FIBER);
					
					try {
						// Some products are now always included
						ProductGroup group = createProductGroup(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE_ITEMS);
						for (String productId : new String[] {
								"0760200",		// Portpolicing 
								"4420099",		// Internet VLan
								"0762800"		// VLan policing
								}) {
							Product product = coreProductDao.findByProductGroupAndProductId(businessArea.getId(), group.getUniqueName(), productId);
							if (product == null) {
								log.warn("Product not found: " + productId);
							} else {
								product.setMinCount(1);
								product.setMaxCount(1);
								productDao.save(product);
							}
						}
					} catch (Exception e) {
						log.error("Some problem", e);
					}			

					update = new SystemUpdate();
					update.setBusinessAreaId(BusinessAreas.FIBER);
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}
			
			{
				String name = "Fiber modifikation 3"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, BusinessAreas.FIBER);
				if (update == null) {
					log.info("Update starting: '" + name + "' for businessarea " + BusinessAreas.FIBER);

					try {
						ProductGroup group = createProductGroup(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE_ITEMS);
						for (String productId : new String[] {
								"4401182"		// Datatekniker 
						}) {
							Product product = coreProductDao.findByProductGroupAndProductId(businessArea.getId(), group.getUniqueName(), productId);
							if (product == null) {
								log.warn("Product not found: " + productId);
							} else {
								product.setPrice(new Amounts(0, 0, 0));
								productDao.save(product);
							}
						}
					} catch (Exception e) {
						log.error("Some problem", e);
					}			
					
					try {
						ProductGroup group = createProductGroup(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE_ITEMS);
						for (String productId : new String[] {
								"0762800"		// VLan policing
								}) {
							Product product = coreProductDao.findByProductGroupAndProductId(businessArea.getId(), group.getUniqueName(), productId);
							if (product == null) {
								log.warn("Product not found: " + productId);
							} else {
								product.setMinCount(0);
								product.setMaxCount(0);
								productDao.save(product);
							}
						}
					} catch (Exception e) {
						log.error("Some problem", e);
					}
					
					update = new SystemUpdate();
					update.setBusinessAreaId(BusinessAreas.FIBER);
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}
			

//			{
//				String name = "Fiber2"; // Don't change this name!
//				SystemUpdate update = systemUpdateDao.findByName(name, BusinessAreas.FIBER);
//				if (update == null) {
//					log.info("Update starting: '" + name + "' for businessarea " + BusinessAreas.FIBER);
//					
//					try {
//						ProductImportHandler importHandler = new ProductImportHandler(businessArea);
//						MobileSalescloudApplication.get().getInjector().injectMembers(importHandler);
//						DataImporter dataImporter = new DataImporter(importHandler, getFileFromClasspath("import/init/excel/fiber2.xls"));
//						dataImporter.getData();
//					} catch (DataImportException e) {
//						log.error("Failed to import excel file", e);
//					}			
//
//					update = new SystemUpdate();
//					update.setBusinessAreaId(BusinessAreas.FIBER);
//					update.setName(name);
//					systemUpdateDao.save(update);
//				}
//			}

			{
				String name = "Fiber plus navn 2"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, 0);
				if (update == null) {
					log.info("Update starting: '" + name + "' for businessarea 0");

					List<BusinessArea> bas = businessAreaDao.findByField("name", "TDC Fiber");
					if (bas.size() == 1) {
						BusinessArea ba = bas.get(0);
						ba.setName("TDC Erhverv Fiber Plus");
						businessAreaDao.save(ba);
					}


					// Fiber erhverv plus:

					// 1. Skift navn - DONE

					// Fiber erhverv:

					// 2. 25/25 skal fjernes - DONE

					// 3. Kampagnekode i tastebilag (Prisaftale 2): FTTXQ2 (Q3, ...) - DONE

					// 4. Fiber fane: "Tlf. nr. til opringning 30 minutter før teknikerbesøg", med på tastebilag (lokation) - DONE

					update = new SystemUpdate();
					update.setBusinessAreaId(0);
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}

			{
				String name = "CPE accesspunkt"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, BusinessAreas.FIBER);
				if (update == null) {
					log.info("Update starting: '" + name + "' for businessarea " +  BusinessAreas.FIBER);

					try {
						{
							String existingProductId = "4401182";
							MobileProduct product = (MobileProduct) coreProductDao.findByBusinessAreaAndProductId(businessArea.getId(), existingProductId);
							if (product == null) {
								log.warn("Product not found: " + existingProductId);
							} else {
								MobileProduct p = product.clone();
								p.setProductId("4401714");
								p.setPublicName("Onsite datatekniker efter regning");
								p.setInternalName("Onsite datatekniker efter regning");
								p.setPrice(new Amounts(0, 0, 0));
								productDao.save(p);
							}
						}
						{
							String existingProductId = "6940009";
							MobileProduct product = (MobileProduct) coreProductDao.findByBusinessAreaAndProductId(businessArea.getId(), existingProductId);
							if (product == null) {
								log.warn("Product not found: " + existingProductId);
							} else {
								MobileProduct p = product.clone();
								p.setEntityState(EntityState.ACTIVE);
								p.setProductId("6940024");
								p.setPublicName("Cisco Meraki MR33 accesspunkt");
								p.setInternalName("Cisco Meraki MR33 accesspunkt");
								p.setPrice(new Amounts(150000, 0, 12500));
								productDao.save(p);
							}
						}
						{
							String existingProductId = "6940009";
							MobileProduct product = (MobileProduct) coreProductDao.findByBusinessAreaAndProductId(businessArea.getId(), existingProductId);
							if (product == null) {
								log.warn("Product not found: " + existingProductId);
							} else {
								MobileProduct p = product.clone();
								p.setEntityState(EntityState.ACTIVE);
								p.setProductId("6940061");
								p.setPublicName("Strømforsyning til Cisco Meraki MR33 accesspunkt");
								p.setInternalName("Strømforsyning til Cisco Meraki MR33 accesspunkt");
								p.setPrice(new Amounts(15000, 0, 0));
								productDao.save(p);
							}
						}
						{
							String existingProductId = "4401182";
							MobileProduct product = (MobileProduct) coreProductDao.findByBusinessAreaAndProductId(businessArea.getId(), existingProductId);
							if (product == null) {
								log.warn("Product not found: " + existingProductId);
							} else {
								MobileProduct p = product.clone();
								p.setProductId("4401182");
								p.setPublicName("Onsite datatekniker efter regning");
								p.setInternalName("Onsite datatekniker efter regning");
								p.setPrice(new Amounts(0, 0, 0));
								productDao.save(p);
							}
						}
					} catch (Exception e) {
						log.error("Some problem", e);
					}

					update = new SystemUpdate();
					update.setBusinessAreaId(BusinessAreas.FIBER);
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}

			{
				String name = "CPE accesspunkt 2"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, BusinessAreas.FIBER);
				if (update == null) {
					log.info("Update starting: '" + name + "' for businessarea " +  BusinessAreas.FIBER);

					try {
						{
							String existingProductId = "4401932";
							MobileProduct product = (MobileProduct) coreProductDao.findByBusinessAreaAndProductId(businessArea.getId(), existingProductId);
							if (product == null) {
								log.warn("Product not found: " + existingProductId);
							} else {
								MobileProduct p = product.clone();
								p.setProductId("4401721");
								p.setPublicName("Trådløs Internet");
								p.setInternalName("Trådløs Internet");
								p.setDefaultCount(0);
								p.setMinCount(0);
								p.setMaxCount(0);
								p.setPrice(new Amounts(0, 0, 0));
								productDao.save(p);
							}
						}
						{
							String existingProductId = "4401932";
							MobileProduct product = (MobileProduct) coreProductDao.findByBusinessAreaAndProductId(businessArea.getId(), existingProductId);
							if (product == null) {
								log.warn("Product not found: " + existingProductId);
							} else {
								MobileProduct p = product.clone();
								p.setProductId("4401722");
								p.setPublicName("Trådløs Internet gæstenetværk");
								p.setInternalName("Trådløs Internet gæstenetværk");
								p.setDefaultCount(0);
								p.setMinCount(0);
								p.setMaxCount(0);
								p.setPrice(new Amounts(0, 0, 0));
								productDao.save(p);
							}
						}
					} catch (Exception e) {
						log.error("Some problem", e);
					}

					update = new SystemUpdate();
					update.setBusinessAreaId(BusinessAreas.FIBER);
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}

			log.info("Done making system updates");
		} catch (Exception e) {
			log.error("Update failed!", e);
			throw e;
		}
	}
}
