package dk.jyskit.salescloud.application.pages.partner;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

import dk.jyskit.salescloud.application.model.Product;

public class ProductNameAndPriceRowEditor extends FormComponentPanel<Object> {
    private TextField<String> nameField;
    private NumberTextField<Integer>priceField;
	
	protected Product product;
	protected String name;
	protected Integer price;

    /**
     * @param id
     */
    public ProductNameAndPriceRowEditor(String id, final PropertyModel propertyModel) {
        super(id, propertyModel);
        
        setOutputMarkupId(true);
        
        name 		= ((ProductNameAndPriceRow) propertyModel.getObject()).getName();
        product 	= ((ProductNameAndPriceRow) propertyModel.getObject()).getProduct();
        price		= ((ProductNameAndPriceRow) propertyModel.getObject()).getPrice();
		
		nameField = new TextField<String>("name", new PropertyModel<String>(this, "name"));
		nameField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				convertInput();
			}
			@Override
			protected void onError(AjaxRequestTarget target, RuntimeException e) {
				super.onError(target, e);
				target.add(ProductNameAndPriceRowEditor.this.getParent().getParent());
			}
		});
		nameField.setOutputMarkupId(true);
		add(nameField);
		
		priceField = new NumberTextField<Integer>("price", new PropertyModel<Integer>(this, "price"));
		priceField.setMinimum(0);
		priceField.setMaximum(100000);
		priceField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				convertInput();
			}
			@Override
			protected void onError(AjaxRequestTarget target, RuntimeException e) {
				super.onError(target, e);
				target.add(ProductNameAndPriceRowEditor.this.getParent().getParent());
			}
		});
		priceField.setOutputMarkupId(true);
		add(priceField);
    }

    @Override
    protected void convertInput() {
    	setConvertedInput(new ProductNameAndPriceRow(product, name, price));
    }
}