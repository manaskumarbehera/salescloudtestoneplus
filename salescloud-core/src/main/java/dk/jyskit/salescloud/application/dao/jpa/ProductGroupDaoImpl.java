package dk.jyskit.salescloud.application.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.persist.Transactional;

import dk.jyskit.salescloud.application.dao.ProductGroupDao;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

@SuppressWarnings("unchecked")
public class ProductGroupDaoImpl extends GenericDaoImpl<ProductGroup> implements ProductGroupDao {
    @Inject
	public ProductGroupDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(ProductGroup.class), emp);
	}

	@Transactional
    public List<ProductGroup> findRootGroupsByBusinessArea(BusinessArea businessArea) {
        TypedQuery<ProductGroup> q = em()
        		.createQuery("select x from " + clazz.getSimpleName() + " x where x.	businessArea = :businessArea and x.parentProductGroup is null", clazz)
        		.setParameter("businessArea", businessArea);
        return q.getResultList();
    }
	
	@Override
	public List<ProductGroup> findByBusinessArea(BusinessArea businessArea) {
        TypedQuery<ProductGroup> q = em()
        		.createQuery("select x from " + clazz.getSimpleName() + " x where x.businessArea = :businessArea", clazz)
        		.setParameter("businessArea", businessArea);
        return q.getResultList();
	}
	
	@Override
	public ProductGroup findByBusinessAreaAndUniqueName(BusinessArea businessArea, String uniqueName) {
		try {
	        TypedQuery<ProductGroup> q = em()
	        		.createQuery("select x from " + clazz.getSimpleName() + " x where x.businessArea = :businessArea and x.uniqueName = :uniqueName", clazz)
	        		.setParameter("businessArea", businessArea)
	        		.setParameter("uniqueName", uniqueName);
	        return q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
