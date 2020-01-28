package dk.jyskit.salescloud.application.apis.auth;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.model.BaseRole;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.utils.encryption.SimpleStringCipher;
import dk.jyskit.waf.utils.guice.Lookup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthUtils {
	public static String checkToken(String token) {
		if (!StringUtils.isEmpty(token)) {
			try {
				UsernamePasswordTime usernamePasswordTime = getUsernamePasswordTime(token);
				if (System.currentTimeMillis() - Long.valueOf(usernamePasswordTime.getTime()) > 1000 * 60 * 60 * 12) {
					return "Token has expired";
				} else {
					if (getUser(usernamePasswordTime) == null) {
						return "Username or password is incorrect";
					} else {
						return null; // OK
					}
				}
			} catch (Exception e) {
				// Intensionally blank
			}
		}
		return "Bad token";
	}

	public static BaseUser getUser(UsernamePasswordTime usernamePasswordTime) throws Exception {
		UserDao userDao = (UserDao) Lookup.lookup(UserDao.class);
		List<BaseUser> usersWithUsername = userDao.findByUsername(usernamePasswordTime.getUsername());
		for (BaseUser user : usersWithUsername) {
			if (user.isActive()) {
				if (user.isAuthenticatedBy(usernamePasswordTime.getPassword())) {
					for (int i = 0; i < user.getBaseRoleList().size(); i++) {
						if (user.getBaseRoleList().get(i) instanceof SalespersonRole) {
							return user;
						}
					}
					continue;
				}
			} else {
				continue;
			}
		}
		return null;
	}
	
	public static UsernamePasswordTime getUsernamePasswordTime(String token) throws Exception {
		UsernamePasswordTime usernamePasswordTime = new UsernamePasswordTime();
		String s = SimpleStringCipher.decrypt(token);
		String[] array = s.split("¤");
		usernamePasswordTime.setUsername(array[0]);
		usernamePasswordTime.setPassword(array[1]);
		usernamePasswordTime.setTime(Long.valueOf(array[2]));
		return usernamePasswordTime;
	}

	public static AuthorizationSuccess buildResponse(UsernamePasswordTime usernamePasswordTime, String token) throws Exception {
		AuthorizationSuccess success = null;
		UserDao userDao = (UserDao) Lookup.lookup(UserDao.class);
		List<BaseUser> usersWithUsername = userDao.findByUsername(usernamePasswordTime.getUsername());
		for (BaseUser user : usersWithUsername) {
			if (user.isActive()) {
				if (user.isAuthenticatedBy(usernamePasswordTime.getPassword())) {
					log.info("Password is ok for user: " + user.getUsername() + " / " + user.getEmail());
					success = new AuthorizationSuccess();
					if (token == null) {
						success.setToken(SimpleStringCipher.encrypt(usernamePasswordTime.getUsername() + '¤' + usernamePasswordTime.getPassword() + '¤' + System.currentTimeMillis()));
					} else {
						success.setToken(token);
					}
					success.setRoles(new String[user.getBaseRoleList().size()]);
					for (int i = 0; i < user.getBaseRoleList().size(); i++) {
						BaseRole role = user.getBaseRoleList().get(i);
						success.getRoles()[i] = role.getRoleName();
						if (role instanceof SalespersonRole) {
							SalespersonRole salespersonRole = (SalespersonRole) role;
							success.setAgent(salespersonRole.isAgent());
							success.setAgent_lb(salespersonRole.isAgent_lb());
							success.setAgent_mb(salespersonRole.isAgent_mb());
							success.setAgent_sa(salespersonRole.isAgent_sa());
							success.setPartner(salespersonRole.isPartner());
							success.setPartner_ec(salespersonRole.isPartner_ec());
							success.setUserId(user.getId());
							success.setFirstName(user.getFirstName());
							success.setLastName(user.getLastName());
							success.setFullName(user.getFullName());
							success.setPersonalPhone(user.getSmsPhone());
							success.setEmail(user.getEmail());
							success.setAgent(salespersonRole.isAgent());
							Organisation organisation = new Organisation();
							if (salespersonRole.getOrganisation() != null) {
								organisation.setAddress(salespersonRole.getOrganisation().getAddress());
								organisation.setCity(salespersonRole.getOrganisation().getCity());
								organisation.setComment(salespersonRole.getOrganisation().getComment());
								organisation.setCompanyId(salespersonRole.getOrganisation().getCompanyId());
								organisation.setCompanyName(salespersonRole.getOrganisation().getCompanyName());
								organisation.setEmail(salespersonRole.getOrganisation().getEmail());
								organisation.setOrganisationId(salespersonRole.getOrganisation().getOrganisationId());
								organisation.setPhone(salespersonRole.getOrganisation().getPhone());
								organisation.setSupportEmail(salespersonRole.getOrganisation().getSupportEmail());
								organisation.setSupportPhone(salespersonRole.getOrganisation().getSupportPhone());
								organisation.setType(salespersonRole.getOrganisation().getType());
								organisation.setZipCode(salespersonRole.getOrganisation().getZipCode());
							}
							success.setOrganisation(organisation);
						}
					}
					break;
				} else {
					log.info("Incorrect password for user: " + user.getUsername() + " / " + user.getEmail());
				}
			} else {
				log.info("User not active: " + user.getUsername() + " / " + user.getEmail());
				break;
			}
		}
		return success;
	}
}
