package dk.jyskit.salescloud.application.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import dk.jyskit.salescloud.application.dao.ProductDao;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.utils.MapUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import static dk.jyskit.salescloud.application.model.MobileProductGroupEnum.*;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@Slf4j
public class MobileProduct extends Product implements MobileSortableItem {
	public static final String PRODUCT_EXTRA_PREFIX = "_extra_";

	@Column(length=200)
	private String kvikCode;
	
	private boolean ipsaDiscountEligible = false;
	
	private boolean gks = true;

	private boolean tdcInstallation;
	
	private boolean subscriberProduct;  			// If true, count=null means: all subscribers
	
	private boolean excludeFromConfigurator;  		// If true, product not in configurator
	
	private boolean excludeFromProductionOutput;  	// If true, product not in Nabs/Kvik/CDM output
	private boolean excludeFromOffer; 			 	// If true, product not offer output (only relevant for Fiber bundles - for now)
	
	private long outputSortIndex;					// Sorting in CDM output
	private long offerSortIndex;  					// Sorting in Tilbud/overslag
	
	private boolean variableInstallationFee; 		// Required for Installation - Diverse
	private boolean variableRecurringFee; 			// Required for partner hardware
	private boolean variableCategory; 				// Required for partner hardware: <category> - <product-name>
	private boolean variableProductName; 			// Required for partner hardware: <category> - <product-name>
	
	@Column(length=200)
	private String provisionOneTimeFee = "";
	
	@Column(length=200)
	private String provisionInstallationFee = "";
	
	@Column(length=200)
	private String provisionRecurringFee = "";
	
	@Column(length=512)
	private String remarks = "";		// Bemærkninger (garanti, el. lign.). Der kan angives flere, adskilt af semikolon.
	
	@Column(length=512)
	private String flags = "";		// Kommasepareret liste af specielle flag (default_on, read_only)

	@Column(length=100)
	private String filter = "";		// Kommasepareret liste af filtreringsregler (P_V1, P_V, I_V3, ...)

	@Column(length=20)
	private String filterID = "";		// eg. V1,V

	// --- One+ --- (start)

	private boolean poolModeBundle;
	private boolean nonPoolModeBundle;
	private int poolIndex;
	private String poolContributions;  // Comma separated list

	// --- One+ --- (end of)

	// --------------------------------
	
