package dk.jyskit.waf.application.dao;

import java.util.Map;

import dk.jyskit.waf.application.model.BaseEntity;
import dk.jyskit.waf.application.model.extradata.ExtraData;

public interface ExtraDataDao extends Dao<ExtraData> {
	Map<String, ExtraData> findForEntity(BaseEntity entity);

	ExtraData findForEntity(BaseEntity entity, String name);
}
