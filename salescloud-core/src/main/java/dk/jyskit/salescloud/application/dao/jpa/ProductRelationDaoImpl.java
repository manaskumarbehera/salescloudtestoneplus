package dk.jyskit.salescloud.application.dao.jpa;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import dk.jyskit.salescloud.application.dao.ProductRelationDao;
import dk.jyskit.salescloud.application.model.ProductRelation;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

@SuppressWarnings("unchecked")
public class ProductRelationDaoImpl extends GenericDaoImpl<ProductRelation> implements ProductRelationDao {
    @Inject
	public ProductRelationDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(ProductRelation.class), emp);
	}
}
