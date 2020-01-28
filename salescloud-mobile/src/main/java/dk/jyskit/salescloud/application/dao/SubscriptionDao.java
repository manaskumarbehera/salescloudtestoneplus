package dk.jyskit.salescloud.application.dao;

import dk.jyskit.salescloud.application.model.Subscription;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.utils.guice.Lookup;

public interface SubscriptionDao extends Dao<Subscription> {
	public static SubscriptionDao lookup() {
		return Lookup.lookup(SubscriptionDao.class);
	}
}
