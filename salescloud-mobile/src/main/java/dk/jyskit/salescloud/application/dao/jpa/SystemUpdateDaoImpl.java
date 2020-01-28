package dk.jyskit.salescloud.application.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.persist.Transactional;

import dk.jyskit.salescloud.application.dao.SystemUpdateDao;
import dk.jyskit.salescloud.application.model.SystemUpdate;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

@SuppressWarnings("unchecked")
public class SystemUpdateDaoImpl extends GenericDaoImpl<SystemUpdate> implements SystemUpdateDao {
    @Inject
	public SystemUpdateDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(SystemUpdate.class), emp);
	}
    
    @Override
    @Transactional
    public SystemUpdate findByName(String name, int businessAreaId) {
        TypedQuery<SystemUpdate> q = em()
        		.createQuery("select x from " + clazz.getSimpleName() + " x "
        				+ "where x.businessAreaId = :businessAreaId "
        				+ "and x.name = :name"
        				, clazz)
        		.setParameter("businessAreaId", businessAreaId)
        		.setParameter("name", name);
        try {
        	return q.getSingleResult();
		} catch (Exception e) {
			return null;
		}
    }
}
