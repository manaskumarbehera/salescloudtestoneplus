package dk.jyskit.salescloud.application.editors.productcountandinstallation;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.PropertyModel;

import dk.jyskit.salescloud.application.model.Product;

public class SelectProductAndTDCInstallationEditor extends FormComponentPanel<Object> {
	protected Product product;
	protected boolean selected;
	protected boolean hasInstallationProducts;
	protected boolean installationSelected;
	protected boolean installationEditable;

    /**
     * @param id
     */
    public SelectProductAndTDCInstallationEditor(String id, final PropertyModel propertyModel) {
        super(id, propertyModel);
        
        setOutputMarkupId(true);
        
        product					= ((ProductCountAndInstallation) propertyModel.getObject()).getProduct();
        hasInstallationProducts	= ((ProductCountAndInstallation) propertyModel.getObject()).isHasInstallationProducts();
        installationSelected	= ((ProductCountAndInstallation) propertyModel.getObject()).isInstallationSelected();
        installationEditable	= ((ProductCountAndInstallation) propertyModel.getObject()).isInstallationEditable();
        selected				= (((ProductCountAndInstallation) propertyModel.getObject()).getCountNew() > 0);
		
        {
    		final WebMarkupContainer icon = new WebMarkupContainer("icon");
    		icon.setOutputMarkupId(true);
    		AjaxLink selectedButton = new AjaxLink("selectedButton") {
    			@Override
    			public void onClick(AjaxRequestTarget target) {
    				selected = !selected;
    				convertInput();
    				icon.add(AttributeModifier.replace("class", selected  ? "fa fa-check-square-o" : "fa fa-square-o"));
    				
    				add(AttributeModifier.replace("class", selected ? "btn btn-primary" : "btn btn-default"));

//    				if (!selected) {
//    					installationSelected =  false;
//    				}
    				target.add(SelectProductAndTDCInstallationEditor.this.getParent().getParent());
    			}
    		};
    		icon.add(AttributeModifier.replace("class", selected ? "fa fa-check-square-o" : "fa fa-square-o"));
    		selectedButton.add(AttributeModifier.replace("class", selected ? "btn btn-primary" : "btn btn-default"));
    		selectedButton.setOutputMarkupId(true);
    		add(selectedButton);
    		selectedButton.add(icon);
        }
        
        {
    		final WebMarkupContainer icon = new WebMarkupContainer("icon");
    		icon.setOutputMarkupId(true);
    		AjaxLink installationSelectedButton = new AjaxLink("installationSelectedButton") {
    			@Override
    			public void onClick(AjaxRequestTarget target) {
    	    		if (installationEditable) {
        				installationSelected = !installationSelected;
        				convertInput();
        				icon.add(AttributeModifier.replace("class", installationSelected  ? "fa fa-check-square-o" : "fa fa-square-o"));
        				
        				add(AttributeModifier.replace("class", installationSelected ? "btn btn-primary" : "btn btn-default"));
        				
        				target.add(SelectProductAndTDCInstallationEditor.this.getParent().getParent());
    	    		}
    			}
    		};
    		
    		if (!installationEditable) {
    			installationSelectedButton.add(AttributeModifier.replace("disabled", "disabled"));
    		}
    		
    		icon.add(AttributeModifier.replace("class", installationSelected ? "fa fa-check-square-o" : "fa fa-square-o"));
    		installationSelectedButton.add(AttributeModifier.replace("class", installationSelected ? "btn btn-primary" : "btn btn-default"));
    		installationSelectedButton.setOutputMarkupId(true);
    		add(installationSelectedButton);
    		installationSelectedButton.add(icon);
    		
    		if (!hasInstallationProducts) {
    			installationSelectedButton.setVisible(false);
    		}
        }
    }

    @Override
    protected void convertInput() {
    	setConvertedInput(new ProductCountAndInstallation(product, null, (selected ? 1 : 0), 0, hasInstallationProducts, installationSelected, installationEditable));
    }
}