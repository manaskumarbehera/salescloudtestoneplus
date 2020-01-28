package dk.jyskit.salescloud.application.pages;

import org.apache.wicket.Page;
import org.apache.wicket.request.Request;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;

import com.google.inject.Guice;

import dk.jyskit.salescloud.application.CoreApplication;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.SalesmanagerRole;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.model.UserManagerRole;
import dk.jyskit.salescloud.application.pages.home.AdminHomePage;
import dk.jyskit.salescloud.application.servlet.CoreModule;
import dk.jyskit.waf.application.JITApplicationInitializer;
import dk.jyskit.waf.application.servlet.JITApplicationServletModule;
import dk.jyskit.waf.wicket.security.UserSession;

public abstract class BasePageTest {
    protected CoreApplication application;
    protected WicketTester tester; 

    @Before
	public void setup() {
		System.setProperty("waf.env", "dev");
		
		tester = new WicketTester(new CoreApplication(Guice.createInjector(new JITApplicationServletModule() {

			@Override
			public String getNamespace() {
				return "salescloud";
			}
			
			@Override
			public void guiceInit() {
			}

			@Override
			protected String getPersistenceUnitName() {
				return "test-persistence";
			}
			
			@Override
			public Class getWicketApplicationClass() {
				return TestApplication.class;
			}
			
		}, new CoreModule())) {
			
			@Override
			public String getNamespace() {
				return "salescloud";
			}

			@Override
			public Class<? extends Page> getAdminHomePage() {
				return AdminHomePage.class;
			}

			@Override
			public Class<? extends Page> getHomePage() {
				return AdminHomePage.class;
			}

			@Override
			public JITApplicationInitializer getApplicationInitializer() {
				return new TestInitializer();
			}

			@Override
			public String getGoogleSiteVerificationId() {
				return null;
			}

			@Override
			public String getSiteMapXml() {
				return null;
			}

			@Override
			public UserSession newUserSession(Request request) {
				return new CoreSession(request) {
					@Override
					public Class[] getRolesByPriority() {
						return new Class[] {AdminRole.class, UserManagerRole.class, SalesmanagerRole.class, SalespersonRole.class};
					}
				};
			}
		});
	}
}  