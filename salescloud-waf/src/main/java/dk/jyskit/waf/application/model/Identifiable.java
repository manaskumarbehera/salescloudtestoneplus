package dk.jyskit.waf.application.model;

import java.io.Serializable;

/**
 * Marks an Object as "identifiable", appropriate for instance in the case of beans mapped to a database with JPA or Hibernate.
 *
 * @param <ID> type of the id for this object.
 */
public interface Identifiable<ID extends Serializable> extends Serializable{
	public ID getId();
}
