package dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.model.IModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
/**
 * EntityAction is primarily used for {@link MultiActionsColumn}.
 * The instantiator of the corresponding link or button is responsible for setting the auttorization roles on the that component.
 * @author Jan, Palfred
 *
 * @param <T>
 */
public abstract class EntityAction<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String tooltipKey;
	private final String textKey;
	private final IconType iconType;
	private Roles authorizedRoles = new Roles();

	public EntityAction(String textKey, String tooltipKey, IconType iconType) {
		this.textKey	= textKey;
		this.tooltipKey	= tooltipKey;
		this.iconType	= iconType;
	}

	public boolean isEnabled(IModel<T> model) {
		return true;
	}

	public String getTooltipKey(IModel<T> model) {
		return tooltipKey;
	}

	public String getTextKey(IModel<T> model) {
		return textKey;
	}

	public IconType getIconType(IModel<T> model) {
		return iconType;
	}

	public abstract void onClick(IModel<T> model, AjaxRequestTarget target);

	public EntityAction<T> authorize(Roles roles) {
		this.authorizedRoles = roles;
		return this;
	}

	public EntityAction<T> authorize(String... roles) {
		for (String role : roles) {
			authorizedRoles.add(role);
		}
		return this;
	}

	public Roles getAuthorizedRoles() {
		return authorizedRoles;
	}

	public void setAuthorizedRoles(Roles authorizedRoles) {
		this.authorizedRoles = authorizedRoles;
	}

}
