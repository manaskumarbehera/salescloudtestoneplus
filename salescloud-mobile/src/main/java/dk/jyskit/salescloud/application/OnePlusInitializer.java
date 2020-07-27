package dk.jyskit.salescloud.application;

import dk.jyskit.salescloud.application.apis.user.UserApiClient;
import dk.jyskit.salescloud.application.dao.*;
import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.salescloud.application.pages.MobilePageIds;
import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.model.BaseUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Slf4j
public class OnePlusInitializer extends AbstractBusinessAreaInitializer {
	public OnePlusInitializer() {
		super("One+ initializer", BusinessAreas.ONE_PLUS, "TDC Erhverv One+", "TDC Erhverv One+...", "import/init/excel/one.xls");
	}

	protected void modifyPage(PageInfo page) {
		if (page.getPageId().equals(MobilePageIds.MOBILE_CONTRACT_SETTINGS)) {
			page.setTitle("Indstillinger");
		} else if (page.getPageId().equals(MobilePageIds.MOBILE_SWITCHBOARD)) {
			page.setTitle("Løsning");
		} else if (page.getPageId().equals(MobilePageIds.MOBILE_SOLUTION_ADDONS)) {
			page.setTitle("Løsning");
			page.setSubTitle("Pakke tilvalg");
		} else if (page.getPageId().equals(MobilePageIds.MOBILE_POOL_ADDONS)) {
			page.setTitle("Løsning");
			page.setSubTitle("Pulje tilvalg");
		} else if (page.getPageId().equals(MobilePageIds.MOBILE_SWITCHBOARD_TYPE)) {
			page.setTitle("Løsning");
			page.setSubTitle("Virksomhedspakke");
		} else if (page.getPageId().equals(MobilePageIds.MOBILE_STANDARD_BUNDLES)) {
			page.setTitle("Brugere");
		} else if (page.getPageId().equals(MobilePageIds.MOBILE_XDSL_BUNDLES)) {
			page.setTitle("Lokationer");
		}
	}

	protected void modifyListOfPageIds(List<String> pageIds) {
		pageIds.remove(MobilePageIds.MOBILE_SWITCHBOARD_ADDONS);
	}

