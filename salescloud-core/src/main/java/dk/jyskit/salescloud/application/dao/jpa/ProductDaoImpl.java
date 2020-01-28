package dk.jyskit.salescloud.application.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.persist.Transactional;

import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

public class ProductDaoImpl extends GenericDaoImpl<Product> implements ProductDao {
    @Inject
	public ProductDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(Product.class), emp);
	}

	@Transactional
    public List<Product> findByNameLike(String name) {
        TypedQuery<Product> q = em()
        		.createQuery("select x from " + clazz.getSimpleName() + " x where name like :name", clazz)
        		.setParameter("name", name);
        return q.getResultList();
    }

	@Override
	public List<Product> findByBusinessAreaAndProductGroupUniqueName(long businessAreaEntityId, String groupUniqueName) {
        TypedQuery<Product> q = em()
        		.createQuery("select p from " + clazz.getSimpleName() + " p "
        				+ "where p.productGroup.businessArea.id = :businessAreaEntityId and p.productGroup.uniqueName = :groupUniqueName "
        				+ "order by p.sortIndex", clazz)
                		.setParameter("businessAreaEntityId", businessAreaEntityId)
                		.setParameter("groupUniqueName", groupUniqueName);
        return q.getResultList();
	}

	@Override
	public List<Product> findByBusinessArea(BusinessArea businessArea) {
        TypedQuery<Product> q = em()
        		.createQuery("select p from " + clazz.getSimpleName() + " p "
        				+ "where p.productGroup.businessArea.id = :businessAreaEntityId "
        				+ "order by p.sortIndex", clazz)
                		.setParameter("businessAreaEntityId", businessArea.getId());
        return q.getResultList();
	}

	@Override
	public Product findByBusinessAreaAndProductId(long businessAreaEntityId, String productEntityId) {
		try {
	        TypedQuery<Product> q = em()
	        		.createQuery("select p from " + clazz.getSimpleName() + " p "
	        				+ "where p.productGroup.businessArea.id = :businessAreaEntityId and p.productId = :productEntityId", clazz)
	                		.setParameter("businessAreaEntityId", businessAreaEntityId)
	                		.setParameter("productEntityId", productEntityId);
	        return q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@Override
	public Product findByProductGroupAndProductId(long businessAreaEntityId, String productGroupKey, String productId) {
		try {
			TypedQuery<Product> q = em()
					.createQuery("select p from " + clazz.getSimpleName() + " p "
							+ "where p.productGroup.businessArea.id = :businessAreaEntityId and p.productGroup.uniqueName = :productGroupKey and p.productId = :productId", clazz)
					.setParameter("businessAreaEntityId", businessAreaEntityId)
					.setParameter("productGroupKey", productGroupKey)
					.setParameter("productId", productId);
			return q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@Override
	public Product findByProductGroupAndProductName(long businessAreaEntityId, String productGroupKey, String publicName) {
		try {
			TypedQuery<Product> q = em()
					.createQuery("select p from " + clazz.getSimpleName() + " p "
							+ "where p.productGroup.businessArea.id = :businessAreaEntityId and p.productGroup.uniqueName = :productGroupKey and p.publicName = :publicName", clazz)
					.setParameter("businessAreaEntityId", businessAreaEntityId)
					.setParameter("productGroupKey", productGroupKey)
					.setParameter("publicName", publicName);
			return q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
