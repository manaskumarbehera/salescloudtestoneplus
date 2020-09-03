package dk.jyskit.waf.application.model.extradata;

import java.util.List;
import java.util.Properties;

import javax.persistence.*;

import dk.jyskit.waf.application.model.BaseEntity;
import dk.jyskit.waf.eclipselink.PropertiesConverter;

/**
 * Extra data that can be associated to any {@link BaseEntity}.
 * Extra data can be "typed" using {@link ExtraDataDefinition}.
 * 
 * {@link PropertiesConverter} must be configured in orm.xml in order for conversion by eclipselink.
 *
 * @author palfred
 */
@Entity
@Table(name = "extra_data_definition",
	uniqueConstraints={@UniqueConstraint(name="UNQ_EXTRA_DATA_DEFINITION", columnNames="NAME, ENTITY_TYPE")}
)
@lombok.Data @lombok.AllArgsConstructor @lombok.NoArgsConstructor
@lombok.ToString(callSuper=false, of={"entityType", "name", "defaultValue", "type", "typeProperties"})
@lombok.EqualsAndHashCode(callSuper=true, of={ })
public class ExtraDataDefinition extends BaseEntity {
	public static enum Cardinality {
		ZERO_OR_ONE, ONE, ZERO_OR_MANY, ONE_OR_MANY;
		public boolean isMultiple() {
			return this == ZERO_OR_MANY || this == ONE_OR_MANY;
		}
	}

	/**
	 * Simple name of the entity type that this applies to e.g. "BaseUser".
	 */
	@Column(name="ENTITY_TYPE")
	private String entityType;

	@Column(name="NAME", nullable=false)
	private String name;

	@Column(name="DESCRIPTION")
	private String description;

	@Column(name="DEFAULT_VALUE")
	private String defaultValue;

	@Column(name="VALUE_TYPE")
	@Enumerated(EnumType.STRING)
	private ExtraDataType type;

	@Enumerated(EnumType.STRING)
	private Cardinality cardinality= Cardinality.ZERO_OR_ONE;
	
	/**
	 * Extra properties for type. Depends on type but some that may be supported are: min=number, max=number, values=list of values, regexp=pattern...
	 */
	@Convert(converter=PropertiesConverter.class)
	@Column(name="VALUE_TYPE_PROPERTIES")
	private Properties typeProperties = new Properties();

	/**
	 * Gets the possible values for enumerated, returns null if not enumerated.
	 * @return
	 */
	public List<String> getTypeValues() {
		return type.getValues(typeProperties);
	}

	public Properties getTypeProperties() {
		if (typeProperties == null) {
			typeProperties = new Properties();
		}
		return typeProperties;
	}
	public boolean isValid(String value) {
		if (getCardinality().isMultiple()) {
			String[] split = value.split(",");
			for (String val : split) {
				if (!type.isValid(val, typeProperties)) {
					return false;
				}
			}
			if (getCardinality() == Cardinality.ONE_OR_MANY) {
				if (split.length == 0) {
					return false;
				}
			}
			return true;
		} else {
			return type.isValid(value, typeProperties);
		}
	}

}
