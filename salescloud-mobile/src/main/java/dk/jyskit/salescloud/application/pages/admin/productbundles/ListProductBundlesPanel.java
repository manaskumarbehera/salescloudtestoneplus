package dk.jyskit.salescloud.application.pages.admin.productbundles;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import dk.jyskit.salescloud.application.model.Campaign;
import dk.jyskit.salescloud.application.model.ProductBundle;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.utils.filter.Equal;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.DaoTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions.EntityAction;
import dk.jyskit.waf.wicket.crud.AbstractListPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;
import dk.jyskit.waf.wicket.crud.CrudEntityAction;

@SuppressWarnings("serial")
public class ListProductBundlesPanel extends AbstractListPanel<ProductBundle,Campaign> {

	@Inject
	private Dao<ProductBundle> childDao;

	@Inject
	private Dao<Campaign> parentDao;

	public ListProductBundlesPanel(CrudContext context, IModel<Campaign> parentModel) {
		super(context, ProductBundle.class.getSimpleName(), parentModel);
	}

	@Override
	protected BootstrapTableDataProvider<ProductBundle, String> getDataProvider() {
		DaoTableDataProvider<ProductBundle, Dao<ProductBundle>> dataProvider = 
				DaoTableDataProvider.create(childDao, "sortIndex", SortOrder.ASCENDING, 
				new Equal("campaign", getParentModel().getObject()));
		dataProvider.setFilterProps("publicName");
		return dataProvider;
	}

	@Override
	protected void addDataColumns(List<IColumn<ProductBundle, String>> cols) {
		cols.add(createColumn("publicName"));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected EntityAction<ProductBundle>[] getRowActions() {
		return new EntityAction[] { getEditAction(), getDeleteAction(), getListProductBundleElementsAction() }; 
	}
	
	private EntityAction getListProductBundleElementsAction() {
		CrudEntityAction<ProductBundle> action = new CrudEntityAction<ProductBundle>(context, getKey("products.list.link"), getKey("products.list.tooltip"), FontAwesomeIconType.table) {
			@Override
			public Panel createPanel(CrudContext context, IModel<ProductBundle> model) {
				return new ListProductsInBundlePanel(context, model);
			}
		};
		return action;
	}

	@Override
	protected Panel createEditPanel(CrudContext context, IModel<ProductBundle> model) {
		return new EditProductBundlePanel(context, model, getParentModel());
	}

	@Override
	protected void deleteObject(ProductBundle entity) {
		getParentModel().getObject().removeProductBundle(entity);
		childDao.delete(childDao.findById(entity.getId()));
		parentDao.save(getParentModel().getObject());
	}
	
	@Override
	protected void saveEntityWithNewState(ProductBundle entity) {
		childDao.save(entity);
	}
}
