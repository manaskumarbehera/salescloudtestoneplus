package dk.jyskit.waf.application.services.users;

import dk.jyskit.waf.application.model.BaseUser;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;

public interface UserEvaluationService {
	/**
	 * Return error key or null
	 * @param user
	 * @return
	 */
	String evaluateUser(Component component, BaseUser user);
}
