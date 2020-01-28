package dk.jyskit.salescloud.application.dao;

import dk.jyskit.salescloud.application.model.MobileProduct;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.application.model.BaseEntity;
import dk.jyskit.waf.utils.guice.Lookup;

public interface MobileProductDao extends Dao<MobileProduct> {
	public static MobileProductDao lookup() {
		return Lookup.lookup(MobileProductDao.class);
	}
}
