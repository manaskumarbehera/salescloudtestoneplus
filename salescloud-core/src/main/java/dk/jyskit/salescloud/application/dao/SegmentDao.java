package dk.jyskit.salescloud.application.dao;

import dk.jyskit.salescloud.application.model.Segment;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.utils.guice.Lookup;

public interface SegmentDao extends Dao<Segment> {
	static SegmentDao lookup() {
		return Lookup.lookup(SegmentDao.class);
	}
}
