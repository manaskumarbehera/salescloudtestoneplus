package dk.jyskit.salescloud.application.pages.admin.reports;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.Report;
import dk.jyskit.salescloud.application.pages.admin.reportelements.ListReportElementsPanel;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.utils.filter.Equal;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.DaoTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions.EntityAction;
import dk.jyskit.waf.wicket.crud.AbstractListPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;
import dk.jyskit.waf.wicket.crud.CrudEntityAction;

@SuppressWarnings("serial")
public class ListReportsPanel extends AbstractListPanel<Report,BusinessArea> {

	@Inject
	private Dao<Report> dao;

	public ListReportsPanel(CrudContext context, IModel<BusinessArea> parentModel) {
		super(context, Report.class.getSimpleName(), parentModel);
	}

	@Override
	protected BootstrapTableDataProvider<Report, String> getDataProvider() {
		DaoTableDataProvider<Report, Dao<Report>> dataProvider = 
				DaoTableDataProvider.create(dao, "title", SortOrder.ASCENDING, new Equal("businessArea", getParentModel().getObject()));
		dataProvider.setFilterProps("title");
		return dataProvider;
	}

	@Override
	protected void addDataColumns(List<IColumn<Report, String>> cols) {
		cols.add(createColumn("title"));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected EntityAction<Report>[] getRowActions() {
		return new EntityAction[] { getEditAction(), getDeleteAction(), getListReportElementsAction() }; 
	}
	
	private EntityAction getListReportElementsAction() {
		CrudEntityAction<Report> action = new CrudEntityAction<Report>(context, getKey("reportelements.list.link"), getKey("reportelements.list.tooltip"), FontAwesomeIconType.table) {
			@Override
			public Panel createPanel(CrudContext context, IModel<Report> model) {
				return new ListReportElementsPanel(context, model);
			}
		};
		return action;
	}

	@Override
	protected Panel createEditPanel(CrudContext context, IModel<Report> model) {
		return new EditReportPanel(context, model, getParentModel());
	}

	@Override
	protected void deleteObject(Report entity) {
		dao.delete(entity);
	}
	
	@Override
	protected void saveEntityWithNewState(Report entity) {
		dao.save(entity);
	}
}
