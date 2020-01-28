package dk.jyskit.salescloud.application;

import java.io.File;

import dk.jyskit.salescloud.application.model.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.rjeschke.txtmark.Processor;
import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.BusinessAreaDao;
import dk.jyskit.salescloud.application.dao.MobileProductDao;
import dk.jyskit.salescloud.application.dao.PageInfoDao;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.dao.ProductGroupDao;
import dk.jyskit.salescloud.application.dao.ProductRelationDao;
import dk.jyskit.salescloud.application.dao.SubscriptionDao;
import dk.jyskit.salescloud.application.extensions.MobileObjectFactory;
import dk.jyskit.waf.application.dao.RoleDao;
import dk.jyskit.waf.application.dao.UserDao;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public abstract class BusinessAreaInitializer implements Initializer {
	@Inject private BusinessAreaDao businessAreaDao;
	@Inject protected ProductDao coreProductDao;
	@Inject protected MobileProductDao mobileProductDao;
	@Inject protected SubscriptionDao subscriptionDao;
	@Inject private PageInfoDao pageInfoDao;
	@Inject private ProductGroupDao productGroupDao;
	@Inject private ProductRelationDao productRelationDao;
	@Inject private UserDao userDao;
	@Inject private RoleDao roleDao;
	@Inject private MobileObjectFactory objectFactory;
	
	private String name;
	protected int businessAreaId;
	protected String businessAreaName;
	protected String introText;
	protected BusinessArea businessArea;

	public BusinessAreaInitializer(String name, int id, String businessAreaName, String introText) {
		this.name = name;
		this.businessAreaId = id;
		this.businessAreaName = businessAreaName;
		this.introText = introText;
	}
	
	@Override
	public boolean needsInitialization() {
		for (BusinessArea businessArea : businessAreaDao.findAll()) {
			if (businessAreaId == businessArea.getBusinessAreaId()) {
				this.businessArea = businessArea; 
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void initialize() {
		initBusinessArea();
		initPages(businessArea);
		initProducts(businessArea);
		initTestContracts(businessArea);
	}
	
	public void initBusinessArea() {
		businessArea = new BusinessArea();
		businessArea.setBusinessAreaId(businessAreaId);
		businessArea.setName(businessAreaName);
		businessArea.setIntroText(introText);
		businessArea.setProvisionFactorGeneral(1.0f);
		businessAreaDao.save(businessArea);
	}

	// --------------------------------
	
	public abstract void initProducts(BusinessArea businessArea);
	public abstract void initTestContracts(BusinessArea businessArea);
	public abstract void initPages(BusinessArea businessArea);

	// --------------------------------
	
	protected void createPage(BusinessArea businessArea, String pageId, String title, String subTitle, String introText) {
		createPage(businessArea, pageId, title, subTitle, introText, 
				"De enkelte trin i guiden vil altid være mulige at navigere i via menupunkterne, som ligger i toppen af konfiguratoren.\n\n" +
				"Branchebeskrivelser\n\n" +
				"  1. El - og VVS installatører\nTekst\n" +
				"  2. Murer og Tømrer\nTekst\n" +
				"  3. Detail-handel\nTekst\n" +
				"  4. Engros-handel\nTekst\n" +
				"  5. Rådgivning\nTekst\n" +
				"  6. IT-rådgivning\nTekst\n" +
				"  7. Transport og logistik\nTekst\n" +
				"  8. Real Estate\nTekst\n");
	}

	protected void createPage(BusinessArea businessArea, String pageId, String title, String subTitle, String introText, String helpText) {
		PageInfo pageInfo = new PageInfo(pageId, title, businessArea);
		pageInfo.setSubTitle(subTitle);
		pageInfo.setIntroMarkdown(introText);
		pageInfo.setIntroHtml(Processor.process(pageInfo.getIntroMarkdown()));
//		http://perspektiv.tdc.dk/personlig-telefonservice-er-penge-vaerd/		
		pageInfo.setMiscMarkdown(getMiscMarkdown());		
		pageInfo.setMiscHtml(Processor.process(pageInfo.getMiscMarkdown()));
		
		pageInfo.setHelpMarkdown(helpText);
		pageInfo.setHelpHtml(pageInfo.getHelpMarkdown() == null ? "" : Processor.process(pageInfo.getHelpMarkdown()));
		
		businessArea.addPage(pageInfo);

		modifyPage(pageInfo);

		log.info("Saving page: " + pageInfo.getTitle());
		pageInfoDao.save(pageInfo);
	}

	protected void modifyPage(PageInfo pageInfo) {
	}

	protected abstract String getMiscMarkdown();

	protected ProductGroup createProductGroup(Object parent, MobileProductGroupEnum groupId) {
		ProductGroup productGroup = productGroupDao.findByBusinessAreaAndUniqueName(businessArea, groupId.getKey());
		if (productGroup == null) {
			productGroup = objectFactory.createProductGroup(groupId);
			productGroupDao.save(productGroup);
			if (parent instanceof BusinessArea) {
				((BusinessArea) parent).addProductGroup(productGroup);
			} else {
				((ProductGroup) parent).addProductGroup(productGroup);
			}
			productGroup.setSortIndex(10 + ArrayUtils.indexOf(MobileProductGroupEnum.values(), groupId));

//			for (MobileProductGroupEnum childGroup: MobileProductGroupEnum.getByPrefix(groupId. .name() + "_")) {
//				createProductGroup(bundleGroup, childGroup);
//			}

		}
		return productGroup;
	}

	protected void addProductWithNabsAndKvikCode(ProductGroup productGroup, String nabsCode, String kvikCode, String publicName, String internalName, double oneTimeFee, double recurringFee, double installationFee, 
			String scaleType, String discountType, int ipsaSum) {
		MobileProduct product = new MobileProduct();
		productGroup.addProduct(product);
		product.setPublicName(publicName);
		product.setInternalName(internalName);
		product.setNabsCode(nabsCode);
		product.setKvikCode((StringUtils.isEmpty(kvikCode) ? internalName : kvikCode));
		product.setPrice(new Amounts(Math.round(oneTimeFee * 100), Math.round(installationFee * 100), Math.round(recurringFee * 100)));
		product.initSortIndex();
		product.setDiscountEligible(!discountType.equals("Ikke rabat"));
		mobileProductDao.save(product);
	}
	
	protected void addProductWithNabsAndKvikCode(ProductGroup productGroup, String productId, String kvikCode, String publicName, String internalName, 
			long oneTimeFee, long recurringFee, long installationFee, boolean discountEligible, boolean ipsaDiscountEligible) {
		MobileProduct product = new MobileProduct();
		productGroup.addProduct(product);
		product.setPublicName(publicName);
		product.setInternalName(internalName);
		product.setProductId(productId);
		product.setKvikCode((StringUtils.isEmpty(kvikCode) ? internalName : kvikCode));
		product.setPrice(new Amounts(oneTimeFee, installationFee, recurringFee));
		product.setDiscountEligible(discountEligible);
		product.setIpsaDiscountEligible(ipsaDiscountEligible);
		product.initSortIndex();
		mobileProductDao.save(product);
	}
	
	protected MobileProduct addProductWithProductId(ProductGroup productGroup, String productId, String publicName, String internalName,
			long oneTimeFee, long recurringFee, long installationFee, boolean discountEligible, boolean ipsaDiscountEligible, 
			boolean inProductionOutput, boolean inOffer, int min, int max) {
		return addProductWithProductId(productGroup, productId, publicName, internalName, oneTimeFee, recurringFee, installationFee, 
				discountEligible, ipsaDiscountEligible, inProductionOutput, inOffer, min, max, null);
	}
	
	protected MobileProduct addProductWithProductId(ProductGroup productGroup, String productId, String publicName, String internalName, 
			long oneTimeFee, long recurringFee, long installationFee, boolean discountEligible, boolean ipsaDiscountEligible, 
			boolean inProductionOutput, boolean inOffer, int min, int max, String remarks) {
		MobileProduct product = new MobileProduct();
		productGroup.addProduct(product);
		product.setPublicName(publicName);   // Used as category!
		product.setInternalName(internalName);
		product.setVariableCategory(StringUtils.isEmpty(publicName));
		if (MobileProductGroupEnum.PRODUCT_GROUP_PARTNER_HARDWARE.getKey().equals(productGroup.getUniqueName()) || 
			(MobileProductGroupEnum.PRODUCT_GROUP_PARTNER_INSTALLATION.getKey().equals(productGroup.getUniqueName()) && publicName.contains("Diverse"))) {
			product.setVariableProductName(true);
		}
		
		product.setProductId(productId);
		product.setPrice(new Amounts(oneTimeFee, installationFee, recurringFee));
		product.setDiscountEligible(discountEligible);
		product.setIpsaDiscountEligible(ipsaDiscountEligible);
		product.setExcludeFromProductionOutput(!inProductionOutput);
		product.setExcludeFromOffer(!inOffer);
		product.initSortIndex();
		product.setMinCount(min);
		product.setMaxCount(max);
		product.setRemarks(remarks);
		mobileProductDao.save(product);
		return product;
	}
	
	protected void moveProductToAnotherGroup(Long businessAreaId, String productId, ProductGroup oldProductGroup, ProductGroup newProductGroup) {
		Product product = coreProductDao.findByProductGroupAndProductId(businessArea.getId(), oldProductGroup.getUniqueName(), productId);
		if (oldProductGroup.getProducts().contains(product)) {
			oldProductGroup.removeProduct(product);
			newProductGroup.addProduct(product);
			
			productGroupDao.save(oldProductGroup);
			productGroupDao.save(newProductGroup);
			coreProductDao.save(product);
		}
	}

	protected void addProductToBundle(MobileProductBundle bundle, Product product, boolean addProductPrice) {
		addProductToBundle(bundle, product, addProductPrice, ProductAccessType.INCLUDED);
	}

	protected void addProductToBundle(MobileProductBundle bundle, Product product, boolean addProductPrice,
									  ProductAccessType productAccessType) {
		BundleProductRelation relation = new BundleProductRelation();
		relation.setProduct(product);
		relation.setAddProductPrice(addProductPrice);
		relation.setSortIndex(1l);
		relation.setProductAccessType(productAccessType);
		bundle.addProductRelation(relation);
	}

//	protected void addProductRelation(BusinessArea businessArea, long productRelationTypeId, MobileProductGroupEnum group, String ... productIds) {
//		addProductRelation(businessArea, null, productRelationTypeId, group, productIds);
//	}

	protected void addProductRelation(BusinessArea businessArea, String displayName, long productRelationTypeId, MobileProductGroupEnum group, String ... productIds) {
		ProductRelation productRelation = new ProductRelation();
		productRelation.setBusinessArea(businessArea);
		productRelation.setRelationTypeId(productRelationTypeId);
		productRelation.setDisplayName(displayName);
		
		for(String productId: productIds) {
			ProductGroup productGroup = businessArea.getProductGroupByUniqueName(group.getKey());
			for (Product p : productGroup.getProducts()) {
				if (productId.equals(p.getProductId())) {
					productRelation.addProduct(p);
				}
			}
		}
		
		productRelationDao.save(productRelation);
		businessArea.addProductRelation(productRelation);
	}

	protected void addProductRelation(BusinessArea businessArea, long productRelationTypeId, String ... productIds) {
		addProductRelation(businessArea, null, productRelationTypeId, productIds);
	}

	protected void addProductRelation(BusinessArea businessArea, String displayName, long productRelationTypeId, String ... productIds) {
		ProductRelation productRelation = new ProductRelation();
		productRelation.setBusinessArea(businessArea);
		productRelation.setRelationTypeId(productRelationTypeId);
		productRelation.setDisplayName(displayName);

		for(String productId: productIds) {
			for(ProductGroup productGroup: businessArea.getProductGroupsAndChildren()) {
				for (Product p : productGroup.getProducts()) {
					if (productId.equals(p.getProductId())) {
						productRelation.addProduct(p);
					}
				}
			}
		}

		productRelationDao.save(productRelation);
		businessArea.addProductRelation(productRelation);
	}

	protected File getFileFromClasspath(String path) {
		// Get file from resources folder
		return new File(getClass().getClassLoader().getResource(path).getFile());
	}

}
