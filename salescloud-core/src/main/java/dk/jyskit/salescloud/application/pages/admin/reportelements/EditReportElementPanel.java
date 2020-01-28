package dk.jyskit.salescloud.application.pages.admin.reportelements;

import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.ReportDao;
import dk.jyskit.salescloud.application.dao.ReportElementDao;
import dk.jyskit.salescloud.application.model.Report;
import dk.jyskit.salescloud.application.model.ReportElement;
import dk.jyskit.waf.application.model.EntityModel;
import dk.jyskit.waf.wicket.components.forms.jsr303form.Jsr303Form;
import dk.jyskit.waf.wicket.crud.AbstractEditPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

public class EditReportElementPanel extends AbstractEditPanel<ReportElement, Report> {
	private static final long serialVersionUID = 1L;

	@Inject
	private ReportElementDao dao;
	
	@Inject
	private ReportDao reportDao;
	
	public EditReportElementPanel(CrudContext context, final IModel<ReportElement> childModel, final IModel<Report> parentModel) {
		super(context, childModel, parentModel);
	}
	
	@Override
	public IModel<ReportElement> createChildModel() {
		return new EntityModel<ReportElement>(new ReportElement());
	}
	
	@Override
	public void addFormFields(Jsr303Form<ReportElement> form) {
		form.addTextField("name");
		form.addTextArea("value");
	}
	
	@Override
	public boolean prepareSave(ReportElement entity) {
		return true;
	}

	@Override
	public boolean save(ReportElement entity, Jsr303Form<ReportElement> form) {
		dao.save(entity);
		return true;
	}

	@Override
	public boolean addToParentAndSave(Report parent, ReportElement entity) {
		parent.addReportElement(entity);
		reportDao.save(parent);
		return true;
	}
}
