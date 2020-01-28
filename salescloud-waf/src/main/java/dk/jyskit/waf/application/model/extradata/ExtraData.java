package dk.jyskit.waf.application.model.extradata;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import dk.jyskit.waf.application.dao.DaoHelper;
import dk.jyskit.waf.application.model.BaseEntity;

/**
 * Extra data that can be associated to any {@link BaseEntity}.
 * Extra data can be "typed" using {@link ExtraDataDefinition}.
 * @author palfred
 *
 */
@Entity
@Table(name = "extra_data",
 uniqueConstraints={@UniqueConstraint(name="UNQ_EXTRA_DATA_ENTITY_KEY", columnNames={"ENTITY_ID", "ENTITY_TYPE", "NAME"})}
)
@lombok.Data @lombok.AllArgsConstructor @lombok.NoArgsConstructor
@lombok.EqualsAndHashCode(callSuper=true, of={ })
@lombok.ToString(callSuper=false, of={"entityType", "entityId", "name", "value"})
public class ExtraData extends BaseEntity {
	/**
	 * Class name of the entity type that this applies to e.g. "dk.jyskit.application.model.BaseUser".
	 */
	@Column(name="ENTITY_TYPE", nullable=false)
	private String entityType;

	@Column(name="ENTITY_ID", nullable=false)
	private Long entityId;

	@Column(name="NAME", nullable=false)
	private String name;

	@Column(name="DATA_VALUE")
	private String value;

	public ExtraData(BaseEntity entity, String name, String value) {
		setEntity(entity);
		setName(name);
		setValue(value);
	}

	public void setEntity(BaseEntity entity) {
		setEntityClass(entity.getClass());
		setEntityId(entity.getId());
	}

	/**
	 * Gets the entity that this extra data relates to.
	 * @return
	 */
	public BaseEntity getEntity() {
		return DaoHelper.getEntityManager().find(getEntityClass(), getEntityId());
	}

	public void setEntityClass(Class<? extends BaseEntity> clazz) {
		setEntityType(clazz.getName());
	}

	@SuppressWarnings("unchecked")
	public Class<? extends BaseEntity> getEntityClass() {
		try {
			Class<?> clazz = Class.forName(entityType);
			return (Class<? extends BaseEntity>) clazz;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("entityType class must be a descendant of BaseEntity");
		}
	}

}
