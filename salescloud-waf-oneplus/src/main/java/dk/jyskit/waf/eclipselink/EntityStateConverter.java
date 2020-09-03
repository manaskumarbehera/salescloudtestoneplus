package dk.jyskit.waf.eclipselink;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import dk.jyskit.waf.application.model.EntityState;

/**
 * Persist EntityState via EclipseLink
 * 
 * @author georgi.knox
 * 
 */
@Converter(autoApply = false)
public class EntityStateConverter implements AttributeConverter<EntityState, Integer> {

	@Override
	public Integer convertToDatabaseColumn(EntityState attribute) {
		if (attribute == null) {
			return null;
		}
		return attribute.getEntityState();
	}

	@Override
	public EntityState convertToEntityAttribute(Integer dbData) {
		if (dbData == null) {
			return EntityState.ACTIVE;
		}
		return EntityState.of(dbData);
	}
}
