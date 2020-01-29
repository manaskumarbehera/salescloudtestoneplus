package dk.jyskit.waf.application.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.wicket.model.PropertyModel;

import dk.jyskit.waf.eclipselink.EntityStateConverter;

@MappedSuperclass
public class BaseEntity implements Identifiable<Long>, Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	// @GeneratedValue(strategy = GenerationType.TABLE)
	protected Long id;

	@Version
	protected Integer version;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModificationDate;
	
	@NotNull
	@Convert(converter=EntityStateConverter.class)
	@Column(name="entity_state")
	private EntityState entityState = EntityState.ACTIVE;

	// -----------------------------------

	@XmlTransient
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastModificationDate() {
		return lastModificationDate;
	}

	public void setLastModificationDate(Date lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
	}
	
	public EntityState getEntityState() {
		return entityState ;
	}

	public void setEntityState(EntityState entityState) {
		this.entityState = entityState;
	}

	public boolean isActive() {
		return entityState.isActive();
	}

	public void updateDates() {
		if (creationDate == null) {
			creationDate = new Date();
		}
		lastModificationDate = new Date();
	}

	public boolean isNewObject() {
		return (id == null);
	}

	// --- helper methods ----------------

	public List asList() {
		List list = new ArrayList();
		list.add(this);
		return list;
	}

	public Object getPropertyValue(String propertyName) throws UnsupportedOperationException {
		try {
			return new PropertyModel<>(this, propertyName).getObject();
		} catch (Exception e) {
			throw new UnsupportedOperationException(e.getMessage());
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseEntity other = (BaseEntity) obj;
		if (id == null) {
			if (other.id == null) {
				return (this == other);
			} else {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
}