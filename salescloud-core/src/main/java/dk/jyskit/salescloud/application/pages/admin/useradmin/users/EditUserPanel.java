package dk.jyskit.salescloud.application.pages.admin.useradmin.users;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.model.SalesmanagerRole;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.pages.admin.useradmin.UsersPage;
import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.FormGroup;
import dk.jyskit.waf.wicket.components.forms.jsr303form.FormRow;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.crud.AbstractWrappedEditPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

@SuppressWarnings("serial")
public class EditUserPanel extends AbstractWrappedEditPanel<BaseUser, BaseUserWrapper, Void> {

	@Inject
	private UserDao childDao;
	
	private TextField<?> usernameField;
	private TextField<?> passwordField;
	
	public EditUserPanel(CrudContext context, IModel<BaseUser> childModel) {
		super(context, childModel);
	}
	
	@Override
	public IModel<BaseUser> createChildModel() {
		return new EntityModel<BaseUser>(new BaseUser());
	}

	@Override
	public BaseUserWrapper wrapChild(BaseUser child) {
		return new BaseUserWrapper(child);
	}

	@Override
	public void addFormFields(Jsr303Form<BaseUserWrapper> form) {
		FormRow<BaseUserWrapper> row = form.createRow();
		
		FormGroup<BaseUserWrapper> userGroup;
		if (childModel.getObject().hasRole(SalespersonRole.class) || childModel.getObject().hasRole(SalesmanagerRole.class)) {
			userGroup = row.createGroup(Model.of("Bruger"));
		} else {
			userGroup = row.createNoLegendGroup();
		}
		
		userGroup.addTextField("user.firstName");
		userGroup.addTextField("user.lastName");
		
		usernameField = userGroup.addTextField("user.username");
		usernameField.setRequired(childModel.getObject().isNewObject());
		usernameField.getParent().getParent().setOutputMarkupId(true);
		
		userGroup.addTextField("user.email").setRequired(true);
		userGroup.addTextField("user.smsPhone").setRequired(true);

		/*
		 * Plain password attribute does not exist in "BaseUser", the
		 * AddCustomerAdminPanelHelper class adds the password field to the user
		 * object in order to save it using UserDao.
		 */

		/*
		 * If user already has a password, then it's not supposed to get
		 * encrypted again. If a password doesn't exist then we add the password
		 * field.
		 */

		passwordField = userGroup.addTextField("password", "type='password'");
		passwordField.getParent().getParent().setOutputMarkupId(true);
	}
	
	@Override
	public boolean prepareSave(BaseUserWrapper wrappedChild) {
		return true;
	}

	@Override
	public boolean save(BaseUserWrapper wrappedChild, Jsr303Form<BaseUserWrapper> form, AjaxRequestTarget target) {
		boolean userAlreadyHasPassword = false;
		
		BaseUser user = wrappedChild.getUser();

		boolean usernameAlreadyUsed = false;
		
		if (user.isNewObject()) {
			user.setIdentity("");
			
			List<BaseUser> existingUsers = childDao.findByUsername(user.getUsername());
			if (existingUsers.size() > 0) {
				usernameAlreadyUsed = true;
			}
		}
		if (usernameAlreadyUsed) {
			usernameField.error(getString("username.already_used"));
			target.add(usernameField.getParent().getParent());
			return false;
		} else if (user.getUsername().length() < 4) {
			usernameField.error(getString("username.too_short"));
			target.add(usernameField.getParent().getParent());
			return false;
		} else {
			if (user.getPasswordEncrypt() != null) {
				userAlreadyHasPassword = true;
			}

			if (wrappedChild.getPassword().equals("¤Dummy¤¤PassWord¤") && !userAlreadyHasPassword) {
				passwordField.error(getString("password.needed"));
				target.add(passwordField.getParent().getParent());
				return false;
			} else if (wrappedChild.getPassword().length() < 4) {
				passwordField.error(getString("password.too_short"));
				target.add(passwordField.getParent().getParent());
				return false;
			} else {
				if (!BaseUserWrapper.DUMMY_PASSWORD.equals(wrappedChild.getPassword())) {
					user.setPassword(wrappedChild.getPassword());
				}
				childDao.saveAndFlush(user);

				form.getForm().info(getString("BaseUser.new.saved"));
				setResponsePage(UsersPage.class);
			}
		}
		return true;
	}

