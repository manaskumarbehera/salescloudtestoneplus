package dk.jyskit.salescloud.application.servlet;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

import dk.jyskit.salescloud.application.dao.*;
import dk.jyskit.salescloud.application.dao.jpa.*;
import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.waf.application.dao.Dao;
import dk.jyskit.waf.application.dao.impl.GenericDaoImpl;

public class CoreModule extends AbstractModule implements Module {
	@Override
	protected void configure() {
		// NOTE: In some cases we bind a DAO implementation to both a specific interface (such as ProductDao) and
		// a "generic" one (such as Dao<Product>). The latter is used for generic code for lists.
		
		bind(new TypeLiteral<Dao<AdminRole>>(){}).to(new TypeLiteral<GenericDaoImpl<AdminRole>>(){}).in(Scopes.SINGLETON);
		
		TypeLiteral<BusinessAreaDaoImpl> businessAreaDaoImpl = new TypeLiteral<BusinessAreaDaoImpl>(){};
		bind(new TypeLiteral<BusinessAreaDao>(){}).to(businessAreaDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<BusinessArea>>(){}).to(businessAreaDaoImpl).in(Scopes.SINGLETON);
		
		TypeLiteral<ContractDaoImpl> contractDao = new TypeLiteral<ContractDaoImpl>(){};
		bind(new TypeLiteral<ContractDao>(){}).to(contractDao).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<Contract>>(){}).to(contractDao).in(Scopes.SINGLETON);
		
		TypeLiteral<CampaignDaoImpl> campaignDao = new TypeLiteral<CampaignDaoImpl>(){};
		bind(new TypeLiteral<CampaignDao>(){}).to(campaignDao).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<Campaign>>(){}).to(campaignDao).in(Scopes.SINGLETON);
		
		TypeLiteral<DiscountSchemeDaoImpl> discountSchemeDao = new TypeLiteral<DiscountSchemeDaoImpl>(){};
		bind(new TypeLiteral<DiscountSchemeDao>(){}).to(discountSchemeDao).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<DiscountScheme>>(){}).to(discountSchemeDao).in(Scopes.SINGLETON);

		bind(new TypeLiteral<Dao<ContractCategory>>(){}).to(new TypeLiteral<GenericDaoImpl<ContractCategory>>(){}).in(Scopes.SINGLETON);
		
		TypeLiteral<OrderLineDaoImpl> orderLineDaoImpl = new TypeLiteral<OrderLineDaoImpl>(){};
		bind(new TypeLiteral<OrderLineDao>(){}).to(orderLineDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<OrderLine>>(){}).to(orderLineDaoImpl).in(Scopes.SINGLETON);
		
		TypeLiteral<ProductDaoImpl> productDaoImpl = new TypeLiteral<ProductDaoImpl>(){};
		bind(new TypeLiteral<ProductDao>(){}).to(productDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<Product>>(){}).to(productDaoImpl).in(Scopes.SINGLETON);
		
		TypeLiteral<PageInfoDaoImpl> pageInfoDaoImpl = new TypeLiteral<PageInfoDaoImpl>(){};
		bind(new TypeLiteral<PageInfoDao>(){}).to(pageInfoDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<PageInfo>>(){}).to(pageInfoDaoImpl).in(Scopes.SINGLETON);
		
		TypeLiteral<ProductGroupDaoImpl> productGroupDaoImpl = new TypeLiteral<ProductGroupDaoImpl>(){};
		bind(new TypeLiteral<ProductGroupDao>(){}).to(productGroupDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<ProductGroup>>(){}).to(productGroupDaoImpl).in(Scopes.SINGLETON);
		
		TypeLiteral<ProductBundleDaoImpl> productBundleDaoImpl = new TypeLiteral<ProductBundleDaoImpl>(){};
		bind(new TypeLiteral<ProductBundleDao>(){}).to(productBundleDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<ProductBundle>>(){}).to(productBundleDaoImpl).in(Scopes.SINGLETON);
		
		TypeLiteral<ProductRelationDaoImpl> productRelationDaoImpl = new TypeLiteral<ProductRelationDaoImpl>(){};
		bind(new TypeLiteral<ProductRelationDao>(){}).to(productRelationDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<ProductRelation>>(){}).to(productRelationDaoImpl).in(Scopes.SINGLETON);
		
		TypeLiteral<ReportDaoImpl> reportDaoImpl = new TypeLiteral<ReportDaoImpl>(){};
		bind(new TypeLiteral<ReportDao>(){}).to(reportDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<Report>>(){}).to(reportDaoImpl).in(Scopes.SINGLETON);
		
		TypeLiteral<ReportElementDaoImpl> reportElementDaoImpl = new TypeLiteral<ReportElementDaoImpl>(){};
		bind(new TypeLiteral<ReportElementDao>(){}).to(reportElementDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<ReportElement>>(){}).to(reportElementDaoImpl).in(Scopes.SINGLETON);
		
		TypeLiteral<SalespersonRoleDaoImpl> salespersonRoleDaoImpl = new TypeLiteral<SalespersonRoleDaoImpl>(){};
		bind(new TypeLiteral<SalespersonRoleDao>(){}).to(salespersonRoleDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<SalespersonRole>>(){}).to(salespersonRoleDaoImpl).in(Scopes.SINGLETON);
		
		TypeLiteral<SalesmanagerRoleDaoImpl> salesmanagerRoleDaoImpl = new TypeLiteral<SalesmanagerRoleDaoImpl>(){};
		bind(new TypeLiteral<SalesmanagerRoleDao>(){}).to(salesmanagerRoleDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<SalesmanagerRole>>(){}).to(salesmanagerRoleDaoImpl).in(Scopes.SINGLETON);
		
		TypeLiteral<UserManagerRoleDaoImpl> userManagerRoleDaoImpl = new TypeLiteral<UserManagerRoleDaoImpl>(){};
		bind(new TypeLiteral<UserManagerRoleDao>(){}).to(userManagerRoleDaoImpl).in(Scopes.SINGLETON);
		bind(new TypeLiteral<Dao<UserManagerRole>>(){}).to(userManagerRoleDaoImpl).in(Scopes.SINGLETON);
	}
}
