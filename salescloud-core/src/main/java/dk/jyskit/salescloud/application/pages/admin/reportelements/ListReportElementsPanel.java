package dk.jyskit.salescloud.application.pages.admin.reportelements;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.model.Report;
import dk.jyskit.salescloud.application.model.ReportElement;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.utils.filter.Equal;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.EntityLabelStrategy;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.DaoTableDataProvider;
import dk.jyskit.waf.wicket.crud.AbstractListPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

@SuppressWarnings("serial")
public class ListReportElementsPanel extends AbstractListPanel<ReportElement,Report> {

	@Inject
	private Dao<ReportElement> dao;

	public ListReportElementsPanel(CrudContext context, IModel<Report> parentModel) {
		super(context, ReportElement.class.getSimpleName(), parentModel);
	}

	@Override
	protected BootstrapTableDataProvider<ReportElement, String> getDataProvider() {
		DaoTableDataProvider<ReportElement, Dao<ReportElement>> dataProvider = 
				DaoTableDataProvider.create(dao, "name", SortOrder.ASCENDING, new Equal("report", getParentModel().getObject()));
		dataProvider.setFilterProps("name");
		return dataProvider;
	}

	@Override
	protected void addDataColumns(List<IColumn<ReportElement, String>> cols) {
		cols.add(createColumn("name"));
	}

	@Override
	protected Panel createEditPanel(CrudContext context, IModel<ReportElement> model) {
		return new EditReportElementPanel(context, model, getParentModel());
	}
	
	@Override
	protected void deleteObject(ReportElement entity) {
		getParentModel().getObject().removeReportElement(entity);
		dao.delete(entity);
	}
	
	@Override
	protected void saveEntityWithNewState(ReportElement entity) {
		dao.save(entity);
	}
}
