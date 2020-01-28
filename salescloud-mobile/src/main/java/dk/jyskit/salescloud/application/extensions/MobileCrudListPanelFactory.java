package dk.jyskit.salescloud.application.extensions;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import dk.jyskit.salescloud.application.extensionpoints.defaultimpl.CoreCrudListPanelFactory;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.Campaign;
import dk.jyskit.salescloud.application.pages.admin.campaigns.ListCampaignsPanel;
import dk.jyskit.salescloud.application.pages.admin.sortgroups.SortProductGroupsPanel;
import dk.jyskit.salescloud.application.pages.admin.sortproductbundles.SortProductBundlesPanel;
import dk.jyskit.salescloud.application.pages.admin.sortproducts.SortProductsPanel;
import dk.jyskit.waf.wicket.crud.AbstractListPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

public class MobileCrudListPanelFactory extends CoreCrudListPanelFactory {
	@Override
	public AbstractListPanel<Campaign, BusinessArea> getCampaignsListPanel(CrudContext context, IModel<BusinessArea> parentModel) {
		AbstractListPanel<Campaign, BusinessArea> listCampaignsPanel = new ListCampaignsPanel(context, parentModel);
		return listCampaignsPanel;
	}
	
	@Override
	public Panel getSortProductGroupsPanel(CrudContext context, IModel<BusinessArea> parentModel) {
		return new SortProductGroupsPanel(context, parentModel);
	}
	
	@Override
	public Panel getSortProductsPanel(CrudContext context, IModel<BusinessArea> parentModel) {
		return new SortProductsPanel(context, parentModel);
	}
	
	@Override
	public Panel getSortProductBundlesPanel(CrudContext context, IModel<Campaign> parentModel) {
		return new SortProductBundlesPanel(context, parentModel);
	}
}
