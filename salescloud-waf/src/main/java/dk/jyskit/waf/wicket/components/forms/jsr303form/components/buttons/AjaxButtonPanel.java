package dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons;

import lombok.extern.slf4j.Slf4j;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;

@Slf4j
public class AjaxButtonPanel extends Panel {
	private AjaxButton button;

	public AjaxButtonPanel(final ComponentContainerPanel<?> container, String labelKey, Buttons.Type buttonType, final AjaxSubmitListener listener, boolean indicating) {
		super("panel");

		IModel<String> labelModel = container.getLabelStrategy().buttonLabel(labelKey);
		if (indicating) {
			this.button = new IndicatingAjaxButton("button", labelModel, container.getForm()) {
				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					container.handleErrorsInForm(target, form);
				}

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					target.add(form);
					try {
						listener.onSubmit(target);
					} catch (Exception e) {
						container.getExceptionHandler().onException(e);
					}
				}
			};
		} else {
			this.button = new AjaxButton("button", labelModel, container.getForm()) {
				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					container.handleErrorsInForm(target, form);
				}

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					target.add(form);

					try {
						listener.onSubmit(target);
					} catch (Exception e) {
						container.getExceptionHandler().onException(e);
					}
				}
			};
		}
		button.add(AttributeModifier.append("class", buttonType.cssClassName()));
		add(button);
	}

	public AjaxButtonPanel(final ComponentContainerPanel<?> container, String labelKey, Buttons.Type buttonType, final AjaxEventListener listener, boolean indicating) {
		super("panel");

		IModel<String> labelModel = container.getLabelStrategy().buttonLabel(labelKey);
		if (indicating) {
			this.button = new IndicatingAjaxButton("button", labelModel, container.getForm()) {
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					target.add(form);  // We MAY want to do this only if an exception is thrown
					try {
						listener.onAjaxEvent(target);
					} catch (Exception e) {
						container.getExceptionHandler().onException(e);
					}
				}

				protected void onError(AjaxRequestTarget target, org.apache.wicket.markup.html.form.Form<?> form) {
					log.error("Unhandled error!!!");
				};
			};
		} else {
			this.button = new AjaxButton("button", labelModel, container.getForm()) {
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					target.add(form);  // We MAY want to do this only if an exception is thrown
					try {
						listener.onAjaxEvent(target);
					} catch (Exception e) {
						container.getExceptionHandler().onException(e);
					}
				}

				protected void onError(AjaxRequestTarget target, org.apache.wicket.markup.html.form.Form<?> form) {
					log.error("Unhandled error!!!");
				};
			};
		}
		button.add(AttributeModifier.append("class", buttonType.cssClassName()));
		button.setDefaultFormProcessing(false);
		add(button);
	}

	public AjaxButton getButton() {
		return button;
	}
}
