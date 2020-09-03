package dk.jyskit.waf.wicket.components.forms.jsr303form;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormType;
import dk.jyskit.waf.application.Environment;
import dk.jyskit.waf.wicket.behaviors.KeepAliveBehavior;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.DefaultLabelStrategy;

/**
 *
 * The form is based on Twitter Bootstrap forms - horizontal layout.
 *
 * @author jan
 *
 * @param <T>
 */
public class Jsr303Form<T extends Serializable> extends ComponentContainerPanel<T> {
	private WebMarkupContainer dialogContainer;
	private NotificationPanel notificationPanel;

	/**
	 * @param id
	 * @param bean
	 */
	public Jsr303Form(String id, T bean) {
		this(id, new Model<T>(bean), Environment.useAjaxValidate());
	}

	/**
	 * @param id
	 * @param beanModel
	 */
	public Jsr303Form(String id, IModel<T> beanModel) {
		this(id, beanModel, Environment.useAjaxValidate());
	}

	/**
	 * @param id
	 * @param bean
	 * @param ajaxValidate
	 */
	public Jsr303Form(String id, T bean, boolean ajaxValidate) {
		this(id, new Model<T>(bean), ajaxValidate);
	}

	/**
	 * @param id
	 * @param beanModel
	 * @param ajaxValidate
	 */
	public Jsr303Form(String id, IModel<T> beanModel, boolean ajaxValidate) {
		super(id, beanModel, ajaxValidate);

		setRenderBodyOnly(true);

		BootstrapForm<T> bootstrapForm = new BootstrapForm<T>("form", new CompoundPropertyModel<T>(beanModel)) {
			protected void onError() {
				super.onError();
			};

			protected void onSubmit() {
				super.onSubmit();
			};
		};

		bootstrapForm.type(FormType.Horizontal);
		form = bootstrapForm;
		add(form);
		setLabelStrategy(new DefaultLabelStrategy(getNameSpace()));

		notificationPanel = new NotificationPanel("feedback");
		notificationPanel.hideAfter(Duration.seconds(30));
		form.add(notificationPanel.setOutputMarkupId(true));
		form.add(getBeforeButtonsPanelsView());
		form.add(getButtonOrLinkPanels());
		form.add(getAfterButtonsPanelsView());

		form.add(new KeepAliveBehavior());

		dialogContainer = new WebMarkupContainer("dialogContainer");
		add(dialogContainer);

		dialogContainer.add(new Modal("dialog"));
	}

	public NotificationPanel getNotificationPanel() {
		return notificationPanel;
	}

	public WebMarkupContainer getDialogContainer() {
		return dialogContainer;
	}

	/**
	 * Add a form group. You must add localization of form group name in the
	 * page properties file:
	 * <code>form-id.group.group-name = localized name</code>
	 *
	 * @param groupNameKey
	 * @return
	 */
	public FormGroup<T> createGroup(String groupNameKey) {
		return createGroup(getLabelStrategy().groupLabel(groupNameKey));
	}

	public FormGroup<T> createGroup(IModel<String> titleModel) {
		boolean first = true;
		for (Iterator<Panel> iterator = beforeButtonsPanels.iterator(); iterator.hasNext();) {
			if (iterator.next() instanceof FormGroup) {
				first = false;
				break;
			}
		}
		FormGroup<T> group = new FormGroup(form, beanModel,	titleModel, first, isAjaxValidate(), getLabelStrategy());
		beforeButtonsPanels.add(group);
		group.setParentContainer(this);
		return group;
	}

	public void setBean(T bean) {
		beanModel = new Model<T>(bean);
	}
}
