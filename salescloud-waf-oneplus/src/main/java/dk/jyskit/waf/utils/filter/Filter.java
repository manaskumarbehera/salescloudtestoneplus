package dk.jyskit.waf.utils.filter;

import java.io.Serializable;

/**
 * Filter interface for container filtering.
 * 
 * If a filter does not support in-memory filtering,
 * {@link #passesFilter(Object)} should throw
 * {@link UnsupportedOperationException}.
 * 
 * Lazy containers must be able to map filters to their internal
 * representation (e.g. SQL or JPA 2.0 Criteria).
 * 
 * An {@link UnsupportedFilterException} can be thrown by the container if a
 * particular filter is not supported by the container.
 * 
 * A {@link Filter} should implement {@link Object#equals(Object)} and
 * {@link Object#hashCode()} correctly to avoid duplicate filter registrations
 * etc.
 * 
 */
public interface Filter extends Serializable {

    /**
     * Check if an item passes the filter (in-memory filtering).
     * @param entity
     *            the entity being filtered
     * 
     * @return true if the item is accepted by this filter
     * @throws UnsupportedOperationException
     *             if the filter cannot be used for in-memory filtering
     */
    public boolean passesFilter(Object entity) throws UnsupportedOperationException;

    /**
     * Check if a change in the value of a property can affect the filtering
     * result. May always return true, at the cost of performance.
     * 
     * If the filter cannot determine whether it may depend on the property
     * or not, should return true.
     * 
     * @param propertyName
     * @return true if the filtering result may/does change based on changes
     *         to the property identified by propertyId
     */
    public boolean appliesToProperty(String propertyName);

}
