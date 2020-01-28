package dk.jyskit.salescloud.application.pages.admin.campaignproducts;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.CampaignDao;
import dk.jyskit.salescloud.application.model.Campaign;
import dk.jyskit.salescloud.application.model.CampaignProductRelation;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapListDataProvider;
import dk.jyskit.waf.wicket.components.tables.bootstraptable.BootstrapTableDataProvider;
import dk.jyskit.waf.wicket.crud.AbstractListPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

@SuppressWarnings("serial")
public class ListCampaignProductsPanel extends AbstractListPanel<CampaignProductRelation,Campaign> {

	@Inject
	private CampaignDao parentDao;

	public ListCampaignProductsPanel(CrudContext context, IModel<Campaign> parentModel) {
		super(context, CampaignProductRelation.class.getSimpleName(), parentModel);
	}

	@Override
	protected BootstrapTableDataProvider<CampaignProductRelation, String> getDataProvider() {
		IModel<List<CampaignProductRelation>> model = new AbstractReadOnlyModel<List<CampaignProductRelation>>() {
			@Override
			public List<CampaignProductRelation> getObject() {
				return getParentModel().getObject().getCampaignProducts();
			}
		};
		return new BootstrapListDataProvider<CampaignProductRelation>(model, "product.publicName", "product.productId");
	}

	@Override
	protected void addDataColumns(List<IColumn<CampaignProductRelation, String>> cols) {
		cols.add(createColumn("product.productId"));
		cols.add(createColumn("product.publicName"));
	}
	
	@Override
	protected Panel createEditPanel(CrudContext context, IModel<CampaignProductRelation> childModel) {
		return new EditProductInCampaignPanel(context, childModel, getParentModel());
	}

	@Override
	protected void deleteObject(CampaignProductRelation entity) {
		getParentModel().getObject().removeCampaignProductRelation(entity.getProduct());
		parentDao.save(getParentModel().getObject());
	}
	
	@Override
	protected void saveEntityWithNewState(CampaignProductRelation entity) {
		// never called
	}
}
