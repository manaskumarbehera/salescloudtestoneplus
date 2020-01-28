package dk.jyskit.salescloud.application.pages.admin.organisations;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.extensionpoints.CrudListPanelFactory;
import dk.jyskit.salescloud.application.model.Organisation;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.DaoTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions.EntityAction;
import dk.jyskit.waf.wicket.crud.AbstractListPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

@SuppressWarnings("serial")
public class ListOrganisationsPanel extends AbstractListPanel<Organisation,Void> {

	@Inject
	private Dao<Organisation> dao;

	@Inject
	private CrudListPanelFactory crudListPanelFactory;

	public ListOrganisationsPanel(CrudContext context) {
		super(context, Organisation.class.getSimpleName());
	}

	@Override
	protected BootstrapTableDataProvider<Organisation, String> getDataProvider() {
		DaoTableDataProvider<Organisation, Dao<Organisation>> dataProvider = DaoTableDataProvider.create(dao, "companyName", SortOrder.ASCENDING);
		dataProvider.setFilterProps("companyName");
		return dataProvider;
	}

	@Override
	protected void addDataColumns(List<IColumn<Organisation, String>> cols) {
		cols.add(createColumn("companyName"));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected EntityAction<Organisation>[] getRowActions() {
		return new EntityAction[] { 
				getEditAction()
			}; 
	}
	
//	@SuppressWarnings("unchecked")
//	protected EntityAction<Organisation>[] getHeaderActions() {
//		EntityAction<?>[] actions = {};
//		return (EntityAction<Organisation>[]) actions;
//	}
	
	@Override
	protected Panel createEditPanel(CrudContext context, IModel<Organisation> model) {
		return new EditOrganisationPanel(context, model);
	}

	@Override
	protected void deleteObject(Organisation entity) {
		dao.delete(dao.findById(entity.getId()));
	}
	
	@Override
	protected void saveEntityWithNewState(Organisation entity) {
		dao.save(entity);
	}
}
