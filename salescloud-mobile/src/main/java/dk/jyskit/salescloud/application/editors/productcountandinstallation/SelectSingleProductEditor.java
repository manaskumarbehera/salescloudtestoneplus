package dk.jyskit.salescloud.application.editors.productcountandinstallation;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.PropertyModel;

import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.wafextension.forms.IdPropChoiceRenderer;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectSingle;

public class SelectSingleProductEditor extends FormComponentPanel<Object> {
	protected Product selectedProduct;
	protected List<Product> alternatives;
	protected boolean selected;
	protected boolean hasInstallationProducts;
	protected boolean installationSelected;
	protected boolean installationEditable;

    /**
     * @param id
     */
    public SelectSingleProductEditor(String id, final PropertyModel propertyModel) {
        super(id, propertyModel);
        
        setOutputMarkupId(true);
        
        selectedProduct			= ((ProductCountAndInstallation) propertyModel.getObject()).getProduct();
        alternatives			= ((ProductCountAndInstallation) propertyModel.getObject()).getAlternatives();
        hasInstallationProducts	= ((ProductCountAndInstallation) propertyModel.getObject()).isHasInstallationProducts();
        installationSelected	= ((ProductCountAndInstallation) propertyModel.getObject()).isInstallationSelected();
        installationEditable	= ((ProductCountAndInstallation) propertyModel.getObject()).isInstallationEditable();
        selected				= (((ProductCountAndInstallation) propertyModel.getObject()).getCountNew() > 0);
		
        {
    		BootstrapSelectSingle<Product> dropdown = new BootstrapSelectSingle("product", new PropertyModel<Product>(this, "selectedProduct"), alternatives, new IdPropChoiceRenderer("publicName"));
    		dropdown.add(new OnChangeAjaxBehavior() {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					// Just need the event to update the model
				}
			});
    		add(dropdown);
        }
    }

    @Override
    protected void convertInput() {
    	setConvertedInput(new ProductCountAndInstallation(selectedProduct, null, (selected ? 1 : 0), 0, hasInstallationProducts, installationSelected, installationEditable));
    }
}
