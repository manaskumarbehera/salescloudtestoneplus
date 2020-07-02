package dk.jyskit.waf.application.pages.nonadmin.auth;

import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.html.WebPage;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.BootstrapBaseBehavior;
import dk.jyskit.waf.application.JITAuthenticatedWicketApplication;
import dk.jyskit.waf.application.components.login.LoginAuxErrorProvider;
import dk.jyskit.waf.application.components.login.email.EmailLoginPanel;
import dk.jyskit.waf.application.components.login.username.UsernameLoginPanel;
import dk.jyskit.waf.application.model.BaseUser;

@SuppressWarnings("serial")
public class LoginPage extends WebPage {
	public LoginPage() {
        add(new BootstrapBaseBehavior());  // Add admin theme
        add(new HeaderResponseContainer("footer-container", "footer-container"));  // For JS to be added
        
//    	LoginAuxErrorProvider auxErrorProvider = new LoginAuxErrorProvider() {
//    		@Override
//    		public String evaluateUser(BaseUser user) {
//    			if (user.isAuthenticatedBy("Slettet")) {
//    				return "auth.error.userNotFound";
//    			}
//    			if (user.isAuthenticatedBy("Passiv")) {
//    				return "auth.error.userNotFound";
//    			}
//    			if (user.isAuthenticatedBy("Deleted")) {
//    				return "auth.error.userNotFound";
//    			}
//    			if (user.isAuthenticatedBy("Passive")) {
//    				return "auth.error.userNotFound";
//    			}
//    			return null;
//    		}
//    	};
    	
		if (JITAuthenticatedWicketApplication.get().isUseEmailForLogin()) {
			add(new EmailLoginPanel("login"));
		} else {
			add(new UsernameLoginPanel("login"));
		}
	}
	
	@Override
	protected void onConfigure() {
		JITAuthenticatedWicketApplication.get().useAdminTheme();
	}
}
