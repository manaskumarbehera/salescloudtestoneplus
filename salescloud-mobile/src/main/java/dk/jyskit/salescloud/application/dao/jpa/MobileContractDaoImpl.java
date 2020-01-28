package dk.jyskit.salescloud.application.dao.jpa;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import dk.jyskit.salescloud.application.dao.MobileContractDao;
import dk.jyskit.salescloud.application.model.MobileContract;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

@SuppressWarnings("unchecked")
public class MobileContractDaoImpl extends GenericDaoImpl<MobileContract> implements MobileContractDao {
    @Inject
	public MobileContractDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(MobileContract.class), emp);
	}
}
