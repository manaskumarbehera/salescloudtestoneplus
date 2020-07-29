package dk.jyskit.salescloud.application.services.users;

import dk.jyskit.salescloud.application.pages.admin.profile.ChangePasswordPage;
import dk.jyskit.waf.application.Environment;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.application.services.users.UserEvaluationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.request.flow.ResetResponseException;

import java.util.Date;

@Slf4j
public class UserEvaluationServiceImpl implements UserEvaluationService {
	public String evaluateUser(Component component, BaseUser user) {
		log.warn("evaluating user " + user.getUsername());
		if (user.isAuthenticatedBy("Slettet")) {
			return "auth.error.userNotFound";
		} else if (user.isAuthenticatedBy("Passiv")) {
			return "auth.error.userNotFound";
		} else if ((user.getPasswordChangedDate() == null) || DateUtils.addMonths(user.getPasswordChangedDate(), 2).before(new Date())) {
			component.setResponsePage(ChangePasswordPage.class);
			return "auth.error.passwordNeedsChanging";
		} else if ((Environment.isOneOf("heroku2"))
					&& (!"RMO@tdc.dk".equalsIgnoreCase(user.getUsername())
					&& !"RMO@tdc.dk".equalsIgnoreCase(user.getEmail())
					&& !"dal@tdcerhvervscenter.dk".equalsIgnoreCase(user.getUsername())
					&& !"dal@tdcerhvervscenter.dk".equalsIgnoreCase(user.getEmail())
					&& !"WHO@tdc.dk".equalsIgnoreCase(user.getUsername())
					&& !"WHO@tdc.dk".equalsIgnoreCase(user.getEmail())
					&& !"CHJENS@tdc.dk".equalsIgnoreCase(user.getUsername())
					&& !"CHJENS@tdc.dk".equalsIgnoreCase(user.getEmail())
					&& !"shch@tdc.dk".equalsIgnoreCase(user.getUsername())
					&& !"shch@tdc.dk".equalsIgnoreCase(user.getEmail())
					&& !"cmic@tdc.dk".equalsIgnoreCase(user.getUsername())
					&& !"cmic@tdc.dk".equalsIgnoreCase(user.getEmail())
					&& !"kica@tdcerhvervscenter.dk".equalsIgnoreCase(user.getUsername())
					&& !"kica@tdcerhvervscenter.dk".equalsIgnoreCase(user.getEmail())
					&& !"SONIEL@tdc.dk".equalsIgnoreCase(user.getUsername())
					&& !"SONIEL@tdc.dk".equalsIgnoreCase(user.getEmail())
					&& !"toboj@tdc.dk".equalsIgnoreCase(user.getUsername())
					&& !"toboj@tdc.dk".equalsIgnoreCase(user.getEmail())
					&& !"danal@tdc.dk".equalsIgnoreCase(user.getUsername())
					&& !"danal@tdc.dk".equalsIgnoreCase(user.getEmail())
					&& !"MARPED@tdc.dk".equalsIgnoreCase(user.getEmail())
					&& !"MARPED@tdc.dk".equalsIgnoreCase(user.getUsername())
					&& !"mdhe@tdc.dk".equalsIgnoreCase(user.getEmail())
					&& !"mdhe@tdc.dk".equalsIgnoreCase(user.getUsername())
					&& !"manb@tdc.dk".equalsIgnoreCase(user.getUsername())
					&& !"manb@tdc.dk".equalsIgnoreCase(user.getEmail())
					&& !"janjysk".equalsIgnoreCase(user.getUsername())
					&& !"janjysk".equalsIgnoreCase(user.getEmail()))) {
				return "auth.error.testUsersOnly";
		}
		return null;
	}
}
