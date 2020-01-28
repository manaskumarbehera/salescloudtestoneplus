package dk.jyskit.salescloud.application.pages.admin.contractcategories;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.Breadcrumb;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.pages.base.AdminBasePage;
import dk.jyskit.salescloud.application.pages.sales.existingcontract.ExistingContractPage;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.wicket.crud.CrudContext;

@AuthorizeInstantiation({ SalespersonRole.ROLE_NAME })
@SuppressWarnings("serial")
public class ListContractCategoryPage extends AdminBasePage {

	public ListContractCategoryPage(PageParameters parameters) {
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
		
		IModel<SalespersonRole> parentModel = EntityModel.forEntity((SalespersonRole) CoreSession.get().getUser().getRole(SalespersonRole.class));
		add(new ListContractCategoryPanel(new CrudContext(this, breadcrumb), parentModel));

		add(new BootstrapLink<String>("back", Buttons.Type.Default) {
			@Override
			public void onClick() {
				setResponsePage(ExistingContractPage.class);
			}
		}.setLabel(Model.of("Tilbage")));

//		
//		add(WidgetPanel.wrap(new ListContractCategoryPanel("panel", this)).labelKey("entities"));
//		
//		add(new BootstrapLink<String>("back", Buttons.Type.Default) {
//			@Override
//			public void onClick() {
//				setResponsePage(ExistingContractPage.class);
//			}
//		}.setLabel(Model.of("Tilbage")));
	}
}
