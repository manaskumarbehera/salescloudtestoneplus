package dk.jyskit.waf.wicket.components.tables.bootstraptable.columns;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.ILabelStrategy;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions.ActionCellCreator;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions.EntityAction;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.colums.checkmark.CheckmarkColumn;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.colums.checkmark.CheckmarkColumn.Markup;

/**
 * Factory for aid of column creation for data table.
 *
 * @author palfred
 *
 */
public class ColumnFactory<T> {
	public enum SupportedType {
		Checkmark, Text, Custom, CellCreator
	}

	@Wither
	@AllArgsConstructor
	@NoArgsConstructor
	public class Builder {
		private String property;
		private String sortProperty;
		private SupportedType type = SupportedType.Text;
		private boolean sortable = true;
		private boolean wrap = true;
		private IModel<String> header;
		private String cssStyle;
		private String cssClass;
		private IColumn<T, String> customColumn;
		private ICellCreator<T> cellCreator;
		private boolean decoration = true;

		public IColumn<T, String> build() {
			String sort = (sortable && sortProperty == null) ? property : sortProperty;
			switch (type) {
			case Checkmark: {
				CheckmarkColumn<T> col = new CheckmarkColumn<>(header, sort, property);
				col.style(Markup.BALLOT_X);
				return col;
			}
			case Custom: {
				if (customColumn != null) {
					if (decoration) {
						ColumnDecorator<T> col = new ColumnDecorator<T>(customColumn, header, cssStyle, cssClass,
								sortable ? sortProperty : null);
						return col;
					}
					return customColumn;
				}
			}
			case CellCreator: {
				if (cellCreator != null) {
					ColumnDecorator<T> col = new ColumnDecorator<T>(cellCreator, header, cssStyle, cssClass,
							sortable ? sortProperty : null);
					return col;
				}
			}

			default: {
				PropertyColumnWithCellCss<T> column = new PropertyColumnWithCellCss<>(header, sort, property);
				if (!wrap) {
					column.withNoWrap();
				}
				if (cssStyle != null) {
					column.setCellCss(cssStyle);
				}
				if (cssClass != null) {
					column.setCellCss(cssClass);
				}
				return column;

			}
			}
		}

		public IColumn<T, String> add() {
			IColumn<T, String> col = build();
			columns.add(col);
			return col;
		}

		public Builder withNoSort() {
			return withSortable(false).withSortProperty(null);
		}

		public Builder withNoWrap() {
			return withCssStyle("white-space: nowrap;");
		}

		public Builder withHeaderKey(String key) {
			return withHeader(labelStrategy.columnLabel(key));
		}

		public Builder withNoHeader() {
			return withHeader(Model.of(""));
		}

		public Builder withCustomColumn(IColumn<T, String> column) {
			return new Builder(this.property, this.sortProperty, SupportedType.Custom, this.sortable, this.wrap, this.header,
					this.cssStyle, this.cssClass, column, null, decoration);
		}

		public Builder withCellCreator(ICellCreator<T> cellCreator) {
			return new Builder(this.property, this.sortProperty, SupportedType.Custom, this.sortable, this.wrap, this.header,
					this.cssStyle, this.cssClass, null, cellCreator, decoration);
		}

		public Builder withAction(EntityAction<T>[] actions) {
			@SuppressWarnings("unchecked")
			ActionCellCreator<T> acc = (ActionCellCreator<T>) ((cellCreator instanceof ActionCellCreator<?>) ? cellCreator : new ActionCellCreator<T>());
			acc.addActions(actions);
			return withCellCreator(acc);
		}

		public Builder withActions(Iterable<EntityAction<T>> actions) {
			@SuppressWarnings("unchecked")
			ActionCellCreator<T> acc = (ActionCellCreator<T>) ((cellCreator instanceof ActionCellCreator<?>) ? cellCreator : new ActionCellCreator<T>());
			acc.addActions(actions);
			return withCellCreator(acc);
		}

		public Builder withNoDecorator() {
			return withDecoration(false);
		}

		public Builder withCheckmark() {
			return withType(SupportedType.Checkmark);
		}

	}

	public Builder column(String property) {
		return new Builder().withProperty(property).withHeader(labelStrategy.columnLabel(property));
	}

	public Builder column(IColumn<T, String> column) {
		return new Builder().withCustomColumn(column);
	}

	public Builder column(ICellCreator<T> creator) {
		return new Builder().withCellCreator(creator);
	}

	public Builder column(@SuppressWarnings("unchecked") EntityAction<T>... actions) {
		return new Builder().withAction(actions).withNoHeader();
	}

	public Builder column(Iterable<EntityAction<T>> actions) {
		return new Builder().withActions(actions);
	}

	private ILabelStrategy labelStrategy;
	private final List<IColumn<T, String>> columns = new ArrayList<>();

	public List<IColumn<T, String>> getColumns() {
		return columns;
	}

	public static <C> ColumnFactory<C> create(ILabelStrategy labelStrategy) {
		return new ColumnFactory<C>(labelStrategy);
	}

	public ColumnFactory(ILabelStrategy labelStrategy) {
		super();
		this.labelStrategy = labelStrategy;
	}

	public ILabelStrategy getLabelStrategy() {
		return labelStrategy;
	}

	public void setLabelStrategy(ILabelStrategy labelStrategy) {
		this.labelStrategy = labelStrategy;
	}

}
