package dk.jyskit.salescloud.application.pages.admin.products;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.OrderLineDao;
import dk.jyskit.salescloud.application.extensionpoints.ObjectFactory;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.utils.filter.Equal;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.EntityLabelStrategy;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.DaoTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions.EntityAction;
import dk.jyskit.waf.wicket.crud.AbstractListPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

@SuppressWarnings("serial")
public class ListProductsPanel extends AbstractListPanel<Product,ProductGroup> {

	@Inject
	private Dao<Product> dao;

	@Inject
	private OrderLineDao orderLinedao;

	@Inject
	private ObjectFactory objectFactory;
	
	public ListProductsPanel(CrudContext context, IModel<ProductGroup> parentModel) {
		super(context, new EntityLabelStrategy("Product"), "Product", parentModel);
	}

	@Override
	protected BootstrapTableDataProvider<Product, String> getDataProvider() {
		DaoTableDataProvider<Product, Dao<Product>> dataProvider = 
				DaoTableDataProvider.create(dao, "sortIndex", SortOrder.ASCENDING, new Equal("productGroup", getParentModel().getObject()));
		dataProvider.setFilterProps("publicName");
		return dataProvider;
	}

//	@SuppressWarnings("unchecked")
//	protected EntityAction<Product>[] getRowActions() {
//		EntityAction<?>[] actions = {getEditAction()                                                                                                                 };
//		return (EntityAction<Product>[]) actions;
//	}
	
	@Override
	protected void addDataColumns(List<IColumn<Product, String>> cols) {
		cols.add(createColumn("productId"));
		cols.add(createColumn("publicName"));
		cols.add(createColumn("entityState"));
	}

	@Override
	protected Panel createEditPanel(CrudContext context, IModel<Product> childModel) {
		return objectFactory.createEditProductPanel(context, childModel, getParentModel());
	}

	@Override
	protected void deleteObject(Product product) {
		orderLinedao.deleteByProductId(product.getId());
		
		Product reloadedProduct = dao.findById(product.getId());
		reloadedProduct.getProductGroup().removeProduct(reloadedProduct);
		dao.delete(reloadedProduct);
	}
	
	@Override
	protected void saveEntityWithNewState(Product entity) {
		dao.save(entity);
	}
}
