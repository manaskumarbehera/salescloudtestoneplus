package dk.jyskit.waf.wicket.components.tables.bootstraptable.columns;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

/**
 * Custom Column expected to be decorated with a {@link ColumnDecorator}.
 *
 * @author palfred
 *
 * @param <R>
 *            The row model type
 */
public interface ICellCreator<R> extends Serializable {

	Component newCell(String componentId, IModel<R> rowModel);

}
