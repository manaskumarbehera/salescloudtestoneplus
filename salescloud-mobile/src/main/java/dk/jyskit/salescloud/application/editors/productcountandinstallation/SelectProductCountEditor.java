package dk.jyskit.salescloud.application.editors.productcountandinstallation;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.events.ValueChangedEvent;
import dk.jyskit.salescloud.application.model.MobileContractMode;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.waf.wicket.components.forms.behaviours.FloatingLabelBehaviour;
import dk.jyskit.waf.wicket.utils.WicketUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEventSource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.model.PropertyModel;

import java.util.Objects;

public class SelectProductCountEditor extends FormComponentPanel<Object> implements IEventSource {
    private NumberTextField<Integer>countNewField;
    private NumberTextField<Integer>countExistingField;

	protected Product product;
	protected Integer countNew;
	protected Integer countExisting;
	protected Integer oldCountNew;
	protected Integer oldCountExisting;

	public SelectProductCountEditor(String id, final PropertyModel propertyModel) {
		this(id, propertyModel, true);
	}

	public Integer getCountNew() {
		return countNew;
	}

	public Integer getCountExisting() {
		return countExisting;
	}

	public SelectProductCountEditor(String id, final PropertyModel propertyModel, boolean pullRight) {
        super(id, propertyModel);
        
        setOutputMarkupId(true);
        
        product				= ((ProductCountAndInstallation) propertyModel.getObject()).getProduct();
        countNew			= ((ProductCountAndInstallation) propertyModel.getObject()).getCountNew();
        countExisting		= ((ProductCountAndInstallation) propertyModel.getObject()).getCountExisting();
        oldCountNew			= countNew;
        oldCountExisting	= countExisting;

		// For updating of "values"
		add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// Nothing needed
			}
		});

		WebMarkupContainer container = new WebMarkupContainer("container");
		container.add(AttributeModifier.replace("class", pullRight ? "pull-right" : "pull-left"));
		add(container);

        {
    		countExistingField = new NumberTextField<Integer>("countExisting", new PropertyModel<Integer>(this, "countExisting"));
			countExistingField.setOutputMarkupId(true);
    		countExistingField.setMinimum(product.getMinCount());
    		countExistingField.setMaximum(product.getMaxCount() == 0 ? Integer.MAX_VALUE : product.getMaxCount());
			countExistingField.add(new FloatingLabelBehaviour());
			if (MobileSession.get().isBusinessAreaOnePlus()) {
				countExistingField.add(AttributeModifier.append("placeholder", "Konvertering"));
			} else if (MobileSession.get().isBusinessAreaTdcOffice()) {
				countExistingField.add(AttributeModifier.append("placeholder", "Tilkøb"));
			} else {
				countExistingField.add(AttributeModifier.append("placeholder", "Genforhandling"));
			}

			countExistingField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
    			@Override
    			protected void onUpdate(AjaxRequestTarget target) {
    				convertInput();
    			}
    			@Override
    			protected void onError(AjaxRequestTarget target, RuntimeException e) {
    				super.onError(target, e);
    				target.add(SelectProductCountEditor.this.getParent().getParent());
    			}
    		});
    		
    		if (!MobileSession.get().getContract().getContractMode().isExistingAccount()) {
    			countExistingField.setVisible(false);
    		} else {
    			countExistingField.setRequired(true);
    		}
    		container.add(countExistingField);
        }
		
        {
    		countNewField = new NumberTextField<Integer>("countNew", new PropertyModel<Integer>(this, "countNew"));
			countNewField.setOutputMarkupId(true);
    		countNewField.setMinimum(product.getMinCount());
    		countNewField.setMaximum(product.getMaxCount() == 0 ? Integer.MAX_VALUE : product.getMaxCount());

			if (MobileSession.get().getContract().getContractMode().isNewAccount() &&
					MobileSession.get().getContract().getContractMode().isExistingAccount()) {
				countNewField.add(new FloatingLabelBehaviour());
				countNewField.add(AttributeModifier.append("placeholder", "Nysalg"));
			}

    		countNewField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
    			@Override
    			protected void onUpdate(AjaxRequestTarget target) {
    				convertInput();
    			}
    			@Override
    			protected void onError(AjaxRequestTarget target, RuntimeException e) {
    				super.onError(target, e);
    				target.add(SelectProductCountEditor.this.getParent().getParent());
    			}
    		});

//    		if (MobileContractMode.RENEGOTIATION.equals(MobileSession.get().getContract().getContractMode())) {
//    			countNewField.add(new FloatingLabelBehaviour());
//    			countNewField.add(AttributeModifier.append("placeholder", "Nysalg"));
//    		}
			if (!MobileSession.get().getContract().getContractMode().isNewAccount()) {
				countNewField.setVisible(false);
			} else {
				countNewField.setRequired(true);
			}
    		countNewField.setRequired(true);
			container.add(countNewField);
        }
    }

    public void onMaxLimitsChanged(int maxNew, int maxExisting, AjaxRequestTarget target) {
    	countNewField.setMaximum(maxNew);
    	if (countNew > maxNew) {
    		countNew = maxNew;
		}
    	countExistingField.setMaximum(maxExisting);
    	if (countExisting > maxExisting) {
    		countExisting = maxExisting;
		}
    	if (countNewField.isVisible()) {
			target.add(countNewField);
		}
		if (countExistingField.isVisible()) {
			target.add(countExistingField);
		}
	}

	@Override
	protected void onModelChanged() {
		super.onModelChanged();
		countNew			= ((ProductCountAndInstallation) getModel().getObject()).getCountNew();
		countExisting		= ((ProductCountAndInstallation) getModel().getObject()).getCountExisting();
	}

	@Override
    protected void convertInput() {
		SelectProductCountEditorPanel panel = (SelectProductCountEditorPanel) WicketUtils.findParentOfClass(this, SelectProductCountEditorPanel.class);
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
    	setConvertedInput(new ProductCountAndInstallation(product, null, countNew, countExisting));
    }
}