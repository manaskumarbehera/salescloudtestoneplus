package dk.jyskit.salescloud.application.dao.jpa;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import dk.jyskit.salescloud.application.dao.OrganisationDao;
import dk.jyskit.salescloud.application.model.Organisation;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

public class OrganisationDaoImpl extends GenericDaoImpl<Organisation> implements OrganisationDao {
    @Inject
	public OrganisationDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(Organisation.class), emp);
	}
}
