package dk.jyskit.salescloud.application.dao.jpa;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import dk.jyskit.salescloud.application.dao.MobileProductBundleDao;
import dk.jyskit.salescloud.application.model.MobileProductBundle;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

@SuppressWarnings("unchecked")
public class MobileProductBundleDaoImpl extends GenericDaoImpl<MobileProductBundle> implements MobileProductBundleDao {
    @Inject
	public MobileProductBundleDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(MobileProductBundle.class), emp);
	}
}
