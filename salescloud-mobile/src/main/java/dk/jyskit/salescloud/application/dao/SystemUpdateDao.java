package dk.jyskit.salescloud.application.dao;

import dk.jyskit.salescloud.application.model.SystemUpdate;
import dk.jyskit.waf.application.dao.Dao;

public interface SystemUpdateDao extends Dao<SystemUpdate> {

	SystemUpdate findByName(String name, int businessAreaId);
}
