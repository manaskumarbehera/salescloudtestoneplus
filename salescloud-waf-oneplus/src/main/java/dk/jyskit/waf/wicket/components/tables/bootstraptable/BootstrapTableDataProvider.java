package dk.jyskit.waf.wicket.components.tables.bootstraptable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;

import dk.jyskit.waf.utils.filter.And;
import dk.jyskit.waf.utils.filter.Filter;
import dk.jyskit.waf.utils.filter.Like;
import dk.jyskit.waf.utils.filter.Or;

public abstract class BootstrapTableDataProvider<T,S> extends SortableDataProvider<T, S> {
	private String filterText;
	private List<Integer> entityStateFilter = new ArrayList<>();

	protected abstract List<T> getPage(long first, long count, String filterText);

	protected abstract long getTotalNumberOfRecords(String filterText);

	/**
	 * Typically do this:
	 * 		Model.of(object)
	 * or
	 * 		new DetachableMyClassModel(object)
	 *
	 * @param object
	 * @return
	 */
	protected abstract IModel<T> createModel(T object);

	@Override
	public Iterator<T> iterator(long first, long count) {
		return getPage(first, count, filterText).iterator();
	}

	/**
	 * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
	 */
	@Override
	public long size() {
		return getTotalNumberOfRecords(filterText);
	}

	/**
	 * @see org.apache.wicket.markup.repeater.data.IDataProvider#model(java.lang.Object)
	 */
	@Override
	public IModel<T> model(T object) {
		return createModel(object);
	}

	public void setFilter(String text) {
		this.filterText = text;
	}

	/**
	 * Builds a filter which will be useful for most tables. If user searches for "John Doe", the filter
	 * will accept properties with exact or partial match, such as name="John Doe Smith". But it will
	 * also accept records such as firstname="John Mayer", lastname="Doe".
	 * Make sure to separate property names properly, or an exception will be thrown. Normally, string
	 * properties are put in the caseInsensitivePropertyNames array, but if you want filtering to be
	 * case-sensitive, put all properties in otherPropertyNames together with numeric properties, etc.
	 *
	 * @param filterText
	 * @param caseInsensitivePropertyNames
	 * @param otherPropertyNames
	 * @return
	 */
	public Filter getStandardOrFilter(String filterText, String[] caseInsensitivePropertyNames, String[] otherPropertyNames) {
		if (StringUtils.isBlank(filterText)) {
			return null;
		}
		String[] filterTextElements = StringUtils.split(filterText, ' ');
		Or[] ors = new Or[filterTextElements.length];
		for (int f = 0; f < filterTextElements.length; f++) {
			Like[] likes = new Like[caseInsensitivePropertyNames.length + otherPropertyNames.length];
			for (int l = 0; l < likes.length; l++) {
				if (l < caseInsensitivePropertyNames.length) {
					likes[l] = new Like(caseInsensitivePropertyNames[l], filterTextElements[f], false);
				} else {
					// Note: for non-string properties we can't specify case-insensitive filtering (causes error)
					likes[l] = new Like(otherPropertyNames[l-caseInsensitivePropertyNames.length], filterTextElements[f]);
				}
			}
			ors[f] = new Or(likes);
		}
		if (ors.length == 1) {
			return ors[0];
		} else {
			return new And(ors);
		}
	}

	public List<Integer> getEntityStateFilter() {
		return entityStateFilter;
	}

	public void setEntityStateFilter(List<Integer> filterStates) {
		this.entityStateFilter = filterStates;
	}

}