	@Transient
	public boolean hasFlag(String flag) {
		if (flags == null) {
			return false;
		}
		String[] flags = StringUtils.split(this.flags, ",");
		for (String f : flags) {
			if (f.trim().equalsIgnoreCase(flag)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String getPublicName() {
		if ((productId != null) && (productId.startsWith(PRODUCT_EXTRA_PREFIX))) {
			MobileContract contract = MobileSession.get().getContract();
			if (contract == null) {
				return "";
			} else {
				switch (productId) {
				case PRODUCT_EXTRA_PREFIX + "1":
					return contract.getExtraProduct1Text();
				case PRODUCT_EXTRA_PREFIX + "2":
					return contract.getExtraProduct2Text();
				case PRODUCT_EXTRA_PREFIX + "3":
					return contract.getExtraProduct3Text();
				default:
					return "";
				}
			}
		} else {
			return publicName;
		}
	}
	
	@Override
	public String getInternalName() {
		if ((productId != null) && (productId.startsWith(PRODUCT_EXTRA_PREFIX))) {
			MobileContract contract = MobileSession.get().getContract();
			if (contract == null) {
				return "";
			} else {
				switch (productId) {
				case PRODUCT_EXTRA_PREFIX + "1":
					return contract.getExtraProduct1Text();
				case PRODUCT_EXTRA_PREFIX + "2":
					return contract.getExtraProduct2Text();
				case PRODUCT_EXTRA_PREFIX + "3":
					return contract.getExtraProduct3Text();
				default:
					return "";
				}
			}
		} else {
			return internalName;
		}
	}
	
	/* 
	 * Take variable fees into account
	 */
	@Transient
	public Amounts getActualPrice() {
		Amounts modifiedPrice = getPrice();
		if (modifiedPrice == null) {
			log.warn("This is not necessarily good!");
			return new Amounts();
		} else {
			modifiedPrice = modifiedPrice.clone();
			if (variableInstallationFee) {
				MobileContract contract = MobileSession.get().getContract();
				Map<Long, Integer> idToPriceMap = MapUtils.stringToLongIntMap(contract.getVariableInstallationFees());
				Long idInMap = id;
				if (MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_CABLING.getKey().equals(getProductGroup().getUniqueName())) {
					idInMap = id * 100 + MobileSession.get().getPricingSubIndex();
				}
				Integer amount = idToPriceMap.get(idInMap);
				
				// amount can be null in the case of cabling for locations where this particular type of cabling was not used - but is is for another location.
				if (amount != null) {
					modifiedPrice.setInstallationFee(amount * 100);
				}
			}
			if (variableRecurringFee) {
				MobileContract contract = MobileSession.get().getContract();
				Map<Long, Integer> idToPriceMap = MapUtils.stringToLongIntMap(contract.getVariableRecurringFees());
				Integer amount = idToPriceMap.get(id);
				modifiedPrice.setRecurringFee(amount * 100);
			}
		} 
		
		return modifiedPrice;  
	}
	
	/* 
	 * Take variable category/name into account
	 */
	@Transient
	public String getActualName() {
		if (variableProductName) {
			String category = publicName;
			MobileContract contract = MobileSession.get().getContract();
			if (variableCategory) {
				Map<Long, String> idToCategoryMap = MapUtils.stringToLongStringMap(contract.getVariableCategories());
				category = idToCategoryMap.get(id);
			}
			Map<Long, String> idToProductNameMap = MapUtils.stringToLongStringMap(contract.getVariableProductNames());
			String name = idToProductNameMap.get(id);
			if (StringUtils.isEmpty(name)) {
				return category;
			} else {
				// Major hack
				if ("Installation - Diverse".equals(internalName)) {
					return name;
				}
				return category + " - " + name;  
			}
		} else {
			return publicName;
		}
	}
	
	@Transient
	public String getInternalNameRaw() {
		return internalName;
	}
	
	@Override
	public Amounts getPrice() {
		if ((productId != null) && (productId.startsWith(PRODUCT_EXTRA_PREFIX))) {
			MobileContract contract = MobileSession.get().getContract();
			if (contract == null) {
				log.warn("Is this supposed to happen?");
				return null;
			} else {
				switch (productId) {
				case PRODUCT_EXTRA_PREFIX + "1":
					return new Amounts(100l * contract.getExtraProduct1OneTimeFee(), 100l * contract.getExtraProduct1InstallationFee(), 100l * contract.getExtraProduct1RecurringFee());
				case PRODUCT_EXTRA_PREFIX + "2":
					return new Amounts(100l * contract.getExtraProduct2OneTimeFee(), 100l * contract.getExtraProduct2InstallationFee(), 100l * contract.getExtraProduct2RecurringFee());
				case PRODUCT_EXTRA_PREFIX + "3":
					return new Amounts(100l * contract.getExtraProduct3OneTimeFee(), 100l * contract.getExtraProduct3InstallationFee(), 100l * contract.getExtraProduct3RecurringFee());
				default:
					return null;
				}
			}
		} else {
//			if (productId != null) {
//				MobileContract contract = MobileSession.get().getContract();
//				if (contract != null) {
//					for (CampaignProductRelation campaignProductRelation : contract.getCampaigns().get(0).getCampaignProducts()) {
//						if (productId.equals(campaignProductRelation.getProduct().getProductId())) {
//							// TODO: er dette det rette sted, eller er det i OrderLine?
//							return campaignProductRelation.getBaseAmounts();
//						}
//					}
//				}
//			}
			
			return price;
		}
	}
	
	@Transient
	@Override
	public Amounts getCampaignDiscounts(Contract contract, OrderLineCount count) {
		CampaignProductRelation campaignProductRelation = ((MobileContract) contract).getCampaignProductRelation(this);
		
		if (campaignProductRelation != null) {
			if (contract.getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CAMPAIGN_DISCOUNT)) {
				// We have to try to calculate campaign discount on products - using the matrix from the bundle.
				String rabataftaleCampaignDiscountMatrix = null;
				String networkCampaignDiscountMatrix = null;
//				if (bundle != null) {
//					rabataftaleCampaignDiscountMatrix = bundle.getRabataftaleCampaignDiscountMatrix();
//				}

				if (StringUtils.isEmpty(rabataftaleCampaignDiscountMatrix)) {
					rabataftaleCampaignDiscountMatrix = campaignProductRelation.getRabataftaleCampaignDiscountMatrix();
				}
				Amounts nonRecurringDiscounts = campaignProductRelation.getCampaignDiscountAmounts().clone();
				nonRecurringDiscounts.multiplyBy(count);
				
				if (StringUtils.isEmpty(rabataftaleCampaignDiscountMatrix)) {
					return nonRecurringDiscounts;
				} else {
					Amounts amounts = getPrice().clone();
					amounts.multiplyBy(count);
					return contract.getRabataftaleCampaignDiscounts(amounts, rabataftaleCampaignDiscountMatrix, nonRecurringDiscounts, false);	// CHECKMIG
				}
			} else {
				// Non-TEM5 campaign discount - i.e. "the old" campaign discount strategy
				Amounts discounts = campaignProductRelation.getCampaignDiscountAmounts().clone();
				
//				discounts.multiplyBy(count.getCountTotal());
//				// Jeg troede at kampagne rabatter skulle beregnes ud fra NYE produkter/pakker, men iflg. Anja skal rabatten
//				// beregnes på nye og genforhandlede.
//				// discounts.multiplyBy(count.getCountNew());
					
				discounts.multiplyBy(count);
				return discounts;
			}
		}
		return new Amounts();
	}
	
	// --------------------------------
	
	public String getNabsCode() {
		return productId;
	}
	
	public void setNabsCode(String nabsCode) {
		productId = nabsCode;
	}
	
	public int getBusinessValueInstallationFee() {
		return (int) price.getInstallationFee() / 100;
	}
	
	public int getBusinessValueOneTimeFee() {
		return (int) price.getOneTimeFee() / 100;
	}
	
	public int getBusinessValueRecurringFee() {
		return (int) (price.getRecurringFee() * 6) / 100;
	}
	
	// --------------------------------

	@Transient
	public boolean isInstallationHandledByTDC() {
		MobileContract contract = MobileSession.get().getContract();
		if (contract.getBusinessArea().isOnePlus()) {
			Product installationTypeProduct = null;
			if (getProductGroup().getUniqueName().startsWith(MobileProductGroupEnum.PRODUCT_GROUP_USER_ADDON.getKey())) {
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

	@Transient
	public String getTextForSorting() {
		if (StringUtils.isEmpty(getPublicName())) {
			return "ID: " + getProductId();
		}
		return getPublicName();
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
	public boolean hasProvisions() {
		return !StringUtils.isEmpty(provisionOneTimeFee) || !StringUtils.isEmpty(provisionInstallationFee) || !StringUtils.isEmpty(provisionRecurringFee);
	}
	
	@Transient 
	public List<Provision> getProvisions(OrderLine orderLine) {
		List<Provision> provisions = new ArrayList<>();
		MobileContract contract = (MobileContract) orderLine.getContract();
		try {
			if (contract.getSegment() != null || BusinessAreas.WIFI == getBusinessArea().getBusinessAreaId()) {
				for (FeeCategory feeCategory : new FeeCategory[] {FeeCategory.INSTALLATION_FEE, FeeCategory.ONETIME_FEE, FeeCategory.RECURRING_FEE}) {
					switch (feeCategory) {
					case ONETIME_FEE:
						if (!StringUtils.isEmpty(provisionOneTimeFee)) {
							Provision provision = new Provision();
							provision.setCount(orderLine.getDeferredCount().getCountNew());
							provision.setText(getInternalName());
							String provisionRule;
							if (BusinessAreas.WIFI == getBusinessArea().getBusinessAreaId()) {
								provisionRule = provisionOneTimeFee.trim();
							} else {
								String[] provisionRules = StringUtils.split(provisionOneTimeFee, ' ');
								provisionRule = provisionRules[contract.getSegment().getCsvIndex()].trim();
							}
							long p = 0;
							if (provisionRule.endsWith("%")) {
								provision.setType(Provision.TYPE_SATS);
								int pct = Integer.valueOf(StringUtils.removeEnd(provisionRule, "%"));
								p = getBusinessValueOneTimeFee() * pct;
							} else {
								provision.setType(Provision.TYPE_STYK);
								p = 100 * Integer.valueOf(provisionRule);
							}
							provision.getAmounts().add(new Amounts(provision.getCount() * p, 0, 0));
							provisions.add(provision);
						}
						break;
					case INSTALLATION_FEE:
						if (!StringUtils.isEmpty(provisionInstallationFee)) {
							Provision provision = new Provision();
							provision.setCount(orderLine.getDeferredCount().getCountNew());
							provision.setText(getInternalName());
							String provisionRule;
							if (BusinessAreas.WIFI == getBusinessArea().getBusinessAreaId()) {
								provisionRule = provisionInstallationFee.trim();
							} else {
								String[] provisionRules = StringUtils.split(provisionInstallationFee, ' ');
								provisionRule = provisionRules[contract.getSegment().getCsvIndex()].trim();
							}
							long p = 0;
							if (provisionRule.endsWith("%")) {
								provision.setType(Provision.TYPE_SATS);
								int pct = Integer.valueOf(StringUtils.removeEnd(provisionRule, "%"));
								p = getBusinessValueInstallationFee() * pct;
							} else {
								provision.setType(Provision.TYPE_STYK);
								p = 100 * Integer.valueOf(provisionRule);
							}
							provision.getAmounts().add(new Amounts(0, provision.getCount() * p, 0));
							provisions.add(provision);
						}
						break;
					case RECURRING_FEE:
						if (!StringUtils.isEmpty(provisionRecurringFee)) {
							Provision provision = new Provision();
							provision.setCount(orderLine.getDeferredCount().getCountNew());
							provision.setText(getInternalName());
							String provisionRule;
							if (BusinessAreas.WIFI == getBusinessArea().getBusinessAreaId()) {
								provisionRule = provisionRecurringFee.trim();
							} else {
								String[] provisionRules = StringUtils.split(provisionRecurringFee, ' ');
								provisionRule = provisionRules[contract.getSegment().getCsvIndex()].trim();
							}
							long p = 0;
							if (provisionRule.endsWith("%")) {
								provision.setType(Provision.TYPE_SATS);
								int pct = Integer.valueOf(StringUtils.removeEnd(provisionRule, "%"));
								p = getBusinessValueRecurringFee() * pct;
							} else {
								provision.setType(Provision.TYPE_STYK);
								p = 100 * Integer.valueOf(provisionRule);
							}
							provision.getAmounts().add(new Amounts(0, 0, provision.getCount() * p));
							provisions.add(provision);
						}
						break;
						
					default:
						break;
					}
				}
			}
		} catch (Exception e) {
			log.error("Unable to calculate provision for product " + getInternalName() + ". Segment is " + contract.getSegment().getName());
		}
		return provisions;
	}
	
	// --------------------------------
	
	public boolean isInGroup(MobileProductGroupEnum ... groups) {
		for (MobileProductGroupEnum group : groups) {
			if (getProductGroup().getUniqueName().equals(group.getKey())) {
				return true;
			}
		}
		return false;
	}

	public boolean isInGroupOrChildGroups(MobileProductGroupEnum productGroupEnum) {
		MobileProductGroup pg = (MobileProductGroup) getBusinessArea().getProductGroupByUniqueName(productGroupEnum.getKey());
		if (pg == null) {
			log.warn("Unknown group: " + productGroupEnum);
			return false;
		} else {
			if (isInGroup(productGroupEnum)) {
				return true;
			}
			for (ProductGroup cpg : pg.getChildProductGroups()) {
				if (isInGroup(MobileProductGroupEnum.getValueByKey(cpg.getUniqueName()))) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isAddOn() {
		return (isInGroup(MobileProductGroupEnum.PRODUCT_GROUP_ADDON_FUNCTIONS) ||
				isInGroup(MobileProductGroupEnum.PRODUCT_GROUP_ADDON_ROAMING) ||
				isInGroup(MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_SPEECH) ||
				isInGroup(MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_SPEECH_TIME) ||
				isInGroup(MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_DATA) ||
				isInGroup(MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_DATA_AMOUNT) ||
				isInGroup(MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_ADDON));
	}
	
	public boolean isExtraProduct() {
		return (productId != null) && productId.startsWith(PRODUCT_EXTRA_PREFIX);
	}

	// --------------------------------

	public MobileProduct clone() {
		MobileProduct p = new MobileProduct();

		p.setEntityState(getEntityState());
		p.setState(getState());
		p.setPublicName(publicName);
		p.setInternalName(internalName);
		p.setProductId(productId);
		p.setSortIndex(getSortIndex());
		p.setPrice(price);
		p.setBusinessArea(getBusinessArea());
		p.setProductGroup(getProductGroup());
//		for (ProductionItem pi: getProductionItems()) {
//		}
		p.setPaymentFrequency(getPaymentFrequency());
		p.setDefaultCount(getDefaultCount());
		p.setMinCount(getMinCount());
		p.setMaxCount(getMaxCount());
		p.setDiscountEligible(isDiscountEligible());
//		for (DiscountScheme s: getDiscountSchemes()) {
//		}
		p.setRabataftaleDiscountEligible(isRabataftaleDiscountEligible());

		p.setKvikCode(kvikCode);
		p.setIpsaDiscountEligible(ipsaDiscountEligible);
		p.setGks(gks);
		p.setTdcInstallation(tdcInstallation);
		p.setSubscriberProduct(subscriberProduct);
		p.setExcludeFromConfigurator(excludeFromConfigurator);
		p.setExcludeFromProductionOutput(excludeFromProductionOutput);
		p.setExcludeFromOffer(excludeFromOffer);
		p.setOutputSortIndex(outputSortIndex);
		p.setOfferSortIndex(offerSortIndex);
		p.setVariableRecurringFee(variableRecurringFee);
		p.setVariableInstallationFee(variableInstallationFee);
		p.setVariableCategory(variableCategory);
		p.setVariableProductName(variableProductName);
		p.setProvisionOneTimeFee(provisionOneTimeFee);
		p.setProvisionInstallationFee(provisionInstallationFee);
		p.setProvisionRecurringFee(provisionRecurringFee);
		p.setRemarks(remarks);
		p.setFlags(flags);
		p.setFilter(filter);
		p.setFilterID(filterID);

		return p;
	}

	// --------------------------------

	@Transient
	public long getGroupAndProductOutputSortIndex() {
		long l = 0;
		if (getProductGroup() != null) {
			l = 1000 * ((MobileProductGroup) getProductGroup()).getOutputSortIndex();
		}
		return l + outputSortIndex;
	}

	@Transient
	public boolean isNetworkProduct() {
		return (isInGroupOrChildGroups(MobileProductGroupEnum.PRODUCT_GROUP_LOCATIONS)
			|| isInGroupOrChildGroups(MobileProductGroupEnum.PRODUCT_GROUP_ACCESS)
			|| isInGroupOrChildGroups(MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE)
			|| isInGroupOrChildGroups(MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE)
		);
	}

	@Transient
	@Override
	public boolean isInstallationHandledByTdc(Integer subIndex) {
		MobileContract contract = MobileSession.get().getContract();
		if (getProductGroup().getUniqueName().startsWith(PRODUCT_GROUP_LOCATIONS_INSTALLATION.getKey())) {
			if (subIndex == null) {
				log.info("How is this possible? " + subIndex);
			} else {
				LocationBundleData locationBundle = contract.getLocationBundle(subIndex);
				return locationBundle.isTDCInstallationProvider();
			}
		} else if (getProductGroup().getUniqueName().startsWith(PRODUCT_GROUP_LOCATIONS.getKey())) {
			if (subIndex == null) {
				log.info("How is this possible? " + subIndex);
			} else {
				LocationBundleData locationBundle = contract.getLocationBundle(subIndex);
				return locationBundle.isTDCHardwareProvider();
			}
		} else if (getProductGroup().getUniqueName().startsWith(PRODUCT_GROUP_STANDARD_BUNDLE.getKey())) {
			return contract.isTdcInstallationUserProfiles();
		} else {
			return contract.isTdcInstallation();
		}
		return true;
	}

	public void addToProductToCountsMap(Map<Product, List<CountAndInstallation>> productToCountsMap, Integer countNew, Integer countExisting, Integer subIndex) {
		if (countExisting == null) {
			// avoid null pointer exceptions
			countExisting = 0;
		}
		if ((countNew > 0) || (countExisting > 0)) {
			List<CountAndInstallation> countAndInstallations = productToCountsMap.get(this);
			if (countAndInstallations == null) {
				countAndInstallations = new ArrayList<>();
			}
			CountAndInstallation countAndInstallation = new CountAndInstallation();
			countAndInstallation.setCountNew(countNew == null ? 0 : countNew.intValue());
			countAndInstallation.setCountExisting(countExisting == null ? 0 : countExisting.intValue());
			countAndInstallation.setSubIndex(subIndex);
			countAndInstallations.add(countAndInstallation);
			productToCountsMap.put(this, countAndInstallations);
		}
	}

	// --------------------------------
	
	@Override
	public String toString() {
		return getPublicName();
	}
}
