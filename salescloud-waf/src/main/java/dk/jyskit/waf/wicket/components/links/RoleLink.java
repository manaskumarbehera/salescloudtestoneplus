package dk.jyskit.waf.wicket.components.links;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type;
import dk.jyskit.waf.application.JITAuthenticatedWicketApplication;
import dk.jyskit.waf.wicket.security.UserSession;

@SuppressWarnings("serial")
public final class RoleLink extends BootstrapAjaxLink<String> {
	private Class roleClass;

	public RoleLink(String id, IModel<String> model, Type type, Class roleClass) {
		super(id, model, type);
		this.roleClass = roleClass;
	}

	@Override
	public void onClick(AjaxRequestTarget target) {
		UserSession.get().setActiveRoleClass(roleClass);
		setResponsePage(JITAuthenticatedWicketApplication.get().getAdminHomePage());
	}
}