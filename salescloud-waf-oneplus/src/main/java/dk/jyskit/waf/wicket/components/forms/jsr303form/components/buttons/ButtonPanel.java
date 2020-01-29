package dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;

public class ButtonPanel extends Panel {
	private Button button;

	public ButtonPanel(final ComponentContainerPanel container, String labelKey, final ButtonSubmitListener listener) {
		super("panel");
		IModel<String> labelModel = container.getLabelStrategy().buttonLabel(labelKey);
		this.button = new Button("button", labelModel) {
			@Override
			public void onSubmit() {
				listener.onSubmit();
			}
//			@Override
//			protected void onError(Form<?> form) {
//				container.handleErrorsInForm(target, form);
//			}
		};
		add(button);
	}

	public ButtonPanel(ComponentContainerPanel container, String labelKey, final ButtonListener listener) {
		super("panel");
		IModel<String> labelModel = container.getLabelStrategy().buttonLabel(labelKey);
		this.button = new Button("button", labelModel) {
			@Override
			public void onSubmit() {
				listener.onClick();
			}
		};
		button.setDefaultFormProcessing(false);
		add(button);
	}

	public Button getButton() {
		return button;
	}
}
