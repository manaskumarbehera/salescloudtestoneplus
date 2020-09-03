package dk.jyskit.waf.wicket.crud;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import dk.jyskit.waf.wicket.components.widget.WidgetBreadCrumbPanel;

public abstract class CrudPanel extends WidgetBreadCrumbPanel {

	protected CrudContext context;

	/**
	 * Construct.
	 * 
	 * @param id
	 *            Component id
	 * @param breadCrumbModel
	 *            The bread crumb model
	 */
	public CrudPanel(CrudContext context) {
		super(context.getPanelWicketId(), context.getBreadcrumb());
		this.context = context;
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 *            Component id
	 * @param breadCrumbModel
	 *            The bread crumb model
	 * @param model
	 *            The model
	 */
	public CrudPanel(CrudContext context, final IModel<?> model) {
		super(context.getPanelWicketId(), context.getBreadcrumb(), model);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		context.addToBreadCrumb(this);
	}
	
	@Override
	public IModel<String> getTitle() {
		return getBreadCrumbText();
	}
	
    public void onAjax(AjaxRequestTarget target) {
    }

	public abstract IModel<String> getBreadCrumbText();
}
