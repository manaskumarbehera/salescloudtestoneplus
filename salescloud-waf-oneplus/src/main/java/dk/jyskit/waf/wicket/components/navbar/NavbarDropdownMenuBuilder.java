package dk.jyskit.waf.wicket.components.navbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.MenuBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.MenuDivider;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.MenuHeader;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarDropDownButton;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.button.DropDownAutoOpen;

/**
 * Builder to build wicket-botstrap navbar dropdown menus.
 * Observe this class must be {@link Serializable} in order to hold the subMenu items, which is used in the constructor of {@link NavbarDropDownButton}.
 * @author m43634
 *
 */
public class NavbarDropdownMenuBuilder implements Serializable {

	private IModel<String> headerModel = Model.of("");
	private List<AbstractLink> subMenu = new ArrayList<>();
	private IconType iconType;

	public NavbarDropdownMenuBuilder(String headerKey) {
		this(new ResourceModel(headerKey));
	}

	public NavbarDropdownMenuBuilder(IModel<String> headerModel) {
		super();
		this.headerModel = headerModel;
	}

	public NavbarDropDownButton build() {
		NavbarDropDownButton menu = new NavbarDropDownButton(headerModel) {

			@Override
			protected List<AbstractLink> newSubMenuButtons(String buttonMarkupId) {
				return subMenu;
			}
		};
		if (iconType != null) {
			menu.setIconType(iconType);
		}
		menu.add(new DropDownAutoOpen());
		return menu;
	}

	public NavbarDropdownMenuBuilder addLink(AbstractLink link) {
		subMenu.add(link);
		return this;
	}

	public NavbarDropdownMenuBuilder addPageMenu(IModel<String> label, Class<? extends Page> pageClass) {
		addLink(new MenuBookmarkablePageLink<Page>(pageClass, label));
		return this;
	}

	public NavbarDropdownMenuBuilder addPageMenu(IModel<String> label, Class<? extends Page> pageClass, PageParameters parameters) {
		addLink(new MenuBookmarkablePageLink<Page>(pageClass, parameters, label));
		return this;
	}

	public NavbarDropdownMenuBuilder addPageMenu(String resourceKey, Class<? extends Page> pageClass) {
		return addPageMenu(new ResourceModel(resourceKey), pageClass);
	}

	public NavbarDropdownMenuBuilder addPageMenu(String resourceKey, Class<? extends Page> pageClass, PageParameters parameters) {
		return addPageMenu(new ResourceModel(resourceKey), pageClass, parameters);
	}

	public NavbarDropdownMenuBuilder addPageMenu(IModel<String> label, Class<? extends Page> pageClass, IconType iconType) {
		addLink(new MenuBookmarkablePageLink<Page>(pageClass, label).setIconType(iconType));
		return this;
	}

	public NavbarDropdownMenuBuilder addPageMenu(IModel<String> label, Class<? extends Page> pageClass, PageParameters parameters, IconType iconType) {
		addLink(new MenuBookmarkablePageLink<Page>(pageClass, parameters, label).setIconType(iconType));
		return this;
	}

	public NavbarDropdownMenuBuilder addPageMenu(String resourceKey, Class<? extends Page> pageClass, IconType iconType) {
		return addPageMenu(new ResourceModel(resourceKey), pageClass, iconType);
	}

	public NavbarDropdownMenuBuilder addPageMenu(String resourceKey, Class<? extends Page> pageClass, PageParameters parameters, IconType iconType) {
		return addPageMenu(new ResourceModel(resourceKey), pageClass, parameters, iconType);
	}

	public NavbarDropdownMenuBuilder addDivider() {
		addLink(new MenuDivider());
		return this;
	}


	public NavbarDropdownMenuBuilder addHeader(String headerKey) {
		addLink(new MenuHeader(new ResourceModel(headerKey)));
		return this;
	}

	public NavbarDropdownMenuBuilder addHeader(IModel<String> headerModel) {
		addLink(new MenuHeader(headerModel));
		return this;
	}

	public NavbarDropdownMenuBuilder iconType(IconType iconType) {
		this.iconType = iconType;
		return this;
	}

}
