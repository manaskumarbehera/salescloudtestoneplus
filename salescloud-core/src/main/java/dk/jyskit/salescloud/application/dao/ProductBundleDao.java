package dk.jyskit.salescloud.application.dao;

import dk.jyskit.salescloud.application.model.ProductBundle;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.utils.guice.Lookup;

public interface ProductBundleDao extends Dao<ProductBundle> {
	static ProductBundleDao lookup() {
		return Lookup.lookup(ProductBundleDao.class);
	}
}
