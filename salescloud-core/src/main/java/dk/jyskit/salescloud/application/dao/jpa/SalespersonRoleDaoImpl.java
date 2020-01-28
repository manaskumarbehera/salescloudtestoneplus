package dk.jyskit.salescloud.application.dao.jpa;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import dk.jyskit.salescloud.application.dao.SalespersonRoleDao;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

@SuppressWarnings("unchecked")
public class SalespersonRoleDaoImpl extends GenericDaoImpl<SalespersonRole> implements SalespersonRoleDao {
    @Inject
	public SalespersonRoleDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(SalespersonRole.class), emp);
	}
}
