package dk.jyskit.waf.application.components.navbar;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

import com.google.common.base.Function;

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.INavbarComponent;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.ImmutableNavbarComponent;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarDropDownButton;
import de.agilecoders.wicket.jquery.util.Generics2;
import dk.jyskit.waf.wicket.security.IAuthModel;

/**
 * Extension of Navbar which does not add menu items for pages the user
 * is not authorized to access.
 * 
 * @author jan
 */
public class AuthNavbar extends Navbar {
	private IAuthModel user;

	public AuthNavbar(String componentId, IAuthModel user) {
		super(componentId);
		this.user = user;
	}

	public Navbar addAuthorizedComponents(final Navbar.ComponentPosition position, Component ... components) {
		List<Component> authorizedComponents = Generics2.newArrayList();
		for (Component comp : components) {
			if (comp == null) {
				continue;
			}
			if (comp instanceof NavbarDropDownButton) {
				if (((NavbarDropDownButton) comp).size() == 0) {
					continue;
				}
			}
			if (comp instanceof NavbarButton) {
				AuthorizeInstantiation authAnnotation = 
						(AuthorizeInstantiation) ((NavbarButton) comp).getPageClass().getAnnotation(AuthorizeInstantiation.class);
				if (authAnnotation != null) {
					Roles allowedRoles = new Roles(authAnnotation.value());
					if (!user.hasAnyRole(allowedRoles)) {
						continue;
					}
				}
			}
			authorizedComponents.add(comp);
		}
        return addComponents(Generics2.transform(authorizedComponents, new Function<Component, INavbarComponent>() {
            @Override
            public INavbarComponent apply(Component component) {
                return new ImmutableNavbarComponent(component, position);
            }
        }));
	}
	
	

}
