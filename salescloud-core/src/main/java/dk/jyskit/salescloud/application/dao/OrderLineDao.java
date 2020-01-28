package dk.jyskit.salescloud.application.dao;

import dk.jyskit.salescloud.application.model.OrderLine;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.utils.guice.Lookup;

public interface OrderLineDao extends Dao<OrderLine> {
	static OrderLineDao lookup() {
		return Lookup.lookup(OrderLineDao.class);
	}

	void deleteByProductId(Long productId);
}
