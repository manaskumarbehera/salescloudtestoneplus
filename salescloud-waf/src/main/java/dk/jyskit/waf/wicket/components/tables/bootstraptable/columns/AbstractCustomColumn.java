package dk.jyskit.waf.wicket.components.tables.bootstraptable.columns;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * Abstract Panel column for property expressions. Makes it a little easier to
 * create custom columns based on a property model.
 *
 * @author palfred
 *
 * @param <R>
 *            The row model type
 * @param <C>
 *            The cell model type
 * @deprecated Consider use of {@link ICellCreator} and {@link ColumnDecorator}
 */
public abstract class AbstractCustomColumn<R, C> extends PropertyColumn<R, String> {

	public AbstractCustomColumn(IModel<String> displayModel, String sortProperty, String propertyExpression) {
		super(displayModel, sortProperty, propertyExpression);
	}

	public AbstractCustomColumn(IModel<String> displayModel, String propertyExpression) {
		super(displayModel, propertyExpression);
	}

	@Override
	public void populateItem(Item<ICellPopulator<R>> item, String componentId, IModel<R> rowModel) {
		PropertyModel<C> cellModel = new PropertyModel<C>(rowModel, getPropertyExpression());
		item.add(createCellPanel(componentId, cellModel, rowModel));
	}

	/**
	 * Creates the component for the table cell.
	 * @param wicketId The id the component must be given-
	 * @param cellModel The model for the cell (property model with the given propertyExpression).
	 * @param rowModel
	 * @return The component to be added to the cell
	 */
	protected abstract Component createCellPanel(String wicketId, IModel<C> cellModel, IModel<R> rowModel);

}
