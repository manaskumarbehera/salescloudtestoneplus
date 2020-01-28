package dk.jyskit.salescloud.application.pages.subscriptionconfiguration;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("serial")
@Slf4j
public class ExternalSubscriptionImplementationPage extends ExternalSubscriptionConfigurationPage {
	public ExternalSubscriptionImplementationPage(PageParameters parameters) {
		super(parameters, true);
	}
}
