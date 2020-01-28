package dk.jyskit.salescloud.application.dao.jpa;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import dk.jyskit.salescloud.application.dao.SubscriptionDao;
import dk.jyskit.salescloud.application.model.Subscription;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

@SuppressWarnings("unchecked")
public class SubscriptionDaoImpl extends GenericDaoImpl<Subscription> implements SubscriptionDao {
    @Inject
	public SubscriptionDaoImpl(Provider<EntityManager> emp) {
    	super(TypeLiteral.get(Subscription.class), emp);
	}
}
