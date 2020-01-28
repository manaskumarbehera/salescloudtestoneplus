package dk.jyskit.salescloud.application.extensions.editroles;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.OrganisationDao;
import dk.jyskit.salescloud.application.dao.SalespersonRoleDao;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.services.cvr.CVRService;
import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.crud.AbstractEditPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

@SuppressWarnings("serial")
public class EditSalespersonRolePanel extends AbstractEditPanel<SalespersonRole, BaseUser> {

	@Inject
	private OrganisationDao organisationDao;
	
	@Inject
	private SalespersonRoleDao dao;
	
	@Inject
	private UserDao userDao;
	
	public EditSalespersonRolePanel(CrudContext context, final IModel<SalespersonRole> childModel, final IModel<BaseUser> parentModel) {
		super(context, childModel, parentModel);
	}

	@Override
	public IModel<SalespersonRole> createChildModel() {
		return new EntityModel(new SalespersonRole());
	}
	
//	@Override
//	public SalespersonRole initEntity(Long entityId) {
//		if (entityId == null) {
//			return new SalespersonRole();
//		} else {
//			return dao.findById(entityId);
//		}
//	}
	
	@Override
	public void addFormFields(Jsr303Form<SalespersonRole> form) {
//		form.addTextField("companyInfo.name");
		form.addCheckBox("agent");
		form.addCheckBox("agent_sa");
		form.addCheckBox("agent_mb");
		form.addCheckBox("agent_lb");
		form.addCheckBox("partner");
		form.addCheckBox("partner_ec");
		
		form.addDropDownChoice("organisation", organisationDao.findAll());
		
		form.addTextField("division");
		
		form.addTextField("accessCodes");
		
//		final TextField companyInfoIdField 		= form.addTextField("companyInfo.companyId");
//		final TextField companyInfoNameField 	= form.addTextField("companyInfo.companyName");
//		final TextField companyInfoAddressField = form.addTextField("companyInfo.address");
//		final TextField companyInfoZipCodeField	= form.addTextField("companyInfo.zipCode");
//		final TextField companyInfoCityField 	= form.addTextField("companyInfo.city");
//		final TextField companyInfoPhoneField 	= form.addTextField("companyInfo.phone");
//		final TextField companyInfoEmailField 	= form.addTextField("companyInfo.email");
//		form.addTextArea("companyInfo.comment");
		
//		OnChangeAjaxBehavior onChangeAjaxBehavior = new OnChangeAjaxBehavior() {
//            @Override
//            protected void onUpdate(AjaxRequestTarget target) {
//            	SalespersonRole entity = childModel.getObject();
//            	if (!StringUtils.isEmpty(entity.getCompanyInfo().getCompanyId())) {
//	            	if (entity.getCompanyInfo().getCompanyId().length() == 8) {
//	            		CVRService cvrService = new CVRService();
//	            		CVRService.Response response = cvrService.fetchDetails(Integer.valueOf(entity.getCompanyInfo().getCompanyId()));
//	            		if ((response != null) && !StringUtils.isEmpty(response.navn)) {
//		            		entity.getCompanyInfo().setCompanyName(response.navn == null ? "" : response.navn);
//		            		target.add(companyInfoNameField);
//		            		entity.getCompanyInfo().setCity(response.by == null ? "" : response.by);
//		            		target.add(companyInfoCityField);
//		            		entity.getCompanyInfo().setZipCode(response.postnr == null ? "" : response.postnr);
//		            		target.add(companyInfoZipCodeField);
//		            		entity.getCompanyInfo().setAddress(response.adresse == null ? "" : response.adresse);
//		            		target.add(companyInfoAddressField);
//		            		entity.getCompanyInfo().setEmail(response.email == null ? "" : response.email);
//		            		target.add(companyInfoEmailField);
//		            		entity.getCompanyInfo().setPhone(response.telefon == null ? "" : response.telefon);
//		            		target.add(companyInfoPhoneField);
//	            		}
//	            	}
//            	}
//            }
//        };
//		companyInfoIdField.add(onChangeAjaxBehavior);
	}
	
	@Override
	public boolean prepareSave(SalespersonRole entity) {
		if (!entity.isAgent() && !entity.isAgent_sa() && !entity.isAgent_lb() && !entity.isAgent_mb() && !entity.isPartner() && !entity.isPartner_ec()) {
			error("Mindst en sælger kategori skal vælges.");
			return false;
		}
		return true;
	}

	@Override
	public boolean save(SalespersonRole entity, Jsr303Form<SalespersonRole> form) {
		dao.save(entity);
		return true;
	}

	@Override
	public boolean addToParentAndSave(BaseUser parent, SalespersonRole entity) {
		dao.save(entity);
		parent.addRole(entity);
		userDao.save(parent);
		return true;
	}
}
