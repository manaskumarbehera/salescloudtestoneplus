package dk.jyskit.salescloud.application.services.accesscodes;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import dk.jyskit.salescloud.application.model.Organisation;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.wicket.security.UserSession;

public class AccessCodeChecker {
	public static boolean isCodeActiveForUser(String code) {
		BaseUser user = UserSession.get().getUser();
		SalespersonRole role = (SalespersonRole) user.getRole(SalespersonRole.class);
		if (role != null) {
			if (hasCode(role, code) || ((role.getOrganisation() != null) && (hasCode(role.getOrganisation(), code)))) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasCode(SalespersonRole role, String code) {
		if ("8888".equals(role.getDivision())) {
			if ("xdsl_no_access".equals(code)) {
				return true;
			}
		}
		if (!StringUtils.isEmpty(role.getAccessCodes())) {
			List<String> codes = Arrays.asList(role.getAccessCodes().split("\\s*,\\s*"));
			for (String c : codes) {
				if (code.equalsIgnoreCase(c)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean hasCode(Organisation organisation, String code) {
		if (!StringUtils.isEmpty(organisation.getAccessCodes())) {
			List<String> codes = Arrays.asList(organisation.getAccessCodes().split("\\s*,\\s*"));
			for (String c : codes) {
				if (code.equalsIgnoreCase(c)) {
					return true;
				}
			}
		}
		return false;
	}
}