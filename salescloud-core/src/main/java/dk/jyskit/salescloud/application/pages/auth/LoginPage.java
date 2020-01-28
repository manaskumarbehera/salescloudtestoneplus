package dk.jyskit.salescloud.application.pages.auth;

import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.image.Image;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.BootstrapBaseBehavior;
import dk.jyskit.salescloud.application.CoreApplication;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.pages.base.themes.magicbootstrap.TdcTheme;
import dk.jyskit.waf.application.Environment;
import dk.jyskit.waf.application.JITAuthenticatedWicketApplication;
import dk.jyskit.waf.application.components.login.LoginAuxErrorProvider;
import dk.jyskit.waf.application.components.login.username.UsernameLoginPanel;
import dk.jyskit.waf.application.model.BaseUser;

@SuppressWarnings("serial")
public class LoginPage extends WebPage {
	public LoginPage() {
        add(new BootstrapBaseBehavior());  // Add admin theme
        add(new HeaderResponseContainer("footer-container", "footer-container"));  // For JS to be added

		add(new Image("logo", (CoreApplication.get().getSetting(Environment.WAF_ENV).equals("staging") ? TdcTheme.LOGO_STAGING_REFERENCE : TdcTheme.LOGO_REFERENCE)));
        
		add(new UsernameLoginPanel("login", new LoginAuxErrorProvider() {
			@Override
			public String evaluateUser(BaseUser user) {
				if (user.isAuthenticatedBy("Slettet")) {
					return "auth.error.userNotFound";
				}
				if (user.isAuthenticatedBy("Passiv")) {
					return "auth.error.userNotFound";
				}
				return null;
			}
		}));
	}
	
	@Override
	protected void onConfigure() {
		JITAuthenticatedWicketApplication.get().useAdminTheme();
	}
}
