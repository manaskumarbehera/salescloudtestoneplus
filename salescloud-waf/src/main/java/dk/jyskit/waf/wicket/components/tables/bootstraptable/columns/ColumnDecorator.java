package dk.jyskit.waf.wicket.components.tables.bootstraptable.columns;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.export.IExportableColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

/**
 * Decorator to allow styling of custom columns. This decoration dictates the column header and sort column.
 * @author palfred
 *
 * @param <T> The row type
 */
public class ColumnDecorator<T> implements IColumn<T, String>, IExportableColumn<T, String, Object> {
	private final IColumn<T, String> delegate;
	private final ICellCreator<T> cellCreator;
	private IModel<String> header;

	private String cssStyle;
	private String cssClass;

	private String sortProperty;

	public ColumnDecorator(ICellCreator<T> cellCreator, IModel<String> header, String cssStyle, String cssClass, String sortProperty) {
		this.cellCreator = cellCreator;
		this.delegate = null;
		this.header = header;
		this.cssStyle = cssStyle;
		this.cssClass = cssClass;
		this.sortProperty = sortProperty;
	}

	public ColumnDecorator(IColumn<T, String> delegate, IModel<String> header, String cssStyle, String cssClass, String sortProperty) {
		super();
		this.cellCreator = null;
		this.delegate = delegate;
		this.header = header;
		this.cssStyle = cssStyle;
		this.cssClass = cssClass;
		this.sortProperty = sortProperty;
	}

	@Override
	public Component getHeader(String componentId) {
		return new Label(componentId, getDisplayModel());
	}

	@Override
	public String getSortProperty() {
		return sortProperty;
	}

	@Override
	public boolean isSortable() {
		return sortProperty != null;
	}

	public void setSortProperty(String sortProperty) {
		this.sortProperty = sortProperty;
	}

	@Override
	public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
		if (cellCreator != null) {
			cellItem.add(cellCreator.newCell(componentId, rowModel));
		} else {
			delegate.populateItem(cellItem, componentId, rowModel);
		}
		Component cell = cellItem.get(componentId);
		if (!Strings.isEmpty(cssClass)) {
			cell.add(AttributeAppender.append("class", cssClass));
		}
		if (!Strings.isEmpty(cssStyle)) {
			cell.add(AttributeAppender.append("style", cssStyle));
		}
	}

	@Override
	public void detach() {
		if (delegate != null)  delegate.detach();
	}

	@Override
	public IModel<Object> getDataModel(IModel<T> rowModel) {
		if (delegate instanceof IExportableColumn<?, ?, ?>) {
			@SuppressWarnings("unchecked")
			IExportableColumn<T, String, Object> exportable = (IExportableColumn<T, String, Object>) delegate;
			return exportable.getDataModel(rowModel);
		}
		return null;
	}

	@Override
	public IModel<String> getDisplayModel() {
		if (header == null && delegate instanceof IExportableColumn<?, ?, ?>) {
			@SuppressWarnings("unchecked")
			IExportableColumn<T, String, Object> exportable = (IExportableColumn<T, String, Object>) delegate;
			return exportable.getDisplayModel();
		}
		return header;
	}

	public IModel<String> getHeader() {
		return header;
	}

	public void setHeader(IModel<String> header) {
		this.header = header;
	}

	public String getCssStyle() {
		return cssStyle;
	}

	public void setCssStyle(String cssStyle) {
		this.cssStyle = cssStyle;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}


	public ColumnDecorator<T> withSortProperty(String sortProperty) {
		setSortProperty(sortProperty);
		return this;
	}

	public ColumnDecorator<T> withCssClass(String cssClass) {
		setCssClass(cssClass);
		return this;
	}

	public ColumnDecorator<T> withCssStyle(String cssStyle) {
		this.cssStyle = cssStyle;
		return this;
	}


	public ColumnDecorator<T> withNoSort() {
		return withSortProperty(null);
	}

	public ColumnDecorator<T> withNoWrap() {
		return withCssStyle("white-space: nowrap;");
	}
}