	@Override
	public boolean addToParentAndSave(Void parent, BaseUserWrapper wrappedChild) {
		// no parent
		return true;
	}
	
	
//	
//	// ----------------------------------
//
//	public EditUserPanel(String wicketId, Long entityId) {
//		super(wicketId);
//		
//		labelKey("user.edit.entity");
//		final BaseUser entity = (BaseUser) (entityId == null ? new BaseUser() : dao.findById(entityId));
//		
//		originalUsername = entity.getUsername();
//		helper = new UserModelHelper(entity);
//		final Jsr303Form<UserModelHelper> form = new Jsr303Form<UserModelHelper>("jsr303form", helper, false);
//
//		form.setLabelStrategy(new EntityLabelStrategy("User"));
//		add(form);
//		
//		FormRow<UserModelHelper> row = form.createRow();
//		
//		FormGroup<UserModelHelper> userGroup;
//		if (entity.hasRole(SalespersonRole.class)) {
//			userGroup = row.createGroup(Model.of("Bruger"));
//		} else {
//			userGroup = row.createNoLegendGroup();
//		}
//		
//		usernameField = userGroup.addTextField("user.username");
//		if (entity.isNewObject()) {
//			usernameField.setRequired(true);
//		} else {
//			usernameField.setEnabled(false);
//		}
//		
//		userGroup.addTextField("user.email").setRequired(true);
//
//		/*
//		 * Plain password attribute does not exist in "BaseUser", the
//		 * AddCustomerAdminPanelHelper class adds the password field to the user
//		 * object in order to save it using UserDao.
//		 */
//
//		/*
//		 * If user already has a password, then it's not supposed to get
//		 * encrypted again. If a password doesn't exist then we add the password
//		 * field.
//		 */
//
//		passwordField = userGroup.addTextField("password", "type='password'");
//
//		form.addSubmitButton("save", Buttons.Type.Primary, new AjaxSubmitListener() {
//			@Override
//			public void onSubmit(AjaxRequestTarget target) {
//
//				boolean userAlreadyHasPassword = false;
//				
//				BaseUser user = helper.getUser();
//
//				boolean usernameAlreadyUsed = false;
//				
//				if (user.isNewObject()) {
//					user.setFirstName("");
//					user.setLastName("");
//					user.setIdentity("");
//					
//					List<BaseUser> existingUsers = dao.findByUsername(user.getUsername());
//					if (existingUsers.size() > 0) {
//						usernameAlreadyUsed = true;
//					}
//				}
//				if (usernameAlreadyUsed) {
//					usernameField.error(getString("username.already_used"));
//				} else if (user.getUsername().length() < 5) {
//					usernameField.error(getString("username.too_short"));
//				} else {
//					if (user.getPasswordEncrypt() != null) {
//						userAlreadyHasPassword = true;
//					}
//
//					if (helper.getPassword().equals("¤Dummy¤¤PassWord¤") && !userAlreadyHasPassword) {
//						passwordField.error(getString("password.needed"));
//					} else if (helper.getPassword().length() < 5) {
//						passwordField.error(getString("password.too_short"));
//					} else {
//						if (!UserModelHelper.DUMMY_PASSWORD.equals(helper.getPassword())) {
//							user.setPassword(helper.getPassword());
//						}
//						dao.saveAndFlush(user);
//
//						form.getForm().info(getString("user.new.saved"));
//						setResponsePage(UsersPage.class);
//					}
//				}
//			}
//		});
//		
//		form.addButton("cancel", Buttons.Type.Default, new AjaxEventListener() {
//			@Override
//			public void onAjaxEvent(AjaxRequestTarget target) {
//				setResponsePage(UsersPage.class);
//			}
//		});
//	}


}
