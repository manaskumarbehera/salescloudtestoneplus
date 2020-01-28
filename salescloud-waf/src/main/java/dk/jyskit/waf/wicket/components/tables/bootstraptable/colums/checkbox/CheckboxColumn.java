package dk.jyskit.waf.wicket.components.tables.bootstraptable.colums.checkbox;

import java.io.Serializable;
import java.util.Collection;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

public abstract class CheckboxColumn<T> extends AbstractColumn<T, String> {

    private IModel<Collection<? extends Serializable>> selectionModel;

	public CheckboxColumn(IModel<String> titleModel, IModel<Collection<? extends Serializable>> selectionModel) {
        super(titleModel);
		this.selectionModel = selectionModel;
	}

	public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
		item.add(new CheckBoxPanel(componentId, selectionModel, getModelObjectToken(rowModel)));
	}

    /**
	 * Generates a token from the model object that will represent the selected state of this row.
	 * The implementation should usually return a primary key of the object inside the provided
	 * model.
	 *
	 * Example: <code>return ((User)model.getObject()).getId();</code>
	 *
	 * @param model
	 *            model that contains an object bound to current row of the table
	 * @return a token which will uniquely identify the selection of this row
	 */
	protected abstract Serializable getModelObjectToken(IModel<T> model);

    @SuppressWarnings({"unchecked"})
    private T getModelObject(IModel model) {
        return (T) model.getObject();
    }

    protected boolean isEnabled(T row) {
        return true;
    }

	private static class CheckBoxPanel<T> extends Panel implements IMarkupResourceStreamProvider {
		private static final long serialVersionUID = 1L;

		public CheckBoxPanel(String id, IModel<Collection<Serializable>> model, Serializable token) {
			super(id, model);
			add(new AjaxCheckBox("checkbox", new CheckboxModel(model, token)) {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					// do nothing!
				}
			});
		}

		public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
			return new StringResourceStream(
				"<wicket:panel><input wicket:id=\"checkbox\" type=\"checkbox\"/></wicket:panel>");
		}
	}
}
