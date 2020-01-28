package dk.jyskit.salescloud.application.dao.jpa;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import dk.jyskit.salescloud.application.dao.SalesmanagerRoleDao;
import dk.jyskit.salescloud.application.model.SalesmanagerRole;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

@SuppressWarnings("unchecked")
public class SalesmanagerRoleDaoImpl extends GenericDaoImpl<SalesmanagerRole> implements SalesmanagerRoleDao {
    @Inject
	public SalesmanagerRoleDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(SalesmanagerRole.class), emp);
	}
}
