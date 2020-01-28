package dk.jyskit.salescloud.application.pages.admin.contractcategories;

import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.SalespersonRoleDao;
import dk.jyskit.salescloud.application.model.ContractCategory;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.crud.AbstractEditPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

public class EditContractCategoryPanel extends AbstractEditPanel<ContractCategory, SalespersonRole> {

	@Inject
	private Dao<ContractCategory> childDao;
	
	@Inject
	private SalespersonRoleDao parentDao;
	
	public EditContractCategoryPanel(CrudContext context, IModel<ContractCategory> childModel, final IModel<SalespersonRole> parentModel) {
		super(context, childModel, parentModel);
	}
	
	@Override
	public IModel<ContractCategory> createChildModel() {
		return new EntityModel<ContractCategory>(new ContractCategory());
	}
	
	@Override
	public void addFormFields(Jsr303Form<ContractCategory> form) {
		form.addTextField("name");
	}
	
	@Override
	public boolean prepareSave(ContractCategory entity) {
		return true;
	}
	
	@Override
	public boolean save(ContractCategory entity, Jsr303Form<ContractCategory> form) {
		childDao.save(entity);
		return true;
	}

	@Override
	public boolean addToParentAndSave(SalespersonRole parent, ContractCategory entity) {
		parent.addContractCategory(entity);
		parentDao.save(parent);
		return true;
	}
	
//	public EditContractCategoryPanel(String wicketId, Long entityId) {
//		super(wicketId);
//		labelKey("edit.entity");
//		final ContractCategory entity = (ContractCategory) (entityId == null ? new ContractCategory() : contractCategoryDao.findById(entityId));
//		
//		final Jsr303Form<ContractCategory> form = new Jsr303Form<ContractCategory>("jsr303form", entity, false);
//		EntityLabelStrategy labelStrategy = new EntityLabelStrategy("ContractCategory");
//		form.setLabelStrategy(labelStrategy);
//		add(form);
//		
//		form.addTextField("name");
//		
//		form.addSubmitButton("save", Buttons.Type.Primary, new AjaxSubmitListener() {
//			@Override
//			public void onSubmit(AjaxRequestTarget target) {
//				if (entity.isNewObject()) {
//					SalespersonRole salesperson = CoreSession.get().getSalespersonRole();
//					salesperson.addContractCategory(entity);
//					salespersonRoleDao.save(salesperson);
//				} else {
//					contractCategoryDao.save(entity);
//				}
//				setResponsePage(ListContractCategoryPage.class);
//			}
//		});
//		form.addButton("cancel", Buttons.Type.Default, new AjaxEventListener() {
//			@Override
//			public void onAjaxEvent(AjaxRequestTarget target) {
//				setResponsePage(ListContractCategoryPage.class);
//			}
//		});
//	}
}
