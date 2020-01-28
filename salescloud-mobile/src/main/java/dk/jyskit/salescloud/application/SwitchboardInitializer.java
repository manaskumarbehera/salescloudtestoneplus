package dk.jyskit.salescloud.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.github.rjeschke.txtmark.Processor;
import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.BusinessAreaDao;
import dk.jyskit.salescloud.application.dao.CampaignDao;
import dk.jyskit.salescloud.application.dao.ContractDao;
import dk.jyskit.salescloud.application.dao.MobileProductDao;
import dk.jyskit.salescloud.application.dao.OrderLineDao;
import dk.jyskit.salescloud.application.dao.ProductBundleDao;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.dao.ProductGroupDao;
import dk.jyskit.salescloud.application.dao.ProductRelationDao;
import dk.jyskit.salescloud.application.dao.SegmentDao;
import dk.jyskit.salescloud.application.dao.SubscriptionDao;
import dk.jyskit.salescloud.application.dao.SystemUpdateDao;
import dk.jyskit.salescloud.application.extensionpoints.defaultimpl.CoreProductRelationTypeProvider;
import dk.jyskit.salescloud.application.extensions.MobileObjectFactory;
import dk.jyskit.salescloud.application.extensions.MobileProductRelationTypeProvider;
import dk.jyskit.salescloud.application.model.Amounts;
import dk.jyskit.salescloud.application.model.BundleProductRelation;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.BusinessAreas;
import dk.jyskit.salescloud.application.model.Campaign;
import dk.jyskit.salescloud.application.model.CampaignProductRelation;
import dk.jyskit.salescloud.application.model.Contract;
import dk.jyskit.salescloud.application.model.FeatureType;
import dk.jyskit.salescloud.application.model.FeeCategory;
import dk.jyskit.salescloud.application.model.MobileContract;
import dk.jyskit.salescloud.application.model.MobileProduct;
import dk.jyskit.salescloud.application.model.MobileProductBundle;
import dk.jyskit.salescloud.application.model.MobileProductBundleEnum;
import dk.jyskit.salescloud.application.model.MobileProductGroupEnum;
import dk.jyskit.salescloud.application.model.OrderLine;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.ProductBundle;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.salescloud.application.model.ProductRelation;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.model.Segment;
import dk.jyskit.salescloud.application.model.Subscription;
import dk.jyskit.salescloud.application.model.SystemUpdate;
import dk.jyskit.salescloud.application.pages.CorePageIds;
import dk.jyskit.salescloud.application.pages.MobilePageIds;
import dk.jyskit.salescloud.application.pages.bundles.BundleCount;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaver;
import dk.jyskit.waf.application.Environment;
import dk.jyskit.waf.application.dao.RoleDao;
import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.application.model.EntityState;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SwitchboardInitializer extends BusinessAreaInitializer {

	public static final String XDSL_SPEED_1 = "052100000";
	public static final String XDSL_SPEED_2 = "052100100";
	public static final String XDSL_SPEED_3 = "052100200";
	public static final String XDSL_SPEED_4 = "052100300";
	public static final String XDSL_SPEED_5 = "052101400";
	public static final String XDSL_SPEED_6 = "052102900";
	
	public static final String BUNDLE_OMSTILLING = "Omstilling";
	public static final String BUNDLE_OMSTILLING_EKSTRA = "Omstilling Ekstra";
	public static final String PRODUCT_OMSTILLINGSBORD = "_omst_OMSTILLINGSBORD";
	
	@Inject private BusinessAreaDao businessAreaDao;
	@Inject private CampaignDao campaignDao;
	@Inject private ContractDao contractDao;
	@Inject private ContractSaver contractSaver;
	@Inject private UserDao userDao;
	@Inject private RoleDao roleDao;
	@Inject private OrderLineDao orderLineDao;
	@Inject private MobileProductDao mobileProductDao;
	@Inject private ProductDao coreProductDao;
	@Inject private SubscriptionDao subscriptionDao;
	@Inject private MobileObjectFactory objectFactory;
	@Inject private ProductBundleDao productBundleDao;
	@Inject private ProductGroupDao productGroupDao;
	@Inject private ProductRelationDao productRelationDao;
	@Inject private SystemUpdateDao systemUpdateDao;
	@Inject private SegmentDao segmentDao;
	private boolean createBusinessArea;
	private ProductGroup omstillingGroup;
	private ProductGroup omstillingTilvalgGroup;
	private ProductGroup mobilPakkeGroup;
	private ProductGroup mobilPakkeTaleGroup;
	private ProductGroup mobilPakkeDataGroup;
	private ProductGroup mobilPakkeTilvalgGroup;
	private ProductGroup mobilPakkeUdlandGroup;
	private ProductGroup mobilMixGroup;
	private ProductGroup mobilMixTaleGroup;
	private ProductGroup mobilMixTaleTidGroup;
	private ProductGroup mobilMixDataGroup;
	private ProductGroup mobilMixDataAmountGroup;
	private ProductGroup mobilMixTilvalgGroup;
	private ProductGroup mobilMixIncludedGroup;
	private ProductGroup andreTilvalgGroup;
	private ProductGroup roamingGroup;
	private ProductGroup productionGroup;
	private ProductGroup productionCdmGroup;
	private ProductGroup extraGroup;
	
	public SwitchboardInitializer(boolean createBusinessArea) {
		super("Switchboard Initializer", BusinessAreas.SWITCHBOARD, "TDC Erhverv Omstilling", "TDC Erhverv Omstilling er løsningen der giver både virksomhedspakker med omstilling og abonnementer til brugerne. Virksomhedspakkerne bidrager til at telefonen altid bliver taget, at man kan stille om til den rette og at alle er tilgængelige.");
		this.createBusinessArea = createBusinessArea;
	}
	
	@Override
	public boolean needsInitialization() {
		if (createBusinessArea) {
			return super.needsInitialization();
		} else {
			for (BusinessArea businessArea : businessAreaDao.findAll()) {
				if (businessAreaId == businessArea.getBusinessAreaId()) {
					this.businessArea = businessArea; 
					deleteBusinessArea(businessArea);
					return true;
				}
			}
			return true;
		}
	}
	
	private void makeSystemUpdates(BusinessArea businessArea) {
		if (!createBusinessArea || (!Environment.isOneOf("dev"))) {
			try {
				log.info("Checking for system updates");
				
				{
					String name = "Features 1"; // Don't change this name!
					SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
					if ((update == null) || (Environment.isOneOf("dev"))) {
						log.info("Update starting: '" + name + "' for businessarea " + businessArea.getName());
						
						addFeatures();

						businessAreaDao.save(businessArea);
						
						log.info("Update done: " + name);
						update = new SystemUpdate();
						update.setBusinessAreaId(businessArea.getBusinessAreaId());
						update.setName(name);
						systemUpdateDao.save(update);
					}
				}

				
				{
					String name = "XDSL Bundles 1"; // Don't change this name!
					SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
					if ((update == null) || (Environment.isOneOf("dev"))) {
						log.info("Update starting: " + name);

						createPage(businessArea, MobilePageIds.MOBILE_XDSL_BUNDLES, "xDSL", null, 
								"Ved angivelse af diverse partner relaterede indstillinger for partner tekniker, support ydelser, udstyr og kundesegment, er det muligt at konfigurere "
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
						
						ProductGroup adslGroup = createProductGroup(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE);
						productGroupDao.save(adslGroup);
						
						{
							ProductGroup group = createProductGroup(adslGroup, MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_SPEED);
							
							int sortIndex = 0;
	  						addProductWithProductId(group, XDSL_SPEED_1, "xDSL 10/1", 	"TDC BizBase L ( 10/1 Mbit)", 0, 29900, 0, false, false, true, true, 0, 0).setSortIndex(++sortIndex);
	  						addProductWithProductId(group, XDSL_SPEED_2, "xDSL 20/1", 	"TDC BizBase L ( 20/2 Mbit)", 0, 29900, 0, false, false, true, true, 0, 0).setSortIndex(++sortIndex);
	  						addProductWithProductId(group, XDSL_SPEED_3, "xDSL 30/5", 	"TDC BizBase L (30/5 Mbit)", 0, 29900, 0, false, false, true, true, 0, 0).setSortIndex(++sortIndex);
	  						addProductWithProductId(group, XDSL_SPEED_4, "xDSL 50/10", 	"TDC BizBase L (50/10 Mbit)", 0, 29900, 0, false, false, true, true, 0, 0).setSortIndex(++sortIndex);
	  						addProductWithProductId(group, XDSL_SPEED_5, "xDSL 40/5",  	"TDC BizBase (40/5 Mbit)", 0, 29900, 0, false, false, true, true, 0, 0).setSortIndex(++sortIndex);
	  						addProductWithProductId(group, XDSL_SPEED_6, "xDSL 80/20", 	"TDC BizBase 80/20 Mbit", 0, 29900, 0, false, false, true, true, 0, 0).setSortIndex(++sortIndex);
	  						
//	 						addProductRelation(businessArea, "xDSL altid inkluderet", CoreProductRelationTypeProvider.ALWAYS_INCLUDED, product1.getProductId(), product2.getProductId());
//	 						addProductRelation(businessArea, "xDSL hastighed", CoreProductRelationTypeProvider.ALTERNATIVE_PRODUCTS, XDSL_SPEED_1, XDSL_SPEED_2, XDSL_SPEED_3, XDSL_SPEED_4, XDSL_SPEED_5, XDSL_SPEED_6);
						}
						
//						{
//							ProductGroup group = createProductGroup(adslGroup, MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_MANAGED_DEVICES);
//
//							int sortIndex = 0;
//							MobileProduct product = addProductWithProductId(group, "999999999", "TDC Router med 3G/4G backup", "BGW Router med 3G/4G backup", 0, 0, 0, false, false, true, true, 0, 0);
//							product.setSortIndex(++sortIndex);
//							product.setFlags("read_only, default_on");
//
//							product = addProductWithProductId(group, "999999998", "Firewall aktiveres", "Firewall konfig 3", 0, 0, 0, false, false, true, true, 0, 0);
//							product.setSortIndex(++sortIndex);
//							product.setFlags("read_only, default_on");
//
//							product = addProductWithProductId(group, "999999997", "Routerens WIFI aktiveres", "Wifi opsat", 0, 0, 0, false, false, true, true, 0, 0);
//							product.setSortIndex(++sortIndex);
//							product.setFlags("read_only, default_on");
//						}
//
//						{
//							ProductGroup group = createProductGroup(adslGroup, MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_CUSTOMER_DEVICES);
//
//							int sortIndex = 0;
//							MobileProduct product = addProductWithProductId(group, "999999996", "TDC homebox med tale", "Homebox", 0, 0, 0, false, false, true, true, 0, 0);
//							product.setSortIndex(++sortIndex);
//							product.setFlags("read_only, default_on");
//
//							product = addProductWithProductId(group, "999999995", "Fast IP adresse", "Fast DHCP tildelt IP adresse", 40000, 5000, 0, false, false, true, true, 0, 0);
//	  						product.setSortIndex(++sortIndex);
//	  						product.setFlags("read_only, default_on");
//						}
						
						{
							ProductGroup group = createProductGroup(adslGroup, MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_INCLUDED);
							
							int sortIndex = 0;
	  						addProductWithProductId(group, "031210000", "Fejlretning hele døgnet - internet", "Service alle dage 00-24 xDSL:", 0, 4000, 0, false, false, true, true, 0, 0).setSortIndex(++sortIndex);
	  						addProductWithProductId(group, "031000000", "Fejlretning hele døgnet - linien", "Service alle dage 00-24 linje:", 0, 4000, 0, false, false, true, true, 0, 0).setSortIndex(++sortIndex);
	  						addProductWithProductId(group, "660050000", "Proaktiv Router overvågning", "Overvågning", 0, 3000, 0, false, false, true, true, 0, 0).setSortIndex(++sortIndex);
	  						addProductWithProductId(group, "414300000", "5 talekanaler", "TDC QOS ekstrakanal 576/576", 0, 2000, 0, false, false, true, true, 0, 0).setSortIndex(++sortIndex);
	  						addProductWithProductId(group, "831990000", "Bærelinie", "Basislinje", 0, 2000, 0, false, false, true, true, 0, 0).setSortIndex(++sortIndex);
	  						addProductWithProductId(group, "824960000", "xDSL installation Onsite", "Godt i Gang", 0, 0, 99600, false, false, true, true, 0, 0).setSortIndex(++sortIndex);
	  						addProductWithProductId(group, "999999991", "Tastes med prisaftale xx", "Prisaftale nr. xxxx", 0, 0, 0, false, false, true, true, 0, 0).setSortIndex(++sortIndex);
						}
						
						productGroupDao.save(adslGroup);

						// XDSL pakker
						
						for (Campaign campaign: businessArea.getCampaigns()) {
							if ("Ingen kampagne".equals(campaign.getName())) {
								// Mobilpakker
								
								ProductGroup productGroup = businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_SPEED.getKey());
								for(Product product: productGroup.getProducts()) {
									MobileProductBundle xdslBundle = new MobileProductBundle();
									xdslBundle.setBundleType(MobileProductBundleEnum.XDSL_BUNDLE);
									xdslBundle.setPublicName(product.getPublicName());
									xdslBundle.setInternalName(product.getInternalName());
									xdslBundle.setSortIndex(product.getSortIndex());
									
									productBundleDao.save(xdslBundle);
									
									campaign.addProductBundle(xdslBundle);
									addProductToBundle(xdslBundle, product, true);
									
									for (Product includedProduct : businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_INCLUDED.getKey()).getProducts()) {
										addProductToBundle(xdslBundle, includedProduct, true);
									}
								}
								
								campaign = campaignDao.save(campaign);		// Bundles needs an ID
								break;
							}
						}
						
						businessAreaDao.save(businessArea);
						
						log.info("Update done: " + name);
						update = new SystemUpdate();
						update.setBusinessAreaId(businessArea.getBusinessAreaId());
						update.setName(name);
						systemUpdateDao.save(update);
					}
				}
				
				{
					String name = "Init of extra row for offer"; // Don't change this name!
					SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
					if ((update == null) || (Environment.isOneOf("dev"))) {
						log.info("Update starting: " + name);
						List<Campaign> campaigns = campaignDao.findAll();		
						for (Campaign campaign : campaigns) {
							List<ProductBundle> productBundles = campaign.getProductBundles();
							for (ProductBundle productBundle : productBundles) {
								((MobileProductBundle) productBundle).setExtraRowInOffer(((MobileProductBundle) productBundle).isExtraRowInOutput());
							}
							
							List<CampaignProductRelation> productRelations = campaign.getCampaignProducts();
							for (CampaignProductRelation productRelation : productRelations) {
								productRelation.setExtraRowInOffer(productRelation.isExtraRowInOutput());
							}
							campaignDao.save(campaign);
						}
						
						log.info("Update done: " + name);
						update = new SystemUpdate();
						update.setBusinessAreaId(businessArea.getBusinessAreaId());
						update.setName(name);
						systemUpdateDao.save(update);
					}
				}
				

				{
					String name = "Partner provision factors"; // Don't change this name!
					SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
					if ((update == null) || (Environment.isOneOf("dev"))) {
						log.info("Update starting: '" + name + "' for businessarea " + businessArea.getName());

						businessArea.setProvisionFactorGeneral(0.9f);
						businessArea.setProvisionFactorXDSL(1.0f);
						
						businessAreaDao.save(businessArea);
						
						log.info("Update done: " + name);
						update = new SystemUpdate();
						update.setBusinessAreaId(businessArea.getBusinessAreaId());
						update.setName(name);
						systemUpdateDao.save(update);
					}
				}

				
				{
					String name = "Partner elements 1"; // Don't change this name!
					SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
					if ((update == null) || (Environment.isOneOf("dev"))) {
						log.info("Update starting: '" + name + "' for businessarea " + businessArea.getName());
						
						createPage(businessArea, MobilePageIds.MOBILE_PARTNER_SETTINGS, "Partner ydelser", null, 
								"Ved angivelse af diverse partner relaterede indstillinger for partner tekniker, support ydelser, udstyr og kundesegment, er det muligt at konfigurere "
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
						
						ProductGroup partnerGroup = createProductGroup(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_PARTNER);
						productGroupDao.save(partnerGroup);
						
						ProductGroup group = createProductGroup(partnerGroup, MobileProductGroupEnum.PRODUCT_GROUP_PARTNER_INSTALLATION);
						group.setHelpMarkdown("**Hjælpetekst**  \n" +
								"\n" +
								"todo  \n");
						group.setHelpHtml(Processor.process(group.getHelpMarkdown()));
						
//						addProductWithProductId(group, "9800002", "Scale installation grundpakke", "Scale installation grundpakke", 0, 0, 10000, false, false, false, true, 0, 0);
//						addProductWithProductId(group, "9800003", "Scale installation avanceret", "Scale installation avanceret", 0, 0, 10000, false, false, false, true, 0, 0);
  						addProductWithProductId(group, "9800001", "Opstart og kørsel", "Opstart og kørsel", 0, 0, 80000, false, false, false, true, 0, 0);
  						addProductWithProductId(group, "9800002", "Godt i Gang, konfiguration af App, Job/Privat Zone, Statusvisning, Kalenderintegration, Communicator og Voicelogger", "Godt i Gang, konfiguration af App, Job/Privat Zone, Statusvisning, Kalenderintegration, Communicator og Voicelogger", 0, 0, 136600, false, false, false, true, 0, 0);
						addProductWithProductId(group, "9800003", "Installation Wallboard Basic", "Installation Wallboard Basic", 0, 0, 60000, false, false, false, true, 0, 0);
						addProductWithProductId(group, "9800004", "Installation Wallboard Supervisor", "Installation Wallboard Supervisor", 0, 0, 140000, false, false, false, true, 0, 0);
						addProductWithProductId(group, "9800005", "Installation supervisor agent", "Installation supervisor agent", 0, 0, 23100, false, false, false, true, 0, 0);
						addProductWithProductId(group, "9800006", "Hjælp til indspilning af besked", "Hjælp til indspilning af besked", 0, 0, 85400, false, false, false, true, 0, 0);
						addProductWithProductId(group, "9800007", "Kunde uddannelse på dagen", "Kunde uddannelse på dagen", 0, 0, 138700, false, false, false, true, 0, 0);
						addProductWithProductId(group, "9800008", "Kunde uddannelse 1-2 uger efter inst.", "Kunde uddannelse 1-2 uger efter inst.", 0, 0, 153600, false, false, false, true, 0, 0);
						MobileProduct p = addProductWithProductId(group, "9800009", "Installation - Diverse", "Installation - Diverse", 0, 0, 0, false, false, false, true, 0, 0);
						p.setVariableInstallationFee(true);
						p.setExcludeFromConfigurator(true);
						addProductWithProductId(group, "9800010", "Installation - Rabat", "Installation - Rabat", 0, 0, 0, false, false, false, true, 0, 0).setVariableInstallationFee(true);
						productGroupDao.save(group);
						
						/*

						< TDC Erhverv Omstilling,Øvrige installationsydelser,Opstart og kørsel,Opstart og kørsel,,9800001,0 kr.,100 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,
						> TDC Erhverv Omstilling,Øvrige installationsydelser,Opstart og kørsel,Opstart og kørsel,,9800001,0 kr.,800 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,

						< TDC Erhverv Omstilling,Øvrige installationsydelser,"",,9800002,0 kr.,100 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,
						> TDC Erhverv Omstilling,Øvrige installationsydelser,"",,9800002,0 kr.,1.366 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,

						< TDC Erhverv Omstilling,Øvrige installationsydelser,Installation Wallboard Basic,Installation Wallboard Basic,,9800003,0 kr.,100 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,
						> TDC Erhverv Omstilling,Øvrige installationsydelser,Installation Wallboard Basic,Installation Wallboard Basic,,9800003,0 kr.,600 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,

						< TDC Erhverv Omstilling,Øvrige installationsydelser,Installation Wallboard Supervisor,Installation Wallboard Supervisor,,9800004,0 kr.,100 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,
						> TDC Erhverv Omstilling,Øvrige installationsydelser,Installation Wallboard Supervisor,Installation Wallboard Supervisor,,9800004,0 kr.,1.400 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,

						< TDC Erhverv Omstilling,Øvrige installationsydelser,Installation supervisor agent,Installation supervisor agent,,9800005,0 kr.,100 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,
						> TDC Erhverv Omstilling,Øvrige installationsydelser,Installation supervisor agent,Installation supervisor agent,,9800005,0 kr.,231 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,

						< TDC Erhverv Omstilling,Øvrige installationsydelser,Hjælp til indspilning af besked,Hjælp til indspilning af besked,,9800006,0 kr.,100 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,5% 30% 50% 30% 20% 5%,,
						> TDC Erhverv Omstilling,Øvrige installationsydelser,Hjælp til indspilning af besked,Hjælp til indspilning af besked,,9800006,854 kr.,0 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,5% 30% 50% 30% 20% 5%,,

						< TDC Erhverv Omstilling,Øvrige installationsydelser,Kunde uddannelse på dagen,Kunde uddannelse på dagen,,9800007,100 kr.,0 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,
						> TDC Erhverv Omstilling,Øvrige installationsydelser,Kunde uddannelse på dagen,Kunde uddannelse på dagen,,9800007,1.387 kr.,0 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,

						< TDC Erhverv Omstilling,Øvrige installationsydelser,Kunde uddannelse 1-2 uger efter inst.,Kunde uddannelse 1-2 uger efter inst.,,9800008,100 kr.,0 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,
						> TDC Erhverv Omstilling,Øvrige installationsydelser,Kunde uddannelse 1-2 uger efter inst.,Kunde uddannelse 1-2 uger efter inst.,,9800008,1.536 kr.,0 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,

						< TDC Erhverv Omstilling,Øvrige installationsydelser,Installation - Diverse,Installation - Diverse,,9800009,0 kr.,0 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,
						> TDC Erhverv Omstilling,Øvrige installationsydelser,_,Installation - Diverse,,9800009,0 kr.,0 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,
						---
						211,217c211,217
						< TDC Erhverv Omstilling,Hardware til rate,Huawei 4G USB modem,Huawei 4G USB modem,,9900001,0 kr.,0 kr.,35 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,
						< TDC Erhverv Omstilling,Hardware til rate,Huawei 4G wifi router,Huawei 4G wifi router,,9900002,0 kr.,0 kr.,75 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,
						< TDC Erhverv Omstilling,Hardware til rate,Jabra/Plantronics/Sennheiser BT headset,Jabra/Plantronics/Sennheiser BT headset,,9900003,0 kr.,0 kr.,40 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,
						< TDC Erhverv Omstilling,Hardware til rate,Jabra/Plantronics/Sennheiser Trådløst Office headset,Jabra/Plantronics/Sennheiser Trådløst Office headset,,9900004,0 kr.,0 kr.,45 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,
						< TDC Erhverv Omstilling,Hardware til rate,Jabra/Plantronics/Sennheiser Rørløfter,Jabra/Plantronics/Sennheiser Rørløfter,,9900005,0 kr.,0 kr.,25 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,
						< TDC Erhverv Omstilling,Hardware til rate,KAZAM Mobil telefon,KAZAM Mobil telefon,,9900006,0 kr.,0 kr.,100 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,
						< TDC Erhverv Omstilling,Hardware til rate,DORO Mobil telefon,DORO Mobil telefon,,9900007,0 kr.,0 kr.,50 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,
						---
						> TDC Erhverv Omstilling,Hardware til rate,Huawei 4G USB modem,Huawei 4G USB modem,,9900001,0 kr.,0 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,
						> TDC Erhverv Omstilling,Hardware til rate,Huawei 4G wifi router,Huawei 4G wifi router,,9900002,0 kr.,0 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,
						> TDC Erhverv Omstilling,Hardware til rate,Jabra/Plantronics/Sennheiser BT headset,Jabra/Plantronics/Sennheiser BT headset,,9900003,0 kr.,0 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,
						> TDC Erhverv Omstilling,Hardware til rate,Jabra/Plantronics/Sennheiser Trådløst Office headset,Jabra/Plantronics/Sennheiser Trådløst Office headset,,9900004,0 kr.,0 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,
						> TDC Erhverv Omstilling,Hardware til rate,Jabra/Plantronics/Sennheiser Rørløfter,Jabra/Plantronics/Sennheiser Rørløfter,,9900005,0 kr.,0 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,
						> TDC Erhverv Omstilling,Hardware til rate,KAZAM Mobil telefon,KAZAM Mobil telefon,,9900006,0 kr.,0 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,
						> TDC Erhverv Omstilling,Hardware til rate,DORO Mobil telefon,DORO Mobil telefon,,9900007,0 kr.,0 kr.,0 kr.,paymentfrequency.monthly,0,0,0,Nej,Nej,Nej,Nej,Ja,,,



						*/						
						
						group = createProductGroup(partnerGroup, MobileProductGroupEnum.PRODUCT_GROUP_PARTNER_HARDWARE);
						group.setHelpMarkdown("**Hjælpetekst**  \n" +
								"\n" +
								"todo  \n");
						group.setHelpHtml(Processor.process(group.getHelpMarkdown()));
						
						addProductWithProductId(group, "9900001", "Huawei 4G USB modem", "Huawei 4G USB modem", 0, 0, 0, false, false, false, true, 0, 0, "Produktet er omfattet af bytteret/service").setVariableRecurringFee(true);
						addProductWithProductId(group, "9900002", "Huawei 4G wifi router", "Huawei 4G wifi router", 0, 0, 0, false, false, false, true, 0, 0, "Produktet er omfattet af bytteret/service").setVariableRecurringFee(true);
						addProductWithProductId(group, "9900003", "Jabra/Plantronics/Sennheiser BT headset", "Jabra/Plantronics/Sennheiser BT headset", 0, 0, 0, false, false, false, true, 0, 0, "Produktet er omfattet af bytteret/service").setVariableRecurringFee(true);
						addProductWithProductId(group, "9900004", "Jabra/Plantronics/Sennheiser Trådløst Office headset", "Jabra/Plantronics/Sennheiser Trådløst Office headset", 0, 0, 0, false, false, false, true, 0, 0, "Produktet er omfattet af bytteret/service").setVariableRecurringFee(true);
						addProductWithProductId(group, "9900005", "Jabra/Plantronics/Sennheiser Rørløfter", "Jabra/Plantronics/Sennheiser Rørløfter", 0, 0, 0, false, false, false, true, 0, 0, "Produktet er omfattet af bytteret/service").setVariableRecurringFee(true);
						addProductWithProductId(group, "9900006", "KAZAM Mobil telefon", "KAZAM Mobil telefon", 0, 0, 0, false, false, false, true, 0, 0, "Produktet er omfattet af {$rate_months} måneders udvidet garanti").setVariableRecurringFee(true);
						addProductWithProductId(group, "9900007", "DORO Mobil telefon", "DORO Mobil telefon", 0, 0, 0, false, false, false, true, 0, 0, "Produktet er omfattet af bytteret/service").setVariableRecurringFee(true);
						addProductWithProductId(group, "9900008", "ALCATEL Mobil telefon", "ALCATEL Mobil telefon", 0, 0, 0, false, false, false, true, 0, 0, "Produktet er omfattet af bytteret/service").setVariableRecurringFee(true);
						addProductWithProductId(group, "9900009", "LG 88xx ip apparat", "LG 88xx ip apparat", 0, 0, 0, false, false, false, true, 0, 0, "Produktet er omfattet af bytteret/service").setVariableRecurringFee(true);
						addProductWithProductId(group, "9900010", "", "Diverse hardware 1", 0, 0, 0, false, false, false, true, 0, 0, null).setVariableRecurringFee(true);
						addProductWithProductId(group, "9900011", "", "Diverse hardware 2", 0, 0, 0, false, false, false, true, 0, 0, null).setVariableRecurringFee(true);
						addProductWithProductId(group, "9900012", "", "Diverse hardware 3", 0, 0, 0, false, false, false, true, 0, 0, null).setVariableRecurringFee(true);
						
						productGroupDao.save(group);
						
						String[] segments = new String[] {"Minimum", "Medium", "Maximum", "EP", "Soho", "CS"};
						for (int j = 0; j < segments.length; j++) {
							String segmentName = segments[j];
							createSegment(segmentName, j);
						}
						
						// Oprettelse:
						
						setProvision(segments, FeeCategory.ONETIME_FEE, "TDC Omstilling", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.ONETIME_FEE, "TDC Omstilling Ekstra", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.ONETIME_FEE, "MobilMix grundabonnement", "38 113 210 100 100 50");   // ???
						setProvision(segments, FeeCategory.ONETIME_FEE, "TDC Mobilpakke Basis", "150 450 840 100 100 50");
						setProvision(segments, FeeCategory.ONETIME_FEE, "TDC Mobilpakke Medium", "225 675 1260 200 200 50");
						setProvision(segments, FeeCategory.ONETIME_FEE, "TDC Mobilpakke Ekstra", "275 825 1540 200 200 50");
						setProvision(segments, FeeCategory.ONETIME_FEE, "TDC Mobilpakke Ekstra Udland", "300 900 1680 200 200 50");
						setProvision(segments, FeeCategory.ONETIME_FEE, "Antal tilvalg SCALE MOBIL MIX", "13 38 70 25 25 25");			// ???
						setProvision(segments, FeeCategory.ONETIME_FEE, "Direkte nummer (1 stk.)", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.ONETIME_FEE, "Direkte nummer (10 stk.)", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.ONETIME_FEE, "Velkomsthilsen", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.ONETIME_FEE, "Udvidet Søgegruppe", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.ONETIME_FEE, "Musik på Hold", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.ONETIME_FEE, "Menuvalg", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.ONETIME_FEE, "PC Omstilling", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.ONETIME_FEE, "PC Opkaldsklient", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.ONETIME_FEE, "Voquant Omstilling Ekstra", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.ONETIME_FEE, "Ipt. Cc Agent", "5% 30% 50% 30% 20% 5%");						// ???
						setProvision(segments, FeeCategory.ONETIME_FEE, "Ipt. Cc Supervisor", "5% 30% 50% 30% 20% 5%");					// ???
						//				setProvision(segments, FeeCategory.ONETIME_FEE, "DIVERSE OPRETTELSE", 1, "5% 30% 50% 30% 20% 5%");
						
						// Installation af løsning: 
						
						setProvision(segments, FeeCategory.INSTALLATION_FEE, "Remote Installation", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.INSTALLATION_FEE, "Scale Installation Grundpakke-Basis", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.INSTALLATION_FEE, "Scale Installation Grunp.Avancere", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.INSTALLATION_FEE, "Scale Installation Opstart og Kørsel", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.INSTALLATION_FEE, "Scale Installation Udvidet Søgegr.", "5% 30% 50% 30% 20% 5%");
						// setProvision(segments, FeeCategory.INSTALLATION_FEE, "Installation wallboard light setProvision(segments, FeeCategory.INSTALLATION_FEE, "
						// setProvision(segments, FeeCategory.INSTALLATION_FEE, "Installation wallboard setProvision(segments, FeeCategory.INSTALLATION_FEE, "
						setProvision(segments, FeeCategory.INSTALLATION_FEE, "Scale Installation Musik på Hold", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.INSTALLATION_FEE, "Scale Installation Menuvalg", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.INSTALLATION_FEE, "Scale Installation PC Omstilling", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.INSTALLATION_FEE, "Scale Installation PC Opkaldsklient", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.INSTALLATION_FEE, "Hjælp til indspilning af besked", "5% 30% 50% 30% 20% 5%");			// ???
						setProvision(segments, FeeCategory.INSTALLATION_FEE, "Voquant Inst. 1-50 Bruger Ekstra", "5% 30% 50% 30% 20% 5%");
						
						setProvision(segments, FeeCategory.INSTALLATION_FEE, "Scale Installation Call Center Supervisor/Agent", "5% 30% 50% 30% 20% 5%");		// ???
						// setProvision(segments, FeeCategory.INSTALLATION_FEE, "kundeuddannelse på installationsdagen 
						// setProvision(segments, FeeCategory.INSTALLATION_FEE, "Ekstra uddannelse 1-2 uger efter installation 
//					setProvision(segments, FeeCategory.INSTALLATION_FEE, "DIVERSE INSTALLATION", "5% 30% 50% 30% 20% 5%");
						
						// Drift: 
						
						setProvision(segments, FeeCategory.RECURRING_FEE, "TDC Omstilling", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.RECURRING_FEE, "TDC Omstilling Ekstra", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.RECURRING_FEE, "TDC Mobilpakke Basis", "150 450 840 100 100 50");
						setProvision(segments, FeeCategory.RECURRING_FEE, "TDC Mobilpakke Ekstra", "275 825 1540 200 200 50");
						setProvision(segments, FeeCategory.RECURRING_FEE, "TDC Mobilpakke Ekstra Udland", "300 900 1680 200 200 50");
						setProvision(segments, FeeCategory.RECURRING_FEE, "Direkte nummer (1 stk.)", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.RECURRING_FEE, "Direkte nummer (10 stk.)", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.RECURRING_FEE, "Velkomsthilsen", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.RECURRING_FEE, "Udvidet Søgegruppe", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.RECURRING_FEE, "Musik på Hold", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.RECURRING_FEE, "Menuvalg", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.RECURRING_FEE, "PC Omstilling", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.RECURRING_FEE, "PC Opkaldsklient", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.RECURRING_FEE, "Voquant Omstilling Ekstra", "5% 30% 50% 30% 20% 5%");
						setProvision(segments, FeeCategory.RECURRING_FEE, "Ipt. Cc Agent", "5% 30% 50% 30% 20% 5%");							// ???
						setProvision(segments, FeeCategory.RECURRING_FEE, "Ipt. Cc Supervisor", "5% 30% 50% 30% 20% 5%");						// ???
//					setProvision(segments, FeeCategory.RECURRING_FEE, "DIVERSE DRIFT", "5% 30% 50% 30% 20% 5%");
						
//						setProvision(segments, null, "Grund indstallation, brugerprofiler, søgegruppe mv.", 0, "150 450 840 100 100 50");		// ???
//						setProvision(segments, null, "Hjælp til indspilning af besked", 0, "275 825 1540 200 200 50");						// ???
						
						businessAreaDao.save(businessArea);

						log.info("Update done: " + name);
						update = new SystemUpdate();
						update.setBusinessAreaId(businessArea.getBusinessAreaId());
						update.setName(name);
						systemUpdateDao.save(update);
					}
				}

				{
					String name = "Make SWITCHBOARD inactive"; // Don't change this name!
					SystemUpdate update = systemUpdateDao.findByName(name, BusinessAreas.SWITCHBOARD);
					if (update == null) {
						log.info("Update starting: '" + name + "' for businessarea SWITCHBOARD");
						
						List<BusinessArea> bas = businessAreaDao.findByField("name", "TDC Erhverv Omstilling");
						if (bas.size() == 1) {
							BusinessArea ba = bas.get(0);
							ba.setEntityState(EntityState.INACTIVE);
							businessAreaDao.save(ba);
						}
						
						update = new SystemUpdate();
						update.setBusinessAreaId(BusinessAreas.SWITCHBOARD);
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

	private void addFeatures() {
		businessArea.addFeature(FeatureType.SWITCHBOARD);
		businessArea.addFeature(FeatureType.MOBILE_BUNDLES_STANDARD);
		businessArea.addFeature(FeatureType.MOBILE_BUNDLES_MIX);
		businessArea.addFeature(FeatureType.XDSL);
		businessArea.addFeature(FeatureType.PARTNER_SETTINGS);
		businessArea.addFeature(FeatureType.IPSA);
		businessArea.addFeature(FeatureType.GKS);
		businessArea.addFeature(FeatureType.FIXED_DISCOUNT_VARIABLE);
		businessArea.addFeature(FeatureType.OUTPUT_CDM);
		businessArea.addFeature(FeatureType.OUTPUT_AUTHORITY);
		businessArea.addFeature(FeatureType.OUTPUT_PROCESS);
		businessArea.addFeature(FeatureType.OUTPUT_PARTNER_SUPPORT);
		businessArea.addFeature(FeatureType.RECURRING_FEE_SPLIT);
		businessArea.addFeature(FeatureType.NETWORK_COVERAGE_MAP);
		businessArea.addFeature(FeatureType.FORDELSAFTALE);
		businessArea.addFeature(FeatureType.SHOW_INSTALLATION_DATE);
		businessArea.addFeature(FeatureType.SHOW_CONTRACT_START_DATE);
	}

	private void createSegment(String segment, int index) {
		segmentDao.save(new Segment(segment, index));
	}
	
	private void setProvision(String[] segments, FeeCategory category, String productName, String provisions) {
		for (ProductGroup productGroup : businessArea.getProductGroupsAndChildren()) {
			for (Product product : productGroup.getProducts()) {
				MobileProduct p = (MobileProduct) product;
				if (productName.equals(p.getInternalNameRaw())) {
					switch (category) {
					case ONETIME_FEE:
						System.out.println("Group: " + productGroup.getName() + ", Product: " + p.getInternalNameRaw() + ", Provisions (one-time): " + provisions);
						p.setProvisionOneTimeFee(provisions);
//						p.setBusinessValueOneTimeFee(businessValue);
						return;

					case INSTALLATION_FEE:
						System.out.println("Group: " + productGroup.getName() + ", Product: " + p.getInternalNameRaw() + ", Provisions (installation): " + provisions);
						p.setProvisionInstallationFee(provisions);
//						p.setBusinessValueInstallationFee(businessValue);
						return;

					case RECURRING_FEE:
						System.out.println("Group: " + productGroup.getName() + ", Product: " + p.getInternalNameRaw() + ", Provisions (recurring): " + provisions);
						p.setProvisionRecurringFee(provisions);
//						p.setBusinessValueRecurringFee(businessValue);
						return;

					default:
						break;
					}
				}
			}
		}
		System.out.println("Switchboard - No provision set for: " + productName);
	}

	public void initBusinessArea() {
		if (createBusinessArea) {
			businessArea = new BusinessArea();
			businessArea.setBusinessAreaId(businessAreaId);
			businessArea.setName(businessAreaName);
			businessArea.setIntroText(introText);
			addFeatures();
			businessAreaDao.save(businessArea);
		}
		initGroups();
	}
	
	@Override
	public void makeUpgrades() {
		initGroups();
		
		if (CoreApplication.get().getSetting(Environment.WAF_ENV).equals("dev")) {
			// Now only necessary in dev mode
			update1();   
			update2();
			update3();
			update4();
			update5();
			update6();
		}
		
		makeSystemUpdates(businessArea);
		
		businessAreaDao.save(businessArea);
	}
	
	private void update1() {
		Product product = coreProductDao.findByProductGroupAndProductId(businessArea.getId(), productionCdmGroup.getUniqueName(), "4400235");
		if (product == null) {
			// --- missing products and product relations ---
			product = coreProductDao.findByProductGroupAndProductId(businessArea.getId(), productionCdmGroup.getUniqueName(), "4400235");
			if (product == null) {
				addProductWithProductId(productionCdmGroup, "4400235", "Scale Vrf", "Scale Vrf", 0, 0, 0, false, false, true, true, 1, 1);
				addProductWithProductId(productionCdmGroup, "0659600", "Scale 1-årig aftale", "Scale 1-årig aftale", 0, 0, 0, false, false, true, true, 0, 1);
				addProductWithProductId(productionCdmGroup, "0659700", "Scale 2-årig aftale", "Scale 2-årig aftale", 0, 0, 0, false, false, true, true, 0, 1);
				addProductWithProductId(productionCdmGroup, "0659800", "Scale 3-årig aftale", "Scale 3-årig aftale", 0, 0, 0, false, false, true, true, 0, 1);
			}

			product = coreProductDao.findByProductGroupAndProductId(businessArea.getId(), mobilMixTilvalgGroup.getUniqueName(), "GMESTATV");
			if (product != null) {
				coreProductDao.delete(product);
			}
			
			product = coreProductDao.findByProductGroupAndProductId(businessArea.getId(), omstillingTilvalgGroup.getUniqueName(), "3234000");
			if (product == null) {
				addProductWithProductId(omstillingTilvalgGroup, "3234000", "Musik på Hold", "Musik på Hold", 10300, 2100, 0, true, true, true, true, 0, 1);
				addProductWithProductId(omstillingTilvalgGroup, "4400281", "Scale Installation Musik på Hold", "Scale Installation Musik på Hold", 16650, 0, 0, false, false, true, true, 0, 0);
				addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_N, "3234000", "4400281");
			}
			
			product = coreProductDao.findByProductGroupAndProductId(businessArea.getId(), omstillingTilvalgGroup.getUniqueName(), "3234700");
			if (product == null) {
				addProductWithProductId(omstillingTilvalgGroup, "3234700", "Menuvalg", "Menuvalg", 25600, 20500, 0, true, true, true, true, 0, 0);
				addProductWithProductId(omstillingTilvalgGroup, "4400285", "Scale Installation Menuvalg", "Scale Installation Menuvalg", 16650, 0, 0, false, false, true, true, 0, 0);
				addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_N, "3234700", "4400285");
			}
			
			product = coreProductDao.findByProductGroupAndProductId(businessArea.getId(), omstillingTilvalgGroup.getUniqueName(), "3234100");
			if (product == null) {
				addProductWithProductId(omstillingTilvalgGroup, "3234100", "PC Omstilling", "PC Omstilling", 5200, 1100, 0, true, true, true, true, 0, 0);
				addProductWithProductId(omstillingTilvalgGroup, "4400282", "Scale Installation PC Omstilling", "Scale Installation PC Omstilling", 10000, 0, 0, false, false, true, true, 0, 0);
				addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_N, "3234100", "4400282");
			}
			
			product = coreProductDao.findByProductGroupAndProductId(businessArea.getId(), omstillingTilvalgGroup.getUniqueName(), "3236600");
			if (product == null) {
				addProductWithProductId(omstillingTilvalgGroup, "3236600", "PC Opkaldsklient", "PC Opkaldsklient", 2100, 1100, 0, true, true, true, true, 0, 0);
				addProductWithProductId(omstillingTilvalgGroup, "4400618", "Scale Installation PC Opkaldsklient", "Scale Installation PC Omstilling", 16650, 0, 0, false, false, true, true, 0, 0);
				addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_N, "3236600", "4400618");
			}
			
			product = coreProductDao.findByProductGroupAndProductId(businessArea.getId(), omstillingTilvalgGroup.getUniqueName(), "3236100");
			if (product == null) {
				addProductWithProductId(omstillingTilvalgGroup, "3236100", "Voquant Omstilling Ekstra", "Voquant Omstilling Ekstra", 102400, 50200, 0, true, true, true, true, 0, 0);
				addProductWithProductId(omstillingTilvalgGroup, "3266500", "Voquant Inst. 1-50 Bruger Ekstra", "Voquant Inst. 1-50 Bruger Ekstra", 153100, 0, 0, false, false, true, true, 0, 0);
				addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_N, "3236100", "3266500");
			}
			
			String[] serviceProductIds = new String[] {"0371000", "0311100", "0311200", "0311300"};
			for (String productId : serviceProductIds) {
				product = coreProductDao.findByProductGroupAndProductId(businessArea.getId(), omstillingTilvalgGroup.getUniqueName(), productId);
				if (product != null) {
					if (product.getSortIndex() < 30) {
						product.setSortIndex(product.getSortIndex() + 30);
						coreProductDao.save(product);
					}
				}
			}
			
			String[][] fixes = new String[][] 
					{ 
					{"GMERHV", "3239505"},
					{"GVPAKMEB", "3239506"},
					{"GVPAKMES", "3239507"},
					{"GVPAKMED", "3239508"},
					{"GVPAKMEG", "3239509"},
					{"GDPMECSC", "3239200"},
					{"GDPMEDSC", "3239604"},
					{"GDPMEESC", "3239605"},
					{"GDPMEGSC", "3239606"},
					{"_pk_BASIS", "3239501"},
					{"_pk_MEDIUM", "3239502"},
					{"_pk_EKSTRA", "3239503"},
					{"_pk_UDLAND", "3239504"},
					{"GNOR2T", "3239510"},
					{"GNORFRIT", "3239511"},
					{"GEUR2T", "3239512"},
					{"GEURFRIT", "3239513"},
					{"GDRME50", "3288900"},
					{"GNOR100MB", "3239601"},
					{"GNOR1GB", "3239602"},
					{"GEUR100MB", "3239614"},
					{"GEUR1GB", "3239615"},
					{"GMEFRISM", "3239607"},
					{"GMEFIK", "3233800"},
					{"GMEBUD", "3239609"},
					{"GMESIK", "3251200"},
					{"GTEM4G", "3283500"},
					{"GEXCE1", "3242700"} 
					};
			for(ProductGroup productGroup: businessArea.getProductGroupsAndChildren()) {
				for (Product p : productGroup.getProducts()) {
					for (String[] fix : fixes) {
						if (fix[0].equals(p.getProductId())) {
							p.setProductId(fix[1]);
							coreProductDao.save(p);
						}
					}
				}
			}
			
			// This product is temporarily unavailable
			MobileProduct mobileProduct = (MobileProduct) coreProductDao.findByProductGroupAndProductId(businessArea.getId(), omstillingTilvalgGroup.getUniqueName(), "3234000");
			if (mobileProduct != null) {
				mobileProduct.setExcludeFromConfigurator(true);
				mobileProduct.setExcludeFromProductionOutput(true);
				coreProductDao.save(mobileProduct);
			}
			
			businessAreaDao.save(businessArea);
		}
	}

	private void update2() {
		upgradeProduct("4400235", false, false);		
		upgradeProduct("3239617", false, true);		
		upgradeProduct("3239618", false, true);		
		upgradeProduct("0727500", false, false);		
		upgradeProduct("4400278", false, false);		
		upgradeProduct("4400279", false, false);		
		upgradeProduct("4400290", false, false);		
		upgradeProduct("9511001", false, true);		
		upgradeProduct("9511009", false, true);		
		upgradeProduct("3234600", false, true);		
		upgradeProduct("3234000", false, true);		
		upgradeProduct("3234700", false, true);		
		upgradeProduct("3234100", false, true);		
		upgradeProduct("3236600", false, true);		
		upgradeProduct("3236100", false, true);		
		upgradeProduct("3234600", false, true);		
		upgradeProduct("4400284", false, false);		
		upgradeProduct("4400281", false, false);		
		upgradeProduct("4400285", false, false);		
		upgradeProduct("4400282", false, false);		
		upgradeProduct("4400618", false, false);		
		upgradeProduct("3266500", false, false);		
		upgradeProduct("0659600", false, false);		
		upgradeProduct("0659700", false, false);		
		upgradeProduct("0659800", false, false);		
		upgradeProduct("3239505", true, false);		
		upgradeProduct("3239501", true, false);		
		upgradeProduct("3239502", true, false);		
		upgradeProduct("3239503", true, false);		
		upgradeProduct("3239504", true, false);		
		upgradeProduct("3239506", true, false);		
		upgradeProduct("3239507", true, false);		
		upgradeProduct("3239508", true, false);		
		upgradeProduct("3239509", true, false);		
		upgradeProduct("3239200", "3289200");		
		upgradeProduct("3289200", true, false);		
		upgradeProduct("3239604", true, false);		
		upgradeProduct("3239605", true, false);		
		upgradeProduct("3239606", true, false);		
		upgradeProduct("3238100", false, false);		
		upgradeProduct("3238200", false, false);		
		upgradeProduct("3238300", false, false);		
		upgradeProduct("3239510", false, false);		
		upgradeProduct("3239511", false, false);		
		upgradeProduct("3239512", false, false);		
		upgradeProduct("3239513", false, false);		
		upgradeProduct("3288900", false, false);		
		upgradeProduct("3239601", false, false);		
		upgradeProduct("3239602", false, false);		
		upgradeProduct("3239614", false, false);		
		upgradeProduct("3239615", false, false);		
		upgradeProduct("3239607", true, false);		
		upgradeProduct("3233800", true, false);		
		upgradeProduct("3239609", true, false);		
		upgradeProduct("3251200", true, false);		
		upgradeProduct("3283500", true, false);		
		upgradeProduct("3242700", false, false);		
		upgradeProduct("0371000", false, true);		
		upgradeProduct("0311100", false, true);		
		upgradeProduct("0311100", 0, 1100, 0);		
		upgradeProduct("0311200", false, true);		
		upgradeProduct("0311200", 0, 1600, 0);		
		upgradeProduct("0311300", false, true);		
		upgradeProduct("0311300", 0, 2100, 0);
		
		upgradeProduct("0727500", 0, 0, 130000);		
		upgradeProduct("4400278", 0, 0, 99900);		
		upgradeProduct("4400279", 0, 0, 133200);		
		upgradeProduct("4400290", 0, 0, 76800);		
		upgradeProduct("4400288", 0, 0, 82000);		
		upgradeProduct("4400284", 0, 0, 33300);		
		upgradeProduct("4400281", 0, 0, 16650);		
		upgradeProduct("4400285", 0, 0, 16650);		
		upgradeProduct("4400282", 0, 0, 10000);		
		upgradeProduct("4400618", 0, 0, 16650);		
		upgradeProduct("3266500", 0, 0, 153100);		

		upgradeProduct("4400281", 0, 1);
		upgradeProduct("4400282", 0, 1);
		upgradeProduct("4400285", 0, 1);
		upgradeProduct("4400618", 0, 1);
		upgradeProduct("3234700", 0, 9999);
		upgradeProduct("3234100", 0, 9999);
		upgradeProduct("3236600", 0, 9999);
		upgradeProduct("3236100", 0, 9999);
		upgradeProduct("3266500", 0, 1);
		upgradeProduct("3266500", "Voquant Inst. 1-50 Bruger Ekstra", "Voquant Inst. 1-50 Bruger Ekstra");
	}
	
	private void update3() {
		MobileProduct product;
		for (String productId : new String[] {"0727500", "4400278", "4400279", "4400290"}) {
			product = (MobileProduct) coreProductDao.findByProductGroupAndProductId(businessArea.getId(), omstillingGroup.getUniqueName(), productId);
			if (product == null) {
				log.error("Product not found: " + productId);
				throw new RuntimeException();
			}
			product.setExcludeFromConfigurator(true);
			product.setTdcInstallation(true);
			mobileProductDao.save(product);
		}
		
		for (String productId : new String[] {"4400281", "4400282", "4400284", "4400285", "4400618", "3266500"}) {
			product = (MobileProduct) coreProductDao.findByProductGroupAndProductId(businessArea.getId(), omstillingTilvalgGroup.getUniqueName(), productId);
			if (product == null) {
				throw new RuntimeException();
			}
			product.setExcludeFromConfigurator(true);
			product.setTdcInstallation(true);
			mobileProductDao.save(product);
		}
		
		moveProductToAnotherGroup(businessArea.getId(), "3233800", mobilMixIncludedGroup, mobilMixTaleGroup);
	}
	
	private void update4() {
//		for (Campaign campaign : businessArea.getCampaigns()) {
//			for (ProductBundle bundle: campaign.getProductBundles()) {
//				MobileProductBundle mobileBundle = (MobileProductBundle) bundle;
//				if (MobileProductBundleEnum.SWITCHBOARD_BUNDLE.equals(mobileBundle.getBundleType())) {
//					List<BundleProductRelation> productRelations = mobileBundle.getProducts();
//					for (BundleProductRelation bundleProductRelation : productRelations) {
//						if (bundleProductRelation.getProduct() != null) {
//							MobileProduct product = (MobileProduct) bundleProductRelation.getProduct();
//							if (product.getProductId().startsWith("_omst_")) {
//								bundleProductRelation.setTags(new ArrayList());
//							}
//						}
//					}
//					productBundleDao.save(bundle);
//				}
//			}
//		}
	}
	
	private void update5() {
		// Note: Covers all business areas!
		for (Contract contract : contractDao.findAll()) {
			if (StringUtils.isEmpty(contract.getOfferIntroText())) {
				contract.setOfferIntroText(
						"Tak for en behagelig samtale.\n\n" +
						"Som lovet sender jeg dig her et tilbud på " + businessArea.getName() + ".\n\n" +
						"Dette tilbud er udarbejdet på baggrund af de oplysninger, som er modtaget fra jer og jeg håber, at tilbuddet matcher jeres behov for en mobilløsning, tilpasset præcis jeres virksomhed.\n\n" +
						"Hvis du har ønsker til ændringer eller spørgsmål er du naturligvis velkommen til at kontakte mig.\n\n" +
						"Venlig hilsen");
//				contractDao.save(contract);
				contractSaver.save(contract);
			}
		}
		
		Product product = coreProductDao.findByProductGroupAndProductId(businessArea.getId(), extraGroup.getUniqueName(), MobileProduct.PRODUCT_EXTRA_PREFIX + "1");
		if (product == null) {
			// --- missing products and product relations ---
			addProductWithProductId(extraGroup, MobileProduct.PRODUCT_EXTRA_PREFIX + "1", "", "", 0, 0, 0, false, false, false, true, 1, 1);
			addProductWithProductId(extraGroup, MobileProduct.PRODUCT_EXTRA_PREFIX + "2", "", "", 0, 0, 0, false, false, false, true, 1, 1);
			addProductWithProductId(extraGroup, MobileProduct.PRODUCT_EXTRA_PREFIX + "3", "", "", 0, 0, 0, false, false, false, true, 1, 1);
		}
	}
	
	private void update6() {
		List<Product> products = coreProductDao.findByBusinessArea(businessArea);
		for (Product product : products) {
			MobileProduct p = (MobileProduct) product;
			if (p.isIpsaDiscountEligible()) {
				((MobileProduct) product).setGks(true);
			} else {
				((MobileProduct) product).setGks(false);
			}
			coreProductDao.save(p);
		}
	}

	
	private void deleteBusinessArea(BusinessArea businessArea) {
		try {
			for (BaseUser user : userDao.findAll()) {
				SalespersonRole salesperson = (SalespersonRole) user.getRole(SalespersonRole.class);
				if (salesperson != null) {
					Iterator<Contract> iter = salesperson.getContracts().iterator();
					while (iter.hasNext()) {
						Contract contract = iter.next();
						if (contract.getBusinessArea().getId().equals(businessArea.getId())) {
							iter.remove();
						}
					}
					roleDao.save(salesperson);
				}
			}
			
			List<Contract> contracts = contractDao.findByBusinessArea(businessArea);
			for (Contract contract : contracts) {
				for(OrderLine orderLine : contract.getOrderLines()) {
					orderLine.setBundle(null);
					orderLine.setProduct(null);
					orderLineDao.delete(orderLine);
				}
				for(Subscription subscription : ((MobileContract) contract).getSubscriptions()) {
					subscription.setBundle(null);
					subscription.setProducts(new ArrayList<MobileProduct>());
					subscriptionDao.delete(subscription);
				}
				for(ProductBundle productBundle : contract.getProductBundles()) {
					productBundle.setProducts(new ArrayList<BundleProductRelation>());
					productBundleDao.delete(productBundle);
				}
				List<Campaign> campaigns = ((MobileContract) contract).getCampaigns();
				contract.setCampaigns(new ArrayList<Campaign>());
				for(Campaign campaign : campaigns) {
					for(ProductBundle productBundle : campaign.getProductBundles()) {
						productBundle.setProducts(null);
						productBundleDao.delete(productBundle);
					}
					campaignDao.delete(campaign);
				}
				contract.setBusinessArea(null);
				contractDao.delete(contract);
			}
			
			for(Campaign campaign : businessArea.getCampaigns()) {
				for(ProductBundle productBundle : campaign.getProductBundles()) {
					productBundle.setProducts(null);
					productBundleDao.delete(productBundle);
				}
				campaign.setProductBundles(new ArrayList<ProductBundle>());
				campaignDao.delete(campaign);
			}
			businessArea.setCampaigns(new ArrayList<Campaign>());
			
			Iterator<ProductRelation> iterator = businessArea.getProductRelations().iterator();
			while (iterator.hasNext()) {
				ProductRelation productRelation = (ProductRelation) iterator.next();
				productRelation.setBusinessArea(null);
				productRelation.setProducts(new ArrayList<Product>());
				productRelationDao.delete(productRelation);
				iterator.remove();
			}
			for(ProductGroup productGroup : businessArea.getProductGroupsAndChildren()) {
				productGroupDao.delete(productGroup);
			}
			businessArea.setProductGroups(new ArrayList<ProductGroup>());
			
			businessAreaDao.save(businessArea);
//			businessAreaDao.delete(businessArea);
		} catch (Exception e) {
			System.out.println(ExceptionUtils.getStackTrace(e));
		}
	}

	private void upgradeProduct(String productId, int min, int max) {
		List<MobileProduct> products = mobileProductDao.findByField("productId", productId);
		if (products.size() == 0) {
			log.warn("Product not found: " + productId);
		}
		for (MobileProduct mobileProduct : products) {
			if (mobileProduct.getBusinessArea().equals(businessArea)) {
				mobileProduct.setMinCount(min); 
				mobileProduct.setMaxCount(max); 
				mobileProductDao.save(mobileProduct);
			}
		}
	}

	private void upgradeProduct(String productId, String publicName, String internalName) {
		List<MobileProduct> products = mobileProductDao.findByField("productId", productId);
		if (products.size() == 0) {
			log.warn("Product not found: " + productId);
		}
		for (MobileProduct mobileProduct : products) {
			if (mobileProduct.getBusinessArea().equals(businessArea)) {
				mobileProduct.setPublicName(publicName); 
				mobileProduct.setInternalName(internalName);
				mobileProductDao.save(mobileProduct);
			}
		}
	}

	/**
	 * This method is a bit of a hack. It does not take product group into account, which is dangerous,
	 * since product ids are not guaranteed to be unique.
	 *  
	 * @param productId
	 * @param discountEligible
	 * @param ipsaDiscountEligible
	 */
	private void upgradeProduct(String productId, boolean discountEligible, boolean ipsaDiscountEligible) {
		List<MobileProduct> products = mobileProductDao.findByField("productId", productId);
		if (products.size() == 0) {
			log.warn("Product not found: " + productId);
		}
		for (MobileProduct mobileProduct : products) {
			if (mobileProduct.getBusinessArea().equals(businessArea)) {
				mobileProduct.setDiscountEligible(discountEligible);
				mobileProduct.setIpsaDiscountEligible(ipsaDiscountEligible);
				mobileProductDao.save(mobileProduct);
			}
		}
	}
	
	/**
	 * This method is a bit of a hack. It does not take product group into account, which is dangerous,
	 * since product ids are not guaranteed to be unique.
	 */
	private void upgradeProduct(String productId, long oneTimeFee, long recurringFee, long installationFee) {
		List<MobileProduct> products = mobileProductDao.findByField("productId", productId);
		if (products.size() == 0) {
			log.warn("Product not found: " + productId);
		}
		for (MobileProduct mobileProduct : products) {
			if (mobileProduct.getBusinessArea().equals(businessArea)) {
				mobileProduct.setPrice(new Amounts(oneTimeFee, installationFee, recurringFee));
				mobileProductDao.save(mobileProduct);
			}
		}
	}

	/**
	 * This method is a bit of a hack. It does not take product group into account, which is dangerous,
	 * since product ids are not guaranteed to be unique.
	 */
	private void upgradeProduct(String productId, String newProductId) {
		List<MobileProduct> products = mobileProductDao.findByField("productId", productId);
		if (products.size() == 0) {
			log.warn("Product not found: " + productId);
		}
		for (MobileProduct mobileProduct : products) {
			if (mobileProduct.getBusinessArea().equals(businessArea)) {
				mobileProduct.setProductId(newProductId);
				mobileProductDao.save(mobileProduct);
			}
		}
	}

	@Override
	protected String getMiscMarkdown() {
		return "**Artikler fra TDC Perspektiv**\n\n" +
				"  1. <a href=\"http://perspektiv.tdc.dk/personlig-telefonservice-er-penge-vaerd/\" target=\"_blank\">Personlig telefonservice er penge værd</a>\n" +
				"  2. <a href=\"http://perspektiv.tdc.dk/nar-kunderne-ringer-kommer-20-aldrig-igennem/\" target=\"_blank\">Når kunderne ringer kommer 20 % aldrig igennem</a>\n" +
				"  3. <a href=\"http://perspektiv.tdc.dk/styrk-virksomheden-med-4g-daekning/\" target=\"_blank\">Styrk virksomheden med 4G dækning</a>\n";
	}
	
	@Override
	public void initPages(BusinessArea businessArea) {
		if (createBusinessArea) {
			createPage(businessArea, CorePageIds.SALES_EXISTING_CONTRACTS, "Vælg kundesag", null, 
					"Velkommen til " + businessArea.getName() + " salgskonfigurator"
							+ ".\n\nKonfiguratoren skal hj\u00e6lpe dig til at lave det materiale, du skal bruge overfor dine kunder. Du kan danne et prisoverslag eller et egentligt tilbud, som kan bruges til at afstemme \u00f8konomi og l\u00f8sning med kunderne.\n\nDu kan altid f\u00e5 inspiration til din kundedialog ved at se n\u00e6rmere p\u00e5 TDC Perspektiv artiklerne som kan findes til h\u00f8jre.",
					"Du har to muligheder for at arbejde med kundesager:\n\n  * Du kan \u00e5bne en eksisterende kundesag ved at klikke \"V\u00e6lg\" til venstre\n\n  * Du kan oprette en ny salgssag ved at klikke p\u00e5 \"Ny kundesag\"\n\nDu kan med fordel arbejde med kategorier for dine kundesager for at skabe st\u00f8rre overblik. Anvend ikonet med den lille mand \u00f8verst i konfiguratoren til h\u00f8jre og klik p\u00e5 \"Kundesagskategorier\" i menuen.");
			
			createPage(businessArea, CorePageIds.SALES_MASTER_DATA, "Stamdata", null, 
					"For at kunne lave et tilbud og et tastebilag, er det n\u00f8dvendigt at du indtaster stamdata, som systemet kan brevflette med. Der er tale om oplysninger p\u00e5 dig som s\u00e6lger, men ogs\u00e5 vigtige oplysninger omkring kunden.\n\nHvis du indtaster CVR nummer henter konfiguratoren selv \u00f8vrige data og sl\u00e5r virksomhedsadressen op p\u00e5 d\u00e6kningskortet. Du kan p\u00e5 d\u00e6kningskortet manuelt frems\u00f8ge alternative adresser, ligesom du, ved anvendelse af indstillingspanelerne i sidderne, kan f\u00e5 vist forskellige teknologier og fremtidig netv\u00e6rksudbygning for 3 m\u00e5neder.\n\nNB: Kontroller venligst de opsl\u00e5ede oplysninger p\u00e5 kunden", 
					"Det anbefales at du anvender knappen \"Videre\" igennem konfiguratoren. P\u00e5 denne m\u00e5de sikres det, at du guides forbi alle relevante trin og derfor f\u00e5r angivet de n\u00f8dvendige input til l\u00f8sningen.\n\nDe enkelte trin i en eksisterende kundesag vil altid v\u00e6re tilg\u00e6ngelige via menupunkterne, som ligger i toppen af konfiguratoren.");
			
			createPage(businessArea, MobilePageIds.MOBILE_SWITCHBOARD, BUNDLE_OMSTILLING, null, 
					"-- denne tekst bruges ikke --",
					"-- denne tekst bruges ikke --");
			
			createPage(businessArea, MobilePageIds.MOBILE_SWITCHBOARD_TYPE, BUNDLE_OMSTILLING, businessArea.getName(),  
					"Vælg en telefonbetjening, der kan ses helt nede på bundlinjen\n\nGod kundeservice begynder ved telefonen. En opringning er typisk den første kontakt, en kunde får med dit firma. Derfor er det afgørende, at telefonen bliver taget, at man kan stille om til den rette, og at alle er tilgængelige\n\nUnder tilvalg findes relevante muligheder for opsalg til løsningerne.", 
					"**Hovednummer**\n\nVirksomheden beholder ét hovednummer. Det kan sagtens være jeres fastnetnummer, selv om alle brugere er mobil.\n\n**Velkomsthilsen**\n\nMulighed for en voice response-menu, så kunden skal kan sælge fx afdeling.\n\n**Kø-gruppe**\n\nI en kø-gruppe kan I samle bestemte medarbejdere, som de alle kan svare kald fra hovednummeret\n\n**IVR – menuvalg**\n\nKunden kan betjene sig selv i en tastemenu og på denne måde ramme den rigtige afdeling\n\n**Omstillingsbord**\n\nMed en receptionist til at svare kald i virksomheden er et omstillingsbord en rigtig god løsning\n\n*Link til Video med omstillingsbord*\n(Tilgår fra NR)\n\n*Link til billede af omstillingsbord*\n(Tilgår fra NR)");
			
			createPage(businessArea, MobilePageIds.MOBILE_SWITCHBOARD_ADDONS, BUNDLE_OMSTILLING, "Tilvalg",  
					"Vælg en telefonbetjening, der kan ses helt nede på bundlinjen\n\nGod kundeservice begynder ved telefonen. En opringning er typisk den første kontakt, en kunde får med dit firma. Derfor er det afgørende, at telefonen bliver taget, at man kan stille om til den rette, og at alle er tilgængelige\n\nUnder tilvalg findes relevante muligheder for opsalg til løsningerne.", 
					"Tilvalg hjælp - todo");
			
			createPage(businessArea, MobilePageIds.MOBILE_CONTRACT_SETTINGS, "Kundeprofil", null, 
					"Inden selve l\u00f8sningen, skal vi vide lidt om kunden og det pris-setup vi skal arbejde videre med.\n\nAnvend nedenst\u00e5ende sp\u00f8rgsm\u00e5l for at afd\u00e6kke kundens behov. Find evt. hj\u00e6lp til de enkelte emner i kassen til h\u00f8jre.\n\n**NB: Det er vigtigt du anvender mark\u00f8rerne ved sp\u00f8rgsm\u00e5lene, da vi anvender input aktivt for at blive klogere p\u00e5 vores kunder.**", 
					"**Kampagne:**\n\nAktive kampagner vil i kampagneperidoen kunne v\u00e6lges i drop down.\n\nOBS:\n\n\n  * To eller flere kampagner kan ikke kombineres\n\n  * V\u00e6lges kampagne er det udelukkende de i kampagnen indeholdte abonnementer som pr\u00e6senteres i konfiguratoren\n\n**Hj\u00e6lp til behovsafd\u00e6kning:**\n\nKunden er meget afh\u00E6ngig af sin telefon:\n\n  * Vores erfaringer viser, at 20 % af alle kald aldrig bliver svaret. Derfor er en omstillingsl\u00F8sning relevant\n\nKundens hovednummer er meget velkendt:\n\n  * Som en del af l\u00F8sningen i TDC kan du beholde dit velkendte hovednummer \n  * Du kan tilk\u00F8be flere hovednumre, hvis du har behov for flere kundeindgange\n\nDet er vigtigt at kunden oplever god service:\n\n  * K\u00F8 og derfor ventetid er ikke optimal service, hvorfor det er vigtigt, at du f\u00E5r sat k\u00F8grupper og telefonnumre rigtigt op. Derfor f\u00E5r du hj\u00E6lp til installation i TDC\n  * Den bedste service opleves gennem ved en menneskelig relation. Derfor anbefaler TDC et omstillingsbord hvor en medarbejder kan h\u00E5ndtere kald\n\nDet giver meget v\u00E6rdi at kald besvares personligt\n\n  * TDC har et af markedets bedste omstillingsborde. Du kan se status, b\u00E5de kalender og telefon, p\u00E5 alle medarbejdere samt automatisk se opkaldshistorik p\u00E5 indkommende kald fra kunder\n\nDet betyder meget, at der altid kan stilles videre til kollegaer\n\n  * De enkelte brugere kan stille aktive kald videre til kollegaer direkte p\u00E5 mobiltelefonen");
			
			createPage(businessArea, MobilePageIds.MOBILE_STANDARD_BUNDLES, "Abonnementer - TDC Mobilpakker", null, 
					"Med udgangspunkt i antallet af medarbejdere i kundens virksomhed skal du nu v\u00e6lge abonnementer til medarbejderne.\n\nNedenst\u00e5ende abonnementer er pakketeret ud fra vores erfaringer omkring tale samt data behov i virksomheder. Hvis kunden ikke \u00f8nsker pakkerne, kan du sammens\u00e6tte 3 skr\u00e6ddersyede under TDC Erhverv Mobil Mix.",
					"NB: Tryk p\u00e5 flere af de pr\u00e6senterede services for at l\u00e6se mere!\n\n**Inkluderede services:**\n\n  * <a href=\"http://privat.tdc.dk/element.php?dogtag=p_tel_fordel_playm\" target=\"_blank\">TDC Play musik</a>\n\n  *  <a href=\"http://erhverv.tdc.dk/element.php?dogtag=e_prod_samlet_app\" target=\"_blank\">TDC Erhverv app</a>\n\n  *  <a href=\"http://erhverv.tdc.dk/publish.php?id=14449\" target=\"_blank\">Online Selvbetjening</a>\n\n  * <a href=\"http://kundeservice.tdc.dk/erhverv/publish.php?dogtag=e_help_mobil_op_vm\" target=\"_blank\">Voicemail</a>\n\n  * Voice@mail\n\n  * HD Voice\n\n  *  <a href=\"http://kundeservice.tdc.dk/erhverv/publish.php?dogtag=e_help_mobil_brug_kk\" target=\"_blank\">Konferencekald</a>\n\n  * <a href=\"http://kundeservice.tdc.dk/erhverv/publish.php?dogtag=e_help_mobil_brug_vs\" target=\"_blank\">Personlig viderestilling</a>\n\n  * <a href=\"http://kundeservice.tdc.dk/erhverv/faq.php?id=27051\" target=\"_blank\">Dataroaming gr\u00e6nser i hele verden (360 kr. pr. mdr.)</a>\n\n  * <a href=\"http://erhverv.tdc.dk/enterprise/element.php?dogtag=e_prod_bb_hotspot\" target=\"_blank\">TDC Hotspot</a>\n\n  * Pr\u00e6senter andet nummer\n\n<a href=\"http://erhverv.tdc.dk/element.php?dogtag=e_prod_mobil_abo\" target=\"_blank\">**Basis**</a>\n\n  * 5 timers tale, fri intern tale, sms og mms\n\n  * 500 Mb data med fleksible datapakker \u2013 Du kan bruge op til 4 ekstra datatrin \u00e1 49 kr. pr. md. Hvert datatrin indeholder samme m\u00e6ngde data, som mobilpakken indeholder (Kan lukke for ekstra datatrin)\n\n  * Datahastighed op til 6 mbit/s\n\n  * Statusvisning\n\n<a href=\"http://erhverv.tdc.dk/element.php?dogtag=e_prod_mobil_abo\" target=\"_blank\">**Medium**</a>\n\n  * 10 timers tale, fri intern tale, sms og mms\n\n  * 1 GB data med fleksible datapakker \u2013 Du kan bruge op til 4 ekstra datatrin \u00e1 49 kr. pr. md. Hvert datatrin indeholder samme m\u00e6ngde data, som mobilpakken indeholder (Kan lukke for ekstra datatrin)\n\n  * Datahastighed op til 10 mbit/s\n\n  * Budgetpakke med fri voicemail\n\n<a href=\"http://erhverv.tdc.dk/element.php?dogtag=e_prod_mobil_abo\" target=\"_blank\">**Ekstra**</a>\n\n  * Fri tale, fri sms og mms\n\n  * 5 GB data med fleksible datapakker \u2013 Du kan bruge op til 4 ekstra datatrin \u00e1 49 kr. pr. md. Hvert datatrin indeholder samme m\u00e6ngde data, som mobilpakken indeholder (Kan lukke for ekstra datatrin)\n\n  * Fuld hastighed p\u00e5 3G og 4G - datahastighed op til 150 mbit/s\n\n  * Budgetpakke med fri voicemail\n\n  * Datadeling \u2013 \u00e9t ekstra SIM til deling af inkluderet data\n\n  * Mobilsikkerhed \u2013 antivirus og sikker browsing samt online storage\n\n<a href=\"http://erhverv.tdc.dk/element.php?dogtag=e_prod_mobil_abo\" target=\"_blank\">**Ekstra Udland**</a>\n\n  * Samme indhold som \"Ekstra\" men til den internationale mobilbruger med EU zone \u2013 2 timers tale, fri sms og 100 MB data i EU");
			
			createPage(businessArea, MobilePageIds.MOBILE_MIX_BUNDLES, "Abonnementer - TDC Erhverv MobilMix", null, 
					"Med udgangspunkt i antallet af medarbejdere i kundens virksomhed skal du nu sammens\u00e6tte abonnementer til medarbejderne.\n\nFor hvert abonnement er der mulighed for at lave tilpasninger s\u00e5 l\u00f8sningen skr\u00e6ddersyes den enkelte bruger. Du kan maksimalt sammens\u00e6tte 3 forskellige pakker pr. kunde", 
					"NB: Tryk p\u00e5 flere af de pr\u00e6senterede services for at l\u00e6se mere!\n\n**Inkluderede services:**\n\n  * <a href=\"http://privat.tdc.dk/element.php?dogtag=p_tel_fordel_playm\" target=\"_blank\">TDC Play musik</a>\n\n  *  <a href=\"http://erhverv.tdc.dk/element.php?dogtag=e_prod_samlet_app\" target=\"_blank\">TDC Erhverv app</a>\n\n  *  <a href=\"http://erhverv.tdc.dk/publish.php?id=14449\" target=\"_blank\">Online Selvbetjening</a>\n\n  * <a href=\"http://kundeservice.tdc.dk/erhverv/publish.php?dogtag=e_help_mobil_op_vm\" target=\"_blank\">Voicemail</a>\n\n  * Voice@mail\n\n  * HD Voice\n\n  *  <a href=\"http://kundeservice.tdc.dk/erhverv/publish.php?dogtag=e_help_mobil_brug_kk\" target=\"_blank\">Konferencekald</a>\n\n  * <a href=\"http://kundeservice.tdc.dk/erhverv/publish.php?dogtag=e_help_mobil_brug_vs\" target=\"_blank\">Personlig viderestilling</a>\n\n  * <a href=\"http://kundeservice.tdc.dk/erhverv/faq.php?id=27051\" target=\"_blank\">Dataroaming gr\u00e6nser i hele verden (360 kr. pr. mdr.)</a>\n\n  * <a href=\"http://erhverv.tdc.dk/enterprise/element.php?dogtag=e_prod_bb_hotspot\" target=\"_blank\">TDC Hotspot</a>\n\n  * Pr\u00e6senter andet nummer\n\n <a href=\"http://erhverv.tdc.dk/element.php?dogtag=e_mobil_mix\" target=\"_blank\">**Tale**</a>\n\n  * 5 timer svarer til 15 minutter pr. arbejdsdag\n\n  * 10 timer svarer til 30 minutter pr. arbejdsdag\n\n  * 20 timer svarer til 60 minutter pr. arbejdsdag\n\n  * Fri tale giver frit forbrug med budgetsikkerhed\n\nV\u00e6lger du ogs\u00e5 fri intern tale, kan du tale alt det du vil med dine kolleger. De skal blot oprettes i samme lokalnummer gruppe.\n\n <a href=\"http://erhverv.tdc.dk/element.php?dogtag=e_mobil_mix\" target=\"_blank\">**Data**</a>\n\n  * 500 MB: Medarbejderen er mest p\u00e5 kontoret, hvor han som oftest tjekker sin mail og surfer via computeren. N\u00e5r han er ude, bruger han applikationer og holder sig orienteret p\u00e5 nyhedssites.\n\n  * 500 MB - 2 GB: Medarbejderens smartphone er uundv\u00e6rlig som arbejdsredskab. Mail og kalender er altid synkroniseret, s\u00e5 han kan holde sig orienteret om indkomne mails, n\u00e5r han er p\u00e5 farten. Han ser enkelte nyhedsklip og downloader dokumenter direkte til sin smartphone.\n\n  * 2-5 GB: Smartphonen er medarbejderens prim\u00e6re netmedie og arbejdsredskab. Mail og kalender er selvf\u00f8lgelig altid synkroniseret, den fungerer som navigation i bilen, som nyhedsmedie, og bruges til at vise streamede nyheds-spots. Smartphonen bruges desuden til at lave backup af dokumenter p\u00e5 en online-storage applikation.\n\n <a href=\"http://erhverv.tdc.dk/element.php?dogtag=e_mobil_mix\" target=\"_blank\">**Tilvalg**</a>\n\n  * Budgetpakke med fri voicemail og gratis oprettelse samt m\u00e5nedsabonnement p\u00e5 udvalgte roamingpakker\n\n  * Datadeling \u2013 \u00e9t ekstra SIM til deling af inkluderet data\n\n  * Mobilsikkerhed \u2013 antivirus og sikker browsing p\u00e5 mobilen samt sikker online storage\n\n  * Statusvisning \u2013 se kollegers status, t\u00e6ndt eller slukket, ledige eller optaget mv.");
			
			createPage(businessArea, MobilePageIds.MOBILE_PRODUCT_SELECTION, "Abonnementer - tilpasninger", null, 
					"V\u00e6lg herunder roaming tilvalg til kunden.\n\nAnvend evt. <a href=\"http://erhverv.tdc.dk/mobil/udlandspriser/udlandet.php\" target=\"_blank\">udlandsberegner</a> fra tdc.dk");

			createPage(businessArea, MobilePageIds.MOBILE_CONTRACT_SUMMARY, "Sammenfatning", null, 
					"Du kan nu danne et indledningsvist prisoverslag eller et f\u00e6rdigt tilbud over den konfigurerede l\u00f8sning. Anvend knapperne i nedenst\u00e5ende felt.\n\nRabatprocenten fra Fordels- eller Erhvervsaftale tastes under \"Kontrakt type\" og \"Valgfri rabat\". NB: Du kan kun arbejde med de kendte rabatprocenter fra Fordels- eller Erhvervsaftale.\n\nEfter kundeaccept skal du forsat udarbejde kontrakt i CDM eller Excel v\u00e6rkt\u00f8j.", 
					"Knappen \"Vilk\u00e5r\" giver dig abonnementsvilk\u00e5r for TDC Mobilpakker og TDC Erhverv MobilMix.");
		}
	}
	
	@Override
	public void initProducts(BusinessArea businessArea) {
		// ----------------------------
		// Products
		// ----------------------------
		{
			roamingGroup.setHelpMarkdown("For at gøre det nemmere for dig at vælge den rette løsning, har vi derfor delt verden op i nogle overskuelige zoner:\n\n" +
					"**Norden**  \n" + 
					"Finland, Island, Norge og Sverige\n\n" +
					"**Europa**  \n" + 
					"Belgien, Bulgarien, Estland, Finland, Frankrig (inkl. Fransk Guniea, Guadelope, Martinique, Réunion og Monaco), Holland, Island, Italien (inkl. Vatikanstaten og San Marino), Kroatien, "
					+ "Letland, Liechtenstein, Litauen, Luxembourg, Malta, Norge, Polen, Portugal (inkl. Azorerne og Madeira), Rumænien, Schweiz, Slovakiet, Spanien (inkl. De Kanariske Øer), "
					+ "Storbritannien (inkl. Guernsey, Jersey og Isle of Man), Sverige, Tjekkiet, Tyskland, Ungarn, Østrig\n\n" +
					"**Verden**  \n" + 
					"**World North**  \n" + 
					"Norden + Alaska, Canada, USA (incl Hawaii, Puerto Rico, Virgin Islands (US), Antigua & Barbuda, Barbados, Caymans Islands, Dominica, St. Lucia & Saint Vincent)\n\n" +
					"**World East**  \n" + 
					"Hong Kong, Indien, Japan, Kina, Kuwait, Libyen, Malaysia, New Zealand, Oman, Singapore, Thailand, Tunesien\n\n" +
					"**World Central**  \n" + 
					"Australien, Bahrain, For. Arabiske Emirater, Pakistan, Saudi Arabien, Sydafrika, Taiwan, Vietnam\n\n" +
					"*Bemærk at udlandsabonnementer og -pakker ikke er rabatberettigede.*");
			roamingGroup.setHelpHtml(Processor.process(roamingGroup.getHelpMarkdown()));
			
			ProductGroup funktionerGroup		= 		createProductGroup(andreTilvalgGroup, MobileProductGroupEnum.PRODUCT_GROUP_ADDON_FUNCTIONS);
			funktionerGroup.setHelpMarkdown("**Kort forklaring til relevante funktionstilvalg**  \n" +
					"\n" +
					"**4G**  \n" + 
					"4G giver LTE adgang med hastighed 150/50 Mbit/s. En eventuel hastighedsnedsættelse følger speed drop på den prisplanen der er på abonnementet.\n" +
					"\n" +
					"**Begrænset bruger**  \n" + 
					"Med Begrænset bruger kan du angive hvor meget den enkelte medarbejder kan ringe ud. Medarbejderen kan som standard kun ringe til andre mobiltelefoner i samme lokalnummergruppe. OBS: Virker ikke i udlandet.\n" +
					"\n" +
					"**TDC Play Film og Seriepakke**  \n" + 
					"Adgang til film og serier via TDC Play App'en. Hvis kunden befinder sig på TDC's netværk, er streaming af film og serier på App'en inkluderet i kundens abonnement.\n" +
					"\n" +
					"**Secure Mobil**  \n" + 
					"Punkt-til-punkt sikkerhedsløsning. Nem og sikker mobil adgang til virksomhedens interne netværk - uden brug af VPN klienter. Virker også fra udlandet.\n" +
					"\n" +
					"**Saldomax**  \n" + 
					"Saldomax er et maksimumsbeløb for forbrug (følger måned). Når dette beløb er nået, spærres mobiltelefonen automatisk for yderligere forbrug. "
					+ "Ophæves ved kald til IVR. Du kan altid ringe til kundeservice og 112.\n");
			funktionerGroup.setHelpHtml(Processor.process(funktionerGroup.getHelpMarkdown()));
			
			// Products (Thomas)
			
			// "Falske" produkter. Taletid regnes som værende implicitte i standard bundles. Det gør det bare unødigt kompliceret.
			addProductWithProductId(mobilPakkeGroup, "3239501", "TDC Mobilpakke Basis","TDC Mobilpakke Basis",9900, 21200, 0, true, false, true, true, 0, 0);
			addProductWithProductId(mobilPakkeGroup, "3239502", "TDC Mobilpakke Medium","TDC Mobilpakke Medium",9900, 33200, 0, true, false, true, true, 0, 0);
			addProductWithProductId(mobilPakkeGroup, "3239503", "TDC Mobilpakke Ekstra","TDC Mobilpakke Ekstra",9900, 43867, 0, true, false, true, true, 0, 0);
			addProductWithProductId(mobilPakkeGroup, "3239504", "TDC Mobilpakke Ekstra Udland","TDC Mobilpakke Ekstra Udland",9900, 59867, 0, true, false, true, true, 0, 0);
			
			addProductWithProductId(mobilPakkeTaleGroup, "_pk_TALE1", "5 Timers tale", "5 Timers tale", 0, 0, 0, false, false, true, true, 0, 0);
			addProductWithProductId(mobilPakkeTaleGroup, "_pk_TALE2", "10 Timers tale", "10 Timers tale", 0, 0, 0, false, false, true, true, 0, 0);
			addProductWithProductId(mobilPakkeTaleGroup, "_pk_TALE3", "20 Timers tale", "20 Timers tale", 0, 0, 0, false, false, true, true, 0, 0);
			addProductWithProductId(mobilPakkeTaleGroup, "_pk_TALE4", "Fri tale", "Fri tale", 0, 0, 0, false, false, true, true, 0, 0);
			
			addProductWithProductId(mobilPakkeDataGroup, "_pk_DATA1", "500 MB Data", "500 MB Data Trintakseret", 0, 0, 0, false, false, true, true, 0, 0);
			addProductWithProductId(mobilPakkeDataGroup, "_pk_DATA2", "1 GB Data", "1 GB Data Trintakseret", 0, 0, 0, false, false, true, true, 0, 0);
			addProductWithProductId(mobilPakkeDataGroup, "_pk_DATA3", "5 GB Data", "5 GB Data Trintakseret", 0, 0, 0, false, false, true, true, 0, 0);
			addProductWithProductId(mobilPakkeDataGroup, "_pk_DATA2a", "1 GB Data", "1 GB Data Trintakseret - inkl. 1 Trin gratis", 0, 0, 0, false, false, true, true, 0, 0);

			addProductWithProductId(mobilPakkeTilvalgGroup, "_pk_TILVALG1", "Budgetpakke", "Budgetpakke", 0, 0, 0, false, false, true, true, 0, 0);
			addProductWithProductId(mobilPakkeTilvalgGroup, "_pk_TILVALG3", "MobilSikkerhed", "MobilSikkerhed, inkluderet", 0, 0, 0, false, false, true, true, 0, 0);
			addProductWithProductId(mobilPakkeTilvalgGroup, "_pk_TILVALG4", "4G", "4G Hastighed  inkluderet", 0, 0, 0, false, false, true, true, 0, 0);
			addProductWithProductId(mobilPakkeTilvalgGroup, "_pk_TILVALG5", "Fri intern tale og fri sms/mms", "Fri intern tale og fri sms/mms", 0, 0, 0, false, false, true, true, 0, 0);
			addProductWithProductId(mobilPakkeTilvalgGroup, "_pk_TILVALG_DATADELING", "Datadeling", "Datadeling, inkluderet", 0, 0, 0, false, false, true, true, 0, 0);
			addProductWithProductId(mobilPakkeTilvalgGroup, "_pk_TILVALG_DATADELING_SIMKORT", "Datadeling Simkort Type", "Datadeling Simkort Type", 0, 0, 0, false, false, true, true, 0, 0);
			
			addProductWithProductId(mobilPakkeUdlandGroup, "_pk_UDLAND1", "Udland EU Zone", "Udland EU Zone", 0, 0, 0, false, false, true, true, 0, 0);
			
			addProductWithProductId(roamingGroup, "3239510", "Norden 2 timers tale","Norden lille tale og SMS", 49.00, 99.00, 0.0, "0", false, false);
			addProductWithProductId(roamingGroup, "3239511", "Norden 100 timers tale","Norden stor tale og fri SMS/MMS",49.00,399.00, 0.0, "0", false, false);
			addProductWithProductId(roamingGroup, "3239601", "Norden 100 MB data","Norden lille data",49.00,149.00, 0.0, "0", false, false);
			addProductWithProductId(roamingGroup, "3239602", "Norden 1 GB data","Norden stor data",49.00,599.00, 0.0, "0", false, false);
			addProductWithProductId(roamingGroup, "3239512", "Europa 2 timers tale","EU lille tale og sms",49.00,149.00, 0.0, "0", false, false);
			addProductWithProductId(roamingGroup, "3239513", "Europa 100 timers tale","EU Stor tale og sms/MMS",49.00,499.00, 0.0, "0", false, false);
			addProductWithProductId(roamingGroup, "3239614", "Europa 100 MB data","EU Lille data",49.00,199.00, 0.0, "0", false, false);
			addProductWithProductId(roamingGroup, "3239615", "Europa 1 GB data","EU stor data",49.00,699.00, 0.0, "0", false, false);
			addProductWithProductId(roamingGroup, "3238100", "TDC World North", "Rabat pris voice North",30.00,20.00, 0.0, "0", false, false);
			addProductWithProductId(roamingGroup, "3238200", "TDC World East", "Rabat pris voice East",30.00,20.00, 0.0, "0", false, false);
			addProductWithProductId(roamingGroup, "3238300", "TDC World Central", "Rabat pris voice Central",30.00,20.00, 0.0, "0", false, false);
			addProductWithProductId(roamingGroup, "3288900", "Øvrige Verden månedspakke 50 MB (1500,- pr pakke efter forbrug)","Rabat pris data øvrig verden",99.00,5.00, 0.0, "0", false, false);
			
			addProductWithProductId(funktionerGroup, "3283500", "4G", "TDC ERHVERV 4G MOBIL", 49.00, 29.00, 0.0, "0", true, false);
			
			addProductWithProductId(mobilMixGroup, "3239505", "MobilMix grundabonnement","MobilMix grundabonnement",99.00,49.00, 0.0, "0", true, false);
			
			addProductWithProductId(mobilMixTaleTidGroup, "3239506", "5 Timer","Lille tale",30.00,69.00, 0.0, "0", true, false);
			addProductWithProductId(mobilMixTaleTidGroup, "3239507", "10 Timer","Mellem tale",30.00,109.00, 0.0, "0", true, false);
			addProductWithProductId(mobilMixTaleTidGroup, "3239508", "20 Timer","Stor tale",30.00,149.00, 0.0, "0", true, false);
			addProductWithProductId(mobilMixTaleTidGroup, "3239509", "Fri tale","Fri tale",30.00,169.00, 0.0, "0", true, false);
			
			addProductWithProductId(mobilMixIncludedGroup, "3233800", "Fri intern tale","Fri intern tale", 49.00, 19.00, 0.0, "0", true, false);
			
			addProductWithProductId(mobilMixTaleGroup, "3239607", "Fri SMS/MMS","Fri sms/MMS",49.00,19.00, 0.0, "0", true, false);
			
			addProductWithProductId(mobilMixDataAmountGroup, "3239200", "500 MB Data Trintakseret","500 mb med automatisk topop",30.00,69.00, 0.0, "0", true, false);
			addProductWithProductId(mobilMixDataAmountGroup, "3239604", "1 GB Data Trintakseret","1 gb med automatisk topop",30.00,129.00, 0.0, "0", true, false);
			addProductWithProductId(mobilMixDataAmountGroup, "3239605", "2 GB Data Trintakseret","2 gb med automatisk topop",30.00,159.00, 0.0, "0", true, false);
			addProductWithProductId(mobilMixDataAmountGroup, "3239606", "5 GB Data Trintakseret","5 gb med automatisk topop",30.00,179.00, 0.0, "0", true, false);
			
			addProductWithProductId(mobilMixTilvalgGroup, "3239609", "Budgetpakke","Fri voicemail",49.00,29.00, 0.0, "0", true, false);
			addProductWithProductId(mobilMixTilvalgGroup, "3251200", "TDC Erhverv Mobilsikkerhed","Sikkerheds pakke",49.00,29.00, 0.0, "0", true, false);
			addProductWithProductId(mobilMixTilvalgGroup, "3242700", "Datadeling","Datadeling",49.00,29.00, 0.0, "0", false, false);
//			addProductWithProductId(mobilMixTilvalgGroup, "GMESTATV", "Statusvisning","Send status til andre",49.00,9.00, 0.0, "0", true, false);

			// Omstilling
			addProductWithProductId(omstillingGroup, "3239617", "TDC Omstilling", "TDC Omstilling",169900, 69900, 0, true, true, true, true, 0, 0);
			addProductWithProductId(omstillingGroup, "3239618", "TDC Omstilling Ekstra", "TDC Omstilling Ekstra",190000, 99900, 0, true, true, true, true, 0, 0);
			addProductWithProductId(omstillingGroup, "0727500", "Remote Installation", "Remote Installation",130000, 0, 0, false, false, true, true, 0, 0).setExcludeFromConfigurator(true);
			addProductWithProductId(omstillingGroup, "4400278", "Scale Installation Grundpakke - Basis", "Scale Installation Grundpakke-Basis",0, 0, 0, false, false, true, true, 0, 0).setExcludeFromConfigurator(true);
			addProductWithProductId(omstillingGroup, "4400279", "Scale Installation Grundpakke - Avanceret", "Scale Installation Grunp.Avancere",0, 0, 0, false, false, true, true, 0, 0).setExcludeFromConfigurator(true);
			addProductWithProductId(omstillingGroup, "4400290", "Scale Installation Opstart og Kørsel", "Scale Installation Opstart Og kørsel",0, 0, 0, false, false, true, true, 0, 0).setExcludeFromConfigurator(true);
			
			// "Falske" omstillingsprodukter
			addProductWithProductId(omstillingGroup, "_omst_HOVEDNR", "Hovednummer", "Hovednummer",0, 0, 0, false, false, true, true, 0, 0);
			addProductWithProductId(omstillingGroup, "_omst_VELKOMST", "Velkomsthilsen", "Velkomsthilsen",0, 0, 0, false, false, true, true, 0, 0);
			addProductWithProductId(omstillingGroup, "_omst_KØGRUPPE", "Kø-gruppe", "Kø-gruppe",0, 0, 0, false, false, true, true, 0, 0);
			addProductWithProductId(omstillingGroup, "_omst_IVR", "IVR - menuvalg", "IVR - menuvalg",0, 0, 0, false, false, true, true, 0, 0);
			addProductWithProductId(omstillingGroup, PRODUCT_OMSTILLINGSBORD, "Omstillingsbord", "Omstillingsbord",0, 0, 0, false, false, true, true, 0, 0);
			
			// Omstilling tilvalg
			addProductWithProductId(omstillingTilvalgGroup, "9511001", "Direkte nummer (1 stk.)", "Direkte nummer (1 stk.)", 41000, 600, 0, true, true, true, true, 0, 0);
			addProductWithProductId(omstillingTilvalgGroup, "9511009", "Direkte nummer (10 stk.)", "Direkte nummer (10 stk.)", 41000, 4100, 0, true, true, true, true, 0, 0);
			
			addProductWithProductId(omstillingTilvalgGroup, "3234600", "Udvidet Søgegruppe", "Udvidet Søgegruppe", 25600, 8800, 0, true, true, true, true, 0, 0);
			addProductWithProductId(omstillingTilvalgGroup, "4400284", "Scale Installation Udvidet Søgegr.", "Scale Installation Udvidet Søgegr.", 33300, 0, 0, false, false, true, true, 0, 0);
			addProductRelation(businessArea, MobileProductRelationTypeProvider.ADD_ORDERLINES_N, "3234600", "4400284");
			
			addProductWithProductId(omstillingTilvalgGroup, "0371000", "Hverdage, 8-16", "Service - Hverdage 8-16", 0, 0, 0, false, false, true, true, 0, 1)
				.setSubscriberProduct(true);
			addProductWithProductId(omstillingTilvalgGroup, "0311100", "Hverdage, 8-20", "Service - Hverdage 8-20", 0, 0, 0, true, true, true, true, 0, 1)
				.setSubscriberProduct(true);
			addProductWithProductId(omstillingTilvalgGroup, "0311200", "Alle dage, 8-22", "Service - Alle dage 8-22", 0, 0, 0, true, true, true, true, 0, 1)
				.setSubscriberProduct(true);
			addProductWithProductId(omstillingTilvalgGroup, "0311300", "Alle dage, 0-24", "Service - Alle dage 0-24", 0, 0, 0, true, true, true, true, 0, 1)
				.setSubscriberProduct(true);
			
//			addProductRelation(businessArea, CoreProductRelationTypeProvider.ADD_ORDERLINES_1, "0311100", "0371000");
//			addProductRelation(businessArea, CoreProductRelationTypeProvider.ADD_ORDERLINES_1, "0311200", "0371000");
//			addProductRelation(businessArea, CoreProductRelationTypeProvider.ADD_ORDERLINES_1, "0311300", "0371000");
			addProductRelation(businessArea, "Service", CoreProductRelationTypeProvider.ALTERNATIVE_PRODUCTS, "0371000", "0311100", "0311200", "0311300");
		}
		
		Campaign someCampaign = null;
		// ----------------------------
		// Campaigns
		// ----------------------------
		{
			Campaign campaign;
			
			campaign = objectFactory.createCampaign();
			campaign.setName("Ingen kampagne");
			campaign.setFromDate(null);
			campaign.setToDate(null);		
			businessArea.addCampaign(campaign);
			
			// ----------------------------
			// Campaigns -> Bundles
			// ----------------------------
			{
				// Mobilpakker
				
				MobileProductBundle baseBundle = new MobileProductBundle();
				baseBundle.setBundleType(MobileProductBundleEnum.MOBILE_BUNDLE);
				baseBundle.setPublicName("Basis");
				baseBundle.setProductId("3239501");
				baseBundle.setKvikCode("TDC Mobilpakke Basis");
				baseBundle.setInternalName(baseBundle.getKvikCode());
				baseBundle.setSortIndex(1);
				campaign.addProductBundle(baseBundle);

				MobileProductBundle mediumBundle = new MobileProductBundle();
				mediumBundle.setBundleType(MobileProductBundleEnum.MOBILE_BUNDLE);
				mediumBundle.setPublicName("Medium");
				mediumBundle.setProductId("3239502");
				mediumBundle.setKvikCode("TDC Mobilpakke Medium");
				mediumBundle.setInternalName(mediumBundle.getKvikCode());
				mediumBundle.setSortIndex(2);
				campaign.addProductBundle(mediumBundle);
				
				MobileProductBundle ekstraBundle = new MobileProductBundle();
				ekstraBundle.setBundleType(MobileProductBundleEnum.MOBILE_BUNDLE);
				ekstraBundle.setPublicName("Ekstra");
				ekstraBundle.setProductId("3239503");
				ekstraBundle.setKvikCode("TDC Mobilpakke Ekstra");
				ekstraBundle.setInternalName(ekstraBundle.getKvikCode());
				ekstraBundle.setSortIndex(3);
				campaign.addProductBundle(ekstraBundle);
				
				MobileProductBundle ekstraUdlandBundle = new MobileProductBundle();
				ekstraUdlandBundle.setBundleType(MobileProductBundleEnum.MOBILE_BUNDLE);
				ekstraUdlandBundle.setPublicName("Ekstra Udland");
				ekstraUdlandBundle.setProductId("3239504");
				ekstraUdlandBundle.setKvikCode("TDC Mobilpakke Ekstra Udland");
				ekstraUdlandBundle.setInternalName(ekstraUdlandBundle.getKvikCode());
				ekstraUdlandBundle.setSortIndex(4);
				campaign.addProductBundle(ekstraUdlandBundle);
				
				// Omstillingspakker
				
				MobileProductBundle omstillingBundle = new MobileProductBundle();
				omstillingBundle.setBundleType(MobileProductBundleEnum.SWITCHBOARD_BUNDLE);
				omstillingBundle.setPublicName(BUNDLE_OMSTILLING);
				omstillingBundle.setInternalName("TDC Omstilling");
				omstillingBundle.setSortIndex(5);
				campaign.addProductBundle(omstillingBundle);
				
				MobileProductBundle omstillingEkstraBundle = new MobileProductBundle();
				omstillingEkstraBundle.setBundleType(MobileProductBundleEnum.SWITCHBOARD_BUNDLE);
				omstillingEkstraBundle.setPublicName(BUNDLE_OMSTILLING_EKSTRA);
				omstillingEkstraBundle.setInternalName("TDC Omstilling Ekstra");
				omstillingEkstraBundle.setSortIndex(6);
				campaign.addProductBundle(omstillingEkstraBundle);
								
				campaign = campaignDao.save(campaign);		// Bundles needs an ID
				
				addProductToBundle(baseBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE.getKey(), "3239501", true);	
				addProductToBundle(baseBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_SPEECH.getKey(), "_pk_TALE1", true);	
				addProductToBundle(baseBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_DATA.getKey(), "_pk_DATA1", true);	
				addProductToBundle(baseBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_ADDON.getKey(), "_pk_TILVALG5", true);
				
				addProductToBundle(mediumBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE.getKey(), "3239502", true);	
				addProductToBundle(mediumBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_SPEECH.getKey(), "_pk_TALE2", true);	
				addProductToBundle(mediumBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_DATA.getKey(), "_pk_DATA2", true);	
				addProductToBundle(mediumBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_ADDON.getKey(), "_pk_TILVALG1", true);
				addProductToBundle(mediumBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_ADDON.getKey(), "_pk_TILVALG5", true);
				
				addProductToBundle(ekstraBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE.getKey(), "3239503", true);	
				addProductToBundle(ekstraBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_SPEECH.getKey(), "_pk_TALE4", true);	
				addProductToBundle(ekstraBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_DATA.getKey(), "_pk_DATA3", true);	
				addProductToBundle(ekstraBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_ADDON.getKey(), "_pk_TILVALG1", true);
				addProductToBundle(ekstraBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_ADDON.getKey(), "_pk_TILVALG3", true);
				addProductToBundle(ekstraBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_ADDON.getKey(), "_pk_TILVALG4", true);
				addProductToBundle(ekstraBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_ADDON.getKey(), "_pk_TILVALG5", true);
				addProductToBundle(ekstraBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_ADDON.getKey(), "_pk_TILVALG_DATADELING", true);
				
				addProductToBundle(ekstraUdlandBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE.getKey(), "3239504", true);	
				addProductToBundle(ekstraUdlandBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_SPEECH.getKey(), "_pk_TALE4", true);	
				addProductToBundle(ekstraUdlandBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_DATA.getKey(), "_pk_DATA3", true);	
				addProductToBundle(ekstraUdlandBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_ADDON.getKey(), "_pk_TILVALG1", true);
				addProductToBundle(ekstraUdlandBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_ADDON.getKey(), "_pk_TILVALG3", true);
				addProductToBundle(ekstraUdlandBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_ADDON.getKey(), "_pk_TILVALG4", true);
				addProductToBundle(ekstraUdlandBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_ADDON.getKey(), "_pk_TILVALG5", true);
				addProductToBundle(ekstraUdlandBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_ADDON.getKey(), "_pk_TILVALG_DATADELING", true);
				addProductToBundle(ekstraUdlandBundle, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_NON_DOMESTIC.getKey(), "_pk_UDLAND1", true);
				
				addProductToBundle(omstillingBundle, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD.getKey(), "3239617", true);
				addProductToBundle(omstillingBundle, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD.getKey(), "_omst_HOVEDNR", true);	
				addProductToBundle(omstillingBundle, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD.getKey(), "_omst_VELKOMST", true);	
				addProductToBundle(omstillingBundle, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD.getKey(), "_omst_KØGRUPPE", true);	
				addProductToBundle(omstillingBundle, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD.getKey(), "_omst_IVR", true);	
				
				addProductToBundle(omstillingEkstraBundle, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD.getKey(), "3239618", true);
				addProductToBundle(omstillingEkstraBundle, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD.getKey(), "_omst_HOVEDNR", true);	
				addProductToBundle(omstillingEkstraBundle, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD.getKey(), "_omst_VELKOMST", true);	
				addProductToBundle(omstillingEkstraBundle, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD.getKey(), "_omst_KØGRUPPE", true);	
				addProductToBundle(omstillingEkstraBundle, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD.getKey(), "_omst_IVR", true);	
				addProductToBundle(omstillingEkstraBundle, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD.getKey(), PRODUCT_OMSTILLINGSBORD, true);
			}
		}

		businessAreaDao.save(businessArea); 
	}

	
	protected void addProductWithProductId(ProductGroup productGroup, String productId, String publicName, String internalName, double oneTimeFee, double recurringFee, double installationFee, 
			String scaleType, boolean fordelsaftale, boolean ipsaDiscount) {
		MobileProduct product = new MobileProduct();
		productGroup.addProduct(product);
		product.setPublicName(publicName);
		product.setInternalName(internalName);
		product.setProductId(productId);
		product.setPrice(new Amounts(Math.round(oneTimeFee * 100), Math.round(installationFee * 100), Math.round(recurringFee * 100)));
		product.initSortIndex();
		product.setDiscountEligible(fordelsaftale);
		product.setIpsaDiscountEligible(ipsaDiscount);
		mobileProductDao.save(product);
	}

	@Override
	public void initTestContracts(BusinessArea businessArea) {
		for (BaseUser user : userDao.findAll()) {
			SalespersonRole salesperson = (SalespersonRole) user.getRole(SalespersonRole.class);
			if ((salesperson != null) && (salesperson.getUser().getUsername().startsWith("partner"))) {
				MobileContract contract = (MobileContract) objectFactory.createAndSaveContract(businessArea, salesperson);
				salesperson.addContract(contract);
				
				contract.setTitle("Test kontrakt");
				
				// Lets say the user orders 5 of each bundle associated with
				// the campaign.
				Map<MobileProductBundle, BundleCount> bundleToCountMap = new HashMap<>(); 
				for (ProductBundle productBundle : contract.getCampaigns().get(0).getProductBundles()) {
					if (MobileProductBundleEnum.MOBILE_BUNDLE.equals(((MobileProductBundle) productBundle).getBundleType())) {
						int subIndex = 0;
						BundleCount bc = new BundleCount((MobileProductBundle) productBundle, subIndex, 5, 0);
						bundleToCountMap.put((MobileProductBundle) productBundle, bc);
					}
				}

				// If the user wants to adjust what he has ordered (numbers, not type of bundles)
				// we first check if this is ok. You can't take away subscriptions that are already
				// fully or partly configured.
				if (contract.adjustSubscriptions(bundleToCountMap, true)) {
					// The change was ok, so adjust orderlines as well
					contract.adjustOrderLinesForBundles(bundleToCountMap, MobileProductBundleEnum.MOBILE_BUNDLE);
				}
				roleDao.save(salesperson);
			}
		}
	}

	protected void addProductToBundle(MobileProductBundle bundle, String productGroupKey, String productId, boolean addProductPrice) {
		BundleProductRelation relation = new BundleProductRelation();
		relation.setProduct(coreProductDao.findByProductGroupAndProductId(businessArea.getId(), productGroupKey, productId));
		relation.setAddProductPrice(addProductPrice);
		relation.setSortIndex(1l);
		bundle.addProductRelation(relation);
	}

	private void initGroups() {
		omstillingGroup = createProductGroup(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD);
		omstillingTilvalgGroup = createProductGroup(omstillingGroup, MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_ADDON);

		mobilPakkeGroup = createProductGroup(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE);
		mobilPakkeTaleGroup = createProductGroup(mobilPakkeGroup, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_SPEECH);
		mobilPakkeDataGroup = createProductGroup(mobilPakkeGroup, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_DATA);
		mobilPakkeTilvalgGroup = createProductGroup(mobilPakkeGroup, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_ADDON);
		mobilPakkeUdlandGroup = createProductGroup(mobilPakkeGroup, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_NON_DOMESTIC);

		mobilMixGroup = createProductGroup(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE);
		mobilMixTaleGroup = createProductGroup(mobilMixGroup, MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_SPEECH);
		mobilMixTaleTidGroup = createProductGroup(mobilMixTaleGroup, MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_SPEECH_TIME);
		mobilMixDataGroup = createProductGroup(mobilMixGroup, MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_DATA);
		mobilMixDataAmountGroup = createProductGroup(mobilMixDataGroup, MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_DATA_AMOUNT);
		mobilMixTilvalgGroup = createProductGroup(mobilMixGroup, MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_ADDON);
		mobilMixIncludedGroup = createProductGroup(mobilMixGroup, MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_INCLUDED);

		andreTilvalgGroup = createProductGroup(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_ADDON);
		roamingGroup = createProductGroup(andreTilvalgGroup, MobileProductGroupEnum.PRODUCT_GROUP_ADDON_ROAMING);
//		funktionerGroup = createProductGroup(andreTilvalgGroup, MobileProductGroupEnum.PRODUCT_GROUP_ADDON_FUNCTIONS);
		
		productionGroup = createProductGroup(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_PRODUCTION_OUTPUT);
		productionCdmGroup = createProductGroup(productionGroup, MobileProductGroupEnum.PRODUCT_GROUP_PRODUCTION_OUTPUT_CDM);
		
		extraGroup = createProductGroup(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_EXTRA);
	}
}


