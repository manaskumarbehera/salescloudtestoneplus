package dk.jyskit.salescloud.application.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import dk.jyskit.salescloud.application.dao.OrderLineDao;
import dk.jyskit.salescloud.application.model.OrderLine;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

@SuppressWarnings("unchecked")
public class OrderLineDaoImpl extends GenericDaoImpl<OrderLine> implements OrderLineDao {
    @Inject
	public OrderLineDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(OrderLine.class), emp);
	}
    
    @Override
    public void deleteByProductId(Long productId) {
		try {
			String hql = "delete from OrderLine where product.id= :productId";
			Query query = em().createQuery(hql);
			query.setParameter("productId", productId);
	        query.executeUpdate();
		} catch (Exception e) {
			throw e;
		}
    }
}
