package dk.jyskit.salescloud.application.pages.admin.businessarea;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.BusinessAreaDao;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.crud.AbstractEditPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

public class EditBusinessAreaPanel extends AbstractEditPanel<BusinessArea, Void> {

	@Inject
	private BusinessAreaDao childDao;
	
	public EditBusinessAreaPanel(CrudContext context, IModel<BusinessArea> childModel) {
		super(context, childModel);
	}
	
	@Override
	public IModel<BusinessArea> createChildModel() {
		return new EntityModel<BusinessArea>(new BusinessArea());
	}

	public void addFormFields(Jsr303Form<BusinessArea> form) {
		TextField nameField = form.addTextField("name");
		nameField.setEnabled(false);
		form.addTextArea("introText");
		form.addCheckBox("cumulativeDiscounts").setEnabled(false);
		form.addTextArea("standardDiscountMatrix");
		form.addNumberTextField("provisionFactorGeneral");
		form.addNumberTextField("provisionFactorXDSL");
	}
	
	@Override
	public boolean prepareSave(BusinessArea entity) {
		return true;
	}

	@Override
	public boolean save(BusinessArea entity, Jsr303Form<BusinessArea> form) {
		childDao.save(entity);
		return true;
	}

	@Override
	public boolean addToParentAndSave(Void parent, BusinessArea entity) {
		// no parent
		return true;
	}
}
