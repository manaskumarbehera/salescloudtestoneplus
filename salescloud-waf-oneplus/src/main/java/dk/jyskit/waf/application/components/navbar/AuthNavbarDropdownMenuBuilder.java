package dk.jyskit.waf.application.components.navbar;

import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton;
import dk.jyskit.waf.wicket.components.navbar.NavbarDropdownMenuBuilder;
import dk.jyskit.waf.wicket.security.IAuthModel;

/**
 * Extension of NavbarDropdownMenuBuilder which does not add menu items for pages the user
 * is not authorized to access.
 * 
 * @author jan
 */
public class AuthNavbarDropdownMenuBuilder extends NavbarDropdownMenuBuilder {

	private IAuthModel user;
	private boolean hasSubItems = false;

	public AuthNavbarDropdownMenuBuilder(String headerKey, IAuthModel user) {
		super(headerKey);
		this.user = user;
	}
	
	public AuthNavbarDropdownMenuBuilder(IModel<String> headerModel, IAuthModel user) {
		super(headerModel);
		this.user = user;
	}

	public NavbarDropdownMenuBuilder addAuthorizedPageMenu(String resourceKey, Class<? extends Page> pageClass) {
		return addAuthorizedPageMenu(resourceKey, pageClass, null);
	}
	
	public NavbarDropdownMenuBuilder addAuthorizedPageMenu(String resourceKey, Class<? extends Page> pageClass, IconType iconType) {
		AuthorizeInstantiation authAnnotation = pageClass.getAnnotation(AuthorizeInstantiation.class);
		if ((authAnnotation != null) && (user != null)) {
			if (user.hasAnyRole(new Roles(authAnnotation.value()))) {
				hasSubItems = true;
				if (iconType == null) {
					return addPageMenu(new ResourceModel(resourceKey), pageClass);
				} else {
					return addPageMenu(new ResourceModel(resourceKey), pageClass, iconType);
				}
			}
		}
		return null;
	}
	
	/**
	 * @see NavbarButton#NavbarButton(Class, IModel)
	 */
	
	public static <T extends Page> NavbarButton<T> navbarPageLink(final Class<T> pageClass, final String labelKey) {
		return new NavbarButton<T>(pageClass, new ResourceModel(labelKey));
	}

	public boolean hasSubItems() {
		return hasSubItems;
	}
}