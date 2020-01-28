package dk.jyskit.salescloud.application.pages.admin.useradmin.roles;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import com.google.inject.Inject;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.extensionpoints.RoleEditorFactory;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.SalesmanagerRole;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.model.UserManagerRole;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.application.dao.DaoHelper;
import dk.jyskit.waf.application.model.BaseRole;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.utils.filter.Equal;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.DaoTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions.EntityAction;
import dk.jyskit.waf.wicket.crud.AbstractListPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;
import dk.jyskit.waf.wicket.crud.CrudEntityAction;

@Slf4j
public class ListRolePanel extends AbstractListPanel<BaseRole, BaseUser> {
	private static final long serialVersionUID = 1L;

	@Inject
	private Dao<BaseRole> dao;
	
	@Inject
	private Dao<BaseUser> userDao;
	
	@Inject
	private RoleEditorFactory roleEditorFactory;

	public ListRolePanel(CrudContext context, IModel<BaseUser> parentModel) {
		super(context, BaseRole.class.getSimpleName(), parentModel);
	}
	
	@Override
	protected BootstrapTableDataProvider<BaseRole, String> getDataProvider() {
		DaoTableDataProvider<BaseRole, Dao<BaseRole>> dataProvider = 
				DaoTableDataProvider.create(dao, "roleName", SortOrder.ASCENDING, new Equal("user", getParentModel().getObject()));
		dataProvider.setFilterProps("roleName");
		return dataProvider;
	}
	
	@Override
	protected void addDataColumns(List<IColumn<BaseRole, String>> cols) {
		cols.add(new AbstractColumn<BaseRole, String>(new ResourceModel("BaseRole.roleName"), "roleName") {
			@Override
			public void populateItem(Item<ICellPopulator<BaseRole>> cellItem, String componentId, IModel<BaseRole> rowModel) {
				cellItem.add(new Label(componentId, rowModel.getObject().toString()));
			}
		});
	}

	/* 
	 * !!!!!!!!
	 * We may ALWAYS want to do this in AbstractListPanels. Without it, only one modification of parent is accepted!!
	 * !!!!!!!!
	 */
	@Override
	protected void onConfigure() {
		super.onConfigure();
		getParentModel().detach();
	}
	
	@SuppressWarnings("unchecked")
	protected EntityAction<BaseRole>[] getHeaderActions() {
		EntityAction<?>[] actions = new EntityAction<?>[] { };
		
		if (!getParentModel().getObject().hasRole(SalespersonRole.class)) {
			actions = addEntityAction(actions, getNewSalespersonRoleAction());
		}
		
        if (CoreSession.get().getActiveRoleClass().equals(AdminRole.class)) {
    		if (!getParentModel().getObject().hasRole(SalesmanagerRole.class)) {
    			actions = addEntityAction(actions, getNewSalesmanagerRoleAction());
    		}
    		if (!getParentModel().getObject().hasRole(UserManagerRole.class)) {
    			actions = addEntityAction(actions, getNewUserManagerRoleAction());
    		}
    		if (!getParentModel().getObject().hasRole(AdminRole.class)) {
    			actions = addEntityAction(actions, getNewAdminRoleAction());
    		}
        }
		
		return (EntityAction<BaseRole>[]) actions;
	}

	public EntityAction<?>[] addEntityAction(EntityAction<?>[] actions, EntityAction<?> action) {
		EntityAction<?>[] newActions = new EntityAction<?>[actions.length + 1];
	    System.arraycopy(actions, 0, newActions, 0, actions.length);
	    newActions[actions.length] = action;
	    return newActions;
	}	
	
	protected CrudEntityAction<BaseRole> getNewSalespersonRoleAction() {
		CrudEntityAction<BaseRole> action = new CrudEntityAction<BaseRole>(context, 
				getKey(getNamespace() + ".new.salesperson.link"), 
				getKey(getNamespace() + ".new.salesperson.tooltip"), FontAwesomeIconType.plus) {
			@Override
			public Panel createPanel(CrudContext context, IModel<BaseRole> model) {
				return createNewSalespersonPanel(context, null);
			}
		};
		return action;
	}
	
