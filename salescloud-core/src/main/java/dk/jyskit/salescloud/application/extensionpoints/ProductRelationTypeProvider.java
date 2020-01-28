package dk.jyskit.salescloud.application.extensionpoints;

import java.util.List;

import dk.jyskit.salescloud.application.model.ProductRelationType;

public interface ProductRelationTypeProvider {
	List<ProductRelationType> getProductRelationTypes();
	ProductRelationType getById(Long id);
}
