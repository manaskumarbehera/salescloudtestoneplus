package dk.jyskit.salescloud.application.dao;

import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.utils.guice.Lookup;

public interface BusinessAreaDao extends Dao<BusinessArea> {
	public static BusinessAreaDao lookup() {
		return (BusinessAreaDao) Lookup.lookup(BusinessAreaDao.class);
	}
}
