package dk.jyskit.salescloud.application.extensions;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import dk.jyskit.salescloud.application.extensionpoints.RoleEditorFactory;
import dk.jyskit.salescloud.application.extensions.editroles.EditAdminRolePanel;
import dk.jyskit.salescloud.application.extensions.editroles.EditSalesmanagerRolePanel;
import dk.jyskit.salescloud.application.extensions.editroles.EditSalespersonRolePanel;
import dk.jyskit.salescloud.application.extensions.editroles.EditUserManagerRolePanel;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.SalesmanagerRole;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.model.UserManagerRole;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.wicket.crud.CrudContext;

public class MobileRoleEditorFactory implements RoleEditorFactory {

	@Override
	public Panel createEditAdminPanel(CrudContext context, IModel<AdminRole> childModel, IModel<BaseUser> parentModel) {
		return new EditAdminRolePanel(context, childModel, parentModel);
	}

	@Override
	public Panel createEditSalespersonPanel(CrudContext context, IModel<SalespersonRole> childModel, IModel<BaseUser> parentModel) {
		return new EditSalespersonRolePanel(context, childModel, parentModel);
	}

	@Override
	public Panel createEditSalesmanagerPanel(CrudContext context, IModel<SalesmanagerRole> childModel, IModel<BaseUser> parentModel) {
		return new EditSalesmanagerRolePanel(context, childModel, parentModel);
	}

	@Override
	public Panel createEditUserManagerPanel(CrudContext context, IModel<UserManagerRole> childModel, IModel<BaseUser> parentModel) {
		return new EditUserManagerRolePanel(context, childModel, parentModel);
	}

}
