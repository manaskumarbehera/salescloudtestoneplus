package dk.jyskit.salescloud.application.editors.simpleproductcount;

import dk.jyskit.salescloud.application.events.ValueChangedEvent;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.waf.wicket.utils.WicketUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEventSource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.model.PropertyModel;

import java.util.Objects;

public class SimpleProductCountEditor extends FormComponentPanel<Object> implements IEventSource {
	private NumberTextField<Integer>countNewField;

	protected Product product;
	protected Integer countNew;
	protected Integer oldCountNew;

	public SimpleProductCountEditor(String id, final PropertyModel propertyModel, boolean pullRight) {
		super(id, propertyModel);

		setOutputMarkupId(true);

		product			= ((SimpleProductCount) propertyModel.getObject()).getProduct();
		countNew		= ((SimpleProductCount) propertyModel.getObject()).getCountNew();
		oldCountNew		= countNew;

		{
			WebMarkupContainer container = new WebMarkupContainer("container");
			container.add(AttributeModifier.replace("class", pullRight ? "pull-right" : "pull-left"));
			add(container);
			countNewField = new NumberTextField<Integer>("countNew", new PropertyModel<Integer>(this, "countNew"));
			countNewField.setOutputMarkupId(true);
			countNewField.setMinimum(product.getMinCount());
			countNewField.setMaximum(product.getMaxCount() == 0 ? Integer.MAX_VALUE : product.getMaxCount());

//			if (MobileSession.get().getContract().getContractMode().isNewAccount() &&
//					MobileSession.get().getContract().getContractMode().isExistingAccount()) {
//				countNewField.add(new FloatingLabelBehaviour());
//				countNewField.add(AttributeModifier.append("placeholder", "Nysalg"));
//			}

//			countNewField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
//				@Override
//				protected void onUpdate(AjaxRequestTarget target) {
//					convertInput();
//				}
//				@Override
//				protected void onError(AjaxRequestTarget target, RuntimeException e) {
//					super.onError(target, e);
//					target.add(SimpleProductCountEditor.this.getParent().getParent());
//				}
//			});

			countNewField.add(new AjaxFormComponentUpdatingBehavior("oninput") {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					convertInput();
				}
				@Override
				protected void onError(AjaxRequestTarget target, RuntimeException e) {
					super.onError(target, e);
					target.add(SimpleProductCountEditor.this.getParent().getParent());
				}
			});

//			// For updating of "values"
//			countNewField.add(new OnChangeAjaxBehavior() {
//				@Override
//				protected void onUpdate(AjaxRequestTarget target) {
//					// Nothing needed
//				}
//			});
//
//			// Keyboard - with throttling
//			countNewField.add(new AjaxEventBehavior("keyup") {
//				@Override
//				protected void onEvent(AjaxRequestTarget target) {
//					convertInput();
//				}
//
//				@Override
//				protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
//					super.updateAjaxAttributes(attributes);
//					attributes.setThrottlingSettings(new ThrottlingSettings(countNewField.getMarkupId(), Duration.ONE_SECOND, true));
//				}
//			});


			countNewField.setRequired(true);
			container.add(countNewField);
		}
	}

    /**
     * @param id
     */
    public SimpleProductCountEditor(String id, final PropertyModel propertyModel) {
    	this(id, propertyModel, true);
    }

	@Override
	protected void onModelChanged() {
		super.onModelChanged();
		countNew	= ((SimpleProductCount) getModel().getObject()).getCountNew();
		oldCountNew = countNew;
	}

	@Override
    protected void convertInput() {
		SimpleProductCountEditorPanel panel = (SimpleProductCountEditorPanel) WicketUtils.findParentOfClass(this, SimpleProductCountEditorPanel.class);
		if (panel != null) {
			if (!Objects.equals(oldCountNew, countNew)) {
				ValueChangedEvent event = new ValueChangedEvent();
				event.setTarget(WicketUtils.getAjaxTarget());
				event.setEntityId(product.getId());
				event.setProductId(product.getProductId());
				event.setOldValue(oldCountNew);
				event.setNewValue(countNew);
				send(panel, Broadcast.BREADTH, event);
				oldCountNew = countNew;
			}
		}
    	setConvertedInput(new SimpleProductCount(product, countNew));
    }
}