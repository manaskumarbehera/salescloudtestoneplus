package dk.jyskit.salescloud.application.dao;

import dk.jyskit.salescloud.application.model.MobileContract;
import dk.jyskit.salescloud.application.pages.switchboard.types.TypeSelectionPanel;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.utils.guice.Lookup;

import java.util.Date;
import java.util.List;

public interface MobileContractDao extends Dao<MobileContract> {
	static MobileContractDao lookup() {
		return Lookup.lookup(MobileContractDao.class);
	}
}
