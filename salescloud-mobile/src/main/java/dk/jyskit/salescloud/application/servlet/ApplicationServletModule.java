package dk.jyskit.salescloud.application.servlet;

import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import dk.jyskit.salescloud.application.MobileSalescloudApplication;
import dk.jyskit.salescloud.application.dao.*;
import dk.jyskit.salescloud.application.dao.jpa.*;
import dk.jyskit.salescloud.application.extensionpoints.*;
import dk.jyskit.salescloud.application.extensions.*;
import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaver;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaverImpl;
import dk.jyskit.salescloud.application.services.supercontract.FakeSuperContractService;
import dk.jyskit.salescloud.application.services.supercontract.SuperContractService;
import dk.jyskit.salescloud.application.services.users.UserEvaluationServiceImpl;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.application.services.users.UserEvaluationService;
import dk.jyskit.waf.application.servlet.JITApplicationServletModule;

public class ApplicationServletModule extends JITApplicationServletModule {

	@Override
	public void guiceInit() {
		// non-core DAOs (see CoreModule for the rest)
		TypeLiteral<SubscriptionDaoImpl> subscriptionDao = new TypeLiteral<SubscriptionDaoImpl>(){};
		bind(new TypeLiteral<SubscriptionDao>(){}).to(subscriptionDao).in(Scopes.SINGLETON);

		TypeLiteral<SegmentDaoImpl> segmentDao = new TypeLiteral<SegmentDaoImpl>(){};
		bind(new TypeLiteral<SegmentDao>(){}).to(segmentDao).in(Scopes.SINGLETON);

		TypeLiteral<SystemUpdateDaoImpl> systemUpdateDaoImpl = new TypeLiteral<SystemUpdateDaoImpl>(){};
		bind(new TypeLiteral<SystemUpdateDao>(){}).to(systemUpdateDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<SystemUpdate>>(){}).to(systemUpdateDaoImpl).in(Scopes.SINGLETON);

		TypeLiteral<MobileCampaignDaoImpl> campaignDaoImpl = new TypeLiteral<MobileCampaignDaoImpl>(){};
		bind(new TypeLiteral<MobileCampaignDao>(){}).to(campaignDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<MobileCampaign>>(){}).to(campaignDaoImpl).in(Scopes.SINGLETON);

		TypeLiteral<MobileContractDaoImpl> contractDaoImpl = new TypeLiteral<MobileContractDaoImpl>(){};
		bind(new TypeLiteral<MobileContractDao>(){}).to(contractDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<MobileContract>>(){}).to(contractDaoImpl).in(Scopes.SINGLETON);
		
		TypeLiteral<OrganisationDaoImpl> partnerCenterDaoImpl = new TypeLiteral<OrganisationDaoImpl>(){};
		bind(new TypeLiteral<OrganisationDao>(){}).to(partnerCenterDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<Organisation>>(){}).to(partnerCenterDaoImpl).in(Scopes.SINGLETON);
		
		TypeLiteral<MobileProductDaoImpl> productDaoImpl = new TypeLiteral<MobileProductDaoImpl>(){};
		bind(new TypeLiteral<MobileProductDao>(){}).to(productDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<MobileProduct>>(){}).to(productDaoImpl).in(Scopes.SINGLETON);
		
		TypeLiteral<MobileContractSummaryDaoImpl> mobileContractSummaryDaoImpl = new TypeLiteral<MobileContractSummaryDaoImpl>(){};
		bind(new TypeLiteral<MobileContractSummaryDao>(){}).to(mobileContractSummaryDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<MobileContractSummary>>(){}).to(mobileContractSummaryDaoImpl).in(Scopes.SINGLETON);
		
		TypeLiteral<MobileProductBundleDaoImpl> productBundleDaoImpl = new TypeLiteral<MobileProductBundleDaoImpl>(){};
		bind(new TypeLiteral<MobileProductBundleDao>(){}).to(productBundleDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<MobileProductBundle>>(){}).to(productBundleDaoImpl).in(Scopes.SINGLETON);
		
		TypeLiteral<SegmentDaoImpl> segmentDaoImpl = new TypeLiteral<SegmentDaoImpl>(){};
		bind(new TypeLiteral<SegmentDao>(){}).to(segmentDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<Segment>>(){}).to(segmentDaoImpl).in(Scopes.SINGLETON);
		
		// Services, factories and stuff...
		bind(PageNavigator.class).to(MobilePageNavigator.class);
		bind(ObjectFactory.class).to(MobileObjectFactory.class);
		bind(CanOrderFilter.class).to(MobileCanOrderFilter.class);
		bind(RoleEditorFactory.class).to(MobileRoleEditorFactory.class);
		bind(CrudListPanelFactory.class).to(MobileCrudListPanelFactory.class);
		bind(ProductRelationTypeProvider.class).to(MobileProductRelationTypeProvider.class);
		bind(OrderLineCountModifier.class).to(MobileOrderLineCountModifier.class);
		bind(SuperContractService.class).to(FakeSuperContractService.class);
		bind(ContractSaver.class).to(ContractSaverImpl.class);
		bind(UserEvaluationService.class).to(UserEvaluationServiceImpl.class);
	}

	@Override
	public Class getWicketApplicationClass() {
		return MobileSalescloudApplication.class;
	}

	@Override
	public String getNamespace() {
		return "salescloud";
	}
}
