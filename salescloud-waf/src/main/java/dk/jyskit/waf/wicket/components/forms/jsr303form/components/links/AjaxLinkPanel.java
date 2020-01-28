package dk.jyskit.waf.wicket.components.forms.jsr303form.components.links;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import dk.jyskit.waf.wicket.components.forms.jsr303form.ComponentContainerPanel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxSubmitListener;
import dk.jyskit.waf.wicket.components.links.IndicatingAjaxSubmitLink;

public class AjaxLinkPanel extends Panel {
	private AbstractLink link;
	private boolean indicating;

	/**
	 * Submitting link with text.
	 *
	 * @param container
	 * @param labelKey
	 * @param listener
	 * @param indicating
	 */
	public AjaxLinkPanel(final ComponentContainerPanel container, String labelKey, final AjaxSubmitListener listener, boolean indicating) {
		super("panel");

		this.indicating = indicating;
		if (indicating) {
			link = new IndicatingAjaxSubmitLink("link", container.getForm()) {
				@Override
				public void onSubmit(AjaxRequestTarget target, Form<?> form) {
					listener.onSubmit(target);
				}

				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					container.handleErrorsInForm(target, form);
				};
			};
		} else {
			link = new AjaxSubmitLink("link", container.getForm()) {
				@Override
				public void onSubmit(AjaxRequestTarget target, Form<?> form) {
					listener.onSubmit(target);
				}

				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					container.handleErrorsInForm(target, form);
				};
			};
		}
		add(link);

		link.add(AttributeModifier.replace("class", labelKey));

		Fragment fragment = new Fragment("fragment", "f_text", this);
		IModel<String> labelModel = container.getLabelStrategy().linkLabel(labelKey);
		fragment.add(new Label("text", labelModel));
        link.add(fragment);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		if (indicating) {
			response.render(CssReferenceHeaderItem.forReference(new PackageResourceReference(getClass(), "indicating.css")));
		}
	}

	/**
	 * Submitting link with image.
	 *
	 * @param container
	 * @param imageResource
	 * @param listener
	 * @param indicating
	 */
	public AjaxLinkPanel(final ComponentContainerPanel container, ResourceReference imageResource, final AjaxSubmitListener listener, boolean indicating) {
		super("panel");

		if (indicating) {
			link = new IndicatingAjaxSubmitLink("link", container.getForm()) {
				@Override
				public void onSubmit(AjaxRequestTarget target, Form<?> form) {
					listener.onSubmit(target);
				}

				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					container.handleErrorsInForm(target, form);
				};
			};
		} else {
			link = new AjaxSubmitLink("link", container.getForm()) {
				@Override
				public void onSubmit(AjaxRequestTarget target, Form<?> form) {
					listener.onSubmit(target);
				}

				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					container.handleErrorsInForm(target, form);
				};
			};
		}
		add(link);

		Fragment fragment = new Fragment("fragment", "f_img", this);
		fragment.add(new Image("img", imageResource));
        link.add(fragment);
	}

	/**
	 * Non-submitting link with text.
	 *
	 * @param container
	 * @param labelKey
	 * @param listener
	 */
	public AjaxLinkPanel(ComponentContainerPanel container, String labelKey, final AjaxEventListener listener) {
		super("panel");

		link = new AjaxLink("link") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				listener.onAjaxEvent(target);
			}
		};
		add(link);

		link.add(AttributeModifier.replace("class", labelKey));

		Fragment fragment = new Fragment("fragment", "f_text", this);
		IModel<String> labelModel = container.getLabelStrategy().linkLabel(labelKey);
		fragment.add(new Label("text", labelModel));
        link.add(fragment);
	}

	/**
	 * Non-submitting link with image.
	 *
	 * @param container
	 * @param imageResource
	 * @param listener
	 */
	public AjaxLinkPanel(ComponentContainerPanel container, ResourceReference imageResource, final AjaxEventListener listener) {
		super("panel");

		this.link = new AjaxLink("link") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				listener.onAjaxEvent(target);
			}
		};
		add(link);

		Fragment fragment = new Fragment("fragment", "f_img", this);
		fragment.add(new Image("img", imageResource));
        link.add(fragment);
	}

	public AbstractLink getLink() {
		return link;
	}
}
