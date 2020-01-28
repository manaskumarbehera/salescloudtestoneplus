package dk.jyskit.salescloud.application.pages.admin.productrelations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.collections.MiniMap;

import com.google.inject.Inject;
import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;

import dk.jyskit.salescloud.application.dao.BusinessAreaDao;
import dk.jyskit.salescloud.application.dao.ProductDao;
import dk.jyskit.salescloud.application.dao.ProductRelationDao;
import dk.jyskit.salescloud.application.extensionpoints.ProductRelationTypeProvider;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.ProductRelation;
import dk.jyskit.salescloud.application.wafextension.forms.IdPropChoiceRenderer;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.MapLabelStrategy;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectOptions;
import dk.jyskit.waf.wicket.components.jquery.bootstrapselect.BootstrapSelectSingle;
import dk.jyskit.waf.wicket.crud.AbstractEditPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

public class EditProductRelationPanel extends AbstractEditPanel<ProductRelation, BusinessArea> {
	private static final long serialVersionUID = 1L;

	@Inject
	private ProductRelationDao dao;
	
	@Inject
	private ProductDao productDao;
	
	@Inject
	private BusinessAreaDao businessAreaDao;
	
	@Inject
	private ProductRelationTypeProvider productRelationTypeProvider;
	
	public EditProductRelationPanel(CrudContext context, final IModel<ProductRelation> childModel, final IModel<BusinessArea> parentModel) {
		super(context, childModel == null ? null : new Model(new ExtendedProductRelation(childModel.getObject())), parentModel);
	}
	
	@Override
	public IModel<ProductRelation> createChildModel() {
		return new EntityModel<ProductRelation>(new ExtendedProductRelation());
	}
	
//	@Override
//	public ProductRelation initEntity(Long entityId) {
//		// We are going to wrap the ProductEntity in an ExtendedProductRelation, because of a RelationType, it only
//		// has a RelationTypeId. This is a problem for the BootstrapSelectSingle component.
//		return (entityId == null ? new ExtendedProductRelation() : new ExtendedProductRelation(dao.findById(entityId)));
//	}

	@Override
	public void addFormFields(Jsr303Form<ProductRelation> form) {
		MiniMap<String, String> map = new MiniMap<>(3);
		map.put("relationType", "Relationstype");
		map.put("products", "Produkter (det første produkt kan have speciel betydning, afhængigt af relationstypen)");
		map.put("displayName", "Tekst til brugergrænseflade (anv. kun ved alternative produkter)");
		
		form.setLabelStrategy(new MapLabelStrategy(map, form.getLabelStrategy()));
		
		BootstrapSelectSingle relationTypeSelector = form.addSelectSinglePanel("relationType", productRelationTypeProvider.getProductRelationTypes(), 
				new IdPropChoiceRenderer("displayText"), new BootstrapSelectOptions());
		relationTypeSelector.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
		});
		
		form.addSelect2MultiChoice("products", new ProductsProvider(productDao.findByField("businessArea", parentModel.getObject())));
		form.addTextField("displayName");
	}

	@Override
	public boolean prepareSave(ProductRelation entity) {
		return true;
	}
	
	@Override
	public boolean save(ProductRelation entity, Jsr303Form<ProductRelation> form) {
		// Saving entity which already existed
		ProductRelation original = ((ExtendedProductRelation) entity).getUpdatedOriginal();
		dao.save(original);
		return true;
	}

	@Override
	public boolean addToParentAndSave(BusinessArea parent, ProductRelation entity) {
		// Saving new entity 
		ProductRelation original = ((ExtendedProductRelation) entity).getUpdatedOriginal();
		parent.addProductRelation(original);
		businessAreaDao.save(parent);
		return true;
	}
	
	public class ProductsProvider extends TextChoiceProvider<Product> {
		private List<Product> products;

		public ProductsProvider(List<Product> products) {
			this.products = products;
			Collections.sort(this.products, new Comparator<Product>() {
				@Override
				public int compare(Product o1, Product o2) {
					return ProductsProvider.this.toString(o1).compareTo(ProductsProvider.this.toString(o2));
				}
			});
		}

		@Override
		protected String getDisplayText(Product choice) {
			return toString(choice);
		}

		@Override
		protected Object getId(Product choice) {
			return choice.getId();
		}

		@Override
		public void query(String term, int page, Response<Product> response) {
			int pageSize = 10;
			List<Product> pageOfProducts = new ArrayList<>();
			boolean hasMore = false;
			if (!StringUtils.isEmpty(term)) {
				term = term.toUpperCase();
				int counter = 0;
				for (Product product : products) {
					if (product.getPublicName().toUpperCase().contains(term)) {
						counter++;
						if (counter > page * pageSize) {
							if (pageOfProducts.size() == pageSize) {
								hasMore = true;
								break;
							}
							pageOfProducts.add(product);
						}
					}
				}
			}
			response.addAll(pageOfProducts);
			response.setHasMore(hasMore);
		}

		@Override
		public Collection<Product> toChoices(Collection<String> ids) {
			ArrayList<Product> result = new ArrayList<Product>();
			for (String id : ids) {
				result.add(productDao.findById(Long.valueOf(id)));
			}
			return result;
		}
		
		private String toString(Product product) {
			return product.getPublicName() + " (" + product.getProductId() + ")";
		}
	}
}
