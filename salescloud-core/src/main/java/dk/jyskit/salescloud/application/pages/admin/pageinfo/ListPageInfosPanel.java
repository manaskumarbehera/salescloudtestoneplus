package dk.jyskit.salescloud.application.pages.admin.pageinfo;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.PageInfo;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.utils.filter.Equal;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.DaoTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions.EntityAction;
import dk.jyskit.waf.wicket.crud.AbstractListPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

@SuppressWarnings("serial")
public class ListPageInfosPanel extends AbstractListPanel<PageInfo,BusinessArea> {

	@Inject
	private Dao<PageInfo> dao;

	public ListPageInfosPanel(CrudContext context, IModel<BusinessArea> parentModel) {
		super(context, PageInfo.class.getSimpleName(), parentModel);
	}

	@Override
	protected BootstrapTableDataProvider<PageInfo, String> getDataProvider() {
		DaoTableDataProvider<PageInfo, Dao<PageInfo>> dataProvider = 
				DaoTableDataProvider.create(dao, "title", SortOrder.ASCENDING, new Equal("businessArea", getParentModel().getObject()));
		dataProvider.setFilterProps("title");
		return dataProvider;
	}

	@Override
	protected void addDataColumns(List<IColumn<PageInfo, String>> cols) {
		cols.add(createColumn("title"));
		cols.add(createColumn("subTitle"));
	}
	
	@SuppressWarnings("unchecked")
	protected EntityAction<PageInfo>[] getHeaderActions() {
		// No "New" button!
		EntityAction<?>[] actions = {};
		return (EntityAction<PageInfo>[]) actions;
	}
	
	@SuppressWarnings("unchecked")
	protected EntityAction<PageInfo>[] getRowActions() {
		// No "Delete" button!
		EntityAction<?>[] actions = {getEditAction()};
		return (EntityAction<PageInfo>[]) actions;
	}
	
	@Override
	protected Panel createEditPanel(CrudContext context, IModel<PageInfo> model) {
		return new EditPageInfoPanel(context, model, getParentModel());
	}

	@Override
	protected void deleteObject(PageInfo entity) {
		dao.delete(dao.findById(entity.getId()));
	}
	
	@Override
	protected void saveEntityWithNewState(PageInfo entity) {
		dao.save(entity);
	}
}
