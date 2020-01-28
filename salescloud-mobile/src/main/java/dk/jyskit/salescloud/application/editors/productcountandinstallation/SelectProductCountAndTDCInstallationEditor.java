package dk.jyskit.salescloud.application.editors.productcountandinstallation;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.model.PropertyModel;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.model.MobileContractMode;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.waf.wicket.components.forms.behaviours.FloatingLabelBehaviour;

public class SelectProductCountAndTDCInstallationEditor extends FormComponentPanel<Object> {
    private NumberTextField<Integer>countNewField;
    private NumberTextField<Integer>countExistingField;
	
	protected Product product;
	protected Integer countNew;
	protected Integer countExisting;
	protected boolean hasInstallationProducts;
	protected boolean installationSelected;
	protected boolean installationEditable;

    /**
     * @param id
     */
    public SelectProductCountAndTDCInstallationEditor(String id, final PropertyModel propertyModel) {
        super(id, propertyModel);
        
        setOutputMarkupId(true);
        
        product					= ((ProductCountAndInstallation) propertyModel.getObject()).getProduct();
        hasInstallationProducts	= ((ProductCountAndInstallation) propertyModel.getObject()).isHasInstallationProducts();
        installationSelected	= ((ProductCountAndInstallation) propertyModel.getObject()).isInstallationSelected();
        installationEditable	= ((ProductCountAndInstallation) propertyModel.getObject()).isInstallationEditable();
        countNew				= ((ProductCountAndInstallation) propertyModel.getObject()).getCountNew();
        countExisting			= ((ProductCountAndInstallation) propertyModel.getObject()).getCountExisting();
        
        {
    		countExistingField = new NumberTextField<Integer>("countExisting", new PropertyModel<Integer>(this, "countExisting"));
    		countExistingField.setMinimum(product.getMinCount());
    		countExistingField.setMaximum(product.getMaxCount() == 0 ? Integer.MAX_VALUE : product.getMaxCount()); 
    		countExistingField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
    			@Override
    			protected void onUpdate(AjaxRequestTarget target) {
    				convertInput();
    			}
    			@Override
    			protected void onError(AjaxRequestTarget target, RuntimeException e) {
    				super.onError(target, e);
    				target.add(SelectProductCountAndTDCInstallationEditor.this.getParent().getParent());
    			}
    		});
    		
    		countExistingField.setOutputMarkupId(true);
    		countExistingField.add(new FloatingLabelBehaviour());
			if (MobileSession.get().isBusinessAreaTdcOffice() || MobileSession.get().isBusinessAreaOnePlus()) {
				countExistingField.add(AttributeModifier.append("placeholder", "Tilkøb"));
			} else {
				countExistingField.add(AttributeModifier.append("placeholder", "Genforhandling"));
			}
    		
    		if (!MobileContractMode.RENEGOTIATION.equals(MobileSession.get().getContract().getContractMode())) {
    			countExistingField.setVisible(false);
    		} else {
    			countExistingField.setRequired(true);
    		}
    		add(countExistingField);
        }
		
        {
    		countNewField = new NumberTextField<Integer>("countNew", new PropertyModel<Integer>(this, "countNew"));
    		countNewField.setMinimum(product.getMinCount());
    		countNewField.setMaximum(product.getMaxCount() == 0 ? Integer.MAX_VALUE : product.getMaxCount()); 
    		countNewField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
    			@Override
    			protected void onUpdate(AjaxRequestTarget target) {
    				convertInput();
    			}
    			@Override
    			protected void onError(AjaxRequestTarget target, RuntimeException e) {
    				super.onError(target, e);
    				target.add(SelectProductCountAndTDCInstallationEditor.this.getParent().getParent());
    			}
    		});
    		
    		countNewField.setOutputMarkupId(true);
    		if (MobileContractMode.RENEGOTIATION.equals(MobileSession.get().getContract().getContractMode())) {
    			countNewField.add(new FloatingLabelBehaviour());
    			countNewField.add(AttributeModifier.append("placeholder", "Nysalg"));
    		}
    		countNewField.setRequired(true);
    		add(countNewField);
        }
        
        {
    		final WebMarkupContainer icon = new WebMarkupContainer("icon");
    		icon.setOutputMarkupId(true);
    		
    		AjaxLink button = new AjaxLink("installationSelectedButton") {
    			@Override
    			public void onClick(AjaxRequestTarget target) {
    	    		if (installationEditable) {
        				installationSelected = !installationSelected;
        				convertInput();
        				icon.add(AttributeModifier.replace("class", installationSelected ? "fa fa-check-square-o" : "fa fa-square-o"));
        				
        				add(AttributeModifier.replace("class", installationSelected ? "btn btn-primary" : "btn btn-default"));
        				
        				target.add(SelectProductCountAndTDCInstallationEditor.this.getParent().getParent());
    	    		}
    			}
    		};
    		
    		if (!installationEditable) {
    			button.add(AttributeModifier.replace("disabled", "disabled"));
    		}
    		
    		icon.add(AttributeModifier.replace("class", installationSelected ? "fa fa-check-square-o" : "fa fa-square-o"));
    		button.add(AttributeModifier.replace("class", installationSelected ? "btn btn-primary" : "btn btn-default"));
    		button.setOutputMarkupId(true);
    		add(button);
    		
    		icon.setOutputMarkupId(true);
    		button.add(icon);
    		
    		if (!hasInstallationProducts) {
    			button.setVisible(false);
    		}
        }
    }

    @Override
    protected void convertInput() {
    	setConvertedInput(new ProductCountAndInstallation(product, null, countNew, countExisting, hasInstallationProducts, installationSelected, installationEditable));
    }
}