package dk.jyskit.salescloud.application.pages.admin.productrelations;

import lombok.Data;
import dk.jyskit.salescloud.application.extensionpoints.ProductRelationTypeProvider;
import dk.jyskit.salescloud.application.model.ProductRelation;
import dk.jyskit.salescloud.application.model.ProductRelationType;
import dk.jyskit.waf.utils.guice.Lookup;

@Data
public class ExtendedProductRelation extends ProductRelation {
	private ProductRelationType relationType;
	private ProductRelation original;
	
	public ExtendedProductRelation() {
		original = new ProductRelation();
	}
	
	public ExtendedProductRelation(ProductRelation productRelation) {
		original = productRelation;
		setRelationType(Lookup.lookup(ProductRelationTypeProvider.class).getById(productRelation.getRelationTypeId()));
		setRelationTypeId(productRelation.getId());
		setDisplayName(productRelation.getDisplayName());
		setProducts(productRelation.getProducts());
	}

	public ProductRelation getUpdatedOriginal() {
		original.setRelationTypeId(getRelationType().getId());
		original.setDisplayName(getDisplayName());
		original.setProducts(getProducts());
		return original;
	}
	
	public String toString() {
		return "";
	}
}
