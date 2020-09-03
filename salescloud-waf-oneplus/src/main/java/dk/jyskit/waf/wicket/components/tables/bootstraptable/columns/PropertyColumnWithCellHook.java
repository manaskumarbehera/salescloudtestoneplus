package dk.jyskit.waf.wicket.components.tables.bootstraptable.columns;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

public class PropertyColumnWithCellHook<T> extends PropertyColumn<T, String> {

	public PropertyColumnWithCellHook(IModel<String> displayModel, String sortProperty, String propertyExpression) {
		super(displayModel, sortProperty, propertyExpression);
	}

	public PropertyColumnWithCellHook(IModel<String> displayModel, String propertyExpression) {
		super(displayModel, propertyExpression);
	}


	@Override
	public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
		Component newCell = newCellForRow(componentId, rowModel);
		item.add(newCell);
	}

	protected Component newCellForRow(String componentId, IModel<T> rowModel) {
		IModel<Object> dataModel = getDataModel(rowModel);
		Component newCell = newCell(componentId, dataModel);
		return newCell;
	}

	protected Component newCell(String componentId, IModel<Object> dataModel) {
		return new Label(componentId, dataModel);
	}
}
