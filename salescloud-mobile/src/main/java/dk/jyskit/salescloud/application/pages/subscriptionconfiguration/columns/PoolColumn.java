package dk.jyskit.salescloud.application.pages.subscriptionconfiguration.columns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import dk.jyskit.salescloud.application.dao.SubscriptionDao;
import dk.jyskit.salescloud.application.model.MobileProduct;
import dk.jyskit.salescloud.application.model.OrderLine;
import dk.jyskit.salescloud.application.model.Subscription;
import dk.jyskit.waf.utils.guice.Lookup;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class PoolColumn extends SubscriptionDetailColumn<Boolean> {

	private IModel<String> headerModel;
	private MobileProduct product;
	private MutableInt remaining;
	private PoolColumnHeaderPanel headerPanel;
	private boolean forceAll;
	private List<AjaxCheckBox> checkBoxes;
	private boolean readonly;

	public PoolColumn(OrderLine orderLine, int used, boolean readonly) {
		this.readonly = readonly;
		product = (MobileProduct) orderLine.getProduct(); 
		headerModel = new Model(product.getPublicName());

		if (orderLine.isCustomFlag()) {
			forceAll = true;
			remaining = new MutableInt(0);
		} else {
			remaining = new MutableInt(orderLine.getTotalCount() - used);
		}
		checkBoxes = new ArrayList<>();
	}

	@Override
	public Component getHeader(final String componentId) {
		headerPanel = new PoolColumnHeaderPanel(componentId, getDisplayModel(), remaining); 
		return headerPanel;
	}
	
	@Override
	public IModel<String> getDisplayModel() {
		return headerModel;
	}

	@Override
	public void populateItem(Item<ICellPopulator<Subscription>> cellItem,
			String componentId, final IModel<Subscription> rowModel) {
		
		AjaxCheckBox checkBox = new AjaxCheckBox(componentId, (IModel<Boolean>) getDataModel(rowModel)) {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (getModelObject()) {
					rowModel.getObject().getProducts().add(product);
					remaining.decrement();
					if (remaining.intValue() == 0) {
						for(AjaxCheckBox cb : checkBoxes) {
							send(cb, Broadcast.EXACT, new PoolEvent(target, true)); 
						}
					}
					target.add(headerPanel);
				} else {
					rowModel.getObject().getProducts().remove(product);
					remaining.increment();
					if (remaining.intValue() == 1) {
						for(AjaxCheckBox cb : checkBoxes) {
							send(cb, Broadcast.EXACT, new PoolEvent(target, false)); 
						}
					}
					target.add(headerPanel);
				}
				Lookup.lookup(SubscriptionDao.class).save(rowModel.getObject());
			}
			
			@Override
			public void onEvent(IEvent<?> event) {
				if ((event.getPayload()) instanceof PoolEvent) {
					PoolEvent poolEvent = (PoolEvent) event.getPayload();
					if (poolEvent.isAllUsed()) {
						if (!getModelObject()) {
							setEnabled(false);
							poolEvent.getTarget().add(this);
						}
					} else {
						if (!getModelObject()) {
							setEnabled(true);
							poolEvent.getTarget().add(this);
						}
					}
				} else {
					super.onEvent(event);
				}
			}
			
			@Override
			protected void onConfigure() {
				if (!getModelObject()) {
					if (remaining.intValue() == 0) {
						setEnabled(false);
					}
				}
			}
		};
		if (readonly || forceAll) {
			checkBox.setEnabled(false);
		}
		checkBox.setOutputMarkupId(true);
		checkBoxes.add(checkBox);
		cellItem.add(new WrapperPanel(componentId, "input", checkBox, "type='checkbox'"));
	}

	@Override
	public IModel<Boolean> getDataModel(IModel<Subscription> rowModel) {
		boolean hasProduct = forceAll || rowModel.getObject().getProducts().contains(product);
		return new PropertyModel<Boolean>(new BooleanWrapper(hasProduct), "value");
	}

	@Data
	@RequiredArgsConstructor
	class BooleanWrapper implements Serializable {
		@NonNull private Boolean value;
	}
}
