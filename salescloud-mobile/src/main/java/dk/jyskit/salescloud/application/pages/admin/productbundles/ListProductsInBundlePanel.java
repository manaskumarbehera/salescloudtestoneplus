package dk.jyskit.salescloud.application.pages.admin.productbundles;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.ProductBundleDao;
import dk.jyskit.salescloud.application.model.BundleProductRelation;
import dk.jyskit.salescloud.application.model.ProductBundle;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapListDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableDataProvider;
import dk.jyskit.waf.wicket.crud.AbstractListPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

@SuppressWarnings("serial")
public class ListProductsInBundlePanel extends AbstractListPanel<BundleProductRelation,ProductBundle> {

	@Inject
	private ProductBundleDao parentDao;
	
	public ListProductsInBundlePanel(CrudContext context, IModel<ProductBundle> parentModel) {
		super(context, BundleProductRelation.class.getSimpleName(), parentModel);
	}

	@Override
	protected BootstrapTableDataProvider<BundleProductRelation, String> getDataProvider() {
		IModel<List<BundleProductRelation>> model = new AbstractReadOnlyModel<List<BundleProductRelation>>() {
			@Override
			public List<BundleProductRelation> getObject() {
				return getParentModel().getObject().getProducts();
			}
		};
		return new BootstrapListDataProvider<BundleProductRelation>(model, "product.publicName", "product.productId");
	}

	@Override
	protected void addDataColumns(List<IColumn<BundleProductRelation, String>> cols) {
		cols.add(createColumn("productAccessType"));
		cols.add(createColumn("product.productId"));
		cols.add(createColumn("product.publicName"));
	}
	
	@Override
	protected Panel createEditPanel(CrudContext context, IModel<BundleProductRelation> childModel) {
		return new EditProductInBundlePanel(context, childModel, getParentModel());
	}

	@Override
	protected void deleteObject(BundleProductRelation entity) {
		getParentModel().getObject().removeProductRelation(entity.getProduct());
		parentDao.save(getParentModel().getObject());
	}
	
	@Override
	protected void saveEntityWithNewState(BundleProductRelation entity) {
		// never called
	}
}
