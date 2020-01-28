package dk.jyskit.salescloud.application.extensions.editroles;

import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.UserManagerRoleDao;
import dk.jyskit.salescloud.application.model.UserManagerRole;
import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.crud.AbstractEditPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

@SuppressWarnings("serial")
public class EditUserManagerRolePanel extends AbstractEditPanel<UserManagerRole, BaseUser> {

	@Inject
	private UserManagerRoleDao dao;
	
	@Inject
	private UserDao userDao;
	
	public EditUserManagerRolePanel(CrudContext context, final IModel<UserManagerRole> childModel, final IModel<BaseUser> parentModel) {
		super(context, childModel, parentModel);
	}

	@Override
	public IModel<UserManagerRole> createChildModel() {
		return new EntityModel(new UserManagerRole());
	}
	
	@Override
	public void addFormFields(Jsr303Form<UserManagerRole> form) {
	}
	
	@Override
	public boolean prepareSave(UserManagerRole entity) {
		return true;
	}

	@Override
	public boolean save(UserManagerRole entity, Jsr303Form<UserManagerRole> form) {
		dao.save(entity);
		return true;
	}

	@Override
	public boolean addToParentAndSave(BaseUser parent, UserManagerRole entity) {
		dao.save(entity);
		parent.addRole(entity);
		userDao.save(parent);
		return true;
	}
}
