package dk.jyskit.salescloud.application.pages.admin.organisations;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.Breadcrumb;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.pages.base.AdminBasePage;
import dk.jyskit.waf.wicket.crud.CrudContext;

@AuthorizeInstantiation({ AdminRole.ROLE_NAME })
@SuppressWarnings("serial")
public class ListOrganisationsPage extends AdminBasePage {

	public ListOrganisationsPage(PageParameters parameters) {
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
		
		add(new ListOrganisationsPanel(new CrudContext(this, breadcrumb)));
	}
}
