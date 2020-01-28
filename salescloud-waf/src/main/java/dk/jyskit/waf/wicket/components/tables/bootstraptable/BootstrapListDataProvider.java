package dk.jyskit.waf.wicket.components.tables.bootstraptable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;

import dk.jyskit.waf.application.model.BaseEntity;
import dk.jyskit.waf.application.model.EntityState;


/**
 * Concrete list data provider based on a given list of data for table.
 * @author palfred
 *
 * @param <T>
 */
public class BootstrapListDataProvider<T extends Serializable> extends AbstractBootstrapListDataProvider<T> {

	private final IModel<List<T>> list;

	public BootstrapListDataProvider(List<T> list, String... filterProps) {
		this(new ListModel<T>(list), filterProps);
	}

	public BootstrapListDataProvider(IModel<List<T>> list, String... filterProps) {
		super(filterProps);
		this.list = list;
	}

	@Override
	protected List<T> getData() {
		List<Integer> entityStateFilter = getEntityStateFilter();
		if (entityStateFilter.size() == 0) {
			return list.getObject();
		} else {
			List<T> filteredList = new ArrayList<>();
			for (T obj : list.getObject()) {
				for (Integer state : entityStateFilter) {
					if (state.equals(EntityState.ALL_VAL)) {
						filteredList.add(obj);
						break;
					} else {
						BaseEntity entity = (BaseEntity) obj;
						if (entity.getEntityState().equals(EntityState.of(state))) {
							filteredList.add(obj);
							break;
						}
					}
				}
			}
			return filteredList;
		}
	}

}