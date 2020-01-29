package dk.jyskit.waf.application.model.extradata;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import dk.jyskit.waf.application.dao.ExtraDataDefinitionDao;
import dk.jyskit.waf.application.model.BaseEntity;
import dk.jyskit.waf.application.model.extradata.ExtraDataDefinition.Cardinality;
import dk.jyskit.waf.utils.guice.Lookup;

/**
 * Represents all {@link ExtraDataDefinition} for an entity type
 * @author palfred
 *
 */
@Slf4j
public class ExtraDataDefinitions implements Serializable {
	private final Class<? extends BaseEntity> entityType;
	private Map<String, ExtraDataDefinition> definitions;

	public ExtraDataDefinitions(Class<? extends BaseEntity> entityType) {
		super();
		this.entityType = entityType;
		definitions = dao().findForEntityClass(this.entityType);
	}

	protected ExtraDataDefinitionDao dao() { return Lookup.lookup(ExtraDataDefinitionDao.class); }

	public Map<String, ExtraDataDefinition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(Map<String, ExtraDataDefinition> definitions) {
		this.definitions = definitions;
	}

	public ExtraDataDefinition updateAndSave(String key, String defaultValue,
			ExtraDataType type, Cardinality cardinality, Properties props, String description) {
		ExtraDataDefinition definition;
		if (!definitions.containsKey(key)) {
			definition =  new ExtraDataDefinition(entityType.getName(), key, description, defaultValue, type, cardinality, props);
		} else {
			definition = definitions.get(key);
			definition.setDescription(description);
			definition.setDefaultValue(defaultValue);
			definition.setType(type);
			definition.setTypeProperties(props);
		}
		dao().save(definition);
		return definition;
	}

	/**
	 * Helper to create properties form one more key value pairs formatted as "[key]=[Value]".
	 * @param propStrs
	 * @return
	 */
	public Properties props(String... propStrs) {
		Properties props = new Properties();
		for (String propStr : propStrs) {
			String[] split = propStr.split("=");
			if (split.length == 2) {
				props.put(split[0].trim(), split[1].trim());
			} else {
				log.warn(propStr + " is not a key value pair");
			}
		}
		return props;
	}
}
