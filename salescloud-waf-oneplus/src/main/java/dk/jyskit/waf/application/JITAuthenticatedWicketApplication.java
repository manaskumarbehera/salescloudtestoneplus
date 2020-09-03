package dk.jyskit.waf.application;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;

import com.google.inject.Injector;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;
import de.agilecoders.wicket.core.markup.html.themes.bootstrap.BootstrapTheme;
import de.agilecoders.wicket.core.settings.ITheme;
import dk.jyskit.waf.application.pages.nonadmin.auth.LoginPage;
import dk.jyskit.waf.wicket.security.ISecureApplication;
import dk.jyskit.waf.wicket.security.UserRoleAuthorizationStrategy;
import dk.jyskit.waf.wicket.security.UserRolesAuthorizer;
import dk.jyskit.waf.wicket.security.UserSession;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 */
public abstract class JITAuthenticatedWicketApplication extends JITWicketApplication implements ISecureApplication {
	private ITheme adminTheme;
	
	/**
     * Constructor.
     */
    public JITAuthenticatedWicketApplication(Injector injector) {
        super(injector);
    }

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init() {
        super.init();

    	// customized auth strategy
        getSecuritySettings().setAuthorizationStrategy(
	        new UserRoleAuthorizationStrategy(new UserRolesAuthorizer()));
	}
	
	public static JITAuthenticatedWicketApplication get() {
		return (JITAuthenticatedWicketApplication) Application.get();
	}

	@Override
	public Session newSession(Request request, Response response) {
		return newUserSession(request);
	}
	
	public abstract UserSession newUserSession(Request request);

	public abstract Class<? extends Page> getAdminHomePage();
	
	public Class<? extends Page> getLoginPage() {
		return LoginPage.class;
	}

	@Override
	public ITheme[] getThemes() {
		return new ITheme[] {getNonAdminTheme(), getAdminTheme()};
	}
	
	protected ITheme createAdminTheme() {
		return new BootstrapTheme();
	}

	public ITheme getAdminTheme() {
		if (adminTheme == null) {
			adminTheme = createAdminTheme();
		}
		return adminTheme;
	}	

	public void useAdminTheme() {
		Bootstrap.getSettings(this).getActiveThemeProvider().setActiveTheme(getAdminTheme().name());
	}

	public boolean isUseEmailForLogin() {
		return true;
	}
	
	public boolean isAdminOnlyApplication() {
		return false;
	}
	
	public abstract Navbar createAdminNavbar(String markupId, Page page);
}
