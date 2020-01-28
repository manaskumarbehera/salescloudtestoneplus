package dk.jyskit.salescloud.application.pages.admin.campaigns;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.Campaign;
import dk.jyskit.waf.application.model.EntityState;
import dk.jyskit.salescloud.application.pages.admin.campaignproducts.ListCampaignProductsPanel;
import dk.jyskit.salescloud.application.pages.admin.productbundles.ListProductBundlesPanel;
import dk.jyskit.salescloud.application.pages.admin.sortproductbundles.SortProductBundlesPanel;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.utils.filter.And;
import dk.jyskit.waf.utils.filter.Equal;
import dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy.EntityLabelStrategy;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.DaoTableDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.columns.actions.EntityAction;
import dk.jyskit.waf.wicket.crud.AbstractListPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;
import dk.jyskit.waf.wicket.crud.CrudEntityAction;

@SuppressWarnings("serial")
public class ListCampaignsPanel extends AbstractListPanel<Campaign,BusinessArea> {

	@Inject
	private Dao<Campaign> dao;

	public ListCampaignsPanel(CrudContext context, IModel<BusinessArea> parentModel) {
		super(context, new EntityLabelStrategy("Campaign"), "Campaign", parentModel, new int[] { EntityState.ACTIVE_VAL, EntityState.SOFTDELETE_VAL });
	}

	@Override
	protected BootstrapTableDataProvider<Campaign, String> getDataProvider() {
		DaoTableDataProvider<Campaign, Dao<Campaign>> dataProvider = 
				DaoTableDataProvider.create(dao, "creationDate", SortOrder.DESCENDING, new Equal("businessArea", getParentModel().getObject()));
		dataProvider.setFilterProps("name");
		return dataProvider;
	}

	@Override
	protected void addDataColumns(List<IColumn<Campaign, String>> cols) {
		cols.add(createColumn("name"));
		cols.add(createNonSortColumn("fromDate", "fromDate"));
		cols.add(createNonSortColumn("toDate", "toDate"));
	}

	@SuppressWarnings("unchecked")
	protected EntityAction<Campaign>[] getRowActions() {
		EntityAction<?>[] actions = {getEditAction(), getDeleteAction(), getListProductBundlesAction(), getListCampaignProductsAction(), getSortProductBundlesAction()};
		return (EntityAction<Campaign>[]) actions;
	}
	
	protected CrudEntityAction<Campaign> getListProductBundlesAction() {
		CrudEntityAction<Campaign> action = new CrudEntityAction<Campaign>(context, getKey("productbundles.list.link"), getKey("productbundles.list.tooltip"), FontAwesomeIconType.table) {
			@Override
			public Panel createPanel(CrudContext context, IModel<Campaign> model) {
				return new ListProductBundlesPanel(context, model);
			}
		};
		return action;
	}
	
	protected CrudEntityAction<Campaign> getListCampaignProductsAction() {
		CrudEntityAction<Campaign> action = new CrudEntityAction<Campaign>(context, getKey("campaignproducts.list.link"), getKey("campaignproducts.list.tooltip"), FontAwesomeIconType.table) {
			@Override
			public Panel createPanel(CrudContext context, IModel<Campaign> model) {
				return new ListCampaignProductsPanel(context, model);
			}
		};
		return action;
	}
	
	private CrudEntityAction getSortProductBundlesAction() {
		CrudEntityAction<Campaign> action = new CrudEntityAction<Campaign>(context, getKey("sortproductbundles.list.link"), getKey("sortproductbundles.list.tooltip"), FontAwesomeIconType.sort) {
			@Override
			public Panel createPanel(CrudContext context, IModel<Campaign> model) {
				return new SortProductBundlesPanel(context, model);
			}
		};
		return action;
	}
	
	@Override
	protected Panel createEditPanel(CrudContext context, IModel<Campaign> entityModel) {
		return new EditCampaignPanel(context, entityModel, getParentModel());
	}

	@Override
	protected void deleteObject(Campaign entity) {
		Campaign campaign = dao.findById(entity.getId());
		campaign.setEntityState(EntityState.SOFTDELETE);
		dao.save(campaign);
	}
	
	@Override
	protected void saveEntityWithNewState(Campaign entity) {
		dao.save(entity);
	}
}
