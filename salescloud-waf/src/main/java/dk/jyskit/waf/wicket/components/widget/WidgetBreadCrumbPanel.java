package dk.jyskit.waf.wicket.components.widget;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbParticipantDelegate;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.model.IModel;

import dk.jyskit.waf.wicket.components.widget.WidgetPanel;

/**
 * A "breadcrumb-aware" variant of WidgetPanel.
 * 
 * @author jan
 */
public abstract class WidgetBreadCrumbPanel extends WidgetPanel implements IBreadCrumbParticipant {
	private static final long serialVersionUID = 1L;
	
	/** The bread crumb model. */
	private IBreadCrumbModel breadCrumbModel;

	/**
	 * Implementation of the participant.
	 */
	private final IBreadCrumbParticipant decorated = new BreadCrumbParticipantDelegate(this)
	{
		private static final long serialVersionUID = 1L;

		@Override
		public IModel<String> getTitle()
		{
			return WidgetBreadCrumbPanel.this.getTitle();
		}
	};

	/**
	 * Construct.
	 * 
	 * @param id
	 *            Component id
	 * @param breadCrumbModel
	 *            The bread crumb model
	 */
	public WidgetBreadCrumbPanel(final String id, final IBreadCrumbModel breadCrumbModel)
	{
		super(id);
		this.breadCrumbModel = breadCrumbModel;
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
	public WidgetBreadCrumbPanel(final String id, final IBreadCrumbModel breadCrumbModel,
		final IModel<?> model)
	{
		super(id, model);
		this.breadCrumbModel = breadCrumbModel;
	}

	/**
	 * Activates the {@link BreadCrumbPanel bread crumb panel} that is the result of calling
	 * {@link IBreadCrumbPanelFactory#create(String, IBreadCrumbModel) the create method} of the
	 * bread crumb panel factory.
	 * 
	 * @param breadCrumbPanelFactory
	 */
	public void activate(final IBreadCrumbPanelFactory breadCrumbPanelFactory)
	{
		activate(breadCrumbPanelFactory.create(getId(), breadCrumbModel));
	}

	/**
	 * Activates the provided participant, which typically has the effect of replacing this current
	 * panel with the one provided - as the participant typically would be a {@link BreadCrumbPanel
	 * bread crumb panel} - and updating the bread crumb model of this panel, pushing the bread
	 * crumb for the given participant on top.
	 * 
	 * @param participant
	 *            The participant to set as the active one
	 */
	public void activate(final IBreadCrumbParticipant participant)
	{
		// get the currently active participant
		final IBreadCrumbParticipant active = breadCrumbModel.getActive();
		if (active == null)
		{
			throw new IllegalStateException("The model has no active bread crumb. Before using " +
				this + ", you have to have at least one bread crumb in the model");
		}

		// add back button support
		addStateChange();

		// set the bread crumb panel as the active one
		breadCrumbModel.setActive(participant);
	}

	/**
	 * Gets the bread crumb panel.
	 * 
	 * @return The bread crumb panel
	 */
	public final IBreadCrumbModel getBreadCrumbModel()
	{
		return breadCrumbModel;
	}

	/**
	 * The participating component == this.
	 * 
	 * @see org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant#getComponent()
	 */
	@Override
	public Component getComponent()
	{
		return decorated.getComponent();
	}

	/**
	 * @see org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant#onActivate(org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant)
	 */
	@Override
	public void onActivate(final IBreadCrumbParticipant previous)
	{
		decorated.onActivate(previous);
	}

	/**
	 * Sets the bread crumb panel.
	 * 
	 * @param breadCrumbModel
	 *            The bread crumb panel
	 */
	public final void setBreadCrumbModel(final IBreadCrumbModel breadCrumbModel)
	{
		this.breadCrumbModel = breadCrumbModel;
	}
}
