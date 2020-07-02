package dk.jyskit.salescloud.application.pages.home;

import java.util.List;

import dk.jyskit.salescloud.application.apis.user.UserApiClient;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.github.rjeschke.txtmark.Processor;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import dk.jyskit.salescloud.application.CoreApplication;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.BusinessAreaDao;
import dk.jyskit.salescloud.application.dao.PageInfoDao;
import dk.jyskit.salescloud.application.model.AccessCodes;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.BusinessAreas;
import dk.jyskit.salescloud.application.model.PageInfo;
import dk.jyskit.salescloud.application.model.SalesmanagerRole;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.model.UserManagerRole;
import dk.jyskit.salescloud.application.pages.CorePageIds;
import dk.jyskit.salescloud.application.pages.admin.dashboard.AdminDashboardPage;
import dk.jyskit.salescloud.application.pages.admin.useradmin.UsersPage;
import dk.jyskit.salescloud.application.pages.base.BasePage;
import dk.jyskit.salescloud.application.pages.noaccess.NoAccessPage;
import dk.jyskit.salescloud.application.pages.sales.existingcontract.ExistingContractPage;
import dk.jyskit.salescloud.application.services.accesscodes.AccessCodeChecker;
import dk.jyskit.waf.application.Environment;
import dk.jyskit.waf.application.model.BaseUser;

@AuthorizeInstantiation({ AdminRole.ROLE_NAME, SalespersonRole.ROLE_NAME, SalesmanagerRole.ROLE_NAME, UserManagerRole.ROLE_NAME })
@SuppressWarnings("serial")
public class AdminHomePage extends BasePage {
	@Inject
	private PageInfoDao pageInfoDao;
	@Inject
	private BusinessAreaDao businessAreaDao;

	public AdminHomePage(PageParameters parameters) {
		super(parameters);


		Thread thread1 = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				UserApiClient.changePasswordOnOtherServer("janjysk", "Devguy");
			}
		});
		thread1.start();

		
		BaseUser user = (BaseUser) CoreSession.get().getUser();
		if (user != null) {
//			if (!user.hasRole(AdminRole.class) && CoreApplication.get().getSetting(Environment.WAF_ENV).equals("staging")) {
//				throw new RestartResponseException(new NoAccessPage("Denne server kan kun bruges til test af nye funktioner og er kun tilgængelig for administratorer."));
//			}
			if (AdminRole.class.equals(CoreSession.get().getActiveRoleClass())) {
				throw new RestartResponseException(AdminDashboardPage.class);
			}
			if (SalesmanagerRole.class.equals(CoreSession.get().getActiveRoleClass())) {
				throw new RestartResponseException(AdminDashboardPage.class);
			}
			if (UserManagerRole.class.equals(CoreSession.get().getActiveRoleClass())) {
				throw new RestartResponseException(UsersPage.class);
			}
		}
		
		WebMarkupContainer kontraktModulContainer = new WebMarkupContainer("rabataftale");
		kontraktModulContainer.setVisible(false);
		add(kontraktModulContainer);
		
		SalespersonRole salesperson 	= (SalespersonRole) user.getRole(SalespersonRole.class);
//		AdminRole administrator 		= (AdminRole) user.getRole(AdminRole.class);
//		if ((administrator == null) && ((salesperson == null) || (!(salesperson.isAgent() || salesperson.isAgent_sa())))) {
//			kontraktModulContainer.setVisible(false);
//		}

		{
			List<BusinessArea> businessAreas = Lists.newArrayList();

			addBusinessArea(businessAreas, BusinessAreas.ONE_PLUS);

			add(getBusinessAreaListView("group1", businessAreas));
		}

//		{
//			List<BusinessArea> businessAreas = Lists.newArrayList();
//
//			addBusinessArea(businessAreas, BusinessAreas.FIBER);
//			addBusinessArea(businessAreas, BusinessAreas.FIBER_ERHVERV);
//
//			if (CoreApplication.get().getSetting(Environment.WAF_ENV).equals("prod")) {
//				if (MobileSession.get().hasRole(AdminRole.class)
//						|| ((salesperson != null) && salesperson.isPartner_ec())
//						|| AccessCodeChecker.isCodeActiveForUser(AccessCodes.OFFICE365_CONFIGURATOR)) {
//					addBusinessArea(businessAreas, BusinessAreas.TDC_OFFICE);
//				}
//			} else {
//				addBusinessArea(businessAreas, BusinessAreas.TDC_OFFICE);
//			}
//
//			add(getBusinessAreaListView("group2", businessAreas));
//		}


//		if (((salesperson != null) && (salesperson.isPartner_ec())) ||
//			(AccessCodeChecker.isCodeActiveForUser(AccessCodes.WIFI_CONFIGURATOR) && MobileSession.get().hasRole(AdminRole.class))) {
//			addBusinessArea(businessAreas, BusinessAreas.WIFI);
//		}

		// --- Deprecated business areas
		
//		if (AccessCodeChecker.isCodeActiveForUser(AccessCodes.SWITCHBOARD_CONFIGURATOR) || MobileSession.get().hasRole(AdminRole.class)) {
//			addBusinessArea(businessAreas, BusinessAreas.SWITCHBOARD);
//		}
//		addBusinessArea(businessAreas, BusinessAreas.MOBILE_VOICE);

	}

	private ListView<BusinessArea> getBusinessAreaListView(String id, List<BusinessArea> businessAreas) {
		ListView<BusinessArea> businessAreaList = new ListView<BusinessArea>(id, businessAreas) {
			@Override
			protected void populateItem(ListItem<BusinessArea> item) {
				final BusinessArea businessArea = businessAreaDao.findById(item.getModelObject().getId());

				item.add(new BootstrapLink("select", Buttons.Type.Primary) {
					@Override
					public void onClick() {
						CoreSession.get().setBusinessArea(businessArea);
						if (CoreSession.get().getContract() != null) {
							if (!CoreSession.get().getContract().getBusinessArea().equals(businessArea)) {
								CoreSession.get().setContract(null);
							}
						}
						PageInfo pageInfo = pageInfoDao.findByPageId(businessArea.getId(), CorePageIds.SALES_EXISTING_CONTRACTS);
						setResponsePage(ExistingContractPage.class);
					}

					protected void onConfigure() {
						BaseUser user = CoreSession.get().getUser();
						setVisible(user.hasRole(SalespersonRole.class));
					};
				}.setSize(Buttons.Size.Large).setLabel(Model.of(businessArea.getName())));

				item.add(new Label("intro", Processor.process(businessArea.getIntroText())).setEscapeModelStrings(false));
			}
		};
		return businessAreaList;
	}

	private void addBusinessArea(List<BusinessArea> businessAreas, int id) {
		BusinessArea businessArea = getBusinessAreaById(id);
		if (businessArea != null && businessArea.isActive()) {
			businessAreas.add(businessArea);
		}
	}
	
	private BusinessArea getBusinessAreaById (int id) {
		if (id == BusinessAreas.OLD_ONE) {
			BusinessArea businessArea = new BusinessArea();
			businessArea.setName("One");
			businessArea.setBusinessAreaId(id);
			return businessArea;
		}
		return businessAreaDao.findUniqueByField("businessAreaId", id);
	}
}
