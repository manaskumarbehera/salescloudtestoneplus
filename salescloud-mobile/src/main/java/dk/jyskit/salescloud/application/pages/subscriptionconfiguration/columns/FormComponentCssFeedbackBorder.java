package dk.jyskit.salescloud.application.pages.subscriptionconfiguration.columns;

import org.apache.wicket.Session;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.FormComponent;

public class FormComponentCssFeedbackBorder extends Border implements IFeedback {
	boolean _hasErrors;
	private CharSequence _cssClass;
	private FormComponent _component;

	public FormComponentCssFeedbackBorder(String id, String cssClass, FormComponent component) {
		super(id);
		_cssClass = cssClass;
		_component = component;
		setOutputMarkupId(true);
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		_hasErrors = _component.getFeedbackMessages().size() != 0;
	}

	@Override
	protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		if (_hasErrors) {
			tag.put("class", _cssClass); 
		}
	}
}