package dk.jyskit.salescloud.application.pages.subscriptionconfiguration.columns;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import dk.jyskit.salescloud.application.dao.SubscriptionDao;
import dk.jyskit.salescloud.application.model.Subscription;
import dk.jyskit.waf.utils.guice.Lookup;

public class TextFieldColumn extends SubscriptionDetailColumn<String> {

	private String propertyExpression;
	private IModel<String> headerModel;

	/**
	 * @param headerModel
	 * @param propertyExpression
	 */
	public TextFieldColumn(IModel<String> headerModel, final String propertyExpression) {
		this.headerModel = headerModel;
		this.propertyExpression = propertyExpression;
	}

	@Override
	public Component getHeader(final String componentId) {
		return new Label(componentId, getDisplayModel());
	}
	
	@Override
	public IModel<String> getDisplayModel() {
		return headerModel;
	}

	@Override
	public void populateItem(Item<ICellPopulator<Subscription>> cellItem,
			String componentId, final IModel<Subscription> rowModel) {
		TextField<String> textField = new TextField<String>("textfield", getDataModel(rowModel));
		WrapperPanel panel = new WrapperPanel(componentId, "input", textField);
		panel.getWrapped()
			.add(AttributeModifier.append("class", "form-control"))
			.add(AttributeModifier.append("style", "min-width: 100px"))
			.add(new AjaxFormComponentUpdatingBehavior("onchange") {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
//					Lookup.lookup(SubscriptionDao.class).save(rowModel.getObject());
				}
			});
		cellItem.add(panel);
	}

	@Override
	public IModel<String> getDataModel(IModel<Subscription> rowModel) {
		return new PropertyModel<String>(rowModel, propertyExpression);
	}
}
