package dk.jyskit.salescloud.application.pages.admin.productrelations;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.extensionpoints.ProductRelationTypeProvider;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.ProductRelation;
import dk.jyskit.salescloud.application.model.ProductRelationType;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.application.model.BaseRole;
import dk.jyskit.waf.utils.filter.Equal;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.EntityLabelStrategy;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.DaoTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions.EntityAction;
import dk.jyskit.waf.wicket.crud.AbstractListPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

@SuppressWarnings("serial")
public class ListProductRelationsPanel extends AbstractListPanel<ProductRelation,BusinessArea> {

	@Inject
	private Dao<ProductRelation> childDao;
	
	@Inject
	private Dao<BusinessArea> parentDao;
	
	@Inject
	private ProductRelationTypeProvider productRelationTypeProvider;

	public ListProductRelationsPanel(CrudContext context, IModel<BusinessArea> parentModel) {
		super(context, ProductRelation.class.getSimpleName(), parentModel);
	}

	@Override
	protected BootstrapTableDataProvider<ProductRelation, String> getDataProvider() {
		DaoTableDataProvider<ProductRelation, Dao<ProductRelation>> dataProvider = 
				DaoTableDataProvider.create(childDao, "relationTypeId", SortOrder.ASCENDING, 
				new Equal("businessArea", getParentModel().getObject()));
		return dataProvider;
	}

	@Override
	protected void addDataColumns(List<IColumn<ProductRelation, String>> cols) {
		cols.add(new AbstractColumn<ProductRelation, String>(new Model("Relationstype"), "relationTypeId") {
			@Override
			public void populateItem(Item<ICellPopulator<ProductRelation>> cellItem, String componentId, IModel<ProductRelation> rowModel) {
				ProductRelationType relationType = productRelationTypeProvider.getById(rowModel.getObject().getRelationTypeId());
				cellItem.add(new Label(componentId, relationType.getDisplayText()));
			}
		});
//		cols.add(createColumn("relationType.displayText", "relationType"));
		cols.add(createNonSortColumn("productNames", "products"));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected EntityAction<ProductRelation>[] getRowActions() {
		return new EntityAction[] { getEditAction(), getDeleteAction() }; 
	}
	
//	private EntityAction getListProductRelationElementsAction() {
//		CrudEntityAction<ProductRelation> action = new CrudEntityAction<ProductRelation>(context, getKey("products.list.link"), getKey("products.list.tooltip"), FontAwesomeIconType.table) {
//			@Override
//			public Panel createPanel(String panelWicketId, IModel<ProductRelation> model) {
//				return new ListProductsPanel(context, model);
//			}
//		};
//		return action;
//	}

	@Override
	protected Panel createEditPanel(CrudContext context, IModel<ProductRelation> model) {
		return new EditProductRelationPanel(context, model, getParentModel());
	}

	@Override
	protected void deleteObject(ProductRelation entity) {
		getParentModel().getObject().removeProductRelation(entity);
		parentDao.save(getParentModel().getObject());
		
		childDao.delete(childDao.findById(entity.getId()));
	}

	@Override
	protected void saveEntityWithNewState(ProductRelation entity) {
		childDao.save(entity);
	}
}
