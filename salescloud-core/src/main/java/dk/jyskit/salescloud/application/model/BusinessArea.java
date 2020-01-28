package dk.jyskit.salescloud.application.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import dk.jyskit.salescloud.application.services.accesscodes.AccessCodeChecker;
import dk.jyskit.waf.wicket.security.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import dk.jyskit.waf.application.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "businessarea")
@Data
@EqualsAndHashCode(callSuper=true, of={"name"})
@NoArgsConstructor
@Slf4j
public class BusinessArea extends BaseEntity {
	
	@NotNull @NonNull
	private String name; 
	
	@NotNull @NonNull
	@Column(length=5000)
	@Lob
	private String introText;
	
	private int businessAreaId;
	
	private boolean cumulativeDiscounts;
	
	private float provisionFactorGeneral;
	private float provisionFactorXDSL;
	
	@Column(length=2000)
	private String standardDiscountMatrix;

	@Column(length=2000)
	private String standardDiscountMatrixNetwork;

	@AttributeOverrides({
	    @AttributeOverride(name="name", column= @Column(name="b_name")),
	    @AttributeOverride(name="position", column= @Column(name="b_position")),
	    @AttributeOverride(name="companyName", column= @Column(name="b_companyName")),
	    @AttributeOverride(name="companyId", column= @Column(name="b_companyId")),
	    @AttributeOverride(name="phone", column= @Column(name="b_phone")),
	    @AttributeOverride(name="email", column= @Column(name="b_email")),
	    @AttributeOverride(name="comment", column= @Column(name="b_comment")),
	    @AttributeOverride(name="address", column= @Column(name="b_address")),
	    @AttributeOverride(name="zipCode", column= @Column(name="b_zipCode")),
	    @AttributeOverride(name="city", column= @Column(name="b_city"))
	  })
	@Embedded
	private BusinessEntity seller;   // TODO: ok?
	
