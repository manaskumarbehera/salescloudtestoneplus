package dk.jyskit.salescloud.application.pages.admin.profile;

import com.google.inject.Inject;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import dk.jyskit.salescloud.application.apis.user.UserApiClient;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.SalesmanagerRole;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.model.UserManagerRole;
import dk.jyskit.salescloud.application.pages.base.BasePage;
import dk.jyskit.waf.application.JITAuthenticatedWicketApplication;
import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxEventListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.components.buttons.AjaxSubmitListener;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.EntityLabelStrategy;
import dk.jyskit.waf.wicket.security.UserSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.Date;

@AuthorizeInstantiation({ AdminRole.ROLE_NAME, SalespersonRole.ROLE_NAME, SalesmanagerRole.ROLE_NAME, UserManagerRole.ROLE_NAME })
public class ChangePasswordPage extends BasePage {
	private static final long serialVersionUID = 1L;

	@Inject
	private UserDao userDao;
	private BaseUser u;

	private int count = 0;
	private TextField<?> oldPasswordField;
	
	public ChangePasswordPage(PageParameters parameters) {
		super(parameters);
		
		u = (BaseUser) UserSession.get().getUser();

		final ChangePasswordHelper eph = new ChangePasswordHelper();

		final Jsr303Form<ChangePasswordHelper> form = new Jsr303Form<ChangePasswordHelper>("jsr303form", eph, false);
		form.setLabelStrategy(new EntityLabelStrategy("Change Password"));
		add(form);

		oldPasswordField = form.addTextField("oldPassword","type='password'");
		oldPasswordField.setRequired(true);
		form.addTextField("newPassword", "type='password'").setRequired(true);
		form.addTextField("repeatNewPassword", "type='password'").setRequired(true);

		form.addSubmitButton("save", Buttons.Type.Primary, new AjaxSubmitListener() {
			@Override
			public void onSubmit(AjaxRequestTarget target) {
				if (count < 2) {
					String old = eph.getOldPassword();
					String newPass = eph.getNewPassword();
					String repeat = eph.getRepeatNewPassword();

					if (u.isAuthenticatedBy(old)) {
						String passwordRegEx = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
						if (!newPass.matches(passwordRegEx)) {
							form.getForm().error(getString("new.pass.too_weak"));
						} else if (newPass.equals(repeat)) {
							u.setPassword(newPass);
							u.setPasswordChangedDate(new Date());
							u = userDao.save(u);
							form.getForm().info(getString("new.pass.saved"));
							eph.setOldPassword(null);
							eph.setNewPassword(null);
							eph.setRepeatNewPassword(null);
							UserApiClient.changePasswordOnOtherServer(u.getUsername(), newPass);
							userDao.flush();
							setResponsePage(JITAuthenticatedWicketApplication.get().getAdminHomePage());
						} else {
							form.getForm().error(getString("new.pass.mismatch"));
							eph.setNewPassword(null);
							eph.setRepeatNewPassword(null);
						}
					} else {
						oldPasswordField.error(getString("old.pass.mismatch"));
						eph.setOldPassword(null);
						count++; // Only counting when old password has a mismatch
					}

				} else {
					UserSession.get().setUser(null);
					UserSession.get().setActiveRoleClass(null);
					UserSession.get().invalidate();
					setResponsePage(JITAuthenticatedWicketApplication.get().getHomePage());
				}
			}
		});

		form.addButton("cancel", Buttons.Type.Default, new AjaxEventListener() {
			@Override
			public void onAjaxEvent(AjaxRequestTarget target) {
				setResponsePage(JITAuthenticatedWicketApplication.get().getAdminHomePage());
			}
		});
	}
}
