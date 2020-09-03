package dk.jyskit.salescloud.application.pages.base;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.pages.admin.profile.ChangePasswordPage;
import lombok.extern.slf4j.Slf4j;

import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import dk.jyskit.salescloud.application.model.PageInfo;

@SuppressWarnings("serial")
@Slf4j
public abstract class AdminBasePage extends BasePage {

	/**
	 * Construct.
	 *
	 * @param parameters
	 *          current page parameters
	 */
	public AdminBasePage(PageParameters parameters) {
		super();

		if (CoreSession.get().isPasswordChangeRequired()) {
			throw new RestartResponseException(ChangePasswordPage.class);
		}
	}
	
	public AdminBasePage() {
		this(new PageParameters());

		if (CoreSession.get().isPasswordChangeRequired()) {
			throw new RestartResponseException(ChangePasswordPage.class);
		}
	}

	protected Component createContentPanel(String wicketId, PageInfo pageInfo) {
		return null;
	}
}
