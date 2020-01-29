package dk.jyskit.waf.wicket.crud;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions.EntityAction;

public abstract class CrudEntityAction<T extends Serializable> extends EntityAction<T> {
	
	private CrudContext context;

	public CrudEntityAction(CrudContext context, String textKey, String tooltipKey, IconType iconType) {
		super(textKey, tooltipKey, iconType);
		this.context = context;
	}

	@Override
	public void onClick(IModel<T> model, AjaxRequestTarget target) {
		Panel panel = createPanel(context, model);
		context.getRootMarkupContainer().addOrReplace(panel);
		target.add(panel);
		if (panel instanceof CrudPanel) {
			((CrudPanel) panel).onAjax(target);
		}
		if (context.getBreadcrumb() != null) {
			target.add(context.getBreadcrumb());
		}
	}

	public abstract Panel createPanel(CrudContext context, IModel<T> model);
}
