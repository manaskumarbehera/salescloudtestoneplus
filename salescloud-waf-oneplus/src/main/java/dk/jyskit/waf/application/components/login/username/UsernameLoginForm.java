package dk.jyskit.waf.application.components.login.username;

import com.google.inject.Inject;
import dk.jyskit.waf.application.JITAuthenticatedWicketApplication;
import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.application.services.users.UserEvaluationService;
import dk.jyskit.waf.wicket.components.forms.BaseForm;
import dk.jyskit.waf.wicket.components.forms.annotations.DefaultFocusBehavior;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import java.util.List;

@Slf4j
public class UsernameLoginForm extends BaseForm<LoginInfo> {
	private static final long serialVersionUID = 1L;

	@Inject
	private UserDao userDao;

	@Inject
	private UserEvaluationService userEvaluationService;

	public UsernameLoginForm(String id) {
		super(id, new CompoundPropertyModel<LoginInfo>(new LoginInfo()));

		add(new FeedbackPanel("feedback"));

		HiddenField<String> widthField = new HiddenField<>("width");
		widthField.setOutputMarkupId(true);
		widthField.setMarkupId("width");
		add(widthField);
		add(new RequiredTextField<String>("username").add(new DefaultFocusBehavior()));
		add(new PasswordTextField("password"));
	}

	@Override
	protected void onError() {
		super.onError();
		log.warn("onError - " + hasError());
	}

	@Override
	public void onSubmit() {
		log.info("onSubmit - " + getModelObject().getUsername());
		List<BaseUser> usersWithName = userDao.findByUsername(getModelObject().getUsername());
		log.info("usersWithName: " + usersWithName.size());
		for (BaseUser user : usersWithName) {
			String userEvaluationKey = null;
			if (userEvaluationService == null) {
				log.error("userEvaluationService not found");
			} else {
				userEvaluationKey = userEvaluationService.evaluateUser(this, user);
				if (!"auth.error.passwordNeedsChanging".equals(userEvaluationKey) &&
						!StringUtils.isEmpty(userEvaluationKey)) {
					transError(userEvaluationKey);
					return;
				}
			}
			// check if user can login and do login
			if (user.isActive() && user.isAuthenticatedBy(getModelObject().getPassword())) {
				log.info("" + getModelObject().getUsername() + " - screen width: " + getModelObject().getWidth());

				// login user
				getSession().setUser(user);
				if (user instanceof BaseUser) {
					((BaseUser) user).getBaseRoleList().size();	// eager load role objects
				}

				if ("auth.error.passwordNeedsChanging".equals(userEvaluationKey)) {
					// setResponsePage has already been called!
				} else {
					// goto home page
					setResponsePage(JITAuthenticatedWicketApplication.get().getAdminHomePage());
				}
				return;
			} else {
				if (!user.isActive()) {
					log.warn("User not active: " + getModelObject().getUsername());
				} else {
					log.warn("User uses wrong password: " + getModelObject().getUsername());
				}
			}
		}
		transError("auth.error.userNotFound");
	}
}
