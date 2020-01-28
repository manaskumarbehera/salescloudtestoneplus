package dk.jyskit.salescloud.application.pages.partner;

import dk.jyskit.salescloud.application.model.ProductBundle;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

import dk.jyskit.salescloud.application.model.Product;

public class ProductCategoryNamePriceAndCountRowEditor extends FormComponentPanel<Object> {
    private TextField<String> categoryField;
    private TextField<String> nameField;
    private NumberTextField<Integer>countField;
    private NumberTextField<Integer>priceField;

	protected Product product;
	protected ProductBundle bundle;
	protected String category;
	protected String name;
	protected Integer count;
	protected Integer price;

    /**
     * @param id
     */
    public ProductCategoryNamePriceAndCountRowEditor(String id, final PropertyModel propertyModel) {
        super(id, propertyModel);
        
        setOutputMarkupId(true);
        
        category	= ((ProductCategoryNamePriceAndCountRow) propertyModel.getObject()).getCategory();
        name 		= ((ProductCategoryNamePriceAndCountRow) propertyModel.getObject()).getName();
        product 	= ((ProductCategoryNamePriceAndCountRow) propertyModel.getObject()).getProduct();
		bundle	 	= ((ProductCategoryNamePriceAndCountRow) propertyModel.getObject()).getBundle();
        price		= ((ProductCategoryNamePriceAndCountRow) propertyModel.getObject()).getPrice();
        count		= ((ProductCategoryNamePriceAndCountRow) propertyModel.getObject()).getCount();
		
		categoryField = new TextField<String>("category", new PropertyModel<String>(this, "category"));
		categoryField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				convertInput();
			}
			@Override
			protected void onError(AjaxRequestTarget target, RuntimeException e) {
				super.onError(target, e);
				target.add(ProductCategoryNamePriceAndCountRowEditor.this.getParent().getParent());
			}
		});
		categoryField.setOutputMarkupId(true);
		add(categoryField);
		
		nameField = new TextField<String>("name", new PropertyModel<String>(this, "name"));
		nameField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				convertInput();
			}
			@Override
			protected void onError(AjaxRequestTarget target, RuntimeException e) {
				super.onError(target, e);
				target.add(ProductCategoryNamePriceAndCountRowEditor.this.getParent().getParent());
			}
		});
		nameField.setOutputMarkupId(true);
		add(nameField);
		
		countField = new NumberTextField<Integer>("count", new PropertyModel<Integer>(this, "count"));
		countField.setMinimum(0);
		countField.setMaximum(10000);
		countField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				convertInput();
			}
			@Override
			protected void onError(AjaxRequestTarget target, RuntimeException e) {
				super.onError(target, e);
				target.add(ProductCategoryNamePriceAndCountRowEditor.this.getParent().getParent());
			}
		});
		countField.setOutputMarkupId(true);
		add(countField);
		
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
				target.add(ProductCategoryNamePriceAndCountRowEditor.this.getParent().getParent());
			}
		});
		priceField.setOutputMarkupId(true);
		add(priceField);
    }

    @Override
    protected void convertInput() {
    	if (product == null) {
			setConvertedInput(new ProductCategoryNamePriceAndCountRow(bundle, category, name, price, count));
		} else {
			setConvertedInput(new ProductCategoryNamePriceAndCountRow(product, category, name, price, count));
		}
    }
}