package dk.jyskit.salescloud.application.dao.jpa;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import dk.jyskit.salescloud.application.dao.UserManagerRoleDao;
import dk.jyskit.salescloud.application.model.UserManagerRole;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

@SuppressWarnings("unchecked")
public class UserManagerRoleDaoImpl extends GenericDaoImpl<UserManagerRole> implements UserManagerRoleDao {
    @Inject
	public UserManagerRoleDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(UserManagerRole.class), emp);
	}
}
