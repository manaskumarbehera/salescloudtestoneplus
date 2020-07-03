package dk.jyskit.salescloud.application.pages.auth;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.BootstrapBaseBehavior;
import dk.jyskit.salescloud.application.CoreApplication;
import dk.jyskit.salescloud.application.pages.base.themes.magicbootstrap.TdcTheme;
import dk.jyskit.waf.application.Environment;
import dk.jyskit.waf.application.JITAuthenticatedWicketApplication;
import dk.jyskit.waf.application.components.login.username.UsernameLoginPanel;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.image.Image;

@SuppressWarnings("serial")
public class LoginPage extends WebPage {
	public LoginPage() {
        add(new BootstrapBaseBehavior());  // Add admin theme
        add(new HeaderResponseContainer("footer-container", "footer-container"));  // For JS to be added
		add(new Image("logo", (CoreApplication.get().getSetting(Environment.WAF_ENV).equals("staging") ? TdcTheme.LOGO_STAGING_REFERENCE : TdcTheme.LOGO_REFERENCE)));
		add(new UsernameLoginPanel("login"));
	}
	
	@Override
	protected void onConfigure() {
		JITAuthenticatedWicketApplication.get().useAdminTheme();
	}
}
