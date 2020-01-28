package dk.jyskit.salescloud.application.dao.jpa;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import dk.jyskit.salescloud.application.dao.MobileProductDao;
import dk.jyskit.salescloud.application.model.MobileProduct;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

@SuppressWarnings("unchecked")
public class MobileProductDaoImpl extends GenericDaoImpl<MobileProduct> implements MobileProductDao {
    @Inject
	public MobileProductDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(MobileProduct.class), emp);
	}
}