	protected void makeSystemUpdates(BusinessArea businessArea) {
		try {
			log.info("Checking for system updates for business area " + businessArea.getName());

			// ===============================
			{
				String name = "Add features 1"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
				if (update == null) {
					log.info("Update starting: " + name);
					
					// -------------------------------
					// The following features create groups, so they must be handled first
					configureFeature(businessArea, FeatureType.MOBILE_BUNDLES_STANDARD);
					configureFeature(businessArea, FeatureType.USER_PROFILES);
					configureFeature(businessArea, FeatureType.SWITCHBOARD);
					configureFeature(businessArea, FeatureType.BUSINESS_FEATURES);
					configureFeature(businessArea, FeatureType.XDSL);
					configureFeature(businessArea, FeatureType.FIBER_ERHVERV);
					configureFeature(businessArea, FeatureType.FIBER);
					configureFeature(businessArea, FeatureType.PARTNER_SETTINGS);
//					configureFeature(businessArea, FeatureType.HARDWARE_BUNDLES);
					configureFeature(businessArea, FeatureType.LOCATIONS);

					// The following features do not create groups - they are more like "marker" features
					configureFeature(businessArea, FeatureType.OUTPUT_CDM);
					// configureFeature(businessArea, FeatureType.OUTPUT_AUTHORITY);	// Fuldmagter
					//configureFeature(businessArea, FeatureType.OUTPUT_PROCESS);		// 4P
					configureFeature(businessArea, FeatureType.OUTPUT_PARTNER_SUPPORT);
					configureFeature(businessArea, FeatureType.RECURRING_FEE_SPLIT);
//					configureFeature(businessArea, FeatureType.SHOW_INSTALLATION_DATE);
					configureFeature(businessArea, FeatureType.SHOW_CONTRACT_START_DATE);
					configureFeature(businessArea, FeatureType.RABATAFTALE_CONTRACT_DISCOUNT);
					configureFeature(businessArea, FeatureType.RABATAFTALE_CAMPAIGN_DISCOUNT);
					configureFeature(businessArea, FeatureType.CONTRACT_ACCEPT_REPORT);
					configureFeature(businessArea, FeatureType.POOLS);

					/* Add product relations, if necessary! */
					// -------------------------------

					initTestContracts(businessArea);

					log.info("Update done: " + name);
					update = new SystemUpdate();
					update.setBusinessAreaId(businessArea.getBusinessAreaId());
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}
			// ===============================
//			{
//				String name = "Add network matrix"; // Don't change this name!
//				SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
//				if (update == null) {
//					log.info("Update starting: " + name);
//
//					for (Campaign c: campaignDao.findAll()) {
//						MobileCampaign campaign = (MobileCampaign) c;
//						if (StringUtils.isEmpty(campaign.getPrisaftaleTextMatrix())) {
//							campaign.setPrisaftaleTextMatrix(
//									"13% rabat mobil prisaftale nr. 14536,14% rabat mobil prisaftale nr. 14574,16% rabat mobil prisaftale nr. 14575,17% rabat mobil prisaftale nr. 14576,19% rabat mobil prisaftale nr. 14577,20% rabat mobil prisaftale nr. 14578#25% rabat mobil prisaftale nr. 14579,28% rabat mobil prisaftale nr. 14580,31% rabat mobil prisaftale nr. 14581,34% rabat mobil prisaftale nr. 14582,37% rabat mobil prisaftale nr. 14583,40% rabat mobil prisaftale nr. 14584#27% rabat mobil prisaftale nr. 14585,30% rabat mobil prisaftale nr. 14586,33% rabat mobil prisaftale nr. 14587,36% rabat mobil prisaftale nr. 14588,39% rabat mobil prisaftale nr. 14589,42% rabat mobil prisaftale nr. 14590#");
//						}
//						if (StringUtils.isEmpty(campaign.getPrisaftaleTextMatrixNetwork())) {
//							campaign.setPrisaftaleTextMatrixNetwork(
//									"13% rabat netværk prisaftale nr. 14192,14% rabat netværk prisaftale nr. 14193,16% rabat netværk prisaftale nr. 14198,17% rabat netværk prisaftale nr. 14199,19% rabat netværk prisaftale nr. 14200,20% rabat netværk prisaftale nr. 14201#25% rabat netværk prisaftale nr. 14202,28% rabat netværk prisaftale nr. 14203,31% rabat netværk prisaftale nr. 14204,34% rabat netværk prisaftale nr. 14205,37% rabat netværk prisaftale nr. 14206,40% rabat netværk prisaftale nr. 14207#27% rabat netværk prisaftale nr. 14208,30% rabat netværk prisaftale nr. 14209,33% rabat netværk prisaftale nr. 14210,36% rabat netværk prisaftale nr. 14211,39% rabat netværk prisaftale nr. 14212,42% rabat netværk prisaftale nr. 14213#");
//						}
//						campaignDao.save(campaign);
//					}
//
//					log.info("Update done: " + name);
//					update = new SystemUpdate();
//					update.setBusinessAreaId(businessArea.getBusinessAreaId());
//					update.setName(name);
//					systemUpdateDao.save(update);
//				}
//			}
			// ===============================
			{
				String name = "Change EC prod names"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
				if (update == null) {
					log.info("Update starting: " + name);

					for (ProductGroup productGroup: businessArea.getProductGroupsAndChildren()) {
						for (Product product: productGroup.getProducts()) {
							if ("_Impl_Partne".equals(product.getProductId())) {
								product.setPublicName("TDC Erhvervscenter");
								ProductDao.lookup().save(product);
							}
						}
					}

					log.info("Update done: " + name);
					update = new SystemUpdate();
					update.setBusinessAreaId(businessArea.getBusinessAreaId());
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}
			// ===============================
			{
				String name = "Add non-pool to kontorbruger"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
				if (update == null) {
					log.info("Update starting: " + name);

					for (ProductGroup productGroup: businessArea.getProductGroupsAndChildren()) {
						for (Product product: productGroup.getProducts()) {
							if ("3233620".equals(product.getProductId())) {
								// Kontorbruger
								((MobileProduct) product).setNonPoolModeBundle(true);
								ProductDao.lookup().save(product);
							}
						}
					}

					log.info("Update done: " + name);
					update = new SystemUpdate();
					update.setBusinessAreaId(businessArea.getBusinessAreaId());
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}
			// ===============================
			{
				String name = "Add a single partner segment"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
				if (update == null) {
					log.info("Update starting: " + name);

					SegmentDao.lookup().save(new Segment("BS Maximum", 0));

					log.info("Update done: " + name);
					update = new SystemUpdate();
					update.setBusinessAreaId(businessArea.getBusinessAreaId());
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}
			// ===============================
			{
				String name = "Give access to one+"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
				if (update == null) {
					log.info("Update starting: " + name);

					setAccessCodes("jan@jyskit.dk", 0, "genforhandling,tem5_konfigurator,wifi_konfigurator,oneplus_konfigurator");
					setAccessCodes("thber@tdc.dk", 0, "genforhandling,tem5_konfigurator,wifi_konfigurator,oneplus_konfigurator");
					setAccessCodes("whe@tdc.dk", 0, "wifi_konfigurator,tem5_konfigurator,oneplus_konfigurator");
					setAccessCodes("mamou@tdc.dk", 0, "oneplus_konfigurator");
					setAccessCodes("giri@tdc.dk", 0, "oneplus_konfigurator");
					setAccessCodes("re@tdc.dk", 0, "oneplus_konfigurator");
					setAccessCodes("aldi@tdc.dk", 0, "tem5_konfigurator,oneplus_konfigurator");
					setAccessCodes("hhdf@tdc.dk", 0, "oneplus_konfigurator");
					setAccessCodes("heh@tdc.dk", 0, "oneplus_konfigurator");
					setAccessCodes("majuh@tdc.dk", 0, "oneplus_konfigurator");

					log.info("Update done: " + name);
					update = new SystemUpdate();
					update.setBusinessAreaId(businessArea.getBusinessAreaId());
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}
			// ===============================
			{
				String name = "Make iPhone bundle inactive"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
				if (update == null) {
					log.info("Update starting: " + name);

					for (Campaign campaign : businessArea.getCampaigns()) {
						for (ProductBundle productBundle : campaign.getProductBundles()) {
							MobileProductBundle b = (MobileProductBundle) productBundle;
							if ("26104".equals(b.getProductId())) {
								b.setActive(false);
							}
							MobileProductBundleDao.lookup().save(b);
						}
					}

					log.info("Update done: " + name);
					update = new SystemUpdate();
					update.setBusinessAreaId(businessArea.getBusinessAreaId());
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}
			// ===============================
			{
				String name = "Omdøb evt. impl_onsite"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
				if (update == null) {
					log.info("Update starting: " + name);

					for (ProductGroup productGroup: businessArea.getProductGroupsAndChildren()) {
						for (Product product: productGroup.getProducts()) {
							if ("_Impl_Onsite".equals(product.getProductId())) {
								if (!StringUtils.equalsIgnoreCase(product.getPublicName(), "Onsite + TDC Remote")) {
									((MobileProduct) product).setPublicName("Onsite + TDC Remote");
									((MobileProduct) product).setInternalName("Onsite + TDC Remote");
									ProductDao.lookup().save(product);
								}
								break;
							}
						}
					}

					log.info("Update done: " + name);
					update = new SystemUpdate();
					update.setBusinessAreaId(businessArea.getBusinessAreaId());
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}
			// ===============================
			{
				String name = "Jan's password"; // Don't change this name!
				SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
				if (update == null) {
					log.info("Update starting: " + name);

					List<BaseUser> users = userDao.findByUsername("janjysk");
					for (BaseUser user : users) {
						if (user.isAuthenticatedBy("Devguy")) {
							log.info("SAME PW!!!!????");
						} else {
							user.setPassword("Devguy");
							userDao.save(user);
						}
					}

					log.info("Update done: " + name);
					update = new SystemUpdate();
					update.setBusinessAreaId(businessArea.getBusinessAreaId());
					update.setName(name);
					systemUpdateDao.save(update);
				}
			}
			// ===============================

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
}

/*
mysql -u root -p
mysql -u root -p scprod < ~/scprod\ (49).sql
mysql -u root -p scprod < ~/scprod\ (52).sql

ALTER TABLE contract MODIFY VARIABLECATEGORIES VARCHAR(1024);
ALTER TABLE contract MODIFY FIBERBUNDLESJSON VARCHAR(1400);
ALTER TABLE contract ADD COLUMN FIBERBUNDLESPLUSJSON VARCHAR(1400) AFTER FIBERBUNDLESJSON;
ALTER TABLE contract ADD COLUMN LOCATIONBUNDLESJSON VARCHAR(1400) AFTER FIBERBUNDLESPLUSJSON;
UPDATE `baseuser` SET entity_state = 0 WHERE email = 'jan@jyskit.dk';

----
select id, name from businessarea;

delete from orderline where contract_id in (select id from contract where business_area_id = 19401);
delete from subscription_product where subscription_id in (select id from subscription where contract_id in (select id from contract where business_area_id = 19401));
delete from subscription where contract_id in (select id from contract where business_area_id = 19401);
delete from contract_campaign where contract_id in (select id from contract where business_area_id = 19401);
delete from discountscheme where contract_id in (select id from contract where business_area_id = 19401);
delete from contract_campaign where contract_id in (select id from contract where business_area_id = 19401);
delete from discountscheme where contract_id in (select id from contract where business_area_id = 19401);
delete from BUS_FEATURE where BUSINESS_AREA_ID = 19401;
delete from campaignproduct where campaignId in (select id from campaign where businessarea_id = 19401);
delete from bundleproduct where productBundleId in (select id from productbundle where campaign_id in (select id from campaign where businessarea_id = 19401));
delete from bundleproduct where productBundleId in (select id from productbundle where contract_id in (select id from contract where business_area_id = 19401));
delete from productbundle where campaign_id in (select id from campaign where businessarea_id = 19401);
delete from campaign where businessarea_id = 19401;
delete from pageinfo where businessarea_id = 19401;
delete from orderline where contract_id in (select id from contract where business_area_id = 19401);
delete from productbundle where campaign_id in (select id from campaign where businessarea_id = 19401);
delete from productbundle where contract_id in (select id from contract where business_area_id = 19401);
delete from orderline where product_id in (select id from product where businessarea_id = 19401);
delete from contract where business_area_id = 19401;
delete from product where businessarea_id = 19401;
delete from productgroup where parentProductGroup_id in (select id from (select p2.id from productgroup p2 where p2.businessarea_id = 19401 and parentproductgroup_id is not null) x);
delete from productgroup where parentProductGroup_id in (select id from (select p2.id from productgroup p2 where p2.businessarea_id = 19401) x);
delete from productgroup where businessarea_id = 19401;
delete from productrelation where businessarea_id = 19401;
delete from businessarea where id = 19401;

 */
