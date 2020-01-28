package dk.jyskit.salescloud.application.pages.admin.productbundles;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.ProductBundleDao;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.model.BundleProductRelation;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.ProductAccessType;
import dk.jyskit.salescloud.application.model.ProductBundle;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectOptions;
import dk.jyskit.waf.wicket.crud.AbstractEditPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

public class EditProductInBundlePanel extends AbstractEditPanel<BundleProductRelation, ProductBundle> {
	private static final long serialVersionUID = 1L;

	@Inject
	private ProductDao productDao;
	
	@Inject
	private ProductBundleDao parentDao;
	
	private Product origProduct = null;
	
	public EditProductInBundlePanel(CrudContext context, IModel<BundleProductRelation> childModel, IModel<ProductBundle> parentModel) {
		super(context, childModel, parentModel);
		if (childModel != null) {
			origProduct = childModel.getObject().getProduct();
		}
	}
	
	@Override
	public IModel<BundleProductRelation> createChildModel() {
		return new Model(new BundleProductRelation());
	}
	
	public void addFormFields(Jsr303Form<BundleProductRelation> form) {
		List<Product> products = productDao.findByBusinessArea(parentModel.getObject().getCampaign().getBusinessArea());
		Collections.sort(products, new Comparator<Product>() {
			@Override
			public int compare(Product p1, Product p2) {
				return getCompositeName(p1).compareTo(getCompositeName(p2));
			}
		});
		
		form.addSelectSinglePanel("product", products, new ChoiceRenderer<Product>() {
			@Override
			public Object getDisplayValue(Product value) {
				return getCompositeName(value);
			}
		}, new BootstrapSelectOptions());
		
		form.addSelectSinglePanel("productAccessType", ProductAccessType.valuesAsList(), new ChoiceRenderer<ProductAccessType>() {
			@Override
			public Object getDisplayValue(ProductAccessType value) {
				return value.getText();
			}
		}, new BootstrapSelectOptions());
		
		form.addCheckBox("addProductPrice");
	}

	private String getCompositeName(Product product) {
		return product.getProductGroup().getFullPath() + " - " + product.getProductId() + " - " + product.getPublicName();
	}

	@Override
	protected boolean onSave(final Jsr303Form<BundleProductRelation> form, AjaxRequestTarget target) {
		if (origProduct == null) {
			parentModel.getObject().addProductRelation(childModel.getObject());
			parentDao.save(parentModel.getObject());
		} else {
			parentModel.getObject().removeProductRelation(origProduct);
			parentDao.save(parentModel.getObject());
			parentModel.getObject().addProductRelation(childModel.getObject());
			parentDao.save(parentModel.getObject());
		}
		return true;
	}
	
	@Override
	public boolean prepareSave(BundleProductRelation entity) {
		// Method is never called, since onSave has been overridden
		return true;  
	}

	@Override
	public boolean save(BundleProductRelation entity, Jsr303Form<BundleProductRelation> form) {
		// Method is never called, since onSave has been overridden
		return true;  
	}
	
	@Override
	public boolean addToParentAndSave(ProductBundle parent, BundleProductRelation child) {
		// Method is never called, since onSave has been overridden
		return true;  
	}
}
