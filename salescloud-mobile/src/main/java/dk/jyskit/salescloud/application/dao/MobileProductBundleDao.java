package dk.jyskit.salescloud.application.dao;

import dk.jyskit.salescloud.application.model.MobileProductBundle;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.utils.guice.Lookup;

public interface MobileProductBundleDao extends Dao<MobileProductBundle> {
	static MobileProductBundleDao lookup() {
		return Lookup.lookup(MobileProductBundleDao.class);
	}
}
