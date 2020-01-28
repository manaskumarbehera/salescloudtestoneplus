package dk.jyskit.salescloud.application.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.google.common.collect.Lists;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.MobileProductBundleDao;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.waf.utils.guice.Lookup;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Objects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Entity
@Data
@EqualsAndHashCode(callSuper=true, of={"productId"})
@Slf4j
public class MobileProductBundle extends ProductBundle implements MobileSortableItem {
	@Column(length=40)
	private String productId;    // productId / Nabs code
	
	@Column(length=200)
	private String kvikCode;
	
	@Column(length=150)
	private String textInOffer;
	
	private MobileProductBundleEnum bundleType;
	
	private long outputSortIndex;					// Sorting in CDM output
	private long offerSortIndex;  					// Sorting in Tilbud/overslag
	
	private boolean gks = false;
	
	// --------------------------------
	// Extra line (with discount) for CDM/Kvik output
	
	private boolean extraRowInOutput = false;
	
	@Column(length=100)
	private String extraRowInOutputCode;
	
	@Column(length=100)
	private String extraRowInOutputKvikCode;
	
	@Column(length=150)
	private String extraRowInOutputText;
	
	@Column(length=512)
	private String flags = "";		// Kommasepareret liste af specielle flag (default_on, read_only)

	// --------------------------------
	// Extra line (with discount) for offer
	
	private boolean extraRowInOffer = false;
	
	@Column(length=150)
	private String extraRowInOfferText;
	
	// --------------------------------
	
	@Transient
	public boolean hasFlag(String flag) {
		String[] flagsArray = StringUtils.split(this.flags, ",");
		if (flagsArray == null) {
			return false;
		}
		for (String f : flagsArray) {
			if (f.trim().equalsIgnoreCase(flag)) {
				return true;
			}
		}
		return false;
	}
	
//	@Transient
//	/* I'm not sure this is optimal! */
//	public boolean isStandardBundle() {
//		if (MobileSession.get().isBusinessAreaOnePlus()) {
//			return true;	// No Mix bundles
//		}
//		return isCampaignBundle();
//	}

	@Transient
	public boolean isInstallationHandledByTDC() {
		MobileContract contract = MobileSession.get().getContract();
		if (contract.getBusinessArea().isOnePlus()) {
			Product installationTypeProduct = null;
			if (getBundleType().equals(MobileProductBundleEnum.MOBILE_BUNDLE)) {
				if (contract.getInstallationTypeUserProfilesEntityId() != null) {
					installationTypeProduct = ProductDao.lookup().findById(contract.getInstallationTypeUserProfilesEntityId());
				}
			} else {
				if (contract.getInstallationTypeBusinessEntityId() != null) {
					installationTypeProduct = ProductDao.lookup().findById(contract.getInstallationTypeBusinessEntityId());
				}
			}
			if (installationTypeProduct != null) {
				if (installationTypeProduct.getProductId().indexOf("Partn") != -1) {
					return false;
				}
			}
		}
		return true;
	}

	@Transient
	public boolean isInstallationHandledByTDCErhvervscenter() {
		return !isInstallationHandledByTDC();
	}

	public Amounts adjustInstallation(boolean partner, Amounts amounts) {
		if (partner == isInstallationHandledByTDCErhvervscenter()) {
			return amounts;
		}
		Amounts a = amounts.clone();
		a.setInstallationFee(0);
		return a;
	}

	@Transient
	public boolean isMixBundle() {
		return isContractBundle();
	}
	
	// --------------------------------
	
	@Transient
	public String getTextForSorting() {
		if (StringUtils.isEmpty(getPublicName())) {
			return "ID: " + getProductId();
		}
		return getPublicName();
	}
	
	@Transient 
	public List<Provision> getProvisions(OrderLine orderLine) {
		// Only ONE product in a bundle can specify provisions
		
		// Special handling for omstilling :(
		if (getPublicName().indexOf("mstilling") != -1) {
			List<Provision> list = new ArrayList<>();
			for (BundleProductRelation relation : products) {
				MobileProduct product = (MobileProduct) relation.getProduct();
				if ((product != null) && (!relation.getProductAccessType().equals(ProductAccessType.SEPARATE_COUNT))) {
					if (product.getPublicName().indexOf("mstilling") != -1) {
						list.addAll(product.getProvisions(orderLine));
					} else if (product.getPublicName().indexOf("Installation ") == 0) {
						if (MobileSession.get().getContract().getContractMode().equals(MobileContractMode.NEW_SALE)) {
							if (MobileSession.get().getContract().isTdcInstallation()) {
								list.addAll(product.getProvisions(orderLine));
							}
						}
					}
				}
			}
			return list;
		}
		
		for (BundleProductRelation relation : products) {
			MobileProduct product = (MobileProduct) relation.getProduct();
			if ((product != null) && product.hasProvisions() && (!relation.getProductAccessType().equals(ProductAccessType.SEPARATE_COUNT))) {
				return Lists.newArrayList(product.getProvisions(orderLine));
			}
		}
		return new ArrayList<Provision>();
	}

	// --------------------------------

