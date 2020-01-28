package dk.jyskit.waf.wicket.components.tables.bootstraptable.colums.checkmark;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Checkmark column for boolean property expressions. The check marsk can be
 * customized either by providing label models for check and nocheck or by
 * overridding {@link #createCheck(String)} and {@link #createNocheck(String)}
 *
 * @author m43634
 *
 * @param <T>
 */
public class CheckmarkColumn<T> extends PropertyColumn<T, String> {
	/**
	 * Different markup styles for the {@link CheckmarkColumn}
	 * @author m43634
	 *
	 */
	public enum Markup {
		CHECKMARK("\u2713", "&nbsp;"),
		CHECKMARK_HEAVY("\u2714", "&nbsp;"),
		BALLOT_X("\u2717", "&nbsp;"),
		BALLOT_X_HEAVY("\u2718", "&nbsp;"),
		ICON_CHECK("<i class=\"glyphicon glyphicon-ok\"></i>", "<i class=\"glyphicon glyphicon-minus\"></i>"),
		;
		public final String check;
		public final String no_check;
		private Markup(String check, String no_check) {
			this.check = check;
			this.no_check = no_check;
		}

	}

	private IModel<String> checkLabelModel = Model.of("\u2713");
	private IModel<String> nocheckLabelModel = Model.of("");


	public CheckmarkColumn(IModel<String> displayModel, String sortProperty, String propertyExpression) {
		super(displayModel, sortProperty, propertyExpression);
		style(Markup.ICON_CHECK);
	}

	public CheckmarkColumn(IModel<String> displayModel, String propertyExpression) {
		super(displayModel, propertyExpression);
		style(Markup.ICON_CHECK);
	}


	public CheckmarkColumn<T> style(Markup markup){
		return style(markup.check, markup.no_check);
	}

	public CheckmarkColumn<T> style(String check, String noCheck) {
		checkLabelModel = Model.of(check);
		nocheckLabelModel = Model.of(noCheck);
		return this;
	}

	@Override
	public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
		Object data = getDataModel(rowModel).getObject();
		if (Boolean.TRUE.equals(data)) {
			item.add(createCheck(componentId));
		} else {
			item.add(createNocheck(componentId));
		}
	}

	protected Component createCheck(String componentId) {
		return new Label(componentId, getCheckLabelModel()).setEscapeModelStrings(false);
	}

	protected Component createNocheck(String componentId) {
		return new Label(componentId, getNocheckLabelModel()).setEscapeModelStrings(false);
	}

	public IModel<String> getCheckLabelModel() {
		return checkLabelModel;
	}

	public void setCheckLabelModel(IModel<String> checkLabelModel) {
		this.checkLabelModel = checkLabelModel;
	}

	public IModel<String> getNocheckLabelModel() {
		return nocheckLabelModel;
	}

	public void setNocheckLabelModel(IModel<String> nocheckLabelModel) {
		this.nocheckLabelModel = nocheckLabelModel;
	}
}
