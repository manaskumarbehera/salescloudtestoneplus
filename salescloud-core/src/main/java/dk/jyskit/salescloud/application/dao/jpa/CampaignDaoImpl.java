package dk.jyskit.salescloud.application.dao.jpa;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.persist.Transactional;

import dk.jyskit.salescloud.application.dao.CampaignDao;
import dk.jyskit.salescloud.application.model.Campaign;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;
import dk.jyskit.waf.application.model.EntityState;

@SuppressWarnings("unchecked")
public class CampaignDaoImpl extends GenericDaoImpl<Campaign> implements CampaignDao {
    @Inject
	public CampaignDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(Campaign.class), emp);
	}
    
	@Transactional
	public List<Campaign> findAvailableByBusinessArea(Long businessAreaId) {
        TypedQuery<Campaign> q = em()
        		.createQuery("select x from " + clazz.getSimpleName() + " x "
        				+ "where x.businessArea.id = :businessAreaId "
        				+ "and (x.entityState = :entityState) "
        				+ "and ((x.fromDate is null) or (x.fromDate <= :today)) "
        				+ "and ((x.toDate is null) or (x.toDate >= :today)) "
        				, clazz)
        		.setParameter("entityState", EntityState.ACTIVE)
        		.setParameter("businessAreaId", businessAreaId)
        		.setParameter("today", new Date(), TemporalType.DATE);
        return q.getResultList();
    }
}
