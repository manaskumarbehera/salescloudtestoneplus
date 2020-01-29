package dk.jyskit.waf.utils.filter;

import java.util.List;

import dk.jyskit.waf.utils.CollectionUtils;
import dk.jyskit.waf.utils.filter.Compare.Greater;
import dk.jyskit.waf.utils.filter.Compare.GreaterOrEqual;
import dk.jyskit.waf.utils.filter.Compare.Less;
import dk.jyskit.waf.utils.filter.Compare.LessOrEqual;

// import com.vaadin.addon.jpacontainer.util.CollectionUtil;

/**
 * Utility class for creating filter instances.
 * 
 * @author Petter Holmström (Vaadin Ltd)
 * @since 1.0
 */
public final class Filters {

	private Filters() {
		// To prevent applications from creating instances of this class.
	}

	/**
	 * Creates a new filter that accepts all items whose value of
	 * <code>propertyName</code> is null.
	 */
	public static Filter isNull(String propertyName) {
		return new IsNull(propertyName);
	}

	/**
	 * Creates a new filter that accepts all items whose value of
	 * <code>propertyName</code> is not null.
	 */
	public static Filter isNotNull(String propertyName) {
		return new Not(isNull(propertyName));
	}

	/**
	 * Creates a new filter that accepts all items whose value of
	 * <code>propertyName</code> is empty.
	 */
	public static Filter isEmpty(String propertyName) {
		return new Equal(propertyName, "");
	}

	/**
	 * Creates a new filter that accepts all items whose value of
	 * <code>propertyName</code> is not empty.
	 */
	public static Filter isNotEmpty(String propertyName) {
		return new Not(isEmpty(propertyName));
	}

//	/**
//	 * Creates a new filter that accepts all items whose value of
//	 * <code>propertyName</code> is equal to <code>value</code>.
//	 */
//	public static Filter eq(String propertyName, String value, boolean caseSensitive) {
//		return new SimpleStringFilter(propertyName, value, !caseSensitive, false);
//	}

	/**
	 * Creates a new filter that accepts all items whose value of
	 * <code>propertyName</code> matches <code>value</code>. The precent-sign
	 * (%) may be used as wildcard.
	 */
	public static Filter like(String propertyName, String value, boolean caseSensitive) {
		return new Like(propertyName.toString(), value, caseSensitive);
	}

	/**
	 * Creates a new filter that accepts all items whose value of
	 * <code>propertyName</code> is equal to <code>value</code>.
	 */
	public static Filter eq(String propertyName, Number value) {
		return new Equal(propertyName, value);
	}

	/**
	 * Creates a new filter that accepts all items whose value of
	 * <code>propertyName</code> is greater than or equal to <code>value</code>.
	 */
	public static Filter gteq(String propertyName, Number value) {
		return new GreaterOrEqual(propertyName, value);
	}

	/**
	 * Creates a new filter that accepts all items whose value of
	 * <code>propertyName</code> is greater than <code>value</code>.
	 */
	public static Filter gt(String propertyName, Number value) {
		return new Greater(propertyName, value);
	}

	/**
	 * Creates a new filter that accepts all items whose value of
	 * <code>propertyName</code> is less than or equal to <code>value</code>.
	 */
	public static Filter lteq(String propertyName, Number value) {
		return new LessOrEqual(propertyName, value);
	}

	/**
	 * Creates a new filter that accepts all items whose value of
	 * <code>propertyName</code> is less than <code>value</code>.
	 */
	public static Filter lt(String propertyName, Number value) {
		return new Less(propertyName, value);
	}

	/**
	 * Creates a new filter that accepts all items whose value of
	 * <code>propertyName</code> is between <code>startingPoint</code> and
	 * <code>endingPoint</code>.
	 */
	public static Filter between(String propertyName, Number startingPoint, Number endingPoint) {
		return new Between(propertyName, startingPoint, endingPoint);
	}

