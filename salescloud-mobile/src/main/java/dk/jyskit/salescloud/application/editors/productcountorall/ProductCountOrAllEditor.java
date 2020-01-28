package dk.jyskit.salescloud.application.editors.productcountorall;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.model.PropertyModel;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.model.Product;

public class ProductCountOrAllEditor extends FormComponentPanel<Object> {
    private NumberTextField<Integer>countField;
	
	protected Product product;
	protected Integer countExisting;
	protected Integer countNew;
	protected boolean allNew;

    /**
     * @param id
     */
    public ProductCountOrAllEditor(String id, final PropertyModel propertyModel) {
        super(id, propertyModel);
        
        setOutputMarkupId(true);
        
        product = ((ProductCountOrAll) propertyModel.getObject()).getProduct();
        allNew	 	= ((ProductCountOrAll) propertyModel.getObject()).isAll();
        if (allNew) {
            countExisting	= MobileSession.get().getContract().getCountExistingSubscriptions();
            countNew		= MobileSession.get().getContract().getCountNewSubscriptions();
        } else {
            countExisting	= ((ProductCountOrAll) propertyModel.getObject()).getCountExisting();
            countNew		= ((ProductCountOrAll) propertyModel.getObject()).getCountNew();
        }
		
		countField = new NumberTextField<Integer>("countNew", new PropertyModel<Integer>(this, "countNew")) {
			@Override
			public void onConfigure() {
				super.onConfigure();
				setEnabled(!allNew);
			}
		};
		countField.setMinimum(0);
		countField.setMaximum(MobileSession.get().getContract().getSubscriptions().size());
		countField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				convertInput();
			}
			@Override
			protected void onError(AjaxRequestTarget target, RuntimeException e) {
				super.onError(target, e);
				target.add(ProductCountOrAllEditor.this.getParent().getParent());
			}
		});
		
		countField.setOutputMarkupId(true);
		add(countField);
		
		final WebMarkupContainer icon = new WebMarkupContainer("icon");
		icon.setOutputMarkupId(true);

		AjaxLink button = new AjaxLink("allButton") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				allNew = !allNew;
				if (allNew) {
					countNew = MobileSession.get().getContract().getSubscriptions().size();
					countField.clearInput();
				}
				convertInput();
				icon.add(AttributeModifier.replace("class", allNew ? "fa fa-check-square-o" : "fa fa-square-o"));
				
				add(AttributeModifier.replace("class", allNew ? "btn btn-primary" : "btn btn-default"));
				
//				target.add(ProductRowEditor.this);
				target.add(ProductCountOrAllEditor.this.getParent().getParent());
			}
		};
		
		icon.add(AttributeModifier.replace("class", allNew ? "fa fa-check-square-o" : "fa fa-square-o"));
		button.add(AttributeModifier.replace("class", allNew ? "btn btn-primary" : "btn btn-default"));
		button.setOutputMarkupId(true);
		add(button);
		
		icon.setOutputMarkupId(true);
		button.add(icon);

    }

    @Override
    protected void convertInput() {
    	setConvertedInput(new ProductCountOrAll(product, countExisting, (allNew ? null : countNew)));
    }
}