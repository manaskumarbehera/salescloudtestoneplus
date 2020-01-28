package dk.jyskit.salescloud.application.model;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.apache.commons.lang3.StringUtils;

import dk.jyskit.waf.application.model.BaseEntity;

@Entity(name="ProductGroup")
@Table(name = "productgroup")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@EqualsAndHashCode(callSuper=true, of={"name"})
@NoArgsConstructor
public class ProductGroup extends BaseEntity {
	// Display name (may include spaces, etc)
	@NonNull @NotNull
	protected String name;
	
	private String uniqueName;  // Name may not be unique
	
	private long sortIndex;
	
//	private String fullPathSortIndex;

	@Column(length=10000)
	private String helpMarkdown;
  
	@Column(length=10000)
	private String helpHtml;

	@OneToMany(mappedBy = "productGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Product> products = new ArrayList<Product>();

	@ManyToOne(optional = false)
	private BusinessArea businessArea;
	
	@ManyToOne(optional = true)
	private ProductGroup parentProductGroup;
	
	@OneToMany(mappedBy = "parentProductGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductGroup> childProductGroups = new ArrayList<ProductGroup>();
	
	@ManyToMany
	@JoinTable(name = "productgroup_discountscheme", joinColumns = { @JoinColumn(name = "productgroup_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "discountscheme_id", referencedColumnName = "id") })
	private List<DiscountScheme> discountSchemes;

	// --------------------------------
	
	public ProductGroup(String name) {
		this.name = name;
	}
	
	public ProductGroup(String name, String uniqueName) {
		this.name = name;
		this.uniqueName = uniqueName;
	}
	
	// --------------------------------
	
	public void addProductGroup(ProductGroup productGroup) {
		childProductGroups.add(productGroup);
		productGroup.setParentProductGroup(this);
		productGroup.setBusinessArea(businessArea);
	}
	
	public void addProduct(Product product) {
		products.add(product);
		product.setProductGroup(this);
		product.setBusinessArea(businessArea);
	}
	
	public void removeProduct(Product product) {
		products.remove(product);
		product.setProductGroup(null);
	}
	
	// --------------------------------
	// The following methods are used to provide a tree-like structure when listing product groups
	
	/**
	 * @return the full path based on names of this object and its ancestors.
	 */
	@Transient
	public String getFullPath(String delimiter) {
		String path = "";
		ProductGroup pg = this;
		while (pg != null) {
			if (!StringUtils.isEmpty(path)) {
				path = delimiter + path;
			}
			path = pg.getName() + path;
			pg = pg.getParentProductGroup();
		}
		return path;
	}
	
	/**
	 * @return the full path based on names of this object and its ancestors.
	 */
	@Transient
	public String getFullPath() {
		return getFullPath(" -> ");
	}

	/**
	 * Pick next available sort index and update full path sort index 
	 */
	public void initSortIndex(List<ProductGroup> productGroups) {
		if (id == null) {
//			fullPathSortIndex = "";
		} else {
			sortIndex = 1;
			for (ProductGroup pg : productGroups) {
				if (((parentProductGroup == null) && (pg.getParentProductGroup() == null)) || 
					((parentProductGroup != null) && (parentProductGroup.equals(pg.getParentProductGroup())))) {
					if (!id.equals(pg.getId())) {
						sortIndex = Math.max(sortIndex, pg.getSortIndex()+1);
					}
				}
			}
			
//			/* Build an index for sorting a tree of objects. A maximum of 7 levels and 1000 items pr. level is supported */
//			long multiplier = 1;
//			ProductGroup pg = this;
//			long compositeSortIndex = 0;
//			while (pg != null) {
//				compositeSortIndex = multiplier * pg.getSortIndex() + compositeSortIndex;
//				pg = pg.getParentProductGroup();
//				multiplier *= 1000;
//			}
//			fullPathSortIndex = String.valueOf(compositeSortIndex);
		}
	}
	
	@Transient
	public ProductGroup getByName(String name) {
		if (this.name.equals(name)) {
			return this;
		} else {
			for (ProductGroup child : getChildProductGroups()) {
				ProductGroup matchingProductGroup = child.getByName(name);
				if (matchingProductGroup != null) {
					return matchingProductGroup;
				}
			}
		}
		return null;
	}

	@Transient
	public ProductGroup getByFullPath(String path) {
		if (getFullPath().equals(path)) {
			return this;
		} else {
			for (ProductGroup child : getChildProductGroups()) {
				ProductGroup matchingProductGroup = child.getByFullPath(path);
				if (matchingProductGroup != null) {
					return matchingProductGroup;
				}
			}
		}
		return null;
	}

	@Transient
	public Product getProductByProductId(String productId) {
		for (Product product : getProducts()) {
			if (productId.equals(product.getProductId())) {
				return product;
			}
		} 
		for (ProductGroup childGroup : getChildProductGroups()) {
			for (Product product : childGroup.getProducts()) {
				if (productId.equals(product.getProductId())) {
					return product;
				}
			} 
		}
		return null;
	}

	@Transient
	public ProductGroup getByUniqueName(String uniqueName) {
		if (this.uniqueName.equals(uniqueName)) {
			return this;
		} else {
			for (ProductGroup child : getChildProductGroups()) {
				ProductGroup matchingProductGroup = child.getByUniqueName(uniqueName);
				if (matchingProductGroup != null) {
					return matchingProductGroup;
				}
			}
		}
		return null;
	}
	
	@javax.persistence.Transient
	public List<ProductGroup> getAll() {
		List<ProductGroup> all = new ArrayList<>();
		all.add(this);
		for (ProductGroup childProductGroup : getChildProductGroups()) {
			List<ProductGroup> allChildren = childProductGroup.getAll();
			for (ProductGroup pg : allChildren) {
				all.add(pg);
			}
		}
		return all;
	}

	@javax.persistence.Transient
	public List<Product> getProductsSortedForUI() {
		List<Product> all = new ArrayList<>(products);
		Collections.sort(all, new Comparator<Product>() {
			@Override
			public int compare(Product o1, Product o2) {
				return Long.valueOf(o1.getSortIndex()).compareTo(Long.valueOf(o2.getSortIndex()));
			}
		});
		return all;
	}

	@Transient
	public long getMaxSortIndexOfChildren() {
		long max = 0;
		for (ProductGroup childGroup : getChildProductGroups()) {
			max = Math.max(max, childGroup.getSortIndex());
		}
		return max;
	}
	
	// --------------------------------
	
	@Override
	public String toString() {
		return name;
	}
}

