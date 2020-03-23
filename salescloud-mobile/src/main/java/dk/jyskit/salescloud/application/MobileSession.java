package dk.jyskit.salescloud.application;

import java.util.HashMap;
import java.util.Map;

import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.waf.wicket.utils.DateUtils;
import org.apache.wicket.request.Request;

import dk.jyskit.salescloud.application.dao.ProductBundleDao;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.utils.guice.Lookup;
import dk.jyskit.waf.wicket.security.UserSession;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true, of={})
public class MobileSession extends CoreSession {
	private Long selectedMixBundleId;
	private boolean externalAccessMode;
	private boolean customerLoggedIn;
	private boolean implementerLoggedIn;
	private Integer dumpYear;
	private Integer dumpMonth;

	// Used for partner hardware products
	private Map<Long, Amounts> customPriceMap = new HashMap<>();
	
	public MobileSession(Request request) {
		super(request);
	}

	public static MobileSession get() {
		return (MobileSession) UserSession.get();
	}
	
	@Override
	public Class[] getRolesByPriority() {
		return new Class[] {AdminRole.class, UserManagerRole.class, SalesmanagerRole.class, SalespersonRole.class};
	}
	
	public MobileContract getContract() {
		return (MobileContract) super.getContract();
	}
	
	public MobileProductBundle getSelectedMixBundle() {
		return selectedMixBundleId == null ? null : (MobileProductBundle) Lookup.lookup(ProductBundleDao.class).findById(selectedMixBundleId);
	}

	public void setSelectedMixBundle(MobileProductBundle productBundle) {
		this.selectedMixBundleId = productBundle == null ? null : productBundle.getId();
	}
	
	public String toString() {
		BaseUser user = getUser();
		if (user == null) {
			return "<user not logged in>";
		}
		return user.getEmail();
	}

	@Override
	public void setBusinessArea(BusinessArea businessArea) {
		super.setBusinessArea(businessArea);
		setExternalAccessMode(false);
	}

	@Deprecated  // Dangerous
	public boolean isBusinessArea(long ... businessAreaEntityIds) {
		for (long id : businessAreaEntityIds) {
			if (businessAreaEntityId.equals(Long.valueOf(id))) {
				return true;
			}
		}
		return false;
	}

	public void setUser(BaseUser user) {
		super.setUser(user);
		externalAccessMode 	= false;
		customerLoggedIn 	= false;
		implementerLoggedIn	= false;
	}

	public boolean isBusinessAreaOnePlus() {
		MobileContract contract = getContract();
		return contract == null ? false : contract.getBusinessArea().getBusinessAreaId() == BusinessAreas.ONE_PLUS;
	}

	public boolean isBusinessAreaTdcWorks() {
		MobileContract contract = getContract();
		return contract == null ? false : contract.getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_WORKS;
	}

	public boolean isBusinessAreaTdcOffice() {
		MobileContract contract = getContract();
		return contract == null ? false : contract.getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_OFFICE;
	}

	public boolean userIsPartner() {
		BaseUser user = getUser();
		if (user != null) {
			SalespersonRole salespersonRole = (SalespersonRole) user.getRole(SalespersonRole.class);
			return salespersonRole.isPartner() || salespersonRole.isPartner_ec() ||
					((salespersonRole.getOrganisation() != null) && (salespersonRole.getOrganisation().getType().equals(OrganisationType.PARTNER_CENTER)));
		}
		return false;
	}

	public boolean userIsPartnerEC() {
		BaseUser user = getUser();
		if (user != null) {
			SalespersonRole salespersonRole = (SalespersonRole) user.getRole(SalespersonRole.class);
			return salespersonRole.isPartner_ec() ||
					((salespersonRole.getOrganisation() != null) && (salespersonRole.getOrganisation().getType().equals(OrganisationType.PARTNER_CENTER)));
		}
		return false;
	}

	public void updateMonthToDump() {
		dumpMonth -= 1;
		if (dumpMonth < 1) {
			dumpYear -= 1;
			dumpMonth = 12;
		}
	}

	public Integer getDumpYear() {
		if (dumpYear == null) {
			dumpYear = DateUtils.getYearNow();
		}
		return dumpYear;
	}

	public Integer getDumpMonth() {
		if (dumpMonth == null) {
			dumpMonth = DateUtils.getMonthNow();
		}
		return dumpMonth;
	}
}
