package dk.jyskit.salescloud.application.extensionpoints;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.Campaign;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.salescloud.application.model.Report;
import dk.jyskit.waf.wicket.crud.AbstractListPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

public interface CrudListPanelFactory {
	AbstractListPanel<Campaign,BusinessArea> getCampaignsListPanel(CrudContext context, IModel<BusinessArea> parentModel); 
	AbstractListPanel<Report,BusinessArea> getReportsListPanel(CrudContext context, IModel<BusinessArea> parentModel);
	AbstractListPanel<ProductGroup,BusinessArea> getProductGroupsListPanel(CrudContext context, IModel<BusinessArea> parentModel);
	
	Panel getSortProductGroupsPanel(CrudContext context, IModel<BusinessArea> parentModel);
	Panel getSortProductsPanel(CrudContext context, IModel<BusinessArea> parentModel);
	Panel getSortProductBundlesPanel(CrudContext context, IModel<Campaign> parentModel);
}
