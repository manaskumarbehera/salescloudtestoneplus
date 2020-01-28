package dk.jyskit.salescloud.application.model;

import javax.persistence.Entity;

import dk.jyskit.waf.application.model.BaseRole;

@Entity
public class UserManagerRole extends BaseRole {
	public final static String ROLE_NAME	= "usermanager";
	
	public UserManagerRole() {
		roleName = ROLE_NAME;
	}

	// --------------------------------
	
	public int hashCode() {
		return (int) Integer.valueOf("" + id);
	}
	
	public boolean equals(Object o) {
		return id.equals(((BaseRole) o).getId());
	}
	
	@Override
	public String toString() {
		return "Brugeradministrator";
	}
	
}
