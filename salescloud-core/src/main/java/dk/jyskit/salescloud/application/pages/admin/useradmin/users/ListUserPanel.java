package dk.jyskit.salescloud.application.pages.admin.useradmin.users;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;

import com.google.inject.Inject;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.model.UserManagerRole;
import dk.jyskit.salescloud.application.pages.admin.useradmin.UsersPage;
import dk.jyskit.salescloud.application.pages.admin.useradmin.roles.ListRolePanel;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.application.model.BaseRole;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.application.model.EntityState;
import dk.jyskit.waf.wicket.components.forms.jsr303form.exceptionhandling.DefaultExceptionHandler;
import dk.jyskit.waf.wicket.components.panels.extradata.EditExtraData;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapListDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions.EntityAction;
import dk.jyskit.waf.wicket.crud.AbstractListPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;
import dk.jyskit.waf.wicket.crud.CrudEntityAction;
import dk.jyskit.waf.wicket.security.UserSession;
import dk.jyskit.waf.wicket.utils.IAjaxCall;

public class ListUserPanel extends AbstractListPanel<BaseUser,Void> {
	private static final long serialVersionUID = 1L;

	@Inject
	private Dao<BaseUser> dao;

	public ListUserPanel(CrudContext context) {
		super(context, BaseUser.class.getSimpleName(),
			(UserSession.get().getActiveRoleClass().equals(AdminRole.class) 
					? new int[] { EntityState.ACTIVE_VAL, EntityState.INACTIVE_VAL, EntityState.SOFTDELETE_VAL } 
					: new int[] { EntityState.ACTIVE_VAL, EntityState.INACTIVE_VAL })
			);
	}

	@Override
	protected BootstrapTableDataProvider<BaseUser, String> getDataProvider() {
		IModel<List<BaseUser>> listModel = new AbstractReadOnlyModel<List<BaseUser>>() {
			@Override
			public List<BaseUser> getObject() {
				if (UserSession.get().getActiveRoleClass().equals(AdminRole.class)) {
					return dao.findAll();
				}
				if (UserSession.get().getActiveRoleClass().equals(UserManagerRole.class)) {
					List<BaseUser> users = new ArrayList<>();
					for (BaseUser user : dao.findAll()) {
						if (user.hasRole(SalespersonRole.class)) {
							users.add(user);
						} else if (user.getRoles().size() == 1) {  
							// No roles, except "user"
							users.add(user);
						}
					}
					return users;
				}
				return null;
			}
		};
		
		BootstrapListDataProvider<BaseUser> provider = new BootstrapListDataProvider<BaseUser>(listModel, "lastName", "firstName", "email", "smsPhone", "username") {
			@Override
			protected void sort(List<BaseUser> filteredList, SortParam<String> sort) {
				if ("roles".equals(sort.getProperty())) {
					Comparator<BaseUser> sortComparator = new Comparator<BaseUser>() {

						@Override
						public int compare(BaseUser o1, BaseUser o2) {
							String value1 = getRolesString(o1);
							String value2 = getRolesString(o2);
							if (value1 !=null && value2 != null) {
								return value1.compareTo(value2);
							} else {
								// null comparison
								return value1 == null ? (value2 == null ? 0 : -1) :  1;
							}
						}
					};
					if (!sort.isAscending()) {
						sortComparator = Collections.reverseOrder(sortComparator);
					}
					Collections.sort(filteredList, sortComparator);
				} else {
					super.sort(filteredList, sort);
				}
			}
		};
		provider.setSort("lastModificationDate", SortOrder.DESCENDING);
		return provider;
	}
	
