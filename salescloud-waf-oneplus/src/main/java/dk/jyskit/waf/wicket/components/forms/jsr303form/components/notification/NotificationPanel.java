package dk.jyskit.waf.wicket.components.forms.jsr303form.components.notification;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

public class NotificationPanel extends FeedbackPanel {
	private static final long serialVersionUID = 1L;
	private final String cssClass = "notificationpanel";
	private String additionalCSSClass = "notificationpanel_top_left";
	private final ResourceReference CSS	= new PackageResourceReference(NotificationPanel.class, "NotificationPanel.css");
	private final ResourceReference JS 	= new PackageResourceReference(NotificationPanel.class, "NotificationPanel.js");

	// Create a notifcation panel with the default additional class, specified as a field variable
	public NotificationPanel(String id) {
		super(id);
		init(id, additionalCSSClass);
	}

	// Create a notifcation panel with a custom additional class, overwriting the field variable
	public NotificationPanel(String id, String additionalCSSClass) {
		super(id);
		this.additionalCSSClass = additionalCSSClass;
		init(id, additionalCSSClass);
	}

	private void init(String id, String additionalCSSClass) {
		// set custom markup id and ouput it, to find the component later on in the js function
		setMarkupId(id);
		setOutputMarkupId(true);

		// Add the additional cssClass and hide the element by default
		add(AttributeModifier.replace("class", cssClass + " " + additionalCSSClass));
		add(AttributeModifier.replace("style", "opacity: 0;"));
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssReferenceHeaderItem.forReference(CSS, "screen"));
		response.render(JavaScriptReferenceHeaderItem.forReference(JS));
	}

	/**
	 * Method to refresh the notification panel
	 *
	 * if there are any feedback messages for the user, find the gravest level,
	 * format the notification panel accordingly and show it
	 *
	 * @param target
	 *            AjaxRequestTarget to add panel and the calling javascript function
	 */
	public void refresh(AjaxRequestTarget target) {

		// any feedback at all in the current form?
		if (anyMessage()) {
			int highestFeedbackLevel = FeedbackMessage.INFO;

			// any feedback with the given level?
			if (anyMessage(FeedbackMessage.WARNING))
				highestFeedbackLevel = FeedbackMessage.WARNING;
			if (anyMessage(FeedbackMessage.ERROR))
				highestFeedbackLevel = FeedbackMessage.ERROR;

			// add the css classes to the notification panel,
			// including the border css which represents the highest level of feedback
			add(AttributeModifier.replace("class", cssClass + " " + additionalCSSClass + " notificationpanel_border_" + String.valueOf(highestFeedbackLevel)));
			
			// refresh the panel and call the js function with the panel markup id
			// and the total count of messages
			target.add(this);
			target.appendJavaScript("showNotification('" + getMarkupId() + "', "
					+ getCurrentMessages().size() + ");");
		}
	}

	/**
	 * Returns css class for the single rows of the panel
	 *
	 * @see org.apache.wicket.markup.html.panel.FeedbackPanel#getCSSClass(org.apache.wicket.feedback.FeedbackMessage)
	 */
	@Override
	protected String getCSSClass(FeedbackMessage message) {
		return "notificationpanel_row_" + message.getLevelAsString();
	}
}

