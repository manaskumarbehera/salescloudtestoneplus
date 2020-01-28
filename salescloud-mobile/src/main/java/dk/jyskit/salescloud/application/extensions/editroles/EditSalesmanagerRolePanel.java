package dk.jyskit.salescloud.application.extensions.editroles;

import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.SalesmanagerRoleDao;
import dk.jyskit.salescloud.application.model.SalesmanagerRole;
import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.crud.AbstractEditPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

@SuppressWarnings("serial")
public class EditSalesmanagerRolePanel extends AbstractEditPanel<SalesmanagerRole, BaseUser> {

	@Inject
	private SalesmanagerRoleDao dao;
	
	@Inject
	private UserDao userDao;
	
	public EditSalesmanagerRolePanel(CrudContext context, final IModel<SalesmanagerRole> childModel, final IModel<BaseUser> parentModel) {
		super(context, childModel, parentModel);
	}

	@Override
	public IModel<SalesmanagerRole> createChildModel() {
		return new EntityModel(new SalesmanagerRole());
	}
	
//	@Override
//	public SalesmanagerRole initEntity(Long entityId) {
//		if (entityId == null) {
//			return new SalesmanagerRole();
//		} else {
//			return dao.findById(entityId);
//		}
//	}
	
	@Override
	public void addFormFields(Jsr303Form<SalesmanagerRole> form) {
		form.addTextField("divisions");
	}
	
	@Override
	public boolean prepareSave(SalesmanagerRole entity) {
		return true;
	}

	@Override
	public boolean save(SalesmanagerRole entity, Jsr303Form<SalesmanagerRole> form) {
		dao.save(entity);
		return true;
	}

	@Override
	public boolean addToParentAndSave(BaseUser parent, SalesmanagerRole entity) {
		dao.save(entity);
		parent.addRole(entity);
		userDao.save(parent);
		return true;
	}
}
