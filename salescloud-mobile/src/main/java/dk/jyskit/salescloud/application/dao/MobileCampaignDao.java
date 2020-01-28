package dk.jyskit.salescloud.application.dao;

import dk.jyskit.salescloud.application.model.Campaign;
import dk.jyskit.salescloud.application.model.MobileCampaign;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.utils.guice.Lookup;
import dk.jyskit.waf.wicket.crud.AbstractEditPanel;

public interface MobileCampaignDao extends Dao<MobileCampaign> {
	static MobileCampaignDao lookup() {
		return Lookup.lookup(MobileCampaignDao.class);
	}
}
