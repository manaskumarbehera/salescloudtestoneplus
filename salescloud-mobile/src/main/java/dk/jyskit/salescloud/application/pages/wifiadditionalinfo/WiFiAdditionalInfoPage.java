package dk.jyskit.salescloud.application.pages.wifiadditionalinfo;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.dao.PageInfoDao;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.PageInfo;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.pages.MobilePageIds;
import dk.jyskit.salescloud.application.pages.sales.content.ContentPage;
import dk.jyskit.waf.utils.guice.Lookup;

@AuthorizeInstantiation({ AdminRole.ROLE_NAME, SalespersonRole.ROLE_NAME })
@SuppressWarnings("serial")
public class WiFiAdditionalInfoPage extends ContentPage {
	public WiFiAdditionalInfoPage(PageParameters parameters) {
		super(parameters, Lookup.lookup(PageInfoDao.class).findByPageId(CoreSession.get().getBusinessAreaEntityId(), MobilePageIds.MOBILE_WIFI_ADDITIONAL_INFO));
	}
	
	@Override
	protected Panel getMainPanel(String wicketId, PageParameters parameters, PageInfo pageInfo) {
//		final Breadcrumb breadcrumb = new Breadcrumb("breadcrumb") {
//			@Override
//			protected void onConfigure() {
//				setVisible(allBreadCrumbParticipants().size() > 1);
//			}
//		};
//		breadcrumb.setOutputMarkupId(true);
//		breadcrumb.setOutputMarkupPlaceholderTag(true);
//		add(breadcrumb);
//		
//		CrudContext crudContext = new CrudContext(this, breadcrumb);
//		crudContext.setPanelWicketId(wicketId);
//		crudContext.setNamespace("WiFiAdditionalInfo");
//		
//		ValueMap valueMap = new ValueMap(); 
//		IModel<ValueMap> childModel = Model.of(valueMap);
//		IModel<MobileContract> parentModel = EntityModel.forEntity(MobileSession.get().getContract());
//		
//		return new EditWifiAdditionalInfoPanel(crudContext, childModel, parentModel);
		
		return new EditWifiAdditionalInfoPanel(wicketId);
	}
}
