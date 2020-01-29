package dk.jyskit.waf.wicket.utils;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.comparators.ReverseComparator;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.core.util.lang.PropertyResolver;

/**
 * General comparator to be used to sort entities by a properties.
 * String values are compared ignoring case else Comparable is used if the values implements, otherwise toString values are compared.
 *
 */
public final class PropertyComparator<T> implements Comparator<T>, Serializable {
	private final String[] propertyName;

	@SuppressWarnings("unchecked")
	public static <S> void orderListByReversed(List<? extends S> list, String... propertyName) {
		Collections.sort(list, new ReverseComparator(new PropertyComparator<S>(propertyName)));
	}

	public static <S> void orderListBy(List<? extends S> list, String... propertyName) {
		Collections.sort(list,  new PropertyComparator<S>(propertyName));
	}

	public static <S> PropertyComparator<S> orderBy(String... propertyName) {
		return new PropertyComparator<S>(propertyName);
	}

	/**
	 * @param propertyName
	 */
	public PropertyComparator(String... propertyName) {
		this.propertyName = propertyName;
	}

	public int compare(T object1, T object2) {
		for (String propName : propertyName) {
			int res = compareProperty(propName, object1, object2);
			if (res != 0) {
				return res;
			}
		}
		return 0;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected int compareProperty(String propName, T object1, T object2) {
		Object value1 = null;
		Object value2 = null;
		try {
			value1 = PropertyResolver.getValue(propName, object1);
			value2 = PropertyResolver.getValue(propName, object2);
		} catch (WicketRuntimeException e) {
			// ignore property not know
		}
		if (value1 !=null && value2 != null) {
			if (value1 instanceof String && value2 instanceof String) {
				return ((String) value1).toLowerCase().compareTo(((String) value2).toLowerCase());
			} else if (value1 instanceof Comparable && value2 instanceof Comparable ) {
				return ((Comparable)value1).compareTo((Comparable)value2);
			} else {
				return (value1.toString()).compareTo(value2.toString());
			}
		} else {
			// null comparison
			return value1 == null ? (value2 == null ? 0 : -1) :  1;
		}
	}
}