	@Override
	protected void addDataColumns(List<IColumn<BaseUser, String>> cols) {
		cols.add(createColumn("username"));
		cols.add(new AbstractColumn<BaseUser, String>(new ResourceModel("roles"), "roles") {
			@Override
			public void populateItem(Item<ICellPopulator<BaseUser>> cellItem, String componentId, IModel<BaseUser> rowModel) {
				cellItem.add(new Label(componentId, getRolesString(rowModel.getObject())));
			}
		});
		cols.add(createColumn("email"));
//		cols.add(new AbstractColumn<BaseUser, String>(new ResourceModel("entityState"), "entityState") {
//			@Override
//			public void populateItem(Item<ICellPopulator<BaseUser>> cellItem, String componentId, IModel<BaseUser> rowModel) {
//				cellItem.add(new Label(componentId, rowModel.getObject().getEntityState().getDisplayModel()));
//			}
//		});
		cols.add(createColumn("lastModificationDate").withNoWrap());
	}

	@Override
	protected Panel createEditPanel(CrudContext context, IModel<BaseUser> model) {
		return new EditUserPanel(context, model);
	}

	@Override
	protected void deleteObject(BaseUser entity) {
		// Hard delete:
		//		dao.delete(dao.findById(entity.getId()));
		
		// Soft delete:
		entity = dao.findById(entity.getId());
		entity.setEntityState(EntityState.SOFTDELETE);
		dao.save(entity);
	}

	@Override
	protected List<String> predeleteCheck(BaseUser object) {
		List<String> messages = new ArrayList<>();
		return messages;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected EntityAction<BaseUser>[] getRowActions() {
		if (UserSession.get().getActiveRoleClass().equals(AdminRole.class)) {
			EntityAction<?>[] actions = {getEditAction(), getRolesAction(), getToggleEntityStateAction(), getDeleteAction()};
			return (EntityAction<BaseUser>[]) actions;
		} else {
			EntityAction<?>[] actions = {getEditAction(), getRolesAction(), getToggleEntityStateAction()};
			return (EntityAction<BaseUser>[]) actions;
		}
	}
	
	private CrudEntityAction<?> getRolesAction() {
		CrudEntityAction<BaseUser> rolesAction = new CrudEntityAction<BaseUser>(context, getKey("roles.link"), getKey("roles.tooltip"), FontAwesomeIconType.pencil) {

			@Override
			public Panel createPanel(CrudContext context, IModel<BaseUser> model) {
				// Note: this model is no good in this case. We have to wrap object in loadabledetachable model
				return new ListRolePanel(context, new EntityModel<BaseUser>(model.getObject()));
			}
//			@Override
//			public void onClick(IModel<BaseUser> model, AjaxRequestTarget target) {
//				Panel panel = WidgetPanel.wrap(new ListRolePanel(componentId, rootMarkupContainer, model.getObject())).labelKey("roles");
//				rootMarkupContainer.addOrReplace(panel);
//				target.add(panel);
//			}
		};
		return rolesAction;
	}

	protected EntityAction<BaseUser> getExtraDataAction() {
		EntityAction<BaseUser> action = new EntityAction<BaseUser>("extra.data.link", "extra.data.tooltip", FontAwesomeIconType.suitcase) {
			@Override
			public void onClick(IModel<BaseUser> model, AjaxRequestTarget target) {
				try {
					IAjaxCall onEndCallback = new IAjaxCall() {
						@Override
						public void invoke(AjaxRequestTarget target) {
							setResponsePage(UsersPage.class);
						}
					};
					Panel panel = new EditExtraData("content", model.getObject(), onEndCallback );
					ListUserPanel.this.getParent().addOrReplace(panel);
					target.add(panel);
				} catch (Exception e) {
					DefaultExceptionHandler exceptionHandler = new DefaultExceptionHandler(ListUserPanel.this);
					exceptionHandler.onException(e);
					setResponsePage(getPage());
				}
			}
		};
		return action;
	}

	public String getRolesString(BaseUser user) {
		List<String> roleDisplayList = new ArrayList<>();
		for (BaseRole role : user.getBaseRoleList()) {
//			roleDisplayList.add(getString("role." + role.getRoleName()));
			roleDisplayList.add(role.toString());
		}
		String roleString = Strings.join(", ", roleDisplayList);
		return roleString;
	}

	@Override
	protected void saveEntityWithNewState(BaseUser entity) {
		dao.save(entity);
	}
}
