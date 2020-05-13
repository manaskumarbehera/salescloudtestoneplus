package dk.jyskit.salescloud.application;

import static dk.jyskit.waf.wicket.utils.BootstrapUtils.navbarPageLink;

import java.util.ArrayList;
import java.util.List;

import com.x5.template.Theme;
import dk.jyskit.salescloud.application.pages.accessnew.locations.LocationsPage;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.Request;

import com.google.inject.Inject;
import com.google.inject.Injector;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonList;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import dk.jyskit.salescloud.application.apis.auth.ApiAuthPage;
import dk.jyskit.salescloud.application.apis.auth.ApiAuthUpdatePage;
import dk.jyskit.salescloud.application.dao.BusinessAreaDao;
import dk.jyskit.salescloud.application.dao.ContractDao;
import dk.jyskit.salescloud.application.model.AccessCodes;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.BusinessAreas;
import dk.jyskit.salescloud.application.model.FeatureType;
import dk.jyskit.salescloud.application.model.OrganisationType;
import dk.jyskit.salescloud.application.model.SalesmanagerRole;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.model.UserManagerRole;
import dk.jyskit.salescloud.application.pages.admin.businessarea.ListBusinessAreasPage;
import dk.jyskit.salescloud.application.pages.admin.contractcategories.ListContractCategoryPage;
import dk.jyskit.salescloud.application.pages.admin.dashboard.AdminDashboardPage;
import dk.jyskit.salescloud.application.pages.admin.organisations.ListOrganisationsPage;
import dk.jyskit.salescloud.application.pages.admin.profile.ChangePasswordPage;
import dk.jyskit.salescloud.application.pages.admin.segments.ListSegmentsPage;
import dk.jyskit.salescloud.application.pages.admin.useradmin.UsersPage;
import dk.jyskit.salescloud.application.pages.accessold.adsl.AdslPage;
import dk.jyskit.salescloud.application.pages.auth.LoginPage;
import dk.jyskit.salescloud.application.pages.base.themes.tdc.TdcThemeNew;
import dk.jyskit.salescloud.application.pages.contractsettings.MobileContractSettingsPage;
import dk.jyskit.salescloud.application.pages.contractsummary.ContractSummaryPage;
import dk.jyskit.salescloud.application.pages.accessnew.fiber.FiberPage;
import dk.jyskit.salescloud.application.pages.home.AdminHomePage;
import dk.jyskit.salescloud.application.pages.makeupdates.RemoveFiberPage;
import dk.jyskit.salescloud.application.pages.makeupdates.RemoveBusinessAreasPage;
import dk.jyskit.salescloud.application.pages.officeadditional.OfficeAdditionalProductsPage;
import dk.jyskit.salescloud.application.pages.officeimplementation.OfficeImplementationPage;
import dk.jyskit.salescloud.application.pages.partner.PartnerSettingsPage;
import dk.jyskit.salescloud.application.pages.productselection.ProductSelectionPage;
import dk.jyskit.salescloud.application.pages.sales.masterdata.MasterDataPage;
import dk.jyskit.salescloud.application.pages.sso.LoginSSOPage;
import dk.jyskit.salescloud.application.pages.standardbundles.StandardBundlesPage;
import dk.jyskit.salescloud.application.pages.subscriptionconfiguration.ExternalSubscriptionConfigurationPage;
import dk.jyskit.salescloud.application.pages.subscriptionconfiguration.ExternalSubscriptionImplementationPage;
import dk.jyskit.salescloud.application.pages.subscriptionconfiguration.SubscriptionConfigurationPage;
import dk.jyskit.salescloud.application.pages.switchboard.SwitchboardPage;
import dk.jyskit.salescloud.application.pages.wifiadditionalinfo.WiFiAdditionalInfoPage;
import dk.jyskit.salescloud.application.pages.wifibundles.WiFiBundlesPage;
import dk.jyskit.salescloud.application.services.accesscodes.AccessCodeChecker;
import dk.jyskit.waf.application.Environment;
import dk.jyskit.waf.application.JITApplicationInitializer;
import dk.jyskit.waf.application.components.navbar.AuthNavbar;
import dk.jyskit.waf.application.components.navbar.AuthNavbarDropdownMenuBuilder;
import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.utils.guice.Lookup;
import dk.jyskit.waf.wicket.security.UserSession;
import lombok.extern.slf4j.Slf4j;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 * 
 */
@Slf4j
public class MobileSalescloudApplication extends CoreApplication {
	public static final String BASE_FOLDER = "/tmp/";

	/**
     * Get Application for current thread.
     *
     * @return The current thread's Application
     */
    public static MobileSalescloudApplication get() {
        return (MobileSalescloudApplication) Application.get();
    }
    
    @Override
    public String getNamespace() {
    	return NAMESPACE;
    }