	protected CrudEntityAction<BaseRole> getNewSalesmanagerRoleAction() {
		CrudEntityAction<BaseRole> action = new CrudEntityAction<BaseRole>(context, 
				getKey(getNamespace() + ".new.salesmanager.link"), 
				getKey(getNamespace() + ".new.salesmanager.tooltip"), FontAwesomeIconType.plus) {
			@Override
			public Panel createPanel(CrudContext context, IModel<BaseRole> model) {
				return createNewSalesmanagerPanel(context, null);
			}
		};
		return action;
	}
	
	protected CrudEntityAction<BaseRole> getNewUserManagerRoleAction() {
		CrudEntityAction<BaseRole> action = new CrudEntityAction<BaseRole>(context, 
				getKey(getNamespace() + ".new.usermanager.link"), 
				getKey(getNamespace() + ".new.usermanager.tooltip"), FontAwesomeIconType.plus) {
			@Override
			public Panel createPanel(CrudContext context, IModel<BaseRole> model) {
				return createNewUserManagerPanel(context, null);
			}
		};
		return action;
	}
	
	protected CrudEntityAction<BaseRole> getNewAdminRoleAction() {
		CrudEntityAction<BaseRole> action = new CrudEntityAction<BaseRole>(context, 
				getKey(getNamespace() + ".new.admin.link"), 
				getKey(getNamespace() + ".new.admin.tooltip"), FontAwesomeIconType.plus) {
			@Override
			public Panel createPanel(CrudContext context, IModel<BaseRole> model) {
//				return createEditPanel(context, null);
				return createNewAdminPanel(context, null);
			}
		};
		return action;
	}

	@Override
	protected Panel createEditPanel(CrudContext context, IModel<BaseRole> model) {
		if (model.getObject() instanceof AdminRole) {
			return roleEditorFactory.createEditAdminPanel(context, new EntityModel<AdminRole>((AdminRole) model.getObject()), getParentModel());
		} else if (model.getObject() instanceof UserManagerRole) {
			return roleEditorFactory.createEditUserManagerPanel(context, new EntityModel<UserManagerRole>((UserManagerRole) model.getObject()), getParentModel());
		} else if (model.getObject() instanceof SalesmanagerRole) {
			return roleEditorFactory.createEditSalesmanagerPanel(context, new EntityModel<SalesmanagerRole>((SalesmanagerRole) model.getObject()), getParentModel());
		} else if (model.getObject() instanceof SalespersonRole) {
			return roleEditorFactory.createEditSalespersonPanel(context, new EntityModel<SalespersonRole>((SalespersonRole) model.getObject()), getParentModel());
		} 
		log.error("Unhandled role: " + model.getObject());
		return null;
	}

	protected Panel createNewAdminPanel(CrudContext context, IModel<BaseRole> model) {
		return roleEditorFactory.createEditAdminPanel(context, model == null ? null: new EntityModel<AdminRole>((AdminRole) model.getObject()), getParentModel());
	}

	protected Panel createNewSalesmanagerPanel(CrudContext context, IModel<BaseRole> model) {
		return roleEditorFactory.createEditSalesmanagerPanel(context, model == null ? null: new EntityModel<SalesmanagerRole>((SalesmanagerRole) model.getObject()), getParentModel());
	}

	protected Panel createNewUserManagerPanel(CrudContext context, IModel<BaseRole> model) {
		return roleEditorFactory.createEditUserManagerPanel(context, model == null ? null: new EntityModel<UserManagerRole>((UserManagerRole) model.getObject()), getParentModel());
	}

	protected Panel createNewSalespersonPanel(CrudContext context, IModel<BaseRole> model) {
		return roleEditorFactory.createEditSalespersonPanel(context, model == null ? null: new EntityModel<SalespersonRole>((SalespersonRole) model.getObject()), getParentModel());
	}

	@Override
	protected void deleteObject(BaseRole entity) {
		BaseRole role = dao.findById(entity.getId());
		getParentModel().getObject().removeRole(role);
		userDao.save(getParentModel().getObject());
		dao.delete(role);
	}

	@Override
	protected void saveEntityWithNewState(BaseRole entity) {
		dao.save(entity);
	}
}
