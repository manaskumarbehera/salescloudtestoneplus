package dk.jyskit.salescloud.application.pages.admin.contractcategories;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.model.ContractCategory;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.utils.filter.Equal;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.DaoTableDataProvider;
import dk.jyskit.waf.wicket.crud.AbstractListPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

@SuppressWarnings("serial")
public class ListContractCategoryPanel extends AbstractListPanel<ContractCategory, SalespersonRole> {

	@Inject
	private Dao<ContractCategory> dao;

	public ListContractCategoryPanel(CrudContext context, IModel<SalespersonRole> parentModel) {
		super(context, ContractCategory.class.getSimpleName(), parentModel);
	}
	
	@Override
	protected BootstrapTableDataProvider<ContractCategory, String> getDataProvider() {
		DaoTableDataProvider<ContractCategory, Dao<ContractCategory>> dataProvider = DaoTableDataProvider.create(dao, "name", SortOrder.ASCENDING, 
				new Equal("salesperson", CoreSession.get().getSalespersonRole()));
		dataProvider.setFilterProps("name");
		return dataProvider;
	}

	@Override
	protected void addDataColumns(List<IColumn<ContractCategory, String>> cols) {
		cols.add(createColumn("name"));
	}

	@Override
	protected Panel createEditPanel(CrudContext context, IModel<ContractCategory> model) {
		return new EditContractCategoryPanel(context, model, getParentModel());
	}

	@Override
	protected void deleteObject(ContractCategory entity) {
		dao.delete(dao.findById(entity.getId()));
	}
	
	@Override
	protected void saveEntityWithNewState(ContractCategory entity) {
		dao.save(entity);
	}
}