    /**
     * Constructor.
     */
    @Inject
    public MobileSalescloudApplication(Injector injector) {
		super(injector);
    }

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends Page> getHomePage() {
		return AdminHomePage.class;
	}

	public Class<? extends Page> getLoginPage() {
		return LoginPage.class;
	}
	
	@Override
	public Class<? extends Page> getAdminHomePage() {
		return AdminHomePage.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init() {
        super.init();
        
        getDebugSettings().setOutputMarkupContainerClassName(false);

//        startFTPServer();
	}

	@Override
	public JITApplicationInitializer getApplicationInitializer() {
		return new ApplicationInitializer();
	}

	/**
	 * Use in cases where DNS (TXT) is not used to verify site for Google Webmasters tools
	 * and Google Analytics.
	 * Return a string similar to: "google659ba521b15cbee3"
	 */
	@Override
	public String getGoogleSiteVerificationId() {
		return null;
	}

	/**
	 * Return an XML document with site map. For at quick-and-easy solution, look for "online sitemap generator" on Google.  
	 */
	@Override
	public String getSiteMapXml() {
		return null;
	}

	@Override
	public void mountPages() {
		super.mountPages();
        // non-admin pages
        mountPage("sso", LoginSSOPage.class);
        mountPage("admin", AdminHomePage.class);
        mountPage("removebusinessareas/${ba}", RemoveBusinessAreasPage.class);
//        mountPage("removefiber", RemoveFiberPage.class);
        mountPage("konfiguration/${businessAreaId}/${contract}", ExternalSubscriptionConfigurationPage.class);
        mountPage("implementering/${businessAreaId}/${contract}", ExternalSubscriptionImplementationPage.class);
        // API
        mountPage("v1/api/auth", ApiAuthPage.class); 
        mountPage("v1/api/auth/update", ApiAuthUpdatePage.class); 
	}

	public boolean isUseEmailForLogin() {
		return false;  
	}

	@Override
	public Navbar createAdminNavbar(String markupId, Page page) {
		BaseUser user = (BaseUser) MobileSession.get().getUser();
		Class activeRole = MobileSession.get().getActiveRoleClass();

		AuthNavbar navbar = new AuthNavbar(markupId, user);

		navbar.setBrandImage(
				(CoreApplication.get().getSetting(Environment.WAF_ENV).equals("staging") ? TdcThemeNew.LOGO_STAGING_REFERENCE : TdcThemeNew.LOGO_REFERENCE), Model.of("TDC"));

		if (!MobileSession.get().isExternalAccessMode()) {
			List<Component> menuItems = new ArrayList();
			
			SalespersonRole salesperson =  (SalespersonRole) (MobileSession.get().getUser().getRole(SalespersonRole.class));
			BusinessArea businessArea2 = MobileSession.get().getBusinessArea();
			BusinessArea businessArea = businessArea2;
			
			if (AdminRole.class.equals(activeRole)) {
				menuItems.add(navbarPageLink(AdminDashboardPage.class, "menu.admin.dashboard").setIconType(FontAwesomeIconType.bar_chart_o));
				menuItems.add(navbarPageLink(ListBusinessAreasPage.class, "menu.admin.businessareas").setIconType(FontAwesomeIconType.building_o));
				menuItems.add(navbarPageLink(ListOrganisationsPage.class, "menu.admin.organisations").setIconType(FontAwesomeIconType.compass));
				menuItems.add(navbarPageLink(ListSegmentsPage.class, "menu.admin.segments").setIconType(FontAwesomeIconType.paperclip));
				menuItems.add(navbarPageLink(UsersPage.class, "menu.admin.users").setIconType(FontAwesomeIconType.users));
			} else if (UserManagerRole.class.equals(activeRole)) {
				menuItems.add(navbarPageLink(UsersPage.class, "menu.admin.users").setIconType(FontAwesomeIconType.users));
			} else if (SalesmanagerRole.class.equals(activeRole)) {
				menuItems.add(navbarPageLink(AdminDashboardPage.class, "menu.admin.dashboard").setIconType(FontAwesomeIconType.bar_chart_o));
			} else if (SalespersonRole.class.equals(activeRole)) {
				if (MobileSession.get().getContractId() != null) {
					menuItems.add(navbarPageLink(MasterDataPage.class, "menu.sales.masterdata"));  // .setIconType(FontAwesomeIconType.file));

					if (businessArea.getBusinessAreaId() == BusinessAreas.TDC_OFFICE) {
						// menuItems.add(navbarPageLink(MobileContractSettingsPage.class, "menu.sales.office_settings"));  // .setIconType(FontAwesomeIconType.cogs));
					} else if (businessArea.getBusinessAreaId() == BusinessAreas.ONE_PLUS) {
						menuItems.add(navbarPageLink(MobileContractSettingsPage.class, "menu.sales.settings.one"));  // .setIconType(FontAwesomeIconType.cogs));
					} else if ((businessArea.getBusinessAreaId() != BusinessAreas.FIBER) && (businessArea.getBusinessAreaId() != BusinessAreas.FIBER_ERHVERV)) {
						menuItems.add(navbarPageLink(MobileContractSettingsPage.class, "menu.sales.settings"));  // .setIconType(FontAwesomeIconType.cogs));
					}

					if (businessArea.getBusinessAreaId() == BusinessAreas.ONE_PLUS) {
						menuItems.add(navbarPageLink(SwitchboardPage.class, "menu.sales.switchboard.one"));  // .setIconType(FontAwesomeIconType.bullseye));
					} else if (businessArea.hasFeature(FeatureType.SWITCHBOARD) && !AccessCodeChecker.hasCode(salesperson, AccessCodes.SWITCHBOARD_FEATURE_NO_ACCESS)) {
						menuItems.add(navbarPageLink(SwitchboardPage.class, "menu.sales.switchboard"));  // .setIconType(FontAwesomeIconType.bullseye));
					}

					if (businessArea.isOnePlus()) {
						menuItems.add(navbarPageLink(StandardBundlesPage.class, "menu.sales.subscriptions.one"));  // .setIconType(FontAwesomeIconType.bullseye));
//						menuItems.add(navbarPageLink(ProductSelectionPage.class, "menu.sales.productselection.one"));  // .setIconType(FontAwesomeIconType.asterisk));
					} else if (businessArea.hasFeature(FeatureType.MOBILE_BUNDLES_STANDARD, FeatureType.TEM5_PRODUCTS)) {
						menuItems.add(navbarPageLink(StandardBundlesPage.class, "menu.sales.subscriptions"));  // .setIconType(FontAwesomeIconType.bullseye));
						menuItems.add(navbarPageLink(ProductSelectionPage.class, "menu.sales.productselection"));  // .setIconType(FontAwesomeIconType.asterisk));
					}
					
					if (businessArea.hasFeature(FeatureType.TDC_OFFICE)) {
						menuItems.add(navbarPageLink(StandardBundlesPage.class, "menu.sales.officebundles"));  // Pakker
						menuItems.add(navbarPageLink(OfficeAdditionalProductsPage.class, "menu.sales.office_additional_products"));
					}

					if (businessArea.isOnePlus()) {
						menuItems.add(navbarPageLink(LocationsPage.class, "menu.sales.locations"));  // .setIconType(FontAwesomeIconType.bullseye));
					} else {
						if (businessArea.hasFeature(FeatureType.XDSL) && !AccessCodeChecker.hasCode(salesperson, AccessCodes.XDSL_FEATURE_NO_ACCESS)) {
							menuItems.add(navbarPageLink(AdslPage.class, "menu.sales.xdsl"));  // .setIconType(FontAwesomeIconType.bullseye));
						}

						if (businessArea.hasFeature(FeatureType.FIBER, FeatureType.FIBER_ERHVERV)) {
							menuItems.add(navbarPageLink(FiberPage.class, "menu.sales.fiber"));  // .setIconType(FontAwesomeIconType.bullseye));
						}
					}

					if (businessArea.hasFeature(FeatureType.WIFI)) {
						menuItems.add(navbarPageLink(WiFiBundlesPage.class, "menu.sales.wifi"));
						menuItems.add(navbarPageLink(WiFiAdditionalInfoPage.class, "menu.sales.wifi-additional-info"));
					}
					
					if (businessArea.hasFeature(FeatureType.PARTNER_SETTINGS)) {
						if (salesperson.getOrganisation().getType().equals(OrganisationType.PARTNER_CENTER)) {
							menuItems.add(navbarPageLink(PartnerSettingsPage.class, "menu.sales.partnersettings"));  // .setIconType(FontAwesomeIconType.bullseye));
						}
					}
					menuItems.add(navbarPageLink(ContractSummaryPage.class, "menu.sales.summary"));  // .setIconType(FontAwesomeIconType.table));
					
//					if (businessArea.hasFeature(FeatureType.FIBER)) {
//						menuItems.add(navbarPageLink(FiberAdditionalInfoPage.class, "menu.sales.fiber-additional-info"));
//					}
					
					if (businessArea.hasFeature(FeatureType.TDC_OFFICE)) {
						menuItems.add(navbarPageLink(OfficeImplementationPage.class, "menu.sales.office_implementation"));
						menuItems.add(navbarPageLink(SubscriptionConfigurationPage.class, "menu.sales.subscriptionconfiguration"));  // .setIconType(FontAwesomeIconType.asterisk));
					}
					
//					if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.MOBILE_VOICE) {
//						menuItems.add(navbarPageLink(SubscriptionConfigurationPage.class, "menu.sales.subscriptionconfiguration"));  // .setIconType(FontAwesomeIconType.asterisk));
//					}
				}
			}

			navbar.addAuthorizedComponents(
					Navbar.ComponentPosition.LEFT,
					(Component[]) menuItems.toArray(new Component[menuItems.size()])
					);
			
			AuthNavbarDropdownMenuBuilder currentUserMenu = new AuthNavbarDropdownMenuBuilder(Model.of(""), user);
			currentUserMenu.addAuthorizedPageMenu("change.password", ChangePasswordPage.class);
			
			if (activeRole.equals(SalespersonRole.class)) {
				currentUserMenu.addPageMenu("menu.admin.contractcategories", ListContractCategoryPage.class);
			}
			
			// user.getRoles() returns ["user", "admin"] if user is admin
			if (user.getRoles().size() > 2) {
				if (user.hasRole(AdminRole.class) && (!activeRole.equals(AdminRole.class))) {
					currentUserMenu.addLink(new RoleLink(ButtonList.getButtonMarkupId(), new Model("-> Administrator"), Buttons.Type.Menu, AdminRole.class));
				}
				if (user.hasRole(SalespersonRole.class) && (!activeRole.equals(SalespersonRole.class))) {
					currentUserMenu.addLink(new RoleLink(ButtonList.getButtonMarkupId(), new Model("-> Sælger"), Buttons.Type.Menu, SalespersonRole.class));
				}
				if (user.hasRole(SalesmanagerRole.class) && (!activeRole.equals(SalesmanagerRole.class))) {
					currentUserMenu.addLink(new RoleLink(ButtonList.getButtonMarkupId(), new Model("-> Sales Manager"), Buttons.Type.Menu, SalesmanagerRole.class));
				}
				if (user.hasRole(UserManagerRole.class) && (!activeRole.equals(UserManagerRole.class))) {
					currentUserMenu.addLink(new RoleLink(ButtonList.getButtonMarkupId(), new Model("-> Brugeradministrator"), Buttons.Type.Menu, UserManagerRole.class));
				}
			}
			
			currentUserMenu.addLink(new LogoutLink(ButtonList.getButtonMarkupId(), new ResourceModel("logout"), Buttons.Type.Menu));
			currentUserMenu.iconType(FontAwesomeIconType.user);

			navbar.addAuthorizedComponents(Navbar.ComponentPosition.RIGHT, currentUserMenu.build()); 
		}
		
		return navbar;
	}

	@Override
	public UserSession newUserSession(Request request) {
		MobileSession session = new MobileSession(request);
		
		String autologin = CoreApplication.get().getSetting("autologin");
		if ("admin".equals(autologin)) {
			List<BaseUser> users = Lookup.lookup(UserDao.class).findAll();
			for (BaseUser user : users) {
				if (user.hasRole(AdminRole.class)) {
					session.setUser(user);
					session.setActiveRoleClass(AdminRole.class);
					break;
				}
			}
		} else if ("salesperson".equals(autologin)) {
			List<BaseUser> users = Lookup.lookup(UserDao.class).findAll();
			for (BaseUser user : users) {
				if ("jan@jyskit.dk".equalsIgnoreCase(user.getEmail()) && user.hasRole(SalespersonRole.class)) {
					session.setUser(user);
					session.setActiveRoleClass(SalespersonRole.class);
					session.setBusinessArea(Lookup.lookup(BusinessAreaDao.class).findAll().get(0));
					session.setContract(Lookup.lookup(ContractDao.class).findAll().get(0));
					break;
				}
			}
		} else if ("salesmanager".equals(autologin)) {
			List<BaseUser> users = Lookup.lookup(UserDao.class).findAll();
			for (BaseUser user : users) {
				if (user.hasRole(SalesmanagerRole.class)) {
					session.setUser(user);
					session.setActiveRoleClass(SalesmanagerRole.class);
					break;
				}
			}
		}
		
		return session;
	}

//	private void startFTPServer() {
//		try {
//			FtpServerFactory serverFactory = new FtpServerFactory();
//			ListenerFactory factory = new ListenerFactory();
//			factory.setPort(2222);
//			
//			// replace the default listener
//			serverFactory.addListener("default", factory.createListener());
//			
//			PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
//			userManagerFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor());
//			UserManager userManagement = userManagerFactory.createUserManager();
//			UserFactory userFact = new UserFactory();
//			userFact.setName("tdc");
//			userFact.setPassword("Ht2fsrf5J8");
//			userFact.setHomeDirectory("/tmp/");
//			User user = userFact.createUser();
//			userManagement.save(user);
//
//			serverFactory.setUserManager(userManagement);
//			
//			// start the server
//			FtpServer server = serverFactory.createServer();
//			server.start();
//		} catch (FtpException e) {
//			log.error("Failed to start FTP server");
//		}		
//	} 	
}