	public MobileProductBundle clone(boolean shallow) {
		MobileProductBundle newProductBundle = new MobileProductBundle();

		newProductBundle = MobileProductBundleDao.lookup().save(newProductBundle);
		MobileProductBundleDao.lookup().em().flush();

		newProductBundle.setActive(isActive());
		newProductBundle.setBaseAmounts(getBaseAmounts());
		newProductBundle.setDiscountAmounts(getDiscountAmounts().clone());
		newProductBundle.setTextInOffer(getTextInOffer());
		newProductBundle.setKvikCode(getKvikCode());
		newProductBundle.setProductId(getProductId());
		newProductBundle.setPublicName(getPublicName());
		newProductBundle.setInternalName(getInternalName());
		newProductBundle.setSortIndex(getSortIndex());
		newProductBundle.setAddProductPrices(isAddProductPrices());
		newProductBundle.setBundleType(getBundleType());
		newProductBundle.setCampaign(getCampaign());
		newProductBundle.setAddToContractDiscount(getAddToContractDiscount());
		newProductBundle.setExtraRowInOffer(isExtraRowInOffer());
		newProductBundle.setExtraRowInOutput(isExtraRowInOutput());
		newProductBundle.setGks(isGks());
		newProductBundle.setRabataftaleCampaignDiscountMatrix(getRabataftaleCampaignDiscountMatrix());
		newProductBundle.setRabataftaleCampaignDiscountMatrixNetwork(getRabataftaleCampaignDiscountMatrixNetwork());

		newProductBundle.setExtraRowInOutputText(getExtraRowInOutputText());
		newProductBundle.setFlags(getFlags());
		newProductBundle.setAddProductPricesToBundlePrice(isAddProductPricesToBundlePrice());

//		newProductBundle.setDiscountInternalName(discountInternalName);
//		newProductBundle.setDiscountKvikCode(discountKvikCode);

		if (!shallow) {
			cloneProductRelations(newProductBundle);
		}

//		MobileProductBundleDao.lookup().save(newProductBundle);

		return newProductBundle;
	}

	public void cloneProductRelations(MobileProductBundle targetProductBundle) {
		for (BundleProductRelation relation : getProducts()) {
			if (relation.getProduct() != null) {
//				BundleProductRelation newRelation = relation.clone();

				BundleProductRelation newRelation = new BundleProductRelation();
				newRelation.setProductBundle(targetProductBundle);
				if (targetProductBundle != null && targetProductBundle.getId() != null) {
					newRelation.setProductBundleId(targetProductBundle.getId());
				}
				newRelation.setProduct(relation.getProduct());
				newRelation.setProductId(relation.getProduct().getId());
				newRelation.setAddProductPrice(isAddProductPrices());
				newRelation.setSortIndex(relation.getSortIndex());
				newRelation.setProductAccessType(relation.getProductAccessType());

				targetProductBundle.getProducts().add(newRelation);
			}
		}
	}

	// --------------------------------

	public MobileProductBundle clone(BusinessArea targetBusinessArea) {
		MobileProductBundle newProductBundle = clone(true);
		Lookup.lookup(MobileProductBundleDao.class).save(newProductBundle);
		List<BundleProductRelation> newProductRelations = new ArrayList<BundleProductRelation>();
		for (BundleProductRelation relation : getProducts()) {
			if (relation.getProduct() != null) {
				BundleProductRelation newRelation = new BundleProductRelation();
				newRelation.setProductBundle(newProductBundle);
				Product p = targetBusinessArea.getProductByProductId(relation.getProduct().getProductId());
				if (p == null) {
					log.warn("Product not found: " + relation.getProduct().getProductId());
				}
				newRelation.setProduct(p);
				newRelation.setAddProductPrice(relation.isAddProductPrice());
				newRelation.setSortIndex(relation.getSortIndex());
				newProductRelations.add(newRelation);
			}
		}
		newProductBundle.setProducts(newProductRelations);
		return newProductBundle;
	}

	public boolean useInPoolsMode() {
		return hasAnyProduct(MobileProduct::isPoolModeBundle);
	}

	public boolean useInNonPoolsMode() {
		return hasAnyProduct(MobileProduct::isNonPoolModeBundle);
	}

	@Transient
	public boolean hasAnyProduct(Predicate<? super MobileProduct> predicate) {
		return products.stream()
				.map(bundleProductRelation -> (MobileProduct) bundleProductRelation.getProduct())
				.anyMatch(predicate);
	}

	@Transient
	public Optional<MobileProduct> getAnyProduct(Predicate<? super MobileProduct> predicate) {
		return products.stream()
				.map(bundleProductRelation -> (MobileProduct) bundleProductRelation.getProduct())
				.filter(predicate)
				.findAny();
	}

	@Transient
	public List<MobileProduct> getProducts(Predicate<? super MobileProduct> predicate) {
		return products.stream()
				.map(bundleProductRelation -> (MobileProduct) bundleProductRelation.getProduct())
				.filter(predicate)
				.collect(Collectors.toList());
	}

	@Transient
	public boolean isNetworkProductBundle() {
		return false;	// There are no network bundles
	}

	@Transient
	@Override
	public boolean isInstallationHandledByTdc(Integer subIndex) {
		MobileContract contract = MobileSession.get().getContract();
		if (bundleType.equals(MobileProductBundleEnum.HARDWARE_BUNDLE)) {
			return contract.isTdcInstallation();
		} else if (bundleType.equals(MobileProductBundleEnum.SWITCHBOARD_BUNDLE)) {
			return contract.isTdcInstallation();
		} else if (bundleType.equals(MobileProductBundleEnum.MOBILE_BUNDLE)) {
			return contract.isTdcInstallationUserProfiles();
		}
		return contract.isTdcInstallation();
	}

	// --------------------------------

	@Override
	public String toString() {
		return getPublicName();
	}
}
