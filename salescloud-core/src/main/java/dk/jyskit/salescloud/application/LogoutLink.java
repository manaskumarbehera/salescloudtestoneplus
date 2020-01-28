package dk.jyskit.salescloud.application;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type;
import dk.jyskit.waf.application.JITAuthenticatedWicketApplication;
import dk.jyskit.waf.wicket.security.UserSession;

@SuppressWarnings("serial")
public final class LogoutLink extends BootstrapAjaxLink<String> {
	public LogoutLink(String id, IModel<String> model, Type type) {
		super(id, model, type, model);
	}

	@Override
	public void onClick(AjaxRequestTarget target) {
		UserSession.get().setUser(null);
		UserSession.get().invalidate();
		// Normally you would do this:
		// setResponsePage(WicketApplication.get().getHomePage());
		// But in this application, I don't use any non-admin pages (except the login page)
		setResponsePage(JITAuthenticatedWicketApplication.get().getLoginPage());
	}
}