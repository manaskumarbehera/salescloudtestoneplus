package dk.jyskit.waf.wicket.components.tables.bootstraptable.columns;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

/**
 * Property column with support for custom css on the table cell.
 * @author palfred
 *
 * @param <T>
 */
public class PropertyColumnWithCellCss<T> extends PropertyColumnWithCellHook<T> {
	private String cellCss;
	private String cellStyle;

	public String getCellStyle() {
		return cellStyle;
	}

	public void setCellStyle(String cellStyle) {
		this.cellStyle = cellStyle;
	}

	public PropertyColumnWithCellCss<T>  withCellStyle(String cellStyle) {
		setCellStyle(cellStyle);
		return this;
	}

	public PropertyColumnWithCellCss<T> withNoWrap() {
		return withCellStyle("white-space: nowrap;");
	}


	public String getCellCss() {
		return cellCss;
	}

	public void setCellCss(String cellCss) {
		this.cellCss = cellCss;
	}

	public PropertyColumnWithCellCss<T>  withCellCss(String cellCss) {
		setCellCss(cellCss);
		return this;
	}

	public PropertyColumnWithCellCss(IModel<String> displayModel, String sortProperty, String propertyExpression) {
		super(displayModel, sortProperty, propertyExpression);
	}

	public PropertyColumnWithCellCss(IModel<String> displayModel, String propertyExpression) {
		super(displayModel, propertyExpression);
	}

	@Override
	protected Component newCell(String componentId, IModel<Object> dataModel) {
		Component newCell = super.newCell(componentId, dataModel);
		if (!Strings.isEmpty(cellCss)) {
			newCell.add(AttributeAppender.append("class", cellCss));
		}
		if (!Strings.isEmpty(cellStyle)) {
			newCell.add(AttributeAppender.append("style", cellStyle));
		}
		return newCell;
	}
}
