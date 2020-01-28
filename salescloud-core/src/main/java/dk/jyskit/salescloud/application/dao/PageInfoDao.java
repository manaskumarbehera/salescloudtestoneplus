package dk.jyskit.salescloud.application.dao;

import dk.jyskit.salescloud.application.model.PageInfo;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.utils.guice.Lookup;

public interface PageInfoDao extends Dao<PageInfo> {
	public static PageInfoDao lookup() {
		return (PageInfoDao) Lookup.lookup(PageInfoDao.class);
	}

	public PageInfo findByPageId(Long businessAreaId, String pageId);
}
