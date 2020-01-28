package dk.jyskit.salescloud.application.dao.jpa;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import dk.jyskit.salescloud.application.dao.BusinessAreaDao;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

@SuppressWarnings("unchecked")
public class BusinessAreaDaoImpl extends GenericDaoImpl<BusinessArea> implements BusinessAreaDao {
    @Inject
	public BusinessAreaDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(BusinessArea.class), emp);
	}
}
