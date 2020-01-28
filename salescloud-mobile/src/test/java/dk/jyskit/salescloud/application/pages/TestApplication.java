package dk.jyskit.salescloud.application.pages;

import org.apache.wicket.Page;
import org.apache.wicket.request.Request;

import com.google.inject.Inject;
import com.google.inject.Injector;

import dk.jyskit.salescloud.application.CoreApplication;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.SalesmanagerRole;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.model.UserManagerRole;
import dk.jyskit.salescloud.application.pages.home.AdminHomePage;
import dk.jyskit.waf.application.JITApplicationInitializer;
import dk.jyskit.waf.wicket.security.UserSession;

public class TestApplication extends CoreApplication {
	
	@Override
	public String getNamespace() {
		return "salescloud";
	}

    @Inject
	public TestApplication(Injector injector) {
		super(injector);
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

}
