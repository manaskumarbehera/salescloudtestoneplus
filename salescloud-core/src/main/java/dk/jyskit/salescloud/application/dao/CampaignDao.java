package dk.jyskit.salescloud.application.dao;

import java.util.List;

import dk.jyskit.salescloud.application.model.Campaign;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.utils.guice.Lookup;

public interface CampaignDao extends Dao<Campaign> {
	static CampaignDao lookup() {
		return Lookup.lookup(CampaignDao.class);
	}

	List<Campaign> findAvailableByBusinessArea(Long businessAreaId);
}
