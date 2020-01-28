package dk.jyskit.salescloud.application.pages.sales.productgroupconfiguration;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.dao.PageInfoDao;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.PageInfo;
import dk.jyskit.salescloud.application.pages.CorePageIds;
import dk.jyskit.salescloud.application.pages.sales.content.ContentPage;
import dk.jyskit.waf.utils.guice.Lookup;

@AuthorizeInstantiation({ AdminRole.ROLE_NAME })
@SuppressWarnings("serial")
public class ProductGroupConfigurationPage extends ContentPage {
	public ProductGroupConfigurationPage(PageParameters parameters) {
		super(parameters, Lookup.lookup(PageInfoDao.class)
				.findByPageId(CoreSession.get().getBusinessAreaEntityId(), CorePageIds.SALES_PRODUCTGROUP_CONFIGURATION));
	}
	
	@Override
	protected Panel getMainPanel(String wicketId, PageParameters parameters, PageInfo pageInfo) {
//		Long productGroupId = parameters.get("productGroupId").toLong();
//		if (productGroupId != null) {
//			CoreSession.get().setProductGroupId(productGroupId);
//		}
		return new ConfiguratorTabPanel(wicketId);
	}
}
