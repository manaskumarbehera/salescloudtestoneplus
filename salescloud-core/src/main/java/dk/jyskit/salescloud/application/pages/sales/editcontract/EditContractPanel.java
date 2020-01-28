package dk.jyskit.salescloud.application.pages.sales.editcontract;

import java.util.List;

import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.dao.ContractDao;
import dk.jyskit.salescloud.application.dao.SalespersonRoleDao;
import dk.jyskit.salescloud.application.extensionpoints.ObjectFactory;
import dk.jyskit.salescloud.application.extensionpoints.PageNavigator;
import dk.jyskit.salescloud.application.model.Contract;
import dk.jyskit.salescloud.application.model.ContractCategory;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.crud.AbstractEditPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

public class EditContractPanel extends AbstractEditPanel<Contract, SalespersonRole> {
	@Inject
	private ObjectFactory objectFactory;
	
	@Inject
	private ContractDao childDao;
	
	@Inject
	private SalespersonRoleDao parentDao;
	
	@Inject
	private PageNavigator pageNavigator;
	
	public EditContractPanel(CrudContext context, IModel<Contract> childModel, final IModel<SalespersonRole> parentModel) {
		super(context, childModel, parentModel);
	}
	
	@Override
	public IModel<Contract> createChildModel() {
		return EntityModel.forEntity(objectFactory.createContract());
	}

	@Override
	public void addFormFields(Jsr303Form<Contract> form) {
		form.addTextField("title");
		
		BaseUser user = CoreSession.get().getUser();
		final SalespersonRole salespersonRole = (SalespersonRole) user.getRole(SalespersonRole.class);
		if (salespersonRole != null) {
			List<ContractCategory> contractCategories = salespersonRole.getContractCategories();  
			form.addDropDownChoice("category", contractCategories);
		}
	}

	@Override
	public boolean prepareSave(Contract entity) {
		return true;
	}

	@Override
	public boolean save(Contract child, Jsr303Form<Contract> form) {
		childDao.save(child);
//		Panel panel = new ListContractPanel(EditContractPanel.this.getId(), rootMarkupContainer) {
//			@Override
//			protected Filter getInitialFilter() {
//				return new And(
//						new Equal("businessArea", CoreSession.get().getBusinessArea()), 
//						new Equal("salesperson", CoreSession.get().getSalespersonRole()));
//			}
//		};
//		
//		EditContractPanel.this.getParent().addOrReplace(panel);
//		target.add(panel);
		return true;
	}

	@Override
	public boolean addToParentAndSave(SalespersonRole parent, Contract child) {
		String title = child.getTitle();
		ContractCategory category = child.getCategory();
		child = objectFactory.createAndSaveContract(CoreSession.get().getBusinessArea(), CoreSession.get().getSalespersonRole());
		child.setTitle(title);
		child.setCategory(category);
		parent.addContract(child);
		child.setBusinessArea(CoreSession.get().getBusinessArea());
		child.onOpen();
		Contract savedContract = childDao.save(child);
		CoreSession.get().setContract(savedContract);   // After saving !
		parentDao.save(parent);
		
		setResponsePage(pageNavigator.first());
		return true;
	}
	
	
	
	
//	public EditContractPanel(String id, Long entityId, final MarkupContainer rootMarkupContainer) {
//		super(id);
//		
//		setOutputMarkupId(true);
//		
//		final Contract contract = (entityId == null ? objectFactory.createContract(CoreSession.get().getBusinessArea(), CoreSession.get().getSalespersonRole()) : contractDao.findById(entityId));
//		
//		Jsr303Form<Contract> form = new Jsr303Form<>("form", contract);
//		form.setLabelStrategy(new EntityLabelStrategy("Contract"));
//		add(form);
//		
//		form.addTextField("title");
//		
//		BaseUser user = CoreSession.get().getUser();
//		final SalespersonRole salespersonRole = (SalespersonRole) user.getRole(SalespersonRole.class);
//		if (salespersonRole != null) {
//			List<ContractCategory> contractCategories = salespersonRole.getContractCategories();  
//			form.addDropDownChoice("category", contractCategories);
//		}
//
//		final String submitKey = (contract.isNewObject() ? "save" : "update");
//		
//		form.addSubmitButton(submitKey, Buttons.Type.Primary, new AjaxSubmitListener() {
//			@Override
//			public void onSubmit(AjaxRequestTarget target) {
//				if (contract.isNewObject()) {
//					salespersonRole.addContract(contract);
//					contract.setBusinessArea(CoreSession.get().getBusinessArea());
//					Contract savedContract = contractDao.save(contract);
//					CoreSession.get().setContract(savedContract);   // After saving !
//					
//					setResponsePage(pageNavigator.first());
//				} else {
//					contractDao.save(contract);
//					Panel panel = new ListContractPanel(EditContractPanel.this.getId(), rootMarkupContainer) {
//						@Override
//						protected Filter getInitialFilter() {
//							return new And(
//									new Equal("businessArea", CoreSession.get().getBusinessArea()), 
//									new Equal("salesperson", CoreSession.get().getSalespersonRole()));
//						}
//					};
//					
//					EditContractPanel.this.getParent().addOrReplace(panel);
//					target.add(panel);
//				}
//			}
//		});
//		
//		form.addButton("cancel", Buttons.Type.Default, new AjaxEventListener() {
//			@Override
//			public void onAjaxEvent(AjaxRequestTarget target) {
//				Panel panel = new ListContractPanel(EditContractPanel.this.getId(), rootMarkupContainer) {
//					@Override
//					protected Filter getInitialFilter() {
//						return new And(
//								new Equal("businessArea", CoreSession.get().getBusinessArea()), 
//								new Equal("salesperson", CoreSession.get().getSalespersonRole()));
//					}
//				};
//				
//				EditContractPanel.this.getParent().addOrReplace(panel);
//				target.add(panel);
//			}
//		});
//	}
}
