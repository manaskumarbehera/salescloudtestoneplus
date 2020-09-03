package dk.jyskit.waf.wicket.utils;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.LabeledWebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton;
import dk.jyskit.waf.wicket.components.forms.jsr303form.ControlGroupWithLabelControl;

public class BootstrapUtils {

	/**
	 *
	 * @param groupWicketId
	 *            The wicket id of the group-control markup
	 * @param labelModel
	 *            The label text to use
	 * @param formComponent
	 *            THe component to wrap in the created control group, must have
	 *            markup inside the control-group element
	 * @param validateOnEvent
	 *            The event to attach field validate behavior to (null == do no immidate field validation). Eg. "onblur"
	 * @param helpModel
	 * @return
	 */
	public static ControlGroupWithLabelControl createControlGroup(String groupWicketId, IModel<String> labelModel, Component formComponent, String validateOnEvent, 
			boolean inlineHelp, boolean hideLabel, boolean isRequired, IModel<String> helpModel) {
		
		final ControlGroupWithLabelControl controlGroup = new ControlGroupWithLabelControl(groupWicketId, labelModel, helpModel, inlineHelp, hideLabel, isRequired);
		controlGroup.add(formComponent);
		controlGroup.setOutputMarkupId(true);
		if (formComponent instanceof LabeledWebMarkupContainer) {
			LabeledWebMarkupContainer labeled = (LabeledWebMarkupContainer) formComponent;
			labeled.setLabel(labelModel);
		}

		if (validateOnEvent != null && !validateOnEvent.isEmpty()) {
			if (formComponent instanceof LabeledWebMarkupContainer) {
				formComponent.add(new AjaxFormComponentUpdatingBehavior(validateOnEvent) {
					private static final long serialVersionUID = 1L;

					protected void onUpdate(AjaxRequestTarget target) {
						getFormComponent().validate();
						target.add(controlGroup);
					}

					protected void onError(AjaxRequestTarget target, RuntimeException e) {
						target.add(controlGroup);
					};
				});
			}
		}
		return controlGroup;
	}

	/**
	 * @see NavbarButton#NavbarButton(Class, PageParameters, IModel)
	 */
	public static <T extends Page> NavbarButton<T> navbarPageLink(final Class<T> pageClass, final PageParameters parameters, final String labelKey) {
		return new NavbarButton<T>(pageClass, parameters, new ResourceModel(labelKey));
	}

	/**
	 * @see NavbarButton#NavbarButton(Class, PageParameters, IModel)
	 */
	public static <T extends Page> NavbarButton<T> navbarPageLink(final Class<T> pageClass, final PageParameters parameters, final IModel<String> label) {
		return new NavbarButton<T>(pageClass, parameters, label);
	}

	/**
	 * @see NavbarButton#NavbarButton(Class, IModel)
	 */
	public static <T extends Page> NavbarButton<T> navbarPageLink(final Class<T> pageClass, final String labelKey) {
		return new NavbarButton<T>(pageClass, new ResourceModel(labelKey));
	}

	/**
	 * @see NavbarButton#NavbarButton(Class, IModel)
	 */
	public static <T extends Page> NavbarButton<T> navbarPageLink(final Class<T> pageClass, final IModel<String> label) {
		return new NavbarButton<T>(pageClass, label);
	}

}
