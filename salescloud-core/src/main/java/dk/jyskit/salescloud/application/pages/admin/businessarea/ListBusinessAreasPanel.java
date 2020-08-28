package dk.jyskit.salescloud.application.pages.admin.businessarea;

import java.util.List;

import dk.jyskit.waf.utils.filter.And;
import dk.jyskit.waf.utils.filter.Not;
import dk.jyskit.waf.wicket.security.UserSession;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import dk.jyskit.salescloud.application.extensionpoints.CrudListPanelFactory;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.pages.admin.pageinfo.ListPageInfosPanel;
import dk.jyskit.salescloud.application.pages.admin.productrelations.ListProductRelationsPanel;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.application.model.EntityState;
import dk.jyskit.waf.utils.filter.Equal;
import dk.jyskit.waf.utils.filter.Filter;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.DaoTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions.EntityAction;
import dk.jyskit.waf.wicket.crud.AbstractListPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;
import dk.jyskit.waf.wicket.crud.CrudEntityAction;

@SuppressWarnings("serial")
public class ListBusinessAreasPanel extends AbstractListPanel<BusinessArea,Void> {

	@Inject
	private Dao<BusinessArea> dao;

	@Inject
	private CrudListPanelFactory crudListPanelFactory;

	public ListBusinessAreasPanel(CrudContext context) {
		super(context, BusinessArea.class.getSimpleName());
	}

	@Override
	protected BootstrapTableDataProvider<BusinessArea, String> getDataProvider() {
		if (UserSession.get().getUser().getEmail() != null) {
			if ((!"jan@escapetech.dk".equalsIgnoreCase(UserSession.get().getUser().getEmail())
					&& !"thber@tdc.dk".equalsIgnoreCase(UserSession.get().getUser().getEmail())
					&& !"whe@tdc.dk".equalsIgnoreCase(UserSession.get().getUser().getEmail()))) {
				DaoTableDataProvider<BusinessArea, Dao<BusinessArea>> dataProvider =
						DaoTableDataProvider.create(dao, "name", SortOrder.ASCENDING,
								new And(
										new Equal("entityState", EntityState.ACTIVE),
										new Not(new Equal("name", "Test"))));
				dataProvider.setFilterProps("name");
				return dataProvider;
			}
		}
		DaoTableDataProvider<BusinessArea, Dao<BusinessArea>> dataProvider =
				DaoTableDataProvider.create(dao, "name", SortOrder.ASCENDING, new Equal("entityState", EntityState.ACTIVE));
		dataProvider.setFilterProps("name");
		return dataProvider;
	}

	@Override
	protected void addDataColumns(List<IColumn<BusinessArea, String>> cols) {
		cols.add(createColumn("name"));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected EntityAction<BusinessArea>[] getRowActions() {
		return new EntityAction[] { 
				getEditAction(), 
				// getListReportsAction(), 
				getListProductGroupsAction(), getListProductRelationsAction(), getListCampaignsAction(), getListPageInfosAction(), 
				getSortProductGroupsAction(), getSortProductsAction() }; 
	}

// Temporarily(?) removed.	
//	protected CrudEntityAction<BusinessArea> getListReportsAction() {
//		CrudEntityAction<BusinessArea> action = new CrudEntityAction<BusinessArea>(context, getKey("reports.list.link"), getKey("reports.list.tooltip"), FontAwesomeIconType.table) {
//			@Override
//			public Panel createPanel(CrudContext context, IModel<BusinessArea> model) {
//				return crudListPanelFactory.getReportsListPanel(context, model);
//			}
//		};
//		return action;
//	}
	
	protected CrudEntityAction<BusinessArea> getListProductGroupsAction() {
		CrudEntityAction<BusinessArea> action = new CrudEntityAction<BusinessArea>(context, getKey("productgroups.list.link"), getKey("productgroups.list.tooltip"), FontAwesomeIconType.table) {
			@Override
			public Panel createPanel(CrudContext context, IModel<BusinessArea> model) {
				return crudListPanelFactory.getProductGroupsListPanel(context, model);
			}
		};
		return action;
	}
	
	protected CrudEntityAction<BusinessArea> getListProductRelationsAction() {
		CrudEntityAction<BusinessArea> action = new CrudEntityAction<BusinessArea>(context, getKey("productrelations.list.link"), getKey("productrelations.list.tooltip"), FontAwesomeIconType.table) {
			@Override
			public Panel createPanel(CrudContext context, IModel<BusinessArea> model) {
				return new ListProductRelationsPanel(context, model);
			}
		};
		return action;
	}
	
	protected CrudEntityAction<BusinessArea> getListCampaignsAction() {
		CrudEntityAction<BusinessArea> action = new CrudEntityAction<BusinessArea>(context, getKey("campaigns.list.link"), getKey("campaigns.list.tooltip"), FontAwesomeIconType.signal) {
			@Override
			public Panel createPanel(CrudContext context, IModel<BusinessArea> model) {
				return crudListPanelFactory.getCampaignsListPanel(context, model);
			}
		};
		return action;
	}
	
	protected CrudEntityAction<BusinessArea> getListPageInfosAction() {
		CrudEntityAction<BusinessArea> action = new CrudEntityAction<BusinessArea>(context, getKey("pages.list.link"), getKey("pages.list.tooltip"), FontAwesomeIconType.laptop) {
			@Override
			public Panel createPanel(CrudContext context, IModel<BusinessArea> model) {
				return new ListPageInfosPanel(context, model);
			}
		};
		return action;
	}
	
	private CrudEntityAction getSortProductGroupsAction() {
		CrudEntityAction<BusinessArea> action = new CrudEntityAction<BusinessArea>(context, getKey("sortgroups.list.link"), getKey("sortgroups.list.tooltip"), FontAwesomeIconType.sort) {
			@Override
			public Panel createPanel(CrudContext context, IModel<BusinessArea> model) {
				return crudListPanelFactory.getSortProductGroupsPanel(context, model);
			}
		};
		return action;
	}
	
	private CrudEntityAction getSortProductsAction() {
		CrudEntityAction<BusinessArea> action = new CrudEntityAction<BusinessArea>(context, getKey("sortproducts.list.link"), getKey("sortproducts.list.tooltip"), FontAwesomeIconType.sort) {
			@Override
			public Panel createPanel(CrudContext context, IModel<BusinessArea> model) {
				return crudListPanelFactory.getSortProductsPanel(context, model);
			}
		};
		return action;
	}
	
	@SuppressWarnings("unchecked")
	protected EntityAction<BusinessArea>[] getHeaderActions() {
		EntityAction<?>[] actions = {};
		return (EntityAction<BusinessArea>[]) actions;
	}
	
	@Override
	protected Panel createEditPanel(CrudContext context, IModel<BusinessArea> model) {
		return new EditBusinessAreaPanel(context, model);
	}

	@Override
	protected void deleteObject(BusinessArea entity) {
		dao.delete(dao.findById(entity.getId()));
	}
	
	@Override
	protected void saveEntityWithNewState(BusinessArea entity) {
		dao.save(entity);
	}
}
