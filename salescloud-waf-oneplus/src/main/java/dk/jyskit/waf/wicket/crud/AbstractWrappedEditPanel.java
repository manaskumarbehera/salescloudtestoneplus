package dk.jyskit.waf.wicket.crud;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.Breadcrumb;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxSubmitListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.EntityLabelStrategy;

/**
 * @author jan
 *
 * @param <C> Child class
 * @param <W> Class for wrapper of child object
 * @param <P> Parent class
 */
public abstract class AbstractWrappedEditPanel<C extends Serializable, W extends Serializable, P> extends CrudPanel {

	public final static String FORM_ID = "jsr303form"; 

	protected IModel<C> childModel;
	protected IModel<P> parentModel;
	private boolean newObject;

	private W wrappedChild;

	public AbstractWrappedEditPanel(CrudContext context, IModel<C> childModel) {
		this(context, childModel, null);
	}
	
	public AbstractWrappedEditPanel(CrudContext context, IModel<C> childModel, IModel<P> parentModel) {
		super(context);
		if (childModel == null) {
			this.childModel = createChildModel();
			newObject = true;
		} else {
			this.childModel = childModel;
		}
		this.parentModel = parentModel;
	}
	
	public abstract IModel<C> createChildModel();

	public abstract W wrapChild(C child);

	@Override
	protected void onInitialize() {
		super.onInitialize();

		if (newObject) {
			labelKey(context.getNamespace() + ".new.caption");
		} else {
			labelKey(context.getNamespace() + ".edit.caption");
		}

		wrappedChild = wrapChild(childModel.getObject());
		
		final Jsr303Form<W> form = new Jsr303Form<W>(FORM_ID, getWrappedChild(), false);
		
		form.setLabelStrategy(new EntityLabelStrategy(context.getNamespace()));
		add(form);

		addFormFields(form);

		form.addSubmitButton("save", Buttons.Type.Primary, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				if (prepareSave(getWrappedChild())) {
					if ((parentModel == null) || (parentModel.getObject() == null)) {
						if (!save(getWrappedChild(), form, target)) {
							return;  // Stay on form
						}
					} else {
						if (isNewObject()) {
							P parent = parentModel.getObject();
							if (!addToParentAndSave(parent, getWrappedChild())) {
								return;  // Stay on form
							}
						} else {
							if (!save(getWrappedChild(), form, target)) {
								return;  // Stay on form
							}
						}
					}
					goBack(target);
				}
			}
		});
		form.addButton("cancel", Buttons.Type.Default, new AjaxEventListener() {
			@Override
			public void onAjaxEvent(AjaxRequestTarget target) {
				goBack(target);
			}
		});

//		context.addToBreadCrumb(this);
	}

	private void goBack(AjaxRequestTarget target) {
		List<IBreadCrumbParticipant> participants = getBreadCrumbModel().allBreadCrumbParticipants();
		IBreadCrumbParticipant breadCrumbParticipant = participants.get(participants.size()-2);
		getBreadCrumbModel().setActive(breadCrumbParticipant);
		context.getRootMarkupContainer().addOrReplace(breadCrumbParticipant.getComponent());
		target.add(breadCrumbParticipant.getComponent());
		target.add((Breadcrumb) getBreadCrumbModel());
	}

	@Override
	public IModel<String> getBreadCrumbText() {
		return (isNewObject() ? new StringResourceModel(context.getNamespace() + ".breadcrumb.new", this, getDefaultModel()) : Model.of(getBreadCrumbText(childModel.getObject())));
	}

	public String getBreadCrumbText(C entity) {
		return entity.toString();
	}

	public abstract boolean prepareSave(W wrappedChild);
	
	public abstract boolean save(W wrappedChild, Jsr303Form<W> form, AjaxRequestTarget target);

	public abstract boolean addToParentAndSave(P parent, W wrappedChild);

	public abstract void addFormFields(final Jsr303Form<W> form);

	public boolean isNewObject() {
		return newObject;
	}

	public void setNewObject(boolean newObject) {
		this.newObject = newObject;
	}

	public W getWrappedChild() {
		return wrappedChild;
	}
}
