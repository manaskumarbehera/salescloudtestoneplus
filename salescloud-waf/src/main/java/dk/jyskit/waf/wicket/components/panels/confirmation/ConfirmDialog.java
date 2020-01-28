package dk.jyskit.waf.wicket.components.panels.confirmation;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import dk.jyskit.waf.wicket.utils.IAjaxCall;


/**
 * A confirm dialog that can be configured with IAjaxCall method objects. Use when we have an existing ajax method we want to call on confirm or
 * decline. Override onConfirm/onDecline if yuo have more context in the surrounding method scope that we need to use.
 * Exmaple: 
 *   <pre><code>
 *   ...
 *   void onConfirmDelete(AjaxRequestTarget target) {
 *     delete();
 *     refresh(target);
 *   }
 *   
 *   void onDeleteClick(AjaxRequestTarget target) {
 *     new ConfirmDialog(Model.of("Delete"), Model.of("Do you really want to delete?")).confirm(new AjaxCall(this, "onConfirmDelete")).show();
 *     // or with resource keys
 *     new ConfirmDialog("key.delete.title", "key.delete.text").withYes("key.delete.yes").confirm(new AjaxCall(this, "onConfirmDelete")).show();
 *   } 
 *   
 *   </code></pre>
 * @author Palfred
 * 
 */
public class ConfirmDialog extends ModalConfirmationPanel {
	private IAjaxCall confirmer;
	private IAjaxCall decliner;

	public ConfirmDialog(IModel<String> titleModel, IModel<String> textModel, boolean isConfirm) {
		super(titleModel, textModel, isConfirm);
	}
	
	public ConfirmDialog(IModel<String> titleModel, IModel<String> textModel) {
		this(titleModel, textModel, true);
	}

	public ConfirmDialog(String titleKey, String textKey) {
		this(titleKey, textKey, true);
	}

	public ConfirmDialog(String titleKey, String textKey, boolean isConfirm) {
		this(new ResourceModel(titleKey), new ResourceModel(textKey), isConfirm);
	}

	@Override
	protected void onConfirm(AjaxRequestTarget target) {
		if (confirmer != null) {
			confirmer.invoke(target);
		}
	}

	@Override
	protected void onDecline(AjaxRequestTarget target) {
		if (decliner != null) {
			decliner.invoke(target);
		}
	}

	public IAjaxCall getConfirmer() {
		return confirmer;
	}

	public void setConfirmer(IAjaxCall confirmer) {
		this.confirmer = confirmer;
	}

	public ModalConfirmationPanel confirmer(IAjaxCall confirmMethod) {
		setConfirmer(confirmMethod);
		return this;
	}

	public IAjaxCall getDecliner() {
		return decliner;
	}

	public void setDecliner(IAjaxCall decliner) {
		this.decliner = decliner;
	}

	public ModalConfirmationPanel decliner(IAjaxCall declineMethod) {
		setDecliner(declineMethod);
		return this;
	}
}
