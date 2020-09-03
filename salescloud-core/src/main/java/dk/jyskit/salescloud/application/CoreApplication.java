package dk.jyskit.salescloud.application;

import lombok.extern.slf4j.Slf4j;

import org.apache.wicket.Application;
import org.apache.wicket.Page;

import com.google.inject.Inject;
import com.google.inject.Injector;

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;
import de.agilecoders.wicket.core.settings.ITheme;
import dk.jyskit.salescloud.application.pages.admin.useradmin.UsersPage;
import dk.jyskit.salescloud.application.pages.auth.LoginPage;
import dk.jyskit.salescloud.application.pages.base.themes.magicbootstrap.TdcTheme;
import dk.jyskit.waf.application.JITAuthenticatedWicketApplication;

/**
 * Base Wicket Application class for salescloud projects. One reason for having
 * this class is to have standard localization.
 */
@Slf4j
public abstract class CoreApplication extends JITAuthenticatedWicketApplication {
    /**
     * Get Application for current thread.
     *
     * @return The current thread's Application
     */
    public static CoreApplication get() {
        return (CoreApplication) Application.get();
    }

    /**
     * Constructor.
     */
    @Inject
    public CoreApplication(Injector injector) {
		super(injector);
    }

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init() {
        super.init();
        
        getApplicationSettings().setPageExpiredErrorPage(LoginPage.class);
	}

	@Override
	public ITheme createNonAdminTheme() {
		return new TdcTheme();
	}

	@Override
	public ITheme createAdminTheme() {
		return new TdcTheme();
	}

	/* 
	 * Don't forget to call this in subclasses
	 */
	@Override
	public void mountPages() {
        // non-admin pages
        mountPage("login", LoginPage.class);

        // admin pages
        mountPage("admin/users", UsersPage.class);
	}

	@Override
	public Navbar createAdminNavbar(String markupId, Page page) {
		return null; // override me
	}

	@Override
	protected void configureResourceBundles() {
	}
}
