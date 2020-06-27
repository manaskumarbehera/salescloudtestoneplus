package dk.jyskit.waf.application.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.hibernate.validator.constraints.Email;

import com.google.common.base.Strings;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.agilecoders.wicket.jquery.util.Generics2;
import dk.jyskit.waf.application.BaseAppRoles;
import dk.jyskit.waf.application.services.passwordencryption.PasswordEncryptionService;
import dk.jyskit.waf.utils.guice.Lookup;
import dk.jyskit.waf.wicket.security.IAuthModel;

@Entity
@Table(name = "baseuser")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@EqualsAndHashCode(callSuper=true, of={ "identity", "email" })
public class BaseUser extends BaseEntity implements IAuthModel {
	public final static FontAwesomeIconType ICON = FontAwesomeIconType.user;
	
	private String username;
	private byte[] passwordEncrypt;
	private byte[] passwordSalt;

	/**
	 * Social security number or other identifier
	 */
	@NotNull
	protected String identity;

	@NotNull
	protected String firstName;

	@NotNull
	protected String lastName;

	@Email
	protected String email;

	protected String smsPhone;

	protected String address;
	
	/* Alternative: A few applications need address divided into street and houseNumber */
	protected String street;
	protected String houseNumber;
	
	protected String postalCode;
	protected String city;

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BaseRole> baseRoleList = new ArrayList<>();

	public BaseUser(){
	}

	public BaseUser(String userName, String password, String identity, String firstName, String lastName, String email, String smsPhone, String street, String houseNumber,
			String postalCode, String city) {
		this.username = userName;
		setPassword(password);
		this.identity = identity;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.smsPhone = smsPhone;
		this.street = street;
		this.houseNumber = houseNumber;
		this.postalCode = postalCode;
		this.city = city;
	}

	public BaseUser(String userName, String password, String identity, String firstName, String lastName, String email, String smsPhone, String address,
			String postalCode, String city) {
		this.username = userName;
		setPassword(password);
		this.identity = identity;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.smsPhone = smsPhone;
		this.address = address;
		this.postalCode = postalCode;
		this.city = city;
	}

	public void setPassword(String plain) {
		PasswordEncryptionService encryptionService = Lookup.lookup(PasswordEncryptionService.class);
		byte[] salt = encryptionService.generateSalt();
		byte[] encrypt = encryptionService.encrypt(plain, salt);
		setPasswordSalt(salt);
		setPasswordEncrypt(encrypt);
	}

	public boolean isAuthenticatedBy(String plainPassword) {
		return Lookup.lookup(PasswordEncryptionService.class).authenticate(plainPassword, passwordEncrypt, passwordSalt);
	}

	public String getFullName() {
		if (StringUtils.isEmpty(lastName)) {
			return firstName;
		} else {
			return firstName + " " + lastName;
		}
	}

	public String getPostAddress() {
		String adr;
		if (address == null) {
			adr = (Strings.nullToEmpty(getStreet()) + " " + Strings.nullToEmpty(getHouseNumber()) + ", " + Strings.nullToEmpty(getPostalCode()) + " " + Strings.nullToEmpty(getCity())).trim();
		} else {
			adr = (address + ", " + Strings.nullToEmpty(getPostalCode()) + " " + Strings.nullToEmpty(getCity())).trim();
		}
		if (adr.endsWith(",")) {
			return adr.substring(0, adr.length() -1);
		} else if (adr.startsWith(",")) {
			return adr.substring(1, adr.length());
		}
		return adr;
	}

	/**
	 * This is an implementation required by the Wicket authentication framework.
	 * Is is not really intended to be used by the application code. Consider
	 * using a method using BaseRole instead.
	 */
	@Override
	public Roles getRoles() {
		List<String> roleNames = Generics2.newArrayList(BaseAppRoles.USER);
		for (BaseRole role : baseRoleList) {
			roleNames.add(role.getRoleName());
		}
		return new Roles(roleNames.toArray(new String[roleNames.size()]));
	}

	/**
	 * This is an implementation required by the Wicket authentication framework.
	 * Is is not really intended to be used by the application code. Consider
	 * using a method using BaseRole instead.
	 */
	@Override
	public boolean hasAnyRole(Roles roles) {
		return !Collections.disjoint(getRoles(), roles);
	}

	public boolean hasAnyRole(Class<? extends BaseRole> ... roleClasses) {
		for (Class<? extends BaseRole> roleClass : roleClasses) {
			if (hasRole(roleClass)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the user has the specified role (based on role name from BaseRole).
	 *
	 * @deprecated It is better to search by class, since inheritance is supported
	 * @param roleName
	 * @return
	 */
	@Override
	public boolean hasRole(String roleName) {
		return getRoles().contains(roleName);
	}

	/**
	 * Check if the user has the specified role. 
	 *
	 * @param baseRoleClass
	 * @return
	 */
	public boolean hasRole(Class<? extends BaseRole> baseRoleClass) {
		return (getRole(baseRoleClass) != null);
	}

	/**
	 * @param baseRoleClass
	 * @return
	 */
	public BaseRole getRole(Class<? extends BaseRole> baseRoleClass) {
		for (BaseRole existingRole : baseRoleList) {
			if (baseRoleClass.isAssignableFrom(existingRole.getClass())) {
				return existingRole;
			}
		}
		return null;
	}

	/**
	 * Remove role from user.
	 *
	 * @param role
	 * @return true if successful
	 */
	public boolean removeRole(BaseRole role) {
		// Do not attempt to optimize this. Using .contains causes very strange problems
		for (BaseRole r : baseRoleList) {
			if (r.getId().equals(role.id)) {
				baseRoleList.remove(r);
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove role from user, based on role class.
	 *
	 * @param baseRoleClass
	 * @return true if successful
	 */
	public boolean removeRole(Class<? extends BaseRole> baseRoleClass) {
		for (BaseRole baseRole : baseRoleList) {
			if (baseRole.getClass().equals(baseRoleClass)) {
				baseRoleList.remove(baseRole);
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove role from user, based on base role name.
	 *
	 * @param baseRoleName
	 * @return true if successful
	 */
	public boolean removeRole(String baseRoleName) {
		for (BaseRole baseRole : baseRoleList) {
			if (baseRole.getRoleName().equals(baseRoleName)) {
				baseRoleList.remove(baseRole);
				return true;
			}
		}
		return false;
	}

//	@Transient
//	protected String[] getAppRoles() {
//		List<String> roleNames = Generics2.newArrayList(BaseAppRoles.USER);
//		for (BaseRole role : baseRoleList) {
//			roleNames.add(role.getRoleName());
//		}
//		return roleNames.toArray(new String[roleNames.size()]);
//	}

	public void addRole(BaseRole role) {
		baseRoleList.add(role);
		role.setUser(this);
	}

	@Transient
	public BaseRole getRoleByName(String roleName) {
		for(BaseRole role : baseRoleList) {
			if (role.getRoleName().equals(roleName)) {
				return role;
			}
		}
		return null;
	}
	
	// --------------------------------
	
	@Override
	public String toString() {
		if (StringUtils.isEmpty(username)) {
			return email;
		} else {
			return username;
		}
	}

}
