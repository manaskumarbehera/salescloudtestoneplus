package dk.jyskit.salescloud.application.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import lombok.extern.slf4j.Slf4j;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.persist.Transactional;

import dk.jyskit.salescloud.application.dao.PageInfoDao;
import dk.jyskit.salescloud.application.model.PageInfo;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

@SuppressWarnings("unchecked")
@Slf4j
public class PageInfoDaoImpl extends GenericDaoImpl<PageInfo> implements PageInfoDao {
    @Inject
	public PageInfoDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(PageInfo.class), emp);
	}

	@Transactional
    public PageInfo findByPageId(Long businessAreaId, String pageId) {
		try {
	        TypedQuery<PageInfo> q = em()
	        		.createQuery("select pi from " + clazz.getSimpleName() + " pi "
	        				+ "where pi.businessArea.id = :businessAreaId "
	        				+ "and pi.pageId = :pageId", clazz)
	        		.setParameter("businessAreaId", businessAreaId)
	        		.setParameter("pageId", pageId);
	        return q.getSingleResult();
		} catch (Exception e) {
			log.warn("pageId: " + pageId + ", businessAreaId: " + businessAreaId);
			throw e;
		}
    }
}
