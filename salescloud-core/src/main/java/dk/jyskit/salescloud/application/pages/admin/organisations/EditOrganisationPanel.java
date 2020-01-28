package dk.jyskit.salescloud.application.pages.admin.organisations;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.hibernate.validator.constraints.Email;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.OrganisationDao;
import dk.jyskit.salescloud.application.model.Organisation;
import dk.jyskit.salescloud.application.model.OrganisationType;
import dk.jyskit.salescloud.application.model.PaymentFrequency;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.crud.AbstractEditPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;
import lombok.NonNull;

public class EditOrganisationPanel extends AbstractEditPanel<Organisation, Void> {

	@Inject
	private OrganisationDao childDao;
	
	public EditOrganisationPanel(CrudContext context, IModel<Organisation> childModel) {
		super(context, childModel);
	}
	
	@Override
	public IModel<Organisation> createChildModel() {
		return new EntityModel<Organisation>(new Organisation());
	}

	public void addFormFields(Jsr303Form<Organisation> form) {
		form.addTextField("companyName");
		form.addDropDownChoice("type", OrganisationType.valuesAsList());
		form.addTextField("organisationId");
		form.addTextField("companyId");
		form.addTextField("phone");
		form.addTextField("email");
		form.addTextField("supportPhone");
		form.addTextField("supportEmail");
		form.addTextField("address");
		form.addTextField("zipCode");
		form.addTextField("city");
//		form.addTextArea("comment");
		form.addTextField("accessCodes");
	}
	
	@Override
	public boolean prepareSave(Organisation entity) {
		return true;
	}

	@Override
	public boolean save(Organisation entity, Jsr303Form<Organisation> form) {
		childDao.save(entity);
		return true;
	}

	@Override
	public boolean addToParentAndSave(Void parent, Organisation entity) {
		// no parent
		return true;
	}
}
