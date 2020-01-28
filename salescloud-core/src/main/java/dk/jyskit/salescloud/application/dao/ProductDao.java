package dk.jyskit.salescloud.application.dao;

import java.util.List;

import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.utils.guice.Lookup;

public interface ProductDao extends Dao<Product> {
	public static ProductDao lookup() {
		return Lookup.lookup(ProductDao.class);
	}

	public List<Product> findByNameLike(String name);
	public List<Product> findByBusinessAreaAndProductGroupUniqueName(long businessAreaEntityId, String groupUniqueName);
	public List<Product> findByBusinessArea(BusinessArea businessArea);
	public Product findByBusinessAreaAndProductId(long businessAreaEntityId, String productId);
	public Product findByProductGroupAndProductId(long businessAreaEntityId, String productGroupKey, String productId);
	public Product findByProductGroupAndProductName(long businessAreaEntityId, String productGroupKey, String publicName);
}
