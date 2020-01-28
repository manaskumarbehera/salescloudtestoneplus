package dk.jyskit.salescloud.application.extensionpoints;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.SalesmanagerRole;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.model.UserManagerRole;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.wicket.crud.CrudContext;

public interface RoleEditorFactory {
	Panel createEditSalespersonPanel(CrudContext context, IModel<SalespersonRole> childModel, IModel<BaseUser> parentModel);
	Panel createEditSalesmanagerPanel(CrudContext context, IModel<SalesmanagerRole> childModel, IModel<BaseUser> parentModel);
	Panel createEditUserManagerPanel(CrudContext context, IModel<UserManagerRole> childModel, IModel<BaseUser> parentModel);
	Panel createEditAdminPanel(CrudContext context, IModel<AdminRole> childModel, IModel<BaseUser> parentModel);
}
