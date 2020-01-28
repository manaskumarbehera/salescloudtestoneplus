package dk.jyskit.salescloud.application.pages.admin.products;

import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.dao.ProductGroupDao;
import dk.jyskit.salescloud.application.extensionpoints.ObjectFactory;
import dk.jyskit.salescloud.application.model.PaymentFrequency;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.crud.AbstractEditPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

public class EditProductPanel extends AbstractEditPanel<Product, ProductGroup> {
	private static final long serialVersionUID = 1L;

	@Inject
	private ProductDao childDao;
	
	@Inject
	private ProductGroupDao parentDao;
	
	@Inject
	private ObjectFactory objectFactory;
	
	public EditProductPanel(CrudContext context, final IModel<Product> childModel, final IModel<ProductGroup> parentModel) {
		super(context, childModel, parentModel);
	}
	
	@Override
	public IModel<Product> createChildModel() {
		return EntityModel.forEntity(objectFactory.createProduct());
	}

	@Override
	public void addFormFields(Jsr303Form<Product> form) {
		form.addTextField("publicName");
		form.addTextField("internalName"); 	
		form.addTextField("price.oneTimeFee");
		form.addTextField("price.installationFee");
		form.addTextField("price.recurringFee");
		form.addCheckBox("discountEligible");
		form.addDropDownChoice("paymentFrequency", PaymentFrequency.valuesAsList());
		form.addTextField("defaultCount");
		form.addTextField("minCount");
		form.addTextField("maxCount");
	}

	@Override
	public boolean prepareSave(Product entity) {
		return true;
	}

	@Override
	public boolean save(Product entity, Jsr303Form<Product> form) {
		childDao.save(entity);
		return true;
	}
	
	@Override
	public boolean addToParentAndSave(ProductGroup parent, Product entity) {
		parent.addProduct(entity);
		parentDao.save(parent);
		return true;
	}
}
