package dk.jyskit.waf.wicket.components.panels.confirmation;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;


import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Alert;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.util.Attributes;
import dk.jyskit.waf.wicket.components.panels.modal.ModalContainer;

@Slf4j
public abstract class ModalConfirmationPanel extends Modal {
	private NotificationPanel notificationPanel;
	private Label yesLabel;
	private Label noLabel;
	public NotificationPanel getNotificationPanel() {
		return notificationPanel;
	}

	public ModalConfirmationPanel(IModel<String> titleModel, IModel<String> textModel) {
		this(titleModel, textModel, true);
	}

	public ModalConfirmationPanel(IModel<String> titleModel, IModel<String> textModel, final boolean isConfirm) {
		super(isConfirm ? ModalContainer.CONFIRM_ID : ModalContainer.MODAL_ID);
		setUseKeyboard(false);
		add(AttributeModifier.replace("data-backdrop", "static"));

		show(true);
//		setFadeIn(false);
		setHeaderVisible(true);
		setFooterVisible(false);

		setOutputMarkupId(true);

		notificationPanel = new NotificationPanel("notificationPanel") {
			@Override
			protected Component newMessageDisplayComponent(String markupId, FeedbackMessage message) {
				Component msgComp = super.newMessageDisplayComponent(markupId, message);
				if (msgComp instanceof Alert) {
					Alert alert = (Alert) msgComp;
					alert.setCloseButtonVisible(isConfirm);
				}
				return msgComp;
			}
		};
		notificationPanel.setOutputMarkupId(true);
		add(notificationPanel);
		add(new Label("text", textModel).setEscapeModelStrings(false));

		// Using Wicket-bootstrap buttons/links causes major problems with js contribution, so basic links are used!

		AjaxLink<String> yesLink = new AjaxLink<String>("yes") {
			@Override
			public void onClick(AjaxRequestTarget target) {
        		try {
							onConfirm(target);
							appendCloseIfNotReplaced(target);
						} catch (Exception e) {
							handleException(target, e);
						}
			}
		};
		yesLabel = new Label("yesLabel", new ResourceModel(isConfirm ? "yes" : "ok"));
		yesLink.add(yesLabel);
		add(yesLink);

		AjaxLink<String> noLink = new AjaxLink<String>("no") {
			@Override
			public void onClick(AjaxRequestTarget target) {
    		onDecline(target);
    		appendCloseIfNotReplaced(target);
			}
		};
		noLabel = new Label("noLabel", new ResourceModel("no"));
		noLink.add(noLabel);
		add(noLink);
		noLink.setVisible(isConfirm);


        header(titleModel);
	}

	protected void handleException(AjaxRequestTarget target, Exception e) {
		log.info("Got exception: " + e.getMessage(), e);
		String dummyValue = "¤¤¤";
		String value = dummyValue;
		List<Throwable> causes = ExceptionUtils.getThrowableList(e);
		Collections.reverse(causes);
		Iterator<Throwable> i = causes.iterator();
		while (dummyValue.equals(value) && i.hasNext()) {
			Throwable cause = i.next();
			value = getString("form303.general.exception." + cause.getClass().getSimpleName(), Model.of(cause), dummyValue);
		}
		if (dummyValue.equals(value)) {
			value = getString("form303.general.exception", Model.of(e));
		}
		this.error(value);
		target.add(getNotificationPanel());

	}

	protected abstract void onConfirm(AjaxRequestTarget target);

	protected void onDecline(AjaxRequestTarget target) {}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
	}

	@Override
	protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		Attributes.addClass(tag, "confirm");
		Attributes.set(tag, "style", "z-index: 1051;");
	}

	/**
	 * Shows this dialog in a {@link ModalContainer} of the current page. Must only be called within ajax call.
	 */
	public void show() {
		ModalContainer.showModal(this);
	}

	public void appendCloseIfNotReplaced(AjaxRequestTarget target) {
		if (ModalConfirmationPanel.this.getParent() != null) {
			// only close if not removed/replaced
			appendCloseDialogJavaScript(target);
		}
	}

	public ModalConfirmationPanel withYes(IModel<String> label) {
		setYesLabelModel(label);
		return this;
	}

	public ModalConfirmationPanel withNo(IModel<String> label) {
		setNoLabelModel(label);
		return this;
	}

	public ModalConfirmationPanel withYes(String key) {
		return withYes(new StringResourceModel(key, this, Model.of("")));
	}

	public ModalConfirmationPanel withNo(String key) {
		return withNo(new StringResourceModel(key, this, Model.of("")));
	}

	@SuppressWarnings("unchecked")
	public IModel<String> getYesLabelModel() {
		return (IModel<String>) yesLabel.getDefaultModel();
	}

	public void setYesLabelModel(IModel<String> yesLabel) {
		this.yesLabel.setDefaultModel(yesLabel);
	}

	@SuppressWarnings("unchecked")
	public IModel<String> getNoLabelModel() {
		return (IModel<String>) noLabel.getDefaultModel();
	}

	public void setNoLabelModel(IModel<String> noLabel) {
		this.noLabel.setDefaultModel(noLabel);
	}

	@Override
	public Modal appendCloseDialogJavaScript(AjaxRequestTarget target) {
        super.appendCloseDialogJavaScript(target);
        replaceWith(new EmptyPanel(getId()));
        return this;
	}
}
