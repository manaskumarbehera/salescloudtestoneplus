package dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

//import dk.mobilethink.common.wicket.crud.list.actions.EntityAction;
//import dk.mobilethink.common.wicket.crud.list.actions.MultiActionsPanel;
//import dk.mobilethink.cornerstone.wicket.util.datatable.HeaderColumnFilter;

/**
 * General action column for entity overview.
 * @author jmi
 *
 * @param <T>
 */
public class MultiActionsColumn<T, S> extends AbstractColumn<T, S> {
	private static final long serialVersionUID = 1L;
	private final EntityAction<T>[] actions;

	@SafeVarargs
	public MultiActionsColumn(IModel<String> displayModel, EntityAction<T> ... actions) {
		super(displayModel);
		this.actions = actions;
	}

	// add the ActionsPanel to the cell item
	public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> model) {
		cellItem.add(new MultiActionsPanel<T>(componentId, model, actions));
	}

//	@Override
//	public Component getHeader(String componentId) {
//		return createHeaderActionPanel(componentId, entityType);
//	}
//
//	protected HeaderActionPanel<T> createHeaderActionPanel(String componentId, String entityType, CreateActionHandler createActionHandler) {
//		HeaderActionPanel<T> headerActionPanel = new HeaderActionPanel<T>(componentId, entityType, createActionHandler) {
//			@Override
//			public boolean isVisible() {
//				return isHeaderVisible();
//			}
//		};
//		return headerActionPanel;
//	}

	protected boolean isHeaderVisible() {
		return true;
	}
}
