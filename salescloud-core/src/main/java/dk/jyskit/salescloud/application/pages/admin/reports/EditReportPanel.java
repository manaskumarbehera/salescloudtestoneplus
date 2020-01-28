package dk.jyskit.salescloud.application.pages.admin.reports;

import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.BusinessAreaDao;
import dk.jyskit.salescloud.application.dao.ReportDao;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.Report;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.crud.AbstractEditPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

public class EditReportPanel extends AbstractEditPanel<Report, BusinessArea> {
	private static final long serialVersionUID = 1L;

	@Inject
	private ReportDao dao;
	
	@Inject
	private BusinessAreaDao businessAreaDao;
	
	public EditReportPanel(CrudContext context, final IModel<Report> childModel, final IModel<BusinessArea> parentModel) {
		super(context, childModel, parentModel);
	}
	
	@Override
	public IModel<Report> createChildModel() {
		return new EntityModel<Report>(new Report());
	}
	
	@Override
	public void addFormFields(Jsr303Form<Report> form) {
		form.addTextField("title");
		form.addTextField("uniqueId");
	}
	
	@Override
	public boolean prepareSave(Report entity) {
		return true;
	}

	@Override
	public boolean save(Report entity, Jsr303Form<Report> form) {
		dao.save(entity);
		return true;
	}

	@Override
	public boolean addToParentAndSave(BusinessArea parent, Report entity) {
		parent.addReport(entity);
		businessAreaDao.save(parent);
		return true;
	}
}
