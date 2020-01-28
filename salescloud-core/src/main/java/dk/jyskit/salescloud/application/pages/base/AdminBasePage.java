package dk.jyskit.salescloud.application.pages.base;

import lombok.extern.slf4j.Slf4j;

import org.apache.wicket.Component;
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
	}
	
	public AdminBasePage() {
		this(new PageParameters());
	}

	protected Component createContentPanel(String wicketId, PageInfo pageInfo) {
		return null;
	}
}
