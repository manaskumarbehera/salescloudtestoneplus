package dk.jyskit.salescloud.application.dao;

import dk.jyskit.salescloud.application.model.Campaign;
import dk.jyskit.salescloud.application.model.DiscountScheme;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.utils.guice.Lookup;

import java.util.List;

public interface DiscountSchemeDao extends Dao<DiscountScheme> {
	static DiscountSchemeDao lookup() {
		return Lookup.lookup(DiscountSchemeDao.class);
	}
}
