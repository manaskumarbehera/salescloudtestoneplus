package dk.jyskit.salescloud.application.pages.admin.dashboard;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.model.AdminRole;
import dk.jyskit.salescloud.application.model.SalesmanagerRole;

public class DivisionHelper {

	public static boolean includeAllDivisions() {
		boolean allUsers = false;
		if (CoreSession.get().getActiveRoleClass().equals(AdminRole.class)) {
        	allUsers = true;
        }
	    if (CoreSession.get().getActiveRoleClass().equals(SalesmanagerRole.class)) {
			SalesmanagerRole salesmanager = CoreSession.get().getSalesmanagerRole();
        	allUsers = (SalesmanagerRole.WILDCARD.equals(salesmanager.getDivisions()));
	    }
		return allUsers;
	}

	public static boolean skipDivision(SalesmanagerRole salesmanager, String division) {
		return StringUtils.isEmpty(salesmanager.getDivisions()) || StringUtils.isEmpty(division) || (!ArrayUtils.contains(salesmanager.getDivisions().split(",[ ]*"), division));
	}

}
