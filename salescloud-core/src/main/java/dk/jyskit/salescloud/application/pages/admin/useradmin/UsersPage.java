package dk.jyskit.salescloud.application.pages.admin.useradmin;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.Breadcrumb;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.UserManagerRole;
import dk.jyskit.salescloud.application.pages.admin.useradmin.users.ListUserPanel;
import dk.jyskit.salescloud.application.pages.base.AdminBasePage;
import dk.jyskit.waf.wicket.crud.CrudContext;

@MountPath("users")
@AuthorizeInstantiation({ AdminRole.ROLE_NAME, UserManagerRole.ROLE_NAME })
public class UsersPage extends AdminBasePage {
	private static final long serialVersionUID = 1L;

	public UsersPage(PageParameters parameters) {
		super(parameters);
		
		Breadcrumb breadcrumb = new Breadcrumb("breadcrumb") {
			@Override
			protected void onConfigure() {
				setVisible(allBreadCrumbParticipants().size() > 1);
			}
		};
		breadcrumb.setOutputMarkupId(true);
		breadcrumb.setOutputMarkupPlaceholderTag(true);
		add(breadcrumb);
		
		add(new ListUserPanel(new CrudContext(this, breadcrumb)));
		
//		add(WidgetPanel.wrap(new ListUserPanel("panel", this)).labelKey("entities"));
	}

}
