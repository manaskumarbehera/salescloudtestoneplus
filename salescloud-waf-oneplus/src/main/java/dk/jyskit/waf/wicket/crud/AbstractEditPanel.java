package dk.jyskit.waf.wicket.crud;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.Breadcrumb;
import dk.jyskit.waf.application.dao.DaoHelper;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxSubmitListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.EntityLabelStrategy;
import dk.jyskit.waf.wicket.utils.WicketUtils;

/**
 * @author jan
 *
 * @param <C> Child class
 * @param <P> Parent class
 */
public abstract class AbstractEditPanel<C extends Serializable, P> extends CrudPanel {

	public final static String FORM_ID = "jsr303form"; 

	protected IModel<C> origChildModel;
	protected IModel<C> childModel;
	protected IModel<P> parentModel;
	protected String breadCrumbText;
	protected boolean newObject;

	protected AjaxButton saveButton;
	protected AjaxButton cancelButton;

	public AbstractEditPanel(CrudContext context, IModel<C> childModel) {
		this(context, childModel, null);
	}
	
	public AbstractEditPanel(CrudContext context, IModel<C> childModel, IModel<P> parentModel) {
		super(context);
		if (childModel == null) {
			this.origChildModel = createChildModel();
			newObject = true;
		} else {
			this.origChildModel = childModel;
		}
		this.childModel = childModel;
		this.parentModel = parentModel;
	}
	
	public abstract IModel<C> createChildModel();

	@Override
	protected void onInitialize() {
		super.onInitialize();

		// Cloning the entity is optional
		C clonedChild = clone(origChildModel.getObject());
		if (clonedChild == null) {
			childModel = origChildModel;
		} else {
			childModel = new Model(clonedChild);
		}
		
		String mode = getMode();
		if (mode == null) {
			mode = (newObject ? "new" : "edit");
		}
		labelKey(context.getNamespace() + "." + mode + ".caption");
		
		breadCrumbText = (newObject ? WicketUtils.getLocalized(context.getNamespace() + ".breadcrumb.new", "") : getBreadCrumbText(childModel.getObject()));

		final Jsr303Form<C> form = new Jsr303Form<C>(FORM_ID, childModel, false);
		form.setLabelStrategy(new EntityLabelStrategy(context.getNamespace()));
		add(form);

		addFormFields(form);

		AjaxSubmitListener onSaveListener = new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				if (onSave(form, target)) {
					goBack(target);
				}
			}
		};
		
		addButtons(form, onSaveListener);
//		context.addToBreadCrumb(this);
	}

	protected void addButtons(final Jsr303Form<C> form, AjaxSubmitListener onSaveListener) {
		if (useIndicatorForSaveButton()) {
			saveButton = form.addIndicatingSubmitButton("save", Buttons.Type.Primary, onSaveListener);
		} else {
			saveButton = form.addSubmitButton("save", Buttons.Type.Primary, onSaveListener);
		}
		
		cancelButton = form.addButton("cancel", Buttons.Type.Default, new AjaxEventListener() {
			@Override
			public void onAjaxEvent(AjaxRequestTarget target) {
				if (onCancel(form, target)) {
					goBack(target);
				}
			}
		});
	}

	protected boolean useIndicatorForSaveButton() {
		return false;
	}

	/**
	 * In some cases, we need to do avoid working directly on entities.
	 * 
	 * @param entity
	 * @return
	 */
	protected C clone(C entity) {
		return null;
	}

	protected String getMode() {
		return null;
	}

	protected boolean onSave(final Jsr303Form<C> form, AjaxRequestTarget target) {
		if (prepareSave(childModel.getObject())) {
			if ((parentModel == null) || (parentModel.getObject() == null)) {
				if (!save(origChildModel.getObject(), form)) {
					return false;  
				}
			} else {
				if (isNewObject()) {
					P parent = parentModel.getObject();
					if (!addToParentAndSave(parent, origChildModel.getObject())) {
						return false;  
					}
				} else {
					if (!save(origChildModel.getObject(), form)) {
						return false;  
					}
				}
			}
			return true;
		}
		DaoHelper.setRollbackOnly();
		return false;
	}
	
	/**
	 * This is the place to do stuff that needs to be done, no matter if the entity
	 * is new or an existing one.
	 * Also, very importantly, if you previously clone the entity, in order not to work on it 
	 * directly, this is where you must copy values back to the original.
	 * 
	 * @param entity
	 * @return true if everything is ok
	 */
	protected boolean prepareSave(C entity) {
		return true;
	}
	
	protected boolean onCancel(final Jsr303Form<C> form, AjaxRequestTarget target) {
		return true;
	}
	
	protected void goBack(AjaxRequestTarget target) {
		if (getBreadCrumbModel() == null) {
			// ok?
			Panel panel = new EmptyPanel(context.getPanelWicketId());
			panel.setOutputMarkupId(true);
			context.getRootMarkupContainer().addOrReplace(panel);
			target.add(panel);
		} else {
			List<IBreadCrumbParticipant> participants = getBreadCrumbModel().allBreadCrumbParticipants();
			if (participants.size() == 1) {
				// No ancestor
				Panel panel = new EmptyPanel(context.getPanelWicketId());
				panel.setOutputMarkupId(true);
				context.getRootMarkupContainer().addOrReplace(panel);
				target.add(panel);
			} else {
				IBreadCrumbParticipant breadCrumbParticipant = participants.get(participants.size()-2);
				getBreadCrumbModel().setActive(breadCrumbParticipant);
				context.getRootMarkupContainer().addOrReplace(breadCrumbParticipant.getComponent());
				target.add(breadCrumbParticipant.getComponent());
				target.add((Breadcrumb) getBreadCrumbModel());
			}
		}
	}

	@Override
	public IModel<String> getBreadCrumbText() {
		return Model.of(breadCrumbText);
	}

	public String getBreadCrumbText(C entity) {
		return entity.toString();
	}
	
	public abstract boolean save(C entity, Jsr303Form<C> form);

	public abstract boolean addToParentAndSave(P parent, C child);

	public abstract void addFormFields(final Jsr303Form<C> form);

	public boolean isNewObject() {
		return newObject;
	}

	public void setNewObject(boolean newObject) {
		this.newObject = newObject;
	}
}
