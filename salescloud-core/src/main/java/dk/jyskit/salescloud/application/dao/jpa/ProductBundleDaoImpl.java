package dk.jyskit.salescloud.application.dao.jpa;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import dk.jyskit.salescloud.application.dao.ProductBundleDao;
import dk.jyskit.salescloud.application.model.ProductBundle;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

@SuppressWarnings("unchecked")
public class ProductBundleDaoImpl extends GenericDaoImpl<ProductBundle> implements ProductBundleDao {
    @Inject
	public ProductBundleDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(ProductBundle.class), emp);
	}
}
