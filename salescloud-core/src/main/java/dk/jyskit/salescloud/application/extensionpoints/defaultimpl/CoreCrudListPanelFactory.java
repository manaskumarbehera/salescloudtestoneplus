package dk.jyskit.salescloud.application.extensionpoints.defaultimpl;

import org.apache.wicket.model.IModel;

import dk.jyskit.salescloud.application.extensionpoints.CrudListPanelFactory;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.Campaign;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.salescloud.application.model.Report;
import dk.jyskit.salescloud.application.pages.admin.productgroups.ListProductGroupsPanel;
import dk.jyskit.salescloud.application.pages.admin.reports.ListReportsPanel;
import dk.jyskit.waf.wicket.crud.AbstractListPanel;
import dk.jyskit.waf.wicket.crud.CrudContext;

public abstract class CoreCrudListPanelFactory implements CrudListPanelFactory {

	@Override
	public AbstractListPanel<Campaign, BusinessArea> getCampaignsListPanel(CrudContext context, IModel<BusinessArea> parentModel) {
		return null;
	}

	@Override
	public AbstractListPanel<Report, BusinessArea> getReportsListPanel(CrudContext context,
			IModel<BusinessArea> parentModel) {
		return new ListReportsPanel(context, parentModel);
	}

	@Override
	public AbstractListPanel<ProductGroup, BusinessArea> getProductGroupsListPanel(CrudContext context, IModel<BusinessArea> parentModel) {
		return new ListProductGroupsPanel(context, parentModel);
	}

}
