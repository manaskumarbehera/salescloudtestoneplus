package dk.jyskit.waf.wicket.components.panels.modal;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import dk.jyskit.waf.application.Environment;
import dk.jyskit.waf.application.JITWicketApplication;
import dk.jyskit.waf.application.utils.exceptions.SystemException;
import dk.jyskit.waf.wicket.utils.WicketUtils;

public class ModalContainer extends Panel {
	public static String MODAL_ID = "modal";
	public static String CONFIRM_ID = "modalconfirm";

	public ModalContainer(String id) {
		super(id);
		add(new Modal(MODAL_ID));
		add(new Modal(CONFIRM_ID));
		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);
	}

	public void show(AjaxRequestTarget target, Modal modal) {
		replace(modal);
		modal.setFadeIn("true".equals(JITWicketApplication.get().getSetting(Environment.WAF_MODAL_FADEIN)));
		target.add(modal);

//		modal.appendShowDialogJavaScript(target);
	}

	public static ModalContainer modalContainer() {
		AjaxRequestTarget target = WicketUtils.getAjaxTarget();
		if (target == null) {
			throw new SystemException("ModalContainer should only be used in an ajax call");
		}
		return modalContainer(target);
	}

	private static ModalContainer modalContainer(AjaxRequestTarget target) {
		ModalContainer container = WicketUtils.findOnPage(target.getPage(), ModalContainer.class);
		if (container == null) {
				throw new SystemException("A ModalContainer must be added to page in order to use modals");
		}
		return container;
	}

	public static void showModal(AjaxRequestTarget target, Modal modal) {
		if (target == null) {
			throw new SystemException("Modal can only be shown in an ajax call");
		}
		modalContainer().show(target, modal);
	}

	/**
	 * Show a modal on the current page.
	 * Must be called within an ajx call and the page must have a modal container.
	 * @param modal
	 */
	public static void showModal(Modal modal) {
		modalContainer().show(WicketUtils.getAjaxTarget(), modal);
	}
}
