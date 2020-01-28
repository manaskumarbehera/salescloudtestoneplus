package dk.jyskit.salescloud.application.pages.partner;

import dk.jyskit.salescloud.application.model.ProductBundle;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.model.PropertyModel;

import dk.jyskit.salescloud.application.model.Product;

public class ProductPriceAndCountRowEditor extends FormComponentPanel<Object> {
    private NumberTextField<Integer>countField;
    private NumberTextField<Integer>priceField;
	
	protected Product product;
	protected ProductBundle bundle;
	protected Integer count;
	protected Integer price;
	protected boolean showName;
	protected boolean readonlyPrice;

	public ProductPriceAndCountRowEditor(String id, final PropertyModel propertyModel) {
		this(id, propertyModel, false, false);
	}

    /**
     * @param id
     */
    public ProductPriceAndCountRowEditor(String id, final PropertyModel propertyModel, boolean showName, boolean readonlyPrice) {
        super(id, propertyModel);

		setOutputMarkupId(true);

        this.showName = showName;
		this.readonlyPrice = readonlyPrice;

        product = ((ProductPriceAndCountRow) propertyModel.getObject()).getProduct();
        bundle 	= ((ProductPriceAndCountRow) propertyModel.getObject()).getBundle();
        price	= ((ProductPriceAndCountRow) propertyModel.getObject()).getPrice();
        count	= ((ProductPriceAndCountRow) propertyModel.getObject()).getCount();

		WebMarkupContainer nameContainer = new WebMarkupContainer("nameContainer");
		add(nameContainer);
        Label nameLabel = new Label("name", product == null ? bundle.getPublicName() : product.getPublicName());
		nameContainer.add(nameLabel);
		nameContainer.setVisible(showName);
		
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
				target.add(ProductPriceAndCountRowEditor.this.getParent().getParent());
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
				target.add(ProductPriceAndCountRowEditor.this.getParent().getParent());
			}
		});
		priceField.setEnabled(!readonlyPrice);
		
		priceField.setOutputMarkupId(true);
		add(priceField);
    }

    @Override
    protected void convertInput() {
    	if (product == null) {
			setConvertedInput(new ProductPriceAndCountRow(bundle, price, count));
		} else {
			setConvertedInput(new ProductPriceAndCountRow(product, price, count));
		}
    }
}