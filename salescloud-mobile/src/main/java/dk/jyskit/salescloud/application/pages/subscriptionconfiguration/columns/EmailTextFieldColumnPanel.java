package dk.jyskit.salescloud.application.pages.subscriptionconfiguration.columns;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.validation.FormComponentFeedbackBorder;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import dk.jyskit.salescloud.application.model.Subscription;

public class EmailTextFieldColumnPanel extends Panel {

	/**
	 * @param headerModel
	 * @param propertyExpression
	 */
	public EmailTextFieldColumnPanel(String id, IModel<Subscription> rowModel, final String propertyExpression) {
		super(id);
		
		EmailTextField emailTextField = new EmailTextField("text", new PropertyModel<String>(rowModel, propertyExpression));
		emailTextField.setOutputMarkupId(true);
		FormComponentCssFeedbackBorder border = new FormComponentCssFeedbackBorder("feedback", "fieldError", emailTextField);
		add(border.add(emailTextField));
		
		emailTextField.add(new AjaxFormComponentUpdatingBehavior("onblur") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(border);
			}
			
			@Override
			protected void onError(AjaxRequestTarget target, RuntimeException e) {
				super.onError(target, e);
				rowModel.getObject().setEmail("");
				emailTextField.setConvertedInput("");
				target.add(emailTextField);
				target.add(border);
			}
		});

		
//		final FormComponentFeedbackBorder border = new FormComponentFeedbackBorder("border");
//		border.setOutputMarkupId(true);
//		add(border);
//		
//		EmailTextField textField = new EmailTextField("textfield", new PropertyModel<String>(rowModel, propertyExpression));
//		textField.setOutputMarkupId(true);
//		border.add(textField);
//		
//		textField.add(new AjaxFormComponentUpdatingBehavior("onblur") {
//			@Override
//			protected void onUpdate(AjaxRequestTarget target) {
//				target.add(border);
//			}
//			
//			@Override
//			protected void onError(AjaxRequestTarget target, RuntimeException e) {
//				super.onError(target, e);
//				target.add(border);
//			}
//		});
	}
}
