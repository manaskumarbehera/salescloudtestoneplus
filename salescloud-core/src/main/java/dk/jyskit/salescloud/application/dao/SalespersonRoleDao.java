package dk.jyskit.salescloud.application.dao;

import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.application.model.BaseEntity;
import dk.jyskit.waf.utils.guice.Lookup;

public interface SalespersonRoleDao extends Dao<SalespersonRole> {
	static SalespersonRoleDao lookup() {
		return Lookup.lookup(SalespersonRoleDao.class);
	}
}
