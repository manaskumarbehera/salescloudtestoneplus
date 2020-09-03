/*******************************************************************************
 * Copyright (c) 2012 Anton Bessonov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons
 * Attribution 3.0 License which accompanies this distribution,
 * and is available at
 * http://creativecommons.org/licenses/by/3.0/
 * 
 * Contributors:
 *     Anton Bessonov - initial API and implementation
 ******************************************************************************/
package dk.jyskit.waf.application.components.login.email;

import java.util.List;

import dk.jyskit.waf.application.services.users.UserEvaluationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import com.google.inject.Inject;

import dk.jyskit.waf.application.JITAuthenticatedWicketApplication;
import dk.jyskit.waf.application.components.login.LoginAuxErrorProvider;
import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.wicket.components.forms.BaseForm;
import dk.jyskit.waf.wicket.components.forms.annotations.DefaultFocusBehavior;

@Slf4j
public class EmailLoginForm extends BaseForm<LoginInfo> {
	private static final long serialVersionUID = 1L;

	@Inject
	private UserDao userDao;

	@Inject
	private UserEvaluationService userEvaluationService;

	public EmailLoginForm(String id) {
		super(id, new CompoundPropertyModel<LoginInfo>(new LoginInfo()));
		add(new FeedbackPanel("feedback"));
		add(new RequiredTextField<String>("email").add(new DefaultFocusBehavior()));
		add(new PasswordTextField("password"));
		log.warn("EmailLoginForm");
	}

	@Override
	public void onSubmit() {
		log.info("onSubmit - " + getModelObject().getEmail());
		List<BaseUser> usersWithEmail = userDao.findByEmail(getModelObject().getEmail());
		log.info("usersWithName: " + usersWithEmail.size());
		for (BaseUser user : usersWithEmail) {
			if (userEvaluationService != null) {
				log.info("Evaluating user: " + getModelObject().getEmail());
				String key = userEvaluationService.evaluateUser(getPage(), user);
				if (!StringUtils.isEmpty(key)) {
					log.info("Error: " + key);
					transError(key);
					return;
				}
			} else {
				log.error("userEvaluationService not found");
			}
			// check if user can login and do login
			if (user.isActive() && user.isAuthenticatedBy(getModelObject().getPassword())) {
				// login user
				getSession().setUser(user);
				if (user instanceof BaseUser) {
					((BaseUser) user).getBaseRoleList().size();	// eager load role objects
				}

				// goto home page
				setResponsePage(JITAuthenticatedWicketApplication.get().getAdminHomePage());
				return;
			} else {
				if (!user.isActive()) {
					log.warn("User not active: " + getModelObject().getEmail());
				} else {
					log.warn("User uses wrong password: " + getModelObject().getEmail());
				}
			}
		}
		transError("auth.error.userNotFound");
	}
}
