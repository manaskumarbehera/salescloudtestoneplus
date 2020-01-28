package dk.jyskit.salescloud.application.extensions.editroles;

import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.crud.AbstractEditPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

@SuppressWarnings("serial")
public class EditAdminRolePanel extends AbstractEditPanel<AdminRole, BaseUser> {

	@Inject
	private Dao<AdminRole> childDao;
	
	@Inject
	private UserDao parentDao;
	
	public EditAdminRolePanel(CrudContext context, final IModel<AdminRole> childModel, final IModel<BaseUser> parentModel) {
		super(context, childModel, parentModel);
	}

	@Override
	public IModel<AdminRole> createChildModel() {
		return new EntityModel<AdminRole>(new AdminRole());
	}
	
	@Override
	public void addFormFields(Jsr303Form<AdminRole> form) {
	}
	
	@Override
	public boolean prepareSave(AdminRole entity) {
		return true;
	}

	@Override
	public boolean save(AdminRole entity, Jsr303Form<AdminRole> form) {
		childDao.save(entity);
		return true;
	}

	@Override
	public boolean addToParentAndSave(BaseUser parent, AdminRole entity) {
		childDao.save(entity);
		parent.addRole(entity);
		parentDao.save(parent);
		return true;
	}
}
