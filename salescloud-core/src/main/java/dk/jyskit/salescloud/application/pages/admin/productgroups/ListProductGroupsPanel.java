package dk.jyskit.salescloud.application.pages.admin.productgroups;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.salescloud.application.pages.admin.products.ListProductsPanel;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.utils.filter.Equal;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.DaoTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions.EntityAction;
import dk.jyskit.waf.wicket.crud.AbstractListPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;
import dk.jyskit.waf.wicket.crud.CrudEntityAction;

@SuppressWarnings("serial")
public class ListProductGroupsPanel extends AbstractListPanel<ProductGroup,BusinessArea> {

	@Inject
	private Dao<ProductGroup> dao;

	public ListProductGroupsPanel(CrudContext context, IModel<BusinessArea> parentModel) {
		super(context, ProductGroup.class.getSimpleName(), parentModel);
	}

	@Override
	protected BootstrapTableDataProvider<ProductGroup, String> getDataProvider() {
		DaoTableDataProvider<ProductGroup, Dao<ProductGroup>> dataProvider = 
				DaoTableDataProvider.create(dao, "sortIndex", SortOrder.ASCENDING, new Equal("businessArea", getParentModel().getObject()));
		dataProvider.setFilterProps("name");
		return dataProvider;
	}

	@Override
	protected void addDataColumns(List<IColumn<ProductGroup, String>> cols) {
		cols.add(createNonSortColumn("fullPath", "productGroup"));
	}

	@SuppressWarnings("unchecked")
	protected EntityAction<ProductGroup>[] getHeaderActions() {
		// No "New" button!
		EntityAction<?>[] actions = {};
		return (EntityAction<ProductGroup>[]) actions;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected EntityAction<ProductGroup>[] getRowActions() {
		return new EntityAction[] { getEditAction(), getListProductGroupElementsAction() }; 
	}
	
	private EntityAction getListProductGroupElementsAction() {
		CrudEntityAction<ProductGroup> action = new CrudEntityAction<ProductGroup>(context, getKey("products.list.link"), getKey("products.list.tooltip"), FontAwesomeIconType.table) {
			@Override
			public Panel createPanel(CrudContext context, IModel<ProductGroup> model) {
				return new ListProductsPanel(context, model);
			}
		};
		return action;
	}

	@Override
	protected Panel createEditPanel(CrudContext context, IModel<ProductGroup> childModel) {
		return new EditProductGroupPanel(context, childModel, getParentModel());
	}

	@Override
	protected void deleteObject(ProductGroup entity) {
		dao.delete(dao.findById(entity.getId()));
	}
	
	@Override
	protected void saveEntityWithNewState(ProductGroup entity) {
		dao.save(entity);
	}
}
