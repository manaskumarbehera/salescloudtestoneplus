package dk.jyskit.salescloud.application;

import java.util.Locale;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.apache.wicket.request.Request;

import dk.jyskit.salescloud.application.dao.BusinessAreaDao;
import dk.jyskit.salescloud.application.dao.ContractDao;
import dk.jyskit.salescloud.application.dao.ProductGroupDao;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.Contract;
import dk.jyskit.salescloud.application.model.DiscountPoint;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.salescloud.application.model.SalesmanagerRole;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.model.UserManagerRole;
import dk.jyskit.waf.application.model.BaseRole;
import dk.jyskit.waf.utils.guice.Lookup;
import dk.jyskit.waf.wicket.security.UserSession;

@Data
@EqualsAndHashCode(callSuper=true, of={})
public abstract class CoreSession extends UserSession {
	protected Long businessAreaEntityId;
	protected Long contractId;
	protected Long productGroupId;
	protected DiscountPoint discountPointNetwork;
	protected DiscountPoint discountPointNonNetwork;

	private Integer pricingSubIndex;

	private static boolean maintenanceMode;
	private static boolean maintenanceModeWarning;
	private static String maintenanceText;
	
	public CoreSession(Request request) {
		super(request);
		
		setLocale(new Locale("da", "DK"));
	}

	public static CoreSession get() {
		return (CoreSession) UserSession.get();
	}
	
	public SalespersonRole getSalespersonRole() {
		for (BaseRole role : getUser().getBaseRoleList()) {
			if (role instanceof SalespersonRole) {
				return (SalespersonRole) role;
			}
		}
		return null;
	}

	public SalesmanagerRole getSalesmanagerRole() {
		for (BaseRole role : getUser().getBaseRoleList()) {
			if (role instanceof SalesmanagerRole) {
				return (SalesmanagerRole) role;
			}
		}
		return null;
	}

	public UserManagerRole getUserManagerRole() {
		for (BaseRole role : getUser().getBaseRoleList()) {
			if (role instanceof UserManagerRole) {
				return (UserManagerRole) role;
			}
		}
		return null;
	}

	public Contract getContract() {
		return contractId == null ? null : Lookup.lookup(ContractDao.class).findById(contractId);
	}

	public void setContract(Contract contract) {
		this.contractId = (contract == null ? null : contract.getId());
	}

	public BusinessArea getBusinessArea() {
		return businessAreaEntityId == null ? null : Lookup.lookup(BusinessAreaDao.class).findById(businessAreaEntityId);
	}

	public void setBusinessArea(BusinessArea businessArea) {
		if (businessArea == null) {
			this.businessAreaEntityId = null;
		} else {
			this.businessAreaEntityId = businessArea.getId();
			setStyle(businessArea.getTypeId());
			if (businessArea.getProductGroups().size() == 0) {
				this.productGroupId = null;
			} else {
				this.productGroupId = businessArea.getProductGroups().get(0).getId();
			}
		}
	}
	
	public ProductGroup getProductGroup() {
		return productGroupId == null ? null : Lookup.lookup(ProductGroupDao.class).findById(productGroupId);
	}

	public void setProductGroup(ProductGroup productGroup) {
		this.productGroupId = productGroup == null ? null : productGroup.getId();
	}

	public static boolean isMaintenanceMode() {
		return maintenanceMode;
	}

	public static boolean isMaintenanceModeWarning() {
		return maintenanceModeWarning;
	}

	public static void setMaintenanceMode(boolean maintenanceMode) {
		CoreSession.maintenanceMode = maintenanceMode;
	}

	public static void setMaintenanceModeWarning(boolean maintenanceModeWarning) {
		CoreSession.maintenanceModeWarning = maintenanceModeWarning;
	}

	public static String getMaintenanceText() {
		return maintenanceText;
	}

	public static void setMaintenanceText(String maintenanceText) {
		CoreSession.maintenanceText = maintenanceText;
	}

	public void setPricingSubIndex(Integer subIndex) {
		this.pricingSubIndex = subIndex;
	}
}
