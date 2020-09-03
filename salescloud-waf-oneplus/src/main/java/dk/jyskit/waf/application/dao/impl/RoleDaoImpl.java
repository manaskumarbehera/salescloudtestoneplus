package dk.jyskit.waf.application.dao.impl;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import dk.jyskit.waf.application.dao.RoleDao;
import dk.jyskit.waf.application.model.BaseRole;

public class RoleDaoImpl extends GenericDaoImpl<BaseRole> implements RoleDao {

    @Inject
	public RoleDaoImpl(Provider<EntityManager> emp) {
		super(TypeLiteral.get(BaseRole.class), emp);
	}
}
