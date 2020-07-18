package dk.jyskit.salescloud.application.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Entity(name="BundleProductRelation")
@Table(name = "bundleproduct")
@Data
@EqualsAndHashCode(callSuper = false, of = { "productBundleId", "productId" })
@IdClass(BundleProductRelationId.class)
public class BundleProductRelation implements Serializable {
	@Id
	private long productBundleId;

	@Id
	private long productId;

	@ManyToOne
	@PrimaryKeyJoinColumn(name = "productBundleId", referencedColumnName = "id")
	private ProductBundle productBundle;

	@ManyToOne
	@PrimaryKeyJoinColumn(name = "productId", referencedColumnName = "id")
	private Product product;

	private long sortIndex;
	
	private boolean addProductPrice = true;
	
	@Enumerated(EnumType.STRING)
	@NotNull @NonNull
	private ProductAccessType productAccessType = ProductAccessType.INCLUDED;   
	
	// --------------------------------
	
	public BundleProductRelation() {
	}
	
	public BundleProductRelation(Product product) {
		setProduct(product);
	}

	// --------------------------------
	
	public void setProductBundle (ProductBundle productBundle) {
		this.productBundle = productBundle;
		if (productBundle.getId() != null) {
			this.productBundleId = productBundle.getId();
		}
	}
	
	public void setProduct (Product product) {
		this.product = product;
		if (product.getId() != null) {
			this.productId = product.getId();
		}
	}

	// --------------------------------

//	public BundleProductRelation clone() {
//		BundleProductRelation r = new BundleProductRelation();
//		r.setProduct(product);
//		r.setProductId(productId);
//		r.setProductBundle(productBundle);
//		r.setProductBundleId(productBundleId);
//		r.setSortIndex(sortIndex);
//		r.setAddProductPrice(addProductPrice);
//		r.setProductAccessType(productAccessType);
//		return r;
//	}

	// --------------------------------

	@Override
	public String toString() {
		if (product == null) {
			return "Ny produkt relation";
		}
		return product.getPublicName();
	}
}


