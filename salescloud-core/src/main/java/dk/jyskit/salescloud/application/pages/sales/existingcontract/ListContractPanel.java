package dk.jyskit.salescloud.application.pages.sales.existingcontract;

import java.util.List;

import dk.jyskit.salescloud.application.dao.ContractDao;
import dk.jyskit.waf.utils.guice.Lookup;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.inject.Inject;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.extensionpoints.PageNavigator;
import dk.jyskit.salescloud.application.model.Contract;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.pages.sales.editcontract.EditContractPanel;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.utils.filter.Filter;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.EntityLabelStrategy;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.ILabelStrategy;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.DaoTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.ColumnFactory;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.ICellCreator;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions.EntityAction;
import dk.jyskit.waf.wicket.crud.AbstractListPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

public abstract class ListContractPanel extends AbstractListPanel<Contract, SalespersonRole> {
	@Inject
	private Dao<Contract> dao;
	@Inject
	private Dao<SalespersonRole> parentDao;
	@Inject
	private PageNavigator pageNavigator;

	public ListContractPanel(CrudContext context) {
		super(context, new EntityLabelStrategy("Contract"), "Contract", new EntityModel<SalespersonRole>(CoreSession.get().getSalespersonRole()));
	}
	
	protected abstract Filter getInitialFilter();
	
	@Override
	protected BootstrapTableDataProvider<Contract, String> getDataProvider() {
		DaoTableDataProvider<Contract, Dao<Contract>> dataProvider = DaoTableDataProvider.create(dao, "lastModificationDate", SortOrder.DESCENDING, getInitialFilter());
		dataProvider.setFilterProps("title");
		return dataProvider;
	}

	@Override
	protected void addDataColumns(List<IColumn<Contract, String>> cols) {
//		cols.add(createColumn("title"));
//		cols.add(createColumn("lastModificationDate"));
//		cols.add(createColumn("category"));
		
		ILabelStrategy labelStrategy = new EntityLabelStrategy("Contract");
		
		ColumnFactory<Contract> cf = ColumnFactory.create(labelStrategy);
		
		cols.add(cf.column("title").build());
		cols.add(cf.column("lastModificationDate").build());
		cols.add(cf.column("category").build());
		
		if (CoreSession.get().getBusinessArea().getBusinessAreaId() == 7) {  // TDC Office !!!!
			ICellCreator<Contract> statusCreator = new ICellCreator<Contract>() {
				@Override
				public Component newCell(String componentId, IModel<Contract> rowModel) {
					return rowModel.getObject().getStatusPanel(componentId);
				}
			};
			cols.add(cf.column(statusCreator).withHeader(Model.of("Status")).build());
		}
	}

	protected EntityAction<Contract>[] getRowActions() {
		EntityAction<?>[] actions = {
			getSelectAction(), 
			getEditAction(), 
			getDeleteAction()
		};
		return (EntityAction<Contract>[]) actions;
	}
	
	protected EntityAction<Contract> getSelectAction() {
		EntityAction<Contract> action = new EntityAction<Contract>(getKey("select.link"), getKey("select.tooltip"), FontAwesomeIconType.arrow_circle_o_up) {
			@Override
			public void onClick(IModel<Contract> model, AjaxRequestTarget target) {
				Contract contract = model.getObject();
				CoreSession.get().setContract(contract);
				contract.onOpen();
				ContractDao contractDao = Lookup.lookup(ContractDao.class);
				contractDao.save(contract);
				setResponsePage(pageNavigator.first());
			}
		};
		return action;
	}
	
	@Override
	protected Panel createEditPanel(CrudContext context, IModel<Contract> childModel) {
		return new EditContractPanel(context, childModel, getParentModel());
	}
	
	@Override
	/**
	 * Soft-delete contract
	 */
	protected void deleteObject(Contract entity) {
		if (ObjectUtils.equals(CoreSession.get().getContractId(), entity.getId())) {
			CoreSession.get().setContract(null);
		}
		entity.setDeleted(true);
		dao.save(entity);
		// Removed hard-delete
		// dao.delete(dao.findById(entity.getId()));
	}

	/* 
	 * !!!!!!!!
	 * We may ALWAYS want to do this in AbstractListPanels. Without it, only one modification of parent is accepted!!
	 * If you experience OptimisticLockException, this may be the way to fix it.
	 * !!!!!!!!
	 */
	@Override
	protected void onConfigure() {
		super.onConfigure();
		getParentModel().detach();
	}
	
	@Override
	protected void saveEntityWithNewState(Contract entity) {
		dao.save(entity);
	}
}
