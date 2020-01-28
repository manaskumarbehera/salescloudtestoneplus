package dk.jyskit.waf.application.dao;

import java.util.Map;

import dk.jyskit.waf.application.model.BaseEntity;
import dk.jyskit.waf.application.model.extradata.ExtraDataDefinition;

public interface ExtraDataDefinitionDao extends Dao<ExtraDataDefinition> {
	Map<String, ExtraDataDefinition> findForEntityClass(Class<? extends BaseEntity> entityClazz);

	ExtraDataDefinition findForEntityClass(Class<? extends BaseEntity> entityClazz, String key);
}
