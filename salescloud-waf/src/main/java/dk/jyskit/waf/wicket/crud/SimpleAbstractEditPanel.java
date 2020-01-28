package dk.jyskit.waf.wicket.crud;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.Breadcrumb;
import dk.jyskit.waf.application.model.BaseEntity;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxSubmitListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.EntityLabelStrategy;

/**
 * @author jan
 *
 * @param <T> Entity class
 * @param <P> Parent entity class
 * @deprecated USe {@link AbstractEditPanel} which not limited by {@link BaseEntity}, but you need to supply the entity as a model in place of {@link #initEntity(Long)}
 */
public abstract class SimpleAbstractEditPanel<T extends BaseEntity, P> extends CrudPanel {
	
	public final static String FORM_ID = "jsr303form"; 

	protected T entity;
	protected IModel<P> parentModel;
	private String namespace;
	private Long entityId;
	
	public SimpleAbstractEditPanel(CrudContext context, Long entityId, final IModel<P> parentModel, String namespace) {
		super(context);
		this.entityId = entityId;
		this.parentModel = parentModel;
		this.namespace = namespace;
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		labelKey(namespace + ".edit.entity");
		
		entity = initEntity(entityId);

		final Jsr303Form<T> form = new Jsr303Form<T>(FORM_ID, entity, false);
		form.setLabelStrategy(new EntityLabelStrategy(namespace));
		add(form);
		
		addFormFields(form);
		
		form.addSubmitButton("save", Buttons.Type.Primary, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				if (prepareSave(entity)) {
					if ((parentModel == null) || (parentModel.getObject() == null)) {
						if (!save(entity, form)) {
							return;  // Stay on form
						}
					} else {
						if (entity.isNewObject()) {
							P parent = parentModel.getObject();
							if (!addToParentAndSave(parent, entity)) {
								return;  // Stay on form
							}
						} else {
							if (!save(entity, form)) {
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
		return (entity.isNewObject() ? new StringResourceModel(namespace + ".breadcrumb.new", this, getDefaultModel()) : Model.of(getBreadCrumbText(entity)));
	}

	public String getBreadCrumbText(T entity) {
		return entity.toString();
	}

	public abstract T initEntity(Long entityId);

	public abstract boolean prepareSave(T entity);
	
	public abstract boolean save(T entity, Jsr303Form<T> form);
	
	public abstract boolean addToParentAndSave(P parent, T entity);
	
	public abstract void addFormFields(final Jsr303Form<T> form);
}
