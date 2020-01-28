package dk.jyskit.salescloud.application.pages.subscriptionconfiguration.columns;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import dk.jyskit.salescloud.application.dao.SubscriptionDao;
import dk.jyskit.salescloud.application.model.Subscription;
import dk.jyskit.waf.utils.guice.Lookup;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectOptions;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectSingle;

public class DropDownColumn<T> extends SubscriptionDetailColumn<T> {

	private String propertyExpression;
	private List<T> choices;
	private IModel<String> headerModel;

	/**
	 * @param headerModel
	 * @param propertyExpression
	 */
	public DropDownColumn(IModel<String> headerModel, final String propertyExpression, List<T> choices) {
		this.headerModel = headerModel;
		this.propertyExpression = propertyExpression;
		this.choices = choices;
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

		BootstrapSelectSingle<T> dropDown = new BootstrapSelectSingle<>(componentId, getDataModel(rowModel), choices).withShowTick(); 
		BootstrapSelectOptions options = new BootstrapSelectOptions();
		options.setContainer("body");
		dropDown.setOptions(options);
		
		WrapperPanel panel = new WrapperPanel(componentId, "select", dropDown);
		panel.getWrapped().add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Lookup.lookup(SubscriptionDao.class).save(rowModel.getObject());
			}
		});
		cellItem.add(panel);
	}

	@Override
	public IModel<T> getDataModel(IModel<Subscription> rowModel) {
		PropertyModel<T> propertyModel = new PropertyModel<T>(rowModel, propertyExpression);
		return propertyModel;
	}
}