	@OneToMany(mappedBy = "businessArea", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductGroup> productGroups = new ArrayList<>();
	
	@OneToMany(mappedBy = "businessArea", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductRelation> productRelations = new ArrayList<>();
	
	@OneToMany(mappedBy = "businessArea", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Campaign> campaigns = new ArrayList<>();

	@OneToMany(mappedBy = "businessArea", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PageInfo> pages = new ArrayList<>();
	
	@OneToMany(mappedBy = "businessArea", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Report> reports = new ArrayList<>();

	// WAF Pattern: collection of enums
	@ElementCollection(targetClass = FeatureType.class) 
	@CollectionTable(name = "BUS_FEATURE",
	    joinColumns = @JoinColumn(name = "BUSINESS_AREA_ID"))
	@Column(name = "FEATURE_ID")
	@Enumerated(EnumType.STRING)
	private List<FeatureType> features = new ArrayList<>();
	
	// Se kommentar øverst i DiscountScheme	
//	@OneToMany(mappedBy = "businessArea", cascade = CascadeType.ALL, orphanRemoval = true)
//	private List<DiscountScheme> discountSchemes = new ArrayList<>();

	// --------------------------------
	
	public void addPage(PageInfo pageInfo) {
		pages.add(pageInfo);
		pageInfo.setBusinessArea(this);
	}
	
	public void addProductGroup(ProductGroup productGroup) {
		productGroups.add(productGroup);
		productGroup.setBusinessArea(this);
	}
	
	public void addProductRelation(ProductRelation productRelation) {
		productRelations.add(productRelation);
		productRelation.setBusinessArea(this);
	}
	
	public void removeProductRelation(ProductRelation productRelation) {
		productRelations.remove(productRelation);
		productRelation.setBusinessArea(null);
	}
	
	public void addCampaign(Campaign campaign) {
		campaigns.add(campaign);
		campaign.setBusinessArea(this);
	}

	public void addReport(Report report) {
		reports.add(report);
		report.setBusinessArea(this);
	}


	// --------------------------------

	public boolean isActive() {
		return super.isActive();
	}

	@Transient
	public String getTypeId() {
		if (2 == businessAreaId) {
			return "OM";
		} else if (1 == businessAreaId) {
			return "MV";
		} else {
			return "?";
		}
	}

	@Transient
	public boolean isOneOf(int ... types) {
		for (int t: types) {
			if (businessAreaId == t) {
				return true;
			}
		}
		return false;
	}

	@Transient
	public Campaign getPermanentCampaign() {
		for (Campaign campaign : campaigns) {
			if ((campaign.getFromDate() == null) && (campaign.getToDate() == null)) {
				return campaign;
			}
		}
		return null;
	}
	
	@Transient
	public ProductGroup getProductGroupByName(String name) {
		for (ProductGroup productGroup : getProductGroups()) {
			ProductGroup matchingProductGroup = productGroup.getByName(name);
			if (matchingProductGroup != null) {
				return matchingProductGroup;
			}
		}
		return null;
	}

	@Transient
	public ProductGroup getProductGroupByFullPath(String path) {
		for (ProductGroup productGroup : getProductGroups()) {
			ProductGroup matchingProductGroup = productGroup.getByFullPath(path);
			if (matchingProductGroup != null) {
				return matchingProductGroup;
			}
		}
		return null;
	}

	@Transient
	public ProductGroup getProductGroupByUniqueName(String uniqueName) {
		for (ProductGroup productGroup : getProductGroups()) {
			ProductGroup matchingProductGroup = productGroup.getByUniqueName(uniqueName);
			if (matchingProductGroup != null) {
				return matchingProductGroup;
			}
		}
		return null;
	}
	
	@Transient
	public List<ProductGroup> getProductGroupsAndChildren() {
		List<ProductGroup> productGroupsAndChildren = new ArrayList<>();
		for (ProductGroup productGroup : getProductGroups()) {
			for (ProductGroup pg : productGroup.getAll()) {
				productGroupsAndChildren.add(pg);
			}
		}
		return productGroupsAndChildren;
	}

	@Transient
	public List<ProductGroup> getProductGroupChildren(String uniqueName) {
		ProductGroup parentGroup = getProductGroupByUniqueName(uniqueName);
		if (parentGroup == null) {
			return null;
		}
		return parentGroup.getChildProductGroups();
	}

	@Transient
	public ProductRelation getProductRelationByName(String displayName) {
		for (ProductRelation productRelation : productRelations) {
			if (displayName.equals(productRelation.getDisplayName())) {
				return productRelation;
			}
		} 
		return null;
	}
	
	@Transient
	public List<ProductRelation> getProductRelations(Product product, boolean considerFirstProduct, boolean considerOtherProducts) {
		List<ProductRelation> pr = new ArrayList<>();
		for (ProductRelation productRelation : productRelations) {
			int index = productRelation.getProducts().indexOf(product);
			if (index > -1) {
				if (considerFirstProduct && (index == 0)) {
					pr.add(productRelation);
				} else if (considerOtherProducts && (index > 0)) {
					pr.add(productRelation);
				}
			}
		} 
		return pr;
	}
	
	@Transient
	public List<ProductRelation> getProductRelations(Product product, boolean considerFirstProduct, boolean considerOtherProducts, long ... productRelationTypeIds) {
		List<ProductRelation> pr = new ArrayList<>();
		for (ProductRelation productRelation : productRelations) {
			if (ArrayUtils.contains(productRelationTypeIds, productRelation.getRelationTypeId().intValue())) {
				int index = productRelation.getProducts().indexOf(product);
				if (index > -1) {
					if (considerFirstProduct && (index == 0)) {
						pr.add(productRelation);
					} else if (considerOtherProducts && (index > 0)) {
						pr.add(productRelation);
					}
				}
			}
		} 
		return pr;
	}
	
	@Transient
	public Product getProductByProductId(String productId) {
		if (StringUtils.isEmpty(productId)) {
			return null;
		}
		for (ProductGroup productGroup : productGroups) {
			for (Product product : productGroup.getProducts()) {
				if (productId.equals(product.getProductId())) {
					return product;
				}
			} 
			for (ProductGroup childGroup : productGroup.getChildProductGroups()) {
				for (Product product : childGroup.getProducts()) {
					if (productId.equals(product.getProductId())) {
						return product;
					}
				} 
			}
		}
		log.warn("Not found: " + productId + " for businessarea " + getName());
		return null;
	}

	@Transient
	public Product getProductById(Long id) {
		if (id == null) {
			return null;
		}
		for (ProductGroup productGroup : productGroups) {
			for (Product product : productGroup.getProducts()) {
				if (id.equals(product.getId())) {
					return product;
				}
			}
			for (ProductGroup childGroup : productGroup.getChildProductGroups()) {
				for (Product product : childGroup.getProducts()) {
					if (id.equals(product.getId())) {
						return product;
					}
				}
			}
		}
		log.warn("Not found: " + id + " for businessarea " + getName());
		return null;
	}

	@Transient
	public boolean hasFeature(FeatureType type) {
		return features.contains(type);
	}

	@Transient
	public boolean hasFeature(FeatureType ... types) {
		for (FeatureType type : types) {
			if (hasFeature(type)) {
				return true;
			}
		}
		return false;
	}

	public void addFeatures(BusinessArea businessArea) {
		for (FeatureType featureType : businessArea.getFeatures()) {
			addFeature(featureType);
		}
	}

	public void addFeature(FeatureType feature) {
		if (!features.contains(feature)) {
			features.add(feature);
		}
	}

	public void removeFeature(FeatureType feature) {
		features.remove(feature);
	}

	// --------------------------------
	
	@Override
	public String toString() {
		return name;
	}

	public boolean isOnePlus() {
		return businessAreaId == 9; // From BusinessAreas
	}

	public boolean isWorks() {
		return businessAreaId == 6; // From BusinessAreas
	}

	public boolean isFiberErhverv() {
		return businessAreaId == 8; // From BusinessAreas
	}

	public boolean isFiberErhvervPlus() {
		return businessAreaId == 4; // From BusinessAreas
	}

	public boolean isOffice() {
		return businessAreaId == 7; // From BusinessAreas
	}
}