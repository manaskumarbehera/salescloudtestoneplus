package dk.jyskit.waf.wicket.components.tables.bootstraptable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import dk.jyskit.waf.utils.filter.Filter;
import dk.jyskit.waf.wicket.utils.PropertyComparator;

/**
 * Bootstrap table list provider.
 * Only need to procide a {@link #getData()} methor to  priovide the data for the table.
 * @author palfred
 *
 * @param <T>
 */
public abstract class AbstractBootstrapListDataProvider<T extends Serializable> extends BootstrapTableDataProvider<T, String> {
	private static final long serialVersionUID = 1L;
	protected String[] filterProps = new String[0];
	private String[] filterPropsCaseSensitive = new String[0];
	/**
	 * constructor
	 * @param filterProps
	 */
	public AbstractBootstrapListDataProvider(String... filterProps) {
		this.filterProps = filterProps;
	}

	@Override
	protected List<T> getPage(long first, long count, String filterText) {
		List<T> list = getData();
		// filtering
		List<T> filteredList = new ArrayList<>();
		Filter filter = getFilter(filterText);
		if (filter != null) {
			for (T t : list) {
				if (filter.passesFilter(t)) {
					filteredList.add(t);
				}
			}
		} else {
			filteredList.addAll(list);
		}

		// Handle sorting
		SortParam<String> sort = this.getSort();
		if (sort != null) {
			sort(filteredList, sort);
		}

		long toIndex = first + count;
		if (toIndex > filteredList.size())
		{
			toIndex = filteredList.size();
		}
		return filteredList.subList((int)first, (int)toIndex);
	}

	protected void sort(List<T> filteredList, SortParam<String> sort) {
		Comparator<T> sortComparator = new PropertyComparator<T>(sort.getProperty());
		if (!sort.isAscending()) {
			sortComparator = Collections.reverseOrder(sortComparator);
		}
		Collections.sort(filteredList, sortComparator);
	}

	protected abstract List<T> getData();

	@Override
	protected long getTotalNumberOfRecords(String filterText) {
		return getData().size();
	}

	@Override
	protected IModel<T> createModel(T entity) {
		return Model.of(entity);
	}

	protected Filter getFilter(String filterText) {
		return getStandardOrFilter(filterText, filterProps, filterPropsCaseSensitive);
	}

	public String[] getFilterProps() {
		return filterProps;
	}

	public void setFilterProps(String[] filterProps) {
		this.filterProps = filterProps;
	}

	public String[] getFilterPropsCaseSensitive() {
		return filterPropsCaseSensitive;
	}

	public void setFilterPropsCaseSensitive(String[] filterPropsCaseSensitive) {
		this.filterPropsCaseSensitive = filterPropsCaseSensitive;
	}
}
