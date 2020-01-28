package dk.jyskit.salescloud.application.pages.admin.segments;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.extensionpoints.CrudListPanelFactory;
import dk.jyskit.salescloud.application.model.Segment;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.DaoTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions.EntityAction;
import dk.jyskit.waf.wicket.crud.AbstractListPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

@SuppressWarnings("serial")
public class ListSegmentsPanel extends AbstractListPanel<Segment,Void> {

	@Inject
	private Dao<Segment> dao;

	@Inject
	private CrudListPanelFactory crudListPanelFactory;

	public ListSegmentsPanel(CrudContext context) {
		super(context, Segment.class.getSimpleName());
	}

	@Override
	protected BootstrapTableDataProvider<Segment, String> getDataProvider() {
		DaoTableDataProvider<Segment, Dao<Segment>> dataProvider = DaoTableDataProvider.create(dao, "name", SortOrder.ASCENDING);
		dataProvider.setFilterProps("name");
		return dataProvider;
	}

	@Override
	protected void addDataColumns(List<IColumn<Segment, String>> cols) {
		cols.add(createColumn("name"));
		cols.add(createColumn("csvIndex", "csvIndex.short"));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected EntityAction<Segment>[] getRowActions() {
		return new EntityAction[] { 
				getEditAction()
			}; 
	}
	
//	@SuppressWarnings("unchecked")
//	protected EntityAction<Segment>[] getHeaderActions() {
//		EntityAction<?>[] actions = {};
//		return (EntityAction<Segment>[]) actions;
//	}
	
	@Override
	protected Panel createEditPanel(CrudContext context, IModel<Segment> model) {
		return new EditSegmentPanel(context, model);
	}

	@Override
	protected void deleteObject(Segment entity) {
		dao.delete(dao.findById(entity.getId()));
	}
	
	@Override
	protected void saveEntityWithNewState(Segment entity) {
		dao.save(entity);
	}
}
