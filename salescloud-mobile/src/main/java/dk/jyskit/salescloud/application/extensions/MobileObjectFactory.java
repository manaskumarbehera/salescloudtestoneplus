package dk.jyskit.salescloud.application.extensions;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import dk.jyskit.salescloud.application.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.google.inject.Inject;
import com.google.inject.Provider;

import dk.jyskit.salescloud.application.dao.CampaignDao;
import dk.jyskit.salescloud.application.dao.MobileContractDao;
import dk.jyskit.salescloud.application.dao.ProductBundleDao;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.extensionpoints.ObjectFactory;
import dk.jyskit.salescloud.application.utils.PasswordGenerator;
import dk.jyskit.waf.wicket.crud.CrudContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MobileObjectFactory implements ObjectFactory {
	
	@Inject
	private CampaignDao campaignDao;
	@Inject
	private MobileContractDao contractDao;
	@Inject
	private ProductDao productDao;
	@Inject
	private ProductBundleDao productBundleDao;

	@Override
	public Contract createContract() {
		return new MobileContract();
	}

	@Override
	public Contract createAndSaveContract(BusinessArea businessArea, SalespersonRole salesperson) {
		MobileContract contract = new MobileContract();
		
//		contract.getSeller().setAddress(salesperson.getCompanyInfo().getAddress());
//		contract.getSeller().setCity(salesperson.getCompanyInfo().getCity());
//		contract.getSeller().setComment(salesperson.getCompanyInfo().getComment());
//		contract.getSeller().setCompanyId(salesperson.getCompanyInfo().getCompanyId());
//		contract.getSeller().setCompanyName(salesperson.getCompanyInfo().getCompanyName());
//		contract.getSeller().setEmail(salesperson.getCompanyInfo().getEmail());
//		contract.getSeller().setName(salesperson.getCompanyInfo().getName());
//		contract.getSeller().setPhone(salesperson.getCompanyInfo().getPhone());
//		contract.getSeller().setPosition(salesperson.getCompanyInfo().getPosition());
//		contract.getSeller().setZipCode(salesperson.getCompanyInfo().getZipCode());
		
		contract.getSeller().setEmail(salesperson.getUser().getEmail());
		contract.getSeller().setName(salesperson.getUser().getFullName());
		contract.getSeller().setAddress(salesperson.getOrganisation().getAddress());
		contract.getSeller().setCity(salesperson.getOrganisation().getCity());
		contract.getSeller().setComment(salesperson.getOrganisation().getComment());
		contract.getSeller().setCompanyId(salesperson.getOrganisation().getCompanyId());
		contract.getSeller().setCompanyName(salesperson.getOrganisation().getCompanyName());
		contract.getSeller().setPhone(salesperson.getOrganisation().getPhone());
		contract.getSeller().setPosition("");
		contract.getSeller().setZipCode(salesperson.getOrganisation().getZipCode());
		
		// To ensure that the entity is loaded correctly.
		// If all embedded values are NULL in the database, the embeddable object becomes null, and
		// we don't want that.
		contract.getCustomer().setName("");

		if (BusinessAreas.ONE_PLUS == businessArea.getBusinessAreaId()) {
			contract.setOfferIntroText(
					"Tak for en behagelig samtale.\n\n" +
							"Som lovet sender jeg dig her et tilbud på " + businessArea.getName() + ".\n\n" +
							"Dette tilbud er udarbejdet på baggrund af de oplysninger, som er modtaget fra jer og jeg håber, at tilbuddet matcher jeres behov for en løsning, tilpasset præcis jeres virksomhed.\n\n" +
							"Hvis du har ønsker til ændringer eller spørgsmål er du naturligvis velkommen til at kontakte mig.\n\n" +
							"Venlig hilsen");
		} else if (BusinessAreas.WIFI == businessArea.getBusinessAreaId()) {
			contract.setOfferIntroText(
					"Tak for en behagelig samtale.\n\n" +
							"Som lovet sender jeg dig her et prisoverslag på " + businessArea.getName() + ".\n\n" +
							"Dette prisoverslag er udarbejdet på baggrund af de oplysninger, som er modtaget fra jer og jeg håber, at prisoverslaget matcher jeres behov for en professionel Wi-Fi løsning.\n\n" +
							"Hvis du har ønsker til ændringer eller spørgsmål er du naturligvis velkommen til at kontakte mig.\n\n" +
							"Venlig hilsen");
		} else if (BusinessAreas.FIBER == businessArea.getBusinessAreaId()) {
			contract.setOfferIntroText(
					"Tak for en behagelig samtale.\n\n" +
							"Som lovet sender jeg dig her et tilbud på TDC Erhverv Fiber Plus.\n\n" +
							"Dette tilbud er udarbejdet på baggrund af de oplysninger, som er modtaget fra jer og jeg håber, at tilbuddet matcher jeres behov for en professionel fiberløsning, tilpasset præcis jeres virksomhed.\n\n" +
							"Hvis du har ønsker til ændringer eller spørgsmål er du naturligvis velkommen til at kontakte mig.\n\n" +
					"Venlig hilsen");
		} else if (BusinessAreas.FIBER_ERHVERV == businessArea.getBusinessAreaId()) {
			contract.setOfferIntroText(
					"Tak for en behagelig samtale.\n\n" +
							"Som lovet sender jeg dig her et tilbud på TDC Erhverv Fiber.\n\n" +
							"Dette tilbud er udarbejdet på baggrund af de oplysninger, som er modtaget fra jer og jeg håber, at tilbuddet matcher jeres behov for en professionel fiberløsning, tilpasset præcis jeres virksomhed.\n\n" +
							"Hvis du har ønsker til ændringer eller spørgsmål er du naturligvis velkommen til at kontakte mig.\n\n" +
					"Venlig hilsen");
		} else if (BusinessAreas.TDC_OFFICE == businessArea.getBusinessAreaId()) {
			contract.setOfferIntroText(
					"Tak for en behagelig samtale.\n\n" +
							"Som lovet sender jeg dig her et tilbud på TDC Erhverv Office in the Cloud.\n\n" +
							"Dette tilbud er udarbejdet på baggrund af de oplysninger, som er modtaget fra jer og jeg håber, at tilbuddet matcher jeres behov for en professional Office 365 løsning, tilpasset præcis jeres virksomhed.\n\n" +
							"Hvis du har ønsker til ændringer eller spørgsmål er du naturligvis velkommen til at kontakte mig.\n\n" +
					"Venlig hilsen");
		} else {
			contract.setOfferIntroText(
					"Tak for en behagelig samtale.\n\n" +
							"Som lovet sender jeg dig her et tilbud på " + businessArea.getName() + ".\n\n" +
							"Dette tilbud er udarbejdet på baggrund af de oplysninger, som er modtaget fra jer og jeg håber, at tilbuddet matcher jeres behov for en mobilløsning, tilpasset præcis jeres virksomhed.\n\n" +
							"Hvis du har ønsker til ændringer eller spørgsmål er du naturligvis velkommen til at kontakte mig.\n\n" +
					"Venlig hilsen");
		}

		// Pick the permanent campaign. If none exist, pick any campaign.
		if (businessArea.getCampaigns().size() == 0) {
			log.warn("Constructing contract for a businessarea without campaigns: " + businessArea.getName());
		} else {
			Campaign contractCampaign = null;
			List<Campaign> campaigns = campaignDao.findAvailableByBusinessArea(businessArea.getId());
			for(Campaign campaign : campaigns) {
				contractCampaign = campaign;
				if ((campaign.getFromDate() == null) && (campaign.getToDate() == null)) {
					break;		// stop looking
				} else {
					continue;	// keep looking
				}
			}
			contract.addCampaign(contractCampaign);  // we "add" and not "set" because core is designed for multiple campaigns pr. contract.

//			for (ProductBundle productBundle: contractCampaign.getProductBundles()) {
//				int subIndex = 0;
//				contract.addOrderLine(new OrderLine(productBundle, subIndex, 0, 0));
//			}
		}
		
		contract.setBusinessArea(businessArea);
		contract.setSalesperson(salesperson);
		contract.setTitle("");
		
		contract.initVariableValueMaps(contract.getCampaigns().get(0));
		
		contract.setRateMonths(36);
		contract.setSupportMonths(36);
		
		contractDao.save(contract);
//		contractSaver.save(contract);
			
		if (businessArea.getBusinessAreaId() == BusinessAreas.WIFI) {
			contract.setContractLength(2);
		}
		
		if (businessArea.getBusinessAreaId() != BusinessAreas.OLD_ONE) {
			if (contract.getBusinessArea().hasFeature(FeatureType.MOBILE_BUNDLES_MIX)) {
				// Add mix bundles
				List<Product> mixProducts = new ArrayList<>();
				for (Product product : productDao.findByBusinessAreaAndProductGroupUniqueName(businessArea.getId(), MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE.getKey())) {
					mixProducts.add(product); 
				}
				
				Product mixSpeechTimeProduct = null;
				for (Product product : productDao.findByBusinessAreaAndProductGroupUniqueName(businessArea.getId(), MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_SPEECH_TIME.getKey())) {
					mixSpeechTimeProduct = product;  // pick the last one - #todo #codesmell
				}
				mixProducts.add(mixSpeechTimeProduct);
				
				Product mixDataAmountProduct = null;
				for(Product product : productDao.findByBusinessAreaAndProductGroupUniqueName(businessArea.getId(), MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_DATA_AMOUNT.getKey())) {
					mixDataAmountProduct = product;  // pick the last one - #todo #codesmell
				}
				mixProducts.add(mixDataAmountProduct);
				
				for(Product product : productDao.findByBusinessAreaAndProductGroupUniqueName(businessArea.getId(), MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_INCLUDED.getKey())) {
					mixProducts.add(product);
				}
				
				addMixBundle(contract, mixProducts, 1);
				addMixBundle(contract, mixProducts, 2);
				addMixBundle(contract, mixProducts, 3);
			}

//			if (contract.getBusinessArea().hasFeature(FeatureType.USER_PROFILES)) {
//				List<UserProfileBundleData> userProfileBundles = Lists.newArrayList();
//				contract.getBusinessArea().getPermanentCampaign().getProductBundles().forEach(productBundle -> {
//					MobileProductBundle b = (MobileProductBundle) productBundle;
//					if (b.hasAnyProduct(p -> MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE.getKey().equals(p.getProductGroup().getUniqueName()))) {
//						UserProfileBundleData userProfile = new UserProfileBundleData();
//						userProfile.setBundleEntityId(productBundle.getId());
//						userProfileBundles.add(userProfile);
//					}
//				});
//				contract.setUserProfileBundles(userProfileBundles);
//			}

			if (contract.getBusinessArea().hasFeature(FeatureType.IPSA)) {
				SwitchboardIpsaDiscountScheme ipsaDiscountScheme = new SwitchboardIpsaDiscountScheme();
				ipsaDiscountScheme.setName("IPSA rabat");
//				ipsaDiscountScheme.setDiscountPercentages(new Amounts());
				contract.addDiscountScheme(ipsaDiscountScheme);
				
				contract.setContractType(MobileContractType.FIXED_DISCOUNT);
			}
			
			if (contract.getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CONTRACT_DISCOUNT)) {
				RabatAftaleDiscountScheme discountScheme = new RabatAftaleDiscountScheme();
				if (businessArea.getBusinessAreaId() == BusinessAreas.FIBER_ERHVERV) {
					discountScheme.setName("TDC Erhverv Rabataftale");
				} else {
					discountScheme.setName("TEM 5 kontraktrabat");
				}
				contract.addDiscountScheme(discountScheme);
			}
			
			if (businessArea.getBusinessAreaId() == BusinessAreas.TDC_OFFICE) {
				contract.setInstallationType(InstallationType.TDC_REMOTE);
				
				PasswordGenerator passwordGenerator = new PasswordGenerator.PasswordGeneratorBuilder()
						.useDigits(true)
						.useLower(true)
						.useUpper(true)
						.build();
				contract.setConfigurationPassword(passwordGenerator.generate(8));
				
				for (Product product : productDao.findByBusinessAreaAndProductGroupUniqueName(businessArea.getId(), MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BASIC.getKey())) {
					MobileProduct p = (MobileProduct) product;
					if (p.getDefaultCount() == 1) {
						// Mandatory product: Tenant
						contract.addOrderLine(new OrderLine(p, 0, 1, 0));
					}
				}
			}

//			if (businessArea.getBusinessAreaId() == BusinessAreas.ONE_PLUS) {
//				for (Product product : productDao.findByBusinessAreaAndProductGroupUniqueName(businessArea.getId(), MobileProductGroupEnum.PRODUCT_GROUP_INSTALLATIONTYPE.getKey())) {
//					MobileProduct p = (MobileProduct) product;
//					if (StringUtils.containsIgnoreCase(p.getFlags(), "type_oyo")) {
//						contract.setInstallationTypeBusinessEntityId(p.getId());
//						contract.setInstallationTypeUserProfilesEntityId(p.getId());
//						break;
//					}
//				}
//			}

//			contract.campaignChanged(contract.getCampaigns().get(0));	// Initialize stuff

			contractDao.save(contract);

			return contract;
		} 
		
		return null;
	}
	
	public Provider<Workbook> getContractsSpreadsheet() {
		return new ContractsSpreadsheet();
	}

	public Provider<Workbook> getUsersSpreadsheet() {
		return new UsersSpreadsheet();
	}

	public Provider<Workbook> getProductsSpreadsheet() {
		return new ProductsSpreadsheet();
	}

	private void addMixBundle(MobileContract contract, List<Product> products, int no) {
		MobileProductBundle mixBundle = new MobileProductBundle();
		mixBundle.setBundleType(MobileProductBundleEnum.MOBILE_BUNDLE);
		mixBundle.setPublicName("Mix " + no);
		mixBundle.setInternalName(mixBundle.getPublicName());
		mixBundle.setSortIndex(no);
		productBundleDao.save(mixBundle);
		
		int sortIndex = 0;
		for (Product product : products) {
			BundleProductRelation relation = new BundleProductRelation();
			relation.setProduct(product);
			relation.setSortIndex(++sortIndex);
			mixBundle.addProductRelation(relation);
		}
		
		contract.addBundle(mixBundle);
		int subIndex = 0;
		contract.addOrderLine(new OrderLine(mixBundle, subIndex, 0, 0));
	}

	@Override
	public Product createProduct() {
		return new MobileProduct();
	}

	@Override
	public ProductGroup createProductGroup() {
		return new MobileProductGroup();
	}

	public ProductGroup createProductGroup(MobileProductGroupEnum groupType) {
		return new MobileProductGroup(groupType.getDisplayText(), groupType.getKey());
	}
	
	@Override
	public ProductBundle createProductBundle() {
		return new MobileProductBundle();
	}

	@Override
	public Panel createEditProductPanel(CrudContext context, IModel<Product> childModel, IModel<ProductGroup> parentModel) {
		return new MobileEditProductPanel<>(context, childModel, parentModel);
	}

	@Override
	public Campaign createCampaign() {
		return new MobileCampaign();
	}
}
