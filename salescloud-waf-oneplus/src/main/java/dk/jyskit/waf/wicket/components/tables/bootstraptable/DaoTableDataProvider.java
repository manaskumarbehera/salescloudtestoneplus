package dk.jyskit.waf.wicket.components.tables.bootstraptable;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.model.IModel;

import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.application.model.BaseEntity;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.application.model.EntityState;
import dk.jyskit.waf.utils.filter.And;
import dk.jyskit.waf.utils.filter.Equal;
import dk.jyskit.waf.utils.filter.Filter;
import dk.jyskit.waf.utils.filter.Or;

/**
 * Generic BootstrapTableDataProvider for {@link BaseEntity} with {@link Dao}
 * If the entity is {@link WithEntityState} then the {@link #getStateFilter()} can be used to limit the list to the states in the filterStates list.
 * @author palfred
 *
 * @param <E> The Entity type
 * @param <D> The Dao type
 */
public class DaoTableDataProvider<E extends BaseEntity, D extends Dao<E>> extends BootstrapTableDataProvider<E, String> {

	/**
	 * Generic factory.
	 * @param dao
	 * @param initialSortProperty
	 * @param initialSortOrder
	 * @param filterProps
	 * @return
	 */
	public static <En extends BaseEntity,Da extends Dao<En>> DaoTableDataProvider<En,Da> create(Da dao, String initialSortProperty, SortOrder initialSortOrder, String... filterProps) {
		return new DaoTableDataProvider<En,Da>(dao, initialSortProperty, initialSortOrder, filterProps);
	}

	public static <En extends BaseEntity,Da extends Dao<En>> DaoTableDataProvider<En,Da> create(Da dao, String initialSortProperty, SortOrder initialSortOrder, Filter initialFilter, String... filterProps) {
		return new DaoTableDataProvider<En,Da>(dao, initialSortProperty, initialSortOrder, initialFilter, filterProps);
	}

	private static final long serialVersionUID = 1L;

	private D dao;

	private String[] filterProps = new String[0];
	private String[] filterPropsCaseSensitive = new String[0];

	private Filter initialFilter;

	public String[] getFilterProps() {
		return filterProps;
	}

	public void setFilterProps(String... filterProps) {
		this.filterProps = filterProps;
	}

	public String[] getFilterPropsCaseSensitive() {
		return filterPropsCaseSensitive;
	}

	public void setFilterPropsCaseSensitive(String... filterPropsCaseSensitive) {
		this.filterPropsCaseSensitive = filterPropsCaseSensitive;
	}

	/**
	 * constructor
	 */
	public DaoTableDataProvider(D dao, String initialSortProperty, SortOrder initialSortOrder, String... filterProps) {
		this.dao = dao;
		this.filterProps = filterProps;
		setSort(initialSortProperty, initialSortOrder);
	}

	public DaoTableDataProvider(D dao, String initialSortProperty, SortOrder initialSortOrder, Filter initialFilter, String... filterProps) {
		this.dao = dao;
		this.initialFilter = initialFilter;
		this.filterProps = filterProps;
		setSort(initialSortProperty, initialSortOrder);
	}

	@Override
	protected List<E> getPage(long first, long count, String filterText) {
		return dao.findPage(first, count, getSort(), buildFilter(filterText));
	}

	public Filter buildFilter(String filterText) {
		Filter propsFilter = getStandardOrFilter(filterText, filterProps, filterPropsCaseSensitive);
		if (initialFilter != null) {
			if (propsFilter == null) {
				propsFilter = initialFilter;
			} else {
				propsFilter = new And(initialFilter, propsFilter);
			} 
		}
		
		List<Integer> states = getEntityStateFilter();
		if (states.isEmpty()) {
			return propsFilter;
		} else {
			Filter[] stateFilters = new Filter[states.size()];
			Iterator<Integer> si = states.iterator();
			for (int i = 0; i < stateFilters.length; i++) {
				Integer state = si.next();
				stateFilters[i] = new Equal("entityState", EntityState.of(state));
			}
			Filter stateFilter = stateFilters.length == 1 ? stateFilters[0] : new Or(stateFilters);
			return (propsFilter == null) ? stateFilter : new And(propsFilter, stateFilter);
		}
	}

	@Override
	protected long getTotalNumberOfRecords(String filterText) {
		return dao.getCount(buildFilter(filterText));
	}

	@Override
	protected IModel<E> createModel(E entity) {
		return new EntityModel<E>(entity);
	}
}