	public static Filter between(String propertyName, Number startingPoint, Number endingPoint,
			boolean includeStartingPoint, boolean includeEndingPoint) {
		return new And((includeStartingPoint ? new GreaterOrEqual(propertyName, startingPoint) : new Greater(
				propertyName, startingPoint)), (includeEndingPoint ? new LessOrEqual(propertyName, endingPoint)
				: new Less(propertyName, endingPoint)));
	}

	/**
	 * Creates a new filter that accepts all items whose value of
	 * <code>propertyName</code> is between <code>startingPoint</code>
	 * (inclusive) and <code>endingPoint</code> (inclusive).
	 */
	public static Filter betweenInclusive(String propertyName, Number startingPoint, Number endingPoint) {
		return between(propertyName, startingPoint, endingPoint, true, true);
	}

	/**
	 * Creates a new filter that accepts all items whose value of
	 * <code>propertyName</code> is between <code>startingPoint</code>
	 * (exclusive) and <code>endingPoint</code> (exclusive).
	 */
	public static Filter betweenExlusive(String propertyName, Number startingPoint, Number endingPoint) {
		return between(propertyName, startingPoint, endingPoint, false, false);
	}

	/**
	 * Creates a new filter that accepts all items whose value of
	 * <code>propertyName</code> is outside <code>startingPoint</code> and
	 * <code>endingPoint</code>.
	 */
	public static Filter outside(String propertyName, Number startingPoint, Number endingPoint,
			boolean includeStartingPoint, boolean includeEndingPoint) {
		return new Or((includeStartingPoint ? new LessOrEqual(propertyName, startingPoint) : new Less(propertyName,
				startingPoint)), (includeEndingPoint ? new GreaterOrEqual(propertyName, endingPoint) : new Greater(
				propertyName, endingPoint)));
	}

	/**
	 * Creates a new filter that accepts all items whose value of
	 * <code>propertyName</code> is outside <code>startingPoint</code>
	 * (inclusive) and <code>endingPoint</code> (inclusive).
	 */
	public static Filter outsideInclusive(String propertyName, Number startingPoint, Number endingPoint) {
		return outside(propertyName, startingPoint, endingPoint, true, true);
	}

	/**
	 * Creates a new filter that accepts all items whose value of
	 * <code>propertyName</code> is outside <code>startingPoint</code>
	 * (exclusive) and <code>endingPoint</code> (exclusive).
	 */
	public static Filter outsideExclusive(String propertyName, Number startingPoint, Number endingPoint) {
		return outside(propertyName, startingPoint, endingPoint, false, false);
	}

	/**
	 * Creates a filter that negates <code>filter</code>.
	 */
	public static Filter not(Filter filter) {
		return new Not(filter);
	}

	/**
	 * Creates a filter that groups <code>filters</code> together in a single
	 * conjunction.
	 */
	public static And and(Filter... filters) {
		return new And(filters);
	}

	/**
	 * Creates a filter that groups <code>filters</code> together in a single
	 * conjunction.
	 */
	public static And and(List<Filter> filters) {
		return new And(CollectionUtils.toArray(Filter.class, filters));
	}

	/**
	 * Creates a filter that groups <code>filters</code> together in a single
	 * disjunction.
	 */
	public static Or or(Filter... filters) {
		return new Or(filters);
	}

	/**
	 * Creates a filter that groups <code>filters</code> together in a single
	 * disjunction.
	 */
	public static Or or(List<Filter> filters) {
		return new Or(CollectionUtils.toArray(Filter.class, filters));
	}

	// /**
	// * Creates a filter that applies <code>filters</code> (as a conjunction)
	// to
	// * the joined property <code>joinProperty</code>. This is only needed for
	// * Hibernate, as EclipseLink implicitly joins on nested properties.
	// */
	// public static JoinFilter joinFilter(String joinProperty, Filter...
	// filters) {
	// return new JoinFilter(joinProperty, filters);
	// }
}
