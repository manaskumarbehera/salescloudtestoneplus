package dk.jyskit.salescloud.application.extensionpoints.defaultimpl;

import java.util.ArrayList;
import java.util.List;

import dk.jyskit.salescloud.application.extensionpoints.ProductRelationTypeProvider;
import dk.jyskit.salescloud.application.model.ProductRelationType;

public class CoreProductRelationTypeProvider implements ProductRelationTypeProvider {
	public final static long ADD_ORDERLINES_0 = 0l;
	public final static long ADD_ORDERLINES_1 = 1l;
	public final static long ADD_ORDERLINES_N = 2l;
	public final static long ALLOW_PRODUCTS = 3l;
	public final static long ALTERNATIVE_PRODUCTS = 4l;
	public final static long ALWAYS_INCLUDED = 5l;
	
	protected List<ProductRelationType> types;
	
	public CoreProductRelationTypeProvider() {
		types = new ArrayList<ProductRelationType>();
		types.add(new ProductRelationType(ADD_ORDERLINES_0, "Tilføj ordreline (antal: 0)", true));
		types.add(new ProductRelationType(ADD_ORDERLINES_1, "Tilføj ordreline (antal: 1)", true));
		types.add(new ProductRelationType(ADD_ORDERLINES_N, "Tilføj ordreline (antal: N)", true));
		types.add(new ProductRelationType(ALLOW_PRODUCTS, "Tillad produkter", true));
		types.add(new ProductRelationType(ALTERNATIVE_PRODUCTS, "Alternative produkter", false));
		types.add(new ProductRelationType(ALWAYS_INCLUDED, "Altid inkluderet", false));
	}
	
	@Override
	public List<ProductRelationType> getProductRelationTypes() {
		return types;
	}
	
	public ProductRelationType getById(Long id) {
		for (ProductRelationType type : types) {
			if (type.getId().equals(id)) {
				return type;
			}
		}
		return null;
	}
}
