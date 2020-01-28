package dk.jyskit.salescloud.application;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import dk.jyskit.salescloud.application.dao.OrganisationDao;
import dk.jyskit.salescloud.application.model.AccessCodes;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.BusinessAreas;
import dk.jyskit.salescloud.application.model.FeatureType;
import dk.jyskit.salescloud.application.model.Organisation;
import dk.jyskit.salescloud.application.model.OrganisationType;
import dk.jyskit.salescloud.application.model.SystemUpdate;
import dk.jyskit.salescloud.application.pages.CorePageIds;
import dk.jyskit.salescloud.application.pages.MobilePageIds;
import dk.jyskit.salescloud.application.services.importdata.DataImporter;
import dk.jyskit.salescloud.application.services.importdata.ProductImportHandler;
import dk.jyskit.waf.utils.dataimport.DataImportException;
import dk.jyskit.waf.utils.guice.Lookup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WiFiInitializer extends CopyInitializer {
	public WiFiInitializer() {
		super("TDC WiFi Initializer", BusinessAreas.WIFI, "TDC Erhverv Wi-Fi Intelligence", "TDC WiFi...", BusinessAreas.SWITCHBOARD);
	}
	
	@Override
	public void initBusinessArea() {
		super.initBusinessArea();
		businessArea.addFeature(FeatureType.WIFI);
		businessArea.addFeature(FeatureType.OUTPUT_CDM);
		businessArea.addFeature(FeatureType.RECURRING_FEE_SPLIT);
	}
	
	@Override
	protected void addPages() {
		super.addPages();
		createPage(businessArea, CorePageIds.SALES_EXISTING_CONTRACTS, "Vælg kundesag", null, 
				"Velkommen til Wi-Fi salgskonfigurator.\n\nKonfiguratoren skal hj\u00e6lpe dig til at lave det materiale, du skal bruge overfor dine kunder. Du kan danne et prisoverslag eller et egentligt tilbud, som kan bruges til at afstemme \u00f8konomi og l\u00f8sning med kunderne.\n\nDu kan altid f\u00e5 inspiration til din kundedialog ved at se n\u00e6rmere p\u00e5 TDC Perspektiv artiklerne som kan findes til h\u00f8jre.",
				"Du har to muligheder for at arbejde med kundesager:\n\n  * Du kan \u00e5bne en eksisterende kundesag ved at klikke \"V\u00e6lg\" til venstre\n\n  * Du kan oprette en ny salgssag ved at klikke p\u00e5 \"Ny kundesag\"\n\nDu kan med fordel arbejde med kategorier for dine kundesager for at skabe st\u00f8rre overblik. Anvend ikonet med den lille mand \u00f8verst i konfiguratoren til h\u00f8jre og klik p\u00e5 \"Kundesagskategorier\" i menuen.");
	}
	
	protected void importProducts() {
		// Some products are copied from the switchboard business area
		try {
			ProductImportHandler importHandler = new ProductImportHandler(businessArea);
			MobileSalescloudApplication.get().getInjector().injectMembers(importHandler);
			// Without Scale fortællenr. (4400235, 0659600, 0659700, 0659800) :
			DataImporter dataImporter = new DataImporter(importHandler, getFileFromClasspath("import/init/excel/switchboard_copy_for_wifi.xls"));
			dataImporter.getData();
		} catch (DataImportException e) {
			log.error("Failed to import excel file", e);
		}
		
		// Products specific for this business area
		try {
			ProductImportHandler importHandler = new ProductImportHandler(businessArea);
			MobileSalescloudApplication.get().getInjector().injectMembers(importHandler);
			DataImporter dataImporter = new DataImporter(importHandler, getFileFromClasspath("import/init/excel/wifi.xls"));
			dataImporter.getData();
		} catch (DataImportException e) {
			log.error("Failed to import excel file", e);
		}	

		// Does not work with mysql ???
//		addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_1, "999 265 00", "44 019 81");
//		addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_1, "999 266 00", "44 019 89");
//		addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_1, "999 267 00", "44 019 89");
//		addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_1, "999 268 00", "44 019 89");
//		addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_1, "999 269 00", "44 019 97");
	}
	
	protected void makeSystemUpdates(BusinessArea businessArea) {
		try {
			log.info("Checking for system updates");
			
			{
				String name = "Give access to all partners"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
				if (update == null) {
					log.info("Update starting: " + name);
					
					OrganisationDao organisationDao = ((OrganisationDao) Lookup.lookup(OrganisationDao.class));
					List<Organisation> organisations = organisationDao.findAll();
					for (Organisation organisation : organisations) {
						if (OrganisationType.PARTNER_CENTER.equals(organisation.getType())) {
							if (StringUtils.isEmpty(organisation.getAccessCodes())) {
								organisation.setAccessCodes(AccessCodes.WIFI_CONFIGURATOR);
							} else {
								organisation.setAccessCodes(organisation.getAccessCodes() + "," + AccessCodes.WIFI_CONFIGURATOR);
							}
							organisationDao.save(organisation);
						}
					}
					
					log.info("Update done: " + name);
					update = new SystemUpdate();
					update.setBusinessAreaId(businessArea.getBusinessAreaId());
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}
			
//			{
//				String name = "New AP group"; // Don't change this name!
//				SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
//				if (update == null) {
//					log.info("Update starting: " + name);
//					
//					MobileProductGroup existingProductGroup = (MobileProductGroup) businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_INCLUDED.getKey());
//					MobileProductGroup parentProductGroup = (MobileProductGroup) existingProductGroup.getParentProductGroup();
//					MobileProductGroup productGroup = (MobileProductGroup) createProductGroup(parentProductGroup, MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_AP);
//					productGroup.setName("AP");
//					productGroup.setUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_LOCATION_AP.getKey());
//					productGroup.setSortIndex(parentProductGroup.getMaxSortIndexOfChildren() + 1);
//					productGroup.setOutputSortIndex(parentProductGroup.getMaxSortIndexOfChildren(true) + 1);
//					productGroup.setOfferSortIndex(parentProductGroup.getMaxSortIndexOfChildren(false) + 1);
//					productGroup.setHelpMarkdown("");
//					productGroup.setHelpHtml("");
//					parentProductGroup.addProductGroup(productGroup);
//					
//					ProductGroupDao productGroupDao = (ProductGroupDao) Lookup.lookup(ProductGroupDao.class);
//					MobileProductDao productDao = (MobileProductDao) Lookup.lookup(MobileProductDao.class);
//					
//					Iterator<Product> productIterator = existingProductGroup.getProducts().iterator();
//					while (productIterator.hasNext()) {
//						Product product = productIterator.next();
//						productGroup.addProduct(product);
//						productIterator.remove();
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
			log.error("Update failed!", e);
			throw e;
		}
	}
}
